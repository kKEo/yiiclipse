package org.maziarz.yiiclipse.hyperlinks;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.ast.ASTVisitor;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.SourceParserUtil;
import org.eclipse.dltk.internal.ui.editor.ModelElementHyperlink;
import org.eclipse.dltk.ui.actions.OpenAction;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.php.internal.core.filenetwork.FileNetworkUtility;
import org.eclipse.php.internal.ui.editor.PHPStructuredEditor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.maziarz.yiiclipse.YiiclipseBundle;
import org.maziarz.yiiclipse.utils.YiiPathResolver;

public class YiiHyperlinkDetector extends AbstractHyperlinkDetector {

	@Override
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
		
		PHPStructuredEditor editor = findPhpEditor(textViewer);
		
		if (editor == null) {
			YiiclipseBundle.debug("Cannot find file php editor instance.");
			return null;
		}
		
		IModelElement input = org.eclipse.dltk.internal.ui.editor.EditorUtility.getEditorInputModelElement(editor, false);
		
		if (!(input instanceof ISourceModule)) {
			YiiclipseBundle.debug("Cannot find source module input.");
			return null;
		}
		
		final int offset = region.getOffset();
		String[] file = new String[1];
		Region[] selectRegion = new Region[1];
		
		final ISourceModule sourceModule = (ISourceModule) input;
		ModuleDeclaration moduleDeclaration = SourceParserUtil.getModuleDeclaration(sourceModule, null);
		
		ASTVisitor visitor = new YiiHyperlinkASTVisitor2(sourceModule, new YiiPathResolver(new WorkspacePathHelper()), offset, file[0], selectRegion[0]);
		
		try {
			moduleDeclaration.traverse(visitor);
		} catch (Exception e) {
			YiiclipseBundle.logError(e.getMessage(), e);
		}
		
		file[0] = ((YiiHyperlinkASTVisitor2)visitor).getFile();
		
		if (file[0]!=null){
			YiiclipseBundle.debug("Found file: "+file[0]);
		}
		
		selectRegion[0] = ((YiiHyperlinkASTVisitor2)visitor).getSelectRegion();
		
		if (file[0] != null){
			if (file[0].endsWith("css")){
				IPath localPath = new Path(file[0]);
				IHyperlink hyperlink = new EFSResourceHyperlink(selectRegion[0], localPath);
				return new IHyperlink[] {hyperlink};
			}
		}
		
		ISourceModule viewSourceModule = FileNetworkUtility.findSourceModule(sourceModule, file[0]);
		
		if (viewSourceModule != null) {
			IHyperlink hyperlink = new ModelElementHyperlink(selectRegion[0], viewSourceModule, new OpenAction(editor));
			return new IHyperlink[] { hyperlink };
		} else {
			IPath localPath = new Path(file[0]);
			
			if (localPath.toFile().exists()){
				// file is most probably external (out of current workspace)
				IHyperlink hyperlink = new ExternalResourceHyperlink(selectRegion[0], localPath, editor);
				return new IHyperlink[] { hyperlink };
			} else {
				IFile localFile  = ResourcesPlugin.getWorkspace().getRoot().getFile(localPath);
				if (localFile.exists()) {
					ISourceModule widgetSourceModule = (ISourceModule) DLTKCore.create(localFile);
					IHyperlink hyperlink = new ModelElementHyperlink(selectRegion[0], widgetSourceModule, new OpenAction(editor));
					return new IHyperlink[] { hyperlink };
				} else {
					YiiclipseBundle.logWarning("File does not exists: "+localFile.toString());
				}
			}
				
			
		}
		
		return null;
	}

	private PHPStructuredEditor findPhpEditor(final ITextViewer textViewer) {
		
		IWorkbenchPage workbenchpage = PlatformUI.getWorkbench()
		.getActiveWorkbenchWindow().getActivePage();

		// Check active editor, first:
		IEditorPart activeEditorPart = workbenchpage.getActiveEditor();
		if (activeEditorPart instanceof PHPStructuredEditor
				&& ((PHPStructuredEditor) activeEditorPart).getTextViewer()
				.getDocument() == textViewer.getDocument()) {
			return (PHPStructuredEditor)activeEditorPart;
		}
		
		// Check other editors:
		IEditorReference[] editorReferences = workbenchpage
				.getEditorReferences();
		for (int i = 0; i < editorReferences.length; i++) {
			IEditorReference editorReference = editorReferences[i];
			IEditorPart editorPart = editorReference.getEditor(false);
			if (activeEditorPart != editorPart && (editorPart instanceof PHPStructuredEditor
					&& ((PHPStructuredEditor) activeEditorPart).getTextViewer()
					.getDocument() == textViewer.getDocument())) {
				return (PHPStructuredEditor)editorPart;
			}
		}
		
		return null;
	}
	
	

}
