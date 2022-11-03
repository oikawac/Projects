package ast;

import util.Position;

public class BinOpExpr extends Expr {

    public Op op;
    public Expr lhsExpr;
    public Expr rhsExpr;

    public BinOpExpr(Position pos, Op op, Expr lhs, Expr rhs) {
        super(pos);
        this.op=op;
        this.lhsExpr =lhs;
        this.rhsExpr =rhs;
    }

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitBinOpExpr(this);
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
