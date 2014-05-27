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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.ObjectName;

/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public final class Domain implements IObjectNamePart {

    private List<ObjectNameKeyValue> _ChildObjectNameKeyValues;
    private final ObjectName _DomainPatternObjectName;
    private Map<ObjectName, ObjectNameKeyValue> _MBeanObjectNameKeyValueMap;
    private Set<ObjectName> _MBeanObjectNames;
    private final String _Name;
    private Map<String, ObjectNameKeyValue> _ObjectNameKeyValueMap;

    /**
     * TODO: Comment.
     * 
     * @param name
     * @param domainPatternObjectName
     */
    Domain(String name, ObjectName domainPatternObjectName) {
        _Name = name;
        _DomainPatternObjectName = domainPatternObjectName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.fusesource.ide.zk.zookeeper.data.jmx.copy.ObjectNamePart#getChildObjectNameKeyValues()
     */
    @Override
    public List<ObjectNameKeyValue> getChildObjectNameKeyValues() {
        if (_ChildObjectNameKeyValues == null) {
            initChildObjectNameParts();
        }
        return _ChildObjectNameKeyValues;
    }

    /**
     * Returns the domainPatternObjectName.
     * 
     * @return The domainPatternObjectName
     */
    public ObjectName getDomainPatternObjectName() {
        return _DomainPatternObjectName;
    }

    /**
     * TODO: Comment.
     * 
     * @param mbeanObjectName
     * @return
     */
    public ObjectNameKeyValue getMBeanObjectNameKeyValue(ObjectName mbeanObjectName) {
        if (_MBeanObjectNameKeyValueMap == null) {
            initChildObjectNameParts();
        }
        return _MBeanObjectNameKeyValueMap.get(mbeanObjectName);
    }

    /**
     * Returns the mBeanObjectNames.
     * 
     * @return The mBeanObjectNames
     */
    public Set<ObjectName> getMBeanObjectNames() {
        return _MBeanObjectNames;
    }

    /**
     * Returns the name.
     * 
     * @return The name
     */
    public String getName() {
        return _Name;
    }

    /**
     * TODO: Comment.
     * 
     * @param keyValuePairString
     * @return
     */
    public ObjectNameKeyValue getObjectNameKeyValue(String keyValuePairString) {
        if (_ObjectNameKeyValueMap == null) {
            initChildObjectNameParts();
        }
        return _ObjectNameKeyValueMap.get(keyValuePairString);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.fusesource.ide.zk.zookeeper.data.jmx.copy.ObjectNamePart#getObjectNamePartString()
     */
    @Override
    public String getObjectNamePartString() {
        return getName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.fusesource.ide.zk.zookeeper.data.jmx.copy.ObjectNamePart#getParentObjectNamePart()
     */
    @Override
    public IObjectNamePart getParentObjectNamePart() {
        // Domain is the root (prefix) of the ObjectName and thus it has no parent part
        return null;
    }

    /**
     * Sets the the mBeanObjectNames.
     * 
     * @param mBeanObjectNames
     *            the mBeanObjectNames to set
     */
    public void setMBeanObjectNames(Set<ObjectName> mBeanObjectNames) {
        _MBeanObjectNames = mBeanObjectNames;
        _ChildObjectNameKeyValues = null;
        _ObjectNameKeyValueMap = null;
        _MBeanObjectNameKeyValueMap = null;
    }

    @Override
    public String toString() {
        return "Domain [" + (_Name != null ? "Name=" + _Name : "") + "]";
    }

    /**
     * TODO: Comment.
     * 
     */
    private void initChildObjectNameParts() {

        _ChildObjectNameKeyValues = new ArrayList<ObjectNameKeyValue>();
        _ObjectNameKeyValueMap = new HashMap<String, ObjectNameKeyValue>();
        _MBeanObjectNameKeyValueMap = new HashMap<ObjectName, ObjectNameKeyValue>();

        if (_MBeanObjectNames == null) {
            return;
        }

        for (ObjectName objectName : _MBeanObjectNames) {

            ObjectNameKeyValue parentObjectNameKeyValue = null;
            String keyPropertyListString = objectName.getKeyPropertyListString();
            String[] keyValuePairStrings = keyPropertyListString.split(",");
            int keyValuePairCount = keyValuePairStrings.length;
            for (int i = 0; i < keyValuePairCount; i++) {

                String keyValuePairString = keyValuePairStrings[i];

                String[] keyValuePair = keyValuePairString.split("=");
                String key = keyValuePair[0];

                if (_ObjectNameKeyValueMap.containsKey(keyValuePairString)) {
                    parentObjectNameKeyValue = _ObjectNameKeyValueMap.get(keyValuePairString);
                    continue;
                }

                boolean isMBean = false;
                if (i == keyValuePairCount - 1) {
                    isMBean = true;
                }

                IObjectNamePart parentObjectNamePart = this;
                List<ObjectNameKeyValue> childParts = _ChildObjectNameKeyValues;
                if (parentObjectNameKeyValue != null) {
                    parentObjectNamePart = parentObjectNameKeyValue;
                    childParts = parentObjectNameKeyValue.getChildObjectNameKeyValues();
                }

                ObjectNameKeyValue objectNameKeyValue = new ObjectNameKeyValue(objectName, isMBean, key,
                        parentObjectNamePart);
                childParts.add(objectNameKeyValue);

                _ObjectNameKeyValueMap.put(keyValuePairString, objectNameKeyValue);
                if (isMBean) {
                    _MBeanObjectNameKeyValueMap.put(objectName, objectNameKeyValue);
                }
                parentObjectNameKeyValue = objectNameKeyValue;

            }

        }

    }

}
