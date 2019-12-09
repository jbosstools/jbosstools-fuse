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
package org.fusesource.ide.launcher.debug.model;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.debug.core.model.IWatchExpressionDelegate;
import org.eclipse.debug.core.model.IWatchExpressionListener;
import org.eclipse.debug.core.model.IWatchExpressionResult;
import org.fusesource.ide.launcher.debug.model.values.BaseCamelValue;

/**
 * @author lhein
 *
 */
public class WatchExpressionDelegate implements IWatchExpressionDelegate {

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IWatchExpressionDelegate#evaluateExpression(java.lang.String, org.eclipse.debug.core.model.IDebugElement, org.eclipse.debug.core.model.IWatchExpressionListener)
	 */
	@Override
	public void evaluateExpression(String expression, IDebugElement context,
			IWatchExpressionListener listener) {
		// find a stack frame context if possible.
		IStackFrame frame = null;
		if (context instanceof IStackFrame) {
			frame = (IStackFrame)context;
		} else if (context instanceof IThread) {
			try {
				frame = ((IThread)context).getTopStackFrame();
			} catch (DebugException e) {
			}
		}
		if (frame == null) {
			listener.watchEvaluationFinished(null);	
		} else {
			// consult the adapter in case of a wrappered debug model
			final CamelStackFrame stackFrame =(CamelStackFrame) ((IAdaptable)frame).getAdapter(CamelStackFrame.class);
			if (stackFrame != null) {
				doEvaluation(stackFrame, expression, listener);
			} else {
				listener.watchEvaluationFinished(null);
			}	
		}
	}
	
	/**
	 * 
	 * @param stackFrame
	 * @param expression
	 * @param listener
	 */
	protected void doEvaluation(CamelStackFrame stackFrame, String expression, IWatchExpressionListener listener) {
		try {
			new EvaluationRunnable(stackFrame, expression, listener).run(new NullProgressMonitor());
		} catch (CoreException ex) {
			listener.watchEvaluationFinished(null);
		}
	}
	
	/**
	 * Runnable used to evaluate the expression.
	 */
	private final class EvaluationRunnable implements IWorkspaceRunnable {
		
		private final CamelStackFrame fStackFrame;
		private final IWatchExpressionListener fListener;
		private final String fExpression;
		
		private EvaluationRunnable(CamelStackFrame frame, String expression, IWatchExpressionListener listener) {
			this.fStackFrame= frame;
			this.fExpression = expression;
			this.fListener = listener;
		}
		
		protected IVariable findVariableForName(Object element, String variableName) throws DebugException {
			IVariable[] vars = getVariables(element);
			for (IVariable var : vars) {
				if (var.getName().equals(variableName)) {
					return var;
				}
				if (var.getValue().hasVariables()) {
					IVariable v = findVariableForName(var.getValue(), variableName);
					if (v != null) return v;
				}
			}
			return null;
		}
		
		protected IVariable[] getVariables(Object element) throws DebugException {
			if (element instanceof CamelStackFrame) {
				return ((CamelStackFrame)element).getVariables();
			} else if (element instanceof BaseCamelValue) {
				return ((BaseCamelValue)element).getVariables();
			}
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.core.resources.IWorkspaceRunnable#run(org.eclipse.core.runtime.IProgressMonitor)
		 */
		@Override
		public void run(IProgressMonitor monitor) throws CoreException {
			try {
				final IVariable var = findVariableForName(fStackFrame, fExpression);
				if (var != null) {
					IWatchExpressionResult watchResult= new IWatchExpressionResult() {
						@Override
						public IValue getValue() {
							try {
								return var.getValue();
							} catch (DebugException ex) {
								return null;
							}
						}
						
						@Override
						public boolean hasErrors() {
							return false;
						}
						
						@Override
						public String[] getErrorMessages() {
							return new String[0];
						}
						
						@Override
						public String getExpressionText() {
							try {
								return var.getName();
							} catch (DebugException ex) {
								return fExpression;
							}
						}
						
						@Override
						public DebugException getException() {
							return null;
						}
					};
					fListener.watchEvaluationFinished(watchResult);
				} else {
					fListener.watchEvaluationFinished(null);
				}
			} catch (DebugException ex) {
				fListener.watchEvaluationFinished(null);
			}
		}
	}
}
