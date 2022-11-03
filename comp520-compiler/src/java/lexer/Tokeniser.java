package lexer;

import lexer.Token.TokenClass;
import util.Pair;

import java.io.EOFException;
import java.io.IOException;

import java.util.HashMap;

/**
 * @author cdubach
 */
public class Tokeniser {

    private Scanner scanner;

    private int error = 0;
    public int getErrorCount() {
	return this.error;
    }

    private static final HashMap<String, TokenClass> reservedAlphWords = new HashMap<>();
    private static final HashMap<Character, Character> escapableCharacters = new HashMap<>();
    static {
        reservedAlphWords.put("int", TokenClass.INT);
        reservedAlphWords.put("if", TokenClass.IF);
        reservedAlphWords.put("void", TokenClass.VOID);
        reservedAlphWords.put("else", TokenClass.ELSE);
        reservedAlphWords.put("while", TokenClass.WHILE);
        reservedAlphWords.put("void", TokenClass.VOID);
        reservedAlphWords.put("char", TokenClass.CHAR);
        reservedAlphWords.put("while", TokenClass.WHILE);
        reservedAlphWords.put("return", TokenClass.RETURN);
        reservedAlphWords.put("struct", TokenClass.STRUCT);
        reservedAlphWords.put("sizeof", TokenClass.SIZEOF);
        escapableCharacters.put('t', '\t');
        escapableCharacters.put('b', '\b');
        escapableCharacters.put('n', '\n');
        escapableCharacters.put('r', '\r');
        escapableCharacters.put('f', '\f');
        escapableCharacters.put('\'', '\'');
        escapableCharacters.put('"', '"');
        escapableCharacters.put('\\', '\\');
        escapableCharacters.put('0', '\0');
    }

    private enum SubToken {
        ALPH_WORD, //ambiguously either an identifier or keyword
        NUM_WORD, //always an int literal
        DOUBLE_QUOTE_WRAPPED_WORD, // always a string literal
        SINGLE_QUOTE_WRAPPED_WORD, //always a char literal
    }


    public Tokeniser(Scanner scanner) { this.scanner = scanner; }

    private void invalidCharError(int line, int col) {
        System.out.println("Lexing error: illegal char definition at " + line + ":" + col);
        error++;
    }
    private void illegalEscapeCharError(char c, int line, int col) {
        System.out.println("Lexing error: illegal escape character (" + c + ") at " + line + ":" + col);
        error++;
    }
    private void error(char c, int line, int col) {
        if (c == '\0') {
            System.out.println("Lexing error: EOF while parsing token");
        } else {
            System.out.println("Lexing error: unrecognised character (" + c + ") at " + line + ":" + col);
        }
	    error++;
    }


    public Token nextToken() {
        Token result;
        try {
             result = next();
        } catch (EOFException eof) {
            // end of file, nothing to worry about, just return EOF token
            return new Token(TokenClass.EOF, scanner.getLine(), scanner.getColumn());
        } catch (IOException ioe) {
            ioe.printStackTrace();
            // something went horribly wrong, abort
            System.exit(-1);
            return null;
        }
        return result;
    }


    private Pair<TokenClass, String> parseSubToken(SubToken subToken, String string, int line, int column) {
        switch (subToken) {
            case ALPH_WORD:
                TokenClass token = reservedAlphWords.getOrDefault(string, TokenClass.IDENTIFIER);
                if (token == TokenClass.IDENTIFIER) {
                    return new Pair(token, string);
                }
                return new Pair(token, "");
            case NUM_WORD:
                return new Pair(TokenClass.INT_LITERAL, string);
            case DOUBLE_QUOTE_WRAPPED_WORD:
                StringBuilder stringData = new StringBuilder(string);
                for (int i=0; i<stringData.length(); i++) {
                    if (stringData.charAt(i) == '\\') {
                        stringData.deleteCharAt(i);
                        char newChar = escapableCharacters.getOrDefault(stringData.charAt(i), 'z');
                        if (newChar == 'z') {
                            illegalEscapeCharError(stringData.charAt(i), line, column);
                            return new Pair<>(TokenClass.INVALID, "");
                        }
                        stringData.setCharAt(i, newChar);
                    }
                }
                return new Pair(TokenClass.STRING_LITERAL, stringData.toString());
            case SINGLE_QUOTE_WRAPPED_WORD:
                StringBuilder charData = new StringBuilder(string);
                if (charData.length() == 0) {
                    invalidCharError(line, column);
                    return new Pair<>(TokenClass.INVALID, "");
                }
                if (charData.charAt(0) == '\\') {
                    charData.deleteCharAt(0);
                    char newChar = escapableCharacters.getOrDefault(charData.charAt(0), 'z');
                    if (newChar == 'z') {
                        illegalEscapeCharError(charData.charAt(0), line, column);
                        return new Pair<>(TokenClass.INVALID, "");
                    }
                    charData.setCharAt(0, newChar);
                }
                if (charData.length() == 1)
                    return new Pair(TokenClass.CHAR_LITERAL, charData.toString());
                else {
                    invalidCharError(line, column);
                    return new Pair<>(TokenClass.INVALID, "");
                }
        }
        System.out.println("Unrecognized Lexical Sub Token Error");
        return new Pair<>(TokenClass.INVALID, "");
    }

