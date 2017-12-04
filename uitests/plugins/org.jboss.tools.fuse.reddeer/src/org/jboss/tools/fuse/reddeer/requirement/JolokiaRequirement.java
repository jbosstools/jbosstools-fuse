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
package org.jboss.tools.fuse.reddeer.requirement;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.eclipse.reddeer.junit.requirement.AbstractConfigurableRequirement;
import org.jboss.tools.fuse.reddeer.requirement.JolokiaRequirement.Jolokia;

/**
 * Requirement for a Jolokia connection. The appropriate yaml file should look
 * like as follows
 * 
 * <pre>
 * org.jboss.tools.fuse.reddeer.requirement.JolokiaRequirement.Jolokia:
 * - name: My Jolokia
 *   jolokiaJarFile: /tmp/jolokia-jvm-1.3.7-agent.jar
 *   host: localhost
 *   port: 8778
 * </pre>
 * 
 * @author Andrej Podhradsky (apodhrad@redhat.com)
 *
 */
public class JolokiaRequirement extends AbstractConfigurableRequirement<JolokiaConfiguration, Jolokia> {

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface Jolokia {

	}

	@Override
	public Class<JolokiaConfiguration> getConfigurationClass() {
		return JolokiaConfiguration.class;
	}

	@Override
	public void fulfill() {

	}

	@Override
	public void cleanUp() {

	}
}
