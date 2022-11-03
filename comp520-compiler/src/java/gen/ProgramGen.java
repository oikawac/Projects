package gen;

import ast.*;
import gen.asm.*;

import java.util.HashMap;

/**
 * This visitor should produce a program. Its job is simply to handle the global variable declaration by allocating
 * these in the data section. Then it should call the FunGen function generator to process each function declaration.
 * The label corresponding to each global variable can either be stored in the VarDecl AST node (simplest solution)
 * or store in an ad-hoc data structure (i.e. a Map) that can be passed to the other visitors.
 */
public class ProgramGen implements ASTVisitor<Void> {

    private final AssemblyProgram asmProg;

    private final AssemblyProgram.Section dataSection;
    private final AssemblyProgram.Section textSection;

    private FunDecl mainDecl;


    public ProgramGen(AssemblyProgram asmProg) {
        this.asmProg = asmProg;
        this.dataSection = asmProg.newSection(AssemblyProgram.Section.Type.DATA);
        this.textSection = asmProg.newSection(AssemblyProgram.Section.Type.TEXT);
    }

    private void preamble_gen() {
        textSection.emit(new Comment("Cailean Oikawa"));
        textSection.emit(new Comment("init fp to sp"));
        textSection.emit(OpCode.OR, Register.Arch.fp, Register.Arch.sp, Register.Arch.zero);
        textSection.emit(new Comment("jump to main:"));
        if (mainDecl != null)
            textSection.emit(OpCode.JAL, mainDecl.label);
        else
            textSection.emit(new Comment("there is no main function to jump to"));
    }
    private void postamble_gen() {
        textSection.emit(new Comment("exit syscall:"));
        textSection.emit(OpCode.LI, Register.Arch.v0, 10);
        textSection.emit(OpCode.SYSCALL);
    }

    @Override
    public Void visitFunDecl(FunDecl fd) {
        if (fd.funcId.equals("main")) mainDecl = fd;
        return new FunDeclGen(asmProg, dataSection).visitFunDecl(fd);
    }

    @Override
    public Void visitProgram(Program p) {
        p.varDeclList.forEach(vd -> vd.accept(this));
        p.funDeclList.forEach(fd -> fd.accept(this));
        preamble_gen();
        postamble_gen();
        return null;
    }

    @Override
    public Void visitVarDecl(VarDecl vd) {
        dataSection.emit(new Comment("global var ("+vd.varId+"): "));
        Label varLbl = Label.create();
        vd.label = varLbl;
        dataSection.emit(varLbl);
        int size = vd.type.getBinaryType().size;
        int padding = 0;
        while ((size+padding) % 4 != 0) {
            padding++;
        }
        size += padding;
        dataSection.emit(new Directive("space "+size));
        return null;
    }

    @Override
    public Void visitBaseType(BaseType bt) {
        throw new ShouldNotReach();
    }
    @Override
    public Void visitPointerType(PointerType t) {
        throw new ShouldNotReach();
    }
    @Override
    public Void visitStructType(StructType t) {
        throw new ShouldNotReach();
    }
    @Override
    public Void visitStructTypeDecl(StructTypeDecl st) {
        throw new ShouldNotReach();
    }
    @Override
    public Void visitBlockStmt(BlockStmt b)  {
        throw new ShouldNotReach();
    }
    @Override
    public Void visitExprStmt(ExprStmt s) {
        throw new ShouldNotReach();
    }
    @Override
    public Void visitIfStmt(IfStmt s) {
        throw new ShouldNotReach();
    }
    @Override
    public Void visitReturnStmt(ReturnStmt s) {
        throw new ShouldNotReach();
    }
    @Override
    public Void visitWhileStmt(WhileStmt s) {
        throw new ShouldNotReach();
    }
    @Override
    public Void visitAddressOfExpr(AddressOfExpr e) {
        throw new ShouldNotReach();
    }
    @Override
    public Void visitArrayAccessExpr(ArrayAccessExpr e) {
        throw new ShouldNotReach();
    }
    @Override
    public Void visitBinOpExpr(BinOpExpr e) {
        throw new ShouldNotReach();
    }
    @Override
    public Void visitCharLiteralExpr(CharLiteralExpr e) {
        throw new ShouldNotReach();
    }
    @Override
    public Void visitFieldAccessExpr(FieldAccessExpr e) {
        throw new ShouldNotReach();
    }
    @Override
    public Void visitFunCallExpr(FunCallExpr e) {
        throw new ShouldNotReach();
    }
    @Override
    public Void visitIntLiteralExpr(IntLiteralExpr e) {
        throw new ShouldNotReach();
    }
    @Override
    public Void visitSizeOfExpr(SizeOfExpr e) {
        throw new ShouldNotReach();
    }
    @Override
    public Void visitStringLiteralExpr(StringLiteralExpr e) {
        throw new ShouldNotReach();
    }
    @Override
    public Void visitTypeCastExpr(TypeCastExpr e) {
        throw new ShouldNotReach();
    }
    @Override
    public Void visitValueAtExpr(ValueAtExpr e) {
        throw new ShouldNotReach();
    }
    @Override
    public Void visitArrayType(ArrayType t) {
        throw new ShouldNotReach();
    }
    @Override
    public Void visitAssignStmt(AssignStmt s) {
        throw new ShouldNotReach();
    }
    @Override
    public Void visitVarExpr(VarExpr v) {
        throw new ShouldNotReach();
    }

}
