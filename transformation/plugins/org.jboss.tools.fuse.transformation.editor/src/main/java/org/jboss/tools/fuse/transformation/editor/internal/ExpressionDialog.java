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
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.fusesource.ide.camel.model.catalog.CamelModelFactory;
import org.fusesource.ide.camel.model.catalog.languages.Language;
import org.jboss.tools.fuse.transformation.Expression;
import org.jboss.tools.fuse.transformation.MappingOperation;
import org.jboss.tools.fuse.transformation.editor.internal.util.BaseDialog;
import org.jboss.tools.fuse.transformation.editor.internal.util.ClasspathResourceSelectionDialog;

/**
 *
 */
public class ExpressionDialog extends BaseDialog {

    final List<Language> languages = new ArrayList<>();
    private Language language;
    private String expression;
    private Button browseBtn;
    private ComboViewer languageComboViewer;
    private Text expressionText;
    private Expression expressionInstance = null;
    private Button valueOption;
    private Button scriptOption;
    private ComboViewer scriptTypeComboViewer;
    private Text pathText;
    private IProject project;

    public ExpressionDialog(Shell shell, MappingOperation<?, ?> mapping, IProject project){
        super(shell);
        this.project = project;
        String languageName = null;
        if (mapping.getSource() instanceof Expression) {
            expressionInstance = (Expression) mapping.getSource();
            this.expression = expressionInstance.getExpression();
            languageName = expressionInstance.getLanguage();
        }
        final String version = CamelModelFactory.getSupportedCamelVersions().get(0);
        for (final Language language : CamelModelFactory.getModelForVersion(version)
                                                        .getLanguageModel()
                                                        .getSupportedLanguages()) {
            final String name = language.getName();
            if (!name.equals("bean") && !name.equals("file") && !name.equals("sql")
                && !name.equals("xtokenize") && !name.equals("tokenize")
                && !name.equals("spel")) {
                if (languageName != null && name.equals(languageName)) {
                    this.language = language;
                }
                languages.add(language);
            }
        }
    }

