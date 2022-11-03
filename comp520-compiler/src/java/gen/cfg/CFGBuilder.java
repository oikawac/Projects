package gen.cfg;

import gen.asm.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import static gen.asm.OpCode.Kind.*;

public class CFGBuilder {

    private LinkedList<Label> previousLabels;
    private boolean nullPredecessor;

    private LinkedList<CFGNode> linearNodes;

    private int debugNodeId = 1;

    public CFGBuilder(AssemblyProgram program) {
        nullPredecessor = true;
        linearNodes = new LinkedList<>();
        linearNodes.add(new CFGNode());//empty node at start of program
        linearNodes.getFirst().debugId = 0;
        previousLabels = new LinkedList<>();
        for (AssemblyProgram.Section section : program.sections) {
            if (section.type == AssemblyProgram.Section.Type.TEXT) {
                buildOutCFG(section);
            }
        }
        HashMap<Label,LinkedList<CFGNode>> labelToNodeMap = new HashMap<>();
        for (CFGNode node : linearNodes) {
            for (Label label : node.inLabels()) {
                if (labelToNodeMap.containsKey(label)) {
                    labelToNodeMap.get(label).add(node);
                } else {
                    LinkedList<CFGNode> list = new LinkedList<>();
                    list.add(node);
                    labelToNodeMap.put(label,list);
                }
            }
        }
        HashMap<Label,LinkedList<CFGNode>> labelFromNodeMap = new HashMap<>();
        for (CFGNode node : linearNodes) {
            for (Label label : node.outLabels()) {
                if (labelFromNodeMap.containsKey(label)) {
                    labelFromNodeMap.get(label).add(node);
                } else {
                    LinkedList<CFGNode> list = new LinkedList<>();
                    list.add(node);
                    labelFromNodeMap.put(label,list);
                }
            }
        }
        for (Label labelFrom : labelFromNodeMap.keySet()) {
            LinkedList<CFGNode> nodesFrom = labelFromNodeMap.get(labelFrom);
            if (!labelToNodeMap.containsKey(labelFrom)) continue;
            LinkedList<CFGNode> nodesTo = labelToNodeMap.get(labelFrom);
            for (CFGNode nodeF : nodesFrom) for (CFGNode nodeT : nodesTo) {
                nodeF.addSuccessor(nodeT);
            }
        }
        HashSet<Label> alreadySeenLabels = new HashSet<>();
        LinkedList<Label> loopLabelStack = new LinkedList<>();
        for (int i=linearNodes.size()-1; i>0; i--)  {
            CFGNode node = linearNodes.get(i);
            if (node.instruction.opcode == OpCode.B) {
                Label label = ((Instruction.Jump)node.instruction).label;
                if (!alreadySeenLabels.contains(label)) loopLabelStack.addFirst(label);
            }
            if (node.inLabels().size() > 0) {
                alreadySeenLabels.addAll(node.inLabels());
            }
            if (loopLabelStack.size() > 0) {
                if (node.inLabels().contains(loopLabelStack.getFirst())) {
                    loopLabelStack.pop();
                }
            }
            node.loopDepth = loopLabelStack.size();
        }
    }

    private void buildOutCFG(AssemblyProgram.Section section) {
        final boolean[] first = {true};//must be final to use in anon. class
        for (AssemblyItem item : section.items) {
            item.accept(new AssemblyItemVisitor() {
                @Override
                public void visitLabel(Label label) {
                    if (first[0]) {
                        linearNodes.getFirst().addOutLabel(label);
                        first[0] = false;
                    }
                    previousLabels.add(label);
                }

                @Override
                public void visitDirective(Directive directive) {

                }

                @Override
                public void visitInstruction(Instruction instruction) {
                    if (instruction.opcode.kind() == ARITHMETIC_WITH_IMMEDIATE) addInstruction(instruction);
                    else if (instruction.opcode.kind() == BINARY_ARITHMETIC) addInstruction(instruction);
                    else if (instruction.opcode.kind() == BINARY_BRANCH) {
                        Label branchTo = ((Instruction.BinaryBranch)instruction).label;
                        addBranchInstruction(instruction, branchTo);
                    } else if (instruction.opcode.kind() == JUMP) {
                        if (instruction.opcode == OpCode.JAL)
                            addInstruction(instruction);
                        else {
                            Label branchTo = ((Instruction.Jump)instruction).label;
                            addBranchInstruction(instruction, branchTo);
                            nullPredecessor = true;
                        }
                    } else if (instruction.opcode.kind() == JUMP_REGISTER) {
                        addInstruction(instruction);
                        nullPredecessor = true;
                    } else if (instruction.opcode.kind() == LOAD) addInstruction(instruction);
                    else if (instruction.opcode.kind() == LOAD_ADDRESS) addInstruction(instruction);
                    else if (instruction.opcode.kind() == LOAD_IMMEDIATE) addInstruction(instruction);
                    else if (instruction.opcode.kind() == NULLARY) addInstruction(instruction);
                    else if (instruction.opcode.kind() == STORE) addInstruction(instruction);
                    else if (instruction.opcode.kind() == TERNARY_ARITHMETIC) addInstruction(instruction);
                    else if (instruction.opcode.kind() == UNARY_ARITHMETIC) addInstruction(instruction);
                    else if (instruction.opcode.kind() == UNARY_BRANCH) {
                        Label branchTo = ((Instruction.UnaryBranch)instruction).label;
                        addBranchInstruction(instruction, branchTo);
                    }
                    if (first[0]) {
                        linearNodes.getFirst().addSuccessor(linearNodes.getLast());
                        first[0] = false;
                    }
                }

                @Override
                public void visitComment(Comment comment) {

                }
            });
        }
        nullPredecessor = true;
    }

