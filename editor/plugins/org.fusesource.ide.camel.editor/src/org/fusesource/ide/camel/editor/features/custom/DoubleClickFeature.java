/******************************************************************************* 
 * Copyright (c) 2015 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 

package org.fusesource.ide.camel.editor.features.custom;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.fusesource.ide.camel.editor.internal.CamelEditorUIActivator;
import org.fusesource.ide.camel.editor.provider.ext.ICustomDblClickHandler;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;

/**
 * @author lhein
 */
public class DoubleClickFeature extends AbstractCustomFeature {
	
	private static final String DBL_CLICK_HANDLER_EXT_POINT_ID = "org.fusesource.ide.editor.dblClickHandler";
	
	/**
	 * @param fp
	 */
	public DoubleClickFeature(IFeatureProvider fp) {
		super(fp);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#canExecute(org.eclipse.graphiti.features.context.ICustomContext)
	 */
	@Override
	public boolean canExecute(ICustomContext context) {
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#isAvailable(org.eclipse.graphiti.features.context.IContext)
	 */
	@Override
	public boolean isAvailable(IContext context) {
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.impl.AbstractFeature#canUndo(org.eclipse.graphiti.features.context.IContext)
	 */
	@Override
	public boolean canUndo(IContext context) {
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.custom.ICustomFeature#execute(org.eclipse.graphiti.features.context.ICustomContext)
	 */
	@Override
	public void execute(ICustomContext context) {
		PictogramElement _pe = context.getPictogramElements()[0] instanceof Connection ? ((Connection) context.getPictogramElements()[0])
                .getStart().getParent() : context.getPictogramElements()[0];
        final Object bo = getBusinessObjectForPictogramElement(_pe);
       
        if (bo instanceof AbstractCamelModelElement) {
        	AbstractCamelModelElement _ep = (AbstractCamelModelElement) bo;
        	
        	// inject palette entries delivered via extension points
            IConfigurationElement[] extensions = Platform.getExtensionRegistry().getConfigurationElementsFor(DBL_CLICK_HANDLER_EXT_POINT_ID);
            try {
                for (IConfigurationElement e : extensions) {
                    final Object o = e.createExecutableExtension("class");
                    if (o instanceof ICustomDblClickHandler) {
                    	ICustomDblClickHandler handler = (ICustomDblClickHandler)o;
                    	if (handler.canHandle(_ep)) {
                    		handler.handleDoubleClick(_ep);
                    		break;
                    	}
                    }
                }
            } catch (CoreException ex) {
                CamelEditorUIActivator.pluginLog().logError(ex);
            }
        	
        }
	}
}
