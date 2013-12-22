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
package org.fusesource.ide.server.fuse.core.server;

import java.io.File;

import org.fusesource.ide.server.karaf.core.server.KarafServerBehaviourDelegate;

/**
 * @author lhein
 */
public class FuseESBServerBehaviourDelegate extends
		KarafServerBehaviourDelegate {

	protected String getVMArguments(String karafInstallDir) {
		StringBuilder vmArguments = new StringBuilder();

		String endorsedDirs = System.getProperty("java.endorsed.dirs");
		String extDirs = System.getProperty("java.ext.dirs");

		vmArguments.append("-server -Xms128M  -Xmx512M -XX:+UnlockDiagnosticVMOptions -XX:+UnsyncloadClass ");
		vmArguments.append(SPACE + "-XX:PermSize=16M -XX:MaxPermSize=128M ");
		vmArguments.append(SPACE + "-Dcom.sun.management.jmxremote ");
		vmArguments.append(SPACE + "-Djava.endorsed.dirs=" + QUOTE + endorsedDirs + File.pathSeparatorChar + karafInstallDir + SEPARATOR + "lib" + SEPARATOR + "endorsed" + QUOTE);
		vmArguments.append(SPACE + "-Djava.ext.dirs=" + QUOTE + extDirs + File.pathSeparatorChar + karafInstallDir + SEPARATOR + "lib" + SEPARATOR + "ext" + QUOTE);
		vmArguments.append(SPACE + "-Dkaraf.instances=" + QUOTE + karafInstallDir + SEPARATOR + "instances" + QUOTE);
		vmArguments.append(SPACE + "-Dkaraf.home=" + QUOTE + karafInstallDir + QUOTE); 
		vmArguments.append(SPACE + "-Dkaraf.base=" + QUOTE + karafInstallDir + QUOTE);
		vmArguments.append(SPACE + "-Dkaraf.data=" + QUOTE + karafInstallDir + SEPARATOR + "data" + QUOTE);
		vmArguments.append(SPACE + "-Dkaraf.etc=" + QUOTE + karafInstallDir + SEPARATOR + "etc" + QUOTE);
		vmArguments.append(SPACE + "-Djava.io.tmpdir=" + QUOTE + karafInstallDir + SEPARATOR + "data" + SEPARATOR + "tmp" + QUOTE);
		vmArguments.append(SPACE + "-Djava.util.logging.config.file=" + QUOTE + karafInstallDir + SEPARATOR + "etc" + SEPARATOR + "java.util.logging.properties" + QUOTE);
		vmArguments.append(SPACE + "-Dkaraf.startLocalConsole=false");
		vmArguments.append(SPACE + "-Dkaraf.startRemoteShell=true");
			
		return vmArguments.toString();
	}
}
