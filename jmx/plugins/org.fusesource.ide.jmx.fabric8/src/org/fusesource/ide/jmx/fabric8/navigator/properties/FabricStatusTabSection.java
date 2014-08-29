/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.jmx.fabric8.navigator.properties;

import io.fabric8.api.ProfileStatus;
import io.fabric8.api.Version;

import java.text.NumberFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.IWorkbenchPart;
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
import org.fusesource.ide.jmx.fabric8.navigator.Fabric8Node;
import org.fusesource.ide.jmx.fabric8.navigator.actions.OpenWebConsoleAction;

/**
 * @author lhein
 */
public class FabricStatusTabSection extends TableViewSupport {

	protected static SeparatorFactory separatorFactory = new SeparatorFactory(FabricStatusTableSheetPage.VIEW_ID);
	
	private boolean showPercentOnStatusColumn;
	private Fabric8Node current;
	private OpenWebConsoleAction openWebConsoleAction;
//	private CreateChildContainerAction createChildContainerAction;
//	private CreateSshContainerAction createSshContainerAction;
	private Separator separator1 = separatorFactory.createSeparator();
	private Separator separator2 = separatorFactory.createSeparator();

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

	public FabricStatusTabSection() {
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		Fabric8Node fabric = (Fabric8Node) Selections.getFirstSelection(selection);
		if (fabric == current) {
			return;
		}
		if (current != null) {
			current.removeFabricUpdateRunnable(refreshRunnable);
		}
		current = fabric;
		if (current != null) {
			current.addFabricUpdateRunnable(refreshRunnable);
		}
		final Collection<ProfileStatus> statuses = current.getProfileStatuses();
		setInput(statuses);
		getViewer().setInput(statuses);
		getViewer().refresh(true);
		if (fabric != null) {
//			getCreateChildContainerAction().setFabric(current);
//			getCreateSshContainerAction().setFabric(current);
		}
		updateActionStatus();
	}

	@Override
	public void dispose() {
		if (current != null) {
			current.removeFabricUpdateRunnable(refreshRunnable);
		}
		super.dispose();
	}

	@Override
	protected void configureViewer() {
		addLocalMenuActions(separator1, 
				getOpenWebConsoleAction(),
//				getCreateChildContainerAction(),
//				getCreateSshContainerAction(),
				separator2);

		addToolBarActions(getOpenWebConsoleAction());
		
		getViewer().addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
//				FabricNavigator nav = FabricPlugin.getFabricNavigator();
//				if (nav != null && current != null) {
//					Object oSel = Selections.getFirstSelection(event
//							.getSelection());
//					if (oSel != null && oSel instanceof ProfileStatus) {
//						ProfileStatus s = asProfileStatus(oSel);
//						String profileId = s.getProfile();
//						ProfileNode profileNode = searchProfile(nav, profileId,
//								current.getVersionsNode());
//						if (profileNode != null)
//							nav.selectReveal(new StructuredSelection(
//									profileNode));
//					}
//				}
			}
		});
		getViewer().addSelectionChangedListener(
				new ISelectionChangedListener() {
					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						updateActionStatus();
					}
				});
		updateData();
	}

	@Override
	public void refresh() {
		Viewers.async(new Runnable() {
			@Override
			public void run() {
				if (current == null) {
					return;
				}
				updateData();
				Refreshables.refresh(getViewer());
				updateActionStatus();
			}
		});
	}

	protected void updateData() {
		setInput(current.getProfileStatuses());
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
		column = addColumnFunction(bounds, column, function, "Status",
				new HealthLabelProvider(function));
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
		column = addColumnFunction(bounds, column, function, "Health",
				new PercentFunctionLabelProvider(function));
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
			text = NumberFormat.getPercentInstance().format(
					status.getHealth(count));
		}
		return Health.newInstance(text, count, status.getMinimumInstances(),
				status.getMaximumInstances());
	}

	@Override
	protected IStructuredContentProvider createContentProvider() {
		return ArrayContentProvider.getInstance();
	}

	@Override
	protected String getHelpID() {
		return FabricStatusTableSheetPage.VIEW_ID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fusesource.ide.commons.ui.IConfigurableColumns#getColumnConfigurationId
	 * ()
	 */
	@Override
	public String getColumnConfigurationId() {
		return FabricStatusTableSheetPage.VIEW_ID;
	}

	protected IStructuredSelection getSelection() {
		return Selections.getStructuredSelection(getViewer());
	}

	protected boolean hasVersionApartFrom(Set<String> names, Version version) {
		int minSize = names.contains(version.getId()) ? 2 : 1;
		return names.size() >= minSize;
	}

	protected OpenWebConsoleAction getOpenWebConsoleAction() {
		if (openWebConsoleAction == null) {
			openWebConsoleAction = createOpenWebConsoleAction(current);
			openWebConsoleAction.updateEnabled();
		}
		return openWebConsoleAction;
	}
	
	protected OpenWebConsoleAction createOpenWebConsoleAction(Fabric8Node fabric) {
		return new OpenWebConsoleAction(fabric);
	}
	
//	protected CreateChildContainerAction getCreateChildContainerAction() {
//		if (createChildContainerAction == null) {
//			createChildContainerAction = createChildContainerAction((Fabric) null);
//			createChildContainerAction.updateEnabled();
//		}
//		return createChildContainerAction;
//	}
//
//	protected void setCreateChildContainerAction(
//			CreateChildContainerAction createChildContainerAction) {
//		this.createChildContainerAction = createChildContainerAction;
//	}

//	protected CreateSshContainerAction getCreateSshContainerAction() {
//		if (createSshContainerAction == null) {
//			createSshContainerAction = createSshContainerAction((Fabric) null);
//		}
//		return createSshContainerAction;
//	}
//
//	protected void setCreateSshContainerAction(
//			CreateSshContainerAction createSshContainerAction) {
//		this.createSshContainerAction = createSshContainerAction;
//	}

//	protected CreateSshContainerAction createSshContainerAction(Fabric fabric) {
//		return new CreateSshContainerAction(fabric);
//	}
//
//	protected CreateChildContainerAction createChildContainerAction(
//			Fabric fabric) {
//		return new CreateChildContainerAction(fabric);
//	}

	protected void updateActionStatus() {
		getOpenWebConsoleAction().setFabric(current);
//		ProfileNode profileNode = getSelectedProfileNode();
//		getCreateChildContainerAction().setProfileNode(profileNode);
//		getCreateSshContainerAction().setProfileNode(profileNode);
//		getCreateCloudContainerAction().setProfileNode(profileNode);
	}
}
