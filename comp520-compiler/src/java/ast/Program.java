package ast;

import util.Position;

import java.util.List;

public class Program implements ASTNode {

    public final List<StructTypeDecl> structTypeDeclList;
    public final List<VarDecl> varDeclList;
    public final List<FunDecl> funDeclList;

    public Program(List<StructTypeDecl> structTypeDeclList, List<VarDecl> varDeclList, List<FunDecl> funDeclList) {
        this.structTypeDeclList = structTypeDeclList;
	    this.varDeclList = varDeclList;
	    this.funDeclList = funDeclList;
    }

    public <T> T accept(ASTVisitor<T> v) {
	return v.visitProgram(this);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public String filePosition() {
        return (new Position(0,0)).toString();
    }
}
