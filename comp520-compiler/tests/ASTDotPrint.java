import ast.ASTDotPrinter;
import ast.ASTPrinter;
import ast.Program;
import lexer.Scanner;
import lexer.Tokeniser;
import parser.Parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ASTDotPrint {
    public static void main(String[] args) {

        File inputFile = new File(args[0]);
        Scanner scanner;
        try {
            scanner = new Scanner(inputFile);
        } catch (FileNotFoundException e) {
            System.out.println("File "+inputFile.toString()+" does not exist.");
            System.exit(1);
            return;
        }

        Tokeniser tokeniser = new Tokeniser(scanner);
        ASTDotPrinter dotPrinter = new ASTDotPrinter();
        Parser parser = new Parser(tokeniser);
        Program programAst = parser.parse();
        if (parser.getErrorCount() == 0) {
            programAst.accept(dotPrinter);
        }
        try {
            String user = System.getProperty("user.name");
            if (user.equals("cailean")) {
                ProcessBuilder builder = new ProcessBuilder();
                builder.command("dot", "-Tpdf", "AST.dot", "-o", "AST.pdf");
                Process process = builder.start();
                int exitCode = process.waitFor();
                assert exitCode == 0;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
