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
package org.fusesource.ide.camel.editor.propertysheet;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.fusesource.ide.camel.editor.AbstractNodes;
import org.fusesource.ide.camel.editor.utils.DiagramUtils;
import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.Endpoint;
import org.fusesource.ide.commons.ui.Selections;

/**
 * @author lhein
 */
public class JMSBindingSection extends AbstractPropertySection {

	private static final String TYPE_QUEUE = "queue";
	private static final String TYPE_TOPIC = "topic";
	
	private FormToolkit toolkit;
    private Form form;
    private DataBindingContext bindingContext;
    private Composite parent;
    
    private Text txtName;
    private Button btnQueue;
    private Button btnTopic;
    private Button btnConfigureConnection;
    
    private Endpoint selectedEP;
    
    /**
     * 
     */
    public JMSBindingSection() {
        bindingContext = new DataBindingContext();
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#dispose()
     */
    @Override
    public void dispose() {
        if (toolkit != null) {
            toolkit.dispose();
            toolkit = null;
        }
        super.dispose();
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#setInput(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
     */
    @Override
    public void setInput(IWorkbenchPart part, ISelection selection) {
        super.setInput(part, selection);
        Object o = Selections.getFirstSelection(selection);
        AbstractNode n = AbstractNodes.toAbstractNode(o);
        if (n instanceof Endpoint) {
            this.selectedEP = (Endpoint)n;
            txtName.setText(getTopicOrQueue(this.selectedEP.getUri()));
            if (isTopic(this.selectedEP.getUri())) {
            	btnTopic.setSelection(true);
            	btnQueue.setSelection(false);
            } else {
            	btnTopic.setSelection(false);
            	btnQueue.setSelection(true);
            }
            form.setText("JMS Configuration - " + DiagramUtils.filterFigureLabel(selectedEP.getDisplayText()));
        } else {
            this.selectedEP = null;
            form.setText("JMS Configuration");
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#createControls(org.eclipse.swt.widgets.Composite, org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage)
     */
    @Override
    public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
        this.parent = parent;
        this.toolkit = new FormToolkit(parent.getDisplay());
        super.createControls(parent, aTabbedPropertySheetPage);        
        
        // now setup the file binding properties page
        parent.setLayout(new GridLayout());
        parent.setLayoutData(new GridData(GridData.FILL_BOTH));

        form = toolkit.createForm(parent);
        form.setLayoutData(new GridData(GridData.FILL_BOTH));
        form.setText("Filesystem Configuration");
        toolkit.decorateFormHeading(form);

        form.getBody().setLayout(new GridLayout(3, false));

        Composite sbody = form.getBody();
                
        // Create Group
        Group group1 = new Group(sbody, SWT.SHADOW_NONE | SWT.FLAT);
        group1.setText("Destination Type:");
        group1.setLayout(new RowLayout(SWT.HORIZONTAL));
        group1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));
        toolkit.adapt(group1);
        
        this.btnQueue = toolkit.createButton(group1, "Queue", SWT.RADIO | SWT.BORDER);
        this.btnQueue.addSelectionListener(new SelectionAdapter() {
            
        	/* (non-Javadoc)
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected(SelectionEvent e) {
            	updateQueueOrTopicName(txtName.getText().length() < 1 ? "" : txtName.getText().trim());
            }
        });

        this.btnTopic = toolkit.createButton(group1, "Topic", SWT.RADIO | SWT.BORDER);
        this.btnTopic.addSelectionListener(new SelectionAdapter() {
            
        	/* (non-Javadoc)
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected(SelectionEvent e) {
            	updateQueueOrTopicName(txtName.getText().length() < 1 ? "" : txtName.getText().trim());
            }
        });

        Label l = toolkit.createLabel(sbody, "Destination Name:");
        l.setLayoutData(new GridData());
        
        this.txtName = toolkit.createText(sbody, "", SWT.BORDER | SWT.LEFT);
        this.txtName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
        this.txtName.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                String name = txtName.getText();
                if (selectedEP != null) {
                    updateQueueOrTopicName(name.length() < 1 ? "" : name.trim());
                }
            }
        });
        
        this.btnConfigureConnection = toolkit.createButton(sbody, "Connection", SWT.BORDER | SWT.PUSH);
        this.btnConfigureConnection.setLayoutData(new GridData());
        this.btnConfigureConnection.setVisible(false);
        this.btnConfigureConnection.addSelectionListener(new SelectionAdapter() {
            /* (non-Javadoc)
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected(SelectionEvent e) {
                execConnectionsConfigDialog();
            }
        });

        form.layout();
    }
    
    /**
     * user clicked configure button to select or create a jms connection config
     */
    protected void execConnectionsConfigDialog() {
    	// TODO 
    }
    
    /**
     * 
     * @param name
     */
    protected void updateQueueOrTopicName(String newName) {
        String newDestination = buildDestinationString(newName);
        String[] parts = selectedEP.getUri().split(":");
        String newUri = "";
        if (parts.length == 0) {
        	// something went wrong - do nothing
        	return;
        } else if (parts.length==1) {
        	// JMS : --> we simply append the new name and type
        	newUri = String.format("%s:%s", parts[0], newDestination);
        } else if (parts.length>=2) {
    		String params = selectedEP.getUri().indexOf("?") != -1 ? selectedEP.getUri().substring(selectedEP.getUri().indexOf("?")+1) : "";
        	if (params.length()>0) {
        		newUri = String.format("%s:%s?%s", parts[0], newDestination, params);
        	} else {
        		newUri = String.format("%s:%s", parts[0], newDestination);
        	}
        }
        selectedEP.setUri(newUri);
    }

    /**
     * takes selected type (QUEUE or TOPIC) and the name to form
     * the destination name like "[QUEUE|TOPIC]:NAME" 
     * 
     * @param destName 	the name of the destination
     * @return	the destination type and name
     */
    protected String buildDestinationString(String destName) {
    	return String.format("%s:%s", btnQueue.getSelection() ? TYPE_QUEUE : TYPE_TOPIC, destName);
    }
    
    /**
     * 
     * @param uri
     * @return
     */
    protected String getTopicOrQueue(String uri) {
        String[] parts = uri.split(":");
        if (parts.length>=2) {
        	// 2 possible notations
        	if (parts.length>2) {
            	// a) JMS : QUEUE | TOPIC : NAME
        		return parts[2].substring(0, parts[2].indexOf("?") != -1 ? parts[2].indexOf("?") : parts[2].length());
        	} else {
            	// b) JMS : NAME  (here its automatically a queue
        		return parts[1].substring(0, parts[1].indexOf("?") != -1 ? parts[1].indexOf("?") : parts[1].length());
        	}
        }
        return "";
    }
    
    /**
     * 
     * @param uri
     * @return
     */
    protected boolean isTopic(String uri) {
    	String[] parts = uri.split(":");
        if (parts.length>2) {
			return parts[1].equalsIgnoreCase(TYPE_TOPIC);
        }
        return false;
    }
}
