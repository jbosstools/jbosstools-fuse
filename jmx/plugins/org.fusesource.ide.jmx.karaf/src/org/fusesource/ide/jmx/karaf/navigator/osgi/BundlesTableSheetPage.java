/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.jmx.karaf.navigator.osgi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import javax.management.Notification;
import javax.management.NotificationListener;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.views.properties.IPropertySource;
import org.fusesource.ide.foundation.core.util.Objects;
import org.fusesource.ide.foundation.ui.jobs.Jobs;
import org.fusesource.ide.foundation.ui.propsrc.PropertySourceTableSheetPage;
import org.fusesource.ide.foundation.ui.util.Selections;
import org.fusesource.ide.foundation.ui.util.Viewers;
import org.fusesource.ide.foundation.ui.views.IViewPage;
import org.fusesource.ide.jmx.karaf.KarafJMXPlugin;
import org.fusesource.ide.jmx.karaf.Messages;


public class BundlesTableSheetPage extends PropertySourceTableSheetPage {
	public static final String VIEW_ID = BundlesTableSheetPage.class.getName();

	private final BundlesNode bundlesNode;
	private Action startBundleAction;
	private Action stopBundleAction;

	private Action uninstallBundleAction;

	private NotificationListener notificationListener;

	public BundlesTableSheetPage(BundlesNode bundlesNode) {
		super(bundlesNode, VIEW_ID, new BundlesTableView(VIEW_ID, bundlesNode));
		this.bundlesNode = bundlesNode;

		startBundleAction = new Action(Messages.StartBundleAction, SWT.CHECK) {
			@Override
			public void run() {
				startBundles();
			}

		};
		startBundleAction.setToolTipText(Messages.StartBundleActionToolTip);
		startBundleAction.setImageDescriptor(KarafJMXPlugin.getDefault().getImageDescriptor("start_task.gif"));


		stopBundleAction = new Action(Messages.StopBundleAction, SWT.CHECK) {
			@Override
			public void run() {
				stopBundles();
			}

		};
		stopBundleAction.setToolTipText(Messages.StopBundleActionToolTip);
		stopBundleAction.setImageDescriptor(KarafJMXPlugin.getDefault().getImageDescriptor("stop_task.gif"));

		uninstallBundleAction = new Action(Messages.UninstallBundleAction, SWT.CHECK) {
			@Override
			public void run() {
				uninstallBundles();
			}

		};
		uninstallBundleAction.setToolTipText(Messages.UninstallBundleActionToolTip);
		uninstallBundleAction.setImageDescriptor(KarafJMXPlugin.getDefault().getImageDescriptor("delete.gif"));

		startBundleAction.setEnabled(false);
		stopBundleAction.setEnabled(false);
		uninstallBundleAction.setEnabled(false);

		notificationListener = new NotificationListener() {

			@Override
			public void handleNotification(Notification notification, Object handback) {
				//FabricPlugin.getLogger().debug("================= notification: " + notification);
				refresh();
			}
		};
		bundlesNode.getFacade().addBundleStateNotificationListener(notificationListener, null, null);
		//FabricPlugin.getLogger().debug("============== added notificationlistener on: "+ this);

	}



	@Override
	public void dispose() {
		//FabricPlugin.getLogger().debug("============== removing notificationlistener on: "+ this);
		bundlesNode.getFacade().removeBundleStateNotificationListener(notificationListener, null, null);
		super.dispose();
	}



	public BundlesNode getNode() {
		return bundlesNode;
	}



