/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.editor.restconfiguration.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.fusesource.ide.camel.editor.restconfiguration.RestConfigEditor;

/**
 * @author lheinema
 */
public abstract class RestEditorAction extends Action {
	
	protected RestConfigEditor parent;
	protected ImageRegistry mImageRegistry;
	
	public RestEditorAction(RestConfigEditor parent, ImageRegistry mImageRegistry) {
		super(null, IAction.AS_PUSH_BUTTON);
		this.parent = parent;
		this.mImageRegistry = mImageRegistry;
	}

	protected abstract String getImageName();
	
	@Override
	public ImageDescriptor getImageDescriptor() {
		return mImageRegistry.getDescriptor(getImageName());
	}
}
