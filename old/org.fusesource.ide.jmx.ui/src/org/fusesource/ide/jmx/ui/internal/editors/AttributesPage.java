/*******************************************************************************
 * Copyright (c) 2007 Jeff Mesnil
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.jmx.ui.internal.editors;


import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IDetailsPageProvider;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.fusesource.ide.commons.ui.UIConstants;
import org.fusesource.ide.jmx.core.MBeanAttributeInfoWrapper;
import org.fusesource.ide.jmx.core.MBeanInfoWrapper;
import org.fusesource.ide.jmx.ui.Messages;
import org.fusesource.ide.jmx.ui.internal.views.navigator.UpdateSelectionJob;


public class AttributesPage extends FormPage {

    static final String ID = "attributes"; //$NON-NLS-1$

    private AttributesBLock block;

    private MBeanInfoWrapper wrapper;

    public class AttributesBLock extends MasterDetailsBlock implements
            IDetailsPageProvider {

        private AttributesSection masterSection;

        private IDetailsPage attributeDetails;

        public AttributesBLock() {
        }

        protected void createMasterPart(IManagedForm managedForm,
                Composite parent) {
            masterSection = new AttributesSection(wrapper, managedForm, parent);
            managedForm.addPart(masterSection);
        }

        protected void registerPages(DetailsPart detailsPart) {
            attributeDetails = new AttributeDetails(masterSection);
            detailsPart.setPageLimit(10);
            detailsPart.setPageProvider(this);
            detailsPart.registerPage(MBeanAttributeInfoWrapper.class,
                    attributeDetails);
        }

        protected void createToolBarActions(IManagedForm managedForm) {
            ActionUtils.createLayoutActions(managedForm, sashForm);
        }

        public Object getPageKey(Object object) {
            return object;
        }

        public IDetailsPage getPage(Object key) {
            if (key instanceof MBeanAttributeInfoWrapper) {
                return attributeDetails;
            }
            return null;
        }
    }

    public AttributesPage(FormEditor editor) {
        super(editor, ID, Messages.AttributesPage_title);
        MBeanEditorInput input = (MBeanEditorInput) editor.getEditorInput();
        this.wrapper = input.getWrapper();
        block = new AttributesBLock();
    }

    protected void createFormContent(IManagedForm managedForm) {
        ScrolledForm form = managedForm.getForm();
        form.getForm().setSeparatorVisible(true);
        form.getForm().setText(wrapper.getObjectName().toString());
        block.createContent(managedForm);
        block.masterSection.getTableViewer().addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				UpdateSelectionJob.launchJob(UIConstants.JMX_EXPLORER_VIEW_ID);
			} 
        });
    }
    
    public IStructuredSelection getSelection() {
    	return (IStructuredSelection)block.masterSection.getTableViewer().getSelection();
    }

    @Override
    public boolean selectReveal(Object object) {
        if (object instanceof MBeanAttributeInfoWrapper) {
            MBeanAttributeInfoWrapper attrWrapper = (MBeanAttributeInfoWrapper) object;
            getEditor().setActivePage(ID);
            return block.masterSection.setFormInput(attrWrapper);
        }
        return super.selectReveal(object);
    }
}