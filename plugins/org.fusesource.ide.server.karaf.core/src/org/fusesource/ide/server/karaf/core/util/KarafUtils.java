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

package org.fusesource.ide.server.karaf.core.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.jar.Manifest;

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.model.ServerBehaviourDelegate;
import org.fusesource.ide.server.karaf.core.server.BaseConfigPropertyProvider;
import org.jboss.ide.eclipse.as.core.server.bean.ServerBeanLoader;
import org.jboss.ide.eclipse.as.core.server.bean.ServerBeanType;

/**
 * @author lhein
 */
public class KarafUtils {
	
	/**
	 * A constant representing that no publish is required. 
	 * This constant is different from the wtp constants in that
	 * this constant is used after taking into account 
	 * the server flags of kind and deltaKind, as well as the module restart state,
	 * to come to a conclusion of what a publisher needs to do.
	 */
	public static final int NO_PUBLISH = 0;
	/**
	 * A constant representing that an incremental publish is required. 
	 * This constant is different from the wtp constants in that
	 * this constant is used after taking into account 
	 * the server flags of kind and deltaKind, as well as the module restart state,
	 * to come to a conclusion of what a publisher needs to do.
	 */
	public static final int INCREMENTAL_PUBLISH = 1;
	/**
	 * A constant representing that a full publish is required. 
	 * This constant is different from the wtp constants in that
	 * this constant is used after taking into account 
	 * the server flags of kind and deltaKind, as well as the module restart state,
	 * to come to a conclusion of what a publisher needs to do.
	 */
	public static final int FULL_PUBLISH = 2;
	
	/**
	 * A constant representing that a removal-type publish is required. 
	 * This constant is different from the wtp constants in that
	 * this constant is used after taking into account 
	 * the server flags of kind and deltaKind, as well as the module restart state,
	 * to come to a conclusion of what a publisher needs to do.
	 */
	public static final int REMOVE_PUBLISH = 3;
	
	/**
	 * the protocol prefix for deployments
	 */
	public static final String PROTOCOL_WRAP = "wrap:";
	
	/**
	 * retrieves the version of the runtime installation
	 * 
	 * @param installFolder	the installation folder
	 * @return	the version string or null on errors
	 */
	public static String getVersion(File installFolder) {
		ServerBeanLoader loader = new ServerBeanLoader(installFolder);
		if( loader.getServerBeanType() != ServerBeanType.UNKNOWN) {
			return loader.getFullServerVersion();
		}
		return null;
	}
	
	/**
	 * Given the various flags, return which of the following options 
	 * our publishers should perform:
	 *    1) A full publish
	 *    2) A removed publish (remove the module)
	 *    3) An incremental publish, or
	 *    4) No publish at all. 
	 * @param module
	 * @param kind
	 * @param deltaKind
	 * @return
	 */
	public static int getPublishType(IServer server, IModule[] module, int kind, int deltaKind) {
		int modulePublishState = server.getModulePublishState(module);
		if( deltaKind == ServerBehaviourDelegate.ADDED ) 
			return FULL_PUBLISH;
		else if (deltaKind == ServerBehaviourDelegate.REMOVED) {
			return REMOVE_PUBLISH;
		} else if (kind == IServer.PUBLISH_FULL 
				|| modulePublishState == IServer.PUBLISH_STATE_FULL 
				|| kind == IServer.PUBLISH_CLEAN ) {
			return FULL_PUBLISH;
		} else if (kind == IServer.PUBLISH_INCREMENTAL 
				|| modulePublishState == IServer.PUBLISH_STATE_INCREMENTAL 
				|| kind == IServer.PUBLISH_AUTO) {
			if( ServerBehaviourDelegate.CHANGED == deltaKind ) 
				return INCREMENTAL_PUBLISH;
		} 
		return NO_PUBLISH;
	}
	
	/**
	 * 
	 * @param module
	 * @return
	 */
	public static String getBundleFilePath(final IModule module) {
		File projectTargetPath = module.getProject().getLocation().append("target").toFile();
		File[] jars = projectTargetPath.listFiles(new FileFilter() {
			/*
			 * (non-Javadoc)
			 * @see java.io.FileFilter#accept(java.io.File)
			 */
			@Override
			public boolean accept(File pathname) {
				return pathname.exists() && pathname.isFile() && pathname.getName().toLowerCase().startsWith(module.getProject().getName()) && pathname.getName().toLowerCase().endsWith(".jar");
			}
		});
		if (jars.length>0) {
			return String.format("%sfile:%s$Bundle-SymbolicName=%s&Bundle-Version=%s", PROTOCOL_WRAP, jars[0].getPath(), module.getProject().getName(), getBundleVersion(module, jars[0]));
		}
		return null;
	}

	/**
	 * 
	 * @param module
	 * @param f
	 * @return
	 */
	public static String getBundleVersion(IModule module, File f) {
		String version = null;
		IFile manifest = module.getProject().getFile("target/classes/META-INF/MANIFEST.MF");
		if (!manifest.exists()) {
			manifest = module.getProject().getFile("META-INF/MANIFEST.MF");
		}
		if (manifest.exists()) {
			try {
				Manifest mf = new Manifest(new FileInputStream(manifest.getLocation().toFile()));
				version = mf.getMainAttributes().getValue("Bundle-Version");
			} catch (IOException ex) {
				version = null;
			}
		} else {
			// no OSGi bundle - lets take the project name instead
			version = null;
		}

		// no manifest - so grab it from the file name
		if (f != null) {
			if (version == null) {
				version = "";
				String[] parts = f.getName().split("-");
				for (String part : parts) {
					if (!Character.isDigit(part.charAt(0))) {
						if (version.length()==0) continue;
						version += "." + part;
					}
					version += part.trim();
				}
				version = version.substring(0, version.indexOf(".jar"));
			}
		} else {
			// no file...parse it from the bundle url
			String uri = getBundleFilePath(module);
			version = uri.substring(uri.indexOf("Bundle-Version=") + "Bundle-Version=".length());
		}
		
		return version;
	}

	/**
	 * retrieve all needed information to connect to JMX server
	 * @param server
	 * @return
	 */
	public static String getJMXConnectionURL(IServer server) {
		String retVal = "";
		BaseConfigPropertyProvider manProv = new BaseConfigPropertyProvider(server.getRuntime().getLocation().append("etc").append("org.apache.karaf.management.cfg").toFile());
		BaseConfigPropertyProvider sysProv = new BaseConfigPropertyProvider(server.getRuntime().getLocation().append("etc").append("system.properties").toFile());
		
		String url = manProv.getConfigurationProperty("serviceUrl");
		if (url == null) return null;
		url = url.trim();
		int pos = -1;
		while ((pos = url.indexOf("${")) != -1) {
			retVal += url.substring(0, pos);
			String placeHolder = url.substring(url.indexOf("${")+2, url.indexOf("}")).trim();
			String replacement = manProv.getConfigurationProperty(placeHolder);
			if (replacement == null) {
				replacement = sysProv.getConfigurationProperty(placeHolder);
			}
			if (replacement == null) {
				return null;
			} else {
				retVal += replacement.trim();
				url = url.substring(pos + placeHolder.length() + 3);
			}
		}
		return retVal;
	}
}
