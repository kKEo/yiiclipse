package org.maziarz.yiiclipse.wizards;

import java.io.ByteArrayInputStream;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.undo.CreateFileOperation;
import org.eclipse.ui.ide.undo.CreateFolderOperation;
import org.eclipse.ui.ide.undo.WorkspaceUndoUtil;

public class YiiProjectWorkspaceHelper {

	private IRunnableContext runnableContext;

	public static YiiProjectWorkspaceHelper INSTANCE = new YiiProjectWorkspaceHelper();
	
	
	public void buildBasicProjectStructure(IRunnableContext context, IProject project) {

		this.runnableContext = context;

		IFolder folder;

		folder = this.createFolder(project, "assets");

		putOtherWritePermission(folder);

		this.createFolder(project, "css");
		this.createFolder(project, "images");
		this.createFolder(project, "protected");
		this.createFolder(project, "protected/components");
		this.createFolder(project, "protected/config");
		this.createFolder(project, "protected/controllers");
		this.createFolder(project, "protected/data");

		folder = this.createFolder(project, "protected/runtime");
		putOtherWritePermission(folder);

		this.createFolder(project, "protected/views");
		this.createFolder(project, "protected/models");
		this.createFolder(project, "protected/views/layouts");

	}

	private void putOtherWritePermission(IFolder folder) {
		int permissions = this.fetchPermissions(folder);
		int other_write = permissions | EFS.ATTRIBUTE_OTHER_WRITE;
		this.putPermissions(folder, other_write);
	}

	protected IFolder createFolder(IContainer container, String folderName) {
		IPath folderPath = container.getFullPath().append(folderName);
		IWorkspaceRoot workspaceRoot = container.getWorkspace().getRoot();
		IFolder folderHandle = workspaceRoot.getFolder(folderPath);

		IRunnableWithProgress op = this.createFolderOperation(folderHandle);
		performOperationWithBusyCursor(op, "Creating folder: " + folderName);

		return folderHandle;
	}
	
	public IFile createFile(IContainer container, String fileName) {
		
		IPath filePath =container.getFullPath().append(fileName);
		IWorkspaceRoot workspaceRoot = container.getWorkspace().getRoot();
		IFile fileHandle = workspaceRoot.getFile(filePath);
		
		IRunnableWithProgress op = this.createFileOperation(fileHandle);
		
		try {
			PlatformUI.getWorkbench().getProgressService().busyCursorWhile(op);
		} catch (Exception e) {
			MessageDialog.openError(getShell(), "Creating file: "+fileName + " Error", e.getMessage());
			e.printStackTrace();
		}
		
		return fileHandle;
	}

