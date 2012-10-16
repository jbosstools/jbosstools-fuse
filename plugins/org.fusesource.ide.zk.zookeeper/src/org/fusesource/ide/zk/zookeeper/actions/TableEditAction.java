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
import org.fusesource.ide.zk.zookeeper.editors.znodetable.ZnodeModelCollectionEditorInput;
import org.fusesource.ide.zk.zookeeper.editors.znodetable.ZnodeModelTableEditor;
import org.fusesource.ide.zk.zookeeper.model.ZnodeModel;
import org.fusesource.ide.zk.core.EclipseCoreActivator;
import org.fusesource.ide.zk.core.actions.BaseAction;

import java.util.Collection;


/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public class TableEditAction extends BaseAction {

    private static final String ACTION_TEXT = "Table Edit";
    private static final String ACTION_TOOL_TIP_TEXT = ACTION_TEXT;
    private static final ImageDescriptor ACTION_IMAGE_DESCRIPTOR = EclipseCoreActivator
            .getManagedImageDescriptor(EclipseCoreActivator.IMAGE_KEY_ACTION_TABLE_EDIT);

    /**
     * TODO: Comment.
     * 
     */
    public TableEditAction() {
        this(InputType.STRUCTURED_SELECTION);
    }

    /**
     * TODO: Comment.
     * 
     */
    public TableEditAction(InputType inputType) {
        super(inputType);
        setText(ACTION_TEXT);
        setToolTipText(ACTION_TOOL_TIP_TEXT);
        setImageDescriptor(ACTION_IMAGE_DESCRIPTOR);
        addInputTypeClass(ZnodeModel.class);
    }

    @Override
    public void runWithStructuredSelection(IStructuredSelection selection) {
        Collection<ZnodeModel> modelSet = getModels(selection);
        ZnodeModelCollectionEditorInput input = new ZnodeModelCollectionEditorInput(ZnodeModelTableEditor.ID, modelSet);

        try {
            openEditor(input, ZnodeModelTableEditor.ID, true);
        }
        catch (Exception e) {
            e.printStackTrace();
            openErrorMessageDialog(e);
        }
    }

    @SuppressWarnings("unchecked")
    protected Collection<ZnodeModel> getModels(IStructuredSelection selection) {
        return selection.toList();
    }

}
