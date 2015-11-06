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
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionResult;
import org.apache.maven.model.Model;
import org.apache.maven.settings.Server;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.IMaven;
import org.eclipse.m2e.core.embedder.IMavenExecutionContext;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.model.ServerBehaviourDelegate;
import org.fusesource.ide.commons.util.Strings;
import org.fusesource.ide.server.karaf.core.Activator;
import org.fusesource.ide.server.karaf.core.server.BaseConfigPropertyProvider;
import org.fusesource.ide.server.karaf.core.server.subsystems.Karaf2xPublishController;
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
	public static final String SERVER_ID				= "fuse-server-id";
	public static final String SERVER_USER				= "fuse-server-user";
	public static final String SERVER_PASSWORD			= "fuse-server-password";
	
	/**
	 * variable substitution
	 */
	public static final String VAR_GROUP_ID				= "${groupId}";
	public static final String VAR_PROJECT_GROUP_ID  	= "${project.groupId}";
	public static final String VAR_ARTIFACT_ID			= "${artifactId}";
	public static final String VAR_PROJECT_ARTIFACT_ID  = "${project.artifactId}";
	public static final String VAR_VERSION				= "${version}";
	public static final String VAR_PROJECT_VERSION  	= "${project.version}";
	
	public static final String[] VARIABLES = new String[] {
			VAR_GROUP_ID,
			VAR_PROJECT_GROUP_ID,
			VAR_ARTIFACT_ID,
			VAR_PROJECT_ARTIFACT_ID,
			VAR_VERSION,
			VAR_PROJECT_VERSION
	};
	
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
		String packaging = getPackaging(module);
		String artifactPath = getFullArtifactPath(module, packaging);
		
		if (packaging.equalsIgnoreCase(PACKAGING_BUNDLE)) {
			return String.format("%sfile:%s", getProtocolPrefixForModule(module), artifactPath);
		} else if (packaging.equalsIgnoreCase(PACKAGING_JAR)) {
			return String.format("%sfile:%s$Bundle-SymbolicName=%s&Bundle-Version=%s", getProtocolPrefixForModule(module), artifactPath, KarafUtils.getBundleSymbolicName(module), getBundleVersion(module, new File(artifactPath)));
		} else if (packaging.equalsIgnoreCase(PACKAGING_WAR)) {
			return String.format("%sfile:%s?Bundle-SymbolicName=%s&Bundle-Version=%s", getProtocolPrefixForModule(module), artifactPath, KarafUtils.getBundleSymbolicName(module), getBundleVersion(module, new File(artifactPath)));	
		}			

		return null;
	}

	/**
	 * checks whether the projects build artifact already exists
	 * 
	 * @param module
	 * @param packaging
	 * @return
	 * @throws CoreException
	 */
	private static File[] getOutputArtifacts(final IModule module, final String packaging) throws CoreException {
		File projectTargetPath = module.getProject().getLocation().append(getOutputFilePath(module)).toFile();
		String finalName = null;
		try {
			finalName = getOutputFilename(module);
		} catch (CoreException ex) {
			Activator.getLogger().error(ex);
		}

		final String artName = finalName;
		File[] files = projectTargetPath.listFiles(new FileFilter() {
			/*
			 * (non-Javadoc)
			 * @see java.io.FileFilter#accept(java.io.File)
			 */
			@Override
			public boolean accept(File pathname) {
				return pathname.getName().startsWith(artName) && pathname.getName().endsWith(getFileExtensionForPackaging(packaging));
			}
		});
		
		// lets sort -> shortest filenames first
		Arrays.sort(files, new Comparator<File>() {
			/* (non-Javadoc)
			 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
			 */
			@Override
			public int compare(File o1, File o2) {
				return o1.getName().length()-o2.getName().length();
			}
		});
		
		return files;
	}
	
	/**
	 * returns the full path of the build artifact
	 * 
	 * @param module
	 * @param packaging
	 * @return
	 * @throws CoreException
	 */
	public static String getFullArtifactPath(final IModule module, final String packaging) throws CoreException {
		String artifactPath = null;

		File[] files = getOutputArtifacts(module, packaging);
		if (files != null && files.length>0) {
			artifactPath = files[0].getPath();
		}
		
		return artifactPath;
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
	
	private static String getOutputFilename(IModule module) throws CoreException {
		Model model = MavenPlugin.getMavenModelManager().readMavenModel(getModelFile(module));
		String finalName = model.getBuild().getFinalName() != null ? model.getBuild().getFinalName() : getArtifactId(module);
		finalName = substituteVariables(finalName, model);
		return finalName;
	}
	
	private static String substituteVariables(String originalString, Model model) {
		String returnValue = "";
		
		for (String var : VARIABLES) {
			int pos = originalString.indexOf(var);
			int len = var.length();
			if (pos != -1) {
				returnValue = originalString.substring(0, pos);
				
				if (var.equals(VAR_GROUP_ID) 	|| var.equals(VAR_PROJECT_GROUP_ID)) 	returnValue += model.getGroupId();
				if (var.equals(VAR_ARTIFACT_ID) || var.equals(VAR_PROJECT_ARTIFACT_ID)) returnValue += model.getArtifactId();
				if (var.equals(VAR_VERSION) 	|| var.equals(VAR_PROJECT_VERSION)) 	returnValue += model.getVersion();
				
				returnValue += originalString.substring(pos+len);
			}
		}
		
		return returnValue;
	}
	
	private static String getOutputFilePath(IModule module) throws CoreException {
		Model model = MavenPlugin.getMavenModelManager().readMavenModel(getModelFile(module));
		return model.getBuild().getOutputDirectory() != null ? model.getBuild().getOutputDirectory() : "target/";
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
			Server server = new Server();
			server.setId(serverProperties.getProperty(SERVER_ID));
			server.setUsername(serverProperties.getProperty(SERVER_USER));
			server.setPassword(serverProperties.getProperty(SERVER_PASSWORD));
			executionRequest.addServer(server);
		}
		executionRequest.setGoals(goals);
		
		MavenExecutionResult result = maven.execute(executionRequest, monitor);
		for (Throwable t : result.getExceptions()) {
			Activator.getLogger().error(t);
		}
		return !result.hasExceptions();
	}

	/**
	 * retrieves the given attribute from the manifest
	 * 
	 * @param module
	 * @param attributeName
	 * @return
	 * @throws CoreException
	 */
	private static String getManifestValue(final IModule module, final String attributeName) throws CoreException {
		String value = null;
		
		// 2 possible ways possible
		// a) we have a manifest.mf file already and can read info from there
		IFile manifest = module.getProject().getFile("target/classes/META-INF/MANIFEST.MF");
		if (!manifest.exists()) {
			manifest = module.getProject().getFile("META-INF/MANIFEST.MF");
		}
		if (manifest.exists()) {
			try {
				Manifest mf = new Manifest(new FileInputStream(manifest.getLocation().toFile()));
				value = mf.getMainAttributes().getValue(attributeName);
			} catch (IOException ex) {
				value = null;
			}
		}
		
		// b) manifest is generated by felix plugin on build -> we need to evaluate pom and/or build the project to extract the manifest
		if (value == null) {
			// now get the manifest and read the symbolic name
			String packaging = getPackaging(module);
			String artifactPath = getFullArtifactPath(module, packaging);
			File f = new File(artifactPath);
			if (!f.exists() || !f.isFile()) {
				// we need to build the project to get the built artifact
				KarafUtils.runBuild(Karaf2xPublishController.GOALS, module, new NullProgressMonitor());
			}
			if (f.exists() && f.isFile()) {
				ZipFile zf = null;
				try {
					zf = new ZipFile(f);
					ZipEntry ze = zf.getEntry("META-INF/MANIFEST.MF");
					Manifest mani = new Manifest(zf.getInputStream(ze));
					// now ready symbolic name
					value = mani.getMainAttributes().getValue(attributeName);
				} catch (Exception ex) {
					Activator.getLogger().error(ex);
				} finally {
					if (zf != null) {
						try {
							zf.close();
						} catch (Exception ex) {
							// ignore
						}
					}
				}
								
			}
		}
		
		return value;
	}
	
	/**
	 * 
	 * @param module
	 * @return
	 * @throws CoreException
	 */
	public static String getBundleSymbolicName(IModule module) throws CoreException {
		String symbolicName = null;

		if (module == null || module.getProject() == null)
			return "";

		// extract the value from manifest
		symbolicName = getManifestValue(module, "Bundle-SymbolicName");
		
		if (symbolicName == null) {
			// no manifest - so grab the artifactId
			symbolicName = getArtifactId(module);
		}
		
		if (symbolicName == null) {
			// if all fails fall back to module id
			symbolicName = module.getId();
		}
		
		return symbolicName;
	}
	
	/**
	 * 
	 * @param module
	 * @param f
	 * @return
	 */
	public static String getBundleVersion(IModule module, File f) throws CoreException {
		String version = null;
		
		if (module == null || module.getProject() == null)
			return "";
		
		// retrieve the bundle version
		version = getManifestValue(module, "Bundle-Version");
		
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
