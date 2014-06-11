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


import io.fabric8.api.ProfileStatus;
import io.fabric8.api.Version;

import java.text.NumberFormat;
import java.util.Collection;
import java.util.Set;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.fusesource.ide.commons.Viewers;
import org.fusesource.ide.commons.tree.Refreshables;
import org.fusesource.ide.commons.ui.Selections;
import org.fusesource.ide.commons.ui.actions.SeparatorFactory;
import org.fusesource.ide.commons.ui.label.Health;
import org.fusesource.ide.commons.ui.label.HealthLabelProvider;
import org.fusesource.ide.commons.ui.label.PercentFunctionLabelProvider;
import org.fusesource.ide.commons.ui.views.TableViewSupport;
import org.fusesource.ide.commons.util.Function1;
import org.fusesource.ide.commons.util.FunctionInteger;
import org.fusesource.ide.fabric.FabricPlugin;
import org.fusesource.ide.fabric.actions.CreateChildContainerAction;
import org.fusesource.ide.fabric.actions.CreateSshContainerAction;
import org.fusesource.ide.fabric.actions.jclouds.CreateJCloudsContainerAction;
import org.jboss.tools.jmx.core.tree.Node;


public class FabricStatusTableSheetPage extends TableViewSupport implements IPropertySheetPage {
	public static final String VIEW_ID = FabricStatusTableSheetPage.class.getName();

	protected static SeparatorFactory separatorFactory = new SeparatorFactory(VIEW_ID);
	private boolean showPercentOnStatusColumn;

	private final Fabric fabric;
	private CreateChildContainerAction createChildContainerAction;
	private CreateJCloudsContainerAction createCloudContainerAction;
	private CreateSshContainerAction createSshContainerAction;
	private MenuManager setVersionMenu;
	private Separator separator1 = separatorFactory.createSeparator();
	private Separator separator2 = separatorFactory.createSeparator();
	private Separator separator3 = separatorFactory.createSeparator();
	private Separator separator4 = separatorFactory.createSeparator();
	private Runnable refreshRunnable = new Runnable() {

		@Override
		public void run() {
			refresh();
		}
	};



	public static ProfileStatus asProfileStatus(Object element) {
		if (element instanceof ProfileStatus) {
			return (ProfileStatus) element;
		}
		return null;
	}


	public FabricStatusTableSheetPage(Fabric fabric) {
		this.fabric = fabric;
		updateActionStatus();
	}



	@Override
	public void dispose() {
		super.dispose();
		fabric.removeFabricUpdateRunnable(refreshRunnable);
	}


