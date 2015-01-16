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
package org.fusesource.ide.server.fabric8.core.runtime;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.fusesource.ide.server.fabric8.core.Activator;
import org.fusesource.ide.server.fabric8.core.util.IFabric8ToolingConstants;
import org.fusesource.ide.server.karaf.core.runtime.KarafRuntimeDelegate;

/**
 * @author lhein
 */
public class Fabric8RuntimeDelegate extends KarafRuntimeDelegate {
	
	/**
	 * empty default constructor
	 */
	public Fabric8RuntimeDelegate() {
	}
	
	@Override
	public IStatus validate() {
		String id = getRuntime().getRuntimeType().getId();
		String version = getVersion();
		if (version != null && version.trim().startsWith("1.1")) {
			if (!id.toLowerCase().equals(IFabric8ToolingConstants.RUNTIME_FABRIC8_11)) 
				return new Status(Status.ERROR, Activator.PLUGIN_ID, "Runtime type not compatible with found version...");
		} else if (version != null && version.trim().startsWith("1.2")) {
            if (!id.toLowerCase().equals(IFabric8ToolingConstants.RUNTIME_FABRIC8_12)) 
                return new Status(Status.ERROR, Activator.PLUGIN_ID, "Runtime type not compatible with found version...");
        } else {
			return new Status(Status.ERROR, Activator.PLUGIN_ID, "No compatible runtime type found for version " + version + "...");
		}
		
		return Status.OK_STATUS;
	}
}
