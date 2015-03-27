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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.ObservablesManager;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
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
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.jboss.tools.fuse.transformation.editor.Activator;
import org.jboss.tools.fuse.transformation.editor.internal.util.ClasspathResourceSelectionDialog;

/**
 * @author brianf
 *
 */
public class JSONPage extends XformWizardPage implements TransformationTypePage {

    final DataBindingContext context = new DataBindingContext(
            SWTObservables.getRealm(Display.getCurrent()));
    final ObservablesManager observablesManager = new ObservablesManager();
    private Composite _page;
    private boolean isSource = true;
    private Text _jsonFileText;
    private Button _jsonSchemaOption;
    private Button _jsonInstanceOption;
    private Text _jsonPreviewText;

    /**
     * @param model
     */
    public JSONPage(String pageName, final Model model, boolean isSource) {
        super(pageName, model);
        setTitle("JSON Page");
        setImageDescriptor(Activator.imageDescriptor("transform.png"));
        this.isSource = isSource;
        observablesManager.addObservablesFromContext(context, true, true);
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
        setErrorMessage(null);
    }

    private void createPage(Composite parent) {
        _page = new Composite(parent, SWT.NONE);
        setControl(_page);
        _page.setLayout(GridLayoutFactory.swtDefaults().spacing(0, 5).numColumns(3).create());

        Group group = new Group(_page, SWT.SHADOW_ETCHED_IN);
        group.setText("JSON Type Definition");
        group.setLayout(new GridLayout(1, false));
        group.setLayoutData(
                new GridData(SWT.FILL, SWT.FILL, true, false, 3, 2));

        _jsonSchemaOption = new Button(group, SWT.RADIO);
        _jsonSchemaOption.setText("JSON Schema");
        _jsonSchemaOption.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
        _jsonSchemaOption.setSelection(true);

        _jsonInstanceOption = new Button(group, SWT.RADIO);
        _jsonInstanceOption.setText("JSON Instance Document");
        _jsonInstanceOption.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

        _jsonSchemaOption.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (isSourcePage()) {
                    model.setSourceType(ModelType.JSON_SCHEMA);
                    model.setSourceFilePath("");
                } else {
                    model.setTargetType(ModelType.JSON_SCHEMA);
                    model.setTargetFilePath("");
                }
                _jsonPreviewText.setText("");
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                // empty
            }
        });

        _jsonInstanceOption.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (isSourcePage()) {
                    model.setSourceType(ModelType.JSON);
                    model.setSourceFilePath("");
                } else {
                    model.setTargetType(ModelType.JSON);
                    model.setTargetFilePath("");
                }
                _jsonPreviewText.setText("");
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                // empty
            }
        });

        // Create file path widgets
        Label label;
        if (isSourcePage()) {
            label = createLabel(_page, "Source File:", "The source JSON file for the transformation.");
        } else {
            label = createLabel(_page, "Target File:", "The target JSON file for the transformation.");
        }

        _jsonFileText = new Text(_page, SWT.BORDER | SWT.READ_ONLY);
        _jsonFileText.setLayoutData(
                new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        _jsonFileText.setToolTipText(label.getToolTipText());

        final Button jsonFileBrowseButton = new Button(_page, SWT.NONE);
        jsonFileBrowseButton.setLayoutData(new GridData());
        jsonFileBrowseButton.setText("...");
        jsonFileBrowseButton.setToolTipText("Browse to specify the JSON file.");

        jsonFileBrowseButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                String extension = "json";
                boolean isJSON = true;
                if (_jsonInstanceOption.getSelection()) {
                    isJSON = true;
                } else if (_jsonSchemaOption.getSelection()) {
                    isJSON = false;
                }
                String path = selectResourceFromWorkspace(_page.getShell(), extension);
                if (path != null) {
                    if (isSourcePage()) {
                        if (isJSON) {
                            model.setSourceType(ModelType.JSON);
                        } else {
                            model.setSourceType(ModelType.JSON_SCHEMA);
                        }
                        model.setSourceFilePath(path);
                    } else {
                        if (isJSON) {
                            model.setTargetType(ModelType.JSON);
                        } else {
                            model.setTargetType(ModelType.JSON_SCHEMA);
                        }
                        model.setTargetFilePath(path);
                    }
                    _jsonFileText.setText(path);

                    IPath tempPath = new Path(path);
                    IFile xmlFile = model.getProject().getFile(tempPath);
                    if (xmlFile != null) {
                        try (InputStream istream = xmlFile.getContents()) {
                            StringBuffer buffer = new StringBuffer();
                            try (BufferedReader in = new BufferedReader(new InputStreamReader(istream))) {
                                String inputLine;
                                while ((inputLine = in.readLine()) != null) {
                                    buffer.append(inputLine + "\n");
                                }
                            }
                            _jsonPreviewText.setText(buffer.toString());
                        } catch (CoreException e1) {
                            e1.printStackTrace();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                    _jsonFileText.notifyListeners(SWT.Modify, new Event());
                }
            }
        });

        Group group2 = new Group(_page, SWT.SHADOW_ETCHED_IN);
        group2.setText("JSON Structure Preview");
        group2.setLayout(new FillLayout());
        group2.setLayoutData(
                new GridData(SWT.FILL, SWT.FILL, true, true, 3, 3));

        _jsonPreviewText = new Text(group2, SWT.V_SCROLL | SWT.READ_ONLY );
        _jsonPreviewText.setBackground(_page.getBackground());

        bindControls();
        validatePage();
    }

    @Override
    public boolean isSourcePage() {
        return isSource;
    }

    @Override
    public boolean isTargetPage() {
        return !isSource;
    }

    private String selectResourceFromWorkspace(Shell shell, final String extension) {
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
        dialog.setInitialPattern("*." + extension); //$NON-NLS-1$
        dialog.open();
        Object[] result = dialog.getResult();
        if (result == null || result.length == 0 || !(result[0] instanceof IResource)) {
            return null;
        }
        return ((IResource) result[0]).getProjectRelativePath().toPortableString();
    }

    private void bindControls() {

        // Bind source file path widget to UI model
        IObservableValue widgetValue = WidgetProperties.text(SWT.Modify).observe(_jsonFileText);
        IObservableValue modelValue = null;
        if (isSourcePage()) {
            modelValue = BeanProperties.value(Model.class, "sourceFilePath").observe(model);
        } else {
            modelValue = BeanProperties.value(Model.class, "targetFilePath").observe(model);
        }
        UpdateValueStrategy strategy = new UpdateValueStrategy();
        strategy.setBeforeSetValidator(new IValidator() {

            @Override
            public IStatus validate(final Object value) {
                final String path = value == null ? null : value.toString().trim();
                if (path == null || path.isEmpty()) {
                    return ValidationStatus
                            .error("A source file path must be supplied for the transformation.");
                }
                if (model.getProject().findMember(path) == null) {
                    return ValidationStatus
                            .error("Unable to find a file with the supplied path");
                }
                return ValidationStatus.ok();
            }
        });
        ControlDecorationSupport.create(context.bindValue(widgetValue, modelValue, strategy, null),
                SWT.TOP | SWT.LEFT);
    }
}
