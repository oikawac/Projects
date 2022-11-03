package ast;

import util.Position;

public class TypeCastExpr extends Expr {

    public Expr exp;

    public TypeCastExpr(Position pos, Type type, Expr exp) {
        super(pos);
        this.type = type;
        this.exp = exp;
    }

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitTypeCastExpr(this);
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
