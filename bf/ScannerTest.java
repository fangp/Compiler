package cop5556sp17;

import static cop5556sp17.Scanner.Kind.*;
import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;
import cop5556sp17.Scanner.Kind;

public class ScannerTest {

	@Rule
    public ExpectedException thrown = ExpectedException.none();


	
	@Test
	public void testEmpty() throws IllegalCharException, IllegalNumberException {
		String input = "";
		Scanner scanner = new Scanner(input);
		scanner.scan();
	}

	@Test
	public void testSemiConcat() throws IllegalCharException, IllegalNumberException {
		//input string
		String input = ";;;";
		//create and initialize the scanner
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//get the first token and check its kind, position, and contents
		Scanner.Token token = scanner.nextToken();
		assertEquals(SEMI, token.kind);
		assertEquals(0, token.pos);
		String text = SEMI.getText();
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());
		//get the next token and check its kind, position, and contents
		Scanner.Token token1 = scanner.nextToken();
		assertEquals(SEMI, token1.kind);
		assertEquals(1, token1.pos);
		assertEquals(text.length(), token1.length);
		assertEquals(text, token1.getText());
		Scanner.Token token2 = scanner.nextToken();
		assertEquals(SEMI, token2.kind);
		assertEquals(2, token2.pos);
		assertEquals(text.length(), token2.length);
		assertEquals(text, token2.getText());
		//check that the scanner has inserted an EOF token at the end
		Scanner.Token token3 = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF,token3.kind);
	}
	
	
	/**
	 * This test illustrates how to check that the Scanner detects errors properly. 
	 * In this test, the input contains an int literal with a value that exceeds the range of an int.
	 * The scanner should detect this and throw and IllegalNumberException.
	 * 
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 */
	@Test
	public void testIntOverflowError() throws IllegalCharException, IllegalNumberException{
		String input = "99999999999999999";
		Scanner scanner = new Scanner(input);
		thrown.expect(IllegalNumberException.class);
		scanner.scan();		
	}
	
	@Test
	public void keywordCheck() throws IllegalCharException, IllegalNumberException{
		String input = "integer boolean image url file frame while if sleep screenheight screenwidth "
				+ "gray convolve blur scale width height xloc yloc hide show move true false ;,(){}|&==!="
				+ "<><=>=+-*/%!->|-><-";
		Scanner scanner = new Scanner(input);
        scanner.scan();
        Scanner.Token token = scanner.nextToken();
		assertEquals(KW_INTEGER, token.kind);
		assertEquals(0, token.pos);
		String text = KW_INTEGER.getText();
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());
		
		
        Scanner.Kind[] result = {KW_INTEGER, KW_BOOLEAN, KW_IMAGE, KW_URL, KW_FILE,
                KW_FRAME, KW_WHILE, KW_IF, OP_SLEEP, KW_SCREENHEIGHT, KW_SCREENWIDTH,
                OP_GRAY, OP_CONVOLVE, OP_BLUR, KW_SCALE, OP_WIDTH, OP_HEIGHT, KW_XLOC, 
                KW_YLOC, KW_HIDE, KW_SHOW, KW_MOVE, KW_TRUE, KW_FALSE, SEMI, COMMA, LPAREN,
                RPAREN, LBRACE, RBRACE, OR, AND, EQUAL, NOTEQUAL, LT, GT, LE, GE, PLUS, MINUS, 
                TIMES, DIV, MOD, NOT, ARROW, BARARROW, ASSIGN};
        int[] resultPos = {0, 8, 16, 22, 26, 31, 37, 43, 46, 52, 65, 76, 81, 90, 95, 101, 107, 
                			114, 119, 124, 129, 134, 139, 144  };
	}
	

//TODO  more tests
	
}
