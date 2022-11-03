package parser;


import ast.*;
import lexer.Token;
import lexer.Token.TokenClass;
import lexer.Tokeniser;
import util.Position;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Parser {
    private Token token;

    // use for backtracking (useful for distinguishing decls from procs when parsing a program for instance)
    private Queue<Token> buffer = new LinkedList<>();

    private final Tokeniser tokeniser;



    public Parser(Tokeniser tokeniser) {
        this.tokeniser = tokeniser;
    }

    public Program parse() {
        // get the first token
        nextToken();

        return parseProgram();
    }


    public int getErrorCount() {
        return error;
    }

    private int error = 0;
    private Token lastErrorToken;

    private void error(String str) {
        System.out.println("Parsing error: expected "+str+" found ("+token+") at "+token.position);
        error++;
    }
    private void error(Token.TokenClass... expected) {

        if (lastErrorToken == token) {
            // skip this error, same token causing trouble
            return;
        }

        StringBuilder sb = new StringBuilder();
        String sep = "";
        for (Token.TokenClass e : expected) {
            sb.append(sep);
            sb.append(e);
            sep = "|";
        }
        System.out.println("Parsing error: expected ("+sb+") found ("+token+") at "+token.position);

        error++;
        lastErrorToken = token;
    }

    /*
     * Look ahead the i^th element from the stream of token.
     * i should be >= 1
     */
    private Token lookAhead(int i) {
        // ensures the buffer has the element we want to look ahead
        while (buffer.size() < i)
            buffer.add(tokeniser.nextToken());
        assert buffer.size() >= i;

        int cnt=1;
        for (Token t : buffer) {
            if (cnt == i)
                return t;
            cnt++;
        }

        assert false; // should never reach this
        return null;
    }

    private boolean lookAheadIs(int i, Token.TokenClass... expected) {
        Token.TokenClass t = lookAhead(i).tokenClass;
        boolean result = false;
        for (Token.TokenClass e : expected)
            result |= (e == t);
        return result;
    }


    /*
     * Consumes the next token from the tokeniser or the buffer if not empty.
     */
    private Token[] nextToken() {
        Token prev = token;
        if (!buffer.isEmpty())
            token = buffer.remove();
        else
            token = tokeniser.nextToken();
        return new Token[]{prev, token};
    }

    /*
     * If the current token is equals to the expected one, then skip it, otherwise report an error.
     * Returns the expected token or null if an error occurred.
     */
    private Token expect(Token.TokenClass... expected) {
        for (Token.TokenClass e : expected) {
            if (e == token.tokenClass) {
                Token cur = token;
                nextToken();
                return cur;
            }
        }
        error(expected);
        nextToken();
        return new Token();
    }

    /*
     * Returns true if the current token is equals to any of the expected ones.
     */
    private boolean accept(Token.TokenClass... expected) {
        boolean result = false;
        for (Token.TokenClass e : expected)
            result |= (e == token.tokenClass);
        return result;
    }

    private static TokenClass[] firstOfInclude = new TokenClass[]{TokenClass.INCLUDE};
    private static TokenClass[] firstOfStructDecl = new TokenClass[]{TokenClass.STRUCT};
    private static TokenClass[] firstOfVarDecl = new TokenClass[]{TokenClass.INT, TokenClass.CHAR, TokenClass.VOID, TokenClass.STRUCT};
    private static TokenClass[] firstOfFunDecl = new TokenClass[]{TokenClass.INT, TokenClass.CHAR, TokenClass.VOID, TokenClass.STRUCT};
    private static TokenClass[] firstOfType = new TokenClass[]{TokenClass.INT, TokenClass.CHAR, TokenClass.VOID, TokenClass.STRUCT};
    private static TokenClass[] firstOfSeveralParam = new TokenClass[]{TokenClass.INT, TokenClass.CHAR, TokenClass.VOID, TokenClass.STRUCT};
    private static TokenClass[] firstOfStmt = new TokenClass[]{TokenClass.LBRA, TokenClass.WHILE, TokenClass.IF, TokenClass.RETURN,TokenClass.LPAR,
            TokenClass.IDENTIFIER, TokenClass.INT_LITERAL, TokenClass.MINUS, TokenClass.PLUS, TokenClass.CHAR_LITERAL, TokenClass.STRING_LITERAL,
            TokenClass.ASTERIX, TokenClass.AND, TokenClass.SIZEOF};
    private static TokenClass[] firstOfExp = new TokenClass[]{TokenClass.LPAR, TokenClass.IDENTIFIER, TokenClass.INT_LITERAL, TokenClass.MINUS,
            TokenClass.PLUS, TokenClass.CHAR_LITERAL, TokenClass.STRING_LITERAL, TokenClass.ASTERIX, TokenClass.AND, TokenClass.SIZEOF};

    private Program parseProgram() {
        parseIncludeStar();
        List<StructTypeDecl> stds = parseStructDeclStar();
        List<VarDecl> vds = parseVarDeclStar();
        List<FunDecl> fds = parseFunDeclStar();
        expect(TokenClass.EOF);
        return new Program(stds, vds, fds);
    }

    private void parseIncludeStar() {
        if (accept(firstOfInclude)) {
            parseInclude();
            parseIncludeStar();
        }
    }
    private void parseInclude() {
        expect(TokenClass.INCLUDE);
        expect(TokenClass.STRING_LITERAL);
    }

    private List<StructTypeDecl> parseStructDeclStar() {
        List<StructTypeDecl> structTypeDeclList = new LinkedList<>();
        if (accept(firstOfStructDecl) && lookAheadIs(2, TokenClass.LBRA)) {
            structTypeDeclList.add(parseStructDecl());
            structTypeDeclList.addAll(parseStructDeclStar());
        }
        return structTypeDeclList;
    }
    private StructTypeDecl parseStructDecl() {
        expect(TokenClass.STRUCT);
        Token id = expect(TokenClass.IDENTIFIER);
        expect(TokenClass.LBRA);
        List<VarDecl> varDecls = parseVarDeclPlus();
        expect(TokenClass.RBRA);
        expect(TokenClass.SC);
        if (!id.isValid()) {
            return new StructTypeDecl(id.position, new StructType(id.position,""), varDecls);
        }
        return new StructTypeDecl(id.position, new StructType(id.position, id.data), varDecls);
    }

    private List<VarDecl> parseVarDeclPlus() {
        List<VarDecl> varDeclList = new LinkedList<>();
        varDeclList.add(parseVarDecl());
        varDeclList.addAll(parseVarDeclStar());
        return varDeclList;
    }
    private List<VarDecl> parseVarDeclStar() {
        List<VarDecl> varDeclList = new LinkedList<>();
        if (accept(firstOfVarDecl)
                && (lookAheadIs(2, TokenClass.SC)
                || lookAheadIs(3, TokenClass.SC)
                || lookAheadIs(4, TokenClass.SC)
                || lookAheadIs(2, TokenClass.LSBR)
                || lookAheadIs(3, TokenClass.LSBR)
                || lookAheadIs(4, TokenClass.LSBR))) {
            varDeclList.add(parseVarDecl());
            varDeclList.addAll(parseVarDeclStar());
        }
        return varDeclList;
    }
    private VarDecl parseVarDecl() {
        Type type = parseType();
        Token id = expect(TokenClass.IDENTIFIER);
        if (!id.isValid()) {
            return new VarDecl(id.position, type,"");
        }
        Token i = new Token();
        if (accept(TokenClass.LSBR)) {
            nextToken();
            i = expect(TokenClass.INT_LITERAL);
            expect(TokenClass.RSBR);
        }
        expect(TokenClass.SC);
        if (!i.isValid()) return new VarDecl(id.position, type, id.data);
        else return new VarDecl(id.position, new ArrayType(type.getPosition(), type, Integer.parseInt(i.data)), id.data);
    }

    private Type parseType() {
        Type type;
        if (accept(TokenClass.STRUCT)) {
            nextToken();
            Token id = expect(TokenClass.IDENTIFIER);
            if (!id.isValid()) {
                type = new StructType(id.position,"");
            } else {
                type = new StructType(id.position, id.data);
            }
        } else {
            Token t = expect(TokenClass.INT, TokenClass.CHAR,TokenClass.VOID);
            if (!t.isValid()) {
                type = BaseType.VOID;
            } else if (t.tokenClass == TokenClass.INT) {
                type = BaseType.INT;
            } else if (t.tokenClass == TokenClass.CHAR) {
                type = BaseType.CHAR;
            } else {
                type = BaseType.VOID;
            }
        }
        if (parseAsterixOption()) {
            return new PointerType(type.getPosition(),type);
        } else {
            return type;
        }
    }
    private boolean parseAsterixOption() {
        if (accept(TokenClass.ASTERIX)) {
            nextToken();
            return true;
        }
        return false;
    }

    private List<FunDecl> parseFunDeclStar() {
        List<FunDecl> funDeclList = new LinkedList<>();
        if (accept(firstOfFunDecl)) {
            funDeclList.add(parseFunDecl());
            funDeclList.addAll(parseFunDeclStar());
        }
        return funDeclList;
    }
    private FunDecl parseFunDecl() {
        Type type = parseType();
        Token id = expect(TokenClass.IDENTIFIER);
        expect(TokenClass.LPAR);
        List<VarDecl> varDecls = parseSeveralParamOption();
        expect(TokenClass.RPAR);
        BlockStmt block = parseBlock();
        if (!id.isValid()) {
            return new FunDecl(id.position, type, "", varDecls, block);
        }
        return new FunDecl(id.position, type, id.data, varDecls, block);
    }

    private List<VarDecl> parseSeveralParamOption() {
        if (accept(firstOfSeveralParam)) {
            return parseSeveralParam();
        }
        return new LinkedList<>();
    }
    private List<VarDecl> parseSeveralParam() {
        List<VarDecl> varDeclList = new LinkedList<>();
        Type type = parseType();
        Token id = expect(TokenClass.IDENTIFIER);
        if (!id.isValid()) {
            varDeclList.add(new VarDecl(id.position, type, ""));
        } else {
            varDeclList.add(new VarDecl(id.position, type, id.data));
        }
        if (accept(TokenClass.COMMA)) {
            nextToken();
            varDeclList.addAll(parseSeveralParam());
        }
        return varDeclList;
    }

    private BlockStmt parseBlock() {
        Token l = expect(TokenClass.LBRA);
        List<VarDecl> varDeclList = parseVarDeclStar();
        List<Stmt> stmtList = parseStmtStar();
        expect(TokenClass.RBRA);
        return new BlockStmt(l.position, varDeclList,stmtList);
    }

    private List<Stmt> parseStmtStar() {
        List<Stmt> stmtList = new LinkedList<>();
        if (accept(firstOfStmt)) {
            stmtList.add(parseStmt());
            stmtList.addAll(parseStmtStar());
        }
        return stmtList;
    }

    private Stmt parseStmt() {
        if (accept(TokenClass.LBRA)) {
            return parseBlock();
        } else if (accept(TokenClass.WHILE)) {
            Token w = nextToken()[0];
            expect(TokenClass.LPAR);
            Expr exp = parseExp();
            expect(TokenClass.RPAR);
            Stmt stmt = parseStmt();
            return new WhileStmt(w.position,exp, stmt);
        } else if (accept(TokenClass.IF)) {
            Token i = nextToken()[0];
            expect(TokenClass.LPAR);
            Expr exp = parseExp();
            expect(TokenClass.RPAR);
            Stmt ifStmt = parseStmt();
            if (accept(TokenClass.ELSE)) {
                nextToken();
                Stmt elseStmt = parseStmt();
                return new IfStmt(i.position,exp, ifStmt, elseStmt);
            } else {
                return new IfStmt(i.position, exp, ifStmt, new EmptyStmt());
            }
        } else if (accept(TokenClass.RETURN)) {
            Token r = nextToken()[0];
            Expr exp = parseExpOption();
            expect(TokenClass.SC);
            return new ReturnStmt(r.position,exp);
        } else {
            Expr exp = parseExp();
            return parseStmtPrime(exp);
        }
    }
    private Stmt parseStmtPrime(Expr exp) {
        if (accept(TokenClass.ASSIGN)) {
            Token a = nextToken()[0];
            Expr valueExp = parseExp();
            expect(TokenClass.SC);
            return new AssignStmt(a.position,exp, valueExp);
        } else {
            Token s = expect(TokenClass.SC);
            return new ExprStmt(s.position,exp);
        }
    }

    private Expr parseExpOption() {
        if (accept(firstOfExp)) {
            return parseExp();
        }
        return new EmptyExpr();
    }

    private Expr parseExp() {
        Expr expr = parseL8Term();
        return expr;
    }

    private Expr parseL8Term() {
        Expr lhs = parseL7Term();
        while (accept(TokenClass.LOGOR)) {
            Token o = nextToken()[0];
            lhs = new BinOpExpr(o.position,Op.OR, lhs, parseL7Term());
        }
        return lhs;
    }
    private Expr parseL7Term() {
        Expr lhs = parseL6Term();
        while (accept(TokenClass.LOGAND)) {
            Token o = nextToken()[0];
            lhs = new BinOpExpr(o.position,Op.AND, lhs, parseL6Term());
        }
        return lhs;
    }
    private Expr parseL6Term() {
        Expr lhs = parseL5Term();
        while (accept(TokenClass.EQ) || accept(TokenClass.NE)) {
            if (accept(TokenClass.EQ)) {
                Token o = nextToken()[0];
                lhs = new BinOpExpr(o.position,Op.EQ, lhs, parseL5Term());
            } else {
                Token o = nextToken()[0];
                lhs = new BinOpExpr(o.position,Op.NE, lhs, parseL5Term());
            }
        }
        return lhs;
    }
    private Expr parseL5Term() {
        Expr lhs = parseL4Term();
        while (accept(TokenClass.LT) || accept(TokenClass.LE) || accept(TokenClass.GT) || accept(TokenClass.GE)) {
            if (accept(TokenClass.LT)) {
                Token o = nextToken()[0];
                lhs = new BinOpExpr(o.position,Op.LT, lhs, parseL4Term());
            } else if (accept(TokenClass.LE)) {
                Token o = nextToken()[0];
                lhs = new BinOpExpr(o.position,Op.LE, lhs, parseL4Term());
            } else if (accept(TokenClass.GT)) {
                Token o = nextToken()[0];
                lhs = new BinOpExpr(o.position,Op.GT, lhs, parseL4Term());
            } else if (accept(TokenClass.GE)) {
                Token o = nextToken()[0];
                lhs = new BinOpExpr(o.position,Op.GE, lhs, parseL4Term());
            }
        }
        return lhs;
    }
    private Expr parseL4Term() {
        Expr lhs = parseL3Term();
        while (accept(TokenClass.PLUS) || accept(TokenClass.MINUS)) {
            if (accept(TokenClass.PLUS)) {
                Token o = nextToken()[0];
                lhs = new BinOpExpr(o.position,Op.ADD, lhs, parseL3Term());
            } else {
                Token o = nextToken()[0];
                lhs = new BinOpExpr(o.position,Op.SUB, lhs, parseL3Term());
            }
        }
        return lhs;
    }
    private Expr parseL3Term() {
        Expr lhs = parseL2Term();
        while (accept(TokenClass.ASTERIX) || accept(TokenClass.DIV) || accept(TokenClass.REM)) {
            if (accept(TokenClass.ASTERIX)) {
                Token o = nextToken()[0];
                lhs = new BinOpExpr(o.position,Op.MUL, lhs, parseL2Term());
            } else if (accept(TokenClass.DIV)){
                Token o = nextToken()[0];
                lhs = new BinOpExpr(o.position,Op.DIV, lhs, parseL2Term());
            } else {
                Token o = nextToken()[0];
                lhs = new BinOpExpr(o.position,Op.MOD, lhs, parseL2Term());
            }
        }
        return lhs;
    }
    private Expr parseL2Term() {
        if (accept(TokenClass.PLUS)) {
            Token o = nextToken()[0];
            return new BinOpExpr(o.position,Op.ADD, new IntLiteralExpr(o.position,0), parseL2Term());
        } else if (accept(TokenClass.MINUS)) {
            Token o = nextToken()[0];
            return new BinOpExpr(o.position,Op.SUB, new IntLiteralExpr(o.position,0), parseL2Term());
        } else if (accept(TokenClass.ASTERIX)) {
            Token o = nextToken()[0];
            return new ValueAtExpr(o.position,parseL2Term());
        } else if (accept(TokenClass.AND)) {
            Token o = nextToken()[0];
            return new AddressOfExpr(o.position,parseL2Term());
        } else if (accept(TokenClass.LPAR)) {
            if (lookAheadIs(1, firstOfType)) {
                Token l = nextToken()[0];
                Type type = parseType();
                expect(TokenClass.RPAR);
                return new TypeCastExpr(l.position,type, parseL2Term());
            } else {
                return parseL1Term();
            }
        } else {
            return parseL1Term();
        }
    }
    private Expr parseL1Term() {
        if (accept(TokenClass.LPAR)) {
            nextToken();
            Expr interiorExp = parseExp();
            expect(TokenClass.RPAR);
            return parseL1TermPrime(interiorExp);
        }
        if (accept(TokenClass.SIZEOF)) {
            Position p = token.position;
            nextToken();
            expect(TokenClass.LPAR);
            Type type = parseType();
            expect(TokenClass.RPAR);
            return parseL1TermPrime(new SizeOfExpr(p,type));
        } else if (accept(TokenClass.IDENTIFIER)) {
            String id = token.data;
            Position p = token.position;
            nextToken();
            if (accept(TokenClass.LPAR)) {
                nextToken();
                List<Expr> params = parseSeveralInputParametersOption();
                expect(TokenClass.RPAR);
                return parseL1TermPrime(new FunCallExpr(p,id, params));
            }
            return parseL1TermPrime(new VarExpr(p,id));
        } else if (accept(TokenClass.INT_LITERAL)) {
            String i = token.data;
            Position p = token.position;
            nextToken();
            return parseL1TermPrime(new IntLiteralExpr(p,i));
        } else if (accept(TokenClass.CHAR_LITERAL)) {
            String c = token.data;
            Position p = token.position;
            nextToken();
            return parseL1TermPrime(new CharLiteralExpr(p,c));
        } else if (accept(TokenClass.STRING_LITERAL)) {
            String s = token.data;
            Position p = token.position;
            nextToken();
            return parseL1TermPrime(new StringLiteralExpr(p,s));
        } else {
            error("an expression");
            nextToken();
            return new EmptyExpr();
        }
    }
    private Expr parseL1TermPrime(Expr exp) {
        if (accept(TokenClass.LSBR)) {
            Token l = nextToken()[0];
            Expr interiorExp = parseExp();
            expect(TokenClass.RSBR);
            return parseL1TermPrime(new ArrayAccessExpr(l.position,exp, interiorExp));
        } else if (accept(TokenClass.DOT)) {
            Token d = nextToken()[0];
            Token id = expect(TokenClass.IDENTIFIER);
            if (!id.isValid()) {
                return parseL1TermPrime(new FieldAccessExpr(d.position,exp, ""));
            }
            return parseL1TermPrime(new FieldAccessExpr(d.position,exp, id.data));
        }
        return exp;
    }
    private List<Expr> parseSeveralInputParametersOption() {
        if (accept(firstOfExp)) {
            return parseSeveralInputParameters();
        }
        return new LinkedList<>();
    }
    private List<Expr> parseSeveralInputParameters() {
        List<Expr> input = new LinkedList<>();
        input.add(parseExp());
        if (accept(TokenClass.COMMA)) {
            nextToken();
            input.addAll(parseSeveralInputParameters());
        }
        return input;
    }
}
