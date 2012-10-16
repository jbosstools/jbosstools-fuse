/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.fusesource.ide.zk.zookeeper;

import java.io.File;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.InvalidACLException;
import org.apache.zookeeper.KeeperException.NoAuthException;
import org.apache.zookeeper.KeeperException.NoNodeException;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.apache.zookeeper.KeeperException.NotEmptyException;
import org.apache.zookeeper.version.Info;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.statushandlers.StatusManager;
import org.fusesource.ide.zk.zookeeper.data.Znode;
import org.fusesource.ide.zk.zookeeper.data.ZooKeeperConnection;
import org.fusesource.ide.zk.zookeeper.data.ZooKeeperConnectionDescriptor;
import org.fusesource.ide.zk.zookeeper.model.ZooKeeperConnectionModel;
import org.fusesource.ide.zk.zookeeper.model.ZooKeeperConnectionModelSource;
import org.fusesource.ide.zk.zookeeper.runtime.ZooKeeperConnectionDescriptorFiles;
import org.fusesource.ide.zk.core.model.DataModelManager;
import org.osgi.framework.BundleContext;


/**
 * The ZooKeeper plug-in class.
 * 
 * @author Mark Masse
 */
public class ZooKeeperActivator extends AbstractUIPlugin {

	/**
	 * String representation of the ZooKeeper version (i.e. "3.2.2").
	 */
	public static final String ZOO_KEEPER_VERSION = String.valueOf(Info.MAJOR) + "." + String.valueOf(Info.MINOR) + "."
			+ String.valueOf(Info.MICRO);

	/**
	 * The suffix to add to the plug-in's editor and view IDs to uniquely qualify them.
	 */
	public static final String VERSION_SUFFIX = ""; //"." + ZOO_KEEPER_VERSION;

	/**
	 * The unique ID of this plug-in.
	 */
	public static final String PLUGIN_ID = "org.fusesource.ide.zk.zookeeper";
	//public static final String PLUGIN_ID = "org.fusesource.ide.zk.zookeeper" + VERSION_SUFFIX;

	//
	// Action Image Keys
	//

	public static final String IMAGE_KEY_ACTION_NEW_ZNODE = "IMAGE_KEY_ACTION_NEW_ZNODE";
	public static final String IMAGE_KEY_ACTION_NEW_ZOO_KEEPER_CONNECTION = "IMAGE_KEY_ACTION_NEW_ZOO_KEEPER_CONNECTION";
	public static final String IMAGE_KEY_ACTION_TABLE_EDIT_CHILDREN = "IMAGE_KEY_ACTION_TABLE_EDIT_CHILDREN";

	//
	// Dialog Image Keys
	//

	public static final String IMAGE_KEY_DIALOG_ADD_AUTH_INFO = "IMAGE_KEY_DIALOG_ADD_AUTH_INFO";

	//
	// Object Image Keys
	//

	public static final String IMAGE_KEY_OBJECT_AUTH = "IMAGE_KEY_OBJECT_AUTH";
	public static final String IMAGE_KEY_OBJECT_ZNODE = "IMAGE_KEY_OBJECT_ZNODE";
	public static final String IMAGE_KEY_OBJECT_ZNODE_ACL = "IMAGE_KEY_OBJECT_ZNODE_ACL";
	public static final String IMAGE_KEY_OBJECT_ZNODE_CHILDREN = "IMAGE_KEY_OBJECT_ZNODE_CHILDREN";
	public static final String IMAGE_KEY_OBJECT_ZNODE_DATA = "IMAGE_KEY_OBJECT_ZNODE_DATA";
	public static final String IMAGE_KEY_OBJECT_ZNODE_EPHEMERAL = "IMAGE_KEY_OBJECT_ZNODE_EPHEMERAL";
	public static final String IMAGE_KEY_OBJECT_ZNODE_EPHEMERAL_LARGE = "IMAGE_KEY_OBJECT_ZNODE_EPHEMERAL_LARGE";
	public static final String IMAGE_KEY_OBJECT_ZNODE_LARGE = "IMAGE_KEY_OBJECT_ZNODE_LARGE";
	public static final String IMAGE_KEY_OBJECT_ZNODE_LEAF = "IMAGE_KEY_OBJECT_ZNODE_LEAF";
	public static final String IMAGE_KEY_OBJECT_ZNODE_LEAF_LARGE = "IMAGE_KEY_OBJECT_ZNODE_LEAF_LARGE";
	public static final String IMAGE_KEY_OBJECT_ZNODE_STAT = "IMAGE_KEY_OBJECT_ZNODE_STAT";
	public static final String IMAGE_KEY_OBJECT_ZOO_KEEPER_CONNECTION = "IMAGE_KEY_OBJECT_ZOO_KEEPER_CONNECTION";
	public static final String IMAGE_KEY_OBJECT_ZOO_KEEPER_CONNECTION_LARGE = "IMAGE_KEY_OBJECT_ZOO_KEEPER_CONNECTION_LARGE";
	public static final String IMAGE_KEY_OBJECT_ZOO_KEEPER_CONNECTION_NOT_CONNECTED = "IMAGE_KEY_OBJECT_ZOO_KEEPER_CONNECTION_NOT_CONNECTED";
	public static final String IMAGE_KEY_OBJECT_ZOO_KEEPER_CONNECTION_NOT_CONNECTED_LARGE = "IMAGE_KEY_OBJECT_ZOO_KEEPER_CONNECTION_NOT_CONNECTED_LARGE";

