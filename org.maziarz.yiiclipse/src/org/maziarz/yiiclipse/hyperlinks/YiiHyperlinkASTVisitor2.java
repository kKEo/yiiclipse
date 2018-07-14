package org.maziarz.yiiclipse.hyperlinks;

import java.util.LinkedList;

import org.eclipse.dltk.ast.ASTVisitor;
import org.eclipse.dltk.ast.declarations.MethodDeclaration;
import org.eclipse.dltk.ast.declarations.TypeDeclaration;
import org.eclipse.dltk.ast.expressions.Expression;
import org.eclipse.dltk.ast.references.SimpleReference;
import org.eclipse.dltk.ast.references.VariableReference;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.jface.text.Region;
import org.eclipse.php.core.compiler.ast.nodes.ArrayCreation;
import org.eclipse.php.core.compiler.ast.nodes.ArrayElement;
import org.eclipse.php.core.compiler.ast.nodes.Assignment;
import org.eclipse.php.core.compiler.ast.nodes.FieldAccess;
import org.eclipse.php.core.compiler.ast.nodes.PHPCallExpression;
import org.eclipse.php.core.compiler.ast.nodes.PHPFieldDeclaration;
import org.eclipse.php.core.compiler.ast.nodes.Scalar;
import org.maziarz.yiiclipse.hyperlinks.HyperlinkTargetCandidate.HyperlinkTargetType;
import org.maziarz.yiiclipse.utils.StringUtils;
import org.maziarz.yiiclipse.utils.YiiPathResolver;

public class YiiHyperlinkASTVisitor2 extends ASTVisitor {

	private boolean found = false;
	private TypeDeclaration currentType;

	private int offset;
	private String file;
	private Region selectRegion;


	private LinkedList<HyperlinkTargetCandidate> results = new LinkedList<HyperlinkTargetCandidate>();
	private ISourceModule sourceModule;

	private YiiPathResolver pathResolver;

	public YiiHyperlinkASTVisitor2(ISourceModule sourceModule, YiiPathResolver pathResolver, int offset, String file,
			Region selectRegion) {
		this.sourceModule = sourceModule;
		this.offset = offset;
		this.pathResolver = pathResolver;
	}

	public String getFile() {
		return file;
	}

	public Region getSelectRegion() {
		return selectRegion;
	}

	@Override
	public boolean visit(TypeDeclaration s) throws Exception {
		this.currentType = s;

		// make sure offset is inside type declaration
		if (s.sourceStart() < offset && offset < s.sourceEnd()) {

			for (Object o : s.getStatements()) {
				if (o instanceof PHPFieldDeclaration) {
					PHPFieldDeclaration fieldDeclaration = (PHPFieldDeclaration) o;

					if ("$layout".equals(fieldDeclaration.getName())) {
						if (fieldDeclaration.sourceStart() < offset && offset < fieldDeclaration.sourceEnd()) {
							if (fieldDeclaration.getVariableValue() instanceof Scalar) {
								String view = StringUtils.stripQuotes(((Scalar) fieldDeclaration.getVariableValue()).getValue());
								Expression variableValue = fieldDeclaration.getVariableValue();
								results.add(new HyperlinkTargetCandidate(variableValue, view, HyperlinkTargetType.LAYOUT));
							}
						}
					}
				}
			}

		} else {
			return false;
		}

		return super.visit(s);
	}

	@Override
	public boolean endvisit(TypeDeclaration s) throws Exception {

		if (results.size() > 0) {
			HyperlinkTargetCandidate theLast = results.getLast();
			selectFileAndRegion(theLast.getExpression(), theLast.getView(), theLast.getType());
		}

		this.currentType = null;

		return super.visit(s);
	}

	@Override
	public boolean visit(MethodDeclaration method) throws Exception {
		if (method.sourceStart() < offset && offset < method.sourceEnd()) {
			return true;
		}

		return false;
	}

	@Override
	public boolean endvisit(MethodDeclaration method) throws Exception {
		return super.endvisit(method);
	}

