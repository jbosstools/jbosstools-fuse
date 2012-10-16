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

package org.fusesource.ide.zk.core.editors;

import org.fusesource.ide.zk.core.model.DataModel;
import org.fusesource.ide.zk.core.model.DataModelManager;
import org.fusesource.ide.zk.core.viewers.DataModelManagerElementType;
import org.fusesource.ide.zk.core.viewers.IElementType;

/**
 * Table editor input that is backed by a {@link DataModelManager}.
 * 
 * @author Mark Masse
 */
public abstract class DataModelManagerEditorInput<M extends DataModel<M, K, D>, K extends Comparable<K>, D> extends
        DataModelTableEditorInput<M> {

    private final static IElementType ELEMENT_TYPE = new DataModelManagerElementType();

    private final DataModelManager<M, K, D> _DataModelManager;

    public DataModelManagerEditorInput(String editorId, DataModelManager<M, K, D> dataModelManager) {
        super(editorId);
        _DataModelManager = dataModelManager;

    }

    @Override
    public boolean exists() {
        return _DataModelManager != null && !_DataModelManager.isDestroyed();
    }

    /**
     * Returns the modelSet.
     * 
     * @return The modelSet
     */
    public DataModelManager<M, K, D> getDataModelManager() {
        return _DataModelManager;
    }

    @Override
    public Object getTableViewerInput() {
        if (_DataModelManager.getSource().isGetKeysSupported()) {
            _DataModelManager.getModels();
        }
        return _DataModelManager;
    }

    @Override
    public IElementType getTableViewerInputElementType() {
        return ELEMENT_TYPE;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_DataModelManager == null) ? 0 : _DataModelManager.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DataModelManagerEditorInput<?, ?, ?> other = (DataModelManagerEditorInput<?, ?, ?>) obj;
        if (_DataModelManager == null) {
            if (other._DataModelManager != null)
                return false;
        }
        else if (!_DataModelManager.equals(other._DataModelManager))
            return false;
        return true;
    }

}
