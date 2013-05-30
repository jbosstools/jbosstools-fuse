/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

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
