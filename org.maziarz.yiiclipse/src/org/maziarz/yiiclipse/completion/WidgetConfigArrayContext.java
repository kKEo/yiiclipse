package org.maziarz.yiiclipse.completion;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.dltk.core.CompletionRequestor;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.php.core.codeassist.ICompletionContext;
import org.eclipse.php.internal.core.codeassist.contexts.AbstractCompletionContext;
import org.eclipse.php.internal.core.util.text.TextSequence;

public class WidgetConfigArrayContext extends AbstractCompletionContext implements ICompletionContext {

	private String widgetAlias;
	private String configArray;
	private String prefix;

	public Pattern p = Pattern.compile("\\$this->widget\\((.+),(.+)");

	public String getWidgetAlias() {
		return widgetAlias;
	}

	public String getConfigArray() {
		return configArray;
	}
	
	public String getPrefixValue() {
		return prefix;
	}
	
	@Override
	public boolean isValid(ISourceModule sourceModule, int offset, CompletionRequestor requestor) {

		if (!super.isValid(sourceModule, offset, requestor)) {
			return false;
		}

		TextSequence statementText = getStatementText();

		String stmt = statementText.toString();

		Matcher m = Pattern.compile("\\$this->widget\\(([^,]+), (.+)").matcher(stmt.replace('\n', ' '));
		if (m.matches()) {
			widgetAlias = m.group(1);
			configArray = m.group(2).trim();

			if (!configArray.startsWith("array")) {
				return false;
			}
		} else {
			return false;
		}

		int isInString = -1;
		int isKey = -1;

		for (int i = configArray.length() - 1; i > 0; i--) {
			char ch = configArray.charAt(i);
			if (ch == '\'' || ch == '"') {
				// context is in string
				
				isInString = i;
				for (int ii = i-1; ii > 0; ii--) {
					if(Character.isWhitespace(configArray.charAt(ii))) {
						continue;
					}
					char iiChar = configArray.charAt(ii); 
					if (iiChar == ',' || iiChar == '('){
						isKey = ii;
						break;
					} else {
						return false;
					}
				}
				String keyArray = configArray.substring(0, isKey);
				if (keyArray.lastIndexOf("array") > 0){
					return false;
				}
		
				prefix = getPrefixWithoutProcessing();
				
//				System.out.println("Prefix: "+prefix);
				
				return true;
			}
		}

		return false;
	}

	protected static int eatString(String document, int offset) throws BadLocationException {
		Character inString = null;

		Character ch = null;
		while ((ch = document.charAt(--offset)) != null) {
			if (ch == '"' || ch == '\'') {
				if (inString == null) {
					inString = ch;
				} else {
					if (ch == inString) {
						if (offset > 0) {
							if (document.charAt(offset - 1) != '\\') {
								return offset;
							}
						} else {
							return offset;
						}
					}
				}
			}
		}

		return 0;
	}

	protected static int eatWhitespaces(String document, int offset) {
		Character ch = null;
		while ((ch = document.charAt(--offset)) != null) {
			if (!Character.isWhitespace(ch)) {
				break;
			}
		}
		return offset + 1;
	}

	protected static boolean isTopLevelKey(String str, int length) {

		return false;
	}

}
