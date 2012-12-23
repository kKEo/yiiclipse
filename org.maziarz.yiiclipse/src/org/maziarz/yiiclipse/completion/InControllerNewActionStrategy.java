package org.maziarz.yiiclipse.completion;

import org.eclipse.dltk.core.CompletionRequestor;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IModelElementVisitor;
import org.eclipse.dltk.internal.core.SourceMethod;
import org.eclipse.dltk.internal.core.SourceRange;
import org.eclipse.php.core.codeassist.ICompletionContext;
import org.eclipse.php.internal.core.PHPVersion;
import org.eclipse.php.internal.core.ast.nodes.ASTNode;
import org.eclipse.php.internal.core.ast.nodes.ASTParser;
import org.eclipse.php.internal.core.ast.nodes.Program;
import org.eclipse.php.internal.core.codeassist.ICompletionReporter;
import org.eclipse.php.internal.core.codeassist.strategies.AbstractCompletionStrategy;
import org.eclipse.php.internal.core.typeinference.FakeMethod;

public class InControllerNewActionStrategy extends AbstractCompletionStrategy {

	public InControllerNewActionStrategy(ICompletionContext context) {
		super(context);
	}

	@Override
	public void apply(ICompletionReporter reporter) throws Exception {

		InControllerContext ctx = null;
		if (getContext() instanceof InControllerContext) {
			ctx = (InControllerContext) getContext();
		}

		final String prefix = ctx.getPrefix();

		CompletionRequestor requstor = ctx.getCompletionRequestor();

		ctx.getSourceModule().getElementAt(ctx.getOffset());

		/*-
		ASTParser parser = ASTParser.newParser(PHPVersion.PHP5_4, ctx.getSourceModule());
		Program p = parser.createAST(null);
		ASTNode node = p.getElementAt(ctx.getOffset());
		 */

		IMethod method = ctx.getSourceModule().getMethod(prefix);
		SourceMethod sm = (SourceMethod) method;

		ctx.getOffset();

		SourceRange replacementRange = this.getReplacementRange(ctx);
		
		int offset = replacementRange.getOffset();
		int length = replacementRange.getLength();
		
		SourceRange r = new SourceRange(offset - prefix.length(), length+prefix.length());
		
//		reporter.reportKeyword("Create "+prefix+" method", "(){$this->render('" + prefix + "', array());}", r);

		//reporter.reportMethod(method, "(){$this->render('" + prefix + "');\n}", replacementRange, new Object());

	}

}
