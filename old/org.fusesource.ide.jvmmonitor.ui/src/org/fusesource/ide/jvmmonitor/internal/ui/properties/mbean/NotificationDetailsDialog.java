/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.mbean;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.management.Notification;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.PlatformUI;
import org.fusesource.ide.jvmmonitor.internal.ui.IHelpContextIds;
import org.fusesource.ide.jvmmonitor.internal.ui.actions.CopyAction;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.PropertiesColumn;
import org.fusesource.ide.jvmmonitor.ui.Activator;
import org.fusesource.ide.jvmmonitor.ui.ISharedImages;

/**
 * The notification details dialog.
 */
class NotificationDetailsDialog extends Dialog {

    /** The dialog height. */
    private static final int DIALOG_HEIGHT = 500;

    /** The dialog width. */
    private static final int DIALOG_WIDTH = 450;

    /** The date format. */
    private static final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss.SSS"; //$NON-NLS-1$

    /** The date label. */
    private Label dateLabel;

    /** The sequence number label. */
    private Label sequenceNumberText;

    /** The source label. */
    private Label sourceLabel;

    /** The type label. */
    private Label typeLabel;

    /** The message text. */
    private Text messageText;

    /** The previous button. */
    private Button prevButton;

    /** The next button. */
    private Button nextButton;

    /** The details viewer. */
    TreeViewer detailsViewer;

    /** The previous image. */
    private Image prevImage;

    /** The next image. */
    private Image nextImage;

    /** The notification tree. */
    NotificationFilteredTree tree;

    /** The copy action. */
    CopyAction copyAction;

    /**
     * The constructor.
     * 
     * @param tree
     *            The notification tree
     */
    protected NotificationDetailsDialog(NotificationFilteredTree tree) {
        super(tree.getShell());
        this.tree = tree;
        setShellStyle(SWT.MODELESS | SWT.MIN | SWT.MAX | SWT.RESIZE | SWT.CLOSE
                | SWT.BORDER | SWT.TITLE);
    }

    /*
     * @see Dialog#create()
     */
    @Override
    public void create() {
        super.create();
        getShell().setSize(Math.max(DIALOG_WIDTH, getShell().getSize().x),
                DIALOG_HEIGHT);
        getShell().setText(Messages.notificationDetailsLabel);
    }

