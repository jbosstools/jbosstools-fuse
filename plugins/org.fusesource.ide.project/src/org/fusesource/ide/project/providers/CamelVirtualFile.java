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
package org.fusesource.ide.project.providers;

import org.eclipse.core.internal.resources.File;
import org.eclipse.core.internal.resources.Workspace;

public class CamelVirtualFile extends File {

	/**
	 * 
	 */
	public CamelVirtualFile(File file) {
		super(file.getFullPath(), (Workspace)file.getWorkspace());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.resources.Resource#getName()
	 */
	@Override
	public String getName() {
		return getFullPath().toString();
	}
}