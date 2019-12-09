/******************************************************************************* 
 * Copyright (c) 2015 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.camel.editor.features.delete;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.impl.DefaultRemoveFeature;

/**
 * @author lhein
 */
public class RemoveFigureFeature extends DefaultRemoveFeature {

	/**
	 * @param fp
	 */
	public RemoveFigureFeature(IFeatureProvider fp) {
		super(fp);
	}
	
	@Override
	public boolean isAvailable(IContext context) {
		// We want to keep the UI always synchronized with Model.
		// So don't want to be able to remove only the graphical element.
		return false;
	}

}
