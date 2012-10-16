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

package org.fusesource.ide.zk.core.model;

import java.util.Set;

/**
 * Provides default implementation of the {@link IDataModelSource} interface. 
 * 
 * @author Mark Masse
 */
public abstract class DataModelSource<M extends DataModel<M, K, D>, K extends Comparable<K>, D> implements
        IDataModelSource<M, K, D> {

    @Override
    public final M createModel(K key) throws DataModelSourceException {
        if (key == null) {
            throw new IllegalArgumentException("null key");
        }

        if (isGetKeysSupported()) {
            Set<K> keys = getKeys();
            if (!keys.contains(key)) {
                throw new IllegalArgumentException("invalid key: " + key);
            }
        }

        D data = getData(key);
        if (data == null) {
            return null;
        }

        M model = createModelInternal(key);
        model.setData(data);
        return model;
    }

    @Override
    public void deleteData(K key) throws DataModelSourceException {
        throw new OperationNotSupportedException();
    }

    @Override
    public Set<K> findKeys(Object criteria) throws DataModelSourceException {
        throw new OperationNotSupportedException();
    }

    @Override
    public Set<K> getKeys() throws DataModelSourceException {
        throw new OperationNotSupportedException();
    }

    @Override
    public K insertData(K key, D data) throws DataModelSourceException {
        throw new OperationNotSupportedException();
    }

    @Override
    public boolean isDeleteDataSupported() {
        return false;
    }

    @Override
    public boolean isFindKeysSupported(Object criteria) {
        return false;
    }

    @Override
    public boolean isGetKeysSupported() {
        return false;
    }

    @Override
    public boolean isInsertDataSupported() {
        return false;
    }

    @Override
    public boolean isUpdateDataSupported() {
        return false;
    }

    @Override
    public void updateData(M model) throws DataModelSourceException {
        throw new OperationNotSupportedException();
    }

    protected abstract M createModelInternal(K key) throws DataModelSourceException;

}
