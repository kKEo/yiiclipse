package org.maziarz.yiiclipse;

import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.swt.widgets.Display;

public class YiiProjectLightweightDecorator extends LabelProvider implements ILightweightLabelDecorator {

	public void addListener(ILabelProviderListener listener) {
		addListenerObject(listener);
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		removeListenerObject(listener);
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public void decorate(Object element, IDecoration decoration) {

		if (element instanceof IScriptProject) {
			IScriptProject scriptProject = (IScriptProject) element;
			IProjectDescription description;
//			try {
//				description = scriptProject.getProject().getDescription();
//				String[] natures = description.getNatureIds();
//
//				for (String nature : natures) {
//					if (YiiProjectNature.NATURE_ID.equals(nature)) {
//						decoration.addSuffix("(yii project)");
//					}
//				}
//			} catch (CoreException e) {
//				e.printStackTrace();
//			}
		}
	}

	public void refresh(Object[] toBeUpdated) {

		final LabelProviderChangedEvent event = new LabelProviderChangedEvent(this, toBeUpdated);

		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				fireLabelProviderChanged(event);
			}
		});

	}

}
