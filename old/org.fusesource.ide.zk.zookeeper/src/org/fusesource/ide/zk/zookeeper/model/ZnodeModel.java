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

import java.util.List;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.fusesource.ide.zk.zookeeper.data.Znode;
import org.fusesource.ide.zk.zookeeper.data.ZooKeeperConnection;
import org.fusesource.ide.zk.core.model.DataModel;
import org.fusesource.ide.zk.core.model.DataModelEvent;
import org.fusesource.ide.zk.core.model.DataModelManager;
import org.fusesource.ide.zk.core.model.IDataModelEventListener;
import org.fusesource.ide.zk.core.model.IGenericDataModelEventListener;


/**
 * <p>
 * {@link DataModel} wrapper for a znode. The model key is a znode {@link String} path and the model data is a
 * {@link Znode}.
 * </p>
 * <p>
 * Internally this class maintains {@link Watcher ZooKeeper watchers} to track changes to the <b>data</b>,
 * <b>children</b>, and <b>stat</b>. This model's registered event listeners (both {@link IDataModelEventListener typed}
 * and {@link IGenericDataModelEventListener generic}) will be notified when any of these attributes change.
 * 
 * @see ZnodeModelSource
 * @see ZooKeeperConnectionModel
 * @see Stat
 * @see ACL
 * 
 * @author Mark Masse
 */
public final class ZnodeModel extends DataModel<ZnodeModel, String, Znode> {

    private boolean _DirtyAcl;
    private boolean _DirtyData;

    private final ExistsWatcher _ExistsWatcher;
    private final GetChildrenWatcher _GetChildrenWatcher;
    private final GetDataWatcher _GetDataWatcher;

    private final ZooKeeperConnectionModel _ZooKeeperConnectionModel;
    private final ZooKeeperConnectionModelEventListener _ZooKeeperConnectionModelEventListener;

    /**
     * Constructor.
     * 
     * @param path The znode path.
     * @param zooKeeperConnectionModel The {@link ZooKeeperConnectionModel} owner of this {@link ZnodeModel}.
     * 
     * @see ZnodeModelSource#createModel(String)
     */
    ZnodeModel(String path, ZooKeeperConnectionModel zooKeeperConnectionModel) {
        super(path);
        _ZooKeeperConnectionModel = zooKeeperConnectionModel;

        _ExistsWatcher = new ExistsWatcher();
        _GetChildrenWatcher = new GetChildrenWatcher();
        _GetDataWatcher = new GetDataWatcher();

        _ZooKeeperConnectionModelEventListener = new ZooKeeperConnectionModelEventListener();
        _ZooKeeperConnectionModel.addEventListener(_ZooKeeperConnectionModelEventListener);
    }

    /**
     * Returns a {@link List} of this znode model's child models.
     * 
     * @return A {@link List} of this znode model's child models.
     */
    public List<ZnodeModel> getChildModels() {
        Znode znode = getData();
        DataModelManager<ZnodeModel, String, Znode> manager = getManager();
        if (manager.isDestroyed()) {
            return null;
        }

        return manager.findModels(znode);
    }

    @Override
    public ZooKeeperConnectionModel getOwnerModel() {
        return _ZooKeeperConnectionModel;
    }

    @Override
    public ZnodeModel getParentModel() {
        Znode znode = getData();
        DataModelManager<ZnodeModel, String, Znode> manager = getManager();
        if (manager.isDestroyed()) {
            return null;
        }

        String parentPath = znode.getParentPath();
        if (parentPath == null) {
            return null;
        }

        return manager.getModel(parentPath);
    }

    /**
     * Returns <code>true</code> if the znode ACL has been modified.
     * 
     * @return <code>true</code> if the znode ACL has been modified.
     */
    public boolean isDirtyAcl() {
        return _DirtyAcl;
    }

    /**
     * Returns <code>true</code> if the znode data has been modified.
     * 
     * @return <code>true</code> if the znode data has been modified.
     */
    public boolean isDirtyData() {
        return _DirtyData;
    }

    /**
     * Sets the dirty ACL flag.
     * 
     * @param dirtyAcl The flag value. <code>true</code> indicates that the ACL has been modified.
     * 
     * @see ZnodeModelSource#updateData(ZnodeModel)
     */
    public void setDirtyAcl(boolean dirtyAcl) {
        _DirtyAcl = dirtyAcl;
    }

    /**
     * Sets the dirty data flag.
     * 
     * @param dirtyData The flag value. <code>true</code> indicates that the data has been modified.
     * 
     * @see ZnodeModelSource#updateData(ZnodeModel)
     */
    public void setDirtyData(boolean dirtyData) {
        _DirtyData = dirtyData;
    }

    @Override
    protected ZnodeModel getThis() {
        return this;
    }

    @Override
    protected void hookAfterDestroyed() {
        _ZooKeeperConnectionModel.removeEventListener(_ZooKeeperConnectionModelEventListener);
    }

    @Override
    protected void hookBeforeFirstListenerAdded() {
        attachWatchers();
    }

