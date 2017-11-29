package cop5556sp17.AST;

import cop5556sp17.AST.Type.TypeName;
import cop5556sp17.Scanner.Token;


public abstract class Chain extends Statement {
	private TypeName typename;
	private Dec dec;
	public TypeName getTypename() {
		return typename;
	}

	public void setTypename(TypeName typename) {
		this.typename = typename;
	}
	public Chain(Token firstToken) {
		super(firstToken);
	}

	public Dec getDec() {
		return dec;
	}

	public void setDec(Dec dec) {
		this.dec = dec;
	}

}
