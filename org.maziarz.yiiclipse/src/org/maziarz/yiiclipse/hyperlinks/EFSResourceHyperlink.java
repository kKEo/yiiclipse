package org.maziarz.yiiclipse.hyperlinks;

import java.io.File;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

public class EFSResourceHyperlink implements IHyperlink {

	private IRegion fRegion;
	private IPath fLocalPath;

	public EFSResourceHyperlink(Region region, IPath lp) {

		Assert.isNotNull(region);
		Assert.isNotNull(lp);

		this.fRegion = region;
		this.fLocalPath = lp;
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
		File fileToOpen = this.fLocalPath.toFile();
		if (fileToOpen.exists() && fileToOpen.isFile()) {
			IFileStore fileStore = EFS.getLocalFileSystem().getStore(fileToOpen.toURI());
			
			IFile file = (IFile)fileStore.getAdapter(IFile.class);
			System.out.println("File: "+file);
			
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			try {
				IDE.openEditorOnFileStore(page, fileStore);
			} catch (PartInitException e) {

			}
		}
	}

}
