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
import java.util.TreeSet;

import org.fusesource.ide.zk.jmx.data.Domain;
import org.fusesource.ide.zk.jmx.data.IObjectNamePart;
import org.fusesource.ide.zk.jmx.data.ObjectNameKeyValue;
import org.fusesource.ide.zk.core.model.DataModelSourceException;
import org.fusesource.ide.zk.core.model.OperationNotSupportedException;

/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public abstract class AbstractObjectNameKeyValueModelSource<M extends AbstractObjectNameKeyValueModel<M, K, D>, K extends Comparable<K>, D>
        extends AbstractJmxModelSource<M, K, D> {

    private final DomainModel _DomainModel;

    /**
     * TODO: Comment.
     * 
     * @param domainModel
     * @param jmxConnectionModel
     */
    public AbstractObjectNameKeyValueModelSource(DomainModel domainModel, JmxConnectionModel jmxConnectionModel) {
        super(jmxConnectionModel);
        _DomainModel = domainModel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.fusesource.ide.zk.zookeeper.model.DataModelSource#findKeys(java.lang.Object)
     */
    @Override
    public Set<K> findKeys(Object criteria) throws DataModelSourceException {

        if (criteria instanceof IObjectNamePart) {
            IObjectNamePart iObjectNamePart = (IObjectNamePart) criteria;
            return getKeys(iObjectNamePart.getChildObjectNameKeyValues());                   
        }
        
        throw new OperationNotSupportedException();
        
    }

    /**
     * Returns the domainModel.
     * 
     * @return The domainModel
     */
    public DomainModel getDomainModel() {
        return _DomainModel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.fusesource.ide.zk.zookeeper.model.AbstractDataModelSource#isFindKeysSupported(java.lang.Object)
     */
    @Override
    public boolean isFindKeysSupported(Object criteria) {
        return (criteria instanceof IObjectNamePart);
    }

    /**
     * TODO: Comment.
     * 
     * @return
     */
    protected Domain getDomain() {
        return _DomainModel.getData();
    }

    /**
     * TODO: Comment.
     * 
     * @param objectNameKeyValue
     * @return
     */
    protected abstract K getKey(ObjectNameKeyValue objectNameKeyValue);

    /**
     * TODO: Comment.
     * 
     * @param childObjectNameKeyValues
     * @return
     */
    private Set<K> getKeys(List<ObjectNameKeyValue> childObjectNameKeyValues) {
        if (childObjectNameKeyValues == null) {
            return null;
        }

        Set<K> keys = new TreeSet<K>();
        for (ObjectNameKeyValue childObjectNameKeyValue : childObjectNameKeyValues) {
            K key = getKey(childObjectNameKeyValue);
            if (key != null) {
                keys.add(key);
            }
        }

        return keys;
    }

}
