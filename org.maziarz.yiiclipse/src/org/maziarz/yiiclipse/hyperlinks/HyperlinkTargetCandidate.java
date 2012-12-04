package org.maziarz.yiiclipse.hyperlinks;

import org.eclipse.dltk.ast.expressions.Expression;

public class HyperlinkTargetCandidate {

	public enum HyperlinkTargetType {VIEW, LAYOUT}
	
	private Expression expression;
	private String view;
	private HyperlinkTargetType type;
	
	public HyperlinkTargetCandidate(Expression variableValue, String view, HyperlinkTargetType type){
		this.expression = variableValue;
		this.view = view;
		this.type = type;
	}
	
	public HyperlinkTargetCandidate(Expression variableValue, String view){
		this(variableValue, view, HyperlinkTargetType.VIEW);
	}
	
	public Expression getExpression() {
		return expression;
	}
	
	public String getView() {
		return view;
	}
	
	public HyperlinkTargetType getType() {
		return type;
	}
}
