package org.maziarz.yiiclipse;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class YiiclipseBundle extends AbstractUIPlugin {
	
	public static final String PLUGIN_ID = "org.maziarz.yiiclipse"; //$NON-NLS-1$

	private static YiiclipseBundle plugin;

	private static Logger logger;
	
	public YiiclipseBundle() {}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		
		if (plugin == null) {
			logger = Logger.getAnonymousLogger();
			ConsoleHandler consoleHandler = new ConsoleHandler();
			consoleHandler.setFormatter(new SimpleFormatter());
			logger.addHandler(consoleHandler);
			
			plugin = this;
		}
		
		debug("Yiiclipse is up and running.");
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static YiiclipseBundle getDefault() {
		return plugin;
	}

	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	public static void logError(String message, Exception e) {
		logError(message);
		e.printStackTrace();
	}
	
	public static void logError(String message) {
		getDefault().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, message));
	}
	
	public static void showError(String title, String message, Shell shell){
		Status s = new Status(IStatus.ERROR, PLUGIN_ID, message);
		ErrorDialog.openError(shell, title, message, s);
	}
	
	public static void showInfo(String title, String message, Shell shell){
		MessageDialog.openInformation(shell, title, message);
	}
	
	public static void logWarning(String message) {
		getDefault().getLog().log(new Status(IStatus.WARNING, PLUGIN_ID, message));
	}

	public static void debug(String message) {
		logger.log(Level.INFO, message);
		if (Platform.inDebugMode()){
			getDefault().getLog().log(new Status(IStatus.INFO, PLUGIN_ID, message));
		}
	}
	
}
