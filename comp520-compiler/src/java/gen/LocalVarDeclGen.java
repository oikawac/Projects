package gen;

import ast.*;
import gen.asm.AssemblyProgram;
import gen.asm.Comment;
import gen.asm.OpCode;
import gen.asm.Register;

public class LocalVarDeclGen implements ASTVisitor<Integer> {

    private AssemblyProgram asmProg;
    private AssemblyProgram.Section text;
    private int fpOffset;

    public LocalVarDeclGen(AssemblyProgram asmProg, AssemblyProgram.Section text, int fpOffset) {
        this.asmProg = asmProg;
        this.text = text;
        this.fpOffset = fpOffset;
    }

    @Override
    public Integer visitVarDecl(VarDecl d) {
        text.emit(new Comment("local var ("+d.varId+"): "));
        int size = d.type.getBinaryType().size;
        int padding = 0;
        while ((size+padding) % 4 != 0) {
            padding++;
        }
        text.emit(OpCode.ADDI, Register.Arch.sp, Register.Arch.sp, -(size+padding));
        fpOffset -= (size+padding);
        d.framePtrOffset = fpOffset;
        return fpOffset;
    }

    @Override
    public Integer visitProgram(Program p) {
        throw new ShouldNotReach();
    }
    @Override
    public Integer visitArrayType(ArrayType t) {
        throw new ShouldNotReach();
    }
    @Override
    public Integer visitBaseType(BaseType t) {
        throw new ShouldNotReach();
    }
    @Override
    public Integer visitPointerType(PointerType t) {
        throw new ShouldNotReach();
    }
    @Override
    public Integer visitStructType(StructType t) {
        throw new ShouldNotReach();
    }
    @Override
    public Integer visitFunDecl(FunDecl d) {
        throw new ShouldNotReach();
    }
    @Override
    public Integer visitStructTypeDecl(StructTypeDecl d) {
        throw new ShouldNotReach();
    }
    @Override
    public Integer visitAssignStmt(AssignStmt s) {
        throw new ShouldNotReach();
    }
    @Override
    public Integer visitBlockStmt(BlockStmt s) {
        throw new ShouldNotReach();
    }
    @Override
    public Integer visitExprStmt(ExprStmt s) {
        throw new ShouldNotReach();
    }
    @Override
    public Integer visitIfStmt(IfStmt s) {
        throw new ShouldNotReach();
    }
    @Override
    public Integer visitReturnStmt(ReturnStmt s) {
        throw new ShouldNotReach();
    }
    @Override
    public Integer visitWhileStmt(WhileStmt s) {
        throw new ShouldNotReach();
    }
    @Override
    public Integer visitAddressOfExpr(AddressOfExpr e) {
        throw new ShouldNotReach();
    }
    @Override
    public Integer visitArrayAccessExpr(ArrayAccessExpr e) {
        throw new ShouldNotReach();
    }
    @Override
    public Integer visitBinOpExpr(BinOpExpr e) {
        throw new ShouldNotReach();
    }
    @Override
    public Integer visitCharLiteralExpr(CharLiteralExpr e) {
        throw new ShouldNotReach();
    }
    @Override
    public Integer visitFieldAccessExpr(FieldAccessExpr e) {
        throw new ShouldNotReach();
    }
    @Override
    public Integer visitFunCallExpr(FunCallExpr e) {
        throw new ShouldNotReach();
    }
    @Override
    public Integer visitIntLiteralExpr(IntLiteralExpr e) {
        throw new ShouldNotReach();
    }
    @Override
    public Integer visitSizeOfExpr(SizeOfExpr e) {
        throw new ShouldNotReach();
    }
    @Override
    public Integer visitStringLiteralExpr(StringLiteralExpr e) {
        throw new ShouldNotReach();
    }
    @Override
    public Integer visitTypeCastExpr(TypeCastExpr e) {
        throw new ShouldNotReach();
    }
    @Override
    public Integer visitValueAtExpr(ValueAtExpr e) {
        throw new ShouldNotReach();
    }
    @Override
    public Integer visitVarExpr(VarExpr e) {
        throw new ShouldNotReach();
    }
}
