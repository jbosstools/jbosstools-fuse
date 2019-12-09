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

package org.fusesource.ide.foundation.ui.jobs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.fusesource.ide.foundation.ui.internal.FoundationUIActivator;

public abstract class LoadListJobSupport extends Job {
	private final WritableList writableList;

	public LoadListJobSupport(String name, WritableList writableList) {
		super(name);
		this.writableList = writableList;
	}

	protected abstract List<?> loadList();

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		FoundationUIActivator.pluginLog().logInfo("Starting load of list of type: " + writableList.getElementType());
		// lets use a copy to make sure we're not going to be concurrently changing it later on
		final List<?> value = new ArrayList(loadList());

		// now lets do this asynchronously...
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				writableList.clear();
				writableList.addAll(value);

				FoundationUIActivator.pluginLog().logInfo("Starting added " + value.size() + " element(s) of type: " + writableList.getElementType());
			}
		});
		return Status.OK_STATUS;
	}

}
