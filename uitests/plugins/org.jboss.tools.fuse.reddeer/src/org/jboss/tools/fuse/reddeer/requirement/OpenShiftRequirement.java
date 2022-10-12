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
package org.jboss.tools.fuse.reddeer.requirement;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.eclipse.reddeer.junit.requirement.AbstractConfigurableRequirement;
import org.jboss.tools.fuse.reddeer.requirement.OpenShiftRequirement.OpenShift;
import org.jboss.tools.fuse.reddeer.view.OpenShiftExplorer;
import org.jboss.tools.fuse.reddeer.wizard.NewOpenShiftConnectionWizard;

/**
 * Requirement for an OpenShift connection. The appropriate yaml file should look like as follows
 * 
 * <pre>
 * org.jboss.tools.fuse.reddeer.requirement.OpenShiftRequirement.OpenShift:
 * - host: 192.168.120.40
 *   port: 8443
 *   oc: /home/tsedmik/devel/git/server-installer/cdk/target/oc
 *   username: developer
 *   password: developer
 *   protocol: Basic
 * </pre>
 * 
 * @author tsedmik
 */
public class OpenShiftRequirement extends AbstractConfigurableRequirement<OpenShiftConfiguration, OpenShift> {

	private OpenShiftConfiguration config;

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface OpenShift {

	}

	@Override
	public Class<OpenShiftConfiguration> getConfigurationClass() {
		return OpenShiftConfiguration.class;
	}

	@Override
	public void setConfiguration(OpenShiftConfiguration config) {
		this.config = config;
	}

	@Override
	public OpenShiftConfiguration getConfiguration() {
		return config;
	}

	@Override
	public void fulfill() {

		String connection_url = "https://" + config.getHost() + ":" + config.getPort();
		String connection_name = config.getUsername() + " " + connection_url;

		// check whether the connection is not already present
		OpenShiftExplorer explorer = new OpenShiftExplorer();
		explorer.open();
		if (explorer.isConnectionPresent(connection_name)) {
			return;
		}

		// create a new connection in OpenShift Explorer
		explorer.clickNewConnection();
		NewOpenShiftConnectionWizard wizard = new NewOpenShiftConnectionWizard();
		wizard.setSelectionServer(connection_url);
		wizard.setSelectionProtocol(config.getProtocol());
		wizard.setTextUsername(config.getUsername());
		wizard.setTextPassword(config.getPassword());
		if(config.getOverrideOC()) {
			wizard.clickAdvancedBTN();
			wizard.toggleOverrideOcLocationCHB(true);
			wizard.setOCLocation(config.getOc());
		}
		wizard.finish();
	}

	@Override
	public void cleanUp() {

	}
}
