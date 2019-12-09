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
import org.eclipse.swt.widgets.Event;

public class RunnableAction extends Action {
	private final Runnable runnable;

	public RunnableAction(String text, Runnable runnable) {
		super(text);
		this.runnable = runnable;
	}

	public RunnableAction(String id, String text, Runnable runnable) {
		this(text, runnable);
		setId(id);
	}

	@Override
	public void run() {
		runnable.run();
	}

	@Override
	public void runWithEvent(Event event) {
		runnable.run();
	}


}
