package ast;

import util.Position;

public class SizeOfExpr extends Expr {

    public Type typeMeasured;

    public SizeOfExpr(Position pos, Type typeMeasured) {
        super(pos);
        this.typeMeasured = typeMeasured;
    }

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitSizeOfExpr(this);
    }

    @Override
    public boolean isLValue() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
