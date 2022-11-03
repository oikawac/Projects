package ast;

import util.Position;

import java.util.List;

public class FunCallExpr extends Expr implements SymbolReference {

    public List<Expr> paramList;
    public String funcId;
    FunDecl declaration; //filled out by name analyser

    public FunCallExpr(Position pos, String id, List<Expr> paramList) {
        super(pos);
        this.funcId=id;
        this.paramList = paramList;
    }

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitFunCallExpr(this);
    }

    @Override
    public boolean isLValue() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public FunDecl getDeclaration() {
        return declaration;
    }
}
