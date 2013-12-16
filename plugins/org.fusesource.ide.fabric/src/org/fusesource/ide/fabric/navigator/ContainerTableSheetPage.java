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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.tabbed.ISection;
import io.fabric8.api.Container;
import io.fabric8.api.Version;
import org.fusesource.ide.commons.Viewers;
import org.fusesource.ide.commons.jobs.Jobs;
import org.fusesource.ide.commons.tree.Refreshables;
import org.fusesource.ide.commons.ui.Selections;
import org.fusesource.ide.commons.ui.Shells;
import org.fusesource.ide.commons.ui.actions.ActionSupport;
import org.fusesource.ide.commons.ui.actions.SeparatorFactory;
import org.fusesource.ide.commons.ui.propsrc.PropertySourceTableSheetPage;
import org.fusesource.ide.commons.ui.views.IViewPage;
import org.fusesource.ide.commons.util.Objects;
import org.fusesource.ide.fabric.FabricPlugin;
import org.fusesource.ide.fabric.actions.CreateChildContainerAction;
import org.fusesource.ide.fabric.actions.CreateSshContainerAction;
import org.fusesource.ide.fabric.actions.Messages;
import org.fusesource.ide.fabric.actions.jclouds.CreateJCloudsContainerAction;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class ContainerTableSheetPage extends PropertySourceTableSheetPage {
	protected static final String VIEW_ID = ContainerTableSheetPage.class.getName();
	protected static SeparatorFactory separatorFactory = new SeparatorFactory(VIEW_ID);

	private final Fabric fabric;
	private Action openTerminalAction;
	private Action startAction;
	private Action stopAction;
	private Action destroyAction;
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

	public ContainerTableSheetPage(Fabric fabric) {
		super(fabric, VIEW_ID, new ContainerTableView(VIEW_ID));
		this.fabric = fabric;

		openTerminalAction = new ActionSupport(Messages.openTerminalLabel, Messages.openTerminalToolTip, FabricPlugin.getDefault().getImageDescriptor("terminal_view.gif")) {
			@Override
			public void run() {
				List<Container> selectedContainers = getSelectedContainers();
				if (selectedContainers.size() > 0) {
					Container container = selectedContainers.get(0);
					if (container != null) {
						ContainerNode.openTerminal(getFabric(), container, null);
					}
				}
			}
		};

		startAction = new ActionSupport(Messages.StartAgentAction, Messages.StartAgentActionToolTip, FabricPlugin.getDefault().getImageDescriptor("start_task.gif")) {
			@Override
			public void run() {
				start();
			}

		};

		stopAction = new ActionSupport(Messages.StopAgentAction, Messages.StopAgentActionToolTip, FabricPlugin.getDefault().getImageDescriptor("stop_task.gif")) {
			@Override
			public void run() {
				stop();
			}

		};

		destroyAction = new ActionSupport(Messages.DestroyContainerAction, Messages.DestroyContainerActionToolTip, FabricPlugin.getDefault().getImageDescriptor("delete.gif")) {
			@Override
			public void run() {
				destroy();
			}

		};

		setVersionMenu = new MenuManager("Set Version", FabricPlugin.getDefault().getImageDescriptor("version.png"), "org.fusesource.ide.actions.update.version");
		setVersionMenu.setRemoveAllWhenShown(true);
		setVersionMenu.addMenuListener(new IMenuListener() {

			@Override
			public void menuAboutToShow(IMenuManager manager) {
				Version[] versions = getFabric().getFabricService().getVersions();
				Set<String> selectedVersionNames = getSelectedVersionNames();
				for (final Version version : versions) {
					Action action = new Action(version.getName()) {
						@Override
						public void run() {
							setSelectedContainersVersion(version);
						}

					};
					action.setEnabled(hasVersionApartFrom(selectedVersionNames, version));
					setVersionMenu.add(action);
				}
			}
		});

		updateActionStatus();
	}



	@Override
	public void dispose() {
		super.dispose();
		fabric.removeFabricUpdateRunnable(refreshRunnable);
	}



	@Override
	public void setPropertySources(List<IPropertySource> propertySources) {
		super.setPropertySources(propertySources);

		// update the state as the content has changed
		getCreateChildContainerAction().updateEnabled();
	}



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


	@Override
	public void refresh() {
		Viewers.async(new Runnable() {

			@Override
			public void run() {
				final Set<String> selectedIds = getSelectedIds();
				Refreshables.refresh(fabric.getContainersNode());

				updateData();
				Refreshables.refresh(getTableView());
				setSelectedContainerIds(selectedIds);
				updateActionStatus();
			}
		});
	}


	public Fabric getFabric() {
		return fabric;
	}


	@Override
	public void createControl(Composite parent) {
		getTableView().addToolBarActions(openTerminalAction, startAction, stopAction, destroyAction);

		getTableView().addLocalMenuActions(
				separator1,
				openTerminalAction, startAction, stopAction, destroyAction,
				separator2,
				setVersionMenu,
				separator3,
				getCreateChildContainerAction(),
				getCreateCloudContainerAction(),
				getCreateSshContainerAction(),
				separator4);

		getTableView().addToolBarActions(openTerminalAction, setVersionMenu, startAction, stopAction, destroyAction);

		getTableView().setDoubleClickAction(new Action() {

			@Override
			public void run() {
				ContainersNode containersNode = fabric.getContainersNode();
				if (containersNode != null) {
					List<Container> selectedContainers = getSelectedContainers();
					if (!selectedContainers.isEmpty()) {
						Container container = selectedContainers.get(0);
						ContainerNode containerNode = containersNode.getContainerNode(container.getId());
						if (containerNode != null) {
							Selections.setSingleSelection(fabric.getRefreshableUI(), containerNode);
						}
					}
				}
			}

		});

		super.createControl(parent);

		getTableView().getViewer().addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				updateActionStatus();
			}
		});

		fabric.addFabricUpdateRunnable(refreshRunnable);
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



	@Override
	public void makeContributions(IMenuManager menuManager, IToolBarManager toolBarManager,
			IStatusLineManager statusLineManager) {
		// TODO Auto-generated method stub
		super.makeContributions(menuManager, toolBarManager, statusLineManager);
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



	protected IStructuredSelection getSelection() {
		return Selections.getStructuredSelection(getTableView().getViewer());
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


	protected void setSelectedContainerIds(Set<String> selectedIds) {
		TableViewer viewer = getTableView().getViewer();
		if (viewer != null) {
			List<?> propertySources = getPropertySources();
			List selected = new ArrayList();
			for (Object object : propertySources) {
				if (object instanceof IPropertySource) {
					IPropertySource source = (IPropertySource) object;
					Object value = source.getPropertyValue("id");
					if (value instanceof String && selectedIds.contains(value.toString())) {
						selected.add(object);
					}
				}
			}
			viewer.setSelection(new StructuredSelection(selected));
			if (selected.size() == 1) {
				Object first = selected.get(0);
				viewer.reveal(first);
			}
		}

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


	protected void updateData() {
		ContainersNode containersNode = fabric.getContainersNode();
		if (containersNode != null) {
			setPropertySources(containersNode.getPropertySourceList());
		}
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
		List<Container> selectedContainers = getSelectedContainers();

		ContainerNode containerNode = getSingleSelectedRootContainerNode(selectedContainers);
		getCreateChildContainerAction().setContainerNode(containerNode);
		getCreateChildContainerAction().updateEnabled();

		int selectedContainerSize = selectedContainers.size();
		openTerminalAction.setEnabled(selectedContainerSize > 0);
		startAction.setEnabled(countStarted(selectedContainers, false) > 0);
		stopAction.setEnabled(countStarted(selectedContainers, true) > 0);
		destroyAction.setEnabled(countStarted(selectedContainers, true) > 0);
	}



	protected ContainerNode getSingleSelectedRootContainerNode(List<Container> selectedContainers) {
		ArrayList<Container> rootContainers = Lists.newArrayList(Iterables.filter(selectedContainers, new Predicate<Container>() {

			@Override
			public boolean apply(Container container) {
				return container != null && container.isRoot();
			}
		}));

		if (rootContainers.size() == 1 && fabric != null) {
			Container rootContainer = rootContainers.get(0);
			ContainersNode containersNode = fabric.getContainersNode();
			if (containersNode != null) {
				return containersNode.getContainerNode(rootContainer.getId());
			}
		}
		return null;
	}

}