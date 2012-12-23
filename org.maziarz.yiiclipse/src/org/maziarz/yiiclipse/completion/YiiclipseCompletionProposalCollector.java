package org.maziarz.yiiclipse.completion;

import org.eclipse.dltk.compiler.CharOperation;
import org.eclipse.dltk.core.CompletionProposal;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.ui.text.completion.IScriptCompletionProposal;
import org.eclipse.dltk.ui.text.completion.MethodProposalInfo;
import org.eclipse.dltk.ui.text.completion.ProposalInfo;
import org.eclipse.dltk.ui.text.completion.ScriptCompletionProposal;
import org.eclipse.dltk.ui.text.completion.ScriptCompletionProposalCollector;
import org.eclipse.php.internal.core.project.PHPNature;

public class YiiclipseCompletionProposalCollector extends ScriptCompletionProposalCollector {

	protected YiiclipseCompletionProposalCollector(ISourceModule cu) {
		super(cu);
	}

	@Override
	protected IScriptCompletionProposal createScriptCompletionProposal(CompletionProposal proposal) {

		if (proposal.getKind() == CompletionProposal.POTENTIAL_METHOD_DECLARATION) {
			return createPotentialMethodDeclaration(proposal);
		}

		return super.createScriptCompletionProposal(proposal);
	}

	private IScriptCompletionProposal createPotentialMethodDeclaration(CompletionProposal proposal) {
		if (getSourceModule() == null) {
			return null;
		}

		IScriptProject scriptProject = getSourceModule().getScriptProject();

		String name = proposal.getName();

		String[] paramTypes = CharOperation.NO_STRINGS;

		int start = proposal.getReplaceStart();
		int length = getLength(proposal);
		String label = proposal.getName();// + "()";// getLabelProvider().createOverrideMethodProposalLabel(proposal);
		
		ScriptCompletionProposal scriptProposal = new YiiclipseOverrideCompletionProposal(proposal, getSourceModule(), name,
				paramTypes, start, length, label, String.valueOf(proposal.getCompletion()));

		scriptProposal.setImage(getImage(getLabelProvider().createMethodImageDescriptor(proposal)));

		ProposalInfo info = new MethodProposalInfo(scriptProject, proposal);
		scriptProposal.setProposalInfo(info);

		scriptProposal.setRelevance(computeRelevance(proposal));
		return scriptProposal;
	}

	@Override
	protected String getNatureId() {
		return PHPNature.ID;
	}

	@Override
	protected void processUnprocessedProposal(CompletionProposal proposal) {
		final IScriptCompletionProposal scriptProposal = createScriptCompletionProposal(proposal);
		if (scriptProposal != null) {
			addProposal(scriptProposal, proposal);
		}
	}

	/*-
	private void acceptPotentialMethodDeclarationLocal(CompletionProposal proposal) {
		if (getSourceModule() == null)
			return;
		// String prefix = String.valueOf(proposal.getName());
		// int completionStart = proposal.getReplaceStart();
		// int completionEnd = proposal.getReplaceEnd();
		// int relevance = computeRelevance(proposal);
		try {
			IModelElement element = getSourceModule().getElementAt(proposal
					.getCompletionLocation() + 1);
			if (element != null) {
				IType type = (IType) element.getAncestor(IModelElement.TYPE);
				if (type != null) {
					
					
					
					System.out.println("Add method here: "+proposal.getCompletion());
					
					//GetterSetterCompletionProposal.evaluateProposals(type,
					// prefix, completionStart, completionEnd
					// - completionStart, relevance + 1,
					// fSuggestedMethodNames, fJavaProposals);
					// MethodCompletionProposal.evaluateProposals(type, prefix,
					// completionStart, completionEnd - completionStart,
					// relevance, fSuggestedMethodNames, fJavaProposals);
					
				}
			}
		} catch (CoreException e) {
			YiiclipseBundle.logError(e.getMessage(), e);
		}
		
	}
	 */

}
