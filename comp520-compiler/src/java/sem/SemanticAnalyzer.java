package sem;

import ast.*;
import util.Position;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class SemanticAnalyzer {

	public void addBuiltInFunctions(ast.Program prog) {
		Position p = new Position(0,0);
		prog.funDeclList.add(0, //void print_s(char* s);
				new FunDecl(p,BaseType.VOID, "print_s",
						Arrays.asList(new VarDecl(p,new PointerType(p,BaseType.CHAR), "s")),
						new BlockStmt(p,new LinkedList<VarDecl>(), new LinkedList<Stmt>())));
		prog.funDeclList.add(0, //void print_i(int i);
				new FunDecl(p,BaseType.VOID, "print_i",
						Arrays.asList(new VarDecl(p,BaseType.INT, "i")),
						new BlockStmt(p,new LinkedList<VarDecl>(), new LinkedList<Stmt>())));
		prog.funDeclList.add(0, //void print_c(char c);
				new FunDecl(p,BaseType.VOID, "print_c",
						Arrays.asList(new VarDecl(p,BaseType.CHAR, "c")),
						new BlockStmt(p,new LinkedList<VarDecl>(), new LinkedList<Stmt>())));
		prog.funDeclList.add(0, //char read_c();
				new FunDecl(p,BaseType.CHAR, "read_c",
						Arrays.asList(),
						new BlockStmt(p,new LinkedList<VarDecl>(), new LinkedList<Stmt>())));
		prog.funDeclList.add(0, //int read_i();
				new FunDecl(p,BaseType.INT, "read_i",
						Arrays.asList(),
						new BlockStmt(p,new LinkedList<VarDecl>(), new LinkedList<Stmt>())));
		prog.funDeclList.add(0, //void* mcmalloc(int size);
				new FunDecl(p,new PointerType(p,BaseType.VOID), "mcmalloc",
						Arrays.asList(new VarDecl(p,BaseType.INT, "size")),
						new BlockStmt(p,new LinkedList<VarDecl>(), new LinkedList<Stmt>())));
	}
	
	public int analyze(ast.Program prog) {
		addBuiltInFunctions(prog);
		// List of visitors
		ArrayList<SemanticVisitor> visitors = new ArrayList<SemanticVisitor>() {{
			add(new NameAnalysisVisitor());
			add(new TypeCheckVisitor());
		}};
		// Error accumulator
		int errors = 0;
		
		// Apply each visitor to the AST
		for (SemanticVisitor v : visitors) {
			if (errors == 0) {
				prog.accept(v);
				errors += v.getErrorCount();
			}
		}
		
		// Return the number of errors.
		return errors;
	}
}
