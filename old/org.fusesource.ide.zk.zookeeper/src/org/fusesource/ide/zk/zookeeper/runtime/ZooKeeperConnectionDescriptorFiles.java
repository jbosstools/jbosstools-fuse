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

package org.fusesource.ide.zk.zookeeper.runtime;

import org.eclipse.ui.IMemento;
import org.eclipse.ui.XMLMemento;
import org.fusesource.ide.zk.zookeeper.data.AuthInfo;
import org.fusesource.ide.zk.zookeeper.data.Znode;
import org.fusesource.ide.zk.zookeeper.data.ZooKeeperConnectionDescriptor;
import org.fusesource.ide.zk.zookeeper.data.ZooKeeperServerDescriptor;
import org.fusesource.ide.zk.zookeeper.data.AuthInfo.Type;
import org.fusesource.ide.zk.core.runtime.ConnectionDescriptorFiles;
import org.fusesource.ide.zk.core.runtime.IConnectionDescriptorXmlSerializer;
import org.fusesource.ide.zk.jmx.data.JmxConnectionDescriptor;
import org.fusesource.ide.zk.jmx.runtime.JmxConnectionDescriptorFiles;

import java.io.File;
import java.util.ArrayList;


/**
 * Provides management of {@link ZooKeeperConnectionDescriptor} files.
 * 
 * @author Mark Masse
 */
public final class ZooKeeperConnectionDescriptorFiles extends ConnectionDescriptorFiles<ZooKeeperConnectionDescriptor> {

    public static final String XML_VERSION_1 = "1";
    public static final String XML_WRITE_VERSION = XML_VERSION_1;

    private static final String XML_TAG_AUTH_INFO = "AuthInfo";
    private static final String XML_TAG_AUTH_INFO_AUTH = "Auth";
    private static final String XML_TAG_AUTH_INFO_SCHEME = "Scheme";
    private static final String XML_TAG_AUTH_INFO_TYPE = "Type";
    private static final String XML_TAG_AUTH_INFOS = "AuthInfos";
    private static final String XML_TAG_ROOT_PATH = "RootPath";
    private static final String XML_TAG_SESSION_TIMEOUT = "SessionTimeout";
    private static final String XML_TAG_ZOO_KEEPER_SERVER = "ZooKeeperServer";
    private static final String XML_TAG_ZOO_KEEPER_SERVER_HOST = "Host";
    private static final String XML_TAG_ZOO_KEEPER_SERVER_JMX_CONNECTION = "JmxConnection";
    private static final String XML_TAG_ZOO_KEEPER_SERVER_PORT = "Port";
    private static final String XML_TAG_ZOO_KEEPER_SERVERS = "ZooKeeperServers";

    private final JmxConnectionDescriptorFiles _JmxConnectionDescriptorFiles;

    public ZooKeeperConnectionDescriptorFiles(File directory) {
        super(directory, XML_WRITE_VERSION);
        addSerializer(XML_VERSION_1, new Version1Serializer());
        _JmxConnectionDescriptorFiles = new JmxConnectionDescriptorFiles(new File(directory, "JMX"));
    }

    @Override
    public boolean delete(ZooKeeperConnectionDescriptor connectionDescriptor) {
        boolean result = super.delete(connectionDescriptor);

        if (result) {
            for (ZooKeeperServerDescriptor serverDescriptor : connectionDescriptor.getServers()) {
                JmxConnectionDescriptor jmxConnectionDescriptor = serverDescriptor.getJmxConnectionDescriptor();
                if (jmxConnectionDescriptor != null) {
                    _JmxConnectionDescriptorFiles.delete(jmxConnectionDescriptor);
                }
            }
        }

        return result;
    }

    /**
     * Returns the jmxConnectionDescriptorFiles.
     * 
     * @return The jmxConnectionDescriptorFiles
     */
    public JmxConnectionDescriptorFiles getJmxConnectionDescriptorFiles() {
        return _JmxConnectionDescriptorFiles;
    }

    private class Version1Serializer implements IConnectionDescriptorXmlSerializer<ZooKeeperConnectionDescriptor> {

