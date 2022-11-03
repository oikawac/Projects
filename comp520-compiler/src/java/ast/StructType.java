package ast;

import gen.BinaryType;
import gen.TypeGen;
import util.Position;

public class StructType implements Type, SymbolReference {

    Position pos;

    BinaryType binType;

    public String structId;
    public StructTypeDecl declaration;

    public StructType(Position pos,String id) {
        this.pos = pos;
        this.structId = id;
    }

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitStructType(this);
    }

    @Override
    public boolean isEmpty() {
        return false;
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
        for (VarDecl vd : declaration.varDeclList) {
            if (vd.id().equals(field)) return true;
        }
        return false;
    }

    @Override
    public Type fieldType(String field) {
        for (VarDecl vd : declaration.varDeclList) {
            if (vd.id().equals(field)) return vd.type();
        }
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
        if (t instanceof StructType)
            return structId.equals(((StructType)t).structId);
        return false;
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

    @Override
    public StructTypeDecl getDeclaration() {
        return declaration;
    }

    @Override
    public String filePosition() {
        return pos.toString();
    }
}
