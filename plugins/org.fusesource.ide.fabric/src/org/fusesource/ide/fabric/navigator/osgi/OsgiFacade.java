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

package org.fusesource.ide.fabric.navigator.osgi;

import javax.management.MalformedObjectNameException;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.openmbean.TabularData;

import org.fusesource.fabric.jolokia.facade.JolokiaFabricConnector;

public class OsgiFacade {
	private static final String BUNDLE_STATE_MBEAN = "osgi.core:type=bundleState";
	private static final String SERVICE_STATE_MBEAN = "osgi.core:type=serviceState";
	private static final String FRAMEWORK_MBEAN = "osgi.core:type=framework";
	
	private JolokiaFabricConnector connector;
	private ObjectName bundleStateQueryObjectName;
	private ObjectName frameworkQueryObjectName;
	private ObjectName packageQueryObjectName;
	private ObjectName serviceStateQueryObjectName;

	/**
	 * A callback API for working with BundleStateMBean
	 */
	public interface BundleStateMBeanCallback<T> {

		T doWithBundleStateMBean() throws Exception;
	}

	/**
	 * A callback API for working with ServiceStateMBean
	 */
	public interface ServiceStateMBeanCallback<T> {

		T doWithServiceStateMBean() throws Exception;
	}

	/**
	 * A callback API for working with FrameworkMBean
	 */
	public interface FrameworkMBeanCallback<T> {

		T doWithFrameworkMBean() throws Exception;
	}

	public OsgiFacade(JolokiaFabricConnector connector) throws MalformedObjectNameException {
		this.connector = connector;
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

		System.out.println("TODO: add notification support for bundle state (add)");
		
//		template.execute(new JmxTemplateSupport.JmxConnectorCallback<Void>() {
//			@Override
//			public Void doWithJmxConnector(JMXConnector connector) throws Exception {
//				MBeanServerConnection connection = connector.getMBeanServerConnection();
//				final Set<ObjectName> queryNames = connection.queryNames(bundleStateQueryObjectName, null);
//				for (ObjectName bundleStateObjectName : queryNames) {
//					connection.addNotificationListener(bundleStateObjectName, listener, filter, handback);
//					return null;
//				}
//				return null;
//			}
//		});
	}




	/**
	 * Removes a NotificationListener to the BundleStateMBean
	 */
	public void removeBundleStateNotificationListener(final NotificationListener listener,
			final NotificationFilter filter,
			final Object handback) {

		System.out.println("TODO: add notification support for bundle state (remove)");
		
//		template.execute(new JmxTemplateSupport.JmxConnectorCallback<Void>() {
//			@Override
//			public Void doWithJmxConnector(JMXConnector connector) throws Exception {
//				MBeanServerConnection connection = connector.getMBeanServerConnection();
//				final Set<ObjectName> queryNames = connection.queryNames(bundleStateQueryObjectName, null);
//				for (ObjectName bundleStateObjectName : queryNames) {
//					connection.removeNotificationListener(bundleStateObjectName, listener, filter, handback);
//					return null;
//				}
//				return null;
//			}
//		});
	}

	/**
	 * Executes a JMX operation on a BundleStateMBean
	 */
	public <T> T execute(final BundleStateMBeanCallback<T> callback) {
		System.out.println("TODO: add execute support for bundle state");
		return null;
//		return template.execute(new JmxTemplateSupport.JmxConnectorCallback<T>() {
//			@Override
//			public T doWithJmxConnector(JMXConnector connector) throws Exception {
//				MBeanServerConnection connection = connector.getMBeanServerConnection();
//				final Set<ObjectName> queryNames = connection.queryNames(bundleStateQueryObjectName, null);
//				for (ObjectName bundleStateObjectName : queryNames) {
//					BundleStateMBean BundleStateMBean = MBeanServerInvocationHandler.newProxyInstance(connection, bundleStateObjectName, BundleStateMBean.class, true);
//					return callback.doWithBundleStateMBean(BundleStateMBean);
//				}
//				return null;
//			}
//		});
	}

