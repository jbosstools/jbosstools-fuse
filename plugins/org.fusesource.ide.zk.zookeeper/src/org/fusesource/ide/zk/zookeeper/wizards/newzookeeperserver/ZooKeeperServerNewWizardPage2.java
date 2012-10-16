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

package org.fusesource.ide.zk.zookeeper.wizards.newzookeeperserver;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.fusesource.ide.zk.core.widgets.grid.GridComposite;
import org.fusesource.ide.zk.core.widgets.grid.GridCompositeStatus;
import org.fusesource.ide.zk.core.widgets.grid.GridTextInput;
import org.fusesource.ide.zk.core.wizards.GridWizardPage;

import java.net.MalformedURLException;

import javax.management.remote.JMXServiceURL;


/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public class ZooKeeperServerNewWizardPage2 extends GridWizardPage {

    public static final String CONTROL_NAME_ENABLED_BUTTON = "Enabled";
    public static final String CONTROL_NAME_JMX_URL_TEXT = "JMX URL";
    public static final String CONTROL_NAME_USER_NAME_TEXT = "User Name";
    public static final String CONTROL_NAME_PASSWORD_TEXT = "Password";

    /**
     * TODO: Comment.
     * 
     * @param wizard
     */
    public ZooKeeperServerNewWizardPage2(ZooKeeperServerNewWizard wizard) {
        super(wizard);
    }

    public boolean isJmxEnabled() {
        Button enableCheckBox = (Button) getGridComposite().getControl(CONTROL_NAME_ENABLED_BUTTON);
        return enableCheckBox.getSelection();
    }

    public JMXServiceURL getServiceUrl() {
        Text jmxUrlText = (Text) getGridComposite().getControl(CONTROL_NAME_JMX_URL_TEXT);
        String jmxServiceUrlString = jmxUrlText.getText();

        try {
            return new JMXServiceURL(jmxServiceUrlString);
        }
        catch (MalformedURLException e) {
            // Validation should ensure that this should never happen
            return null;
        }
    }

    public String getUserName() {
        Text userNameText = (Text) getGridComposite().getControl(CONTROL_NAME_USER_NAME_TEXT);
        String userName = userNameText.getText().trim();
        if (userName.isEmpty()) {
            return null;
        }
        return userName;
    }

    public String getPassword() {
        Text passwordText = (Text) getGridComposite().getControl(CONTROL_NAME_PASSWORD_TEXT);
        String password = passwordText.getText();
        if (password.isEmpty()) {
            return null;
        }
        return password;
    }

    @Override
    protected GridComposite createGridComposite(Composite parent) {

        GridComposite gridComposite = new GridComposite(parent) {

            @Override
            protected void createContents() {

                final Button enableCheckBox = new Button(this, SWT.CHECK);
                enableCheckBox.setText("Establish JMX connection");
                enableCheckBox.setSelection(false);
                enableCheckBox.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false, 2, 1));
                addControl(CONTROL_NAME_ENABLED_BUTTON, enableCheckBox);

                final GridTextInput jmxUrlGridTextInput = new GridTextInput(this, GridTextInput.Type.VALUE_REQUIRED,
                        CONTROL_NAME_JMX_URL_TEXT, "&Service URL:", null);
                addGridTextInput(jmxUrlGridTextInput);

                final GridTextInput userNameGridTextInput = new GridTextInput(this, GridTextInput.Type.DEFAULT,
                        CONTROL_NAME_USER_NAME_TEXT, "&User Name:", null);
                addGridTextInput(userNameGridTextInput);

                // HACK: Add spaces after password label text to force the layout to not crop the previous, longer
                // labels.
                final GridTextInput passwordGridTextInput = new GridTextInput(this, GridTextInput.Type.DEFAULT,
                        CONTROL_NAME_PASSWORD_TEXT, "&Password:           ", null);
                addGridTextInput(passwordGridTextInput);
                passwordGridTextInput.getText().setEchoChar('*');

                final Label passwordLabel = new Label(this, SWT.LEFT | SWT.WRAP);
                passwordLabel.setText("NOTE:  Password will be stored unencrypted in a file.");
                passwordLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false, 2, 1));

                SelectionAdapter enableCheckBoxSelectionAdapter = new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        boolean enabled = enableCheckBox.getSelection();
                        jmxUrlGridTextInput.setEnabled(enabled);
                        userNameGridTextInput.setEnabled(enabled);
                        passwordGridTextInput.setEnabled(enabled);
                        passwordLabel.setEnabled(enabled);
                        modified(enableCheckBox);
                    }

                };

                enableCheckBox.addSelectionListener(enableCheckBoxSelectionAdapter);
                enableCheckBoxSelectionAdapter.widgetSelected(null);

            }

            @Override
            protected GridCompositeStatus updateStatus(Object source) {

                Button enableCheckBox = (Button) getControl(CONTROL_NAME_ENABLED_BUTTON);
                if (!enableCheckBox.getSelection()) {
                    return GridCompositeStatus.OK_STATUS;
                }

                GridCompositeStatus status = super.updateStatus(source);
                if (status.getType().isError()) {
                    return status;
                }

                String message;

                if (source instanceof GridTextInput) {

                    GridTextInput gridTextInput = (GridTextInput) source;

                    if (gridTextInput.getName().equals(CONTROL_NAME_JMX_URL_TEXT)) {
                        String jmxUrlString = gridTextInput.getText().getText().trim();

                        try {
                            new JMXServiceURL(jmxUrlString);
                        }
                        catch (MalformedURLException e) {
                            message = e.getMessage();
                            return new GridCompositeStatus(CONTROL_NAME_JMX_URL_TEXT, message,
                                    GridCompositeStatus.Type.ERROR_INVALID);

                        }
                    }

                }

                return GridCompositeStatus.OK_STATUS;
            }

        };

        return gridComposite;
    }
}