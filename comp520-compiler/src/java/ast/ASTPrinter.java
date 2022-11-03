package ast;

import java.io.PrintWriter;
import java.util.LinkedList;

public class ASTPrinter implements ASTVisitor<Void> {

    private PrintWriter writer;

    int globalTabLevel = 0;
    LinkedList<Integer> tabulationStack = new LinkedList<>();

    public ASTPrinter(PrintWriter writer) {
        this.writer = writer;
    }
    

    
    private void tabbedPrint(String s) {
        char[] charArray = s.toCharArray();
        for (char c : charArray) {
            writer.print(c);
            if (c == ',') {
                writer.print('\n');
                for (int i=0;i<globalTabLevel;i++) writer.print(' ');
            }
            if (c == '(') {
                writer.print('\n');
                globalTabLevel += s.length();
                tabulationStack.addLast(s.length());
                for (int i=0;i<globalTabLevel;i++) writer.print(' ');
            }
            if (c == ')') {
                writer.print('\n');
                globalTabLevel -= tabulationStack.removeLast();
                for (int i=0;i<globalTabLevel;i++) writer.print(' ');
            }
        }
    }
    private void tabbedPrint(char c) {
        tabbedPrint(Character.toString(c));
    }
    private void tabbedPrint(int i) {
        tabbedPrint(Integer.toString(i));
    }


    @Override
    public Void visitProgram(Program p) {
        tabbedPrint("Program(");
        String delimiter = "";
        for (StructTypeDecl std : p.structTypeDeclList) {
            tabbedPrint(delimiter);
            delimiter = ",";
            std.accept(this);
        }
        for (VarDecl vd : p.varDeclList) {
            tabbedPrint(delimiter);
            delimiter = ",";
            vd.accept(this);
        }
        for (FunDecl fd : p.funDeclList) {
            tabbedPrint(delimiter);
            delimiter = ",";
            fd.accept(this);
        }
        tabbedPrint(")");
        writer.flush();
        return null;
    }

    @Override
    public Void visitArrayType(ArrayType t) {
        tabbedPrint("ArrayType(");
        t.type.accept(this);
        tabbedPrint(",");
        tabbedPrint(t.size);
        tabbedPrint(")");
        return null;
    }

    @Override
    public Void visitBaseType(BaseType t) {
        tabbedPrint(t.toString());
        return null;
    }

    @Override
    public Void visitPointerType(PointerType t) {
        tabbedPrint("PointerType(");
        t.pointedType.accept(this);
        tabbedPrint(")");
        return null;
    }

    @Override
    public Void visitStructType(StructType t) {
        tabbedPrint("StructType(");
        tabbedPrint(t.structId);
        tabbedPrint(")");
        return null;
    }

    @Override
    public Void visitFunDecl(FunDecl d) {
        tabbedPrint("FunDecl(");
        d.type.accept(this);
        tabbedPrint(","+d.funcId +",");
        for (VarDecl vd : d.paramDeclList) {
            vd.accept(this);
            tabbedPrint(",");
        }
        d.block.accept(this);
        tabbedPrint(")");
        return null;
    }

    @Override
    public Void visitStructTypeDecl(StructTypeDecl d) {
        tabbedPrint("StructTypeDecl(");
        d.structType.accept(this);
        for (VarDecl vd : d.varDeclList) {
            tabbedPrint(",");
            vd.accept(this);
        }
        tabbedPrint(")");
        return null;
    }

    @Override
    public Void visitVarDecl(VarDecl d) {
        tabbedPrint("VarDecl(");
        d.type.accept(this);
        tabbedPrint(","+d.varId);
        tabbedPrint(")");
        return null;
    }

    @Override
    public Void visitAssignStmt(AssignStmt s) {
        tabbedPrint("Assign(");
        s.identifierExpr.accept(this);
        tabbedPrint(",");
        s.newValueExpr.accept(this);
        tabbedPrint(")");
        return null;
    }

    @Override
    public Void visitBlockStmt(BlockStmt s) {
        tabbedPrint("Block(");
        String delimeter = "";
        for (VarDecl vd: s.varDeclList) {
            tabbedPrint(delimeter);
            delimeter=",";
            vd.accept(this);
        }
        for (Stmt stmt: s.stmtList) {
            tabbedPrint(delimeter);
            delimeter=",";
            stmt.accept(this);
        }
        tabbedPrint(")");
        return null;
    }

