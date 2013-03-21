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


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.fusesource.fabric.api.Container;
import org.fusesource.fabric.api.CreateContainerMetadata;
import org.fusesource.fabric.api.CreateContainerOptions;
import org.fusesource.fabric.api.CreationStateListener;
import org.fusesource.fabric.api.FabricService;
import org.fusesource.fabric.api.Profile;
import org.fusesource.fabric.api.ProfileStatus;
import org.fusesource.fabric.api.Version;
import org.fusesource.fabric.zookeeper.IZKClient;
import org.fusesource.ide.commons.Viewers;
import org.fusesource.ide.commons.jobs.Jobs;
import org.fusesource.ide.commons.tree.GraphableNode;
import org.fusesource.ide.commons.tree.HasRefreshableUI;
import org.fusesource.ide.commons.tree.HasViewer;
import org.fusesource.ide.commons.tree.Node;
import org.fusesource.ide.commons.tree.RefreshableCollectionNode;
import org.fusesource.ide.commons.tree.RefreshableUI;
import org.fusesource.ide.commons.ui.ImageProvider;
import org.fusesource.ide.commons.ui.actions.HasDoubleClickAction;
import org.fusesource.ide.fabric.FabricConnector;
import org.fusesource.ide.fabric.FabricPlugin;
import org.fusesource.ide.fabric.actions.CreateChildContainerAction;
import org.fusesource.ide.fabric.actions.CreateSshContainerAction;
import org.fusesource.ide.fabric.actions.FabricConnectAction;
import org.fusesource.ide.fabric.actions.FabricDetails;
import org.fusesource.ide.fabric.actions.FabricDetailsDeleteAction;
import org.fusesource.ide.fabric.actions.FabricDetailsEditAction;
import org.fusesource.ide.fabric.actions.FabricDisconnectAction;
import org.fusesource.ide.fabric.actions.jclouds.CreateJCloudsContainerAction;
import org.fusesource.ide.fabric.views.logs.FabricLogBrowser;
import org.fusesource.ide.fabric.views.logs.HasLogBrowser;
import org.fusesource.ide.fabric.views.logs.ILogBrowser;
import org.fusesource.ide.jmx.ui.internal.views.navigator.ContextMenuProvider;

import com.google.common.base.Objects;

public class Fabric extends RefreshableCollectionNode implements ImageProvider, HasRefreshableUI, GraphableNode, HasLogBrowser, ContextMenuProvider, HasDoubleClickAction {
	public static final String DEFAULT_NAME = "Fabric";

	private static final boolean changeSelectionOnCreate = false;
	private static final boolean changeSelectionOnConnect = false;

	private String userName = "admin";
	private String password = "admin";
	private FabricConnector connector;
	private final Fabrics fabrics;

	private ContainersNode containersNode;
	private VersionsNode versionsNode;

	private FabricLogBrowser logBrowser;

	private FabricDetailsEditAction editAction;
	private FabricDetailsDeleteAction deleteAction;

	private FabricDetails details;

	private boolean selectContainersOnCreate = true;
	private Set<Runnable> fabricUpdateTasks = new HashSet<Runnable>();

