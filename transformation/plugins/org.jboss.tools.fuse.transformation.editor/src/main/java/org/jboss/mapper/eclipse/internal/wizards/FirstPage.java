/******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. and others. 
 * All rights reserved. This program and the accompanying materials are 
 * made available under the terms of the Eclipse Public License v1.0 which 
 * accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: JBoss by Red Hat - Initial implementation.
 *****************************************************************************/
package org.jboss.mapper.eclipse.internal.wizards;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.StringCharacterIterator;
import java.util.Arrays;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.ObservablesManager;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.property.Properties;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationUpdater;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ViewerProperties;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.jboss.mapper.camel.CamelConfigBuilder;
import org.jboss.mapper.eclipse.Activator;
import org.jboss.mapper.eclipse.internal.util.JavaUtil;
import org.jboss.mapper.eclipse.internal.util.Util;

/**
 *
 */
public class FirstPage extends WizardPage {

    final DataBindingContext context = new DataBindingContext(
            SWTObservables.getRealm(Display.getCurrent()));
    final ObservablesManager observablesManager = new ObservablesManager();
    final Model model;

    /**
     * @param model
     */
    public FirstPage(final Model model) {
        super("New Transformation", "New Transformation", 
                Activator.imageDescriptor("transform.png"));
        this.model = model;
        observablesManager.addObservablesFromContext(context, true, true);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createControl(final Composite parent) {
        setDescription("Supply the ID, project, and path for the the new transformation.\n"
                + "Optionally, supply the source and target files for the transformation.");

        observablesManager.runAndCollect(new Runnable() {

            @Override
            public void run() {
                createPage(parent);
            }
        });

        WizardPageSupport.create(this, context);
        setErrorMessage(null);
    }

    void createFileControls(final Group group,
            final Label pathLabel,
            final String schemaType,
            final Text pathText,
            final Button pathButton,
            final Label typeLabel,
            final ComboViewer typeComboViewer) {
        group.setLayoutData(GridDataFactory.swtDefaults()
                .grab(true, false)
                .span(3, 1)
                .align(SWT.FILL, SWT.CENTER)
                .create());
        group.setLayout(GridLayoutFactory.swtDefaults().spacing(0, 5).numColumns(3).create());
        group.setText(schemaType + " File");
        pathLabel.setText("File path:");
        pathText.setLayoutData(GridDataFactory.swtDefaults().grab(true, false)
                .align(SWT.FILL, SWT.CENTER).create());
        pathButton.setText("...");
        typeLabel.setText("Type:");
        typeComboViewer.getCombo().setLayoutData(
                GridDataFactory.swtDefaults().span(2, 1).grab(true, false).create());
        pathButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
                final String path = Util.selectFile(getShell(), model.getProject(), schemaType);
                if (path != null) {
                    pathText.setText(path);
                    if (typeComboViewer.getSelection().isEmpty()) {
                        final String ext = path.substring(path.lastIndexOf('.') + 1).toLowerCase();
                        switch (ext) {
                            case "class":
                                typeComboViewer.setSelection(new StructuredSelection(
                                        ModelType.CLASS));
                                break;
                            case "java":
                                typeComboViewer
                                        .setSelection(new StructuredSelection(ModelType.JAVA));
                                break;
                            case "json":
                                try (InputStream stream =
                                        model.getProject().getFile(path).getContents()) {
                                    char quote = '\0';
                                    final StringBuilder builder = new StringBuilder();
                                    ModelType type = ModelType.JSON;
                                    for (char chr = (char) stream.read(); chr != -1; chr =
                                            (char) stream.read()) {
                                        // Find quote
                                        if (quote == '\0') {
                                            if (chr == '"' || chr == '\'') {
                                                quote = chr;
                                            }
                                        } else if (chr == quote) {
                                            final String keyword = builder.toString();
                                            switch (keyword) {
                                                case "$schema":
                                                case "title":
                                                case "type":
                                                case "id":
                                                    type = ModelType.JSON_SCHEMA;
                                                    break;
                                                default:
                                                    // nothing
                                            }
                                            break;
                                        } else {
                                            builder.append(chr);
                                        }
                                    }
                                    typeComboViewer.setSelection(new StructuredSelection(type));
                                } catch (IOException | CoreException e) {
                                    Activator.error(e);
                                    typeComboViewer.setSelection(new StructuredSelection(
                                            ModelType.JSON));
                                }
                                break;
                            case "xml":
                                typeComboViewer
                                        .setSelection(new StructuredSelection(ModelType.XML));
                                break;
                            case "xsd":
                                typeComboViewer
                                        .setSelection(new StructuredSelection(ModelType.XSD));
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        });
    }

    void createPage(final Composite parent) {

        final Composite page = new Composite(parent, SWT.NONE);
        setControl(page);
        page.setLayout(GridLayoutFactory.swtDefaults().spacing(0, 5).numColumns(3).create());

        // Create project widgets
        Label label = new Label(page, SWT.NONE);
        label.setText("Project:");
        label.setToolTipText("The project that will contain the mapping file.");
        final ComboViewer projectViewer = new ComboViewer(new Combo(page, SWT.READ_ONLY));
        projectViewer.getCombo().setLayoutData(GridDataFactory.swtDefaults()
                .grab(true, false)
                .span(2, 1)
                .align(SWT.FILL, SWT.CENTER)
                .create());
        projectViewer.getCombo().setToolTipText(label.getToolTipText());
        projectViewer.setLabelProvider(new LabelProvider() {

            @Override
            public String getText(final Object element) {
                return ((IProject) element).getName();
            }
        });

        // Create ID widgets
        label = new Label(page, SWT.NONE);
        label.setText("ID:");
        label.setToolTipText("The transformation ID that will be shown in the Fuse editor");
        final Text idText = new Text(page, SWT.BORDER);
        idText.setLayoutData(GridDataFactory.swtDefaults().span(2, 1).grab(true, false)
                .align(SWT.FILL, SWT.CENTER).create());
        idText.setToolTipText(label.getToolTipText());

        // Create file path widgets
        label = new Label(page, SWT.NONE);
        label.setText("Dozer File path: ");
        label.setToolTipText("The path to the Dozer transformation file.");
        final Text pathText = new Text(page, SWT.BORDER);
        pathText.setLayoutData(GridDataFactory.swtDefaults().span(2, 1).grab(true, false)
                .align(SWT.FILL, SWT.CENTER).create());
        pathText.setToolTipText(label.getToolTipText());

        // Create camel file path widgets
        label = new Label(page, SWT.NONE);
        label.setText("Camel File path: ");
        label.setToolTipText("Path to the Camel configuration file.");
        final Text camelFilePathText = new Text(page, SWT.BORDER);
        camelFilePathText.setLayoutData(GridDataFactory.swtDefaults().span(1, 1).grab(true, false)
                .align(SWT.FILL, SWT.CENTER).create());
        camelFilePathText.setToolTipText(label.getToolTipText());

        final Button camelPathButton = new Button(page, SWT.NONE);
        camelPathButton.setText("...");
        camelPathButton.setToolTipText("Browse to select an available Camel file.");
        camelPathButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
                final IResource res =
                        Util.selectResourceFromWorkspace(getShell(), ".xml", model.getProject());
                if (res != null) {
                    final IPath respath = JavaUtil.getJavaPathForResource(res);
                    final String path = respath.makeRelative().toString();
                    model.setCamelFilePath(path);
                    camelFilePathText.setText(path);
                    camelFilePathText.notifyListeners(SWT.Modify, new Event());
                }
            }
        });

