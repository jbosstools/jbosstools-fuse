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
		String version = getKarafVersion();
		if (version != null && version.trim().startsWith("4.0")) {
			if (!id.toLowerCase().endsWith("smx.runtime.40")) return new Status(Status.ERROR, Activator.PLUGIN_ID, "Runtime type not compatible with found version...");
		} else if (version != null && version.trim().startsWith("4.2")) {
			if (!id.toLowerCase().endsWith("smx.runtime.42")) return new Status(Status.ERROR, Activator.PLUGIN_ID, "Runtime type not compatible with found version...");
		} else if (version != null && version.trim().startsWith("4.3")) {
			if (!id.toLowerCase().endsWith("smx.runtime.43")) return new Status(Status.ERROR, Activator.PLUGIN_ID, "Runtime type not compatible with found version...");
		} else if (version != null && version.trim().startsWith("4.4")) {
			if (!id.toLowerCase().endsWith("smx.runtime.44")) return new Status(Status.ERROR, Activator.PLUGIN_ID, "Runtime type not compatible with found version...");
		} else if (version != null && version.trim().startsWith("4.5")) {
			if (!id.toLowerCase().endsWith("smx.runtime.45")) return new Status(Status.ERROR, Activator.PLUGIN_ID, "Runtime type not compatible with found version...");
		} else {
			return new Status(Status.ERROR, Activator.PLUGIN_ID, "No compatible runtime type found for version " + version + "...");
		}
		
		return Status.OK_STATUS;
	}
}
