package org.maziarz.yiiclipse.hyperlinks;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;

public class ExternalResourceHyperlink implements IHyperlink {

	private IRegion fRegion;
	private IPath fLocalPath;
	private TextEditor fEditor;
	
	public ExternalResourceHyperlink(Region region, IPath lp, TextEditor editor) {
		
		Assert.isNotNull(region);
		Assert.isNotNull(lp);
		Assert.isNotNull(editor);
		
		this.fRegion = region;
		this.fLocalPath = lp;
		this.fEditor = editor;
	}

	private String getEditorId(){
		return fEditor.getSite().getId();
	}
	
	private IWorkbenchPage getActivePage(){
		return fEditor.getSite().getWorkbenchWindow().getActivePage();
	}
	
	@Override
	public IRegion getHyperlinkRegion() {
		return fRegion;
	}

	@Override
	public String getTypeLabel() {
		return "Open Declaration";
	}

	@Override
	public String getHyperlinkText() {
		return "Open declaration (hyperlink text)";
	}

	@Override
	public void open() {
		
		IEditorInput editorInput;
		IFileStore fileStore;
		try {
			fileStore = EFS.getStore(fLocalPath.toFile().toURI());
			editorInput = new FileStoreEditorInput(fileStore);
		} catch (CoreException e) {
			e.printStackTrace();
			return;
		}
		
		IEditorPart editorPart;
		try {
			editorPart = getActivePage().openEditor(editorInput, getEditorId(), true);
		} catch (PartInitException e1) {
			e1.printStackTrace();
			return;
		}
		
		IDocumentProvider provider = fEditor.getDocumentProvider();
		try {
			provider.connect(editorInput);
			
			IDocument document = provider.getDocument(editorInput);
			IRegion lineRegion = document.getLineInformation(1);
			((TextEditor)editorPart).selectAndReveal(lineRegion.getOffset(), lineRegion.getLength());

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		provider.disconnect(editorInput);

	}

}
