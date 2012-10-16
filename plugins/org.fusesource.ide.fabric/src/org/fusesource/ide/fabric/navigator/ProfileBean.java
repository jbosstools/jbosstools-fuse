package org.fusesource.ide.fabric.navigator;

import java.util.Map;

import org.fusesource.fabric.api.Profile;
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
