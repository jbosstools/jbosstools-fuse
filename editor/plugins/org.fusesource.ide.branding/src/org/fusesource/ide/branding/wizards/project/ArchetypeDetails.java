/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.branding.wizards.project;

import java.net.URL;

import org.fusesource.ide.commons.camel.tools.Archetype;
import org.fusesource.ide.foundation.core.util.Objects;
import org.fusesource.ide.foundation.core.util.Strings;

public class ArchetypeDetails implements Comparable<ArchetypeDetails> {

	private final Archetype archetype;
	private String groupId;
	private String artifactId;
	private String version;
	private String description;
	private String repository;
	private URL resource;
	private String fullName;

	public ArchetypeDetails(Archetype archetype) {
		this.archetype = archetype;
		this.groupId = archetype.getGroupId();
		this.artifactId = archetype.getArtifactId();
		this.version = archetype.getVersion();
		this.description = archetype.getDescription();
		this.fullName = artifactId + "-" + version + ".jar";
	}

	public boolean contains(String currentFilter) {
		return Strings.contains(currentFilter, groupId, artifactId, version);
	}

	@Override
	public int compareTo(ArchetypeDetails that) {
		int answer = Objects.compare(groupId, that.groupId);
		if (answer == 0) {
			answer = Objects.compare(artifactId, that.artifactId);
			if (answer == 0) {
				answer = Objects.compare(version, that.version);
			}			
		}
		return answer;
	}

	public Archetype getArchetype() {
		return archetype;
	}

	public String getGroupId() {
		return groupId;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public String getVersion() {
		return version;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRepository() {
		return repository;
	}

	public void setRepository(String repository) {
		this.repository = repository;
	}

	public URL getResource() {
		return resource;
	}

	public void setResource(URL resource) {
		this.resource = resource;
	}

	public String getFullName() {
		return fullName;
	}

}
