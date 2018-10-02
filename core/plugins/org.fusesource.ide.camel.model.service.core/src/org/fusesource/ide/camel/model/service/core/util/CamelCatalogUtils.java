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
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.fusesource.ide.camel.model.service.core.util.versionmapper.CamelForFIS20ToBomMapper;
import org.fusesource.ide.camel.model.service.core.util.versionmapper.CamelForFuse6ToBomMapper;
import org.fusesource.ide.camel.model.service.core.util.versionmapper.CamelForFuse71ToBomMapper;
import org.fusesource.ide.camel.model.service.core.util.versionmapper.CamelForFuse7ToBomMapper;
import org.fusesource.ide.camel.model.service.core.util.versionmapper.CamelForFuseOnOpenShiftToBomMapper;
import org.fusesource.ide.camel.model.service.core.util.versionmapper.CamelForWildflyFuse7ToBomMapper;
import org.fusesource.ide.camel.model.service.core.util.versionmapper.DefaultVersions;
import org.fusesource.ide.camel.model.service.core.util.versionmapper.FISBomToFabric8MavenPluginMapper;
import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.foundation.core.util.VersionUtil;

/**
 * collection of camel catalog related util methods
 * 
 * @author lhein
 */
public class CamelCatalogUtils {

	public static final String KEY_CAMEL_TEST_VERSIONS = "FUSE_TOOLING_CAMEL_TEST_VERSIONS";

	public static final String DEFAULT_CAMEL_VERSION = new DefaultVersions().getDefaultCamelVersion();
	
	public static final String CAMEL_SPRING_BOOT_STARTER = "camel-spring-boot-starter";
	public static final String CAMEL_WILDFLY = "org.wildfly.camel";
	
	public static final String CAMEL_VERSION_LATEST_COMMUNITY = "2.22.0";
	public static final String CAMEL_VERSION_LATEST_PRODUCTIZED_62 = CamelForFuse6ToBomMapper.FUSE_621_R9_CAMEL_VERSION;
	public static final String CAMEL_VERSION_LATEST_PRODUCTIZED_63 = CamelForFuse6ToBomMapper.FUSE_63_R8_CAMEL_VERSION;
	public static final String CAMEL_VERSION_LATEST_FIS_20 = CamelForFIS20ToBomMapper.FIS_20_R6_CAMEL_VERSION;
	
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
	
	private static final Set<String> OFFICIAL_SUPPORTED_CAMEL_CATALOG_VERSIONS = new HashSet<>();
	private static final Set<String> ALL_CAMEL_CATALOG_VERSIONS = new HashSet<>();
	private static final Set<String> TEST_CAMEL_VERSIONS = new HashSet<>();
	static final Map<String, String> CAMEL_VERSION_2_FUSE_6_BOM_MAPPING = new CamelForFuse6ToBomMapper().getMapping();
	static final Map<String, String> CAMEL_VERSION_2_FUSE_FIS_BOM_MAPPING = new CamelForFIS20ToBomMapper().getMapping();
	static final Map<String, String> CAMEL_VERSION_2_FUSE_ON_OPENSHIFT_BOM_MAPPING = new CamelForFuseOnOpenShiftToBomMapper().getMapping();
	static final Map<String, String> CAMEL_VERSION_2_FUSE_7_BOM_MAPPING = new CamelForFuse7ToBomMapper().getMapping();
	static final Map<String, String> CAMEL_VERSION_2_FUSE_7_WILDFLY_BOM_MAPPING = new CamelForWildflyFuse7ToBomMapper().getMapping();
	static final Map<String, String> CAMEL_VERSION_2_FUSE_71_BOM_MAPPING = new CamelForFuse71ToBomMapper().getMapping();
	static final Map<String, String> FISBOM_TO_FABRIC8MAVENPLUGIN_MAPPING = new FISBomToFabric8MavenPluginMapper().getMapping();
	
