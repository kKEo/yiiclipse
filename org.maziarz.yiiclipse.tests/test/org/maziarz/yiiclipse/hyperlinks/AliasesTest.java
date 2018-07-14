package org.maziarz.yiiclipse.hyperlinks;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.maziarz.yiiclipse.utils.YiiPathResolver;


public class AliasesTest {

	private static String pathToSandbox = "/home/krma/Workspaces/PHP/zt/workspace/ptest";
	private static String pathToFramework = "/home/krma/Downloads/PHP/Yii/yii118/framework";
	private static YiiPathResolver resolver;
	
	@BeforeClass
	public static void prepareSandbox(){
		LocalPathHelperMock ph = new LocalPathHelperMock(new Path(pathToSandbox), new Path(pathToFramework));
		resolver = new YiiPathResolver(ph);
	}
	
	@AfterClass
	public static void purgeSandbox(){
		
	}
	
	private static String buildPath(String base, String... elements){
		String path = base;
		for (String el : elements){
			path += File.separator + el;
		}
		return path;
	} 
	
	@Test
	public void testApplicationPath(){
		String alias = "application";
		IPath path = resolver.resolveAliasPath(alias, null);
		Assert.assertEquals(buildPath(pathToSandbox,"protected"), path.toOSString());
	}
	
	@Test
	public void testExtPath(){
		String alias = "ext";
		IPath path = resolver.resolveAliasPath(alias, null);
		
		Assert.assertEquals(buildPath(pathToSandbox,"protected","extensions"), path.toOSString());
	}
	
	@Test
	public void testViewPath(){
		String alias = "application.views.client";
		IPath path = resolver.resolveAliasPath(alias, null);
		
		Assert.assertEquals(buildPath(pathToSandbox,"protected","views","client"), path.toOSString());
	}
	
	/**
	 * FRAMEWORK PATHS
	 * 
	 */
	
	@Test
	public void testSystemPath(){
		String alias = "system";
		IPath path = resolver.resolveAliasPath(alias, null);
		
		Assert.assertEquals(buildPath(pathToFramework), path.toOSString());
	}
	
	@Test
	public void testZiiPath(){
		String alias = "zii";
		IPath path = resolver.resolveAliasPath(alias, null);
		
		Assert.assertEquals(buildPath(pathToFramework,"zii"), path.toOSString());
	}
	
	@Test
	public void testJuiPath(){
		String alias = "zii.widgets.jui.CJuiTabs";
		IPath path = resolver.resolveAliasPath(alias, null);
		
		Assert.assertEquals(buildPath(pathToFramework,"zii","widgets", "jui", "CJuiTabs"), path.toOSString());
	}
	
}
