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

import org.fusesource.ide.zk.core.model.IDataModelCategory;

/**
 * An {@link IElementType} backed by a {@link IDataModelCategory}.
 * 
 * @author Mark Masse
 */
public abstract class AbstractDataModelCategoryElementType extends BaseElementType {

    @Override
    public final int getChildCount(Object parent) {
        IDataModelCategory<?> dataModelCategory = (IDataModelCategory<?>) parent;
        return dataModelCategory.getElementCount();
    }

    @Override
    public final Object getChildElement(Object parent, int index) {
        IDataModelCategory<?> dataModelCategory = (IDataModelCategory<?>) parent;
        return dataModelCategory.getElements().get(index);
    }

    @Override
    public final Object[] getChildren(Object parent) {
        IDataModelCategory<?> dataModelCategory = (IDataModelCategory<?>) parent;
        return dataModelCategory.getElements().toArray();
    }

    @Override
    public final Object getParent(Object element) {
        IDataModelCategory<?> dataModelCategory = (IDataModelCategory<?>) element;
        return dataModelCategory.getParentModel();
    }

    @Override
    public String getText(Object element) {
        IDataModelCategory<?> dataModelCategory = (IDataModelCategory<?>) element;
        return dataModelCategory.getName();
    }

}