	/**
	 * Executes a JMX operation on a ServiceStateMBean
	 */
	public <T> T execute(final ServiceStateMBeanCallback<T> callback) {
		System.out.println("TODO: add execute support for service state");
		return null;
//		return template.execute(new JmxTemplateSupport.JmxConnectorCallback<T>() {
//			@Override
//			public T doWithJmxConnector(JMXConnector connector) throws Exception {
//				MBeanServerConnection connection = connector.getMBeanServerConnection();
//				final Set<ObjectName> queryNames = connection.queryNames(serviceStateQueryObjectName, null);
//				for (ObjectName ServiceStateObjectName : queryNames) {
//					ServiceStateMBean serviceStateMBean = MBeanServerInvocationHandler.newProxyInstance(connection, ServiceStateObjectName, ServiceStateMBean.class, true);
//					return callback.doWithServiceStateMBean(serviceStateMBean);
//				}
//				return null;
//			}
//		});
	}

	/**
	 * Executes a JMX operation on a FrameworkMBean
	 */
	public <T> T execute(final FrameworkMBeanCallback<T> callback) throws Exception {
		System.out.println("TODO: add execute support for framework");
		return null;
//		return template.executeAndThrow(new JmxTemplateSupport.JmxConnectorCallback<T>() {
//			@Override
//			public T doWithJmxConnector(JMXConnector connector) throws Exception {
//				MBeanServerConnection connection = connector.getMBeanServerConnection();
//				final Set<ObjectName> queryNames = connection.queryNames(frameworkQueryObjectName, null);
//				for (ObjectName FrameworkObjectName : queryNames) {
//					FrameworkMBean mbean = MBeanServerInvocationHandler.newProxyInstance(connection, FrameworkObjectName, FrameworkMBean.class, true);
//					return callback.doWithFrameworkMBean(mbean);
//				}
//				return null;
//			}
//		});
	}

	/**
	 * Lists the mbean data for the bundles
	 */
	public TabularData listBundles() throws Exception {
		System.out.println("TODO: return the list of bundles");
		return null;
//		return execute(new BundleStateMBeanCallback<TabularData>() {
//			@Override
//			public TabularData doWithBundleStateMBean(BundleStateMBean bean) throws Exception {
//				return bean.listBundles();
//			}
//		});
	}

	public void startBundle(final long bundleIdentifier) throws Exception {
		System.out.println("TODO: start bundle");
//		execute(new FrameworkMBeanCallback<Void>() {
//			@Override
//			public Void doWithFrameworkMBean(FrameworkMBean mbean) throws Exception {
//				mbean.startBundle(bundleIdentifier);
//				return null;
//			}
//		});
	}

	public void startBundles(final long[] bundleIds) throws Exception {
		System.out.println("TODO: start bundles");
//		execute(new FrameworkMBeanCallback<Void>() {
//			@Override
//			public Void doWithFrameworkMBean(FrameworkMBean mbean) throws Exception {
//				mbean.startBundles(bundleIds);
//				return null;
//			}
//		});
	}

	public void stopBundle(final long bundleIdentifier) throws Exception {
		System.out.println("TODO: stop bundle");
//		execute(new FrameworkMBeanCallback<Void>() {
//			@Override
//			public Void doWithFrameworkMBean(FrameworkMBean mbean) throws Exception {
//				mbean.stopBundle(bundleIdentifier);
//				return null;
//			}
//		});
	}

	public void stopBundles(final long[] bundleIds) throws Exception {
		System.out.println("TODO: stop bundles");
//		execute(new FrameworkMBeanCallback<Void>() {
//			@Override
//			public Void doWithFrameworkMBean(FrameworkMBean mbean) throws Exception {
//				mbean.stopBundles(bundleIds);
//				return null;
//			}
//		});
	}

	public void updateBundles(final long[] bundleIds) throws Exception {
		System.out.println("TODO: update bundles");
//		execute(new FrameworkMBeanCallback<Void>() {
//			@Override
//			public Void doWithFrameworkMBean(FrameworkMBean mbean) throws Exception {
//				mbean.updateBundles(bundleIds);
//				return null;
//			}
//		});
	}

