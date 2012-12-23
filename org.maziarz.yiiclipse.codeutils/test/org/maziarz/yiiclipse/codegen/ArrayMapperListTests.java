package org.maziarz.yiiclipse.codegen;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.eclipse.php.internal.core.ast.nodes.ArrayCreation;
import org.eclipse.php.internal.core.ast.nodes.ArrayElement;
import org.eclipse.php.internal.core.ast.nodes.Expression;
import org.eclipse.php.internal.core.ast.nodes.Program;

public class ArrayMapperListTests extends BaseArrayMapperTest{

	@Test
	public void testStringList(){
		PhpCodeGenerator generator = new PhpCodeGenerator();
		Program program = generator.getProgram();
		
		ArrayMapper am = new ArrayMapper(program.getAST());
		
		String[] value = new String[] {"raz", "dwa"};
		
		List<String> l = new LinkedList<String>();
		l.addAll(Arrays.asList(value));
		
		Expression expr = am.convert(l);
		
		addToProgram(program, expr);
		Assert.assertEquals("array('raz','dwa',);", generator.emit().trim());
	}
	
	@Test
	public void testNestedLists(){
		PhpCodeGenerator generator = new PhpCodeGenerator();
		Program program = generator.getProgram();
		
		ArrayMapper am = new ArrayMapper(program.getAST());
		
		String[] element = new String[]{"A","B"};
		
		Object[] value = new Object[] {"raz", "dwa", Arrays.asList(element)};
		List<Object> l = new LinkedList<Object>();
		l.addAll(Arrays.asList(value));
		
		Expression expr = am.convert(l);
		
		addToProgram(program, expr);
		Assert.assertEquals("array('raz','dwa',array('A','B',),);", generator.emit().trim());
	}

}