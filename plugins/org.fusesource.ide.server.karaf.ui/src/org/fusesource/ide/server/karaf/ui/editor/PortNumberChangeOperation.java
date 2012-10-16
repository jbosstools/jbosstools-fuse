package org.fusesource.ide.server.karaf.ui.editor;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.fusesource.ide.server.karaf.core.internal.server.IServerConfigurationWorkingCopy;


public class PortNumberChangeOperation extends AbstractOperation {

	private final IServerConfigurationWorkingCopy copy;
	private final int newPort;
	private final int oldPort;
	public PortNumberChangeOperation(IServerConfigurationWorkingCopy copy, int newPort,String label) {
		super(label);
		this.copy = copy;
		oldPort = copy.getPortNumber();
		this.newPort = newPort;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		copy.setPortNumber(newPort);
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
		copy.setPortNumber(oldPort);
		return Status.OK_STATUS;
	}

}
