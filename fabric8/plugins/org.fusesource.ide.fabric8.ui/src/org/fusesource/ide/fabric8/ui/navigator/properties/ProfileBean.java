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

package org.fusesource.ide.fabric8.ui.navigator.properties;

import java.util.List;
import java.util.Map;

import org.fusesource.ide.commons.util.BeanSupport;
import org.fusesource.ide.commons.util.Strings;
import org.fusesource.ide.fabric8.core.dto.ProfileDTO;


public class ProfileBean extends BeanSupport {
	private final ProfileDTO profile;

	public ProfileBean(ProfileDTO profile) {
		this.profile = profile;
	}

	public Map<String, Map<String, String>> getConfigurations() {
		return profile.getConfigurations();
	}

	public String getId() {
		return profile.getId();
	}

	public String getVersion() {
		return Strings.getOrElse(profile.getVersionId(), "");
	}

	public List<ProfileDTO> getParents() {
		return profile.getParents();
	}

	public boolean isOverlay() {
		return profile.isOverlay();
	}

	public String getParentIds() {
		StringBuffer buffer = new StringBuffer();
		for (String parentId : profile.getParentIDs()) {
			buffer.append(parentId);
			buffer.append(" ");
		}
		return buffer.toString();
	}
}
