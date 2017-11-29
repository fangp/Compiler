package cop5556sp17;

import java.util.ArrayList;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import cop5556sp17.Scanner.Kind;
import cop5556sp17.Scanner.Token;
import cop5556sp17.AST.ASTVisitor;
import cop5556sp17.AST.AssignmentStatement;
import cop5556sp17.AST.BinaryChain;
import cop5556sp17.AST.BinaryExpression;
import cop5556sp17.AST.Block;
import cop5556sp17.AST.BooleanLitExpression;
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
import cop5556sp17.AST.Type.TypeName;
import cop5556sp17.AST.WhileStatement;

public class CodeGenVisitor implements ASTVisitor, Opcodes {

	/**
	 * @param DEVEL
	 *            used as parameter to genPrint and genPrintTOS
	 * @param GRADE
	 *            used as parameter to genPrint and genPrintTOS
	 * @param sourceFileName
	 *            name of source file, may be null.
	 */
	public CodeGenVisitor(boolean DEVEL, boolean GRADE, String sourceFileName) {
		super();
		this.DEVEL = DEVEL;
		this.GRADE = GRADE;
		this.sourceFileName = sourceFileName;
	}

	ClassWriter cw;
	String className;
	String classDesc;
	String sourceFileName;
	int slot = 1;
	int paramIndex = 0;

	MethodVisitor mv; // visitor of method currently under construction

	/** Indicates whether genPrint and genPrintTOS should generate code. */
	final boolean DEVEL;
	final boolean GRADE;

	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		className = program.getName();
		classDesc = "L" + className + ";";
		String sourceFileName = (String) arg;
		cw.visit(52, ACC_PUBLIC + ACC_SUPER, className, null, "java/lang/Object",
				new String[] { "java/lang/Runnable" });
		cw.visitSource(sourceFileName, null);

		// generate constructor code
		// get a MethodVisitor
		mv = cw.visitMethod(ACC_PUBLIC, "<init>", "([Ljava/lang/String;)V", null,
				null);
		mv.visitCode();
		// Create label at start of code
		Label constructorStart = new Label();
		mv.visitLabel(constructorStart);
		// this is for convenience during development--you can see that the code
		// is doing something.
		CodeGenUtils.genPrint(DEVEL, mv, "\nentering <init>");
		// generate code to call superclass constructor
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
		// visit parameter decs to add each as field to the class
		// pass in mv so decs can add their initialization code to the
		// constructor.
		ArrayList<ParamDec> params = program.getParams();
		for (ParamDec dec : params)
			dec.visit(this, mv);
		mv.visitInsn(RETURN);
		// create label at end of code
		Label constructorEnd = new Label();
		mv.visitLabel(constructorEnd);
		// finish up by visiting local vars of constructor
		// the fourth and fifth arguments are the region of code where the local
		// variable is defined as represented by the labels we inserted.
		mv.visitLocalVariable("this", classDesc, null, constructorStart, constructorEnd, 0);
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, constructorStart, constructorEnd, 1);
		// indicates the max stack size for the method.
		// because we used the COMPUTE_FRAMES parameter in the classwriter
		// constructor, asm
		// will do this for us. The parameters to visitMaxs don't matter, but
		// the method must
		// be called.
		mv.visitMaxs(1, 1);
		// finish up code generation for this method.
		mv.visitEnd();
		// end of constructor

		// create main method which does the following
		// 1. instantiate an instance of the class being generated, passing the
		// String[] with command line arguments
		// 2. invoke the run method.
		mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null,
				null);
		mv.visitCode();
		Label mainStart = new Label();
		mv.visitLabel(mainStart);
		// this is for convenience during development--you can see that the code
		// is doing something.
		CodeGenUtils.genPrint(DEVEL, mv, "\nentering main");
		mv.visitTypeInsn(NEW, className);
		mv.visitInsn(DUP);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, className, "<init>", "([Ljava/lang/String;)V", false);
		mv.visitMethodInsn(INVOKEVIRTUAL, className, "run", "()V", false);
		mv.visitInsn(RETURN);
		Label mainEnd = new Label();
		mv.visitLabel(mainEnd);
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, mainStart, mainEnd, 0);
		mv.visitLocalVariable("instance", classDesc, null, mainStart, mainEnd, 1);
		mv.visitMaxs(0, 0);
		mv.visitEnd();

		// create run method
		mv = cw.visitMethod(ACC_PUBLIC, "run", "()V", null, null);
		mv.visitCode();
		Label startRun = new Label();
		mv.visitLabel(startRun);
		CodeGenUtils.genPrint(DEVEL, mv, "\nentering run");
		program.getB().visit(this, null);
		mv.visitInsn(RETURN);
		Label endRun = new Label();
		mv.visitLabel(endRun);
		mv.visitLocalVariable("this", classDesc, null, startRun, endRun, 0);
