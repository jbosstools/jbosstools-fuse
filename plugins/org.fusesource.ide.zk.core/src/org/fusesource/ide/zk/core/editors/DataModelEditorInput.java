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
import org.fusesource.ide.zk.core.viewers.DataModelElementType;

/**
 * Editor input that is backed by a {@link DataModel}.
 * 
 * @see DataModelFormEditor
 * 
 * @author Mark Masse
 */
public abstract class DataModelEditorInput<M extends DataModel<M, ?, ?>> extends BaseEditorInput {

    private final DataModelElementType _ElementType;
    private final M _Model;

    /**
     * Constructor.
     *
     * @param editorId The associated (target) editor id.
     * @param model The {@link DataModel}.
     * @param elementType The {@link DataModelElementType} associated with the model type.
     */
    public DataModelEditorInput(String editorId, M model, DataModelElementType elementType) {
        super(editorId);
        _Model = model;
        _ElementType = elementType;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DataModelEditorInput<?> other = (DataModelEditorInput<?>) obj;
        if (_Model == null) {
            if (other._Model != null)
                return false;
        }
        else if (!_Model.equals(other._Model))
            return false;
        return true;
    }

    @Override
    public boolean exists() {
        M model = getModel();
        return (model != null && !model.isDestroyed());
    }

    /**
     * Returns the elementType.
     * 
     * @return The elementType
     */
    public final DataModelElementType getElementType() {
        return _ElementType;
    }

    /**
     * Returns the model.
     * 
     * @return The model
     */
    public M getModel() {
        return _Model;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_Model == null) ? 0 : _Model.hashCode());
        return result;
    }

    @Override
    public final String getName() {
        return getElementType().getText(getModel());
    }

    @Override
    public final String getToolTipText() {
        return getElementType().getToolTipText(getModel());
    }

    @Override
    public String toString() {
        return getClass().getName() + " [Model=" + _Model + "]";
    }

}
