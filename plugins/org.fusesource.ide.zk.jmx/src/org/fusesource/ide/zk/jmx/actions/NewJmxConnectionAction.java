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

package org.fusesource.ide.zk.jmx.actions;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWizard;
import org.fusesource.ide.zk.jmx.JmxActivator;
import org.fusesource.ide.zk.jmx.wizards.newjmxconnection.JmxConnectionNewWizard;
import org.fusesource.ide.zk.core.actions.BaseWizardAction;


/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public class NewJmxConnectionAction extends BaseWizardAction {

    private static final ImageDescriptor ACTION_IMAGE_DESCRIPTOR = JmxActivator
            .getManagedImageDescriptor(JmxActivator.IMAGE_KEY_ACTION_NEW_JMX_CONNECTION);
    private static final String ACTION_TEXT = "New JMX Connection...";
    private static final String ACTION_TOOL_TIP_TEXT = ACTION_TEXT;

    /**
     * TODO: Comment.
     * 
     */
    public NewJmxConnectionAction() {
        super(InputType.NONE);
        setText(ACTION_TEXT);
        setToolTipText(ACTION_TOOL_TIP_TEXT);
        setImageDescriptor(ACTION_IMAGE_DESCRIPTOR);
    }

    @Override
    public void runWithNothing() {
        runWithStructuredSelection(getCurrentStructuredSelection());
    }

    @Override
    protected IWorkbenchWizard getWizard(IStructuredSelection selection) {
        return new JmxConnectionNewWizard();
    }

}
