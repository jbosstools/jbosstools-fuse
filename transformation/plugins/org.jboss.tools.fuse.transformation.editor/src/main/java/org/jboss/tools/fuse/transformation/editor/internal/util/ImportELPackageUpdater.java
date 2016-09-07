/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.transformation.editor.internal.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.osgi.util.ManifestElement;
import org.jboss.tools.fuse.transformation.editor.Activator;
import org.osgi.framework.Constants;
import org.osgi.framework.Version;
import org.eclipse.pde.internal.core.bundle.WorkspaceBundleModel;

/**
 * This class is responsible to configure the project to import the Expression Language package if needed.
 * 
 */
public class ImportELPackageUpdater {

	private static final String COM_SUN_EL_VERSION = "com.sun.el;version=";
	private static final String LIMIT_CAMEL_VERSION_FROM_WHICH_NEED_TO_ADD_IMPORT_PACKAGE = "2.17.0";
	private static final String ORG_APACHE_CAMEL = "org.apache.camel";
	private static final String CAMEL_CORE = "camel-core";
	private static final String ORG_APACHE_FELIX = "org.apache.felix";
	private static final String MAVEN_BUNDLE_PLUGIN = "maven-bundle-plugin";
	private static final String MAVEN_BUNDLE_PLUGIN_VERSION = "3.2.0";

	public void updatePackageImports(IProject project, IProgressMonitor monitor) {
		IFile manifestFile = project.getFile("src/main/resources/META-INF/MANIFEST.MF");
		if(manifestFile.exists()){
			updateImportPackageForExistingManifest(project, manifestFile);
		} else {
			updateImportPackageForGeneratedManifest(project, monitor);
		}
	}

	private void updateImportPackageForExistingManifest(IProject project, IFile manifestFile) {
		File pomFile = project.getFile(IMavenConstants.POM_FILE_NAME).getLocation().toFile();
		Model pomModel = null;
		try {
			pomModel = MavenPlugin.getMaven().readModel(pomFile);
		} catch (CoreException e) {
			Activator.error(e);
		}
		if(pomModel == null || shouldAddImportPackage(pomModel)){
			WorkspaceBundleModel bundleModel = new WorkspaceBundleModel(manifestFile);
			String importPackage = bundleModel.getBundle().getHeader(Constants.IMPORT_PACKAGE);
			if(importPackage == null || !importPackage.contains(COM_SUN_EL_VERSION)){
				importPackage = addELPackage(importPackage);
				bundleModel.getBundle().setHeader(Constants.IMPORT_PACKAGE, importPackage);
				bundleModel.save();
			}
		}
	}

	private void updateImportPackageForGeneratedManifest(IProject project, IProgressMonitor monitor) {
		try {
			File pomFile = project.getFile(IMavenConstants.POM_FILE_NAME).getLocation().toFile();
			Model pomModel = MavenPlugin.getMaven().readModel(pomFile);
			if (!shouldAddImportPackage(pomModel)){ //$NON-NLS-1$
				return; 
			}
			managePlugins(pomModel);

			try (OutputStream out = new BufferedOutputStream(new FileOutputStream(pomFile))) {
				MavenPlugin.getMaven().writeModel(pomModel, out);
				project.getFile(IMavenConstants.POM_FILE_NAME).refreshLocal(IResource.DEPTH_ZERO, monitor);
			}
		} catch (CoreException | XmlPullParserException | IOException e1) {
			Activator.error(e1);
			return;
		}
	}

	boolean shouldAddImportPackage(Model pomModel) {
		return !"war".equals(pomModel.getPackaging()) && isCamelDependencyHigherThan63(pomModel);
	}

	private boolean isCamelDependencyHigherThan63(Model pomModel) {
		try{
			Build build = pomModel.getBuild();
			Map<String, Plugin> pluginsByName = build.getPluginsAsMap();
			Plugin plugin = pluginsByName.get(ORG_APACHE_CAMEL+":"+CAMEL_CORE); //$NON-NLS-1$
			if(plugin == null || plugin.getVersion() == null){
				return true;
			}
			String pluginVersion = plugin.getVersion();
			if(pluginVersion.startsWith("${") && pluginVersion.endsWith("}") && pomModel.getProperties() != null){
				String camelCoreVersionFromProperty = pomModel.getProperties().getProperty(pluginVersion.substring(2, pluginVersion.length() -1));
				if(camelCoreVersionFromProperty ==  null){
					return true;
				} else {
					return isVersionHigherThanLimitForImportPackage(camelCoreVersionFromProperty);
				}
			} else {
				return isVersionHigherThanLimitForImportPackage(pluginVersion);
			}
		} catch(IllegalArgumentException e){
			Activator.log(new Status(IStatus.INFO, Activator.PLUGIN_ID, "The version of camel-core has not been resolved correctly.", e));
			return true;
		}
	}