    @Override
    public Void visitExprStmt(ExprStmt s) {
        tabbedPrint("ExprStmt(");
        s.exp.accept(this);
        tabbedPrint(")");
        return null;
    }

    @Override
    public Void visitIfStmt(IfStmt s) {
        tabbedPrint("If(");
        s.conditionExpr.accept(this);
        tabbedPrint(",");
        s.thenBodyStmt.accept(this);
        if (!s.elseBodyStmt.isEmpty()) {
            tabbedPrint(",");
            s.elseBodyStmt.accept(this);
        }
        tabbedPrint(")");
        return null;
    }

    @Override
    public Void visitReturnStmt(ReturnStmt s) {
        tabbedPrint("Return(");
        s.returnValueExpr.accept(this);
        tabbedPrint(")");
        return null;
    }

    @Override
    public Void visitWhileStmt(WhileStmt s) {
        tabbedPrint("While(");
        s.conditionExpr.accept(this);
        tabbedPrint(",");
        s.bodyStmt.accept(this);
        tabbedPrint(")");
        return null;
    }

    @Override
    public Void visitAddressOfExpr(AddressOfExpr e) {
        tabbedPrint("AddressOfExpr(");
        e.exp.accept(this);
        tabbedPrint(")");
        return null;
    }

    @Override
    public Void visitArrayAccessExpr(ArrayAccessExpr e) {
        tabbedPrint("ArrayAccessExpr(");
        e.arrayExpr.accept(this);
        tabbedPrint(",");
        e.indexExpr.accept(this);
        tabbedPrint(")");
        return null;
    }

    @Override
    public Void visitBinOpExpr(BinOpExpr e) {
        tabbedPrint("BinOp(");
        e.lhsExpr.accept(this);
        tabbedPrint(",");
        tabbedPrint(e.op.toString());
        tabbedPrint(",");
        e.rhsExpr.accept(this);
        tabbedPrint(")");
        return null;
    }

    @Override
    public Void visitCharLiteralExpr(CharLiteralExpr e) {
        tabbedPrint("ChrLiteral(");
        tabbedPrint(e.value);
        tabbedPrint(")");
        return null;
    }

    @Override
    public Void visitFieldAccessExpr(FieldAccessExpr e) {
        tabbedPrint("FieldAccessExpr(");
        e.structExpr.accept(this);
        tabbedPrint(",");
        tabbedPrint(e.field);
        tabbedPrint(")");
        return null;
    }

    @Override
    public Void visitFunCallExpr(FunCallExpr e) {
        tabbedPrint("FunCallExpr(");
        tabbedPrint(e.funcId);
        for (Expr exp : e.paramList) {
            tabbedPrint(",");
            exp.accept(this);
        }
        tabbedPrint(")");
        return null;
    }

    @Override
    public Void visitIntLiteralExpr(IntLiteralExpr e) {
        tabbedPrint("IntLiteral(");
        tabbedPrint(e.value);
        tabbedPrint(")");
        return null;
    }

    @Override
    public Void visitSizeOfExpr(SizeOfExpr e) {
        tabbedPrint("SizeOfExpr(");
        e.typeMeasured.accept(this);
        tabbedPrint(")");
        return null;
    }

    @Override
    public Void visitStringLiteralExpr(StringLiteralExpr e) {
        tabbedPrint("StrLiteral(");
        tabbedPrint(e.value);
        tabbedPrint(")");
        return null;
    }

    @Override
    public Void visitTypeCastExpr(TypeCastExpr e) {
        tabbedPrint("TypecastExpr(");
        e.type.accept(this);
        tabbedPrint(",");
        e.exp.accept(this);
        tabbedPrint(")");
        return null;
    }

    @Override
    public Void visitValueAtExpr(ValueAtExpr e) {
        tabbedPrint("ValueAtExpr(");
        e.exp.accept(this);
        tabbedPrint(")");
        return null;
    }

    @Override
    public Void visitVarExpr(VarExpr e) {
        tabbedPrint("VarExpr(");
        tabbedPrint(e.varId);
        tabbedPrint(")");
        return null;
    }
}
