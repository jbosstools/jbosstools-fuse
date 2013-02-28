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

package org.fusesource.ide.jmx.ui.internal;


import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.fusesource.ide.jmx.core.MBeanFeatureInfoWrapper;
import org.fusesource.ide.jmx.core.MBeanInfoWrapper;
import org.fusesource.ide.jmx.core.tree.ObjectNameNode;
import org.fusesource.ide.jmx.ui.JMXUIActivator;
import org.fusesource.ide.jmx.ui.internal.editors.MBeanEditor;
import org.fusesource.ide.jmx.ui.internal.editors.MBeanEditorInput;


public class EditorUtils {

    public static IEditorInput getEditorInput(Object input) {
        Assert.isNotNull(input);

        if (input instanceof ObjectNameNode) {
            ObjectNameNode node = (ObjectNameNode) input;
            MBeanInfoWrapper wrapper = node.getMbeanInfoWrapper();
            return new MBeanEditorInput(wrapper);
        }
        if (input instanceof MBeanInfoWrapper) {
            MBeanInfoWrapper wrapper = (MBeanInfoWrapper) input;
            return new MBeanEditorInput(wrapper);
        }
        if (input instanceof MBeanFeatureInfoWrapper) {
            MBeanFeatureInfoWrapper wrapper = (MBeanFeatureInfoWrapper) input;
            return new MBeanEditorInput(wrapper.getMBeanInfoWrapper());
        }

        return null;
    }

    public static IEditorPart isOpenInEditor(Object inputElement) {
        IEditorInput input = getEditorInput(inputElement);
        return isOpenInEditor(input);
    }

    public static IEditorPart isOpenInEditor(IEditorInput input) {
        if (input != null) {
            IWorkbenchPage p = JMXUIActivator.getActivePage();
            if (p != null) {
                return p.findEditor(input);
            }
        }
        return null;
    }

    public static IEditorPart openMBeanEditor(IEditorInput input) {
        IEditorPart part = EditorUtils.isOpenInEditor(input);
        if (part != null) {
            JMXUIActivator.getActivePage().bringToTop(part);
            return part;
        } else {
            try {
                return JMXUIActivator.getActivePage().openEditor(input,
                        MBeanEditor.ID);
            } catch (PartInitException e) {
                JMXUIActivator.log(IStatus.ERROR, e.getMessage(), e);
            }
        }
        return null;
    }

    public static void revealInEditor(IEditorPart editor, Object input) {
        if (input instanceof MBeanFeatureInfoWrapper) {
            MBeanFeatureInfoWrapper feature = (MBeanFeatureInfoWrapper) input;
            if (editor instanceof MBeanEditor) {
                ((MBeanEditor) editor).selectReveal(feature);
            }
        }
    }
}
