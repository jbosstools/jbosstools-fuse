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

package org.fusesource.ide.fabric.navigator;

import java.util.Map;

import io.fabric8.api.Profile;
import org.fusesource.ide.commons.util.BeanSupport;
import org.fusesource.ide.commons.util.Strings;


public class ProfileBean extends BeanSupport {
	private final Profile profile;

	public ProfileBean(Profile profile) {
		this.profile = profile;
	}

	public Map<String, Map<String, String>> getConfigurations() {
		return profile.getConfigurations();
	}

	public String getId() {
		return profile.getId();
	}

	public String getVersion() {
		return Strings.getOrElse(profile.getVersion(), "");
	}

	public Profile[] getParents() {
		return profile.getParents();
	}

	public boolean isOverlay() {
		return profile.isOverlay();
	}

	public String getParentIds() {
		StringBuffer buffer = new StringBuffer();
		for (Profile parent : getParents()) {
			buffer.append(parent.getId());
			buffer.append(" ");
		}
		return buffer.toString();
	}
}
