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

/**
 * Base class for a named category of model child elements.
 * 
 * @see DataModel
 * 
 * @author Mark Masse
 */
public abstract class AbstractDataModelCategory<M extends DataModel<M, ?, ?>> implements IDataModelCategory<M> {

    private final M _ParentModel;
    private final String _Name;

    public AbstractDataModelCategory(M parentModel, String name) {
        _ParentModel = parentModel;
        _Name = name;
    }

    /**
     * Returns the name.
     * 
     * @return The name
     */
    public String getName() {
        return _Name;
    }

    /**
     * Returns the parentModel.
     * 
     * @return The parentModel
     */
    public M getParentModel() {
        return _ParentModel;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" + (_Name != null ? "Name=" + _Name + ", " : "")
                + (_ParentModel != null ? "ParentModel=" + _ParentModel : "") + "]";
    }

}
