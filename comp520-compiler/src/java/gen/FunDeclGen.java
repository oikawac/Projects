package gen;

import ast.*;
import gen.asm.*;

import java.util.Collections;
import java.util.HashMap;

import static java.util.Collections.reverse;

/**
 * A visitor that produces code for a function declaration and local var declarations
 */
public class FunDeclGen implements ASTVisitor<Void> {

    private AssemblyProgram asmProg;
    private AssemblyProgram.Section data;

    public FunDeclGen(AssemblyProgram asmProg, AssemblyProgram.Section data) {
        this.asmProg = asmProg;
        this.data = data;
    }
    private Void mcmalloc_gen(AssemblyProgram.Section text) {
        //one integer should exist at fp 0
        text.emit(OpCode.LW, Register.Arch.a0, Register.Arch.fp, 0);
        text.emit(OpCode.LI, Register.Arch.v0, 9);
        text.emit(OpCode.SYSCALL);
        text.emit(OpCode.SW, Register.Arch.v0, Register.Arch.fp, 12);
        text.emit(OpCode.JR, Register.Arch.ra);
        return null;
    }
    private Void print_i_gen(AssemblyProgram.Section text) {
        //one integer should exist at fp 0
        text.emit(OpCode.LW, Register.Arch.a0, Register.Arch.fp, 0);
        text.emit(OpCode.LI, Register.Arch.v0, 1);
        text.emit(OpCode.SYSCALL);
        text.emit(OpCode.JR, Register.Arch.ra);
        return null;
    }
    private Void print_c_gen(AssemblyProgram.Section text) {
        //one char should exist at fp 0
        text.emit(OpCode.LW, Register.Arch.a0, Register.Arch.fp, 0);
        text.emit(OpCode.LI, Register.Arch.v0, 11);
        text.emit(OpCode.SYSCALL);
        text.emit(OpCode.JR, Register.Arch.ra);
        return null;
    }
    private Void print_s_gen(AssemblyProgram.Section text) {
        //one address should exist at fp 0
        text.emit(OpCode.LW, Register.Arch.a0, Register.Arch.fp, 0);
        text.emit(OpCode.LI, Register.Arch.v0, 4);
        text.emit(OpCode.SYSCALL);
        text.emit(OpCode.JR, Register.Arch.ra);
        return null;
    }
    private Void read_i_gen(AssemblyProgram.Section text) {
        text.emit(OpCode.LI, Register.Arch.v0, 5);
        text.emit(OpCode.SYSCALL);
        text.emit(OpCode.SW, Register.Arch.v0, Register.Arch.fp, 8);
        text.emit(OpCode.JR, Register.Arch.ra);
        return null;
    }
    private Void read_c_gen(AssemblyProgram.Section text) {
        text.emit(OpCode.LI, Register.Arch.v0, 12);
        text.emit(OpCode.SYSCALL);
        text.emit(OpCode.SB, Register.Arch.v0, Register.Arch.fp, 8);
        text.emit(OpCode.JR, Register.Arch.ra);
        return null;
    }

    @Override
    public Void visitFunDecl(FunDecl p) {
        AssemblyProgram.Section text = asmProg.newSection(AssemblyProgram.Section.Type.TEXT);
        text.emit(new Comment(p.funcId));
        Label lbl = Label.create();
        p.label = lbl;
        text.emit(lbl);
        if (p.funcId.equals("mcmalloc")) {
            p.calleeBottomRetOffset = 12;
            p.calleeRaOffset = 8;
            p.calleeFpOffset = 4;
            p.stackFootprintSize = 16;
            return mcmalloc_gen(text);
        }
        if (p.funcId.equals("print_i")) {
            p.calleeBottomRetOffset = 8;
            p.calleeRaOffset = 8;
            p.calleeFpOffset = 4;
            p.stackFootprintSize = 12;
            return print_i_gen(text);
        }
        if (p.funcId.equals("print_c")) {
            p.calleeBottomRetOffset = 8;
            p.calleeRaOffset = 8;
            p.calleeFpOffset = 4;
            p.stackFootprintSize = 12;
            return print_c_gen(text);
        }
        if (p.funcId.equals("print_s")) {
            p.calleeBottomRetOffset = 8;
            p.calleeRaOffset = 8;
            p.calleeFpOffset = 4;
            p.stackFootprintSize = 12;
            return print_s_gen(text);
        }
        if (p.funcId.equals("read_i")){
            p.calleeBottomRetOffset = 8;
            p.calleeRaOffset = 4;
            p.calleeFpOffset = 0;
            p.stackFootprintSize = 12;
            return read_i_gen(text);
        }
        if (p.funcId.equals("read_c")){
            p.calleeBottomRetOffset = 8;
            p.calleeRaOffset = 4;
            p.calleeFpOffset = 0;
            p.stackFootprintSize = 12;
            return read_c_gen(text);
        }
        int argSize = 0;
        for (VarDecl vd : p.block.functionAttachedVarDeclList) {
            int size = 0;
            if (vd.type.getBinaryType().stores == BinaryType.Value.ARRAY)
                size = 4;
            else if (vd.type.getBinaryType().stores == BinaryType.Value.STRUCT)
                size = vd.type.getBinaryType().size;
            else if (vd.type.getBinaryType().stores == BinaryType.Value.NUMBER
                    || vd.type.getBinaryType().stores == BinaryType.Value.POINTER)
                size = 4;
            vd.framePtrOffset = argSize;
            argSize += size;
        }
        int retSize = 0;
        if (p.type.getBinaryType().stores == BinaryType.Value.ARRAY)
            retSize += 4;
        else if (p.type.getBinaryType().stores == BinaryType.Value.STRUCT)
            retSize += p.type.getBinaryType().size;
        else if (p.type.getBinaryType().stores == BinaryType.Value.NUMBER
                || p.type.getBinaryType().stores == BinaryType.Value.POINTER)
            retSize += 4;
        p.stackFootprintSize = retSize+8+argSize;
        p.calleeFpOffset = argSize;
        p.calleeRaOffset = argSize+4;
        p.calleeBottomRetOffset = argSize+8;
        StmtGen sg = new StmtGen(asmProg, text, data, 0, p.calleeBottomRetOffset, true, 0);
        p.block.accept(sg);
        ReturnStmt ret = new ReturnStmt(null, new EmptyExpr());
        ret.accept(sg);
        return null;
    }

    @Override
    public Void visitVarDecl(VarDecl vd) {
        throw new ShouldNotReach();
    }
    @Override
    public Void visitBlockStmt(BlockStmt b) {
        throw new ShouldNotReach();
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
    public Void visitProgram(Program p) {
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
