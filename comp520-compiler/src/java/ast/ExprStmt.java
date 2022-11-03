package ast;

import util.Position;

public class ExprStmt extends Stmt{

    public Expr exp;

    public ExprStmt(Position pos, Expr exp) {
        super(pos);
        this.exp = exp;
    }

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitExprStmt(this);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
