package org.maziarz.yiiclipse.completion;

import org.eclipse.dltk.core.CompletionRequestor;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.dltk.core.SourceRange;
import org.eclipse.dltk.internal.core.SourceMethod;
import org.eclipse.php.core.codeassist.ICompletionContext;
import org.eclipse.php.core.codeassist.ICompletionReporter;
import org.eclipse.php.internal.core.codeassist.strategies.AbstractCompletionStrategy;

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

		ISourceRange replacementRange = this.getReplacementRange(ctx);
		
		int offset = replacementRange.getOffset();
		int length = replacementRange.getLength();
		
		SourceRange r = new SourceRange(offset - prefix.length(), length+prefix.length());
		
//		reporter.reportKeyword("Create "+prefix+" method", "(){$this->render('" + prefix + "', array());}", r);

		//reporter.reportMethod(method, "(){$this->render('" + prefix + "');\n}", replacementRange, new Object());

	}

}
