package ast;

import util.Position;

public interface ASTNode {
    public <T> T accept(ASTVisitor<T> v);
    public boolean isEmpty();
    public String filePosition();
}
