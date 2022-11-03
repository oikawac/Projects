package ast;

import gen.BinaryType;
import gen.asm.Label;
import util.Position;

public class VarDecl implements SymbolDeclaration<VarExpr> {

    Position pos;
    public final Type type;
    public final String varId;
    public Label label;
    public Integer framePtrOffset = null;

    public VarDecl(Position pos, Type type, String varId) {
        this.pos = pos;
        this.type = type;
        this.varId = varId;
    }

     public <T> T accept(ASTVisitor<T> v) {
	return v.visitVarDecl(this);
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
        return SymbolClass.VAR;
    }

    @Override
    public Type type() {
        return type;
    }

    @Override
    public String id() {
        return varId;
    }

    @Override
    public void linkToReference(VarExpr ref) {
        ref.declaration = this;
    }

    @Override
    public Label asmLabel() {
        return label;
    }
}
