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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.maven.model.Model;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.IPropertySource;
import org.fusesource.fabric.api.Profile;
import org.fusesource.ide.commons.properties.PropertySources;
import org.fusesource.ide.commons.tree.GraphableNode;
import org.fusesource.ide.commons.tree.HasRefreshableUI;
import org.fusesource.ide.commons.tree.Node;
import org.fusesource.ide.commons.tree.RefreshableUI;
import org.fusesource.ide.commons.ui.ImageProvider;
import org.fusesource.ide.commons.ui.drop.DropHandler;
import org.fusesource.ide.commons.ui.drop.DropHandlerFactory;
import org.fusesource.ide.commons.util.Objects;
import org.fusesource.ide.deployment.maven.MavenUtils;
import org.fusesource.ide.deployment.maven.ProjectDropHandler;
import org.fusesource.ide.deployment.maven.ProjectDropTarget;
import org.fusesource.ide.fabric.FabricPlugin;
import org.fusesource.ide.fabric.actions.CreateChildContainerAction;
import org.fusesource.ide.fabric.actions.CreateSshContainerAction;
import org.fusesource.ide.fabric.actions.ProfileAddAction;
import org.fusesource.ide.fabric.actions.ProfileDeleteAction;
import org.fusesource.ide.fabric.actions.jclouds.CreateJCloudsContainerAction;
import org.fusesource.ide.fabric.navigator.maven.FabricDeployAction;
import org.fusesource.ide.jmx.ui.internal.views.navigator.ContextMenuProvider;
import org.fusesource.ide.launcher.ui.ExecutePomActionPostProcessor;
import org.fusesource.ide.launcher.ui.ExecutePomActionSupport;


public class ProfileNode extends IdBasedFabricNode implements HasRefreshableUI, ImageProvider, GraphableNode, ContextMenuProvider, ProjectDropTarget, DropHandlerFactory {
	private static final String AGENT_PID = "org.fusesource.fabric.agent";
	private static final boolean addContainersToTree = false;

	private Map<String,ProfileNode> map = new HashMap<String, ProfileNode>();
	private final Fabric fabric;
	private final Profile profile;
	private final VersionNode versionNode;

	public ProfileNode getProfileNode(String profileId) {
		checkLoaded();
		ProfileNode answer = map.get(profileId);
		if (answer == null) {
			answer = getProfileNode(profileId, getProfileChildren());
		}
		return answer;
	}

	public ProfileNode(VersionNode versionNode, Node parent, Profile profile) {
		super(parent, versionNode.getFabric(), profile.getId());
		this.versionNode = versionNode;
		this.fabric = versionNode.getFabric();
		this.profile = profile;
		setPropertyBean(new ProfileBean(profile));
	}

	public ProfileNode(VersionNode parent, Profile profile) {
		this(parent, parent, profile);
	}

