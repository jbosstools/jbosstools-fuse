package org.fusesource.ide.jmx.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class DeleteConnectionJob extends ChainedJob {
	final IConnectionWrapper[] connections;
	public DeleteConnectionJob(IConnectionWrapper[] wrappers) {
		super(JMXCoreMessages.DeleteConnectionJob, JMXActivator.PLUGIN_ID);
		this.connections = wrappers;
	}
	protected IStatus run(IProgressMonitor monitor) {
		for( int i = 0; i < connections.length; i++ )
			connections[i].getProvider().removeConnection(connections[i]);
		return Status.OK_STATUS;
	}
}
