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

import org.eclipse.graphiti.features.IFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.tb.AbstractContextEntry;

/**
 * A dummy feature for custom ContextEntry implementations
 *
 */
public class ContextEntryFeature implements IFeature {
	private final IFeatureProvider featureProvider;
	private AbstractContextEntry contextEntry;

	/**
	 * Configure the ContextEntryFeature passed into the AbstractContextEntry constructor
	 */
	public static void configure(AbstractContextEntry entry) {
		IFeature feature = entry.getFeature();
		if (feature instanceof ContextEntryFeature) {
			ContextEntryFeature aFeature = (ContextEntryFeature) feature;
			aFeature.setContextEntry(entry);
		}

	}

	public ContextEntryFeature(IFeatureProvider featureProvider) {
		this.featureProvider = featureProvider;
	}

	public AbstractContextEntry getContextEntry() {
		return contextEntry;
	}

	public void setContextEntry(AbstractContextEntry buttonEntry) {
		this.contextEntry = buttonEntry;
	}


	@Override
	public String getName() {
		return contextEntry.getText();
	}

	@Override
	public String getDescription() {
		return contextEntry.getDescription();			}

	@Override
	public IFeatureProvider getFeatureProvider() {
		return featureProvider;
	}

	@Override
	public boolean isAvailable(IContext context) {
		return true;
	}

	@Override
	public boolean canExecute(IContext context) {
		return true;
	}

	@Override
	public void execute(IContext context) {
	}

	@Override
	public boolean canUndo(IContext context) {
		return false;
	}

	@Override
	public boolean hasDoneChanges() {
		return true;
	}

}