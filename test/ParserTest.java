package cop5556sp17;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp17.Parser.SyntaxException;
import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;
import cop5556sp17.TypeCheckVisitor.TypeCheckException;


public class ParserTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testFactor0() throws IllegalCharException, IllegalNumberException, SyntaxException, TypeCheckException {
		String input = "abc";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.factor();
	}

	@Test
	public void testArg() throws IllegalCharException, IllegalNumberException, SyntaxException, TypeCheckException {
		String input = "  (3,5) ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.arg();
	}

	@Test
	public void testArgerror() throws IllegalCharException, IllegalNumberException, SyntaxException, TypeCheckException {
		String input = "  (3,) ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
		parser.arg();
	}


	@Test
	public void testProgram0() throws IllegalCharException, IllegalNumberException, SyntaxException, TypeCheckException{
		String input = "prog0 {integer a}";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.parse();
	}

	// My test
	@Test
	public void test1() throws IllegalCharException, IllegalNumberException, SyntaxException, TypeCheckException{
		String input = "prog0 {integer a";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(Parser.SyntaxException.class);
		parser.parse();
	}
	@Test
	public void test2() throws IllegalCharException, IllegalNumberException, SyntaxException, TypeCheckException{
		String input = "(((a)),(b))";
		Parser parser = new Parser(new Scanner(input).scan());
		//thrown.expect(Parser.SyntaxException.class);
		parser.arg();
	}
	public void test3() throws IllegalCharException, IllegalNumberException, SyntaxException, TypeCheckException{
		String input = "main integer a, boolean b {"
				+ " while((a)) {"
				+ "if(abbc){"
				+ "integer aa"
				+ "i <- 7a7;}} "
				+ "}";
		Parser parser = new Parser(new Scanner(input).scan());
		//thrown.expect(Parser.SyntaxException.class);
		parser.parse();
	}
	public void test4() throws IllegalCharException, IllegalNumberException, SyntaxException, TypeCheckException{
		String input = "blur -> 1+2 -> a";
		Parser parser = new Parser(new Scanner(input).scan());
		//thrown.expect(Parser.SyntaxException.class);
		parser.chain();
	}

}
