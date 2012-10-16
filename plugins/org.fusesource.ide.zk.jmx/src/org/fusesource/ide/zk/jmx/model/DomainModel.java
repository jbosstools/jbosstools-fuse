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

package org.fusesource.ide.zk.jmx.model;


import java.util.List;
import java.util.Set;

import javax.management.ObjectName;

import org.fusesource.ide.zk.jmx.data.Domain;
import org.fusesource.ide.zk.jmx.data.MBean;
import org.fusesource.ide.zk.jmx.data.ObjectNameKeyValue;
import org.fusesource.ide.zk.core.model.DataModel;
import org.fusesource.ide.zk.core.model.DataModelManager;

/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public final class DomainModel extends AbstractJmxModel<DomainModel, String, Domain> {

    private DataModelManager<MBeanModel, ObjectName, MBean> _MBeanModelManager;
    
    private DataModelManager<ObjectNameKeyValueModel, String, ObjectNameKeyValue> _ObjectNameKeyValueModelManager;

    /**
     * TODO: Comment.
     * 
     * @param domainName
     * @param jmxConnectionModel
     */
    DomainModel(String domainName, JmxConnectionModel jmxConnectionModel) {
        super(domainName, jmxConnectionModel);
    }

    /**
     * TODO: Comment.
     * 
     * @return
     */
    public List<MBeanModel> getAllMBeanModels() {
        if (isDestroyed()) {
            return null;
        }

        return _MBeanModelManager.getModels();
    }

    /**
     * TODO: Comment.
     * 
     * @return
     */
    public Set<ObjectName> getAllMBeanObjectNames() {
        if (isDestroyed()) {
            return null;
        }

        return _MBeanModelManager.getKeys();
    }

    /**
     * TODO: Comment.
     * 
     * @param objectName
     * @return
     */
    public MBeanModel getMBeanModel(ObjectName objectName) {
        if (isDestroyed()) {
            return null;
        }

        return _MBeanModelManager.getModel(objectName);
    }

    /**
     * Returns the mBeanModelManager.
     * 
     * @return The mBeanModelManager
     */
    public final DataModelManager<MBeanModel, ObjectName, MBean> getMBeanModelManager() {
        return _MBeanModelManager;
    }

    /**
     * TODO: Comment.
     * 
     * @param objectNameKeyValue
     * @return
     */
    public List<MBeanModel> getMBeanModels(ObjectNameKeyValue objectNameKeyValue) {
        if (isDestroyed()) {
            return null;
        }

        return _MBeanModelManager.findModels(objectNameKeyValue);
    }

    /**
     * TODO: Comment.
     * 
     * @param objectNameKeyValue
     * @return
     */
    public Set<ObjectName> getMBeanObjectNames(ObjectNameKeyValue objectNameKeyValue) {
        if (isDestroyed()) {
            return null;
        }

        return _MBeanModelManager.findKeys(objectNameKeyValue);
    }

    /**
     * TODO: Comment.
     * 
     * @param keyValuePairString
     * @return
     */
    public ObjectNameKeyValueModel getObjectNameKeyValueModel(String keyValuePairString) {
        if (isDestroyed()) {
            return null;
        }

        return _ObjectNameKeyValueModelManager.getModel(keyValuePairString);
    }

    /**
     * TODO: Comment.
     * 
     * @return
     */
    public List<ObjectNameKeyValueModel> getObjectNameKeyValueModels() {
        if (isDestroyed()) {
            return null;
        }

        return _ObjectNameKeyValueModelManager.findModels(getData());
    }

    /**
     * TODO: Comment.
     * 
     * @param objectNameKeyValue
     * @return
     */
    public List<ObjectNameKeyValueModel> getObjectNameKeyValueModels(ObjectNameKeyValue objectNameKeyValue) {
        if (isDestroyed()) {
            return null;
        }

        return _ObjectNameKeyValueModelManager.findModels(objectNameKeyValue);
    }

    /**
     * TODO: Comment.
     * 
     * @return
     */
    public Set<String> getObjectNameKeyValuePairStrings() {
        if (isDestroyed()) {
            return null;
        }

        return _ObjectNameKeyValueModelManager.findKeys(getData());
    }

    /**
     * TODO: Comment.
     * 
     * @param objectNameKeyValue
     * @return
     */
    public Set<String> getObjectNameKeyValuePairStrings(ObjectNameKeyValue objectNameKeyValue) {
        if (isDestroyed()) {
            return null;
        }

        return _ObjectNameKeyValueModelManager.findKeys(objectNameKeyValue);
    }

    @Override
    public DataModel<?, ?, ?> getParentModel() {
        // In this conceptual model the DomainModel is also a direct child of the JMX connection
        return getOwnerModel();
    }

    /**
     * TODO: Comment.
     * 
     * @return
     */
    public List<MBeanModel> getRootMBeanModels() {
        if (isDestroyed()) {
            return null;
        }

        return _MBeanModelManager.findModels(getData());
    }

    /**
     * TODO: Comment.
     * 
     * @return
     */
    public Set<ObjectName> getRootMBeanObjectNames() {
        if (isDestroyed()) {
            return null;
        }

        return _MBeanModelManager.findKeys(getData());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.fusesource.ide.zk.zookeeper.model.DataModel#getThis()
     */
    @Override
    protected DomainModel getThis() {
        return this;
    }

    @Override
    protected void hookAfterDestroyed() {
        _ObjectNameKeyValueModelManager.destroy();
        _MBeanModelManager.destroy();
    }

    @Override
    protected void hookAfterSetData() {
        if (_ObjectNameKeyValueModelManager == null) {
            ObjectNameKeyValueModelSource objectNameKeyValueModelSource = new ObjectNameKeyValueModelSource(this,
                    getOwnerModel());
            _ObjectNameKeyValueModelManager = new DataModelManager<ObjectNameKeyValueModel, String, ObjectNameKeyValue>(
                    objectNameKeyValueModelSource);
        }

        if (_MBeanModelManager == null) {
            MBeanModelSource mbeanModelSource = new MBeanModelSource(this, getOwnerModel());
            _MBeanModelManager = new DataModelManager<MBeanModel, ObjectName, MBean>(mbeanModelSource);
        }

        _ObjectNameKeyValueModelManager.refreshManagedModels();
        _MBeanModelManager.refreshManagedModels();
    }

}
