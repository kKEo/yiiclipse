package org.maziarz.yiiclipse.codegen;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.php.core.compiler.PHPFlags;
import org.eclipse.php.internal.core.ast.nodes.AST;
import org.eclipse.php.internal.core.ast.nodes.ArrayCreation;
import org.eclipse.php.internal.core.ast.nodes.Block;
import org.eclipse.php.internal.core.ast.nodes.ClassDeclaration;
import org.eclipse.php.internal.core.ast.nodes.Expression;
import org.eclipse.php.internal.core.ast.nodes.FunctionDeclaration;
import org.eclipse.php.internal.core.ast.nodes.FunctionInvocation;
import org.eclipse.php.internal.core.ast.nodes.Identifier;
import org.eclipse.php.internal.core.ast.nodes.MethodDeclaration;
import org.eclipse.php.internal.core.ast.nodes.MethodInvocation;
import org.eclipse.php.internal.core.ast.nodes.SingleFieldDeclaration;
import org.eclipse.php.internal.core.ast.nodes.VariableBase;
import org.eclipse.php.internal.core.compiler.ast.nodes.Scalar;

public class PhpAstHelper {

	/**
	 * Creates class declaration node
	 * <pre>
	 * class NewClassName extends SuperClass{
	 * }
	 * </pre>
	 * @param container - owner node
	 * @param newClassName - class name
	 * @param superClass - name of the parent type
	 * @param modifier 
	 * @return
	 */
	public static ClassDeclaration addClass(AST container, String newClassName, String superClass, int modifier){
		Block body = container.newBlock();
		ClassDeclaration classDeclaration = container.newClassDeclaration(modifier, newClassName, superClass, new ArrayList<Identifier>(), body);
		return classDeclaration;
	}
	
	public static SingleFieldDeclaration addField(Block container, String fieldName, String fieldValue){
		SingleFieldDeclaration simpleField = container.getAST().newSingleFieldDeclaration();
		simpleField.setName(container.getAST().newVariable(fieldName));
		if (fieldValue != null)
			simpleField.setValue(container.getAST().newScalar(fieldValue, Scalar.TYPE_STRING));
		
		return simpleField;
	}
	
	public static FunctionDeclaration addMethod(Block container, String newMethodName, int modifier) {
		MethodDeclaration md = container.getAST().newMethodDeclaration();
		FunctionDeclaration fd = container.getAST().newFunctionDeclaration();
		Identifier ident = container.getAST().newIdentifier();ident.setName(newMethodName);
		fd.setFunctionName(ident);
		md.setFunction(fd);
		md.setModifier(modifier);
		
		container.statements().add(md);
		
		return fd;
	}
	
	public static Block addFunctionsBodyBlock(FunctionDeclaration function) {
		function.setBody(function.getAST().newBlock());
		return function.getBody();
	}
	
	public static void addAction(Block body, String actionName){
		
		String functionName = "action".concat(actionName.substring(0,1).toUpperCase().concat(actionName.substring(1).toLowerCase()));
		
		FunctionDeclaration fd = addMethod(body, functionName, PHPFlags.AccPublic);
		
		Block funcBody = addFunctionsBodyBlock(fd);
		
		// add render statement
		VariableBase dispatcher = funcBody.getAST().newVariable("this");
		
		List<Expression> parameters = new ArrayList<Expression>();
		parameters.add(funcBody.getAST().newScalar("'"+actionName.toLowerCase()+"'"));
		parameters.add(funcBody.getAST().newScalar("null"));
		
		FunctionInvocation method = funcBody.getAST().newFunctionInvocation(funcBody.getAST().newFunctionName(funcBody.getAST().newScalar("render")), parameters);
		
		MethodInvocation mi = funcBody.getAST().newMethodInvocation(dispatcher, method);
		
		funcBody.statements().add(funcBody.getAST().newExpressionStatement(mi));
	}
	
}
