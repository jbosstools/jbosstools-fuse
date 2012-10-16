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

package org.fusesource.ide.zk.zookeeper.viewers;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.fusesource.ide.zk.zookeeper.actions.ZooKeeperDeleteAction;
import org.fusesource.ide.zk.zookeeper.actions.ZooKeeperOpenAction;
import org.fusesource.ide.zk.core.actions.BaseAction;
import org.fusesource.ide.zk.core.actions.RefreshAction;
import org.fusesource.ide.zk.core.viewers.DataModelElementType;

import java.util.List;


/**
 * Base class for the ZooKeeper plug-in's {@link DataModelElementType}s.
 * 
 * @author Mark Masse
 */
public abstract class AbstractZooKeeperDataModelElementType extends DataModelElementType {

    private ZooKeeperDeleteAction _DeleteAction;
    private ZooKeeperOpenAction _OpenAction;

    @Override
    public void fillContextMenu(IMenuManager manager) {

        ZooKeeperOpenAction openAction = getOpenAction();
        if (openAction != null && openAction.isEnabled()) {
            manager.add(openAction);
        }

        ZooKeeperDeleteAction deleteAction = getDeleteAction();
        if (deleteAction != null && deleteAction.isEnabled()) {
            manager.add(new Separator());
            manager.add(deleteAction);
        }

        RefreshAction refreshAction = getRefreshAction();
        if (refreshAction != null && refreshAction.isEnabled()) {
            manager.add(new Separator());
            manager.add(refreshAction);
        }
    }

    /**
     * Returns the deleteAction.
     * 
     * @return The deleteAction
     */
    public final ZooKeeperDeleteAction getDeleteAction() {
        return _DeleteAction;
    }

    /**
     * Returns the openAction.
     * 
     * @return The openAction
     */
    @Override
    public final ZooKeeperOpenAction getOpenAction() {
        return _OpenAction;
    }

    @Override
    protected List<BaseAction> createActions() {
        List<BaseAction> tableActions = super.createActions();

        _OpenAction = new ZooKeeperOpenAction();
        tableActions.add(_OpenAction);

        _DeleteAction = new ZooKeeperDeleteAction();
        tableActions.add(_DeleteAction);

        return tableActions;
    }

}
