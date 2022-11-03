package sem;

import ast.SymbolDeclaration;

import java.util.HashMap;
import java.util.Map;

public class Scope {
	private Scope outer;
	private Map<String, SymbolDeclaration> symbolTable;
	
	public Scope(Scope outer) {
		symbolTable = new HashMap<>();
		this.outer = outer; 
	}
	
	public Scope() { this(null); }
	
	public SymbolDeclaration lookupDeclaration(String name) {
		SymbolDeclaration found = lookupDeclarationCurrent(name);
		if (found == null) {
			if (outer != null)
				return outer.lookupDeclaration(name);
			else
				return null;
		} else {
			return found;
		}
	}
	
	public SymbolDeclaration lookupDeclarationCurrent(String name) {
		return symbolTable.getOrDefault(name, null);
	}
	
	public void put(SymbolDeclaration symDecl) {
		symbolTable.put(symDecl.id(), symDecl);
	}

	public Scope getOuter() {
		return outer;
	}
}
