/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.foundation.ui.archetypes;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

/**
 * A simple DTO
 */
@XmlRootElement(name = "archetype")
public class Archetype {

    private String groupId = "";
    private String artifactId = "";
    private String version = "";
    private String repository = "";
    private String description = "";

    public Archetype() {
    }

    public Archetype(String groupId, String artifactId, String version, String description) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.description = description;
    }

    /**
	 * @return the groupId
	 */
    @XmlAttribute(name = "groupId")
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
	@XmlAttribute(name = "artifactId")
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
	@XmlAttribute(name = "version")
	public String getVersion() {
		return this.version;
	}
	
	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}
	
	/**
	 * @return the description
	 */
	@XmlValue
	public String getDescription() {
		return this.description;
	}
	
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * @return the repository
	 */
	@XmlAttribute(name = "repository")
	public String getRepository() {
		return this.repository;
	}
	
	/**
	 * @param repository the repository to set
	 */
	public void setRepository(String repository) {
		this.repository = repository;
	}
    
    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Archtype(" + groupId + ":" + artifactId + ":" + version + ")";
    }

}
