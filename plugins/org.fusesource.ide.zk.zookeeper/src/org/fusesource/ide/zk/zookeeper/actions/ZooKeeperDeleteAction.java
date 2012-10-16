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

package org.fusesource.ide.zk.zookeeper.actions;

import org.fusesource.ide.zk.zookeeper.ZooKeeperActivator;
import org.fusesource.ide.zk.zookeeper.data.Znode;
import org.fusesource.ide.zk.zookeeper.model.ZnodeModel;
import org.fusesource.ide.zk.zookeeper.model.ZooKeeperConnectionModel;
import org.fusesource.ide.zk.core.actions.BaseDeleteAction;


/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public class ZooKeeperDeleteAction extends BaseDeleteAction {

    public ZooKeeperDeleteAction() {
        addInputTypeClass(ZooKeeperConnectionModel.class);
        addInputTypeClass(ZnodeModel.class);
    }

    @Override
    public boolean canRunWithObject(Object object) {
        if (object instanceof ZnodeModel) {
            ZnodeModel znodeModel = (ZnodeModel) object;
            Znode znode = znodeModel.getData();
            return (znode.isLeaf() && !Znode.isSystemPath(znode.getPath()));
        }

        return true;
    }

    @Override
    public void reportError(Exception e) {
        ZooKeeperActivator.reportError(e);
    }

    @Override
    protected String getObjectName(Object selectedObject) {
        if (selectedObject instanceof ZooKeeperConnectionModel) {
            return ((ZooKeeperConnectionModel) selectedObject).getData().getDescriptor().getName();
        }
        else if (selectedObject instanceof ZnodeModel) {
            return ((ZnodeModel) selectedObject).getData().getPath();
        }

        return null;
    }

    @Override
    protected String getObjectTypeName(Object selectedObject) {
        if (selectedObject instanceof ZooKeeperConnectionModel) {
            return "ZooKeeper connection";
        }
        else if (selectedObject instanceof ZnodeModel) {
            return "Znode";
        }

        return null;
    }

}
