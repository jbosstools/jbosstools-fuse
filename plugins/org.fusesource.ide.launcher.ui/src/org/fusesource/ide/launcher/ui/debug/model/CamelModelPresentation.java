/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.launcher.ui.debug.model;

import org.eclipse.core.resources.IFile;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.IValueDetailListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.fusesource.ide.launcher.debug.model.CamelDebugTarget;
import org.fusesource.ide.launcher.debug.model.CamelEndpointBreakpoint;
import org.fusesource.ide.launcher.debug.model.CamelStackFrame;
import org.fusesource.ide.launcher.debug.model.CamelThread;
import org.fusesource.ide.launcher.debug.model.variables.BaseCamelVariable;
import org.fusesource.ide.launcher.debug.model.variables.CamelDebuggerVariable;
import org.fusesource.ide.launcher.debug.model.variables.CamelExchangeVariable;
import org.fusesource.ide.launcher.debug.model.variables.CamelMessageVariable;
import org.fusesource.ide.launcher.debug.model.variables.CamelProcessorVariable;
import org.fusesource.ide.launcher.debug.util.ICamelDebugConstants;
import org.fusesource.ide.launcher.ui.Activator;

/**
 * Renders camel debug elements
 * 
 * @author lhein
 */
public class CamelModelPresentation extends LabelProvider implements IDebugModelPresentation {
	
	private static final String IMG_CAMEL_DEBUG_TARGET         	= "camel.png";
	private static final String IMG_CAMEL_THREAD_RUN   			= "run_camel_context.png";
	private static final String IMG_CAMEL_THREAD_PAUSE 			= "pause_camel_context.png";
	private static final String IMG_CAMEL_STACK_FRAME  		 	= "endpoint_node.png";
	private static final String IMG_CAMEL_DEBUGGER	   			= "camel.png";
	private static final String IMG_CAMEL_EXCHANGE     			= "message.png";
	private static final String IMG_CAMEL_MESSAGE	   			= "message.png";
	private static final String IMG_CAMEL_VARIABLE 	   			= "variable.png";
	private static final String IMG_CAMEL_PROCESSOR  		 	= "endpoint_node.png";
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.ui.IDebugModelPresentation#setAttribute(java.lang.String, java.lang.Object)
	 */
	@Override
	public void setAttribute(String attribute, Object value) {
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
	 */
	@Override
	public Image getImage(Object element) {
		if (element instanceof CamelDebugTarget) {
			return Activator.getDefault().getImage(IMG_CAMEL_DEBUG_TARGET);			
		} else if (element instanceof CamelThread) {
			CamelThread t = (CamelThread)element;
			if (t.isSuspended()) {
				return Activator.getDefault().getImage(IMG_CAMEL_THREAD_PAUSE);
			} else {
				return Activator.getDefault().getImage(IMG_CAMEL_THREAD_RUN);				
			}
		} else if (element instanceof CamelStackFrame) {
			return Activator.getDefault().getImage(IMG_CAMEL_STACK_FRAME);
		} else if (element instanceof CamelDebuggerVariable) {
			return Activator.getDefault().getImage(IMG_CAMEL_DEBUGGER);
		} else if (element instanceof CamelExchangeVariable) {
			return Activator.getDefault().getImage(IMG_CAMEL_EXCHANGE);
		} else if (element instanceof CamelMessageVariable) {
			return Activator.getDefault().getImage(IMG_CAMEL_MESSAGE);
		} else if (element instanceof CamelProcessorVariable) {
			return Activator.getDefault().getImage(IMG_CAMEL_PROCESSOR);
		} else if (element instanceof BaseCamelVariable) {
			return Activator.getDefault().getImage(IMG_CAMEL_VARIABLE);
		} 
		
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(Object element) {
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.ui.IDebugModelPresentation#computeDetail(org.eclipse.debug.core.model.IValue, org.eclipse.debug.ui.IValueDetailListener)
	 */
	@Override
	public void computeDetail(IValue value, IValueDetailListener listener) {
		String detail = "";
		try {
			detail = value.getValueString();
		} catch (DebugException e) {
			Activator.getLogger().error(e);
		}
		listener.detailComputed(value, detail);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.ui.ISourcePresentation#getEditorInput(java.lang.Object)
	 */
	@Override
	public IEditorInput getEditorInput(Object element) {
		if (element instanceof IFile) {
			return new FileEditorInput((IFile)element);
		}
		if (element instanceof CamelEndpointBreakpoint) {
			return new FileEditorInput((IFile)((CamelEndpointBreakpoint)element).getMarker().getResource());
		}
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.ui.ISourcePresentation#getEditorId(org.eclipse.ui.IEditorInput, java.lang.Object)
	 */
	@Override
	public String getEditorId(IEditorInput input, Object element) {
		if (element instanceof IFile || element instanceof CamelEndpointBreakpoint) {
			return ICamelDebugConstants.CAMEL_EDITOR_ID;
		}
		return null;
	}
}
