package gen;

import ast.*;
import gen.asm.*;

import java.util.HashMap;


/**
 * Generates code to evaluate an expression and return the result in a register.
 */
public class ExprGen implements ASTVisitor<Register> {

    private AssemblyProgram asmProg;
    private AssemblyProgram.Section text;
    private AssemblyProgram.Section data;

    private static HashMap<Character, String> specialChars = new HashMap<>();
    static {
        specialChars.put('\t', "\\t");
        specialChars.put('\b', "\\b");
        specialChars.put('\n', "\\n");
        specialChars.put('\r', "\\r");
        specialChars.put('\f', "\\f");
        specialChars.put('\"', "\\\"");
        specialChars.put('\\', "\\");
        specialChars.put('\0', "\\0");
    }

    public ExprGen(AssemblyProgram asmProg, AssemblyProgram.Section text, AssemblyProgram.Section data) {
        this.asmProg = asmProg;
        this.text = text;
        this.data = data;
    }

    @Override
    public Register visitAddressOfExpr(AddressOfExpr e) {
        text.emit(new Comment("address of: "));
        Register addr = e.exp.accept(new AddrGen(asmProg, text, data));
        return addr;
    }

    @Override
    public Register visitArrayAccessExpr(ArrayAccessExpr e) {
        text.emit(new Comment("array access: "));
        Register addr = e.arrayExpr.accept(this);
        Register offset = e.indexExpr.accept(this);
        Register res = Register.Virtual.create();
        Register unitSize = Register.Virtual.create();
        text.emit(OpCode.LI, unitSize, e.arrayExpr.type.getBinaryType().unitSize);
        text.emit(OpCode.MUL, offset, offset, unitSize);
        if (e.type.getBinaryType().stores == BinaryType.Value.NUMBER
                || e.type.getBinaryType().stores == BinaryType.Value.POINTER) {
            text.emit(OpCode.ADD, addr, addr, offset);
            if (e.arrayExpr.type.getBinaryType().unitSize == 4) text.emit(OpCode.LW, res, addr, 0);
            else text.emit(OpCode.LB, res, addr, 0);
        } else if (e.type.getBinaryType().stores == BinaryType.Value.ARRAY) {
            text.emit(OpCode.ADD, res, addr, offset);
        } else if (e.type.getBinaryType().stores == BinaryType.Value.STRUCT) {
            text.emit(OpCode.ADD, res, addr, offset);
        }
        return res;
    }

    @Override
    public Register visitBinOpExpr(BinOpExpr e) {
        Register binRes = Register.Virtual.create();
        if (e.op == Op.ADD) {
            text.emit(new Comment("add:"));
            Register lhsRes = e.lhsExpr.accept(this);
            Register rhsRes = e.rhsExpr.accept(this);
            text.emit(OpCode.ADD, binRes, lhsRes, rhsRes);
        } else if (e.op == Op.SUB) {
            text.emit(new Comment("sub:"));
            Register lhsRes = e.lhsExpr.accept(this);
            Register rhsRes = e.rhsExpr.accept(this);
            text.emit(OpCode.SUB, binRes, lhsRes, rhsRes);
        } else if (e.op == Op.MUL) {
            text.emit(new Comment("mul:"));
            Register lhsRes = e.lhsExpr.accept(this);
            Register rhsRes = e.rhsExpr.accept(this);
            text.emit(OpCode.MUL, binRes, lhsRes, rhsRes);
        } else if (e.op == Op.DIV) {
            text.emit(new Comment("div:"));
            Register lhsRes = e.lhsExpr.accept(this);
            Register rhsRes = e.rhsExpr.accept(this);
            text.emit(OpCode.DIV, lhsRes, rhsRes);
            text.emit(OpCode.MFLO, binRes);
        } else if (e.op == Op.MOD) {
            text.emit(new Comment("mod:"));
            Register lhsRes = e.lhsExpr.accept(this);
            Register rhsRes = e.rhsExpr.accept(this);
            text.emit(OpCode.DIV, lhsRes, rhsRes);
            text.emit(OpCode.MFHI, binRes);
        } else if (e.op == Op.EQ) {
            text.emit(new Comment("eq:"));
            Register lhsRes = e.lhsExpr.accept(this);
            Register rhsRes = e.rhsExpr.accept(this);
            text.emit(OpCode.SUBU, binRes, lhsRes, rhsRes);
            Register one = Register.Virtual.create();
            text.emit(OpCode.LI, one, 1);
            text.emit(OpCode.SLTU, binRes, binRes, one);
        } else if (e.op == Op.NE) {
            text.emit(new Comment("neq:"));
            Register lhsRes = e.lhsExpr.accept(this);
            Register rhsRes = e.rhsExpr.accept(this);
            text.emit(OpCode.SUBU, binRes, lhsRes, rhsRes);
        } else if (e.op == Op.AND) {
            text.emit(new Comment("and:"));
            Register lhsRes = e.lhsExpr.accept(this);
            Label rhsEvalLabel = Label.create();
            Label skipEvalLabel = Label.create();
            text.emit(OpCode.BNEZ, lhsRes, rhsEvalLabel);
            text.emit(OpCode.OR, binRes, lhsRes, Register.Arch.zero);
            text.emit(OpCode.B, skipEvalLabel);
            text.emit(rhsEvalLabel);
            Register rhsRes = e.rhsExpr.accept(this);
            text.emit(OpCode.OR, binRes, rhsRes, Register.Arch.zero);
            text.emit(skipEvalLabel);
        } else if (e.op == Op.OR) {
            text.emit(new Comment("or:"));
            Register lhsRes = e.lhsExpr.accept(this);
            Label rhsEvalLabel = Label.create();
            Label skipEvalLabel = Label.create();
            text.emit(OpCode.OR, binRes, lhsRes, Register.Arch.zero);
            text.emit(OpCode.BNEZ, lhsRes, skipEvalLabel);
            text.emit(rhsEvalLabel);
            Register rhsRes = e.rhsExpr.accept(this);
            text.emit(OpCode.OR, binRes, rhsRes, Register.Arch.zero);
            text.emit(skipEvalLabel);
        } else if (e.op == Op.GT) {
            text.emit(new Comment("gt:"));
            Register lhsRes = e.lhsExpr.accept(this);
            Register rhsRes = e.rhsExpr.accept(this);
            text.emit(OpCode.SLT, binRes, rhsRes, lhsRes);
        } else if (e.op == Op.GE) {
            text.emit(new Comment("ge:"));
            Register lhsRes = e.lhsExpr.accept(this);
            Register rhsRes = e.rhsExpr.accept(this);
            text.emit(OpCode.SLT, binRes, lhsRes, rhsRes);
            text.emit(OpCode.XORI, binRes, binRes, 1);
        } else if (e.op == Op.LT) {
            text.emit(new Comment("lt:"));
            Register lhsRes = e.lhsExpr.accept(this);
            Register rhsRes = e.rhsExpr.accept(this);
            text.emit(OpCode.SLT, binRes, lhsRes, rhsRes);
        } else if (e.op == Op.LE) {
            text.emit(new Comment("le:"));
            Register lhsRes = e.lhsExpr.accept(this);
            Register rhsRes = e.rhsExpr.accept(this);
            text.emit(OpCode.SLT, binRes, rhsRes, lhsRes);
            text.emit(OpCode.XORI, binRes, binRes, 1);
        }
        return binRes;
    }