	//
	// Wizard Banner Image Keys
	//

	public static final String IMAGE_KEY_WIZARD_BANNER_NEW_ZNODE = "IMAGE_KEY_WIZARD_BANNER_NEW_ZNODE";
	public static final String IMAGE_KEY_WIZARD_BANNER_NEW_ZOO_KEEPER_CONNECTION = "IMAGE_KEY_WIZARD_BANNER_NEW_ZOO_KEEPER_CONNECTION";

	//
	// Wizard (Extension) Image Keys
	//

	public static final String IMAGE_KEY_WIZARD_NEW_ZNODE = "IMAGE_KEY_WIZARD_NEW_ZNODE";
	public static final String IMAGE_KEY_WIZARD_NEW_ZOO_KEEPER_CONNECTION = "IMAGE_KEY_WIZARD_NEW_ZOO_KEEPER_CONNECTION";

	//
	// Action Image Paths
	//

	private static final String IMAGE_PATH_ACTION = "resources/images/png/actions/";
	private static final String IMAGE_PATH_ACTION_NEW_ZNODE = IMAGE_PATH_ACTION + "new-znode.png";
	private static final String IMAGE_PATH_ACTION_NEW_ZOO_KEEPER_CONNECTION = IMAGE_PATH_ACTION
			+ "new-zookeeper-connection.png";
	private static final String IMAGE_PATH_ACTION_TABLE_EDIT_CHILDREN = IMAGE_PATH_ACTION + "table-edit-children.png";

	//
	// Dialog Image Paths
	//

	private static final String IMAGE_PATH_DIALOG = "resources/images/png/dialogs/";
	private static final String IMAGE_PATH_DIALOG_ADD_AUTH_INFO = IMAGE_PATH_DIALOG + "add-auth-info.png";

	//
	// Object Image Paths
	//

	private static final String IMAGE_PATH_OBJECT = "resources/images/png/objects/";

	private static final String IMAGE_PATH_OBJECT_AUTH = IMAGE_PATH_OBJECT + "auth.png";

	private static final String IMAGE_PATH_OBJECT_ZNODE = IMAGE_PATH_OBJECT + "znode.png";
	private static final String IMAGE_PATH_OBJECT_ZNODE_ACL = IMAGE_PATH_OBJECT + "znode-acl.png";

	private static final String IMAGE_PATH_OBJECT_ZNODE_CHILDREN = IMAGE_PATH_OBJECT + "znode-children.png";
	private static final String IMAGE_PATH_OBJECT_ZNODE_DATA = IMAGE_PATH_OBJECT + "znode-data.png";

	private static final String IMAGE_PATH_OBJECT_ZNODE_EPHEMERAL = IMAGE_PATH_OBJECT + "znode-ephemeral.png";
	private static final String IMAGE_PATH_OBJECT_ZNODE_EPHEMERAL_LARGE = IMAGE_PATH_OBJECT
			+ "znode-ephemeral_large.png";

	private static final String IMAGE_PATH_OBJECT_ZNODE_LARGE = IMAGE_PATH_OBJECT + "znode_large.png";
	private static final String IMAGE_PATH_OBJECT_ZNODE_LEAF = IMAGE_PATH_OBJECT + "znode-leaf.png";
	private static final String IMAGE_PATH_OBJECT_ZNODE_LEAF_LARGE = IMAGE_PATH_OBJECT + "znode-leaf_large.png";
	private static final String IMAGE_PATH_OBJECT_ZNODE_STAT = IMAGE_PATH_OBJECT + "znode-stat.png";