        @Override
        public ZooKeeperConnectionDescriptor fromXml(XMLMemento memento) {
            String name = memento.getString(XML_TAG_NAME);
            int sessionTimeout = Integer.parseInt(memento.getString(XML_TAG_SESSION_TIMEOUT));
            String rootPath = memento.getString(XML_TAG_ROOT_PATH);
            ZooKeeperConnectionDescriptor connectionDescriptor = new ZooKeeperConnectionDescriptor(name, sessionTimeout);
            if (rootPath != null) {
                rootPath = rootPath.trim();
                if (rootPath.length() > 0 && !rootPath.equals(Znode.ROOT_PATH)) {
                    connectionDescriptor.setRootPath(rootPath);
                }
            }

            IMemento[] serverMementos = memento.getChild(XML_TAG_ZOO_KEEPER_SERVERS).getChildren(
                    XML_TAG_ZOO_KEEPER_SERVER);
            for (IMemento serverMemento : serverMementos) {
                String host = serverMemento.getString(XML_TAG_ZOO_KEEPER_SERVER_HOST);
                int port = Integer.parseInt(serverMemento.getString(XML_TAG_ZOO_KEEPER_SERVER_PORT));
                ZooKeeperServerDescriptor severDescriptor = new ZooKeeperServerDescriptor(host, port);

                String jmxConnectionDescriptorName = serverMemento.getString(XML_TAG_ZOO_KEEPER_SERVER_JMX_CONNECTION);
                if (jmxConnectionDescriptorName != null) {
                    JmxConnectionDescriptor jmxConnectionDescriptor = _JmxConnectionDescriptorFiles
                            .load(jmxConnectionDescriptorName);
                    severDescriptor.setJmxConnectionDescriptor(jmxConnectionDescriptor);
                }

                connectionDescriptor.getServers().add(severDescriptor);
            }

            IMemento authInfosMemento = memento.getChild(XML_TAG_AUTH_INFOS);
            if (authInfosMemento != null) {

                IMemento[] authInfoMementos = authInfosMemento.getChildren(XML_TAG_AUTH_INFO);

                ArrayList<AuthInfo> authInfos = new ArrayList<AuthInfo>(authInfoMementos.length);
                for (IMemento authInfoMemento : authInfoMementos) {

                    String typeName = authInfoMemento.getString(XML_TAG_AUTH_INFO_TYPE);
                    String scheme = authInfoMemento.getString(XML_TAG_AUTH_INFO_SCHEME);
                    String authString = authInfoMemento.getString(XML_TAG_AUTH_INFO_AUTH);

                    AuthInfo authInfo = new AuthInfo(Type.valueOf(typeName), scheme, authString);
                    authInfos.add(authInfo);
                }

                if (authInfos.size() > 0) {
                    connectionDescriptor.setAuthInfos(authInfos);
                }
            }

            return connectionDescriptor;
        }

        @Override
        public void toXml(ZooKeeperConnectionDescriptor connectionDescriptor, XMLMemento memento) {
            memento.putString(XML_TAG_SESSION_TIMEOUT, String.valueOf(connectionDescriptor.getSessionTimeout()));
            String rootPath = connectionDescriptor.getRootPath();
            if (rootPath != null) {
                memento.putString(XML_TAG_ROOT_PATH, rootPath);
            }

            IMemento serversMemento = memento.createChild(XML_TAG_ZOO_KEEPER_SERVERS);
            for (ZooKeeperServerDescriptor serverDescriptor : connectionDescriptor.getServers()) {
                IMemento serverMemento = serversMemento.createChild(XML_TAG_ZOO_KEEPER_SERVER);
                serverMemento.putString(XML_TAG_ZOO_KEEPER_SERVER_HOST, serverDescriptor.getHost());
                serverMemento.putString(XML_TAG_ZOO_KEEPER_SERVER_PORT, String.valueOf(serverDescriptor.getPort()));

                JmxConnectionDescriptor jmxConnectionDescriptor = serverDescriptor.getJmxConnectionDescriptor();
                if (jmxConnectionDescriptor != null) {
                    serverMemento
                            .putString(XML_TAG_ZOO_KEEPER_SERVER_JMX_CONNECTION, jmxConnectionDescriptor.getName());
                    _JmxConnectionDescriptorFiles.save(jmxConnectionDescriptor);
                }

            }

            IMemento authInfosMemento = memento.createChild(XML_TAG_AUTH_INFOS);
            for (AuthInfo authInfo : connectionDescriptor.getAuthInfos()) {
                IMemento authInfoMemento = authInfosMemento.createChild(XML_TAG_AUTH_INFO);
                authInfoMemento.putString(XML_TAG_AUTH_INFO_TYPE, authInfo.getType().name());
                authInfoMemento.putString(XML_TAG_AUTH_INFO_SCHEME, authInfo.getScheme());
                authInfoMemento.putString(XML_TAG_AUTH_INFO_AUTH, authInfo.getAuthString());
            }

        }

    }

}