	@Override
	protected void configureViewer() {
		addLocalMenuActions(
				separator1,
				getCreateChildContainerAction(),
				getCreateCloudContainerAction(),
				getCreateSshContainerAction(),
				separator2);

		getViewer().addDoubleClickListener(new IDoubleClickListener() {			
			@Override
			public void doubleClick(DoubleClickEvent event) {
				FabricNavigator nav = FabricPlugin.getFabricNavigator();
				if (nav != null) {
					Object oSel = Selections.getFirstSelection(event.getSelection());
					if (oSel != null && oSel instanceof ProfileStatus) {
						ProfileStatus s = asProfileStatus(oSel);
						String profileId = s.getProfile();
						ProfileNode profileNode = searchProfile(nav, profileId, getFabric().getVersionsNode());
						if (profileNode != null) nav.selectReveal(new StructuredSelection(profileNode));
					}
				}
			}
		});
		
		getViewer().addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				updateActionStatus();
			}
		});

		fabric.addFabricUpdateRunnable(refreshRunnable);
		updateData();
	}

	private ProfileNode searchProfile(FabricNavigator nav, String profileId, Node node) {
		ProfileNode result = null;
		
		if (node instanceof ProfileNode &&
			((ProfileNode)node).getId().equals(profileId)) {
			// found the profile
			result = ((ProfileNode)node);
		} else {
			Node[] nodes = node.getChildren();
			if (nodes != null) {
				for (Node n : node.getChildren()) {
					result = searchProfile(nav, profileId, n);
					if (result != null) {
						break;
					}
				}			
			}			
		}		
			
		return result;
	}
	
	@Override
	public void refresh() {
		Viewers.async(new Runnable() {

			@Override
			public void run() {
				// TODO
				/*
				final Set<String> selectedIds = getSelectedIds();
				Refreshables.refresh(fabric.getContainersNode());
				 */

				updateData();
				Refreshables.refresh(getViewer());
				/*
				setSelectedContainerIds(selectedIds);
				 */
				updateActionStatus();
			}
		});
	}

	protected void updateData() {
		setInput(getProfileStatuses());
	}


	protected Collection<ProfileStatus> getProfileStatuses() {
		return fabric.getProfileStatuses();
	}



	@Override
	protected void createColumns() {
		clearColumns();

		int bounds = 100;
		int column = 0;

		Function1 function = new Function1() {
			@Override
			public Object apply(Object element) {
				ProfileStatus status = asProfileStatus(element);
				if (status != null) {
					return status.getProfile();
				}
				return null;
			}
		};
		column = addColumnFunction(250, column, function, "Profile");

		function = new FunctionInteger() {
			@Override
			public Integer apply(Object element) {
				ProfileStatus status = asProfileStatus(element);
				if (status != null) {
					return status.getCount();
				}
				return null;
			}
		};
		column = addColumnFunction(bounds, column, function, "Count");

		function = new FunctionInteger() {
			@Override
			public Integer apply(Object element) {
				ProfileStatus status = asProfileStatus(element);
				if (status != null) {
					return status.getMinimumInstances();
				}
				return null;
			}
		};
		column = addColumnFunction(bounds, column, function, "Minumum");

		function = new FunctionInteger() {
			@Override
			public Integer apply(Object element) {
				ProfileStatus status = asProfileStatus(element);
				if (status != null) {
					return status.getMaximumInstances();
				}
				return null;
			}
		};
		column = addColumnFunction(bounds, column, function, "Maximum");

		function = new Function1() {
			@Override
			public Object apply(Object element) {
				ProfileStatus status = asProfileStatus(element);
				if (status != null) {
					return toHealth(status);
				}
				return null;
			}
		};
		column = addColumnFunction(bounds, column, function, "Status", new HealthLabelProvider(function));

		function = new Function1() {
			@Override
			public Double apply(Object element) {
				ProfileStatus status = asProfileStatus(element);
				if (status != null) {
					return status.getHealth(status.getCount());
				}
				return null;
			}
		};
		column = addColumnFunction(bounds, column, function, "Health", new PercentFunctionLabelProvider(function));

		function = new Function1() {
			@Override
			public Object apply(Object element) {
				ProfileStatus status = asProfileStatus(element);
				if (status != null) {
					return status.getDependentProfiles();
				}
				return null;
			}
		};
		column = addColumnFunction(250, column, function, "Dependencies");
	}


	protected Health toHealth(ProfileStatus status) {
		int count = status.getCount();
		String text = null;
		if (showPercentOnStatusColumn) {
			text = NumberFormat.getPercentInstance().format(status.getHealth(count));
		}
		return Health.newInstance(text, count, status.getMinimumInstances(), status.getMaximumInstances());
	}


	@Override
	protected IStructuredContentProvider createContentProvider() {
		return ArrayContentProvider.getInstance();
	}

	@Override
	protected String getHelpID() {
		return VIEW_ID;
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.commons.ui.IConfigurableColumns#getColumnConfigurationId()
	 */
	@Override
	public String getColumnConfigurationId() {
		return VIEW_ID;
	}
	
	public Fabric getFabric() {
		return fabric;
	}

	protected IStructuredSelection getSelection() {
		return Selections.getStructuredSelection(getViewer());
	}


	/*
	protected void stop() {
		final List<Container> containers = getSelectedContainers();
		String message = Objects.makeString("Stopping ", ", ", "", getSelectedIds());
		Jobs.schedule(message, new Runnable() {

			@Override
			public void run() {
				for (Container container : containers) {
					if (container.isAlive()) {
						container.stop();
					}
				}
				refresh();
			}
		});
	}



	protected void start() {
		final List<Container> containers = getSelectedContainers();
		String message = Objects.makeString("Starting ", ", ", "", getSelectedIds());
		Jobs.schedule(message, new Runnable() {

			@Override
			public void run() {
				for (Container container : containers) {
					if (!container.isAlive()) {
						container.start();
					}
				}
				refresh();
			}
		});
	}


	protected void destroy() {
		final List<Container> containers = getSelectedContainers();
		boolean confirm = MessageDialog.openConfirm(Shells.getShell(), "Destroy Container(s)", Objects.makeString("Do you really want to destroy the selected container(s) ", ", ", "?\nThis will terminate the container process and removes it from Fabric!", getSelectedIds()));
		if (confirm) {
			Jobs.schedule(Objects.makeString("Destroying container(s) ", ", ", "", getSelectedIds()), new Runnable() {

				@Override
				public void run() {
					for (Container container : containers) {
						container.destroy();
					}
					refresh();
				}
			});
		}
	}

	protected int countStarted(List<Container> containers, boolean flag) {
		int counter = 0;
		for (Container container : containers) {
			boolean alive = container.isAlive();
			if (alive == flag) {
				counter++;
			}
		}
		return counter;
	}



	protected List<Container> getSelectedContainers() {
		List<Container> containers = new ArrayList<Container>();
		IStructuredSelection selection = getSelection();
		if (selection != null) {
			boolean changed = false;
			Iterator iterator = selection.iterator();
			while (iterator.hasNext()) {
				Container container = ContainerNode.toContainer(iterator.next());
				if (container != null) {
					containers.add(container);
				}
			}
		}
		return containers;
	}

	protected Set<String> getSelectedVersionNames() {
		Set<String> answer = new HashSet<String>();
		List<Container> containers = getSelectedContainers();
		for (Container container : containers) {
			Version version = container.getVersion();
			if (version != null) {
				String name = version.getName();
				if (name != null) {
					answer.add(name);
				}
			}
		}
		return answer;
	}

	protected Set<String> getSelectedIds() {
		Set<String> answer = new HashSet<String>();
		List<Container> containers = getSelectedContainers();
		for (Container container : containers) {
			String id = container.getId();
			if (id != null) {
				answer.add(id);
			}
		}
		return answer;
	}


	protected void setSelectedContainersVersion(Version version) {
		IStructuredSelection selection = getSelection();
		if (selection != null) {
			boolean changed = false;
			Iterator iterator = selection.iterator();
			while (iterator.hasNext()) {
				ContainerNode agentNode = ContainerNode.toContainerNode(iterator.next());
				if (agentNode != null) {
					if (!agentNode.matches(version)) {
						agentNode.getContainer().setVersion(version);
						changed = true;
					}
				}
			}

			if (changed) {
				IViewPage view = getView();
				if (view instanceof ISection) {
					ISection section = (ISection) view;
					section.refresh();
				}
				getFabric().getContainersNode().refresh();
				getFabric().getVersionsNode().refresh();
			}
		}
	}

	 */


	protected ProfileNode getSelectedProfileNode() {
		Object first = Selections.getFirstSelection(getViewer());
		if (first instanceof ProfileStatus) {
			ProfileStatus status = (ProfileStatus) first;
			String id = status.getProfile();
			VersionNode version = getFabric().getDefaultVersionNode();
			if (version != null) {
				return version.getProfileNode(id);
			}
		}
		return null;
	}



	protected boolean hasVersionApartFrom(Set<String> names, Version version) {
		int minSize = names.contains(version.getName()) ? 2 : 1;
		return names.size() >= minSize;
	}

	protected CreateChildContainerAction getCreateChildContainerAction() {
		if (createChildContainerAction == null) {
			createChildContainerAction = createChildContainerAction(fabric);
			createChildContainerAction.updateEnabled();
		}
		return createChildContainerAction;
	}

	protected void setCreateChildContainerAction(CreateChildContainerAction createChildContainerAction) {
		this.createChildContainerAction = createChildContainerAction;
	}

	protected CreateJCloudsContainerAction getCreateCloudContainerAction() {
		if (createCloudContainerAction == null) {
			createCloudContainerAction = createCloudContainerAction(fabric);
		}
		return createCloudContainerAction;
	}

	protected void setCreateCloudContainerAction(CreateJCloudsContainerAction createCloudContainerAction) {
		this.createCloudContainerAction = createCloudContainerAction;
	}

	protected CreateSshContainerAction getCreateSshContainerAction() {
		if (createSshContainerAction == null) {
			createSshContainerAction = createSshContainerAction(fabric);
		}
		return createSshContainerAction;
	}

	protected void setCreateSshContainerAction(CreateSshContainerAction createSshContainerAction) {
		this.createSshContainerAction = createSshContainerAction;
	}

	protected CreateJCloudsContainerAction createCloudContainerAction(Fabric fabric) {
		return new CreateJCloudsContainerAction(fabric);
	}

	protected CreateSshContainerAction createSshContainerAction(Fabric fabric) {
		return new CreateSshContainerAction(fabric);
	}

	protected CreateChildContainerAction createChildContainerAction(Fabric fabric) {
		return new CreateChildContainerAction(fabric);
	}

	protected void updateActionStatus() {
		ProfileNode profileNode = getSelectedProfileNode();
		getCreateChildContainerAction().setProfileNode(profileNode);
		getCreateSshContainerAction().setProfileNode(profileNode);
		getCreateCloudContainerAction().setProfileNode(profileNode);
	}
}