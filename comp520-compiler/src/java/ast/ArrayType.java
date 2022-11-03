package ast;

import gen.BinaryType;
import gen.TypeGen;
import util.Position;

public class ArrayType implements Type {

    Position pos;
    BinaryType binType;

    public Type type;
    public int size;

    public ArrayType(Position p, Type type, int size) {
        this.pos = p;
        this.type = type;
        this.size = size;
    }


    public <T> T accept(ASTVisitor<T> v) {
        return v.visitArrayType(this);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean indexable() {
        return true;
    }

    @Override
    public Type indexedType() {
        return type;
    }

    @Override
    public boolean fieldAccessible(String field) {
        return false;
    }

    @Override
    public Type fieldType(String field) {
        return null;
    }

    @Override
    public boolean pointer() {
        return false;
    }

    @Override
    public Type pointedType() {
        return null;
    }

    @Override
    public boolean is(Type t) {
        if (t instanceof ArrayType)
            return type.is(((ArrayType)t).type);
        return false;
    }

    @Override
    public String filePosition() {
        return pos.toString();
    }

    @Override
    public Position getPosition() {
        return pos;
    }

    @Override
    public BinaryType getBinaryType() {
        if (binType == null) binType = this.accept(new TypeGen());
        return binType;
    }
}
