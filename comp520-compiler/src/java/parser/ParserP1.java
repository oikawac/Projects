package parser;


import lexer.Token;
import lexer.Token.TokenClass;
import lexer.Tokeniser;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


/**
 * @author cdubach
 */
public class ParserP1 {

    private Token token;

    // use for backtracking (useful for distinguishing decls from procs when parsing a program for instance)
    private Queue<Token> buffer = new LinkedList<>();

    private final Tokeniser tokeniser;



    public ParserP1(Tokeniser tokeniser) {
        this.tokeniser = tokeniser;
    }

    public void parse() {
        // get the first token
        nextToken();

        parseProgram();
    }

    public int getErrorCount() {
        return error;
    }

    private int error = 0;
    private Token lastErrorToken;

    private void error(TokenClass... expected) {

        if (lastErrorToken == token) {
            // skip this error, same token causing trouble
            return;
        }

        StringBuilder sb = new StringBuilder();
        String sep = "";
        for (TokenClass e : expected) {
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

    private boolean lookAheadIs(int i, TokenClass... expected) {
        TokenClass t = lookAhead(i).tokenClass;
        boolean result = false;
        for (TokenClass e : expected)
            result |= (e == t);
        return result;
    }


    /*
     * Consumes the next token from the tokeniser or the buffer if not empty.
     */
    private void nextToken() {
        if (!buffer.isEmpty())
            token = buffer.remove();
        else
            token = tokeniser.nextToken();
    }

    /*
     * If the current token is equals to the expected one, then skip it, otherwise report an error.
     * Returns the expected token or null if an error occurred.
     */
    private Token expect(TokenClass... expected) {
        for (TokenClass e : expected) {
            if (e == token.tokenClass) {
                Token cur = token;
                nextToken();
                return cur;
            }
        }

        error(expected);
        return null;
    }

    /*
    * Returns true if the current token is equals to any of the expected ones.
    */
    private boolean accept(TokenClass... expected) {
        boolean result = false;
        for (TokenClass e : expected)
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
    private static TokenClass[] firstOfExpPrime = new TokenClass[]{TokenClass.LT,TokenClass.LE,TokenClass.GT,
            TokenClass.GE,TokenClass.NE,TokenClass.EQ,TokenClass.LOGAND,TokenClass.LOGOR,TokenClass.PLUS,
            TokenClass.MINUS,TokenClass.DIV,TokenClass.ASTERIX,TokenClass.REM,TokenClass.LSBR,TokenClass.DOT};

    private void parseProgram() {
        parseIncludeStar();
        parseStructDeclStar();
        parseVarDeclStar();
        parseFunDeclStar();
        expect(TokenClass.EOF);
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

    private void parseStructDeclStar() {
        if (accept(firstOfStructDecl)) {
            parseStructDecl();
            parseStructDeclStar();
        }
    }
    private void parseStructDecl() {
        expect(TokenClass.STRUCT);
        expect(TokenClass.IDENTIFIER);
        expect(TokenClass.LBRA);
        parseVarDeclPlus();
        expect(TokenClass.RBRA);
        expect(TokenClass.SC);
    }

    private void parseVarDeclPlus() {
        parseVarDecl();
        parseVarDeclStar();
    }
    private void parseVarDeclStar() {
        if (accept(firstOfVarDecl) && !(lookAheadIs(2, TokenClass.LPAR) || lookAheadIs(3, TokenClass.LPAR))) {
            parseVarDecl();
            parseVarDeclStar();
        }
    }
    private void parseVarDecl() {
        parseType();
        expect(TokenClass.IDENTIFIER);
        if (accept(TokenClass.LSBR)) {
            nextToken();
            expect(TokenClass.INT_LITERAL);
            expect(TokenClass.RSBR);
        }
        expect(TokenClass.SC);
    }

    private void parseType() {
        if (accept(TokenClass.STRUCT)) {
            nextToken();
            expect(TokenClass.IDENTIFIER);
        } else {
            expect(TokenClass.INT, TokenClass.CHAR,TokenClass.VOID);
        }
        parseAsterixOption();
    }

    private void parseAsterixOption() {
        if (accept(TokenClass.ASTERIX)) {
            nextToken();
        }
    }

    private void parseFunDeclStar() {
        if (accept(firstOfFunDecl)) {
            parseFunDecl();
            parseFunDeclStar();
        }
    }

    private void parseFunDecl() {
        parseType();
        expect(TokenClass.IDENTIFIER);
        expect(TokenClass.LPAR);
        parseSeveralParamOption();
        expect(TokenClass.RPAR);
        parseBlock();
    }

    private void parseSeveralParamOption() {
        if (accept(firstOfSeveralParam)) {
            parseSeveralParam();
        }
    }
    private void parseSeveralParam() {
        parseType();
        expect(TokenClass.IDENTIFIER);
        if (accept(TokenClass.COMMA)) {
            nextToken();
            parseSeveralParam();
        }
    }

    private void parseBlock() {
        expect(TokenClass.LBRA);
        parseVarDeclStar();
        parseStmtStar();
        expect(TokenClass.RBRA);
    }

    private void parseStmtStar() {
        if (accept(firstOfStmt)) {
            parseStmt();
            parseStmtStar();
        }
    }

    private void parseStmt() {
        if (accept(TokenClass.LBRA)) {
            parseBlock();
        } else if (accept(TokenClass.WHILE)) {
            nextToken();
            expect(TokenClass.LPAR);
            parseExp();
            expect(TokenClass.RPAR);
            parseStmt();
        } else if (accept(TokenClass.IF)) {
            nextToken();
            expect(TokenClass.LPAR);
            parseExp();
            expect(TokenClass.RPAR);
            parseStmt();
            if (accept(TokenClass.ELSE)) {
                nextToken();
                parseStmt();
            }
        } else if (accept(TokenClass.RETURN)) {
            nextToken();
            parseExpOption();
            expect(TokenClass.SC);
        } else {
            parseExp();
            parseStmtPrime();
        }
    }
    private void parseStmtPrime() {
        if (accept(TokenClass.ASSIGN)) {
            nextToken();
            parseExp();
            expect(TokenClass.SC);
        } else {
            expect(TokenClass.SC);
        }
    }

    private void parseExpOption() {
        if (accept(firstOfExp)) {
            parseExp();
        }
    }
    private void parseExp() {
        boolean parsedSomeToken = false;
        if (accept(TokenClass.LPAR)) {
            parsedSomeToken = true;
            nextToken();
            if (accept(firstOfType)) {
                parseType();
                expect(TokenClass.RPAR);
                parseExp();
            } else {
                parseExp();
                expect(TokenClass.RPAR);
            }
        } else if (accept(TokenClass.IDENTIFIER)) {
            parsedSomeToken = true;
            nextToken();
            if (accept(TokenClass.LPAR)) {
                nextToken();
                parseSeveralInputOption();
                expect(TokenClass.RPAR);
            }
        } else if (accept(TokenClass.INT_LITERAL)) {
            parsedSomeToken = true;
            nextToken();
        } else if (accept(TokenClass.CHAR_LITERAL)) {
            parsedSomeToken = true;
            nextToken();
        } else if (accept(TokenClass.STRING_LITERAL)) {
            parsedSomeToken = true;
            nextToken();
        } else if (accept(TokenClass.MINUS)) {
            parsedSomeToken = true;
            nextToken();
            parseExp();
        } else if (accept(TokenClass.PLUS)) {
            parsedSomeToken = true;
            nextToken();
            parseExp();
        } else if (accept(TokenClass.ASTERIX)) {
            parsedSomeToken = true;
            nextToken();
            parseExp();
        } else if (accept(TokenClass.AND)) {
            parsedSomeToken = true;
            nextToken();
            parseExp();
        } else if (accept(TokenClass.SIZEOF)) {
            parsedSomeToken = true;
            nextToken();
            expect(TokenClass.LPAR);
            parseType();
            expect(TokenClass.RPAR);
        }
        if (accept(firstOfExpPrime)) {
            parsedSomeToken = true;
            parseExpPrime();
        }
        if (!parsedSomeToken) {
            error(TokenClass.IDENTIFIER, TokenClass.INT_LITERAL, TokenClass.CHAR_LITERAL, TokenClass.STRING_LITERAL);
        }
    }

    private static TokenClass[] binaryOperators = new TokenClass[]{TokenClass.LT,TokenClass.LE,TokenClass.GT,
            TokenClass.GE,TokenClass.NE,TokenClass.EQ,TokenClass.LOGAND,TokenClass.LOGOR,TokenClass.PLUS,
            TokenClass.MINUS,TokenClass.DIV,TokenClass.ASTERIX,TokenClass.REM};
    private void parseExpPrime() {
        if (accept(TokenClass.LSBR)) {
            nextToken();
            parseExp();
            expect(TokenClass.RSBR);
        } else if (accept(TokenClass.DOT)) {
            nextToken();
            expect(TokenClass.IDENTIFIER);
        } else if (accept(binaryOperators)) {
            nextToken();
            parseExp();
        } else {
            error(binaryOperators);
        }
        if (accept(firstOfExpPrime)) {
            parseExpPrime();
        }
    }

    private void parseSeveralInputOption() {
        if (accept(firstOfExp)) {
            parseSeveralInput();
        }
    }
    private void parseSeveralInput() {
        parseExp();
        if (accept(TokenClass.COMMA)) {
            nextToken();
            parseSeveralInput();
        }
    }
}
