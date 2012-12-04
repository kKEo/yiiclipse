/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Red Hat, Inc - Was TarFileStructureProvider, performed changes from 
 *     IImportStructureProvider to ILeveledImportStructureProvider
 *******************************************************************************/
package org.maziarz.yiiclipse.datatransfer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.ui.wizards.datatransfer.IImportStructureProvider;

import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * This class provides information regarding the context structure and content of specified tar file entry objects.
 */
public class TarHandler implements IImportStructureProvider{

	private TarFile tarFile;
	private TarEntry root = new TarEntry("/");//$NON-NLS-1$
	private Map<TarEntry, List<TarEntry>> children;
	private Map<IPath, TarEntry> directoryEntryCache = new HashMap<IPath, TarEntry>();
	private int stripLevel;

	/**
	 * Creates a <code>TarFileStructureProvider</code>, which will operate on the passed tar file.
	 * 
	 * @param sourceFile
	 *            the source TarFile
	 */
	public TarHandler(TarFile sourceFile) {
		super();
		tarFile = sourceFile;
		root.setFileType(TarEntry.DIRECTORY);
	}

	/**
	 * Creates a new container tar entry with the specified name, if it has not already been created. If the parent of the given element does not
	 * already exist it will be recursively created as well.
	 * 
	 * @param pathname
	 *            The path representing the container
	 * @return The element represented by this pathname (it may have already existed)
	 */
	protected TarEntry createContainer(IPath pathname) {
		TarEntry existingEntry = (TarEntry) directoryEntryCache.get(pathname);
		if (existingEntry != null) {
			return existingEntry;
		}

		TarEntry parent;
		if (pathname.segmentCount() == 1) {
			parent = root;
		} else {
			parent = createContainer(pathname.removeLastSegments(1));
		}
		TarEntry newEntry = new TarEntry(pathname.toString());
		newEntry.setFileType(TarEntry.DIRECTORY);
		directoryEntryCache.put(pathname, newEntry);
		List<TarEntry> childList = new ArrayList<TarEntry>();
		children.put(newEntry, childList);

		List<TarEntry> parentChildList = children.get(parent);
		parentChildList.add(newEntry);
		return newEntry;
	}

	/**
	 * Creates a new tar file entry with the specified name.
	 */
	protected void createFile(TarEntry entry) {
		IPath pathname = new Path(entry.getName());
		TarEntry parent;
		if (pathname.segmentCount() == 1) {
			parent = root;
		} else {
			parent = directoryEntryCache.get(pathname.removeLastSegments(1));
		}

		List<TarEntry> childList = children.get(parent);
		childList.add(entry);
	}

	public List<TarEntry> getChildren(TarEntry entry) {
		if (children == null) {
			initialize();
		}
		return children.get(entry);
	}

