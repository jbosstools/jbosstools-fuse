/******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial implementation.
 *****************************************************************************/
package org.jboss.tools.fuse.transformation.editor.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.ui.ISharedImages;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.actions.OpenNewClassWizardAction;
import org.eclipse.jdt.ui.wizards.NewClassWizardPage;
import org.eclipse.jface.dialogs.IDialogConstants;
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
import org.jboss.tools.fuse.transformation.editor.Activator;
import org.jboss.tools.fuse.transformation.editor.internal.l10n.Messages;
import org.jboss.tools.fuse.transformation.editor.internal.util.BaseDialog;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util.Decorations;

final class AddCustomTransformationDialog extends BaseDialog {

    private static final String[] TYPES = {
        "java.lang.String", //$NON-NLS-1$
        "java.lang.Integer", //$NON-NLS-1$
        "java.lang.Boolean", //$NON-NLS-1$
        "java.lang.Long", //$NON-NLS-1$
        "java.lang.Double", //$NON-NLS-1$
        "java.lang.Float", //$NON-NLS-1$
        "java.util.Date", //$NON-NLS-1$
        "java.lang.Short", //$NON-NLS-1$
        "java.lang.Character", //$NON-NLS-1$
        "java.lang.Byte", //$NON-NLS-1$
    };

    private final IProject project;
    private final String sourceType;
    IType type;
    IMethod method;

    AddCustomTransformationDialog(Shell shell,
                                  IProject project,
                                  String sourceType) {
        super(shell);
        this.project = project;
        this.sourceType = Util.nonPrimitiveClassName(sourceType);
    }

