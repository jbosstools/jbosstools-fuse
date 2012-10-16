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

import java.util.Collection;

import org.fusesource.ide.zk.core.model.DataModel;
import org.fusesource.ide.zk.core.viewers.CollectionElementType;
import org.fusesource.ide.zk.core.viewers.IElementType;


/**
 * Table editor input that is backed by a {@link Collection} of {@link DataModel}.
 * 
 * @author Mark Masse
 */
public abstract class DataModelCollectionEditorInput<M extends DataModel<M, ?, ?>> extends DataModelTableEditorInput<M> {

    private final static IElementType ELEMENT_TYPE = new CollectionElementType();

    private final Collection<M> _Models;

    public DataModelCollectionEditorInput(String editorId, Collection<M> models) {
        super(editorId);
        _Models = models;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DataModelCollectionEditorInput<?> other = (DataModelCollectionEditorInput<?>) obj;
        if (_Models == null) {
            if (other._Models != null)
                return false;
        }
        else if (!_Models.equals(other._Models))
            return false;
        return true;
    }

    @Override
    public boolean exists() {
        return _Models != null;
    }

    /**
     * Returns the modelSet.
     * 
     * @return The modelSet
     */
    public Collection<M> getModels() {
        return _Models;
    }

    @Override
    public Object getTableViewerInput() {
        return getModels();
    }

    @Override
    public IElementType getTableViewerInputElementType() {
        return ELEMENT_TYPE;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_Models == null) ? 0 : _Models.hashCode());
        return result;
    }

}
