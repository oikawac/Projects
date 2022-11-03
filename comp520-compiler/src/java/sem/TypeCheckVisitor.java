package sem;

import ast.*;
import gen.TypeGen;

public class TypeCheckVisitor extends BaseSemanticVisitor<Void> {

	FunDecl currentlyCheckingFun;

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

	@Override
	public Void visitArrayType(ArrayType t) {
		return null;
	}

	@Override
	public Void visitBaseType(BaseType t) {
		return null;
	}

	@Override
	public Void visitPointerType(PointerType t) {
		return null;
	}

	@Override
	public Void visitStructType(StructType t) {
		return null;
	}

	@Override
	public Void visitFunDecl(FunDecl d) {
		currentlyCheckingFun = d;
		d.type.accept(this);
		d.block.accept(this);
		return null;
	}

	@Override
	public Void visitStructTypeDecl(StructTypeDecl d) {
		for (VarDecl vd : d.varDeclList) {
			vd.accept(this);
		}
		return null;
	}

	@Override
	public Void visitVarDecl(VarDecl d) {
		if (d.type().is(BaseType.VOID))
			error(d, "variable has incomplete type VOID");
		return null;
	}

	@Override
	public Void visitAssignStmt(AssignStmt s) {
		s.identifierExpr.accept(this);
		if (!s.identifierExpr.isLValue()) {
			error(s, "can only assign to lvalue");
		}
		s.newValueExpr.accept(this);
		if (s.identifierExpr.type instanceof ArrayType || s.identifierExpr.type.is(BaseType.VOID)) {
			error(s, "assigned unassignable type");
		}
		if (!s.identifierExpr.type.is(s.newValueExpr.type))
			error(s, "assigned invalid type");
		return null;
	}

	@Override
	public Void visitBlockStmt(BlockStmt s) {
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
		if (!s.conditionExpr.type.is(BaseType.INT)) {
			error(s, "condition must be of type int");
		}
		s.thenBodyStmt.accept(this);
		if (s.elseBodyStmt != null)
			s.elseBodyStmt.accept(this);
		return null;
	}

	@Override
	public Void visitReturnStmt(ReturnStmt s) {
		s.returnValueExpr.accept(this);
		if (s.returnValueExpr.isEmpty()) {
			if (!currentlyCheckingFun.type().is(BaseType.VOID))
				error(s, "return expression of void for non-void function");
		} else if (!s.returnValueExpr.type.is(currentlyCheckingFun.type()))
			error(s, "return expression of wrong type");
		return null;
	}

	@Override
	public Void visitWhileStmt(WhileStmt s) {
		s.conditionExpr.accept(this);
		if (!s.conditionExpr.type.is(BaseType.INT)) {
			error(s, "condition must be of type int");
		}
		s.bodyStmt.accept(this);
		return null;
	}

	//Exprs
	@Override
	public Void visitAddressOfExpr(AddressOfExpr e) {
		e.exp.accept(this);
		if (!e.exp.isLValue()) {
			error(e.exp, "can only take address of lvalue");
		}
		e.type = new PointerType(e.exp.type.getPosition(),e.exp.type);
		return null;
	}

	@Override
	public Void visitArrayAccessExpr(ArrayAccessExpr e) {
		e.arrayExpr.accept(this);
		e.indexExpr.accept(this);
		if (!e.indexExpr.type.is(BaseType.INT))
			error(e, "array index must by of type int");
		if (e.arrayExpr.type.indexable())
			e.type = e.arrayExpr.type.indexedType();
		else {
			error(e, "expression is not indexable");
			e.type = BaseType.VOID;
		}
		return null;
	}

	@Override
	public Void visitBinOpExpr(BinOpExpr e) {
		e.lhsExpr.accept(this);
		e.rhsExpr.accept(this);
		e.type = e.op.producesType();
		if (!e.lhsExpr.type.is(e.rhsExpr.type) || !e.op.canOperateOn(e.lhsExpr.type))
			error(e, "binary operator type mismatch");
		return null;
	}

	@Override
	public Void visitCharLiteralExpr(CharLiteralExpr e) {
		e.type = BaseType.CHAR;
		return null;
	}

	@Override
	public Void visitFieldAccessExpr(FieldAccessExpr e) {
		e.structExpr.accept(this);
		if (e.structExpr.type.fieldAccessible(e.field))
			e.type = (e.structExpr.type.fieldType(e.field));
		else {
			error(e, "expression has no field " + e.field);
			e.type = BaseType.VOID;
		}
		return null;
	}

	@Override
	public Void visitFunCallExpr(FunCallExpr e) {
		for (Expr exp : e.paramList) {
			exp.accept(this);
		}
		if (e.getDeclaration().paramDeclList.size() == e.paramList.size()) {
			for (int i=0;i<e.paramList.size();i++) {
				if (!e.getDeclaration().paramDeclList.get(i).type().is(e.paramList.get(i).type))
					error(e, "function passed wrong type of parameter");
			}
		} else error(e, "function passed wrong number of parameters");
		e.type = e.getDeclaration().type();
		return null;
	}

	@Override
	public Void visitIntLiteralExpr(IntLiteralExpr e) {
		e.type = BaseType.INT;
		return null;
	}

	@Override
	public Void visitSizeOfExpr(SizeOfExpr e) {
		e.type = BaseType.INT;
		return null;
	}

	@Override
	public Void visitStringLiteralExpr(StringLiteralExpr e) {
		e.type = new ArrayType(e.pos,BaseType.CHAR, e.length());
		return null;
	}

	@Override
	public Void visitTypeCastExpr(TypeCastExpr e) {
		e.exp.accept(this);
		if (e.exp.type.is(BaseType.CHAR)) {
			if (!e.type.is(BaseType.INT))
				error(e, "type char can only be cast to type int");
		} else if (e.exp.type instanceof ArrayType) {
			if (!e.type.is(new PointerType(e.pos,e.exp.type.indexedType())))
				error(e, "type array can only be cast to type pointer of same base type");
		} else if (e.exp.type instanceof PointerType) {
			if (!(e.type instanceof PointerType))
				error(e, "type pointer can only be cast to another type pointer");
		} else {
			error(e, "invalid type cast");
		}
		return null;
	}

	@Override
	public Void visitValueAtExpr(ValueAtExpr e) {
		e.exp.accept(this);
		if (e.exp.type.pointer())
			e.type = e.exp.type.pointedType();
		else {
			error(e, "expression is not a pointer");
			e.type = BaseType.VOID;
		}
		return null;
	}

	@Override
	public Void visitVarExpr(VarExpr e) {
		e.type = e.getDeclaration().type();
		return null;
	}
}
