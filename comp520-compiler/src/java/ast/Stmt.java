package ast;

import util.Position;

public abstract class Stmt implements ASTNode {

    Position pos;

    public Stmt(Position p) {
        this.pos = p;
    }

    public abstract <T> T accept(ASTVisitor<T> v);

    @Override
    public String filePosition() {
        return pos.toString();
    }
}