    private Character scanNextAndFailWithEOF() {
        try {
            char c = scanner.next();
            return c;
        } catch (IOException e) {
            error('\0', scanner.getLine(), scanner.getColumn());
            return null;
        }
    }
    private Character peekNextAndReturnWithEOF() {
        try {
            char c = scanner.peek();
            return c;
        } catch (IOException e) {
            return null;
        }
    }
    private Character scanNextAndReturnWithEOF() {
        try {
            char c = scanner.next();
            return c;
        } catch (IOException e) {
            return null;
        }
    }

    private Token next() throws IOException {

        int line = scanner.getLine();
        int column = scanner.getColumn();

        // get the first character
        Character c = scanner.next();
        // skip white spaces
        if (Character.isWhitespace(c))
            return next();

        StringBuilder subTokenString = new StringBuilder();

        if (Character.isAlphabetic(c) || c =='_') {
            while (Character.isAlphabetic(c) || c =='_' || Character.isDigit(c)) {
                subTokenString.append(c);
                c = peekNextAndReturnWithEOF();
                if (c == null)
                    break;
                if (Character.isAlphabetic(c) || c =='_' || Character.isDigit(c))
                    scanner.next();
            }
            Pair<TokenClass, String> tokenStringPair = parseSubToken(SubToken.ALPH_WORD, subTokenString.toString(), line, column);
            return new Token(tokenStringPair.first, tokenStringPair.second, line, column);
        } else if (Character.isDigit(c)) {
            while (Character.isDigit(c)) {
                subTokenString.append(c);
                c = peekNextAndReturnWithEOF();
                if (c == null)
                    break;
                if (Character.isDigit(c))
                    scanner.next();
            }
            Pair<TokenClass, String> tokenStringPair = parseSubToken(SubToken.NUM_WORD, subTokenString.toString(), line, column);
            return new Token(tokenStringPair.first, tokenStringPair.second, line, column);
        } else if (c == '"') {
            c = scanNextAndFailWithEOF();
            if (c == null)
                return new Token(TokenClass.INVALID, line, column);
            while (c != '"') {
                if (c == '\\') {
                    subTokenString.append(c);
                    c = scanNextAndFailWithEOF();
                    if (c == null)
                        return new Token(TokenClass.INVALID, line, column);
                }
                subTokenString.append(c);
                c = scanNextAndFailWithEOF();
                if (c == null)
                    return new Token(TokenClass.INVALID, line, column);
            }
            Pair<TokenClass, String> tokenStringPair = parseSubToken(SubToken.DOUBLE_QUOTE_WRAPPED_WORD, subTokenString.toString(), line, column);
            return new Token(tokenStringPair.first, tokenStringPair.second, line, column);
        } else if (c == '\'') {
            c = scanNextAndFailWithEOF();
            if (c == null)
                return new Token(TokenClass.INVALID, line, column);
            while (c != '\'') {
                if (c == '\\') {
                    subTokenString.append(c);
                    c = scanNextAndFailWithEOF();
                    if (c == null)
                        return new Token(TokenClass.INVALID, line, column);
                }
                subTokenString.append(c);
                c = scanNextAndFailWithEOF();
                if (c == null)
                    return new Token(TokenClass.INVALID, line, column);
            }
            Pair<TokenClass, String> tokenStringPair = parseSubToken(SubToken.SINGLE_QUOTE_WRAPPED_WORD, subTokenString.toString(), line, column);
            return new Token(tokenStringPair.first, tokenStringPair.second, line, column);
        } else if (c == '+')
            return new Token(TokenClass.PLUS, line, column);
        else if (c == '-')
            return new Token(TokenClass.MINUS, line, column);
        else if (c == '(')
            return new Token(TokenClass.LPAR, line, column);
        else if (c == ')')
            return new Token(TokenClass.RPAR, line, column);
        else if (c == '[')
            return new Token(TokenClass.LSBR, line, column);
        else if (c == ']')
            return new Token(TokenClass.RSBR, line, column);
        else if (c == '{')
            return new Token(TokenClass.LBRA, line, column);
        else if (c == '}')
            return new Token(TokenClass.RBRA, line, column);
        else if (c == ';')
            return new Token(TokenClass.SC, line, column);
        else if (c == '*')
            return new Token(TokenClass.ASTERIX, line, column);
        else if (c == '%')
            return new Token(TokenClass.REM, line, column);
        else if (c == '.')
            return new Token(TokenClass.DOT, line, column);
        else if (c == ',')
            return new Token(TokenClass.COMMA, line, column);
        else if (c == '/') {
            c = peekNextAndReturnWithEOF();
            if (c == null) {
                return new Token(TokenClass.DIV, line, column);
            }
            if (c == '/') {
                while (c != '\n' || c == null) {
                    c = peekNextAndReturnWithEOF();
                    if (c != '\n' || c == null)
                        scanner.next();
                }
                return next();
            } else if (c == '*') {
                scanner.next();
                Character c1, c2;
                c2 = c;
                do {
                    c1 = c2;
                    c2 = scanNextAndReturnWithEOF();
                    if (c1 == '*' && c2 == '/') {
                        return next();
                    }
                } while (c1 != null && c2 != null);
                error('\0', line, column);
                return new Token(TokenClass.INVALID, line, column);
            } else {
                return new Token(TokenClass.DIV, line, column);
            }
        } else if (c == '#') {
            StringBuilder includeString = new StringBuilder();
            c = peekNextAndReturnWithEOF();
            if (c == null) {
                error('#', line, column);
                return new Token(TokenClass.INVALID, line, column);
            }
            while (Character.isAlphabetic(c)) {
                scanNextAndReturnWithEOF();
                includeString.append(c);
                c = peekNextAndReturnWithEOF();
                if (c == null) {
                    break;
                }
            }
            if (includeString.toString().equals("include")) {
                return new Token(TokenClass.INCLUDE, line, column);
            } else {
                error('#', line, column);
                return new Token(TokenClass.INVALID, line, column);
            }
        } else if (c == '=') {
            c = peekNextAndReturnWithEOF();
            if (c == null) {
                return new Token(TokenClass.ASSIGN, line, column);
            }
            if (c == '=') {
                scanner.next();
                return new Token(TokenClass.EQ, line, column);
            } else {
                return new Token(TokenClass.ASSIGN, line, column);
            }
        } else if (c == '!') {
            c = peekNextAndReturnWithEOF();
            if (c == null) {
                error('!', line, column);
                return new Token(TokenClass.INVALID, line, column);
            }
            if (c == '=') {
                scanner.next();
                return new Token(TokenClass.NE, line, column);
            } else {
                error('!', line, column);
                return new Token(TokenClass.INVALID, line, column);
            }
        } else if (c == '&') {
            c = peekNextAndReturnWithEOF();
            if (c == null) {
                return new Token(TokenClass.AND, line, column);
            }
            if (c == '&') {
                scanner.next();
                return new Token(TokenClass.LOGAND, line, column);
            } else {
                return new Token(TokenClass.AND, line, column);
            }
        } else if (c == '<') {
            c = peekNextAndReturnWithEOF();
            if (c == null) {
                return new Token(TokenClass.LT, line, column);
            }
            if (c == '=') {
                scanner.next();
                return new Token(TokenClass.LE, line, column);
            } else {
                return new Token(TokenClass.LT, line, column);
            }
        } else if (c == '>') {
            c = peekNextAndReturnWithEOF();
            if (c == null) {
                return new Token(TokenClass.GT, line, column);
            }
            if (c == '=') {
                scanner.next();
                return new Token(TokenClass.GE, line, column);
            } else {
                return new Token(TokenClass.GT, line, column);
            }
        } else if (c == '|') {
            c = peekNextAndReturnWithEOF();
            if (c == null) {
                error('|', line, column);
                return new Token(TokenClass.INVALID, line, column);
            }
            if (c == '|') {
                scanner.next();
                return new Token(TokenClass.LOGOR, line, column);
            } else {
                error('|', line, column);
                return new Token(TokenClass.INVALID, line, column);
            }
        } else {
            error(c, line, column);
            return new Token(TokenClass.INVALID, line, column);
        }
    }
}
