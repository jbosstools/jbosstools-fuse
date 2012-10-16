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
 * The C.R.U.D interface for {@link DataModel models}. Instances of this interface should always be wrapped by a
 * {@link DataModelManager}.
 * 
 * @author Mark Masse
 */
public interface IDataModelSource<M extends DataModel<M, K, D>, K extends Comparable<K>, D> {

    /**
     * Creates a new model for the specified key.
     * 
     * @param key The key
     * @return The model associated with the key.
     * @see #getData(Comparable)
     */
    public M createModel(K key) throws DataModelSourceException;

    /**
     * Deletes the data associated with the specified key. Subclasses that do not support delete must throw an
     * {@link OperationNotSupportedException}.
     * 
     * @param key The key that identifies the data to be deleted.
     * @see #isDeleteDataSupported()
     */
    public void deleteData(K key) throws DataModelSourceException;

    /**
     * Returns the set of keys associated with the specified criteria. Subclasses must either accept the criteria object
     * and return the keys or throw an {@link OperationNotSupportedException}.
     * 
     * @param criteria The criteria for the keys query.
     * @return The set of keys associated with the specified criteria.
     * @see #isFindKeysSupported(Object)
     */
    public Set<K> findKeys(Object criteria) throws DataModelSourceException;

    /**
     * Returns the data associated with the specified key.
     * 
     * @param key The key
     * @return The data associated with the key.
     * @see #createModel(Comparable)
     */
    public D getData(K key) throws DataModelSourceException;

    /**
     * Returns all of the keys available from this source. Subclasses that cannot support this must throw an
     * {@link OperationNotSupportedException}.
     * 
     * @return All of the keys available from this source.
     * @see #isGetKeysSupported()
     */
    public Set<K> getKeys() throws DataModelSourceException;

    /**
     * Insert the new data associated with the specified key. Note that either the specified key or data may be null but
     * not both. Subclasses that do not support inserts must throw an {@link OperationNotSupportedException}.
     * 
     * @param key The key to insert (or <code>null</code>).
     * @param data The data to insert (or <code>null</code>).
     * @return The key associated with the newly inserted data.
     * @see #isInsertDataSupported()
     */
    public K insertData(K key, D data) throws DataModelSourceException;

    /**
     * Returns <code>true</code> if this source supports {@link #deleteData(Comparable) delete}.
     * 
     * @return <code>true</code> if this source supports delete.
     * @see #deleteData(Comparable)
     */
    public boolean isDeleteDataSupported();

    /**
     * Returns <code>true</code> if this source supports {@link #findKeys(Object) findKeys}.
     * 
     * @return <code>true</code> if this source supports findKeys.
     * @see #findKeys(Object)
     */
    public boolean isFindKeysSupported(Object criteria);

    /**
     * Returns <code>true</code> if this source supports {@link #getKeys() getKeys}.
     * 
     * @return <code>true</code> if this source supports getKeys.
     * @see #getKeys()
     */
    public boolean isGetKeysSupported();

    /**
     * Returns <code>true</code> if this source supports {@link #insertData(Comparable, Object) insertData}.
     * 
     * @return <code>true</code> if this source supports insertData.
     * @see #insertData(Comparable, Object)
     */
    public boolean isInsertDataSupported();

    /**
     * Returns <code>true</code> if this source supports {@link #updateData(DataModel) updateData}.
     * 
     * @return <code>true</code> if this source supports updateData.
     * @see #updateData(DataModel)
     */
    public boolean isUpdateDataSupported();

    /**
     * Updates the data associated with the specified model. Subclasses that do not support updates must throw an
     * {@link OperationNotSupportedException}.
     * 
     * @param model The model to update.
     * @see #isUpdateDataSupported()
     */
    public void updateData(M model) throws DataModelSourceException;

}
