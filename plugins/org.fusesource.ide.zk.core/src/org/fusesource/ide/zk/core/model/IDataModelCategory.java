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

import java.util.List;

/**
 * Named category of model child elements.
 * 
 * @see DataModel
 * 
 * @author Mark Masse
 */
public interface IDataModelCategory<M extends DataModel<M, ?, ?>> {

    /**
     * Returns the name.
     * 
     * @return The name
     */
    public abstract String getName();

    /**
     * Returns the parent model.
     * 
     * @return The parent model.
     */
    public abstract M getParentModel();

    /**
     * Returns the number of elements in the category.
     * 
     * @return
     */
    public abstract int getElementCount();

    /**
     * Returns the elements in the category.
     * 
     * @return
     */
    public abstract List<?> getElements();

}