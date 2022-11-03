package lexer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ThreadPoolExecutor;

import static org.junit.jupiter.api.Assertions.*;

class TokeniserTest {
    static String[] isolatedStrings;
    static Token.TokenClass[] matchingSingleTokens;
    static {
        isolatedStrings = new String[]{
                "+", "-", "*", "/", ".", ",", ";",
                ")", "(", "]", "[", "}", "{", "if",
                "else", "while", "struct", "int",
                "char", "return", "void", "=", "==",
                "<=", "<", ">=", ">", "&", "&&", "||",
                "#include", "\"abc\"", "\"\"\t\"",
                "123", "_f132123", "f12313212", "_",
                "VAR", "Vas_1", "#incl", "#asd",
                "\'", "\"", "\"\t\t\t\ta\"",
                "ch", "//commemarmaemramera\n+", "/*sdfasfd*/+"
        };
        matchingSingleTokens = new Token.TokenClass[] {
                Token.TokenClass.PLUS, Token.TokenClass.MINUS,
                Token.TokenClass.ASTERIX, Token.TokenClass.DIV,
                Token.TokenClass.DOT, Token.TokenClass.COMMA,
                Token.TokenClass.SC, Token.TokenClass.RPAR,
                Token.TokenClass.LPAR, Token.TokenClass.RSBR,
                Token.TokenClass.LSBR, Token.TokenClass.RBRA,
                Token.TokenClass.LBRA, Token.TokenClass.IF,
                Token.TokenClass.ELSE, Token.TokenClass.WHILE,
                Token.TokenClass.STRUCT, Token.TokenClass.INT,
                Token.TokenClass.CHAR, Token.TokenClass.RETURN,
                Token.TokenClass.VOID, Token.TokenClass.ASSIGN,
                Token.TokenClass.EQ, Token.TokenClass.LE,
                Token.TokenClass.LT, Token.TokenClass.GE, Token.TokenClass.GT,
                Token.TokenClass.AND, Token.TokenClass.LOGAND,
                Token.TokenClass.LOGOR, Token.TokenClass.INCLUDE,
                Token.TokenClass.STRING_LITERAL, Token.TokenClass.STRING_LITERAL,
                Token.TokenClass.INT_LITERAL, Token.TokenClass.IDENTIFIER,
                Token.TokenClass.IDENTIFIER, Token.TokenClass.IDENTIFIER,
                Token.TokenClass.IDENTIFIER, Token.TokenClass.IDENTIFIER,
                Token.TokenClass.INVALID, Token.TokenClass.INVALID,
                Token.TokenClass.INVALID, Token.TokenClass.INVALID,
                Token.TokenClass.STRING_LITERAL, Token.TokenClass.IDENTIFIER,
                Token.TokenClass.PLUS, Token.TokenClass.PLUS};
    };

    private static String allTokensString =
            "a _ _1 _a _a1 = == === ==== ({[)}];,int void char if else while return struct sizeof #include\"hi\t\0 \"1234123213'2''\n'&&||&&& &!=<><=>=+-*/%.";


    private static Token.TokenClass[] allTokensStringTokens = new Token.TokenClass[]{
            Token.TokenClass.IDENTIFIER,
            Token.TokenClass.IDENTIFIER,
            Token.TokenClass.IDENTIFIER,
            Token.TokenClass.IDENTIFIER,
            Token.TokenClass.IDENTIFIER,
            Token.TokenClass.ASSIGN,
            Token.TokenClass.EQ,
            Token.TokenClass.EQ,
            Token.TokenClass.ASSIGN,
            Token.TokenClass.EQ,
            Token.TokenClass.EQ,
            Token.TokenClass.LPAR,
            Token.TokenClass.LBRA,
            Token.TokenClass.LSBR,
            Token.TokenClass.RPAR,
            Token.TokenClass.RBRA,
            Token.TokenClass.RSBR,
            Token.TokenClass.SC,
            Token.TokenClass.COMMA,
            Token.TokenClass.INT,  // "int"
            Token.TokenClass.VOID, // "void"
            Token.TokenClass.CHAR, // "char"
            Token.TokenClass.IF,     // "if"
            Token.TokenClass.ELSE,   // "else"
            Token.TokenClass.WHILE,  // "while"
            Token.TokenClass.RETURN, // "return"
            Token.TokenClass.STRUCT, // "struct"
            Token.TokenClass.SIZEOF, // "sizeof"
            Token.TokenClass.INCLUDE,
            Token.TokenClass.STRING_LITERAL, // \".*\"  any sequence of characters enclosed within two double quote " (please be aware of the escape character backslash \)
            Token.TokenClass.INT_LITERAL,    // ('0'|...|'9')+
            Token.TokenClass.CHAR_LITERAL,
            Token.TokenClass.CHAR_LITERAL,
            Token.TokenClass.LOGAND, // "&&"
            Token.TokenClass.LOGOR,
            Token.TokenClass.LOGAND,
            Token.TokenClass.AND,
            Token.TokenClass.AND,
            Token.TokenClass.NE, // "!="
            Token.TokenClass.LT, // '<'
            Token.TokenClass.GT, // '>'
            Token.TokenClass.LE, // "<="
            Token.TokenClass.GE, // ">="
            Token.TokenClass.PLUS,    // '+'
            Token.TokenClass.MINUS,   // '-'
            Token.TokenClass.ASTERIX, // '*'  // can be used for multiplication or pointers
            Token.TokenClass.DIV,     // '/'
            Token.TokenClass.REM,     // '%'
            Token.TokenClass.DOT
    };

