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
 * Typed data model manager event.
 * 
 * 
 * @see DataModelManager
 * @see IDataModelManagerEventListener
 * 
 * @author Mark Masse
 */
@SuppressWarnings("serial")
public final class DataModelManagerEvent<M extends DataModel<M, K, D>, K extends Comparable<K>, D> extends EventObject {

    private final M _Model;

    /**
     * Constructor.
     * 
     * @param manager The event source.
     * @param model Optional model.
     */
    public DataModelManagerEvent(DataModelManager<M, K, D> manager, M model) {
        super(manager);
        _Model = model;
    }

    /**
     * The event source.
     * 
     * @return The event source.
     */
    @SuppressWarnings("unchecked")
    public DataModelManager<M, K, D> getManager() {
        return (DataModelManager<M, K, D>) getSource();
    }

    /**
     * The model.
     * 
     * @return The model.
     */
    public M getModel() {
        return _Model;
    }
}
