/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved.
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.fabric.actions.jclouds;


import java.net.URI;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.fusesource.fabric.api.CreationStateListener;
import org.fusesource.fabric.service.jclouds.CreateJCloudsContainerMetadata;
import org.fusesource.fabric.service.jclouds.CreateJCloudsContainerOptions;
import org.fusesource.fabric.service.jclouds.JcloudsContainerProvider;
import org.fusesource.fabric.service.jclouds.firewall.internal.Ec2FirewallSupport;
import org.fusesource.fabric.service.jclouds.firewall.internal.FirewallManagerFactoryImpl;
import org.fusesource.fabric.service.jclouds.firewall.internal.NovaFirewallSupport;
import org.fusesource.ide.commons.Viewers;
import org.fusesource.ide.commons.jobs.Jobs;
import org.fusesource.ide.fabric.FabricPlugin;
import org.fusesource.ide.fabric.actions.FabricDetails;
import org.fusesource.ide.fabric.actions.FabricDetailsAddAction;
import org.fusesource.ide.fabric.actions.Messages;
import org.fusesource.ide.fabric.navigator.Fabrics;
import org.jclouds.compute.ComputeService;
import org.osgi.framework.BundleContext;

import com.google.common.base.Strings;


/**
 * The wizard for creating agents via jclouds
 */
public class CreateJCloudsFabricWizard extends Wizard {
	private final Fabrics fabrics;
	private final String defaultAgentName;
	private IStructuredSelection selection;
	private CloudDetailsWizardPage page1;
	private CloudFabricDetailsWizardPage page2;

	public CreateJCloudsFabricWizard(Fabrics fabrics, String defaultAgentName) {
		this.fabrics = fabrics;
		this.defaultAgentName = defaultAgentName;
		super.setWindowTitle(Messages.createJCloudsFabricTitle);
	}


	public String getDefaultAgentName() {
		return defaultAgentName;
	}

	public IStructuredSelection getSelection() {
		return selection;
	}


	public CloudDetailsWizardPage getPage1() {
		return page1;
	}


	public CloudFabricDetailsWizardPage getPage2() {
		return page2;
	}


	@Override
	public void addPages() {
		page1 = new CloudDetailsWizardPage();
		addPage(page1);


		page2 = new CloudFabricDetailsWizardPage(this);
		addPage(page2);
	}


	@Override
	public boolean performFinish() {
		final CloudFabricDetailsForm form = getPage2().getForm();
		form.saveSettings();
		
		Jobs.schedule(new Job("Create container") {

			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				final String fabricName = form.getFabricName();
				try {
					String agentName = form.getAgentName();
					CreateJCloudsContainerOptions.Builder args = form.getCreateCloudArguments();
					args = args.name(agentName);
					args = args.ensembleServer(true);
					args = args.adminAccess(true);
					//args.setResolver(ZkDefs.PUBLIC_IP);
					String proxyUri = Fabrics.DEFAULT_MAVEN_PROXY_URI;
					if (form instanceof CloudFabricDetailsForm) {
						CloudFabricDetailsForm fabricForm = form;
						proxyUri = fabricForm.getProxyUri();
					}
					if (!Strings.isNullOrEmpty(proxyUri)) {
						args = args.proxyUri(new URI(proxyUri));
					}
					System.out.println("============ proxy URI: " + args.getProxyUri());
					args = args.creationStateListener(new CreationStateListener() {
						@Override
						public void onStateChange(String message) {
							monitor.subTask(message);
						}
					});

					System.out.println("Create cloud fabric: " + fabricName + " container: " + agentName);

					BundleContext context = FabricPlugin.getDefault().getBundle().getBundleContext();
					
					JcloudsContainerProvider provider = new JcloudsContainerProvider();
					provider.activateComponent();
//					provider.setBundleContext(context);
					FirewallManagerFactoryImpl firewallManagerFactory = new FirewallManagerFactoryImpl();
					firewallManagerFactory.activateComponent();
					String providerName = args.getProviderName();
					args = args.contextName(providerName);
					ComputeService computeClient = CloudDetails.createComputeService(getSelectedCloud());
					args = args.computeService(computeClient);
					System.out.println("Creating Jclouds provider type: " + providerName);
					Ec2FirewallSupport ec2fw = new Ec2FirewallSupport();
					ec2fw.activateComponent();
				    firewallManagerFactory.bindFirewallSupport(ec2fw);
				    NovaFirewallSupport novafw = new NovaFirewallSupport();
				    novafw.activateComponent();
				    firewallManagerFactory.bindFirewallSupport(novafw);
				    provider.bindFirewallManagerFactory(firewallManagerFactory);
					
					CreateJCloudsContainerOptions opts = args.build();
					System.err.println("Compute Service: " + opts.getComputeService());
					Set<CreateJCloudsContainerMetadata> metadatas = provider.create(opts);

					final StringBuilder urisBuilder = new StringBuilder();

					for (CreateJCloudsContainerMetadata metadata : metadatas) {
						Throwable failure = metadata.getFailure();
						if (failure != null) {
							return new Status(Status.ERROR, FabricPlugin.PLUGIN_ID, "Failed to create Fabric: " + fabricName, failure);
						}
						for(String address:metadata.getPublicAddresses()) {
							urisBuilder.append(address).append(",");
						}
					}
					final CreateJCloudsContainerOptions.Builder arguments = args; 
					Viewers.async(new Runnable() {

						@Override
						public void run() {
							String uris = urisBuilder.toString();
							if(uris.endsWith(",")) {
								uris = uris.substring(0, uris.length() - 1);
							}
							System.out.println("Creating fabric with uris: " + uris);
							FabricDetails details = FabricDetails.newInstance(fabricName, uris);
							details.setUserName(arguments.getUser());
							details.setPassword(arguments.getPassword());
							details.setZkPassword(arguments.getZookeeperPassword());
							FabricDetailsAddAction action = new FabricDetailsAddAction(fabrics);
							action.addCloud(details);
						}});
					return Status.OK_STATUS;
				} catch (Throwable e) {
					return new Status(Status.ERROR, FabricPlugin.PLUGIN_ID, "Failed to create Fabric: " + fabricName, e);
				}
			}
		});
		return true;
	}

	public CloudDetails getSelectedCloud() {
		return page1.getSelectedCloud();
	}


}
