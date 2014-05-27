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

import org.eclipse.ui.IEditorInput;
import org.fusesource.ide.zk.zookeeper.ZooKeeperActivator;
import org.fusesource.ide.zk.zookeeper.editors.znodeform.ZnodeModelEditorInput;
import org.fusesource.ide.zk.zookeeper.editors.znodeform.ZnodeModelFormEditor;
import org.fusesource.ide.zk.zookeeper.editors.zookeeperconnectionform.ZooKeeperConnectionModelEditorInput;
import org.fusesource.ide.zk.zookeeper.editors.zookeeperconnectionform.ZooKeeperConnectionModelFormEditor;
import org.fusesource.ide.zk.zookeeper.editors.zookeeperconnectionform.ZooKeeperConnectionModelServersFormPage;
import org.fusesource.ide.zk.zookeeper.editors.zookeeperserverform.ZooKeeperServerModelEditorInput;
import org.fusesource.ide.zk.zookeeper.editors.zookeeperserverform.ZooKeeperServerModelFormEditor;
import org.fusesource.ide.zk.zookeeper.model.ZnodeModel;
import org.fusesource.ide.zk.zookeeper.model.ZooKeeperConnectionModel;
import org.fusesource.ide.zk.zookeeper.model.ZooKeeperServerModel;
import org.fusesource.ide.zk.zookeeper.model.ZooKeeperServersModelCategory;
import org.fusesource.ide.zk.jmx.actions.JmxOpenAction;


/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public class ZooKeeperOpenAction extends JmxOpenAction {

    public ZooKeeperOpenAction() {
        addInputTypeClass(ZnodeModel.class);
        addInputTypeClass(ZooKeeperConnectionModel.class);
        addInputTypeClass(ZooKeeperServersModelCategory.class);
        addInputTypeClass(ZooKeeperServerModel.class);
    }

    @Override
    public void reportError(Exception e) {
        ZooKeeperActivator.reportError(e);
    }

    @Override
    protected String getAssociatedEditorId(Object object) {

        if (object instanceof ZnodeModel) {
            return ZnodeModelFormEditor.ID;
        }
        else if (object instanceof ZooKeeperConnectionModel) {
            return ZooKeeperConnectionModelFormEditor.ID;
        }
        else if (object instanceof ZooKeeperServerModel) {
            return ZooKeeperServerModelFormEditor.ID;
        }
        else if (object instanceof ZooKeeperServersModelCategory) {
            return ZooKeeperConnectionModelFormEditor.ID;
        }
        return super.getAssociatedEditorId(object);
    }

    @Override
    protected IEditorInput getAssociatedEditorInput(String editorId, Object object) {
        if (object instanceof ZnodeModel) {
            ZnodeModel model = (ZnodeModel) object;
            return new ZnodeModelEditorInput(editorId, model);
        }
        else if (object instanceof ZooKeeperConnectionModel) {
            ZooKeeperConnectionModel model = (ZooKeeperConnectionModel) object;
            return new ZooKeeperConnectionModelEditorInput(editorId, model);
        }
        else if (object instanceof ZooKeeperServerModel) {
            ZooKeeperServerModel model = (ZooKeeperServerModel) object;
            return new ZooKeeperServerModelEditorInput(editorId, model);
        }
        else if (object instanceof ZooKeeperServersModelCategory) {
            ZooKeeperConnectionModel model = ((ZooKeeperServersModelCategory) object).getParentModel();
            return new ZooKeeperConnectionModelEditorInput(editorId, model);
        }
        return super.getAssociatedEditorInput(editorId, object);
    }

    @Override
    protected String getAssociatedEditorPageId(String editorId, Object object) {
        if (object instanceof ZooKeeperServersModelCategory) {
            return ZooKeeperConnectionModelServersFormPage.ID;
        }

        return super.getAssociatedEditorPageId(editorId, object);
    }

}
