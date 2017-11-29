package cop5556sp17;

import cop5556sp17.AST.ASTNode;
import cop5556sp17.AST.ASTVisitor;
import cop5556sp17.AST.Tuple;
import cop5556sp17.AST.Type;
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
import cop5556sp17.AST.Type.TypeName;
import cop5556sp17.AST.WhileStatement;

import java.util.ArrayList;

import cop5556sp17.Scanner.Kind;
import cop5556sp17.Scanner.LinePos;
import cop5556sp17.Scanner.Token;
import cop5556sp17.TypeCheckVisitor.TypeCheckException;

import static cop5556sp17.AST.Type.TypeName.*;
import static cop5556sp17.Scanner.Kind.*;

public class TypeCheckVisitor implements ASTVisitor {

	@SuppressWarnings("serial")
	public static class TypeCheckException extends Exception {
		TypeCheckException(String message) {
			super(message);
		}
	}

	SymbolTable symtab = new SymbolTable();
	
	public static void BCcombination(BinaryChain BC) throws TypeCheckException{
		//TypeName bc = BC.getTypename();
		TypeName c0 = BC.getE0().getTypename();
		TypeName bc1 = BC.getE1().getTypename();
		Kind op = BC.getArrow().kind;
		if(c0.equals(TypeName.URL)&&op==Kind.ARROW&&bc1.equals(TypeName.IMAGE)){
			BC.setTypename(TypeName.IMAGE);
			return ;
		}
		if(c0.equals(TypeName.FILE)&&op==Kind.ARROW&&bc1.equals(TypeName.IMAGE)){
			BC.setTypename(TypeName.IMAGE);
			return ;
		}
		if(c0.equals(TypeName.FRAME)
				&&op==Kind.ARROW&&BC.getE1() instanceof FrameOpChain
				&&BC.getE1().getFirstToken().isKind(new Kind[]{Kind.KW_XLOC, Kind.KW_YLOC})){
			BC.setTypename(TypeName.INTEGER);
			return ;
		}
		if(c0.equals(TypeName.FRAME)
				&&op==Kind.ARROW&&BC.getE1() instanceof FrameOpChain
				&&BC.getE1().getFirstToken().isKind(new Kind[]{Kind.KW_SHOW, Kind.KW_HIDE, Kind.KW_MOVE})){
			BC.setTypename(TypeName.FRAME);
			return ;
		}
		if(c0.equals(TypeName.IMAGE)
				&&op==Kind.ARROW&&BC.getE1() instanceof ImageOpChain
				&&BC.getE1().getFirstToken().isKind(new Kind[]{Kind.OP_WIDTH, Kind.OP_HEIGHT})){
			BC.setTypename(TypeName.INTEGER);
			return ;
		}
		if(c0.equals(TypeName.IMAGE)&&bc1.equals(TypeName.FRAME)&&op==Kind.ARROW){
			BC.setTypename(TypeName.FRAME);
			return ;
		}
		if(c0.equals(TypeName.IMAGE)&&bc1.equals(TypeName.FILE)&&op==Kind.ARROW){
			BC.setTypename(TypeName.NONE);
			return ;
		}
		if(c0.equals(TypeName.IMAGE)
				&&(op==Kind.ARROW||op==Kind.BARARROW)
				&&BC.getE1() instanceof FilterOpChain
				&&BC.getE1().getFirstToken().isKind(new Kind[]{Kind.OP_BLUR, Kind.OP_GRAY, Kind.OP_CONVOLVE})){
			BC.setTypename(TypeName.IMAGE);
			return ;
		}
		if(c0.equals(TypeName.IMAGE)
				&&op==Kind.ARROW
				&&BC.getE1() instanceof ImageOpChain
				&&BC.getE1().getFirstToken().isKind(Kind.KW_SCALE)){
			BC.setTypename(TypeName.IMAGE);
			return ;
		}
		if(c0.equals(TypeName.IMAGE)
				&&op==Kind.ARROW
				&&BC.getE1() instanceof IdentChain&&bc1.equals(TypeName.IMAGE)){
			BC.setTypename(TypeName.IMAGE);
			return ;
		}
		if(c0.equals(TypeName.INTEGER)
				&&op==Kind.ARROW
				&&BC.getE1() instanceof IdentChain&&bc1.equals(TypeName.INTEGER)){
			BC.setTypename(TypeName.INTEGER);
			return ;
		}
		if(c0.equals(TypeName.IMAGE)
				&&op==Kind.ARROW
				&&BC.getE1() instanceof IdentChain){
			BC.setTypename(TypeName.IMAGE);
			return ;
		}
		throw new TypeCheckVisitor.TypeCheckException("unexpected chain combination");
	}
	@Override
	public Object visitBinaryChain(BinaryChain binaryChain, Object arg) throws Exception {
		// TODO Auto-generated method stub
		ChainElem ce = binaryChain.getE1();
		Chain c = binaryChain.getE0();
		ce.visit(this, arg);
		c.visit(this, arg);
		BCcombination(binaryChain);
		//System.out.println(binaryChain.getTypename());
		return null;
	}
	public static void BEcombination(BinaryExpression BE) throws TypeCheckException{
		//TypeName be = BE.getTypename();
		TypeName e0 = BE.getE0().getTypename();
		TypeName e1 = BE.getE1().getTypename();
		Token op = BE.getOp();
		if(e0.equals(TypeName.INTEGER)
				&&op.isKind(new Kind[]{MINUS, PLUS})
				&&e1.equals(TypeName.INTEGER)){
				BE.setTypename(TypeName.INTEGER);
				return ;
		}
		if(e0.equals(TypeName.IMAGE)
				&&op.isKind(new Kind[]{MINUS, PLUS})
				&&e1.equals(TypeName.IMAGE)){
				BE.setTypename(TypeName.IMAGE);
				return ;
		}
		if(e0.equals(TypeName.INTEGER)
				&&op.isKind(new Kind[]{TIMES, DIV, MOD})
				&&e1.equals(TypeName.INTEGER)){
				BE.setTypename(TypeName.INTEGER);
				return ;
		}
		if(e0.equals(TypeName.INTEGER)
				&&op.isKind(new Kind[]{TIMES})
				&&e1.equals(TypeName.IMAGE)){
				BE.setTypename(TypeName.IMAGE);
				return ;
		}
		if(e0.equals(TypeName.IMAGE)
				&&op.isKind(new Kind[]{TIMES, DIV, MOD})
				&&e1.equals(TypeName.INTEGER)){
				BE.setTypename(TypeName.IMAGE);
				return ;
		}
		if(e0.equals(TypeName.INTEGER)
				&&op.isKind(new Kind[]{LT, LE, GT, GE})
				&&e1.equals(TypeName.INTEGER)){
				BE.setTypename(TypeName.BOOLEAN);
				return ;
		}
		if(e0.equals(TypeName.BOOLEAN)
				&&op.isKind(new Kind[]{LT, LE, GT, GE, AND, OR})
				&&e1.equals(TypeName.BOOLEAN)){
				BE.setTypename(TypeName.BOOLEAN);
				return ;
		}
		if(e0.equals(e1)&&op.isKind(new Kind[]{EQUAL, NOTEQUAL})){
				BE.setTypename(TypeName.BOOLEAN);
				return ;
		}
		throw new TypeCheckVisitor.TypeCheckException(
				"illegal combination e0: "+e0+", e1: "+e1+", op: "+op.kind);
	}
	@Override
	public Object visitBinaryExpression(BinaryExpression binaryExpression, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Expression ex1 = binaryExpression.getE0();
		Expression ex2 = binaryExpression.getE1();
		ex1.visit(this, null);
		ex2.visit(this, null);
		BEcombination(binaryExpression);
		//binaryExpression.visit(, arg);
		return null;
	}

	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		// TODO Auto-generated method stub
		symtab.enterScope();
		//visit
		ArrayList<Dec> dec =  block.getDecs();
		ArrayList<Statement> stat = block.getStatements();
		for(int i=0;i<dec.size();i++)
			dec.get(i).visit(this, null);
		for(int j=0;j<stat.size();j++)
			stat.get(j).visit(this, null);
		symtab.leaveScope();
		return null;
	}

	@Override
	public Object visitBooleanLitExpression(BooleanLitExpression booleanLitExpression, Object arg) throws Exception {
		// TODO Auto-generated method stub
		booleanLitExpression.setTypename(TypeName.BOOLEAN);
		return null;
	}

	@Override
	public Object visitFilterOpChain(FilterOpChain filterOpChain, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Tuple tp = filterOpChain.getArg();
		tp.visit(this, null);
		if(tp.getExprList().size()!=0)
			throw new TypeCheckVisitor.TypeCheckException("have unexpected expressions");
		filterOpChain.setTypename(TypeName.IMAGE);
		return null;
	}

	@Override
	public Object visitFrameOpChain(FrameOpChain frameOpChain, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Token op = frameOpChain.getFirstToken();
		Tuple tp = frameOpChain.getArg();
		tp.visit(this, null);
		if(op.isKind(new Kind[]{KW_SHOW, KW_HIDE})){
			if(tp.getExprList().size()!=0)
				throw new TypeCheckVisitor.TypeCheckException("have unexpected expressions");
			frameOpChain.setTypename(TypeName.NONE);
		}
		else if(op.isKind(new Kind[]{KW_XLOC, KW_YLOC})){
			if(tp.getExprList().size()!=0)
				throw new TypeCheckVisitor.TypeCheckException("have unexpected expressions");
			frameOpChain.setTypename(TypeName.INTEGER);
		}
		else if(op.isKind(new Kind[]{KW_MOVE})){
			if(tp.getExprList().size()!=2)
				throw new TypeCheckVisitor.TypeCheckException("have unexpected expressions");
			frameOpChain.setTypename(TypeName.NONE);
		}
		else
			throw new TypeCheckVisitor.TypeCheckException("bug");
		return null;
	}

	@Override
	public Object visitIdentChain(IdentChain identChain, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Token temp1 = identChain.getFirstToken();
		//System.out.println(symtab.table.containsKey(temp1.getText()));
		if(symtab.lookup(temp1.getText())==null)
			throw new TypeCheckVisitor.TypeCheckException("can not find this dec");
		TypeName identtype = Type.getTypeName(symtab.lookup(temp1.getText()).getType());
		identChain.setTypename(identtype);
		identChain.setDec(symtab.lookup(temp1.getText()));
		return null;
	}

	@Override
	public Object visitIdentExpression(IdentExpression identExpression, Object arg) throws Exception {
		// TODO Auto-generated method stub
		String temp = identExpression.getFirstToken().getText();
		if(symtab.lookup(temp)==null)
			throw new TypeCheckVisitor.TypeCheckException("can not find this dec");
		TypeName identtype = Type.getTypeName(symtab.lookup(temp).getType());
		identExpression.setTypename(identtype);
		identExpression.setDec(symtab.lookup(temp));
		return null;
	}

	@Override
	public Object visitIfStatement(IfStatement ifStatement, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Expression ex = ifStatement.getE();
		Block block = ifStatement.getB();
		ex.visit(this, null);
		block.visit(this, null);
		if(!ifStatement.getE().getTypename().equals(TypeName.BOOLEAN))
			throw new TypeCheckVisitor.TypeCheckException("not boolean");
		return null;
	}

	@Override
	public Object visitIntLitExpression(IntLitExpression intLitExpression, Object arg) throws Exception {
		// TODO Auto-generated method stub
		intLitExpression.setTypename(TypeName.INTEGER);
		return null;
	}

	@Override
	public Object visitSleepStatement(SleepStatement sleepStatement, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Expression ex = sleepStatement.getE();
		ex.visit(this, null);
		if(!sleepStatement.getE().getTypename().isType(TypeName.INTEGER)){
			throw new TypeCheckVisitor.TypeCheckException("not integer");
		}
		
		return null;
	}

	@Override
	public Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Expression ex = whileStatement.getE();
		Block block = whileStatement.getB();
		ex.visit(this, null);
		block.visit(this, null);
		if(!whileStatement.getE().getTypename().equals(TypeName.BOOLEAN))
			throw new TypeCheckVisitor.TypeCheckException("not boolean");
		
		return null;
	}

	@Override
	public Object visitDec(Dec declaration, Object arg) throws Exception {
		// TODO Auto-generated method stub
		if(!symtab.insert(declaration.getIdent().getText(), declaration)){
			throw new TypeCheckVisitor.TypeCheckException(declaration.getIdent().getText()+" ident already declared");
		}
		declaration.setTypename(Type.getTypeName(declaration.getType()));
		return null;
	}

	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		// TODO Auto-generated method stub
		//System.out.println(program.getName());
		symtab.enterScope();
		ArrayList<ParamDec> list = program.getParams();
		Block block = program.getB();
		for(int i=0;i<list.size();i++){
			ParamDec parad = list.get(i);
			parad.visit(this, null);
		}
		block.visit(this, null);
		symtab.leaveScope();
		return null;
	}

	@Override
	public Object visitAssignmentStatement(AssignmentStatement assignStatement, Object arg) throws Exception {
		// TODO Auto-generated method stub
		IdentLValue identval = assignStatement.getVar();
		Expression expr = assignStatement.getE();
		expr.visit(this, null);
		identval.visit(this, null);
		//System.out.println();
		if(!Type.getTypeName(identval.getDec().getType()).equals(expr.getTypename()))
			throw new TypeCheckVisitor.TypeCheckException(identval.getDec().getTypename()+ " not equal with " + expr.getTypename() );
		
		return null;
	}

	@Override
	public Object visitIdentLValue(IdentLValue identX, Object arg) throws Exception {
		// TODO Auto-generated method stub
		if(symtab.lookup(identX.getFirstToken().getText())!=null){
			Dec dec = symtab.lookup(identX.getFirstToken().getText());
			identX.setDec(dec);
			return null;
		}
		throw new TypeCheckVisitor.TypeCheckException(identX.getFirstToken().getText()+" can not find dec");
		
	}

	@Override
	public Object visitParamDec(ParamDec paramDec, Object arg) throws Exception {
		// TODO Auto-generated method stub
		//System.out.println(paramDec.getIdent().getText());
		if(!symtab.insert(paramDec.getIdent().getText(), paramDec)){
			throw new TypeCheckVisitor.TypeCheckException("can not insert");
		}
		paramDec.setTypename(Type.getTypeName(paramDec.getType()));
		return null;
	}

	@Override
	public Object visitConstantExpression(ConstantExpression constantExpression, Object arg) {
		// TODO Auto-generated method stub
		constantExpression.setTypename(TypeName.INTEGER);
		return null;
	}

	@Override
	public Object visitImageOpChain(ImageOpChain imageOpChain, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Token op = imageOpChain.getFirstToken();
		Tuple tp = imageOpChain.getArg();
		//System.out.println(tp);
		tp.visit(this, null);
		if(op.isKind(new Kind[]{OP_WIDTH, OP_HEIGHT})){
			if(tp.getExprList().size()!=0)
				throw new TypeCheckVisitor.TypeCheckException("have unexpected expressions");
			imageOpChain.setTypename(TypeName.INTEGER);
		}
		else if(op.isKind(new Kind[]{KW_SCALE})){
			if(tp.getExprList().size()!=1)
				throw new TypeCheckVisitor.TypeCheckException("have unexpected expressions");
			imageOpChain.setTypename(TypeName.IMAGE);
		}
		
		return null;
	}

	@Override
	public Object visitTuple(Tuple tuple, Object arg) throws Exception {
		// TODO Auto-generated method stub
		for(int i=0;i<tuple.getExprList().size();i++){
			Expression expr = tuple.getExprList().get(i);
			expr.visit(this, null);
			if(!expr.getTypename().equals(TypeName.INTEGER)){
				throw new TypeCheckVisitor.TypeCheckException("not integer");
			}	
		}
		return null;
	}

	/*public static void main(String[] args) throws Exception{
		String input = "";
		//System.out.println(input);
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
		System.out.println(v.symtab.scope.size());
	}*/

}
