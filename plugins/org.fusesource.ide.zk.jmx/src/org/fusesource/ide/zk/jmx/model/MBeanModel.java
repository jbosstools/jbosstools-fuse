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

import org.fusesource.ide.zk.jmx.data.MBean;
import org.fusesource.ide.zk.jmx.data.MBeanAttribute;
import org.fusesource.ide.zk.jmx.data.MBeanOperation;
import org.fusesource.ide.zk.jmx.data.ObjectNameKeyValue;
import org.fusesource.ide.zk.jmx.jmxdoc.MBeanDoc;
import org.fusesource.ide.zk.core.model.DataModelManager;


/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public final class MBeanModel extends AbstractObjectNameKeyValueModel<MBeanModel, ObjectName, MBean> {

    private final DataModelManager<MBeanAttributeModel, String, MBeanAttribute> _MBeanAttributeModelManager;
    private final MBeanAttributesModelCategory _MBeanAttributesModelCategory;
    private final DataModelManager<MBeanOperationModel, String, MBeanOperation> _MBeanOperationModelManager;
    private final MBeanOperationsModelCategory _MBeanOperationsModelCategory;

    /**
     * TODO: Comment.
     * 
     * @param objectName
     * @param domainModel
     * @param jmxConnectionModel
     */
    MBeanModel(ObjectName objectName, DomainModel domainModel, JmxConnectionModel jmxConnectionModel) {
        super(objectName, domainModel, jmxConnectionModel);

        MBeanAttributeModelSource mbeanAttributeModelSource = new MBeanAttributeModelSource(this, jmxConnectionModel);
        _MBeanAttributeModelManager = new DataModelManager<MBeanAttributeModel, String, MBeanAttribute>(
                mbeanAttributeModelSource);

        MBeanOperationModelSource mbeanOperationModelSource = new MBeanOperationModelSource(this, jmxConnectionModel);
        _MBeanOperationModelManager = new DataModelManager<MBeanOperationModel, String, MBeanOperation>(
                mbeanOperationModelSource);

        _MBeanAttributesModelCategory = new MBeanAttributesModelCategory(this);
        _MBeanOperationsModelCategory = new MBeanOperationsModelCategory(this);
    }

    /**
     * TODO: Comment.
     * 
     * @param attributeName
     * @return
     */
    public MBeanAttributeModel getAttributeModel(String attributeName) {
        return _MBeanAttributeModelManager.getModel(attributeName);
    }

    /**
     * TODO: Comment.
     * 
     * @return
     */
    public List<MBeanAttributeModel> getAttributeModels() {
        return _MBeanAttributeModelManager.getModels();
    }

    /**
     * TODO: Comment.
     * 
     * @return
     */
    public Set<String> getAttributeNames() {
        return _MBeanAttributeModelManager.getKeys();
    }

    /**
     * Returns the mBeanAttributesModelCategory.
     * 
     * @return The mBeanAttributesModelCategory
     */
    public MBeanAttributesModelCategory getAttributesModelCategory() {
        return _MBeanAttributesModelCategory;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.fusesource.ide.zk.zookeeper.model.jmx.AbstractObjectNameKeyValueModel#getObjectNameKeyValue()
     */
    @Override
    public ObjectNameKeyValue getObjectNameKeyValue() {
        return getDomainModel().getData().getMBeanObjectNameKeyValue(getKey());
    }

    /**
     * TODO: Comment.
     * 
     * @param operationName
     * @return
     */
    public MBeanOperationModel getOperationModel(String operationName) {
        return _MBeanOperationModelManager.getModel(operationName);
    }

    /**
     * TODO: Comment.
     * 
     * @return
     */
    public List<MBeanOperationModel> getOperationModels() {
        return _MBeanOperationModelManager.getModels();
    }

    /**
     * TODO: Comment.
     * 
     * @return
     */
    public Set<String> getOperationNames() {
        return _MBeanOperationModelManager.getKeys();
    }

    /**
     * Returns the mBeanOperationsModelCategory.
     * 
     * @return The mBeanOperationsModelCategory
     */
    public MBeanOperationsModelCategory getOperationsModelCategory() {
        return _MBeanOperationsModelCategory;
    }

    @Override
    protected MBeanModel getThis() {
        return this;
    }

    @Override
    protected void hookAfterDestroyed() {
        _MBeanAttributeModelManager.destroy();
        _MBeanOperationModelManager.destroy();
    }

    /**
     * TODO: Comment.
     * 
     * @return
     */
    public MBeanDoc getDoc() {
        return new MBeanDoc(getData().getInfo());
    }

}
