package cop5556sp17;

import cop5556sp17.Scanner.Kind;
import static cop5556sp17.Scanner.Kind.*;

import org.junit.rules.ExpectedException;

import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;
import cop5556sp17.Scanner.Token;

public class Parser {

	/**
	 * Exception to be thrown if a syntax error is detected in the input.
	 * You will want to provide a useful error message.
	 *
	 */
	@SuppressWarnings("serial")
	public static class SyntaxException extends Exception {
		public SyntaxException(String message) {
			super(message);
		}
	}
	
	/**
	 * Useful during development to ensure unimplemented routines are
	 * not accidentally called during development.  Delete it when 
	 * the Parser is finished.
	 *
	 */
	@SuppressWarnings("serial")	
	public static class UnimplementedFeatureException extends RuntimeException {
		public UnimplementedFeatureException() {
			super();
		}
	}

	Scanner scanner;
	Token t;

	Parser(Scanner scanner) {
		this.scanner = scanner;
		t = scanner.nextToken();
	}

	/**
	 * parse the input using tokens from the scanner.
	 * Check for EOF (i.e. no trailing junk) when finished
	 * 
	 * @throws SyntaxException
	 */
	void parse() throws SyntaxException {
		program();
		matchEOF();
		return;
	}

	void expression() throws SyntaxException {
		//TODO
		term();
		Kind[] kinds = {Kind.LT, Kind.LE, Kind.GE, Kind.GT, Kind.EQUAL, Kind.NOTEQUAL};
		while(t.isKind(kinds)){
			consume();
			term();
		}
	}

	void term() throws SyntaxException {
		//TODO
		elem();
		Kind[] kinds={Kind.PLUS, Kind.MINUS, Kind.OR};
		while(t.isKind(kinds)){
			consume();
			elem();
		}
	}

	void elem() throws SyntaxException {
		//TODO
		factor();
		Kind[] kinds = {Kind.DIV, Kind.TIMES, Kind.AND, Kind.MOD};
		while(t.isKind(kinds)){
			consume();
			factor();
		}
	}

	void factor() throws SyntaxException {
		Kind kind = t.kind;
		switch (kind) {
		case IDENT: {
			consume();
		}
			break;
		case INT_LIT: {
			consume();
		}
			break;
		case KW_TRUE:
		case KW_FALSE: {
			consume();
		}
			break;
		case KW_SCREENWIDTH:
		case KW_SCREENHEIGHT: {
			consume();
		}
			break;
		case LPAREN: {
			consume();
			expression();
			match(RPAREN);
		}
			break;
		default:
			//you will want to provide a more useful error message
			throw new SyntaxException("illegal factor "+t.kind);
		}
	}

	void block() throws SyntaxException {
		//TODO
		Kind[] decOp={Kind.KW_INTEGER, Kind.KW_BOOLEAN, Kind.KW_IMAGE, Kind.KW_FRAME};
		Kind[] pdec = {Kind.KW_URL, Kind.KW_FILE, Kind.KW_BOOLEAN, Kind.KW_INTEGER};
		Kind[] stat = {Kind.OP_SLEEP, Kind.KW_WHILE, Kind.KW_IF, Kind.IDENT};
		match(Kind.LBRACE);
		if(t.isKind(decOp)){
			dec();
		}
		while(!t.isKind(Kind.RBRACE)){
			if(t.isKind(decOp)){
				dec();
			}
			else{
				statement();
			}
		}
		match(Kind.RBRACE);
	}

	void program() throws SyntaxException {
		//TODO
		Kind[] decOp={Kind.KW_INTEGER, Kind.KW_BOOLEAN, Kind.KW_IMAGE, Kind.KW_FRAME};
		Kind[] pdec = {Kind.KW_URL, Kind.KW_FILE, Kind.KW_BOOLEAN, Kind.KW_INTEGER};
		Kind[] stat = {Kind.OP_SLEEP, Kind.KW_WHILE, Kind.KW_IF, Kind.IDENT};
		match(Kind.IDENT);
		if(t.isKind(pdec)){
			paramDec();
			while(t.isKind(Kind.COMMA)){
				consume();
				paramDec();
			}
			if(t.isKind(Kind.LBRACE)){
				block();
			}
		}
		else if(t.isKind(Kind.LBRACE)){
			block();
		}
		
		
	}

	void paramDec() throws SyntaxException {
		//TODO
		Kind[] pdec = {Kind.KW_URL, Kind.KW_FILE, Kind.KW_BOOLEAN, Kind.KW_INTEGER};
		match(pdec);
		match(Kind.IDENT);
	}

	void dec() throws SyntaxException {
		//TODO
		Kind[] decOp={Kind.KW_INTEGER, Kind.KW_BOOLEAN, Kind.KW_IMAGE, Kind.KW_FRAME};
		match(decOp);
		match(Kind.IDENT);
	}

