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

import java.io.File;
import java.text.StringCharacterIterator;
import java.util.Arrays;
import java.util.Iterator;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.property.Properties;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ViewerProperties;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.progress.UIJob;
import org.jboss.tools.fuse.transformation.editor.Activator;
import org.jboss.tools.fuse.transformation.editor.internal.l10n.Messages;
import org.jboss.tools.fuse.transformation.editor.internal.util.CamelConfigurationHelper;
import org.jboss.tools.fuse.transformation.editor.internal.util.JavaUtil;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util;
import org.jboss.tools.fuse.transformation.editor.wizards.NewTransformationWizard;

/**
 * @author brianf
 *
 */
public class StartPage extends XformWizardPage {

    private Composite _page;
    private ComboViewer _projectCombo;
    private Text _idText;
    private Text _dozerPathText;
    private Text _camelFilePathText;
    private ComboViewer _sourceCV;
    private ComboViewer _targetCV;
    private Binding _filePathBinding;
    private Binding _camelPathBinding;
    private Binding _endpointIdBinding;
    
    /**
     * @param model
     */
    public StartPage(final Model model) {
		super(Messages.StartPage_title, Messages.StartPage_description, Activator.imageDescriptor("transform.png"), model); //$NON-NLS-1$
        observablesManager.addObservablesFromContext(context, true, true);
    }

    @Override
    public void createControl(final Composite parent) {
		setDescription(Messages.StartPage_descriptionStartPage);
        observablesManager.runAndCollect(new Runnable() {

            @Override
            public void run() {
                createPage(parent);
            }
        });

        WizardPageSupport.create(this, context);
        setErrorMessage(null); // clear any error messages at first
//        setMessage(null); // now that we're using info messages, we must reset
//                          // this too
    }

