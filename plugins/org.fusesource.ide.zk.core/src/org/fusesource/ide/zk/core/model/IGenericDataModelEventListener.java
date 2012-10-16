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

import java.util.EventListener;

/**
 * Untyped data model listener interface.
 * 
 * @author Mark Masse
 */
public interface IGenericDataModelEventListener extends EventListener {

    /**
     * Signifies that the data model has been destroyed.
     * 
     * @param event The data model event.
     * @see DataModel#destroy()
     */
    public void dataModelDestroyed(GenericDataModelEvent event);

    /**
     * Signifies that the data model has changed in some way.
     * 
     * @param event The data model event.
     */
    public void dataModelDataChanged(GenericDataModelEvent event);

    /**
     * Signifies that the data model has been refreshed (had its data replaced).
     * 
     * @param event The data model event.
     * @see DataModel#refreshData()
     */
    public void dataModelDataRefreshed(GenericDataModelEvent event);
}
