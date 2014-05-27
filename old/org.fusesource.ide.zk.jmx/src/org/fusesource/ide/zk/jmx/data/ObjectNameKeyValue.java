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
import java.util.List;

import javax.management.ObjectName;

/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public final class ObjectNameKeyValue implements IObjectNamePart {

    private final List<ObjectNameKeyValue> _ChildObjectNameKeyValues;
    private final String _KeyValuePairString;
    private final boolean _MBean;
    private final ObjectName _ObjectName;
    private final String _ObjectNameKey;
    private final IObjectNamePart _ParentObjectNamePart;

    /**
     * TODO: Comment.
     * 
     * @param objectName
     * @param mBean
     * @param objectNameKey
     * @param parentObjectNamePart
     */
    public ObjectNameKeyValue(ObjectName objectName, boolean isMBean, String objectNameKey,
            IObjectNamePart parentObjectNamePart) {

        _ObjectName = objectName;
        _MBean = isMBean;
        _ObjectNameKey = objectNameKey;
        _ParentObjectNamePart = parentObjectNamePart;
        _ChildObjectNameKeyValues = new ArrayList<ObjectNameKeyValue>();

        _KeyValuePairString = objectNameKey + "=" + getObjectNameValue();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.fusesource.ide.zk.zookeeper.data.jmx.ObjectNamePart#getChildObjectNameKeyValues()
     */
    @Override
    public List<ObjectNameKeyValue> getChildObjectNameKeyValues() {
        return _ChildObjectNameKeyValues;
    }

    /**
     * Returns the keyValuePairString.
     * 
     * @return The keyValuePairString
     */
    public String getKeyValuePairString() {
        return _KeyValuePairString;
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
     * Returns the objectName key.
     * 
     * @return The objectName key
     */
    public String getObjectNameKey() {
        return _ObjectNameKey;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.fusesource.ide.zk.zookeeper.data.jmx.ObjectNamePart#getObjectNamePartString()
     */
    @Override
    public String getObjectNamePartString() {
        return getObjectNameValue();
    }

    /**
     * Returns the objectName value.
     * 
     * @return The objectName value
     */
    public String getObjectNameValue() {
        return getObjectName().getKeyProperty(getObjectNameKey());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.fusesource.ide.zk.zookeeper.data.jmx.ObjectNamePart#getParentObjectNamePart()
     */
    @Override
    public IObjectNamePart getParentObjectNamePart() {
        return _ParentObjectNamePart;
    }

    /**
     * Returns the isMBean flag.
     * 
     * @return The isMBean flag.
     */
    public boolean isMBean() {
        return _MBean;
    }

    @Override
    public String toString() {
        return "ObjectNameKeyValue [" + (_ObjectName != null ? "ObjectName=" + _ObjectName + ", " : "")
                + (_ObjectNameKey != null ? "ObjectNameKey=" + _ObjectNameKey : "") + "]";
    }

}
