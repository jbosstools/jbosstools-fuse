/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.server.karaf.core.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.jar.Manifest;

import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionResult;
import org.apache.maven.model.Model;
import org.apache.maven.settings.Server;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.IMaven;
import org.eclipse.m2e.core.embedder.IMavenExecutionContext;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.model.IModuleFolder;
import org.eclipse.wst.server.core.model.IModuleResource;
import org.eclipse.wst.server.core.model.ModuleDelegate;
import org.eclipse.wst.server.core.model.ServerBehaviourDelegate;
import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.server.karaf.core.Activator;
import org.fusesource.ide.server.karaf.core.server.BaseConfigPropertyProvider;
import org.jboss.ide.eclipse.as.core.server.bean.ServerBeanLoader;
import org.jboss.ide.eclipse.as.core.server.bean.ServerBeanType;

/**
 * @author lhein
 */
public class KarafUtils {
	
	private static final String BUNDLE_VERSION_STRING = "Bundle-Version=";
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
	public static final String PROTOCOL_PREFIX_JAR 		= "wrap:";
	public static final String PROTOCOL_PREFIX_MAVEN 	= "mvn:";
	public static final String PROTOCOL_PREFIX_OSGI 	= "";
	public static final String PROTOCOL_PREFIX_WEB 		= "war:";
	
	/**
	 * packaging types
	 */
	public static final String PACKAGING_JAR			= "jar";
	public static final String PACKAGING_BUNDLE			= "bundle";
	public static final String PACKAGING_WAR			= "war";
	
	/**
	 * property keys
	 */
	public static final String SERVER_ID				= "fabric8-server-id";
	public static final String SERVER_USER				= "fabric8-server-user";
	public static final String SERVER_PASSWORD			= "fabric8-server-password";
	
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
	public static String getBundleFilePath(final IModule module) throws CoreException {
		final String packaging = getPackaging(module);
		final String artifactId = getArtifactId(module);
		File projectTargetPath = module.getProject().getLocation().append("target").toFile();
		File[] jars = projectTargetPath.listFiles(new FileFilter() {
			/*
			 * (non-Javadoc)
			 * @see java.io.FileFilter#accept(java.io.File)
			 */
			@Override
			public boolean accept(File pathname) {
				return 	pathname.exists() && 
						pathname.isFile() && 
						(pathname.getName().toLowerCase().startsWith(module.getProject().getName().toLowerCase()) || 
						 pathname.getName().toLowerCase().startsWith(artifactId.toLowerCase())) && 
						pathname.getName().toLowerCase().endsWith(getFileExtensionForPackaging(packaging));
			}
		});
		if (jars != null && jars.length>0) {
			if (packaging.equalsIgnoreCase(PACKAGING_BUNDLE)) {
				return String.format("%sfile:%s", getProtocolPrefixForModule(module), jars[0].getPath());
			} else if (packaging.equalsIgnoreCase(PACKAGING_JAR)) {
				return String.format("%sfile:%s$Bundle-SymbolicName=%s&Bundle-Version=%s", getProtocolPrefixForModule(module), jars[0].getPath(), KarafUtils.getBundleSymbolicName(module), getBundleVersion(module, jars[0]));
			} else if (packaging.equalsIgnoreCase(PACKAGING_WAR)) {
				return String.format("%sfile:%s?Bundle-SymbolicName=%s&Bundle-Version=%s", getProtocolPrefixForModule(module), jars[0].getPath(), KarafUtils.getBundleSymbolicName(module), getBundleVersion(module, jars[0]));	
			}			
		}
		return null;
	}

	/**
	 * returns the default file extension for the built artifact of a given packaging type
	 * 
	 * @param packaging
	 * @return
	 */
	private static String getFileExtensionForPackaging(String packaging) {
		if (packaging.equalsIgnoreCase(PACKAGING_BUNDLE) || packaging.equalsIgnoreCase(PACKAGING_JAR)) {
			return ".jar";
		} else if (packaging.equalsIgnoreCase(PACKAGING_WAR)) {
			return ".war";
		} else {
			return ".jar";
		}
	}
	
	/**
	 * returns the protocol prefix for deployments like WRAP: or WAR:
	 * 
	 * @param module
	 * @return
	 */
	private static String getProtocolPrefixForModule(IModule module) throws CoreException {	
		String packaging = getPackaging(module);
		if (Strings.isBlank(packaging) || packaging.equalsIgnoreCase(PACKAGING_JAR)) {
			return PROTOCOL_PREFIX_JAR;
		} else if (packaging.equalsIgnoreCase(PACKAGING_BUNDLE)) {
			return PROTOCOL_PREFIX_OSGI;
		} else if (packaging.equalsIgnoreCase(PACKAGING_WAR)) {
			return PROTOCOL_PREFIX_WEB;
		} else {
			return PROTOCOL_PREFIX_JAR;
		}
	}
	
