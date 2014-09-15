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

package org.fusesource.ide.fabric8.ui.actions.jclouds;

import io.fabric8.service.jclouds.CreateJCloudsContainerOptions;
import io.fabric8.zookeeper.utils.ZooKeeperUtils;

import java.util.Map;

import org.apache.curator.framework.CuratorFramework;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.fusesource.ide.fabric8.core.dto.ContainerDTO;
import org.fusesource.ide.fabric8.core.dto.ProfileDTO;
import org.fusesource.ide.fabric8.ui.FabricPlugin;
import org.fusesource.ide.fabric8.ui.actions.Messages;
import org.fusesource.ide.fabric8.ui.navigator.ContainerNode;
import org.fusesource.ide.fabric8.ui.navigator.Fabric;
import org.fusesource.ide.fabric8.ui.navigator.ProfileNode;
import org.fusesource.ide.fabric8.ui.navigator.VersionNode;


/**
 * The wizard for creating agents via jclouds
 */
public class CreateJCloudsContainerWizard extends Wizard {

	private static final String PUBLIC_IP = "publicip";
	
	private final VersionNode versionNode;
	private final ContainerNode selectedAgent;
	private final String defaultAgentName;
	private final ProfileNode selectedProfile;
	private IStructuredSelection selection;
	private CloudDetailsWizardPage page1;
	private CloudContainerDetailsWizardPage page2;

	public CreateJCloudsContainerWizard(VersionNode versionNode, ContainerNode selectedAgent, String defaultAgentName, ProfileNode selectedProfile) {
		this.versionNode = versionNode;
		this.selectedAgent = selectedAgent;
		this.defaultAgentName = defaultAgentName;
		this.selectedProfile = selectedProfile;
		super.setWindowTitle(Messages.createJCloudsAgentTitle);
	}


	public VersionNode getVersionNode() {
		return versionNode;
	}


	public ContainerNode getSelectedAgent() {
		return selectedAgent;
	}


	public String getDefaultAgentName() {
		return defaultAgentName;
	}


	public ProfileNode getSelectedProfile() {
		return selectedProfile;
	}


	public IStructuredSelection getSelection() {
		return selection;
	}


	public CloudDetailsWizardPage getPage1() {
		return page1;
	}


	public CloudContainerDetailsWizardPage getPage2() {
		return page2;
	}


	@Override
	public void addPages() {
		page1 = new CloudDetailsWizardPage();
		addPage(page1);


		page2 = new CloudContainerDetailsWizardPage(this);
		addPage(page2);
	}
	
	@Override
	public boolean performFinish() {
//		FabricPlugin.getLogger().debug("Create the container!!!");

		CloudContainerDetailsForm form = getPage2().getForm();
		form.saveSettings();
		String agentName = form.getAgentName();
		CreateJCloudsContainerOptions.Builder args = form.getCreateCloudArguments();
		args.name(agentName);
		args.resolver(PUBLIC_IP);

		Fabric fabric = getFabric();

		// we must use the local IP address of the ZooKeeper URL when creating a cloud container so that it can connect
		// properly to the ZK node if its on the cloud
		ProfileNode defaultProfile = getVersionNode().getProfileNode("default");
		if (defaultProfile != null && fabric != null) {
			ProfileDTO profile = defaultProfile.getProfile();
			if (profile != null) {
				Map<String, String> config = profile.getConfigurations().get("io.fabric8.zookeeper");
				if (config != null) {
					String zkUrl = config.get("zookeeper.url");
					if (zkUrl != null) {
						try {
							// TODO: HOW TO GET THE CURATORFRAMEWORK OBJECT?
							FabricPlugin.getLogger().debug("TODO: HOW TO GET THE CURATOR FRAMEWORK");
							CuratorFramework curator = null;
							zkUrl = ZooKeeperUtils.getSubstitutedData(curator, zkUrl);
							args.zookeeperUrl(zkUrl);
						} catch (Exception e) {
							FabricPlugin.getLogger().warning("Failed to get ZooKeeperURL: " + e, e);
						}
					}
				}

			}
		}

		ContainerDTO agent = null;
		if (selectedAgent != null) {
			agent = selectedAgent.getContainer();
		}
		ProfileDTO[] profiles = form.getSelectedProfileArray();
		versionNode.getFabric().createContainer(agent, profiles, args.build());

		return true;
	}


	public Fabric getFabric() {
		return (versionNode != null) ? versionNode.getFabric() : (selectedProfile != null) ? selectedProfile.getFabric() : (selectedAgent != null) ? selectedAgent.getFabric() : null;
	}

	public CloudDetails getSelectedCloud() {
		return page1.getSelectedCloud();
	}


}