package cop5556sp17;

import java.util.ArrayList;

import cop5556sp17.Scanner.Kind;

public class Scanner {
	/**
	 * Kind enum
	 */
	
	public static enum Kind {
		IDENT(""), INT_LIT(""), KW_INTEGER("integer"), KW_BOOLEAN("boolean"), 
		KW_IMAGE("image"), KW_URL("url"), KW_FILE("file"), KW_FRAME("frame"), 
		KW_WHILE("while"), KW_IF("if"), KW_TRUE("true"), KW_FALSE("false"), 
		SEMI(";"), COMMA(","), LPAREN("("), RPAREN(")"), LBRACE("{"), 
		RBRACE("}"), ARROW("->"), BARARROW("|->"), OR("|"), AND("&"), 
		EQUAL("=="), NOTEQUAL("!="), LT("<"), GT(">"), LE("<="), GE(">="), 
		PLUS("+"), MINUS("-"), TIMES("*"), DIV("/"), MOD("%"), NOT("!"), 
		ASSIGN("<-"), OP_BLUR("blur"), OP_GRAY("gray"), OP_CONVOLVE("convolve"), 
		KW_SCREENHEIGHT("screenheight"), KW_SCREENWIDTH("screenwidth"), 
		OP_WIDTH("width"), OP_HEIGHT("height"), KW_XLOC("xloc"), KW_YLOC("yloc"), 
		KW_HIDE("hide"), KW_SHOW("show"), KW_MOVE("move"), OP_SLEEP("sleep"), 
		KW_SCALE("scale"), EOF("eof");

		Kind(String text) {
			this.text = text;
		}

		final String text;

		String getText() {
			return text;
		}
	}
/**
 * Thrown by Scanner when an illegal character is encountered
 */
	@SuppressWarnings("serial")
	public static class IllegalCharException extends Exception {
		public IllegalCharException(String message) {
			super(message);
		}
	}
	
	/**
	 * Thrown by Scanner when an int literal is not a value that can be represented by an int.
	 */
	@SuppressWarnings("serial")
	public static class IllegalNumberException extends Exception {
	public IllegalNumberException(String message){
		super(message);
		}
	}
	

	/**
	 * Holds the line and position in the line of a token.
	 */
	static class LinePos {
		public final int line;
		public final int posInLine;
		
		public LinePos(int line, int posInLine) {
			super();
			this.line = line;
			this.posInLine = posInLine;
		}

		@Override
		public String toString() {
			return "LinePos [line=" + line + ", posInLine=" + posInLine + "]";
		}
	}
		

	

	public class Token {
		public final Kind kind;
		public final int pos;  //position in input array
		public final int length;  

		//returns the text of this Token
		public String getText() {
			//TODO IMPLEMENT THIS
			String temp = chars.substring(pos, pos+length);
			if(this.kind==Kind.EOF)
				temp = Kind.EOF.text;
			return temp;
		}
		
		//returns a LinePos object representing the line and column of this Token
		LinePos getLinePos(){
			//TODO IMPLEMENT THIS
			int temline=0;
			int tempos = 0;
			for(int i=0;i<lines.size();i++){
				if(pos>lines.get(i)){
					continue;
				}
				
				else{
					temline=i;
					if(i==0){
						
						tempos = pos;
					}
					else{
						//System.out.println(pos+" "+lines.get(i-1));
						tempos = pos-lines.get(i-1)-1;
					}
					break;
				}
			}
			
			return new LinePos(temline, tempos);
		}

		Token(Kind kind, int pos, int length) {
			this.kind = kind;
			this.pos = pos;
			this.length = length;
		}

		/** 
		 * Precondition:  kind = Kind.INT_LIT,  the text can be represented with a Java int.
		 * Note that the validity of the input should have been checked when the Token was created.
		 * So the exception should never be thrown.
		 * 
		 * @return  int value of this token, which should represent an INT_LIT
		 * @throws NumberFormatException
		 */
		public int intVal() throws NumberFormatException{
			//TODO IMPLEMENT THIS
			if(this.kind==Kind.INT_LIT){
				return Integer.parseInt(this.getText());
			}
			else
				throw new NumberFormatException("unknown source");
				
		}

		public boolean isKind(Kind... kinds) {
			for(int i=0;i<kinds.length;i++){
				if(this.kind==kinds[i])
					return true;
			}
			return false;
		}
		@Override
		  public int hashCode() {
		   final int prime = 31;
		   int result = 1;
		   result = prime * result + getOuterType().hashCode();
		   result = prime * result + ((kind == null) ? 0 : kind.hashCode());
		   result = prime * result + length;
		   result = prime * result + pos;
		   return result;
		  }

