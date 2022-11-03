package ast;

import util.Position;

public class StringLiteralExpr extends Expr{

    public String value;

    public StringLiteralExpr(Position pos, String val) {
        super(pos);
        value = val;
    }

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitStringLiteralExpr(this);
    }

    @Override
    public boolean isLValue() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    public int length() {
        return value.length();
    }
}
