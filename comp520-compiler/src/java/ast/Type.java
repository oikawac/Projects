package ast;

import gen.BinaryType;
import util.Position;

public interface Type extends ASTNode {

    public <T> T accept(ASTVisitor<T> v);

    public boolean indexable();
    public Type indexedType();

    public boolean fieldAccessible(String field);
    public Type fieldType(String field);

    public boolean pointer();
    public Type pointedType();

    public boolean is(Type t);

    public Position getPosition();

    public BinaryType getBinaryType();
}
