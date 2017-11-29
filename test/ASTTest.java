package cop5556sp17;

import static cop5556sp17.Scanner.Kind.PLUS;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp17.Parser.SyntaxException;
import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;
import cop5556sp17.Scanner.Kind;
import cop5556sp17.TypeCheckVisitor.TypeCheckException;
import cop5556sp17.AST.*;

public class ASTTest {

	static final boolean doPrint = true;
	static void show(Object s){
		if(doPrint){System.out.println(s);}
	}
	

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testFactor0() throws IllegalCharException, IllegalNumberException, SyntaxException, TypeCheckException {
		String input = "abc";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.expression();
		assertEquals(IdentExpression.class, ast.getClass());
	}

	@Test
	public void testFactor1() throws IllegalCharException, IllegalNumberException, SyntaxException, TypeCheckException {
		String input = "x <- 33 ;";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		Statement ast =   parser.statement();
		assertEquals(ast.getClass(), Kind.INT_LIT);
	}



	@Test
	public void testBinaryExpr0() throws IllegalCharException, IllegalNumberException, SyntaxException, TypeCheckException {
		String input = "1+abc";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.expression();
		assertEquals(BinaryExpression.class, ast.getClass());
		BinaryExpression be = (BinaryExpression) ast;
		assertEquals(IntLitExpression.class, be.getE0().getClass());
		assertEquals(IdentExpression.class, be.getE1().getClass());
		assertEquals(PLUS, be.getOp().kind);
	}
	@Test
	public void testBinaryExpr1() throws IllegalCharException, IllegalNumberException, SyntaxException, TypeCheckException {
		String input = "1<2<3";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.expression();
		assertEquals(BinaryExpression.class, ast.getClass());
		BinaryExpression be = (BinaryExpression) ast;
		assertEquals(BinaryExpression.class, be.getE0().getClass());
		assertEquals(IntLitExpression.class, be.getE1().getClass());
		//assertEquals(PLUS, be.getOp().kind);
	}
	
	@Test
	public void testChain0() throws IllegalCharException, IllegalNumberException, SyntaxException, TypeCheckException {
		String input = "blur -> b -> a";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.chain();
		assertEquals(BinaryChain.class, ast.getClass());
		BinaryChain be = (BinaryChain) ast;
		BinaryChain be1 = (BinaryChain)be.getE0();
		assertEquals(BinaryChain.class, be1.getClass());
		assertEquals(FilterOpChain.class, be1.getE0().getClass());
		assertEquals(IdentChain.class, be1.getE1().getClass());
		assertEquals(Kind.ARROW, be.getArrow().kind);
	}
	