    @Override
    protected void constructContents(Composite parent) {
        parent.setLayout(GridLayoutFactory.swtDefaults().numColumns(3).create());
        Label label = new Label(parent, SWT.NONE);
        label.setText(Messages.AddCustomTransformationDialog_label_Class);
        final Button classButton = new Button(parent, SWT.NONE);
        classButton.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).create());
        classButton.setAlignment(SWT.LEFT);
        classButton.setText(Messages.AddCustomTransformationDialog_button_selectAnExistingClass);
        Button newClassButton = new Button(parent, SWT.NONE);
        newClassButton.setImage(new DecorationOverlayIcon(JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_CLASS),
                                                          Decorations.ADD,
                                                          IDecoration.TOP_RIGHT).createImage());
        label = new Label(parent, SWT.NONE);
        label.setText(Messages.AddCustomTransformationDialog_label_method);
        final ComboViewer methodComboViewer = new ComboViewer(parent, SWT.READ_ONLY);
        methodComboViewer.getCombo().setLayoutData(GridDataFactory.swtDefaults()
                                                                  .span(2, 1)
                                                                  .align(SWT.FILL, SWT.CENTER)
                                                                  .grab(true, false)
                                                                  .create());
        methodComboViewer.setContentProvider(new ArrayContentProvider());
        methodComboViewer.setLabelProvider(new LabelProvider() {

            @Override
            public String getText(Object element) {
                IMethod method = (IMethod)element;
                try {
                    StringBuilder builder = new StringBuilder();
                    builder.append(Signature.getSignatureSimpleName(method.getReturnType()));
                    builder.append(" "); //$NON-NLS-1$
                    builder.append(method.getElementName());
                    builder.append("("); //$NON-NLS-1$
                    String[] types = method.getParameterTypes();
                    String[] names = method.getParameterNames();
                    boolean hasPrm = false;
                    for (int ndx = 0; ndx < types.length; ndx++) {
                        if (hasPrm) builder.append(", "); //$NON-NLS-1$
                        else {
                            builder.append(" "); //$NON-NLS-1$
                            hasPrm = true;
                        }
                        builder.append(Signature.getSignatureSimpleName(types[ndx]));
                        builder.append(" "); //$NON-NLS-1$
                        builder.append(names[ndx]);
                    }
                    if (hasPrm) builder.append(" "); //$NON-NLS-1$
                    builder.append(")"); //$NON-NLS-1$
                    return builder.toString();
                } catch (JavaModelException e) {
                    return ""; //$NON-NLS-1$
                }
            }
        });
        methodComboViewer.setComparator(new ViewerComparator() {

            @Override
            public int compare(Viewer viewer,
                               Object object1,
                               Object object2) {
                IMethod method1 = (IMethod)object1;
                IMethod method2 = (IMethod)object2;
                int comparison = method1.getElementName().compareTo(method2.getElementName());
                if (comparison != 0) return comparison;
                String[] types1 = method1.getParameterTypes();
                String[] types2 = method2.getParameterTypes();
                comparison = types1.length - types2.length;
                if (comparison != 0) return comparison;
                for (int ndx = 0; ndx < types1.length; ndx++) {
                    comparison =
                        Signature.getSignatureSimpleName(types1[ndx]).compareTo(Signature.getSignatureSimpleName(types2[ndx]));
                    if (comparison != 0) return comparison;
                }
                return 0;
            }
        });
        methodComboViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                methodSelected(methodComboViewer);
            }
        });
        classButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                try {
                    selectClass(classButton, methodComboViewer);
                } catch (JavaModelException e) {
                    Activator.error(e);
                }
            }
        });
        newClassButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                createNewClass(classButton, methodComboViewer);
            }
        });
    }

    private void createNewClass(Button classButton,
                                ComboViewer methodComboViewer) {
        OpenNewClassWizardAction action = new OpenNewClassWizardAction();
        action.setSelection(new StructuredSelection(project));
        Page page = new Page(sourceType);
        page.init(new StructuredSelection(project));
        action.setConfiguredWizardPage(page);
        action.run();
        IType type = (IType)action.getCreatedElement();
        if (type != null) {
            try {
                if (page.returnType.equals("Date")) page.returnType = "java.util.Date"; //$NON-NLS-1$ //$NON-NLS-2$
                if (page.prmType.equals("Date")) page.prmType = "java.util.Date"; //$NON-NLS-1$ //$NON-NLS-2$
                type.createMethod("public " + page.returnType + " " + page.methodName + "(" + page.prmType + " input) {\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                                  + "\treturn null;\n" + "}", //$NON-NLS-1$ //$NON-NLS-2$
                                  null, false, null);
                if (type.getCompilationUnit().isWorkingCopy()) type.getCompilationUnit().commitWorkingCopy(true, null);
                setClass(type, classButton, methodComboViewer);
            } catch (Exception e) {
                Activator.error(e);
            }
        }
    }

    @Override
    protected String message() {
        return Messages.AddCustomTransformationDialog_dialogMessage;
    }

    private void methodSelected(ComboViewer methodComboViewer) {
        IStructuredSelection selection = (IStructuredSelection)methodComboViewer.getSelection();
        method = (IMethod)selection.getFirstElement();
    }

    private void selectClass(Button classButton,
                             ComboViewer methodComboViewer) throws JavaModelException {
        Util.Filter filter = new Util.Filter() {

            @Override
            public boolean accept(IType type) {
                try {
                    for (IMethod method : type.getMethods()) {
                        if (valid(method)) return true;
                    }
                } catch (JavaModelException ignored) {}
                return false;
            }
        };
        IType type = Util.selectCustomTransformationClass(getShell(), project, filter);
        if (type != null) setClass(type, classButton, methodComboViewer);
    }

    private void setClass(IType type,
                          Button classButton,
                          ComboViewer methodComboViewer) throws JavaModelException {
        classButton.setText(type.getFullyQualifiedName());
        List<IMethod> methods = new ArrayList<>(Arrays.asList(type.getMethods()));
        for (Iterator<IMethod> iter = methods.iterator(); iter.hasNext();) {
            if (!valid(iter.next())) iter.remove();
        }
        methodComboViewer.setInput(methods.toArray());
        if (!methods.isEmpty()) methodComboViewer.setSelection(new StructuredSelection(methods.get(0)));
        this.type = type;
        getButton(IDialogConstants.OK_ID).setEnabled(true);
    }

    @Override
    protected String title() {
        return Messages.AddCustomTransformationDialog_dialogTitle;
    }

    private boolean valid(IMethod method) throws JavaModelException {
        String[] types = method.getParameterTypes();
        return Arrays.asList(TYPES).contains(resolvedTypeName(method, method.getReturnType()))
               && types.length == 1
               && resolvedTypeName(method, types[0]).equals(sourceType);
    }

    private String resolvedTypeName(IMethod method,
                                    String typeName) throws JavaModelException {
        typeName = Signature.toString(typeName);
        if (typeName.contains(".")) return typeName; //$NON-NLS-1$
        for (IImportDeclaration decl : method.getCompilationUnit().getImports()) {
            if (decl.getElementName().endsWith("." + typeName)) return decl.getElementName(); //$NON-NLS-1$
        }
        return "java.lang." + typeName; //$NON-NLS-1$
    }

    private class Page extends NewClassWizardPage {

        private final String sourceType;
        private String returnType;
        private String methodName;
        private String prmType;
        private IStatus returnTypeStatus = typeStatus(null, "return"); //$NON-NLS-1$
        private IStatus methodNameStatus = nameStatus(null, "method"); //$NON-NLS-1$
        private IStatus prmTypeStatus = typeStatus(null, "parameter"); //$NON-NLS-1$
        private Control pkgText;

        private Page(final String sourceType) {
            this.sourceType = sourceType;
        }

        private void createComboPane(Composite parent,
                                     String initialText,
                                     String labelText,
                                     final CustomTransformationListener listener) {
            final Combo combo = new Combo(parent, SWT.READ_ONLY);
            combo.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).create());
            combo.setItems(TYPES);
            combo.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent event) {
                    listener.transformationChanged(combo.getText());
                }
            });
            combo.select(combo.indexOf(initialText));
            listener.transformationChanged(initialText);
            new Label(parent, SWT.NONE).setText(labelText);
        }

        @Override
        protected void createEnclosingTypeControls(Composite composite,
                                                   int columns) {}

        private void createLabelPane(Composite parent,
                                     String text) {
            Label label = new Label(parent, SWT.NONE);
            label.setLayoutData(GridDataFactory.swtDefaults().span(2, 1).create());
            label.setText(text);
        }

        @Override
        protected void createModifierControls(Composite composite,
                                              int columns) {}

        @Override
        protected void createPackageControls(Composite composite,
                                             int nColumns) {
            super.createPackageControls(composite, nColumns);
            pkgText = composite.getChildren()[4];
        }

        @Override
        protected void createSuperInterfacesControls(Composite composite,
                                                     int columns) {
            super.createSuperInterfacesControls(composite, columns);

            Group group = new Group(composite, SWT.NONE);
            group.setLayoutData(GridDataFactory.fillDefaults().span(columns, 1).grab(true, false).create());
            group.setLayout(GridLayoutFactory.fillDefaults().spacing(0, 0).numColumns(6).create());
            group.setText(Messages.AddCustomTransformationDialog_GroupText_CustomTransformation);
            createLabelPane(group, Messages.AddCustomTransformationDialog_labelPane_returnType);
            createLabelPane(group, Messages.AddCustomTransformationDialog_labelPane_MethodName);
            createLabelPane(group, Messages.AddCustomTransformationDialog_labelPane_ParameterType);
            createComboPane(group, sourceType, " ", new CustomTransformationListener() { //$NON-NLS-1$

                @Override
                public void transformationChanged(String text) {
                    returnType = text;
                    returnTypeStatus = typeStatus(returnType, "return"); //$NON-NLS-1$
                    updateStatus();
                }
            });
            createTextPane(group, "map", "(", new CustomTransformationListener() { //$NON-NLS-1$ //$NON-NLS-2$

                @Override
                public void transformationChanged(String text) {
                    methodName = text.trim();
                    methodNameStatus = nameStatus(methodName, "method"); //$NON-NLS-1$
                    updateStatus();
                }
            });
            createComboPane(group, sourceType, " input)", new CustomTransformationListener() { //$NON-NLS-1$

                @Override
                public void transformationChanged(String text) {
                    prmType = text;
                    prmTypeStatus = typeStatus(prmType, "parameter"); //$NON-NLS-1$
                    updateStatus();
                }
            });
        }

        private Text createTextPane(Composite parent,
                                    String initialText,
                                    String labelText,
                                    final CustomTransformationListener listener) {
            final Text text = new Text(parent, SWT.BORDER);
            text.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).create());
            text.addModifyListener(new ModifyListener() {

                @Override
                public void modifyText(final ModifyEvent event) {
                    listener.transformationChanged(text.getText());
                }
            });
            text.setText(initialText);
            new Label(parent, SWT.NONE).setText(labelText);
            return text;
        }

        private IStatus nameStatus(String name,
                                   String nameName) {
            if (name == null || name.isEmpty())
                return new Status(IStatus.ERROR,
                                  Activator.plugin().getBundle().getSymbolicName(),
                                  "A " + nameName + " name for the custom transformation must be provided"); //$NON-NLS-1$ //$NON-NLS-2$
            char[] chars = name.toCharArray();
            char firstChar = chars[0];
            if (!Character.isJavaIdentifierStart(firstChar))
                return new Status(IStatus.ERROR,
                                  Activator.plugin().getBundle().getSymbolicName(),
                                  "The " + nameName + " name for the custom transformation begins with an invalid character"); //$NON-NLS-1$ //$NON-NLS-2$
            for (int ndx = 1; ndx < chars.length; ++ndx) {
                if (!Character.isJavaIdentifierPart(chars[ndx]))
                    return new Status(IStatus.ERROR,
                                      Activator.plugin().getBundle().getSymbolicName(),
                                      "The " + nameName //$NON-NLS-1$
                                      + " name for the custom transformation contains at least one invalid character"); //$NON-NLS-1$
            }
            if (Character.isUpperCase(firstChar))
                return new Status(IStatus.WARNING,
                                  Activator.plugin().getBundle().getSymbolicName(),
                                  "The " + nameName + " name for the custom transformation begins with an uppercase letter"); //$NON-NLS-1$ //$NON-NLS-2$
            return Status.OK_STATUS;
        }

        @Override
        protected void setFocus() {
            pkgText.setFocus();
        }

        private IStatus typeStatus(String type,
                                   String typeName) {
            return type == null ? new Status(IStatus.ERROR,
                                             Activator.plugin().getBundle().getSymbolicName(),
                                             "A " + typeName + " type for the custom transformation must be selected") //$NON-NLS-1$ //$NON-NLS-2$
                                : Status.OK_STATUS;
        }

        private void updateStatus() {
            updateStatus(new IStatus[] {
                fContainerStatus,
                fPackageStatus,
                fTypeNameStatus,
                fSuperClassStatus,
                fSuperInterfacesStatus
            });
        }

        @Override
        protected void updateStatus(IStatus[] status) {
            List<IStatus> list = new ArrayList<>(Arrays.asList(status));
            list.add(returnTypeStatus);
            list.add(methodNameStatus);
            list.add(prmTypeStatus);
            super.updateStatus(list.toArray(new IStatus[list.size()]));
        }
    }

    private interface CustomTransformationListener {

        void transformationChanged(String text);
    }
}
