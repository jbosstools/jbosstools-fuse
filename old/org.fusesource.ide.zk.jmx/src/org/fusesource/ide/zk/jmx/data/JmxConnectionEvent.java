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

import java.util.EventObject;

import javax.management.ObjectName;
import javax.management.remote.JMXConnectionNotification;

/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
@SuppressWarnings("serial")
public final class JmxConnectionEvent extends EventObject {

    private final JMXConnectionNotification _Notification;
    private final ObjectName _ObjectName;

    public JmxConnectionEvent(JmxConnection jmxConnection, JMXConnectionNotification notification) {
        this(jmxConnection, null, notification);
    }

    
    public JmxConnectionEvent(JmxConnection jmxConnection, ObjectName objectName) {
        this(jmxConnection, objectName, null);
    }

    private JmxConnectionEvent(JmxConnection jmxConnection, ObjectName objectName,
            JMXConnectionNotification notification) {
        super(jmxConnection);
        _ObjectName = objectName;
        _Notification = notification;
    }

    /**
     * TODO: Comment.
     * 
     * @return
     */
    public final JmxConnection getJmxConnection() {
        return (JmxConnection) getSource();
    }

    /**
     * Returns the notification.
     * 
     * @return The notification
     */
    public final JMXConnectionNotification getNotification() {
        return _Notification;
    }

    /**
     * Returns the objectName.
     * 
     * @return The objectName
     */
    public final ObjectName getObjectName() {
        return _ObjectName;
    }

}
