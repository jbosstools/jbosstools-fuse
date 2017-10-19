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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.maven.artifact.versioning.ComparableVersion;
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
import org.fusesource.ide.foundation.core.util.Strings;

/**
 * collection of camel catalog related util methods
 * 
 * @author lhein
 */
public class CamelCatalogUtils {
	public static final String CAMEL_TO_BOM_MAPPING_URL = "https://raw.githubusercontent.com/jbosstools/jbosstools-fuse/master/configuration/camel2bom.properties";
	public static final String FIS_MAPPING_URL = "https://raw.githubusercontent.com/jbosstools/jbosstools-fuse/master/configuration/fismarker.properties";
	
	public static final String KEY_CAMEL_TEST_VERSIONS = "FUSE_TOOLING_CAMEL_TEST_VERSIONS";
	
	private static final String FUSE_63_R0_CAMEL_VERSION = "2.17.0.redhat-630187";
	private static final String FUSE_63_R1_CAMEL_VERSION = "2.17.0.redhat-630224";
	private static final String FUSE_63_R2_CAMEL_VERSION = "2.17.0.redhat-630254";
	private static final String FUSE_63_R3_CAMEL_VERSION = "2.17.0.redhat-630262";
	public static final String FUSE_63_R4_CAMEL_VERSION = "2.17.0.redhat-630283";

	private static final String FUSE_63_R0_BOM_VERSION = "6.3.0.redhat-187";
	private static final String FUSE_63_R1_BOM_VERSION = "6.3.0.redhat-224";
	private static final String FUSE_63_R2_BOM_VERSION = "6.3.0.redhat-254";
	private static final String FUSE_63_R3_BOM_VERSION = "6.3.0.redhat-262";
	private static final String FUSE_63_R4_BOM_VERSION = "6.3.0.redhat-283";
	
	public static final String FIS_20_R1_CAMEL_VERSION = "2.18.1.redhat-000012";
	public static final String FIS_20_R2_CAMEL_VERSION = "2.18.1.redhat-000015";
	public static final String FIS_20_R3_CAMEL_VERSION = "2.18.1.redhat-000021";
	
	public static final String DEFAULT_CAMEL_VERSION = FIS_20_R3_CAMEL_VERSION;
	
	public static final String CAMEL_SPRING_BOOT_STARTER = "camel-spring-boot-starter";
	public static final String CAMEL_WILDFLY = "org.wildfly.camel";
	
	public static final String CAMEL_VERSION_LATEST_COMMUNITY = "2.19.3";
	public static final String CAMEL_VERSION_LATEST_PRODUCTIZED_62 = "2.15.1.redhat-621186";
	public static final String CAMEL_VERSION_LATEST_PRODUCTIZED_63 = FUSE_63_R4_CAMEL_VERSION;
	public static final String CAMEL_VERSION_LATEST_FIS_20 = FIS_20_R3_CAMEL_VERSION;
	
	public static final String RUNTIME_PROVIDER_KARAF = "karaf";
	public static final String RUNTIME_PROVIDER_SPRINGBOOT = "springboot";
	public static final String RUNTIME_PROVIDER_WILDFLY = "wildfly";

	public static final String CATALOG_CAMEL_ARTIFACTID = "camel-catalog";
	
	public static final String CATALOG_KARAF_GROUPID = "org.apache.camel";
	public static final String CATALOG_KARAF_ARTIFACTID = "camel-catalog-provider-karaf";
	
	public static final String CATALOG_SPRINGBOOT_GROUPID = "org.apache.camel";
	public static final String CATALOG_SPRINGBOOT_ARTIFACTID = "camel-catalog-provider-springboot";
	
	public static final String CATALOG_WILDFLY_GROUPID = CAMEL_WILDFLY;
	public static final String CATALOG_WILDFLY_ARTIFACTID = "wildfly-camel-catalog";
	
	public static final String GAV_KEY_GROUPID = "groupId";
	public static final String GAV_KEY_ARTIFACTID = "artifactId";
	public static final String GAV_KEY_VERSION = "version";
	
	private static final List<String> OFFICIAL_SUPPORTED_CAMEL_CATALOG_VERSIONS;
	private static final List<String> ALL_CAMEL_CATALOG_VERSIONS;
	private static final List<String> TEST_CAMEL_VERSIONS;
	private static final Map<String, String> CAMEL_VERSION_2_FUSE_BOM_MAPPING;
	private static final Map<String, String> PURE_FIS_CAMEL_VERSIONS;
	
