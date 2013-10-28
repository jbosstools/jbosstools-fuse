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

package org.fusesource.ide.jmx.ui.internal.localjmx;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXServiceURL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.fusesource.ide.commons.tree.HasName;
import org.fusesource.ide.commons.tree.HasRefreshableUI;
import org.fusesource.ide.commons.tree.RefreshableUI;
import org.fusesource.ide.commons.ui.ImageProvider;
import org.fusesource.ide.commons.ui.drop.DelegateDropListener;
import org.fusesource.ide.commons.ui.drop.DropHandler;
import org.fusesource.ide.commons.ui.drop.DropHandlerFactory;
import org.fusesource.ide.commons.util.Objects;
import org.fusesource.ide.commons.util.Strings;
import org.fusesource.ide.jmx.core.ExtensionManager;
import org.fusesource.ide.jmx.core.IConnectionProvider;
import org.fusesource.ide.jmx.core.IConnectionWrapper;
import org.fusesource.ide.jmx.core.IJMXRunnable;
import org.fusesource.ide.jmx.core.JMXActivator;
import org.fusesource.ide.jmx.core.JMXCoreMessages;
import org.fusesource.ide.jmx.core.JMXException;
import org.fusesource.ide.jmx.core.providers.DefaultConnectionProvider;
import org.fusesource.ide.jmx.core.tree.NodeUtils;
import org.fusesource.ide.jmx.core.tree.Root;
import org.fusesource.ide.jmx.ui.JMXUIActivator;
import org.fusesource.ide.jvmmonitor.core.IActiveJvm;
import org.fusesource.ide.jvmmonitor.internal.ui.IJvmFacade;
import org.fusesource.ide.jvmmonitor.ui.JvmMonitorPreferences;



public class JvmConnectionWrapper implements IConnectionWrapper, HasName, ImageProvider, HasRefreshableUI, IAdaptable, IJvmFacade, DropHandlerFactory {
	private static final String MAVEN_PREFIX = "org.codehaus.plexus.classworlds.launcher.Launcher";
	private static final String ECLIPSE_MAVEN_PROCESS_PREFIX  = "-DECLIPSE_PROCESS_NAME='";
	private static final String ECLIPSE_MAVEN_PROCESS_POSTFIX = "'";
	private static final String KARAF_HOME_PREFIX = " -Dkaraf.home=";
	private static final String KARAF_HOME_POSTFIX = " ";

	protected static final Map<String,String> vmAliasMap = new HashMap<String, String>();
	protected static final Map<String,String> karafSubTypeMap = new HashMap<String, String>();
	protected static final Map<Integer, String> processInformationStore = new HashMap<Integer, String>();

	private final JvmConnectionsNode parent;
	private IActiveJvm activeJvm;
	private String name;
	private Root root;
	private Image image;
	private List<Runnable> afterLoadRunnables = new ArrayList<Runnable>();

	static {
		vmAliasMap.put("com.intellij.rt.execution.application.AppMain", "idea");
		vmAliasMap.put("org.apache.karaf.main.Main", "karaf");
		vmAliasMap.put("org.eclipse.equinox.launcher.Main", "equinox");
		vmAliasMap.put("org.jetbrains.idea.maven.server.RemoteMavenServer", "idea maven server");
		vmAliasMap.put("idea maven server", "");
		vmAliasMap.put("scala.tools.nsc.MainGenericRunner", "scala repl");

		karafSubTypeMap.put("default", "Apache Karaf");
		karafSubTypeMap.put("esb-version.jar", "JBoss Fuse");
		karafSubTypeMap.put("fabric-version.jar", "Fuse Fabric");
		karafSubTypeMap.put("mq-version.jar", "JBoss A-MQ");
		karafSubTypeMap.put("servicemix-version.jar", "Apache ServiceMix");
	}

	public JvmConnectionWrapper(JvmConnectionsNode parent, JMXServiceURL url, IActiveJvm vm) {
		this.parent = parent;
		this.activeJvm = vm;
	}

	@Override
	public String toString() {
		return getName();
	}

	public IActiveJvm getActiveJvm() {
		return activeJvm;
	}

