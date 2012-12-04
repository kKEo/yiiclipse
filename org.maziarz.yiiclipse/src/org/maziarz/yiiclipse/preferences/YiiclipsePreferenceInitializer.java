package org.maziarz.yiiclipse.preferences;

import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.maziarz.yiiclipse.YiiclipseBundle;

public class YiiclipsePreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = YiiclipseBundle.getDefault().getPreferenceStore();
		
		Path basePath = new Path(System.getProperty("user.home"));
		Path fullPath = (Path) basePath.append("php").append("frameworks").append("yii");
		
		store.setDefault(YiiclipsePreferenceConstants.FRAMEWORK_PATH, fullPath.toPortableString());
		
		store.setDefault(YiiclipsePreferenceConstants.ENABLE_ALIAS_COMPLETION, true);
		store.setDefault(YiiclipsePreferenceConstants.ENABLE_WIDGET_CONFIG_COMPLETION, true);
	}

}