        // Create source widgets
        Group group = new Group(page, SWT.SHADOW_ETCHED_IN);
        Label fileLabel = new Label(group, SWT.NONE);
        final Text sourcePathText = new Text(group, SWT.BORDER);
        final Button sourcePathButton = new Button(group, SWT.NONE);
        Label typeLabel = new Label(group, SWT.NONE);
        final ComboViewer sourceTypeViewer = new ComboViewer(new Combo(group, SWT.READ_ONLY));
        createFileControls(group, fileLabel, "Source", sourcePathText, sourcePathButton, typeLabel,
                sourceTypeViewer);

        // Create target widgets
        group = new Group(page, SWT.SHADOW_ETCHED_IN);
        fileLabel = new Label(group, SWT.NONE);
        final Text targetPathText = new Text(group, SWT.BORDER);
        final Button targetPathButton = new Button(group, SWT.NONE);
        typeLabel = new Label(group, SWT.NONE);
        final ComboViewer targetTypeViewer = new ComboViewer(new Combo(group, SWT.READ_ONLY));
        createFileControls(group, fileLabel, "Target", targetPathText, targetPathButton, typeLabel,
                targetTypeViewer);

        // Bind project widget to UI model
        projectViewer.setContentProvider(new ObservableListContentProvider());
        IObservableValue widgetValue = ViewerProperties.singleSelection().observe(projectViewer);
        IObservableValue modelValue = BeanProperties.value(Model.class, "project").observe(model);
        UpdateValueStrategy strategy = new UpdateValueStrategy();
        strategy.setBeforeSetValidator(new IValidator() {

            @Override
            public IStatus validate(final Object value) {
                if (value == null) {
                    sourcePathButton.setEnabled(false);
                    targetPathButton.setEnabled(false);
                    return ValidationStatus.error("A project must be selected");
                }
                sourcePathButton.setEnabled(true);
                targetPathButton.setEnabled(true);
                return ValidationStatus.ok();
            }
        });
        ControlDecorationSupport.create(context.bindValue(widgetValue, modelValue, strategy, null),
                SWT.LEFT);
        projectViewer.setInput(Properties.selfList(IProject.class).observe(model.projects));