    @Override
    public Register visitCharLiteralExpr(CharLiteralExpr e) {
        text.emit(new Comment("char literal: "));
        Register res = Register.Virtual.create();
        text.emit(OpCode.LI, res, e.value);
        return res;
    }

    @Override
    public Register visitFieldAccessExpr(FieldAccessExpr e) {
        text.emit(new Comment("field access: "));
        Register addr = e.structExpr.accept(new AddrGen(asmProg, text, data));
        int offset = e.structExpr.type.getBinaryType().fieldNameOffset(e.field);
        BinaryType.Value stored = e.structExpr.type.getBinaryType().field(offset).stores;
        int size = e.structExpr.type.getBinaryType().field(offset).size;
        Register res = Register.Virtual.create();
        if (stored == BinaryType.Value.NUMBER || stored == BinaryType.Value.POINTER) {
            if (size == 1) {
                text.emit(OpCode.LB, res, addr, offset);//return value
            } else if (size == 4) {
                text.emit(OpCode.LW, res, addr, offset);//return value
            }
        } else if (stored == BinaryType.Value.ARRAY) {
            text.emit(OpCode.ADDI, res, addr, offset);//return address of start
        } else if (stored == BinaryType.Value.STRUCT) {
            text.emit(OpCode.ADDI, res, addr, offset);//return address of start
        }
        return res;
    }

