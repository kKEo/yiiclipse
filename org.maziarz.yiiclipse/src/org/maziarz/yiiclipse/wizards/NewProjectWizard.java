package org.maziarz.yiiclipse.wizards;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.ide.undo.CreateProjectOperation;
import org.eclipse.ui.ide.undo.WorkspaceUndoUtil;
import org.eclipse.ui.part.ISetSelectionTarget;
import org.maziarz.yiiclipse.YiiclipseBundle;
import org.maziarz.yiiclipse.YiiclipseBundleMessages;
import org.maziarz.yiiclipse.YiiclipseNature;

public class NewProjectWizard extends Wizard implements INewWizard, IExecutableExtension {

	private WizardNewProjectCreationPage page;
	private WizardNewYiiProjectConfigurationPage projectConfigurationPage;

	private IProject newProject;
	private IWorkbench workbench;

	public NewProjectWizard() {
		super();
		this.setWindowTitle("New Yii Project");
	}

	@Override
	public void addPages() {
		page = new WizardNewProjectCreationPage("WizardNewProjectPage") {

			@Override
			public void createControl(Composite parent) {
				super.createControl(parent);
				setPageComplete(validatePage());
			}

			@Override
			protected boolean validatePage() {
				if (!super.validatePage()) {
					return false;
				}

				String name = getProjectName();
				if (name.indexOf('%') >= 0) {
					setErrorMessage("Invalid project name");
				}

				return true;
			}
		};

		page.setTitle(YiiclipseBundleMessages.getString("WizardNewProjectPage_title"));
		page.setDescription(YiiclipseBundleMessages.getString("WizardNewProjectPage_description"));
		page.setInitialProjectName(YiiclipseBundleMessages.getString("WizardNewProjectPage_defaultProjectName"));

		addPage(page);

		projectConfigurationPage = new WizardNewYiiProjectConfigurationPage("Yii project initialization");
		projectConfigurationPage.setTitle(YiiclipseBundleMessages.getString("WizardNewProjectPage_title"));
		projectConfigurationPage.setDescription(YiiclipseBundleMessages.getString("WizardNewProjectPage_description"));

		addPage(projectConfigurationPage);
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbench = workbench;
		setNeedsProgressMonitor(true);
		setWindowTitle("Yii Framework Project Creation Wizard");

	}

	@Override
	public boolean performFinish() {

		if (!projectConfigurationPage.validatePage()) {
			return false;
		}

		IProject project = this.getNewPHPProject();

		projectConfigurationPage.performFinish(project);

		try {
			toggleYiiNature(project);
		} catch (CoreException e) {
			YiiclipseBundle.logError(e.getMessage());
		}

		if (newProject == null) {
			return false;
		}

		IWorkingSet[] workingSets = page.getSelectedWorkingSets();
		this.workbench.getWorkingSetManager().addToWorkingSets(newProject, workingSets);

		selectAndReveal(newProject);

		return true;
	}

	private IProject getNewPHPProject() {

		if (newProject != null) {
			return newProject;
		}

		/**
		 * Prepare project description
		 */
		IProject newProjectHandle = page.getProjectHandle();

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IProjectDescription description = workspace.newProjectDescription(newProjectHandle.getName());

		if (page.useDefaults() == false) {
			URI location = page.getLocationURI();
			description.setLocationURI(location);
		}

		/**
		 * Creates new project
		 */
		IRunnableWithProgress op = new IRunnableWithProgress() {

			@Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

				CreateProjectOperation op = new CreateProjectOperation(description, "New Project Creation");

				try {
					op.execute(monitor, WorkspaceUndoUtil.getUIInfoAdapter(getShell()));
				} catch (ExecutionException e) {
					YiiclipseBundle.logError(e.getMessage());
				}

			}
		};

		/**
		 * Runs the new project creation operation
		 */
		try {
			getContainer().run(true, true, op);
		} catch (InterruptedException e) {
			return null;
		} catch (InvocationTargetException e) {
			Throwable t = e.getTargetException();
			if (t instanceof ExecutionException && t.getCause() instanceof CoreException) {
				YiiclipseBundle.logError("NewProjectWizard.createNewProject(): wielka dupa: " + e.getMessage());
			}
			return null;
		}

		/**
		 * Add PHPNature to the newly created project
		 */
		try {
			IProjectDescription desc = null;

			String[] natureIds = new String[] { "org.eclipse.php.core.PHPNature" };
			if (null != natureIds) {

				desc = newProjectHandle.getDescription();

				desc.setNatureIds(natureIds);
				newProjectHandle.setDescription(desc, null);
			}
		} catch (CoreException e) {
			YiiclipseBundle.logError("Problem with assiging PHPNature to the project. Details: " + e.getMessage());
		}

		this.newProject = newProjectHandle;

		return this.newProject;
	}

	protected void selectAndReveal(IResource resource) {

		IWorkbenchWindow window = this.workbench.getActiveWorkbenchWindow();

		if (window == null || resource == null) {
			return;
		}

		IWorkbenchPage page = window.getActivePage();

		if (page == null) {
			return;
		}

		/**
		 * Get all the views and editor parts
		 */
		List<IWorkbenchPart> parts = new ArrayList<IWorkbenchPart>();

		IWorkbenchPartReference refs[] = page.getViewReferences();
		for (int i = 0; i < refs.length; i++) {
			IWorkbenchPart part = refs[i].getPart(false);
			if (part != null) {
				parts.add(part);
			}
		}

		refs = page.getEditorReferences();
		for (int i = 0; i < refs.length; i++) {
			if (refs[i].getPart(false) != null) {
				parts.add(refs[i].getPart(false));
			}
		}

		final ISelection selection = new StructuredSelection(resource);

		Iterator<IWorkbenchPart> itr = parts.iterator();

		while (itr.hasNext()) {
			IWorkbenchPart part = (IWorkbenchPart) itr.next();

			/**
			 * Get the part's ISetSelectionTarget implementation
			 */
			ISetSelectionTarget target = null;
			if (part instanceof ISetSelectionTarget) {
				target = (ISetSelectionTarget) part;
			} else {
				target = (ISetSelectionTarget) part.getAdapter(ISetSelectionTarget.class);
			}

			if (target != null) {
				/**
				 * Select and reveal resource
				 */
				final ISetSelectionTarget finalTarget = target;
				window.getShell().getDisplay().asyncExec(new Runnable() {

					@Override
					public void run() {
						finalTarget.selectReveal(selection);
					}
				});
			}
		}

	}

	@Override
	public void setInitializationData(IConfigurationElement config, String propertyName, Object data) throws CoreException {
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
