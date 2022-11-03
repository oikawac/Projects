package ast;

import util.Position;

public abstract class Expr implements ASTNode {

    public Position pos;

    public Expr(Position p) { this.pos = p; }

    public Type type; // to be filled in by the type analyser
    public abstract <T> T accept(ASTVisitor<T> v);

    public abstract boolean isLValue();

    @Override
    public String filePosition() {
        return pos.toString();
    }
}
