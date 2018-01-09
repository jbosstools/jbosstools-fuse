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
import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
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
	public static final String CAMEL_TO_BOM_MAPPING_PROPERTY = "org.jboss.tools.fuse.camel2bom.url";
	public static final String CAMEL_TO_BOM_MAPPING_DEFAULT_URL = "https://raw.githubusercontent.com/jbosstools/jbosstools-fuse/master/configuration/camel2bom.properties";
	public static final String CAMEL_TO_BOM_MAPPING_URL = System.getProperty(CAMEL_TO_BOM_MAPPING_PROPERTY, CAMEL_TO_BOM_MAPPING_DEFAULT_URL);
	public static final String CAMEL_TO_BOM_MAPPING_FUSE_7_PROPERTY = "org.jboss.tools.fuse.camel2bom.fuse7.url";
	public static final String CAMEL_TO_BOM_MAPPING_FUSE_7_DEFAULT_URL = "https://raw.githubusercontent.com/jbosstools/jbosstools-fuse/master/configuration/camel2bom.fuse7.properties";
	public static final String CAMEL_TO_BOM_MAPPING_URL_FUSE_7 = System.getProperty(CAMEL_TO_BOM_MAPPING_FUSE_7_PROPERTY, CAMEL_TO_BOM_MAPPING_FUSE_7_DEFAULT_URL);
	public static final String CAMEL_TO_BOM_MAPPING_FUSE_7_WILDFLY_PROPERTY = "org.jboss.tools.fuse.camel2bom.fuse7wildfly.url";
	public static final String CAMEL_TO_BOM_MAPPING_FUSE_7_WILDFLY_DEFAULT_URL = "https://raw.githubusercontent.com/jbosstools/jbosstools-fuse/master/configuration/camel2bom.fuse7wildfly.properties";
	public static final String CAMEL_TO_BOM_MAPPING_URL_FUSE_7_WILDFLY = System.getProperty(CAMEL_TO_BOM_MAPPING_FUSE_7_WILDFLY_PROPERTY, CAMEL_TO_BOM_MAPPING_FUSE_7_WILDFLY_DEFAULT_URL);
	public static final String FISBOM_TOFABRIC8MAVENPLUGIN_MAPPING_FUSE_7_PROPERTY = "org.jboss.tools.fuse.fisbom2fabric8MavenVersion.fuse7.url";
	public static final String FISBOM_TOFABRIC8MAVENPLUGIN_MAPPING_FUSE_7_DEFAULT_URL = "https://raw.githubusercontent.com/apupier/jbosstools-fuse/FUSETOOLS-2686-provideFISForFuse7Template/configuration/fisBomToFabric8MavenPlugin.fuse7.properties";
	public static final String FISBOM_TOFABRIC8MAVENPLUGIN_URL_MAPPING_FUSE_7 = System.getProperty(FISBOM_TOFABRIC8MAVENPLUGIN_MAPPING_FUSE_7_PROPERTY, FISBOM_TOFABRIC8MAVENPLUGIN_MAPPING_FUSE_7_DEFAULT_URL);

	public static final String FIS_MAPPING_PROPERTY = "org.jboss.tools.fuse.fismarker.url";
	public static final String FIS_MAPPING_DEFAULT_URL = "https://raw.githubusercontent.com/jbosstools/jbosstools-fuse/master/configuration/fismarker.properties";
	public static final String FIS_MAPPING_URL = System.getProperty(FIS_MAPPING_PROPERTY, FIS_MAPPING_DEFAULT_URL);
	
	public static final String KEY_CAMEL_TEST_VERSIONS = "FUSE_TOOLING_CAMEL_TEST_VERSIONS";

	private static final String FUSE_621_R0_CAMEL_VERSION = "2.15.1.redhat-621084";
	private static final String FUSE_621_R1_CAMEL_VERSION = "2.15.1.redhat-621090";
	private static final String FUSE_621_R2_CAMEL_VERSION = "2.15.1.redhat-621107";
	private static final String FUSE_621_R3_CAMEL_VERSION = "2.15.1.redhat-621117";
	private static final String FUSE_621_R4_CAMEL_VERSION = "2.15.1.redhat-621159";
	private static final String FUSE_621_R5_CAMEL_VERSION = "2.15.1.redhat-621169";
	private static final String FUSE_621_R6_CAMEL_VERSION = "2.15.1.redhat-621177";
	private static final String FUSE_621_R7_CAMEL_VERSION = "2.15.1.redhat-621186";
	private static final String FUSE_621_R8_CAMEL_VERSION = "2.15.1.redhat-621195";
	private static final String FUSE_621_R9_CAMEL_VERSION = "2.15.1.redhat-621216";
	
	private static final String FUSE_621_R0_BOM_VERSION = "6.2.1.redhat-084";
	private static final String FUSE_621_R1_BOM_VERSION = "6.2.1.redhat-090";
	private static final String FUSE_621_R2_BOM_VERSION = "6.2.1.redhat-107";
	private static final String FUSE_621_R3_BOM_VERSION = "6.2.1.redhat-117";
	private static final String FUSE_621_R4_BOM_VERSION = "6.2.1.redhat-159";
	private static final String FUSE_621_R5_BOM_VERSION = "6.2.1.redhat-169";
	private static final String FUSE_621_R6_BOM_VERSION = "6.2.1.redhat-177";
	private static final String FUSE_621_R7_BOM_VERSION = "6.2.1.redhat-186";
	private static final String FUSE_621_R8_BOM_VERSION = "6.2.1.redhat-195";
	private static final String FUSE_621_R9_BOM_VERSION = "6.2.1.redhat-216";
	
	private static final String FUSE_63_R0_CAMEL_VERSION = "2.17.0.redhat-630187";
	private static final String FUSE_63_R1_CAMEL_VERSION = "2.17.0.redhat-630224";
	private static final String FUSE_63_R2_CAMEL_VERSION = "2.17.0.redhat-630254";
	private static final String FUSE_63_R3_CAMEL_VERSION = "2.17.0.redhat-630262";
	public static final String FUSE_63_R4_CAMEL_VERSION = "2.17.0.redhat-630283";
	public static final String FUSE_63_R5_CAMEL_VERSION = "2.17.0.redhat-630310";

	private static final String FUSE_63_R0_BOM_VERSION = "6.3.0.redhat-187";
	private static final String FUSE_63_R1_BOM_VERSION = "6.3.0.redhat-224";
	private static final String FUSE_63_R2_BOM_VERSION = "6.3.0.redhat-254";
	private static final String FUSE_63_R3_BOM_VERSION = "6.3.0.redhat-262";
	static final String FUSE_63_R4_BOM_VERSION = "6.3.0.redhat-283";
	static final String FUSE_63_R5_BOM_VERSION = "6.3.0.redhat-310";
	
	public static final String FIS_20_R1_CAMEL_VERSION = "2.18.1.redhat-000012";
	public static final String FIS_20_R2_CAMEL_VERSION = "2.18.1.redhat-000015";
	public static final String FIS_20_R3_CAMEL_VERSION = "2.18.1.redhat-000021";
	
	public static final String DEFAULT_CAMEL_VERSION = FIS_20_R3_CAMEL_VERSION;
	
	public static final String CAMEL_SPRING_BOOT_STARTER = "camel-spring-boot-starter";
	public static final String CAMEL_WILDFLY = "org.wildfly.camel";
	
	public static final String CAMEL_VERSION_LATEST_COMMUNITY = "2.20.1";
	public static final String CAMEL_VERSION_LATEST_PRODUCTIZED_62 = FUSE_621_R9_CAMEL_VERSION;
	public static final String CAMEL_VERSION_LATEST_PRODUCTIZED_63 = FUSE_63_R5_CAMEL_VERSION;
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
	static final Map<String, String> CAMEL_VERSION_2_FUSE_6_BOM_MAPPING;
	static final Map<String, String> CAMEL_VERSION_2_FUSE_FIS_BOM_MAPPING;
	static final Map<String, String> CAMEL_VERSION_2_FUSE_7_BOM_MAPPING;
	static final Map<String, String> CAMEL_VERSION_2_FUSE_7_WILDFLY_BOM_MAPPING;
	static final Map<String, String> FISBOM_TO_FABRIC8MAVENPLUGIN_MAPPING;
	
	static {
		CAMEL_VERSION_2_FUSE_6_BOM_MAPPING = new HashMap<>();
		CAMEL_VERSION_2_FUSE_FIS_BOM_MAPPING = new HashMap<>();
		CAMEL_VERSION_2_FUSE_7_BOM_MAPPING = new HashMap<>();
		CAMEL_VERSION_2_FUSE_7_WILDFLY_BOM_MAPPING = new HashMap<>();
		ALL_CAMEL_CATALOG_VERSIONS = new ArrayList<>();
		OFFICIAL_SUPPORTED_CAMEL_CATALOG_VERSIONS = new ArrayList<>();
		TEST_CAMEL_VERSIONS = new ArrayList<>();
		FISBOM_TO_FABRIC8MAVENPLUGIN_MAPPING = new HashMap<>();
		
		try {
			createMappingFromOnlineFiles(CAMEL_VERSION_2_FUSE_6_BOM_MAPPING, CAMEL_TO_BOM_MAPPING_URL);
		} catch (IOException ex) {
			CamelModelServiceCoreActivator.pluginLog().logError("Unable to retrieve the Camel Version -> BOM Version mappings for Fuse 6.x from online repo. Falling back to defaults.", ex);

			// DEFAULTS
			CAMEL_VERSION_2_FUSE_6_BOM_MAPPING.put(FUSE_621_R0_CAMEL_VERSION, FUSE_621_R0_BOM_VERSION);
			CAMEL_VERSION_2_FUSE_6_BOM_MAPPING.put(FUSE_621_R1_CAMEL_VERSION, FUSE_621_R1_BOM_VERSION);
			CAMEL_VERSION_2_FUSE_6_BOM_MAPPING.put(FUSE_621_R2_CAMEL_VERSION, FUSE_621_R2_BOM_VERSION);
			CAMEL_VERSION_2_FUSE_6_BOM_MAPPING.put(FUSE_621_R3_CAMEL_VERSION, FUSE_621_R3_BOM_VERSION);
			CAMEL_VERSION_2_FUSE_6_BOM_MAPPING.put(FUSE_621_R4_CAMEL_VERSION, FUSE_621_R4_BOM_VERSION);
			CAMEL_VERSION_2_FUSE_6_BOM_MAPPING.put(FUSE_621_R5_CAMEL_VERSION, FUSE_621_R5_BOM_VERSION);
			CAMEL_VERSION_2_FUSE_6_BOM_MAPPING.put(FUSE_621_R6_CAMEL_VERSION, FUSE_621_R6_BOM_VERSION);
			CAMEL_VERSION_2_FUSE_6_BOM_MAPPING.put(FUSE_621_R7_CAMEL_VERSION, FUSE_621_R7_BOM_VERSION);
			CAMEL_VERSION_2_FUSE_6_BOM_MAPPING.put(FUSE_621_R8_CAMEL_VERSION, FUSE_621_R8_BOM_VERSION);
			CAMEL_VERSION_2_FUSE_6_BOM_MAPPING.put(FUSE_621_R9_CAMEL_VERSION, FUSE_621_R9_BOM_VERSION);
			CAMEL_VERSION_2_FUSE_6_BOM_MAPPING.put(FUSE_63_R0_CAMEL_VERSION, FUSE_63_R0_BOM_VERSION);
			CAMEL_VERSION_2_FUSE_6_BOM_MAPPING.put(FUSE_63_R1_CAMEL_VERSION, FUSE_63_R1_BOM_VERSION);
			CAMEL_VERSION_2_FUSE_6_BOM_MAPPING.put(FUSE_63_R2_CAMEL_VERSION, FUSE_63_R2_BOM_VERSION);
			CAMEL_VERSION_2_FUSE_6_BOM_MAPPING.put(FUSE_63_R3_CAMEL_VERSION, FUSE_63_R3_BOM_VERSION);
			CAMEL_VERSION_2_FUSE_6_BOM_MAPPING.put(FUSE_63_R4_CAMEL_VERSION, FUSE_63_R4_BOM_VERSION);
			CAMEL_VERSION_2_FUSE_6_BOM_MAPPING.put(FUSE_63_R5_CAMEL_VERSION, FUSE_63_R5_BOM_VERSION);
			
			OFFICIAL_SUPPORTED_CAMEL_CATALOG_VERSIONS.addAll(CAMEL_VERSION_2_FUSE_6_BOM_MAPPING.keySet());
		}
		
		try {
			createMappingFromOnlineFiles(CAMEL_VERSION_2_FUSE_7_BOM_MAPPING, CAMEL_TO_BOM_MAPPING_URL_FUSE_7);
		} catch (IOException ex) {
			CamelModelServiceCoreActivator.pluginLog().logError("Unable to retrieve the Camel Version -> BOM Version mappings for Fuse 7.x from online repo. Falling back to defaults.", ex);
		}
		
		try {
			createMappingFromOnlineFiles(CAMEL_VERSION_2_FUSE_7_WILDFLY_BOM_MAPPING, CAMEL_TO_BOM_MAPPING_URL_FUSE_7_WILDFLY);
		} catch (IOException ex) {
			CamelModelServiceCoreActivator.pluginLog().logError("Unable to retrieve the Camel Version -> BOM Version mappings for Fuse 7.x on Wildfly from online repo. Falling back to defaults.", ex);
		}
		
		try {
			createMappingFromOnlineFiles(FISBOM_TO_FABRIC8MAVENPLUGIN_MAPPING, FISBOM_TOFABRIC8MAVENPLUGIN_URL_MAPPING_FUSE_7);
		} catch (IOException ex) {
			CamelModelServiceCoreActivator.pluginLog().logError("Unable to retrieve the FIS bom Version -> Farbic8 Maven Plugin Version mappings for Fuse 7.x from online repo. Falling back to defaults.", ex);
		}
		
		try {
			Properties fisMapping = new Properties();
			URL url = new URL(FIS_MAPPING_URL);
			fisMapping.load(url.openStream());
		
			for(String camelVersion : fisMapping.stringPropertyNames()) {
				String bomVersion = fisMapping.getProperty(camelVersion);
				CAMEL_VERSION_2_FUSE_FIS_BOM_MAPPING.put(camelVersion, bomVersion);
			}
		} catch (IOException ex) {
			CamelModelServiceCoreActivator.pluginLog().logError("Unable to retrieve the FIS-ONLY Camel Versions list from online repo. Falling back to defaults.", ex);

			// DEFAULTS
			CAMEL_VERSION_2_FUSE_FIS_BOM_MAPPING.put(FIS_20_R1_CAMEL_VERSION, "2.2.170.redhat-000010");			
			CAMEL_VERSION_2_FUSE_FIS_BOM_MAPPING.put(FIS_20_R2_CAMEL_VERSION, "2.2.170.redhat-000013");
			CAMEL_VERSION_2_FUSE_FIS_BOM_MAPPING.put(FIS_20_R3_CAMEL_VERSION, "2.2.170.redhat-000019");
		}

		initCamelVersionToTest();
		
		ALL_CAMEL_CATALOG_VERSIONS.addAll(CAMEL_VERSION_2_FUSE_FIS_BOM_MAPPING.keySet());
		ALL_CAMEL_CATALOG_VERSIONS.addAll(OFFICIAL_SUPPORTED_CAMEL_CATALOG_VERSIONS);
	}

	protected static void initCamelVersionToTest() {
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
	}

	protected static void createMappingFromOnlineFiles(Map<String, String> bomMapping, String onlineUrl) throws IOException {
		Properties vMapping = new Properties();
		URL url = new URL(onlineUrl);
		vMapping.load(url.openStream());
		
		for(String camelVersion : vMapping.stringPropertyNames()) {
			String bomVersion = vMapping.getProperty(camelVersion);
			bomMapping.put(camelVersion, bomVersion);
			
			// we only add camel versions later than 2.17.0 to the supported versions map (prior versions had
			// too many errors in the catalog or not catalog at all) 
			OFFICIAL_SUPPORTED_CAMEL_CATALOG_VERSIONS.add(camelVersion);
		}
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
		return Arrays.asList(CAMEL_VERSION_2_FUSE_FIS_BOM_MAPPING.keySet().toArray(new String[CAMEL_VERSION_2_FUSE_FIS_BOM_MAPPING.size()]));
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
	 * @param monitor 
	 * @param mavenModel 
	 * @return
	 */
	public static String getBomVersionForCamelVersion(String camelVersion, IProject project, Model mavenModel, IProgressMonitor monitor) {
		org.apache.maven.model.Dependency fuseBomUsed = new OnlineArtifactVersionSearcher().retrieveAnyFuseBomUsed(mavenModel.getDependencyManagement());
		return getBomVersionForCamelVersion(camelVersion, project, monitor, fuseBomUsed);
	}

	protected static String getBomVersionForCamelVersion(String camelVersion, IProject project, IProgressMonitor monitor, org.apache.maven.model.Dependency fuseBomUsed) {
		String bomVersion = null;
		if(fuseBomUsed != null) {
			if(isBom(FuseBomFilter.BOM_FUSE_6, fuseBomUsed)) {
				bomVersion = getFuse6BomVersion(camelVersion);
			} else if(isBom(FuseBomFilter.BOM_FUSE_FIS, fuseBomUsed)) {
				bomVersion = getFuseFISBomVersion(camelVersion, project, monitor);
			} else if(isBom(FuseBomFilter.BOM_FUSE_7, fuseBomUsed)) {
				bomVersion = getFuse7BomVersion(camelVersion, project, monitor);
			} else if(isBom(FuseBomFilter.BOM_FUSE_7_WILDFLY, fuseBomUsed)) {
				bomVersion = getFuse7WildflyBomVersion(camelVersion, project, monitor);
			}
		}
		return bomVersion;
	}

	protected static String getFuse7WildflyBomVersion(String camelVersion, IProject project, IProgressMonitor monitor) {
		if (CAMEL_VERSION_2_FUSE_7_WILDFLY_BOM_MAPPING.containsKey(camelVersion)) {
			return CAMEL_VERSION_2_FUSE_7_BOM_MAPPING.get(camelVersion);
		} else {
			return new OnlineArtifactVersionSearcher().findLatestBomVersionOnAvailableRepo(project, monitor);
		}
	}

	protected static String getFuse7BomVersion(String camelVersion, IProject project, IProgressMonitor monitor) {
		if (CAMEL_VERSION_2_FUSE_7_BOM_MAPPING.containsKey(camelVersion)) {
			return CAMEL_VERSION_2_FUSE_7_BOM_MAPPING.get(camelVersion);
		} else {
			return new OnlineArtifactVersionSearcher().findLatestBomVersionOnAvailableRepo(project, monitor);
		}
	}

	protected static String getFuseFISBomVersion(String camelVersion, IProject project, IProgressMonitor monitor) {
		if(CAMEL_VERSION_2_FUSE_FIS_BOM_MAPPING.containsKey(camelVersion)) {
			return CAMEL_VERSION_2_FUSE_FIS_BOM_MAPPING.get(camelVersion);
		} else if(new ComparableVersion("2.20.0").compareTo(new ComparableVersion(camelVersion)) > 0) {
			return CAMEL_VERSION_2_FUSE_FIS_BOM_MAPPING.get(CAMEL_VERSION_LATEST_FIS_20);
		} else {
			return new OnlineArtifactVersionSearcher().findLatestBomVersionOnAvailableRepo(project, monitor);
		}
	}

	protected static String getFuse6BomVersion(String camelVersion) {
		if(CAMEL_VERSION_2_FUSE_6_BOM_MAPPING.containsKey(camelVersion)) {
			return CAMEL_VERSION_2_FUSE_6_BOM_MAPPING.get(camelVersion);
		} else {
			return CAMEL_VERSION_2_FUSE_6_BOM_MAPPING.values().stream().sorted(Comparator.reverseOrder()).findFirst().orElse(null);
		}
	}

	private static boolean isBom(org.apache.maven.model.Dependency bom, org.apache.maven.model.Dependency fuseBomUsed) {
		return bom.getGroupId().equals(fuseBomUsed.getGroupId()) && bom.getArtifactId().equals(fuseBomUsed.getArtifactId());
	}

	/**
	 * checks if the given camel version is a pure fis version
	 * 
	 * @param camelVersion
	 * @return
	 */
	public static boolean isPureFISVersion(String camelVersion) {
		return CAMEL_VERSION_2_FUSE_FIS_BOM_MAPPING.containsKey(camelVersion);
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
	
	public static CamelCatalogCoordinates getCatalogCoordinatesForProject(IProject project, IProgressMonitor monitor) {
		String camelVersion = new CamelMavenUtils().getCamelVersionFromMaven(project);
		String runtimeProvider = CamelCatalogUtils.getRuntimeprovider(project, monitor);
		if (CamelCatalogUtils.RUNTIME_PROVIDER_SPRINGBOOT.equalsIgnoreCase(runtimeProvider)) {
			return CamelCatalogUtils.getCatalogCoordinatesFor(CATALOG_SPRINGBOOT_GROUPID, CATALOG_SPRINGBOOT_ARTIFACTID, camelVersion);
		} else {
			// unsupported - fall back to Karaf
			return CamelCatalogUtils.getCatalogCoordinatesFor(CATALOG_KARAF_GROUPID, CATALOG_KARAF_ARTIFACTID, camelVersion);
		}
	}
	
	public static String getRuntimeprovider(IProject camelProject, IProgressMonitor monitor) {
		SubMonitor subMonitor = SubMonitor.convert(monitor, 2);
		if(camelProject != null){
			IMavenProjectFacade m2prj = MavenPlugin.getMavenProjectRegistry().create(camelProject, subMonitor.split(1));
			try {
				if(m2prj != null){
					MavenProject mavenProject = m2prj.getMavenProject(subMonitor.split(1));
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

	public static String getFabric8MavenPluginVersionForBomVersion(String bomVersion, IProgressMonitor monitor) {
		if(FISBOM_TO_FABRIC8MAVENPLUGIN_MAPPING.containsKey(bomVersion)) {
			return FISBOM_TO_FABRIC8MAVENPLUGIN_MAPPING.get(bomVersion);
		} else {
			org.apache.maven.model.Dependency artifactToSearch = new org.apache.maven.model.Dependency();
			artifactToSearch.setGroupId("io.fabric8");
			artifactToSearch.setArtifactId("fabric8-maven-plugin");
			try {
				return new OnlineArtifactVersionSearcher().findLatestVersion(monitor, artifactToSearch);
			} catch (CoreException e) {
				CamelModelServiceCoreActivator.pluginLog().logError(e);
				return "";
			}
		}
	}
}
