package ast;

import java.util.Arrays;
import java.util.List;

public enum Op {
    ADD,
    SUB,
    MUL,
    DIV,
    MOD,
    GT,
    LT,
    GE,
    LE,
    NE,
    EQ,
    OR,
    AND;

    public boolean canOperateOn(Type t) {
        if (this == ADD
            || this == SUB
            || this == MUL
            || this == DIV
            || this == MOD
            || this == GT
            || this == LT
            || this == GE
            || this == LE
            || this == OR
            || this == AND) {
            return t == BaseType.INT;
        } else {
            return (t != BaseType.VOID && !(t instanceof ArrayType) && !(t instanceof StructType));
        }
    }

    public Type producesType() {
        return BaseType.INT;
    }
}
