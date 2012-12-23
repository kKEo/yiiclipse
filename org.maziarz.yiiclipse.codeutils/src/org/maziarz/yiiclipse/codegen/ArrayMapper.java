package org.maziarz.yiiclipse.codegen;


import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.php.internal.core.ast.nodes.AST;
import org.eclipse.php.internal.core.ast.nodes.ArrayCreation;
import org.eclipse.php.internal.core.ast.nodes.ArrayElement;
import org.eclipse.php.internal.core.ast.nodes.Expression;
import org.eclipse.php.internal.core.ast.nodes.ExpressionStatement;
import org.eclipse.php.internal.core.ast.nodes.Program;
import org.eclipse.php.internal.core.ast.nodes.Scalar;

/**
 * Translates Lists and HashMap object into relevant PHP array 
 * @author krma
 */
public class ArrayMapper {

	private AST container;
	
	public ArrayMapper(AST container) {
		this.container = container;
	}
	
	public Expression convert(Object o){
		return this.getMapper(o).convert(o);
	}
	
	public ArrayCreation getArray(Object object){
		if (object instanceof Collection || object instanceof Map) {
			Expression element = (getMapper(object)).convert(object);
			return (ArrayCreation)element;
		} 
		throw new RuntimeException("Object is not supported ("+object.getClass()+")");
	}
	
	public static String getArrayAsString(Object o){
		PhpCodeGenerator generator = new PhpCodeGenerator();
		Program program = generator.getProgram();
		ArrayMapper am = new ArrayMapper(program.getAST());
		Expression expr = am.getMapper(o).convert(o);
		ExpressionStatement stmt = program.getAST().newExpressionStatement(expr);
		program.statements().add(stmt);
		return generator.emit().replace("\n", "");
	}
	
	public ObjectArrayMapper getMapper(Object o) {
		
		if (o == null){
			return null;
		}
		
		if (o instanceof List) {
			return new CollectionArrayMapper(container);
		} else
		if (o instanceof Map) {
			return new MapArrayMapper(container);
		} else	
		if (o instanceof String) {
			return new ScalarArrayMapper(container);
		} else
		if (o instanceof Integer) {
			return new ScalarArrayMapper(container);
		}
		
		throw new RuntimeException("Unsupported object type ("+o.getClass()+")");		
		
	}
	
	public static String s(String string){
		return "'"+string+"'";
	}
	
	public static Map getMap(org.eclipse.php.internal.core.compiler.ast.nodes.ArrayCreation array){
		Map map = new LinkedHashMap(); 
		for (org.eclipse.php.internal.core.compiler.ast.nodes.ArrayElement el : array.getElements()){
			if (el.getKey() != null) {
				map.put(getStringValue(el.getKey()), getStringValue(el.getValue()));
			} else {
				map.put(getStringValue(el.getValue()), null);
			}
		}
		return map;
	}
	
	protected static Object getStringValue(org.eclipse.dltk.ast.expressions.Expression expression){
		if (expression instanceof org.eclipse.php.internal.core.compiler.ast.nodes.Scalar){
			return ((org.eclipse.php.internal.core.compiler.ast.nodes.Scalar)expression).getValue();
		} else if (expression instanceof org.eclipse.php.internal.core.compiler.ast.nodes.ArrayCreation){
			return getMap((org.eclipse.php.internal.core.compiler.ast.nodes.ArrayCreation)expression);
		}
		return "<NONE>";
	}

	
}

