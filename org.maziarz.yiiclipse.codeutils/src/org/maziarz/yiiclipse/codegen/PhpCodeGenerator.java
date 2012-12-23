package org.maziarz.yiiclipse.codegen;


import java.io.IOException;
import java.util.HashMap;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.formatter.IContentFormatter;
import org.eclipse.php.internal.core.PHPVersion;
import org.eclipse.php.internal.core.ast.nodes.ASTParser;
import org.eclipse.php.internal.core.ast.nodes.Program;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

public class PhpCodeGenerator {

	private String initialContent;
	protected Program program;
	
	private static IConfigurationElement phpFormatterElement;
	private static String phpFormatterClassName;
	private static IContentFormatter phpFormatter;
	
	private ASTParser parser;
	
	public PhpCodeGenerator() {
		this(null);
	}
	
	public PhpCodeGenerator(String initialContent) {
		
		this.initialContent = initialContent;
		
		parser = ASTParser.newParser(PHPVersion.PHP5_3, true);
		
		if (initialContent != null) {
			try {
				parser.setSource(initialContent.toCharArray());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			program = parser.createAST(null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		program.recordModifications();
	}
	
	public Program getProgram() {
		return program;
	}
	
	public String emit(){
		
		IDocument document = new Document();
		document.set(this.initialContent);
		
		TextEdit edit = program.rewrite(document, new HashMap<String, String>());
		try {
			edit.apply(document, TextEdit.UPDATE_REGIONS);
		} catch (MalformedTreeException e) {
			e.printStackTrace();
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		
//		PhpFormatProcessorImpl formatProcessor = new PhpFormatProcessorImpl();
//		try {
//			formatProcessor.formatDocument(document);
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (CoreException e) {
//			e.printStackTrace();
//		}

		return document.get();
	}

}