	static {
		CAMEL_VERSION_2_FUSE_BOM_MAPPING = new HashMap<>();
		PURE_FIS_CAMEL_VERSIONS = new HashMap<>();
		ALL_CAMEL_CATALOG_VERSIONS = new ArrayList<>();
		OFFICIAL_SUPPORTED_CAMEL_CATALOG_VERSIONS = new ArrayList<>();
		TEST_CAMEL_VERSIONS = new ArrayList<>();
				
		try {
			Properties vMapping = new Properties();
			URL url = new URL(CAMEL_TO_BOM_MAPPING_URL);
			vMapping.load(url.openStream());
			
			for(String camelVersion : vMapping.stringPropertyNames()) {
				String bomVersion = vMapping.getProperty(camelVersion);
				CAMEL_VERSION_2_FUSE_BOM_MAPPING.put(camelVersion, bomVersion);
				
				// we only add camel versions later than 2.17.0 to the supported versions map (prior versions had
				// too many errors in the catalog or not catalog at all) 
				OFFICIAL_SUPPORTED_CAMEL_CATALOG_VERSIONS.add(camelVersion);
			}
		} catch (IOException ex) {
			CamelModelServiceCoreActivator.pluginLog().logError("Unable to retrieve the Camel Version -> BOM Version mappings from online repo. Falling back to defaults.", ex);

			// DEFAULTS
			CAMEL_VERSION_2_FUSE_BOM_MAPPING.put("2.15.1.redhat-621084", "6.2.1.redhat-084");
			CAMEL_VERSION_2_FUSE_BOM_MAPPING.put("2.15.1.redhat-621090", "6.2.1.redhat-090");
			CAMEL_VERSION_2_FUSE_BOM_MAPPING.put("2.15.1.redhat-621107", "6.2.1.redhat-107");
			CAMEL_VERSION_2_FUSE_BOM_MAPPING.put("2.15.1.redhat-621117", "6.2.1.redhat-117");
			CAMEL_VERSION_2_FUSE_BOM_MAPPING.put("2.15.1.redhat-621159", "6.2.1.redhat-159");
			CAMEL_VERSION_2_FUSE_BOM_MAPPING.put("2.15.1.redhat-621169", "6.2.1.redhat-169");
			CAMEL_VERSION_2_FUSE_BOM_MAPPING.put("2.15.1.redhat-621177", "6.2.1.redhat-177");
			CAMEL_VERSION_2_FUSE_BOM_MAPPING.put("2.15.1.redhat-621186", "6.2.1.redhat-186");
			CAMEL_VERSION_2_FUSE_BOM_MAPPING.put(FUSE_63_R0_CAMEL_VERSION, FUSE_63_R0_BOM_VERSION);
			CAMEL_VERSION_2_FUSE_BOM_MAPPING.put(FUSE_63_R1_CAMEL_VERSION, FUSE_63_R1_BOM_VERSION);
			CAMEL_VERSION_2_FUSE_BOM_MAPPING.put(FUSE_63_R2_CAMEL_VERSION, FUSE_63_R2_BOM_VERSION);
			CAMEL_VERSION_2_FUSE_BOM_MAPPING.put(FUSE_63_R3_CAMEL_VERSION, FUSE_63_R3_BOM_VERSION);
			CAMEL_VERSION_2_FUSE_BOM_MAPPING.put(FUSE_63_R4_CAMEL_VERSION, FUSE_63_R4_BOM_VERSION);
			
			OFFICIAL_SUPPORTED_CAMEL_CATALOG_VERSIONS.addAll(CAMEL_VERSION_2_FUSE_BOM_MAPPING.keySet());
		}
		
		try {
			Properties fisMapping = new Properties();
			URL url = new URL(FIS_MAPPING_URL);
			fisMapping.load(url.openStream());
		
			for(String camelVersion : fisMapping.stringPropertyNames()) {
				String bomVersion = fisMapping.getProperty(camelVersion);
				PURE_FIS_CAMEL_VERSIONS.put(camelVersion, bomVersion);
			}
		} catch (IOException ex) {
			CamelModelServiceCoreActivator.pluginLog().logError("Unable to retrieve the FIS-ONLY Camel Versions list from online repo. Falling back to defaults.", ex);

			// DEFAULTS
			PURE_FIS_CAMEL_VERSIONS.put(FIS_20_R1_CAMEL_VERSION, "2.2.170.redhat-000010");			
			PURE_FIS_CAMEL_VERSIONS.put(FIS_20_R2_CAMEL_VERSION, "2.2.170.redhat-000013");
			PURE_FIS_CAMEL_VERSIONS.put(FIS_20_R3_CAMEL_VERSION, "2.2.170.redhat-000019");
		}

		String camelVersionsForTesting = System.getProperty(KEY_CAMEL_TEST_VERSIONS, "").trim();
		if (camelVersionsForTesting.equalsIgnoreCase("null")) {
			camelVersionsForTesting = "";
		}

		if (!Strings.isBlank(camelVersionsForTesting)) {
			Arrays.stream(camelVersionsForTesting.split(","))
				.map(String::trim)
				.filter( (String s) -> !Strings.isBlank(s))
				.forEach(TEST_CAMEL_VERSIONS::add);
		} else {
			TEST_CAMEL_VERSIONS.add(CAMEL_VERSION_LATEST_COMMUNITY);
			TEST_CAMEL_VERSIONS.add(CAMEL_VERSION_LATEST_PRODUCTIZED_62);
			TEST_CAMEL_VERSIONS.add(CAMEL_VERSION_LATEST_PRODUCTIZED_63);
			TEST_CAMEL_VERSIONS.add(CAMEL_VERSION_LATEST_FIS_20);
		}
		
		ALL_CAMEL_CATALOG_VERSIONS.addAll(PURE_FIS_CAMEL_VERSIONS.keySet());
		ALL_CAMEL_CATALOG_VERSIONS.addAll(OFFICIAL_SUPPORTED_CAMEL_CATALOG_VERSIONS);
	}

