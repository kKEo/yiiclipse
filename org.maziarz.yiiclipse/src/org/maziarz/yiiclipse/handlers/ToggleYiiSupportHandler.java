package org.maziarz.yiiclipse.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.maziarz.yiiclipse.YiiclipseBundle;
import org.maziarz.yiiclipse.YiiclipseNature;

public class ToggleYiiSupportHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		ISelection selection = HandlerUtil.getCurrentSelection(event);

		if (selection instanceof IStructuredSelection) {

			for (Object o : ((IStructuredSelection) selection).toArray()) {
				if (o instanceof IScriptProject) {
					final IScriptProject element = (IScriptProject) o;

					IProject project = null;

					if (element instanceof IProject) {
						project = (IProject) element;
					} else if (element instanceof IAdaptable) {
						project = (IProject) ((IAdaptable) element).getAdapter(IProject.class);
					}
					if (project != null) {
						try {
							toggleYiiNature(project);
						} catch (CoreException e) {
							YiiclipseBundle.logError(e.getMessage());
						}
					}
				}
			}

		}

		return null;
	}

	private void toggleYiiNature(IProject project) throws CoreException {

		IProjectDescription description = project.getDescription();
		String[] natures = description.getNatureIds();

		for (int i = 0; i < natures.length; ++i) {
			if (YiiclipseNature.NATURE_ID.equals(natures[i])) {
				// Remove the nature
				String[] newNatures = new String[natures.length - 1];

				System.arraycopy(natures, 0, newNatures, 0, i);
				System.arraycopy(natures, i + 1, newNatures, i, natures.length - i - 1);
				description.setNatureIds(newNatures);
				project.setDescription(description, null);
				return;
			}
		}

		// Add the nature
		String[] newNatures = new String[natures.length + 1];
		System.arraycopy(natures, 0, newNatures, 1, natures.length);
		newNatures[0] = YiiclipseNature.NATURE_ID;
		description.setNatureIds(newNatures);
		project.setDescription(description, null);
	}

}
