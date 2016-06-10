/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.transformation.editor.internal.wizards;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.jboss.tools.fuse.transformation.editor.Activator;
import org.jboss.tools.fuse.transformation.editor.internal.util.ClasspathResourceSelectionDialog;
import org.jboss.tools.fuse.transformation.editor.internal.util.CompoundValidator;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util;

/**
 *
 */
public class Hl7Page extends XformWizardPage implements TransformationTypePage {

    private Composite _page;
    private boolean isSource = true;
    private Text hl7FileText;
    private Button hl7SchemaOption;
    private Button hl7InstanceOption;
    private Text hl7PreviewText;
    private Binding _binding;

    /**
     * @param pageName
     * @param model
     * @param isSource
     */
    public Hl7Page(final String pageName, final Model model, final boolean isSource) {
        super(pageName, model);
        setTitle("HL7 Page");
        setImageDescriptor(Activator.imageDescriptor("transform.png"));
        this.isSource = isSource;
        observablesManager.addObservablesFromContext(context, true, true);
    }

    private void bindControls() {

        // Bind source file path widget to UI model
        final IObservableValue widgetValue = WidgetProperties.text(SWT.Modify).observe(hl7FileText);
        IObservableValue modelValue = null;
        if (isSourcePage()) {
            modelValue = BeanProperties.value(Model.class, "sourceFilePath").observe(model);
        } else {
            modelValue = BeanProperties.value(Model.class, "targetFilePath").observe(model);
        }
        final UpdateValueStrategy strategy = new UpdateValueStrategy();

        PathValidator pathValidator = new PathValidator();
        FileEmptyValidator fileEmptyValidator = new FileEmptyValidator();
        JSONValidator jsonValidator = new JSONValidator();
        CompoundValidator compoundJSONTextValidator = new CompoundValidator(
                pathValidator, fileEmptyValidator, jsonValidator);
        strategy.setBeforeSetValidator(compoundJSONTextValidator);

        _binding = context.bindValue(widgetValue, modelValue, strategy, null);
        ControlDecorationSupport.create(_binding, decoratorPosition, hl7FileText.getParent());

        widgetValue.addValueChangeListener(new IValueChangeListener() {

			@Override
			public void handleValueChange(ValueChangeEvent event) {
				if (!Hl7Page.this.isCurrentPage()) {
					return;
				}
                Object value = event.diff.getNewValue();
                String path = null;
                if (value != null && !value.toString().trim().isEmpty()) {
                    path = value.toString().trim();
                }
                if (path != null) {
                    try {
                        IResource resource = model.getProject().findMember(path);
                        if (resource == null || !resource.exists() || !(resource instanceof IFile)) {
                            return;
                        }
                        if (fileIsEmpty(path)) {
                            return;
                        }
                        String jsonText = getJsonText((IFile) resource);
                        if (!Util.jsonValid(jsonText)) {
                            return;
                        }
                        IPath filePath = resource.getLocation();
                        String fullpath = filePath.makeAbsolute().toPortableString();
                        updateSettingsBasedOnFilePath(fullpath);
                        if (isSourcePage()) {
                            model.setSourceFilePath(path);
                        } else {
                            model.setTargetFilePath(path);
                        }
                        updatePreview(resource.getProjectRelativePath().toString());
                        hl7FileText.notifyListeners(SWT.Modify, new Event());
                    } catch (final Exception e) {
                        Activator.error(e);
                    }
                }
			}
        });

        listenForValidationChanges();
    }

