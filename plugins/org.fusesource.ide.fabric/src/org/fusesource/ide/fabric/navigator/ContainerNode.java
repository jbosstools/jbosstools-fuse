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

package org.fusesource.ide.fabric.navigator;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.fusesource.fabric.api.Container;
import org.fusesource.fabric.api.CreateChildContainerOptions;
import org.fusesource.fabric.api.CreateContainerMetadata;
import org.fusesource.fabric.api.CreateContainerOptions;
import org.fusesource.fabric.api.FabricService;
import org.fusesource.fabric.api.Profile;
import org.fusesource.fabric.api.Version;
import org.fusesource.fabric.service.ContainerTemplate;
import org.fusesource.fabric.service.Containers;
import org.fusesource.fabric.service.JmxTemplateSupport;
import org.fusesource.ide.commons.Activator;
import org.fusesource.ide.commons.tree.GraphableNode;
import org.fusesource.ide.commons.tree.GraphableNodeConnected;
import org.fusesource.ide.commons.tree.HasOwner;
import org.fusesource.ide.commons.tree.HasRefreshableUI;
import org.fusesource.ide.commons.tree.Node;
import org.fusesource.ide.commons.tree.RefreshableNode;
import org.fusesource.ide.commons.tree.RefreshableUI;
import org.fusesource.ide.commons.ui.ImageProvider;
import org.fusesource.ide.commons.ui.actions.ActionSupport;
import org.fusesource.ide.commons.util.Objects;
import org.fusesource.ide.fabric.FabricPlugin;
import org.fusesource.ide.fabric.actions.CreateChildContainerAction;
import org.fusesource.ide.fabric.actions.CreateSshContainerAction;
import org.fusesource.ide.fabric.actions.Messages;
import org.fusesource.ide.fabric.actions.jclouds.CreateJCloudsContainerAction;
import org.fusesource.ide.fabric.navigator.jmx.FabricConnectionWrapper;
import org.fusesource.ide.fabric.views.logs.ContainerLogBrowser;
import org.fusesource.ide.fabric.views.logs.HasLogBrowser;
import org.fusesource.ide.fabric.views.logs.ILogBrowser;
import org.fusesource.ide.fabric.views.logs.Logs;
import org.fusesource.ide.jmx.ui.internal.views.navigator.ContextMenuProvider;
import org.fusesource.ide.server.view.ITerminalConnectionListener;
import org.fusesource.ide.server.view.SshView;

import scala.actors.threadpool.Arrays;

import com.google.common.base.Joiner;

public class ContainerNode extends IdBasedFabricNode implements HasRefreshableUI, ImageProvider, GraphableNode, GraphableNodeConnected, ContextMenuProvider, ITerminalConnectionListener, HasLogBrowser {

	private final Container container;
	private ILogBrowser logBrowser;
	private JmxTemplateSupport jmxTemplate;
	private ContainerTemplate containerTemplate;
	private FabricConnectionWrapper connectionWrapper;

	public static ContainerNode toContainerNode(Object object) {
		if (object instanceof ContainerNode) {
			return (ContainerNode) object;
		}
		if (object instanceof HasOwner) {
			HasOwner ho = (HasOwner) object;
			return toContainerNode(ho.getOwner());
		}
		return null;
	}

	public static Container toContainer(Object object) {
		if (object instanceof Container) {
			return (Container) object;
		}
		ContainerNode node = toContainerNode(object);
		if (node != null) {
			return node.getContainer();
		}
		return null;
	}

	public ContainerNode(RefreshableNode parent, Container agent, Fabric fabric) {
		super(parent, fabric, agent.getId());
		this.container = agent;
		setPropertyBean(new ContainerViewBean(agent));

		Joiner joiner = Joiner.on("\n    ");
		Profile[] profiles = agent.getProfiles();
		for (Profile profile : profiles) {
			// TODO throws exception!
			//Profile overlay = profile.getOverlay();
			Profile overlay = profile;
			FabricPlugin.getLogger().debug("Profile: " + overlay);
			FabricPlugin.getLogger().debug("  bundles: " + joiner.join(overlay.getBundles()));
			FabricPlugin.getLogger().debug("  features: " + joiner.join(overlay.getFeatures()));
			FabricPlugin.getLogger().debug("  repos:   " + joiner.join(overlay.getRepositories()));
		}
	}

