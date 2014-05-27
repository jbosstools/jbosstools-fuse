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

import org.fusesource.ide.zk.jmx.data.JmxConnectionDescriptor;

/**
 * A descriptor for a single ZooKeeper server (host, port, and JMX connection details).
 * 
 * @author Mark Masse
 */
public final class ZooKeeperServerDescriptor implements Comparable<ZooKeeperServerDescriptor> {

    public static final String DEFAULT_HOST = "localhost";
    public static final int DEFAULT_JMX_PORT = 2182;
    public static final int DEFAULT_PORT = 2181;
    public static final String ZOOKEEPER_JMX_DOMAIN_NAME = "org.apache.ZooKeeperService";

    private final String _Host;
    private final String _HostPortString;
    private JmxConnectionDescriptor _JmxConnectionDescriptor;
    private final int _Port;

    public ZooKeeperServerDescriptor(String host, int port) {
        _Host = host;
        _Port = port;
        _HostPortString = _Host + ":" + _Port;
    }

    @Override
    public int compareTo(ZooKeeperServerDescriptor o) {
        return toString().compareTo(o.toString());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ZooKeeperServerDescriptor other = (ZooKeeperServerDescriptor) obj;
        if (_Host == null) {
            if (other._Host != null)
                return false;
        }
        else if (!_Host.equals(other._Host))
            return false;
        if (_JmxConnectionDescriptor == null) {
            if (other._JmxConnectionDescriptor != null)
                return false;
        }
        else if (!_JmxConnectionDescriptor.equals(other._JmxConnectionDescriptor))
            return false;
        if (_Port != other._Port)
            return false;
        return true;
    }

    /**
     * Returns the host.
     * 
     * @return The host.
     */
    public String getHost() {
        return _Host;
    }

    /**
     * Returns the host:port string.
     * 
     * @return The host:port string.
     */
    public String getHostPortString() {
        return _HostPortString;
    }

    /**
     * Returns the jmxConnectionDescriptor.
     * 
     * @return The jmxConnectionDescriptor
     */
    public JmxConnectionDescriptor getJmxConnectionDescriptor() {
        return _JmxConnectionDescriptor;
    }

    /**
     * Returns the port.
     * 
     * @return The port.
     */
    public int getPort() {
        return _Port;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_Host == null) ? 0 : _Host.hashCode());
        result = prime * result + ((_JmxConnectionDescriptor == null) ? 0 : _JmxConnectionDescriptor.hashCode());
        result = prime * result + _Port;
        return result;
    }

    /**
     * Sets the jmxConnectionDescriptor.
     * 
     * @param jmxConnectionDescriptor the jmxConnectionDescriptor to set
     */
    public void setJmxConnectionDescriptor(JmxConnectionDescriptor jmxConnectionDescriptor) {
        _JmxConnectionDescriptor = jmxConnectionDescriptor;
    }

    @Override
    public String toString() {
        return "ZooKeeperServerDescriptor [Host=" + _Host + ", Port=" + _Port + ", JmxConnectionDescriptor="
                + _JmxConnectionDescriptor + "]";
    }

}
