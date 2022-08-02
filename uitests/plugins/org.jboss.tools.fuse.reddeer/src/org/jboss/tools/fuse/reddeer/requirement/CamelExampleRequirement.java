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
 * "https://github.com/apache/camel/tree/main/examples">https://github.com/apache/camel/tree/main/examples</a>.
 * <p>
 * Optionally, you can specify configuration for JMX or Jolokia. If you do not
 * specify JMX configuration then the camel example will not be accessible via
 * JMX connection (only from 'Local Processes'). At the moment, we do not
 * support any authentication.
 * <p>
 * The appropriate yaml file should look like as follows
 * 
 * <pre>
 * org.jboss.tools.fuse.reddeer.requirement.CamelExampleRequirement.CamelExample:
 * - name: camel-example-spring-boot
 *   version: 2.20.1
 *   jarFile: /tmp/camel-example-spring-boot-2.20.1.jar
 *   # optional
 *   jmxConfiguration:
 *     name: My JMX
 *     host: localhost
 *     port: 9010
 *   # optional
 *   jolokiaConfiguration:
 *     name: My Jolokia
 *     jolokiaJarFile: /tmp/jolokia-jvm-1.3.7-agent.jar
 *     host: localhost
 *     port: 8778
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
				JolokiaConfiguration jolokiaConfig = config.getJolokiaConfiguration();
				if (jolokiaConfig == null) {
					throw new RedDeerException("No jolokia configuration was specified");
				}
				File jolokiaJarFile = jolokiaConfig.getJolokiaJarFile();
				StringBuilder jolokiaAgent = new StringBuilder(jolokiaJarFile.getAbsolutePath());
				if (!jolokiaJarFile.exists()) {
					throw new RedDeerException("Jolokia agent '" + jolokiaAgent + "' doesn't exist");
				}
				jolokiaAgent.append("=");
				jolokiaAgent.append("host=").append(jolokiaConfig.getHost());
				jolokiaAgent.append(",");
				jolokiaAgent.append("port=").append(jolokiaConfig.getPort());
				runner.setJavaAgent(jolokiaAgent.toString());
			} else if (config.getJmxConfiguration() != null) {
				JMXConfiguration jmxConfig = config.getJmxConfiguration();
				runner.setSystemProperty("java.rmi.server.hostname", jmxConfig.getHost());
				runner.setSystemProperty("com.sun.management.jmxremote.port", jmxConfig.getPort());
				runner.setSystemProperty("com.sun.management.jmxremote", "true");
				runner.setSystemProperty("com.sun.management.jmxremote.local.only", "true");
				runner.setSystemProperty("com.sun.management.jmxremote.authenticate", "false");
				runner.setSystemProperty("com.sun.management.jmxremote.ssl", "false");
			}
			System.setProperty("java.home", config.getRequiredJRE());
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
