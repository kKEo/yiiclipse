package org.maziarz.yiiclipse.codegen;

import org.eclipse.php.core.ast.nodes.Expression;
import org.eclipse.php.core.ast.nodes.Program;
import org.junit.Test;

import junit.framework.Assert;

public class ArrayMapperScalarTests extends BaseArrayMapperTest{

	@Test
	public void testString(){
		PhpCodeGenerator generator = new PhpCodeGenerator();
		Program program = generator.getProgram();
		
		ArrayMapper am = new ArrayMapper(program.getAST());
		
		String value = "raz";
		
		ObjectArrayMapper sam = am.getMapper(value);
		Expression expr = sam.convert(value);
		
		addToProgram(program, expr);
		Assert.assertEquals("'raz';", generator.emit().trim());
	}

	@Test
	public void testInteger(){
		PhpCodeGenerator generator = new PhpCodeGenerator();
		Program program = generator.getProgram();
		
		ArrayMapper am = new ArrayMapper(program.getAST());
		
		Integer value = 1;
		
		ObjectArrayMapper sam = am.getMapper(value);
		Expression expr = sam.convert(value);
		
		addToProgram(program, expr);
		Assert.assertEquals("1;", generator.emit().trim());
	}
	
}