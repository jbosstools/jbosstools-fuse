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

import java.util.Set;

import org.fusesource.ide.zk.zookeeper.data.ZooKeeperConnection;
import org.fusesource.ide.zk.zookeeper.data.ZooKeeperServer;
import org.fusesource.ide.zk.zookeeper.data.ZooKeeperServerDescriptor;
import org.fusesource.ide.zk.core.model.DataModelSource;
import org.fusesource.ide.zk.core.model.DataModelSourceException;
import org.fusesource.ide.zk.core.model.IDataModelSource;


/**
 * {@link IDataModelSource} for {@link ZooKeeperServerModel}.
 * 
 * @author Mark Masse
 */
public class ZooKeeperServerModelSource extends
        DataModelSource<ZooKeeperServerModel, ZooKeeperServerDescriptor, ZooKeeperServer> {

    private final ZooKeeperConnectionModel _ZooKeeperConnectionModel;

    /**
     * Constructor.
     * 
     * @param zooKeeperConnectionModel The {@link ZooKeeperConnectionModel} backing this source.
     */
    public ZooKeeperServerModelSource(ZooKeeperConnectionModel zooKeeperConnectionModel) {
        _ZooKeeperConnectionModel = zooKeeperConnectionModel;
    }

    @Override
    public ZooKeeperServer getData(ZooKeeperServerDescriptor descriptor) throws DataModelSourceException {
        return new ZooKeeperServer(descriptor);
    }

    @Override
    public Set<ZooKeeperServerDescriptor> getKeys() throws DataModelSourceException {
        return getZooKeeperConnection().getDescriptor().getServers();
    }

    @Override
    public boolean isGetKeysSupported() {
        return true;
    }

    @Override
    protected ZooKeeperServerModel createModelInternal(ZooKeeperServerDescriptor descriptor) {
        return new ZooKeeperServerModel(descriptor, _ZooKeeperConnectionModel);
    }

    private ZooKeeperConnection getZooKeeperConnection() {
        return _ZooKeeperConnectionModel.getData();
    }
}
