package org.maziarz.yiiclipse.codegen;


import org.eclipse.php.core.ast.nodes.AST;
import org.eclipse.php.core.ast.nodes.Expression;
import org.eclipse.php.core.ast.nodes.Scalar;

public class ScalarArrayMapper extends ObjectArrayMapper {

	public ScalarArrayMapper(AST container) {
		super(container);
	}
	
	@Override
	public Expression convert(Object o) {
		if (o instanceof String) {
			return convert((String)o);
		}	
		if (o instanceof Integer) {
			return convert((Integer)o);
		}	
		throw new IllegalArgumentException(o.getClass()+" class is not supported yet");
	}

	private Expression createScalar(String value, int type) {
		Scalar scalarValue = new Scalar(container);
		scalarValue.setScalarType(type);
		scalarValue.setStringValue(value);
		return scalarValue;
	}	
	
	public Expression convert(String value) {
		return createScalar("'"+value+"'", Scalar.TYPE_STRING);
	}

	public Expression convert(Integer value){
		return createScalar(value.toString(), Scalar.TYPE_INT);
	}
	
}
