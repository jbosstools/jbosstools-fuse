/******************************************************************************* 
 * Copyright (c) 2013 Red Hat, Inc. 
 *  All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 *
 ******************************************************************************/
package org.fusesource.ide.jmx.karaf.navigator.osgi;

import java.util.ArrayList;
import java.util.Collections;
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
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.IPropertySource;
import org.fusesource.ide.foundation.core.util.Objects;
import org.fusesource.ide.foundation.ui.jobs.Jobs;
import org.fusesource.ide.foundation.ui.util.Selections;
import org.fusesource.ide.foundation.ui.util.Viewers;
import org.fusesource.ide.jmx.karaf.KarafJMXPlugin;
import org.fusesource.ide.jmx.karaf.Messages;

public class BundlesTabSection extends BundlesTableView {

    private BundlesNode current;
    private Action startBundleAction;
    private Action stopBundleAction;
    private Action uninstallBundleAction;
    private NotificationListener notificationListener;

    public BundlesTabSection() {
        super(BundlesTableSheetPage.VIEW_ID, null);
        startBundleAction = new Action(Messages.StartBundleAction, SWT.CHECK) {
            @Override
            public void run() {
                startBundles();
            }
        };
        startBundleAction.setToolTipText(Messages.StartBundleActionToolTip);
        startBundleAction.setImageDescriptor(KarafJMXPlugin.getDefault().getImageDescriptor("start_task.gif"));
        startBundleAction.setId(BundlesTabSection.class.getName()+".Start");

        stopBundleAction = new Action(Messages.StopBundleAction, SWT.CHECK) {
            @Override
            public void run() {
                stopBundles();
            }

        };
        stopBundleAction.setToolTipText(Messages.StopBundleActionToolTip);
        stopBundleAction.setImageDescriptor(KarafJMXPlugin.getDefault().getImageDescriptor("stop_task.gif"));
        stopBundleAction.setId(BundlesTabSection.class.getName()+".Stop");

        uninstallBundleAction = new Action(Messages.UninstallBundleAction, SWT.CHECK) {
            @Override
            public void run() {
                uninstallBundles();
            }

        };
        uninstallBundleAction.setToolTipText(Messages.UninstallBundleActionToolTip);
        uninstallBundleAction.setImageDescriptor(KarafJMXPlugin.getDefault().getImageDescriptor("delete.gif"));
        uninstallBundleAction.setId(BundlesTabSection.class.getName()+".Uninstall");

        startBundleAction.setEnabled(false);
        stopBundleAction.setEnabled(false);
        uninstallBundleAction.setEnabled(false);

        notificationListener = new NotificationListener() {
            @Override
            public void handleNotification(Notification notification, Object handback) {
                refresh();
            }
        };
    }

    @Override
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);
        getViewer().addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
				Set<String> states = new HashSet<>(getSelectedBundleIDStates().values());

                startBundleAction.setEnabled(!states.isEmpty() && !states.contains("ACTIVE"));
                stopBundleAction.setEnabled(!states.isEmpty() && states.contains("ACTIVE"));
                uninstallBundleAction.setEnabled(!states.isEmpty() && !states.contains("ACTIVE"));
            }
        });
        addToolBarActions(startBundleAction, stopBundleAction, uninstallBundleAction);
        addLocalMenuActions(startBundleAction, stopBundleAction, uninstallBundleAction);
    }

    @Override
    public void setInput(IWorkbenchPart part, ISelection selection) {
        BundlesNode bundles = (BundlesNode) Selections.getFirstSelection(selection);
        if (bundles == current) {
            return;
        }
        if (current != null) {
            current.getFacade().removeBundleStateNotificationListener(notificationListener, null, null);
        }
        current = bundles;
		List<IPropertySource> propertySources = bundles == null ? Collections.emptyList() : bundles.getPropertySourceList();
        setPropertySources(propertySources);
        getViewer().setInput(propertySources);
        recreateColumns();
        getSearchText().setText(getInitialSearchText());
        getViewer().refresh(true);
    }

    @Override
    protected String getInitialSearchText() {
        String searchText = current == null ? null : current.getBundlefilterText();
        return searchText == null ? "" : searchText;
    }

    @Override
    protected void recreateColumns() {
        if (current != null) {
            super.recreateColumns();
        }
    }

    @Override
    public void aboutToBeShown() {
        if (current != null) {
            current.getFacade().addBundleStateNotificationListener(notificationListener, null, null);
        }
        super.aboutToBeShown();
    }

    @Override
    public void aboutToBeHidden() {
        if (current != null) {
            current.getFacade().removeBundleStateNotificationListener(notificationListener, null, null);
        }
        super.aboutToBeHidden();
    }

    protected void startBundles() {
        final long[] ids = getSelectedBundleIds();
        if (ids.length > 0) {
            String message = Objects.makeString("Start bundles ", ", ", "", ids);
            doUpdate(message, new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    current.getFacade().startBundles(ids);
                    return true;
                }
            });
        }
    }

    protected void stopBundles() {
        final long[] ids = getSelectedBundleIds();
        if (ids.length > 0) {
            String message = Objects.makeString("Stop bundles ", ", ", "", ids);
            doUpdate(message, new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    current.getFacade().stopBundles(ids);
                    return true;
                }
            });
        }
    }

    protected void uninstallBundles() {
        final long[] ids = getSelectedBundleIds();
        if (ids.length > 0) {
            String message = Objects.makeString("Uninstall bundles ", ", ", "", ids);
            doUpdate(message, new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    current.getFacade().uninstallBundles(ids);
                    return true;
                }
            });
        }
    }

    protected void doUpdate(String message, final Callable<Boolean> callable) {
        Jobs.schedule(message, new Callable<Boolean>() {

            @Override
            public Boolean call() throws Exception {
                Boolean answer = callable.call();
                if (answer != null && answer.booleanValue()) {
                    refresh();
                }
                return answer;
            }
        });
    }

    @Override
    public void refresh() {
        Viewers.async(new Runnable() {
            @Override
            public void run() {
                Set<Long> selectedBundleIds = getSelectedBundleIDStates().keySet();
                setPropertySources(current.getPropertySourceList());
                BundlesTabSection.super.refresh();
                setSelectedBundleIds(selectedBundleIds);
            }
        });
    }

    protected void setSelectedBundleIds(Set<Long> selectedBundleIds) {
        TableViewer viewer = getViewer();
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
        long[] answer = new long[ids.size()];
        int idx = 0;
        for (Long n : ids) {
            if (n != null) {
                answer[idx++] = n.longValue();
            }
        }
        return answer;
    }

    protected Map<Long, String> getSelectedBundleIDStates() {
		Map<Long, String> answer = new HashMap<>();
        IStructuredSelection selection = Selections.getStructuredSelection(getViewer());
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
    public BundlesNode getCurrent() {
		return current;
	}
}
