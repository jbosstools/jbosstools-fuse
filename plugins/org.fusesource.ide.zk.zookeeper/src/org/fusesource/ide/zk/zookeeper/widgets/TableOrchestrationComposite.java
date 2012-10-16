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

package org.fusesource.ide.zk.zookeeper.widgets;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

public abstract class TableOrchestrationComposite extends OrchestrationComposite {

    private Button _AddButton;
    private Button _RemoveButton;
    private Table _Table;

    public TableOrchestrationComposite(Composite parent, int style) {
        super(parent, style);
    }

    /**
     * Returns the addButton.
     * 
     * @return The addButton
     */
    public final Button getAddButton() {
        return _AddButton;
    }

    /**
     * Returns the removeButton.
     * 
     * @return The removeButton
     */
    public final Button getRemoveButton() {
        return _RemoveButton;
    }

    /**
     * Returns the table.
     * 
     * @return The table
     */
    public final Table getTable() {
        return _Table;
    }

    /**
     * Sets the addButton.
     * 
     * @param addButton the addButton to set
     */
    public final void setAddButton(Button addButton) {
        _AddButton = addButton;
    }

    /**
     * Sets the removeButton.
     * 
     * @param removeButton the removeButton to set
     */
    public final void setRemoveButton(Button removeButton) {
        _RemoveButton = removeButton;
    }

    /**
     * Sets the table.
     * 
     * @param table the table to set
     */
    public final void setTable(Table table) {
        _Table = table;
    }
}
