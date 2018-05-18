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
import java.util.Arrays;
import java.util.Map;

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
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.pde.internal.core.bundle.WorkspaceBundleModel;
import org.fusesource.ide.camel.model.service.core.util.CamelMavenUtils;
import org.jboss.tools.fuse.transformation.editor.Activator;
import org.jboss.tools.fuse.transformation.editor.internal.l10n.Messages;
import org.osgi.framework.Constants;
import org.osgi.framework.Version;


/**
 * This class is responsible to configure the project to import the Expression Language package if needed.
 *
 */
public class ImportExportPackageUpdater {

	private static final String DEFAULT_PACKAGE_EXPORT = "."; //$NON-NLS-1$
	private static final String COM_SUN_EL_VERSION = "com.sun.el;version="; //$NON-NLS-1$
	private static final String LIMIT_CAMEL_VERSION_FROM_WHICH_NEED_TO_ADD_IMPORT_PACKAGE = "2.17.0"; //$NON-NLS-1$
	private static final String LIMIT_CAMEL_VERSION_FROM_WHICH_NO_MORE_NEED_TO_ADD_IMPORT_PACKAGE = "2.21.0";  //$NON-NLS-1$
	private static final String ORG_APACHE_CAMEL = "org.apache.camel"; //$NON-NLS-1$
	private static final String CAMEL_CORE = "camel-core"; //$NON-NLS-1$
	private static final String ORG_APACHE_FELIX = "org.apache.felix"; //$NON-NLS-1$
	private static final String MAVEN_BUNDLE_PLUGIN = "maven-bundle-plugin"; //$NON-NLS-1$
	private static final String MAVEN_BUNDLE_PLUGIN_VERSION = "3.2.0"; //$NON-NLS-1$

	private IProject project;
	private String sourceClassName;
	private String targetClassName;


	public ImportExportPackageUpdater(IProject project, String sourceClassName, String targetClassName) {
		this.project = project;
		this.sourceClassName= sourceClassName;
		this.targetClassName = targetClassName;
	}

	public void updatePackageImports(IProgressMonitor monitor) {
		IFile manifestFile = project.getFile("src/main/resources/META-INF/MANIFEST.MF"); //$NON-NLS-1$
		if(manifestFile.exists()){
			updateImportExportPackageForExistingManifest(manifestFile, monitor);
		} else {
			updateImportExportPackageForGeneratedManifest(monitor);
		}
	}

	private void updateImportExportPackageForExistingManifest(IFile manifestFile, IProgressMonitor monitor) {
		SubMonitor subMonitor = SubMonitor.convert(monitor, Messages.UpdatingMANIFESTMF, 3);
		Model pomModel = new CamelMavenUtils().getMavenModel(project);
		subMonitor.worked(1);
		if(pomModel == null || shouldAddImportExportPackage(pomModel)){
			WorkspaceBundleModel bundleModel = new WorkspaceBundleModel(manifestFile);
			updateImportPackage(bundleModel);
			subMonitor.worked(1);
			updateExportPackage(bundleModel);
		}
		subMonitor.done();
	}

	private void updateExportPackage(WorkspaceBundleModel bundleModel) {
		String exportPackage = bundleModel.getBundle().getHeader(Constants.EXPORT_PACKAGE);
		String initialExportPackage = exportPackage;
		exportPackage = addPackageForClass(sourceClassName, exportPackage);
		exportPackage = addPackageForClass(targetClassName, exportPackage);
		if(exportPackage != null && !exportPackage.equals(initialExportPackage)){
			bundleModel.getBundle().setHeader(Constants.EXPORT_PACKAGE, exportPackage);
			bundleModel.save();
		}
	}

	private String addPackageForClass(String className, String exportPackage) {
		if(className != null){
			String packageName = getPackage(className);
			if(exportPackage == null || !exportPackage.contains(packageName)){
				if(packageName != null){
					if(isPackageInsideSource(packageName)){
						return addPackage(exportPackage, packageName);
					}
				} else {
					return addPackage(exportPackage, DEFAULT_PACKAGE_EXPORT);
				}
			}
		}
		return exportPackage;
	}