	private CamelCatalogUtils() {
		// utility class
	}
	
	public static List<String> getOfficialSupportedCamelCatalogVersions() {
		return OFFICIAL_SUPPORTED_CAMEL_CATALOG_VERSIONS;
	}
	
	public static List<String> getAllCamelCatalogVersions() {
		return ALL_CAMEL_CATALOG_VERSIONS;
	}
	
	public static List<String> getCamelVersionsToTestWith() {
		return TEST_CAMEL_VERSIONS;
	}
	
	public static List<String> getPureFISVersions() {
		return Arrays.asList(PURE_FIS_CAMEL_VERSIONS.keySet().toArray(new String[PURE_FIS_CAMEL_VERSIONS.size()]));
	}
	
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
		String bomVersion = CAMEL_VERSION_2_FUSE_BOM_MAPPING.get(camelVersion);
		// TODO: revisit once https://issues.apache.org/jira/browse/CAMEL-8502 got solved
		if (bomVersion == null) {
			// seems it's not a Fuse Camel version so we currently don't support it, so try to get the latest known BOM version
			bomVersion = CAMEL_VERSION_2_FUSE_BOM_MAPPING.values().stream().sorted(Comparator.reverseOrder()).findFirst().orElse(null);
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
		return PURE_FIS_CAMEL_VERSIONS.containsKey(camelVersion);
	}
	
	/**
	 * takes a label from the catalog which is a possibly comma separated string and splits it into pieces storing 
	 * each of the substrings in a string array list
	 * 
	 * @param label
	 * @return
	 */
	public static List<String> initializeTags(String label) {
		List<String> tags = new ArrayList<>();
		if (label != null && label.trim().length()>0) {
			String[] tagArray = label.split(",");
			for (String s_tag : tagArray) {
				tags.add(s_tag);
			}
		}
		return tags;
	}
	
