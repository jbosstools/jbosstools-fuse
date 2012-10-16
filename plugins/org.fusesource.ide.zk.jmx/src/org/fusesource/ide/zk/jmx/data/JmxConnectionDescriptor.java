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

package org.fusesource.ide.zk.jmx.data;

import javax.management.remote.JMXServiceURL;

import org.fusesource.ide.zk.core.data.AbstractConnectionDescriptor;


/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public final class JmxConnectionDescriptor extends AbstractConnectionDescriptor<JmxConnectionDescriptor> {

    public static final int NAME_LENGTH_LIMIT = 100;    
    public static final String DEFAULT_NAME = "My JMX Connection";
    
    private JMXServiceURL _JmxServiceUrl;
    private String _Password;
    private String _UserName;

    /**
     * TODO: Comment.
     * 
     * @param name
     */
    public JmxConnectionDescriptor(String name, JMXServiceURL jmxServiceUrl, String userName, String password) {
        super(name);
        _JmxServiceUrl = jmxServiceUrl;
        _UserName = userName;
        _Password = password;
    }

    /**
     * Returns the jmxServiceUrl.
     * 
     * @return The jmxServiceUrl
     */
    public JMXServiceURL getJmxServiceUrl() {
        return _JmxServiceUrl;
    }

    /**
     * Returns the password.
     * 
     * @return The password
     */
    public String getPassword() {
        return _Password;
    }

    /**
     * Returns the userName.
     * 
     * @return The userName
     */
    public String getUserName() {
        return _UserName;
    }

    /**
     * Sets the jmxServiceUrl.
     * 
     * @param jmxServiceUrl the jmxServiceUrl to set
     */
    public final void setJmxServiceUrl(JMXServiceURL jmxServiceUrl) {
        _JmxServiceUrl = jmxServiceUrl;
    }

    /**
     * Sets the password.
     * 
     * @param password the password to set
     */
    public final void setPassword(String password) {
        _Password = password;
    }

    /**
     * Sets the userName.
     * 
     * @param userName the userName to set
     */
    public final void setUserName(String userName) {
        _UserName = userName;
    }

    @Override
    public String toString() {
        return "JmxConnectionDescriptor [Name=" + getName() + ", JmxServiceUrl=" + _JmxServiceUrl + "]";
    }

}
