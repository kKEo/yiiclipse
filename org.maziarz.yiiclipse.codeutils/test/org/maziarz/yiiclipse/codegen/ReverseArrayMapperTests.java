package org.maziarz.yiiclipse.codegen;

import java.io.StringReader;
import java.util.Map;

import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.ast.parser.IModuleDeclaration;
import org.eclipse.php.core.PHPVersion;
import org.eclipse.php.internal.core.compiler.ast.parser.AbstractPHPSourceParser;
import org.eclipse.php.internal.core.compiler.ast.parser.PHPSourceParserFactory;
import org.junit.Test;

import junit.framework.Assert;

public class ReverseArrayMapperTests {

	@Test
	public void simpleTest1() {
		String initialContent = "<?php array('app'=>'aa', 'app2'=>'ee') ?>";
		String expected = "{'app'='aa', 'app2'='ee'}";
		testMap(initialContent, expected);
	}

	@Test
	public void simpleTest2() {
		String initialContent = "<?php array('app'=>'aa', 'app2'=>array('a'=>'b', 'c'=>'d')) ?>";
		testMap(initialContent, "{'app'='aa', 'app2'={'a'='b', 'c'='d'}}");
	}

	@Test
	public void simpleTest3() {
		String initialContent = "<?php array('app'=>'aa', 'app2'=>array('0','a'=>'b', 'c'=>'d')) ?>";
		testMap(initialContent, "{'app'='aa', 'app2'={'0'=null, 'a'='b', 'c'='d'}}");
	}

	@Test
	public void simpleTest4() {
		String initialContent = "<?php array('app', array('0','a'=>'b', 'c'=>'d')) ?>";
		testMap(initialContent, "{'app'=null, {'0'=null, 'a'='b', 'c'='d'}=null}");
	}

	@Test
	public void simpleTest5WithInteger() {
		String initialContent = "<?php array('app', array('0','a'=>1, 'c'=>2)) ?>";
		testMap(initialContent, "{'app'=null, {'0'=null, 'a'=1, 'c'=2}=null}");
	}	
	
	private void testMap(String initialContent, String expected) {
		AbstractPHPSourceParser parser = PHPSourceParserFactory.createParser(PHPVersion.PHP5_3);
		IModuleDeclaration m = null;
		try {
			m = parser.parse(new StringReader(initialContent), null, true, false);
		} catch (Exception e) {
		}
		if (m instanceof ModuleDeclaration) {
			ModuleDeclaration md = (ModuleDeclaration) m;
			Object array = ((org.eclipse.php.core.compiler.ast.nodes.ExpressionStatement) md.getStatements().get(0)).getExpr();
			Map map = ArrayMapper.getMap((org.eclipse.php.core.compiler.ast.nodes.ArrayCreation) array);
			Assert.assertEquals(expected, map.toString());
		}
	}



}