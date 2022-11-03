package ast;

import gen.asm.Label;
import util.Position;

import java.util.List;

public class StructTypeDecl implements SymbolDeclaration<StructType> {

    Position pos;
    public List<VarDecl> varDeclList;
    StructType structType;
    Label label;

    public StructTypeDecl(Position pos, StructType sType, List<VarDecl> varDeclList) {
        this.pos = pos;
        this.structType = sType;
        this.varDeclList = varDeclList;
    }

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitStructTypeDecl(this);
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
    public SymbolClass symbolClass() {
        return SymbolClass.STRUCT;
    }

    @Override
    public Type type() {
        return structType;
    }

    @Override
    public String id() {
        return structType.structId;
    }

    @Override
    public void linkToReference(StructType ref) {
        ref.declaration = this;
    }

    @Override
    public Label asmLabel() {
        return label;
    }
}
