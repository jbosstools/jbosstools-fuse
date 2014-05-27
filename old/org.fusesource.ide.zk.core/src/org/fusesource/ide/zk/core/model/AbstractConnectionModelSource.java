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

import org.fusesource.ide.zk.core.data.AbstractConnectionDescriptor;
import org.fusesource.ide.zk.core.runtime.ConnectionDescriptorFiles;


/**
 * Source that is backed by {@link ConnectionDescriptorFiles}.
 * 
 * @author Mark Masse
 */
public abstract class AbstractConnectionModelSource<M extends DataModel<M, K, D>, K extends AbstractConnectionDescriptor<K>, D>
        extends DataModelSource<M, K, D> {

    private final ConnectionDescriptorFiles<K> _Files;

    public AbstractConnectionModelSource(ConnectionDescriptorFiles<K> files) {
        _Files = files;
    }

    @Override
    public void deleteData(K descriptor) throws DataModelSourceException {
        if (!getFiles().delete(descriptor)) {
            throw new DataModelSourceException("Failed to delete descriptor '" + descriptor.getName() + "'");
        }
    }

    /**
     * Returns the files.
     * 
     * @return The files
     */
    public ConnectionDescriptorFiles<K> getFiles() {
        return _Files;
    }

    @Override
    public Set<K> getKeys() throws DataModelSourceException {
        return getFiles().loadAll();
    }

    @Override
    public K insertData(K key, D data) throws DataModelSourceException {
        getFiles().save(key);
        return key;
    }

    @Override
    public boolean isDeleteDataSupported() {
        return true;
    }

    @Override
    public boolean isGetKeysSupported() {
        return true;
    }

    @Override
    public boolean isInsertDataSupported() {
        return true;
    }

    @Override
    public boolean isUpdateDataSupported() {
        return true;
    }

    @Override
    public void updateData(M model) throws DataModelSourceException {
        getFiles().save(model.getKey());
    }

}