	static {
		OFFICIAL_SUPPORTED_CAMEL_CATALOG_VERSIONS.addAll(CAMEL_VERSION_2_FUSE_6_BOM_MAPPING.keySet());
		OFFICIAL_SUPPORTED_CAMEL_CATALOG_VERSIONS.addAll(CAMEL_VERSION_2_FUSE_7_BOM_MAPPING.keySet());
		OFFICIAL_SUPPORTED_CAMEL_CATALOG_VERSIONS.addAll(CAMEL_VERSION_2_FUSE_7_WILDFLY_BOM_MAPPING.keySet());
		OFFICIAL_SUPPORTED_CAMEL_CATALOG_VERSIONS.addAll(CAMEL_VERSION_2_FUSE_FIS_BOM_MAPPING.keySet());
		OFFICIAL_SUPPORTED_CAMEL_CATALOG_VERSIONS.addAll(CAMEL_VERSION_2_FUSE_ON_OPENSHIFT_BOM_MAPPING.keySet());
		OFFICIAL_SUPPORTED_CAMEL_CATALOG_VERSIONS.addAll(CAMEL_VERSION_2_FUSE_71_BOM_MAPPING.keySet());
		
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
			TEST_CAMEL_VERSIONS.add(CamelForFuseOnOpenShiftToBomMapper.FUSE_710_CAMEL_VERSION);
		}
	}

	private CamelCatalogUtils() {
		// utility class
	}
	
	public static List<String> getOfficialSupportedCamelCatalogVersions() {
		return new ArrayList<>(OFFICIAL_SUPPORTED_CAMEL_CATALOG_VERSIONS);
	}
	
	public static List<String> getAllCamelCatalogVersions() {
		return new ArrayList<>(ALL_CAMEL_CATALOG_VERSIONS);
	}
	
	public static List<String> getCamelVersionsToTestWith() {
		return new ArrayList<>(TEST_CAMEL_VERSIONS);
	}
	
	public static List<String> getPureFISVersions() {
		return new ArrayList<>(CAMEL_VERSION_2_FUSE_FIS_BOM_MAPPING.keySet());
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
	public static String getBomVersionForCamelVersion(String camelVersion, Model mavenModel, IProgressMonitor monitor) {
		org.apache.maven.model.Dependency fuseBomUsed = new OnlineArtifactVersionSearcher().retrieveAnyFuseBomUsed(mavenModel.getDependencyManagement());
		return getBomVersionForCamelVersion(camelVersion, monitor, fuseBomUsed);
	}

	public static String getBomVersionForCamelVersion(String camelVersion, IProgressMonitor monitor, org.apache.maven.model.Dependency fuseBomUsed) {
		String bomVersion = null;
		if (camelVersion != null) {
			if(fuseBomUsed != null) {
				if(isBom(FuseBomFilter.BOM_FUSE_6, fuseBomUsed)) {
					bomVersion = getFuse6BomVersion(camelVersion);
				} else if(isBom(FuseBomFilter.BOM_FUSE_FIS, fuseBomUsed)) {
					bomVersion = getFuseFISBomVersion(camelVersion, fuseBomUsed, monitor);
				} else if(isBom(FuseBomFilter.BOM_FUSE_7, fuseBomUsed)) {
					bomVersion = getFuse7BomVersion(camelVersion, fuseBomUsed, monitor);
				} else if(isBom(FuseBomFilter.BOM_FUSE_7_WILDFLY, fuseBomUsed)) {
					bomVersion = getFuse7WildflyBomVersion(camelVersion, fuseBomUsed, monitor);
				} else if(isBom(FuseBomFilter.BOM_FUSE_71_WILDFLY, fuseBomUsed)
						|| isBom(FuseBomFilter.BOM_FUSE_71_KARAF, fuseBomUsed)
						|| isBom(FuseBomFilter.BOM_FUSE_71_SPRINGBOOT, fuseBomUsed)) {
					bomVersion = getFuse71BomVersion(camelVersion, fuseBomUsed, monitor);
				}
			}
		} else {
			return null;
		}
		return bomVersion;
	}
	
	private static String getFuse71BomVersion(String camelVersion, org.apache.maven.model.Dependency fuseBomUsed,
			IProgressMonitor monitor) {
		if (CAMEL_VERSION_2_FUSE_71_BOM_MAPPING.containsKey(camelVersion)) {
			return CAMEL_VERSION_2_FUSE_71_BOM_MAPPING.get(camelVersion);
		} else {
			return new OnlineArtifactVersionSearcher().findLatestVersion(monitor, fuseBomUsed);
		}
	}

	protected static String getFuse7WildflyBomVersion(String camelVersion, org.apache.maven.model.Dependency fuseBomUsed, IProgressMonitor monitor) {
		if (CAMEL_VERSION_2_FUSE_7_WILDFLY_BOM_MAPPING.containsKey(camelVersion)) {
			return CAMEL_VERSION_2_FUSE_7_WILDFLY_BOM_MAPPING.get(camelVersion);
		} else {
			return new OnlineArtifactVersionSearcher().findLatestVersion(monitor, fuseBomUsed);
		}
	}

	protected static String getFuse7BomVersion(String camelVersion, org.apache.maven.model.Dependency fuseBomUsed, IProgressMonitor monitor) {
		if (CAMEL_VERSION_2_FUSE_7_BOM_MAPPING.containsKey(camelVersion)) {
			return CAMEL_VERSION_2_FUSE_7_BOM_MAPPING.get(camelVersion);
		} else {
			return new OnlineArtifactVersionSearcher().findLatestVersion(monitor, fuseBomUsed);
		}
	}

	protected static String getFuseFISBomVersion(String camelVersion, org.apache.maven.model.Dependency fuseBomUsed, IProgressMonitor monitor) {
		if(CAMEL_VERSION_2_FUSE_FIS_BOM_MAPPING.containsKey(camelVersion)) {
			return CAMEL_VERSION_2_FUSE_FIS_BOM_MAPPING.get(camelVersion);
		} else if (CAMEL_VERSION_2_FUSE_ON_OPENSHIFT_BOM_MAPPING.containsKey(camelVersion)) {
			return CAMEL_VERSION_2_FUSE_ON_OPENSHIFT_BOM_MAPPING.get(camelVersion);
		} else if(new VersionUtil().isStrictlyLowerThan2200(camelVersion)) {
			return CAMEL_VERSION_2_FUSE_FIS_BOM_MAPPING.get(CAMEL_VERSION_LATEST_FIS_20);
		} else {
			return new OnlineArtifactVersionSearcher().findLatestVersion(monitor, fuseBomUsed);
		}
	}

	protected static String getFuse6BomVersion(String camelVersion) {
		if(CAMEL_VERSION_2_FUSE_6_BOM_MAPPING.containsKey(camelVersion)) {
			return CAMEL_VERSION_2_FUSE_6_BOM_MAPPING.get(camelVersion);
		} else if(new VersionUtil().isStrictlyGreaterThan("2.17.0", camelVersion)) {
			return getLatest621BomVersion();
		} else {
			return CAMEL_VERSION_2_FUSE_6_BOM_MAPPING.values().stream().sorted(Comparator.reverseOrder()).findFirst().orElse(null);
		}
	}
	
	public static String getLatest621BomVersion() {
		return getLatestBuild("6.2.1");
	}
	
	public static String getLatest630BomVersion() {
		return getLatestBuild("6.3.0");
	}
	
	protected static String getLatestBuild(String fuseVersion) {
		return CAMEL_VERSION_2_FUSE_6_BOM_MAPPING.values().stream()
				.filter(version -> version.startsWith(fuseVersion))
				.sorted(Comparator.reverseOrder()).findFirst().orElse(null);
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
					.anyMatch(dependency -> dependencyToCheck.equals(dependency.getArtifactId()));
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
			return new OnlineArtifactVersionSearcher().findLatestVersion(monitor, artifactToSearch);
		}
	}
}
