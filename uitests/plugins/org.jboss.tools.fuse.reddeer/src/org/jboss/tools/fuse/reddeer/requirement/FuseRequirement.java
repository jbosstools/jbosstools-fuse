/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.reddeer.requirement;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

import org.eclipse.reddeer.common.logging.Logger;
import org.eclipse.reddeer.direct.preferences.Preferences;
import org.eclipse.reddeer.junit.requirement.ConfigurableRequirement;
import org.eclipse.reddeer.requirements.server.ServerRequirementState;
import org.jboss.tools.fuse.reddeer.requirement.FuseRequirement.Fuse;
import org.jboss.tools.fuse.reddeer.runtime.ServerBase;

/**
 * @author tsedmik
 */
public class FuseRequirement implements ConfigurableRequirement<FuseConfiguration, Fuse> {

	public static final Logger LOGGER = Logger.getLogger(FuseRequirement.class);

	private FuseConfiguration config;
	private Fuse fuse;

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface Fuse {
		ServerRequirementState state() default ServerRequirementState.PRESENT;
	}

	@Override
	public Class<FuseConfiguration> getConfigurationClass() {
		return FuseConfiguration.class;
	}

	@Override
	public void setConfiguration(FuseConfiguration config) {
		this.config = config;
	}

	@Override
	public FuseConfiguration getConfiguration() {
		return config;
	}

	@Override
	public void fulfill() {
		ServerBase serverBase = config.getServer();
		List<String> preferences = serverBase.getProperties("preference");
		for (String preference : preferences) {
			// Example: org.eclipse.m2e.core/eclipse.m2.userSettingsFile=settings.xml
			if (preference.matches("([^/=]+)/([^/=]+)=.+")) {
				String[] parts = preference.split("=");
				String key = parts[0];
				String value = parts[1];
				parts = key.split("/");
				String plugin = parts[0];
				String pluginKey = parts[1];
				Preferences.set(plugin, pluginKey, value);
			} else {
				LOGGER.warn("Preference '" + preference + "' doesn't match the patter. SKIPPED");
			}
		}
		if (!serverBase.exists()) {
			serverBase.create();
		}
		serverBase.setState(fuse.state());
	}

	@Override
	public void cleanUp() {
		// TODO Auto-generated method stub
	}

	@Override
	public void setDeclaration(Fuse fuse) {
		this.fuse = fuse;
	}

	@Override
	public Fuse getDeclaration() {
		return fuse;
	}

}