    @Override
    protected void constructContents(Composite parent) {
        parent.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).create());
        Label label = new Label(parent, SWT.NONE);
        label.setText("Language:");
        languageComboViewer = new ComboViewer(parent, SWT.READ_ONLY);
        languageComboViewer.setContentProvider(ArrayContentProvider.getInstance());
        languageComboViewer.setComparator(new ViewerComparator() {

            @Override
            public int compare(final Viewer viewer,
                               final Object object1,
                               final Object object2) {
                return ((Language)object1).getTitle().compareTo(((Language)object2).getTitle());
            }
        });
        languageComboViewer.setLabelProvider(new LabelProvider() {

            @Override
            public String getText(final Object element) {
                return ((Language)element).getTitle();
            }
        });
        languageComboViewer.getCombo().setLayoutData(GridDataFactory.fillDefaults().grab(true, false).span(1, 1).create());

        Group detailsGroup = new Group(parent, SWT.NONE);
        detailsGroup.setText("Details");
        detailsGroup.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).span(2, 1).create());
        detailsGroup.setLayout(GridLayoutFactory.swtDefaults().numColumns(3).create());

        valueOption = new Button(detailsGroup, SWT.RADIO);
        valueOption.setText("Value");
        valueOption.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).span(3, 1).create());
        valueOption.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                scriptTypeComboViewer.setSelection(new StructuredSelection(""));
                pathText.setText("");
                expression = "";
                expressionText.setText(expression.replace("\\${", "${"));
                validate();
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });

        label = new Label(detailsGroup, SWT.NONE);
        label.setText("Expression:");
        label.setLayoutData(GridDataFactory.fillDefaults().indent(20, 0).create());
        expressionText = new Text(detailsGroup, SWT.BORDER);
        expressionText.setLayoutData(GridDataFactory.fillDefaults().indent(20, 0).grab(true, false).span(2, 1).create());
        if (expression != null) {
            expressionText.setText(expression.replace("\\${", "${"));
        }

        scriptOption = new Button(detailsGroup, SWT.RADIO);
        scriptOption.setText("Script");
        scriptOption.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).span(3, 1).create());
        scriptOption.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                validate();
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });

        label = new Label(detailsGroup, SWT.NONE);
        label.setText("Source:");
        label.setLayoutData(GridDataFactory.fillDefaults().indent(20, 0).create());
        scriptTypeComboViewer = new ComboViewer(detailsGroup, SWT.READ_ONLY);
        scriptTypeComboViewer.setContentProvider(ArrayContentProvider.getInstance());
        scriptTypeComboViewer.getCombo().setLayoutData(GridDataFactory.fillDefaults().grab(true, false).indent(20, 0).span(1, 1).create());
        scriptTypeComboViewer.setContentProvider(new ObservableListContentProvider());
        scriptTypeComboViewer.getCombo().addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent event) {
                updateExpression();
            }
        });

        WritableList sourceList = new WritableList();
        sourceList.add("classpath");
        sourceList.add("file");
        sourceList.add("http");
        sourceList.add("");
        scriptTypeComboViewer.setInput(sourceList);

        browseBtn = new Button(detailsGroup,  SWT.PUSH);
        browseBtn.setText("...");
        browseBtn.setLayoutData(GridDataFactory.fillDefaults().grab(false, false).create());
        browseBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent event) {
                IStructuredSelection selection =
                        (IStructuredSelection)scriptTypeComboViewer.getSelection();
                String value = (String) selection.getFirstElement();
                String path = null;
                if (value.equalsIgnoreCase("classpath")) {
                    path = selectResourceFromWorkspace(browseBtn.getShell(), "");
                } else if (value.equalsIgnoreCase("file")) {
                    FileDialog dialog = new FileDialog(browseBtn.getShell());
                    dialog.setText("Select Script File");
                    String[] filterExt = { "*.*" };
                    dialog.setFilterExtensions(filterExt);
                    path = dialog.open();
                    if (path != null) {
                        path = convertToFileURL(path);
                    }
                }
                if (path != null) {
                    pathText.setText(path);
                    pathText.notifyListeners(SWT.Modify, null);
                }
            }
        });

        label = new Label(detailsGroup, SWT.NONE);
        label.setText("Path:");
        label.setLayoutData(GridDataFactory.fillDefaults().indent(20, 0).create());
        pathText = new Text(detailsGroup, SWT.BORDER);
        pathText.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).indent(20, 0).span(2, 1).create());
        pathText.addModifyListener(new ModifyListener(){

            @Override
            public void modifyText(ModifyEvent e) {
                updateExpression();
            }}
        );

        languageComboViewer.getCombo().addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
                final IStructuredSelection selection =
                    (IStructuredSelection)languageComboViewer.getSelection();
                language = (Language)selection.getFirstElement();
                valueOption.setEnabled(true);
                scriptOption.setEnabled(true);
                expressionText.setFocus();
                validate();
                if (scriptOption.getSelection() == false && valueOption.getSelection() == false) {
                    valueOption.setSelection(true);
                    valueOption.notifyListeners(SWT.Selection, null);
                }
            }
        });
        expressionText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(final ModifyEvent event) {
                String expr = expressionText.getText().trim();
                for (int ndx = expr.indexOf("${"); ndx >= 0; ndx = expr.indexOf("${", ndx)) {
                    if (ndx == 0 || expr.charAt(ndx - 1) != '\\') {
                        expr = expr.substring(0, ndx) + '\\' + expr.substring(ndx);
                        ndx += 3;
                    } else if (expr.charAt(ndx - 1) == '\\') {
                        // ignore it and move on
                        ndx +=3;
                    }
                }
                expression = expr;
                validate();
            }
        });

        languageComboViewer.setInput(languages);
        if (language != null) {
            languageComboViewer.setSelection(new StructuredSelection(language));
        }

        valueOption.setSelection(false);
        valueOption.setEnabled(false);
        scriptOption.setSelection(false);
        scriptOption.setEnabled(false);

        if (expression != null) {
            String part0 = getParameterPart(expression, 0);
            valueOption.setSelection(true);
            valueOption.setEnabled(true);
            scriptOption.setSelection(false);
            scriptOption.setEnabled(true);
            if (part0.contentEquals("resource")) {
                valueOption.setSelection(false);
                scriptOption.setSelection(true);
                String part1 = getParameterPart(expression, 1);
                scriptTypeComboViewer.setSelection(new StructuredSelection(part1));
                String part2 = getParameterPart(expression, 2);
                pathText.setText(part2.replace("\\${", "${"));
            }
        }
    }

    private static String convertToFileURL ( String filename ) {
        String path = new File(filename).toURI().toString();
        if ( File.separatorChar != '/' ) {
            path = path.replace ( File.separatorChar, '/' );
        }
        if (path.startsWith("file:")) {
            path = path.replace("file:", "");
        }
        if (path.indexOf(':') > -1) {
            path = path.replace(":", "");
        }
        if ( !path.startsWith ( "/" ) ) {
            path = "/" + path;
        }
        return path;
    }

    private void updateExpression() {
        if (expression != null) {
            if (scriptOption.getSelection()) {
                final IStructuredSelection selection =
                        (IStructuredSelection)scriptTypeComboViewer.getSelection();
                String value = (String) selection.getFirstElement();
                expression = "resource:" + value + ":" + pathText.getText().trim();
                expressionText.setText(expression.replace("\\${", "${"));
            } else {
                expressionText.setText(expression.replace("\\${", "${"));
            }
        }
    }

    @Override
    public void create() {
        super.create();
        validate();
    }

    @Override
    protected String message() {
        return "Select the expression language, then specify details for the expression.";
    }

    @Override
    protected String title() {
        return "Set Expression";
    }

    private String getParameterPart(String expression, int idx) {
        String part = null;
        String[] parts = expression.split(":", 3);
        if (parts.length > idx) {
            part = parts[idx];
        }
        return part;
    }


    void validate() {
        if (getButton(IDialogConstants.OK_ID) != null) {
            getButton(IDialogConstants.OK_ID).setEnabled(language != null
                                                     && expression != null
                                                     && !expression.isEmpty());
        }
        if (expressionText != null && !expressionText.isDisposed()) {
            browseBtn.setEnabled(false);
            expressionText.setEnabled(valueOption.getSelection());
            pathText.setEnabled(scriptOption.getSelection());
            scriptTypeComboViewer.getCombo().setEnabled(scriptOption.getSelection());
            final IStructuredSelection selection =
                    (IStructuredSelection)scriptTypeComboViewer.getSelection();
            String value = (String) selection.getFirstElement();
            browseBtn.setEnabled(value != null
                    && (value.equalsIgnoreCase("classpath") || value.equalsIgnoreCase("file"))
                    && scriptOption.getSelection());
        }
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    private String selectResourceFromWorkspace(Shell shell, final String extension) {
        IJavaProject javaProject = null;
        if (project != null) {
            javaProject = JavaCore.create(project);
        }
        ClasspathResourceSelectionDialog dialog = null;
        if (javaProject == null) {
            dialog = new ClasspathResourceSelectionDialog(shell, ResourcesPlugin.getWorkspace().getRoot(), extension);
        } else {
            dialog = new ClasspathResourceSelectionDialog(shell, javaProject.getProject(), extension);
        }
        dialog.setTitle("Select " + extension.toUpperCase() + " From Project");
        dialog.setInitialPattern("*." + extension); //$NON-NLS-1$
        dialog.open();
        Object[] result = dialog.getResult();
        if (result == null || result.length == 0 || !(result[0] instanceof IResource)) {
            return null;
        }
        return ((IResource) result[0]).getProjectRelativePath().toPortableString();
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText("Set Expression");
     }

}