	@Override
	public List<Node> getChildrenGraph() {
		// lets add all my children and all my parent profiles...

		Set<Node> answer = new HashSet<Node>();
		answer.addAll(getChildrenList());
		// TODO add all agents for this profile...
		return new ArrayList<Node>(answer);
	}


	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		if (adapter == IPropertySheetPage.class) {
			return new ProfileTabViewPage(this);
		}
		return super.getAdapter(adapter);
	}


	@Override
    public boolean requiresContentsPropertyPage() {
        checkLoaded();
        getVersionNode().getChildren();
        return false;
    }

    @Override
	public RefreshableUI getRefreshableUI() {
		return super.getRefreshableUI();
	}

	@Override
	public Image getImage() {
		return FabricPlugin.getDefault().getImage("profile.png");
	}


	@Override
	protected void loadChildren() {
		Map<String, Map<String, String>> configurations = getProfile().getConfigurations();
		System.out.println("Configuration of " + this + " = " + configurations);


		Set<Profile> childProfiles = versionNode.getChildProfiles(getProfileId());
		if (childProfiles != null) {
			for (Profile profile : childProfiles) {
				addChild(versionNode.createProfile(this, profile));
			}
		}

		if (addContainersToTree ) {
			// lets get all the agents for this profile...
			List<ContainerNode> list = getContainerNodes();
			for (ContainerNode agentNode : list) {
				// lets add a new child to avoid strange tree
				addChild(new ContainerNode(this, agentNode));
			}
		}
	}

	public List<ContainerNode> getContainerNodes() {
		return getFabric().getAgentsFor(getProfile());
	}

	public static ProfileNode getProfileNode(String profileId, ProfileNode[] children) {
		ProfileNode answer = null;
		for (ProfileNode child : children) {
			answer  = child.getProfileNode(profileId);
			if (answer != null) {
				return answer;
			}
		}
		return answer;
	}

	public ProfileNode[] getProfileChildren() {
		return Objects.getArrayOf(getChildrenList(), ProfileNode.class);
	}

	@Override
	public DropHandler createDropHandler(DropTargetEvent event) {
		return new ProjectDropHandler(this);
	}

	@Override
	public Fabric getFabric() {
		return fabric;
	}

	@Override
	public void dropProject(IProject project, final Model mavenModel) {
		if (mavenModel != null) {
			ExecutePomActionSupport action = new FabricDeployAction(this);

			// set a post processor
			action.setPostProcessor(new ExecutePomActionPostProcessor() {
				/* (non-Javadoc)
				 * @see org.fusesource.ide.launcher.ui.ExecutePomActionPostProcessor#executeOnFailure()
				 */
				@Override
				public void executeOnFailure() {
				}

				/* (non-Javadoc)
				 * @see org.fusesource.ide.launcher.ui.ExecutePomActionPostProcessor#executeOnSuccess()
				 */
				@Override
				public void executeOnSuccess() {
					if (profile != null) {
						String uri = MavenUtils.getBundleURI(mavenModel);
						if ( uri != null) {
							if (uri.startsWith("fab:")) {
								uri = uri.substring(4);
								List<String> list = profile.getFabs();
								if (list != null && !list.contains(uri)) {
									list.add(uri);
									profile.setFabs(list);
								}

							} else {
								List<String> list = profile.getBundles();
								if (list != null && !list.contains(uri)) {
									list.add(uri);
									profile.setBundles(list);
								}
							}
						} else {
							// TODO handle feature file
							// deduce if there's a feature file created for this project and use that instead!
						}
						Map<String, Map<String, String>> config = profile.getConfigurations();
						if (config == null) {
							config = new HashMap<String, Map<String, String>>();
						}
						Map<String,String> agentConfig = config.get(AGENT_PID);
						if (agentConfig == null) {
							agentConfig = new HashMap<String,String>();
							config.put(AGENT_PID, agentConfig);
						}
						agentConfig.put("org.fusesource.fabric.buildTime", "" + uri + " at " + new Date());
						profile.setConfigurations(config);
						refresh();
					}
				}

			});

			MavenUtils.launch(action);

		}
	}

	public String getMavenDeployParameter() {
		final Fabric theFabric = getFabric();
		URI u = theFabric.getFabricService().getMavenRepoUploadURI();

		try {
			String uriText;
			boolean addUserPasswordToUrl = false;
			if (addUserPasswordToUrl) {
				String userInfo = theFabric.getUserName() + ":" + theFabric.getPassword();

				URI mavenRepoURI = new URI(u.getScheme(),
						userInfo, u.getHost(), u.getPort(),
						u.getPath(), u.getQuery(),
						u.getFragment());

				uriText = mavenRepoURI.toString();
			} else {
				uriText = u.toString();
			}
			System.out.println("===== deploying to the mavenRepoURL: " + uriText + " based on the URI from Fabric: " + u);

			return getFabricNameWithoutSpaces() + "::default::" + uriText;
		} catch (URISyntaxException e) {
			FabricPlugin.getLogger().error("Failed to create upload URI from " + u + ". Reason: " + e, e);
			return null;
		}
	}

	/**
	 * Returns the fabric name without spaces to avoid freaking out maven when doing a deploy
	 */
	public String getFabricNameWithoutSpaces() {
		return getFabric().getName().replace(' ', '_');
	}

	public Profile getProfile() {
		return profile;
	}

	public String getProfileId() {
		return profile.getId();
	}

	public VersionNode getVersionNode() {
		return versionNode;
	}

	public void addAndDescendants(Set<Node> answer) {
		answer.add(this);
		Node p = getParent();
		if (p instanceof ProfileNode) {
			ProfileNode parentNode = (ProfileNode) p;
			parentNode.addAndDescendants(answer);
		}
	}

	@Override
	public void provideContextMenu(IMenuManager menu) {
		menu.add(new CreateChildContainerAction(this));
		if (CreateJCloudsContainerAction.createLocalAgents) {
			menu.add(new CreateJCloudsContainerAction(getVersionNode(), null, this));
		}
		if (CreateSshContainerAction.createLocalAgents) {
			menu.add(new CreateSshContainerAction(getVersionNode(), null, this));
		}
		menu.add(new Separator());
		menu.add(new ProfileAddAction(this));
		menu.add(new ProfileDeleteAction(this));
	}

	public List<IPropertySource> getContainerPropertySourceList() {
		return PropertySources.toPropertySourceList(getContainerNodes());
	}
}
