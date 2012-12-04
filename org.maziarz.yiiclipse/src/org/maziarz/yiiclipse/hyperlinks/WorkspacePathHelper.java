package org.maziarz.yiiclipse.hyperlinks;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.index2.search.ISearchEngine.MatchRule;
import org.eclipse.dltk.core.search.IDLTKSearchScope;
import org.eclipse.dltk.core.search.SearchEngine;
import org.eclipse.php.internal.core.model.PhpModelAccess;
import org.maziarz.yiiclipse.YiiclipseBundle;
import org.maziarz.yiiclipse.utils.IYiiPathsHelper;

public class WorkspacePathHelper implements IYiiPathsHelper{

	@Override
	public IPath getWebRoot(ISourceModule relativePath) {
		IPath root = ResourcesPlugin.getWorkspace().getRoot().getLocation();
		return root.append(relativePath.getScriptProject().getPath());
	}

	@Override
	public IPath getSystemPath(ISourceModule relativePath) {
		
		try {
			for (IBuildpathEntry entry : relativePath.getScriptProject().getResolvedBuildpath(true)){
				if (new File(entry.getPath().append("yii.php").setDevice(null).toOSString()).exists()){
					return entry.getPath().setDevice(null);
				}
			}
		} catch (ModelException e) {
			YiiclipseBundle.logError(e.getMessage(), e);
		};
		
		YiiclipseBundle.logWarning("Yii Framework not found in project configuration");
		throw new IllegalStateException("Yii Framework not found in project configuration");
		
	}

	@Override
	public String findWidgetByName(String widgetTypeName, ISourceModule sourceModule) {
		IType type = findWidgetType(widgetTypeName, sourceModule);
		
		return (type == null)?null:type.getPath().setDevice("").toOSString();
	}
	
	@Override
	public IType findWidgetType(String widgetTypeName, ISourceModule sourceModule) {
		IDLTKSearchScope scope = SearchEngine.createSearchScope(sourceModule.getScriptProject());
		
		String widgetClass = widgetTypeName.replaceFirst(".*\\.", "");
		IType[] types = PhpModelAccess.getDefault().findTypes(widgetClass, MatchRule.EXACT, 0, 0, scope, null);
		
		if (types.length > 0) {
			return types[0];
		}
		
		// if not found in project context look for the object in whole workspace context
		IDLTKLanguageToolkit dltkToolkit = sourceModule.getScriptProject().getLanguageToolkit();
		scope = SearchEngine.createWorkspaceScope(dltkToolkit);
		
		types = PhpModelAccess.getDefault().findTypes(widgetClass, MatchRule.EXACT, 0, 0, scope, null);
		
		if (types.length > 0) {
			return types[0];
		}
		
		return null;
	}

	@Override
	public IFile getLocalPath(IPath app) {
		return ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(app);
	}

}
