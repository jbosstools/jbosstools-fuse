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

package org.fusesource.ide.fabric.views.logs;

import javax.management.ObjectName;

import org.jboss.tools.jmx.core.IConnectionWrapper;


public class JmxFabricLogBrowser extends LogBrowserSupport {
	private final IConnectionWrapper connection;

	public JmxFabricLogBrowser(final IConnectionWrapper connection, ObjectName objectName) {
		this.connection = connection;
//		this.jmxTemplate = new JmxTemplateSupport(){
//
//			@Override
//			public <T> T execute(final JmxConnectorCallback<T> callback) {
//				try {
//					connection.run(new IJMXRunnable() {
//
//						@Override
//						public void run(MBeanServerConnection connection) throws JMXException {
//							JMXConnector connector = new LocalJMXConnector(connection);
//							try {
//								callback.doWithJmxConnector(connector);
//							} catch (JMXException e) {
//								throw e;
//							} catch (Exception e) {
//								throw new JMXException(e);
//							}
//						}
//					});
//				} catch (Exception e) {
//					Activator.getLogger().warning("Failed to query logs: " + e, e);
//				}
//				return null;
//			}};
	}

//	@Override
//	protected <T> T execute(LogQueryCallback<T> callback) {
//		FabricPlugin.getLogger().debug("TODO: query fabric logs via jolokia");
//		return null;
////		return jmxTemplate.execute(callback);
//	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((connection == null) ? 0 : connection.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JmxFabricLogBrowser other = (JmxFabricLogBrowser) obj;
		if (connection == null) {
			if (other.connection != null)
				return false;
		} else if (!connection.equals(other.connection))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.fabric.views.logs.LogBrowserSupport#getJolokiaPassword()
	 */
	@Override
	protected String getJolokiaPassword() {
		// TODO Auto-generated method stub
		return "";
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.fabric.views.logs.LogBrowserSupport#getJolokiaUrl()
	 */
	@Override
	protected String getJolokiaUrl() {
		// TODO Auto-generated method stub
		return "";
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.fabric.views.logs.LogBrowserSupport#getJolokiaUser()
	 */
	@Override
	protected String getJolokiaUser() {
		// TODO Auto-generated method stub
		return "";
	}
}