	@Test
	public void testChain1() throws IllegalCharException, IllegalNumberException, SyntaxException, TypeCheckException {
		String input = "blur -> hide (a, b)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.chain();
		assertEquals(BinaryChain.class, ast.getClass());
		BinaryChain be = (BinaryChain) ast;
		assertEquals(FilterOpChain.class, be.getE0().getClass());
		assertEquals(FrameOpChain.class, be.getE1().getClass());
		assertEquals(FrameOpChain.class, be.getE1().getClass());
		assertEquals(Kind.ARROW, be.getArrow().kind);
	}
	@Test
	public void testprogram() throws IllegalCharException, IllegalNumberException, SyntaxException, TypeCheckException {
		String input = "program integer b, file a {"
				+ "frame aa "
				+ "boolean bb "
				+ "sleep 1+3; "
				+ "hide -> blur -> scale |-> cc; "
				+ "while (a<b+c) {"
				+ "abc <- 2;"
				+ "if (i) {"
				+ "sleep 55;"
				+ "hide (1, (a+c)<4)-> aan;"
				+ "}"
				+ "}"
				+ "}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.parse();
		assertEquals(Program.class, ast.getClass());
		Program be = (Program) ast;
		//proram
		assertEquals("program", be.getName());
		assertEquals(Block.class, be.getB().getClass());
		List<ParamDec> list1 = new ArrayList<>(be.getParams());
		//integer b, file a
		assertEquals(list1.get(0).getType().kind, Kind.KW_INTEGER);
		assertEquals(list1.get(1).getType().kind, Kind.KW_FILE);
		Block B = be.getB();
		List<Dec> list2 = new ArrayList<>(B.getDecs());
		//frame aa
		assertEquals(list2.get(0).getType().kind, Kind.KW_FRAME);
		//boolean bb
		assertEquals(list2.get(1).getType().kind, Kind.KW_BOOLEAN);
		List<Statement> list3 = new ArrayList<>(B.getStatements());
		//sleep 1+3;
		assertEquals(list3.get(0).getClass(), SleepStatement.class);
		SleepStatement ss = (SleepStatement) list3.get(0);
		assertEquals(ss.getE().getClass(), BinaryExpression.class);
		BinaryExpression be1 = (BinaryExpression) ss.getE();
		assertEquals(be1.getE0().firstToken.kind, Kind.INT_LIT);
		assertEquals(be1.getOp().kind, Kind.PLUS);
		assertEquals(be1.getE1().firstToken.kind, Kind.INT_LIT);
		assertEquals(list3.get(1).getClass(), BinaryChain.class);
		BinaryChain bc = (BinaryChain) list3.get(1);
		//hide -> blur -> scale |-> cc;
		assertEquals(bc.getE0().getClass(), BinaryChain.class);
		assertEquals(bc.getArrow().kind, Kind.BARARROW);
		assertEquals(bc.getE1().getClass(), IdentChain.class);
		BinaryChain bc1 = (BinaryChain) bc.getE0();
		assertEquals(bc1.getE0().getClass(), BinaryChain.class);
		assertEquals(bc1.getArrow().kind, Kind.ARROW);
		assertEquals(bc1.getE1().getClass(), ImageOpChain.class);
		BinaryChain bc2 = (BinaryChain) bc1.getE0();
		assertEquals(bc2.getE0().getClass(),  FrameOpChain.class);
		assertEquals(bc2.getArrow().kind, Kind.ARROW);
		assertEquals(bc2.getE1().getClass(), FilterOpChain.class);
		//while 
		WhileStatement ws = (WhileStatement) list3.get(2);
		//(a<b+c)
		assertEquals(ws.getE().getClass(), BinaryExpression.class );
		BinaryExpression e1 = (BinaryExpression) ws.getE();
		assertEquals(e1.getE1().getClass(), BinaryExpression.class );
		assertEquals(e1.getOp().kind, Kind.LT );
		assertEquals(e1.getE0().getClass(), IdentExpression.class );
		BinaryExpression e2 = (BinaryExpression) e1.getE1();
		assertEquals(e2.getE0().firstToken.getText(), "b" );
		assertEquals(e2.getOp().kind, Kind.PLUS );
		assertEquals(e2.getE1().firstToken.getText(), "c" );
		Block B1 = ws.getB();
		List<Dec> list4 = new ArrayList<>(B1.getDecs());
		List<Statement> list5 = new ArrayList<>(B1.getStatements());
		assertEquals(list4.size(), 0 );
		assertEquals(list5.get(0).getClass(), AssignmentStatement.class );
		AssignmentStatement as = (AssignmentStatement) list5.get(0);
		//abc <- 2
		assertEquals(as.getE().getFirstToken().kind, Kind.INT_LIT);
		assertEquals(as.getVar().getFirstToken().kind, Kind.IDENT);
		//if
		assertEquals(list5.get(1).getClass(), IfStatement.class );
		IfStatement is = (IfStatement) list5.get(1);
		//(i)
		assertEquals(is.getE().firstToken.kind, Kind.IDENT);
		Block b3 = is.getB();
		assertEquals(b3.getDecs().size(), 0);
		assertEquals(b3.getStatements().size(), 2);
		List<Statement> list6 = new ArrayList<>(b3.getStatements());
		//sleep 55;
		assertEquals(list6.get(0).getClass(), SleepStatement.class);
		assertEquals(list6.get(1).getClass(), BinaryChain.class);
		BinaryChain bc3 = (BinaryChain) list6.get(1);
		//hide (1, (a+c)<4)-> aan;
		assertEquals(bc3.getE0().getClass(), FrameOpChain.class);
		FrameOpChain foc = (FrameOpChain) bc3.getE0();
		assertEquals(foc.getArg().getExprList().size(), 2);
		List<Expression> list7 = new ArrayList<>(foc.getArg().getExprList());
		assertEquals(list7.get(0).getClass(), IntLitExpression.class);
		assertEquals(list7.get(1).getClass(), BinaryExpression.class);
		assertEquals(list7.get(1).getClass(), BinaryExpression.class);
		BinaryExpression bc5 = (BinaryExpression) list7.get(1);
		assertEquals(bc5.getE1().getClass(), IntLitExpression.class);
	}
	
}
