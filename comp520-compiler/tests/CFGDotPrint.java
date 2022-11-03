import ast.Program;
import gen.CodeGenerator;
import gen.ProgramGen;
import gen.asm.AssemblyParser;
import gen.asm.AssemblyProgram;
import gen.cfg.CFGBuilder;
import gen.cfg.LivenessAnalyzer;
import lexer.Scanner;
import lexer.Tokeniser;
import parser.Parser;
import regalloc.RegAllocMappingGenerator;
import sem.SemanticAnalyzer;

import java.io.*;

public class CFGDotPrint {
    public static void main(String[] args) {
        File inputFile = new File(args[0]);
        /*
        Scanner scanner;
        try {
            scanner = new Scanner(inputFile);
        } catch (FileNotFoundException e) {
            System.out.println("File "+inputFile.toString()+" does not exist.");
            System.exit(1);
            return;
        }

        Tokeniser tokeniser = new Tokeniser(scanner);
        Parser parser = new Parser(tokeniser);
        Program programAst = parser.parse();
        SemanticAnalyzer sem = new SemanticAnalyzer();
        int errors = sem.analyze(programAst);
        if (errors > 0) System.exit(1);
        AssemblyProgram asmProgWithVirtualRegs = new AssemblyProgram();
        ProgramGen progGen = new ProgramGen(asmProgWithVirtualRegs);
        progGen.visitProgram(programAst);*/
        AssemblyProgram program;
        try {
            var reader = new FileReader(inputFile);
            program = AssemblyParser.readAssemblyProgram(new BufferedReader(reader));
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("File " + inputFile + " does not exist.");
            System.exit(1);
            return;
        } catch (IOException e) {
            System.out.println("An I/O exception occurred when reading " + inputFile + ".");
            System.exit(1);
            return;
        }
        CFGBuilder cfgBuilder = new CFGBuilder(program);
        //LivenessAnalyzer.analyze(cfgBuilder.getProgramNode());
        //RegAllocMappingGenerator.generateMapping(cfgBuilder.getProgramNode());
        cfgBuilder.debugPrintCFG();
        try {
            String user = System.getProperty("user.name");
            if (user.equals("cailean")) {
                ProcessBuilder builder = new ProcessBuilder();
                builder.command("dot", "-Tpdf", "CFG.dot", "-o", "CFG.pdf");
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
