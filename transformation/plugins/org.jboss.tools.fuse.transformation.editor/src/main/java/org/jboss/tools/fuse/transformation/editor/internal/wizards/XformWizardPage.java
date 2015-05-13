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
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.ObservablesManager;
import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.jboss.tools.fuse.transformation.editor.wizards.NewTransformationWizard;

/**
 * @author brianf
 *
 */
public abstract class XformWizardPage extends WizardPage {

    final DataBindingContext context = new DataBindingContext(
            SWTObservables.getRealm(Display.getCurrent()));
    final ObservablesManager observablesManager = new ObservablesManager();
    final Model model;
    
    protected int decoratorPosition = SWT.TOP; //SWT.TOP | SWT.LEFT;


    protected XformWizardPage(String pageName, final Model model) {
        super(pageName);
        this.model = model;
    }
    
    protected XformWizardPage(String pageName, String title,
            ImageDescriptor titleImage, Model model) {
        this(pageName, model);
        setTitle(title);
        setImageDescriptor(titleImage);
    }
    

    protected Label createLabel(Composite parent, int style, String labeltext, String tooltip) {
        Label label = new Label(parent, style);
        label.setText(labeltext);
        label.setToolTipText(tooltip);
        return label;
    }

    protected Label createLabel(Composite parent, String labeltext, String tooltip) {
        return createLabel(parent, SWT.NONE, labeltext, tooltip);
    }

    @Override
    public void createControl(Composite parent) {
        // empty
        setErrorMessage(null);
    }

    @Override
    public IWizardPage getNextPage() {
        if (this instanceof StartPage) {
            return getSourcePage();
        } else if (this instanceof TransformationTypePage) {
            TransformationTypePage page = (TransformationTypePage) this;
            if (page.isSourcePage()) {
                return getTargetPage();
            } else {
                return null;
            }
        }
        return super.getNextPage();
    }
    
    public void resetFinish() {
        setPageComplete(false);
    }
    
    public IWizardPage getSourcePage() {
        if (model.getSourceTypeStr() != null) {
            if (model.getSourceTypeStr().equalsIgnoreCase("java")) {
                NewTransformationWizard wizard = (NewTransformationWizard) getWizard();
                return wizard.javaSource;
            } else if (model.getSourceTypeStr().equalsIgnoreCase("xml")) {
                NewTransformationWizard wizard = (NewTransformationWizard) getWizard();
                return wizard.xmlSource;
            } else if (model.getSourceTypeStr().equalsIgnoreCase("json")) {
                NewTransformationWizard wizard = (NewTransformationWizard) getWizard();
                return wizard.jsonSource;
            } else if (model.getSourceTypeStr().equalsIgnoreCase("other")) {
                NewTransformationWizard wizard = (NewTransformationWizard) getWizard();
                return wizard.otherSource;
            }

        }
        return null;
    }

    public IWizardPage getTargetPage() {
        if (model.getTargetTypeStr() != null) {
            if (model.getTargetTypeStr().equalsIgnoreCase("java")) {
                NewTransformationWizard wizard = (NewTransformationWizard) getWizard();
                return wizard.javaTarget;
            } else if (model.getTargetTypeStr().equalsIgnoreCase("xml")) {
                NewTransformationWizard wizard = (NewTransformationWizard) getWizard();
                return wizard.xmlTarget;
            } else if (model.getTargetTypeStr().equalsIgnoreCase("json")) {
                NewTransformationWizard wizard = (NewTransformationWizard) getWizard();
                return wizard.jsonTarget;
            } else if (model.getTargetTypeStr().equalsIgnoreCase("other")) {
                NewTransformationWizard wizard = (NewTransformationWizard) getWizard();
                return wizard.otherTarget;
            }
        }
        return null;
    }

    void validatePage() {
        setPageComplete(getErrorMessage() == null);
    }
    
    protected Model getModel() {
        return this.model;
    }
    
    protected void listenForValidationChanges() {
        if (context != null) {
            // get the validation status provides
            IObservableList bindings = context.getValidationStatusProviders();

            IChangeListener listener = new ValidationChangedListener();
            // register the listener to all bindings
            for (Object o : bindings) {
                Binding b = (Binding) o;
                b.getTarget().addChangeListener(listener);
            }
        }
    }

    class ValidationChangedListener implements IChangeListener {

        @Override
        public void handleChange(ChangeEvent event) {
            validatePage();
        }

    }
}