	@Override
	public boolean visit(Expression expr) throws Exception {

		if (expr.sourceStart() < offset && offset < expr.sourceEnd()) {

			if (expr instanceof Assignment) {
				Assignment assignment = (Assignment) expr;

				Expression e = assignment.getVariable();
				if (e instanceof FieldAccess) {
					FieldAccess fa = (FieldAccess) e;
					Expression vref = fa.getDispatcher();
					if (vref instanceof VariableReference) {
						// probably could be worth make sure $this is instance of Controller
						// but at the moment it seems to be not crucial (WorseIsBetter)
						if ("$this".equals(((VariableReference) vref).getName()) == false) {
							return !found;
						}
					}
					Expression accessedField = fa.getField();
					if (accessedField instanceof SimpleReference) {
						if ("layout".equals(((SimpleReference) accessedField).getName()) == false) {
							return !found;
						}
					}
				}

				Expression valueExpr = assignment.getValue();
				if (valueExpr instanceof Scalar) {
					String viewPath = StringUtils.stripQuotes(((Scalar) valueExpr).getValue());
					results.add(new HyperlinkTargetCandidate(valueExpr, viewPath));
				}

			} else {

				if (expr instanceof PHPCallExpression) {
					PHPCallExpression callExpr = (PHPCallExpression) expr;

					String functionName = callExpr.getCallName().getName();

					if ("beginWidget".equals(functionName) || "import".equals(functionName)) {
						String widget = null;
						for (Object arg : callExpr.getArgs().getChilds()) {
							if (arg instanceof Scalar) {
								Scalar widgetPath = ((Scalar) arg);
								widget = StringUtils.stripQuotes(widgetPath.getValue());
								String filePath = pathResolver.resolveWidgetPath(widget, sourceModule);

								if (!filePath.isEmpty() && widgetPath.sourceStart() < offset && offset < widgetPath.sourceEnd()) {
									file = filePath;
									int startIndex = widgetPath.sourceStart() + 1;
									int length = widget.length();
									selectRegion = new Region(startIndex, length);
									found = true;
									return !found;
								}
							}
						}
					} else if ("widget".equals(functionName)) {
						String widget = null;
						for (Object arg : callExpr.getArgs().getChilds()) {
							if (arg instanceof Scalar) {
								Scalar widgetPath = ((Scalar) arg);
								widget = StringUtils.stripQuotes(widgetPath.getValue());
								String filePath = pathResolver.resolveWidgetPath(widget, sourceModule);

								if (widgetPath.sourceStart() < offset && offset < widgetPath.sourceEnd()) {
									file = filePath;
									int startIndex = widgetPath.sourceStart() + 1;
									int length = widget.length();
									selectRegion = new Region(startIndex, length);
									found = true;
									return !found;
								}
							} else {
								if (arg instanceof ArrayCreation) {
									ArrayCreation arr = (ArrayCreation) arg;
									for (ArrayElement element : arr.getElements()) {
										if (element.getKey() instanceof Scalar) {
											Scalar key = (Scalar) element.getKey();
											if ("itemView".equals(StringUtils.stripQuotes(key.getValue()))) {
												if (element.getValue().sourceStart() < offset
														&& offset < element.getValue().sourceEnd()) {
													if (element.getValue() instanceof Scalar) {
														String view = StringUtils.stripQuotes(((Scalar) element.getValue())
																.getValue());
														results.add(new HyperlinkTargetCandidate(element.getValue(), view));
													}
												}
											}
										}
									}
									;
								}
							}
						}
					} // end of "widget"
					else if ("render".equals(functionName) || "renderPartial".equals(functionName)) {
						Object[] arguments = callExpr.getArgs().getChilds().toArray();
						if (arguments.length > 0) {
							Object arg1 = arguments[0];
							if (arg1 instanceof Scalar) {
								Scalar view = (Scalar) arg1;
								if (view.sourceStart() < offset && offset < view.sourceEnd()) {
									String viewName = StringUtils.stripQuotes(view.getValue());
									results.add(new HyperlinkTargetCandidate(view, viewName));
								}
							}
						}

					} // end of "render"
					else if ("registerCssFile".equals(functionName)) {
						Object[] arguments = callExpr.getArgs().getChilds().toArray();
						if (arguments.length > 0) {
							Object arg1 = arguments[0];
							if (arg1 instanceof Scalar) {
								Scalar scalar = (Scalar) arg1;
								String cssName = StringUtils.stripQuotes(scalar.getValue());
								String cssPath = pathResolver.resolveAliasPath("application", sourceModule) + "/" + cssName;
								if (scalar.sourceStart() < offset && offset < scalar.sourceEnd()) {
									file = cssPath;
									int startIndex = scalar.sourceStart() + 1;
									int lenght = cssName.length();

									selectRegion = new Region(startIndex, lenght);
									found = true;
									return !found;
								}

							}
						}
					} // end of "registerCssFile"
					else if ("beginContent".equals(functionName)) {
						Object[] arguments = callExpr.getArgs().getChilds().toArray();
						if (arguments.length > 0) {
							Object arg1 = arguments[0];
							if (arg1 instanceof Scalar) {
								Scalar view = (Scalar) arg1;
								if (view.sourceStart() < offset && offset < view.sourceEnd()) {
									String viewName = StringUtils.stripQuotes(view.getValue());
									results.add(new HyperlinkTargetCandidate(view, viewName, HyperlinkTargetType.LAYOUT));
								}
							}
						}
					} // end of "beginContent"
				}
			}
		}

		if (currentType == null && results.size() > 0) {
			HyperlinkTargetCandidate thelast = results.getLast();
			selectFileAndRegion(thelast.getExpression(), thelast.getView(), thelast.getType());
		}

		return !found;
	}

	private void selectFileAndRegion(Expression expression, String view, HyperlinkTargetType type) {

		int startIndex = expression.sourceStart() + 1;
		selectRegion = new Region(startIndex, view.length());

		if (type == HyperlinkTargetType.LAYOUT && !view.startsWith("//")) {
			view = "//layouts/" + view;
		}

		String typeName = (currentType != null) ? currentType.getName() : null;
		String viewFilePath = YiiPathResolver.resolveViewPath(sourceModule, typeName, view);
		file = viewFilePath;
		found = true;
	}

	public void setPathResolver(YiiPathResolver resolver) {
		pathResolver = resolver;
	}

}
