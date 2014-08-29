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

import java.text.NumberFormat;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
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
import org.fusesource.ide.jmx.fabric8.navigator.Fabric8Node;

public class FabricStatusTableSheetPage extends TableViewSupport implements
		IPropertySheetPage {
	
	public static final String VIEW_ID = FabricStatusTableSheetPage.class.getName();
	protected static SeparatorFactory separatorFactory = new SeparatorFactory(VIEW_ID);
	
	private boolean showPercentOnStatusColumn;
	private final Fabric8Node fabric;
//	private CreateChildContainerAction createChildContainerAction;
//	private CreateSshContainerAction createSshContainerAction;
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

	public FabricStatusTableSheetPage(Fabric8Node fabric) {
		this.fabric = fabric;
		updateActionStatus();
	}

	@Override
	public void dispose() {
		super.dispose();
//		fabric.removeFabricUpdateRunnable(refreshRunnable);
	}

	@Override
	protected void configureViewer() {
//		addLocalMenuActions(separator1, 
//				getCreateChildContainerAction(),
//				getCreateCloudContainerAction(), 
//				getCreateSshContainerAction(),
//				separator2);
		
		getViewer().addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
//				FabricNavigator nav = FabricPlugin.getFabricNavigator();
//				if (nav != null) {
//					Object oSel = Selections.getFirstSelection(event
//							.getSelection());
//					if (oSel != null && oSel instanceof ProfileStatus) {
//						ProfileStatus s = asProfileStatus(oSel);
//						String profileId = s.getProfile();
//						ProfileNode profileNode = searchProfile(nav, profileId,
//								getFabric().getVersionsNode());
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
//		fabric.addFabricUpdateRunnable(refreshRunnable);
//		updateData();
	}

	@Override
	public void refresh() {
		Viewers.async(new Runnable() {
			@Override
			public void run() {
				Refreshables.refresh(getViewer());
				updateActionStatus();
			}
		});
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
		return VIEW_ID;
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
		return VIEW_ID;
	}

	public Fabric8Node getFabric() {
		return fabric;
	}

	protected IStructuredSelection getSelection() {
		return Selections.getStructuredSelection(getViewer());
	}

//	protected CreateChildContainerAction getCreateChildContainerAction() {
//		if (createChildContainerAction == null) {
//			createChildContainerAction = createChildContainerAction(fabric);
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
//			createSshContainerAction = createSshContainerAction(fabric);
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
//		ProfileNode profileNode = getSelectedProfileNode();
//		getCreateChildContainerAction().setProfileNode(profileNode);
//		getCreateSshContainerAction().setProfileNode(profileNode);
//		getCreateCloudContainerAction().setProfileNode(profileNode);
	}
}