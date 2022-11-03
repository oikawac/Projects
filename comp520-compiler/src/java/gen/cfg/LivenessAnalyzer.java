package gen.cfg;

import gen.asm.Register;

import java.util.HashSet;
import java.util.LinkedList;

public class LivenessAnalyzer {

    public static void analyze(CFGNode programStartNode) {
        LinkedList<CFGNode> stack1 = new LinkedList<>();
        LinkedList<CFGNode> stack2 = new LinkedList<>();
        HashSet<Integer> visited = new HashSet<>();
        stack1.addFirst(programStartNode);
        while (!stack1.isEmpty()) {
            CFGNode currentNode = stack1.removeFirst();
            visited.add(currentNode.debugId);
            if (currentNode.liveIn == null) currentNode.liveIn = new HashSet<>();
            if (currentNode.liveOut == null) currentNode.liveOut = new HashSet<>();
            for (CFGNode successorNode : currentNode.getSuccessorList()) {
                if (!visited.contains(successorNode.debugId)) stack1.addFirst(successorNode);
            }
            if (currentNode.instruction != null) stack2.addLast(currentNode);
        }
        boolean pointFixed;
        do {
            pointFixed = true;
            for (CFGNode currentNode : stack2){
                int cardinalityLiveIn = currentNode.liveIn.size();
                int cardinalityLiveOut = currentNode.liveOut.size();
                for (CFGNode successor : currentNode.getSuccessorList()) for (Register reg : successor.liveIn) {
                    if (reg.isVirtual()) currentNode.liveOut.add(reg);
                }
                for (Register reg : currentNode.instruction.uses()) {
                    if (reg.isVirtual()) currentNode.liveIn.add(reg);
                }
                for (Register reg : currentNode.liveOut) {
                    if (reg != currentNode.instruction.def()) currentNode.liveIn.add(reg);
                }
                if (cardinalityLiveIn != currentNode.liveIn.size() || cardinalityLiveOut != currentNode.liveOut.size()) {
                    pointFixed = false;
                }
            }
        } while (!pointFixed);
    }
}