	void statement() throws SyntaxException {
		//TODO
		if(t.isKind(Kind.OP_SLEEP)){
			consume();
			expression();
			if(t.isKind(Kind.SEMI))
				consume();
		}
		else if(t.isKind(Kind.KW_WHILE)){
			consume();
			match(Kind.LPAREN);
			expression();
			match(Kind.RPAREN);
			block();
		}
		else if(t.isKind(Kind.KW_IF)){
			consume();
			match(Kind.LPAREN);
			expression();
			match(Kind.RPAREN);
			block();
		}
		else if(t.isKind(Kind.IDENT)&&scanner.peek().isKind(Kind.ASSIGN)){
			match(Kind.IDENT);
			match(Kind.ASSIGN);
			expression();
			match(Kind.SEMI);
			//System.out.println(t.getText());
		}
		else{
			chain();
			match(Kind.SEMI);
		}
	}

	void chain() throws SyntaxException {
		//TODO
		chainElem();
		Kind[] arrowOp = {Kind.ARROW, Kind.BARARROW};
		if(match(arrowOp)!=null){
			chainElem();
			while(t.isKind(arrowOp)){
				consume();
				chainElem();
			}
		}
	}

	void chainElem() throws SyntaxException {
		//TODO
		Kind kind = t.kind;
		Kind[] filterOp= {Kind.OP_BLUR, Kind.OP_GRAY, Kind.OP_CONVOLVE};
		Kind[] frameOp = {Kind.KW_MOVE, Kind.KW_SHOW, Kind.KW_HIDE, Kind.KW_XLOC, Kind.KW_YLOC};
		Kind[] imageOp = {Kind.OP_HEIGHT, Kind.OP_WIDTH, Kind.KW_SCALE};
		if(kind==Kind.IDENT)
			consume();
		else if(t.isKind(filterOp)){
			consume();
			arg();
		}
		else if(t.isKind(frameOp)){
			consume();
			arg();
		}
		else if(t.isKind(imageOp)){
			consume();
			arg();
		}
		else
			throw new SyntaxException("illegal " + kind);
			
		
	}

	void arg() throws SyntaxException {
		//TODO
		if(t.isKind(Kind.LPAREN)){
			consume();
			expression();
			while(t.isKind(Kind.COMMA)){
				consume();
				expression();
			}
			match(Kind.RPAREN);
		}
	}

	/**
	 * Checks whether the current token is the EOF token. If not, a
	 * SyntaxException is thrown.
	 * 
	 * @return
	 * @throws SyntaxException
	 */
	private Token matchEOF() throws SyntaxException {
		if (t.isKind(EOF)) {
			return t;
		}
		throw new SyntaxException("expected EOF "+t.kind);
	}

	/**
	 * Checks if the current token has the given kind. If so, the current token
	 * is consumed and returned. If not, a SyntaxException is thrown.
	 * 
	 * Precondition: kind != EOF
	 * 
	 * @param kind
	 * @return
	 * @throws SyntaxException
	 */
	private Token match(Kind kind) throws SyntaxException {
		if (t.isKind(kind)) {
			return consume();
		}
		throw new SyntaxException("saw " + t.kind + " expected " + kind);
	}

	/**
	 * Checks if the current token has one of the given kinds. If so, the
	 * current token is consumed and returned. If not, a SyntaxException is
	 * thrown.
	 * 
	 * * Precondition: for all given kinds, kind != EOF
	 * 
	 * @param kinds
	 *            list of kinds, matches any one
	 * @return
	 * @throws SyntaxException
	 */
	private Token match(Kind... kinds) throws SyntaxException {
		// TODO. Optional but handy
		for(int i=0;i<kinds.length;i++){
			if(t.isKind(kinds[i]))
				return consume();
		}
		throw new SyntaxException("Wrong kind "+ t.kind);
	}

	/**
	 * Gets the next token and returns the consumed token.
	 * 
	 * Precondition: t.kind != EOF
	 * 
	 * @return
	 * 
	 */
	private Token consume() throws SyntaxException {
		Token tmp = t;
		t = scanner.nextToken();
		return tmp;
	}
	public static void main(String args[]) throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "main { integer a";
		/*String input = "main integer a, integer b {\n" +
		            "boolean c\n" +
		            "c <- a == b;\n" +
		            "image output\n" +
		            "output <- 5*5/1;\n" +
		            "image otherOutput\n" +
		            "otherOutput <- screenwidth * screenheight;\n" +
		            "if (c == true) {\n" +
		            "   output -> gray(1+2);\n" +
		            "   output -> show;\n" +
		            "}\n" +
		            "integer i\n" +
		            "i <- 3;\n" +
		            "while (i > 0) {\n" +
		            "   otherOutput -> scale(2);\n" +
		            "}\n" +
		            "sleep 1;\n" +
		            "}\n";*/
		    Parser parser = new Parser(new Scanner(input).scan());
		    parser.parse();
	}

}
