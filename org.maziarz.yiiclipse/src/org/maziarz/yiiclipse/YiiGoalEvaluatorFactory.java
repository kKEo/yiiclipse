package org.maziarz.yiiclipse;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.references.SimpleReference;
import org.eclipse.dltk.ast.references.VariableReference;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.ti.GoalState;
import org.eclipse.dltk.ti.IGoalEvaluatorFactory;
import org.eclipse.dltk.ti.goals.ExpressionTypeGoal;
import org.eclipse.dltk.ti.goals.GoalEvaluator;
import org.eclipse.dltk.ti.goals.IGoal;
import org.eclipse.php.internal.core.compiler.ast.nodes.StaticMethodInvocation;
import org.eclipse.php.internal.core.typeinference.PHPClassType;
import org.eclipse.php.internal.core.typeinference.context.FileContext;
import org.eclipse.php.internal.core.typeinference.context.MethodContext;
import org.eclipse.php.internal.core.typeinference.goals.phpdoc.PHPDocMethodReturnTypeGoal;
import org.maziarz.yiiclipse.hyperlinks.WorkspacePathHelper;
import org.maziarz.yiiclipse.utils.YiiPathResolver;

public class YiiGoalEvaluatorFactory implements IGoalEvaluatorFactory {

	private IPath viewPath;
	
	@Override
	public GoalEvaluator createEvaluator(IGoal goal) {

		if (goal.getClass() == ExpressionTypeGoal.class){
			
			ExpressionTypeGoal exprGoal = ((ExpressionTypeGoal) goal);
			ASTNode expression = exprGoal.getExpression();
			
			if (exprGoal.getContext() instanceof FileContext && expression instanceof VariableReference && "$this".equals(((VariableReference)expression).getName())){
				FileContext ctx = (FileContext)exprGoal.getContext();
				
				//retrieve view path
				if (viewPath == null) {
					YiiPathResolver resolver = new YiiPathResolver(new WorkspacePathHelper());
					viewPath = resolver.resolveAliasPath("application.views", ctx.getSourceModule());
					viewPath = viewPath.makeRelativeTo(ResourcesPlugin.getWorkspace().getRoot().getLocation());
				}
				
				if (viewPath.isPrefixOf(ctx.getSourceModule().getPath())){
					return new YiiAppGoalEvaluator(goal, "CController");
				}
			}
			
			if (expression instanceof StaticMethodInvocation) {
				StaticMethodInvocation inv = (StaticMethodInvocation) expression;
				ASTNode reciever = inv.getReceiver();

				if (reciever instanceof SimpleReference && "Yii".equals(((SimpleReference) reciever).getName())) {
					if ("app".equals(inv.getCallName().getName())) {
						return new YiiAppGoalEvaluator(goal, "CWebApplication");
					}
				}
			}
			
		} else
		if (goal.getClass() == PHPDocMethodReturnTypeGoal.class){
			PHPDocMethodReturnTypeGoal docGoal = ((PHPDocMethodReturnTypeGoal) goal);
			
			if (docGoal != null && docGoal.getTypes() != null) {
				if (docGoal.getTypes().length > 0){
					IType type = docGoal.getTypes()[0];
					if("Yii".equals(type.getElementName())){
						if ("app".equals(docGoal.getMethodName())){
							return new YiiAppGoalEvaluator(goal, "CWebApplication");
						}
					}
				}
			}
			
		}

		return null;
	}

	class YiiAppGoalEvaluator extends GoalEvaluator {
		private String className;

		public YiiAppGoalEvaluator(IGoal goal, String className) {
			super(goal);
			this.className = className;
		}

		public Object produceResult() {
			return new PHPClassType(className);
		}

		public IGoal[] init() {
			return null;
		}

		public IGoal[] subGoalDone(IGoal subgoal, Object result, GoalState state) {
			return null;
		}

	}

}