//TODO  visit the local variables
		for(Dec d: program.getB().getDecs()){
			String DecName = d.getIdent().getText();
			String DecJVMName = d.getTypename().getJVMTypeDesc();
			mv.visitLocalVariable(DecName, DecJVMName, null, startRun, endRun, d.getSlot());
		}
		
		
		mv.visitMaxs(1, 1);
		mv.visitEnd(); // end of run method
		
		
		cw.visitEnd();//end of class
		
		//generate classfile and return it
		return cw.toByteArray();
	}



	@Override
	public Object visitAssignmentStatement(AssignmentStatement assignStatement, Object arg) throws Exception {
		mv.visitVarInsn(ALOAD, 0);
		assignStatement.getE().visit(this, arg);
		CodeGenUtils.genPrint(DEVEL, mv, "\nassignment: " + assignStatement.var.getText() + "=");
		CodeGenUtils.genPrintTOS(GRADE, mv, assignStatement.getE().getTypename());
		assignStatement.getVar().visit(this, arg);
		return null;
	}

	@Override
	public Object visitBinaryChain(BinaryChain binaryChain, Object arg) throws Exception {
		//assert false : "not yet implemented";
		binaryChain.getE0().visit(this, 0);
		TypeName typeName = binaryChain.getE0().getTypename();
		switch (typeName) {
		case URL:
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "readFromURL", PLPRuntimeImageIO.readFromURLSig, false);
			break;
		case FILE:
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "readFromFile", PLPRuntimeImageIO.readFromFileDesc, false);
			break;
		}
		binaryChain.getE1().visit(this, 1);
		return null;
	}

	@Override
	public Object visitBinaryExpression(BinaryExpression binaryExpression, Object arg) throws Exception {
      //TODO  Implement this
		Kind op = binaryExpression.getOp().kind;
		Expression be0 = binaryExpression.getE0();
		Expression be1 = binaryExpression.getE1();
		be0.visit(this, arg);
		be1.visit(this, arg);
		Label TRUE = new Label();
		Label FALSE = new Label();
		Label END = new Label();
		switch(op){
		case PLUS:
			if(binaryExpression.getTypename()==TypeName.INTEGER)
				mv.visitInsn(IADD);
			else{
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "add", PLPRuntimeImageOps.addSig, false);
			}
			mv.visitJumpInsn(GOTO, END);
			break;
		case MINUS:
			if(binaryExpression.getTypename()==TypeName.INTEGER)
				mv.visitInsn(ISUB);
			else
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "sub", PLPRuntimeImageOps.subSig, false);
			mv.visitJumpInsn(GOTO, END);
			break;
		case TIMES:
			if(binaryExpression.getTypename()==TypeName.INTEGER)
				mv.visitInsn(IMUL);
			else{
				if(be1.getTypename()==TypeName.IMAGE)
					mv.visitInsn(SWAP);
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "mul", PLPRuntimeImageOps.mulSig, false);
			}
				
			mv.visitJumpInsn(GOTO, END);
			break;
		case DIV:
			if(binaryExpression.getTypename()==TypeName.INTEGER)
				mv.visitInsn(IDIV);
			else
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "div", PLPRuntimeImageOps.divSig, false);
			mv.visitJumpInsn(GOTO, END);
			break;
		case MOD:
			if (binaryExpression.getTypename() == TypeName.INTEGER)
				mv.visitInsn(IREM);
			else
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "mod", PLPRuntimeImageOps.modSig, false);
			mv.visitJumpInsn(GOTO, END);
			break;
		case LT:
			mv.visitInsn(ISUB);
			mv.visitJumpInsn(IFLT, TRUE);
			mv.visitJumpInsn(GOTO, FALSE);
			break;
		case LE:
			mv.visitInsn(ISUB);
			mv.visitJumpInsn(IFLE, TRUE);
			mv.visitJumpInsn(GOTO, FALSE);
			break;
		case GE:
			mv.visitInsn(ISUB);
			mv.visitJumpInsn(IFGE, TRUE);
			mv.visitJumpInsn(GOTO, FALSE);
			break;
		case GT:
			mv.visitInsn(ISUB);
			mv.visitJumpInsn(IFGT, TRUE);
			mv.visitJumpInsn(GOTO, FALSE);
			break;
		case EQUAL:
			mv.visitInsn(ISUB);
			mv.visitJumpInsn(IFEQ, TRUE);
			mv.visitJumpInsn(GOTO, FALSE);
			break;
		case NOTEQUAL:
			mv.visitInsn(ISUB);
			mv.visitJumpInsn(IFNE, TRUE);
			mv.visitJumpInsn(GOTO, FALSE);
			break;
		case AND:
			mv.visitInsn(IAND);
			mv.visitJumpInsn(GOTO, END);
			break;
		case OR:
			mv.visitInsn(IOR);
			mv.visitJumpInsn(GOTO, END);
			break;
		}
		mv.visitLabel(TRUE);
		mv.visitInsn(ICONST_1);
		mv.visitJumpInsn(GOTO, END);
		mv.visitLabel(FALSE);
		mv.visitInsn(ICONST_0);
		mv.visitLabel(END);
		return null;
	}

	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		//TODO  Implement this
		for(Dec d:block.getDecs()){
			d.visit(this, arg);
		}
		for(Statement s:block.getStatements()){
			s.visit(this, arg);
			if(s instanceof BinaryChain)
				mv.visitInsn(POP);
		}
		return null;
	}

	@Override
	public Object visitBooleanLitExpression(BooleanLitExpression booleanLitExpression, Object arg) throws Exception {
		//TODO Implement this
		mv.visitLdcInsn(booleanLitExpression.getValue());
		
		return null;
	}

	@Override
	public Object visitConstantExpression(ConstantExpression constantExpression, Object arg) {
		//assert false : "not yet implemented";
		if(constantExpression.getFirstToken().isKind(Kind.KW_SCREENWIDTH))
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFrame.JVMClassName, "getScreenWidth",PLPRuntimeFrame.getScreenWidthSig, false);
		else if(constantExpression.getFirstToken().isKind(Kind.KW_SCREENHEIGHT))
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFrame.JVMClassName, "getScreenHeight",PLPRuntimeFrame.getScreenHeightSig, false);
		return null;
	}

	@Override
	public Object visitDec(Dec declaration, Object arg) throws Exception {
		//TODO Implement this
		declaration.setSlot(slot++);
		if (declaration.getTypename() == TypeName.FRAME){
			mv.visitInsn(ACONST_NULL);
			mv.visitVarInsn(ASTORE, slot-1);
		}
		return null;
	}

	@Override
	public Object visitFilterOpChain(FilterOpChain filterOpChain, Object arg) throws Exception {
		//assert false : "not yet implemented";
		filterOpChain.getArg().visit(this, 1);
		Kind op = filterOpChain.getFirstToken().kind;
		switch(op){
		case OP_BLUR:
			mv.visitInsn(ACONST_NULL);
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFilterOps.JVMName, "blurOp", PLPRuntimeFilterOps.opSig, false);
			break;
		case OP_GRAY:
			mv.visitInsn(DUP);
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFilterOps.JVMName, "grayOp", PLPRuntimeFilterOps.opSig, false);
			break;
		case OP_CONVOLVE:
			mv.visitInsn(ACONST_NULL);
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFilterOps.JVMName, "convolveOp", PLPRuntimeFilterOps.opSig, false);
			break;
		}
		return null;
	}

	@Override
	public Object visitFrameOpChain(FrameOpChain frameOpChain, Object arg) throws Exception {
		//assert false : "not yet implemented";
		Kind op = frameOpChain.getFirstToken().kind;
		frameOpChain.getArg().visit(this, null);
		switch(op){
		case KW_SHOW:
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, "showImage", PLPRuntimeFrame.showImageDesc, false);
			break;
		case KW_HIDE:
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, "hideImage", PLPRuntimeFrame.hideImageDesc, false);
			break;
		case KW_MOVE:
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, "moveFrame", PLPRuntimeFrame.moveFrameDesc, false);
			break;
		case KW_XLOC:
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, "getXVal", PLPRuntimeFrame.getXValDesc, false);
			break;
		case KW_YLOC:
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, "getYVal", PLPRuntimeFrame.getYValDesc, false);
			break;
		}
		return null;
	}

	@Override
	public Object visitIdentChain(IdentChain identChain, Object arg) throws Exception {
		//assert false : "not yet implemented";
		if ((int)arg == 0){
			if (identChain.getTypename()==TypeName.INTEGER||identChain.getTypename()==TypeName.BOOLEAN) {
				if (identChain.getDec().getSlot() == -1){
					mv.visitVarInsn(ALOAD, 0);
					mv.visitFieldInsn(GETFIELD, className, identChain.getDec().getIdent().getText(),identChain.getDec().getTypename().getJVMTypeDesc());
				}
				else {
					mv.visitVarInsn(ILOAD, identChain.getDec().getSlot());
				}
			}
			else{
				if (identChain.getDec().getSlot() == -1){
					mv.visitVarInsn(ALOAD, 0);
					mv.visitFieldInsn(GETFIELD, className, identChain.getDec().getIdent().getText(),identChain.getDec().getTypename().getJVMTypeDesc());
				}
				else {
					mv.visitVarInsn(ALOAD, identChain.getDec().getSlot());
				}
			}
		}
		else{
			switch (identChain.getTypename()){
			case INTEGER:
				if (identChain.getDec().getSlot() == -1){
					mv.visitVarInsn(ALOAD, 0);
					mv.visitInsn(SWAP);
					mv.visitFieldInsn(PUTFIELD, className, identChain.getDec().getIdent().getText(),identChain.getDec().getTypename().getJVMTypeDesc());
					mv.visitVarInsn(ALOAD, 0);
					mv.visitFieldInsn(GETFIELD, className, identChain.getDec().getIdent().getText(),identChain.getDec().getTypename().getJVMTypeDesc());
				}
				else{
					mv.visitVarInsn(ISTORE, identChain.getDec().getSlot());
					mv.visitVarInsn(ILOAD, identChain.getDec().getSlot());
				}
				break;
			case IMAGE:{
				mv.visitVarInsn(ASTORE, identChain.getDec().getSlot());
				mv.visitVarInsn(ALOAD, identChain.getDec().getSlot());
				
			}
			break;
			case FILE:{
				if (identChain.getDec().getSlot() == -1){
					mv.visitVarInsn(ALOAD, 0);           
					mv.visitFieldInsn(GETFIELD, className, identChain.getDec().getIdent().getText(), identChain.getTypename().getJVMTypeDesc());
					mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "write", PLPRuntimeImageIO.writeImageDesc, false);
				}
				else{
					mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "write", PLPRuntimeImageIO.writeImageDesc, false);
				}
				
			}
			break;
			case FRAME:
				mv.visitVarInsn(ALOAD, identChain.getDec().getSlot());
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFrame.JVMClassName, "createOrSetFrame", PLPRuntimeFrame.createOrSetFrameSig, false);
				mv.visitVarInsn(ASTORE, identChain.getDec().getSlot());
				mv.visitVarInsn(ALOAD, identChain.getDec().getSlot());
				break;
			}
		}
		return null;
	}

	@Override
	public Object visitIdentExpression(IdentExpression identExpression, Object arg) throws Exception {
		//TODO Implement this
		if(identExpression.getDec().getSlot()==-1){
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, 
					className, identExpression.getDec().getIdent().getText(), 
					identExpression.getDec().getTypename().getJVMTypeDesc());
		}
		else{
			if(identExpression.getDec().getTypename()==TypeName.INTEGER
					||identExpression.getDec().getTypename()==TypeName.BOOLEAN)
				mv.visitVarInsn(ILOAD, identExpression.getDec().getSlot());
			else
				mv.visitVarInsn(ALOAD, identExpression.getDec().getSlot());
		}
		return null;
	}

	@Override
	public Object visitIdentLValue(IdentLValue identX, Object arg) throws Exception {
		//TODO Implement this
		if(identX.getDec().getSlot()==-1){
			if(identX.getDec().getTypename()==TypeName.IMAGE)
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "copyImage", PLPRuntimeImageOps.copyImageSig, false);
			mv.visitFieldInsn(PUTFIELD, 
				className, identX.getDec().getIdent().getText(), 
				identX.getDec().getTypename().getJVMTypeDesc());
		}
		else{
			if(identX.getDec().getTypename()==TypeName.INTEGER||
					identX.getDec().getTypename()==TypeName.BOOLEAN){
				mv.visitVarInsn(ISTORE, identX.getDec().getSlot());
			}
			else if(identX.getDec().getTypename()==TypeName.IMAGE){
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "copyImage", PLPRuntimeImageOps.copyImageSig, false);
				mv.visitVarInsn(ASTORE, identX.getDec().getSlot());
			}
			mv.visitInsn(POP);
		}
		return null;

	}

	@Override
	public Object visitIfStatement(IfStatement ifStatement, Object arg) throws Exception {
		//TODO Implement this
		Expression e =  ifStatement.getE();
		e.visit(this, arg);
		Label AFTER = new Label();
		Label BEFORE = new Label();
		mv.visitJumpInsn(IFEQ, AFTER);
		mv.visitLabel(BEFORE);
		Block b = ifStatement.getB();
		b.visit(this, arg);
		mv.visitLabel(AFTER);
		for(Dec d: b.getDecs()){
			String DecName = d.getIdent().getText();
			String DecJVMName = d.getTypename().getJVMTypeDesc();
			mv.visitLocalVariable(DecName, DecJVMName, null, BEFORE, AFTER, d.getSlot());
		}
		
		return null;
	}

	@Override
	public Object visitImageOpChain(ImageOpChain imageOpChain, Object arg) throws Exception {
		//assert false : "not yet implemented";
		imageOpChain.getArg().visit(this, null);
		Kind op = imageOpChain.firstToken.kind;
		switch(op){
		case OP_WIDTH:
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeImageIO.BufferedImageClassName, "getWidth", PLPRuntimeImageOps.getWidthSig, false);
			break;
		case OP_HEIGHT:
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeImageIO.BufferedImageClassName, "getHeight", PLPRuntimeImageOps.getHeightSig, false);
			break;
		case KW_SCALE:
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "scale", PLPRuntimeImageOps.scaleSig, false);
			break;
		}
		return null;
	}

	@Override
	public Object visitIntLitExpression(IntLitExpression intLitExpression, Object arg) throws Exception {
		//TODO Implement this
		mv.visitLdcInsn(intLitExpression.value);
		return null;
	}


	@Override
	public Object visitParamDec(ParamDec paramDec, Object arg) throws Exception {
		//TODO Implement this
		//For assignment 5, only needs to handle integers and booleans
		FieldVisitor fv;
		paramDec.setSlot(-1);
		String DecName = paramDec.getIdent().getText();
		TypeName typename = paramDec.getTypename();
		//System.out.println(DecName);
		String DecJVMName = typename.getJVMTypeDesc();
		fv = cw.visitField(ACC_PRIVATE, DecName, DecJVMName, null, 0);
		
		
		//System.out.println(paramIndex);
		if(typename == TypeName.INTEGER){
			((MethodVisitor)arg).visitVarInsn(ALOAD, 0);
			((MethodVisitor)arg).visitVarInsn(ALOAD, 1);
			((MethodVisitor)arg).visitLdcInsn(paramIndex++);
			((MethodVisitor)arg).visitInsn(AALOAD);
			((MethodVisitor)arg).visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "parseInt", "(Ljava/lang/String;)I", false);
		}
		else if(typename == TypeName.BOOLEAN){
			((MethodVisitor)arg).visitVarInsn(ALOAD, 0);
			((MethodVisitor)arg).visitVarInsn(ALOAD, 1);
			((MethodVisitor)arg).visitLdcInsn(paramIndex++);
			((MethodVisitor)arg).visitInsn(AALOAD);
			((MethodVisitor)arg).visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "parseBoolean", "(Ljava/lang/String;)Z", false);
		}
		else if(typename == TypeName.FILE){
			((MethodVisitor)arg).visitVarInsn(ALOAD, 0);
			((MethodVisitor)arg).visitTypeInsn(NEW, "java/io/File");
			((MethodVisitor)arg).visitInsn(DUP);
			((MethodVisitor)arg).visitVarInsn(ALOAD, 1);
			((MethodVisitor)arg).visitLdcInsn(paramIndex++);
			((MethodVisitor)arg).visitInsn(AALOAD);
			((MethodVisitor)arg).visitMethodInsn(INVOKESPECIAL, "java/io/File", "<init>", "(Ljava/lang/String;)V", false);
		}
		else if(typename == TypeName.URL){
			((MethodVisitor)arg).visitVarInsn(ALOAD, 0);
			((MethodVisitor)arg).visitVarInsn(ALOAD, 1);
			((MethodVisitor)arg).visitLdcInsn(paramIndex++);
			((MethodVisitor)arg).visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "getURL", PLPRuntimeImageIO.getURLSig, false);
		}
		((MethodVisitor)arg).visitFieldInsn(PUTFIELD, className, DecName, DecJVMName);
		fv.visitEnd();
		return null;

	}

	@Override
	public Object visitSleepStatement(SleepStatement sleepStatement, Object arg) throws Exception {
		//assert false : "not yet implemented";
		sleepStatement.getE().visit(this, arg);
		mv.visitInsn(I2L);
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "sleep", "(J)V", false);
		return null;
	}

	@Override
	public Object visitTuple(Tuple tuple, Object arg) throws Exception {
		//assert false : "not yet implemented";
		for(int i=0;i<tuple.getExprList().size();i++){
			tuple.getExprList().get(i).visit(this, arg);
		}
		return null;
	}

	@Override
	public Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws Exception {
		//TODO Implement this
		int i=0;
		Label GUARD = new Label();
		Label BODY = new Label();
		Label END = new Label();
		mv.visitJumpInsn(GOTO, GUARD);
		mv.visitLabel(BODY);
		Block b = whileStatement.getB();
		b.visit(this, arg);
		mv.visitLabel(END);
		for(Dec d: b.getDecs()){
			String DecName = d.getIdent().getText();
			String DecJVMName = d.getTypename().getJVMTypeDesc();
			mv.visitLocalVariable(DecName, DecJVMName, null, BODY, END, d.getSlot());
		}
		mv.visitLabel(GUARD);
		Expression e = whileStatement.getE();
		e.visit(this, arg);
		mv.visitJumpInsn(IFNE, BODY);
		return null;
	}

}
