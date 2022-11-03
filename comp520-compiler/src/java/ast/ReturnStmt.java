package ast;

import util.Position;

public class ReturnStmt extends Stmt {

    public Expr returnValueExpr;

    public ReturnStmt(Position pos, Expr exp) {
        super(pos);
        this.returnValueExpr = exp;
    }

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitReturnStmt(this);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
