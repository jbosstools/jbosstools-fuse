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

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.dialogs.ITypeSelectionComponent;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ViewerProperties;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.progress.UIJob;
import org.fusesource.ide.camel.editor.utils.CamelUtils;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.jboss.tools.fuse.transformation.core.camel.CamelConfigBuilder;
import org.jboss.tools.fuse.transformation.core.model.ModelBuilder;
import org.jboss.tools.fuse.transformation.editor.Activator;
import org.jboss.tools.fuse.transformation.editor.internal.ModelViewer;
import org.jboss.tools.fuse.transformation.editor.internal.l10n.Messages;
import org.jboss.tools.fuse.transformation.editor.wizards.NewTransformationWizard;
/**
 * @author brianf
 *
 */
public class OtherPage extends XformWizardPage implements TransformationTypePage {

    private Composite _page;
    private boolean isSource = true;
    private Text _javaClassText;
    private ComboViewer _dataFormatIdCombo;
    private org.jboss.tools.fuse.transformation.core.model.Model _javaModel = null;
    private ModelViewer _modelViewer;
    private Label _dfErrorLabel;
    private Binding _binding;
    private Binding _binding2;
    private IObservableValue idModelValue;

    /**
     * @param pageName
     * @param model
     * @param isSource
     */
    public OtherPage(String pageName, final Model model, boolean isSource) {
        super(pageName, model);
        setTitle(Messages.OtherPage_title);
        setImageDescriptor(Activator.imageDescriptor("transform.png")); //$NON-NLS-1$
        this.isSource = isSource;
        observablesManager.addObservablesFromContext(context, true, true);
    }

