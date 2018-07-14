package org.maziarz.yiiclipse.utils;

import org.eclipse.dltk.compiler.env.ISourceType;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IType;
import org.eclipse.php.core.ast.nodes.ASTNode;
import org.eclipse.php.core.ast.nodes.Expression;
import org.eclipse.php.core.ast.nodes.ITypeBinding;
import org.eclipse.php.core.ast.nodes.Identifier;
import org.eclipse.php.core.ast.nodes.TypeDeclaration;
import org.eclipse.php.core.ast.nodes.Variable;

public class ASTUtils {

	@Deprecated
	/**
	 * Use StringUtils.stripQuotes() instead.
	 * @param string
	 * @return
	 */
	public static String stripQuotes(String name) {
		int len = name.length();
		if (len > 1
				&& (name.charAt(0) == '\'' && name.charAt(len - 1) == '\'' || name.charAt(0) == '"'
						&& name.charAt(len - 1) == '"')) {
			name = name.substring(1, len - 1);
		}
		return name;
	}

	public static boolean isSimpleVariable(Expression name, String string, boolean b) {

		if (string == null || name == null) {
			return false;
		}

		if (name instanceof Variable) {
			if (((Variable) name).isDollared() != b) {
				return false;
			}
			if (((Variable) name).getName() instanceof Identifier) {
				return string.equals(((Identifier) ((Variable) name).getName()).getName());
			}
		}

		return false;
	}

	public static IType getEnclosingType(ASTNode node) {

		if (node == null) {
			return null;
		}

		while (!(node.getParent() instanceof TypeDeclaration)) {
			node = node.getParent();
			if (node == null) {
				return null;
			}
		}
		node = node.getParent();
		ITypeBinding type = ((TypeDeclaration)node).resolveTypeBinding();
		
		if (type == null) {
			return null;
		}
		
		IModelElement me = type.getPHPElement();
		
		if (me instanceof ISourceType) {
			return (IType)me;
		}
		
		return null;

	}

}
