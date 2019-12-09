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

package org.fusesource.ide.server.karaf.ui.editor;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.fusesource.ide.server.karaf.core.server.IKarafServerDelegateWorkingCopy;


public class PasswordChangeOperation extends AbstractOperation {

	private final IKarafServerDelegateWorkingCopy copy;
	private final String newPassword;
	private final String oldPassword;
	public PasswordChangeOperation(IKarafServerDelegateWorkingCopy copy, String newPassword,String label) {
		super(label);
		this.copy = copy;
		oldPassword = copy.getPassword();
		this.newPassword = newPassword;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		// DO nothing on the execute, since the editor may not be saved yet
		copy.setPassword(newPassword);
		return Status.OK_STATUS;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return Status.OK_STATUS;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		copy.setPassword(oldPassword);
		return Status.OK_STATUS;
	}

}