    private void createPage(Composite parent) {
        _page = new Composite(parent, SWT.NONE);
        setControl(_page);
        GridLayout layout = new GridLayout(3, false);
        layout.marginRight = 5;
        layout.horizontalSpacing = 10;
        _page.setLayout(layout);

        // Create project widgets
        Label label = createLabel(_page, Messages.StartPage_labelProject, Messages.StartPage_labelProjectTooltip);

        _projectCombo = new ComboViewer(new Combo(_page, SWT.READ_ONLY));
        _projectCombo.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        _projectCombo.getCombo().setToolTipText(label.getToolTipText());
        _projectCombo.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(final Object element) {
                return ((IProject) element).getName();
            }
        });

        // Create ID widgets
        label = createLabel(_page, Messages.StartPage_labelTransformationID, Messages.StartPage_labelTransformationIDTooltip);

        _idText = new Text(_page, SWT.BORDER);
        _idText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        _idText.setToolTipText(label.getToolTipText());

        // Create file path widgets
        label = createLabel(_page, Messages.StartPage_labelDozerFilePath, Messages.StartPage_labelDozerFilePathTooltip);

        _dozerPathText = new Text(_page, SWT.BORDER);
        _dozerPathText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        _dozerPathText.setToolTipText(label.getToolTipText());

        final Button dozerPathButton = new Button(_page, SWT.NONE);
        dozerPathButton.setLayoutData(new GridData());
        dozerPathButton.setText("..."); //$NON-NLS-1$
        dozerPathButton.setToolTipText(Messages.StartPage_browseDozerTooltip);

        dozerPathButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
                final IResource res = Util.selectDozerResourceFromWorkspace(getShell(), model.getProject());
                if (res != null) {
                    final IPath respath = JavaUtil.getJavaPathForResource(res);
                    final String path = respath.makeRelative().toString();
                    model.setFilePath(path);
                    _dozerPathText.setText(path);
                    _dozerPathText.notifyListeners(SWT.Modify, new Event());
                }
            }
        });

        // Create camel file path widgets
        label = createLabel(_page, Messages.StartPage_labelCamelFilepath, Messages.StartPage_labelCamelFilePathTooltip);

        _camelFilePathText = new Text(_page, SWT.BORDER);
        _camelFilePathText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        _camelFilePathText.setToolTipText(label.getToolTipText());

        final Button camelPathButton = new Button(_page, SWT.NONE);
        camelPathButton.setText("..."); //$NON-NLS-1$
        camelPathButton.setToolTipText(Messages.StartPage_browseCamelTooltip);

        camelPathButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
                final IResource res = Util.selectCamelResourceFromWorkspace(getShell(), model.getProject());
                if (res != null) {
                    final IPath respath = JavaUtil.getJavaPathForResource(res);
                    final String path = respath.makeRelative().toString();
                    model.setCamelFilePath(path);
                    _camelFilePathText.setText(path);

                    // if the project wasn't previously set...
                    if (model.getProject() == null) {
                        // set it based on the camel file selected
                        model.setProject(res.getProject());
                        _projectCombo.getCombo().notifyListeners(SWT.Modify, new Event());
                    }

                    _camelFilePathText.notifyListeners(SWT.Modify, new Event());
                    _endpointIdBinding.validateTargetToModel();
                }
            }
        });

        label = createLabel(_page, "", ""); // spacer //$NON-NLS-1$ //$NON-NLS-2$
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

        Group group = new Group(_page, SWT.SHADOW_ETCHED_IN);
        group.setText(Messages.StartPage_groupTypesTransformed);
        group.setLayout(new GridLayout(3, false));
        group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 2));

        createLabel(group, Messages.StartPage_labelSourceType, Messages.StartPage_labelSourceTypeTooltip);
        createLabel(group, "", ""); // spacer //$NON-NLS-1$ //$NON-NLS-2$
        createLabel(group, Messages.StartPage_labelTargetType, Messages.StartPage_labelTargetTypeTooltip);

        _sourceCV = new ComboViewer(new Combo(group, SWT.READ_ONLY));
        GridData sourceGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        sourceGD.horizontalIndent = 5;
        _sourceCV.getCombo().setLayoutData(sourceGD);

        new Label(group, SWT.NONE).setImage(Activator.imageDescriptor("mapped16.gif").createImage()); //$NON-NLS-1$
        _targetCV = new ComboViewer(new Combo(group, SWT.READ_ONLY));
        GridData targetGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        targetGD.horizontalIndent = 5;
        _targetCV.getCombo().setLayoutData(targetGD);

        bindControls();
        initialize();
        validatePage();
    }

    private void initialize() {
        IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
        model.projects.clear();
        model.projects.addAll(Arrays.asList(projects));

        for (final Iterator<IProject> iter = model.projects.iterator(); iter.hasNext();) {
            IProject toTest = iter.next();
            if (!toTest.isOpen() || !Util.projectHasCamelResource(toTest)) {
                iter.remove();
            }
        }
        _projectCombo.setInput(Properties.selfList(IProject.class).observe(model.projects));

        if (model.getProject() == null) {
            _projectCombo.getCombo().setFocus();
        } else {
            _idText.setFocus();
        }

        // Set focus to appropriate control
        _page.addPaintListener(new PaintListener() {

            @Override
            public void paintControl(final PaintEvent event) {
                if (model.getProject() == null) {
                    _projectCombo.getCombo().setFocus();
                } else {
                    _idText.setFocus();
                }
                _page.removePaintListener(this);
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

        if (model.getProject() != null) {
            _projectCombo.setSelection(new StructuredSelection(model.getProject()));
        }

        if (model.getCamelFilePath() != null) {
            _camelFilePathText.setText(model.getCamelFilePath());
            _camelFilePathText.notifyListeners(SWT.Modify, new Event());
        }

        if (model.getFilePath() != null) {
            _dozerPathText.setText(model.getFilePath());
            _dozerPathText.notifyListeners(SWT.Modify, new Event());
        }

        if (model.getId() != null) {
            _idText.setText(model.getId());
            _idText.notifyListeners(SWT.Modify, new Event());
        }
    }

    private void bindControls() {

        _projectCombo.setContentProvider(new ObservableListContentProvider());

        IObservableValue projectComboValue = ViewerProperties.singleSelection().observe(_projectCombo);
        IObservableValue projectValue = BeanProperties.value(Model.class, "project").observe(model); //$NON-NLS-1$
        
        IObservableValue dozerPathTextValue = WidgetProperties.text(SWT.Modify).observe(_dozerPathText);
        IObservableValue dozerPathValue = BeanProperties.value(Model.class, "filePath").observe(model); //$NON-NLS-1$
        
        // bind the project dropdown
        UpdateValueStrategy strategy = new UpdateValueStrategy();
        strategy.setBeforeSetValidator(new IValidator() {

            @Override
            public IStatus validate(final Object value) {
                if (value == null) {
                    return ValidationStatus.error(Messages.StartPage_errorMessageProjectMustBeSelected);
                }
                return ValidationStatus.ok();
            }
        });
        
        projectValue.addChangeListener(new IChangeListener() {
            
            @Override
            public void handleChange(ChangeEvent event) {
                _filePathBinding.validateTargetToModel();
            }
        });

        Binding projectBinding = 
                context.bindValue(projectComboValue, projectValue, strategy, null);
        ControlDecorationSupport.create(projectBinding, decoratorPosition, _projectCombo.getControl().getParent());

        // Bind transformation ID widget to UI model
        IObservableValue idTextValue = WidgetProperties.text(SWT.Modify).observe(_idText);
        IObservableValue idValue = BeanProperties.value(Model.class, "id").observe(model); //$NON-NLS-1$

        strategy = new UpdateValueStrategy();
        strategy.setBeforeSetValidator(new IValidator() {

            @Override
            public IStatus validate(final Object value) {
                if (value == null || value.toString().trim().isEmpty()) {
                    return ValidationStatus.error(Messages.StartPage_errorMessageIDMustBeSupplied);
                }
                final String id = value.toString().trim();
                final StringCharacterIterator iter = new StringCharacterIterator(id);
                for (char chr = iter.first(); chr != StringCharacterIterator.DONE; chr = iter.next()) {
                    if (!Character.isJavaIdentifierPart(chr)) {
						return ValidationStatus.error(Messages.StartPage_errorMessageInvalidCharacters);
                    }
                }
                if (model.camelConfig != null && model.camelConfig.getConfigBuilder() != null) {
                    for (final String endpointId : model.camelConfig.getConfigBuilder().getTransformEndpointIds()) {
                        if (id.equalsIgnoreCase(endpointId)) {
                            return ValidationStatus.error(Messages.StartPage_errorMessageIDAlreadyExists);
                        }
                    }
                }
                return ValidationStatus.ok();
            }
        });
        _endpointIdBinding = context.bindValue(idTextValue, idValue, strategy, null);
        ControlDecorationSupport.create(_endpointIdBinding, decoratorPosition, _idText.getParent());

        // Bind file path widget to UI model
        strategy = new UpdateValueStrategy();
        strategy.setBeforeSetValidator(new IValidator() {

            @Override
            public IStatus validate(final Object value) {
                if (value == null || value.toString().trim().isEmpty()) {
                    return ValidationStatus.error(Messages.StartPage_errorMessageFlePathMissing);
                }
                if (!(value.toString().trim().isEmpty())) {
                    if (model.getProject() != null) {
                        final IFile file = model.getProject().getFile(Util.RESOURCES_PATH + (String) value);
                        if (file != null && file.exists()) {
                            return ValidationStatus.warning(Messages.StartPage_errorMessageNameFileAlreadyExists);
                        }
                    }
                }
                return ValidationStatus.ok();
            }
        });
        _filePathBinding = 
                context.bindValue(dozerPathTextValue, dozerPathValue, strategy, null);
        ControlDecorationSupport.create(
                _filePathBinding, decoratorPosition, _dozerPathText.getParent());

        // Bind camel file path widget to UI model
        IObservableValue camelFileTextValue = WidgetProperties.text(SWT.Modify).observe(_camelFilePathText);
        IObservableValue camelFileValue = BeanProperties.value(Model.class, "camelFilePath").observe(model); //$NON-NLS-1$
        strategy = new UpdateValueStrategy();
        strategy.setBeforeSetValidator(new IValidator() {

            @Override
            public IStatus validate(final Object value) {
                if (value == null || value.toString().trim().isEmpty()) {
                    return ValidationStatus.error(Messages.StartPage_errorMessageCamelFilePathMustBeSupplied);
                }
                if (!(value.toString().trim().isEmpty())) {
                    File testFile = null;
                    final String path = (String) value;
                    testFile = new File(model.getProject().getFile(path).getLocationURI());
                    if (!testFile.exists()) {
                        testFile = new File(model.getProject().getFile(Util.RESOURCES_PATH + path).getLocationURI());
                        if (!testFile.exists()) {
                            return ValidationStatus.error(Messages.StartPage_errorMessageCamelFileInvalidLocation);
                        }
                    }
                    try {
                        CamelConfigurationHelper.load(testFile).getConfigBuilder();
                    } catch (final Exception e) {
                        return ValidationStatus.error(Messages.StartPage_errorMessageInvalidCamelFile);
                    }
                }
                return ValidationStatus.ok();
            }
        });
        _camelPathBinding = context.bindValue(camelFileTextValue, camelFileValue, strategy, null);
        _camelPathBinding.getModel().addChangeListener(new IChangeListener() {
            
            @Override
            public void handleChange(ChangeEvent event) {
                _endpointIdBinding.validateTargetToModel();
            }
        });
        ControlDecorationSupport.create(_camelPathBinding, decoratorPosition, _camelFilePathText.getParent());

        // bind the source type string dropdown
        _sourceCV.setContentProvider(new ObservableListContentProvider());
        IObservableValue widgetValue = ViewerProperties.singleSelection().observe(_sourceCV);
        IObservableValue modelValue = BeanProperties.value(Model.class, "sourceTypeStr").observe(model); //$NON-NLS-1$
        strategy = new UpdateValueStrategy();
        strategy.setBeforeSetValidator(new IValidator() {

            @Override
            public IStatus validate(final Object value) {
                getModel().setSourceFilePath(new String());
                ((NewTransformationWizard) getWizard()).resetSourceAndTargetPages();
                if (StartPage.this.getSourcePage() != null) {
                    ((XformWizardPage) StartPage.this.getSourcePage()).clearControls();
                }
                UIJob uiJob = new UIJob(Messages.StartPage_openErroUiJobName) {
                    @Override
                    public IStatus runInUIThread(IProgressMonitor monitor) {
                        if (StartPage.this.getTargetPage() != null) {
                            ((XformWizardPage) StartPage.this.getTargetPage()).pingBinding();
                        }
                        return Status.OK_STATUS;
                    }
                };
                uiJob.setSystem(true);
                uiJob.schedule();

                if (value == null || ((String) value).trim().isEmpty()) {
                    resetFinish();
                    return ValidationStatus.error(Messages.StartPage_errorMessageSourceTypeMissing);
                }
                return ValidationStatus.ok();
            }
        });

        WritableList sourceList = new WritableList();
        sourceList.add("Java"); //$NON-NLS-1$
        sourceList.add("XML"); //$NON-NLS-1$
        sourceList.add("JSON"); //$NON-NLS-1$
        sourceList.add("Other"); //$NON-NLS-1$
        sourceList.add(""); //$NON-NLS-1$
        _sourceCV.setInput(sourceList);
        ControlDecorationSupport.create(context.bindValue(widgetValue, modelValue, strategy, null), decoratorPosition,
                null);

        // bind the source type string dropdown
        _targetCV.setContentProvider(new ObservableListContentProvider());
        widgetValue = ViewerProperties.singleSelection().observe(_targetCV);
        modelValue = BeanProperties.value(Model.class, "targetTypeStr").observe(model); //$NON-NLS-1$
        strategy = new UpdateValueStrategy();
        strategy.setBeforeSetValidator(new IValidator() {

            @Override
            public IStatus validate(final Object value) {
                getModel().setTargetFilePath(new String());
                ((NewTransformationWizard) getWizard()).resetSourceAndTargetPages();
                if (StartPage.this.getTargetPage() != null) {
                    ((XformWizardPage) StartPage.this.getTargetPage()).clearControls();
                }
                UIJob uiJob = new UIJob(Messages.StartPage_openErroruiJobName) {
                    @Override
                    public IStatus runInUIThread(IProgressMonitor monitor) {
                        if (StartPage.this.getSourcePage() != null) {
                            ((XformWizardPage) StartPage.this.getSourcePage()).pingBinding();
                        }
                        return Status.OK_STATUS;
                    }
                };
                uiJob.setSystem(true);
                uiJob.schedule();

                if (value == null || ((String) value).trim().isEmpty()) {
                    resetFinish();
                    return ValidationStatus.error(Messages.StartPage_errorMessageTargetTypeMissing);
                }
                return ValidationStatus.ok();
            }
        });

        WritableList targetList = new WritableList();
        targetList.add("Java"); //$NON-NLS-1$
        targetList.add("XML"); //$NON-NLS-1$
        targetList.add("JSON"); //$NON-NLS-1$
        targetList.add("Other"); //$NON-NLS-1$
        targetList.add(""); //$NON-NLS-1$
        _targetCV.setInput(targetList);
        ControlDecorationSupport.create(context.bindValue(widgetValue, modelValue, strategy, null), decoratorPosition,
                null);

        listenForValidationChanges();
    }

    @Override
    public void notifyListeners() {
        if (_camelFilePathText != null && !_camelFilePathText.isDisposed()) {
            notifyControl(_sourceCV.getCombo(), SWT.Selection);
            notifyControl(_targetCV.getCombo(), SWT.Selection);
            notifyControl(_camelFilePathText, SWT.Modify);
            notifyControl(_dozerPathText, SWT.Modify);
            notifyControl(_idText, SWT.Modify);
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
