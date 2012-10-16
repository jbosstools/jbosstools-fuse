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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.fusesource.ide.zk.zookeeper.ZooKeeperActivator;
import org.fusesource.ide.zk.zookeeper.data.Znode;
import org.fusesource.ide.zk.zookeeper.editors.znodeform.ZnodeModelEditorInput;
import org.fusesource.ide.zk.zookeeper.model.ZnodeModel;

import java.util.Collection;


/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public class TableEditChildrenAction extends TableEditAction {

    private static final ImageDescriptor ACTION_IMAGE_DESCRIPTOR = ZooKeeperActivator
            .getManagedImageDescriptor(ZooKeeperActivator.IMAGE_KEY_ACTION_TABLE_EDIT_CHILDREN);
    private static final String ACTION_TEXT = "Table Edit Children";
    private static final String ACTION_TOOL_TIP_TEXT = ACTION_TEXT;

    /**
     * TODO: Comment.
     * 
     */
    public TableEditChildrenAction() {
        super(InputType.SINGLE_STRUCTURED_SELECTION);
        setText(ACTION_TEXT);
        setToolTipText(ACTION_TOOL_TIP_TEXT);
        setImageDescriptor(ACTION_IMAGE_DESCRIPTOR);
    }

    @Override
    public boolean canRunWithObject(Object object) {

        ZnodeModel znodeModel = null;
        if (object instanceof ZnodeModel) {
            znodeModel = (ZnodeModel) object;
        }
        else if (object instanceof ZnodeModelEditorInput) {
            znodeModel = ((ZnodeModelEditorInput) object).getModel();
        }

        if (znodeModel == null) {
            return false;
        }

        Znode znode = znodeModel.getData();
        return (!znode.isLeaf() && !Znode.isSystemPath(znode.getPath()));
    }

    @Override
    protected Collection<ZnodeModel> getModels(IStructuredSelection selection) {
        ZnodeModel parentModel = (ZnodeModel) selection.getFirstElement();
        return parentModel.getChildModels();
    }

}
