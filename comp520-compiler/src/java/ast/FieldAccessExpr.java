package ast;

import util.Position;

public class FieldAccessExpr extends Expr {

    public Expr structExpr;
    public String field;

    public FieldAccessExpr(Position pos, Expr struct, String field) {
        super(pos);
        this.structExpr = struct;
        this.field = field;
    }

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitFieldAccessExpr(this);
    }

    @Override
    public boolean isLValue() {
        return structExpr.isLValue();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
