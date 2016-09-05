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
package org.jboss.tools.fuse.transformation;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.jboss.tools.fuse.transformation.editor.Activator;

public class ManifestConfigurationUpdater {

	private static final String ORG_APACHE_FELIX = "org.apache.felix";
	private static final String MAVEN_BUNDLE_PLUGIN = "maven-bundle-plugin";
	private static final String MAVEN_BUNDLE_PLUGIN_VERSION = "3.2.0";

	public void updateManifestPackageImports(IProject project, IProgressMonitor monitor) {
		try {
			File pomFile = project.getFile(IMavenConstants.POM_FILE_NAME).getLocation().toFile();
			Model pomModel = MavenPlugin.getMaven().readModel(pomFile);
			if ("war".equals(pomModel.getPackaging())){ //$NON-NLS-1$
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
							"    <excludeDependencies>false</excludeDependencies>" + //$NON-NLS-1$
							"    <archive>" + //$NON-NLS-1$
							"        <manifestEntries>" + //$NON-NLS-1$
							"            <Project-Group-Id>${project.groupId}</Project-Group-Id>" + //$NON-NLS-1$
							"            <Project-Artifact-Id>${project.artifactId}</Project-Artifact-Id>" + //$NON-NLS-1$
							"            <Project-Version>${project.version}</Project-Version>" + //$NON-NLS-1$
							"        </manifestEntries>" + //$NON-NLS-1$
							"    </archive>" + //$NON-NLS-1$
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
		if (!importPkgs.contains("com.sun.el;version=")) { //$NON-NLS-1$
			if (!importPkgs.isEmpty()){
				importPkgs += ",\n"; //$NON-NLS-1$
			}
			importPkgs += "*,com.sun.el;version=\"[2,3)\""; //$NON-NLS-1$
			importPkg.setValue(importPkgs);
		}
	}

}
