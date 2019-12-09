/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.camel.editor.features.custom;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.fusesource.ide.camel.editor.utils.DiagramUtils;


/**
 * @author lhein
 *
 */
public class SetGridVisibilityFeature extends AbstractCustomFeature {

	private static boolean gridVisible = true;
	
	public SetGridVisibilityFeature(IFeatureProvider fp) {
		super(fp);
	}
	
	@Override
	public void execute(ICustomContext context) {
		updateGridVisible(!gridVisible);
		DiagramUtils.setGridVisible(gridVisible, null);
	}

	private static synchronized void updateGridVisible(boolean newValue) {
		gridVisible = newValue;
	}

	@Override
	public String getName() {
		if (gridVisible) {
			return "Hide Grid";
		} else {
			return "Show Grid";			
		}
	}
	
	@Override
	public String getDescription() {
		if (gridVisible) {
			return "Hides the grid...";
		} else {
			return "Shows the grid...";			
		}
	}
	
	@Override
	public boolean canExecute(ICustomContext context) {
		return true;
	}
}
