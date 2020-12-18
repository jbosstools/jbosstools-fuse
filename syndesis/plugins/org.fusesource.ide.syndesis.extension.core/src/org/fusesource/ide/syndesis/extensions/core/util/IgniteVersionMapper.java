/******************************************************************************* 
 * Copyright (c) 2018 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.fusesource.ide.syndesis.extensions.core.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.maven.model.Dependency;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.fusesource.ide.camel.model.service.core.util.OnlineArtifactVersionSearcher;
import org.fusesource.ide.foundation.core.util.OnlineVersionMapper;
import org.fusesource.ide.foundation.core.util.Strings;

public class IgniteVersionMapper extends OnlineVersionMapper {
	
	private static final String URL_IGNITE_VERSIONS_FILE = BASE_REPO_CONFIG_URI + "igniteVersionToDisplayName.properties";
	private static final String SNAPSHOT_POSTFIX = "-SNAPSHOT";

	public IgniteVersionMapper() {
		super("org.jboss.tools.fuse.ignite.url", IgniteVersionMapper.URL_IGNITE_VERSIONS_FILE);
	}

	@Override
	protected Map<String, String> createFallbackMapping() {
		Map<String, String> mapping = new HashMap<>();
		mapping.put("1.3.10.fuse-000001-redhat-1", "1.3.10.fuse-000001-redhat-1");
		mapping.put("1.3.12.fuse-000002-redhat-1", "1.3.12.fuse-000001-redhat-2");
		mapping.put("1.4.8.fuse-710001-redhat-00001", "1.4.8.fuse-710001-redhat-00001 (7.1.0 GA)");
		mapping.put("1.5.8.fuse-720001-redhat-00002", "1.5.8.fuse-720001-redhat-00002 (7.2.0 GA)");
		mapping.put("1.6.11.fuse-730003-redhat-00001", "1.6.11.fuse-730003-redhat-00001 (7.3.0 GA)");
		mapping.put("1.6.13.fuse-731002-redhat-00001", "1.6.13.fuse-731002-redhat-00001 (7.3.1 GA)");
		mapping.put("1.7.13.fuse-740001-redhat-00002", "1.7.13.fuse-740001-redhat-00002 (7.4.0.GA)");
		mapping.put("1.8.6.fuse-750001-redhat-00001", "1.8.6.fuse-750001-redhat-00001 (7.5.0.GA)");
		mapping.put("1.9.0.fuse-760020-redhat-00001", "1.9.0.fuse-760020-redhat-00001 (7.6.0 GA)");
		mapping.put("1.10.0.fuse-770020-redhat-00001", "1.10.0.fuse-770020-redhat-00001 (7.7.0 GA)");
		mapping.put("1.11.0.fuse-780011-redhat-00001", "1.11.0.fuse-780011-redhat-00001 (7.8.0 GA)");
		return mapping;
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.foundation.core.util.OnlineVersionMapper#consolidateMapping(java.util.Map)
	 */
	@Override
	protected Map<String, String> consolidateMapping(Map<String, String> mapping) {
		Map<String, String> map = super.consolidateMapping(mapping);
		consolidateSnapshots(map, new NullProgressMonitor());		
		return map;
	}
	
	public static void consolidateSnapshots(Map<String, String> map, IProgressMonitor monitor) {
		// check for -SNAPSHOT versions
		Map<String, String> toAdd = new HashMap<>();
		Iterator<Entry<String, String>> it = map.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, String> entry = it.next();
			String version = entry.getKey();
			String label = entry.getValue();
			if (isSnapshot(version) && !SyndesisVersionUtil.isSyndesisVersionExisting(version, monitor)) {
				// snapshot doesn't exist - try to replace with latest available version
				String newVersion = new OnlineArtifactVersionSearcher().findLatestVersion(monitor, getBOMDependency(), getVersionWithoutSnapshot(version)+".");
				if (!Strings.isBlank(newVersion)) {
					// add the new map entry
					toAdd.put(newVersion, label.replaceAll(version, newVersion));
				}
				// remove the old entry
				it.remove();
			}
		}
		for (Entry<String, String> e : toAdd.entrySet()) {
			map.put(e.getKey(), e.getValue());
		}
	}
	
	private static boolean isSnapshot(String value) {
		return value.trim().toUpperCase().endsWith(SNAPSHOT_POSTFIX);
	}
	
	private static String getVersionWithoutSnapshot(String version) {
		return version.replaceAll(SNAPSHOT_POSTFIX, "");
	}
	
	private static Dependency getBOMDependency() {
		Dependency bomDep = new Dependency();
		bomDep.setGroupId(SyndesisVersionUtil.BOM_GROUPID);
		bomDep.setArtifactId(SyndesisVersionUtil.BOM_ARTIFACTID);
		bomDep.setType(SyndesisVersionUtil.BOM_TYPE);
		return bomDep;
	}
}
