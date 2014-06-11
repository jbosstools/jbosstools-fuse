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

package org.fusesource.ide.fabric;

import io.fabric8.api.FabricException;
import io.fabric8.service.JmxTemplateSupport;
import io.fabric8.service.LocalJMXConnector;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;

import org.jboss.tools.jmx.core.IConnectionWrapper;
import org.jboss.tools.jmx.core.IJMXRunnable;
import org.jboss.tools.jmx.core.JMXException;


public class JmxPluginJmxTemplate extends JmxTemplateSupport {

	private final IConnectionWrapper connectionWrapper;

	public JmxPluginJmxTemplate(IConnectionWrapper connectionWrapper) {
		this.connectionWrapper = connectionWrapper;
	}

	@Override
	public <T> T execute(final JmxConnectorCallback<T> callback) {
		final Object[] answerHolder = new Object[1];

		try {
			connectionWrapper.run(new IJMXRunnable() {
				@Override
				public void run(MBeanServerConnection connection) throws JMXException {
					try {
						// TODO replace with better JmxTemplate reusing the
						// Connection!!!
						JMXConnector connector = null; // TODO: find out how to improve -->  connectionWrapper.getConnector();
						if (connector == null) {
							connector = new LocalJMXConnector(connection);
						}
						Object answer = callback.doWithJmxConnector(connector);
						answerHolder[0] = answer;
					} catch (Exception e) {
						FabricPlugin.getLogger().warning("Failed to connect to JMX: " + e, e);
					}
				}
			});
			return (T) answerHolder[0];
		} catch (Exception e) {
			throw new FabricException(e);
		}

	}

	public <T> T executeAndThrow(final JmxConnectorCallback<T> callback) throws Exception {
		final Object[] answerHolder = new Object[1];

		try {
			connectionWrapper.run(new IJMXRunnable() {
				@Override
				public void run(MBeanServerConnection connection) throws JMXException {
					try {
						// TODO replace with better JmxTemplate reusing the
						// Connection!!!
						JMXConnector connector = null; //TODO: find out how to improve -->  connectionWrapper.getConnector();
						if (connector == null) {
							connector = new LocalJMXConnector(connection);
						}
						Object answer = callback.doWithJmxConnector(connector);
						answerHolder[0] = answer;
					} catch (Exception e) {
						throw new RethrowRuntimeException(e);
					}
				}
			});
			return (T) answerHolder[0];
		} catch (RethrowRuntimeException e) {
			e.rethrowCause();
			return null;
		}

	}

}