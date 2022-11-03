package sem;

import ast.*;

public class NameAnalysisVisitor extends BaseSemanticVisitor<Void> {

	Scope globalScope;
	Scope currentScope;

	public NameAnalysisVisitor() {
		globalScope = new Scope();
		currentScope = globalScope;
	}

	@Override
	public Void visitProgram(Program p) {
		for (StructTypeDecl std : p.structTypeDeclList) {
			std.accept(this);
		}
		for (VarDecl vd : p.varDeclList) {
			vd.accept(this);
		}
		for (FunDecl fd : p.funDeclList) {
			fd.accept(this);
		}
		return null;
	}

	//Types
	@Override
	public Void visitArrayType(ArrayType t) {
		t.type.accept(this);
		return null;
	}
	@Override
	public Void visitBaseType(BaseType t) {
		return null;
	}
	@Override
	public Void visitPointerType(PointerType t) {
		t.pointedType.accept(this);
		return null;
	}
	@Override
	public Void visitStructType(StructType t) {
		SymbolDeclaration decl = currentScope.lookupDeclaration(t.structId);
		if (decl == null)
			error(t, "struct "+t.structId+" is not defined");
		else if (decl.symbolClass() != SymbolClass.STRUCT)
			error(t, "struct "+t.structId+" is not defined");
		else
			decl.linkToReference(t);
		return null;
	}

	//Decls
	@Override
	public Void visitFunDecl(FunDecl d) {
		d.type.accept(this);
		SymbolDeclaration decl = currentScope.lookupDeclarationCurrent(d.id());
		if (decl == null)
			currentScope.put(d);
		else if (decl.symbolClass() == SymbolClass.FUN)
			error(d, "function "+d.id()+" has already been declared");
		else
			error(d, "identifier "+d.id()+" has already been declared");
		d.block.functionAttachedVarDeclList = d.paramDeclList;
		d.block.accept(this);
		return null;
	}


	@Override
	public Void visitStructTypeDecl(StructTypeDecl d) {
		SymbolDeclaration decl = currentScope.lookupDeclarationCurrent(d.id());
		if (decl == null)
			currentScope.put(d);
		else if (decl.symbolClass() == SymbolClass.STRUCT)
			error(d, "struct type "+d.id()+" has already been declared");
		else
			error(d, "identifier "+d.id()+" has already been declared");
		Scope localScope = new Scope(currentScope);
		currentScope = localScope;
		for (VarDecl vd : d.varDeclList) {
			vd.accept(this);
		}
		currentScope = currentScope.getOuter();
		return null;
	}

	@Override
	public Void visitVarDecl(VarDecl d) {
		d.type.accept(this);
		SymbolDeclaration decl = currentScope.lookupDeclarationCurrent(d.id());
		if (decl == null)
			currentScope.put(d);
		else if (decl.symbolClass() == SymbolClass.VAR)
			error(d, "variable "+d.id()+" has already been declared");
		else
			error(d, "identifier "+d.id()+" has already been declared");
		return null;
	}

	@Override
	public Void visitAssignStmt(AssignStmt s) {
		s.identifierExpr.accept(this);
		s.newValueExpr.accept(this);
		return null;
	}

	@Override
	public Void visitBlockStmt(BlockStmt s) {
		Scope localScope = new Scope(currentScope);
		currentScope = localScope;
		if (s.functionAttachedVarDeclList != null) {
			for (VarDecl vd : s.functionAttachedVarDeclList) {
				vd.accept(this);
			}
		}
		for (VarDecl vd : s.varDeclList) {
			vd.accept(this);
		}
		for (Stmt stmt : s.stmtList) {
			stmt.accept(this);
		}
		currentScope = currentScope.getOuter();
		return null;
	}

	@Override
	public Void visitExprStmt(ExprStmt s) {
		s.exp.accept(this);
		return null;
	}

	@Override
	public Void visitIfStmt(IfStmt s) {
		s.conditionExpr.accept(this);
		s.thenBodyStmt.accept(this);
		if (!s.elseBodyStmt.isEmpty())
			s.elseBodyStmt.accept(this);
		return null;
	}

	@Override
	public Void visitReturnStmt(ReturnStmt s) {
		s.returnValueExpr.accept(this);
		return null;
	}

	@Override
	public Void visitWhileStmt(WhileStmt s) {
		s.conditionExpr.accept(this);
		s.bodyStmt.accept(this);
		return null;
	}

	//Exprs
	@Override
	public Void visitAddressOfExpr(AddressOfExpr e) {
		e.exp.accept(this);
		return null;
	}

	@Override
	public Void visitArrayAccessExpr(ArrayAccessExpr e) {
		e.arrayExpr.accept(this);
		e.indexExpr.accept(this);
		return null;
	}

	@Override
	public Void visitBinOpExpr(BinOpExpr e) {
		e.lhsExpr.accept(this);
		e.rhsExpr.accept(this);
		return null;
	}

	@Override
	public Void visitCharLiteralExpr(CharLiteralExpr e) {
		return null;
	}

	@Override
	public Void visitFieldAccessExpr(FieldAccessExpr e) {
		e.structExpr.accept(this);
		return null;
	}

	@Override
	public Void visitFunCallExpr(FunCallExpr e) {
		SymbolDeclaration decl = currentScope.lookupDeclaration(e.funcId);
		if (decl == null)
			error(e, "function "+e.funcId+" is not defined");
		else if (decl.symbolClass() != SymbolClass.FUN)
			error(e, "function "+e.funcId+" is not defined");
		else
			decl.linkToReference(e);
		for (Expr exp : e.paramList) {
			exp.accept(this);
		}
		return null;
	}

	@Override
	public Void visitIntLiteralExpr(IntLiteralExpr e) {
		return null;
	}

	@Override
	public Void visitSizeOfExpr(SizeOfExpr e) {
		e.typeMeasured.accept(this);
		return null;
	}

	@Override
	public Void visitStringLiteralExpr(StringLiteralExpr e) {
		return null;
	}

	@Override
	public Void visitTypeCastExpr(TypeCastExpr e) {
		e.type.accept(this);
		e.exp.accept(this);
		return null;
	}

	@Override
	public Void visitValueAtExpr(ValueAtExpr e) {
		e.exp.accept(this);
		return null;
	}

	@Override
	public Void visitVarExpr(VarExpr e) {
		SymbolDeclaration decl = currentScope.lookupDeclaration(e.varId);
		if (decl == null)
			error(e, "variable "+e.varId+" is not defined");
		else if (decl.symbolClass() != SymbolClass.VAR)
			error(e, "variable "+e.varId+" is not defined");
		else
			decl.linkToReference(e);
		return null;
	}
}