	private boolean isPackageInsideSource(String packageName) {
		IJavaProject jProject = JavaCore.create(project);
		try{
			IJavaElement findElement = jProject.findElement(Path.fromPortableString(packageName.replaceAll("\\.", "/"))); //$NON-NLS-1$ //$NON-NLS-2$
			if(findElement instanceof IPackageFragment){
				return IPackageFragmentRoot.K_SOURCE == ((IPackageFragment) findElement).getKind();
			}
		} catch (Exception e) {
			Activator.log(new Status(IStatus.WARNING, Activator.PLUGIN_ID, "Cannot determine where the package "+ packageName+ " comes from.", e)); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return true;
	}

	private void updateImportPackage(WorkspaceBundleModel bundleModel) {
		String importPackage = bundleModel.getBundle().getHeader(Constants.IMPORT_PACKAGE);
		if(importPackage == null || !importPackage.contains(COM_SUN_EL_VERSION)){
			importPackage = addELPackage(importPackage);
			bundleModel.getBundle().setHeader(Constants.IMPORT_PACKAGE, importPackage);
			bundleModel.save();
		}
	}

	/**
	 * @param fullyQualifiedClassName
	 * @return the package name or null if it is the default one.
	 */
	private String getPackage(String fullyQualifiedClassName) {
		if(fullyQualifiedClassName.contains(DEFAULT_PACKAGE_EXPORT)){
			return fullyQualifiedClassName.substring(0, fullyQualifiedClassName.lastIndexOf('.'));
		}
		return null;
	}

	private void updateImportExportPackageForGeneratedManifest(IProgressMonitor monitor) {
		try {
			File pomFile = project.getFile(IMavenConstants.POM_FILE_NAME).getLocation().toFile();
			Model pomModel = new CamelMavenUtils().getMavenModel(project);
			if (!shouldAddImportExportPackage(pomModel)){
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

	boolean shouldAddImportExportPackage(Model pomModel) {
		return !"war".equals(pomModel.getPackaging()) && isCamelDependencyHigherThan63AndLowerThan70(pomModel); //$NON-NLS-1$
	}

	private boolean isCamelDependencyHigherThan63AndLowerThan70(Model pomModel) {
		try{
			Build build = pomModel.getBuild();
			Map<String, Plugin> pluginsByName = build.getPluginsAsMap();
			Plugin plugin = pluginsByName.get(ORG_APACHE_CAMEL+":"+CAMEL_CORE); //$NON-NLS-1$
			if(plugin == null || plugin.getVersion() == null){
				return true;
			}
			String pluginVersion = plugin.getVersion();
			if(pluginVersion.startsWith("${") && pluginVersion.endsWith("}") && pomModel.getProperties() != null) { //$NON-NLS-1$ //$NON-NLS-2$
				String camelCoreVersionFromProperty = pomModel.getProperties().getProperty(pluginVersion.substring(2, pluginVersion.length() -1));
				if(camelCoreVersionFromProperty ==  null){
					return true;
				}
				return isVersionInRangeForImportPackage(camelCoreVersionFromProperty);
			}
			return isVersionInRangeForImportPackage(pluginVersion);
		} catch(IllegalArgumentException e){
			Activator.log(new Status(IStatus.INFO, Activator.PLUGIN_ID, Messages.ImportExportPackageUpdater_UnresolvedCamelCoreVersion, e));
			return true;
		}
	}

	private boolean isVersionInRangeForImportPackage(String camelCoreVersion) {
		return new Version(camelCoreVersion).compareTo(new Version(LIMIT_CAMEL_VERSION_FROM_WHICH_NEED_TO_ADD_IMPORT_PACKAGE)) >= 0
				&& new Version(LIMIT_CAMEL_VERSION_FROM_WHICH_NO_MORE_NEED_TO_ADD_IMPORT_PACKAGE).compareTo(new Version(camelCoreVersion)) > 0;
	}

	private void managePlugins(Model pomModel) throws XmlPullParserException, IOException {
		Build build = pomModel.getBuild();
		Map<String, Plugin> pluginsByName = build.getPluginsAsMap();
		Plugin plugin = pluginsByName.get(ORG_APACHE_FELIX+":"+MAVEN_BUNDLE_PLUGIN); //$NON-NLS-1$
		if (plugin == null) {
			plugin = new Plugin();
			plugin.setGroupId(ORG_APACHE_FELIX);
			plugin.setArtifactId(MAVEN_BUNDLE_PLUGIN);
			plugin.setVersion(MAVEN_BUNDLE_PLUGIN_VERSION);
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
		manageExportPkg(instructions);
	}

	private void manageExportPkg(Xpp3Dom instructions) {
		Xpp3Dom exporttPkg = instructions.getChild("Export-Package"); //$NON-NLS-1$
		if (exporttPkg == null) {
			//By default, all packages are exported
			return;
		}
		manageExportPkgs(exporttPkg);
	}

	private void manageExportPkgs(Xpp3Dom exportPkg) {
		String exportPkgs = exportPkg.getValue().trim();
		exportPkgs = addPackageForClass(sourceClassName, exportPkgs);
		exportPkgs = addPackageForClass(targetClassName, exportPkgs);
		exportPkg.setValue(exportPkgs);
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
		if (!importPkgs.contains(COM_SUN_EL_VERSION)) {
			importPkgs = addELPackage(importPkgs);
			importPkg.setValue(importPkgs);
		}
	}

	private String addELPackage(String importPkgs) {
		if(importPkgs == null){
			importPkgs = ""; //$NON-NLS-1$
		}
		if (!importPkgs.isEmpty()){
			importPkgs += ",\n "; //$NON-NLS-1$
		}
		if(importPkgs.contains("*")){ //$NON-NLS-1$
			importPkgs += "com.sun.el;version=\"[2,3)\""; //$NON-NLS-1$
		} else {
			importPkgs += "*,com.sun.el;version=\"[2,3)\""; //$NON-NLS-1$
		}
		return importPkgs;
	}


	private String addPackage(String initialPackageList, String packageToAdd) {
		if(initialPackageList == null){
			return packageToAdd;
		}
		if(!Arrays.asList(initialPackageList.split(",")).contains(packageToAdd)){ //$NON-NLS-1$
			if(initialPackageList.isEmpty()){
				return packageToAdd;
			}
			return initialPackageList + ",\n " + packageToAdd; //$NON-NLS-1$
		}
		return initialPackageList;
	}

}