    @Test
    @DisplayName("Test all tokens together...")
    void testAllTokensTogether() {
        try {
            File source = new File("temp");
            source.createNewFile();
            FileWriter writer = new FileWriter(source);
            writer.write(allTokensString);
            writer.flush();
            Scanner scanner = new Scanner(source);
            Tokeniser tokeniser = new Tokeniser(scanner);
            int index = 0;
            for (Token t = tokeniser.nextToken(); t.tokenClass != Token.TokenClass.EOF; t = tokeniser.nextToken()) {
                assertEquals(allTokensStringTokens[index], t.tokenClass);
                index++;
            }
        } catch (IOException e) {
            System.out.println("failed to create temporary source file");
            fail();
        }
    }
    private static String allLiterals = "\"123123\"\"\t\t\0\t\"\"\"'1''\t''\0'''123 13";
    private static Token.TokenClass[] allLiteralsTokens = new Token.TokenClass[]{
            Token.TokenClass.STRING_LITERAL,
            Token.TokenClass.STRING_LITERAL,
            Token.TokenClass.STRING_LITERAL,
            Token.TokenClass.CHAR_LITERAL,
            Token.TokenClass.CHAR_LITERAL,
            Token.TokenClass.CHAR_LITERAL,
            Token.TokenClass.INVALID,
            Token.TokenClass.INT_LITERAL,
            Token.TokenClass.INT_LITERAL,
    };

    @Test
    @DisplayName("Test all literals")
    void testAllLiterals() {
        try {
            File source = new File("temp");
            source.createNewFile();
            FileWriter writer = new FileWriter(source);
            writer.write(allLiterals);
            writer.flush();
            Scanner scanner = new Scanner(source);
            Tokeniser tokeniser = new Tokeniser(scanner);
            int index = 0;
            for (Token t = tokeniser.nextToken(); t.tokenClass != Token.TokenClass.EOF; t = tokeniser.nextToken()) {
                assertEquals(allLiteralsTokens[index], t.tokenClass);
                index++;
            }
        } catch (IOException e) {
            System.out.println("failed to create temporary source file");
            fail();
        }
    }

    @Test
    @DisplayName("Test individual isolated strings for matching tokens")
    void testSingleTokens() {
        try {
            for (int i=0; i<isolatedStrings.length; i++) {
                File source = new File("temp");
                source.createNewFile();
                FileWriter writer = new FileWriter(source);
                writer.write(isolatedStrings[i]);
                writer.flush();
                Scanner scanner = new Scanner(source);
                Tokeniser tokeniser = new Tokeniser(scanner);
                Token t = tokeniser.nextToken();
                assertEquals(matchingSingleTokens[i], t.tokenClass);
                scanner.close();
                writer.close();
                source.delete();
            }
        } catch (IOException e) {
            System.out.println("failed to create temporary source file");
            fail();
        }
    }

    @Test
    @DisplayName("Test fibonacci.c program")
    void testFibonacci() {
        try {
            File source = new File("tests/fibonacci.c");
            Scanner scanner = new Scanner(source);
            Tokeniser tokeniser = new Tokeniser(scanner);
            for (Token t = tokeniser.nextToken(); t.tokenClass != Token.TokenClass.EOF; t = tokeniser.nextToken())
                System.out.println(t);
            if (tokeniser.getErrorCount() == 0)
                System.out.println("Lexing: pass");
            else
                System.out.println("Lexing: failed ("+tokeniser.getErrorCount()+" errors)");
        } catch(IOException e) {
            System.out.println("failed to load test source file");
            fail();
        }
    }
}