package org.maziarz.yiiclipse;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

public class YiiclipseIncrementalProjectBuilder extends IncrementalProjectBuilder {

	public static final String BUILDER_ID = "org.maziarz.yiiclipse.builder";
	
	@Override
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
			throws CoreException {
		return null;
	}

}
