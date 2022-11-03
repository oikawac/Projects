package gen;

import gen.asm.*;
import regalloc.AssemblyPass;

import java.util.*;

public class PushPopRegistersAlternative implements AssemblyPass {

    private PushPopRegistersAlternative() { };

    private static HashSet<Register> validRegisters;

    private static LinkedList<Register> usedRegisters(AssemblyProgram.Section section) {
        final LinkedList<Register> usedReg = new LinkedList<>();
        section.items.forEach(item ->
                item.accept(new AssemblyItemVisitor() {
                    public void visitComment(Comment comment) {}
                    public void visitLabel(Label label) {}
                    public void visitDirective(Directive directive) {}

                    public void visitInstruction(Instruction insn) {
                        insn.registers().forEach(reg -> {
                            if (validRegisters.contains(reg) && !usedReg.contains(reg)) {
                               usedReg.add(reg);
                            }
                        });
                    }
                }));
        return usedReg;
    }

    public static final PushPopRegistersAlternative INSTANCE = new PushPopRegistersAlternative();

    private static AssemblyProgram run(AssemblyProgram prog) {
        AssemblyProgram newProg = new AssemblyProgram();
        prog.sections.forEach(section -> {
            if (section.type == AssemblyProgram.Section.Type.DATA)
                newProg.emitSection(section);
            else {
                assert (section.type == AssemblyProgram.Section.Type.TEXT);
                LinkedList<Register> used = usedRegisters(section);
                LinkedList<Register> rev_used = usedRegisters(section);
                Collections.reverse(rev_used);
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
                                newSection.emit(new Comment("push registers (alternative): "));
                                int i = 0;
                                for (Register r : used) {
                                    i++;
                                    newSection.emit(OpCode.SW, r, Register.Arch.sp, -4*i);
                                }
                                newSection.emit(OpCode.ADDI, Register.Arch.sp, Register.Arch.sp, -4*i);
                            } else if (insn.opcode == OpCode.POP_REGISTERS) {
                                newSection.emit(new Comment("pop registers (alternative): "));
                                int i = 0;
                                for (Register r : rev_used) {
                                    newSection.emit(OpCode.LW, r, Register.Arch.sp, 4*i);
                                    i++;
                                }
                                newSection.emit(OpCode.ADDI, Register.Arch.sp, Register.Arch.sp, 4*i);
                            } else {
                                    newSection.emit(insn);
                                }
                            }
                    }));
            }
        });
        return newProg;
    }

    public static void setValidRegisters(HashSet<Register> validRegisters) {
        PushPopRegistersAlternative.validRegisters = validRegisters;
    }

    @Override
    public AssemblyProgram apply(AssemblyProgram program) {
        return run(program);
    }


}
