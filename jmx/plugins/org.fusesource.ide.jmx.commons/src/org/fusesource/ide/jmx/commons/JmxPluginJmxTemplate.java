/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.jmx.commons;

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
				public void run(MBeanServerConnection connection) throws Exception {
					try {
						call(callback, answerHolder, connection);
					} catch (Exception e) {
						Activator.getLogger().warning("Failed to connect to JMX: " + e, e);
					}
				}
			});
			return (T) answerHolder[0];
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public <T> T executeAndThrow(final JmxConnectorCallback<T> callback) throws JMXException {
		final Object[] answerHolder = new Object[1];

		try {
			connectionWrapper.run(new IJMXRunnable() {
				@Override
				public void run(MBeanServerConnection connection) throws JMXException {
					try {
						call(callback, answerHolder, connection);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			});
			return (T) answerHolder[0];
		} catch (RuntimeException e) {
			Activator.getLogger().error(e);
			return null;
		}
	}
	
	protected <T> void call(final JmxConnectorCallback<T> callback, final Object[] answerHolder,
			MBeanServerConnection connection) throws Exception {
		//TODO: replace with better JmxTemplate reusing the Connection!!!
		//TODO: find out how to improve -->  connectionWrapper.getConnector();
		JMXConnector connector = new LocalJMXConnector(connection);
		Object answer = callback.doWithJmxConnector(connector);
		answerHolder[0] = answer;
	}

}