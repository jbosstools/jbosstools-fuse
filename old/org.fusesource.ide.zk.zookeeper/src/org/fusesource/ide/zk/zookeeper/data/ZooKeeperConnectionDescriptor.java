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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.zookeeper.ZooKeeper;
import org.fusesource.ide.zk.core.data.AbstractConnectionDescriptor;

/**
 * Holds the configuration details for a {@link ZooKeeperConnection}.
 * 
 * @author Mark Masse
 */
public final class ZooKeeperConnectionDescriptor extends AbstractConnectionDescriptor<ZooKeeperConnectionDescriptor> {

    public static final String DEFAULT_NAME = "My ZooKeeper";
    public static final int DEFAULT_SESSION_TIMEOUT = 5000;
    public static final int NAME_LENGTH_LIMIT = 100;

    /**
     * Assembles a ZooKeeper connection String from the server list and (optional) root path (the chroot).
     * 
     * @param servers The set of {@link ZooKeeperServerDescriptor} describing the ZooKeeper ensemble.
     * @param rootPath The optional chroot suffix.
     * @return The connect string is a comma separated host:port pairs, each corresponding to a ZooKeeper server. e.g.
     *         "127.0.0.1:3000,127.0.0.1:3001,127.0.0.1:3002" If the optional chroot suffix is used the example would
     *         look like: "127.0.0.1:3000,127.0.0.1:3001,127.0.0.1:3002/app/a" where the client would be rooted at
     *         "/app/a" and all paths would be relative to this root - ie getting/setting/etc... "/foo/bar" would result
     *         in operations being run on "/app/a/foo/bar" (from the server perspective).
     * 
     * @see ZooKeeper#ZooKeeper(String, int, org.apache.zookeeper.Watcher)
     */
    public static String buildConnectString(Set<ZooKeeperServerDescriptor> servers, String rootPath) {
        if (servers.size() == 0) {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        for (ZooKeeperServerDescriptor server : servers) {
            builder.append(server.getHostPortString()).append(',');
        }

        String connectString = builder.substring(0, builder.length() - 1);

        if (rootPath != null && !rootPath.equals(Znode.ROOT_PATH)) {
            if (!rootPath.startsWith(Znode.ROOT_PATH)) {
                rootPath = Znode.ROOT_PATH + rootPath;
            }
            connectString = connectString + rootPath;
        }

        return connectString;
    }

    private List<AuthInfo> _AuthInfos;
    private String _RootPath;
    private final Set<ZooKeeperServerDescriptor> _Servers;

    private int _SessionTimeout;

    /**
     * Constructor.
     * 
     * @param name The connection descriptor name.
     * @param sessionTimeout The connection's session timeout (in milliseconds)
     */
    public ZooKeeperConnectionDescriptor(String name, int sessionTimeout) {
        super(name);
        _SessionTimeout = sessionTimeout;
        _Servers = new TreeSet<ZooKeeperServerDescriptor>();
    }

    /**
     * Returns the {@link AuthInfo} {@link List}.
     * 
     * @return The {@link AuthInfo} {@link List}.
     */
    public List<AuthInfo> getAuthInfos() {
        if (_AuthInfos == null) {
            return Collections.emptyList();
        }

        return Collections.unmodifiableList(_AuthInfos);
    }

    /**
     * Assembles a ZooKeeper connection String from the server list and (optional) root path (the chroot).
     * 
     * @return The connect string is a comma separated host:port pairs, each corresponding to a ZooKeeper server. e.g.
     *         "127.0.0.1:3000,127.0.0.1:3001,127.0.0.1:3002" If the optional chroot suffix is used the example would
     *         look like: "127.0.0.1:3000,127.0.0.1:3001,127.0.0.1:3002/app/a" where the client would be rooted at
     *         "/app/a" and all paths would be relative to this root - ie getting/setting/etc... "/foo/bar" would result
     *         in operations being run on "/app/a/foo/bar" (from the server perspective).
     * 
     * @see ZooKeeper#ZooKeeper(String, int, org.apache.zookeeper.Watcher)
     * @see #buildConnectString(Set, String)
     */
    public String getConnectString() {
        return buildConnectString(_Servers, getRootPath());
    }

    /**
     * Returns the root path (chroot) or <code>null</code>.
     * 
     * @return The root path.
     */
    public String getRootPath() {
        return _RootPath;
    }

    /**
     * Returns the servers.
     * 
     * @return The servers
     */
    public Set<ZooKeeperServerDescriptor> getServers() {
        return _Servers;
    }

    /**
     * Returns the session timeout value.
     * 
     * @return The session timeout
     */
    public int getSessionTimeout() {
        return _SessionTimeout;
    }

    /**
     * Sets the {@link AuthInfo} {@link List}.
     * 
     * @param authInfos the {@link AuthInfo} {@link List} to set
     */
    public void setAuthInfos(List<AuthInfo> authInfos) {
        _AuthInfos = authInfos;
    }

    /**
     * Sets the root path (chroot) or <code>null</code>.
     * 
     * @param rootPath The root path (chroot) or <code>null</code>.
     */
    public void setRootPath(String rootPath) {
        _RootPath = rootPath;
    }

    /**
     * Sets the servers.
     * 
     * @param servers the servers to set
     */
    public final void setServers(Collection<ZooKeeperServerDescriptor> servers) {
        _Servers.clear();
        _Servers.addAll(servers);
    }

    /**
     * Sets the session timeout value.
     * 
     * @param sessionTimeout the new session timeout to set
     */
    public final void setSessionTimeout(int sessionTimeout) {
        _SessionTimeout = sessionTimeout;
    }

    @Override
    public String toString() {
        return "ZooKeeperConnectionDescriptor [Name=" + getName() + ", SessionTimeout=" + _SessionTimeout
                + ", RootPath=" + _RootPath + ", Servers=" + _Servers + "]";
    }

}
