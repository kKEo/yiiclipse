package org.maziarz.yiiclipse.quickfix;

import org.eclipse.dltk.ui.text.completion.IScriptCompletionProposal;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

public class WorkspaceModificationProposal implements IScriptCompletionProposal {
	
	private Runnable runnable;

	public WorkspaceModificationProposal(Runnable runnable) {
		this.runnable = runnable;
	}

	@Override
	public Point getSelection(IDocument document) {
		return null;
	}

	@Override
	public Image getImage() {
		return null;
	}

	@Override
	public String getDisplayString() {
		return "Create view file";
	}

	@Override
	public IContextInformation getContextInformation() {
		return null;
	}

	@Override
	public String getAdditionalProposalInfo() {
		return null;
	}

	@Override
	public void apply(IDocument document) {
		if (runnable != null) {
			runnable.run();
		}
	}

	@Override
	public int getRelevance() {
		return 0;
	}
}