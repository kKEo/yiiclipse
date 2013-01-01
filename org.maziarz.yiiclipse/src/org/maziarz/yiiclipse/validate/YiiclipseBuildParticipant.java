package org.maziarz.yiiclipse.validate;

import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IModelElementVisitor;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.builder.IBuildContext;
import org.eclipse.dltk.core.builder.IBuildParticipant;
import org.eclipse.dltk.core.builder.ISourceLineTracker;
import org.eclipse.dltk.internal.core.ModelElement;
import org.eclipse.dltk.internal.core.SourceMethod;
import org.eclipse.dltk.internal.core.SourceModule;
import org.eclipse.dltk.internal.core.SourceType;
import org.eclipse.php.internal.core.ast.nodes.ASTNode;
import org.eclipse.php.internal.core.ast.nodes.ASTParser;
import org.eclipse.php.internal.core.ast.nodes.Expression;
import org.eclipse.php.internal.core.ast.nodes.MethodInvocation;
import org.eclipse.php.internal.core.ast.nodes.Program;
import org.eclipse.php.internal.core.ast.nodes.Scalar;
import org.eclipse.php.internal.core.ast.visitor.AbstractVisitor;
import org.maziarz.yiiclipse.utils.ASTUtils;
import org.maziarz.yiiclipse.utils.StringUtils;
import org.maziarz.yiiclipse.utils.YiiPathResolver;

public class YiiclipseBuildParticipant implements IBuildParticipant {

	/*-
	 *  TODO: do participants read from extension point
	  private final TclCheckInfo[] checks = ChecksExtensionManager...
	 */
	
//	private YiiclipseCheckInfo[] checks = new YiiclipseCheckInfo[];
	
	public YiiclipseBuildParticipant(IScriptProject project) {
	}
	
	@Override
	public void build(final IBuildContext context) throws CoreException {
		
		final ISourceModule module = context.getSourceModule();
		final ISourceLineTracker lineTracker = context.getLineTracker();
		
		module.accept(new IModelElementVisitor() {
			
			private ModelElement modelElement;
			
			private ASTParser parser = ASTParser.newParser(module);
			private Program p;
			
			private IType currentType;
			
			{
				try {
				p = parser.createAST(null);
				} catch (Exception e) {}
			}
			
			
			
			@SuppressWarnings("restriction")
			@Override
			public boolean visit(IModelElement element) {

				/*-
				if (element instanceof ModelElement) {
					modelElement = (ModelElement) element;
					try {
						IModelElement[] me = ((ModelElement)element).getChildren();
						for (IModelElement e : me) {
							System.out.println("Element: "+e.toString());
						}
					} catch (ModelException e) {
						e.printStackTrace();
					}
				}
				*/
				
				if (element instanceof SourceModule) {
					return true;
				}
				
				if (element instanceof SourceType) {
					currentType = (SourceType)element;
					return true;
				}
				
				if (element instanceof SourceMethod) {
					SourceMethod sourceMethod = (SourceMethod) element;
					
					try {
						String name = element.getElementName();
						ISourceRange range = sourceMethod.getNameRange();
						
						if (name.startsWith("action")) {
							ASTNode elementAt = p.getElementAt(range.getOffset());
							ASTNode functionDeclaration = elementAt.getParent();
							functionDeclaration.accept(new AbstractVisitor() {
								public boolean visit(MethodInvocation mi) {
									if (ASTUtils.isSimpleVariable(mi.getDispatcher(), "this", true)) {
										if (ASTUtils.isSimpleVariable(mi.getMethod().getFunctionName().getName(), "render", false)) {
											List<Expression> params = mi.getMethod().parameters();
											if (params.size() > 0) {
												if (params.get(0) instanceof Scalar) {
													Scalar view = (Scalar)params.get(0);
													
													
													String expectedViewName = StringUtils.stripQuotes(view.getStringValue());
													String resolvedPath = YiiPathResolver.resolveViewPath(//
															context.getSourceModule(), //
															currentType.getElementName(),//
															expectedViewName);
													
													Path path = new Path(resolvedPath);
													IResource findElement = ResourcesPlugin.getWorkspace().getRoot().findMember(path);
													
													if (findElement == null) {
														context.getProblemReporter().reportProblem(//
															new YiiclipseProblem("View file not found: "+path, //
																	view.getStart() + 1,//
																	view.getStart() + view.getLength() - 1, //
																	lineTracker.getLineOffset(view.getStart())
																));
													}
												}
											}
										}
									};
									return false;
								}
							});
						}
					} catch (Exception e) {
						e.printStackTrace();
						return false;
					}
					return true;
				}
				return false;
			}
		});
	}

}