    @Override
    public void createControl(final Composite parent) {
        if (this.isSource) {
            setTitle(Messages.OtherPage_titleSource);
            setDescription(Messages.OtherPage_descriptionSource);
        } else {
            setTitle(Messages.OtherPage_titletarget);
            setDescription(Messages.OtherPage_descriptionTarget);
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
            label = createLabel(_page, Messages.OtherPage_labelSourceClass, Messages.OtherPage_tooltipSourceClass);
        } else {
            label = createLabel(_page, Messages.OtherPage_labelTargetClass, Messages.OtherPage_tooltipTargetClass);
        }

        _javaClassText = new Text(_page, SWT.BORDER);
        _javaClassText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        _javaClassText.setToolTipText(label.getToolTipText());

        final Button javaClassBrowseButton = new Button(_page, SWT.NONE);
        javaClassBrowseButton.setLayoutData(new GridData());
        javaClassBrowseButton.setText("..."); //$NON-NLS-1$
        javaClassBrowseButton.setToolTipText(Messages.OtherPage_tooltipBrowseButton);

        javaClassBrowseButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
                try {
                    IType selected = selectType(_page.getShell(), "java.lang.Object", null); //$NON-NLS-1$
                    if (selected != null) {
                        _javaClassText.setText(selected.getFullyQualifiedName());
                        if (isSourcePage()) {
                            model.setSourceType(ModelType.OTHER);
                            model.setSourceFilePath(selected.getFullyQualifiedName());
                        } else {
                            model.setTargetType(ModelType.OTHER);
                            model.setTargetFilePath(selected.getFullyQualifiedName());
                        }

                        UIJob uiJob = new UIJob(Messages.OtherPage_uiJobNameOpenError) {
                            @Override
                            public IStatus runInUIThread(IProgressMonitor monitor) {
                                NewTransformationWizard wizard = (NewTransformationWizard) getWizard();
                                try {
                                    Class<?> tempClass = wizard.loader().loadClass(selected.getFullyQualifiedName());
                                    _javaModel = ModelBuilder.fromJavaClass(tempClass);
                                    _modelViewer.setModel(_javaModel);
                                } catch (ClassNotFoundException e) {
                                	Activator.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Error while reading Java model", e)); //$NON-NLS-1$
                                }
                                return Status.OK_STATUS;
                            }
                        };
                        uiJob.setSystem(true);
                        uiJob.schedule();
                        _javaClassText.notifyListeners(SWT.Modify, new Event());
                    }
                } catch (JavaModelException e1) {
                	Activator.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Error while trying to update the newly selected class", e1)); //$NON-NLS-1$
                }
             }
        });

        createLabel(_page, Messages.OtherPage_labelDataFormatID, Messages.OtherPage_tooltipDataFormatID);

        _dataFormatIdCombo = new ComboViewer(_page, SWT.DROP_DOWN | SWT.READ_ONLY);
        _dataFormatIdCombo.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        _dataFormatIdCombo.getCombo().setToolTipText(label.getToolTipText());
        _dataFormatIdCombo.setContentProvider(new ObservableListContentProvider());

        createLabel(_page, "", ""); // spacer //$NON-NLS-1$ //$NON-NLS-2$
        _dfErrorLabel = createLabel(_page, "", ""); //$NON-NLS-1$ //$NON-NLS-2$
        _dfErrorLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

        Group group = new Group(_page, SWT.SHADOW_ETCHED_IN);
        group.setText(Messages.OtherPage_groupNameClassStructurePreview);
        group.setLayout(new GridLayout(3, false));
        group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 3));

        _modelViewer = new ModelViewer(group, _javaModel);
        _modelViewer.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
        _modelViewer.layout();

        bindControls();
        validatePage();

    }

    private void bindControls() {

        // Bind source file path widget to UI model
        IObservableValue widgetValue = WidgetProperties.text(SWT.Modify).observe(_javaClassText);
        IObservableValue modelValue;
        if (isSourcePage()) {
            modelValue = BeanProperties.value(Model.class, "sourceFilePath").observe(model); //$NON-NLS-1$
        } else {
            modelValue = BeanProperties.value(Model.class, "targetFilePath").observe(model); //$NON-NLS-1$
        }
        UpdateValueStrategy strategy = new UpdateValueStrategy();
        strategy.setBeforeSetValidator(new IValidator() {

            @Override
            public IStatus validate(final Object value) {
                final String path = value == null ? null : value.toString().trim();
                String pathEmptyError;
                String unableToFindError;
                if (isSourcePage()) {
                    pathEmptyError = Messages.OtherPage_errorMessagePathEmptySource;
                    unableToFindError = Messages.OtherPage_errorMessageNotFoundSource;
                } else {
                    pathEmptyError = Messages.OtherPage_errorMessagePathEmptyTarget;
                    unableToFindError = Messages.OtherPage_errorMessageNotFoundTarget;
                }
                if (path == null || path.isEmpty()) {
                    return ValidationStatus.error(pathEmptyError);
                }
                NewTransformationWizard wizard = (NewTransformationWizard) getWizard();
                try {
                    Class<?> tempClass = wizard.loader().loadClass(path);
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
        ControlDecorationSupport.create(_binding, decoratorPosition,
                _javaClassText.getParent());

        listenForValidationChanges();
    }

    public void initialize() {

        // Bind id widget to UI model
        IObservableValue widgetValue = ViewerProperties.singleSelection().observe(_dataFormatIdCombo);
        idModelValue = null;

        WritableList dfList = new WritableList();
        CamelConfigBuilder configBuilder = new CamelConfigBuilder();

        Collection<AbstractCamelModelElement> dataFormats = configBuilder.getDataFormats();
        for (Iterator<AbstractCamelModelElement> iterator = dataFormats.iterator(); iterator.hasNext();) {
        	AbstractCamelModelElement df = iterator.next();
            if (df.getId() != null) {
                dfList.add(df.getId());
            }
        }
        if (dfList.isEmpty()) {
            _dfErrorLabel.setText(Messages.OtherPage_errormessageNoAvailableDataFormats);
            _dfErrorLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
            _dataFormatIdCombo.getCombo().setEnabled(false);
        } else {
            _dfErrorLabel.setText(""); //$NON-NLS-1$
            _dataFormatIdCombo.getCombo().setEnabled(true);
        }
        _dataFormatIdCombo.setInput(dfList);
        if (isSourcePage()) {
            idModelValue = BeanProperties.value(Model.class, "sourceDataFormatid").observe(model); //$NON-NLS-1$
        } else {
            idModelValue = BeanProperties.value(Model.class, "targetDataFormatid").observe(model); //$NON-NLS-1$
        }
        UpdateValueStrategy strategy = new UpdateValueStrategy();
        strategy.setBeforeSetValidator(new IValidator() {

            @Override
            public IStatus validate(final Object value) {
                final String path = value == null ? null : value.toString().trim();
                if (path == null || path.isEmpty()) {
                    return ValidationStatus.error(Messages.OtherPage_errormessageNoDataFormatId);
                }
                return ValidationStatus.ok();
            }
        });
        _binding2 = context.bindValue(widgetValue, idModelValue, strategy, null);
        ControlDecorationSupport.create(_binding2, decoratorPosition, _javaClassText.getParent());
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
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            initialize();
            pingBinding();
            notifyListeners();
        }
    }

    @Override
    public void notifyListeners() {
        if (_javaClassText != null && !_javaClassText.isDisposed()) {
            _javaClassText.notifyListeners(SWT.Modify, new Event());
            _dataFormatIdCombo.getCombo().notifyListeners(SWT.Selection, new Event());
        }
    }

    @Override
    public void clearControls() {
        if (_javaClassText != null && !_javaClassText.isDisposed()) {
            _javaModel = null; // new org.jboss.tools.fuse.transformation.model.Model("", "");
            _modelViewer.setModel(_javaModel);
            _javaClassText.setText(""); //$NON-NLS-1$
            _dataFormatIdCombo.getCombo().deselectAll();
            _dataFormatIdCombo.getCombo().clearSelection();
            if (idModelValue != null) {
            	idModelValue.setValue(""); //$NON-NLS-1$
            }
        }
        notifyListeners();
    }

    @Override
    public void pingBinding() {
        if (_binding != null) {
            _binding.validateTargetToModel();
        }
        if (_binding2 != null) {
            _binding2.validateTargetToModel();
        }
    }

    /**
     * @param shell Shell for the window
     * @param superTypeName supertype to search for
     * @param project project to look in
     * @return IType the type created
     * @throws JavaModelException exception thrown
     */
    public IType selectType(Shell shell, String superTypeName, IProject project) throws JavaModelException {
        IJavaSearchScope searchScope = null;
        if (project == null) {
            ISelection selection = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService()
                    .getSelection();
            if (selection instanceof IStructuredSelection) {
            	IStructuredSelection selectionToPass = (IStructuredSelection) selection;
                if (selectionToPass.getFirstElement() instanceof IFile) {
                    project = ((IFile) selectionToPass.getFirstElement()).getProject();
                }
            }
        }
        if (superTypeName == null) {
        	superTypeName = "java.lang.Object"; //$NONNLS-1$ //$NON-NLS-1$
        }
        if (CamelUtils.project() != null) {
            if (project == null) {
                project = CamelUtils.project();
            }
            IJavaProject javaProject = JavaCore.create(project);
            IType superType = javaProject.findType(superTypeName);
            if (superType != null) {
                searchScope = SearchEngine.createStrictHierarchyScope(javaProject, superType, true, false, null);
            }
        } else {
            searchScope = SearchEngine.createWorkspaceScope();
        }
        SelectionDialog dialog = JavaUI.createTypeDialog(shell, new ProgressMonitorDialog(shell), searchScope,
                IJavaElementSearchConstants.CONSIDER_CLASSES_AND_INTERFACES, false, "**"); //$NON-NLS-1$
        dialog.setTitle(Messages.OtherPage_selectClassDialogTitle);
        dialog.setMessage(Messages.OtherPage_matchingitemsMessageSelectClassDialog);
        if (dialog instanceof ITypeSelectionComponent) {
            ((ITypeSelectionComponent)dialog).triggerSearch();
          }
        if (dialog.open() == IDialogConstants.CANCEL_ID) {
            return null;
        }
        Object[] types = dialog.getResult();
        if (types == null || types.length == 0) {
            return null;
        }
        return (IType) types[0];
     }
}
