package org.maziarz.yiiclipse.codegen;


import java.io.StringReader;
import java.util.HashMap;

import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.ast.parser.IModuleDeclaration;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.php.core.PHPVersion;
import org.eclipse.php.core.ast.nodes.Program;
import org.eclipse.php.internal.core.compiler.ast.parser.AbstractPHPSourceParser;
import org.eclipse.php.internal.core.compiler.ast.parser.PHPSourceParserFactory;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

public class PhpCodeGenerator2 {

	private String initialContent;
	protected Program program;
	
	private AbstractPHPSourceParser parser;
	
	public PhpCodeGenerator2() {
		this(null);
	}
	
	public PhpCodeGenerator2(String initialContent) {
		
		AbstractPHPSourceParser parser = PHPSourceParserFactory.createParser(PHPVersion.PHP5_3);
		
		this.initialContent = initialContent;
		
		if (initialContent == null) {
			initialContent = "<?php ?>";
		}
		
		try {
			IModuleDeclaration m = parser.parse(new StringReader(initialContent),null, true, false);
			if (m instanceof ModuleDeclaration) {
				ModuleDeclaration md = (ModuleDeclaration) m;
				
				
				
				
			}
		
		
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