	private static final String IMAGE_PATH_OBJECT_ZOO_KEEPER_CONNECTION = IMAGE_PATH_OBJECT
			+ "zookeeper-connection.png";
	private static final String IMAGE_PATH_OBJECT_ZOO_KEEPER_CONNECTION_LARGE = IMAGE_PATH_OBJECT
			+ "zookeeper-connection_large.png";

	private static final String IMAGE_PATH_OBJECT_ZOO_KEEPER_CONNECTION_NOT_CONNECTED = IMAGE_PATH_OBJECT
			+ "zookeeper-connection_not_connected.png";
	private static final String IMAGE_PATH_OBJECT_ZOO_KEEPER_CONNECTION_NOT_CONNECTED_LARGE = IMAGE_PATH_OBJECT
			+ "zookeeper-connection_not_connected_large.png";

	//
	// Wizard (Extension) Image Paths
	//

	private static final String IMAGE_PATH_WIZARD = "resources/images/png/wizards/";
	private static final String IMAGE_PATH_WIZARD_NEW_ZNODE = IMAGE_PATH_WIZARD + "new-znode.png";
	private static final String IMAGE_PATH_WIZARD_NEW_ZOO_KEEPER_CONNECTION = IMAGE_PATH_WIZARD
			+ "new-zookeeper-connection.png";

	//
	// Wizard Banner Image Paths
	//

	private static final String IMAGE_PATH_WIZARD_BANNER = "resources/images/png/wizard-banners/";
	private static final String IMAGE_PATH_WIZARD_BANNER_NEW_ZNODE = IMAGE_PATH_WIZARD_BANNER + "new-znode.png";
	private static final String IMAGE_PATH_WIZARD_BANNER_NEW_ZOO_KEEPER_CONNECTION = IMAGE_PATH_WIZARD_BANNER
			+ "new-zookeeper-connection.png";

	private static ZooKeeperActivator __Plugin;


	/**
	 * Returns an image descriptor for the image file at the given plug-in relative path
	 * 
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	/**
	 * Returns the managed {@link Image} for the specified key.
	 * 
	 * @param key One of the IMAGE_KEY constants defined by this class.
	 * @return The managed {@link Image} for the specified key.
	 */
	public static Image getManagedImage(String key) {
		return __Plugin.getImageRegistry().get(key);
	}

	/**
	 * Returns the managed {@link ImageDescriptor} for the specified key.
	 * 
	 * @param key One of the IMAGE_KEY constants defined by this class.
	 * @return The managed {@link ImageDescriptor} for the specified key.
	 */
	public static ImageDescriptor getManagedImageDescriptor(String key) {
		return __Plugin.getImageRegistry().getDescriptor(key);
	}

	/**
	 * Returns the shared instance.
	 * 
	 * @return the shared instance.
	 */
	public static ZooKeeperActivator getDefault() {
		return __Plugin;
	}

	public static Image getZnodeLargeImage(Znode znode) {
		return getManagedImage(getZnodeLargeImageKey(znode));
	}

	public static ImageDescriptor getZnodeLargeImageDescriptor(Znode znode) {
		return getManagedImageDescriptor(getZnodeLargeImageKey(znode));
	}

	public static Image getZnodeSmallImage(Znode znode) {
		return getManagedImage(getZnodeSmallImageKey(znode));
	}

	public static ImageDescriptor getZnodeSmallImageDescriptor(Znode znode) {
		return getManagedImageDescriptor(getZnodeSmallImageKey(znode));
	}

