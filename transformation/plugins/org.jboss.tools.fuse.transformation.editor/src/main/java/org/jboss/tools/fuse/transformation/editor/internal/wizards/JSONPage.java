/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
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
import org.eclipse.core.runtime.Status;
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
import org.fusesource.ide.camel.editor.utils.CamelUtils;
import org.fusesource.ide.foundation.core.util.CompoundValidator;
import org.jboss.tools.fuse.transformation.editor.Activator;
import org.jboss.tools.fuse.transformation.editor.internal.l10n.Messages;
import org.jboss.tools.fuse.transformation.editor.internal.util.ClasspathResourceSelectionDialog;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util;

/**
 * @author brianf
 *
 */
public class JSONPage extends XformWizardPage implements TransformationTypePage {

    private Composite _page;
    private boolean isSource = true;
    private Text _jsonFileText;
    private Button _jsonSchemaOption;
    private Button _jsonInstanceOption;
    private Text _jsonPreviewText;
    private Binding _binding;

    /**
     * @param pageName
     * @param model
     * @param isSource
     */
    public JSONPage(final String pageName, final Model model, final boolean isSource) {
        super(pageName, model);
        setTitle(Messages.JSONPage_title);
        setImageDescriptor(Activator.imageDescriptor("transform.png")); //$NON-NLS-1$
        this.isSource = isSource;
        observablesManager.addObservablesFromContext(context, true, true);
    }

    private void bindControls() {

        // Bind source file path widget to UI model
        final IObservableValue widgetValue = WidgetProperties.text(SWT.Modify).observe(_jsonFileText);
        IObservableValue modelValue;
        if (isSourcePage()) {
            modelValue = BeanProperties.value(Model.class, "sourceFilePath").observe(model); //$NON-NLS-1$
        } else {
            modelValue = BeanProperties.value(Model.class, "targetFilePath").observe(model); //$NON-NLS-1$
        }
        final UpdateValueStrategy strategy = new UpdateValueStrategy();

        PathValidator pathValidator = new PathValidator();
        FileEmptyValidator fileEmptyValidator = new FileEmptyValidator();
        JSONValidator jsonValidator = new JSONValidator();
        CompoundValidator compoundJSONTextValidator = new CompoundValidator(
                pathValidator, fileEmptyValidator, jsonValidator);
        strategy.setBeforeSetValidator(compoundJSONTextValidator);

        _binding = context.bindValue(widgetValue, modelValue, strategy, null);
        ControlDecorationSupport.create(_binding, decoratorPosition, _jsonFileText.getParent());

        widgetValue.addValueChangeListener(new IValueChangeListener() {

			@Override
			public void handleValueChange(ValueChangeEvent event) {
				if (!JSONPage.this.isCurrentPage()) {
					return;
				}
                Object value = event.diff.getNewValue();
                String path = null;
                if (value != null && !value.toString().trim().isEmpty()) {
                    path = value.toString().trim();
                }
                if (path != null) {
                    try {
                        IResource resource = CamelUtils.project().findMember(path);
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
                        _jsonFileText.notifyListeners(SWT.Modify, new Event());
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
        IFile xmlFile = CamelUtils.project().getFile(tempPath);
        if (xmlFile != null && xmlFile.exists()) {
            try (InputStream istream = xmlFile.getContents()) {
            	StringBuilder buffer = new StringBuilder();
                try (BufferedReader in = new BufferedReader(new InputStreamReader(istream))) {
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        buffer.append(inputLine + "\n"); //$NON-NLS-1$
                    }
                }
                _jsonPreviewText.setText(buffer.toString());

            } catch (CoreException | IOException e1) {
            	Activator.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Error while trying to update preview", e1)); //$NON-NLS-1$
            }
        }
    }

    private void updateSettingsBasedOnFilePath(String path) throws Exception {
        final IPath tempPath = new Path(path);
        final IFile xmlFile = CamelUtils.project().getFile(tempPath);
        if (xmlFile == null || !xmlFile.exists()) {
        	return;
        }
        final boolean schema = jsonSchema(path);
        _jsonInstanceOption.setSelection(!schema);
        _jsonSchemaOption.setSelection(schema);
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
            setTitle(Messages.JSONPage_sourceTypeTitle);
            setDescription(Messages.JSONPage_sourceTypeDescription);
        } else {
            setTitle(Messages.JSONPage_targetTypeTitle);
            setDescription(Messages.JSONPage_targetTypeDescription);
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
        group.setText(Messages.JSONPage_groupTitleJsonTypeDefinition);
        group.setLayout(new GridLayout(1, false));
        group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 2));

        _jsonSchemaOption = new Button(group, SWT.RADIO);
        _jsonSchemaOption.setText(Messages.JSONPage_labelJSONSchema);
        _jsonSchemaOption.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
        _jsonSchemaOption.setSelection(true);

        _jsonInstanceOption = new Button(group, SWT.RADIO);
        _jsonInstanceOption.setText(Messages.JSONPage_labelJsonDocument);
        _jsonInstanceOption.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

        _jsonSchemaOption.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
                if (isSourcePage()) {
                    model.setSourceType(ModelType.JSON_SCHEMA);
                } else {
                    model.setTargetType(ModelType.JSON_SCHEMA);
                }
                model.setTargetFilePath(""); //$NON-NLS-1$
                _jsonPreviewText.setText(""); //$NON-NLS-1$
                JSONPage.this.resetFinish();
            }
        });

