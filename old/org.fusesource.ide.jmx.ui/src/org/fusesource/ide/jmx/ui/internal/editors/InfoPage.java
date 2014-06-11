/*******************************************************************************
 * Copyright (c) 2007 Jeff Mesnil
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

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

package org.fusesource.ide.jmx.ui.internal.editors;


import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.fusesource.ide.jmx.core.MBeanInfoWrapper;
import org.fusesource.ide.jmx.core.MBeanNotificationInfoWrapper;
import org.fusesource.ide.jmx.ui.Messages;


public class InfoPage extends FormPage {

    static final String ID = "info"; //$NON-NLS-1$

    private MBeanInfoWrapper wrapper;

    private Font bold;

    public InfoPage(FormEditor editor) {
        super(editor, ID, Messages.InfoPage_title);
        MBeanEditorInput input = (MBeanEditorInput) editor.getEditorInput();
        this.wrapper = input.getWrapper();
    }

    protected void createFormContent(IManagedForm managedForm) {
        ScrolledForm form = managedForm.getForm();
        form.setText(wrapper.getObjectName().toString());
        FormToolkit toolkit = managedForm.getToolkit();
        form.getForm().setSeparatorVisible(true);

        Composite body = form.getBody();
        FontData fd[] = body.getFont().getFontData();
        bold = new Font(body.getDisplay(), fd[0].getName(), fd[0].getHeight(),
                SWT.BOLD);

        GridLayout layout = new GridLayout(2, false);
        body.setLayout(layout);
        GridDataFactory defaultGridData = GridDataFactory.fillDefaults();

        String className = wrapper.getMBeanInfo().getClassName();

        toolkit.createLabel(body, Messages.className);
        Label classNameLabel = toolkit.createLabel(body, className, SWT.WRAP
                | SWT.READ_ONLY);
        classNameLabel.setFont(bold);
        classNameLabel.setLayoutData(defaultGridData.create());

        String description = wrapper.getMBeanInfo().getDescription();

        toolkit.createLabel(body, Messages.description);
        Text descriptionText = toolkit.createText(body, description, SWT.MULTI
                | SWT.WRAP | SWT.READ_ONLY);
        descriptionText.setLayoutData(defaultGridData.create());

        Section notifSection = toolkit.createSection(body, Section.TITLE_BAR
                | Section.TWISTIE | Section.TWISTIE);
        notifSection.setText(Messages.InfoPage_notificationsSectionTitle);
        GridDataFactory.fillDefaults().span(2, 1).grab(true, true).applyTo(
                notifSection);
        if (wrapper.getMBeanNotificationInfoWrappers().length == 0) {
            notifSection.setEnabled(false);
            notifSection.setExpanded(false);
        } else {
            notifSection.setEnabled(true);
            notifSection.setExpanded(true);
        }

        Composite notificationContainer = toolkit.createComposite(notifSection);
        notifSection.setClient(notificationContainer);
        GridLayoutFactory.fillDefaults().generateLayout(notificationContainer);

        Tree notificationTree = toolkit.createTree(notificationContainer,
                SWT.BORDER);
        GridDataFactory.fillDefaults().hint(500, 150).applyTo(notificationTree);

        TreeViewer notificationViewer = new TreeViewer(notificationTree);
        notificationViewer.setLabelProvider(new LabelProvider() {
            public String getText(Object element) {
                if (element instanceof MBeanNotificationInfoWrapper) {
                    MBeanNotificationInfoWrapper notifWrapper = (MBeanNotificationInfoWrapper) element;
                    return notifWrapper.getMBeanNotificationInfo().getName();
                }
                return super.getText(element);
            }
        });
        notificationViewer.setContentProvider(new ITreeContentProvider() {
            public Object[] getChildren(Object parent) {
                if (parent instanceof MBeanNotificationInfoWrapper) {
                    MBeanNotificationInfoWrapper notifWrapper = (MBeanNotificationInfoWrapper) parent;
                    return notifWrapper.getMBeanNotificationInfo()
                            .getNotifTypes();
                }
                return new Object[0];
            }

            public Object getParent(Object element) {
                return null;
            }

            public boolean hasChildren(Object element) {
                if (element instanceof MBeanNotificationInfoWrapper) {
                    MBeanNotificationInfoWrapper notifWrapper = (MBeanNotificationInfoWrapper) element;
                    return (notifWrapper.getMBeanNotificationInfo()
                            .getNotifTypes().length > 0);
                }
                return false;
            }

            public Object[] getElements(Object input) {
                return ((MBeanInfoWrapper) input)
                        .getMBeanNotificationInfoWrappers();
            }

            public void dispose() {
            }

            public void inputChanged(Viewer viewer, Object oldInput,
                    Object newInput) {
            }
        });
        notificationViewer.setInput(wrapper);
    }

    @Override
    public void dispose() {
        super.dispose();
        if (bold != null) {
            bold.dispose();
            bold = null;
        }
    }
}