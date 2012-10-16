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

package org.fusesource.ide.zk.core.viewers;

import org.fusesource.ide.zk.core.model.DataModelManager;

/**
 * An {@link IElementType} backed by a {@link DataModelManager}.
 * 
 * @author Mark Masse
 */
public class DataModelManagerElementType extends ElementTypeAdapter {

    @Override
    public int getChildCount(Object element) {
        DataModelManager<?, ?, ?> dataModelManager = (DataModelManager<?, ?, ?>) element;
        return dataModelManager.getManagedKeys().size();
    }

    @Override
    public Object getChildElement(Object element, int index) {
        DataModelManager<?, ?, ?> dataModelManager = (DataModelManager<?, ?, ?>) element;
        return dataModelManager.getManagedModels().get(index);
    }

    @Override
    public Object[] getChildren(Object element) {
        DataModelManager<?, ?, ?> dataModelManager = (DataModelManager<?, ?, ?>) element;
        return dataModelManager.getManagedModels().toArray();
    }

}
