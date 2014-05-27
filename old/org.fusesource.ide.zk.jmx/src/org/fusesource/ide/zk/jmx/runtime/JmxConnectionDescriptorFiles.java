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

package org.fusesource.ide.zk.jmx.runtime;

import org.eclipse.ui.XMLMemento;
import org.fusesource.ide.zk.jmx.data.JmxConnectionDescriptor;
import org.fusesource.ide.zk.core.runtime.ConnectionDescriptorFiles;
import org.fusesource.ide.zk.core.runtime.IConnectionDescriptorXmlSerializer;

import java.io.File;
import java.net.MalformedURLException;

import javax.management.remote.JMXServiceURL;


/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public final class JmxConnectionDescriptorFiles extends ConnectionDescriptorFiles<JmxConnectionDescriptor> {

    public static final String XML_VERSION_1 = "1";
    public static final String XML_WRITE_VERSION = XML_VERSION_1;

    private static final String XML_TAG_JMX_URL = "JmxUrl";
    private static final String XML_TAG_USER_NAME = "UserName";
    private static final String XML_TAG_PASSWORD = "Password";

    public JmxConnectionDescriptorFiles(File directory) {
        super(directory, XML_WRITE_VERSION);
        addSerializer(XML_VERSION_1, new Version1Serializer());
    }

    private class Version1Serializer implements IConnectionDescriptorXmlSerializer<JmxConnectionDescriptor> {

        @Override
        public JmxConnectionDescriptor fromXml(XMLMemento memento) {

            String name = memento.getString(XML_TAG_NAME);
            String jmxServiceUrlString = memento.getString(XML_TAG_JMX_URL);
            String userName = memento.getString(XML_TAG_USER_NAME);
            String password = memento.getString(XML_TAG_PASSWORD);

            JMXServiceURL jmxServiceUrl = null;
            try {
                jmxServiceUrl = new JMXServiceURL(jmxServiceUrlString);
            }
            catch (MalformedURLException e) {
                return null;
            }

            JmxConnectionDescriptor connectionDescriptor = new JmxConnectionDescriptor(name, jmxServiceUrl, userName,
                    password);
            return connectionDescriptor;
        }

        @Override
        public void toXml(JmxConnectionDescriptor connectionDescriptor, XMLMemento memento) {

            memento.putString(XML_TAG_JMX_URL, String.valueOf(connectionDescriptor.getJmxServiceUrl()));

            String userName = connectionDescriptor.getUserName();
            if (userName != null) {
                memento.putString(XML_TAG_USER_NAME, userName);
            }

            String password = connectionDescriptor.getPassword();
            if (password != null) {
                memento.putString(XML_TAG_PASSWORD, password);
            }

        }

    }

}
