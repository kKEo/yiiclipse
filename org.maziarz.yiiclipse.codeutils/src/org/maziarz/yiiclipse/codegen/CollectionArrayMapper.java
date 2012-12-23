package org.maziarz.yiiclipse.codegen;


import java.util.Collection;

import org.eclipse.php.internal.core.ast.nodes.AST;
import org.eclipse.php.internal.core.ast.nodes.ArrayCreation;
import org.eclipse.php.internal.core.ast.nodes.ArrayElement;
import org.eclipse.php.internal.core.ast.nodes.Expression;

public class CollectionArrayMapper extends ObjectArrayMapper {

	public CollectionArrayMapper(AST container) {
		super(container);
	}

	@Override
	public Expression convert(Object o) {
		if (o instanceof Collection) {
			return convert((Collection)o);
		}
		throw new IllegalArgumentException(o.getClass()+" is not supported");
	}
	
	public Expression convert(Collection<Object> collection) {
		ArrayCreation ac = container.newArrayCreation();
		ArrayMapper am = new ArrayMapper(container);

		for (Object item : collection){
			Expression expr = am.convert(item);
			
			
			ArrayElement ae = new ArrayElement(container);
			ae.setValue(expr);
			ac.elements().add(ae);
		}
		
		return ac;
	}

}
