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

package org.fusesource.ide.zk.zookeeper.editors.znodeform;


import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.fusesource.ide.zk.zookeeper.model.ZnodeModel;
import org.fusesource.ide.zk.core.editors.DataModelFormPage;

/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public abstract class BaseZnodeModelFormPage extends DataModelFormPage<ZnodeModel> {

    protected static final String EXTERNAL_MODIFICATION_INFO_TEXT = "The Znode has been modified outside of this editor.  Do you want to reload it?";

    private Label _ToolBarLabel;

    /**
     * TODO: Comment.
     * 
     * @param editor
     * @param id
     * @param title
     */
    public BaseZnodeModelFormPage(ZnodeModelFormEditor editor, String id, String title, Image image) {
        super(editor, id, title);
        setImage(image);
    }

    @Override
    protected void contributeToToolBar(IToolBarManager toolBarManager) {
        super.contributeToToolBar(toolBarManager);

        ControlContribution toolBarCompositeContribution = new ControlContribution(getToolBarCompositeContributionId()) {

            @Override
            protected Control createControl(Composite parent) {

                FormToolkit toolkit = getManagedForm().getToolkit();
                Composite toolBarComposite = toolkit.createComposite(parent);
                FormLayout toolBarCompositeLayout = new FormLayout();
                toolBarCompositeLayout.marginTop = 0;
                toolBarCompositeLayout.marginBottom = 0;
                toolBarCompositeLayout.marginLeft = 4;
                toolBarCompositeLayout.marginRight = 4;
                toolBarCompositeLayout.spacing = 4;
                toolBarComposite.setLayout(toolBarCompositeLayout);

                _ToolBarLabel = toolkit.createLabel(toolBarComposite, "", SWT.RIGHT);

                FormData toolBarLabelFormData = new FormData();
                toolBarLabelFormData.top = new FormAttachment(0, 0);
                toolBarLabelFormData.left = new FormAttachment(0, 0);
                toolBarLabelFormData.right = new FormAttachment(100, 0);

                // HACK: I really struggled to get this label to show up.
                toolBarLabelFormData.width = 100;

                _ToolBarLabel.setLayoutData(toolBarLabelFormData);

                return toolBarComposite;
            }
        };

        toolBarManager.add(toolBarCompositeContribution);

    }

    protected void setToolbarLabelText(String text, String toolTipText) {
        _ToolBarLabel.setText(text);
        _ToolBarLabel.setToolTipText(toolTipText);
        _ToolBarLabel.pack(true);
        _ToolBarLabel.getParent().layout(true);
    }

    private String getToolBarCompositeContributionId() {
        return getId() + " - ToolBar Contribution";
    }

}