	private boolean isVersionHigherThanLimitForImportPackage(String camelCoreVersion) {
		return new Version(camelCoreVersion).compareTo(new Version(LIMIT_CAMEL_VERSION_FROM_WHICH_NEED_TO_ADD_IMPORT_PACKAGE)) >= 0;
	}

	private void managePlugins(Model pomModel) throws XmlPullParserException, IOException {
		Build build = pomModel.getBuild();
		Map<String, Plugin> pluginsByName = build.getPluginsAsMap();
		Plugin plugin = pluginsByName.get(ORG_APACHE_FELIX+":"+MAVEN_BUNDLE_PLUGIN); //$NON-NLS-1$
		if (plugin == null) {
			plugin = new Plugin();
			plugin.setGroupId(ORG_APACHE_FELIX); //$NON-NLS-1$
			plugin.setArtifactId(MAVEN_BUNDLE_PLUGIN); //$NON-NLS-1$
			plugin.setVersion(MAVEN_BUNDLE_PLUGIN_VERSION); //$NON-NLS-1$
			plugin.setExtensions(true);
			build.addPlugin(plugin);
		}
		manageConfigurations(plugin);
	}

	private void manageConfigurations(Plugin plugin) throws XmlPullParserException, IOException {
		Xpp3Dom config = (Xpp3Dom)plugin.getConfiguration();
		if (config == null) {
			config = Xpp3DomBuilder.build(new ByteArrayInputStream((
					"<configuration>" + //$NON-NLS-1$
					"</configuration>").getBytes(StandardCharsets.UTF_8)), //$NON-NLS-1$
					StandardCharsets.UTF_8.name());
			plugin.setConfiguration(config);
		}
		manageInstructions(config);
	}

	private void manageInstructions(Xpp3Dom config) throws XmlPullParserException, IOException {
		Xpp3Dom instructions = config.getChild("instructions"); //$NON-NLS-1$
		if (instructions == null) {
			instructions = Xpp3DomBuilder.build(new ByteArrayInputStream(("<instructions>" + //$NON-NLS-1$
					"</instructions>").getBytes(StandardCharsets.UTF_8)), //$NON-NLS-1$
					StandardCharsets.UTF_8.name());
			config.addChild(instructions);
		}
		manageImportPkg(instructions);
	}

	private void manageImportPkg(Xpp3Dom instructions) throws XmlPullParserException, IOException {
		Xpp3Dom importPkg = instructions.getChild("Import-Package"); //$NON-NLS-1$
		if (importPkg == null) {
			importPkg = Xpp3DomBuilder.build(new ByteArrayInputStream(("<Import-Package>" + //$NON-NLS-1$
					"</Import-Package>").getBytes(StandardCharsets.UTF_8)), //$NON-NLS-1$
					StandardCharsets.UTF_8.name());
			instructions.addChild(importPkg);
		}
		manageImportPkgs(importPkg);
	}

	private void manageImportPkgs(Xpp3Dom importPkg) {
		String importPkgs = importPkg.getValue().trim();
		if (!importPkgs.contains(COM_SUN_EL_VERSION)) { //$NON-NLS-1$
			importPkgs = addELPackage(importPkgs);
			importPkg.setValue(importPkgs);
		}
	}

	private String addELPackage(String importPkgs) {
		if(importPkgs == null){
			importPkgs = "";
		}
		if (!importPkgs.isEmpty()){
			importPkgs += ",\n"; //$NON-NLS-1$
		}
		if(importPkgs.contains("*")){ //$NON-NLS-1$
			importPkgs += "com.sun.el;version=\"[2,3)\""; //$NON-NLS-1$
		} else {
			importPkgs += "*,com.sun.el;version=\"[2,3)\""; //$NON-NLS-1$
		}
		return importPkgs;
	}

}
