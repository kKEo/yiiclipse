package org.maziarz.yiiclipse.codegen;

import java.util.LinkedHashMap;
import java.util.Map;

import junit.framework.Assert;

import org.eclipse.php.internal.core.ast.nodes.Expression;
import org.eclipse.php.internal.core.ast.nodes.ExpressionStatement;
import org.eclipse.php.internal.core.ast.nodes.Program;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class BaseArrayMapperTest {

	protected void addToProgram(Program program, Expression expr) {
		ExpressionStatement stmt = program.getAST().newExpressionStatement(expr);
		program.statements().add(stmt);
	}
	
	protected void testMap(Map map, String expectedCode){
		PhpCodeGenerator generator = new PhpCodeGenerator();
		Program program = generator.getProgram();
		ArrayMapper am = new ArrayMapper(program.getAST());
		Expression expr = am.getArray(map);
		addToProgram(program, expr);
		Assert.assertEquals(expectedCode, generator.emit().replace("\n", ""));
	}
	
}
