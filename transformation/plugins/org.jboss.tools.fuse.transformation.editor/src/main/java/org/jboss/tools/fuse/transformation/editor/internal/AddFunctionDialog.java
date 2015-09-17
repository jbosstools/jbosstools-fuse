/******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: JBoss by Red Hat - Initial implementation.
 *****************************************************************************/
package org.jboss.tools.fuse.transformation.editor.internal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.jboss.tools.fuse.transformation.editor.Activator;
import org.jboss.tools.fuse.transformation.editor.Function;
import org.jboss.tools.fuse.transformation.editor.internal.util.BaseDialog;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util.Colors;
import org.jboss.tools.fuse.transformation.model.Model;

class AddFunctionDialog extends BaseDialog {

    final Model model;
    final IProject project;
    Method method;

    AddFunctionDialog(Shell shell,
                      Model model,
                      IProject project) {
        super(shell);
        this.model = model;
        this.project = project;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.tools.fuse.transformation.editor.internal.util.BaseDialog#constructContents(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected void constructContents(Composite parent) {
        parent.setLayout(GridLayoutFactory.fillDefaults().create());
        SashForm splitter = new SashForm(parent, SWT.VERTICAL);
        splitter.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
        splitter.setBackground(Colors.SASH);
        Composite functionPane = new Composite(splitter, SWT.NONE);
        functionPane.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).create());
        ListViewer viewer = new ListViewer(functionPane, SWT.SINGLE | SWT.BORDER);
        viewer.getList().setLayoutData(GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.FILL).grab(false, true).create());
        viewer.setLabelProvider(new LabelProvider() {

            @Override
            public String getText(Object element) {
                return ((Method)element).getName();
            }
        });
        viewer.setComparator(new ViewerComparator() {

            @Override
            public int compare(Viewer viewer, Object element1, Object element2) {
                return ((Method)element1).getName().compareTo(((Method)element2).getName());
            }
        });
        final Map<Method, Function> functionsByMethod = new HashMap<>();
        for (IConfigurationElement element :
             Platform.getExtensionRegistry().getConfigurationElementsFor(Activator.PLUGIN_ID, "function")) {
            try {
                Function function = (Function)element.createExecutableExtension("class");
                // Copy function source to project
                copySourceToProject(Util.RESOURCES_PATH + Function.class.getName().replace('.', '/') + ".java", Function.class);
                copySourceToProject(element.getAttribute("source"), function.getClass());
                for (Method method : function.getClass().getDeclaredMethods()) {
                    Class<?>[] types = method.getParameterTypes();
                    if (!Modifier.isPublic(method.getModifiers()) || types.length < 1
                        || !types[0].getName().equals(model.getType()))
                        continue;
                    viewer.add(method);
                    functionsByMethod.put(method, function);
                }
            } catch (InvalidRegistryObjectException | CoreException | IOException e) {
                Activator.error(e);
            }
        }
        final Label description = new Label(functionPane, SWT.WRAP);
        description.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
        Composite argsPane = new Composite(splitter, SWT.NONE);
        argsPane.setLayout(GridLayoutFactory.swtDefaults().create());
        final Group groupPane = new Group(argsPane, SWT.NONE);
        groupPane.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
        groupPane.setLayout(GridLayoutFactory.swtDefaults().numColumns(4).create());
        groupPane.setText("Arguments");
        viewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                // Remove any existing argument-related components
                for (Control control : groupPane.getChildren()) {
                    control.dispose();
                }
                if (event.getSelection().isEmpty()) {
                    method = null;
                    description.setText("");
                    getButton(IDialogConstants.OK_ID).setEnabled(false);
                    return;
                }
                method = (Method)((IStructuredSelection)event.getSelection()).getFirstElement();
                Function.Info info = functionsByMethod.get(method).info(method);
                description.setText(info.description());
                // Create new components for selected method's arguments
                Label label = new Label(groupPane, SWT.NONE);
                label.setLayoutData(GridDataFactory.fillDefaults().create());
                label.setText("Name");
                label.setBackground(label.getDisplay().getSystemColor(SWT.COLOR_BLACK));
                label.setForeground(label.getDisplay().getSystemColor(SWT.COLOR_WHITE));
                label = new Label(groupPane, SWT.NONE);
                label.setLayoutData(GridDataFactory.fillDefaults().create());
                label.setText("Value");
                label.setBackground(label.getDisplay().getSystemColor(SWT.COLOR_BLACK));
                label.setForeground(label.getDisplay().getSystemColor(SWT.COLOR_WHITE));
                label = new Label(groupPane, SWT.NONE);
                label.setLayoutData(GridDataFactory.fillDefaults().create());
                label.setText("Type");
                label.setBackground(label.getDisplay().getSystemColor(SWT.COLOR_BLACK));
                label.setForeground(label.getDisplay().getSystemColor(SWT.COLOR_WHITE));
                label = new Label(groupPane, SWT.NONE);
                label.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
                label.setText("Description");
                label.setBackground(label.getDisplay().getSystemColor(SWT.COLOR_BLACK));
                label.setForeground(label.getDisplay().getSystemColor(SWT.COLOR_WHITE));
                Class<?>[] types = method.getParameterTypes();
                for (int ndx = 0; ndx < types.length; ndx++) {
                    Class<?> type = types[ndx];
                    label = new Label(groupPane, SWT.NONE);
                    label.setLayoutData(GridDataFactory.swtDefaults().align(SWT.LEFT, SWT.TOP).create());
                    label.setText(info.name(ndx));
                    label = new Label(groupPane, SWT.NONE);
                    label.setLayoutData(GridDataFactory.swtDefaults().align(SWT.LEFT, SWT.TOP).create());
                    if (ndx == 0) {
                        label.setText(model.getName());
                        label.setToolTipText(Util.fullyQualifiedName(model));
                    }
                    label = new Label(groupPane, SWT.NONE);
                    label.setLayoutData(GridDataFactory.swtDefaults().align(SWT.LEFT, SWT.TOP).create());
                    label.setText(type.getSimpleName());
                    label.setToolTipText(type.getName());
                    label = new Label(groupPane, SWT.WRAP);
                    label.setLayoutData(GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.TOP).grab(true, false).create());
                    label.setText(info.description(ndx));
                }
                groupPane.layout();
                getButton(IDialogConstants.OK_ID).setEnabled(true);
            }
        });
    }

    private void copySourceToProject(String sourcePath,
                                     Class<?> sourceClass) throws FileNotFoundException, IOException {
        try (InputStream in = sourceClass.getClassLoader().getResourceAsStream(sourcePath)) {
            IPath path = project.getLocation().append(Util.JAVA_PATH);
            File file = path.append(sourceClass.getPackage().getName().replace('.', '/')).toFile();
            file.mkdirs();
            byte[] buf = new byte[4096];
            try (OutputStream out = new FileOutputStream(new File(file, sourceClass.getSimpleName() + ".java"))) {
                for (int len = in.read(buf); len > 0; len = in.read(buf)) {
                    out.write(buf, 0, len);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.tools.fuse.transformation.editor.internal.util.BaseDialog#message()
     */
    @Override
    protected String message() {
        return "Select a function to perform, along with any applicable arguments";
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.tools.fuse.transformation.editor.internal.util.BaseDialog#title()
     */
    @Override
    protected String title() {
        return "Add Function";
    }
}