    private void updatePreview(String path) {
        IPath tempPath = new Path(path);
        IFile xmlFile = model.getProject().getFile(tempPath);
        if (xmlFile != null && xmlFile.exists()) {
            try (InputStream istream = xmlFile.getContents()) {
                StringBuffer buffer = new StringBuffer();
                try (BufferedReader in = new BufferedReader(new InputStreamReader(istream))) {
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        buffer.append(inputLine + "\n");
                    }
                }
                hl7PreviewText.setText(buffer.toString());

            } catch (CoreException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void updateSettingsBasedOnFilePath(String path) throws Exception {
        final IPath tempPath = new Path(path);
        final IFile xmlFile = model.getProject().getFile(tempPath);
        if (xmlFile == null || !xmlFile.exists()) {
        	return;
        }
        final boolean schema = jsonSchema(path);
        hl7InstanceOption.setSelection(!schema);
        hl7SchemaOption.setSelection(schema);
        if (isSourcePage()) {
            if (schema) {
                model.setSourceType(ModelType.JSON_SCHEMA);
            } else {
                model.setSourceType(ModelType.JSON);
            }
        } else {
            if (schema) {
                model.setTargetType(ModelType.JSON_SCHEMA);
            } else {
                model.setTargetType(ModelType.JSON);
            }
        }
    }


    @Override
    public void createControl(final Composite parent) {
        if (this.isSource) {
            setTitle("Source Type (JSON)");
            setDescription("Specify details for the source JSON for this transformation.");
        } else {
            setTitle("Target Type (JSON)");
            setDescription("Specify details for the target JSON for this transformation.");
        }
        observablesManager.runAndCollect(new Runnable() {

            @Override
            public void run() {
                createPage(parent);
            }
        });

        WizardPageSupport.create(this, context);
        setErrorMessage(null); // clear any error messages at first
        setMessage(null); // now that we're using info messages, we must reset
                          // this too
    }

    private void createPage(final Composite parent) {
        _page = new Composite(parent, SWT.NONE);
        setControl(_page);

        GridLayout layout = new GridLayout(3, false);
        layout.marginRight = 5;
        layout.horizontalSpacing = 10;
        _page.setLayout(layout);

        final Group group = new Group(_page, SWT.SHADOW_ETCHED_IN);
        group.setText("JSON Type Definition");
        group.setLayout(new GridLayout(1, false));
        group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 2));

        hl7SchemaOption = new Button(group, SWT.RADIO);
        hl7SchemaOption.setText("JSON Schema");
        hl7SchemaOption.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
        hl7SchemaOption.setSelection(true);

        hl7InstanceOption = new Button(group, SWT.RADIO);
        hl7InstanceOption.setText("JSON Instance Document");
        hl7InstanceOption.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

        hl7SchemaOption.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
                if (isSourcePage()) {
                    model.setSourceType(ModelType.JSON_SCHEMA);
                } else {
                    model.setTargetType(ModelType.JSON_SCHEMA);
                }
                model.setTargetFilePath("");
                hl7PreviewText.setText("");
                Hl7Page.this.resetFinish();
            }
        });

        hl7InstanceOption.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
                if (isSourcePage()) {
                    model.setSourceType(ModelType.JSON);
                } else {
                    model.setTargetType(ModelType.JSON);
                }
                model.setTargetFilePath("");
                hl7PreviewText.setText("");
                Hl7Page.this.resetFinish();
            }
        });

        // Create file path widgets
        Label label;
        if (isSourcePage()) {
            label = createLabel(_page, "Source File:", "The source JSON file for the transformation.");
        } else {
            label = createLabel(_page, "Target File:", "The target JSON file for the transformation.");
        }

        hl7FileText = new Text(_page, SWT.BORDER);
        hl7FileText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        hl7FileText.setToolTipText(label.getToolTipText());

        final Button jsonFileBrowseButton = new Button(_page, SWT.NONE);
        jsonFileBrowseButton.setLayoutData(new GridData());
        jsonFileBrowseButton.setText("...");
        jsonFileBrowseButton.setToolTipText("Browse to specify the JSON file.");

        jsonFileBrowseButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent event) {
                final String extension = "json";
                final String path = selectResourceFromWorkspace(_page.getShell(), extension);
                if (path != null) {
                    try {
                        hl7PreviewText.setText("");
                        if (fileIsEmpty(path)) {
                            hl7FileText.setText(path);
                            notifyControl(hl7FileText, SWT.Modify);
                            return;
                        }
                        IPath tempPath = new Path(path);
                        IFile xmlFile = model.getProject().getFile(tempPath);
                        String jsonText = getJsonText(xmlFile);
                        if (!Util.jsonValid(jsonText)) {
                            hl7FileText.setText(path);
                            notifyControl(hl7FileText, SWT.Modify);
                            return;
                        }

                        boolean schema = jsonSchema(path);
                        hl7InstanceOption.setSelection(!schema);
                        hl7SchemaOption.setSelection(schema);
                        if (isSourcePage()) {
                            if (schema) {
                                model.setSourceType(ModelType.JSON_SCHEMA);
                            } else {
                                model.setSourceType(ModelType.JSON);
                            }
                            model.setSourceFilePath(path);
                        } else {
                            if (schema) {
                                model.setTargetType(ModelType.JSON_SCHEMA);
                            } else {
                                model.setTargetType(ModelType.JSON);
                            }
                            model.setTargetFilePath(path);
                        }
                        hl7FileText.setText(path);
                        hl7PreviewText.setText(jsonText);
                        notifyControl(hl7FileText, SWT.Modify);
                    } catch (final Exception e) {
                        Activator.error(e);
                    }
                }
            }
        });

        final Group group2 = new Group(_page, SWT.SHADOW_ETCHED_IN);
        group2.setText("JSON Structure Preview");
        group2.setLayout(new FillLayout());
        group2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 3));

        hl7PreviewText = new Text(group2, SWT.V_SCROLL | SWT.READ_ONLY | SWT.H_SCROLL );
        hl7PreviewText.setBackground(_page.getBackground());

        bindControls();
        validatePage();
    }

    private String getJsonText(IFile jsonFile) {
        if (jsonFile != null && jsonFile.exists()) {
            try (final InputStream istream = jsonFile.getContents()) {
                final StringBuffer buffer = new StringBuffer();
                try (final BufferedReader in = new BufferedReader(new InputStreamReader(istream))) {
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        buffer.append(inputLine + "\n");
                    }
                }
                return buffer.toString();
            } catch (final CoreException e1) {
                e1.printStackTrace();
            } catch (final IOException e1) {
                e1.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public boolean isSourcePage() {
        return isSource;
    }

    @Override
    public boolean isTargetPage() {
        return !isSource;
    }

    boolean fileIsEmpty(final String path) {
        try {
            IFile testIFile = model.getProject().getFile(path);
            if (testIFile.exists()) {
                File testFile = testIFile.getRawLocation().makeAbsolute().toFile();
                if (testFile.length() == 0) {
                    return true;
                }
                String jsonText = getJsonText(testIFile);
                if (jsonText != null && jsonText.trim().isEmpty()) {
                    return true;
                }
            } else {
                return true;
            }
        } catch (Exception e) {
            // ignore
        }
        return false;
    }

    boolean jsonSchema(final String path) throws Exception {
        final IPath tempPath = new Path(path);
        final IFile jsonFile = model.getProject().getFile(tempPath);
        if (fileIsEmpty(path)) {
            return false;
        }
        File testFile = jsonFile.getRawLocation().makeAbsolute().toFile();
        try (FileInputStream fileInput = new FileInputStream(testFile)) {
            char quote = '\0';
            final StringBuilder builder = new StringBuilder();
            int r;
            while ((r = fileInput.read()) != -1) {
                char chr = (char) r;
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
                        return true;
                    default:
                        // all other cases ignored
                    }
                    break;
                } else {
                    builder.append(chr);
                }
            }
        }
        return false;
    }

    private String selectResourceFromWorkspace(final Shell shell, final String extension) {
        IJavaProject javaProject = null;
        if (getModel() != null) {
            if (getModel().getProject() != null) {
                javaProject = JavaCore.create(getModel().getProject());
            }
        }
        ClasspathResourceSelectionDialog dialog = null;
        if (javaProject == null) {
            dialog = new ClasspathResourceSelectionDialog(shell, ResourcesPlugin.getWorkspace().getRoot(), extension);
        } else {
            dialog = new ClasspathResourceSelectionDialog(shell, javaProject.getProject(), extension);
        }
        dialog.setTitle("Select " + extension.toUpperCase() + " From Project");
        dialog.setInitialPattern("*." + extension);
        dialog.open();
        final Object[] result = dialog.getResult();
        if (result == null || result.length == 0 || !(result[0] instanceof IResource)) {
            return null;
        }
        return ((IResource) result[0]).getProjectRelativePath().toPortableString();
    }

    @Override
    public void notifyListeners() {
        if (hl7FileText != null && !hl7FileText.isDisposed()) {
            notifyControl(hl7FileText, SWT.Modify);
        }
    }

    @Override
    public void clearControls() {
        if (hl7FileText != null && !hl7FileText.isDisposed()) {
            hl7FileText.setText("");
            hl7PreviewText.setText("");
        }
        notifyListeners();
    }

    @Override
    public void pingBinding() {
        if (_binding != null) {
            _binding.validateTargetToModel();
        }
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            notifyListeners();
        }
    }

    class PathValidator implements IValidator {

        @Override
        public IStatus validate(final Object value) {
            final String path = value == null ? null : value.toString().trim();
            String pathEmptyError = null;
            String unableToFindError = null;
            if (isSourcePage()) {
                pathEmptyError = "A source file path must be supplied for the transformation.";
                unableToFindError = "Unable to find a source file with the supplied path";
            } else {
                pathEmptyError = "A target file path must be supplied for the transformation.";
                unableToFindError = "Unable to find a target file with the supplied path";
            }
            if (path == null || path.isEmpty()) {
                hl7PreviewText.setText("");
                return ValidationStatus.error(pathEmptyError);
            }
            if (model.getProject().findMember(path) == null) {
                hl7PreviewText.setText("");
                return ValidationStatus.error(unableToFindError);
            }
            IResource resource = model.getProject().findMember(path);
            if (resource == null || !resource.exists() || !(resource instanceof IFile)) {
                hl7PreviewText.setText("");
                return ValidationStatus.error(unableToFindError);
            }
            return ValidationStatus.ok();
        }
    }

    class FileEmptyValidator implements IValidator {

        @Override
        public IStatus validate(final Object value) {
            final String path = value == null ? null : value.toString().trim();
            String fileEmptyError = null;
            if (isSourcePage()) {
                fileEmptyError = "Source file selected is empty.";
            } else {
                fileEmptyError = "Target file selected is empty.";
            }
            if (fileIsEmpty(path)) {
                hl7PreviewText.setText("");
                return ValidationStatus.error(fileEmptyError);
            }
            IResource resource = model.getProject().findMember(path);
            if (resource instanceof IFile) {
                String jsonText = getJsonText((IFile) resource);
                if (jsonText == null || jsonText.trim().isEmpty()) {
                    hl7PreviewText.setText("");
                    return ValidationStatus.error(fileEmptyError);
                }
            }
            return ValidationStatus.ok();
        }
    }

    class JSONValidator implements IValidator {

        @Override
        public IStatus validate(final Object value) {
            final String path = value == null ? null : value.toString().trim();
            IResource resource = model.getProject().findMember(path);
            if (resource instanceof IFile) {
                String jsonText = getJsonText((IFile) resource);
                if (!Util.jsonValid(jsonText)) {
                    hl7PreviewText.setText("");
                    return ValidationStatus.error("Invalid JSON");
                }
            }
            return ValidationStatus.ok();
        }
    }
}
