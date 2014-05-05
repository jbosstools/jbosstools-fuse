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
package org.fusesource.ide.server.fuse.core.runtime;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.fusesource.ide.server.fuse.core.Activator;
import org.fusesource.ide.server.karaf.core.runtime.KarafRuntimeDelegate;

/**
 * @author lhein
 */
public class FuseESBRuntimeDelegate extends KarafRuntimeDelegate {
	
	/**
	 * empty default constructor
	 */
	public FuseESBRuntimeDelegate() {
	}
	
	@Override
	public IStatus validate() {
		String id = getRuntime().getRuntimeType().getId();
		String version = getVersion();
		if (version != null && version.trim().startsWith("6.0")) {
			if (!id.toLowerCase().endsWith("fuseesb.runtime.60")) return new Status(Status.ERROR, Activator.PLUGIN_ID, "Runtime type not compatible with found version...");
		} else if (version != null && version.trim().startsWith("6.1")) {
			if (!id.toLowerCase().endsWith("fuseesb.runtime.61")) return new Status(Status.ERROR, Activator.PLUGIN_ID, "Runtime type not compatible with found version...");
		} else {
			return new Status(Status.ERROR, Activator.PLUGIN_ID, "No compatible runtime type found for version " + version + "...");
		}
		
		return Status.OK_STATUS;
	}
}
