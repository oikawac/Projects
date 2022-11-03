package ast;

import gen.asm.Label;
import util.Position;

import java.util.List;

public class FunDecl implements SymbolDeclaration<FunCallExpr> {

    Position pos;
    public final Type type;
    public final String funcId;
    public final List<VarDecl> paramDeclList;
    public final BlockStmt block;
    public Label label;
    public int calleeBottomRetOffset;
    public int calleeRaOffset;
    public int calleeFpOffset;
    public int stackFootprintSize;

    public FunDecl(Position pos, Type type, String funcId, List<VarDecl> paramDeclList, BlockStmt block) {
        this.pos = pos;
	    this.type = type;
	    this.funcId = funcId;
	    this.paramDeclList = paramDeclList;
	    this.block = block;
    }

    public <T> T accept(ASTVisitor<T> v) {
	return v.visitFunDecl(this);
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
        return SymbolClass.FUN;
    }

    @Override
    public Type type() {
        return type;
    }

    @Override
    public String id() {
        return funcId;
    }

    @Override
    public void linkToReference(FunCallExpr ref) {
        ref.declaration = this;
    }

    @Override
    public Label asmLabel() {
        return label;
    }
}
