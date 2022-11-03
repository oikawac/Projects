package ast;

import util.Position;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

public class ASTDotPrinter implements ASTVisitor<Void> {

    Position pos;

    private FileWriter fileWriter;

    String currentParent = "";
    Integer uniqueNodeId = 0;

    int globalTabLevel = 0;
    LinkedList<Integer> tabulationStack = new LinkedList<>();

    public ASTDotPrinter() {
        try {
            fileWriter = new FileWriter("AST.dot");
            fileWriter.write("digraph AST {\n");
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private String addDotNode(String label) {
        try {
            String newNode = "node"+uniqueNodeId.toString();
            uniqueNodeId++;
            fileWriter.write(newNode+" [label=\""+label+"\"];\n");
            return newNode;
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }

    private void addDotEdge(String fromNode, String toNode) {
        try {
            fileWriter.write(fromNode+" -> "+toNode+";\n");
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }


    @Override
    public Void visitProgram(Program p) {
        String node = addDotNode("Program");
        for (StructTypeDecl std : p.structTypeDeclList) {
            currentParent = node;
            std.accept(this);
        }
        for (VarDecl vd : p.varDeclList) {
            currentParent = node;
            vd.accept(this);
        }
        for (FunDecl fd : p.funDeclList) {
            currentParent = node;
            fd.accept(this);
        }
        try {
            fileWriter.write("}\n");
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    @Override
    public Void visitArrayType(ArrayType t) {
        String node = addDotNode("ArrayType");
        addDotEdge(currentParent, node);
        currentParent = node;
        t.type.accept(this);
        currentParent = node;
        String sizeNode = addDotNode(Integer.toString(t.size));
        addDotEdge(currentParent, sizeNode);
        return null;
    }

    @Override
    public Void visitBaseType(BaseType t) {
        String node = addDotNode(t.toString());
        addDotEdge(currentParent, node);
        return null;
    }

    @Override
    public Void visitPointerType(PointerType t) {
        String node = addDotNode("PointerType");
        addDotEdge(currentParent, node);
        currentParent = node;
        t.pointedType.accept(this);
        return null;
    }

    @Override
    public Void visitStructType(StructType t) {
        String node = addDotNode("StructType");
        addDotEdge(currentParent, node);
        currentParent = node;
        String idNode = addDotNode(t.structId);
        addDotEdge(currentParent, idNode);
        return null;
    }

    @Override
    public Void visitFunDecl(FunDecl d) {
        String node = addDotNode("FunDecl");
        addDotEdge(currentParent, node);
        currentParent = node;
        d.type.accept(this);
        currentParent = node;
        String idNode = addDotNode(d.funcId);
        addDotEdge(currentParent, idNode);
        for (VarDecl vd : d.paramDeclList) {
            currentParent = node;
            vd.accept(this);
        }
        currentParent = node;
        d.block.accept(this);
        return null;
    }

    @Override
    public Void visitStructTypeDecl(StructTypeDecl d) {
        String node = addDotNode("StructTypeDecl");
        addDotEdge(currentParent, node);
        currentParent = node;
        d.structType.accept(this);
        for (VarDecl vd : d.varDeclList) {
            currentParent = node;
            vd.accept(this);
        }
        return null;
    }

    @Override
    public Void visitVarDecl(VarDecl d) {
        String node = addDotNode("VarDecl");
        addDotEdge(currentParent, node);
        currentParent = node;
        d.type.accept(this);
        currentParent = node;
        String idNode = addDotNode(d.varId);
        addDotEdge(currentParent, idNode);
        return null;
    }

    @Override
    public Void visitAssignStmt(AssignStmt s) {
        String node = addDotNode("Assign");
        addDotEdge(currentParent, node);
        currentParent = node;
        s.identifierExpr.accept(this);
        currentParent = node;
        s.newValueExpr.accept(this);
        return null;
    }

    @Override
    public Void visitBlockStmt(BlockStmt s) {
        String node = addDotNode("Block");
        addDotEdge(currentParent, node);
        currentParent = node;
        for (VarDecl vd: s.varDeclList) {
            currentParent = node;
            vd.accept(this);
        }
        for (Stmt stmt: s.stmtList) {
            currentParent = node;
            stmt.accept(this);
        }
        return null;
    }

    @Override
    public Void visitExprStmt(ExprStmt s) {
        String node = addDotNode("ExprStmt");
        addDotEdge(currentParent, node);
        currentParent = node;
        s.exp.accept(this);
        return null;
    }

    @Override
    public Void visitIfStmt(IfStmt s) {
        String node = addDotNode("If");
        addDotEdge(currentParent, node);
        currentParent = node;
        s.conditionExpr.accept(this);
        currentParent = node;
        s.thenBodyStmt.accept(this);
        if (!s.elseBodyStmt.isEmpty()) {
            currentParent = node;
            s.elseBodyStmt.accept(this);
        }
        return null;
    }

    @Override
    public Void visitReturnStmt(ReturnStmt s) {
        String node = addDotNode("Return");
        addDotEdge(currentParent, node);
        currentParent = node;
        s.returnValueExpr.accept(this);
        return null;
    }

    @Override
    public Void visitWhileStmt(WhileStmt s) {
        String node = addDotNode("While");
        addDotEdge(currentParent, node);
        currentParent = node;
        s.conditionExpr.accept(this);
        currentParent = node;
        s.bodyStmt.accept(this);
        return null;
    }

    @Override
    public Void visitAddressOfExpr(AddressOfExpr e) {
        String node = addDotNode("AddressOfExpr");
        addDotEdge(currentParent, node);
        currentParent = node;
        e.exp.accept(this);
        return null;
    }

    @Override
    public Void visitArrayAccessExpr(ArrayAccessExpr e) {
        String node = addDotNode("ArrayAccessExpr");
        addDotEdge(currentParent, node);
        currentParent = node;
        e.arrayExpr.accept(this);
        currentParent = node;
        e.indexExpr.accept(this);
        return null;
    }

    @Override
    public Void visitBinOpExpr(BinOpExpr e) {
        String node = addDotNode("BinOp");
        addDotEdge(currentParent, node);
        currentParent = node;
        e.lhsExpr.accept(this);
        currentParent = node;
        String opNode = addDotNode(e.op.toString());
        addDotEdge(currentParent, opNode);
        currentParent = node;
        e.rhsExpr.accept(this);
        return null;
    }

    @Override
    public Void visitCharLiteralExpr(CharLiteralExpr e) {
        String node = addDotNode("ChrLiteral("+Character.toString(e.value)+")");
        addDotEdge(currentParent, node);
        return null;
    }

    @Override
    public Void visitFieldAccessExpr(FieldAccessExpr e) {
        String node = addDotNode("FieldAccessExpr");
        addDotEdge(currentParent, node);
        currentParent = node;
        e.structExpr.accept(this);
        currentParent = node;
        String fieldNode = addDotNode(e.field);
        addDotEdge(currentParent, fieldNode);
        return null;
    }

    @Override
    public Void visitFunCallExpr(FunCallExpr e) {
        String node = addDotNode("FunCallExpr");
        addDotEdge(currentParent, node);
        currentParent = node;
        String idNode = addDotNode(e.funcId);
        addDotEdge(currentParent, idNode);
        for (Expr exp : e.paramList) {
            currentParent = node;
            exp.accept(this);
        }
        return null;
    }

    @Override
    public Void visitIntLiteralExpr(IntLiteralExpr e) {
        String node = addDotNode("IntLiteral("+Integer.toString(e.value)+")");
        addDotEdge(currentParent, node);
        return null;
    }

    @Override
    public Void visitSizeOfExpr(SizeOfExpr e) {
        String node = addDotNode("SizeOfExpr");
        addDotEdge(currentParent, node);
        currentParent = node;
        e.typeMeasured.accept(this);
        return null;
    }

    @Override
    public Void visitStringLiteralExpr(StringLiteralExpr e) {
        String node = addDotNode("StrLiteral("+e.value+")");
        addDotEdge(currentParent, node);
        return null;
    }

    @Override
    public Void visitTypeCastExpr(TypeCastExpr e) {
        String node = addDotNode("TypecastExpr");
        addDotEdge(currentParent, node);
        currentParent = node;
        e.type.accept(this);
        currentParent = node;
        e.exp.accept(this);
        return null;
    }

    @Override
    public Void visitValueAtExpr(ValueAtExpr e) {
        String node = addDotNode("ValueAtExpr");
        addDotEdge(currentParent, node);
        currentParent = node;
        e.exp.accept(this);
        return null;
    }

    @Override
    public Void visitVarExpr(VarExpr e) {
        String node = addDotNode("VarExpr("+e.varId+")");
        addDotEdge(currentParent, node);
        return null;
    }
}
