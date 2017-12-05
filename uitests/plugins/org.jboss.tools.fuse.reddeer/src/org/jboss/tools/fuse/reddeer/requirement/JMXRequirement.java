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
import org.jboss.tools.fuse.reddeer.requirement.JMXRequirement.JMX;

/**
 * Requirement for a JMX connection. The appropriate yaml file should look like
 * as follows
 * 
 * <pre>
 * org.jboss.tools.fuse.reddeer.requirement.JMXRequirement.JMX:
 * - name: My JMX
 *   host: localhost
 *   port: 9010
 *   username: admin
 *   password: admin123
 * </pre>
 * 
 * @author Andrej Podhradsky (apodhrad@redhat.com)
 *
 */
public class JMXRequirement extends AbstractConfigurableRequirement<JMXConfiguration, JMX> {

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface JMX {

	}

	@Override
	public Class<JMXConfiguration> getConfigurationClass() {
		return JMXConfiguration.class;
	}

	@Override
	public void fulfill() {

	}

	@Override
	public void cleanUp() {

	}

}