	public ContainerNode(RefreshableNode parent, ContainerNode agentNode) {
		this(parent, agentNode.getContainer(), agentNode.getFabric());
	}

	public ContainerNode(FabricNodeSupport agents, Container agent) {
		this(agents, agent, agents.getFabric());
	}

	public VersionNode getVersionNode() {
		return getFabric().getVersionNode(container.getVersion().getName());
	}


	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		if (adapter == IPropertySheetPage.class) {
			checkLoaded();
			VersionNode versionNode = getVersionNode();
			if (versionNode != null) {
				versionNode.getChildren();
				return new ContainerTabViewPage(this);
			}
		}
		return super.getAdapter(adapter);
	}


	@Override
	public List<Node> getChildrenGraph() {
		// lets add all my children and all my parent profiles...
		Set<Node> answer = new HashSet<Node>();
		answer.add(this);
		Profile[] profiles = getContainer().getProfiles();
		Collection<ProfileNode> profileNodes = getFabric().getProfileNodes(profiles);
		for (ProfileNode profileNode : profileNodes) {
			profileNode.addAndDescendants(answer);
		}
		return new ArrayList<Node>(answer);
	}



	@Override
	public ILogBrowser getLogBrowser() {
		if (logBrowser == null) {
			if (connectionWrapper != null) {
				logBrowser = Logs.toLogBrowser(connectionWrapper);
			}
			if (logBrowser == null){
				logBrowser = new ContainerLogBrowser(this);
			}
		}
		return logBrowser;
	}

	@Override
	public Collection<Node> getGraphConnectedTo() {
		ContainersNode containersNode = getFabric().getContainersNode();
		if (getContainer().isRoot() && containersNode != null) {
			// lets find the children
			List<Node> answer = new ArrayList<Node>();
			Container[] list = getContainer().getChildren();
			for (Container container : list) {
				ContainerNode containerNode = containersNode.getContainerNode(container.getId());
				if (containerNode != null) {
					answer.add(containerNode);
				}
			}
			return answer;
		}
		return Collections.emptyList();
	}


	@Override
	public RefreshableUI getRefreshableUI() {
		return super.getRefreshableUI();
	}

	@Override
	public Image getImage() {
		return FabricPlugin.getDefault().getImage("container.png");
	}

	public String getSshUrl() {
		return getContainer().getSshUrl();
	}


	@Override
	protected void checkLoaded() {
		if (hasJmxConnector()) {
			super.checkLoaded();
		}
	}

	@Override
	protected void refreshUIAfterLazyLoad() {
		// we typically lazy load after we've been viewed so lets force a refresh
		refreshUI();
	}

	@Override
	protected void loadChildren() {
// commented out as we currently do not support JMX under a container node
//		if (hasJmxConnector()) {
//			connectionWrapper = new FabricConnectionWrapper(this);
//			addChild(connectionWrapper);
//		}
	}

	protected boolean hasJmxConnector() {
		Container a = getContainer();
		String jmxUrl = a.getJmxUrl();
		boolean provisioned = a.isAlive() && a.isProvisioningComplete();
		return jmxUrl != null && jmxUrl.trim().length()>0 && (provisioned || a.isRoot());
	}
	
	public Container getContainer() {
		return container;
	}

	/**
	 * Returns true if the given JMX domain is supported by the given agent
	 */
	public boolean supportsJmxDomain(String domain) {
		return container.getJmxDomains().contains(domain);
	}


	public boolean matches(Profile profile) {
		Container ag = getContainer();
		Profile[] profiles = ag.getProfiles();
		for (Profile prof : profiles) {
			if (Objects.equal(prof.getId(), profile.getId())) {
				if (Objects.equal(Fabrics.getVersionName(ag.getVersion()), profile.getVersion())) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean matches(Version version) {
		return Objects.equal(Fabrics.getVersionName(getContainer().getVersion()), Fabrics.getVersionName(version));
	}

	@Override
	public void provideContextMenu(IMenuManager menu) {
		Action openTerminalAction = new ActionSupport(Messages.openTerminalLabel, Messages.openTerminalToolTip, FabricPlugin.getDefault().getImageDescriptor("terminal_view.gif")) {
			@Override
			public void run() {
				openTerminal();
			}
		};
		menu.add(openTerminalAction);

		final ImageDescriptor versionImage = FabricPlugin.getDefault().getImageDescriptor("version.png");
		final MenuManager subMenu = new MenuManager("Set Version", versionImage, "org.fusesource.ide.actions.update");
		menu.add(subMenu);

		subMenu.setRemoveAllWhenShown(true);
		subMenu.addMenuListener(new IMenuListener() {

			@Override
			public void menuAboutToShow(IMenuManager manager) {
				Version[] versions = getFabricService().getVersions();
				for (final Version version : versions) {
					Action action = new Action(version.getName(), versionImage) {
						@Override
						public void run() {
							getContainer().setVersion(version);
							refresh();
							getFabric().getVersionsNode().refresh();
						}

					};
					if (matches(version)) {
						action.setEnabled(false);
					}
					subMenu.add(action);
				}
				subMenu.setVisible(true);
			}
		});



		Action startStopAction;
		if (getContainer().isAlive()) {
			startStopAction = new Action(Messages.StopAgentAction, SWT.CHECK) {
				@Override
				public void run() {
					stop();
				}

			};
			startStopAction.setToolTipText(Messages.StopAgentActionToolTip);
			startStopAction.setImageDescriptor(FabricPlugin.getDefault().getImageDescriptor("stop_task.gif"));
		} else {
			startStopAction = new Action(Messages.StartAgentAction, SWT.CHECK) {
				@Override
				public void run() {
					start();
				}

			};
			startStopAction.setToolTipText(Messages.StartAgentActionToolTip);
			startStopAction.setImageDescriptor(FabricPlugin.getDefault().getImageDescriptor("start_task.gif"));
		}
		menu.add(startStopAction);
		menu.add(new Separator());

		if (getContainer().isRoot()) {
			menu.add(new CreateChildContainerAction(this));
		}

		menu.add(new CreateJCloudsContainerAction(getVersionNode(), this, null));
		menu.add(new CreateSshContainerAction(getVersionNode(), this, null));

	}

	protected void openTerminal() {
		openTerminal(getFabric(), getContainer(), this);
	}

	public static void openTerminal(Fabric theFabric, Container theContainer, ITerminalConnectionListener listener) {
		String url = theContainer.getSshUrl();
		String user = theFabric.getUserName();
		String password = theFabric.getPassword();

		if (!url.contains("://")) {
			url = "http://" + url;
		}
		URL sshUrl = null;
		try {
			sshUrl = new URL(url);
		} catch (Exception e) {
			Activator.getLogger().warning("Failed to parse URI: '" + url + "'. " + e, e);
			return;
		}

		String host = sshUrl.getHost();
		int port = sshUrl.getPort();


		// open the terminal view
		IViewPart vp = FabricPlugin.openTerminalView();
		if (vp == null || vp instanceof SshView == false) {
			FabricPlugin.getLogger().error("Unable to open the terminal view!");
			return;
		}

		// get the view
		final SshView connectorView = (SshView)vp;

		connectorView.setPartName(theContainer.getId());

		// add a connection listener
		if (listener != null) {
			connectorView.addConnectionListener(listener);
		}

		// create the connection
		try {
			FabricPlugin.getLogger().debug("Creating the connection if it doesn't exist for host: " + host + " port " + port + " user " + user + " pwd " + password);

			// TODO set the title? open a new view if there's not one already etc?
			connectorView.createConnectionIfNotExists(host,
					port,
					user,
					password);
		} catch (Exception ex) {
			FabricPlugin.getLogger().error("Unable to connect via SSH", ex);
		}
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.server.view.ITerminalConnectionListener#onConnect()
	 */
	@Override
	public void onConnect() {
		FabricPlugin.openTerminalView().setFocus();
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.server.view.ITerminalConnectionListener#onDisconnect()
	 */
	@Override
	public void onDisconnect() {
		// open the terminal view
		IViewPart vp = FabricPlugin.openTerminalView();
		if (vp == null || vp instanceof SshView == false) {
			FabricPlugin.getLogger().error("Unable to open the terminal view!");
			return;
		}

		// get the view
		final SshView connectorView = (SshView)vp;

		connectorView.setPartName(Messages.shellViewLabel);

		// add a connection listener
		connectorView.removeConnectionListener(this);
	}
	/**
	 * Creates a child container
	 */
	public void createContainer(final String name, Profile[] profiles, VersionNode version) {
		try {
			ArrayList<String> profilesList = new ArrayList<String>();
			for (Profile p : profiles) {
				profilesList.add(p.getId());
			}
			
			FabricPlugin.getLogger().debug("About to create child container of " + this + " with name: " + name + " and profiles: " + Arrays.asList(profiles));
			FabricService fabricService = getFabricService();
			CreateContainerOptions options = CreateChildContainerOptions.builder()
					.name(name)
					.parent(getId())
					.version(version.getVersionId())
					.profiles(profilesList)
					.zookeeperUrl(fabricService.getZookeeperUrl())
					.zookeeperPassword(getFabric().getDetails().getZkPassword())
					.jmxUser(getFabric().getDetails().getUserName())
					.jmxPassword(getFabric().getDetails().getPassword())
					.proxyUri(fabricService.getMavenRepoURI()).build();

			CreateContainerMetadata[] newContainers = fabricService.createContainers(options);

//			for(CreateContainerMetadata metadata : newContainers) {
//				Container newContainer = metadata.getContainer();
//				getFabric().setContainerProfiles(newContainer, profiles);
//			}
			getFabric().refreshCreatedAgent(name);
		} catch (Exception e) {
			FabricPlugin.showUserError("Failed to create new child container of " + this, e.getMessage(), e);
		}
	}

	public void start() {
		try {
			getContainer().start();

			// lets refresh the list of agents
			// TODO better refresh stuff should go here!!!
			getFabric().refresh();

		} catch (Exception e) {
			FabricPlugin.showUserError("Failed to start container " + this, e.getMessage(), e);
		}
	}

	public void stop() {
		try {
			getContainer().stop();

			// lets refresh the list of agents
			// TODO better refresh stuff should go here!!!
			getFabric().refresh();

		} catch (Exception e) {
			FabricPlugin.showUserError("Failed to stop container " + this, e.getMessage(), e);
		}
	}

	public void destroy() {
		try {
			getContainer().destroy();

			// lets refresh the list of agents
			// TODO better refresh stuff should go here!!!
			getFabric().refresh();

		} catch (Exception e) {
			FabricPlugin.showUserError("Failed to destroy container " + this, e.getMessage(), e);
		}
	}

	public ContainerTemplate getContainerTemplate() {
		if (containerTemplate == null) {
			Fabric fabric = getFabric();
			containerTemplate = Containers.newContainerTemplate(container, fabric.getUserName(), fabric.getPassword());
			containerTemplate.setLogin(fabric.getUserName());
			containerTemplate.setPassword(fabric.getPassword());
		}
		return containerTemplate;
	}
	
	public void setContainerTemplate(ContainerTemplate agentTemplate) {
		this.containerTemplate = agentTemplate;
	}
	
	public JmxTemplateSupport getJmxTemplate() {
		if (jmxTemplate == null) {
			jmxTemplate = getContainerTemplate().getJmxTemplate();
		}
		return jmxTemplate;
	}
	
	@Override
	public RefreshableNode getParent() {
		return (RefreshableNode) super.getParent();
	}

	public FabricService getFabricService() {
		return getFabric().getFabricService();
	}
}