		  @Override
		  public boolean equals(Object obj) {
		   if (this == obj) {
		    return true;
		   }
		   if (obj == null) {
		    return false;
		   }
		   if (!(obj instanceof Token)) {
		    return false;
		   }
		   Token other = (Token) obj;
		   if (!getOuterType().equals(other.getOuterType())) {
		    return false;
		   }
		   if (kind != other.kind) {
		    return false;
		   }
		   if (length != other.length) {
		    return false;
		   }
		   if (pos != other.pos) {
		    return false;
		   }
		   return true;
		  }

		 

		  private Scanner getOuterType() {
		   return Scanner.this;
		  }
		
	}

	 


	Scanner(String chars)  {
		this.chars = chars;
		tokens = new ArrayList<Token>();
		lines = new ArrayList<>();
		

	}


	
	/**
	 * Initializes Scanner object by traversing chars and adding tokens to tokens list.
	 * 
	 * @return this scanner
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 */
	public Scanner scan() throws IllegalCharException, IllegalNumberException {
		int pos = 0; 
		//TODO IMPLEMENT THIS!!!!
		check(chars);
		//tokens.add(new Token(Kind.EOF,pos,0));
		return this;  
	}



	final ArrayList<Token> tokens;
	final String chars;
	final ArrayList<Integer> lines;
	int tokenNum;

	/*
	 * Return the next token in the token list and update the state so that
	 * the next call will return the Token..  
	 */
	public Token nextToken() {
		if (tokenNum >= tokens.size())
			return null;
		return tokens.get(tokenNum++);
	}
	
	/*
	 * Return the next token in the token list without updating the state.
	 * (So the following call to next will return the same token.)
	 */
	public Token peek(){
		if (tokenNum >= tokens.size())
			return null;
		return tokens.get(tokenNum);		
	}

	

