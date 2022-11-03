package ast;

import gen.BinaryType;
import gen.TypeGen;
import util.Position;

public enum BaseType implements Type {
    INT, CHAR, VOID;

    Position pos;
    BinaryType binType;

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitBaseType(this);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public String filePosition() {
        return pos.toString();
    }

    @Override
    public boolean indexable() {
        return false;
    }

    @Override
    public Type indexedType() {
        return null;
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
        return this == t;
    }

    @Override
    public Position getPosition() {
        return pos;
    }

    @Override
    public BinaryType getBinaryType() {
        if (binType == null) binType = accept(new TypeGen());
        return binType;
    }
}
