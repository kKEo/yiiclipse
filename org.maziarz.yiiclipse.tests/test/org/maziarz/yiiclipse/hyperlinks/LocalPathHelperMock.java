package org.maziarz.yiiclipse.hyperlinks;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.maziarz.yiiclipse.utils.IYiiPathsHelper;

public class LocalPathHelperMock implements IYiiPathsHelper{

	private IPath projectDir;
	private IPath frameworkDir;
	
	public LocalPathHelperMock(IPath projectDir, IPath frameworkDir) {
		this.projectDir = projectDir;
		this.frameworkDir = frameworkDir;
	}
	
	@Override
	public IPath getWebRoot(ISourceModule sourceModule) {
		return projectDir;
	}

	@Override
	public IPath getSystemPath(ISourceModule relativePath) {
		return frameworkDir;
	}

	@Override
	public String findWidgetByName(String widgetTypeName, ISourceModule sourceModule) {
		return null;
	}

	@Override
	public IFile getLocalPath(IPath app) {
		return null;
	}

	@Override
	public IType findWidgetType(String widgetTypeName, ISourceModule sourceModule) {
		return null;
	}

}
