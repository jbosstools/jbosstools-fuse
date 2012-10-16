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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerDelegate;
import javax.management.MBeanServerNotification;
import javax.management.Notification;
import javax.management.NotificationFilterSupport;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.QueryExp;
import javax.management.remote.JMXConnectionNotification;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public class JmxConnection {

    private final JmxConnectionDescriptor _Descriptor;
    private CopyOnWriteArrayList<IJmxConnectionEventListener> _EventListeners;
    private JMXConnector _JMXConnector;
    private MBeanServerConnection _MBeanServerConnection;
    private MBeanServerDelegateNotificationListener _MBeanServerDelegateNotificationListener;
    private ConnectionNotificationListener _ConnectionNotificationListener;
    private boolean _Connected;

    /**
     * TODO: Comment.
     * 
     * @param descriptor
     */
    public JmxConnection(JmxConnectionDescriptor descriptor) {
        _Descriptor = descriptor;
    }

    /**
     * TODO: Comment.
     * 
     * @param listener
     * @return
     */
    public void addEventListener(IJmxConnectionEventListener listener) {

        if (_EventListeners == null) {
            _EventListeners = new CopyOnWriteArrayList<IJmxConnectionEventListener>();
        }

        if (!_EventListeners.contains(listener)) {
            _EventListeners.add(listener);
        }
    }

    public void close() {
        if (_JMXConnector == null) {
            return;
        }
        
        try {
            _JMXConnector.close();            
        }
        catch (IOException e) {        
        }
        
        _Connected = false;

        removeConnectionListener();        
        removeMBeanServerDelegateListener();
        
        _JMXConnector = null;
        _MBeanServerConnection = null;
        
        fireConnectionStateChanged(null);
        
    }

    public void connect() {

        close();

        // JMXServiceURL jmxUrl = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + _HostPortString + "/jmxrmi");

        JmxConnectionDescriptor descriptor = getDescriptor();
        JMXServiceURL jmxUrl = descriptor.getJmxServiceUrl();

        String userName = descriptor.getUserName();
        String password = descriptor.getPassword();

        Map<String, String[]> env = null;

        if (userName != null) {
            env = new HashMap<String, String[]>();
            env.put(JMXConnector.CREDENTIALS, new String[] { userName, password });
        }

        try {
            _JMXConnector = JMXConnectorFactory.connect(jmxUrl, env);
            _MBeanServerConnection = _JMXConnector.getMBeanServerConnection();
            
            _Connected = true;

            addConnectionListener();
            addMBeanServerDelegateListener();

            // System.out.println("JMX connection opened");
            fireConnectionStateChanged(null);
        }
        catch (IOException e) {
        }

    }

    public Domain getDefaultDomain() {
        if (!isConnected()) {
            return null;
        }

        String defaultDomainName = getDefaultDomainName();
        return getDomain(defaultDomainName);
    }

    public String getDefaultDomainName() {

        if (!isConnected()) {
            return null;
        }

        String defaultDomainName = null;

        try {
            defaultDomainName = _MBeanServerConnection.getDefaultDomain();
        }
        catch (IOException e) {
        }

        return defaultDomainName;
    }

    /**
     * Returns the descriptor.
     * 
     * @return The descriptor
     */
    public JmxConnectionDescriptor getDescriptor() {
        return _Descriptor;
    }

    /**
     * TODO: Comment.
     * 
     * @param domainName
     * @return
     */
    public Domain getDomain(String domainName) {

        if (domainName == null) {
            throw new IllegalArgumentException("null domain name");
        }

        ObjectName domainPatternObjectName = null;
        try {
            domainPatternObjectName = new ObjectName(domainName + ":*");
        }
        catch (Throwable t) {
            return null;
        }

        if (domainPatternObjectName == null) {
            return null;
        }

        Set<ObjectName> mbeanObjectNames = getObjectNames(domainPatternObjectName);
        Domain domain = new Domain(domainName, domainPatternObjectName);
        domain.setMBeanObjectNames(mbeanObjectNames);
        return domain;
    }

    public Set<String> getDomainNames() {

        if (!isConnected()) {
            return Collections.emptySet();
        }

        String[] domainNames = null;
        try {
            domainNames = _MBeanServerConnection.getDomains();
        }
        catch (IOException e) {
            return null;
        }

        // Arrays.sort(domainNames);
        return new TreeSet<String>(Arrays.asList(domainNames));
    }

    public List<Domain> getDomains() {
        Set<String> domainNames = getDomainNames();
        if (domainNames == null) {
            return null;
        }

        List<Domain> domainList = new ArrayList<Domain>(domainNames.size());
        for (String domainName : domainNames) {
            Domain domain = getDomain(domainName);
            if (domain != null) {
                domainList.add(domain);
            }
        }

        return domainList;
    }

    /**
     * Returns the jMXConnector.
     * 
     * @return The jMXConnector
     */
    public JMXConnector getJMXConnector() {
        return _JMXConnector;
    }

    /**
     * TODO: Comment.
     * 
     * @param objectName
     * @return
     */
    public MBean getMBean(ObjectName objectName) {

        if (objectName == null) {
            throw new IllegalArgumentException("null ObjectName");
        }

        List<MBean> mbeans = getMBeans(objectName);
        if (mbeans.size() == 0) {
            return null;
        }
        if (mbeans.size() > 1) {
            throw new IllegalArgumentException("ObjectName: " + objectName + " matched more than one MBean");
        }

        return mbeans.get(0);
    }

    public MBeanAttribute getMBeanAttribute(MBean mbean, String attributeName) {

        if (mbean == null) {
            throw new IllegalArgumentException("null MBean");
        }

        if (attributeName == null) {
            throw new IllegalArgumentException("null attributeName");
        }

        // TODO: Handle connection errors and try to reconnect.

        MBeanAttributeInfo attributeInfo = mbean.getAttributeInfo(attributeName);
        MBeanAttribute mbeanAttribute = new MBeanAttribute(attributeName);
        mbeanAttribute.setInfo(attributeInfo);

        Object value = null;

        try {
            value = _MBeanServerConnection.getAttribute(mbean.getObjectName(), attributeName);
        }
        catch (Throwable t) {
            String errorMessage = t.getLocalizedMessage();
            mbeanAttribute.setValueRetrievalErrorMessage(errorMessage);
        }

        mbeanAttribute.setValue(value);
        return mbeanAttribute;
    }

    public List<MBeanAttribute> getMBeanAttributes(MBean mbean) {

        if (mbean == null) {
            throw new IllegalArgumentException("null MBean");
        }

        if (!isConnected()) {
            return Collections.emptyList();
        }

        MBeanInfo mbeanInfo = mbean.getInfo();
        MBeanAttributeInfo[] attributeInfos = mbeanInfo.getAttributes();
        if (attributeInfos == null) {
            return null;
        }

        String[] attributeNames = new String[attributeInfos.length];

        Map<String, MBeanAttribute> mbeanAttributeMap = new HashMap<String, MBeanAttribute>(attributeInfos.length);

        for (int i = 0; i < attributeInfos.length; i++) {
            MBeanAttributeInfo attributeInfo = attributeInfos[i];
            String attributeName = attributeInfo.getName();
            MBeanAttribute mbeanAttribute = new MBeanAttribute(attributeName);
            mbeanAttribute.setInfo(attributeInfo);
            mbeanAttributeMap.put(attributeName, mbeanAttribute);
            attributeNames[i] = attributeName;
        }

        AttributeList attributes = null;
        try {
            attributes = _MBeanServerConnection.getAttributes(mbean.getObjectName(), attributeNames);
        }
        catch (Exception e) {
            return null;
        }

        if (attributes == null) {
            return null;
        }

        List<Attribute> attributeList = attributes.asList();
        List<MBeanAttribute> mbeanAttributeList = new ArrayList<MBeanAttribute>(attributeList.size());

        for (Attribute attribute : attributeList) {
            String attributeName = attribute.getName();
            MBeanAttribute mbeanAttribute = mbeanAttributeMap.get(attributeName);
            if (mbeanAttribute != null) {
                mbeanAttribute.setValue(attribute.getValue());
                mbeanAttributeList.add(mbeanAttribute);
            }
        }

        return mbeanAttributeList;
    }

    /**
     * TODO: Comment.
     * 
     * @param mbean
     * @param operationName
     * @return
     */
    public MBeanOperation getMBeanOperation(MBean mbean, String operationName) {

        if (mbean == null) {
            throw new IllegalArgumentException("null MBean");
        }

        if (operationName == null) {
            throw new IllegalArgumentException("null operationName");
        }

        MBeanOperationInfo operationInfo = mbean.getOperationInfo(operationName);
        MBeanOperation mbeanOperation = new MBeanOperation(operationName);
        mbeanOperation.setInfo(operationInfo);

        return mbeanOperation;
    }

    /**
     * TODO: Comment.
     * 
     * @param mbean
     * @return
     */
    public List<MBeanOperation> getMBeanOperations(MBean mbean) {

        if (mbean == null) {
            throw new IllegalArgumentException("null MBean");
        }

        Set<String> operationNames = mbean.getOperationNames();
        List<MBeanOperation> mbeanOperations = new ArrayList<MBeanOperation>(operationNames.size());
        for (String operationName : operationNames) {
            MBeanOperation mbeanOperation = getMBeanOperation(mbean, operationName);
            mbeanOperations.add(mbeanOperation);
        }

        return mbeanOperations;
    }

    /**
     * TODO: Comment.
     * 
     * @return
     */
    public List<MBean> getMBeans() {
        return getMBeans(null, null);
    }

    /**
     * TODO: Comment.
     * 
     * @param objectNamePattern
     * @return
     */
    public List<MBean> getMBeans(ObjectName objectNamePattern) {
        return getMBeans(objectNamePattern, null);
    }

    /**
     * TODO: Comment.
     * 
     * @param objectNamePattern
     * @param queryExp
     * @return
     */
    public List<MBean> getMBeans(ObjectName objectNamePattern, QueryExp queryExp) {
        Set<ObjectName> objectNames = getObjectNames(objectNamePattern, queryExp);
        return getMBeans(objectNames);
    }

    /**
     * TODO: Comment.
     * 
     * @param objectNamePattern
     * @param queryExp
     * @return
     */
    public List<MBean> getMBeans(Set<ObjectName> objectNames) {

        if (objectNames == null) {
            return null;
        }

        if (!isConnected()) {
            return Collections.emptyList();
        }

        List<MBean> mbeanList = new ArrayList<MBean>(objectNames.size());

        for (ObjectName objectName : objectNames) {

            MBeanInfo mbeanInfo = null;
            try {
                mbeanInfo = _MBeanServerConnection.getMBeanInfo(objectName);
            }
            catch (Exception e) {
            }

            if (mbeanInfo == null) {
                continue;
            }

            MBean mbean = new MBean(objectName);
            mbean.setInfo(mbeanInfo);
            mbeanList.add(mbean);
        }

        return mbeanList;
    }

    /**
     * Returns the mBeanServerConnection.
     * 
     * @return The mBeanServerConnection
     */
    public MBeanServerConnection getMBeanServerConnection() {
        return _MBeanServerConnection;
    }

    public Set<ObjectName> getObjectNames() {
        return getObjectNames(null);
    }

    public Set<ObjectName> getObjectNames(ObjectName objectNamePattern) {
        return getObjectNames(objectNamePattern, null);
    }

    public Set<ObjectName> getObjectNames(ObjectName objectNamePattern, QueryExp queryExp) {

        if (!isConnected()) {
            return Collections.emptySet();
        }

        Set<ObjectName> objectNameSet = null;
        try {
            objectNameSet = _MBeanServerConnection.queryNames(objectNamePattern, queryExp);
        }
        catch (Exception e) {
            return null;
        }

        return new TreeSet<ObjectName>(objectNameSet);
    }

    /**
     * TODO: Comment.
     * 
     * @param mbean
     * @param mbeanOperation
     * @param params
     * @return
     */
    public Object invokeMBeanOperation(MBean mbean, MBeanOperation mbeanOperation, Object[] params) {

        if (mbean == null) {
            throw new IllegalArgumentException("null MBean");
        }

        if (mbeanOperation == null) {
            throw new IllegalArgumentException("null operation");
        }

        String[] signature = null;
        if (params != null) {
            signature = mbeanOperation.getInvocationSignature();
        }

        if (!isConnected()) {
            return null;
        }

        Object result = null;
        try {
            result = _MBeanServerConnection.invoke(mbean.getObjectName(), mbeanOperation.getName(), params, signature);
        }
        catch (Throwable t) {
        }

        return result;
    }

    public boolean isConnected() {
        return _Connected;
    }

    /**
     * TODO: Comment.
     * 
     * @param listener
     * @return
     */
    public void removeEventListener(IJmxConnectionEventListener listener) {

        if (_EventListeners == null) {
            return;
        }

        if (!_EventListeners.contains(listener)) {
            return;
        }

        _EventListeners.remove(listener);
    }

    @Override
    public String toString() {
        return "JmxConnection [" + (_Descriptor != null ? "Descriptor=" + _Descriptor : "") + "]";
    }

    private void addConnectionListener() {
        
        _ConnectionNotificationListener = new ConnectionNotificationListener();
        _JMXConnector.addConnectionNotificationListener(_ConnectionNotificationListener, null, this);   
    }
    
    private void addMBeanServerDelegateListener() {
      
        _MBeanServerDelegateNotificationListener = new MBeanServerDelegateNotificationListener();
        NotificationFilterSupport filter = new NotificationFilterSupport();
        filter.enableType(MBeanServerNotification.REGISTRATION_NOTIFICATION);
        filter.enableType(MBeanServerNotification.UNREGISTRATION_NOTIFICATION);

        try {
            _MBeanServerConnection.addNotificationListener(MBeanServerDelegate.DELEGATE_NAME,
                    _MBeanServerDelegateNotificationListener, filter, null);
        }
        catch (Exception e) {
        }

    }

    /**
     * TODO: Comment.
     * 
     * @param objectName
     */
    private void fireMBeanRegistered(ObjectName objectName) {
        if (_EventListeners != null) {
            JmxConnectionEvent event = new JmxConnectionEvent(this, objectName);
            for (IJmxConnectionEventListener listener : _EventListeners) {
                listener.mbeanRegistered(event);
            }
        }

        // System.out.print(this + " MBean registered - " + objectName);
    }

    /**
     * TODO: Comment.
     * 
     * @param objectName
     */
    private void fireMBeanUnregistered(ObjectName objectName) {
        if (_EventListeners != null) {
            JmxConnectionEvent event = new JmxConnectionEvent(this, objectName);
            for (IJmxConnectionEventListener listener : _EventListeners) {
                listener.mbeanUnregistered(event);
            }
        }
    }

    private void fireConnectionStateChanged(JMXConnectionNotification notification) {

        if (_EventListeners != null) {

            JmxConnectionEvent event = new JmxConnectionEvent(this, notification);
            for (IJmxConnectionEventListener listener : _EventListeners) {
                listener.connectionStateChanged(event);
            }
        }
    }


    private void removeConnectionListener() {

        if (_JMXConnector == null || _ConnectionNotificationListener == null) {
            return;
        }
        
        try {
            _JMXConnector.removeConnectionNotificationListener(_ConnectionNotificationListener);
        }
        catch (Exception e) {
        }
    }

    private void removeMBeanServerDelegateListener() {

        if (_MBeanServerConnection == null || _MBeanServerDelegateNotificationListener == null) {
            return;
        }
        
        try {
            _MBeanServerConnection.removeNotificationListener(MBeanServerDelegate.DELEGATE_NAME,
                    _MBeanServerDelegateNotificationListener);
        }
        catch (Exception e) {
        }

    }

    private class MBeanServerDelegateNotificationListener implements NotificationListener {

        @Override
        public void handleNotification(Notification notification, Object handback) {
            if (notification instanceof MBeanServerNotification) {
                MBeanServerNotification mbeanNotification = (MBeanServerNotification) notification;
                String notificationType = mbeanNotification.getType();
                ObjectName objectName = mbeanNotification.getMBeanName();

                if (notificationType.equals(MBeanServerNotification.REGISTRATION_NOTIFICATION)) {
                    fireMBeanRegistered(objectName);
                }
                else if (notificationType.equals(MBeanServerNotification.UNREGISTRATION_NOTIFICATION)) {
                    fireMBeanUnregistered(objectName);
                }
            }
        }
    }

    private class ConnectionNotificationListener implements NotificationListener {

        @Override
        public void handleNotification(Notification notification, Object handback) {

            if (handback != JmxConnection.this || !(notification instanceof JMXConnectionNotification)) {
                return;
            }

            JMXConnectionNotification jmxConnectionNotification = (JMXConnectionNotification) notification;

            _Connected = JMXConnectionNotification.OPENED.equals(jmxConnectionNotification.getType());

            fireConnectionStateChanged(jmxConnectionNotification);
        }

    }

}
