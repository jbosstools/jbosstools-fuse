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

package org.fusesource.ide.camel.model.io;


import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.fusesource.ide.camel.model.RouteContainer;
import org.fusesource.ide.foundation.core.util.ResourceModelUtils;


public abstract class ContainerMarshallerSupport implements ContainerMarshaler  {

	public ContainerMarshallerSupport() {
		super();
	}

	@Override
	public RouteContainer loadRoutes(IFile ifile) {
		return loadRoutes(ResourceModelUtils.toFile(ifile));
	}

	public void save(IFile ifile, RouteContainer model) throws CoreException {
		save(ifile, model, new NullProgressMonitor());
	}
	
}