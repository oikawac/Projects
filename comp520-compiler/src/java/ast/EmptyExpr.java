package ast;

import util.Position;

public class EmptyExpr extends Expr {

    public EmptyExpr() {
        super(new Position(0, 0));
    }

    @Override
    public <T> T accept(ASTVisitor<T> v) {
        return null;
    }

    @Override
    public boolean isLValue() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }
}