        // Bind transformation ID widget to UI model
        widgetValue = WidgetProperties.text(SWT.Modify).observe(idText);
        modelValue = BeanProperties.value(Model.class, "id").observe(model);
        strategy = new UpdateValueStrategy();
        strategy.setBeforeSetValidator(new IValidator() {

            @Override
            public IStatus validate(final Object value) {
                if (value == null || value.toString().trim().isEmpty()) {
                    return ValidationStatus.error("A transformation ID must be supplied");
                }
                final String id = value.toString().trim();
                final StringCharacterIterator iter = new StringCharacterIterator(id);
                for (char chr = iter.first(); chr != StringCharacterIterator.DONE; chr =
                        iter.next()) {
                    if (!Character.isJavaIdentifierPart(chr)) {
                        return ValidationStatus.error(
                                "The transformation ID may only contain letters, "
                                        + "digits, currency symbols, or underscores");
                    }
                }
                if (model.camelConfigBuilder != null) {
                    for (final String endpointId : model.camelConfigBuilder
                            .getTransformEndpointIds()) {
                        if (id.equalsIgnoreCase(endpointId)) {
                            return ValidationStatus
                                    .error("A transformation with the supplied ID already exists");
                        }
                    }
                }
                return ValidationStatus.ok();
            }
        });
        ControlDecorationSupport.create(context.bindValue(widgetValue, modelValue, strategy, null),
                SWT.LEFT);

        // Bind file path widget to UI model
        widgetValue = WidgetProperties.text(SWT.Modify).observe(pathText);
        modelValue = BeanProperties.value(Model.class, "filePath").observe(model);
        strategy = new UpdateValueStrategy();
        strategy.setBeforeSetValidator(new IValidator() {

            @Override
            public IStatus validate(final Object value) {
                if (value == null || value.toString().trim().isEmpty()) {
                    return ValidationStatus.error("The transformation file path must be supplied");
                }
                if (!(value.toString().trim().isEmpty())) {
                    final IFile file =
                            model.getProject().getFile(Util.RESOURCES_PATH + (String) value);
                    if (file.exists()) {
                        return ValidationStatus
                                .warning("A transformation file with that name already exists.");
                    }
                }
                return ValidationStatus.ok();
            }
        });
        ControlDecorationSupport.create(context.bindValue(widgetValue, modelValue, strategy, null),
                SWT.LEFT);

        // Bind camel file path widget to UI model
        widgetValue = WidgetProperties.text(SWT.Modify).observe(camelFilePathText);
        modelValue = BeanProperties.value(Model.class, "camelFilePath").observe(model);
        strategy = new UpdateValueStrategy();
        strategy.setBeforeSetValidator(new IValidator() {

            @Override
            public IStatus validate(final Object value) {
                if (value == null || value.toString().trim().isEmpty()) {
                    return ValidationStatus.error("The Camel file path must be supplied");
                }
                if (!(value.toString().trim().isEmpty())) {
                    File testFile = null;
                    final String path = (String) value;
                    testFile = new File(model.getProject().getFile(path).getLocationURI());
                    if (!testFile.exists()) {
                        testFile =
                                new File(model.getProject().getFile(Util.RESOURCES_PATH + path)
                                        .getLocationURI());
                        if (!testFile.exists()) {
                            return ValidationStatus
                                    .error("The Camel file path must be a valid file location");
                        }
                    }
                    try {
                        CamelConfigBuilder.loadConfig(testFile);
                    } catch (final Exception e) {
                        return ValidationStatus.error(
                                "The Camel file path must refer to a valid Camel file");
                    }
                }
                return ValidationStatus.ok();
            }
        });
        ControlDecorationSupport.create(context.bindValue(widgetValue, modelValue, strategy, null),
                SWT.LEFT | SWT.TOP);

