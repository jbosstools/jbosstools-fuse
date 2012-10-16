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

package org.fusesource.ide.zk.core.actions;

import org.eclipse.jface.resource.ImageDescriptor;
import org.fusesource.ide.zk.core.EclipseCoreActivator;
import org.fusesource.ide.zk.core.editors.DataModelEditorInput;
import org.fusesource.ide.zk.core.model.DataModel;


/**
 * Implementation of the "Refresh" action that assumes the input is a {@link DataModel}
 * 
 * @author Mark Masse
 */
public class RefreshAction extends BaseAction {

    private static final String ACTION_TEXT = "Refresh";
    private static final String ACTION_TOOL_TIP_TEXT = ACTION_TEXT;

    private static final ImageDescriptor ACTION_IMAGE_DESCRIPTOR = EclipseCoreActivator
            .getManagedImageDescriptor(EclipseCoreActivator.IMAGE_KEY_ACTION_REFRESH);

    public RefreshAction(InputType inputType) {
        super(inputType);
        setText(ACTION_TEXT);
        setToolTipText(ACTION_TOOL_TIP_TEXT);
        setImageDescriptor(ACTION_IMAGE_DESCRIPTOR);
        addInputTypeClass(DataModel.class);
        addInputTypeClass(DataModelEditorInput.class);
    }

    @Override
    public void runWithObject(Object object) throws Exception {

        DataModel<?, ?, ?> dataModel = null;

        if (object instanceof DataModel<?, ?, ?>) {
            dataModel = (DataModel<?, ?, ?>) object;
        }
        else if (object instanceof DataModelEditorInput<?>) {
            dataModel = ((DataModelEditorInput<?>) object).getModel();
        }

        if (dataModel != null) {
            refreshModel(dataModel);
        }
    }

    protected void refreshModel(DataModel<?, ?, ?> dataModel) {
        if (!dataModel.isDestroyed()) {
            dataModel.refreshData();
        }
    }

}
