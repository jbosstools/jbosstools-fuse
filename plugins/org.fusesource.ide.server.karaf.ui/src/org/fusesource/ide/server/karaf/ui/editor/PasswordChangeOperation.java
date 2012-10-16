package org.fusesource.ide.server.karaf.ui.editor;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.fusesource.ide.server.karaf.core.internal.server.IServerConfigurationWorkingCopy;


public class PasswordChangeOperation extends AbstractOperation {

	private final IServerConfigurationWorkingCopy copy;
	private final String newPassword;
	private final String oldPassword;
	public PasswordChangeOperation(IServerConfigurationWorkingCopy copy, String newPassword,String label) {
		super(label);
		this.copy = copy;
		oldPassword = copy.getPassword();
		this.newPassword = newPassword;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
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
