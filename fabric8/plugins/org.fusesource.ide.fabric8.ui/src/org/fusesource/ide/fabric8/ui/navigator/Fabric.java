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

import io.fabric8.api.CreateContainerOptions;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.fusesource.ide.commons.Viewers;
import org.fusesource.ide.commons.jobs.Jobs;
import org.fusesource.ide.commons.tree.GraphableNode;
import org.fusesource.ide.commons.tree.HasRefreshableUI;
import org.fusesource.ide.commons.tree.HasViewer;
import org.fusesource.ide.commons.tree.RefreshableUI;
import org.fusesource.ide.commons.ui.ContextMenuProvider;
import org.fusesource.ide.commons.ui.actions.HasDoubleClickAction;
import org.fusesource.ide.fabric8.core.connector.Fabric8Facade;
import org.fusesource.ide.fabric8.core.dto.ContainerDTO;
import org.fusesource.ide.fabric8.core.dto.CreateContainerMetadataDTO;
import org.fusesource.ide.fabric8.core.dto.CreateContainerOptionsDTO;
import org.fusesource.ide.fabric8.core.dto.ProfileDTO;
import org.fusesource.ide.fabric8.core.dto.ProfileStatusDTO;
import org.fusesource.ide.fabric8.core.dto.VersionDTO;
import org.fusesource.ide.fabric8.ui.FabricConnectionListener;
import org.fusesource.ide.fabric8.ui.FabricConnector;
import org.fusesource.ide.fabric8.ui.FabricPlugin;
import org.fusesource.ide.fabric8.ui.actions.FabricConnectAction;
import org.fusesource.ide.fabric8.ui.actions.FabricDetails;
import org.fusesource.ide.fabric8.ui.actions.FabricDetailsDeleteAction;
import org.fusesource.ide.fabric8.ui.actions.FabricDetailsEditAction;
import org.fusesource.ide.fabric8.ui.actions.FabricDisconnectAction;
import org.fusesource.ide.fabric8.ui.navigator.properties.FabricTabViewPage;
import org.fusesource.ide.fabric8.ui.view.logs.FabricLogBrowser;
import org.fusesource.ide.fabric8.ui.view.logs.HasLogBrowser;
import org.fusesource.ide.fabric8.ui.view.logs.ILogBrowser;
import org.jboss.tools.jmx.core.tree.Node;
import org.jboss.tools.jmx.ui.ImageProvider;

import com.google.common.base.Objects;

