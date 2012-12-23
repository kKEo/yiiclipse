package org.maziarz.yiiclipse.codegen;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.eclipse.php.internal.core.ast.nodes.Expression;
import org.eclipse.php.internal.core.ast.nodes.Program;
import org.junit.Test;


public class ArrayMapperMapTests extends BaseArrayMapperTest{

	@Test
	public void testSimple(){
		
		PhpCodeGenerator gen = new PhpCodeGenerator();
		Program program = gen.getProgram();
		
		Map<String, Object> simpleMap = new LinkedHashMap<String, Object>();
		
		simpleMap.put("klucz1", "wartosc1");
		simpleMap.put("klucz2", "wartosc2");
		
		List<String> list = new ArrayList<String>();
		list.add("raz");
		list.add("dwy");
		list.add("trzy");
		
		simpleMap.put("lista", list);
		
		Expression expr = (new ArrayMapper(program.getAST())).convert(simpleMap);
		
		addToProgram(program, expr);
		
		String expectedCode = "array('klucz1'=>'wartosc1','klucz2'=>'wartosc2','lista'=>array('raz','dwy','trzy',),);";
		Assert.assertEquals(expectedCode, gen.emit().replace("\n", ""));
	}


	
}
