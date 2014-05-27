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

package org.fusesource.ide.zk.zookeeper.data;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper.States;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

// TODO:  Handle reconnect for all operations.

/**
 * Proxy for a {@link ZooKeeper} instance.
 * 
 * @author Mark Masse
 */
public final class ZooKeeperConnection implements Watcher {

    private final ZooKeeperConnectionDescriptor _Descriptor;
    private CopyOnWriteArrayList<IZooKeeperConnectionEventListener> _EventListeners;
    private ZooKeeper _ZooKeeper;

    /**
     * Constructor.
     * 
     * @param descriptor The connection configuration details.
     */
    public ZooKeeperConnection(ZooKeeperConnectionDescriptor descriptor) {
        _Descriptor = descriptor;
    }

    /**
     * TODO: Comment.
     * 
     * @param listener
     * @return
     */
    public void addEventListener(IZooKeeperConnectionEventListener listener) {

        if (_EventListeners == null) {
            _EventListeners = new CopyOnWriteArrayList<IZooKeeperConnectionEventListener>();
        }

        if (!_EventListeners.contains(listener)) {
            _EventListeners.add(listener);
        }
    }

    /**
     * @see org.apache.zookeeper.ZooKeeper#close()
     */
    public void close() throws InterruptedException {

        if (_ZooKeeper == null) {
            return;
        }

        _ZooKeeper.close();
        _ZooKeeper = null;
        fireConnectionStateChanged();
    }

    /**
     * Establishes the ZooKeeper connection based on the {@link ZooKeeperConnectionDescriptor descriptor's}
     * configuration.
     * 
     * @throws IOException In cases of network failure.
     * @throws IllegalArgumentException If an invalid chroot path is specified.
     * 
     * @see org.apache.zookeeper.ZooKeeper#ZooKeeper(String, int, Watcher)
     */
    public void connect() throws IOException {
        if (_ZooKeeper != null) {

            if (_ZooKeeper.getState().isAlive()) {
                return;
            }
        }

        String connectString = _Descriptor.getConnectString();
        _ZooKeeper = new ZooKeeper(connectString, _Descriptor.getSessionTimeout(), this);
    }

    /**
     * @see org.apache.zookeeper.ZooKeeper#create(java.lang.String, byte[], java.util.List,
     *      org.apache.zookeeper.CreateMode)
     */
    public String create(String path, byte[] data, List<ACL> acl, CreateMode createMode) throws KeeperException,
            InterruptedException {
        return _ZooKeeper.create(path, data, acl, createMode);
    }

    /**
     * @see org.apache.zookeeper.ZooKeeper#delete(java.lang.String, int)
     */
    public void delete(String path, int version) throws InterruptedException, KeeperException {
        _ZooKeeper.delete(path, version);
    }

    /**
     * @see org.apache.zookeeper.ZooKeeper#exists(java.lang.String, boolean)
     */
    public Stat exists(String path, boolean watch) throws KeeperException, InterruptedException {
        return _ZooKeeper.exists(path, watch);
    }

    /**
     * @see org.apache.zookeeper.ZooKeeper#exists(java.lang.String, org.apache.zookeeper.Watcher)
     */
    public Stat exists(String path, Watcher watcher) throws KeeperException, InterruptedException {
        return _ZooKeeper.exists(path, watcher);
    }

    /**
     * @see org.apache.zookeeper.ZooKeeper#getACL(java.lang.String, org.apache.zookeeper.data.Stat)
     */
    public List<ACL> getACL(String path, Stat stat) throws KeeperException, InterruptedException {
        return _ZooKeeper.getACL(path, stat);
    }

    /**
     * @see org.apache.zookeeper.ZooKeeper#getChildren(java.lang.String, boolean)
     */
    public List<String> getChildren(String path, boolean watch) throws KeeperException, InterruptedException {
        return _ZooKeeper.getChildren(path, watch);
    }

    /**
     * @see org.apache.zookeeper.ZooKeeper#getChildren(java.lang.String, org.apache.zookeeper.Watcher)
     */
    public List<String> getChildren(String path, Watcher watcher) throws KeeperException, InterruptedException {
        return _ZooKeeper.getChildren(path, watcher);
    }

    /**
     * @see org.apache.zookeeper.ZooKeeper#getData(java.lang.String, boolean, org.apache.zookeeper.data.Stat)
     */
    public byte[] getData(String path, boolean watch, Stat stat) throws KeeperException, InterruptedException {
        return _ZooKeeper.getData(path, watch, stat);
    }

    /**
     * @see org.apache.zookeeper.ZooKeeper#getData(java.lang.String, org.apache.zookeeper.Watcher,
     *      org.apache.zookeeper.data.Stat)
     */
    public byte[] getData(String path, Watcher watcher, Stat stat) throws KeeperException, InterruptedException {
        return _ZooKeeper.getData(path, watcher, stat);
    }

