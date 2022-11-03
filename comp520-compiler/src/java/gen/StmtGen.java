package gen;

import ast.*;
import gen.asm.*;

public class StmtGen implements ASTVisitor<Void> {

    private AssemblyProgram asmProg;
    private AssemblyProgram.Section text;
    private AssemblyProgram.Section data;
    private int fpOffset;
    private int retFpOffset;
    private boolean pushRegistersAtTopOfBlock;
    private int pushRegFpOffset;

    public StmtGen(AssemblyProgram asmProg, AssemblyProgram.Section text, AssemblyProgram.Section data, int fpOffset, int retFpOffset, boolean pushReg, int pushRegFpOffset) {
        this.asmProg = asmProg;
        this.text = text;
        this.data = data;
        this.fpOffset = fpOffset;
        this.retFpOffset = retFpOffset;
        this.pushRegistersAtTopOfBlock = pushReg;
        this.pushRegFpOffset = pushRegFpOffset;
    }

    @Override
    public Void visitAssignStmt(AssignStmt s) {
        text.emit(new Comment("assign: "));
        Register addr = s.identifierExpr.accept(new AddrGen(asmProg, text, data));
        int size = s.identifierExpr.type.getBinaryType().size;
        BinaryType.Value stored = s.identifierExpr.type.getBinaryType().stores;
        if (stored == BinaryType.Value.NUMBER) {
            Register val = s.newValueExpr.accept(new ExprGen(asmProg, text, data));
            if (size == 1) {
                text.emit(OpCode.SB, val, addr, 0);
            } else if (size == 4) {
                text.emit(OpCode.SW, val, addr, 0);
            }
        } else if (stored == BinaryType.Value.ARRAY) {
            Register val = s.newValueExpr.accept(new ExprGen(asmProg, text, data));
            text.emit(OpCode.SW, val, addr, 0);
        } else if (stored == BinaryType.Value.POINTER) {
            Register val = s.newValueExpr.accept(new ExprGen(asmProg, text, data));
            text.emit(OpCode.SW, val, addr, 0);
        } else if (stored == BinaryType.Value.STRUCT) {
            Register val = s.newValueExpr.accept(new ExprGen(asmProg, text, data));
            Register temp = Register.Virtual.create();
            for (int i=0;i<size;i+=4) {
                text.emit(OpCode.LW, temp, val, i);
                text.emit(OpCode.SW, temp, addr, i);
            }
        }
        return null;
    }

    @Override
    public Void visitBlockStmt(BlockStmt s) {
        for (VarDecl vd : s.varDeclList) {
            fpOffset = vd.accept(new LocalVarDeclGen(asmProg, text, fpOffset));
        }
        if (pushRegistersAtTopOfBlock) {
            pushRegFpOffset = fpOffset;
            text.emit(OpCode.PUSH_REGISTERS);
            pushRegistersAtTopOfBlock = false;
        }
        for (Stmt stmt : s.stmtList) {
            stmt.accept(this);
        }
        return null;
    }

    @Override
    public Void visitExprStmt(ExprStmt s) {
        s.exp.accept(new ExprGen(asmProg, text, data));
        return null;
    }

    @Override
    public Void visitIfStmt(IfStmt s) {
        text.emit(new Comment("if: "));
        Register cond = s.conditionExpr.accept(new ExprGen(asmProg, text, data));
        Label falseLbl = Label.create();
        Label bottomLbl = Label.create();
        text.emit(OpCode.BEQZ, cond, falseLbl);
        text.emit(new Comment("then: "));
        s.thenBodyStmt.accept(this);
        text.emit(OpCode.B, bottomLbl);
        text.emit(new Comment("else: "));
        text.emit(falseLbl);
        s.elseBodyStmt.accept(this);
        text.emit(bottomLbl);
        return null;
    }

    @Override
    public Void visitReturnStmt(ReturnStmt s) {
        text.emit(new Comment("return: "));
        if (!s.returnValueExpr.isEmpty()) {
            Register addr = Register.Virtual.create();
            text.emit(OpCode.ADDI, addr, Register.Arch.fp, retFpOffset);
            int size = s.returnValueExpr.type.getBinaryType().size;
            BinaryType.Value stored = s.returnValueExpr.type.getBinaryType().stores;
            if (stored == BinaryType.Value.NUMBER) {
                Register val = s.returnValueExpr.accept(new ExprGen(asmProg, text, data));
                if (size == 1) {
                    text.emit(OpCode.SB, val, addr, 0);
                } else if (size == 4) {
                    text.emit(OpCode.SW, val, addr, 0);
                }
            } else if (stored == BinaryType.Value.ARRAY) {
                Register val = s.returnValueExpr.accept(new ExprGen(asmProg, text, data));
                text.emit(OpCode.SW, val, addr, 0);
            } else if (stored == BinaryType.Value.POINTER) {
                Register val = s.returnValueExpr.accept(new ExprGen(asmProg, text, data));
                text.emit(OpCode.SW, val, addr, 0);
            } else if (stored == BinaryType.Value.STRUCT) {
                Register val = s.returnValueExpr.accept(new ExprGen(asmProg, text, data));
                Register temp = Register.Virtual.create();
                for (int i = 0; i < size; i += 4) {
                    text.emit(OpCode.LW, temp, val, i);
                    text.emit(OpCode.SW, temp, addr, i);
                }
            }
        }
        text.emit(OpCode.ADDI, Register.Arch.sp, Register.Arch.fp, pushRegFpOffset);
        text.emit(OpCode.POP_REGISTERS);
        text.emit(OpCode.JR, Register.Arch.ra);
        return null;
    }

    @Override
    public Void visitWhileStmt(WhileStmt s) {
        text.emit(new Comment("while: "));
        Label topLbl = Label.create();
        Label bottomLbl = Label.create();
        text.emit(topLbl);
        Register res = s.conditionExpr.accept(new ExprGen(asmProg, text, data));
        text.emit(OpCode.BEQZ, res, bottomLbl);
        s.bodyStmt.accept(this);
        text.emit(OpCode.B, topLbl);
        text.emit(bottomLbl);
        return null;
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
    public Void visitBaseType(BaseType t) {
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
    public Void visitFunDecl(FunDecl d) {
        throw new ShouldNotReach();
    }
    @Override
    public Void visitStructTypeDecl(StructTypeDecl d) {
        throw new ShouldNotReach();
    }
    @Override
    public Void visitVarDecl(VarDecl d) {
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
    public Void visitVarExpr(VarExpr e) {
        throw new ShouldNotReach();
    }
}
