package ast;

import util.Position;

public class IfStmt extends Stmt {

    public Expr conditionExpr;
    public Stmt thenBodyStmt;
    public Stmt elseBodyStmt;

    public IfStmt(Position pos, Expr cond, Stmt ifBody, Stmt elseBody) {
        super(pos);
        this.conditionExpr = cond;
        this.thenBodyStmt = ifBody;
        this.elseBodyStmt = elseBody;
    }

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitIfStmt(this);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
