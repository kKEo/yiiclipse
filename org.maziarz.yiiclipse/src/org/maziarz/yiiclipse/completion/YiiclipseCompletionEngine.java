package org.maziarz.yiiclipse.completion;

import java.util.Collections;
import java.util.Map;
import java.util.TimerTask;
import java.util.Arrays;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.codeassist.ICompletionEngine;
import org.eclipse.dltk.codeassist.ScriptCompletionEngine;
import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.core.CompletionProposal;
import org.eclipse.dltk.core.CompletionRequestor;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.php.internal.core.PHPVersion;
import org.eclipse.php.internal.core.util.text.PHPTextSequenceUtilities;
import org.maziarz.yiiclipse.wizards.YiiProjectWorkspaceHelper;


public class YiiclipseCompletionEngine extends ScriptCompletionEngine implements ICompletionEngine {

	/* package */ static String POSTCOMPLETION = "POST";
	
	public YiiclipseCompletionEngine() {
		
	}

	@Override
	public void complete(final IModuleSource module, int position, int i) {

		CharSequence text = module.getSourceContents();
		ISourceRange range = PHPTextSequenceUtilities.getEnclosingIdentifier(text, position-1);
		
		String prefix = text.subSequence(range.getOffset(), position).toString();

		if (prefix.length() > 6 && prefix.startsWith("action")) {
			String action = getActionViewName(prefix);
			
			CompletionProposal proposal = createProposal(CompletionProposal.POTENTIAL_METHOD_DECLARATION, position);
			
			proposal.setExtraInfo(Collections.singletonMap(POSTCOMPLETION,new Runnable() {
				
				@Override
				public void run() {
					;
				}
			}));
			proposal.setName(prefix+"() - create method");
			
			proposal.setCompletion("public function "+prefix+"() {\n\t\t$this->render('"+action+"', array());\n\t}");
			proposal.setReplaceRange(range.getOffset(), position);
			this.requestor.accept(proposal);
			
			/*- TODO: 
			proposal = createProposal(CompletionProposal.POTENTIAL_METHOD_DECLARATION, position);
			proposal.setName(prefix+"() - create method and create view file");
			proposal.setCompletion("public function "+prefix+"() {\n\t\t$this->render('"+action+"', array());\n\t}");
			proposal.setReplaceRange(range.getOffset(), position);
			
			proposal.setExtraInfo(Collections.singletonMap(POSTCOMPLETION,new Runnable() {
				@Override
				public void run() {
					YiiProjectWorkspaceHelper.INSTANCE.createFile(module.getModelElement().getScriptProject().getProject(), "file1.php");
					
				}
			}));
			
			this.requestor.accept(proposal);
			*/
		}

	}

	private String getActionViewName(String prefix) {
		String view = prefix.replaceFirst("action", "");
		String firstLetter = (new String(new char[] {view.toCharArray()[0]})).toLowerCase();
		return firstLetter + new String(Arrays.copyOfRange(view.toCharArray(), 1, view.length()));
	}

}
