package org.maziarz.yiiclipse.quickfix;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.ui.text.completion.IScriptCompletionProposal;
import org.eclipse.php.internal.core.ast.nodes.Scalar;
import org.eclipse.php.internal.ui.editor.contentassist.PHPCompletionProposal;
import org.eclipse.php.internal.ui.text.correction.IInvocationContext;
import org.eclipse.php.internal.ui.text.correction.IProblemLocation;
import org.eclipse.php.internal.ui.text.correction.IQuickFixProcessor;
import org.maziarz.yiiclipse.utils.ASTUtils;
import org.maziarz.yiiclipse.utils.StringUtils;
import org.maziarz.yiiclipse.utils.YiiPathResolver;
import org.maziarz.yiiclipse.wizards.YiiProjectWorkspaceHelper;

public class ViewQuickFixProcessor implements IQuickFixProcessor {

	@Override
	public boolean hasCorrections(ISourceModule unit, int problemId) {
		return problemId > 0;
	}

	@Override
	public IScriptCompletionProposal[] getCorrections(final IInvocationContext context, IProblemLocation[] locations)
			throws CoreException {

		int start = context.getCoveringNode().getStart();
		int end = context.getCoveringNode().getEnd();

		String view = null;
		if (context.getCoveringNode() instanceof Scalar) {
			view = ((Scalar) context.getCoveringNode()).getStringValue();
		}

		if (view == null) {
			return null;
		}
		final String viewName = StringUtils.stripQuotes(view);

		final IPath path = context.getCompilationUnit().getPath();

		final IType type = ASTUtils.getEnclosingType(context.getCoveringNode());
		if (type == null) {
			return null;
		}

		List<IScriptCompletionProposal> corrections = new ArrayList<IScriptCompletionProposal>();

		IScriptCompletionProposal proposal = new WorkspaceModificationProposal(new Runnable() {

			@Override
			public void run() {
				String resolvedPath = YiiPathResolver.resolveViewPath(//
						context.getCompilationUnit(), //
						type.getElementName(),//
						viewName);

				IPath targetContainerPath = new Path(resolvedPath).removeLastSegments(1);
				
				IResource findElement = ResourcesPlugin.getWorkspace().getRoot().findMember(targetContainerPath);
				if (findElement instanceof IContainer) {
					IContainer container = (IContainer) findElement;
					YiiProjectWorkspaceHelper.INSTANCE.createFile(container, viewName + ".php");
				} else {
					throw new RuntimeException("Resource not found: "+targetContainerPath);
				}
			}
		});

		corrections.add(proposal);

		// proposal =new CreateViewProposal("", 0, 100, null, "Create view file", 100);

		return corrections.toArray(new IScriptCompletionProposal[corrections.size()]);
	}

}