	public void setActiveJvm(IActiveJvm activeJvm) {
		if (this.activeJvm != activeJvm) {
			IActiveJvm oldJvm = this.activeJvm;
			this.activeJvm = activeJvm;
			if (oldJvm != null) {
				try {
					oldJvm.disconnect();
				} catch (Throwable t) {
					// ignore
				}
			}
		}
	}

	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		if (adapter == IPropertySheetPage.class) {
			ITabbedPropertySheetPageContributor contributor = new ITabbedPropertySheetPageContributor() {
				public String getContributorId() {
					return "org.fusesource.ide.jvmmonitor.ui.JvmExplorer";
				}
			};
			TabbedPropertySheetPage page = new TabbedPropertySheetPage(contributor);
			return page;
		} else if (adapter == ITabbedPropertySheetPageContributor.class) {
		    return new ITabbedPropertySheetPageContributor() {
                public String getContributorId() {
                    return "org.fusesource.ide.jvmmonitor.ui.JvmExplorer";
                }
            };
		}
		return null;
	}

	public DropHandler createDropHandler(DropTargetEvent event) {
		if (isConnected()) {
			if (getRoot() == null)
				loadRoot();
			DropHandler handler = DelegateDropListener.createDropHandler(getRoot(), event);
			if (handler != null) {
				return handler;
			}
		}
		String lowerName = getName().toLowerCase();
		// only support karaf and fuse esb
		if (lowerName.contains("karaf") || lowerName.contains("fuse") || lowerName.contains("fmc") || lowerName.contains("jboss a-mq")) {
			// lets return a lazy drop handler for later when we've actually connected
			return new DropHandler() {

				public void drop(final DropTargetEvent localEvent) {
					addOnLoadRunnable(new Runnable() {
						public void run() {
							Root r = getRoot();
							if (r == null) {
								Activator.getLogger().warning("Cannot drop as no root yet, we've probably not been able to connect to " + getName());
							} else {
								DropHandler handler = DelegateDropListener.createDropHandler(r, localEvent);
								if (handler != null) {
									handler.drop(localEvent);
								} else {
									Activator.getLogger().warning("No DropHandler available. We've probably not been able to connect to " + getName());
								}
							}
						}
					});
					if (!isConnected()) {
						try {
							connect();

							// now lets force the lazy load
							loadRoot();
						} catch (Exception e) {
							Activator.getLogger().warning("Could not connect to " + this + ". " + e, e);
						}
					}

				}};
		} else {
			return null;
		}
	}

	protected void addOnLoadRunnable(Runnable runnable) {
		afterLoadRunnables.add(runnable);
	}

	public JMXConnector getConnector() {
		return activeJvm.getMBeanServer().getConnector();
	}

	public MBeanServerConnection getConnection() {
		return activeJvm.getMBeanServer().getConnection();
	}

	public synchronized void connect() throws Exception {
		if (!activeJvm.isConnected() && activeJvm.isConnectionSupported()) {
			int updatePeriod = JvmMonitorPreferences.getJvmUpdatePeriod();
			activeJvm.connect(updatePeriod);
			fireConnectionChanged();
		}
	}

	public synchronized void disconnect() throws Exception {
		root = null;
		activeJvm.disconnect();
		fireConnectionChanged();
	}

	public boolean isConnected() {
		return activeJvm.isConnected();
	}

	public Root getRoot() {
		return root;
	}

	public void loadRoot() {
		if (isConnected() && root == null) {
			try {
				root = NodeUtils.createObjectNameTree(this);

				for (Runnable task : afterLoadRunnables) {
					task.run();
				}
				afterLoadRunnables.clear();
			} catch (Throwable e) {
				Activator.getLogger().warning("Failed to load JMX tree for " + this + ". " + e, e);
			}
		}
	}

	public void run(IJMXRunnable runnable) throws CoreException {
		try {
			runnable.run(getConnection());
		} catch (JMXException ce) {
			IStatus s = new Status(IStatus.ERROR, JMXActivator.PLUGIN_ID,
					JMXCoreMessages.DefaultConnection_ErrorRunningJMXCode, ce);
			throw new CoreException(s);
		}
	}

	public boolean canControl() {
		return true;
	}

	protected void fireConnectionChanged() {
		DefaultConnectionProvider provider = (DefaultConnectionProvider) getProvider();
		provider.fireChanged(this);
	}

	public IConnectionProvider getProvider() {
		return ExtensionManager.getProvider(DefaultConnectionProvider.PROVIDER_ID);
	}

	public RefreshableUI getRefreshableUI() {
		return parent.getRefreshableUI();
	}

	/**
	 * Returns true if this process is a karaf container at the given root
	 * directory
	 */
	public boolean isKaraf(String rootDir) {
		String displayName = activeJvm.getMainClass();
		return Objects.equal("org.apache.karaf.main.Main", displayName);
	}

	public String getName() {
		if (name == null) {
			String displayName = activeJvm.getMainClass();
			if (Strings.isBlank(displayName)) {
				displayName = "Java process";
			} else if (displayName.startsWith(MAVEN_PREFIX)) {
				displayName = "maven" + displayName.substring(MAVEN_PREFIX.length());
				if (displayName.endsWith("org.apache.camel:camel-maven-plugin:run") || displayName.endsWith("camel:run")) {
					displayName = "Local Camel Context";
					image = JMXUIActivator.getDefault().getImage("camel.png");
				} else {
					if (!activeJvm.isRemote()) {
						String pInfo = queryProcessInformation(activeJvm.getPid());
						if (pInfo != null) {
							int start = pInfo.indexOf(ECLIPSE_MAVEN_PROCESS_PREFIX);
							if (start != -1) {
								int end   = pInfo.indexOf(ECLIPSE_MAVEN_PROCESS_POSTFIX, start+ECLIPSE_MAVEN_PROCESS_PREFIX.length()+1);
								if (end != -1) {
									displayName = pInfo.substring(start + ECLIPSE_MAVEN_PROCESS_PREFIX.length(), end);
								} else {
									displayName = pInfo.substring(start + ECLIPSE_MAVEN_PROCESS_PREFIX.length());
								}
							}
						}
					}
				}
			} else if (isKaraf(displayName)) {
				// we need to distinguish between pure karaf, apache smx, fuse esb and fuse fabric, etc
				String karafHomeFolder = null;
				if (!activeJvm.isRemote()) {
					String pInfo = queryProcessInformation(activeJvm.getPid());
					if (pInfo != null) {
						int start = pInfo.indexOf(KARAF_HOME_PREFIX);
						if (start != -1) {
							int end   = pInfo.indexOf(KARAF_HOME_POSTFIX, start+KARAF_HOME_PREFIX.length()+1);
							if (end != -1) {
								karafHomeFolder = pInfo.substring(start + KARAF_HOME_PREFIX.length(), end);
							}
						}
					}
				}

				String karafSubType = null;
				if (karafHomeFolder != null) {
					File libFolder = new File(String.format("%s%slib%s", karafHomeFolder, File.separator, File.separator));
					if (libFolder.exists() && libFolder.isDirectory()) {
						File[] jars = libFolder.listFiles(new FileFilter() {
							public boolean accept(File f) {
								return f.isFile() && (f.getName().toLowerCase().endsWith("-version.jar"));
							}
						});
						if (jars != null && jars.length==1) {
							File f = jars[0];
							if (karafSubTypeMap.containsKey(f.getName())) {
								karafSubType = karafSubTypeMap.get(f.getName());
							} else {
								karafSubType = karafSubTypeMap.get("default");
							}
						}
					}
				}
				if (karafSubType != null) {
					if (karafSubType.toLowerCase().contains("esb")) {
						image = JMXUIActivator.getDefault().getImage("fuse_server.png");
					} else if (karafSubType.toLowerCase().contains("fabric")) {
						image = JMXUIActivator.getDefault().getImage("fabric.png");
					} else if (karafSubType.toLowerCase().contains("mq")) {
						image = JMXUIActivator.getDefault().getImage("mq_server.png");
					} else if (karafSubType.toLowerCase().contains("servicemix")) {
						image = JMXUIActivator.getDefault().getImage("smx_server.png");
					} else {
						image = JMXUIActivator.getDefault().getImage("container.png");
					}
				}

				if (karafSubType == null) {
					displayName = getNameFromAliasMap(displayName);
				} else {
					displayName = karafSubType;
				}
			} else {
				displayName = getNameFromAliasMap(displayName);
			}
			// include pid in name
			displayName += " [" + activeJvm.getPid() + "]";
			name = displayName;
		}
		return name;
	}

	private String getNameFromAliasMap(String displayName) {
		Set<Entry<String, String>> entrySet = vmAliasMap.entrySet();
		for (Entry<String, String> entry : entrySet) {
			String key = entry.getKey();
			if (displayName.startsWith(key)) {
				return (entry.getValue() + displayName.substring(key.length()));
			}
		}
		return displayName;
	}

	public void setName(String name) {
		this.name = name;
	}


	public Image getImage() {
		String n = getName();
		if (image == null) {
			if (n != null && n.contains("Camel")) {
				image = JMXUIActivator.getDefault().getImage("camel.png");
			}
		}
		if (image != null) {
			return image;
		}
		return JMXUIActivator.getDefault().getImage("container.gif");
	}

	public Properties getAgentProperties() {
		/*
		try {
			return vm.getAgentProperties();
		} catch (IOException e) {
			JMXUIActivator.getLogger().warning("Failed to get Agent Properties: " + e, e);
		}
		 */
		return new Properties();
	}

	public Map<String,String> getSystemProperties() {
		return System.getenv();
	}

	/**
	 * 
	 * @param pid
	 * @return
	 */
	private String queryProcessInformation(int pid) {
		String retVal = null;

		if (!processInformationStore.containsKey(pid)) {
			refreshProcessInformationStore();
		}

		retVal = processInformationStore.get(pid);

		return retVal;
	}

	/**
	 * rebuilds the local process information store
	 */
	public static void refreshProcessInformationStore() {
		processInformationStore.clear();

		String path = String.format("%s%sbin%s", System.getProperty("java.home"), File.separator, File.separator);
		List<String> cmds = new ArrayList<String>();
		cmds.add("jps");
		cmds.add("-v");
		ProcessBuilder pb = new ProcessBuilder(cmds);
		pb.directory(new File(path));
		BufferedReader br = null;
		try {
			Process p = pb.start();
			br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = "";
			while ((line = br.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(line, " ");
				int pid = -1;
				if (st.hasMoreElements()) {
					String sVal = st.nextToken();
					try {
						pid = Integer.parseInt(sVal);
					} catch (NumberFormatException e) {
						pid = -1;
					}
				}

				if (pid != -1) {
					processInformationStore.put(pid, line);
				}
			}
		} catch (Exception ex) {
			// we don't want to scare the user with this
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (Exception e) {
					// we don't want to scare the user with this
				}
			}
		}
	}
}