	public void updateBundle(final long bundleIdentifier) throws Exception {
		System.out.println("TODO: update bundle");
//		execute(new FrameworkMBeanCallback<Void>() {
//			@Override
//			public Void doWithFrameworkMBean(FrameworkMBean mbean) throws Exception {
//				mbean.updateBundle(bundleIdentifier);
//				return null;
//			}
//		});
	}

	public void updateBundleFromURL(final long bundleIdentifier, final String url) throws Exception {
		System.out.println("TODO: update bundle from url");
//		execute(new FrameworkMBeanCallback<Void>() {
//			@Override
//			public Void doWithFrameworkMBean(FrameworkMBean mbean) throws Exception {
//				mbean.updateBundleFromURL(bundleIdentifier, url);
//				return null;
//			}
//		});
	}

	public void refreshBundle(final long bundleIdentifier) throws Exception {
		System.out.println("TODO: refresh bundle");
//		execute(new FrameworkMBeanCallback<Void>() {
//			@Override
//			public Void doWithFrameworkMBean(FrameworkMBean mbean) throws Exception {
//				mbean.refreshBundle(bundleIdentifier);
//				return null;
//			}
//		});
	}

	public void refreshBundles(final long[] bundleIds) throws Exception {
		System.out.println("TODO: refresh bundles");
//		execute(new FrameworkMBeanCallback<Void>() {
//			@Override
//			public Void doWithFrameworkMBean(FrameworkMBean mbean) throws Exception {
//				mbean.refreshBundles(bundleIds);
//				return null;
//			}
//		});
	}

	public void uninstallBundle(final long bundleIdentifier) throws Exception {
		System.out.println("TODO: uninstall bundle");
//		execute(new FrameworkMBeanCallback<Void>() {
//			@Override
//			public Void doWithFrameworkMBean(FrameworkMBean mbean) throws Exception {
//				mbean.uninstallBundle(bundleIdentifier);
//				return null;
//			}
//		});
	}

	public void uninstallBundles(final long[] bundleIds) throws Exception {
		System.out.println("TODO: uninstall bundles");
//		execute(new FrameworkMBeanCallback<Void>() {
//			@Override
//			public Void doWithFrameworkMBean(FrameworkMBean mbean) throws Exception {
//				mbean.uninstallBundles(bundleIds);
//				return null;
//			}
//		});
	}

	public Long installBundle(final String url, final boolean startOnDeploy) throws Exception {
		System.out.println("TODO: install bundle");
		return null;
//		return execute(new FrameworkMBeanCallback<Long>() {
//			@Override
//			public Long doWithFrameworkMBean(FrameworkMBean mbean) throws Exception {
//				long id = mbean.installBundle(url);
//				if (startOnDeploy) {
//					mbean.startBundle(id);
//				}
//				return id;
//			}
//		});
	}

	/**
	 * Either performs an install or an update dependending on if the bundle is already installed or not and then starts it if the given
	 * flag is enabled and then returns the newly created bundle ID.
	 * 
	 * @param startOnDeploy
	 */
	public Long installOrUpdateBundle(final String url, final boolean startOnDeploy) throws Exception {
		System.out.println("TODO: install or update bundle");
		return null;
//		return execute(new FrameworkMBeanCallback<Long>() {
//			@Override
//			public Long doWithFrameworkMBean(FrameworkMBean mbean) throws Exception {
//				long id = mbean.installBundle(url);
//				if (startOnDeploy) {
//					mbean.startBundle(id);
//				}
//				return id;
//			}
//		});
	}


	/**
	 * Lists the mbean data for the services
	 */
	public TabularData listServices() throws Exception {
		System.out.println("TODO: list services");
		return null;
//		return execute(new ServiceStateMBeanCallback<TabularData>() {
//			@Override
//			public TabularData doWithServiceStateMBean(ServiceStateMBean bean) throws Exception {
//				return bean.listServices();
//			}
//		});
	}

}
