package org.maziarz.yiiclipse.completion;

import org.eclipse.dltk.core.CompletionRequestor;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.php.core.codeassist.ICompletionContext;
import org.eclipse.php.internal.core.codeassist.contexts.AbstractCompletionContext;
import org.eclipse.php.internal.core.codeassist.contexts.StatementContext;
import org.eclipse.php.internal.core.util.text.PHPTextSequenceUtilities;
import org.eclipse.php.internal.core.util.text.TextSequence;

public class AliasEnabledContext extends AbstractCompletionContext implements ICompletionContext {

	@Override
	public boolean isValid(ISourceModule sourceModule, int offset, CompletionRequestor requestor) {

		if (!super.isValid(sourceModule, offset, requestor)) {
			return false;
		}

		TextSequence statementText = getStatementText();
		if (statementText.toString().startsWith("$this->widget") || statementText.toString().startsWith("Yii::import")) {
			return isFirstArgument(statementText);
		}

		return false;
	}

	private boolean isFirstArgument(TextSequence textSequence) {
		
		int startPosition = textSequence.length();
		
		int rv = startPosition;
		int openParanCount = 0;
		
		for (; rv > 0; rv--) {
			if (textSequence.charAt(rv - 1) == '(') {
				openParanCount++;
			} else if (textSequence.charAt(rv -1) == ','){
				return false;
			}
		}
		
		return openParanCount == 1;
	}

	@Override
	public String getPrefix() throws BadLocationException {

		if (hasWhitespaceBeforeCursor()) {
			return ""; //$NON-NLS-1$
		}
		TextSequence statementText = getStatementText();
		int statementLength = statementText.length();
		int prefixEnd = readBackwardSpaces(statementText, statementLength);
		int prefixStart = readAliasStartIndex(statementText, prefixEnd, true);

		return statementText.subSequence(prefixStart, prefixEnd).toString();
	}

	public static int readBackwardSpaces(CharSequence textSequence, int startPosition) {
		int rv = startPosition;
		for (; rv > 0; rv--) {
			if (!Character.isWhitespace(textSequence.charAt(rv - 1))) {
				break;
			}
		}
		return rv;
	}

	public static int readAliasStartIndex(CharSequence textSequence, int startPosition, boolean includeDolar) {
		while (startPosition > 0) {
			char ch = textSequence.charAt(startPosition - 1);

			if (ch == '"' || ch == '\'') {
				break;
			}

			startPosition--;
		}
		return startPosition;
	}

}
