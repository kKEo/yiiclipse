package org.maziarz.yiiclipse.completion;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.php.core.codeassist.ICompletionContext;
import org.eclipse.php.internal.core.codeassist.contexts.CompletionContextResolver;
import org.maziarz.yiiclipse.YiiclipseBundle;
import org.maziarz.yiiclipse.preferences.YiiclipsePreferenceConstants;

public class YiiContextResolver extends CompletionContextResolver {

	@Override
	public ICompletionContext[] createContexts() {
		
		final IPreferenceStore preferences = YiiclipseBundle.getDefault().getPreferenceStore();

		List<ICompletionContext> contexts = new LinkedList<ICompletionContext>();
		
		if (preferences.getBoolean(YiiclipsePreferenceConstants.ENABLE_ALIAS_COMPLETION)){
			contexts.add(new AliasEnabledContext());
		}
		if (preferences.getBoolean(YiiclipsePreferenceConstants.ENABLE_WIDGET_CONFIG_COMPLETION)) {
			contexts.add(new WidgetConfigArrayContext());
		}
		
		//contexts.add(new InControllerContext());
		
		return contexts.toArray(new ICompletionContext[0]);
	}
	
}
