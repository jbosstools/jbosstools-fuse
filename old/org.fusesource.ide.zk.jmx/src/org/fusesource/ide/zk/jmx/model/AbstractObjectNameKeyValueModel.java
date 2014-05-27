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
import org.fusesource.ide.zk.jmx.data.IObjectNamePart;
import org.fusesource.ide.zk.jmx.data.ObjectNameKeyValue;
import org.fusesource.ide.zk.core.model.DataModel;

/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public abstract class AbstractObjectNameKeyValueModel<T extends AbstractObjectNameKeyValueModel<T, K, D>, K extends Comparable<K>, D>
        extends AbstractJmxModel<T, K, D> {

    private final DomainModel _DomainModel;

    /**
     * TODO: Comment.
     * 
     * @param key
     * @param domainModel
     * @param jmxConnectionModel
     */
    AbstractObjectNameKeyValueModel(K key, DomainModel domainModel, JmxConnectionModel jmxConnectionModel) {
        super(key, jmxConnectionModel);
        _DomainModel = domainModel;
    }

    /**
     * TODO: Comment.
     * 
     * @return
     */
    public List<MBeanModel> getChildMBeanModels() {
        return getDomainModel().getMBeanModels(getObjectNameKeyValue());
    }

    /**
     * TODO: Comment.
     * 
     * @return
     */
    public Set<ObjectName> getChildMBeanObjectNames() {
        return getDomainModel().getMBeanObjectNames(getObjectNameKeyValue());
    }

    /**
     * TODO: Comment.
     * 
     * @return
     */
    public List<ObjectNameKeyValueModel> getChildObjectNameKeyValueModels() {
        return getDomainModel().getObjectNameKeyValueModels(getObjectNameKeyValue());
    }

    /**
     * TODO: Comment.
     * 
     * @return
     */
    public Set<String> getChildObjectNameKeyValuePairStrings() {
        return getDomainModel().getObjectNameKeyValuePairStrings(getObjectNameKeyValue());
    }

    /**
     * Returns the domainModel.
     * 
     * @return The domainModel
     */
    public DomainModel getDomainModel() {
        return _DomainModel;
    }

    /**
     * TODO: Comment.
     * 
     * @return
     */
    public abstract ObjectNameKeyValue getObjectNameKeyValue();

    @Override
    public DataModel<?, ?, ?> getParentModel() {
        ObjectNameKeyValue objectNameKeyValue = getObjectNameKeyValue();
        IObjectNamePart parentPart = objectNameKeyValue.getParentObjectNamePart();
        if (parentPart instanceof Domain) {
            return getDomainModel();
        }
        else if (parentPart instanceof ObjectNameKeyValue) {
            ObjectNameKeyValue parentKeyValue = (ObjectNameKeyValue) parentPart;
            if (parentKeyValue.isMBean()) {
                return getDomainModel().getMBeanModel(parentKeyValue.getObjectName());
            }
            else {
                return getDomainModel().getObjectNameKeyValueModel(parentKeyValue.getKeyValuePairString());
            }
        }
        return null;
    }
}
