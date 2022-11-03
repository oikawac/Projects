package gen;

import gen.asm.*;
import regalloc.AssemblyPass;

import java.util.HashSet;
import java.util.Set;

public class PushPopRegistersNaive implements AssemblyPass {

    private PushPopRegistersNaive() { };

    private static Set<Register> usedRegisters(AssemblyProgram.Section section) {
        final Set<Register> usedReg = new HashSet<>();

        section.items.forEach(item ->
                item.accept(new AssemblyItemVisitor() {
                    public void visitComment(Comment comment) {}
                    public void visitLabel(Label label) {}
                    public void visitDirective(Directive directive) {}

                    public void visitInstruction(Instruction insn) {
                        insn.registers().forEach(reg -> {
                            if (reg.isVirtual()) {
                               usedReg.add(reg);
                            }
                        });
                    }
                }));
        return usedReg;
    }

    public static final PushPopRegistersNaive INSTANCE = new PushPopRegistersNaive();

    private static AssemblyProgram run(AssemblyProgram prog) {
        AssemblyProgram newProg = new AssemblyProgram();

        prog.sections.forEach(section -> {
            if (section.type == AssemblyProgram.Section.Type.DATA)
                newProg.emitSection(section);
            else {
                assert (section.type == AssemblyProgram.Section.Type.TEXT);
                Set<Register> used = usedRegisters(section);
                AssemblyProgram.Section newSection = newProg.newSection(AssemblyProgram.Section.Type.TEXT);
                section.items.forEach(item ->
                    item.accept(new AssemblyItemVisitor() {
                        public void visitComment(Comment comment) {
                            newSection.emit(comment);
                        }
                        public void visitLabel(Label label) {
                            newSection.emit(label);
                        }
                        public void visitDirective(Directive directive) {
                            newSection.emit(directive);
                        }
                        public void visitInstruction(Instruction insn) {
                            if (insn.opcode == OpCode.PUSH_REGISTERS) {
                                newSection.emit(new Comment("push registers: "));
                                int i = 0;
                                for (Register r : used) {
                                    i++;
                                    newSection.emit(OpCode.SW, r, Register.Arch.sp, -4*i);
                                }
                                newSection.emit(OpCode.ADDI, Register.Arch.sp, Register.Arch.sp, -4*i);
                            } else if (insn.opcode == OpCode.POP_REGISTERS) {
                                newSection.emit(new Comment("pop registers: "));
                                int i = 0;
                                for (Register r : used) {
                                    i++;
                                    newSection.emit(OpCode.LW, r, Register.Arch.sp, -4*i);
                                }
                            } else {
                                    newSection.emit(insn);
                                }
                            }
                    }));
            }
        });
        return newProg;
    }

    @Override
    public AssemblyProgram apply(AssemblyProgram program) {
        return run(program);
    }


}
