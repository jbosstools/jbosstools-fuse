/*******************************************************************************
 * Copyright (c) 2022 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.reddeer.utils;

import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardRuntimeType.EAP;
import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardRuntimeType.KARAF;

import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitWhile;
import org.eclipse.reddeer.swt.condition.ShellIsAvailable;
import org.eclipse.reddeer.workbench.core.condition.JobIsRunning;
import org.jboss.tools.fuse.reddeer.SupportedCamelVersions;
import org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardRuntimeType;
import org.osgi.framework.Version;

public class JDKTemplateCompatibleChecker {

	String camelVersion;
	NewFuseIntegrationProjectWizardRuntimeType runtimeType;

	public JDKTemplateCompatibleChecker(NewFuseIntegrationProjectWizardRuntimeType runtimeType, String camelVersion) {
		this.runtimeType = runtimeType;
		this.camelVersion = camelVersion;
	}

	public boolean isJDK11Compatible() {
		Version fuseVersion;
		try {
			fuseVersion = new Version(SupportedCamelVersions.getFuseVersionFromString(camelVersion));
		} catch (Exception e) { // All other Camel versions which do not allow to extract Fuse runtime version
			return false; // <= Fuse 7.1 (e.g. Camel version - 2.18.1.redhat-000026)
		}

		// >= Fuse 7.10
		if(fuseVersion.compareTo(new Version("7.10.0")) >= 0) {
			return true;
		}

		// == Fuse 7.9
		if(fuseVersion.compareTo(new Version("7.9.0")) == 0) {
			if(runtimeType == KARAF) {
				return false;
			} else {
				return true;
			}
		}

		// == Fuse 7.8
		if(fuseVersion.compareTo(new Version("7.8.0")) == 0) {
			if(runtimeType == EAP) {
				return true;
			} else {
				return false;
			}
		}

		// <= Fuse 7.7
		if(fuseVersion.compareTo(new Version("7.7.0")) <= 0) {
			return false;
		}

		return false;
	}

	public boolean isJDK17Compatible() {
		return false;
	}

	public void handleNoStrictlyCompliantJRETemplates(String shell) {
		JDKCheck.handleJDKWarningDialog();
		waitForProjectDependencies(shell);
	}

	public void handleNoStrictlyCompliantJRETemplates(boolean hasJava8, boolean hasJava11, boolean hasJava17, String shell) {
		if(hasJava11) {
			if(!this.isJDK11Compatible()) {
				if(!hasJava8) {
					JDKCheck.handleJDKWarningDialog();
					waitForProjectDependencies(shell);
				}
			}
		}
		if(hasJava17) {
			if(!this.isJDK17Compatible()) {
				if(!hasJava8 || !hasJava11) {
					JDKCheck.handleJDKWarningDialog();
					waitForProjectDependencies(shell);
				}
			}
		}
	}

	private void waitForProjectDependencies(String shell) {
		new WaitWhile(new JobIsRunning(), TimePeriod.LONG);
		new WaitWhile(new ShellIsAvailable(shell), TimePeriod.getCustom(900));
	}

}
