package org.maziarz.yiiclipse.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.maziarz.yiiclipse.YiiclipseBundle;

public class GeneralPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public GeneralPage() {
		super(GRID);
		setPreferenceStore(YiiclipseBundle.getDefault().getPreferenceStore());
		setDescription("Yiiclipse configuration page");
	}

	@Override
	protected void createFieldEditors() {
		addField(new DirectoryFieldEditor(YiiclipsePreferenceConstants.FRAMEWORK_PATH, "Yii &Directory:", getFieldEditorParent()));
		
		addField(new BooleanFieldEditor(YiiclipsePreferenceConstants.ENABLE_ALIAS_COMPLETION, "Enable alias completion", getFieldEditorParent()));
		addField(new BooleanFieldEditor(YiiclipsePreferenceConstants.ENABLE_WIDGET_CONFIG_COMPLETION, "Enable widget config completion", getFieldEditorParent()));
	}

	@Override
	public void init(IWorkbench workbench) {

	}

}