	private static boolean isValidGAV(String groupId, String artifactId, String version) {
		return 	!Strings.isBlank(groupId) &&
				!Strings.isBlank(artifactId) && 
				!Strings.isBlank(version);
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
		if (!CATALOG_CAMEL_ARTIFACTID.equals(artifactId) && isCamelVersionWithoutProviderSupport(version)) {
			CamelCatalogCoordinates coord = getDefaultCatalogCoordinates();
			coord.setArtifactId(CATALOG_CAMEL_ARTIFACTID);
			coord.setVersion(version);
			return coord;
		}
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
		if (isCamelVersionWithoutProviderSupport(version)) {
			CamelCatalogCoordinates coord = getDefaultCatalogCoordinates();
			coord.setArtifactId(CATALOG_CAMEL_ARTIFACTID);
			coord.setVersion(version);
			return coord;
		}
		if (RUNTIME_PROVIDER_SPRINGBOOT.equalsIgnoreCase(runtimeProvider)) {
			return new CamelCatalogCoordinates(CATALOG_SPRINGBOOT_GROUPID, CATALOG_SPRINGBOOT_ARTIFACTID, version);
		} else {
			return new CamelCatalogCoordinates(CATALOG_KARAF_GROUPID, CATALOG_KARAF_ARTIFACTID, version);
		}
	}
	
	public static boolean isCamelVersionWithoutProviderSupport(String version) {
		if (version == null) {
			return true; // happens if no camel dep is defined in the pom.xml
		}
		ComparableVersion v1 = new ComparableVersion(version);
		ComparableVersion v2 = new ComparableVersion("2.18.1");
		return v1.compareTo(v2) < 0;
	}
	
	public static CamelCatalogCoordinates getDefaultCatalogCoordinates() {
		return CamelCatalogUtils.getCatalogCoordinatesFor(CATALOG_KARAF_GROUPID, CATALOG_KARAF_ARTIFACTID, DEFAULT_CAMEL_VERSION);
	}
	
	public static CamelCatalogCoordinates getCatalogCoordinatesForProject(IProject project) {
		String camelVersion = new CamelMavenUtils().getCamelVersionFromMaven(project);
		String runtimeProvider = CamelCatalogUtils.getRuntimeprovider(project, new NullProgressMonitor());
		if (CamelCatalogUtils.RUNTIME_PROVIDER_SPRINGBOOT.equalsIgnoreCase(runtimeProvider)) {
			return CamelCatalogUtils.getCatalogCoordinatesFor(CATALOG_SPRINGBOOT_GROUPID, CATALOG_SPRINGBOOT_ARTIFACTID, camelVersion);
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
						return getRuntimeProviderFromDependencyList(dependencies);
					}
				}
			} catch (CoreException e) {
				CamelModelServiceCoreActivator.pluginLog().logWarning(e);
			}
		}
		return RUNTIME_PROVIDER_KARAF;
	}
	
	public static String getRuntimeProviderFromDependency(org.apache.maven.model.Dependency dependency) {
		List<org.apache.maven.model.Dependency> deps = Arrays.asList(dependency);
		return getRuntimeProviderFromDependencyList(deps);
	}
	
	public static String getRuntimeProviderFromDependencyList(List<org.apache.maven.model.Dependency> dependencies) {
		if(hasSpringBootDependency(dependencies)){
			return RUNTIME_PROVIDER_SPRINGBOOT;
		} else {
			return RUNTIME_PROVIDER_KARAF;
		}
	}
		
	public static boolean hasSpringBootDependency(List<org.apache.maven.model.Dependency> dependencies){
		return hasDependency(dependencies, CAMEL_SPRING_BOOT_STARTER);
	}

	public static boolean hasWildflyDependency(List<org.apache.maven.model.Dependency> dependencies){
		return hasDependency(dependencies, CAMEL_WILDFLY);
	}
	
	private static boolean hasDependency(List<org.apache.maven.model.Dependency> dependencies, String dependencyToCheck) {
		return dependencies != null
				&& dependencies.stream()
					.filter(dependency -> dependencyToCheck.equals(dependency.getArtifactId()))
					.findFirst().isPresent();
	}
	
	public static void parseDependencies(List<Dependency> dependencies, Map<String, String> properties) {
		String grpId = properties.get(GAV_KEY_GROUPID);
		String artId = properties.get(GAV_KEY_ARTIFACTID);
		String version = properties.get(GAV_KEY_VERSION);
		
		if (isValidGAV(grpId, artId, version)) {
			// we process only fully specified dependencies
			Dependency dep = new Dependency();
			
			dep.setGroupId(grpId);
			dep.setArtifactId(artId);
			dep.setVersion(version);
			
			dependencies.add(dep);
		}
	}
}
