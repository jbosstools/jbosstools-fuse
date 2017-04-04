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
package org.fusesource.ide.camel.model.service.core.catalog.cache;

import org.fusesource.ide.camel.model.service.core.catalog.Dependency;
import org.fusesource.ide.camel.model.service.core.util.CamelCatalogUtils;

/**
 * @author lhein
 */
public class CamelCatalogCoordinates {
	private String groupId;
	private String artifactId;
	private String version;
	
	/**
	 * creates new camel catalog coordinates object from the given parameters
	 * 
	 * @param groupId		the maven group id
	 * @param artifactId	the maven artifact id
	 * @param version		the maven version
	 */
	public CamelCatalogCoordinates(String groupId, String artifactId, String version) {
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;
	}
	
	/**
	 * creates new camel catalog coordinates object from the given parameter
	 * 
	 * @param dependency	the maven dependency
	 */
	public CamelCatalogCoordinates(Dependency dependency) {
		this.groupId = dependency.getGroupId();
		this.artifactId = dependency.getArtifactId();
		this.version = dependency.getVersion();
		org.apache.maven.model.Dependency dep = new org.apache.maven.model.Dependency();
		dep.setGroupId(groupId);
		dep.setArtifactId(artifactId);
		dep.setVersion(version);
		CamelCatalogUtils.getRuntimeProvider(dep);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object arg0) {
		if (arg0 instanceof CamelCatalogCoordinates) {
			CamelCatalogCoordinates other = (CamelCatalogCoordinates)arg0;
			return  getGroupId().equals(other.getGroupId()) &&
					getArtifactId().equals(other.getArtifactId()) &&
					getVersion().equals(other.getVersion());
		}
		return false; 
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return String.format("%s:%s:%s", getGroupId(), getArtifactId(), getVersion()).hashCode();
	}
	
	/**
	 * @return the groupId
	 */
	public String getGroupId() {
		return this.groupId;
	}
	
	/**
	 * @param groupId the groupId to set
	 */
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	
	/**
	 * @return the artifactId
	 */
	public String getArtifactId() {
		return this.artifactId;
	}
	
	/**
	 * @param artifactId the artifactId to set
	 */
	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}
	
	/**
	 * @return the version
	 */
	public String getVersion() {
		return this.version;
	}
	
	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}
}