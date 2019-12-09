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

package org.fusesource.ide.jmx.karaf.navigator.osgi;

import java.io.IOException;
import java.util.Set;

import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.MalformedObjectNameException;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.openmbean.TabularData;
import javax.management.remote.JMXConnector;

import org.fusesource.ide.jmx.commons.JmxPluginJmxTemplate;
import org.fusesource.ide.jmx.commons.JmxTemplateSupport;
import org.fusesource.ide.jmx.karaf.KarafJMXPlugin;
import org.osgi.jmx.framework.BundleStateMBean;
import org.osgi.jmx.framework.FrameworkMBean;
import org.osgi.jmx.framework.ServiceStateMBean;


public class OsgiFacade {
	private final JmxPluginJmxTemplate template;
	private ObjectName bundleStateQueryObjectName;
	private ObjectName frameworkQueryObjectName;
	@SuppressWarnings("unused")
	private ObjectName packageQueryObjectName;
	private ObjectName serviceStateQueryObjectName;

	/**
	 * A callback API for working with BundleStateMBean
	 */
	public interface BundleStateMBeanCallback<T> {

		T doWithBundleStateMBean(BundleStateMBean mbean) throws Exception;
	}

	/**
	 * A callback API for working with ServiceStateMBean
	 */
	public interface ServiceStateMBeanCallback<T> {

		T doWithServiceStateMBean(ServiceStateMBean mbean) throws Exception;
	}

	/**
	 * A callback API for working with FrameworkMBean
	 */
	public interface FrameworkMBeanCallback<T> {

		T doWithFrameworkMBean(FrameworkMBean mbean) throws Exception;
	}

	public OsgiFacade(JmxPluginJmxTemplate template) throws MalformedObjectNameException {
		this.template = template;
		bundleStateQueryObjectName = new ObjectName("osgi.core:type=bundleState,*");
		frameworkQueryObjectName = new ObjectName("osgi.core:type=framework,*");
		packageQueryObjectName = new ObjectName("osgi.core:type=packageState,*");
		serviceStateQueryObjectName = new ObjectName("osgi.core:type=serviceState,*");
	}

	/**
	 * Adds a NotificationListener to the BundleStateMBean
	 */
	public void addBundleStateNotificationListener(final NotificationListener listener,
			final NotificationFilter filter,
			final Object handback) {

		template.execute(new JmxTemplateSupport.JmxConnectorCallback<Void>() {
			@Override
			public Void doWithJmxConnector(JMXConnector connector) throws Exception {
				MBeanServerConnection connection = null;
				try { 
					// No need to report a closed or not yet established connection.
					if ((connection = connector.getMBeanServerConnection()) == null)
						return null;
				} catch (IOException ex) {					
					if (!ex.getMessage().contentEquals("Connection closed"))
						KarafJMXPlugin.getLogger().warning(ex);
					return null;
				}
				
				final Set<ObjectName> queryNames = connection.queryNames(bundleStateQueryObjectName, null);
				for (ObjectName bundleStateObjectName : queryNames) {
					connection.addNotificationListener(bundleStateObjectName, listener, filter, handback);
					return null;
				}
				return null;
			}
		});
	}

	/**
	 * Removes a NotificationListener to the BundleStateMBean
	 */
	public void removeBundleStateNotificationListener(final NotificationListener listener,
			final NotificationFilter filter,
			final Object handback) {

		template.execute(new JmxTemplateSupport.JmxConnectorCallback<Void>() {
			@Override
			public Void doWithJmxConnector(JMXConnector connector) throws Exception {
				MBeanServerConnection connection = null;
				try { 
					// No need to report a closed or not yet established connection.
					if ((connection = connector.getMBeanServerConnection()) == null)
						return null;
				} catch (IOException ex) {
					if (!ex.getMessage().contentEquals("Connection closed"))
						KarafJMXPlugin.getLogger().warning(ex);
					return null;
				}
				final Set<ObjectName> queryNames = connection.queryNames(bundleStateQueryObjectName, null);
				for (ObjectName bundleStateObjectName : queryNames) {
					try {
						connection.removeNotificationListener(bundleStateObjectName, listener, filter, handback);
					} catch (Exception ex) {
						KarafJMXPlugin.getLogger().warning(ex);
					}
					return null;
				}
				return null;
			}
		});
	}

