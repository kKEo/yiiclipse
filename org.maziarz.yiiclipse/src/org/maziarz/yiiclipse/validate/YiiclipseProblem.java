package org.maziarz.yiiclipse.validate;

import org.eclipse.dltk.compiler.problem.IProblem;
import org.eclipse.dltk.compiler.problem.IProblemIdentifier;
import org.eclipse.dltk.compiler.problem.ProblemSeverity;

public class YiiclipseProblem implements IProblem {

	private String message;
	private int sourceEnd;
	private int sourceLineNumber;
	private int sourceStart;

	public YiiclipseProblem(String message, int sourceStart, int sourceEnd, int sourceLineNumber) {
		this.message = message;
		this.sourceStart = sourceStart;
		this.sourceEnd = sourceEnd;
		this.sourceLineNumber = sourceLineNumber;
	}
	
	@Override
	public String[] getArguments() {
		return new String[]{};
	}

	@Override
	public IProblemIdentifier getID() {
		return IProblemIdentifier.NULL;
	}

	@Override
	public String getMessage() {
		return this.message;
	}

	@Override
	public String getOriginatingFileName() {
		return "";
	}

	@Override
	public int getSourceEnd() {
		return this.sourceEnd;
	}

	@Override
	public int getSourceLineNumber() {
		return this.sourceLineNumber;
	}

	@Override
	public int getSourceStart() {
		return this.sourceStart;
	}

	@Override
	public ProblemSeverity getSeverity() {
		return ProblemSeverity.WARNING;
	}

	@Override
	public void setSeverity(ProblemSeverity severity) {
		throw new IllegalAccessError("Not supported");
	}

	@Override
	public boolean isError() {
		return false;
	}

	@Override
	public boolean isWarning() {
		return true;
	}

	@Override
	public boolean isTask() {
		return false;
	}

	@Override
	public void setSourceEnd(int sourceEnd) {
		throw new IllegalAccessError("Not supported");
	}

	@Override
	public void setSourceLineNumber(int lineNumber) {
		throw new IllegalAccessError("Not supported");
	}

	@Override
	public void setSourceStart(int sourceStart) {
		throw new IllegalAccessError("Not supported");
	}

}
