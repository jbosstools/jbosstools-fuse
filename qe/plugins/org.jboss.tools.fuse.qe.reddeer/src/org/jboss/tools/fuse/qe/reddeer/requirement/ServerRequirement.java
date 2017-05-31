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
import org.jboss.tools.fuse.qe.reddeer.requirement.ServerRequirement.Server;
import org.jboss.tools.fuse.qe.reddeer.runtime.ServerBase;

/**
 * 
 * @author apodhrad
 * 
 */

public class ServerRequirement implements Requirement<Server>, CustomConfiguration<ServerConfig> {

	private static final Logger LOGGER = Logger.getLogger(ServerRequirement.class);

	private ServerConfig config;
	private Server server;

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface Server {
		ServerReqType[] type() default ServerReqType.ANY;

		ServerReqState state() default ServerReqState.RUNNING;

		String[] property() default "";

		ServerConnType[] connectionType() default ServerConnType.ANY;
	}

	@Override
	public boolean canFulfill() {
		String[] requiredProperties = server.property();
		for (String requiredProperty : requiredProperties) {
			if (requiredProperty != null && !requiredProperty.isEmpty()
					&& config.getServerBase().getProperties(requiredProperty).isEmpty()) {
				return false;
			}
		}

		boolean serverTypeMatches = false;
		boolean connectionTypeMatches = false;

		ServerReqType[] type = server.type();
		if (type.length == 0) {
			serverTypeMatches = true;
		}
		for (int i = 0; i < type.length; i++) {
			if (type[i].matches(config.getServerBase())) {
				serverTypeMatches = true;
			}
		}

		ServerConnType[] connTypes = server.connectionType();
		if (connTypes.length == 0) {
			connectionTypeMatches = true;
		}
		for (int i = 0; i < connTypes.length; i++) {
			if (connTypes[i].matches(config.getServerBase())) {
				connectionTypeMatches = true;
			}
		}
		return serverTypeMatches && connectionTypeMatches;
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
		serverBase.setState(server.state());
	}

	@Override
	public void setDeclaration(Server server) {
		this.server = server;
	}

	@Override
	public Class<ServerConfig> getConfigurationClass() {
		return ServerConfig.class;
	}

	@Override
	public void setConfiguration(ServerConfig config) {
		this.config = config;
	}

	public ServerConfig getConfig() {
		return this.config;
	}

	@Override
	public void cleanUp() {
		// TODO cleanUp()

	}
}
