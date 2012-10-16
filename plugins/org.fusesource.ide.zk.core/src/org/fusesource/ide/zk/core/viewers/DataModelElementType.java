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

import org.eclipse.swt.graphics.Image;
import org.fusesource.ide.zk.core.actions.BaseAction;
import org.fusesource.ide.zk.core.actions.BaseOpenAction;
import org.fusesource.ide.zk.core.actions.RefreshAction;
import org.fusesource.ide.zk.core.actions.BaseAction.InputType;
import org.fusesource.ide.zk.core.model.DataModel;

import java.util.List;


/**
 * An {@link IElementType} backed by a {@link DataModel}.
 * 
 * @author Mark Masse
 */
public abstract class DataModelElementType extends BaseElementType {

    private RefreshAction _RefreshAction;

    /**
     * Returns the "large" image for the specified element.
     * 
     * @param element The viewer element.
     * @return The larger sized image for the specified element.
     * 
     * @see #getImage(Object)
     */
    public abstract Image getLargeImage(Object element);

    /**
     * Returns the {@link BaseOpenAction}.
     * 
     * @return The "Open" action
     */
    public abstract BaseOpenAction getOpenAction();

    @Override
    public Object getParent(Object element) {
        DataModel<?, ?, ?> model = (DataModel<?, ?, ?>) element;
        return model.getParentModel();
    }

    /**
     * Returns the {@link RefreshAction}.
     * 
     * @return The "Refresh" action
     */
    public final RefreshAction getRefreshAction() {
        return _RefreshAction;
    }

    @Override
    protected List<BaseAction> createActions() {
        List<BaseAction> tableActions = super.createActions();
        _RefreshAction = new RefreshAction(InputType.STRUCTURED_SELECTION);
        tableActions.add(_RefreshAction);
        return tableActions;
    }

}
