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
package org.fusesource.ide.launcher.debug.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.sourcelookup.AbstractSourceLookupParticipant;
import org.fusesource.ide.launcher.debug.model.CamelDebugTarget;
import org.fusesource.ide.launcher.debug.model.CamelStackFrame;
import org.fusesource.ide.launcher.debug.util.CamelDebugRegistry;

/**
 * The Camel source lookup participant knows how to translate a 
 * Camel stack frame into a source file name 
 * 
 * @author lhein
 */
public class CamelSourceLookupParticipant extends
		AbstractSourceLookupParticipant {

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.sourcelookup.ISourceLookupParticipant#getSourceName(java.lang.Object)
	 */
	@Override
	public String getSourceName(Object object) throws CoreException {
		if (object instanceof CamelStackFrame) {
			CamelStackFrame stackFrame = (CamelStackFrame)object;
			CamelDebugTarget dt = (CamelDebugTarget) stackFrame.getDebugTarget();
			return CamelDebugRegistry.getInstance().getEntry(dt.getLaunch().getLaunchConfiguration()).getEditorInput().getFile().getName();
		}
		return null;
	}

}
