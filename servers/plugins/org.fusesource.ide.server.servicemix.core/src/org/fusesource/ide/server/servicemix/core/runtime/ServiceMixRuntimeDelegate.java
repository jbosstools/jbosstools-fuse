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
package org.fusesource.ide.server.servicemix.core.runtime;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.fusesource.ide.server.karaf.core.runtime.KarafRuntimeDelegate;
import org.fusesource.ide.server.servicemix.core.Activator;
import org.fusesource.ide.server.servicemix.core.util.IServiceMixToolingConstants;

/**
 * @author lhein
 */
public class ServiceMixRuntimeDelegate extends KarafRuntimeDelegate {
	
	/**
	 * empty default constructor
	 */
	public ServiceMixRuntimeDelegate() {
	}
	
	@Override
	public IStatus validate() {
		String id = getRuntime().getRuntimeType().getId();
		String version = getVersion();
		if (version != null && version.trim().startsWith("4.5")) {
			if (!id.toLowerCase().equals(IServiceMixToolingConstants.RUNTIME_SMX_45)) 
				return new Status(Status.ERROR, Activator.PLUGIN_ID, "Runtime type not compatible with found version...");
		} else if (version != null && version.trim().startsWith("5.0")) {
			if (!id.toLowerCase().equals(IServiceMixToolingConstants.RUNTIME_SMX_50)) 
				return new Status(Status.ERROR, Activator.PLUGIN_ID, "Runtime type not compatible with found version...");
		} else if (version != null && version.trim().startsWith("5.1")) {
            if (!id.toLowerCase().equals(IServiceMixToolingConstants.RUNTIME_SMX_51)) 
                return new Status(Status.ERROR, Activator.PLUGIN_ID, "Runtime type not compatible with found version...");
		} else if (version != null && version.trim().startsWith("5.2")) {
            if (!id.toLowerCase().equals(IServiceMixToolingConstants.RUNTIME_SMX_52)) 
                return new Status(Status.ERROR, Activator.PLUGIN_ID, "Runtime type not compatible with found version...");
		} else if (version != null && version.trim().startsWith("5.3")) {
            if (!id.toLowerCase().equals(IServiceMixToolingConstants.RUNTIME_SMX_53)) 
                return new Status(Status.ERROR, Activator.PLUGIN_ID, "Runtime type not compatible with found version...");
		} else if (version != null && version.trim().startsWith("5.4")) {
            if (!id.toLowerCase().equals(IServiceMixToolingConstants.RUNTIME_SMX_54)) 
                return new Status(Status.ERROR, Activator.PLUGIN_ID, "Runtime type not compatible with found version...");
        } else {
			return new Status(Status.ERROR, Activator.PLUGIN_ID, "No compatible runtime type found for version " + version + "...");
		}
		
		return Status.OK_STATUS;
	}
}
