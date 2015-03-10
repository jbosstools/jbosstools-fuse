/******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: JBoss by Red Hat - Initial implementation.
 *****************************************************************************/
package org.jboss.mapper.eclipse.internal.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.ui.ISharedImages;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.actions.OpenNewClassWizardAction;
import org.eclipse.jdt.ui.wizards.NewClassWizardPage;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.jboss.mapper.eclipse.Activator;
import org.jboss.mapper.eclipse.internal.editor.MappingsViewer.CustomFunctionListener;
import org.jboss.mapper.eclipse.internal.util.Util;

final class AddCustomFunctionDialog extends TitleAreaDialog {

    IProject project;
    String sourceType;
    IType type;
    IMethod method;

    AddCustomFunctionDialog(final Shell shell,
            final IProject project,
            final String sourceType) {
        super(shell);
        this.project = project;
        this.sourceType = "java.lang.String".equals(sourceType) ? "String" : sourceType;
    }

    @Override
    public void create() {
        super.create();
        getButton(IDialogConstants.OK_ID).setEnabled(false);
    }

    @Override
    protected Control createDialogArea(final Composite parent) {
        setTitle("Add Custom Operation");
        setMessage("Select or create the Java class and method that implements the custom operation");
        setHelpAvailable(false);
        final Composite area = new Composite(parent, SWT.NONE);
        area.setLayout(GridLayoutFactory.swtDefaults().numColumns(3).create());
        area.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
        Label label = new Label(area, SWT.NONE);
        label.setText("Class:");
        final Button classButton = new Button(area, SWT.NONE);
        classButton.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER)
                .grab(true, false).create());
        classButton.setAlignment(SWT.LEFT);
        classButton.setText("< Click to select an existing class >");
        final Button newClassButton = new Button(area, SWT.NONE);
        newClassButton.setImage(new DecorationOverlayIcon(JavaUI.getSharedImages().getImage(
                ISharedImages.IMG_OBJS_CLASS),
                Util.Decorations.ADD,
                IDecoration.TOP_RIGHT).createImage());
        label = new Label(area, SWT.NONE);
        label.setText("Method:");
        final ComboViewer methodComboViewer = new ComboViewer(area, SWT.READ_ONLY);
        methodComboViewer.getCombo().setLayoutData(GridDataFactory.swtDefaults()
                .span(2, 1)
                .align(SWT.FILL, SWT.CENTER)
                .grab(true, false)
                .create());
        methodComboViewer.setContentProvider(new ArrayContentProvider());
        methodComboViewer.setLabelProvider(new LabelProvider() {

            @Override
            public String getText(final Object element) {
                final IMethod method = (IMethod) element;
                try {
                    final StringBuilder builder = new StringBuilder();
                    builder.append(Signature.getSignatureSimpleName(method.getReturnType()));
                    builder.append(" ");
                    builder.append(method.getElementName());
                    builder.append("(");
                    final String[] types = method.getParameterTypes();
                    final String[] names = method.getParameterNames();
                    boolean hasPrm = false;
                    for (int ndx = 0; ndx < types.length; ndx++) {
                        if (hasPrm) {
                            builder.append(", ");
                        } else {
                            builder.append(" ");
                            hasPrm = true;
                        }
                        builder.append(Signature.getSignatureSimpleName(types[ndx]));
                        builder.append(" ");
                        builder.append(names[ndx]);
                    }
                    if (hasPrm) {
                        builder.append(" ");
                    }
                    builder.append(")");
                    return builder.toString();
                } catch (final JavaModelException e) {
                    return "";
                }
            }
        });
        methodComboViewer.setComparator(new ViewerComparator() {

            @Override
            public int compare(final Viewer viewer,
                    final Object object1,
                    final Object object2) {
                final IMethod method1 = (IMethod) object1;
                final IMethod method2 = (IMethod) object2;
                int comparison = method1.getElementName().compareTo(method2.getElementName());
                if (comparison != 0) {
                    return comparison;
                }
                final String[] types1 = method1.getParameterTypes();
                final String[] types2 = method2.getParameterTypes();
                comparison = types1.length - types2.length;
                if (comparison != 0) {
                    return comparison;
                }
                for (int ndx = 0; ndx < types1.length; ndx++) {
                    comparison =
                            Signature.getSignatureSimpleName(types1[ndx]).compareTo(
                                    Signature.getSignatureSimpleName(types2[ndx]));
                    if (comparison != 0) {
                        return comparison;
                    }
                }
                return 0;
            }
        });
        methodComboViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(final SelectionChangedEvent event) {
                methodSelected(methodComboViewer);
            }
        });
        classButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
                selectClass(classButton, methodComboViewer);
            }
        });
        newClassButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
                createNewClass(classButton, methodComboViewer);
            }
        });
        return area;
    }

    void createNewClass(final Button classButton,
            final ComboViewer methodComboViewer) {
        final OpenNewClassWizardAction action = new OpenNewClassWizardAction();
        action.setSelection(new StructuredSelection(project));
        final Page page = new Page(sourceType);
        page.init(new StructuredSelection(project));
        action.setConfiguredWizardPage(page);
        action.run();
        final IType type = (IType) action.getCreatedElement();
        if (type != null) {
            try {
                type.createMethod("public " + page.returnType + " " + page.methodName + "("
                        + page.prmType + " input) {\n"
                        + "\treturn null;\n"
                        + "}",
                        null, false, null);
                if (type.getCompilationUnit().isWorkingCopy()) {
                    type.getCompilationUnit().commitWorkingCopy(true, null);
                }
                setClass(type, classButton, methodComboViewer);
            } catch (final JavaModelException e) {
                Activator.error(e);
            }
        }
    }

    @Override
    protected int getShellStyle() {
        return super.getShellStyle() | SWT.RESIZE;
    }

    void methodSelected(final ComboViewer methodComboViewer) {
        method =
                (IMethod) ((IStructuredSelection) methodComboViewer.getSelection())
                        .getFirstElement();
    }

    void selectClass(final Button classButton,
            final ComboViewer methodComboViewer) {
        final Util.Filter filter = new Util.Filter() {

            @Override
            public boolean accept(final IType type) {
                try {
                    for (final IMethod method : type.getMethods()) {
                        if (valid(method)) {
                            return true;
                        }
                    }
                } catch (final JavaModelException ignored) {
                }
                return false;
            }
        };
        final IType type = Util.selectClass(getShell(), project, filter);
        if (type != null) {
            setClass(type, classButton, methodComboViewer);
        }
    }

    void setClass(final IType type,
            final Button classButton,
            final ComboViewer methodComboViewer) {
        try {
            classButton.setText(type.getFullyQualifiedName());
            final List<IMethod> methods = new ArrayList<>(Arrays.asList(type.getMethods()));
            for (final Iterator<IMethod> iter = methods.iterator(); iter.hasNext();) {
                if (!valid(iter.next())) {
                    iter.remove();
                }
            }
            methodComboViewer.setInput(methods.toArray());
            if (!methods.isEmpty()) {
                methodComboViewer.setSelection(new StructuredSelection(methods.get(0)));
            }
            this.type = type;
            getButton(IDialogConstants.OK_ID).setEnabled(true);
        } catch (final JavaModelException e) {
            Activator.error(e);
        }
    }

    boolean valid(final IMethod method) {
        try {
            return !Signature.getSignatureSimpleName(method.getReturnType()).equals("void")
                    && method.getParameters().length == 1;
        } catch (final JavaModelException e) {
            return false;
        }
    }

    private class Page extends NewClassWizardPage {

        String sourceType;
        String returnType;
        String methodName;
        String prmType;
        IStatus returnTypeStatus = typeStatus(null, "return");
        IStatus methodNameStatus = nameStatus(null, "method");
        IStatus prmTypeStatus = typeStatus(null, "parameter");
        Control pkgText;

        Page(final String sourceType) {
            this.sourceType = sourceType;
        }

        private void createComboPane(final Composite parent,
                final String initialText,
                final String labelText,
                final CustomFunctionListener listener) {
            final Combo combo = new Combo(parent, SWT.READ_ONLY);
            combo.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER)
                    .grab(true, false).create());
            combo.setItems(new String[] {
                    "boolean",
                    "byte",
                    "char",
                    "double",
                    "float",
                    "int",
                    "java.util.List< ? >",
                    "long",
                    "Object",
                    "short",
                    "String",
            });
            combo.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(final SelectionEvent event) {
                    listener.functionChanged(combo.getText());
                }
            });
            combo.select(combo.indexOf(initialText));
            listener.functionChanged(initialText);
            final Label label = new Label(parent, SWT.NONE);
            label.setText(labelText);
        }

        @Override
        protected void createEnclosingTypeControls(final Composite composite,
                final int columns) {}

        private void createLabelPane(final Composite parent,
                final String text) {
            final Label label = new Label(parent, SWT.NONE);
            label.setLayoutData(GridDataFactory.swtDefaults().span(2, 1).create());
            label.setText(text);
        }

        @Override
        protected void createModifierControls(final Composite composite,
                final int columns) {}

        @Override
        protected void createPackageControls(final Composite composite,
                final int nColumns) {
            super.createPackageControls(composite, nColumns);
            pkgText = composite.getChildren()[4];
        }

        @Override
        protected void createSuperInterfacesControls(final Composite composite,
                final int columns) {
            super.createSuperInterfacesControls(composite, columns);

            final Group group = new Group(composite, SWT.NONE);
            group.setLayoutData(GridDataFactory.fillDefaults().span(columns, 1).grab(true, false)
                    .create());
            group.setLayout(GridLayoutFactory.fillDefaults().spacing(0, 0).numColumns(6).create());
            group.setText("Custom Function");
            createLabelPane(group, "Return Type");
            createLabelPane(group, "Method Name");
            createLabelPane(group, "Parameter Type");
            createComboPane(group, sourceType, " ", new CustomFunctionListener() {

                @Override
                public void functionChanged(final String text) {
                    returnType = text;
                    returnTypeStatus = typeStatus(returnType, "return");
                    updateStatus();
                }
            });
            createTextPane(group, "map", "(", new CustomFunctionListener() {

                @Override
                public void functionChanged(final String text) {
                    methodName = text.trim();
                    methodNameStatus = nameStatus(methodName, "method");
                    updateStatus();
                }
            });
            createComboPane(group, sourceType, " input)", new CustomFunctionListener() {

                @Override
                public void functionChanged(final String text) {
                    prmType = text;
                    prmTypeStatus = typeStatus(prmType, "parameter");
                    updateStatus();
                }
            });
        }

        private Text createTextPane(final Composite parent,
                final String initialText,
                final String labelText,
                final CustomFunctionListener listener) {
            final Text text = new Text(parent, SWT.BORDER);
            text.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER)
                    .grab(true, false).create());
            text.addModifyListener(new ModifyListener() {

                @Override
                public void modifyText(final ModifyEvent event) {
                    listener.functionChanged(text.getText());
                }
            });
            text.setText(initialText);
            final Label label = new Label(parent, SWT.NONE);
            label.setText(labelText);
            return text;
        }

        IStatus nameStatus(final String name,
                final String nameName) {
            // TODO I think there is an Apache library that does this along with
            // checking for Java reserved keywords
            if (name == null || name.isEmpty())
                return new Status(IStatus.ERROR,
                        Activator.plugin().getBundle().getSymbolicName(),
                        "A " + nameName + " name for the custom operation must be provided");
            final char[] chars = name.toCharArray();
            final char firstChar = chars[0];
            if (!Character.isJavaIdentifierStart(firstChar)) {
                return new Status(IStatus.ERROR,
                        Activator.plugin().getBundle().getSymbolicName(),
                        "The " + nameName
                                + " name for the custom operation begins with an invalid character");
            }
            for (int ndx = 1; ndx < chars.length; ++ndx) {
                if (!Character.isJavaIdentifierPart(chars[ndx])) {
                    return new Status(
                            IStatus.ERROR,
                            Activator.plugin().getBundle().getSymbolicName(),
                            "The "
                                    + nameName
                                    + " name for the custom operation contains at least one invalid character");
                }
            }
            if (Character.isUpperCase(firstChar)) {
                return new Status(IStatus.WARNING,
                        Activator.plugin().getBundle().getSymbolicName(),
                        "The " + nameName
                                + " name for the custom operation begins with an uppercase letter");
            }
            return Status.OK_STATUS;
        }

        @Override
        protected void setFocus() {
            pkgText.setFocus();
        }

        IStatus typeStatus(final String type,
                final String typeName) {
            if (type == null)
                return new Status(IStatus.ERROR,
                        Activator.plugin().getBundle().getSymbolicName(),
                        "A " + typeName + " type for the custom operation must be selected");
            return Status.OK_STATUS;
        }

        void updateStatus() {
            updateStatus(new IStatus[] {
                    fContainerStatus,
                    fPackageStatus,
                    fTypeNameStatus,
                    fSuperClassStatus,
                    fSuperInterfacesStatus
            });
        }

        @Override
        protected void updateStatus(final IStatus[] status) {
            final List<IStatus> list = new ArrayList<>(Arrays.asList(status));
            list.add(returnTypeStatus);
            list.add(methodNameStatus);
            list.add(prmTypeStatus);
            super.updateStatus(list.toArray(new IStatus[list.size()]));
        }
    }
}
