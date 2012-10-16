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

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.ACL;
import org.fusesource.ide.zk.zookeeper.data.Znode;
import org.fusesource.ide.zk.zookeeper.data.ZooKeeperConnection;
import org.fusesource.ide.zk.core.model.DataModelSource;
import org.fusesource.ide.zk.core.model.DataModelSourceException;
import org.fusesource.ide.zk.core.model.IDataModelSource;
import org.fusesource.ide.zk.core.model.OperationNotSupportedException;


/**
 * The {@link IDataModelSource source} for {@link ZnodeModel znode models}. This source is backed by a
 * {@link ZooKeeperConnectionModel} instance.
 * 
 * @author Mark Masse
 */
public class ZnodeModelSource extends DataModelSource<ZnodeModel, String, Znode> {

    private final ZooKeeperConnectionModel _ZooKeeperConnectionModel;

    private final Set<String> EMPTY_KEYS = Collections.emptySet();

    /**
     * Constructor.
     * 
     * @param zooKeeperConnectionModel The {@link ZooKeeperConnectionModel} used to retrieve znode data, stat, children and ACL.
     */
    public ZnodeModelSource(ZooKeeperConnectionModel zooKeeperConnectionModel) {
        _ZooKeeperConnectionModel = zooKeeperConnectionModel;
    }

    @Override
    public void deleteData(String path) throws DataModelSourceException {
        ZooKeeperConnection zooKeeperConnection = getZooKeeperConnection();

        try {
            Znode znode = getData(path);
            zooKeeperConnection.delete(path, znode.getStat().getVersion());
        }
        catch (Exception e) {
            throw new DataModelSourceException(e);
        }
    }

    @Override
    public Set<String> findKeys(Object criteria) throws DataModelSourceException {

        if (!(criteria instanceof Znode)) {
            throw new OperationNotSupportedException();
        }

        Znode znode = (Znode) criteria;

        List<String> childRelativePaths = znode.getChildren();
        if (childRelativePaths == null) {
            return EMPTY_KEYS;
        }

        String pathPrefix = znode.getPath();
        if (!pathPrefix.endsWith(Znode.PATH_SEPARATOR_STRING)) {
            pathPrefix = pathPrefix + Znode.PATH_SEPARATOR_STRING;
        }

        Set<String> keySet = new TreeSet<String>();

        for (String childRelativePath : childRelativePaths) {
            String childFullPath = pathPrefix + childRelativePath;
            keySet.add(childFullPath);
        }

        return keySet;
    }

    @Override
    public Znode getData(String path) throws DataModelSourceException {        
        
        try {
            return getZooKeeperConnection().getZnode(path);
        }
        catch (Exception e) {
            throw new DataModelSourceException(e);
        }
    }

    @Override
    public String insertData(String path, Znode znode) throws DataModelSourceException {

        ZooKeeperConnection zooKeeperConnection = getZooKeeperConnection();

        CreateMode createMode = CreateMode.PERSISTENT;

        if (znode.isEphemeral()) {
            createMode = CreateMode.EPHEMERAL;
        }
        if (znode.isSequential()) {
            createMode = CreateMode.PERSISTENT_SEQUENTIAL;
        }
        if (znode.isEphemeral() && znode.isSequential()) {
            createMode = CreateMode.EPHEMERAL_SEQUENTIAL;
        }

        byte[] data = znode.getData();
        List<ACL> acl = znode.getAcl();

        try {
            zooKeeperConnection.create(path, data, acl, createMode);
        }
        catch (Exception e) {
            throw new DataModelSourceException(e);
        }

        return path;
    }

    @Override
    public boolean isDeleteDataSupported() {
        return true;
    }

    @Override
    public boolean isFindKeysSupported(Object criteria) {
        return (criteria instanceof Znode);
    }

    @Override
    public boolean isInsertDataSupported() {
        return true;
    }

    @Override
    public boolean isUpdateDataSupported() {
        return true;
    }

    @Override
    public void updateData(ZnodeModel model) throws DataModelSourceException {

        ZooKeeperConnection zooKeeperConnection = getZooKeeperConnection();

        Znode znode = model.getData();
        String path = model.getKey();

        try {

            if (model.isDirtyAcl()) {
                List<ACL> acl = znode.getAcl();
                zooKeeperConnection.setACL(path, acl, -1);
                model.setDirtyAcl(false);
            }
            
            if (model.isDirtyData()) {
                byte[] data = znode.getData();
                zooKeeperConnection.setData(path, data, -1);
                model.setDirtyData(false);
            }


        }
        catch (Exception e) {
            throw new DataModelSourceException(e);
        }
    }

    @Override
    protected ZnodeModel createModelInternal(String path) {
        return new ZnodeModel(path, _ZooKeeperConnectionModel);
    }

    private ZooKeeperConnection getZooKeeperConnection() {
        return _ZooKeeperConnectionModel.getData();
    }
}
