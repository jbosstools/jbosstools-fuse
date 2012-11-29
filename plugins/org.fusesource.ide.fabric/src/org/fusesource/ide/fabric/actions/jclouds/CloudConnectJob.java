package org.fusesource.ide.fabric.actions.jclouds;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.fusesource.ide.commons.jobs.Jobs;
import org.fusesource.ide.fabric.FabricPlugin;
import org.jclouds.compute.ComputeService;
import org.jclouds.rest.AuthorizationException;


public class CloudConnectJob extends Job {
	private final CloudDetailsCachedData cacheData;
	private final CloudDetails details;
	private final Job[] nextJobs;
	private boolean cancelled = false;


	public CloudConnectJob(CloudDetailsCachedData cacheData, Job... nextJobs) {
		super("Connect to cloud");
		this.cacheData = cacheData;
		this.details = cacheData.getDetails();
		this.nextJobs = nextJobs;
	}


	public CloudDetails getDetails() {
		return details;
	}

	public ComputeService getComputeClient() {
		return cacheData.getComputeClient();
	}

	public void setComputeClient(ComputeService computeClient) {
		cacheData.setComputeClient(computeClient);
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		if (details.getApi() == JClouds.EMPTY_API && details.getProvider() == JClouds.EMPTY_PROVIDER) return Status.OK_STATUS;
		try {
			ComputeService computeClient = cacheData.getComputeClient();
			if (computeClient == null) {
				System.out.println("Starting to connect to " + details);
				computeClient = CloudDetails.createComputeService(details);
			}
			if (computeClient != null) {
				setComputeClient(computeClient);
				onConnected(computeClient, monitor);
			}
		} catch (final AuthorizationException e) {
			if (!cancelled) {
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						FabricPlugin.showUserError("Could not authorize cloud details", e.getMessage(), e);
					}});
			}
		} catch (Exception e) {
			if (!cancelled) {
				return new Status(IStatus.WARNING, FabricPlugin.PLUGIN_ID, "Failed to connect to cloud " + details.getName() + ". Reason: " + e, e);
			}
		}
		return Status.OK_STATUS;
	}

	protected void onConnected(ComputeService client, IProgressMonitor monitor) {
		Jobs.schedule(nextJobs);
	}
	
	@Override
	public void canceling() {
		cancelled = true;
	}
}
