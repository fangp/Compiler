package cop5556sp17.AST;

import cop5556sp17.Scanner.Token;
import cop5556sp17.AST.Type.TypeName;

public abstract class Expression extends ASTNode {
	private TypeName typename;
	private Dec dec;
	public Dec getDec() {
		return dec;
	}

	public void setDec(Dec dec) {
		this.dec = dec;
	}

	public TypeName getTypename() {
		return typename;
	}

	public void setTypename(TypeName typename) {
		this.typename = typename;
	}

	protected Expression(Token firstToken) {
		super(firstToken);
	}

	@Override
	abstract public Object visit(ASTVisitor v, Object arg) throws Exception;

}
