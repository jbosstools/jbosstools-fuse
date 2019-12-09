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

package org.fusesource.ide.foundation.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

public abstract class ActionSupport extends Action {

	public ActionSupport(String text, ImageDescriptor image) {
		super(text, image);
		init();
	}

	public ActionSupport(String text, String tooltip, ImageDescriptor image) {
		this(text, image);
		setToolTipText(tooltip);
		init();
	}

	public ActionSupport(String text, int style) {
		super(text, style);
		init();
	}

	public ActionSupport(String text) {
		super(text);
		init();
	}

	private void init() {
		setId(getClass().getName());
	}
}
