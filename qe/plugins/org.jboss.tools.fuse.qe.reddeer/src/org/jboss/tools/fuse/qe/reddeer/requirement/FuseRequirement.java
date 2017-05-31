/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.qe.reddeer.requirement;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

import org.jboss.reddeer.common.logging.Logger;
import org.jboss.reddeer.direct.preferences.Preferences;
import org.jboss.reddeer.junit.requirement.CustomConfiguration;
import org.jboss.reddeer.junit.requirement.Requirement;
import org.jboss.reddeer.requirements.server.ServerReqState;
import org.jboss.tools.fuse.qe.reddeer.requirement.FuseRequirement.Fuse;
import org.jboss.tools.fuse.qe.reddeer.requirement.ServerRequirement.Server;
import org.jboss.tools.fuse.qe.reddeer.runtime.ServerBase;

/**
 * @author tsedmik
 */
public class FuseRequirement implements Requirement<Fuse>, CustomConfiguration<FuseConfig> {

	private static final Logger LOGGER = Logger.getLogger(FuseRequirement.class);

	private FuseConfig config;
	private Fuse fuse;

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface Fuse {
		String camelVersion() default "2.17.0.redhat-630187";
		Server server() default @Server(type = {}, state = ServerReqState.PRESENT);
	}
	
	@Override
	public Class<FuseConfig> getConfigurationClass() {
		return FuseConfig.class;
	}

	@Override
	public void setConfiguration(FuseConfig config) {
		this.config = config;		
	}

	@Override
	public boolean canFulfill() {
		ServerReqType[] type = fuse.server().type();
		if (type.length == 0) {
			return true;
		}
		for (int i = 0; i < type.length; i++) {
			if (type[i].matches(config.getServerBase())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void fulfill() {
		ServerBase serverBase = config.getServerBase();
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
		serverBase.setState(fuse.server().state());
	}

	public FuseConfig getConfig() {
		return config;
	}

	@Override
	public void cleanUp() {
		// TODO Auto-generated method stub
	}

	@Override
	public void setDeclaration(Fuse fuse) {
		this.fuse = fuse;
	}
	
}