	/**
	 * returns the packaging of the maven project
	 * 
	 * @param module
	 * @return
	 * @throws CoreException
	 */
	private static String getPackaging(IModule module) throws CoreException {
		Model model = MavenPlugin.getMavenModelManager().readMavenModel(getModelFile(module));
		return model.getPackaging();
	}
	
	private static String getArtifactId(IModule module) throws CoreException {
		Model model = MavenPlugin.getMavenModelManager().readMavenModel(getModelFile(module));
		return model.getArtifactId();
	}
	
	/**
	 * returns a file reference to the maven pom file of the module
	 * @param module
	 * @return
	 */
	public static File getModelFile(IModule module) {
		if (module == null)
			return null;
		return module.getProject().getLocation().append(IMavenConstants.POM_FILE_NAME).toFile();
	}
	
	/**
	 * 
	 * @param goals
	 * @param module
	 * @param monitor
	 * @return
	 * @throws CoreException
	 */
	public static boolean runBuild(List<String> goals, IModule module, IProgressMonitor monitor)  throws CoreException {
		return runBuild(goals, null, module, monitor);
	}
	
	/**
	 * 
	 * @param goals
	 * @param serverProperties
	 * @param module
	 * @param monitor
	 * @return
	 * @throws CoreException
	 */
	public static boolean runBuild(List<String> goals, Properties serverProperties, IModule module, IProgressMonitor monitor)  throws CoreException {
		IMaven maven = MavenPlugin.getMaven();
		IMavenExecutionContext executionContext = maven.createExecutionContext();
		MavenExecutionRequest executionRequest = executionContext.getExecutionRequest();
		executionRequest.setPom(getModelFile(module));
		if (serverProperties != null && serverProperties.isEmpty() == false) {
			Server fabric8Server = new Server();
			fabric8Server.setId(serverProperties.getProperty(SERVER_ID));
			fabric8Server.setUsername(serverProperties.getProperty(SERVER_USER));
			fabric8Server.setPassword(serverProperties.getProperty(SERVER_PASSWORD));
			executionRequest.addServer(fabric8Server);
		}
		executionRequest.setGoals(goals);
		
		MavenExecutionResult result = maven.execute(executionRequest, monitor);
		for (Throwable t : result.getExceptions()) {
			Activator.getLogger().error(t);
		}
		return !result.hasExceptions();
	}

	public static String getBundleSymbolicName(IModule module) throws CoreException {
		String symbolicName;
		
		if (module == null || module.getProject() == null)
			return "";
		
		IFile manifest = module.getProject().getFile("target/classes/META-INF/MANIFEST.MF");
		if (!manifest.exists()) {
			manifest = module.getProject().getFile("META-INF/MANIFEST.MF");
		}
		if (manifest.exists()) {
			try {
				Manifest mf = new Manifest(new FileInputStream(manifest.getLocation().toFile()));
				symbolicName = mf.getMainAttributes().getValue("Bundle-SymbolicName");
				symbolicName = stripParametersFromSymbolicName(symbolicName);
			} catch (IOException ex) {
				symbolicName = null;
			}
		} else {
			// no OSGi bundle - lets take the project name instead
			symbolicName = null;
		}
		
		if (symbolicName == null) {
			// no manifest - so grab the artifactId
			symbolicName = getArtifactId(module);
		}
		
		if (symbolicName == null) {
			symbolicName = module.getId();
		}
		
		return symbolicName;
	}
	
	public static String stripParametersFromSymbolicName(String symbolicName) {
		String resVal = symbolicName;
		if (symbolicName != null) {
			// sometimes parameters are added to the symbolic name - we should ignore them
			int paramIdx = symbolicName.indexOf(';');
			if (paramIdx != -1) {
				resVal = symbolicName.substring(0, paramIdx);
			}
		}
		return resVal;
	}
	
	/**
	 * retrieves the bundle version from the given manifest file
	 * 
	 * @param manifest
	 * @return
	 */
	public static String getBundleVersionFromManifest(File manifest) {
		String version;
		
		if (manifest.exists()) {
			try {
				Manifest mf = new Manifest(new FileInputStream(manifest));
				version = mf.getMainAttributes().getValue("Bundle-Version");
			} catch (IOException ex) {
				version = null;
			}
		} else {
			// no OSGi bundle - lets take the project name instead
			version = null;
		}
		
		return version;
	}
	
	/**
	 * parses the file name for the version
	 * 
	 * @param f
	 * @param packaging
	 * @return
	 */
	public static String getBundleVersionFromFileName(File f, String packaging) {
		String version = "";
		String[] parts = f.getName().split("-");
		for (String part : parts) {
			if (!Character.isDigit(part.charAt(0))) {
				if (version.isEmpty()) continue;
				version += "." + part;
			}
			version += part.trim();
		}
		version = version.substring(0, version.indexOf(getFileExtensionForPackaging(packaging)));
		
		return version;
	}
	
