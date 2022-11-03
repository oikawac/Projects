package ast;

import gen.asm.Label;

public interface SymbolDeclaration<T extends SymbolReference> extends ASTNode {

    public SymbolClass symbolClass();
    public Type type();
    public String id();

    public void linkToReference(T ref);

    public Label asmLabel();

}