	private IRunnableWithProgress createFileOperation(final IFile fileHandle) {
		return new IRunnableWithProgress() {
			
			@Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				CreateFileOperation op = new CreateFileOperation(fileHandle, null, new ByteArrayInputStream("".getBytes()), "Creating file");
				
				try {
					op.execute(monitor, WorkspaceUndoUtil.getUIInfoAdapter(getShell()));
				} catch (ExecutionException e) {
					throw new IllegalStateException(e);
				}
			}
		};
	}

	protected IRunnableWithProgress createFolderOperation(final IFolder folderHandle) {

		return new IRunnableWithProgress() {

			@Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				CreateFolderOperation op = new CreateFolderOperation(folderHandle, null, "Creating folder");
				try {
					op.execute(monitor, WorkspaceUndoUtil.getUIInfoAdapter(getShell()));
				} catch (Exception e) {
					throw new IllegalStateException(e);
				}
			}
		};

	}

	protected void performOperationWithBusyCursor(IRunnableWithProgress runnable, String title) {

		try {
			// PlatformUI.getWorkbench().getProgressService().busyCursorWhile(runnable);
			runnableContext.run(true, false, runnable);
		} catch (InterruptedException e) {
			return;
		} catch (InvocationTargetException e) {
			MessageDialog.openError(getShell(), title + " Error", e.getTargetException().getMessage());
			return;
		}
	}

	public Shell getShell() {
		if (runnableContext instanceof IWizardContainer) {
			return ((IWizardContainer) runnableContext).getShell();
		}
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
	}

	private int fetchPermissions(IResource resource) {
		IFileStore store = null;
		try {
			store = EFS.getStore(resource.getLocationURI());
		} catch (CoreException e) {
			return 0;
		}
		IFileInfo info = store.fetchInfo();
		int permissions = 0;
		if (info.exists()) {
			permissions |= info.getAttribute(EFS.ATTRIBUTE_OWNER_READ) ? EFS.ATTRIBUTE_OWNER_READ : 0;
			permissions |= info.getAttribute(EFS.ATTRIBUTE_OWNER_WRITE) ? EFS.ATTRIBUTE_OWNER_WRITE : 0;
			permissions |= info.getAttribute(EFS.ATTRIBUTE_OWNER_EXECUTE) ? EFS.ATTRIBUTE_OWNER_EXECUTE : 0;
			permissions |= info.getAttribute(EFS.ATTRIBUTE_GROUP_READ) ? EFS.ATTRIBUTE_GROUP_READ : 0;
			permissions |= info.getAttribute(EFS.ATTRIBUTE_GROUP_WRITE) ? EFS.ATTRIBUTE_GROUP_WRITE : 0;
			permissions |= info.getAttribute(EFS.ATTRIBUTE_GROUP_EXECUTE) ? EFS.ATTRIBUTE_GROUP_EXECUTE : 0;
			permissions |= info.getAttribute(EFS.ATTRIBUTE_OTHER_READ) ? EFS.ATTRIBUTE_OTHER_READ : 0;
			permissions |= info.getAttribute(EFS.ATTRIBUTE_OTHER_WRITE) ? EFS.ATTRIBUTE_OTHER_WRITE : 0;
			permissions |= info.getAttribute(EFS.ATTRIBUTE_OTHER_EXECUTE) ? EFS.ATTRIBUTE_OTHER_EXECUTE : 0;
			permissions |= info.getAttribute(EFS.ATTRIBUTE_IMMUTABLE) ? EFS.ATTRIBUTE_IMMUTABLE : 0;
		}
		return permissions;
	}

	private boolean putPermissions(IResource resource, int permissions) {
		IFileStore store = null;
		try {
			store = EFS.getStore(resource.getLocationURI());
		} catch (CoreException e) {
			return false;
		}
		IFileInfo fileInfo = store.fetchInfo();
		if (!fileInfo.exists())
			return false;
		fileInfo.setAttribute(EFS.ATTRIBUTE_OWNER_READ, (permissions & EFS.ATTRIBUTE_OWNER_READ) != 0);
		fileInfo.setAttribute(EFS.ATTRIBUTE_OWNER_WRITE, (permissions & EFS.ATTRIBUTE_OWNER_WRITE) != 0);
		fileInfo.setAttribute(EFS.ATTRIBUTE_OWNER_EXECUTE, (permissions & EFS.ATTRIBUTE_OWNER_EXECUTE) != 0);
		fileInfo.setAttribute(EFS.ATTRIBUTE_GROUP_READ, (permissions & EFS.ATTRIBUTE_GROUP_READ) != 0);
		fileInfo.setAttribute(EFS.ATTRIBUTE_GROUP_WRITE, (permissions & EFS.ATTRIBUTE_GROUP_WRITE) != 0);
		fileInfo.setAttribute(EFS.ATTRIBUTE_GROUP_EXECUTE, (permissions & EFS.ATTRIBUTE_GROUP_EXECUTE) != 0);
		fileInfo.setAttribute(EFS.ATTRIBUTE_OTHER_READ, (permissions & EFS.ATTRIBUTE_OTHER_READ) != 0);
		fileInfo.setAttribute(EFS.ATTRIBUTE_OTHER_WRITE, (permissions & EFS.ATTRIBUTE_OTHER_WRITE) != 0);
		fileInfo.setAttribute(EFS.ATTRIBUTE_OTHER_EXECUTE, (permissions & EFS.ATTRIBUTE_OTHER_EXECUTE) != 0);
		fileInfo.setAttribute(EFS.ATTRIBUTE_IMMUTABLE, (permissions & EFS.ATTRIBUTE_IMMUTABLE) != 0);
		try {
			store.putInfo(fileInfo, EFS.SET_ATTRIBUTES, null);
		} catch (CoreException e) {
			return false;
		}
		return true;
	}

}
