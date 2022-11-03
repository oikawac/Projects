package regalloc;

import gen.asm.Register;
import gen.cfg.CFGNode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class RegAllocMappingGenerator {

    private static HashMap<Register, HashSet<Register>> interferenceGraph;
    public static HashSet<Register> archRegisters = new HashSet<>();
    static {
        //archRegisters.add(Register.Arch.t0);
        //archRegisters.add(Register.Arch.t1);
        archRegisters.add(Register.Arch.t2);
        archRegisters.add(Register.Arch.t3);
        archRegisters.add(Register.Arch.t4);
        archRegisters.add(Register.Arch.t5);
        archRegisters.add(Register.Arch.t6);
        archRegisters.add(Register.Arch.t7);
        archRegisters.add(Register.Arch.t8);
        archRegisters.add(Register.Arch.t9);
        archRegisters.add(Register.Arch.s0);
        archRegisters.add(Register.Arch.s1);
        archRegisters.add(Register.Arch.s2);
        archRegisters.add(Register.Arch.s3);
        archRegisters.add(Register.Arch.s4);
        archRegisters.add(Register.Arch.s5);
        archRegisters.add(Register.Arch.s6);
        archRegisters.add(Register.Arch.s7);
    }

    private static void interferenceSet(HashSet<Register> registers) {
        for (Register reg : registers) {
            if (!interferenceGraph.containsKey(reg)) {
                interferenceGraph.put(reg, new HashSet<>());
            }
        }
        for (Register reg : registers) {
            for (Register regInterferes : registers) {
                if (reg == regInterferes) continue;
                interferenceGraph.get(reg).add(regInterferes);
            }
        }
    }

    public static HashMap<Register, Register> generateMapping(CFGNode programStartNode) {
        HashMap<Register, Integer> maxLoopDepth = new HashMap<>();
        HashMap<Register, Integer> usesCount = new HashMap<>();
        interferenceGraph = new HashMap<>();
        LinkedList<CFGNode> stack = new LinkedList<>();
        HashSet<Integer> visited = new HashSet<>();
        stack.addLast(programStartNode);
        while (!stack.isEmpty()) {
            CFGNode currentNode = stack.removeLast();
            if (currentNode.instruction != null) {
                for (Register reg : currentNode.instruction.registers()) {
                    if (!reg.isVirtual()) continue;
                    if (!maxLoopDepth.containsKey(reg)) maxLoopDepth.put(reg, 0);
                    if (!usesCount.containsKey(reg)) usesCount.put(reg, 0);
                    usesCount.put(reg, usesCount.get(reg)+1);
                    if (maxLoopDepth.get(reg) < currentNode.loopDepth) {
                        maxLoopDepth.put(reg, currentNode.loopDepth);
                    }
                }
            }
            visited.add(currentNode.debugId);
            if (currentNode.liveOut != null) {
                interferenceSet(currentNode.liveOut);
            }
            if (currentNode.liveIn != null) {
                interferenceSet(currentNode.liveIn);
            }
            for (CFGNode successor : currentNode.getSuccessorList()) {
                if (visited.contains(successor.debugId)) continue;
                stack.addLast(successor);
            }
        }
        HashMap<Register, HashSet<Register>> copyOfInterferenceGraph = new HashMap<>();
        for (Register reg : interferenceGraph.keySet()) {
            HashSet<Register> conflict = new HashSet<>();
            conflict.addAll(interferenceGraph.get(reg));
            copyOfInterferenceGraph.put(reg, conflict);
        }
        LinkedList<Register> registersToSpill = new LinkedList<>();
        int K = archRegisters.size();
        LinkedList<Register> lessThanKStack = new LinkedList<>();
        while (!copyOfInterferenceGraph.isEmpty()) {
            boolean thereExistsSomeVertexWithFewerThanKVertices = true;
            while (thereExistsSomeVertexWithFewerThanKVertices) {
                Register regToRemove = null;
                int maxUses = 0;
                for (Register reg : copyOfInterferenceGraph.keySet()) {
                    if (copyOfInterferenceGraph.get(reg).size() < K) {
                        if (usesCount.get(reg) > maxUses) {
                            maxUses = usesCount.get(reg);
                            regToRemove = reg;
                        }
                    }
                }
                if (regToRemove != null) {
                    lessThanKStack.add(regToRemove);
                    copyOfInterferenceGraph.remove(regToRemove);
                    for (Register reg : copyOfInterferenceGraph.keySet()) {
                        copyOfInterferenceGraph.get(reg).remove(regToRemove);
                    }
                } else thereExistsSomeVertexWithFewerThanKVertices = false;
            }
            if (!copyOfInterferenceGraph.isEmpty()) {
                Register best = null;
                int max = 0;
                for (Register reg : copyOfInterferenceGraph.keySet()) {
                    if (copyOfInterferenceGraph.get(reg).size()/ usesCount.get(reg) > max) {
                        best = reg;
                        max = copyOfInterferenceGraph.get(reg).size()/ usesCount.get(reg);
                    }
                }
                assert(best != null);
                registersToSpill.add(best);
                copyOfInterferenceGraph.remove(best);
                for (Register reg : copyOfInterferenceGraph.keySet()) {
                    copyOfInterferenceGraph.get(reg).remove(best);
                }
            }
        }
        HashMap<Register, Register> mapping = new HashMap<>();
        for (int i=lessThanKStack.size()-1; i>=0; i--) {
            for (Register archReg : archRegisters) {
                boolean canBeMapped = true;
                for (Register conflict : interferenceGraph.get(lessThanKStack.get(i))) {
                    if (mapping.containsKey(conflict)) {
                        if (mapping.get(conflict) == archReg) {
                            canBeMapped = false;
                            break;
                        }
                    }
                }
                if (canBeMapped) {
                    mapping.put(lessThanKStack.get(i), archReg);
                    break;
                }
            }
        }
        for (Register spilled : registersToSpill) {
            mapping.put(spilled, null);
        }
        return mapping;
    }
}
