package gen.cfg;

import gen.asm.Instruction;
import gen.asm.Label;
import gen.asm.Register;

import java.util.HashSet;
import java.util.LinkedList;

public class CFGNode {

    public int loopDepth = 0;

    public Instruction instruction;
    private LinkedList<CFGNode> successorList;
    private LinkedList<CFGNode> predecessorList;

    private LinkedList<Label> inLabels;
    private LinkedList<Label> outLabels;

    public HashSet<Register> liveOut;
    public HashSet<Register> liveIn;

    public int debugId;

    public CFGNode() {
        successorList = new LinkedList<>();
        predecessorList = new LinkedList<>();
        inLabels = new LinkedList<>();
        outLabels = new LinkedList<>();
    }
    public CFGNode(Instruction instruction) {
        this.instruction = instruction;
        successorList = new LinkedList<>();
        predecessorList = new LinkedList<>();
        inLabels = new LinkedList<>();
        outLabels = new LinkedList<>();
    }

    public CFGNode addSuccessor(CFGNode node) {
        successorList.add(node);
        if (!node.succeeds(this)) {
            node.addPredecessor(this);
        }
        return node;
    }

    public CFGNode addPredecessor(CFGNode node) {
        predecessorList.add(node);
        if (!node.precedes(this)) {
            node.addSuccessor(this);
        }
        return node;
    }

    public boolean succeeds(CFGNode node) {
        return predecessorList.contains(node);
    }
    public boolean precedes(CFGNode node) {
        return successorList.contains(node);
    }

    public void addInLabels(LinkedList<Label> labels) {
        inLabels.addAll(labels);
    }

    public void addOutLabels(LinkedList<Label> labels) {
        outLabels.addAll(labels);
    }

    public void addInLabel(Label label) {
        inLabels.add(label);
    }

    public void addOutLabel(Label label) {
        outLabels.add(label);
    }

    public LinkedList<Label> inLabels() {
        return inLabels;
    }

    public LinkedList<Label> outLabels() {
        return outLabels;
    }

    public LinkedList<CFGNode> getSuccessorList() {
        return successorList;
    }

}
