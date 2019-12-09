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
package org.fusesource.ide.launcher.debug.launching;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.sourcelookup.AbstractSourceLookupParticipant;
import org.fusesource.ide.launcher.debug.model.CamelStackFrame;

/**
 * The Camel source lookup participant knows how to translate a 
 * Camel stack frame into a source file name 
 * 
 * @author lhein
 */
public class CamelSourceLookupParticipant extends AbstractSourceLookupParticipant {

	@Override
	public String getSourceName(Object object) throws CoreException {
		if (object instanceof CamelStackFrame) {
			CamelStackFrame stackFrame = (CamelStackFrame)object;
			File contextFile = stackFrame.getContextFile();
			return contextFile.getName();
		}
		return null;
	}

}