	/**
	 * Returns a LinePos object containing the line and position in line of the 
	 * given token.  
	 * 
	 * Line numbers start counting at 0
	 * 
	 * @param t
	 * @return
	 */
	public LinePos getLinePos(Token t) {
		//TODO IMPLEMENT THIS
		
		return t.getLinePos();
	}
	// start 0
	// after equal 1
	// after not 2
	// after lt 3
	// after gt 4
	// after or 5
	// after minus 6
	// after times 7
	// after div 8
	// num 9
	// letter 10
	// comment content  11
	// after \r 12
	// comment after * 13
	public void check(String str) throws IllegalCharException, IllegalNumberException{
		int pos=0;
		int length = str.length();
		int state=0;
		int startpos = 0;
		int ch=0;
		int localpos=0;
		int commentend = 0;
		int lineCount = 0;
		ArrayList<Character> letters = new ArrayList<>();
		while(pos<=length){
			ch=pos<length?str.charAt(pos):-1;
			//System.out.println(ch);
			switch(state){
			case 0: 
				while(Character.isWhitespace(ch)&&ch!='\n'&&ch!='\r'&&pos<length){
					pos++;
					startpos = pos;
					ch=pos<length?str.charAt(pos):-1;
				}
				ch=pos<length?str.charAt(pos):-1;
				//System.out.println(ch);
				switch(ch){
					case '\n':
						lines.add(pos);
						pos++;
						startpos = pos;
						state = 0;
						break;
					case '\r':
						pos++;
						startpos = pos;
						state = 12;
						break;
					case -1:
						tokens.add(new Token(Kind.EOF, startpos, 0)); 
						lines.add(pos);
						pos++;
						break;
					case '0':
						tokens.add(new Token(Kind.INT_LIT, startpos, pos-startpos+1));
						pos++;
						startpos = pos;
						break;
					case ',':
						tokens.add(new Token(Kind.COMMA, startpos, pos-startpos+1));
						pos++;
						startpos = pos;
						break;
					case ';':
						tokens.add(new Token(Kind.SEMI, startpos, pos-startpos+1));
						pos++;
						startpos = pos;
						break;
					case '(':
						tokens.add(new Token(Kind.LPAREN, startpos, pos-startpos+1));
						pos++;
						startpos = pos;
						break;
					case ')':
						tokens.add(new Token(Kind.RPAREN, startpos, pos-startpos+1));
						pos++;
						startpos = pos;
						break;
					case '{':
						tokens.add(new Token(Kind.LBRACE, startpos, pos-startpos+1));
						pos++;
						startpos = pos;
						break;
					case '}':
						tokens.add(new Token(Kind.RBRACE, startpos, pos-startpos+1));
						pos++;
						startpos = pos;
						break;
					case '&':
						tokens.add(new Token(Kind.AND, startpos, pos-startpos+1));
						pos++;
						startpos = pos;
						break;
					case '+':
						tokens.add(new Token(Kind.PLUS, startpos, pos-startpos+1));
						pos++;
						startpos = pos;
						break;
					case '%':
						tokens.add(new Token(Kind.MOD, startpos, pos-startpos+1));
						pos++;
						startpos = pos;
						break;
					case '=':
						state = 1;
						pos++;
						break;
					case '!':
						state = 2;
						pos++;
						break;
					case '<':
						state = 3;
						pos++;
						break;
					case '>':
						state = 4;
						pos++;
						break;
					case '|':
						state = 5;
						pos++;
						break;
					case '-':
						state = 6;
						pos++;
						break;
					case '*':
						state = 7;
						pos++;
						break;
					case '/':
						state = 8;
						pos++;
						break;
					default:
						if(checkNum(ch,0)){
							//System.out.println("1");
							state = 9;
							pos++;
						}
						else if(checkLetter(ch)){
							state = 10;
							letters.add((char) ch);
							pos++;
						}
						else
							throw new IllegalCharException("illegal char " +(char)ch+" at pos "+pos);
						break;
						
				}
				break;
			case 1:
				ch=pos<length?str.charAt(pos):-1;
				//System.out.println("2");
				if(ch==61){ //"="
					tokens.add(new Token(Kind.EQUAL, startpos, pos-startpos+1));
					pos++;
					startpos = pos;
					state = 0;
				}
				else
					throw new IllegalCharException("illegal char " +'='+" at pos "
													+(pos-1-(lines.size()>0?lines.get(lines.size()-1)+1:0))
													+" in line "+lines.size());

				break;
			case 2:
				ch=pos<length?str.charAt(pos):-1;
				if(ch==61){//"="
					tokens.add(new Token(Kind.NOTEQUAL, startpos, pos-startpos+1));
					pos++;
					startpos = pos;
					state = 0;
				}
				else{
					tokens.add(new Token(Kind.NOT, startpos, 1));
					startpos = pos;
					state = 0;
				}
				break;
			case 3:
				ch=pos<length?str.charAt(pos):-1;
				if(ch==61){//"="
					tokens.add(new Token(Kind.LE, startpos, pos-startpos+1));
					pos++;
					startpos = pos;
					state = 0;
				}
				else if(ch == '-'){
					tokens.add(new Token(Kind.ASSIGN, startpos, pos- startpos+1));
					pos++;
					startpos = pos;
					state = 0;
				}
				else{
					tokens.add(new Token(Kind.LT, startpos, 1));
					startpos = pos;
					state = 0;
				}
				break;
			case 4:
				ch=pos<length?str.charAt(pos):-1;
				if(ch==61){//"="
					tokens.add(new Token(Kind.GE, startpos, pos-startpos+1));
					pos++;
					startpos = pos;
					state = 0;
				}
				else{
					tokens.add(new Token(Kind.GT, startpos, 1));
					startpos = pos;
					state = 0;
				}
				break;
			case 5:
				ch=pos<length?str.charAt(pos):-1;
				if(ch=='-'){//"="
					pos++;
					if(pos<length){
						ch=str.charAt(pos);
						if(ch=='>'){
							tokens.add(new Token(Kind.BARARROW, startpos, pos-startpos+1));
							pos++;
							startpos=pos;
							state=0;
						}
						else{
							tokens.add(new Token(Kind.OR, startpos, 1));
							tokens.add(new Token(Kind.MINUS, startpos+1, 1));
							startpos=pos;
							state=0;
						}
					}
				}
				else{
					tokens.add(new Token(Kind.OR, startpos, 1));
					startpos = pos;
					state = 0;
				}
				break;
			case 6:
				ch=pos<length?str.charAt(pos):-1;
				if(ch=='>'){
					tokens.add(new Token(Kind.ARROW,startpos, pos- startpos+1));
					pos++;
					startpos = pos;
					state = 0;
				}
				else{
					tokens.add(new 	Token(Kind.MINUS, startpos, 1));
					startpos = pos;
					state = 0;
				}
				break;
			case 7:
				ch=pos<length?str.charAt(pos):-1;
				//if(ch=='/'){
				//		throw new IllegalCharException("illegal comment end " +"*/"+" at pos "+(pos-1));
				//}	
				//else{
					tokens.add(new Token(Kind.TIMES, startpos, 1));
					startpos = pos;
					state = 0;
				//}
				break;
			case 8:
				ch=pos<length?str.charAt(pos):-1;
				if(ch=='*'){
					pos++;
					startpos = pos;
					state = 11;
					commentend=2;
				}
				else{
					tokens.add(new Token(Kind.DIV, startpos, 1));
					startpos = pos;
					state = 0;
				}
				break;
			case 9:
				ch=pos<length?str.charAt(pos):-1;
				if(checkNum(ch,1))
					pos++;
				else{
					tokens.add(new Token(Kind.INT_LIT, startpos, pos- startpos));
					String temp = chars.substring(startpos, pos);
					if(temp.length()>10)
						throw new IllegalNumberException(temp+" out of range");
					else if(Long.parseLong(temp)>Integer.MAX_VALUE){
						throw new IllegalNumberException(temp+" out of range");
					}
					startpos = pos;
					state = 0;
				}
				break;
			case 10:
				ch=pos<length?str.charAt(pos):-1;
				if(checkNum(ch,1)||checkLetter(ch)){
					letters.add((char) ch);
					pos++;
				}
				else{
					String temp = "";
					for(char c:letters){
						temp=temp+c;
					}
					switch(temp){
					case "integer":
						tokens.add(new Token(Kind.KW_INTEGER, startpos, pos- startpos));
						startpos = pos;
						state = 0;
						letters.clear();
						break;
					case "boolean":
						tokens.add(new Token(Kind.KW_BOOLEAN, startpos, pos- startpos));
						startpos = pos;
						state = 0;
						letters.clear();
						break;
					case "image":
						tokens.add(new Token(Kind.KW_IMAGE, startpos, pos- startpos));
						startpos = pos;
						state = 0;
						letters.clear();
						break;
					case "url":
						tokens.add(new Token(Kind.KW_URL, startpos, pos- startpos));
						startpos = pos;
						state = 0;
						letters.clear();
						break;
					case "file":
						tokens.add(new Token(Kind.KW_FILE, startpos, pos- startpos));
						startpos = pos;
						state = 0;
						letters.clear();
						break;
					case "frame":
						tokens.add(new Token(Kind.KW_FRAME, startpos, pos- startpos));
						startpos = pos;
						state = 0;
						letters.clear();
						break;
					case "while":
						tokens.add(new Token(Kind.KW_WHILE, startpos, pos- startpos));
						startpos = pos;
						state = 0;
						letters.clear();
						break;
					case "if":
						tokens.add(new Token(Kind.KW_IF, startpos, pos- startpos));
						startpos = pos;
						state = 0;
						letters.clear();
						break;
					case "sleep":
						tokens.add(new Token(Kind.OP_SLEEP, startpos, pos- startpos));
						startpos = pos;
						state = 0;
						letters.clear();
						break;
					case "screenheight":
						tokens.add(new Token(Kind.KW_SCREENHEIGHT, startpos, pos- startpos));
						startpos = pos;
						state = 0;
						letters.clear();
						break;
					case "screenwidth":
						tokens.add(new Token(Kind.KW_SCREENWIDTH, startpos, pos- startpos));
						startpos = pos;
						state = 0;
						letters.clear();
						break;
					case "gray":
						tokens.add(new Token(Kind.OP_GRAY, startpos, pos- startpos));
						startpos = pos;
						state = 0;
						letters.clear();
						break;
					case "scale":
						tokens.add(new Token(Kind.KW_SCALE, startpos, pos- startpos));
						startpos = pos;
						state = 0;
						letters.clear();
						break;
					case "convolve":
						tokens.add(new Token(Kind.OP_CONVOLVE, startpos, pos- startpos));
						startpos = pos;
						state = 0;
						letters.clear();
						break;
					case "blur":
						tokens.add(new Token(Kind.OP_BLUR, startpos, pos- startpos));
						startpos = pos;
						state = 0;
						letters.clear();
						break;
					case "width":
						tokens.add(new Token(Kind.OP_WIDTH, startpos, pos- startpos));
						startpos = pos;
						state = 0;
						letters.clear();
						break;
					case "height":
						tokens.add(new Token(Kind.OP_HEIGHT, startpos, pos- startpos));
						startpos = pos;
						state = 0;
						letters.clear();
						break;
					case "xloc":
						tokens.add(new Token(Kind.KW_XLOC, startpos, pos- startpos));
						startpos = pos;
						state = 0;
						letters.clear();
						break;
					case "yloc":
						tokens.add(new Token(Kind.KW_YLOC, startpos, pos- startpos));
						startpos = pos;
						state = 0;
						letters.clear();
						break;
					case "hide":
						tokens.add(new Token(Kind.KW_HIDE, startpos, pos- startpos));
						startpos = pos;
						state = 0;
						letters.clear();
						break;
					case "show":
						tokens.add(new Token(Kind.KW_SHOW, startpos, pos- startpos));
						startpos = pos;
						state = 0;
						letters.clear();
						break;
					case "move":
						tokens.add(new Token(Kind.KW_MOVE, startpos, pos- startpos));
						startpos = pos;
						state = 0;
						letters.clear();
						break;
					case "true":
						tokens.add(new Token(Kind.KW_TRUE, startpos, pos- startpos));
						startpos = pos;
						state = 0;
						letters.clear();
						break;
					case "false":
						tokens.add(new Token(Kind.KW_FALSE, startpos, pos- startpos));
						startpos = pos;
						state = 0;
						letters.clear();
						break;
					default:
						tokens.add(new Token(Kind.IDENT, startpos, pos - startpos));
						startpos=pos;
						state = 0;
						letters.clear();
						break;
					}
				}
				break;
			case 11:
				ch=pos<length?str.charAt(pos):-1;
				if(ch=='\n'){
					lines.add(pos);
					pos++;
					startpos = pos;
				}
				else if(ch=='\r'){
					pos++;
					startpos = pos;
					int t = pos<length?str.charAt(pos):-1;
					if(t=='\n'){
						lines.add(pos);
						pos++;
						startpos = pos;
					}
					else{
						lines.add(pos-1);
						startpos = pos;
					}
				}
				else if(Character.isWhitespace(ch)){
					pos++;
					startpos = pos;
				}
				else if(ch==-1){
					startpos = pos;
					state = 0;
				}
				else if(ch=='*'){
					pos++;
					startpos = pos;
					state = 13;
				}
				else{
					pos++;
					startpos = pos;
				}
				/*else if(commentend==2){
					if(ch=='*'){
						commentend--;
						pos++;
						startpos = pos;
					}
					else{
						pos++;
						startpos = pos;
					}
				}
				else if(commentend==1){
					if(ch=='/'){
						commentend--;
						pos++;
						startpos = pos;
					}
					else{
						pos++;
						startpos = pos;
					}
				}
				else if(commentend==0){
					state = 0;
				}*/
				break;
			case 12:
				ch=pos<length?str.charAt(pos):-1;
				if(ch=='\n'){
					lines.add(pos);
					pos++;
					startpos = pos;
					state=0;
				}
				else{
					//lines.add(pos-1);
					startpos = pos;
					state=0;
				}
				break;
			case 13:
				ch=pos<length?str.charAt(pos):-1;
				if(ch=='/'){
					pos++;
					startpos = pos;
					state=0;
				}
				else{
					state = 11;
				}
			}
		}
	}
	
	public boolean checkNum(int ch, int type){
		if(type==0){
			if(ch>48&&ch<=57)
				return true;
			else
				return false;
		}
		else{
			if(ch>=48&&ch<=57)
				return true;
			else
				return false;
		}
	}
	public boolean checkLetter(int ch){
		if((ch<=90&&ch>=65)||(ch>=97&&ch<=122)||ch==36||ch==95)
			return true;
		else
			return false;
	}
	public boolean checkSpace(int ch){
		if(ch==32)
			return true;
		else
			return false;
	}
	/*public static void main(String args[]) throws IllegalCharException, IllegalNumberException{
		String input = "main integer a, boolean b {"
				+ " while((a)) {"
				+ "if(abbc){"
				+ "integer aa"
				+ "i <- 7a7;}} "
				+ "}";
		Scanner sc = new Scanner(input);
		sc.scan();
		System.out.println(sc.tokens.size());
		for(int i=0;i<sc.tokens.size();i++){
			//System.out.println(tokens.get(i).pos+" "+tokens.get(i).length);
			System.out.print(sc.tokens.get(i).getText()+"\t");
			System.out.println(sc.tokens.get(i).kind);
		}
	}*/
}
