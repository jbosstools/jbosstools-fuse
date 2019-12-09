/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.launcher.ui.debug.model;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.IValueDetailListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.fusesource.ide.launcher.debug.model.CamelConditionalBreakpoint;
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
import org.fusesource.ide.launcher.ui.Messages;

/**
 * Renders camel debug elements
 * 
 * @author lhein
 */
public class CamelModelPresentation extends LabelProvider implements IDebugModelPresentation {
	
	private static final String IMG_CAMEL_DEBUG_TARGET                      = "camel.png";
	private static final String IMG_CAMEL_DISCONNECTED                      = "camel_disabled.png";
	private static final String IMG_CAMEL_THREAD_RUN                        = "run_camel_context.png";
	private static final String IMG_CAMEL_THREAD_PAUSE                      = "pause_camel_context.png";
	private static final String IMG_CAMEL_STACK_FRAME                       = "endpoint_node.png";
	private static final String IMG_CAMEL_DEBUGGER                          = "camel.png";
	private static final String IMG_CAMEL_EXCHANGE                          = "message.png";
	private static final String IMG_CAMEL_MESSAGE                           = "message.png";
	private static final String IMG_CAMEL_VARIABLE                          = "variable.png";
	private static final String IMG_CAMEL_PROCESSOR                         = "endpoint_node.png";
	private static final String IMG_CAMEL_BREAKPOINT_ENABLED                = "red-dot.png";
	private static final String IMG_CAMEL_CONDITIONAL_BREAKPOINT_ENABLED    = "yellow-dot.png";
	private static final String IMG_CAMEL_BREAKPOINT_DISABLED               = "gray-dot.png";
	
	@Override
	public void setAttribute(String attribute, Object value) {
		/* No specific display configuration*/
	}
	
	@Override
	public Image getImage(Object element) {
		Activator plugin = Activator.getDefault();
		if (element instanceof CamelDebugTarget) {
			return getImage(element, plugin);
		} else if (element instanceof CamelThread) {
			return getImage((CamelThread) element, plugin);
		} else if (element instanceof CamelStackFrame) {
			return plugin.getImage(IMG_CAMEL_STACK_FRAME);
		} else if (element instanceof CamelEndpointBreakpoint) {
			getImage((CamelEndpointBreakpoint) element, plugin);
		} else if (element instanceof CamelDebuggerVariable) {
			return plugin.getImage(IMG_CAMEL_DEBUGGER);
		} else if (element instanceof CamelExchangeVariable) {
			return plugin.getImage(IMG_CAMEL_EXCHANGE);
		} else if (element instanceof CamelMessageVariable) {
			return plugin.getImage(IMG_CAMEL_MESSAGE);
		} else if (element instanceof CamelProcessorVariable) {
			return plugin.getImage(IMG_CAMEL_PROCESSOR);
		} else if (element instanceof BaseCamelVariable) {
			return plugin.getImage(IMG_CAMEL_VARIABLE);
		} 
		
		return null;
	}

	private Image getImage(Object camelDebugTarget, Activator plugin) {
		if(((CamelDebugTarget) camelDebugTarget).isDisconnected()){
			return plugin.getImage(IMG_CAMEL_DISCONNECTED);
		} else if(((CamelDebugTarget) camelDebugTarget).isSuspended()){
			return plugin.getImage(IMG_CAMEL_THREAD_PAUSE);
		} else {
			return plugin.getImage(IMG_CAMEL_DEBUG_TARGET);		
		}
	}

	private Image getImage(CamelThread camelThread, Activator plugin) {
		if (camelThread.isSuspended()) {
			return plugin.getImage(IMG_CAMEL_THREAD_PAUSE);
		} else if (camelThread.isTerminated()) {
			return plugin.getImage(IMG_CAMEL_DISCONNECTED);
		} else {
			return plugin.getImage(IMG_CAMEL_THREAD_RUN);				
		}
	}

	private Image getImage(CamelEndpointBreakpoint breakpoint, Activator plugin) {
		try {
			if (breakpoint.isEnabled()) {
				if(breakpoint instanceof CamelConditionalBreakpoint){
					return plugin.getImage(IMG_CAMEL_CONDITIONAL_BREAKPOINT_ENABLED);
				} else {
					return plugin.getImage(IMG_CAMEL_BREAKPOINT_ENABLED);
				}
			} else {
				return plugin.getImage(IMG_CAMEL_BREAKPOINT_DISABLED);
			}
		} catch (CoreException ex) {
			Activator.getLogger().error(ex);
		}
		return null;
	}
	
	@Override
	public String getText(Object element) {
		if (element instanceof CamelEndpointBreakpoint) {
			return element.toString();
		} else if (element instanceof CamelDebugTarget) {
			return getText((CamelDebugTarget) element);
		}
		return null;
	}

	private String getText(CamelDebugTarget camelDebugTarget) {
		String name = camelDebugTarget.getName();
		if(camelDebugTarget.isDisconnected()) {
			return Messages.disconnected+name;
		} else if(camelDebugTarget.isSuspended()){
			return Messages.suspended+name;
		} else if(camelDebugTarget.isTerminated()){
			return Messages.terminated+name;
		} else {
			return null;
		}
	}

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
	
	@Override
	public String getEditorId(IEditorInput input, Object element) {
		if (element instanceof IFile || element instanceof CamelEndpointBreakpoint) {
			return ICamelDebugConstants.CAMEL_EDITOR_ID;
		}
		return null;
	}
}
