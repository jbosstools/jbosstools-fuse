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
package org.fusesource.ide.fabric8.ui.actions.jclouds;

import io.fabric8.api.CreationStateListener;
import io.fabric8.service.jclouds.CreateJCloudsContainerMetadata;
import io.fabric8.service.jclouds.CreateJCloudsContainerOptions;
import io.fabric8.service.jclouds.JcloudsContainerProvider;
import io.fabric8.service.jclouds.firewall.internal.Ec2FirewallSupport;
import io.fabric8.service.jclouds.firewall.internal.FirewallManagerFactoryImpl;
import io.fabric8.service.jclouds.firewall.internal.NovaFirewallSupport;

import java.net.URI;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.fusesource.ide.commons.Viewers;
import org.fusesource.ide.commons.jobs.Jobs;
import org.fusesource.ide.commons.util.Strings;
import org.fusesource.ide.fabric8.ui.FabricPlugin;
import org.fusesource.ide.fabric8.ui.actions.FabricDetails;
import org.fusesource.ide.fabric8.ui.actions.FabricDetailsAddAction;
import org.fusesource.ide.fabric8.ui.actions.Messages;
import org.fusesource.ide.fabric8.ui.navigator.Fabrics;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.options.TemplateOptions;


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
		super.setDefaultPageImageDescriptor(FabricPlugin.getPlugin().getImageDescriptor("fabric8_logo.png"));
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
				String agentName = form.getAgentName();
				CreateJCloudsContainerOptions.Builder args = form.getCreateCloudArguments();
				String proxyUri = (form instanceof CloudFabricDetailsForm) ? ((CloudFabricDetailsForm)form).getProxyUri() : Fabrics.DEFAULT_MAVEN_PROXY_URI;
				
				try {
					args = args.name(agentName);
					args = args.ensembleServer(true);
					args = args.adminAccess(true);
					if (!Strings.isBlank(proxyUri)) {
						args = args.proxyUri(new URI(proxyUri));
					}
					
					FabricPlugin.getLogger().debug("============ proxy URI: " + args.getProxyUri());
					FabricPlugin.getLogger().debug("Create cloud fabric: " + fabricName + " container: " + agentName);

					// create and activate firewall manager
					FirewallManagerFactoryImpl firewallManagerFactory = new FirewallManagerFactoryImpl();
					firewallManagerFactory.activateComponent();
					Ec2FirewallSupport ec2fw = new Ec2FirewallSupport();
					ec2fw.activateComponent();
				    firewallManagerFactory.bindFirewallSupport(ec2fw);
				    NovaFirewallSupport novafw = new NovaFirewallSupport();
				    novafw.activateComponent();
				    firewallManagerFactory.bindFirewallSupport(novafw);
				    
					// create and activate provider
					JcloudsContainerProvider provider = new JcloudsContainerProvider();
					provider.activateComponent();
				    provider.bindFirewallManagerFactory(firewallManagerFactory);

				    // get and set the provider name
					String providerName = args.getProviderName();
					args = args.contextName(providerName);

					// create and set the compute service
					ComputeService computeClient = CloudDetails.createComputeService(getSelectedCloud());
					args = args.computeService(computeClient);

					FabricPlugin.getLogger().debug("Creating Jclouds provider type: " + providerName);

				    // we need to set the fabric user , pw and role in that way, otherwise the user is not created
					CreateJCloudsContainerOptions opts = args.withUser(args.getUser(), args.getPassword(), "admin").build();

					FabricPlugin.getLogger().debug("Compute Service: " + opts.getComputeService());

					// finally create the image
					final CreateJCloudsContainerMetadata metadata = provider.create(opts, new CreationStateListener() {
						@Override
						public void onStateChange(String message) {
							monitor.subTask(message);
						}
					});

					// on failure we don't add the cloud image to the navigator
					Throwable failure = metadata.getFailure();
					if (failure != null) {
						return new Status(Status.ERROR, FabricPlugin.PLUGIN_ID, "Failed to create Fabric: " + fabricName, failure);
					}

					// now extract the public IP from the meta data
					final StringBuilder urisBuilder = new StringBuilder();
					for(String address:metadata.getPublicAddresses()) {
						urisBuilder.append(address).append(",");
					}

					final CreateJCloudsContainerOptions.Builder arguments = args; 
					Viewers.async(new Runnable() {
						@Override
						public void run() {							
							String uris = urisBuilder.toString();
							if(uris.endsWith(",")) {
								uris = uris.substring(0, uris.length() - 1);
							}
							FabricPlugin.getLogger().debug("Creating fabric with uris: " + uris);
							FabricDetails details = FabricDetails.newInstance(fabricName, uris);
							details.setUserName(arguments.getUser());
							details.setPassword(arguments.getPassword());
							details.setZkPassword(arguments.getZookeeperPassword());
							new FabricDetailsAddAction(fabrics).addCloud(details);
						}});
					return Status.OK_STATUS;
				} catch (Throwable e) {
					return new Status(Status.ERROR, FabricPlugin.PLUGIN_ID, "Failed to create Fabric: " + fabricName, e);
				}
			}
		});
		return true;
	}

	/**
	 * returns the selected cloud
	 * 
	 * @return
	 */
	public CloudDetails getSelectedCloud() {
		return page1.getSelectedCloud();
	}
}
