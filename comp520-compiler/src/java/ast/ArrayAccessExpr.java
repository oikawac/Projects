package ast;

import util.Position;

public class ArrayAccessExpr extends Expr {

    public Expr arrayExpr;
    public Expr indexExpr;

    public ArrayAccessExpr(Position pos, Expr arr, Expr ind) {
        super(pos);
        this.arrayExpr = arr;
        this.indexExpr = ind;
    }

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitArrayAccessExpr(this);
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
