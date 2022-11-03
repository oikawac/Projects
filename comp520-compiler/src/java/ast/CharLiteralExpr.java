package ast;

import util.Position;

public class CharLiteralExpr extends Expr {


    public char value;

    public CharLiteralExpr(Position pos, char val) {
        super(pos);
        this.value = val;
    }

    public CharLiteralExpr(Position pos, String val) {
        super(pos);
        this.value = val.charAt(0);
    }

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitCharLiteralExpr(this);
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
