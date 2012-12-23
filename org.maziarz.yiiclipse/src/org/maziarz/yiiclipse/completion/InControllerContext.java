package org.maziarz.yiiclipse.completion;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.CompletionRequestor;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.internal.core.SourceType;
import org.eclipse.php.core.codeassist.ICompletionContext;
import org.eclipse.php.internal.core.codeassist.contexts.AbstractCompletionContext;
import org.eclipse.php.internal.core.util.text.TextSequence;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.exceptions.ResourceAlreadyExists;
import org.maziarz.yiiclipse.YiiclipseBundle;

public class InControllerContext extends AbstractCompletionContext implements ICompletionContext{

	
	@Override
	public boolean isValid(ISourceModule sourceModule, int offset, CompletionRequestor requestor) {
		
		if (!super.isValid(sourceModule, offset, requestor)) {
			return false;
		}
		
		try {
			String[] s = ((SourceType)(getSourceModule().getElementAt(offset))).getSuperClasses();
			if (s.length > 0) {
				return s[0].endsWith("Controller");
			}
			
		} catch (ModelException e) {
			// ignore
		} 
		
		return false;
	}
	
}
