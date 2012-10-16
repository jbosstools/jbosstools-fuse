package org.fusesource.ide.fabric.views.logs;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;

import org.fusesource.fabric.service.JmxTemplateSupport;
import org.fusesource.fabric.service.LocalJMXConnector;
import org.fusesource.ide.commons.Activator;
import org.fusesource.ide.jmx.core.IConnectionWrapper;
import org.fusesource.ide.jmx.core.IJMXRunnable;
import org.fusesource.ide.jmx.core.JMXException;
import org.fusesource.insight.log.service.LogQueryCallback;


public class JmxFabricLogBrowser extends LogBrowserSupport {
	private final IConnectionWrapper connection;
	private JmxTemplateSupport jmxTemplate;

	public JmxFabricLogBrowser(final IConnectionWrapper connection, ObjectName objectName) {
		this.connection = connection;
		this.jmxTemplate = new JmxTemplateSupport(){

			@Override
			public <T> T execute(final JmxConnectorCallback<T> callback) {
				try {
					connection.run(new IJMXRunnable() {

						@Override
						public void run(MBeanServerConnection connection) throws JMXException {
							JMXConnector connector = new LocalJMXConnector(connection);
							try {
								callback.doWithJmxConnector(connector);
							} catch (JMXException e) {
								throw e;
							} catch (Exception e) {
								throw new JMXException(e);
							}
						}
					});
				} catch (Exception e) {
					Activator.getLogger().warning("Failed to query logs: " + e, e);
				}
				return null;
			}};
	}

	@Override
	protected <T> T execute(LogQueryCallback<T> callback) {
		return jmxTemplate.execute(callback);
	}

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


}
