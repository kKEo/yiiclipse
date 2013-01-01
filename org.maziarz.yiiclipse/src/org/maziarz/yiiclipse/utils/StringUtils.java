package org.maziarz.yiiclipse.utils;

public class StringUtils {

	public static String stripQuotes(String name) {
		int len = name.length();
		if (len > 1
				&& (name.charAt(0) == '\'' && name.charAt(len - 1) == '\'' || name.charAt(0) == '"'
						&& name.charAt(len - 1) == '"')) {
			name = name.substring(1, len - 1);
		}
		return name;
	}
	
	public static String capitalize(String s) {
		if (s == null || s.length() == 0) return s; 
		return s.substring(0, 1).toUpperCase()+s.substring(1);
	}
	
	public static String decapitalize(String s) {
		if (s== null || s.length() == 0) return s; 
		return s.substring(0, 1).toLowerCase()+s.substring(1);
	}
	
}
