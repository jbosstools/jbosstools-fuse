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

package org.fusesource.ide.zk.zookeeper.model;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.fusesource.ide.commons.ui.actions.IConnectable;
import org.fusesource.ide.zk.zookeeper.data.IZooKeeperConnectionEventListener;
import org.fusesource.ide.zk.zookeeper.data.Znode;
import org.fusesource.ide.zk.zookeeper.data.ZooKeeperConnection;
import org.fusesource.ide.zk.zookeeper.data.ZooKeeperConnectionDescriptor;
import org.fusesource.ide.zk.zookeeper.data.ZooKeeperConnectionEvent;
import org.fusesource.ide.zk.zookeeper.data.ZooKeeperServer;
import org.fusesource.ide.zk.zookeeper.data.ZooKeeperServerDescriptor;
import org.fusesource.ide.zk.core.model.DataModel;
import org.fusesource.ide.zk.core.model.DataModelManager;


/**
 * {@link DataModel} wrapper for a {@link ZooKeeperConnection}. The model key is a {@link ZooKeeperConnectionDescriptor}
 * and the model data is a {@link ZooKeeperConnection}.
 * 
 * @author Mark Masse
 */
public final class ZooKeeperConnectionModel extends
DataModel<ZooKeeperConnectionModel, ZooKeeperConnectionDescriptor, ZooKeeperConnection> implements IConnectable {

	private DataModelManager<ZnodeModel, String, Znode> _ZnodeModelManager;
	private ZooKeeperConnectionEventListener _ZooKeeperConnectionEventListener;
	private DataModelManager<ZooKeeperServerModel, ZooKeeperServerDescriptor, ZooKeeperServer> _ZooKeeperServerModelManager;
	private final ZooKeeperServersModelCategory _ZooKeeperServersModelCategory;
	private boolean triedConnect;

	/**
	 * Constructor.
	 * 
	 * @param descriptor The {@link ZooKeeperConnectionDescriptor}.
	 * 
	 * @see ZooKeeperConnectionModelSource#createModel(ZooKeeperConnectionDescriptor)
	 */
	ZooKeeperConnectionModel(ZooKeeperConnectionDescriptor descriptor) {
		super(descriptor);
		_ZooKeeperServersModelCategory = new ZooKeeperServersModelCategory(this);
		_ZooKeeperConnectionEventListener = new ZooKeeperConnectionEventListener();
	}

	/**
	 * Returns the model for the root znode.
	 * 
	 * @return The model for the root znode.
	 */
	public ZnodeModel getRootZnodeModel() {
		return _ZnodeModelManager.getModel(Znode.ROOT_PATH);
	}

	/**
	 * Returns the set of {@link ZooKeeperServerDescriptor} associated with this connection.
	 * 
	 * @return The set of {@link ZooKeeperServerDescriptor} associated with this connection.
	 */
	public Set<ZooKeeperServerDescriptor> getZooKeeperServerDescriptors() {
		return _ZooKeeperServerModelManager.getKeys();
	}

	/**
	 * Returns the {@link ZooKeeperServerModel} associated with the specified {@link ZooKeeperServerDescriptor}.
	 * 
	 * @param descriptor The {@link ZooKeeperServerDescriptor} key for the desired {@link ZooKeeperServerModel}.
	 * @return The {@link ZooKeeperServerModel} associated with the specified {@link ZooKeeperServerDescriptor}.
	 */
	public ZooKeeperServerModel getZooKeeperServerModel(ZooKeeperServerDescriptor descriptor) {
		return _ZooKeeperServerModelManager.getModel(descriptor);
	}

	/**
	 * Returns the list of {@link ZooKeeperServerModel server models}.
	 * 
	 * @return A list of ZooKeeperServerModel representing the ZooKeeper servers associated with the ZooKeeper
	 *         connection.
	 */
	public List<ZooKeeperServerModel> getZooKeeperServerModels() {
		return _ZooKeeperServerModelManager.getModels();
	}

	/**
	 * Returns the {@link ZooKeeperServersModelCategory}.
	 * 
	 * @return The {@link ZooKeeperServersModelCategory}.
	 */
	public ZooKeeperServersModelCategory getZooKeeperServersModelCategory() {
		return _ZooKeeperServersModelCategory;
	}

	/**
	 * Returns <code>true</code> if the connection is currently established.
	 * 
	 * @return <code>true</code> if the connection is currently established.
	 */
	@Override
	public boolean isConnected() {
		ZooKeeperConnection zooKeeperConnection = getData();
		return (zooKeeperConnection != null && zooKeeperConnection.isConnected());
	}

	@Override
	public boolean shouldConnect() {
		return !isConnected() && !triedConnect;
	}



	@Override
	public void connect() throws Exception {
		ZooKeeperConnection zooKeeperConnection = getData();
		if (zooKeeperConnection != null) {
			doConnect(zooKeeperConnection);
		}
	}

	@Override
	public void disconnect() throws Exception {
		ZooKeeperConnection zooKeeperConnection = getData();
		if (zooKeeperConnection != null) {
			doClose(zooKeeperConnection);
		}
	}

	@Override
	protected ZooKeeperConnectionModel getThis() {
		return this;
	}

	@Override
	protected void hookAfterDestroyed() {

		if (_ZnodeModelManager != null) {
			_ZnodeModelManager.destroy();
		}

		if (_ZooKeeperServerModelManager != null) {
			_ZooKeeperServerModelManager.destroy();
		}

		ZooKeeperConnection zooKeeperConnection = getData();

		if (zooKeeperConnection != null) {
			zooKeeperConnection.removeEventListener(_ZooKeeperConnectionEventListener);

			doClose(zooKeeperConnection);
		}
	}

	@Override
	protected void hookAfterSetData() {

		ZooKeeperConnection zooKeeperConnection = getData();
		if (zooKeeperConnection != null) {

			zooKeeperConnection.addEventListener(_ZooKeeperConnectionEventListener);

			if (hasListeners()) {
				shouldConnect(zooKeeperConnection);
			}
		}

		if (_ZooKeeperServerModelManager == null) {
			ZooKeeperServerModelSource zooKeeperServerModelSource = new ZooKeeperServerModelSource(this);
			_ZooKeeperServerModelManager = new DataModelManager<ZooKeeperServerModel, ZooKeeperServerDescriptor, ZooKeeperServer>(
					zooKeeperServerModelSource);
		}
		else {
			_ZooKeeperServerModelManager.refreshManagedModels();
		}

		if (_ZnodeModelManager == null) {
			ZnodeModelSource znodeModelSource = new ZnodeModelSource(this);
			_ZnodeModelManager = new DataModelManager<ZnodeModel, String, Znode>(znodeModelSource);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fusesource.ide.zk.zookeeper.model.DataModel#beforeFirstListenerAdded()
	 */
	@Override
	protected void hookBeforeFirstListenerAdded() {
		super.hookBeforeFirstListenerAdded();

		ZooKeeperConnection zooKeeperConnection = getData();

		shouldConnect(zooKeeperConnection);
	}

	protected void shouldConnect(ZooKeeperConnection zooKeeperConnection) {
		boolean autoConnect = false;
		if (autoConnect) {
			doConnect(zooKeeperConnection);
		}
	}

	protected void doConnect(ZooKeeperConnection zooKeeperConnection) {
		if (!zooKeeperConnection.isConnected()) {
			triedConnect = true;
			try {
				zooKeeperConnection.connect();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	protected void doClose(ZooKeeperConnection zooKeeperConnection) {
		if (zooKeeperConnection.isConnected() || triedConnect) {
			try {
				zooKeeperConnection.close();
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				triedConnect = false;
			}
		}
	}

	@Override
	protected void hookBeforeSetData() {
		ZooKeeperConnection zooKeeperConnection = getData();
		if (zooKeeperConnection != null) {

			doClose(zooKeeperConnection);

			zooKeeperConnection.removeEventListener(_ZooKeeperConnectionEventListener);
		}
	}

	private class ZooKeeperConnectionEventListener implements IZooKeeperConnectionEventListener {

		@Override
		public void connectionStateChanged(ZooKeeperConnectionEvent event) {

			ZooKeeperConnection zooKeeperConnection = event.getZooKeeperConnection();
			if (zooKeeperConnection.isConnected()) {
				if (_ZnodeModelManager != null) {
					_ZnodeModelManager.refreshManagedModels();
				}
			}

			fireDataModelDataChanged();
		}
	}

}
