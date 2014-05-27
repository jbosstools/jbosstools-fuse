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


import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchWizard;
import org.fusesource.ide.zk.core.editors.DataModelEditorInput;
import org.fusesource.ide.zk.core.model.DataModel;

/**
 * Base class for actions that open a {@link WizardDialog} with a {@link IWorkbenchWizard}.
 * 
 * @author Mark Masse
 */
public abstract class BaseWizardAction extends BaseAction {

    public BaseWizardAction(InputType inputType) {
        super(inputType);
    }

    @Override
    public void runWithStructuredSelection(IStructuredSelection selection) {
        IWorkbenchWizard wizard = getWizard(selection);
        wizard.init(getWorkbench(), selection);
        WizardDialog wizardDialog = new WizardDialog(getActiveShell(), wizard);

        wizardDialog.setBlockOnOpen(true);
        wizardDialog.open();
    }

    @Override
    public void runWithEditorInput(IEditorInput editorInput) {
        if (editorInput instanceof DataModelEditorInput<?>) {
            DataModel<?, ?, ?> dataModel = ((DataModelEditorInput<?>) editorInput).getModel();
            runWithStructuredSelection(new StructuredSelection(dataModel));
        }
    }

    /**
     * Subclasses must override to return the specific {@link IWorkbenchWizard}.
     * 
     * @param selection The {@link IStructuredSelection} that can be used to initialize the wizard.
     * @return The {@link IWorkbenchWizard}.
     */
    protected abstract IWorkbenchWizard getWizard(IStructuredSelection selection);

}