        final ControlDecorationUpdater sourceUpdator = new ControlDecorationUpdater();
        final ControlDecorationUpdater targetUpdator = new ControlDecorationUpdater();

        // Bind source file path widget to UI model
        widgetValue = WidgetProperties.text(SWT.Modify).observe(sourcePathText);
        modelValue = BeanProperties.value(Model.class, "sourceFilePath").observe(model);
        strategy = new UpdateValueStrategy();
        strategy.setBeforeSetValidator(new IValidator() {

            @Override
            public IStatus validate(final Object value) {
                final String path = value == null ? null : value.toString().trim();
                if (path == null || path.isEmpty()) {
                    return ValidationStatus
                            .error("A source file path must be supplied for the supplied target file path");
                }
                if (model.getProject().findMember(path) == null) {
                    return ValidationStatus
                            .error("Unable to find a source file with the supplied path");
                }
                return ValidationStatus.ok();
            }
        });
        ControlDecorationSupport.create(context.bindValue(widgetValue, modelValue, strategy, null),
                SWT.LEFT, null, sourceUpdator);

        // Bind target file path widget to UI model
        widgetValue = WidgetProperties.text(SWT.Modify).observe(targetPathText);
        modelValue = BeanProperties.value(Model.class, "targetFilePath").observe(model);
        strategy = new UpdateValueStrategy();
        strategy.setBeforeSetValidator(new IValidator() {

            @Override
            public IStatus validate(final Object value) {
                final String path = value == null ? null : value.toString().trim();
                if (path == null || path.isEmpty()) {
                    return ValidationStatus
                            .error("A target file path must be supplied for the supplied source file path");
                }
                if (model.getProject().findMember(path) == null) {
                    return ValidationStatus
                            .error("Unable to find a target file with the supplied path");
                }
                return ValidationStatus.ok();
            }
        });
        ControlDecorationSupport.create(context.bindValue(widgetValue, modelValue, strategy, null),
                SWT.LEFT, null, targetUpdator);

        // Bind source type widget to UI model
        sourceTypeViewer.setContentProvider(new ObservableListContentProvider());
        widgetValue = ViewerProperties.singleSelection().observe(sourceTypeViewer);
        modelValue = BeanProperties.value(Model.class, "sourceType").observe(model);
        context.bindValue(widgetValue, modelValue);
        sourceTypeViewer.setInput(Properties.selfList(ModelType.class).observe(
                Arrays.asList(ModelType.values())));

        // Bind target type widget to UI model
        targetTypeViewer.setContentProvider(new ObservableListContentProvider());
        widgetValue = ViewerProperties.singleSelection().observe(targetTypeViewer);
        modelValue = BeanProperties.value(Model.class, "targetType").observe(model);
        context.bindValue(widgetValue, modelValue);
        targetTypeViewer.setInput(Properties.selfList(ModelType.class).observe(
                Arrays.asList(ModelType.values())));

        // Set focus to appropriate control
        page.addPaintListener(new PaintListener() {

            @Override
            public void paintControl(final PaintEvent event) {
                if (model.getProject() == null) {
                    projectViewer.getCombo().setFocus();
                } else {
                    idText.setFocus();
                }
                page.removePaintListener(this);
            }
        });

        for (final Object observable : context.getValidationStatusProviders()) {
            ((Binding) observable).getTarget().addChangeListener(new IChangeListener() {

                @Override
                public void handleChange(final ChangeEvent event) {
                    validatePage();
                }
            });
        }

        if (model.getProject() == null) {
            validatePage();
        } else {
            projectViewer.setSelection(new StructuredSelection(model.getProject()));
        }
    }

    void validatePage() {
        setPageComplete(getErrorMessage() == null);
    }
}
