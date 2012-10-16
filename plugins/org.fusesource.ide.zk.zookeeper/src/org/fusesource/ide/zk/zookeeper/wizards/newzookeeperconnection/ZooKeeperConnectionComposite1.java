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

package org.fusesource.ide.zk.zookeeper.wizards.newzookeeperconnection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.fusesource.ide.zk.zookeeper.ZooKeeperActivator;
import org.fusesource.ide.zk.zookeeper.data.Znode;
import org.fusesource.ide.zk.zookeeper.data.ZooKeeperConnectionDescriptor;
import org.fusesource.ide.zk.zookeeper.runtime.ZooKeeperConnectionDescriptorFiles;
import org.fusesource.ide.zk.zookeeper.widgets.ZooKeeperConnectionServerComposite;
import org.fusesource.ide.zk.zookeeper.widgets.OrchestrationComposite.IOrchestrationCompositeListener;
import org.fusesource.ide.zk.core.widgets.grid.GridComposite;
import org.fusesource.ide.zk.core.widgets.grid.GridCompositeStatus;
import org.fusesource.ide.zk.core.widgets.grid.GridTextInput;

import java.util.EventObject;
import java.util.HashSet;
import java.util.Set;


/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public class ZooKeeperConnectionComposite1 extends GridComposite {

    private static final String CONTROL_NAME_CONNECT_STRING_VALUE_LABEL = "Connect String";
    private static final String CONTROL_NAME_NAME_TEXT = "Name";
    private static final String CONTROL_NAME_ROOT_PATH_TEXT = "Root Path";
    private static final String CONTROL_NAME_SERVERS_TABLE = "Servers";
    private static final String CONTROL_NAME_SESSION_TIMEOUT_TEXT = "Session Timeout";

    private final Set<String> _ZooKeeperConnectionNames;
    private ZooKeeperConnectionServerComposite _ServerComposite;

    /**
     * TODO: Comment.
     * 
     * @param parent
     */
    public ZooKeeperConnectionComposite1(Composite parent) {
        super(parent);

        ZooKeeperConnectionDescriptorFiles files = ZooKeeperActivator.getDefault()
                .getZooKeeperConnectionDescriptorFiles();

        _ZooKeeperConnectionNames = new HashSet<String>(files.getNames());
    }

    public ZooKeeperConnectionDescriptor getConnectionDescriptor() {

        Text nameText = (Text) getControl(CONTROL_NAME_NAME_TEXT);
        Text sessionTimeoutText = (Text) getControl(CONTROL_NAME_SESSION_TIMEOUT_TEXT);

        String name = nameText.getText();
        int sessionTimeout = Integer.parseInt(sessionTimeoutText.getText());

        ZooKeeperConnectionDescriptor connection = new ZooKeeperConnectionDescriptor(name, sessionTimeout);

        connection.getServers().addAll(_ServerComposite.getElementList());
        connection.setRootPath(getRootPath());
        return connection;
    }

    @Override
    protected void createContents() {

        GridTextInput nameGridTextInput = new GridTextInput(this, GridTextInput.Type.VALUE_REQUIRED,
                CONTROL_NAME_NAME_TEXT, "&Name:", ZooKeeperConnectionDescriptor.DEFAULT_NAME);
        addGridTextInput(nameGridTextInput);
        Text nameText = nameGridTextInput.getText();
        nameText.selectAll();
        nameText.setTextLimit(ZooKeeperConnectionDescriptor.NAME_LENGTH_LIMIT);

        GridTextInput rootPathTextInput = new GridTextInput(this, GridTextInput.Type.DEFAULT,
                CONTROL_NAME_ROOT_PATH_TEXT, "&Root Path:", null);
        addGridTextInput(rootPathTextInput);

        GridTextInput sessionTimeoutGridTextInput = new GridTextInput(this, GridTextInput.Type.INTEGER_VALUE_REQUIRED,
                CONTROL_NAME_SESSION_TIMEOUT_TEXT, "Session &Timeout:", String
                        .valueOf(ZooKeeperConnectionDescriptor.DEFAULT_SESSION_TIMEOUT));
        addGridTextInput(sessionTimeoutGridTextInput);

        final Group serversGroup = new Group(this, SWT.NULL);
        serversGroup.setText("Servers");
        serversGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

        GridLayout serversGroupLayout = new GridLayout(getNumColumns(), false);
        int margin = getMargin();
        serversGroupLayout.horizontalSpacing = margin;
        serversGroupLayout.verticalSpacing = margin;
        serversGroupLayout.marginWidth = margin;
        serversGroupLayout.marginHeight = margin;

        serversGroup.setLayout(serversGroupLayout);

        _ServerComposite = new ZooKeeperConnectionServerComposite(serversGroup, SWT.NULL);
        _ServerComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        GridLayout serverCompositeLayout = new GridLayout(2, false);
        serverCompositeLayout.marginWidth = 0;
        serverCompositeLayout.marginHeight = 0;
        serverCompositeLayout.horizontalSpacing = ((GridLayout) getLayout()).horizontalSpacing;
        _ServerComposite.setLayout(serverCompositeLayout);

        final Table table = new Table(_ServerComposite, SWT.SINGLE | SWT.FULL_SELECTION | SWT.BORDER | SWT.H_SCROLL
                | SWT.V_SCROLL);

        GridData tableLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2);
        tableLayoutData.heightHint = 100;
        table.setLayoutData(tableLayoutData);

        addControl(CONTROL_NAME_SERVERS_TABLE, table);
        addControlDecoration(CONTROL_NAME_SERVERS_TABLE, table, SWT.LEFT | SWT.TOP, _ServerComposite);
        _ServerComposite.setTable(table);

        Button addServerButton = new Button(_ServerComposite, SWT.NULL);
        addServerButton.setText("Add...");
        addServerButton.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, false, false));
        _ServerComposite.setAddButton(addServerButton);

        Button removeServerButton = new Button(_ServerComposite, SWT.NULL);
        removeServerButton.setText("Remove");
        removeServerButton.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, false, false));
        _ServerComposite.setRemoveButton(removeServerButton);

        _ServerComposite.addOrchestrationCompositeListener(new IOrchestrationCompositeListener() {

            @Override
            public void orchestrationChange(EventObject e) {
                connectStringChanged();
                modified(table);

            }
        });

        _ServerComposite.init();
        
        Label connectStringLabel = new Label(this, SWT.LEAD);
        connectStringLabel.setText("Connect String:   ");
        connectStringLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));

        Label connectStringValueLabel = new Label(this, SWT.WRAP);
        connectStringValueLabel.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
        addControl(CONTROL_NAME_CONNECT_STRING_VALUE_LABEL, connectStringValueLabel);

        connectStringChanged();
    }

    protected String getRootPath() {
        Text rootPathText = (Text) getControl(CONTROL_NAME_ROOT_PATH_TEXT);
        String rootPath = rootPathText.getText();
        if (rootPath.trim().length() == 0) {
            rootPath = null;
        }
        return rootPath;
    }

    @Override
    protected GridCompositeStatus updateStatus(Object source) {
        GridCompositeStatus status = super.updateStatus(source);
        if (status.getType().isError()) {
            return status;
        }

        String message;

        if (source instanceof GridTextInput) {
            GridTextInput gridTextInput = (GridTextInput) source;
            if (gridTextInput.getName().equals(CONTROL_NAME_NAME_TEXT)) {

                Text nameText = gridTextInput.getText();
                String name = nameText.getText();
                if (_ZooKeeperConnectionNames.contains(name)) {
                    message = "Name must be unique.";
                    return new GridCompositeStatus(CONTROL_NAME_NAME_TEXT, message,
                            GridCompositeStatus.Type.ERROR_INVALID);
                }
            }
            else if (gridTextInput.getName().equals(CONTROL_NAME_ROOT_PATH_TEXT)) {

                String rootPath = getRootPath();
                if (rootPath != null) {
                    try {
                        Znode.validatePath(rootPath, false);
                    }
                    catch (IllegalArgumentException e) {
                        message = e.getMessage();
                        return new GridCompositeStatus(CONTROL_NAME_ROOT_PATH_TEXT, message,
                                GridCompositeStatus.Type.ERROR_INVALID);
                    }
                }
            }
        }
        else if (source instanceof Table) {
            Table serversTable = (Table) source;
            if (serversTable.getItemCount() == 0) {
                message = "At least one server must be added.";
                return new GridCompositeStatus(CONTROL_NAME_SERVERS_TABLE, message,
                        GridCompositeStatus.Type.ERROR_REQUIRED);
            }
        }

        return GridCompositeStatus.OK_STATUS;
    }

    private void connectStringChanged() {

        String connectString = ZooKeeperConnectionDescriptor.buildConnectString(_ServerComposite.getElementSet(),
                getRootPath());
        connectString = (connectString != null) ? connectString : "";
        Label connectStringValueLabel = (Label) getControl(CONTROL_NAME_CONNECT_STRING_VALUE_LABEL);
        connectStringValueLabel.setText(connectString);
    }

}
