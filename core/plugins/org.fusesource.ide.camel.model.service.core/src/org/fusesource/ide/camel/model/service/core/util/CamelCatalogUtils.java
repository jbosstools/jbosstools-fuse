/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.model.service.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.maven.project.MavenProject;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.fusesource.ide.camel.model.service.core.catalog.Dependency;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelCatalogCoordinates;
import org.fusesource.ide.camel.model.service.core.internal.CamelModelServiceCoreActivator;

/**
 * collection of camel catalog related util methods
 * 
 * @author lhein
 */
public class CamelCatalogUtils {
	
	public static final String CAMEL_SPRING_BOOT_STARTER = "camel-spring-boot-starter";
	public static final String CAMEL_WILDFLY = "org.wildfly.camel";
	
	public static final String DEFAULT_CAMEL_VERSION = "2.18.1.redhat-000012";
	
	public static final String RUNTIME_PROVIDER_KARAF = "karaf";
	public static final String RUNTIME_PROVIDER_SPRINGBOOT = "springboot";
	public static final String RUNTIME_PROVIDER_WILDFLY = "wildfly";
	
	public static final String CATALOG_KARAF_GROUPID = "org.apache.camel";
	public static final String CATALOG_KARAF_ARTIFACTID = "camel-catalog-provider-karaf";
	
	public static final String CATALOG_SPRINGBOOT_GROUPID = "org.apache.camel";
	public static final String CATALOG_SPRINGBOOT_ARTIFACTID = "camel-catalog-provider-springboot";
	
	public static final String CATALOG_WILDFLY_GROUPID = CAMEL_WILDFLY;
	public static final String CATALOG_WILDFLY_ARTIFACTID = "wildfly-camel-catalog";
	
	private static final Map<String, String> camelVersionToFuseBOMMapping;
	static {
		camelVersionToFuseBOMMapping = new HashMap<>();
		camelVersionToFuseBOMMapping.put("2.15.1.redhat-621084", "6.2.1.redhat-084");
		camelVersionToFuseBOMMapping.put("2.15.1.redhat-621117", "6.2.1.redhat-117");
		camelVersionToFuseBOMMapping.put("2.17.0.redhat-630187", "6.3.0.redhat-187");
		camelVersionToFuseBOMMapping.put("2.17.0.redhat-630224", "6.3.0.redhat-224");
		camelVersionToFuseBOMMapping.put("2.17.3",               "6.3.0.redhat-224");
	}
	
	private static final Set<String> pureFisVersions = Stream.of("2.18.1.redhat-000012").collect(Collectors.toSet());
	
	private static final String LATEST_BOM_VERSION = "6.3.0.redhat-224";

	/**
	 * returns the latest and greatest supported Camel version we have a catalog 
	 * for. If there are 2 catalogs with the same version (for instance 2.15.1 and 
	 * 2.15.1.redhat-114) then we will always prefer the Red Hat variant.
	 * 
	 * @return
	 */
	public static String getLatestCamelVersion() {
		return DEFAULT_CAMEL_VERSION;
	}

	/**
	 * tries to map a FUSE BOM version to the Camel version
	 * 
	 * @param camelVersion
	 * @return
	 */
	public static String getFuseVersionForCamelVersion(String camelVersion) {
		String bomVersion = camelVersionToFuseBOMMapping.get(camelVersion);
		if (bomVersion == null) {
			bomVersion = LATEST_BOM_VERSION;
		}
		return bomVersion;
	}
	
	/**
	 * checks if the given camel version is a pure fis version
	 * 
	 * @param camelVersion
	 * @return
	 */
	public static boolean isPureFISVersion(String camelVersion) {
		return pureFisVersions.contains(camelVersion);
	}
	
	/**
	 * takes a label from the catalog which is a possibly comma separated string and splits it into pieces storing 
	 * each of the substrings in a string array list
	 * 
	 * @param label
	 * @return
	 */
	public static ArrayList<String> initializeTags(String label) {
		ArrayList<String> tags = new ArrayList<>();
		if (label != null && label.trim().length()>0) {
			String[] s_tags = label.split(",");
			for (String s_tag : s_tags) {
				tags.add(s_tag);
			}
		}
		return tags;
	}
	
	/**
	 * takes maven coordinates and creates a dependency from it storing it in the array list
	 * 
	 * @param groupId
	 * @param artifactId
	 * @param version
	 * @return
	 */
	public static ArrayList<Dependency> initializeDependency(String groupId, String artifactId, String version) {
		ArrayList<Dependency> dependencies = new ArrayList<>();
		if (groupId != null && groupId.trim().length()>0 &&
			artifactId != null && artifactId.trim().length()>0 && 
			version != null && version.trim().length()>0) {
			Dependency dep = new Dependency();
			dep.setGroupId(groupId);
			dep.setArtifactId(artifactId);
			dep.setVersion(version);
			dependencies.add(dep);
		}
		return dependencies;
	}
	
	/**
	 * returns the catalog coordinates for the given maven coords
	 * 
	 * @param groupId
	 * @param artifactId
	 * @param version
	 * @return
	 */
	public static CamelCatalogCoordinates getCatalogCoordinatesFor(String groupId, String artifactId, String version) {
		return new CamelCatalogCoordinates(groupId, artifactId, version);
	}
	
