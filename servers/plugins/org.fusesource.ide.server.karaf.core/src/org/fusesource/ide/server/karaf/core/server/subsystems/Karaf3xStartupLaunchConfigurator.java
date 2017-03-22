/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.server.karaf.core.server.subsystems;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.server.core.IServer;
import org.fusesource.ide.server.karaf.core.runtime.IKarafRuntime;

public class Karaf3xStartupLaunchConfigurator extends Karaf2xStartupLaunchConfigurator {
	

	public Karaf3xStartupLaunchConfigurator(IServer server)
			throws CoreException {
		super(server);
	}
	
	@Override
	protected String getVMArguments(String karafInstallDir) {
		return get3xVMArguments(karafInstallDir);
	}
	
	protected String get2xVMArguments(String karafInstallDir) {
		return super.getVMArguments(karafInstallDir);
	}	
	
	protected String get3xVMArguments(String karafInstallDir) {
		StringBuilder vmArguments = new StringBuilder();

		String endorsedDirs = System.getProperty("java.endorsed.dirs");
		String extDirs = System.getProperty("java.ext.dirs");
		
		if (server.getRuntime() != null) {
			IKarafRuntime runtime = (IKarafRuntime)server.getRuntime().loadAdapter(IKarafRuntime.class, null);
			File vmLoc = runtime.getVM().getInstallLocation();
			
//			JAVA_ENDORSED_DIRS="${JAVA_HOME}/jre/lib/endorsed:${JAVA_HOME}/lib/endorsed:${KARAF_HOME}/lib/endorsed"
			endorsedDirs = String.format("%s%sjre%slib%sendorsed%s%s%slib%sendorsed%s%s%slib%sendorsed", 
										vmLoc.getPath(), SEPARATOR, SEPARATOR, SEPARATOR,
										File.pathSeparator, 
										vmLoc.getPath(), SEPARATOR, SEPARATOR,
										File.pathSeparator,
										karafInstallDir, SEPARATOR, SEPARATOR);
//		    JAVA_EXT_DIRS="${JAVA_HOME}/jre/lib/ext:${JAVA_HOME}/lib/ext:${KARAF_HOME}/lib/ext"
			extDirs = String.format("%s%sjre%slib%sext%s%s%slib%sext%s%s%slib%sext", 
					vmLoc.getPath(), SEPARATOR, SEPARATOR, SEPARATOR,
					File.pathSeparator, 
					vmLoc.getPath(), SEPARATOR, SEPARATOR,
					File.pathSeparator,
					karafInstallDir, SEPARATOR, SEPARATOR);
		}
		
		vmArguments.append("-Xms128M");
		vmArguments.append(SPACE + "-Xmx512M");
		vmArguments.append(SPACE + "-XX:+UnlockDiagnosticVMOptions");
		vmArguments.append(SPACE + "-XX:+UnsyncloadClass");
		vmArguments.append(SPACE + "-server ");
		vmArguments.append(SPACE + "-Dcom.sun.management.jmxremote");
		vmArguments.append(SPACE + "-Djava.endorsed.dirs=" + QUOTE + endorsedDirs + QUOTE);
		vmArguments.append(SPACE + "-Djava.ext.dirs=" + QUOTE + extDirs + QUOTE);
		vmArguments.append(SPACE + "-Dkaraf.startLocalConsole=false");
		vmArguments.append(SPACE + "-Dkaraf.startRemoteShell=true");
		vmArguments.append(SPACE + "-Dkaraf.home=" + QUOTE + karafInstallDir + QUOTE); 
		vmArguments.append(SPACE + "-Dkaraf.base=" + QUOTE + karafInstallDir + QUOTE);
		vmArguments.append(SPACE + "-Dkaraf.instances=" + QUOTE + karafInstallDir + SEPARATOR + "instances" + QUOTE);
		vmArguments.append(SPACE + "-Dkaraf.data=" + QUOTE + karafInstallDir + SEPARATOR + "data" + QUOTE);
		vmArguments.append(SPACE + "-Dkaraf.etc=" + QUOTE + karafInstallDir + SEPARATOR + "etc" + QUOTE);
		vmArguments.append(SPACE + "-Djava.io.tmpdir=" + QUOTE + karafInstallDir + SEPARATOR + "data" + SEPARATOR + "tmp" + QUOTE);
		vmArguments.append(SPACE + "-Djava.util.logging.config.file=" + QUOTE + karafInstallDir + SEPARATOR + "etc" + SEPARATOR + "java.util.logging.properties" + QUOTE);
		return vmArguments.toString();
	}
}