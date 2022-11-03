package ast;

import util.Position;

public class VarExpr extends Expr implements SymbolReference {

    public final String varId;
    public VarDecl declaration;
    
    public VarExpr(Position pos, String varId){
        super(pos);
        this.varId = varId;
    }

    public <T> T accept(ASTVisitor<T> v) {
	    return v.visitVarExpr(this);
    }

    @Override
    public boolean isLValue() {
        return true;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public VarDecl getDeclaration() {
        return declaration;
    }

}
