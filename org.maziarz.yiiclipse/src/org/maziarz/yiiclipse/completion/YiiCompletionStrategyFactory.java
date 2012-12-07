package org.maziarz.yiiclipse.completion;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.php.core.codeassist.ICompletionContext;
import org.eclipse.php.core.codeassist.ICompletionStrategy;
import org.eclipse.php.core.codeassist.ICompletionStrategyFactory;

public class YiiCompletionStrategyFactory implements ICompletionStrategyFactory{

	@Override
	public ICompletionStrategy[] create(ICompletionContext[] contexts) {
		
		List<ICompletionStrategy> result = new LinkedList<ICompletionStrategy>();
		for (ICompletionContext ctx : contexts) {
			
			if (ctx.getClass() == AliasEnabledContext.class){
				result.add(new AliasEnabledStrategy(ctx));
			} else
			if (ctx.getClass() == WidgetConfigArrayContext.class){
				result.add(new WidgetConfigArrayStrategy(ctx));
			}
			
		}
		
		return result.toArray(new ICompletionStrategy[result.size()]);
	}

	
	
	
	
}