	public Fabric(Fabrics fabrics, FabricDetails details) {
		super(fabrics);
		this.fabrics = fabrics;
		this.details = details;
		this.userName = details.getUserName();
		this.password = details.getPassword();

		editAction = new FabricDetailsEditAction() {

			@Override
			protected FabricDetails getSelectedFabricDetails() {
				return getDetails();
			}

			@Override
			protected void onFabricDetailsEdited(FabricDetails found) {
				setDetails(found);
			}
		};

		// TODO
		// setDoubleClickAction(editAction);

		deleteAction = new FabricDetailsDeleteAction() {
			@Override
			protected FabricDetails getSelectedFabricDetails() {
				return getDetails();
			}
		};
	}


	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		if (adapter == IPropertySheetPage.class) {
			return new FabricTabViewPage(this);
		}
		return super.getAdapter(adapter);
	}

	@Override
    public boolean requiresContentsPropertyPage() {
        return false;
    }


    public FabricService getFabricService() {
		if (connector != null) {
			return connector.getFabricService();
		}
		return null;
	}

	@Override
	public String toString() {
		return getName();
	}

	public FabricDetails getDetails() {
		return details;
	}

	public void addFabricUpdateRunnable(Runnable runnable) {
		fabricUpdateTasks.add(runnable);

		System.out.println("=============== Now have " + fabricUpdateTasks.size() + " runnables");
	}

	public void removeFabricUpdateRunnable(Runnable runnable) {
		fabricUpdateTasks.remove(runnable);
	}

	public void setDetails(FabricDetails details) {
		String oldUrls = this.details.getUrls();
		this.details = details;
		String newUrls = details.getUrls();
		if (!Objects.equal(oldUrls, newUrls)) {
			if (connector != null) {
				connector.dispose();
			}
			connector = new FabricConnector(this);
			setPropertyBean(connector);
		}

	}

	@Override
	public RefreshableUI getRefreshableUI() {
		return fabrics.getRefreshableUI();
	}

	@Override
	public Image getImage() {
		return FabricPlugin.getDefault().getImage("fabric.png");
	}

	@Override
	protected void loadChildren() {
		if (!isConnected()) return;

		containersNode = new ContainersNode(this);
		versionsNode = new VersionsNode(this);

		addChild(containersNode);
		addChild(versionsNode);
	}


	@Override
	public void provideContextMenu(IMenuManager menu) {
		menu.add(new FabricConnectAction(this));
		menu.add(new FabricDisconnectAction(this));
		menu.add(new Separator());
		menu.add(editAction);
		menu.add(deleteAction);
		if (isConnected()) {
			menu.add(new Separator());
			CreateChildContainerAction.addIfSingleRootContainer(menu, this);
			menu.add(new CreateJCloudsContainerAction(this));
			menu.add(new CreateSshContainerAction(this));
		}
	}


	/*
	@Override
	protected PropertySourceTableSheetPage createPropertySourceTableSheetPage() {
		return new ContainerTableSheetPage(this);
	}
	 */

	public VersionNode getDefaultVersionNode() {
		if (!isConnected() || versionsNode == null) return null;
		return versionsNode.getDefaultVersionNode();
	}

	@Override
	public List<Node> getChildrenGraph() {
		Set<Node> answer = new HashSet<Node>();

		if (isConnected()) {
			// lets add the versions too???
			ContainersNode node = getContainersNode();
			if (node != null) {
				answer.addAll(node.getChildrenList());
			}

			/*
			//addChildren(answer, agentsNode);
			Node[] children = versionsNode.getChildren();
			if (children.length == 1) {
				addChildren(answer, children[0], false);
			} else {
				addChildren(answer, versionsNode, true);
			}
			 */
		}

		return new ArrayList<Node>(answer);
	}


	@Override
	public ILogBrowser getLogBrowser() {
		if (logBrowser == null) {
			// TODO disable elastic search
			// logBrowser = new FabricLogBrowser(this);
		}
		return logBrowser;
	}


	@Override
	public Action getDoubleClickAction() {
		return new FabricConnectAction(this);
	}

	protected void addChildren(Set<Node> set, Node node, boolean showVersions) {
		if (node != null && isConnected()) {
			List<Node> childrenList = node.getChildrenList();
			for (Node child : childrenList) {
				if (child instanceof ProfileNode || child instanceof VersionNode) {
					addChildren(set, child, showVersions);
				} else if (!(child instanceof ContainerNode) && (!showVersions && child instanceof VersionNode)) {
					continue;
				}
				set.add(child);
			}
		}
	}

	public List<ContainerNode> getAgentsFor(Profile profile) {
		List<ContainerNode> answer = new ArrayList<ContainerNode>();
		if (containersNode != null && isConnected()) {
			Node[] children = containersNode.getChildren();
			for (Node node : children) {
				if (node instanceof ContainerNode) {
					ContainerNode agent = (ContainerNode) node;
					if (agent.matches(profile)) {
						answer.add(agent);
					}
				}
			}
		}
		return answer;
	}

	public ContainersNode getContainersNode() {
		return containersNode;
	}

	public VersionsNode getVersionsNode() {
		return versionsNode;
	}

	public String getName() {
		return details.getName();
	}

	/*
	public void setName(String name) {
		this.name = name;
	}
	 */

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public FabricConnector getConnector() {
		return connector;
	}

	public void setConnector(FabricConnector conn) {
		if (conn == null && this.connector != null) {
			this.connector.dispose();
		}
		this.connector = conn;
		setPropertyBean(this.connector);
	}

	public boolean isConnected() {
		if (this.connector == null) return false;
		return connector.isConnected();
	}

	public Collection<ProfileNode> getProfileNodes(Profile[] profiles) {
		Set<ProfileNode> answer = new HashSet<ProfileNode>();
		if (isConnected()) {
			for (Profile profile : profiles) {
				VersionNode version = getVersionNode(profile.getVersion());
				if (version != null) {
					ProfileNode profileNode = version.getProfileNode(profile);
					if (profileNode != null) {
						answer.add(profileNode);
					}
				}
			}
		}
		return answer;
	}

	public VersionNode getVersionNode(String version) {
		if (versionsNode != null) {
			return versionsNode.getVersionNode(version);
		}
		return null;
	}


	public Version getVersion(String versionName) {
		VersionNode node = getVersionNode(versionName);
		if (node != null) {
			return node.getVersion();
		}
		return null;
	}

	public void createContainer(final Container agent, final Profile[] profiles, final CreateContainerOptions args) {
		if (!isConnected()) return;
		try {
			if (agent != null) {
				args.setParent(agent.getId());
			}
			Jobs.schedule(new Job("Create container") {

				@Override
				protected IStatus run(final IProgressMonitor monitor) {
					try {
						args.setCreationStateListener(new CreationStateListener() {

							@Override
							public void onStateChange(String message) {
								monitor.subTask(message);
							}
						});
						FabricService fabricService = getFabricService();
						final CreateContainerMetadata[] newAgents;
						if (agent != null) {
							newAgents = fabricService.createContainers(args);

						} else {
							newAgents = fabricService.createContainers(args);
						}
						Viewers.async(new Runnable() {

							@Override
							public void run() {
								for(CreateContainerMetadata metadata : newAgents) {
									Container newContainer = metadata.getContainer();
									if (newContainer != null) {
										setContainerProfiles(newContainer, profiles);
										refreshCreatedAgent(args.getName());
									}
								}
							}

						});
						return Status.OK_STATUS;
					} catch (Throwable e) {
						return new Status(Status.ERROR, FabricPlugin.PLUGIN_ID, "Failed to create container: " + e, e);
					}
				}

			});

		} catch (Exception e) {
			FabricPlugin.showUserError("Failed to create new child container of " + this, e.getMessage(), e);
		}
	}


	/**
	 * Sets the active profiles on the given container, updating the version if required
	 */
	public void setContainerProfiles(Container newContainer, Profile[] profiles) {
		if (!isConnected() || newContainer == null) return;
		Version version = getProfilesVersion(profiles);
		if (version != null) {
			newContainer.setVersion(version);
		}
		newContainer.setProfiles(profiles);
	}


	/** Returns the version for the profiles (assuming they are all part of the same version */
	public Version getProfilesVersion(Profile[] profiles) {
		Version version = null;
		if (isConnected()) {
			// lets assume all the versions are the same
			for (Profile profile : profiles) {
				String versionName = profile.getVersion();
				if (versionName != null) {
					version = getVersion(versionName);
				}
			}
		}
		return version;
	}

	public void refreshCreatedAgent(final String agentName) {
		if (!isConnected()) return;
		final Fabric fabric = this;
		//refresh();
		final ContainersNode containers = getContainersNode();
		if (containers != null) {
			containers.refresh();

			// expand the agents
			if (changeSelectionOnCreate) {
				RefreshableUI refreshableUI = getRefreshableUI();
				if (refreshableUI instanceof HasViewer) {
					HasViewer hv = (HasViewer) refreshableUI;
					Viewer viewer = hv.getViewer();
					if (viewer instanceof CommonViewer) {
						final CommonViewer cv = (CommonViewer) viewer;

						Display.getDefault().asyncExec(new Runnable() {


							@Override
							public void run() {
								VersionsNode versionsNode = fabric.getVersionsNode();
								cv.setExpandedElements(new Object[] { fabric, containers, versionsNode });


								ContainerNode newNode = containers.getContainerNode(agentName);
								if (selectContainersOnCreate || newNode == null) {
									cv.setSelection(new StructuredSelection(containers));
								} else {
									cv.setSelection(new StructuredSelection(newNode));
								}
							}});
					}
				}
			}
		}

	}

	public String getNewAgentName() {
		return "container" + (getContainersNode().getChildCount() + 1);
	}

	public void onConnect() {
		RefreshableUI refreshableUI = getRefreshableUI();
		if (refreshableUI instanceof HasViewer) {
			HasViewer hv = (HasViewer) refreshableUI;
			Viewer viewer = hv.getViewer();
			final Fabric fabric = this;
			if (viewer instanceof CommonViewer) {
				final CommonViewer cv = (CommonViewer) viewer;

				Display.getDefault().asyncExec(new Runnable() {


					@Override
					public void run() {
						ContainersNode containersNode = getContainersNode();
						VersionsNode versionsNode = getVersionsNode();
						if (containersNode != null) {
							cv.setExpandedElements(new Object[] { fabric, containersNode });
							if (changeSelectionOnConnect) {
								cv.setSelection(new StructuredSelection(containersNode));
							}
						}
					}});
			}
		}

	}

	public void onDisconnect() {
		// nothing to do
		FabricConnector conn = getConnector();
		if (conn != null) {
			conn.dispose();
		}
		setConnector(null);
		refresh();
	}

	public List<ContainerNode> getRootContainers() {
		List<ContainerNode> answer = new ArrayList<ContainerNode>();
		if (containersNode != null) {
			for (ContainerNode node : containersNode.getContainerNodes()) {
				if (node.getContainer().isRoot()) {
					answer.add(node);
				}
			}
		}
		return answer ;
	}

	/**
	 * Notification that the tree has updated
	 */
	public void onZooKeeperUpdate() {
		Viewers.async(new Runnable() {

			@Override
			public void run() {
				//FabricPlugin.getLogger().info("Refreshing due to ZK change!");
				System.out.println("Refreshing Fabric " + this + " due to ZK change!");

				getContainersNode().refresh();

				for (Runnable task : fabricUpdateTasks) {
					task.run();
				}
			}
		});
	}

	public IZKClient getZooKeeper() {
		if (connector != null) {
			return connector.getZooKeeper();
		}
		return null;
	}


	public Collection<ProfileStatus> getProfileStatuses() {
		FabricService service = getFabricService();
		if (service == null || !isConnected()) {
			return new ArrayList<ProfileStatus>();
		}
		return service.getFabricStatus().getProfileStatusMap().values();
	}
}
