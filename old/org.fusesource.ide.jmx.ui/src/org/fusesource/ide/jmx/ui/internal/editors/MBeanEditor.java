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


import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.fusesource.ide.jmx.core.MBeanInfoWrapper;
import org.fusesource.ide.jmx.ui.JMXUIActivator;


public class MBeanEditor extends FormEditor {

    public static final String ID = "org.fusesource.ide.jmx.ui.internal.editors.MBeanEditor"; //$NON-NLS-1$

    public MBeanEditor() {
    }

    @Override
    public void init(IEditorSite site, IEditorInput input)
            throws PartInitException {
        super.init(site, input);
        MBeanInfoWrapper wrapper = ((MBeanEditorInput) input).getWrapper();
        setPartName(wrapper.getObjectName().getCanonicalName());
    }

    protected FormToolkit createToolkit(Display display) {
        return new FormToolkit(Display.getDefault());
    }

    protected void addPages() {
        try {
            addPage(new AttributesPage(this));
            addPage(new OperationsPage(this));
            addPage(new NotificationsPage(this));
            addPage(new InfoPage(this));
        } catch (PartInitException e) {
            JMXUIActivator.log(IStatus.ERROR, e.getMessage(), e);
        }
    }
    
    public void doSave(IProgressMonitor monitor) {

    }

    public void doSaveAs() {
    }

    public boolean isSaveAsAllowed() {
        return false;
    }
    
    public String toString() {
    	return getPartName();
    }
}