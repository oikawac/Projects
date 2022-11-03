package ast;

import util.Position;

public class EmptyStmt extends Stmt {

    public EmptyStmt() {
        super(new Position(0,0));
    }

    @Override
    public <T> T accept(ASTVisitor<T> v) {
        return null;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }
}
