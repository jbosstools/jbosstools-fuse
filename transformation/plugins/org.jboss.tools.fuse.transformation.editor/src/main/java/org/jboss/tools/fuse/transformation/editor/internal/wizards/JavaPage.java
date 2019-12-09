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

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaElement;
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
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.progress.UIJob;
import org.fusesource.ide.camel.editor.utils.CamelUtils;
import org.jboss.tools.fuse.transformation.core.model.ModelBuilder;
import org.jboss.tools.fuse.transformation.editor.Activator;
import org.jboss.tools.fuse.transformation.editor.internal.ModelViewer;
import org.jboss.tools.fuse.transformation.editor.internal.l10n.Messages;
import org.jboss.tools.fuse.transformation.editor.wizards.NewTransformationWizard;

/**
 * @author brianf
 *
 */
public class JavaPage extends XformWizardPage implements TransformationTypePage {

    private Composite _page;
    private boolean isSource = true;
    private Text _javaClassText;
    private org.jboss.tools.fuse.transformation.core.model.Model _javaModel = null;
    private ModelViewer _modelViewer;
    private Binding _binding;

    /**
     * @param pageName
     * @param model
     * @param isSource
     */
    public JavaPage(String pageName, final Model model, boolean isSource) {
        super(pageName, model);
        setTitle(Messages.JavaPage_pageTitle);
        setImageDescriptor(Activator.imageDescriptor("transform.png")); //$NON-NLS-1$
        this.isSource = isSource;
        observablesManager.addObservablesFromContext(context, true, true);
    }

    @Override
    public void createControl(final Composite parent) {
        if (this.isSource) {
            setTitle(Messages.JavaPage_SourceTypeJavaTitle);
            setDescription(Messages.JavaPage_SourceTypeJava_Description);
        } else {
            setTitle(Messages.JavaPage_TargetTypeJavaTitle);
            setDescription(Messages.JavaPage_TargetTypeJava_Description);
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
            label = createLabel(_page, Messages.JavaPage_label_theSourceClass, Messages.JavaPage_label_theSourClassTooltip);
        } else {
            label = createLabel(_page, Messages.JavaPage_label_theTargetClass, Messages.JavaPage_label_theTargetClassTooltip);
        }

        _javaClassText = new Text(_page, SWT.BORDER);
        _javaClassText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        _javaClassText.setToolTipText(label.getToolTipText());

        final Button javaClassBrowseButton = new Button(_page, SWT.NONE);
        javaClassBrowseButton.setLayoutData(new GridData());
        javaClassBrowseButton.setText("..."); //$NON-NLS-1$
        javaClassBrowseButton.setToolTipText(Messages.JavaPage_tooltipBrowseButton);

        javaClassBrowseButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
               try {
					IType selected = selectType(_page.getShell(), null);
                    if (selected != null) {
                        _javaClassText.setText(selected.getFullyQualifiedName());
                        if (isSourcePage()) {
                            model.setSourceType(ModelType.CLASS);
                            model.setSourceFilePath(selected.getFullyQualifiedName());
                        } else {
                            model.setTargetType(ModelType.CLASS);
                            model.setTargetFilePath(selected.getFullyQualifiedName());
                        }

                        UIJob uiJob = new UIJob(Messages.JavaPage_jobName_openError) {
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

        Group group = new Group(_page, SWT.SHADOW_ETCHED_IN);
        group.setText(Messages.JavaPage_groupText_ClassStructurepreview);
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
                    pathEmptyError = Messages.JavaPage_pathEmptyError_source;
                    unableToFindError = Messages.JavaPage_unableToFindError_source;
                } else {
                    pathEmptyError = Messages.JavaPage_pathEmptyError_target;
                    unableToFindError = Messages.JavaPage_unableToFindError_target;
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
            _javaModel = null;
            _modelViewer.setModel(_javaModel);
            _javaClassText.setText(""); //$NON-NLS-1$
        }
        notifyListeners();
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            notifyListeners();
        }
    }

    /**
     * @param shell Shell for the window
     * @param project project to look in
     * @return IType the type created
     * @throws JavaModelException exception thrown
     */
	public IType selectType(Shell shell, IProject project) throws JavaModelException {
        IJavaSearchScope searchScope = computeSearchScope(project);
        SelectionDialog dialog = JavaUI.createTypeDialog(shell, new ProgressMonitorDialog(shell), searchScope,
                IJavaElementSearchConstants.CONSIDER_CLASSES_AND_INTERFACES, false, "**"); //$NON-NLS-1$
        dialog.setTitle(Messages.JavaPage_SelectClass_title);
        dialog.setMessage(Messages.JavaPage_SelectClassDialog_message);
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

	/**
	 * @param project
	 * @return the scope of resources to be searched
	 */
	private IJavaSearchScope computeSearchScope(IProject project) {
		IJavaSearchScope searchScope;
        if (project == null) {
			ISelection selection = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection();
            if (selection instanceof IStructuredSelection) {
            	IStructuredSelection selectionToPass = (IStructuredSelection) selection;
                if (selectionToPass.getFirstElement() instanceof IFile) {
                    project = ((IFile) selectionToPass.getFirstElement()).getProject();
                }
            }
        }
        if (CamelUtils.project() != null) {
            if (project == null) {
                project = CamelUtils.project();
            }
            IJavaProject javaProject = JavaCore.create(project);
			searchScope = SearchEngine.createJavaSearchScope(new IJavaElement[] { javaProject });
        } else {
            searchScope = SearchEngine.createWorkspaceScope();
        }
		return searchScope;
	}
}
