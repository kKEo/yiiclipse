package org.maziarz.yiiclipse.codegen;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.eclipse.php.internal.core.ast.nodes.Expression;
import org.eclipse.php.internal.core.ast.nodes.Program;
import org.junit.Test;

public class ArrayMapperTests extends BaseArrayMapperTest{

	@Test
	public void testSimpleList(){
		PhpCodeGenerator generator = new PhpCodeGenerator();
		Program program = generator.getProgram();
		ArrayMapper am = new ArrayMapper(program.getAST());
		
		String[] values = new String[] {"raz", "dwa", "trzy"};
		
		Expression expr = am.getArray(Arrays.asList(values));
		
		addToProgram(program, expr);
		String expectedCode = "array('raz','dwa','trzy',);";
		Assert.assertEquals(expectedCode, generator.emit().replace("\n", ""));
	}
	
	@Test
	public void testSimpleMap1(){
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("app", "aa");
		map.put("app2", "ee");
		
		String expectedCode = "array('app'=>'aa','app2'=>'ee',);";
		testMap(map, expectedCode);
	}

	@Test
	public void testSimpleMap1a(){
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("app", null);
		map.put("app2", "ee");
		
		String expectedCode = "array('app','app2'=>'ee',);";
		testMap(map, expectedCode);
	}	
	
	@Test
	public void testSimpleMap2(){
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("app", "aa");
		
		Map<String, String> m2 = new LinkedHashMap<String, String>();
		m2.put("a", "b");
		m2.put("c", "d");
		
		map.put("app2", m2);
		
		String expectedCode = "array('app'=>'aa','app2'=>array('a'=>'b','c'=>'d',),);";
		testMap(map, expectedCode);
	}
	
	@Test
	public void testSimpleMap3(){
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("app", "aa");
		
		Map<String, String> m2 = new LinkedHashMap<String, String>();
		m2.put("0", null);
		m2.put("a", "b");
		m2.put("c", "d");
		
		map.put("app2", m2);
		
		String expectedCode = "array('app'=>'aa','app2'=>array('0','a'=>'b','c'=>'d',),);";
		testMap(map, expectedCode);
	}
	
	@Test
	public void testSimpleMap4(){
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("app", null);
		
		Map<String, String> m2 = new LinkedHashMap<String, String>();
		m2.put("0", null);
		m2.put("a", "b");
		m2.put("c", "d");
		
		map.put("app2", m2);
		
		String expectedCode = "array('app','app2'=>array('0','a'=>'b','c'=>'d',),);";
		testMap(map, expectedCode);
	}
	
	@Test
	public void testSimpleMap5(){
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("app", null);
		
		Map<String, Object> m2 = new LinkedHashMap<String, Object>();
		m2.put("0", null);
		m2.put("a", "b");
		
		List<String> list = Arrays.asList(new String[]{"1", "2", "3"});
		
		m2.put("c", list);
		
		map.put("app2", m2);
		
		String expectedCode = "array('app','app2'=>array('0','a'=>'b','c'=>array('1','2','3',),),);";
		testMap(map, expectedCode);
	}
	
	@Test
	public void testSimpleMap5WithIntegers(){
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("app", null);
		
		Map<String, Object> m2 = new LinkedHashMap<String, Object>();
		m2.put("0", null);
		m2.put("a", "b");
		
		List<Integer> list = Arrays.asList(new Integer[]{1, 2, 3});
		
		m2.put("c", list);
		
		map.put("app2", m2);
		
		String expectedCode = "array('app','app2'=>array('0','a'=>'b','c'=>array(1,2,3,),),);";
		testMap(map, expectedCode);
	}
	
	@Test
	public void testSimpleMap5WithMixed(){
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("app", null);
		
		Map<String, Object> m2 = new LinkedHashMap<String, Object>();
		m2.put("0", null);
		m2.put("a", "b");
		
		List<Object> list = Arrays.asList(new Object[]{1, "2", Arrays.asList(new String[]{"3"})});
		
		m2.put("c", list);
		
		map.put("app2", m2);
		
		String expectedCode = "array('app','app2'=>array('0','a'=>'b','c'=>array(1,'2',array('3',),),),);";
		testMap(map, expectedCode);
	}
	
}