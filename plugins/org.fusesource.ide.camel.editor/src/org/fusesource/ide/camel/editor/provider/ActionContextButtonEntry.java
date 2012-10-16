package org.fusesource.ide.camel.editor.provider;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.tb.ContextButtonEntry;

/**
 * A helper class for adding custom actions onto the Graphiti popup hover context thingy
 */
public abstract class ActionContextButtonEntry extends ContextButtonEntry {

	public ActionContextButtonEntry(IFeatureProvider featureProvider, IContext context) {
		super(new ContextEntryFeature(featureProvider), context);
		ContextEntryFeature.configure(this);
	}

	@Override
	public boolean canExecute() {
		return true;
	}

	@Override
	public abstract void execute();

}
