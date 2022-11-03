package ast;

public interface ASTVisitor<T> {

    class ShouldNotReach extends Error {
        public ShouldNotReach() {
            super("Current visitor should never reach this node");
        }
    }

    T visitProgram(Program p);

    T visitArrayType(ArrayType t);
    T visitBaseType(BaseType t);
    T visitPointerType(PointerType t);
    T visitStructType(StructType t);

    T visitFunDecl(FunDecl d);
    T visitStructTypeDecl(StructTypeDecl d);
    T visitVarDecl(VarDecl d);

    T visitAssignStmt(AssignStmt s);
    T visitBlockStmt(BlockStmt s);
    T visitExprStmt(ExprStmt s);
    T visitIfStmt(IfStmt s);
    T visitReturnStmt(ReturnStmt s);
    T visitWhileStmt(WhileStmt s);

    T visitAddressOfExpr(AddressOfExpr e);
    T visitArrayAccessExpr(ArrayAccessExpr e);
    T visitBinOpExpr(BinOpExpr e);
    T visitCharLiteralExpr(CharLiteralExpr e);
    T visitFieldAccessExpr(FieldAccessExpr e);
    T visitFunCallExpr(FunCallExpr e);
    T visitIntLiteralExpr(IntLiteralExpr e);
    T visitSizeOfExpr(SizeOfExpr e);
    T visitStringLiteralExpr(StringLiteralExpr e);
    T visitTypeCastExpr(TypeCastExpr e);
    T visitValueAtExpr(ValueAtExpr e);
    T visitVarExpr(VarExpr e);

    // to complete ... (should have one visit method for each concrete AST node class)
}