    /*
     * @see Window#configureShell(Shell)
     */
    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        PlatformUI.getWorkbench().getHelpSystem()
                .setHelp(newShell, IHelpContextIds.NOTIFICATION_DETAILS_DIALOG);
    }

    /*
     * @see Dialog#close()
     */
    @Override
    public boolean close() {
        dispose();
        return super.close();
    }

    /*
     * @see Dialog#createDialogArea(Composite)
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite composite = new Composite(parent, SWT.NULL);
        composite.setLayout(new GridLayout(2, false));
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Composite textsPanel = new Composite(composite, SWT.NONE);
        createTexts(textsPanel);
        textsPanel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Composite buttonsPanel = new Composite(composite, SWT.NONE);
        createButtons(buttonsPanel);
        buttonsPanel.setLayoutData(new GridData());

        Composite treePanel = new Composite(composite, SWT.NONE);
        createTree(treePanel);
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        gridData.horizontalSpan = 2;
        treePanel.setLayoutData(gridData);

        refreshWidgets();
        return composite;
    }

    /*
     * @see Dialog#isResizable()
     */
    @Override
    protected boolean isResizable() {
        return true;
    }

    /*
     * @see Dialog#createButtonsForButtonBar(Composite)
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
                true).setFocus();
    }

    /**
     * Gets the state indicating if the dialog is opened.
     * 
     * @return The state indicating if the dialog is opened
     */
    protected boolean isOpened() {
        Shell shell = getShell();
        return shell != null && shell.isVisible();
    }

    /**
     * Refreshes the widgets.
     */
    protected void refreshWidgets() {
        final Notification notification = (Notification) ((StructuredSelection) tree
                .getViewer().getSelection()).getFirstElement();
        if (notification == null) {
            return;
        }

        dateLabel.setText(new SimpleDateFormat(DATE_FORMAT).format(new Date(
                notification.getTimeStamp())));
        sequenceNumberText.setText(String.valueOf(notification
                .getSequenceNumber()));
        sourceLabel.setText(notification.getSource().toString());
        typeLabel.setText(notification.getType());
        messageText.setText(notification.getMessage());

        Object userData = notification.getUserData();
        if (userData != null) {
            IContentProvider contentProvider = detailsViewer
                    .getContentProvider();
            if (contentProvider != null) {
                ((DetailsContentProvider) contentProvider).refresh(userData);
            }
        }

        if (prevButton != null && !prevButton.isDisposed()) {
            prevButton.setEnabled(tree.getPrevItem() != null);
        }
        if (nextButton != null && !nextButton.isDisposed()) {
            nextButton.setEnabled(tree.getNextItem() != null);
        }
    }

    /**
     * Disposes the resources.
     */
    private void dispose() {
        if (prevImage != null) {
            prevImage.dispose();
        }
        if (nextImage != null) {
            nextImage.dispose();
        }
    }

    /**
     * Creates the texts.
     * 
     * @param composite
     *            The parent composite
     */
    private void createTexts(Composite composite) {
        composite.setLayout(new GridLayout(2, false));

        new Label(composite, SWT.NONE).setText(Messages.dateLabel);
        dateLabel = new Label(composite, SWT.NONE);
        dateLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        new Label(composite, SWT.NONE).setText(Messages.sequenceNumberLabel);
        sequenceNumberText = new Label(composite, SWT.NONE);
        sequenceNumberText
                .setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        new Label(composite, SWT.NONE).setText(Messages.sourceLabel);
        sourceLabel = new Label(composite, SWT.NONE);
        sourceLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        new Label(composite, SWT.NONE).setText(Messages.typeLabel);
        typeLabel = new Label(composite, SWT.NONE);
        typeLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label messageLabel = new Label(composite, SWT.NONE);
        messageLabel.setText(Messages.messageLabel);
        messageText = new Text(composite, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL
                | SWT.WRAP);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.heightHint = 44;
        messageText.setLayoutData(gridData);
        messageText.setEditable(false);
    }

    /**
     * Creates the buttons.
     * 
     * @param composite
     *            The parent composite
     */
    private void createButtons(Composite composite) {
        composite.setLayout(new GridLayout(1, false));
        prevButton = new Button(composite, SWT.PUSH);
        prevButton.setImage(getPrevImage());
        prevButton.setToolTipText(Messages.prevButtonToolTip);
        prevButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                tree.selectPrevItem();
                refreshWidgets();
            }
        });

        nextButton = new Button(composite, SWT.PUSH);
        nextButton.setImage(getNextImage());
        nextButton.setToolTipText(Messages.nextButtonToolTip);
        nextButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                tree.selectNextItem();
                refreshWidgets();
            }
        });
    }

    /**
     * Creates the tree.
     * 
     * @param composite
     *            The parent composite
     */
    private void createTree(Composite composite) {
        composite.setLayout(new FillLayout());

        detailsViewer = new TreeViewer(composite, SWT.MULTI
                | SWT.FULL_SELECTION);
        configureTree(detailsViewer.getTree());

        detailsViewer.setContentProvider(new DetailsContentProvider());
        detailsViewer.setLabelProvider(new DetailsLabelProvider());
        detailsViewer.setInput(new Object());

        copyAction = new CopyAction();
        copyAction.setActionDefinitionId(null);
        detailsViewer.addSelectionChangedListener(copyAction);
    }

    /**
     * Configures the tree.
     * 
     * @param detailsTree
     *            The tree
     */
    private void configureTree(Tree detailsTree) {
        detailsTree.setLinesVisible(true);
        detailsTree.setHeaderVisible(true);

        for (PropertiesColumn column : PropertiesColumn.values()) {
            TreeColumn treeColumn = new TreeColumn(detailsTree, SWT.NONE);
            treeColumn.setText(column.label);
            treeColumn.setWidth(column.defalutWidth);
            treeColumn.setAlignment(column.alignment);
            treeColumn.setToolTipText(column.toolTip);
        }

        MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            @Override
            public void menuAboutToShow(IMenuManager manager) {
                manager.add(copyAction);
            }
        });
        Menu menu = menuMgr.createContextMenu(detailsTree);
        detailsTree.setMenu(menu);
    }

    /**
     * Gets the next image.
     * 
     * @return The next image
     */
    private Image getNextImage() {
        if (nextImage == null || nextImage.isDisposed()) {
            nextImage = Activator.getImageDescriptor(
                    ISharedImages.NEXT_IMG_PATH).createImage();
        }
        return nextImage;
    }

    /**
     * Gets the previous image.
     * 
     * @return The previous image
     */
    private Image getPrevImage() {
        if (prevImage == null || prevImage.isDisposed()) {
            prevImage = Activator.getImageDescriptor(
                    ISharedImages.PREV_IMG_PATH).createImage();
        }
        return prevImage;
    }

    /**
     * The content provider for notification details.
     */
    private class DetailsContentProvider implements ITreeContentProvider {

        /** The attribute root node. */
        protected AttributeNode attributeRootNode;

        /**
         * The constructor.
         */
        public DetailsContentProvider() {
            // do nothing
        }

        /*
         * @see IContentProvider#inputChanged(Viewer, Object, Object)
         */
        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            // do nothing
        }

        /*
         * @see ITreeContentProvider#getElements(Object)
         */
        @Override
        public Object[] getElements(Object input) {
            if (attributeRootNode != null) {
                if (attributeRootNode.hasChildren()) {
                    return attributeRootNode.getChildren().toArray(
                            new AttributeNode[0]);
                }
                return new AttributeNode[] { attributeRootNode };
            }
            return new Object[0];
        }

        /*
         * @see ITreeContentProvider#getChildren(Object)
         */
        @Override
        public Object[] getChildren(Object parentElement) {
            if (parentElement instanceof AttributeNode) {
                return ((AttributeNode) parentElement).getChildren().toArray(
                        new AttributeNode[0]);
            }
            return null;
        }

        /*
         * @see ITreeContentProvider#hasChildren(Object)
         */
        @Override
        public boolean hasChildren(Object element) {
            if (element instanceof AttributeNode) {
                return ((AttributeNode) element).hasChildren();
            }
            return false;
        }

        /*
         * @see ITreeContentProvider#getParent(Object)
         */
        @Override
        public Object getParent(Object element) {
            return null;
        }

        /*
         * @see IContentProvider#dispose()
         */
        @Override
        public void dispose() {
            // do nothing
        }

        /**
         * Refreshes the attribute root node.
         * 
         * @param userData
         *            The user data
         */
        public void refresh(Object userData) {
            if (attributeRootNode != null
                    && attributeRootNode.getName().equals(
                            userData.getClass().getCanonicalName())) {
                attributeRootNode.setValue(userData);
            } else {
                attributeRootNode = new AttributeNode(userData.getClass()
                        .getCanonicalName(), null, userData);
            }
            new AttributeParser().refreshAttribute(attributeRootNode);

            Display.getDefault().asyncExec(new Runnable() {
                @Override
                public void run() {
                    detailsViewer.refresh();
                }
            });
        }
    }

    /**
     * The label provider for notification details.
     */
    private static class DetailsLabelProvider extends LabelProvider implements
            ITableLabelProvider {

        /**
         * The constructor.
         */
        public DetailsLabelProvider() {
            // do nothing
        }

        /*
         * @see ITableLabelProvider#getColumnText(Object, int)
         */
        @Override
        public String getColumnText(Object element, int columnIndex) {
            AttributeNode attribute = (AttributeNode) element;
            if (columnIndex == 0) {
                return attribute.getName();
            } else if (columnIndex == 1) {
                Object value = attribute.getValue();
                if (value != null && attribute.isValidLeaf()) {
                    return value.toString();
                }
            }
            return ""; //$NON-NLS-1$
        }

        /*
         * @see ITableLabelProvider#getColumnImage(Object, int)
         */
        @Override
        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }
    }
}