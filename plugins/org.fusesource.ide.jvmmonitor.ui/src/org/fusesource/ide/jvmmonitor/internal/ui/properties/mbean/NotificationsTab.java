/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.mbean;

import javax.management.ObjectName;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.part.PageBook;
import org.fusesource.ide.jvmmonitor.core.IActiveJvm;
import org.fusesource.ide.jvmmonitor.internal.ui.RefreshJob;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.AbstractJvmPropertySection;
import org.fusesource.ide.jvmmonitor.ui.Activator;
import org.fusesource.ide.jvmmonitor.ui.ISharedImages;


/**
 * The notification tab.
 */
public class NotificationsTab extends PageBook {

    /** The tree viewer. */
    TreeViewer treeViewer;

    /** The tab item. */
    CTabItem tabItem;

    /** The tab folder. */
    private CTabFolder tabFolder;

    /** The notification filtered tree. */
    NotificationFilteredTree tree;

    /** The message page. */
    Composite messagePage;

    /** The object name. */
    ObjectName objectName;

    /** The action to subscribe notification. */
    SubscribeAction subscribeAction;

    /** The notification image. */
    private Image notificationImage;

    /** The property section. */
    AbstractJvmPropertySection section;

    /**
     * The constructor.
     * 
     * @param tabFolder
     *            The tab folder
     * @param section
     *            The property section
     */
    public NotificationsTab(CTabFolder tabFolder,
            AbstractJvmPropertySection section) {
        super(tabFolder, SWT.NONE);

        this.tabFolder = tabFolder;
        this.section = section;
        addTabItem();

        tree = new NotificationFilteredTree(this, section);
        tree.setLayoutData(null);
        treeViewer = tree.getViewer();

        messagePage = new Composite(this, SWT.NONE);
        messagePage.setLayout(new GridLayout(3, false));
        FormToolkit toolkit = new FormToolkit(Display.getDefault());
        toolkit.createLabel(messagePage, Messages.notificationsNotSubscribedMsg);
        Hyperlink hyperlink = toolkit.createHyperlink(messagePage,
                Messages.subscribeLinkLabel, SWT.NONE);
        toolkit.createLabel(messagePage, Messages.notificationsLabel);
        hyperlink.addHyperlinkListener(new HyperlinkAdapter() {
            @Override
            public void linkActivated(HyperlinkEvent e) {
                subscribeAction.run();
            }
        });
        messagePage.setBackground(Display.getDefault().getSystemColor(
                SWT.COLOR_LIST_BACKGROUND));

        showPage(tree);

        subscribeAction = new SubscribeAction(section);
    }

    /*
     * @see Widget#dispose()
     */
    @Override
    public void dispose() {
        super.dispose();
        if (notificationImage != null) {
            notificationImage.dispose();
        }
    }

    /**
     * Notifies that selection has been changed.
     * 
     * @param selection
     *            The selection
     */
    public void selectionChanged(ISelection selection) {
        if (!(selection instanceof StructuredSelection)) {
            return;
        }

        objectName = getObjectName((StructuredSelection) selection);
        if (objectName == null) {
            return;
        }

        tree.setInput(objectName);
        subscribeAction.setSelection(objectName);

        refresh();
    }

    /**
     * Refreshes.
     */
    protected void refresh() {
        new RefreshJob(Messages.refreshNotificationTabJobLabel, toString()) {
            private boolean isSubscribed;
            private boolean isSupported;

            @Override
            protected void refreshModel(IProgressMonitor monitor) {
                IActiveJvm jvm = section.getJvm();
                if (objectName == null || jvm == null || !jvm.isConnected()) {
                    return;
                }
                isSupported = jvm.getMBeanServer().getMBeanNotification()
                        .isSupported(objectName);
                if (isSupported) {
                    isSubscribed = jvm.getMBeanServer().getMBeanNotification()
                            .isSubscribed(objectName);
                }
            }

            @Override
            protected void refreshUI() {
                if (tree.isDisposed() || messagePage.isDisposed()) {
                    return;
                }

                if (!isSupported) {
                    tabItem.dispose();
                    return;
                }

                if (tabItem.isDisposed() && isSupported) {
                    addTabItem();
                }

                tree.setInput(objectName);
                treeViewer.refresh();
                updatePage(isSubscribed);
            }
        }.schedule();
    }

    /**
     * Clears the notifications.
     */
    protected void clear() {
        IActiveJvm jvm = section.getJvm();
        if (jvm != null) {
            jvm.getMBeanServer().getMBeanNotification().clear(objectName);
        }
    }

    /**
     * Invoked when section is deactivated.
     */
    protected void deactivated() {
        Job.getJobManager().cancel(toString());
    }

    /**
     * Adds the tab item.
     */
    void addTabItem() {
        tabItem = new CTabItem(tabFolder, SWT.NONE);
        tabItem.setText(Messages.notificationsTabLabel);
        tabItem.setImage(getNotificationImage());
        tabItem.setControl(this);
    }

    /**
     * Update the page.
     * 
     * @param isSubscribed
     *            The state indicating if the notification is subscribed
     */
    void updatePage(boolean isSubscribed) {
        if (isSubscribed) {
            showPage(tree);
        } else {
            showPage(messagePage);
        }
    }

    /**
     * Gets the object name.
     * 
     * @param selection
     *            The selection
     * @return The object name
     */
    private ObjectName getObjectName(StructuredSelection selection) {
        Object element = selection.getFirstElement();
        if (element instanceof MBeanType) {
            MBeanName[] mBeanNames = ((MBeanType) element).getMBeanNames();
            if (mBeanNames != null && mBeanNames.length == 1) {
                return mBeanNames[0].getObjectName();
            }
        } else if (element instanceof MBeanName) {
            return ((MBeanName) element).getObjectName();
        }

        return null;
    }

    /**
     * Gets the notification image.
     * 
     * @return The notification image
     */
    private Image getNotificationImage() {
        if (notificationImage == null || notificationImage.isDisposed()) {
            notificationImage = Activator.getImageDescriptor(
                    ISharedImages.NOTIFICATION_IMG_PATH).createImage();
        }
        return notificationImage;
    }
}