	/**
	 * returns the catalog coordinates for the given maven coords
	 * 
	 * @param groupId
	 * @param artifactId
	 * @param version
	 * @return
	 */
	public static CamelCatalogCoordinates getCatalogCoordinatesFor(String runtimeProvider, String version) {
		if (RUNTIME_PROVIDER_SPRINGBOOT.equalsIgnoreCase(runtimeProvider)) {
			return new CamelCatalogCoordinates(CATALOG_SPRINGBOOT_GROUPID, CATALOG_SPRINGBOOT_ARTIFACTID, version);
		} else if (RUNTIME_PROVIDER_WILDFLY.equalsIgnoreCase(runtimeProvider)) {
			return new CamelCatalogCoordinates(CATALOG_WILDFLY_GROUPID, CATALOG_WILDFLY_ARTIFACTID, version);
		} else {
			return new CamelCatalogCoordinates(CATALOG_KARAF_GROUPID, CATALOG_KARAF_ARTIFACTID, version);
		}
	}
	
	public static CamelCatalogCoordinates getDefaultCatalogCoordinates() {
		return CamelCatalogUtils.getCatalogCoordinatesFor(CATALOG_KARAF_GROUPID, CATALOG_KARAF_ARTIFACTID, DEFAULT_CAMEL_VERSION);
	}
	
	public static CamelCatalogCoordinates getCatalogCoordinatesForProject(IProject project) {
		CamelMavenUtils cmu = new CamelMavenUtils();
		String camelVersion = cmu.getCamelVersionFromMaven(project);
		String wildFlyCamelVersion = cmu.getWildFlyCamelVersionFromMaven(project);
		String runtimeProvider = CamelCatalogUtils.getRuntimeprovider(project, new NullProgressMonitor());
		if (CamelCatalogUtils.RUNTIME_PROVIDER_KARAF.equalsIgnoreCase(runtimeProvider)) {
			return CamelCatalogUtils.getCatalogCoordinatesFor(CATALOG_KARAF_GROUPID, CATALOG_KARAF_ARTIFACTID, camelVersion);
		} else if (CamelCatalogUtils.RUNTIME_PROVIDER_SPRINGBOOT.equalsIgnoreCase(runtimeProvider)) {
			return CamelCatalogUtils.getCatalogCoordinatesFor(CATALOG_SPRINGBOOT_GROUPID, CATALOG_SPRINGBOOT_ARTIFACTID, camelVersion);
		} else if (CamelCatalogUtils.RUNTIME_PROVIDER_WILDFLY.equalsIgnoreCase(runtimeProvider)) {
			return CamelCatalogUtils.getCatalogCoordinatesFor(CATALOG_WILDFLY_GROUPID, CATALOG_WILDFLY_ARTIFACTID, wildFlyCamelVersion);
		} else {
			// unsupported - fall back to Karaf
			return CamelCatalogUtils.getCatalogCoordinatesFor(CATALOG_KARAF_GROUPID, CATALOG_KARAF_ARTIFACTID, camelVersion);
		}
	}
	
	public static String getRuntimeprovider(IProject camelProject, IProgressMonitor monitor) {
		if(camelProject != null){
			IMavenProjectFacade m2prj = MavenPlugin.getMavenProjectRegistry().create(camelProject, monitor);
			try {
				if(m2prj != null){
					MavenProject mavenProject = m2prj.getMavenProject(monitor);
					if(mavenProject != null){
						List<org.apache.maven.model.Dependency> dependencies = mavenProject.getDependencies();
						return getRuntimeProvider(dependencies);
					}
				}
			} catch (CoreException e) {
				CamelModelServiceCoreActivator.pluginLog().logWarning(e);
			}
		}
		return RUNTIME_PROVIDER_KARAF;
	}
	
	public static String getRuntimeProvider(org.apache.maven.model.Dependency dependency) {
		List<org.apache.maven.model.Dependency> deps = Arrays.asList(dependency);
		return getRuntimeProvider(deps);
	}
	
	public static String getRuntimeProvider(List<org.apache.maven.model.Dependency> dependencies) {
		if(hasSpringBootDependency(dependencies)){
			return RUNTIME_PROVIDER_SPRINGBOOT;
		} else if (hasWildflyDependency(dependencies)) {
			return RUNTIME_PROVIDER_WILDFLY;
		} else {
			return RUNTIME_PROVIDER_KARAF;
		}
	}
		
	public static boolean hasSpringBootDependency(List<org.apache.maven.model.Dependency> dependencies){
		return dependencies != null
				&& dependencies.stream()
					.filter(dependency -> CAMEL_SPRING_BOOT_STARTER.equals(dependency.getArtifactId()))
					.findFirst().isPresent();
	}
	
	// TODO: put in the correct maven coords for a wildfly swarm project
	public static boolean hasWildflyDependency(List<org.apache.maven.model.Dependency> dependencies){
		return dependencies != null
				&& dependencies.stream()
					.filter(dependency -> CAMEL_WILDFLY.equals(dependency.getGroupId()))
					.findFirst().isPresent();
	}
}