	public static void reportError(Throwable t) {
		Throwable cause = t.getCause();
		if (cause != null && cause != t) {
			reportError(cause);
			return;
		}

		boolean showCustomErrorMessageDialog = false;
		int style = StatusManager.LOG;
		String title = "Error";
		String message = t.getLocalizedMessage();
		if (t instanceof KeeperException) {

			KeeperException ke = (KeeperException) t;

			title = "ZooKeeper Error";
			showCustomErrorMessageDialog = true;

			if (ke instanceof InvalidACLException) {
				title = "Invalid ACL";
				message = "ACL is invalid for '" + ke.getPath() + "'.";
			}
			else if (ke instanceof NodeExistsException) {
				title = "Znode Exists";
				message = "Znode '" + ke.getPath() + "' already exists.";
			}
			else if (ke instanceof NoAuthException) {
				title = "Not Authorized";
				message = "Not authorized to perform this action on '" + ke.getPath() + "'.";
			}
			else if (ke instanceof NoNodeException) {
				title = "No Znode";
				message = "Znode '" + ke.getPath() + "' does not exist.";
			}
			else if (ke instanceof NotEmptyException) {
				title = "Not Empty";
				message = "Znode '" + ke.getPath() + "' has children.";
			}

		}

		if (showCustomErrorMessageDialog) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), title, message);
		}
		else {
			style = style | StatusManager.BLOCK;
		}

		Status status = new Status(IStatus.ERROR, PLUGIN_ID, message, t);
		StatusManager.getManager().handle(status, style);
	}

	private static String getZnodeLargeImageKey(Znode znode) {
		return getZnodeSmallImageKey(znode) + "_LARGE";
	}

	private static String getZnodeSmallImageKey(Znode znode) {
		String imageKey = null;

		if (znode.isLeaf()) {
			if (znode.isEphemeral()) {
				imageKey = IMAGE_KEY_OBJECT_ZNODE_EPHEMERAL;
			}
			else {
				imageKey = IMAGE_KEY_OBJECT_ZNODE_LEAF;
			}
		}
		else {
			// if (znode.isEphemeral()) {
			// imageKey = IMAGE_KEY_OBJECT_ZNODE_EPHEMERAL;
			// }
			// else {
			imageKey = IMAGE_KEY_OBJECT_ZNODE;
			// }
		}

		return imageKey;
	}

	private ZooKeeperConnectionDescriptorFiles _ZooKeeperConnectionDescriptorFiles;
	private DataModelManager<ZooKeeperConnectionModel, ZooKeeperConnectionDescriptor, ZooKeeperConnection> _ZooKeeperConnectionModelManager;

	/**
	 * Constructor.
	 */
	public ZooKeeperActivator() {
	}

	public File getZnodeDataDirectory() {
		return getStateLocation().append("Znode").append("Data").toFile();
	}

	/**
	 * Returns the zooKeeperConnectionDescriptorFiles.
	 * 
	 * @return The zooKeeperConnectionDescriptorFiles
	 */
	public ZooKeeperConnectionDescriptorFiles getZooKeeperConnectionDescriptorFiles() {
		return _ZooKeeperConnectionDescriptorFiles;
	}

	/**
	 * Returns the zooKeeperConnectionModelManager.
	 * 
	 * @return The zooKeeperConnectionModelManager
	 */
	public DataModelManager<ZooKeeperConnectionModel, ZooKeeperConnectionDescriptor, ZooKeeperConnection> getZooKeeperConnectionModelManager() {
		return _ZooKeeperConnectionModelManager;
	}

	public File getZooKeeperConnectionsDirectory() {
		return getStateLocation().append("ZooKeeperConnections").toFile();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);

		__Plugin = this;

		_ZooKeeperConnectionDescriptorFiles = new ZooKeeperConnectionDescriptorFiles(getZooKeeperConnectionsDirectory());

		ZooKeeperConnectionModelSource zooKeeperConnectionModelSource = new ZooKeeperConnectionModelSource(
				_ZooKeeperConnectionDescriptorFiles);
		_ZooKeeperConnectionModelManager = new DataModelManager<ZooKeeperConnectionModel, ZooKeeperConnectionDescriptor, ZooKeeperConnection>(
				zooKeeperConnectionModelSource);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		__Plugin = null;

		_ZooKeeperConnectionModelManager.destroy();
		_ZooKeeperConnectionModelManager = null;

		super.stop(context);
	}

	@Override
	protected void initializeImageRegistry(ImageRegistry imageRegistry) {
		super.initializeImageRegistry(imageRegistry);

		imageRegistry.put(IMAGE_KEY_ACTION_NEW_ZNODE, getImageDescriptor(IMAGE_PATH_ACTION_NEW_ZNODE));
		imageRegistry.put(IMAGE_KEY_ACTION_NEW_ZOO_KEEPER_CONNECTION,
				getImageDescriptor(IMAGE_PATH_ACTION_NEW_ZOO_KEEPER_CONNECTION));
		imageRegistry.put(IMAGE_KEY_ACTION_TABLE_EDIT_CHILDREN,
				getImageDescriptor(IMAGE_PATH_ACTION_TABLE_EDIT_CHILDREN));

		imageRegistry.put(IMAGE_KEY_DIALOG_ADD_AUTH_INFO, getImageDescriptor(IMAGE_PATH_DIALOG_ADD_AUTH_INFO));

		imageRegistry.put(IMAGE_KEY_OBJECT_AUTH, getImageDescriptor(IMAGE_PATH_OBJECT_AUTH));
		imageRegistry.put(IMAGE_KEY_OBJECT_ZNODE, getImageDescriptor(IMAGE_PATH_OBJECT_ZNODE));
		imageRegistry.put(IMAGE_KEY_OBJECT_ZNODE_LARGE, getImageDescriptor(IMAGE_PATH_OBJECT_ZNODE_LARGE));
		imageRegistry.put(IMAGE_KEY_OBJECT_ZNODE_LEAF, getImageDescriptor(IMAGE_PATH_OBJECT_ZNODE_LEAF));
		imageRegistry.put(IMAGE_KEY_OBJECT_ZNODE_LEAF_LARGE, getImageDescriptor(IMAGE_PATH_OBJECT_ZNODE_LEAF_LARGE));
		imageRegistry.put(IMAGE_KEY_OBJECT_ZNODE_EPHEMERAL, getImageDescriptor(IMAGE_PATH_OBJECT_ZNODE_EPHEMERAL));
		imageRegistry.put(IMAGE_KEY_OBJECT_ZNODE_EPHEMERAL_LARGE,
				getImageDescriptor(IMAGE_PATH_OBJECT_ZNODE_EPHEMERAL_LARGE));

		imageRegistry.put(IMAGE_KEY_OBJECT_ZNODE_ACL, getImageDescriptor(IMAGE_PATH_OBJECT_ZNODE_ACL));
		imageRegistry.put(IMAGE_KEY_OBJECT_ZNODE_CHILDREN, getImageDescriptor(IMAGE_PATH_OBJECT_ZNODE_CHILDREN));
		imageRegistry.put(IMAGE_KEY_OBJECT_ZNODE_DATA, getImageDescriptor(IMAGE_PATH_OBJECT_ZNODE_DATA));
		imageRegistry.put(IMAGE_KEY_OBJECT_ZNODE_STAT, getImageDescriptor(IMAGE_PATH_OBJECT_ZNODE_STAT));

		imageRegistry.put(IMAGE_KEY_OBJECT_ZOO_KEEPER_CONNECTION,
				getImageDescriptor(IMAGE_PATH_OBJECT_ZOO_KEEPER_CONNECTION));
		imageRegistry.put(IMAGE_KEY_OBJECT_ZOO_KEEPER_CONNECTION_LARGE,
				getImageDescriptor(IMAGE_PATH_OBJECT_ZOO_KEEPER_CONNECTION_LARGE));

		imageRegistry.put(IMAGE_KEY_OBJECT_ZOO_KEEPER_CONNECTION_NOT_CONNECTED,
				getImageDescriptor(IMAGE_PATH_OBJECT_ZOO_KEEPER_CONNECTION_NOT_CONNECTED));
		imageRegistry.put(IMAGE_KEY_OBJECT_ZOO_KEEPER_CONNECTION_NOT_CONNECTED_LARGE,
				getImageDescriptor(IMAGE_PATH_OBJECT_ZOO_KEEPER_CONNECTION_NOT_CONNECTED_LARGE));

		imageRegistry.put(IMAGE_KEY_WIZARD_NEW_ZOO_KEEPER_CONNECTION,
				getImageDescriptor(IMAGE_PATH_WIZARD_NEW_ZOO_KEEPER_CONNECTION));
		imageRegistry.put(IMAGE_KEY_WIZARD_NEW_ZNODE, getImageDescriptor(IMAGE_PATH_WIZARD_NEW_ZNODE));

		imageRegistry.put(IMAGE_KEY_WIZARD_BANNER_NEW_ZOO_KEEPER_CONNECTION,
				getImageDescriptor(IMAGE_PATH_WIZARD_BANNER_NEW_ZOO_KEEPER_CONNECTION));
		imageRegistry.put(IMAGE_KEY_WIZARD_BANNER_NEW_ZNODE, getImageDescriptor(IMAGE_PATH_WIZARD_BANNER_NEW_ZNODE));


	}
}
