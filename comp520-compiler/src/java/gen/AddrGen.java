package gen;

import ast.*;
import gen.asm.AssemblyProgram;
import gen.asm.Comment;
import gen.asm.OpCode;
import gen.asm.Register;

/**
 * Generates code to calculate the address of an expression and return the result in a register.
 */
public class AddrGen implements ASTVisitor<Register> {


    private AssemblyProgram asmProg;
    private AssemblyProgram.Section text;
    private  AssemblyProgram.Section data;

    public AddrGen(AssemblyProgram asmProg, AssemblyProgram.Section text, AssemblyProgram.Section data) {
        this.asmProg = asmProg;
        this.text = text;
        this.data = data;
    }

    @Override
    public Register visitAddressOfExpr(AddressOfExpr e) {
        throw new ShouldNotReach();
    }
    @Override
    public Register visitArrayAccessExpr(ArrayAccessExpr e) {
        text.emit(new Comment("addr of array access: "));
        Register baseAddr = e.arrayExpr.accept(new ExprGen(asmProg, text, data));
        Register offset = e.indexExpr.accept(new ExprGen(asmProg, text, data));
        Register unitSize = Register.Virtual.create();
        text.emit(OpCode.LI, unitSize, e.arrayExpr.type.getBinaryType().unitSize);
        text.emit(OpCode.MUL, offset, offset, unitSize);
        Register addr = Register.Virtual.create();
        text.emit(OpCode.ADD, addr, baseAddr, offset);
        return addr;
    }
    @Override
    public Register visitBinOpExpr(BinOpExpr e) {
        throw new ShouldNotReach();
    }
    @Override
    public Register visitCharLiteralExpr(CharLiteralExpr e) {
        throw new ShouldNotReach();
    }
    @Override
    public Register visitFieldAccessExpr(FieldAccessExpr e) {
        text.emit(new Comment("addr of field access: "));
        Register baseAddr = e.structExpr.accept(this);
        int offset = e.structExpr.type.getBinaryType().fieldNameOffset(e.field);
        text.emit(OpCode.ADDI, baseAddr, baseAddr, offset);
        return baseAddr;
    }
    @Override
    public Register visitFunCallExpr(FunCallExpr e) {
        throw new ShouldNotReach();
    }
    @Override
    public Register visitIntLiteralExpr(IntLiteralExpr e) {
        throw new ShouldNotReach();
    }
    @Override
    public Register visitSizeOfExpr(SizeOfExpr e) {
        throw new ShouldNotReach();
    }
    @Override
    public Register visitStringLiteralExpr(StringLiteralExpr e) {
        throw new ShouldNotReach();
    }
    @Override
    public Register visitTypeCastExpr(TypeCastExpr e) {
        return e.exp.accept(this);
    }
    @Override
    public Register visitValueAtExpr(ValueAtExpr e) {
        text.emit(new Comment("addr of value at: "));
        Register addr = e.exp.accept(new ExprGen(asmProg, text, data));
        return addr;
    }
    @Override
    public Register visitVarExpr(VarExpr v) {
        text.emit(new Comment("addr of var ("+v.varId+"): "));
        Register addr = Register.Virtual.create();
        if (v.getDeclaration().label == null) { //local var
            text.emit(OpCode.ADDI, addr, Register.Arch.fp, v.getDeclaration().framePtrOffset);
        } else { //global var
            text.emit(OpCode.LA, addr, v.getDeclaration().label);
        }
        return addr;
    }
    @Override
    public Register visitFunDecl(FunDecl p) {
        throw new ShouldNotReach();
    }
    @Override
    public Register visitProgram(Program p) {
        throw new ShouldNotReach();
    }
    @Override
    public Register visitArrayType(ArrayType t) {
        throw new ShouldNotReach();
    }
    @Override
    public Register visitVarDecl(VarDecl vd) {
        throw new ShouldNotReach();
    }
    @Override
    public Register visitAssignStmt(AssignStmt s) {
        throw new ShouldNotReach();
    }
    @Override
    public Register visitBaseType(BaseType bt) {
        throw new ShouldNotReach();
    }
    @Override
    public Register visitPointerType(PointerType t) {
        throw new ShouldNotReach();
    }
    @Override
    public Register visitStructType(StructType t) {
        throw new ShouldNotReach();
    }
    @Override
    public Register visitStructTypeDecl(StructTypeDecl st) {
        throw new ShouldNotReach();
    }
    @Override
    public Register visitBlockStmt(BlockStmt b) {
        throw new ShouldNotReach();
    }
    @Override
    public Register visitExprStmt(ExprStmt s) {
        throw new ShouldNotReach();
    }
    @Override
    public Register visitIfStmt(IfStmt s) {
        throw new ShouldNotReach();
    }
    @Override
    public Register visitReturnStmt(ReturnStmt s) {
        throw new ShouldNotReach();
    }
    @Override
    public Register visitWhileStmt(WhileStmt s) {
        throw new ShouldNotReach();
    }
}