    @Override
    public Register visitFunCallExpr(FunCallExpr e) {
        FunDecl fd = e.getDeclaration();
        text.emit(new Comment("fun call ("+e.funcId+"): "));
        text.emit(OpCode.ADDI, Register.Arch.sp, Register.Arch.sp, -fd.stackFootprintSize);
        text.emit(new Comment("save caller state: "));
        text.emit(OpCode.SW, Register.Arch.ra, Register.Arch.sp, fd.calleeRaOffset);
        text.emit(OpCode.SW, Register.Arch.fp, Register.Arch.sp, fd.calleeFpOffset);
        text.emit(new Comment("pass args: "));
        int argOffset = 0;
        for (Expr param : e.paramList) {//pass args on stack
            text.emit(new Comment("arg: ("+param.type.getBinaryType().stores.toString()+"): "));
            if (param.type.getBinaryType().stores == BinaryType.Value.ARRAY) {
                Register paramReg = param.accept(this);
                text.emit(OpCode.SW, paramReg, Register.Arch.sp, argOffset);
                argOffset += 4;
            } else if (param.type.getBinaryType().stores == BinaryType.Value.STRUCT) {
                Register paramReg = param.accept(this);
                Register temp = Register.Virtual.create();
                for (int i=0;i<param.type.getBinaryType().size;i+=4) {
                    text.emit(OpCode.LW, temp, paramReg, i);
                    text.emit(OpCode.SW, temp, Register.Arch.sp, argOffset);
                    argOffset += 4;
                }
            } else if (param.type.getBinaryType().stores == BinaryType.Value.NUMBER
                    || param.type.getBinaryType().stores == BinaryType.Value.POINTER) {
                Register paramReg = param.accept(this);
                if (param.type.getBinaryType().size == 4)
                    text.emit(OpCode.SW, paramReg, Register.Arch.sp, argOffset);
                else text.emit(OpCode.SB, paramReg, Register.Arch.sp, argOffset);
                argOffset += 4;
            }
        }
        text.emit(OpCode.OR, Register.Arch.fp, Register.Arch.sp, Register.Arch.zero);//set fp for callee
        text.emit(OpCode.JAL, e.getDeclaration().label);
        text.emit(new Comment("post return: "));
        Register ret = Register.Virtual.create();
        if (e.getDeclaration().type.getBinaryType().stores == BinaryType.Value.NUMBER
                || e.getDeclaration().type.getBinaryType().stores == BinaryType.Value.POINTER) {
            if (e.getDeclaration().type.getBinaryType().size == 4)
                text.emit(OpCode.LW, ret, Register.Arch.fp, fd.calleeBottomRetOffset);
            else text.emit(OpCode.LB, ret, Register.Arch.fp, fd.calleeBottomRetOffset);
        } else if (e.getDeclaration().type.getBinaryType().stores == BinaryType.Value.ARRAY) {
            text.emit(OpCode.LW, ret, Register.Arch.fp, fd.calleeBottomRetOffset);
        } else if (e.getDeclaration().type.getBinaryType().stores == BinaryType.Value.STRUCT) {
            text.emit(OpCode.ADDI, ret, Register.Arch.fp, fd.calleeBottomRetOffset);
        }
        text.emit(OpCode.LW, Register.Arch.ra, Register.Arch.fp, fd.calleeRaOffset);//restore ra
        text.emit(OpCode.ADDI, Register.Arch.sp, Register.Arch.fp, fd.stackFootprintSize);//restore sp//todo: evaluate if we can overwrite the return alloc
        text.emit(OpCode.LW, Register.Arch.fp, Register.Arch.fp, fd.calleeFpOffset);//restore fp
        return ret;
    }

    @Override
    public Register visitIntLiteralExpr(IntLiteralExpr e) {
        text.emit(new Comment("int literal:"));
        Register res = Register.Virtual.create();
        text.emit(OpCode.LI, res, e.value);
        return res;
    }

    @Override
    public Register visitSizeOfExpr(SizeOfExpr e) {
        text.emit(new Comment("size of: "));
        Register res = Register.Virtual.create();
        int size = e.typeMeasured.getBinaryType().size;//todo: check this
        text.emit(OpCode.LI, res, size);
        return res;
    }

    @Override
    public Register visitStringLiteralExpr(StringLiteralExpr e) {
        text.emit(new Comment("string literal:"));
        Register res = Register.Virtual.create();//should store address to string literal
        Label strLabel = Label.create();
        data.emit(new Comment("string literal: "));
        data.emit(strLabel);
        StringBuilder s = new StringBuilder();
        s.append("asciiz \"");
        for (char c : e.value.toCharArray()) {
            s.append(specialChars.getOrDefault(c, String.valueOf(c)));
        }
        s.append("\"");
        data.emit(new Directive(s.toString()));
        data.emit(new Directive("align 2"));
        text.emit(OpCode.LA, res, strLabel);
        return res;
    }

    @Override
    public Register visitTypeCastExpr(TypeCastExpr e) {
        return e.exp.accept(this);
    }

    @Override
    public Register visitValueAtExpr(ValueAtExpr e) {
        text.emit(new Comment("value at: "));
        Register addr = e.exp.accept(this);
        Register res = Register.Virtual.create();
        if (e.exp.type.pointedType().getBinaryType().size == 1) {
            text.emit(OpCode.LB, res, addr, 0);
        } else {
            text.emit(OpCode.LW, res, addr, 0);
        }
        return res;
    }

    @Override
    public Register visitVarExpr(VarExpr v) {
        text.emit(new Comment("var "+v.varId+": "));
        Register addr = v.accept(new AddrGen(asmProg, text, data));
        int size = v.getDeclaration().type.getBinaryType().size;
        BinaryType.Value stored = v.getDeclaration().type.getBinaryType().stores;
        if (stored == BinaryType.Value.NUMBER || stored == BinaryType.Value.POINTER) {
            Register res = Register.Virtual.create();
            if (size == 1)
                text.emit(OpCode.LB, res, addr, 0);//return value
            else
                text.emit(OpCode.LW, res, addr, 0);//return value
            return res;
        } else if (stored == BinaryType.Value.ARRAY) {
            return addr; //return address to start of array
        } else if (stored == BinaryType.Value.STRUCT) {
            return addr; //return address to start
        }
        return null;
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
    public Register visitBlockStmt(BlockStmt s) {
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
