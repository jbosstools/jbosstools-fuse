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

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.progress.UIJob;
import org.jboss.tools.fuse.transformation.editor.Activator;
import org.jboss.tools.fuse.transformation.editor.internal.ModelViewer;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util;
import org.jboss.tools.fuse.transformation.editor.wizards.NewTransformationWizard;
import org.jboss.tools.fuse.transformation.model.ModelBuilder;

/**
 * @author brianf
 *
 */
public class JavaPage extends XformWizardPage implements TransformationTypePage {

    private Composite _page;
    private boolean isSource = true;
    private Text _javaClassText;
    private ModelBuilder _builder;
    private org.jboss.tools.fuse.transformation.model.Model _javaModel = null;
    private SimplerModelViewer _modelViewer;
    private Binding _binding;

    /**
     * @param model
     */
    public JavaPage(String pageName, final Model model, boolean isSource) {
        super(pageName, model);
        setTitle("Java Page");
        setImageDescriptor(Activator.imageDescriptor("transform.png"));
        this.isSource = isSource;
        observablesManager.addObservablesFromContext(context, true, true);
        _builder = new ModelBuilder();
    }

    @Override
    public void createControl(final Composite parent) {
        if (this.isSource) {
            setTitle("Source Type (Java)");
            setDescription("Specify details for the source Java class for this transformation.");
        } else {
            setTitle("Target Type (Java)");
            setDescription("Specify details for the target Java class for this transformation.");
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

    private void createPage(Composite parent) {
        _page = new Composite(parent, SWT.NONE);
        setControl(_page);
        GridLayout layout = new GridLayout(3, false);
        layout.marginRight = 5;
        layout.horizontalSpacing = 10;
        _page.setLayout(layout);

        // Create file path widgets
        Label label;
        if (isSourcePage()) {
            label = createLabel(_page, "Source Class:", "The source Java class for the transformation.");
        } else {
            label = createLabel(_page, "Target Class:", "The target Java class for the transformation.");
        }

        _javaClassText = new Text(_page, SWT.BORDER);
        _javaClassText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        _javaClassText.setToolTipText(label.getToolTipText());

        final Button javaClassBrowseButton = new Button(_page, SWT.NONE);
        javaClassBrowseButton.setLayoutData(new GridData());
        javaClassBrowseButton.setText("...");
        javaClassBrowseButton.setToolTipText("Browse to specify the selected class.");

        javaClassBrowseButton.addSelectionListener(new SelectionAdapter() {

            @SuppressWarnings("static-access")
            @Override
            public void widgetSelected(final SelectionEvent event) {
                final IType selected = Util.selectClass(
                        getShell(), model.getProject(), null,
                        "Select Class",
                        "Matching items");
                if (selected != null) {
                    _javaClassText.setText(selected.getFullyQualifiedName());
                    if (isSourcePage()) {
                        model.setSourceType(ModelType.CLASS);
                        model.setSourceFilePath(selected.getFullyQualifiedName());
                    } else {
                        model.setTargetType(ModelType.CLASS);
                        model.setTargetFilePath(selected.getFullyQualifiedName());
                    }

                    UIJob uiJob = new UIJob("open error") {
                        @Override
                        public IStatus runInUIThread(IProgressMonitor monitor) {
                            NewTransformationWizard wizard = (NewTransformationWizard) getWizard();
                            try {
                                Class<?> tempClass = wizard.getLoader().loadClass(selected.getFullyQualifiedName());
                                _javaModel = _builder.fromJavaClass(tempClass);
                                _modelViewer.setModel(_javaModel);
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                            return Status.OK_STATUS;
                        }
                    };
                    uiJob.setSystem(true);
                    uiJob.schedule();
                    _javaClassText.notifyListeners(SWT.Modify, new Event());
                }
            }
        });

        Group group = new Group(_page, SWT.SHADOW_ETCHED_IN);
        group.setText("Class Structure Preview");
        group.setLayout(new GridLayout(3, false));
        group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 3));

        _modelViewer = new SimplerModelViewer(group, _javaModel);
        _modelViewer.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
        _modelViewer.layout();

        bindControls();
        validatePage();

    }

    private void bindControls() {

        // Bind source file path widget to UI model
        IObservableValue widgetValue = WidgetProperties.text(SWT.Modify).observe(_javaClassText);
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
                    return ValidationStatus.error(pathEmptyError);
                }
                NewTransformationWizard wizard = (NewTransformationWizard) getWizard();
                try {
                    Class<?> tempClass = wizard.getLoader().loadClass(path);
                    if (tempClass == null) {
                        return ValidationStatus.error(unableToFindError);
                    }
                } catch (ClassNotFoundException e) {
                    return ValidationStatus.error(unableToFindError);
                }
                return ValidationStatus.ok();
            }
        });
        _binding = context.bindValue(widgetValue, modelValue, strategy, null);
        ControlDecorationSupport.create(_binding, decoratorPosition, _javaClassText.getParent());

        listenForValidationChanges();
    }

    @Override
    public boolean isSourcePage() {
        return isSource;
    }

    @Override
    public boolean isTargetPage() {
        return !isSource;
    }

    @Override
    public void notifyListeners() {
        if (_javaClassText != null && !_javaClassText.isDisposed()) {
            notifyControl(_javaClassText, SWT.Modify);
        }
    }

    @Override
    public void pingBinding() {
        if (_binding != null) {
            _binding.validateTargetToModel();
        }

    }

    @Override
    public void clearControls() {
        if (_javaClassText != null && !_javaClassText.isDisposed()) {
            _javaModel = null; //new org.jboss.tools.fuse.transformation.model.Model("", "");
            _modelViewer.setModel(_javaModel);
            _javaClassText.setText("");
        }
        notifyListeners();
    }

    /**
     * Hide the search field and mapped fields buttons.
     * @author brianf
     */
    class SimplerModelViewer extends ModelViewer {

        /**
         * Constructor
         * @param config
         * @param parent
         * @param rootModel
         * @param potentialDropTargets
         */
        public SimplerModelViewer(Composite parent,
                                  org.jboss.tools.fuse.transformation.model.Model rootModel) {
            super(null, parent, rootModel, null, null);
        }

        @Override
        protected void setViewOptions() {
            this.showMappedFieldsButton = false;
            this.showSearchField = false;
        }

    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            notifyListeners();
        }
    }
}
