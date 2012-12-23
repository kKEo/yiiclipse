package org.maziarz.yiiclipse.wizards;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IAccessRule;
import org.eclipse.dltk.core.IBuildpathAttribute;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.IOverwriteQuery;
import org.eclipse.ui.wizards.datatransfer.ImportOperation;
import org.maziarz.yiiclipse.YiiclipseBundle;
import org.maziarz.yiiclipse.datatransfer.TarEntry;
import org.maziarz.yiiclipse.datatransfer.TarException;
import org.maziarz.yiiclipse.datatransfer.TarFile;
import org.maziarz.yiiclipse.datatransfer.TarHandler;
import org.maziarz.yiiclipse.preferences.YiiclipsePreferenceConstants;

public class WizardNewYiiProjectConfigurationPage extends WizardPage implements IOverwriteQuery {

	private String frameworkLocation;

	protected WizardNewYiiProjectConfigurationPage(String pageName) {
		super(pageName);
		yiiVersionSizes.put("yii-1.1.13-RC.0f7bee", "3921070");
		yiiVersionSizes.put("yii-1.1.12.b600af", "3829049");
		yiiVersionSizes.put("yii-1.1.11.58da45", "3829033");
		yiiVersionSizes.put("yii-1.0.12.r1898", "1898464");

	}

	public String getFrameworkLocation() {
		return frameworkLocation;
	}

	private Map<Button, String> yiiVersionButtons;
	Map<String, String> yiiVersionSizes = new HashMap<String, String>();

	private boolean isDownloadRequired;

	private Map<Button, String> yiiApplication;

	private Text downloadDestination;

	private File tmpDownloadedFile;
	private Button removeYiiliteButton;
	private String errorMessage;

