package org.fusesource.ide.server.karaf.ui.editor;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.fusesource.ide.server.karaf.core.internal.server.IServerConfigurationWorkingCopy;


public class UserNameChangeOperation extends AbstractOperation {

	private final IServerConfigurationWorkingCopy copy;
	private final String newUserName;
	private final String oldUserName;
	public UserNameChangeOperation(IServerConfigurationWorkingCopy copy, String newUserName,String label) {
		super(label);
		this.copy = copy;
		oldUserName = copy.getUserName();
		this.newUserName = newUserName;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		copy.setUserName(newUserName);
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
		copy.setUserName(oldUserName);
		return Status.OK_STATUS;
	}

}