	@Override
	public void setView(IViewPage view) {
		super.setView(view);

		/*
		Action setVersionAction = new ActionSupport("Set Version") {};

		getTableView().addLocalMenuActions(setVersionAction);
		 */

	}



	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);

		getTableView().getViewer().addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				HashSet<String> states = new HashSet<String>(getSelectedBundleIDStates().values());

				startBundleAction.setEnabled(!states.isEmpty() && !states.contains("ACTIVE"));
				stopBundleAction.setEnabled(!states.isEmpty() && states.contains("ACTIVE"));
				uninstallBundleAction.setEnabled(!states.isEmpty() && !states.contains("ACTIVE"));
			}
		});
	}


	@Override
	public void setActionBars(IActionBars actionBars) {
		super.setActionBars(actionBars);
		IMenuManager menu = actionBars.getMenuManager();

		menu.add(startBundleAction);
		menu.add(stopBundleAction);
		menu.add(uninstallBundleAction);

		final IToolBarManager toolBarManager = actionBars.getToolBarManager();
		if (toolBarManager != null) {
			toolBarManager.add(startBundleAction);
			toolBarManager.add(stopBundleAction);
			toolBarManager.add(uninstallBundleAction);
		}
	}


	protected void startBundles() {
		final long[] ids = getSelectedBundleIds();
		if (ids.length > 0) {
			String message = Objects.makeString("Start bundles ", ", ", "", ids);
			doUpdate(message, new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					getNode().getFacade().startBundles(ids);
					return true;
				}});
		}
	}

	protected void stopBundles() {
		final long[] ids = getSelectedBundleIds();
		if (ids.length > 0) {
			String message = Objects.makeString("Stop bundles ", ", ", "", ids);
			doUpdate(message, new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					getNode().getFacade().stopBundles(ids);
					return true;
				}});
		}
	}

	protected void uninstallBundles() {
		final long[] ids = getSelectedBundleIds();
		if (ids.length > 0) {
			String message = Objects.makeString("Uninstall bundles ", ", ", "", ids);
			doUpdate(message, new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					getNode().getFacade().uninstallBundles(ids);
					return true;
				}});
		}
	}

	protected void doUpdate(String message, final Callable<Boolean> callable) {
		Jobs.schedule(message,  new Callable<Boolean>() {

			@Override
			public Boolean call() throws Exception {
				Boolean answer = callable.call();
				if (answer != null && answer.booleanValue()) {
					refresh();
				}
				return answer;
			}});
	}


	@Override
	public void refresh() {
		Viewers.async(new Runnable() {

			@Override
			public void run() {
				Set<Long> selectedBundleIds = getSelectedBundleIDStates().keySet();
				setPropertySources(bundlesNode.getPropertySourceList());
				getTableView().refresh();
				setSelectedBundleIds(selectedBundleIds);
			}
		});
	}

	protected void setSelectedBundleIds(Set<Long> selectedBundleIds) {
		TableViewer viewer = getTableView().getViewer();
		if (viewer != null) {
			List<?> propertySources = getPropertySources();
			List<Object> selected = new ArrayList<>();
			for (Object object : propertySources) {
				if (object instanceof IPropertySource) {
					BundleStateFacade bundleState = new BundleStateFacade((IPropertySource) object);
					final Long id = bundleState.getId();
					if (id != null && selectedBundleIds.contains(id)) {
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


	protected long[] getSelectedBundleIds() {
		Set<Long> ids = getSelectedBundleIDStates().keySet();
		long[]  answer = new long[ids.size()];
		int idx = 0;
		for (Long n : ids) {
			if (n != null) {
				answer[idx++] = n.longValue();
			}
		}
		return answer;
	}

	protected Map<Long,String> getSelectedBundleIDStates() {
		Map<Long, String> answer = new HashMap<Long, String>();
		IStructuredSelection selection = Selections.getStructuredSelection(getTableView().getViewer());
		if (selection != null) {
			Iterator<?> iter = selection.iterator();
			while (iter.hasNext()) {
				Object value = iter.next();
				if (value instanceof IPropertySource) {
					BundleStateFacade bundleState = new BundleStateFacade((IPropertySource) value);
					Long id = bundleState.getId();
					String state = bundleState.getState();
					if (id != null && state != null) {
						answer.put(id, state);
					}
				}
			}
		}
		return answer;
	}
}