	@Override
	public void createControl(Composite parent) {

		Composite container = new Composite(parent, SWT.None);
		initializeDialogUnits(parent);

		container.setLayout(new GridLayout());

		final Group group = new Group(container, SWT.None);
		group.setLayoutData(new GridData(GridData.FILL_BOTH));
		group.setLayout(new GridLayout(3, false));
		group.setText("Yii framework location");

		final Button useLocalButton = new Button(group, SWT.RADIO);
		useLocalButton.setText("Use local");

		GridData gData = new GridData();
		gData.horizontalSpan = 3;
		useLocalButton.setLayoutData(gData);
		useLocalButton.setSelection(true);

		final Label localDirLabel = new Label(group, SWT.NONE);
		localDirLabel.setText("Path: ");
		final Text localDirText = new Text(group, SWT.BORDER);
		localDirText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		localDirText.setEditable(false);

		final Button localSelectDir = new Button(group, SWT.PUSH);
		localSelectDir.setText("Browse");

		final IPreferenceStore preferences = YiiclipseBundle.getDefault().getPreferenceStore();

		final String defaultLoc = preferences.getString(YiiclipsePreferenceConstants.FRAMEWORK_PATH);
		if (defaultLoc.isEmpty() == false) {
			localDirText.setText(defaultLoc);
			frameworkLocation = defaultLoc;
		}

		localSelectDir.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				DirectoryDialog directoryDialog = new DirectoryDialog(group.getShell());

				directoryDialog.setText("Choose location of yii framework copy.");
				directoryDialog.setFilterPath(defaultLoc);
				directoryDialog.setMessage("Please select a directory and click OK");

				String dir = directoryDialog.open();
				if (dir != null) {
					preferences.putValue(YiiclipsePreferenceConstants.FRAMEWORK_PATH, dir);
					localDirText.setText(dir);
					frameworkLocation = dir;
					YiiclipseBundle.debug("LocalSelectDir: " + dir);
				}
			}
		});

		final Button downloadButton = new Button(group, SWT.RADIO);
		downloadButton.setText("Download");

		gData = new GridData();
		gData.horizontalSpan = 3;
		downloadButton.setLayoutData(gData);
		downloadButton.setEnabled(true);

		final Group chooseVersionGroup = new Group(group, SWT.None);
		gData = new GridData(SWT.FILL, SWT.None, true, false);
		gData.horizontalSpan = 3;
		chooseVersionGroup.setLayoutData(gData);
		chooseVersionGroup.setLayout(new RowLayout());
		chooseVersionGroup.setText("Yii Framework version");

		yiiVersionButtons = new HashMap<Button, String>();
		final Button version11Dev = new Button(chooseVersionGroup, SWT.RADIO);
		version11Dev.setText("1.1.13-RC");
		yiiVersionButtons.put(version11Dev, "yii-1.1.13-RC.0f7bee");

		final Button version117 = new Button(chooseVersionGroup, SWT.RADIO);
		version117.setText("1.1.12");
		version117.setSelection(true);
		yiiVersionButtons.put(version117, "yii-1.1.12.b600af");

		final Button version116 = new Button(chooseVersionGroup, SWT.RADIO);
		version116.setText("1.1.11");
		yiiVersionButtons.put(version116, "yii-1.1.11.58da45");

		final Button version1012 = new Button(chooseVersionGroup, SWT.RADIO);
		version1012.setText("1.0.12");
		yiiVersionButtons.put(version1012, "yii-1.0.12.r1898");

		final Label destDirLabel = new Label(group, SWT.NONE);
		destDirLabel.setText("Extract to: ");
		final Text destDirText = new Text(group, SWT.BORDER);
		downloadDestination = destDirText;

		destDirText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				validatePage();
			}
		});

		destDirText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		final Button destSelectDir = new Button(group, SWT.PUSH);
		destSelectDir.setText("Browse");

		if (defaultLoc.isEmpty() == false) {
			destDirText.setText(defaultLoc);
			frameworkLocation = defaultLoc;
		}

		destSelectDir.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				DirectoryDialog directoryDialog = new DirectoryDialog(group.getShell());
				directoryDialog.setFilterPath(frameworkLocation);
				directoryDialog.setMessage("Please select a directory where framework will be extracted.");

				String dir = directoryDialog.open();
				if (dir != null) {
					preferences.putValue(YiiclipsePreferenceConstants.FRAMEWORK_PATH, dir);
					destDirText.setText(dir);
					frameworkLocation = dir;
					YiiclipseBundle.debug("DestSelectDir: " + dir);
				}
			}
		});

		removeYiiliteButton = new Button(group, SWT.CHECK);
		removeYiiliteButton.setText("Remove yiilite.php file from buildpath");
		removeYiiliteButton.setToolTipText("Phisically deletes yiilite.php from downloaded framework.");
		removeYiiliteButton.setSelection(true);
		GridData layoutData = new GridData(GridData.GRAB_HORIZONTAL);
		layoutData.horizontalSpan = 3;
		removeYiiliteButton.setLayoutData(layoutData);
		
		final Group chooseApplicationGroup = new Group(container, SWT.None);
		gData = new GridData(SWT.FILL, SWT.None, true, false);
		gData.horizontalSpan = 3;
		chooseApplicationGroup.setLayoutData(gData);
		chooseApplicationGroup.setLayout(new RowLayout());
		chooseApplicationGroup.setText("Initialize project with");

		yiiApplication = new HashMap<Button, String>();

		final Button yiiAppNone = new Button(chooseApplicationGroup, SWT.RADIO);
		yiiAppNone.setText("None");
		yiiApplication.put(yiiAppNone, "none");

		final Button yiiAppHelloWorld = new Button(chooseApplicationGroup, SWT.RADIO);
		yiiAppHelloWorld.setText("Hello World");
		yiiAppHelloWorld.setSelection(true);
		yiiApplication.put(yiiAppHelloWorld, "helloworld");

		final Button yiiAppBlog = new Button(chooseApplicationGroup, SWT.RADIO);
		yiiAppBlog.setText("Blog");
		yiiApplication.put(yiiAppBlog, "blog");

		final Button yiiAppHangman = new Button(chooseApplicationGroup, SWT.RADIO);
		yiiAppHangman.setText("Hangman");
		yiiApplication.put(yiiAppHangman, "hangman");

		final Button yiiAppPhonebook = new Button(chooseApplicationGroup, SWT.RADIO);
		yiiAppPhonebook.setText("Phonebook");
		yiiApplication.put(yiiAppPhonebook, "phonebook");

		downloadButton.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(MouseEvent e) {
				chooseVersionGroup.setEnabled(true);
				version11Dev.setEnabled(true);
				version117.setEnabled(true);
				version116.setEnabled(true);
				version1012.setEnabled(true);
				destDirLabel.setEnabled(true);
				destDirText.setEnabled(true);
				destSelectDir.setEnabled(true);
				removeYiiliteButton.setEnabled(true);
				localDirLabel.setEnabled(false);
				localDirText.setEnabled(false);
				localSelectDir.setEnabled(false);
				toogleGroup(chooseApplicationGroup, true);

				isDownloadRequired = true;

				validatePage();
			}
		});

		useLocalButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				chooseVersionGroup.setEnabled(false);
				version11Dev.setEnabled(false);
				version117.setEnabled(false);
				version116.setEnabled(false);
				version1012.setEnabled(false);
				destDirLabel.setEnabled(false);
				destDirText.setEnabled(false);
				destSelectDir.setEnabled(false);
				removeYiiliteButton.setEnabled(false);
				localDirLabel.setEnabled(true);
				localDirText.setEnabled(true);
				localSelectDir.setEnabled(true);

				toogleGroup(chooseApplicationGroup, false);

				isDownloadRequired = false;

				validatePage();
			}
		});

		chooseVersionGroup.setEnabled(false);
		version11Dev.setEnabled(false);
		version117.setEnabled(false);
		version116.setEnabled(false);
		version1012.setEnabled(false);
		destDirLabel.setEnabled(false);
		destDirText.setEnabled(false);
		destSelectDir.setEnabled(false);
		removeYiiliteButton.setEnabled(false);
		localDirLabel.setEnabled(true);
		localDirText.setEnabled(true);
		localSelectDir.setEnabled(true);

		toogleGroup(chooseApplicationGroup, false);

		// Assign form to the wizard page
		setControl(container);
		Dialog.applyDialogFont(container);

		setPageComplete(validatePage());
	}

	private void toogleGroup(Group containerGroup, boolean isEnable) {
		for (Control c : containerGroup.getChildren()) {
			c.setEnabled(isEnable);
		}
		containerGroup.setEnabled(isEnable);
	}

	protected boolean validatePage() {

		boolean result = true;

		if (this.isDownloadRequired()) {
			// String test = downloadDestination.getText();

			if (downloadDestination != null && downloadDestination.getText().isEmpty()) {
				setErrorMessage("Extracting target location have to be set.");
				result = false;
			} else if (new File(downloadDestination.getText()).exists() == false) {
				setErrorMessage("Extracting target location must be exist.");
				result = false;
			} else if (new File(downloadDestination.getText()).isDirectory() == false) {
				setErrorMessage("Extracting target location must be valid directory.");
				result = false;
			} else {
				setErrorMessage(null);
				result = true;
			}
		} else {
			setErrorMessage(null);
		}

		setPageComplete(result);
		return result;
	}

	public boolean isDownloadRequired() {
		return this.isDownloadRequired;
	}

	public String getVersionForDownload() {
		for (Entry<Button, String> entry : yiiVersionButtons.entrySet()) {
			if (entry.getKey().getSelection()) {
				return entry.getValue();
			}
		}
		return null;
	}

	public String getApplicationToBeInstalled() {
		for (Entry<Button, String> entry : yiiApplication.entrySet()) {
			if (entry.getKey().getSelection()) {
				return entry.getValue();
			}
		}
		return null;
	}

	public void performFinish(IProject project) {

		String targetLocation = this.getFrameworkLocation();

		/**
		 * Add yii framework location to the project
		 */
		boolean isFrameworkReady = false;

		if (this.isDownloadRequired()) {
			final String version = this.getVersionForDownload();

			try {
				tmpDownloadedFile = File.createTempFile("yii-", ".tar.gz");
				tmpDownloadedFile.deleteOnExit();

				IRunnableWithProgress downloadOperation = new IRunnableWithProgress() {

					@Override
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

						monitor.setTaskName("Downloading yii framework");
						int total = Integer.parseInt(yiiVersionSizes.get(version));

						String urlString = getFrameworkRemoteHost() + version + ".tar.gz";
						monitor.beginTask("Downloading file: " + urlString, total);

						try {
							URL url = new URL(urlString);

							YiiclipseBundle.debug("Tmp download location: " + tmpDownloadedFile);

							OutputStream outStream = new BufferedOutputStream(new FileOutputStream(tmpDownloadedFile));
							URLConnection connection = url.openConnection();
							InputStream is = connection.getInputStream();

							int byteRead, byteWritten = 0;

							byte[] buf = new byte[8196];
							while ((byteRead = is.read(buf)) != -1) {
								outStream.write(buf, 0, byteRead);
								byteWritten += byteRead;

								monitor.worked(byteRead);
								monitor.subTask(byteWritten / 1000 + "kb downloaded");
							}

							is.close();
							outStream.close();

						} catch (MalformedURLException e) {
							errorMessage = e.getMessage();
							YiiclipseBundle.logError("MalformedURLException: " + e.getMessage(), e);
						} catch (IOException e) {
							errorMessage = e.getMessage();
							YiiclipseBundle.logError(e.getMessage(), e);
						}
					}
				};

				try {
					getContainer().run(true, true, downloadOperation);
				} catch (InvocationTargetException e) {
					Throwable t = e.getTargetException();
					if (t instanceof ExecutionException && t.getCause() instanceof CoreException) {
						YiiclipseBundle.logError("Downloading yii framework: wielka dupa: " + e.getMessage());
					}
				} catch (InterruptedException e) {
				}

				// Extract framework folder
				TarFile yiiTarArchive = new TarFile(tmpDownloadedFile);
				TarHandler th = new TarHandler(yiiTarArchive);
				TarEntry frameworkEntry = th.getEntry(version + "/framework/");

				File destFolder = new File(targetLocation);
				for (TarEntry entry : th.getChildren(frameworkEntry)) {
					th.extractRecursively(entry, destFolder);
				}

				yiiTarArchive.close();

				if (removeYiiliteButton.getSelection()) {
					File yiilite = new File(destFolder.getCanonicalPath() + File.separator + "yiilite.php");
					if (yiilite.exists()) {
						yiilite.delete();
					}
				}

				isFrameworkReady = true;

			} catch (IOException e1) {
				if (errorMessage == null) {
					errorMessage = e1.getMessage();
				}
				YiiclipseBundle.logError(e1.getMessage());
			} catch (TarException e) {
				if (errorMessage == null) {
					errorMessage = e.getMessage();
				}
				YiiclipseBundle.logError(e.getMessage());
			}
		} else {

			this.createYiiProjectDirectoryStructure(project);

			isFrameworkReady = true;
		}

		/**
		 * Add yii framework to the path
		 */
		if (isFrameworkReady && this.getFrameworkLocation() != null && "".equals(this.getFrameworkLocation()) == false) {

			IScriptProject scriptProject = DLTKCore.create(project.getProject());
			if (scriptProject != null) {
				List<IBuildpathEntry> entries = new LinkedList<IBuildpathEntry>();

				IPath yiiFrameworkPath = new Path("org.eclipse.dltk.core.environment.localEnvironment/:",
						this.getFrameworkLocation());

				IBuildpathEntry entry = DLTKCore.newBuiltinEntry(yiiFrameworkPath, new IAccessRule[0],
						new IBuildpathAttribute[0], new IPath[0], new IPath[0], false, true);

				entries.add(entry);

				try {
					IBuildpathEntry[] rawBuildpath = scriptProject.getRawBuildpath();
					Set<IBuildpathEntry> newRawBuildpath = new HashSet<IBuildpathEntry>();
					for (IBuildpathEntry buildpathEntry : rawBuildpath) {
						newRawBuildpath.add(buildpathEntry);
					}
					for (IBuildpathEntry buildpathEntry : entries) {
						newRawBuildpath.add(buildpathEntry);
					}
					scriptProject.setRawBuildpath(newRawBuildpath.toArray(new IBuildpathEntry[newRawBuildpath.size()]), null);
				} catch (ModelException e) {
					YiiclipseBundle.logError(e.getMessage(), e);
				}
			}
		} else {
			if (errorMessage == null) {
				errorMessage = "Framework not found in specified location: " + this.getFrameworkLocation();
			}

			MessageDialog.openError(getShell(), "Initialization error", " Problem with initializing yii application: "
					+ errorMessage);
			return;
		}

		/**
		 * Install initial application
		 */
		String application = getApplicationToBeInstalled();
		String version = this.getVersionForDownload();

		if (this.isDownloadRequired() && !application.equals("none")) {
			try {
				TarFile yiiTarArchive = new TarFile(tmpDownloadedFile);
				TarHandler importStructureProvider = new TarHandler(yiiTarArchive);
				TarEntry frameworkEntry = importStructureProvider.getEntry(version + "/demos/" + application + "/");

				ImportOperation operation = new ImportOperation(project.getFullPath(), frameworkEntry, importStructureProvider,
						this, null);

				operation.setContext(getShell());
				operation.setCreateContainerStructure(false);
				operation.setOverwriteResources(true);

				try {
					getContainer().run(true, true, operation);
				} catch (InterruptedException e) {
				} catch (InvocationTargetException e) {
					YiiclipseBundle.logError(e.getTargetException().getMessage());
				}

				IStatus status = operation.getStatus();
				if (!status.isOK()) {
					ErrorDialog.openError(getContainer().getShell(), "Problem with initializing application", null, status);
				}

				yiiTarArchive.close();

			} catch (Exception e) {
				YiiclipseBundle.logError(e.getMessage());
			}
		}

	}

	@Override
	public String queryOverwrite(String pathString) {
		return null;
	}

	private String getFrameworkRemoteHost() {

		String remoteHost = null;

		if (System.getProperty("ryfl") != null) {
			remoteHost = System.getProperty("ryfl");

			if (!remoteHost.endsWith("/")) {
				remoteHost += "/";
			}

			if (!remoteHost.startsWith("http")) {
				remoteHost = "file://" + remoteHost;
			}

		} else {
			remoteHost = "http://yii.googlecode.com/files/";
		}

		YiiclipseBundle.debug("Remote Yii Framework location: " + remoteHost);

		return remoteHost;

	}

	private void createYiiProjectDirectoryStructure(IProject project) {
		YiiProjectWorkspaceHelper.INSTANCE.buildBasicProjectStructure(getContainer(), project);
	}
}