	public InputStream getContents(TarEntry element) {
		try {
			return tarFile.getInputStream(element);
		} catch (TarException e) {
			throw new DataTransferException(e.getLocalizedMessage(), e);
//			YiiclipseBundle.logError(e.getLocalizedMessage(), e);
		} catch (IOException e) {
			throw new DataTransferException(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * Returns the resource attributes for this file.
	 * 
	 * @param element
	 * @return the attributes of the file
	 */
	public ResourceAttributes getResourceAttributes(TarEntry entry) {
		ResourceAttributes attributes = new ResourceAttributes();
		attributes.setExecutable((entry.getMode() & 0100) != 0);
		attributes.setReadOnly((entry.getMode() & 0200) == 0);
		return attributes;
	}

	public String getFullPath(TarEntry entry) {
		return stripPath(entry.getName());
	}

	public String getLabel(TarEntry entry) {
		if (entry.equals(root)) {
			return entry.getName();
		}
		return stripPath(new Path(entry.getName()).lastSegment());
	}

	/**
	 * Returns the entry that this importer uses as the root sentinel.
	 * 
	 * @return TarEntry entry
	 */
	public TarEntry getRoot() {
		return root;
	}

	/**
	 * Returns the tar file that this provider provides structure for.
	 * 
	 * @return TarFile file
	 */
	public TarFile getTarFile() {
		return tarFile;
	}

	public boolean closeArchive() {
		try {
			if (getTarFile() != null) {
				getTarFile().close();
			}
		} catch (IOException e) {
			// ignore
		}
		return true;
	}

	/**
	 * Initializes this object's children table based on the contents of the specified source file.
	 */
	protected void initialize() {
		children = new HashMap<TarEntry, List<TarEntry>>(1000);

		children.put(root, new ArrayList<TarEntry>());
		Enumeration<TarEntry> entries = tarFile.entries();
		while (entries.hasMoreElements()) {
			TarEntry entry = (TarEntry) entries.nextElement();
			IPath path = new Path(entry.getName()).addTrailingSeparator();

			if (entry.getFileType() == TarEntry.DIRECTORY) {
				createContainer(path);
			} else {
				// Ensure the container structure for all levels above this is initialized
				// Once we hit a higher-level container that's already added we need go no further
				int pathSegmentCount = path.segmentCount();
				if (pathSegmentCount > 1) {
					createContainer(path.uptoSegment(pathSegmentCount - 1));
				}
				createFile(entry);
			}
		}
	}

	public boolean isFolder(TarEntry entry) {
		return (entry.getFileType() == TarEntry.DIRECTORY);
	}

	/*
	 * Strip the leading directories from the path
	 */
	private String stripPath(String path) {
		String pathOrig = new String(path);
		for (int i = 0; i < stripLevel; i++) {
			int firstSep = path.indexOf('/');
			
			// If the first character was a separator we must strip to the next separator as well
			if (firstSep == 0) {
				path = path.substring(1);
				firstSep = path.indexOf('/');
			}

			// No separator was present so we're in a higher directory right now
			if (firstSep == -1) {
				return pathOrig;
			}
			path = path.substring(firstSep);
		}
		return path;
	}

	public void setStrip(int level) {
		stripLevel = level;
	}

	public int getStrip() {
		return stripLevel;
	}
	
	public TarEntry getEntry(String name) throws TarException{
		if (children == null) {
			initialize();
		}
		
		TarEntry entry = directoryEntryCache.get(new Path(name)); 
		if (entry != null) {
			return entry;
		}
		throw new TarException("Entry not found: "+name);			 
	}
	
	private void extractFile(TarEntry entry, File destFolder) throws IOException{

		if (!destFolder.exists()){
			throw new RuntimeException("Folder does not exists: "+destFolder);
		}
		
		String fileObjectPath = this.getFullPath(entry);
		InputStream contentStream = this.getContents(entry);
		String filename = new Path(fileObjectPath).lastSegment();
		File destFile = (new Path(destFolder.getAbsolutePath())).append(filename).toFile();
		FileUtils.copyInputStreamToFile(contentStream, destFile);
	}
	
	public void extractRecursively(TarEntry entry, File destFolder) throws IOException{
		
		if (!this.isFolder(entry)){
			extractFile(entry, destFolder);
			return;
		}
		
		if (!destFolder.exists()){
			throw new RuntimeException("Folder does not exit: "+destFolder);
		}
		
		Path path = new Path(entry.getName());
		String lastSegment = path.lastSegment();
		path = (Path) new Path(destFolder.getAbsolutePath()).append(lastSegment);
		path.toFile().mkdir();
		
		for(TarEntry child :this.getChildren(entry)){
			extractRecursively(child, path.toFile());
		}
		
	}

	@Override
	public List getChildren(Object element) {
		return getChildren((TarEntry)element);
	}

	@Override
	public InputStream getContents(Object element) {
		return getContents((TarEntry)element);
	}

	@Override
	public String getFullPath(Object element) {
		return getFullPath((TarEntry)element);
	}

	@Override
	public String getLabel(Object element) {
		return getLabel((TarEntry)element);
	}

	@Override
	public boolean isFolder(Object element) {
		return isFolder((TarEntry)element);
	}
	
	
}
