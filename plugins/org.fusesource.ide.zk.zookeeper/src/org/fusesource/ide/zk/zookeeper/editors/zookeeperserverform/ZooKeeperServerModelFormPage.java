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

package org.fusesource.ide.zk.zookeeper.editors.zookeeperserverform;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.HyperlinkGroup;
import org.eclipse.ui.forms.HyperlinkSettings;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.fusesource.ide.zk.zookeeper.data.ZooKeeperServer;
import org.fusesource.ide.zk.zookeeper.model.ZooKeeperServerModel;
import org.fusesource.ide.zk.core.EclipseCoreActivator;
import org.fusesource.ide.zk.core.actions.BaseOpenAction;
import org.fusesource.ide.zk.core.editors.DataModelFormPage;
import org.fusesource.ide.zk.core.widgets.ElementTypeDataModelImageHyperlinkView;
import org.fusesource.ide.zk.jmx.model.JmxConnectionModel;
import org.fusesource.ide.zk.jmx.viewers.JmxConnectionModelElementType;


/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public final class ZooKeeperServerModelFormPage extends DataModelFormPage<ZooKeeperServerModel> {

    public static final String ID = ZooKeeperServerModelFormPage.class.getName();
    public static final Image IMAGE = EclipseCoreActivator
            .getManagedImage(EclipseCoreActivator.IMAGE_KEY_OBJECT_SERVER_ADMIN);
    public static final String TITLE = "Administration";

    /**
     * TODO: Comment.
     * 
     * @param editor
     * @param id
     * @param title
     */
    public ZooKeeperServerModelFormPage(ZooKeeperServerModelFormEditor editor) {
        super(editor, ID, TITLE);
        setImage(IMAGE);
    }

    @Override
    protected void createModelFormContent(IManagedForm managedForm, Composite client) {

        FormToolkit toolkit = managedForm.getToolkit();

        Label commandLabel = toolkit.createLabel(client, "Command: ");
        final CCombo commandsCombo = new CCombo(client, SWT.BORDER | SWT.READ_ONLY);
        for (String command : ZooKeeperServer.COMMANDS) {
            commandsCombo.add(command);
        }
        commandsCombo.setText(ZooKeeperServer.COMMAND_RUOK);

        toolkit.adapt(commandsCombo, true, false);
        final Button executeButton = toolkit.createButton(client, "Execute", SWT.PUSH);

        executeButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                executeCommand(commandsCombo.getText());
            }
        });

        Label jmxConnectionLabel = null;
        ImageHyperlink jmxConnectionImageHyperlink = null;
        final JmxConnectionModel jmxConnectionModel = getModel().getJmxConnectionModel();
        if (jmxConnectionModel != null) {

            jmxConnectionLabel = toolkit.createLabel(client, "JMX Connection: ");

            final JmxConnectionModelElementType jmxConnectionModelElementType = new JmxConnectionModelElementType();
            jmxConnectionImageHyperlink = toolkit.createImageHyperlink(client, SWT.TOP | SWT.WRAP);
            HyperlinkGroup group = new HyperlinkGroup(jmxConnectionImageHyperlink.getDisplay());
            group.setHyperlinkUnderlineMode(HyperlinkSettings.UNDERLINE_HOVER);
            group.add(jmxConnectionImageHyperlink);

            jmxConnectionImageHyperlink.addHyperlinkListener(new HyperlinkAdapter() {

                @Override
                public void linkActivated(HyperlinkEvent e) {
                    BaseOpenAction openAction = jmxConnectionModelElementType.getOpenAction();
                    if (openAction != null) {

                        try {
                            openAction.runWithObject(jmxConnectionModel);
                        }
                        catch (Exception e1) {
                            // TODO: Log?
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                    }
                }
            });

            ElementTypeDataModelImageHyperlinkView view = new ElementTypeDataModelImageHyperlinkView(
                    jmxConnectionModel, jmxConnectionImageHyperlink, jmxConnectionModelElementType);
            view.updateView();
        }

        FormData commandsLabelFormData = new FormData();
        commandsLabelFormData.top = new FormAttachment(commandsCombo, 0, SWT.CENTER);
        commandsLabelFormData.left = new FormAttachment(0, 0);
        commandLabel.setLayoutData(commandsLabelFormData);

        FormData commandsComboFormData = new FormData();
        commandsComboFormData.top = new FormAttachment(0, 0);
        commandsComboFormData.left = new FormAttachment(commandLabel);
        commandsComboFormData.right = new FormAttachment(executeButton, 0, SWT.LEFT);
        commandsCombo.setLayoutData(commandsComboFormData);

        FormData executeButtonFormData = new FormData();
        executeButtonFormData.top = new FormAttachment(commandsCombo, 0, SWT.CENTER);
        executeButtonFormData.right = new FormAttachment(100, 0);
        executeButton.setLayoutData(executeButtonFormData);

        if (jmxConnectionImageHyperlink != null) {

            FormData jmxConnectionLabelFormData = new FormData();
            jmxConnectionLabelFormData.top = new FormAttachment(commandLabel, 10);
            jmxConnectionLabelFormData.left = new FormAttachment(0, 0);
            jmxConnectionLabel.setLayoutData(jmxConnectionLabelFormData);

            FormData jmxConnectionImageHyperlinkFormData = new FormData();
            jmxConnectionImageHyperlinkFormData.top = new FormAttachment(jmxConnectionLabel, 0, SWT.CENTER);
            jmxConnectionImageHyperlinkFormData.left = new FormAttachment(jmxConnectionLabel);
            jmxConnectionImageHyperlinkFormData.right = new FormAttachment(100, 0);
            jmxConnectionImageHyperlink.setLayoutData(jmxConnectionImageHyperlinkFormData);
        }

    }

    private void executeCommand(String command) {

        ZooKeeperServer server = getModel().getData();

        String result = null;

        if (command.equals(ZooKeeperServer.COMMAND_DUMP)) {
            result = server.dump();
        }
        else if (command.equals(ZooKeeperServer.COMMAND_GET_TRACE_MASK)) {
            result = server.getTraceMask();
        }
        else if (command.equals(ZooKeeperServer.COMMAND_RUOK)) {
            result = server.ruok();
        }
        else if (command.equals(ZooKeeperServer.COMMAND_STAT)) {
            result = server.stat();
        }
        else {
            MessageDialog.openError(getSite().getShell(), "Bad command", "Command '" + command + "' not supported.");
            return;
        }

        if (result == null) {
            result = "<Command '" + command + "' returned no result>";
        }

        new CommandResultDialog(command, result).open();

    }

    @Override
    protected void initFromModelInternal() {
    }

    private class CommandResultDialog extends Dialog {

        private final String _Command;
        private final String _Result;

        protected CommandResultDialog(String command, String result) {
            super(getSite().getShell());
            _Command = command;
            _Result = result;
            setBlockOnOpen(true);
        }

        @Override
        protected void createButtonsForButtonBar(Composite parent) {
            createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        }

        @Override
        protected Control createContents(Composite parent) {

            this.getShell().setText(_Command);

            ScrolledComposite scrolledComposite = new ScrolledComposite(parent, SWT.V_SCROLL);
            scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            scrolledComposite.setExpandHorizontal(true);
            Text text = new Text(scrolledComposite, SWT.MULTI | SWT.READ_ONLY | SWT.WRAP);
            text.setText(_Result);
            text.pack();
            scrolledComposite.setContent(text);

            return scrolledComposite;
        }

        @Override
        protected boolean isResizable() {
            return true;
        }

        @Override
        protected Point getInitialSize() {
            return new Point(600, 300);
        }

    }

}
