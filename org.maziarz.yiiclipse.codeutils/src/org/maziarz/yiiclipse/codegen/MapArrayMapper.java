package org.maziarz.yiiclipse.codegen;


import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.php.internal.core.ast.nodes.AST;
import org.eclipse.php.internal.core.ast.nodes.ASTNode;
import org.eclipse.php.internal.core.ast.nodes.ArrayCreation;
import org.eclipse.php.internal.core.ast.nodes.ArrayElement;
import org.eclipse.php.internal.core.ast.nodes.Expression;

public class MapArrayMapper extends ObjectArrayMapper {

	public MapArrayMapper(AST container) {
		super(container);
	}

	@Override
	public Expression convert(Object o) {
		if (o instanceof Map) {
			return convert((Map)o);
		} 
		throw new IllegalArgumentException(o.getClass()+" is not supported.");
	}
	
	public Expression convert(Map map) {
		ArrayCreation ac = container.newArrayCreation();
		
		ArrayMapper am = new ArrayMapper(container);
		
		for (Object o : map.entrySet()){
			
			Entry entry = (Entry)o;
			
			Expression key = null;
			if (entry.getKey() instanceof String) {
				key = am.convert(entry.getKey());
			} else {
				throw new IllegalArgumentException("Keys of "+entry.getKey().getClass()+" type are not supported.");
			}
			
			Expression value = null;
			if (entry.getValue() != null) {
				value = am.convert(entry.getValue());
			}
			
			ArrayElement ae = new ArrayElement(container);
			if (value != null) {
				ae.setKey(key);
				ae.setValue(value);
			} else {
				ae.setValue(key);
			}
			
			ac.elements().add(ae);
		}
		
		return ac;
	}


}
