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
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.fusesource.ide.zk.core.EclipseCoreActivator;
import org.fusesource.ide.zk.core.model.DataModel;


/**
 * Base class for "Delete" actions.
 * 
 * @author Mark Masse
 */
public abstract class BaseDeleteAction extends BaseAction {

    private static final ImageDescriptor ACTION_IMAGE_DESCRIPTOR = EclipseCoreActivator
            .getManagedImageDescriptor(EclipseCoreActivator.IMAGE_KEY_ACTION_DELETE);

    private static final String ACTION_TEXT = "Delete";
    private static final String ACTION_TOOL_TIP_TEXT = ACTION_TEXT;

    public BaseDeleteAction() {
        this(InputType.STRUCTURED_SELECTION);
    }

    public BaseDeleteAction(InputType inputType) {
        super(inputType);
        setImageDescriptor(ACTION_IMAGE_DESCRIPTOR);
        setText(ACTION_TEXT);
        setToolTipText(ACTION_TOOL_TIP_TEXT);
    }

    @Override
    public void runWithObject(Object object) throws Exception {
        String typeName = getObjectTypeName(object);
        if (typeName == null) {
            return;
        }

        String name = getObjectName(object);
        if (name == null) {
            return;
        }

        MessageBox messageBox = new MessageBox(getActiveShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
        messageBox.setMessage("Are you sure you want to delete the " + typeName + " '" + name + "'?");
        messageBox.setText("Confirm Delete");
        int response = messageBox.open();
        if (response == SWT.YES) {
            try {
                delete(object);
            }
            catch (Exception e) {
                throw new Exception("Failed to delete the " + typeName + " '" + name + "'.", e);
            }
        }
    }

    protected void delete(Object object) throws Exception {
        if (object instanceof DataModel<?, ?, ?>) {
            ((DataModel<?, ?, ?>) object).deleteData();
        }
    }

    protected abstract String getObjectName(Object object);

    protected abstract String getObjectTypeName(Object object);
}
