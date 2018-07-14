package org.maziarz.yiiclipse.completion;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.CompletionRequestor;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.php.core.codeassist.ICompletionContext;
import org.eclipse.php.core.codeassist.ICompletionReporter;
import org.eclipse.php.internal.core.codeassist.strategies.AbstractCompletionStrategy;
import org.maziarz.yiiclipse.hyperlinks.WorkspacePathHelper;
import org.maziarz.yiiclipse.utils.YiiPathResolver;

public class AliasEnabledStrategy extends AbstractCompletionStrategy {

	private AliasEnabledContext ctx;
	
	private YiiPathResolver pathResolver;
	
	public AliasEnabledStrategy(ICompletionContext context) {
		super(context);
		
		if (context instanceof AliasEnabledContext){
			ctx = (AliasEnabledContext) context;
		}
	}
	
	public YiiPathResolver getPathResolver() {
	
		if (pathResolver == null) {
			pathResolver = new YiiPathResolver(new WorkspacePathHelper());
		}
		
		return pathResolver;
	}

	@Override
	public void apply(ICompletionReporter reporter) throws Exception {
		
		if (ctx == null){
			return;
		}
		
		CompletionRequestor requestor = ctx.getCompletionRequestor();
		
		final String prefix = ctx.getPrefix();
		ISourceRange replaceRange = getReplacementRange(ctx);
		
		List<String> proposals = new LinkedList<String>();
		
		if (prefix.startsWith("application") || prefix.startsWith("ext") || prefix.startsWith("zii") || prefix.startsWith("system") || prefix.startsWith("webroot")){
			IPath path = resolvePath(prefix);
				
			if (path != null) {
				for (File child : path.toFile().listFiles()){
					applyFilterProposals(prefix, child, proposals);
				}
			}
		} else if ("".equals(prefix)){
			proposals.add("application");
			proposals.add("ext");
			proposals.add("system");
			proposals.add("webroot");
			proposals.add("zii");
		} 
		
		for (String proposal : proposals){
			reporter.reportKeyword(proposal, getSuffix(), replaceRange);
		}
	}

	private void applyFilterProposals(final String prefix, File child, List<String> proposals) {
		
		
		ProposalFilter matchPrefix = new ProposalFilter() {
			@Override
			public boolean filter(File proposal) {
				String actualPrefix = getActualPrefix(prefix);
				return proposal.getName().toLowerCase().startsWith(actualPrefix.toLowerCase());
			}
		};
		
		filterProposals(proposals, prefix, child, 
			new ProposalFilter(matchPrefix){

			@Override
			public boolean filter(File proposal) {
				return proposal.isDirectory();
			}
		}, new ProposalFilter(matchPrefix){

			@Override
			public boolean filter(File proposal) {
				IPath proposalPath = new Path(proposal.toString());
				
				if (proposal.isFile() && !"php".equals(proposalPath.getFileExtension())){
					return false;
				} 
				
				return true;
			}
		});
	}

	/**
	 * Iterate through all delivered proposal filters. If one filter passes the proposal other will be not performed.
	 * @param proposals
	 * @param prefix
	 * @param child
	 * @param proposalFilters
	 */
	private void filterProposals(List<String> proposals, String prefix, File child, ProposalFilter ... proposalFilters) {
		
		boolean isValid = false;
		
		for(ProposalFilter f : proposalFilters){
			if (f.doFilter(child)){
				isValid = true;
				break;
			}
		}
		
		if (isValid) {
			String baseAlias = getBaseAlias(prefix);
			String object = child.getName().replaceFirst(".php$", "");
			proposals.add(baseAlias+object);
		}
		
		
	}

	private IPath resolvePath(String prefix) {
		ISourceModule sourceModule = ctx.getSourceModule();
		
		IPath path = null;
		
		if (prefix.indexOf('.') > 0){
			String alias = prefix.substring(0, prefix.lastIndexOf('.'));
			path = getPathResolver().resolveAliasPath(alias, sourceModule);
		}
		
		return path;
	}
	
	private String getBaseAlias(String prefix) {
		
		if (prefix.indexOf('.') > 0){
			String alias = prefix.substring(0, prefix.lastIndexOf('.')+1);
			return alias;
		}
		
		return "";
	}

	private String getActualPrefix(String prefix) {
		if (prefix.indexOf('.') > 0){
			return prefix.substring(prefix.lastIndexOf('.')+1, prefix.length());
		}
		return prefix;
	}

	private String getIfIsWorthPresenting(File proposal, IPath basePath, String actualPrefix) {
		
		if (proposal.isDirectory()){
			return proposal.getName()+".";
		}
		
		IPath proposalPath = new Path(proposal.toString());
		if ("php".equals(proposalPath.getFileExtension())){
			return proposalPath.removeFileExtension().lastSegment();
		}
		
		return null;
	}

	public String getSuffix() {
		return "";
	}
	
	abstract class ProposalFilter{
		
		protected ProposalFilter parent;
		
		public ProposalFilter() {
		}
		
		public ProposalFilter(ProposalFilter parent) {
			this.parent = parent;
		}
		
		public boolean doFilter(File proposal){
			if (parent != null && !parent.doFilter(proposal)){
				return false;
			}
			
			return filter(proposal);
		}
		
		public abstract boolean filter(File proposal);
	}
	
}
