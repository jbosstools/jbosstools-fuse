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

package org.fusesource.ide.zk.jmx.viewers;


import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.fusesource.ide.zk.jmx.actions.JmxOpenAction;
import org.fusesource.ide.zk.core.actions.BaseAction;
import org.fusesource.ide.zk.core.actions.BaseOpenAction;
import org.fusesource.ide.zk.core.actions.RefreshAction;
import org.fusesource.ide.zk.core.viewers.DataModelElementType;

import java.util.List;

public abstract class AbstractJmxDataModelElementType extends DataModelElementType {

    private JmxOpenAction _OpenAction;

    @Override
    public void fillContextMenu(IMenuManager manager) {

        BaseOpenAction openAction = getOpenAction();
        if (openAction != null && !openAction.getSelectionProvider().getSelection().isEmpty()) {
            manager.add(openAction);
        }

        RefreshAction refreshAction = getRefreshAction();
        if (refreshAction != null && !refreshAction.getSelectionProvider().getSelection().isEmpty()) {
            manager.add(new Separator());
            manager.add(refreshAction);
        }
    }

    /**
     * Returns the openAction.
     * 
     * @return The openAction
     */
    @Override
    public final JmxOpenAction getOpenAction() {
        return _OpenAction;
    }

    @Override
    protected List<BaseAction> createActions() {
        List<BaseAction> tableActions = super.createActions();
        _OpenAction = new JmxOpenAction();
        tableActions.add(_OpenAction);

        return tableActions;
    }

}
