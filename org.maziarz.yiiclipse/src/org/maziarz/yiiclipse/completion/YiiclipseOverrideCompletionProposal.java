package org.maziarz.yiiclipse.completion;

import java.util.Map;

import org.eclipse.dltk.core.CompletionProposal;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.ui.text.completion.ScriptOverrideCompletionProposal;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

public class YiiclipseOverrideCompletionProposal extends ScriptOverrideCompletionProposal {

	private CompletionProposal proposal;

	public YiiclipseOverrideCompletionProposal(CompletionProposal proposal,
			ISourceModule sourceModule, String name, String[] paramTypes, int start, int length, String displayName, String completionProposal) {
		super(sourceModule.getScriptProject(), sourceModule, name, paramTypes, start, length, displayName, completionProposal);
		this.proposal = proposal;
		
	}
		
	@Override
	protected void postReplace(IDocument document) throws BadLocationException {
		if (proposal.getExtraInfo() instanceof Map) {
			Object o = ((Map)proposal.getExtraInfo()).get(YiiclipseCompletionEngine.POSTCOMPLETION);
			if (o instanceof Runnable) {
				((Runnable) o).run();
			}
		}
		
	}

}
