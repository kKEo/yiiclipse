package org.maziarz.yiiclipse.utils;

import java.io.File;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.maziarz.yiiclipse.YiiclipseBundle;
import org.maziarz.yiiclipse.hyperlinks.WorkspacePathHelper;

public class YiiPathResolver {
	
	private IYiiPathsHelper pathsHelper;
	private static YiiPathResolver pathResolver;
	
	public YiiPathResolver(IYiiPathsHelper pathHelper) {
		this.pathsHelper = pathHelper;
	}

	private static YiiPathResolver getPathResolver() {
		
		if (pathResolver == null){
			pathResolver = new YiiPathResolver(new WorkspacePathHelper());
		}
		
		return pathResolver;
	}
	
	public static String resolveViewPath(final ISourceModule sourceModule, String currentTypeName, String fileName) {

		
		String viewFilePath = null;

		// method invocation is inside of other view (not from the controller class)
		if (currentTypeName == null) {

			// target view is located in other folder
			if (fileName.startsWith("//")) {
				viewFilePath = getPathResolver().resolveAliasPath("application.views", sourceModule) + File.separator + fileName.substring(2, fileName.length())
						+ ".php";
			} else {
				IPath currentPath = sourceModule.getPrimaryElement().getParent().getPath();
				viewFilePath = currentPath.append(fileName + ".php").toOSString();
			}

		} else {
			if (fileName.startsWith("//")) {
				viewFilePath = getPathResolver().resolveAliasPath("application.views", sourceModule) + File.separator + fileName.substring(2, fileName.length())
						+ ".php";
			} else if (fileName.contains("/")) {
				viewFilePath = getPathResolver().resolveAliasPath("application.views", sourceModule) + File.separator + fileName + ".php";
			} else {
				int controllerIdx = currentTypeName.lastIndexOf("Controller");
				if (controllerIdx != -1) {
					String controller = currentTypeName.substring(0, controllerIdx);
					
					IModelElement primaryElement = sourceModule.getPrimaryElement();
					IPath baseContorllersPath = getBaseControllersPath(primaryElement.getPath());
					String extraFolders = primaryElement.getParent().getPath().makeRelativeTo(baseContorllersPath.append("controllers"))
							.toPortableString();
					if (!extraFolders.isEmpty()) {
						extraFolders += File.separator;
					}
					String viewPathId = extraFolders + StringUtils.decapitalize(controller) + File.separator + fileName.trim() + ".php";
					viewFilePath = baseContorllersPath.append("views").append(viewPathId).toOSString();
				} else {
					// try to find views folder
					IPath parent = sourceModule.getParent().getPath();
					IPath absoluteParentPath = ResourcesPlugin.getWorkspace().getRoot().getLocation().append(parent);
					IPath viewsPath = absoluteParentPath.append("views");

					if (viewsPath.toFile().exists()) {
						viewFilePath = viewsPath.toOSString() + File.separator + fileName + ".php";
					} else {
						throw new RuntimeException("Folder not found ("+viewsPath+")");
					}
				}
			}
		}

		return viewFilePath;
	}	
	
	public String resolveWidgetPath(String widgetAlias, ISourceModule sourceModule) {

		IPath path = new Path(widgetAlias.replace('.', File.separatorChar));

		switch (path.segmentCount()) {
		case 0:
			break;
		case 1:
			String widgetTypeName = new Path(widgetAlias).lastSegment();
			String widgetPath = pathsHelper.findWidgetByName(widgetTypeName, sourceModule);
			if (widgetPath != null){
				return widgetPath;
			}
			break;
		default:
			IPath p = this.resolveAliasPath(widgetAlias, sourceModule);
			
			if (p.addFileExtension("php").toFile().exists()){
				String sourcePath = p.addFileExtension("php").toOSString();
				
				return sourcePath;
			}
		}
		
		YiiclipseBundle.debug("Unable to resolve alias: \"" + widgetAlias+"\"");
		return null;
	}

	public IPath resolveAliasPath(String alias, ISourceModule sourceModule) {

		if ("application".equals(alias)) {
			return resolveAliasPath("webroot.protected",sourceModule);
		} else if ("webroot".equals(alias)){
			IPath project = pathsHelper.getWebRoot(sourceModule);
			return project;
		} else if ("ext".equals(alias)){
			return resolveAliasPath("webroot.protected.extensions",sourceModule);
		} else if ("system".equals(alias)){
			IPath frameworkPath = pathsHelper.getSystemPath(sourceModule);
			return frameworkPath;
		} else if ("zii".equals(alias)){
			return resolveAliasPath("system.zii", sourceModule);
		}
		
		IPath path = new Path(alias.replace('.', File.separatorChar));

		String lastSegment = path.lastSegment();
		IPath trunkedPath = path.removeLastSegments(1);

		String trunkedPathStr = trunkedPath.toOSString();

		if ("".equals(trunkedPathStr) == false) {
			return resolveAliasPath(trunkedPathStr, sourceModule).append(lastSegment);
		} else {
			return new Path(lastSegment);
		}
	}

	public static IPath getBaseControllersPath(IPath path) {
		int i = 0;
		for (i = 0; path.segmentCount() > i; i++) {
			if ("controllers".equals(path.segment(i))) {
				break;
			}
		}
		return path.uptoSegment(i);
	}

}