	/**
	 * Executes a JMX operation on a BundleStateMBean
	 */
	public <T> T execute(final BundleStateMBeanCallback<T> callback) {
		return template.execute(new JmxTemplateSupport.JmxConnectorCallback<T>() {
			@Override
			public T doWithJmxConnector(JMXConnector connector) throws Exception {
				MBeanServerConnection connection = null;
				try { 
					// No need to report a closed or not yet established connection.
					if ((connection = connector.getMBeanServerConnection()) == null)
						return null;
				} catch (IOException ex) {
					if (!ex.getMessage().contentEquals("Connection closed"))
						KarafJMXPlugin.getLogger().warning(ex);
					return null;
				}
				final Set<ObjectName> queryNames = connection.queryNames(bundleStateQueryObjectName, null);
				for (ObjectName bundleStateObjectName : queryNames) {
					BundleStateMBean BundleStateMBean = MBeanServerInvocationHandler.newProxyInstance(connection, bundleStateObjectName, BundleStateMBean.class, true);
					return callback.doWithBundleStateMBean(BundleStateMBean);
				}
				return null;
			}
		});
	}

	/**
	 * Executes a JMX operation on a ServiceStateMBean
	 */
	public <T> T execute(final ServiceStateMBeanCallback<T> callback) {
		return template.execute(new JmxTemplateSupport.JmxConnectorCallback<T>() {
			@Override
			public T doWithJmxConnector(JMXConnector connector) throws Exception {
				MBeanServerConnection connection = null;
				try { 
					// No need to report a closed or not yet established connection.
					if ((connection = connector.getMBeanServerConnection()) == null)
						return null;
				} catch (IOException ex) {
					if (!ex.getMessage().contentEquals("Connection closed"))
						KarafJMXPlugin.getLogger().warning(ex);
					return null;
				}
				final Set<ObjectName> queryNames = connection.queryNames(serviceStateQueryObjectName, null);
				for (ObjectName ServiceStateObjectName : queryNames) {
					ServiceStateMBean serviceStateMBean = MBeanServerInvocationHandler.newProxyInstance(connection, ServiceStateObjectName, ServiceStateMBean.class, true);
					return callback.doWithServiceStateMBean(serviceStateMBean);
				}
				return null;
			}
		});
	}

	/**
	 * Executes a JMX operation on a FrameworkMBean
	 */
	public <T> T execute(final FrameworkMBeanCallback<T> callback) throws Exception {
		return template.executeAndThrow(new JmxTemplateSupport.JmxConnectorCallback<T>() {
			@Override
			public T doWithJmxConnector(JMXConnector connector) throws Exception {
				MBeanServerConnection connection = null;
				try { 
					// No need to report a closed or not yet established connection.
					if ((connection = connector.getMBeanServerConnection()) == null)
						return null;
				} catch (IOException ex) {
					if (!ex.getMessage().contentEquals("Connection closed"))
						KarafJMXPlugin.getLogger().warning(ex);
					return null;
				}
				final Set<ObjectName> queryNames = connection.queryNames(frameworkQueryObjectName, null);
				for (ObjectName FrameworkObjectName : queryNames) {
					FrameworkMBean mbean = MBeanServerInvocationHandler.newProxyInstance(connection, FrameworkObjectName, FrameworkMBean.class, true);
					return callback.doWithFrameworkMBean(mbean);
				}
				return null;
			}
		});
	}

	/**
	 * Lists the mbean data for the bundles
	 */
	public TabularData listBundles() throws Exception {
		return execute(new BundleStateMBeanCallback<TabularData>() {
			@Override
			public TabularData doWithBundleStateMBean(BundleStateMBean bean) throws Exception {
				return bean.listBundles();
			}
		});
	}

	public void startBundle(final long bundleIdentifier) throws Exception {
		execute(new FrameworkMBeanCallback<Void>() {
			@Override
			public Void doWithFrameworkMBean(FrameworkMBean mbean) throws Exception {
				mbean.startBundle(bundleIdentifier);
				return null;
			}
		});
	}

	public void startBundles(final long[] bundleIds) throws Exception {
		execute(new FrameworkMBeanCallback<Void>() {
			@Override
			public Void doWithFrameworkMBean(FrameworkMBean mbean) throws Exception {
				mbean.startBundles(bundleIds);
				return null;
			}
		});
	}

