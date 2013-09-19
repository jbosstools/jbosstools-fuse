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

package org.fusesource.ide.server.karaf.core.runtime;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.server.core.model.RuntimeDelegate;
import org.fusesource.ide.server.karaf.core.Activator;

/**
 * @author lhein
 */
public class KarafRuntimeDelegate extends RuntimeDelegate implements IKarafRuntimeWorkingCopy {

	/**
	 * empty default constructor
	 */
	public KarafRuntimeDelegate() {
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.server.karaf.core.runtime.IKarafRuntime#getKarafInstallDir()
	 */
	public String getKarafInstallDir() {
		return getAttribute(IKarafRuntime.INSTALL_DIR, "");
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.server.karaf.core.runtime.IKarafRuntime#getKarafPropertiesFileLocation()
	 */
	public String getKarafPropertiesFileLocation() {
		return getAttribute(IKarafRuntime.PROPERTIES_FILE_LOC, "");
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.server.karaf.core.runtime.IKarafRuntime#getKarafVersion()
	 */
	@Override
	public String getKarafVersion() {
		return getAttribute(IKarafRuntime.VERSION, "");
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.server.karaf.core.runtime.IKarafRuntimeWorkingCopy#setKarafInstallDir(java.lang.String)
	 */
	public void setKarafInstallDir(String installDir) {
		setAttribute(IKarafRuntime.INSTALL_DIR, installDir);
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.server.karaf.core.runtime.IKarafRuntimeWorkingCopy#setKarafPropertiesFileLocation(java.lang.String)
	 */
	public void setKarafPropertiesFileLocation(String propFile) {
		setAttribute(IKarafRuntime.PROPERTIES_FILE_LOC, propFile);
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.server.karaf.core.runtime.IKarafRuntimeWorkingCopy#setKarafVersion(java.lang.String)
	 */
	@Override
	public void setKarafVersion(String version) {
		setAttribute(IKarafRuntime.VERSION, version);
	}
	
	@Override
	public IStatus validate() {
		IStatus status = super.validate();
		if (!status.isOK())
			return status;

		String id = getRuntime().getRuntimeType().getId();
		String version = getKarafVersion();
		if (version != null && version.trim().startsWith("2.0")) {
			if (!id.toLowerCase().endsWith("karaf.runtime.20")) return new Status(Status.ERROR, Activator.PLUGIN_ID, "Runtime type not compatible with found version...");
		} else if (version != null && version.trim().startsWith("2.1")) {
			if (!id.toLowerCase().endsWith("karaf.runtime.21")) return new Status(Status.ERROR, Activator.PLUGIN_ID, "Runtime type not compatible with found version...");
		} else if (version != null && version.trim().startsWith("2.2")) {
			if (!id.toLowerCase().endsWith("karaf.runtime.22")) return new Status(Status.ERROR, Activator.PLUGIN_ID, "Runtime type not compatible with found version...");
		} else if (version != null && version.trim().startsWith("2.3")) {
			if (!id.toLowerCase().endsWith("karaf.runtime.23")) return new Status(Status.ERROR, Activator.PLUGIN_ID, "Runtime type not compatible with found version...");
		} else {
			return new Status(Status.ERROR, Activator.PLUGIN_ID, "No compatible runtime type found for version " + version + "...");
		}
		
		return Status.OK_STATUS;
	}
}
