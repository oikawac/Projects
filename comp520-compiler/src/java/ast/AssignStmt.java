package ast;

import util.Position;

public class AssignStmt extends Stmt {


    public Expr identifierExpr;
    public Expr newValueExpr;

    public AssignStmt(Position pos, Expr id, Expr val) {
        super(pos);
        this.identifierExpr = id;
        this.newValueExpr = val;
    }

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitAssignStmt(this);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