public class Fabric extends FabricNodeSupport implements ImageProvider,
		HasRefreshableUI, GraphableNode, ContextMenuProvider,
		HasDoubleClickAction, FabricConnectionListener, HasLogBrowser {
	public static final String DEFAULT_NAME = "Fabric";

	private static final boolean changeSelectionOnCreate = false;
	private static final boolean changeSelectionOnConnect = false;

	private String userName = "admin";
	private String password = "admin";
	private FabricConnector connector;
	private final Fabrics fabrics;

	private ContainersNode containersNode;
	private VersionsNode versionsNode;

	private FabricDetailsEditAction editAction;
	private FabricDetailsDeleteAction deleteAction;

	private FabricDetails details;

	private ILogBrowser logBrowser;
	
	private boolean selectContainersOnCreate = true;
	private Set<Runnable> fabricUpdateTasks = new HashSet<Runnable>();

	public Fabric(Fabrics fabrics, FabricDetails details) {
		super(fabrics, (Fabric)null);
		this.fabrics = fabrics;
		this.details = details;
		this.userName = details.getUserName();
		this.password = details.getPassword();
		super.setFabric(this);
		
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

	public Fabric8Facade getFabricService() {
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

		FabricPlugin.getLogger().debug(
				"=============== Now have " + fabricUpdateTasks.size()
						+ " runnables");
	}

	public void removeFabricUpdateRunnable(Runnable runnable) {
		fabricUpdateTasks.remove(runnable);
	}

	public void setDetails(FabricDetails details) {
		String oldUrls = this.details.getUrls();
		this.details = details;
		String newUrls = details.getUrls();
		if (!Objects.equal(oldUrls, newUrls)) {
			setConnector(new FabricConnector(this));
		}
	}

	@Override
	public RefreshableUI getRefreshableUI() {
		return fabrics.getRefreshableUI();
	}

	@Override
	public Image getImage() {
		return FabricPlugin.getDefault().getImage("fabric8.png");
	}

	@Override
	protected synchronized void loadChildren() {
		clearChildren();

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
	}

	/*
	 * @Override protected PropertySourceTableSheetPage
	 * createPropertySourceTableSheetPage() { return new
	 * ContainerTableSheetPage(this); }
	 */

	public VersionNode getDefaultVersionNode() {
		if (!isConnected() || versionsNode == null)
			return null;
		return versionsNode.getDefaultVersionNode();
	}

	@Override
	public List<Node> getChildrenGraph() {
		Set<Node> answer = new HashSet<Node>();

		if (isConnected()) {
			answer.add(this);
			answer.addAll(getChildrenList());
		}

		return new ArrayList<Node>(answer);
	}

	@Override
	public Action getDoubleClickAction() {
		return new FabricConnectAction(this);
	}

	protected void addChildren(Set<Node> set, Node node, boolean showVersions) {
		if (node != null && isConnected()) {
			List<Node> childrenList = node.getChildrenList();
			for (Node child : childrenList) {
				if (child instanceof ProfileNode
						|| child instanceof VersionNode) {
					addChildren(set, child, showVersions);
				} else if (!(child instanceof ContainerNode)
						&& (!showVersions && child instanceof VersionNode)) {
					continue;
				}
				set.add(child);
			}
		}
	}

	public List<ContainerNode> getAgentsFor(ProfileDTO profile) {
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
	 * public void setName(String name) { this.name = name; }
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

	/**
	 * sets the connector
	 * 
	 * @param conn
	 */
	public void setConnector(FabricConnector conn) {
		if (conn == null && this.connector != null) {
			this.connector.removeFabricConnectionListener(this);
		}
		this.connector = conn;
		if (this.connector != null) this.connector.addFabricConnectionListener(this);
	}

	public boolean isConnected() {
		if (this.connector == null) return false;
		return connector.isConnected();
	}

	public Collection<ProfileNode> getProfileNodes(List<ProfileDTO> profiles) {
		Set<ProfileNode> answer = new HashSet<ProfileNode>();
		if (isConnected()) {
			for (ProfileDTO profile : profiles) {
				VersionNode version = getVersionNode(profile.getVersionId());
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

	public VersionDTO getVersion(String versionName) {
		VersionNode node = getVersionNode(versionName);
		if (node != null) {
			return node.getVersion();
		}
		return null;
	}

	public void createContainer(final ContainerDTO agent,
			final ProfileDTO[] profiles, final CreateContainerOptionsDTO args) {
		if (!isConnected()) return;
		try {
			if (agent != null) {
				// args.setParent(agent.getId());
			}
			Jobs.schedule(new Job("Create container") {

				@Override
				protected IStatus run(final IProgressMonitor monitor) {
					try {
						// args.setCreationStateListener(new
						// CreationStateListener() {
						//
						// @Override
						// public void onStateChange(String message) {
						// monitor.subTask(message);
						// }
						// });
						Fabric8Facade fabricService = getFabricService();
						final CreateContainerMetadataDTO[] newAgents;
						newAgents = fabricService.createContainers(args);
						Viewers.async(new Runnable() {

							@Override
							public void run() {
								for (CreateContainerMetadataDTO metadata : newAgents) {
									ContainerDTO newContainer = metadata.getContainer();
									if (newContainer != null) {
										setContainerProfiles(newContainer, profiles);
										refreshCreatedAgent(args.getName());
									}
								}
							}

						});
						return Status.OK_STATUS;
					} catch (Throwable e) {
						return new Status(Status.ERROR, FabricPlugin.PLUGIN_ID,
								"Failed to create container: " + e, e);
					}
				}

			});

		} catch (Exception e) {
			FabricPlugin.showUserError(
					"Failed to create new child container of " + this,
					e.getMessage(), e);
		}
	}

	public void createContainer(final ContainerDTO agent,
			final ProfileDTO[] profiles, final CreateContainerOptions args) {
		if (!isConnected()) return;
		try {
			if (agent != null) {
				// args.setParent(agent.getId());
			}
			Jobs.schedule(new Job("Create container") {

				@Override
				protected IStatus run(final IProgressMonitor monitor) {
					try {
						// args.setCreationStateListener(new
						// CreationStateListener() {
						//
						// @Override
						// public void onStateChange(String message) {
						// monitor.subTask(message);
						// }
						// });
						Fabric8Facade fabricService = getFabricService();
						final CreateContainerMetadataDTO[] newAgents;
						newAgents = fabricService.createContainers(args);
						Viewers.async(new Runnable() {

							@Override
							public void run() {
								for (CreateContainerMetadataDTO metadata : newAgents) {
									ContainerDTO newContainer = metadata.getContainer();
									if (newContainer != null) {
										setContainerProfiles(newContainer, profiles);
										refreshCreatedAgent(args.getName());
									}
								}
							}

						});
						return Status.OK_STATUS;
					} catch (Throwable e) {
						return new Status(Status.ERROR, FabricPlugin.PLUGIN_ID,
								"Failed to create container: " + e, e);
					}
				}

			});

		} catch (Exception e) {
			FabricPlugin.showUserError(
					"Failed to create new child container of " + this,
					e.getMessage(), e);
		}
	}
	
	/**
	 * Sets the active profiles on the given container, updating the version if
	 * required
	 */
	public void setContainerProfiles(ContainerDTO newContainer, ProfileDTO[] profiles) {
		if (!isConnected() || newContainer == null)
			return;
		VersionDTO version = getProfilesVersion(profiles);
		if (version != null) {
			newContainer.setVersion(version);
		}
		newContainer.setProfileDTOs(Arrays.asList(profiles));
	}

	/**
	 * Returns the version for the profiles (assuming they are all part of the
	 * same version
	 */
	public VersionDTO getProfilesVersion(ProfileDTO[] profiles) {
		VersionDTO version = null;
		if (isConnected()) {
			// lets assume all the versions are the same
			for (ProfileDTO profile : profiles) {
				String versionName = profile.getVersionId();
				if (versionName != null) {
					version = getVersion(versionName);
				}
			}
		}
		return version;
	}

	public void refreshCreatedAgent(final String agentName) {
		if (!isConnected())
			return;
		final Fabric fabric = this;
		// refresh();
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
								VersionsNode versionsNode = fabric
										.getVersionsNode();
								cv.setExpandedElements(new Object[] { fabric,
										containers, versionsNode });

								ContainerNode newNode = containers
										.getContainerNode(agentName);
								if (selectContainersOnCreate || newNode == null) {
									cv.setSelection(new StructuredSelection(
											containers));
								} else {
									cv.setSelection(new StructuredSelection(
											newNode));
								}
							}
						});
					}
				}
			}
		}

	}

	public String getNewAgentName() {
		return "container" + (getContainersNode().getChildCount() + 1);
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
		return answer;
	}

	/**
	 * Notification that the tree has updated
	 */
	public void onZooKeeperUpdate() {
		Viewers.async(new Runnable() {

			@Override
			public void run() {
				// FabricPlugin.getLogger().info("Refreshing due to ZK change!");
				FabricPlugin.getLogger().debug(
						"Refreshing Fabric " + this + " due to ZK change!");

				getContainersNode().refresh();

				for (Runnable task : fabricUpdateTasks) {
					task.run();
				}
			}
		});
	}

	public Collection<ProfileStatusDTO> getProfileStatuses() {
		Fabric8Facade service = getFabricService();
		if (service == null || 
			service.getFabricStatus() == null ||
			!isConnected()) {
			return new ArrayList<ProfileStatusDTO>();
		}
		return service.getFabricStatus().getProfileStatusMap().values();
	}
	
	/* (non-Javadoc)
	* @see org.fusesource.ide.fabric8.ui.FabricConnectionListener#onFabricConnected()
	*/
	@Override
	public void onFabricConnected() {
		RefreshableUI refreshableUI = getRefreshableUI();
		if (refreshableUI instanceof HasViewer) {
			HasViewer hv = (HasViewer) refreshableUI;
			Viewer viewer = hv.getViewer();
			final Fabric fabric = this;
			if (viewer instanceof CommonViewer) {
				final CommonViewer cv = (CommonViewer) viewer;

				UIJob job = new UIJob("Connecting to " + this + "...") {
					
					/* (non-Javadoc)
					 * @see org.eclipse.ui.progress.UIJob#runInUIThread(org.eclipse.core.runtime.IProgressMonitor)
					 */
					@Override
					public IStatus runInUIThread(IProgressMonitor monitor) {
						cv.expandToLevel(fabric, 1); // just showing COntainers and Versions Nodes
						setPropertyBean(Fabric.this.connector);
						return Status.OK_STATUS;
					}
				};
				job.schedule();
			}
		}
	}
	
	/* (non-Javadoc)
	* @see org.fusesource.ide.fabric8.ui.FabricConnectionListener#onFabricDisconnected()
	*/
	@Override
	public void onFabricDisconnected() {
		FabricConnector conn = getConnector();
		if (conn != null) {
			conn.dispose();
		}
		setConnector(null);
		setPropertyBean(connector);
		refresh();
	}
	
	@Override
	public ILogBrowser getLogBrowser() {
		if (logBrowser == null) {
			logBrowser = new FabricLogBrowser(this);
		}
		return logBrowser;
	}
}