    private void addInstruction(Instruction instruction) {
        if (nullPredecessor) {
            nullPredecessor = false;
            linearNodes.addLast(new CFGNode(instruction));
        } else {
            linearNodes.addLast(linearNodes.getLast().addSuccessor(new CFGNode(instruction)));
        }
        linearNodes.getLast().addInLabels(previousLabels);
        linearNodes.getLast().debugId = debugNodeId;
        debugNodeId++;
        previousLabels.clear();
    }

    private void addBranchInstruction(Instruction instruction, Label label) {
        if (nullPredecessor) {
            nullPredecessor = false;
            linearNodes.addLast(new CFGNode(instruction));
        } else {
            linearNodes.addLast(linearNodes.getLast().addSuccessor(new CFGNode(instruction)));
        }
        linearNodes.getLast().addOutLabel(label);
        linearNodes.getLast().addInLabels(previousLabels);
        linearNodes.getLast().debugId = debugNodeId;
        debugNodeId++;
        previousLabels.clear();
    }

    public void debugPrintCFG() {
        FileWriter fileWriter;
        try {
            fileWriter = new FileWriter("CFG.dot");
            fileWriter.write("digraph CFG {\n");
            for (CFGNode node : linearNodes) {
                if (node.instruction == null) fileWriter.write("node"+node.debugId+" [label=\"program\"];\n");
                else fileWriter.write("node"+node.debugId+" [label=\""+node.instruction.toString()+"["+node.loopDepth+"]"+"\"];\n");
                if (!node.inLabels().isEmpty()) {
                    StringBuilder string = new StringBuilder();
                    for (Label label : node.inLabels()) {
                        string.append(label.toString());
                        string.append(" ");
                    }
                    fileWriter.write("nodelblin" + node.debugId + " [label=\"label: " + string.toString() + "\"];\n");
                    fileWriter.write("nodelblin" + node.debugId + " -> " + "node" + node.debugId + ";\n");
                }
                if (node.liveOut != null) {
                    StringBuilder string = new StringBuilder();
                    int count = 0;
                    for (Register reg : node.liveOut) {
                        count++;
                        string.append(reg.toString());
                        string.append(" ");
                    }
                    if (count > 0) {
                        fileWriter.write("nodeliveout" + node.debugId + " [label=\"liveout: " + string.toString() + "\"];\n");
                        fileWriter.write("node" + node.debugId + " -> " + "nodeliveout" + node.debugId + ";\n");
                    }
                }
                if (node.liveIn != null) {
                    StringBuilder string = new StringBuilder();
                    int count = 0;
                    for (Register reg : node.liveIn) {
                        count++;
                        string.append(reg.toString());
                        string.append(" ");
                    }
                    if (count > 0) {
                        fileWriter.write("nodelivein" + node.debugId + " [label=\"livein: " + string.toString() + "\"];\n");
                        fileWriter.write("node" + node.debugId + " -> " + "nodelivein" + node.debugId + ";\n");
                    }
                }
            }
            for (CFGNode node : linearNodes) for (CFGNode successor : node.getSuccessorList()) {
                fileWriter.write("node"+node.debugId+" -> "+"node"+successor.debugId+";\n");
            }
            fileWriter.write("}\n");
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public CFGNode getProgramNode() {
        return linearNodes.getFirst();
    }
}