	public void stopBundle(final long bundleIdentifier) throws Exception {
		execute(new FrameworkMBeanCallback<Void>() {
			@Override
			public Void doWithFrameworkMBean(FrameworkMBean mbean) throws Exception {
				mbean.stopBundle(bundleIdentifier);
				return null;
			}
		});
	}

	public void stopBundles(final long[] bundleIds) throws Exception {
		execute(new FrameworkMBeanCallback<Void>() {
			@Override
			public Void doWithFrameworkMBean(FrameworkMBean mbean) throws Exception {
				mbean.stopBundles(bundleIds);
				return null;
			}
		});
	}

	public void updateBundles(final long[] bundleIds) throws Exception {
		execute(new FrameworkMBeanCallback<Void>() {
			@Override
			public Void doWithFrameworkMBean(FrameworkMBean mbean) throws Exception {
				mbean.updateBundles(bundleIds);
				return null;
			}
		});
	}

	public void updateBundle(final long bundleIdentifier) throws Exception {
		execute(new FrameworkMBeanCallback<Void>() {
			@Override
			public Void doWithFrameworkMBean(FrameworkMBean mbean) throws Exception {
				mbean.updateBundle(bundleIdentifier);
				return null;
			}
		});
	}

	public void updateBundleFromURL(final long bundleIdentifier, final String url) throws Exception {
		execute(new FrameworkMBeanCallback<Void>() {
			@Override
			public Void doWithFrameworkMBean(FrameworkMBean mbean) throws Exception {
				mbean.updateBundleFromURL(bundleIdentifier, url);
				return null;
			}
		});
	}

	public void refreshBundle(final long bundleIdentifier) throws Exception {
		execute(new FrameworkMBeanCallback<Void>() {
			@Override
			public Void doWithFrameworkMBean(FrameworkMBean mbean) throws Exception {
				mbean.refreshBundle(bundleIdentifier);
				return null;
			}
		});
	}

	public void refreshBundles(final long[] bundleIds) throws Exception {
		execute(new FrameworkMBeanCallback<Void>() {
			@Override
			public Void doWithFrameworkMBean(FrameworkMBean mbean) throws Exception {
				mbean.refreshBundles(bundleIds);
				return null;
			}
		});
	}

	public void uninstallBundle(final long bundleIdentifier) throws Exception {
		execute(new FrameworkMBeanCallback<Void>() {
			@Override
			public Void doWithFrameworkMBean(FrameworkMBean mbean) throws Exception {
				mbean.uninstallBundle(bundleIdentifier);
				return null;
			}
		});
	}

	public void uninstallBundles(final long[] bundleIds) throws Exception {
		execute(new FrameworkMBeanCallback<Void>() {
			@Override
			public Void doWithFrameworkMBean(FrameworkMBean mbean) throws Exception {
				mbean.uninstallBundles(bundleIds);
				return null;
			}
		});
	}




	public Long installBundle(final String url, final boolean startOnDeploy) throws Exception {
		return execute(new FrameworkMBeanCallback<Long>() {
			@Override
			public Long doWithFrameworkMBean(FrameworkMBean mbean) throws Exception {
				long id = mbean.installBundle(url);
				if (startOnDeploy) {
					mbean.startBundle(id);
				}
				return id;
			}
		});
	}

	/**
	 * Either performs an install or an update dependending on if the bundle is already installed or not and then starts it if the given
	 * flag is enabled and then returns the newly created bundle ID.
	 * 
	 * @param startOnDeploy
	 */
	public Long installOrUpdateBundle(final String url, final boolean startOnDeploy) throws Exception {
		return execute(new FrameworkMBeanCallback<Long>() {
			@Override
			public Long doWithFrameworkMBean(FrameworkMBean mbean) throws Exception {
				long id = mbean.installBundle(url);
				if (startOnDeploy) {
					mbean.startBundle(id);
				}
				return id;
			}
		});
	}


	/**
	 * Lists the mbean data for the services
	 */
	public TabularData listServices() throws Exception {
		return execute(new ServiceStateMBeanCallback<TabularData>() {
			@Override
			public TabularData doWithServiceStateMBean(ServiceStateMBean bean) throws Exception {
				return bean.listServices();
			}
		});
	}

}