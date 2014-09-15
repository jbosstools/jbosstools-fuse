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

package org.fusesource.ide.fabric8.ui.navigator;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.fusesource.ide.commons.Activator;
import org.fusesource.ide.commons.Viewers;
import org.fusesource.ide.commons.tree.GraphableNode;
import org.fusesource.ide.commons.tree.GraphableNodeConnected;
import org.fusesource.ide.commons.tree.HasOwner;
import org.fusesource.ide.commons.tree.HasRefreshableUI;
import org.fusesource.ide.commons.tree.RefreshableNode;
import org.fusesource.ide.commons.tree.RefreshableUI;
import org.fusesource.ide.commons.ui.ContextMenuProvider;
import org.fusesource.ide.commons.ui.actions.ActionSupport;
import org.fusesource.ide.commons.ui.views.ViewPropertySheetPage;
import org.fusesource.ide.commons.util.Objects;
import org.fusesource.ide.fabric8.core.connector.Fabric8Facade;
import org.fusesource.ide.fabric8.core.dto.ContainerDTO;
import org.fusesource.ide.fabric8.core.dto.CreateContainerMetadataDTO;
import org.fusesource.ide.fabric8.core.dto.CreateContainerOptionsDTO;
import org.fusesource.ide.fabric8.core.dto.ProfileDTO;
import org.fusesource.ide.fabric8.core.dto.VersionDTO;
import org.fusesource.ide.fabric8.ui.FabricPlugin;
import org.fusesource.ide.fabric8.ui.actions.CreateChildContainerAction;
import org.fusesource.ide.fabric8.ui.actions.CreateSshContainerAction;
import org.fusesource.ide.fabric8.ui.actions.Messages;
import org.fusesource.ide.fabric8.ui.actions.jclouds.CreateJCloudsContainerAction;
import org.fusesource.ide.fabric8.ui.navigator.properties.ContainerTabViewPage;
import org.fusesource.ide.fabric8.ui.navigator.properties.ContainerTableSheetPage;
import org.fusesource.ide.fabric8.ui.navigator.properties.ContainerViewBean;
import org.fusesource.ide.fabric8.ui.view.logs.FabricLogBrowser;
import org.fusesource.ide.fabric8.ui.view.logs.HasLogBrowser;
import org.fusesource.ide.fabric8.ui.view.logs.ILogBrowser;
import org.fusesource.ide.server.view.ITerminalConnectionListener;
import org.fusesource.ide.server.view.SshView;
import org.jboss.tools.jmx.core.tree.Node;
import org.jboss.tools.jmx.ui.ImageProvider;

import com.google.common.base.Joiner;

