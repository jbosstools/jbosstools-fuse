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

package org.fusesource.ide.zk.zookeeper.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.fusesource.ide.zk.zookeeper.ZooKeeperActivator;
import org.fusesource.ide.zk.zookeeper.data.AuthInfo;
import org.fusesource.ide.zk.zookeeper.data.ZooKeeperConnectionDescriptor;
import org.fusesource.ide.zk.core.dialogs.GridDialog;
import org.fusesource.ide.zk.core.widgets.grid.GridComposite;
import org.fusesource.ide.zk.core.widgets.grid.GridTextInput;


/**
 * Dialog used to add AuthInfo to a {@link ZooKeeperConnectionDescriptor}.
 * 
 * @author Mark Masse
 */
public class AddAuthInfoDialog extends GridDialog {

    private static final String CONTROL_NAME_AUTH_STRING_TEXT = "Auth";
    private static final String CONTROL_NAME_SCHEME_TEXT = "Scheme";
    private static final String CONTROL_NAME_TYPE_COMBO = "Type";
    private static final String MESSAGE = "Add authentication information to the ZooKeeper connection.";
    private static final String TITLE = "Add Authentication Information";

    private AuthInfo _AuthInfo;

    /**
     * Constructor.
     * 
     * @param parentShell The parent SWT {@link Shell}.
     */
    public AddAuthInfoDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    public void create() {
        super.create();

        setTitle(TITLE);
        setTitleImage(ZooKeeperActivator.getManagedImage(ZooKeeperActivator.IMAGE_KEY_DIALOG_ADD_AUTH_INFO));
        setMessage(MESSAGE);
        Shell shell = getShell();
        shell.setText(TITLE);

    }

    /**
     * Returns the {@link AuthInfo} created by this dialog or <code>null</code> if the dialog was canceled.
     *
     * @return The {@link AuthInfo} created by this dialog.
     */
    public AuthInfo getAuthInfo() {
        return _AuthInfo;
    }

    @Override
    protected GridComposite createGridComposite(Composite parent) {

        GridComposite gridComposite = new GridComposite(parent) {

            @Override
            public void init() {
                setNumColumns(3);
                super.init();
                Text schemeText = (Text) getControl(CONTROL_NAME_SCHEME_TEXT);
                schemeText.forceFocus();
            }

            @Override
            protected void createContents() {

                Label typeLabel = new Label(this, SWT.LEAD);
                typeLabel.setText("&Type:   ");
                typeLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));

                final CCombo typeCombo = new CCombo(this, SWT.BORDER | SWT.READ_ONLY);
                typeCombo.add(AuthInfo.Type.Text.name());
                typeCombo.add(AuthInfo.Type.File.name());
                typeCombo.select(0);
                typeCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
                addControl(CONTROL_NAME_TYPE_COMBO, typeCombo);

                GridTextInput schemeGridTextInput = new GridTextInput(this, GridTextInput.Type.VALUE_REQUIRED,
                        CONTROL_NAME_SCHEME_TEXT, "&Scheme: ", null, 2);
                addGridTextInput(schemeGridTextInput);
                schemeGridTextInput.getText().setTextLimit(250);

                GridTextInput authStringGridTextInput = new GridTextInput(this, GridTextInput.Type.VALUE_REQUIRED,
                        CONTROL_NAME_AUTH_STRING_TEXT, "&Auth:     ", null);
                addGridTextInput(authStringGridTextInput);

                final Button browseButton = new Button(this, SWT.PUSH);
                browseButton.setText("&Browse...");
                browseButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));

                final Button showPasswordTextCheckBox = new Button(this, SWT.CHECK);
                showPasswordTextCheckBox.setText("Show auth text");
                showPasswordTextCheckBox.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
                showPasswordTextCheckBox.addSelectionListener(new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        Text authStringText = (Text) getControl(CONTROL_NAME_AUTH_STRING_TEXT);
                        char echoChar = (showPasswordTextCheckBox.getSelection()) ? '\0' : '*';
                        authStringText.setEchoChar(echoChar);
                    }

                });

                final Label noteLabel = new Label(this, SWT.LEFT | SWT.WRAP);
                noteLabel.setText("NOTE:  Authentication information will be stored unencrypted in a file");
                noteLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

                browseButton.addSelectionListener(new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        Text authStringText = (Text) getControl(CONTROL_NAME_AUTH_STRING_TEXT);

                        FileDialog fileDialog = new FileDialog(getShell());
                        fileDialog.setFileName(authStringText.getText());
                        String newPath = fileDialog.open();
                        if (newPath != null) {
                            authStringText.setText(newPath);
                        }
                    }

                });

                SelectionListener typeComboSelectionListener = new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent e) {

                        Text authStringText = (Text) getControl(CONTROL_NAME_AUTH_STRING_TEXT);
                        authStringText.setText("");

                        String typeString = typeCombo.getText();
                        AuthInfo.Type type = AuthInfo.Type.valueOf(typeString);

                        boolean isFileType = (type == AuthInfo.Type.File);
                        browseButton.setVisible(isFileType);

                        showPasswordTextCheckBox.setVisible(!isFileType);
                        noteLabel.setVisible(!isFileType);

                        char echoChar = (isFileType || showPasswordTextCheckBox.getSelection()) ? '\0' : '*';
                        authStringText.setEchoChar(echoChar);

                    }

                };

                typeCombo.addSelectionListener(typeComboSelectionListener);
                typeComboSelectionListener.widgetSelected(null);
            }

        };

        return gridComposite;
    }

    @Override
    protected void okPressed() {
        GridComposite gridComposite = getGridComposite();
        CCombo typeCombo = (CCombo) gridComposite.getControl(CONTROL_NAME_TYPE_COMBO);
        Text schemeText = (Text) gridComposite.getControl(CONTROL_NAME_SCHEME_TEXT);
        Text authStringText = (Text) gridComposite.getControl(CONTROL_NAME_AUTH_STRING_TEXT);

        AuthInfo.Type type = AuthInfo.Type.valueOf(typeCombo.getText());
        String scheme = schemeText.getText();
        String authString = authStringText.getText();

        _AuthInfo = new AuthInfo(type, scheme, authString);
        super.okPressed();
    }

}
