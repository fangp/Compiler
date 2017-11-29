
package cop5556sp17;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp17.AST.ASTNode;
import cop5556sp17.AST.Program;

public class CodeGenVisitorTest {

	static final boolean doPrint = true;
	static void show(Object s) {
		if (doPrint) {
			System.out.println(s);
		}
	}

	boolean devel = false;
	boolean grade = true;
	@Before
	public void initLog(){
	if (devel || grade) PLPRuntimeLog.initLog();
	}
	@After
	public void printLog(){
	System.out.println(PLPRuntimeLog.getString());
	}
	@Test
	public void emptyProg() throws Exception {
		//scan, parse, and type check the program
		String progname = "compProg2";
		String input = progname + " integer x, integer y, integer z, boolean bool_1, boolean bool_2 { \nx <- 100; \ny <- x / 3 * 2; \nz <- y; \nbool_1 <- false; \nbool_2 <- true; \ninteger y \ny <- z + 20; \nz <- y; \nif(bool_2){ \nboolean bool_1 \nbool_1 <- bool_2; \n} \nif(bool_1) { \ninteger err \nerr <- 2333; \n} \ninteger pass_token \npass_token <- 0; \nwhile(pass_token != 4) { \ninteger local_1 \ninteger local_2 \nlocal_1 <- 45; \nlocal_2 <- 46; \nif(local_1 != local_2) {pass_token <- pass_token + 1;} \nif(local_1 == local_2) {pass_token <- pass_token + 1;} \nif(local_1 > local_2) {pass_token <- pass_token + 1;} \nif(local_1 >= 45) {pass_token <- pass_token + 1;} \nif(local_1 < local_2) {pass_token <- pass_token + 1;} \nif(46 <= local_2) {pass_token <- pass_token + 1;} \nif((local_1 > local_2)) {pass_token <- pass_token + 1;} \n} \n} ";		
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
		show(program);
		
		//generate code
		CodeGenVisitor cv = new CodeGenVisitor(devel,grade,null);
		byte[] bytecode = (byte[]) program.visit(cv, null);
		
		//output the generated bytecode
		CodeGenUtils.dumpBytecode(bytecode);
		
		//write byte code to file 
		String name = ((Program) program).getName();
		String classFileName = "bin/" + name + ".class";
		OutputStream output = new FileOutputStream(classFileName);
		output.write(bytecode);
		output.close();
		System.out.println("wrote classfile to " + classFileName);
		
		// directly execute bytecode
		String[] args = new String[5]; //create command line argument array to initialize params, none in this case
		args[0] = "0";
		args[1] = "0";
		args[2] = "0";
		args[3] = "false";
		args[4] = "false";
		Runnable instance = CodeGenUtils.getInstance(name, bytecode, args);
		instance.run();
	}


}