    /**
     * Returns the descriptor.
     * 
     * @return The descriptor
     */
    public ZooKeeperConnectionDescriptor getDescriptor() {
        return _Descriptor;
    }

    /**
     * @see org.apache.zookeeper.ZooKeeper#getSessionId()
     */
    public long getSessionId() {
        if (_ZooKeeper == null) {
            return 0;
        }
        
        return _ZooKeeper.getSessionId();
    }

    /**
     * @see org.apache.zookeeper.ZooKeeper#getSessionPasswd()
     */
    public byte[] getSessionPasswd() {
        if (_ZooKeeper == null) {
            return null;
        }
        
        return _ZooKeeper.getSessionPasswd();
    }

    /**
     * @see org.apache.zookeeper.ZooKeeper#getState()
     */
    public States getState() {
        if (_ZooKeeper == null) {
            return States.CLOSED;
        }

        return _ZooKeeper.getState();
    }

    /**
     * Returns the znode specified by the path.
     * 
     * @param path The znode path.
     * @return A new znode instance based on the specified path.
     * 
     * @throws KeeperException If the server signals an error.
     * @throws InterruptedException If the server transaction is interrupted.
     */
    public Znode getZnode(String path) throws KeeperException, InterruptedException {

        if (!isConnected()) {
            return null;
        }

        Znode znode = null;
        Stat stat = exists(path, false);
        if (stat == null) {
            return null;
        }

        znode = new Znode(path);
        znode.setStat(stat);

        byte[] data = null;
        try {
            data = getData(path, false, stat);
            znode.setData(data);
            znode.setDataReadable(true);
        }
        catch (Exception e) {
        }

        List<ACL> acl = null;
        try {
            acl = getACL(path, stat);
            znode.setAcl(acl);
            znode.setAclReadable(true);
        }
        catch (Exception e) {
        }

        List<String> children = null;
        try {
            children = getChildren(path, false);
            znode.setChildren(children);
            znode.setChildrenReadable(true);
        }
        catch (Exception e) {
        }

        return znode;
    }

    /**
     * Returns <code>true</code> if the connection is currently established.
     * 
     * @return <code>true</code> if the connection is currently established.
     */
    public boolean isConnected() {
        return (getState() == States.CONNECTED);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.zookeeper.Watcher#process(org.apache.zookeeper.WatchedEvent)
     */
    @Override
    public void process(WatchedEvent event) {
        KeeperState state = event.getState();

        if (state == KeeperState.SyncConnected) {
            ZooKeeperConnectionDescriptor zooKeeperConnectionDescriptor = getDescriptor();
            List<AuthInfo> authInfos = zooKeeperConnectionDescriptor.getAuthInfos();
            if (authInfos != null && !authInfos.isEmpty()) {

                for (AuthInfo authInfo : authInfos) {
                    String scheme = authInfo.getScheme();
                    byte[] auth;

                    try {
                        auth = authInfo.getAuth();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                        continue;
                    }

                    // TODO: Is the lack of thread saftey of this method a concern here?
                    addAuthInfo(scheme, auth);
                }
            }
        }

        fireConnectionStateChanged();        
    }

    /**
     * TODO: Comment.
     * 
     * @param listener
     * @return
     */
    public void removeEventListener(IZooKeeperConnectionEventListener listener) {

        if (_EventListeners == null) {
            return;
        }

        if (!_EventListeners.contains(listener)) {
            return;
        }

        _EventListeners.remove(listener);
    }

    /**
     * @see org.apache.zookeeper.ZooKeeper#setACL(java.lang.String, java.util.List, int)
     */
    public Stat setACL(String path, List<ACL> acl, int version) throws KeeperException, InterruptedException {
        return _ZooKeeper.setACL(path, acl, version);
    }

    /**
     * @see org.apache.zookeeper.ZooKeeper#setData(java.lang.String, byte[], int)
     */
    public Stat setData(String path, byte[] data, int version) throws KeeperException, InterruptedException {
        return _ZooKeeper.setData(path, data, version);
    }

    @Override
    public String toString() {
        return "ZooKeeperConnection [Descriptor=" + _Descriptor + "]";
    }

    /**
     * @see org.apache.zookeeper.ZooKeeper#addAuthInfo(java.lang.String, byte[])
     */
    private void addAuthInfo(String scheme, byte[] auth) {
        _ZooKeeper.addAuthInfo(scheme, auth);
    }

    private void fireConnectionStateChanged() {

        if (_EventListeners != null) {

            ZooKeeperConnectionEvent event = new ZooKeeperConnectionEvent(this);
            for (IZooKeeperConnectionEventListener listener : _EventListeners) {
                listener.connectionStateChanged(event);
            }
        }
    }

}
