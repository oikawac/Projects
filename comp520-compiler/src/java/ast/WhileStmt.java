package ast;

import util.Position;

public class WhileStmt extends Stmt {

    public Stmt bodyStmt;
    public Expr conditionExpr;

    public WhileStmt(Position pos, Expr cond, Stmt body) {
        super(pos);
        this.bodyStmt = body;
        this.conditionExpr = cond;
    }

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitWhileStmt(this);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
