package cop5556sp17;

import cop5556sp17.Scanner.Kind;
import static cop5556sp17.Scanner.Kind.*;

import java.util.ArrayList;
import java.util.List;

import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;
import cop5556sp17.Scanner.Token;
import cop5556sp17.AST.AssignmentStatement;
import cop5556sp17.AST.BinaryChain;
import cop5556sp17.AST.BinaryExpression;
import cop5556sp17.AST.Block;
import cop5556sp17.AST.BooleanLitExpression;
import cop5556sp17.AST.Chain;
import cop5556sp17.AST.ChainElem;
import cop5556sp17.AST.ConstantExpression;
import cop5556sp17.AST.Dec;
import cop5556sp17.AST.Expression;
import cop5556sp17.AST.FilterOpChain;
import cop5556sp17.AST.FrameOpChain;
import cop5556sp17.AST.IdentChain;
import cop5556sp17.AST.IdentExpression;
import cop5556sp17.AST.IdentLValue;
import cop5556sp17.AST.IfStatement;
import cop5556sp17.AST.ImageOpChain;
import cop5556sp17.AST.IntLitExpression;
import cop5556sp17.AST.ParamDec;
import cop5556sp17.AST.Program;
import cop5556sp17.AST.SleepStatement;
import cop5556sp17.AST.Statement;
import cop5556sp17.AST.Tuple;
import cop5556sp17.AST.WhileStatement;

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
	Program parse() throws SyntaxException {
		Program p = program();
		matchEOF();
		return p;
	}

	Expression expression() throws SyntaxException {
		//TODO
		Token first = t;
		Expression e1 = term();
		Kind[] kinds = {Kind.LT, Kind.LE, Kind.GE, Kind.GT, Kind.EQUAL, Kind.NOTEQUAL};
		while(t.isKind(kinds)){
			Token op = consume();
			Expression e2 = term();
			e1 = new BinaryExpression(first, e1, op, e2 );
		}
		return e1;
	}

	Expression term() throws SyntaxException {
		//TODO
		Token first = t;
		Expression e1 = elem();
		Kind[] kinds={Kind.PLUS, Kind.MINUS, Kind.OR};
		while(t.isKind(kinds)){
			Token op = consume();
			Expression e2 = elem();
			e1 = new BinaryExpression(first, e1, op, e2);
		}
		return e1;
	}

	Expression elem() throws SyntaxException {
		//TODO
		Token first = t;
		Expression e1 = factor();
		Kind[] kinds = {Kind.DIV, Kind.TIMES, Kind.AND, Kind.MOD};
		while(t.isKind(kinds)){
			Token op = consume();
			Expression e2 = factor();
			e1 = new BinaryExpression(first, e1, op, e2);
		}
		return e1;
	}

	Expression factor() throws SyntaxException {
		Kind kind = t.kind;
		Expression expr=null;
		switch (kind) {
		case IDENT: {
			return new IdentExpression(consume());
		}
		case INT_LIT: {
			return new IntLitExpression(consume());
		}
		case KW_TRUE:
		case KW_FALSE: {
			return new BooleanLitExpression(consume());
		}
		case KW_SCREENWIDTH:
		case KW_SCREENHEIGHT: {
			return new ConstantExpression(consume());
		}
		case LPAREN: {
			consume();
			Expression e = expression();
			match(RPAREN);
			return e;
		}
		default:
			//you will want to provide a more useful error message
			throw new SyntaxException("illegal factor "+t.kind);
		}
	}

	Block block() throws SyntaxException {
		//TODO
		ArrayList<Dec> declist = new ArrayList<>();
		ArrayList<Statement> statlist = new ArrayList<>();
		Token first = t;
		Kind[] decOp={Kind.KW_INTEGER, Kind.KW_BOOLEAN, Kind.KW_IMAGE, Kind.KW_FRAME};
		Kind[] pdec = {Kind.KW_URL, Kind.KW_FILE, Kind.KW_BOOLEAN, Kind.KW_INTEGER};
		Kind[] stat = {Kind.OP_SLEEP, Kind.KW_WHILE, Kind.KW_IF, Kind.IDENT};
		match(Kind.LBRACE);
		if(t.isKind(decOp)){
			declist.add(dec());
		}
		while(!t.isKind(Kind.RBRACE)){
			if(t.isKind(decOp)){
				declist.add(dec());
			}
			else{
				statlist.add(statement());
			}
		}
		match(Kind.RBRACE);
		return new Block(first, declist, statlist);
	}

	Program program() throws SyntaxException {
		//TODO
		ArrayList<ParamDec> list = new ArrayList<>();
		Block b = null;
		Kind[] decOp={Kind.KW_INTEGER, Kind.KW_BOOLEAN, Kind.KW_IMAGE, Kind.KW_FRAME};
		Kind[] pdec = {Kind.KW_URL, Kind.KW_FILE, Kind.KW_BOOLEAN, Kind.KW_INTEGER};
		Kind[] stat = {Kind.OP_SLEEP, Kind.KW_WHILE, Kind.KW_IF, Kind.IDENT};
		Token first = match(Kind.IDENT);
		if(t.isKind(pdec)){
			list.add(paramDec());
			while(t.isKind(Kind.COMMA)){
				consume();
				list.add(paramDec());
			}
			if(t.isKind(Kind.LBRACE)){
				b = block();
			}
		}
		else if(t.isKind(Kind.LBRACE)){
			b = block();
		}
		return new Program(first, list, b);
		
	}

	ParamDec paramDec() throws SyntaxException {
		//TODO
		Kind[] pdec = {Kind.KW_URL, Kind.KW_FILE, Kind.KW_BOOLEAN, Kind.KW_INTEGER};
		Token first = match(pdec);
		Token ident = match(Kind.IDENT);
		return new ParamDec(first, ident);
	}

	Dec dec() throws SyntaxException {
		//TODO
		Kind[] decOp={Kind.KW_INTEGER, Kind.KW_BOOLEAN, Kind.KW_IMAGE, Kind.KW_FRAME};
		Token type = match(decOp);
		Token ident = match(Kind.IDENT);
		return new Dec(type, ident);
	}

	Statement statement() throws SyntaxException {
		//TODO
		if(t.isKind(Kind.OP_SLEEP)){
			Token op = consume();
			Expression e = expression();
			match(Kind.SEMI);
			return new SleepStatement(op, e);
		}
		else if(t.isKind(Kind.KW_WHILE)){
			Token op = consume();
			match(Kind.LPAREN);
			Expression e = expression();
			match(Kind.RPAREN);
			Block b = block();
			return new WhileStatement(op, e, b);
		}
		else if(t.isKind(Kind.KW_IF)){
			Token op = consume();
			match(Kind.LPAREN);
			Expression e = expression();
			match(Kind.RPAREN);
			Block b = block();
			return new IfStatement(op, e, b);
		}
		else if(t.isKind(Kind.IDENT)&&scanner.peek().isKind(Kind.ASSIGN)){
			Token first = t;
			IdentLValue ident = new IdentLValue(match(Kind.IDENT));
			match(Kind.ASSIGN);
			Expression e = expression();
			match(Kind.SEMI);
			return new AssignmentStatement(first, ident, e);
			//System.out.println(t.getText());
		}
		else{
			Chain c = chain();
			match(Kind.SEMI);
			return c;
		}
	}

	Chain chain() throws SyntaxException {
		//TODO
		Token first = t;
		Chain e1 = chainElem();
		Kind[] arrowOp = {Kind.ARROW, Kind.BARARROW};
		Token op = match(arrowOp);
		ChainElem e2 = chainElem();
		e1 = new BinaryChain(first, e1, op, e2);
		while(t.isKind(arrowOp)){
			Token op2 = consume();
			ChainElem e3= chainElem();
			e1 = new BinaryChain(first, e1, op2, e3);
		}
		return e1;
	}

	ChainElem chainElem() throws SyntaxException {
		//TODO
		Kind kind = t.kind;
		Kind[] filterOp= {Kind.OP_BLUR, Kind.OP_GRAY, Kind.OP_CONVOLVE};
		Kind[] frameOp = {Kind.KW_MOVE, Kind.KW_SHOW, Kind.KW_HIDE, Kind.KW_XLOC, Kind.KW_YLOC};
		Kind[] imageOp = {Kind.OP_HEIGHT, Kind.OP_WIDTH, Kind.KW_SCALE};
		if(kind==Kind.IDENT){
			return new IdentChain(consume());
		}
		else if(t.isKind(filterOp)){
			Token op = consume();
			Tuple tp = arg();
			return new FilterOpChain(op, tp);
		}
		else if(t.isKind(frameOp)){
			Token op = consume();
			Tuple tp = arg();
			return new FrameOpChain(op, tp);
		}
		else if(t.isKind(imageOp)){
			Token op = consume();
			Tuple tp = arg();
			return new ImageOpChain(op, tp);
		}
		else
			throw new SyntaxException("illegal " + kind);
			
		
	}

	Tuple arg() throws SyntaxException {
		//TODO
		Token first = null;
		List<Expression> list = new ArrayList<>();
		if(t.isKind(Kind.LPAREN)){
			first = t;
			consume();
			list.add(expression());
			while(t.isKind(Kind.COMMA)){
				consume();
				list.add(expression());
			}
			match(Kind.RPAREN);
		}
		return new Tuple(first, list);
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
		throw new SyntaxException("expected EOF "+ "saw" + t.kind);
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
	/*public static void main(String args[]) throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input ="(3+4 , 5)";
		Parser parser = new Parser(new Scanner(input).scan());
		Tuple be =  (Tuple) parser.arg();
		List<Expression> list = be.getExprList();
		BinaryExpression be1 = (BinaryExpression) list.get(0);
		System.out.println(be1.getE1().getFirstToken().intVal());
	}*/

}
