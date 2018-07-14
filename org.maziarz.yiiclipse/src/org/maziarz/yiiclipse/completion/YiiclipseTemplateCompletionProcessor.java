package org.maziarz.yiiclipse.completion;

import org.eclipse.dltk.ui.templates.ScriptTemplateAccess;
import org.eclipse.dltk.ui.templates.ScriptTemplateCompletionProcessor;
import org.eclipse.dltk.ui.text.completion.ScriptContentAssistInvocationContext;
import org.eclipse.jface.preference.IPreferenceStore;
//import static org.eclipse.php.internal.ui.editor.templates.PhpTemplateContextType.PHP_CONTEXT_TYPE_ID;
import org.maziarz.yiiclipse.YiiclipseBundle;

public class YiiclipseTemplateCompletionProcessor extends ScriptTemplateCompletionProcessor {

	private static final String PHP_CONTEXT_TYPE_ID = "php";
	
	public YiiclipseTemplateCompletionProcessor(ScriptContentAssistInvocationContext context) {
		super(context);
		
	}

	@Override
	protected String getContextTypeId() {
		return PHP_CONTEXT_TYPE_ID;
	}

	@Override
	protected ScriptTemplateAccess getTemplateAccess() {
		return new ScriptTemplateAccess(){

			@Override
			protected String getContextTypeId() {
				return PHP_CONTEXT_TYPE_ID;
			}

			@Override
			protected String getCustomTemplatesKey() {
				return "org.maziarz.yiiclipse.templates";
			}

			@Override
			protected IPreferenceStore getPreferenceStore() {
				return YiiclipseBundle.getDefault().getPreferenceStore();
			}};
	}


}
