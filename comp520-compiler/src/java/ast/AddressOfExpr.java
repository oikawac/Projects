package ast;

import util.Position;

public class AddressOfExpr extends Expr {
    public Expr exp;

    public AddressOfExpr(Position pos, Expr exp) {
        super(pos);
        this.exp = exp;
    }

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitAddressOfExpr(this);
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
