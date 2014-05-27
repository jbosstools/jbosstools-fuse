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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.ObjectName;

/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public final class MBean {

    private Map<String, MBeanAttributeInfo> _AttributeInfoMap;
    private Set<String> _AttributeNames;
    private MBeanInfo _Info;
    private final ObjectName _ObjectName;
    private Map<String, MBeanOperationInfo> _OperationInfoMap;
    private Set<String> _OperationNames;

    /**
     * TODO: Comment.
     * 
     * @param objectName
     */
    MBean(ObjectName objectName) {
        _ObjectName = objectName;
    }

    /**
     * TODO: Comment.
     * 
     * @param attributeName
     * @return
     */
    public MBeanAttributeInfo getAttributeInfo(String attributeName) {
        if (_AttributeInfoMap == null) {
            initAttributeCollections();
        }
        return _AttributeInfoMap.get(attributeName);
    }

    /**
     * Returns the attributeNames.
     * 
     * @return The attributeNames.
     */
    public Set<String> getAttributeNames() {
        if (_AttributeNames == null) {
            initAttributeCollections();
        }
        return _AttributeNames;
    }

    /**
     * Returns the info.
     * 
     * @return The info.
     */
    public MBeanInfo getInfo() {
        return _Info;
    }

    /**
     * Returns the objectName.
     * 
     * @return The objectName
     */
    public ObjectName getObjectName() {
        return _ObjectName;
    }

    /**
     * TODO: Comment.
     * 
     * @param operationName
     * @return
     */
    public MBeanOperationInfo getOperationInfo(String operationName) {
        if (_OperationInfoMap == null) {
            initOperationCollections();
        }
        return _OperationInfoMap.get(operationName);
    }

    /**
     * Returns the operationNames.
     * 
     * @return The operationNames.
     */
    public Set<String> getOperationNames() {
        if (_OperationNames == null) {
            initOperationCollections();
        }
        return _OperationNames;
    }

    /**
     * Sets the info.
     * 
     * @param info The info to set.
     */
    public void setInfo(MBeanInfo info) {
        _Info = info;

        _AttributeNames = null;
        _AttributeInfoMap = null;
    }

    @Override
    public String toString() {
        return "MBean [" + (_ObjectName != null ? "ObjectName=" + _ObjectName : "") + "]";
    }

    /**
     * TODO: Comment.
     * 
     */
    private void initAttributeCollections() {

        MBeanAttributeInfo[] attributeInfos = _Info.getAttributes();
        int size = 0;
        if (attributeInfos != null) {
            size = attributeInfos.length;
        }
        _AttributeNames = new TreeSet<String>();
        _AttributeInfoMap = new HashMap<String, MBeanAttributeInfo>(size);

        if (size == 0) {
            return;
        }

        for (MBeanAttributeInfo attributeInfo : attributeInfos) {
            String attributeName = attributeInfo.getName();

            _AttributeNames.add(attributeName);
            _AttributeInfoMap.put(attributeName, attributeInfo);
        }

    }

    /**
     * TODO: Comment.
     * 
     */
    private void initOperationCollections() {

        MBeanOperationInfo[] operationInfos = _Info.getOperations();
        int size = 0;
        if (operationInfos != null) {
            size = operationInfos.length;
        }
        _OperationNames = new TreeSet<String>();
        _OperationInfoMap = new HashMap<String, MBeanOperationInfo>(size);

        if (size == 0) {
            return;
        }

        for (MBeanOperationInfo operationInfo : operationInfos) {
            String operationName = JmxUtils.getOperationName(operationInfo);
            _OperationNames.add(operationName);
            _OperationInfoMap.put(operationName, operationInfo);
        }
    }
}
