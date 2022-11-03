package ast;

import util.Position;

import java.util.List;

public class BlockStmt extends Stmt {


    public List<VarDecl> varDeclList;
    public List<Stmt> stmtList;
    public List<VarDecl> functionAttachedVarDeclList;

    public BlockStmt(Position pos, List<VarDecl> varDeclList, List<Stmt> stmtList) {
        super(pos);
        this.varDeclList = varDeclList;
        this.stmtList = stmtList;
    }

    public <T> T accept(ASTVisitor<T> v) {
	    return v.visitBlockStmt(this);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