public class ContainerNode extends IdBasedFabricNode implements
		HasRefreshableUI, ImageProvider, GraphableNode, GraphableNodeConnected,
		ContextMenuProvider, ITerminalConnectionListener, HasLogBrowser {

	private final ContainerDTO container;
	private ILogBrowser logBrowser;
	
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

	public static ContainerDTO toContainer(Object object) {
		if (object instanceof ContainerDTO) {
			return (ContainerDTO) object;
		}
		ContainerNode node = toContainerNode(object);
		if (node != null) {
			return node.getContainer();
		}
		return null;
	}

	public ContainerNode(RefreshableNode parent, ContainerDTO agent, Fabric fabric) {
		super(parent, fabric, agent.getId());
		this.container = agent;
		setPropertyBean(new ContainerViewBean(agent));

		Joiner joiner = Joiner.on("\n    ");
		List<String> profileIds = agent.getProfileIDs();
		for (String profileId : profileIds) {
			// TODO throws exception!
			// Profile overlay = profile.getOverlay();
			ProfileDTO overlay = getFabricService().getProfile(container.getVersionId(), profileId);
			FabricPlugin.getLogger().debug("Profile: " + overlay);
			FabricPlugin.getLogger().debug("  bundles: " + joiner.join(overlay.getBundles()));
			FabricPlugin.getLogger().debug("  features: " + joiner.join(overlay.getFeatures()));
			FabricPlugin.getLogger().debug("  repos:   " + joiner.join(overlay.getRepositories()));
		}
	}

	public ContainerNode(RefreshableNode parent, ContainerNode agentNode) {
		this(parent, agentNode.getContainer(), agentNode.getFabric());
	}

	public ContainerNode(FabricNodeSupport agents, ContainerDTO agent) {
		this(agents, agent, agents.getFabric());
	}

	public VersionNode getVersionNode() {
		return getFabric().getVersionNode(container.getVersionId());
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
		List<String> profileIds = getContainer().getProfileIDs();
		List<ProfileDTO> profiles = new ArrayList<ProfileDTO>();
		for (String id : profileIds) {
			ProfileDTO p = getFabricService().getProfile(getVersionNode().getVersionId(), id);
			if (p != null) profiles.add(p);
		}
		Collection<ProfileNode> profileNodes = getFabric().getProfileNodes(profiles);
		for (ProfileNode profileNode : profileNodes) {
			profileNode.addAndDescendants(answer);
		}
		return new ArrayList<Node>(answer);
	}

	@Override
	public Collection<Node> getGraphConnectedTo() {
		ContainersNode containersNode = getFabric().getContainersNode();
		if (getContainer().isRoot() && containersNode != null) {
			// lets find the children
			List<Node> answer = new ArrayList<Node>();
			List<String> list = getContainer().getChildrenIds();
			for (String containerId : list) {
				ContainerNode containerNode = containersNode.getContainerNode(containerId);
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
	protected void loadChildren() {
		// commented out as we currently do not support JMX under a container
		// node
		// if (hasJmxConnector()) {
		// connectionWrapper = new FabricConnectionWrapper(this);
		// addChild(connectionWrapper);
		// }
	}

	protected boolean hasJmxConnector() {
		ContainerDTO a = getContainer();
		String jmxUrl = a.getJMXUrl();
		boolean provisioned = a.isAlive() && a.isProvisioningComplete();
		return jmxUrl != null && jmxUrl.trim().length() > 0
				&& (provisioned || a.isRoot());
	}

	public ContainerDTO getContainer() {
		return container;
	}

	/**
	 * Returns true if the given JMX domain is supported by the given agent
	 */
	public boolean supportsJmxDomain(String domain) {
		return container.getJmxDomains().contains(domain);
	}

	public boolean matches(ProfileDTO profile) {
		ContainerDTO ag = getContainer();
		List<String> profileIds = ag.getProfileIDs();
		for (String pId : profileIds) {
			if (Objects.equal(pId, profile.getId())) {
				if (Objects.equal(ag.getVersionId(), profile.getVersionId())) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean matches(VersionDTO version) {
		return Objects.equal(getContainer().getVersionId(), Fabrics.getVersionName(version));
	}

	@Override
	public void provideContextMenu(IMenuManager menu) {
		Action openTerminalAction = new ActionSupport(
				Messages.openTerminalLabel, Messages.openTerminalToolTip,
				FabricPlugin.getDefault().getImageDescriptor(
						"terminal_view.gif")) {
			@Override
			public void run() {
				openTerminal();
			}
		};
		menu.add(openTerminalAction);

		final ImageDescriptor versionImage = FabricPlugin.getDefault()
				.getImageDescriptor("version.png");
		final MenuManager subMenu = new MenuManager("Set Version",
				versionImage, "org.fusesource.ide.actions.update");
		menu.add(subMenu);

		subMenu.setRemoveAllWhenShown(true);
		subMenu.addMenuListener(new IMenuListener() {

			@Override
			public void menuAboutToShow(IMenuManager manager) {
				UIJob loadJob = new UIJob("Loading data...") {
					@Override
					public IStatus runInUIThread(IProgressMonitor monitor) {
						List<Node> versions = getFabric().getVersionsNode().getChildrenList();
						for (final Node node : versions) {
							if (node != null && node instanceof VersionNode) {
								final VersionNode version = (VersionNode)node;
								Action action = new Action(version.getVersionId(), versionImage) {
									@Override
									public void run() {
										getContainer().setVersion(version.getVersion());
										getFabric().getVersionsNode().refresh();
									}	
								};
								if (matches(version.getVersion())) {
									action.setEnabled(false);
								}
								subMenu.add(action);
							}
						}
						subMenu.update(true);
						return Status.OK_STATUS;
					}
				};
				loadJob.schedule();
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
			startStopAction.setImageDescriptor(FabricPlugin.getDefault()
					.getImageDescriptor("stop_task.gif"));
		} else {
			startStopAction = new Action(Messages.StartAgentAction, SWT.CHECK) {
				@Override
				public void run() {
					start();
				}

			};
			startStopAction.setToolTipText(Messages.StartAgentActionToolTip);
			startStopAction.setImageDescriptor(FabricPlugin.getDefault()
					.getImageDescriptor("start_task.gif"));
		}
		if (!getContainer().isRoot()) {
			menu.add(startStopAction);
		}

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

	public static void openTerminal(Fabric theFabric, ContainerDTO theContainer,
			ITerminalConnectionListener listener) {
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
			Activator.getLogger().warning(
					"Failed to parse URI: '" + url + "'. " + e, e);
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
		final SshView connectorView = (SshView) vp;

		connectorView.setPartName(theContainer.getId());

		// add a connection listener
		if (listener != null) {
			connectorView.addConnectionListener(listener);
		}

		// create the connection
		try {
			FabricPlugin.getLogger().debug(
					"Creating the connection if it doesn't exist for host: "
							+ host + " port " + port + " user " + user
							+ " pwd " + password);

			// TODO set the title? open a new view if there's not one already
			// etc?
			connectorView.createConnectionIfNotExists(host, port, user,
					password);
		} catch (Exception ex) {
			FabricPlugin.getLogger().error("Unable to connect via SSH", ex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fusesource.ide.server.view.ITerminalConnectionListener#onConnect()
	 */
	@Override
	public void onConnect() {
		FabricPlugin.openTerminalView().setFocus();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fusesource.ide.server.view.ITerminalConnectionListener#onDisconnect()
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
		final SshView connectorView = (SshView) vp;

		connectorView.setPartName(Messages.shellViewLabel);

		// add a connection listener
		connectorView.removeConnectionListener(this);
	}

	/**
	 * Creates a child container
	 */
	public void createContainer(final String name, List<ProfileDTO> profiles,
			VersionNode version) {
		try {
			ArrayList<String> profilesList = new ArrayList<String>();
			for (ProfileDTO p : profiles) {
				profilesList.add(p.getId());
			}

			FabricPlugin.getLogger().debug(
					"About to create child container of " + this
							+ " with name: " + name + " and profiles: "
							+ Arrays.asList(profiles));
			Fabric8Facade fabricService = getFabricService();
			CreateContainerOptionsDTO options = CreateContainerOptionsDTO
					.builder()
					.name(name)
					.parent(getId())
					.version(version.getVersionId())
					.profiles(Collections.unmodifiableSet(new HashSet<String>(profilesList)))
					.zookeeperUrl(fabricService.getZookeeperUrl())
					.zookeeperPassword(getFabric().getDetails().getZkPassword())
					.jmxUser(getFabric().getDetails().getUserName())
					.jmxPassword(getFabric().getDetails().getPassword())
					.proxyUri(fabricService.getMavenRepoURI()).build();

			CreateContainerMetadataDTO[] newContainers = fabricService.createContainers(options);

			// for(CreateContainerMetadata metadata : newContainers) {
			// Container newContainer = metadata.getContainer();
			// getFabric().setContainerProfiles(newContainer, profiles);
			// }
			getFabric().refreshCreatedAgent(name);
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					getFabric().getContainersNode().refresh();
					ViewPropertySheetPage p = getFabric().getContainersNode()
							.getPropertySourceTablePage();
					if (p instanceof ContainerTableSheetPage) {
						((ContainerTableSheetPage) p).updateData();
						Viewers.refresh(((ContainerTableSheetPage) p)
								.getTableView().getViewer());
					}
					refresh();
				}
			});
		} catch (Exception e) {
			FabricPlugin.showUserError(
					"Failed to create new child container of " + this,
					e.getMessage(), e);
		}
	}

	public void start() {
		try {
			getFabricService().startContainer(getContainer());

			// lets refresh the list of agents
			// TODO better refresh stuff should go here!!!
			getFabric().refresh();

		} catch (Exception e) {
			FabricPlugin.showUserError("Failed to start container " + this,
					e.getMessage(), e);
		}
	}

	public void stop() {
		try {
			getFabricService().stopContainer(getContainer());

			// lets refresh the list of agents
			// TODO better refresh stuff should go here!!!
			getFabric().refresh();

		} catch (Exception e) {
			FabricPlugin.showUserError("Failed to stop container " + this,
					e.getMessage(), e);
		}
	}

	public void destroy() {
		try {
			getFabricService().destroyContainer(getContainer());

			// lets refresh the list of agents
			// TODO better refresh stuff should go here!!!
			getFabric().refresh();

		} catch (Exception e) {
			FabricPlugin.showUserError("Failed to destroy container " + this,
					e.getMessage(), e);
		}
	}

	@Override
	public RefreshableNode getParent() {
		return (RefreshableNode) super.getParent();
	}

	public Fabric8Facade getFabricService() {
		return getFabric().getFabricService();
	}
	
	@Override
	public ILogBrowser getLogBrowser() {
		if (logBrowser == null) {
			logBrowser = new FabricLogBrowser(this);
		}
		return logBrowser;
	}
}
