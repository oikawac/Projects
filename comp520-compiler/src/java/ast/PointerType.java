package ast;

import gen.BinaryType;
import gen.TypeGen;
import util.Position;

public class PointerType implements Type {

    Position pos;
    BinaryType binType;

    public Type pointedType;

    public PointerType(Position pos, Type type) {
        this.pos = pos;
        this.pointedType=type;
    }

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitPointerType(this);
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
        return pointedType;
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
        return true;
    }

    @Override
    public Type pointedType() {
        return pointedType;
    }

    @Override
    public boolean is(Type t) {
        if (t instanceof PointerType)
            return pointedType.is(((PointerType)t).pointedType);
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
