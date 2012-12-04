package org.maziarz.yiiclipse.utils;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;

public interface IYiiPathsHelper {

	public IPath getWebRoot(ISourceModule sourceModule);
	public IPath getSystemPath(ISourceModule relativePath);
	public IType findWidgetType(String widgetTypeName, ISourceModule sourceModule);
	public String findWidgetByName(String widgetTypeName, ISourceModule sourceModule);
	public IFile getLocalPath(IPath app);
}
