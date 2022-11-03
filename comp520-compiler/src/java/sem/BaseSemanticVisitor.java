package sem;


import ast.ASTNode;

/**
 * 
 * @author dhil
 * A base class providing basic error accumulation.
 */
public abstract class BaseSemanticVisitor<T> implements SemanticVisitor<T> {
	private int errors;
	
	
	public BaseSemanticVisitor() {
		errors = 0;
	}
	
	public int getErrorCount() {
		return errors;
	}
	
	protected void error(ASTNode n, String message) {
		System.err.print(n.filePosition());
		System.err.println(" semantic error: " + message);
		errors++;
	}
}
