package gen;

import gen.asm.*;
import gen.cfg.CFGBuilder;
import gen.cfg.LivenessAnalyzer;
import regalloc.AssemblyPass;
import regalloc.RegAllocMappingGenerator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class MapVirtualRegisters implements AssemblyPass {

    private static AssemblyPass pushPopPassInstance = PushPopRegistersAlternative.INSTANCE;

    private MapVirtualRegisters() { };

    public static final MapVirtualRegisters INSTANCE = new MapVirtualRegisters();

    private static AssemblyProgram run(AssemblyProgram prog) {
        AssemblyProgram newProg = new AssemblyProgram();
        CFGBuilder cfgBuilder = new CFGBuilder(prog);
        LivenessAnalyzer.analyze(cfgBuilder.getProgramNode());
        HashMap<Register, Register> mapping = RegAllocMappingGenerator.generateMapping(cfgBuilder.getProgramNode());
        LinkedList<Register> registersToSpill = new LinkedList<>();
        HashMap<Register, Integer> spillOffset = new HashMap<>();
        int spillByteOffset = 0;
        for (Register reg : mapping.keySet()) {
            if (mapping.get(reg) == null) {
                registersToSpill.add(reg);
                spillOffset.put(reg, spillByteOffset);
                spillByteOffset += 4;
            }
        }
        for (Register reg : registersToSpill) {
            mapping.remove(reg);
        }
        prog.sections.forEach(section -> {
            if (section.type == AssemblyProgram.Section.Type.DATA)
                newProg.emitSection(section);
            else {
                assert (section.type == AssemblyProgram.Section.Type.TEXT);
                AssemblyProgram.Section newSection = newProg.newSection(AssemblyProgram.Section.Type.TEXT);
                section.items.forEach(item ->
                        item.accept(new AssemblyItemVisitor() {
                            public void visitComment(Comment comment) {
                                if (comment.comment.equals("Cailean Oikawa")) {
                                    pushPopPassInstance = PushPopRegisters.INSTANCE;
                                }
                                newSection.emit(comment);
                            }
                            public void visitLabel(Label label) {
                                newSection.emit(label);
                            }
                            public void visitDirective(Directive directive) {
                                newSection.emit(directive);
                            }
                            public void visitInstruction(Instruction insn) {
                                boolean unreachableCode = false;
                                for (Register reg : insn.registers()) {
                                    if (reg.isVirtual() && !mapping.containsKey(reg) && !registersToSpill.contains(reg)) {
                                        unreachableCode = true;
                                    }
                                }
                                if (unreachableCode) {
                                    newSection.emit(new Comment("unreachable: "+insn.toString()));
                                } else {
                                    Instruction newInsn = insn.rebuild(mapping);
                                    newSection.emit(newInsn);
                                }
                            }
                        }));
            }
        });
        HashSet<Register> pushPopReg = new HashSet<>();
        pushPopReg.addAll(RegAllocMappingGenerator.archRegisters);
        pushPopReg.addAll(registersToSpill);
        PushPopRegistersAlternative.setValidRegisters(pushPopReg);
        PushPopRegisters.setValidRegisters(pushPopReg);
        AssemblyProgram asmProgPushPopRegisters = pushPopPassInstance.apply(newProg);
        AssemblyProgram asmProgSpilledRegisters = new AssemblyProgram();
        AssemblyProgram.Section spillData = asmProgSpilledRegisters.newSection(AssemblyProgram.Section.Type.DATA);
        Label globalSpillPointer = Label.create();
        spillData.emit(new Comment("spilled registers heap allocation"));
        spillData.emit(globalSpillPointer);
        spillData.emit(new Directive("space "+registersToSpill.size()*4));
        asmProgPushPopRegisters.sections.forEach(section -> {
            AssemblyProgram.Section newSection = asmProgSpilledRegisters.newSection(AssemblyProgram.Section.Type.TEXT);
            AssemblyItemVisitor visitor = new AssemblyItemVisitor() {
                Register holdingSpillAddress = null;
                public void visitComment(Comment comment) {
                    newSection.emit(comment);
                }
                public void visitLabel(Label label) {
                    holdingSpillAddress = null;
                    newSection.emit(label);
                }
                public void visitDirective(Directive directive) {
                    newSection.emit(directive);
                }
                public void visitInstruction(Instruction insn) {
                    HashMap<Register, Register> mapping = new HashMap<>();
                    Register savet0 = null;
                    Register loadt0 = null;
                    Register loadt1 = null;
                    if (registersToSpill.contains(insn.def())) {
                        savet0 = insn.def();
                        mapping.put(insn.def(), Register.Arch.t0);
                        if (insn.uses().contains(insn.def())) {
                            loadt0 = savet0;
                        }
                    }
                    for (Register reg : insn.uses()) {
                        if (!registersToSpill.contains(reg)) continue;
                        if (savet0 == reg) continue;
                        if (loadt0 == null) {
                            loadt0 = reg;
                            mapping.put(reg, Register.Arch.t0);
                        } else if (loadt1 == null) {
                            loadt1 = reg;
                            mapping.put(reg, Register.Arch.t1);
                        }
                    }
                    if (loadt0 != null) {
                        if (holdingSpillAddress == null) {
                            holdingSpillAddress = Register.Arch.t1;
                            newSection.emit(OpCode.LA, Register.Arch.t1, globalSpillPointer);
                        }
                        newSection.emit(OpCode.LW, Register.Arch.t0, holdingSpillAddress, spillOffset.get(loadt0));
                    }
                    if (loadt1 != null) {
                        if (holdingSpillAddress == null) {
                            newSection.emit(OpCode.LA, Register.Arch.t1, globalSpillPointer);
                        }
                        newSection.emit(OpCode.LW, Register.Arch.t1, holdingSpillAddress, spillOffset.get(loadt1));
                        holdingSpillAddress = null;
                    }
                    if (insn.opcode.kind() == OpCode.Kind.JUMP
                            || insn.opcode.kind() == OpCode.Kind.JUMP_REGISTER
                            || insn.opcode.kind() == OpCode.Kind.UNARY_BRANCH
                            || insn.opcode.kind() == OpCode.Kind.BINARY_BRANCH) {
                        holdingSpillAddress = null;
                    }
                    Instruction newInsn = insn.rebuild(mapping);
                    newSection.emit(newInsn);
                    if (savet0 != null) {
                        if (holdingSpillAddress == null) {
                            holdingSpillAddress = Register.Arch.t1;
                            newSection.emit(OpCode.LA, Register.Arch.t1, globalSpillPointer);
                        }
                        newSection.emit(OpCode.SW, Register.Arch.t0, Register.Arch.t1, spillOffset.get(savet0));
                    }
                }
            };
            if (section.type == AssemblyProgram.Section.Type.DATA)
                asmProgSpilledRegisters.emitSection(section);
            else {
                assert (section.type == AssemblyProgram.Section.Type.TEXT);
                section.items.forEach(item ->
                        item.accept(visitor));
            }
        });
        return asmProgSpilledRegisters;
    }

    @Override
    public AssemblyProgram apply(AssemblyProgram program) {
        return run(program);
    }
}