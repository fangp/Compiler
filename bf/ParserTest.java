package cop5556sp17;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp17.Parser.SyntaxException;
import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;


public class ParserTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testFactor0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "abc";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.factor();
	}

	@Test
	public void testArg() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "  (3,5) ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.arg();
	}

	@Test
	public void testArgerror() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "  (3,) ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
		parser.arg();
	}


	@Test
	public void testProgram0() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "prog0 {integer a}";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.parse();
	}

	// My own test cases

    @Test
    public void testWrongProgram() throws IllegalCharException, IllegalNumberException, SyntaxException{
        String[] input = {"1 {}",
                "program }",
                "program {",
                "{}",
                ""};
        for (String s : input) {
            Parser parser = new Parser(new Scanner(s).scan());
            thrown.expect(Parser.SyntaxException.class);
            parser.parse();
            thrown = ExpectedException.none();
        }
    }

    @Test
    public void testDec() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "program {integer a\n" +
                "boolean b\n" +
                "image c\n" +
                "frame d\n}";
        Parser parser = new Parser(new Scanner(input).scan());
        parser.parse();
    }

    @Test
    public void testWrongDec() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String[] input = {"program {file a}",
                "program {integer a <- 5;}",
                "program {integer a <- 5}",
                "program {integer a;}",
                "program {integer 5;}"};
        for (String s : input) {
            Parser parser = new Parser(new Scanner(s).scan());
            thrown.expect(Parser.SyntaxException.class);
            parser.parse();
            thrown = ExpectedException.none();
        }
    }

    @Test
    public void testParamDec() throws IllegalCharException, IllegalNumberException, SyntaxException{
        String[] input = {"program integer a {}",
                "program url a {}",
                "program file a {}",
                "program boolean a, integer b {}"};
        for (String s : input) {
            Parser parser = new Parser(new Scanner(s).scan());
            parser.parse();
        }
    }

    @Test
    public void testWrongParamDec() throws IllegalCharException, IllegalNumberException, SyntaxException{
        String[] input = {"program image a {}",
                "program url 1 {}",
                "program 2 {}",
                "program integer a, {}",
                "program file, {}",
                "program , {}"};
        for (String s : input) {
            Parser parser = new Parser(new Scanner(s).scan());
            thrown.expect(Parser.SyntaxException.class);
            parser.parse();
            thrown = ExpectedException.none();
        }
    }

    @Test
    public void testStatement() throws IllegalCharException, IllegalNumberException, SyntaxException{
        String[] input = {"sleep 1;",
                "while (b) {}",
                "if (1 == 2) {}",
                "a -> show(1,2) |-> c;",
                "a <- 10;"};
        for (String s : input) {
            Parser parser = new Parser(new Scanner(s).scan());
            parser.statement();
        }
    }

    @Test
    public void testWrongStatement() throws IllegalCharException, IllegalNumberException, SyntaxException{
        String[] input = {"sleep 1",
                "while (b) {", "while (b) }", "while (b {}", "while () {}",
                "while ( {}", "while ) {}", "while {}",
                "if (b) {", "if (b) }", "if (b {}", "if () {}", "if ( {}",
                "if ) {}", "if {}",
                "show;", "1 -> show(1,2) |-> c;", "a -> show(1,2) |-> c",
                "-> show(1,2) |-> c;", "1 -> show(1,2) <- c;", "1 -> ;",
                "a <- 10;", "10 <- 10;", "a <- 10","show <- 10;", "a == 10;",
                ";", "a;", "10;", "<-;", "boolean;", "{}"};
        for (String s : input) {
            Parser parser = new Parser(new Scanner(s).scan());
            thrown.expect(Parser.SyntaxException.class);
            parser.statement();
            thrown = ExpectedException.none();
        }
    }

    @Test
    public void testExpression() throws IllegalCharException, IllegalNumberException, SyntaxException{
        String[] input = {"1+2",
                "(1+2)*3",
                "5%2",
                "true | false",
                "1 != 2",
                "4 % ((true & 1) - 2 * 5) >= false + screenheight * a"};
        for (String s : input) {
            Parser parser = new Parser(new Scanner(s).scan());
            parser.expression();
        }
    }


    @Test
    public void testComplexProgram() throws IllegalCharException, IllegalNumberException, SyntaxException{
        String input = "main integer a, integer b {\n" +
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
                "   otherOutput -> scale(1/2, (5+1)*4);\n" +
                "}\n" +
                "hide -> output |-> otherOutput;\n" +
                "sleep 1;\n" +
                "}\n";
        Parser parser = new Parser(new Scanner(input).scan());
        parser.parse();
    }

}
