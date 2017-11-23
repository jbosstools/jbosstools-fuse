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

import java.io.File;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.eclipse.reddeer.common.exception.RedDeerException;
import org.eclipse.reddeer.junit.requirement.AbstractConfigurableRequirement;
import org.jboss.tools.fuse.reddeer.requirement.CamelExampleRequirement.CamelExample;

/**
 * Requirement for an existing Camel example. The camel example must be one of
 * the examples at <a href=
 * "https://github.com/apache/camel/tree/master/examples">https://github.com/apache/camel/tree/master/examples</a>.
 * The appropriate yaml file should look like
 * 
 * <pre>
 * org.jboss.tools.fuse.reddeer.requirement.CamelExampleRequirement.CamelExample:
 * - name: camel-example-spring-boot
 *   version: 2.20.1
 *   jarFile: /tmp/camel-example-spring-boot-2.20.1.jar
 *   # optional
 *   jolokiaJarFile: /tmp/jolokia-jvm-1.3.7-agent.jar
 * </pre>
 * 
 * At the moment we support only stand-alone jar file such as spring-boot.
 * 
 * @author Andrej Podhradsky (apodhrad@redhat.com)
 *
 */
public class CamelExampleRequirement extends AbstractConfigurableRequirement<CamelExampleConfiguration, CamelExample> {

	private CamelExampleRunner runner;

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface CamelExample {
		boolean useJolokia() default false;
	}

	@Override
	public Class<CamelExampleConfiguration> getConfigurationClass() {
		return CamelExampleConfiguration.class;
	}

	@Override
	public void fulfill() {
		CamelExampleConfiguration config = getConfiguration();
		if (config.getJarFile() != null) {
			runner = new CamelExampleRunner(getConfiguration().getJarFile(), getDeclaration().useJolokia());
			if (getDeclaration().useJolokia()) {
				File jolokiaJarFile = config.getJolokiaJarFile();
				if (jolokiaJarFile == null) {
					throw new RedDeerException("No jolokia agent was specified");
				}
				runner.setJavaAgent(jolokiaJarFile.getAbsolutePath());
			}
			runner.run();
		}
	}

	@Override
	public void cleanUp() {
		if (runner != null && runner.isRunning()) {
			runner.stop();
		}
	}

	public CamelExampleRunner getRunner() {
		return runner;
	}

}
