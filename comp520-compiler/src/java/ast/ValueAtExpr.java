package ast;

import util.Position;

public class ValueAtExpr extends Expr {

    public Expr exp;

    public ValueAtExpr(Position pos, Expr exp) {
        super(pos);
        this.exp = exp;
    }

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitValueAtExpr(this);
    }

    @Override
    public boolean isLValue() {
        return true;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