	/**
	 * searches the Manifest.mf file in the module
	 * 
	 * @param module
	 * @return
	 */
	public static File findManifest(IModule module) throws CoreException {
		ModuleDelegate md = (ModuleDelegate)module.loadAdapter(ModuleDelegate.class, null);
		IModuleResource[] res = md.members();
		for( int i = 0; i < res.length; i++ ) {
			if( "META-INF".equals(res[i].getName())) {
				IModuleResource meta = res[i];
				if( meta instanceof IModuleFolder) {
					IModuleResource[] metaContents = ((IModuleFolder)meta).members();
					for( int j = 0; j < metaContents.length; j++ ) {
						if( "manifest.mf".equalsIgnoreCase(metaContents[j].getName())) {
							IModuleResource mf = metaContents[j];
							return mf.getAdapter(File.class);
						}
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * retrieves the version from the install uri
	 * 
	 * @param uri
	 * @param packaging
	 * @return
	 */
	public static String getBundleVersionFromURI(String uri, String packaging) {
		String version = null;
		
		if (uri != null && uri.indexOf(BUNDLE_VERSION_STRING) != -1) {
			version = uri.substring(uri.indexOf(BUNDLE_VERSION_STRING) + BUNDLE_VERSION_STRING.length());
		} else if (uri != null && uri.endsWith(KarafUtils.getFileExtensionForPackaging(packaging))) {
			String s = uri.substring(uri.lastIndexOf(File.separatorChar)+1);
			String pack = KarafUtils.getFileExtensionForPackaging(packaging);
			boolean versionDigitsFound = false;
			int pointCount = 0;
			version = "";
			for (int i=s.length()-pack.length()-1; i>=0; i--) {
				char c = s.charAt(i);
				if (Character.isAlphabetic(c)) {
					if (versionDigitsFound) break;
				} else if (Character.isDigit(c)) {
					versionDigitsFound = true;
				} else if (c == '-') {
					if (versionDigitsFound) break;
				} else if (c == '.') {
					pointCount++;
					if (pointCount>2) break;
				}
				version = c + version;
			}
			// finally replace a possible - with a . to be OSGi compliant
			if (version.indexOf('-') != -1) {
				version = version.replaceAll("-", ".");
			}
		}
		
		return version;
	}
	
	/**
	 * 
	 * @param module
	 * @param f
	 * @return
	 */
	public static String getBundleVersion(IModule module, File f) throws CoreException {
		if (module == null)
			return "";
		
		String version;
		String packaging = getPackaging(module);
		
		File manifest = findManifest(module);
		if (manifest == null || !manifest.exists()) {
			manifest = module.getProject().getFile("META-INF/MANIFEST.MF").getLocation().toFile();
		}

		// retrieve the version from the found manifest.mf file
		version = getBundleVersionFromManifest(manifest);
		
		// if that fails...
		if (version == null) {
			// no manifest - so grab it from the file name
			if (f != null) {
				version = getBundleVersionFromFileName(f, packaging);
			} else {
				// no file...parse it from the bundle url
				String uri = getBundleFilePath(module);
				version = getBundleVersionFromURI(uri, packaging);
			}
		}
		
		return version;
	}

	/**
	 * retrieve all needed information to connect to JMX server
	 * @param server
	 * @return
	 */
	public static String getJMXConnectionURL(IServer server) {
		StringBuilder retVal = new StringBuilder();
		BaseConfigPropertyProvider manProv = new BaseConfigPropertyProvider(server.getRuntime().getLocation().append("etc").append("org.apache.karaf.management.cfg").toFile());
		BaseConfigPropertyProvider sysProv = new BaseConfigPropertyProvider(server.getRuntime().getLocation().append("etc").append("system.properties").toFile());
		
		String url = manProv.getConfigurationProperty("serviceUrl");
		if (url == null) {
			return null;
		}
		url = url.trim();
		int pos;
		while ((pos = url.indexOf("${")) != -1) {
			retVal.append(url.substring(0, pos));
			String placeHolder = url.substring(url.indexOf("${")+2, url.indexOf('}')).trim();
			String replacement = manProv.getConfigurationProperty(placeHolder);
			if (replacement == null) {
				replacement = sysProv.getConfigurationProperty(placeHolder);
			}
			if (replacement == null) {
				return null;
			} else {
				retVal.append(replacement.trim());
				url = url.substring(pos + placeHolder.length() + 3);
			}
		}
		if (retVal.length()<1) {
			retVal.append(url);
		}
		return retVal.toString();
	}
}
