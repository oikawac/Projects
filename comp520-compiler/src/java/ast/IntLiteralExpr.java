package ast;

import util.Position;

public class IntLiteralExpr extends Expr{

    public int value;

    public IntLiteralExpr(Position pos, int val) {
        super(pos);
        this.value = val;
    }
    public IntLiteralExpr(Position pos, String val) {
        super(pos);
        this.value = Integer.parseInt(val);
    }

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitIntLiteralExpr(this);
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