    private void attachWatchers() {

        if (!getZooKeeperConnection().isConnected()) {
            return;
        }

        Stat stat = updateZnodeStat(_ExistsWatcher);
        if (stat != null) {
            updateZnodeAcl(stat);
            updateZnodeData(stat, _GetDataWatcher);
            updateZnodeChildren(_GetChildrenWatcher);
        }
    }

    private ZooKeeperConnection getZooKeeperConnection() {
        return _ZooKeeperConnectionModel.getData();
    }

    private void updateZnodeAcl(Stat stat) {

        if (isDestroyed()) {
            return;
        }

        Znode znode = getData();
        ZooKeeperConnection zooKeeperConnection = getZooKeeperConnection();
        String path = znode.getPath();
        List<ACL> acl;
        try {
            acl = zooKeeperConnection.getACL(path, stat);
            znode.setAcl(acl);
            znode.setAclReadable(true);
        }
        catch (Exception e) {
            znode.setAclReadable(false);
        }

    }

    private void updateZnodeChildren(Watcher watcher) {

        if (isDestroyed()) {
            return;
        }

        Znode znode = getData();
        ZooKeeperConnection zooKeeperConnection = getZooKeeperConnection();
        String path = znode.getPath();
        List<String> children;
        try {
            if (watcher != null) {
                children = zooKeeperConnection.getChildren(path, watcher);
            }
            else {
                children = zooKeeperConnection.getChildren(path, false);
            }

            znode.setChildren(children);
            znode.setChildrenReadable(true);
        }
        catch (Exception e) {
            znode.setChildrenReadable(false);
        }
    }

    private void updateZnodeData(Stat stat, Watcher watcher) {

        if (isDestroyed()) {
            return;
        }

        Znode znode = getData();
        ZooKeeperConnection zooKeeperConnection = getZooKeeperConnection();
        String path = znode.getPath();
        byte[] data;

        try {
            if (watcher != null) {
                data = zooKeeperConnection.getData(path, watcher, stat);
            }
            else {
                data = zooKeeperConnection.getData(path, false, stat);
            }

            znode.setData(data);
            znode.setDataReadable(true);
        }
        catch (Exception e) {
            znode.setDataReadable(false);
        }
    }

    private Stat updateZnodeStat(Watcher watcher) {

        if (isDestroyed()) {
            return null;
        }

        Znode znode = getData();
        String path = znode.getPath();
        ZooKeeperConnection zooKeeperConnection = getZooKeeperConnection();
        Stat stat = null;

        try {
            if (watcher != null) {
                stat = zooKeeperConnection.exists(path, watcher);
            }
            else {
                stat = zooKeeperConnection.exists(path, false);
            }
            znode.setStat(stat);
        }
        catch (Exception e) {
        }

        if (stat == null) {
            destroy();
        }

        return stat;
    }

    private abstract class BaseWatcher implements Watcher {

        @Override
        public void process(WatchedEvent event) {

            if (isDestroyed()) {
                return;
            }
            
            if (!getZooKeeperConnection().isConnected()) {
                return;
            }

            String eventPath = event.getPath();
            EventType eventType = event.getType();

            Znode znode = getData();
            String path = znode.getPath();

            if (path.equals(eventPath) && eventType.equals(EventType.NodeDeleted)) {
                destroy();
                return;
            }

            processUpdate(event);

            fireDataModelDataChanged();
        }

        protected abstract void processUpdate(WatchedEvent event);
    }

    private class ExistsWatcher extends BaseWatcher {

        @Override
        protected void processUpdate(WatchedEvent event) {
            Stat stat = updateZnodeStat(_ExistsWatcher);
            if (stat != null) {
                updateZnodeData(stat, null);
                updateZnodeAcl(stat);
            }
        }
    }

    private class GetChildrenWatcher extends BaseWatcher {

        @Override
        protected void processUpdate(WatchedEvent event) {
            updateZnodeStat(null);
            updateZnodeChildren(_GetChildrenWatcher);
        }
    }

    private class GetDataWatcher extends BaseWatcher {

        @Override
        protected void processUpdate(WatchedEvent event) {

            Znode znode = getData();
            Stat stat = znode.getStat();
            updateZnodeData(stat, _GetDataWatcher);
            updateZnodeAcl(stat);
        }
    }

    private class ZooKeeperConnectionModelEventListener implements IDataModelEventListener<ZooKeeperConnectionModel> {

        @Override
        public void dataModelDataChanged(DataModelEvent<ZooKeeperConnectionModel> event) {
            zooKeeperConnectionModelChanged(event);
        }

        @Override
        public void dataModelDataRefreshed(DataModelEvent<ZooKeeperConnectionModel> event) {
            zooKeeperConnectionModelChanged(event);
        }

        @Override
        public void dataModelDestroyed(DataModelEvent<ZooKeeperConnectionModel> event) {
        }

        private void zooKeeperConnectionModelChanged(DataModelEvent<ZooKeeperConnectionModel> event) {
            if (hasListeners()) {
                attachWatchers();
            }
        }

    }

}
