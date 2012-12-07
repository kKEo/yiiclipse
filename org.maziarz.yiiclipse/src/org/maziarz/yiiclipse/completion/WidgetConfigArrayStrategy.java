package org.maziarz.yiiclipse.completion;

import java.io.File;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.dltk.core.CompletionRequestor;
import org.eclipse.dltk.core.IField;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.internal.core.SourceRange;
import org.eclipse.php.core.codeassist.ICompletionContext;
import org.eclipse.php.core.codeassist.ICompletionStrategy;
import org.eclipse.php.core.compiler.PHPFlags;
import org.eclipse.php.internal.core.codeassist.ICompletionReporter;
import org.eclipse.php.internal.core.codeassist.strategies.AbstractCompletionStrategy;
import org.maziarz.yiiclipse.hyperlinks.WorkspacePathHelper;
import org.maziarz.yiiclipse.utils.ASTUtils;
import org.maziarz.yiiclipse.utils.YiiPathResolver;

public class WidgetConfigArrayStrategy extends AbstractCompletionStrategy implements ICompletionStrategy {

	private YiiPathResolver pathResolver;
	private WorkspacePathHelper pathHelper;
	
	public WidgetConfigArrayStrategy(ICompletionContext context) {
		super(context);
	}

	@Override
	public WidgetConfigArrayContext getContext() {
		ICompletionContext context = super.getContext();
		if (context instanceof WidgetConfigArrayContext) {
			return (WidgetConfigArrayContext) context;
		}
		return null;
	}

	@Override
	public void apply(ICompletionReporter reporter) throws Exception {

		WidgetConfigArrayContext ctx = getContext();

		if (ctx == null) {
			return;
		}

		CompletionRequestor requestor = ctx.getCompletionRequestor();
		final String prefix = ctx.getPrefix();
		SourceRange replaceRange = getReplacementRange(ctx);
		
		String widget = ASTUtils.stripQuotes(ctx.getWidgetAlias());
		
		if (pathResolver == null){
			pathHelper = new WorkspacePathHelper();
			pathResolver = new YiiPathResolver(new WorkspacePathHelper());
		}
		
		IType type = pathHelper.findWidgetType(widget, ctx.getSourceModule());
		
//		String widgetPath = pathResolver.resolveWidgetPath(widget, ctx.getSourceModule());
//		getIFile(widgetPath);
		
		for (IField field : type.getFields()){
			if (PHPFlags.isPublic(field.getFlags())) {
				
				if (field.getElementName().startsWith("$"+prefix)){
					reporter.reportKeyword(field.getElementName().replaceFirst("\\$", ""), "", replaceRange);
				}
			}
		}
		
		
		
	}

	private IFile getIFile(String widgetPath) {
		File fileToOpen = new File(widgetPath);
		if (fileToOpen.exists() && fileToOpen.isFile()) {
			IFileStore fileStore = EFS.getLocalFileSystem().getStore(fileToOpen.toURI());
			
			Object o = fileStore.getAdapter(org.eclipse.dltk.core.IModelElement.class);
			Object o2 = fileStore.getAdapter(org.eclipse.ui.editors.text.ILocationProvider.class);
			Object o3 = fileStore.getAdapter(org.eclipse.dltk.core.ISourceModule.class);
			
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			IFile[] files = root.findFilesForLocationURI(fileStore.toURI());
			if (files != null && files.length > 0) {
				return files[0];
			} else {
			}
		}
		
		
		
		return null;
	}

}