        _jsonInstanceOption.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
                if (isSourcePage()) {
                    model.setSourceType(ModelType.JSON);
                } else {
                    model.setTargetType(ModelType.JSON);
                }
                model.setTargetFilePath(""); //$NON-NLS-1$
                _jsonPreviewText.setText(""); //$NON-NLS-1$
                JSONPage.this.resetFinish();
            }
        });

        // Create file path widgets
        Label label;
        if (isSourcePage()) {
            label = createLabel(_page, Messages.JSONPage_labelSourceFile, Messages.JSONPage_labelSourceFileTooltip);
        } else {
            label = createLabel(_page, Messages.JSONPage_labeltargetFile, Messages.JSONPage_labeltargetFileTooltip);
        }

        _jsonFileText = new Text(_page, SWT.BORDER);
        _jsonFileText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        _jsonFileText.setToolTipText(label.getToolTipText());

        final Button jsonFileBrowseButton = new Button(_page, SWT.NONE);
        jsonFileBrowseButton.setLayoutData(new GridData());
        jsonFileBrowseButton.setText("..."); //$NON-NLS-1$
        jsonFileBrowseButton.setToolTipText(Messages.JSONPage_tooltipButtonBrowse);

        jsonFileBrowseButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent event) {
                final String extension = "json"; //$NON-NLS-1$
                final String path = selectResourceFromWorkspace(_page.getShell(), extension);
                if (path != null) {
                    try {
                        _jsonPreviewText.setText(""); //$NON-NLS-1$
                        if (fileIsEmpty(path)) {
                            _jsonFileText.setText(path);
                            notifyControl(_jsonFileText, SWT.Modify);
                            return;
                        }
                        IPath tempPath = new Path(path);
                        IFile xmlFile = CamelUtils.project().getFile(tempPath);
                        String jsonText = getJsonText(xmlFile);
                        if (!Util.jsonValid(jsonText)) {
                            _jsonFileText.setText(path);
                            notifyControl(_jsonFileText, SWT.Modify);
                            return;
                        }

                        boolean schema = jsonSchema(path);
                        _jsonInstanceOption.setSelection(!schema);
                        _jsonSchemaOption.setSelection(schema);
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
                        _jsonFileText.setText(path);
                        _jsonPreviewText.setText(jsonText);
                        notifyControl(_jsonFileText, SWT.Modify);
                    } catch (final Exception e) {
                        Activator.error(e);
                    }
                }
            }
        });

        final Group group2 = new Group(_page, SWT.SHADOW_ETCHED_IN);
        group2.setText(Messages.JSONPage_groupTitleStructurePreview);
        group2.setLayout(new FillLayout());
        group2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 3));

        _jsonPreviewText = new Text(group2, SWT.V_SCROLL | SWT.READ_ONLY | SWT.H_SCROLL );
        _jsonPreviewText.setBackground(_page.getBackground());

        bindControls();
        validatePage();
    }

    private String getJsonText(IFile jsonFile) {
        if (jsonFile != null && jsonFile.exists()) {
            try (final InputStream istream = jsonFile.getContents()) {
                final StringBuilder buffer = new StringBuilder();
                try (final BufferedReader in = new BufferedReader(new InputStreamReader(istream))) {
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        buffer.append(inputLine + "\n"); //$NON-NLS-1$
                    }
                }
                return buffer.toString();
            } catch (final CoreException | IOException e1) {
            	Activator.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Error while retrieving json text", e1)); //$NON-NLS-1$
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
            IFile testIFile = CamelUtils.project().getFile(path);
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
        final IFile jsonFile = CamelUtils.project().getFile(tempPath);
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
                    case "$schema": //$NON-NLS-1$
                    case "title": //$NON-NLS-1$
                    case "type": //$NON-NLS-1$
                    case "id": //$NON-NLS-1$
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
            if (CamelUtils.project() != null) {
                javaProject = JavaCore.create(CamelUtils.project());
            }
        }
        ClasspathResourceSelectionDialog dialog;
        if (javaProject == null) {
            dialog = new ClasspathResourceSelectionDialog(shell, ResourcesPlugin.getWorkspace().getRoot(), extension);
        } else {
            dialog = new ClasspathResourceSelectionDialog(shell, javaProject.getProject(), extension);
        }
		dialog.setTitle(Messages.bind(Messages.JSONPage_dialogTitleSelectFormProject, extension.toUpperCase()));
        dialog.setInitialPattern("*." + extension); //$NON-NLS-1$
        dialog.open();
        final Object[] result = dialog.getResult();
        if (result == null || result.length == 0 || !(result[0] instanceof IResource)) {
            return null;
        }
        return ((IResource) result[0]).getProjectRelativePath().toPortableString();
    }

    @Override
    public void notifyListeners() {
        if (_jsonFileText != null && !_jsonFileText.isDisposed()) {
            notifyControl(_jsonFileText, SWT.Modify);
        }
    }

    @Override
    public void clearControls() {
        if (_jsonFileText != null && !_jsonFileText.isDisposed()) {
            _jsonFileText.setText(""); //$NON-NLS-1$
            _jsonPreviewText.setText(""); //$NON-NLS-1$
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
            String pathEmptyError;
            String unableToFindError;
            if (isSourcePage()) {
                pathEmptyError = Messages.JSONPage_errorMessageEmptySourceFilePath;
                unableToFindError = Messages.JSONPage_errorMessageInvalidSourcePath;
            } else {
                pathEmptyError = Messages.JSONPage_errorMessageEmptyTargetFilepath;
                unableToFindError = Messages.JSONPage_errorMessageInvalidTargetFilePath;
            }
            if (path == null || path.isEmpty()) {
                _jsonPreviewText.setText(""); //$NON-NLS-1$
                return ValidationStatus.error(pathEmptyError);
            }
            if (CamelUtils.project().findMember(path) == null) {
                _jsonPreviewText.setText(""); //$NON-NLS-1$
                return ValidationStatus.error(unableToFindError);
            }
            IResource resource = CamelUtils.project().findMember(path);
            if (resource == null || !resource.exists() || !(resource instanceof IFile)) {
                _jsonPreviewText.setText(""); //$NON-NLS-1$
                return ValidationStatus.error(unableToFindError);
            }
            return ValidationStatus.ok();
        }
    }

    class FileEmptyValidator implements IValidator {

        @Override
        public IStatus validate(final Object value) {
            final String path = value == null ? null : value.toString().trim();
            String fileEmptyError;
            if (isSourcePage()) {
                fileEmptyError = Messages.JSONPage_errorMessageSourceFileEmpty;
            } else {
                fileEmptyError = Messages.JSONPage_errorMessageTargetFileEmpty;
            }
            if (fileIsEmpty(path)) {
                _jsonPreviewText.setText(""); //$NON-NLS-1$
                return ValidationStatus.error(fileEmptyError);
            }
            IResource resource = CamelUtils.project().findMember(path);
            if (resource instanceof IFile) {
                String jsonText = getJsonText((IFile) resource);
                if (jsonText == null || jsonText.trim().isEmpty()) {
                    _jsonPreviewText.setText(""); //$NON-NLS-1$
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
            IResource resource = CamelUtils.project().findMember(path);
            if (resource instanceof IFile) {
                String jsonText = getJsonText((IFile) resource);
                if (!Util.jsonValid(jsonText)) {
                    _jsonPreviewText.setText(""); //$NON-NLS-1$
                    return ValidationStatus.error(Messages.JSONPage_errorMessageInvalidJSON);
                }
            }
            return ValidationStatus.ok();
        }
    }
}
