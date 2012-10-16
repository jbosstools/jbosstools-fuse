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

import java.util.EventObject;

/**
 * Untyped data model manager event.
 * 
 * 
 * @see DataModelManager
 * @see IGenericDataModelManagerEventListener
 * 
 * @author Mark Masse
 */
@SuppressWarnings("serial")
public final class GenericDataModelManagerEvent extends EventObject {

    private final DataModel<?, ?, ?> _Model;

    /**
     * Constructor.
     * 
     * @param manager The event source.
     * @param model Optional model.
     */
    public GenericDataModelManagerEvent(DataModelManager<?, ?, ?> manager, DataModel<?, ?, ?> model) {
        super(manager);
        _Model = model;
    }

    /**
     * Returns the event source.
     * 
     * @return The event source.
     */
    public DataModelManager<?, ?, ?> getManager() {
        return (DataModelManager<?, ?, ?>) getSource();
    }

    /**
     * Returns the model.
     * 
     * @return The model.
     */
    public DataModel<?, ?, ?> getModel() {
        return _Model;
    }
}
