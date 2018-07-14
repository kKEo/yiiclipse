package org.maziarz.yiiclipse.hyperlinks;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.DLTKLanguageManager;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISearchPatternProcessor;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.index2.IElementResolver;
import org.eclipse.dltk.core.index2.search.ISearchEngine.MatchRule;
import org.eclipse.dltk.core.index2.search.ISearchEngine.SearchFor;
import org.eclipse.dltk.core.index2.search.ISearchRequestor;
import org.eclipse.dltk.core.index2.search.ModelAccess;
import org.eclipse.dltk.core.search.IDLTKSearchScope;
import org.eclipse.dltk.core.search.SearchEngine;
import org.eclipse.php.core.compiler.IPHPModifiers;
import org.eclipse.php.internal.core.PHPLanguageToolkit;

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
		
		IDLTKLanguageToolkit dltkToolkit = sourceModule.getScriptProject().getLanguageToolkit();
		
		IType type =  lookForType(widgetTypeName, scope, dltkToolkit);
		
		if (type != null) {
			return type;
		}
		
		// if not found in project context look for the object in whole workspace context
		scope = SearchEngine.createWorkspaceScope(dltkToolkit);
		
		return lookForType(widgetTypeName, scope, dltkToolkit);
	}

	private IType lookForType(String widgetTypeName, IDLTKSearchScope scope, IDLTKLanguageToolkit dltkToolkit) {
		String name = widgetTypeName;
		String qualifier = null;
		if (name != null) {
			ISearchPatternProcessor processor = DLTKLanguageManager
					.getSearchPatternProcessor(scope.getLanguageToolkit());
			if (processor != null) {
				String delim = processor.getDelimiterReplacementString();
				int i = name.lastIndexOf(delim);
				if (i != -1) {
					qualifier = name.substring(0, i);
					name = name.substring(i + 1);
				}
			}
		}
	
		final IElementResolver elementResolver = ModelAccess.getElementResolver(dltkToolkit);
		if (elementResolver == null) {
			return null;
		}
		
		List<IType> types = new ArrayList<>();
		
		ISearchRequestor requestor = new ISearchRequestor() {

			@Override
			@SuppressWarnings("unchecked")
			public void match(int elementType, int flags, int offset,
					int length, int nameOffset, int nameLength,
					String elementName, String metadata, String doc,
					String qualifier, String parent, ISourceModule sourceModule,
					boolean isReference) {

				IModelElement element = elementResolver.resolve(elementType,
						flags, offset, length, nameOffset, nameLength,
						elementName, metadata, doc, qualifier, parent,
						sourceModule);
				if (element != null) {
					types.add((IType)element);
				}
			}
		};
	
		
		ModelAccess.getSearchEngine(dltkToolkit).search(IModelElement.TYPE, qualifier, name, 0, IPHPModifiers.AccTrait, 1, SearchFor.DECLARATIONS, MatchRule.EXACT, scope, requestor, null);
		
		
		if (types.size() > 0) {
			return types.get(0);
		}
		
		return null;
	}

	@Override
	public IFile getLocalPath(IPath app) {
		return ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(app);
	}

}
