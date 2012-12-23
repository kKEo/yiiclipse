package org.maziarz.yiiclipse.completion;

import org.eclipse.dltk.ui.text.completion.IScriptCompletionProposalComputer;
import org.eclipse.dltk.ui.text.completion.ScriptCompletionProposalCollector;
import org.eclipse.dltk.ui.text.completion.ScriptCompletionProposalComputer;
import org.eclipse.dltk.ui.text.completion.ScriptContentAssistInvocationContext;
import org.eclipse.jface.text.templates.TemplateCompletionProcessor;

public class YiiScriptCompletionProposalComputer extends ScriptCompletionProposalComputer implements IScriptCompletionProposalComputer {

	@Override
	protected TemplateCompletionProcessor createTemplateProposalComputer(ScriptContentAssistInvocationContext context) {
		return new YiiclipseTemplateCompletionProcessor(context);
	}

	@Override
	protected ScriptCompletionProposalCollector createCollector(ScriptContentAssistInvocationContext context) {
		ScriptCompletionProposalCollector collector = new YiiclipseCompletionProposalCollector(context.getSourceModule());
		return collector;
	}

}
