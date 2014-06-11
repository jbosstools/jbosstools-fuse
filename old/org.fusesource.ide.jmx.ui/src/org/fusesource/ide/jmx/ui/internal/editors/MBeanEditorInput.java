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


import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.fusesource.ide.jmx.core.MBeanInfoWrapper;


public class MBeanEditorInput implements IEditorInput {

    private MBeanInfoWrapper wrapper;

    public MBeanEditorInput(MBeanInfoWrapper wrapper) {
        this.wrapper = wrapper;
    }

    public boolean exists() {
        return false;
    }

    public ImageDescriptor getImageDescriptor() {
        return null;
    }

    public String getName() {
        return wrapper.getObjectName().toString();
    }

    public IPersistableElement getPersistable() {
        return null;
    }

    public String getToolTipText() {
        return wrapper.getObjectName().getCanonicalName();
    }

    public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
        return null;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof MBeanEditorInput)) {
            return false;
        }
        MBeanEditorInput other = (MBeanEditorInput) obj;
        return other.wrapper.getObjectName().equals(wrapper.getObjectName());
    }

    public MBeanInfoWrapper getWrapper() {
        return wrapper;
    }

}
