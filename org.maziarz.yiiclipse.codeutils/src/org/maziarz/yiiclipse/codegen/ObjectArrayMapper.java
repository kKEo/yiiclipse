package org.maziarz.yiiclipse.codegen;


import org.eclipse.php.core.ast.nodes.AST;
import org.eclipse.php.core.ast.nodes.Expression;

public abstract class ObjectArrayMapper {
	
	protected AST container;
	
	public ObjectArrayMapper(AST container) {
		this.container = container;
	}
	
	public Expression convert(Object o){
		throw new IllegalAccessError("Convert method is not implemented for "+o.getClass().getCanonicalName()+" class.");
	};
}
