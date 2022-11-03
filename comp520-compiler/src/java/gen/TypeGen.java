package gen;

import ast.*;

import java.util.LinkedList;

public class TypeGen implements ASTVisitor<BinaryType> {

    @Override
    public BinaryType visitArrayType(ArrayType t) {
        return new BinaryType(t.type.accept(this), t.size);
    }

    @Override
    public BinaryType visitBaseType(BaseType t) {
        if (t == BaseType.INT) {
            return new BinaryType(4, BinaryType.Value.NUMBER);
        } else if (t == BaseType.CHAR) {
            return new BinaryType(1, BinaryType.Value.NUMBER);
        } else if (t == BaseType.VOID) {
            return new BinaryType(4, BinaryType.Value.VOID);
        }
        return null;
    }

    @Override
    public BinaryType visitPointerType(PointerType t) {
        return new BinaryType(4, BinaryType.Value.POINTER);
    }

    @Override
    public BinaryType visitStructType(StructType t) {
        LinkedList<BinaryType> fields = new LinkedList<>();
        for (VarDecl vd : t.declaration.varDeclList) {
            BinaryType bt = vd.type.accept(this);
            bt.setName(vd.varId);
            fields.add(bt);
        }
        return new BinaryType(fields);
    }

    @Override
    public BinaryType visitProgram(Program p) {
        throw new ShouldNotReach();
    }
    @Override
    public BinaryType visitFunDecl(FunDecl d) {
        throw new ShouldNotReach();
    }
    @Override
    public BinaryType visitStructTypeDecl(StructTypeDecl d) {
        throw new ShouldNotReach();
    }
    @Override
    public BinaryType visitVarDecl(VarDecl d) {
        throw new ShouldNotReach();
    }
    @Override
    public BinaryType visitAssignStmt(AssignStmt s) {
        throw new ShouldNotReach();
    }
    @Override
    public BinaryType visitBlockStmt(BlockStmt s) {
        throw new ShouldNotReach();
    }
    @Override
    public BinaryType visitExprStmt(ExprStmt s) {
        throw new ShouldNotReach();
    }
    @Override
    public BinaryType visitIfStmt(IfStmt s) {
        throw new ShouldNotReach();
    }
    @Override
    public BinaryType visitReturnStmt(ReturnStmt s) {
        throw new ShouldNotReach();
    }
    @Override
    public BinaryType visitWhileStmt(WhileStmt s) {
        throw new ShouldNotReach();
    }
    @Override
    public BinaryType visitAddressOfExpr(AddressOfExpr e) {
        throw new ShouldNotReach();
    }
    @Override
    public BinaryType visitArrayAccessExpr(ArrayAccessExpr e) {
        throw new ShouldNotReach();
    }
    @Override
    public BinaryType visitBinOpExpr(BinOpExpr e) {
        throw new ShouldNotReach();
    }
    @Override
    public BinaryType visitCharLiteralExpr(CharLiteralExpr e) {
        throw new ShouldNotReach();
    }
    @Override
    public BinaryType visitFieldAccessExpr(FieldAccessExpr e) {
        throw new ShouldNotReach();
    }
    @Override
    public BinaryType visitFunCallExpr(FunCallExpr e) {
        throw new ShouldNotReach();
    }
    @Override
    public BinaryType visitIntLiteralExpr(IntLiteralExpr e) {
        throw new ShouldNotReach();
    }
    @Override
    public BinaryType visitSizeOfExpr(SizeOfExpr e) {
        throw new ShouldNotReach();
    }
    @Override
    public BinaryType visitStringLiteralExpr(StringLiteralExpr e) {
        throw new ShouldNotReach();
    }
    @Override
    public BinaryType visitTypeCastExpr(TypeCastExpr e) {
        throw new ShouldNotReach();
    }
    @Override
    public BinaryType visitValueAtExpr(ValueAtExpr e) {
        throw new ShouldNotReach();
    }
    @Override
    public BinaryType visitVarExpr(VarExpr e) {
        throw new ShouldNotReach();
    }
}
