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

package org.fusesource.ide.zk.zookeeper.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.fusesource.ide.zk.zookeeper.data.Znode;
import org.fusesource.ide.zk.core.dialogs.GridDialog;
import org.fusesource.ide.zk.core.widgets.grid.GridComposite;
import org.fusesource.ide.zk.core.widgets.grid.GridTextInput;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;

/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public class ZnodeAclComposite extends TableOrchestrationComposite {

    public static final String ID_ANYONE = ZooDefs.Ids.ANYONE_ID_UNSAFE.getId();
    public static final String ID_AUTH = ZooDefs.Ids.AUTH_IDS.getId();

    public static final int[] PERMS = new int[] { ZooDefs.Perms.ALL, ZooDefs.Perms.ADMIN, ZooDefs.Perms.CREATE,
            ZooDefs.Perms.DELETE, ZooDefs.Perms.READ, ZooDefs.Perms.WRITE };

    public static final String SCHEME_AUTH = ZooDefs.Ids.AUTH_IDS.getScheme();
    public static final String SCHEME_DIGEST = "digest";
    public static final String SCHEME_IP = "ip";
    public static final String SCHEME_WORLD = ZooDefs.Ids.ANYONE_ID_UNSAFE.getScheme();
    public static final String[] SCHEMES = new String[] { SCHEME_WORLD, SCHEME_AUTH, SCHEME_DIGEST, SCHEME_IP };

    public static final int TABLE_COLUMN_ID = 1;
    public static final int TABLE_COLUMN_SCHEME = 0;
    public static final String[] TABLE_COLUMN_TITLES = new String[] { "Scheme", "Id", "ALL", "ADMIN", "CREATE",
            "DELETE", "READ", "WRITE" };
    public static final int[] TABLE_COLUMN_WIDTHS = new int[] { 100, 150, SWT.DEFAULT, SWT.DEFAULT, SWT.DEFAULT,
            SWT.DEFAULT, SWT.DEFAULT, SWT.DEFAULT };

    // private static final int LOCAL_IP_ADDRESS_SIZE;

    private static final String LOCAL_IP_ADDRESS;

    static {
        InetAddress address = null;
        try {
            address = InetAddress.getLocalHost();
        }
        catch (UnknownHostException e) {
        }

        String ipString = null;
        // int ipSize = -1;
        if (address != null) {
            ipString = address.getHostAddress();
            // ipSize = address.getAddress().length * 8;
        }

        LOCAL_IP_ADDRESS = ipString;
        // LOCAL_IP_ADDRESS_SIZE = ipSize;
    }

    public static int getPermissionColumnIndex(int zooDefPerm) {
        switch (zooDefPerm) {
        case ZooDefs.Perms.ALL:
            return 2;

        case ZooDefs.Perms.ADMIN:
            return 3;

        case ZooDefs.Perms.CREATE:
            return 4;

        case ZooDefs.Perms.DELETE:
            return 5;

        case ZooDefs.Perms.READ:
            return 6;

        case ZooDefs.Perms.WRITE:
            return 7;
        }

        return -1;
    }

    private static String base64Encode(byte b[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < b.length;) {
            int pad = 0;
            int v = (b[i++] & 0xff) << 16;
            if (i < b.length) {
                v |= (b[i++] & 0xff) << 8;
            }
            else {
                pad++;
            }
            if (i < b.length) {
                v |= (b[i++] & 0xff);
            }
            else {
                pad++;
            }
            sb.append(encode(v >> 18));
            sb.append(encode(v >> 12));
            if (pad < 2) {
                sb.append(encode(v >> 6));
            }
            else {
                sb.append('=');
            }
            if (pad < 1) {
                sb.append(encode(v));
            }
            else {
                sb.append('=');
            }
        }
        return sb.toString();
    }

    private static char encode(int i) {
        i &= 0x3f;
        if (i < 26) {
            return (char) ('A' + i);
        }
        if (i < 52) {
            return (char) ('a' + i - 26);
        }
        if (i < 62) {
            return (char) ('0' + i - 52);
        }
        return i == 62 ? '+' : '/';
    }

    private static String generateDigest(String idPassword) {
        String parts[] = idPassword.split(":", 2);
        byte digest[];
        try {
            digest = MessageDigest.getInstance("SHA1").digest(idPassword.getBytes());
            return parts[0] + ":" + base64Encode(digest);
        }
        catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return idPassword;
    }

    private TableEditor _IdTableEditor;
    private TableEditor _SchemeTableEditor;
    private Button _SetIdButton;

    /**
     * TODO: Comment.
     * 
     * @param parent
     * @param style
     */
    public ZnodeAclComposite(Composite parent, int style) {
        super(parent, style);
    }

    public TableItem addAclTableItem(ACL acl) {

        final Table table = getTable();

        final TableItem item = new TableItem(table, SWT.NONE);
        item.setData("ACL", acl);
        Id id = acl.getId();

        int aclPerms = acl.getPerms();
        boolean hasAll = ((aclPerms & ZooDefs.Perms.ALL) == ZooDefs.Perms.ALL);

        item.setText(0, id.getScheme());
        item.setText(1, id.getId());

        for (final int perm : PERMS) {
            final int permColumnIndex = getPermissionColumnIndex(perm);
            TableEditor permCheckBoxTableEditor = new TableEditor(table);
            setItemPermTableEditor(item, perm, permCheckBoxTableEditor);

            final Button permCheckBox = new Button(table, SWT.CHECK);

            boolean hasPerm = ((aclPerms & perm) == perm);
            permCheckBox.setSelection(hasPerm);
            permCheckBox.setEnabled(!hasAll || (hasAll && perm == ZooDefs.Perms.ALL));

            permCheckBox.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {

                    if (perm == ZooDefs.Perms.ALL) {
                        for (int subPerm : PERMS) {
                            if (subPerm == ZooDefs.Perms.ALL) {
                                continue;
                            }

                            Button subPermCheckBox = getItemPermCheckBox(item, subPerm);
                            boolean allIsSelected = permCheckBox.getSelection();
                            if (allIsSelected) {
                                subPermCheckBox.setSelection(true);
                            }
                            subPermCheckBox.setEnabled(!allIsSelected);
                        }
                    }

                    fireOrchestrationChange();
                }

            });

            permCheckBox.pack();
            permCheckBoxTableEditor.minimumWidth = permCheckBox.getSize().x;
            permCheckBoxTableEditor.horizontalAlignment = SWT.CENTER;
            permCheckBoxTableEditor.setEditor(permCheckBox, item, permColumnIndex);

        }

        return item;
    }

    /**
     * Returns the setIdButton.
     * 
     * @return The setIdButton
     */
    public Button getSetIdButton() {
        return _SetIdButton;
    }

    public List<ACL> getZnodeAclFromTable() {

        Table table = getTable();
        TableItem[] items = table.getItems();

        Set<ACL> aclSet = new HashSet<ACL>(items.length);
        for (TableItem item : items) {

            int perms = getItemPerms(item);
            Id id = getItemId(item);

            ACL acl = new ACL(perms, id);
            aclSet.add(acl);
        }
        return new ArrayList<ACL>(aclSet);
    }

    @Override
    public void init() {

        Table table = getTable();

        _SchemeTableEditor = new TableEditor(table);
        _SchemeTableEditor.horizontalAlignment = SWT.LEFT;
        _SchemeTableEditor.grabHorizontal = true;

        _IdTableEditor = new TableEditor(table);
        _IdTableEditor.horizontalAlignment = SWT.LEFT;
        _IdTableEditor.grabHorizontal = true;

        table.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                tableSelectionChanged((TableItem) e.item);
            }

        });

        Button addButton = getAddButton();
        addButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                addNewItem();
            }
        });

        Button removeButton = getRemoveButton();
        removeButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                removeSelectedItems();
            }
        });

        Button setIdButton = getSetIdButton();
        setIdButton.setEnabled(false);
        setIdButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                setId();
            }

        });

    }

    public void initTableItemsFromZnode(Znode znode) {

        Table table = getTable();
        removeItems(table.getItems());

        List<ACL> aclList = znode.getAcl();
        if (aclList == null || aclList.isEmpty()) {
            return;
        }

        for (ACL acl : aclList) {
            addAclTableItem(acl);
        }

    }

    /**
     * Sets the setIdButton.
     * 
     * @param setIdButton the setIdButton to set
     */
    public void setSetIdButton(Button setIdButton) {
        _SetIdButton = setIdButton;
    }

    private void addNewItem() {
        ACL acl = new ACL();
        acl.setId(ZooDefs.Ids.ANYONE_ID_UNSAFE);
        acl.setPerms(ZooDefs.Perms.ALL);

        TableItem newItem = addAclTableItem(acl);
        Table table = getTable();
        table.setSelection(newItem);
        tableSelectionChanged(newItem);

        fireOrchestrationChange();
    }

    private void fixLayout() {

        Table table = getTable();

        // HACK to get the removed item to disappear.
        table.pack();
        layout(true);

        int[] columnWidths = TABLE_COLUMN_WIDTHS;
        TableColumn[] columns = table.getColumns();
        for (int i = 0; i < columns.length; i++) {

            if (columnWidths == null) {
                columns[i].pack();
            }
            else {
                int columnWidth = columnWidths[i];
                if (columnWidth == SWT.DEFAULT) {
                    columns[i].pack();
                }
                else {
                    columns[i].setWidth(columnWidth);
                }
            }
        }
    }

    private String getIpId() {

        String ipId = "";

        // According to the docs the format should be:
        // addr/bits where the most significant bits of addr are matched against the most significant bits of the client
        // host IP. For example 19.22.0.0/16.
        // However the IPAuthenticationProvider seems to only allow IPs with 16 significant bits to be specified in
        // ACLs.
        /*
         * if (LOCAL_IP_ADDRESS != null) { ipId = LOCAL_IP_ADDRESS + "/" + LOCAL_IP_ADDRESS_SIZE; }
         */

        if (LOCAL_IP_ADDRESS != null) {
            String[] parts = LOCAL_IP_ADDRESS.split("\\.", -1);
            if (parts.length == 4) {
                ipId = parts[0] + "." + parts[1] + ".0.0";
            }
        }

        return ipId;

    }

    private void tableSelectionChanged(TableItem item) {

        Button setIdButton = getSetIdButton();
        setIdButton.setEnabled(SCHEME_DIGEST.equals(item.getText(TABLE_COLUMN_SCHEME)));

        Button removeButton = getRemoveButton();
        removeButton.setEnabled(item != null);

        initSchemeTableEditor(item);
        initIdTableEditor(item);

        Control idEditor = _IdTableEditor.getEditor();
        if (idEditor != null && !idEditor.isDisposed()) {
            idEditor.setFocus();
        }
    }

    private Id getItemId(TableItem item) {
        String scheme = item.getText(0).trim();
        String id = item.getText(1);
        Id itemId = new Id(scheme, id);
        return itemId;
    }

    private Button getItemPermCheckBox(TableItem item, int zooDefPerm) {
        return (Button) getItemPermTableEditor(item, zooDefPerm).getEditor();
    }

    private int getItemPerms(TableItem item) {
        int perms = 0;
        Button allCheckBox = getItemPermCheckBox(item, ZooDefs.Perms.ALL);
        if (allCheckBox.getSelection()) {
            perms = ZooDefs.Perms.ALL;
        }
        else {
            for (int perm : PERMS) {
                Button permCheckBox = getItemPermCheckBox(item, perm);
                if (permCheckBox.getSelection()) {
                    perms |= perm;
                }
            }
        }

        return perms;
    }

    private TableEditor getItemPermTableEditor(TableItem item, int zooDefPerm) {
        return (TableEditor) item.getData(String.valueOf(zooDefPerm));
    }

    private void initIdTableEditor(TableItem item) {

        Control oldEditor = _IdTableEditor.getEditor();
        if (oldEditor != null) {
            oldEditor.dispose();
        }

        if (item == null) {
            return;
        }

        Table table = getTable();

        Text newEditor = new Text(table, SWT.SINGLE);

        newEditor.setText(item.getText(TABLE_COLUMN_ID));
        newEditor.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                Text editor = (Text) _IdTableEditor.getEditor();
                _IdTableEditor.getItem().setText(TABLE_COLUMN_ID, editor.getText());

                fireOrchestrationChange();
            }
        });

        _IdTableEditor.setEditor(newEditor, item, TABLE_COLUMN_ID);
    }

    private void initSchemeTableEditor(TableItem item) {

        Control oldEditor = _SchemeTableEditor.getEditor();
        if (oldEditor != null) {
            oldEditor.dispose();
        }

        if (item == null) {
            return;
        }

        Table table = getTable();
        CCombo newEditor = new CCombo(table, SWT.FLAT);
        for (String scheme : SCHEMES) {
            newEditor.add(scheme);
        }

        newEditor.setText(item.getText(TABLE_COLUMN_SCHEME));
        newEditor.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                CCombo editor = (CCombo) _SchemeTableEditor.getEditor();
                String text = editor.getText();
                TableItem item = _SchemeTableEditor.getItem();
                item.setText(TABLE_COLUMN_SCHEME, text);

                Text idText = (Text) _IdTableEditor.getEditor();
                if (SCHEME_WORLD.equals(text)) {
                    idText.setText(ID_ANYONE);
                }
                else if (SCHEME_AUTH.equals(text)) {
                    idText.setText(ID_AUTH);
                }
                else if (SCHEME_IP.equals(text)) {
                    String ipId = getIpId();
                    idText.setText(ipId);
                }
                else {
                    idText.setText("");
                }

                getSetIdButton().setEnabled(SCHEME_DIGEST.equals(text));

                fireOrchestrationChange();
            }
        });

        _SchemeTableEditor.setEditor(newEditor, item, TABLE_COLUMN_SCHEME);
    }

    private void removeItems(TableItem[] items) {

        Table table = getTable();
        table.setRedraw(false);
        try {

            for (TableItem item : items) {

                Control schemeTableEditorControl = _SchemeTableEditor.getEditor();
                if (schemeTableEditorControl != null) {
                    schemeTableEditorControl.dispose();
                }

                _SchemeTableEditor.setEditor(null, item, 0);

                Control idTableEditorControl = _IdTableEditor.getEditor();
                if (idTableEditorControl != null) {
                    idTableEditorControl.dispose();
                }

                _IdTableEditor.setEditor(null, item, 1);

                for (int perm : PERMS) {
                    TableEditor tableEditor = getItemPermTableEditor(item, perm);
                    Button checkBox = (Button) tableEditor.getEditor();
                    tableEditor.dispose();
                    checkBox.dispose();
                }

                item.dispose();
            }
        }
        finally {
            table.setRedraw(true);
        }
    }

    private void removeSelectedItems() {
        Table table = getTable();
        removeItems(table.getSelection());
        fixLayout();
        fireOrchestrationChange();
    }

    private void setId() {

        SetDigestIdDialog dialog = new SetDigestIdDialog(getShell());
        dialog.setBlockOnOpen(true);
        if (dialog.open() == SetDigestIdDialog.OK) {
            Text idEditor = (Text) _IdTableEditor.getEditor();
            if (idEditor != null && !idEditor.isDisposed()) {
                idEditor.setText(dialog.getDigestId());
                idEditor.forceFocus();
            }
        }

    }

    private void setItemPermTableEditor(TableItem item, int zooDefPerm, TableEditor tableEditor) {
        item.setData(String.valueOf(zooDefPerm), tableEditor);
    }

    private class SetDigestIdDialog extends GridDialog {

        private static final String CONTROL_NAME_PASSWORD_TEXT = "Password";
        private static final String CONTROL_NAME_USER_NAME_TEXT = "User name";

        private static final String MESSAGE = "Generates the MD5 hash of the user name and password which is then used as an ACL ID.";
        private static final String TITLE = "Set Digest ACL ID.";

        private String _DigestId;

        /**
         * TODO: Comment.
         * 
         * @param parentShell
         */
        SetDigestIdDialog(Shell parentShell) {
            super(parentShell);
        }

        @Override
        public void create() {
            super.create();

            setTitle(TITLE);
            // setTitleImage(ZooKeeperActivator.getManagedImage(ZooKeeperActivator.IMAGE_KEY_DIALOG_ADD_AUTH_INFO));
            setMessage(MESSAGE);
            Shell shell = getShell();
            shell.setText(TITLE);

        }

        /**
         * Returns the digestId.
         * 
         * @return The digestId
         */
        public String getDigestId() {
            return _DigestId;
        }

        @Override
        protected GridComposite createGridComposite(Composite parent) {

            GridComposite gridComposite = new GridComposite(parent) {

                @Override
                protected void createContents() {

                    GridTextInput userNameGridTextInput = new GridTextInput(this, GridTextInput.Type.VALUE_REQUIRED,
                            CONTROL_NAME_USER_NAME_TEXT, "&User name: ", null);
                    addGridTextInput(userNameGridTextInput);

                    GridTextInput passwordGridTextInput = new GridTextInput(this, GridTextInput.Type.VALUE_REQUIRED,
                            CONTROL_NAME_PASSWORD_TEXT, "&Password:     ", null);
                    addGridTextInput(passwordGridTextInput);
                    passwordGridTextInput.getText().setEchoChar('*');

                    final Button showPasswordTextCheckBox = new Button(this, SWT.CHECK);
                    showPasswordTextCheckBox.setText("Show password text");
                    showPasswordTextCheckBox.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
                    showPasswordTextCheckBox.addSelectionListener(new SelectionAdapter() {

                        @Override
                        public void widgetSelected(SelectionEvent e) {
                            Text passwordText = (Text) getControl(CONTROL_NAME_PASSWORD_TEXT);
                            char echoChar = (showPasswordTextCheckBox.getSelection()) ? '\0' : '*';
                            passwordText.setEchoChar(echoChar);
                        }

                    });

                }
            };

            return gridComposite;

        }

        @Override
        protected void okPressed() {
            GridComposite gridComposite = getGridComposite();

            Text userNameText = (Text) gridComposite.getControl(CONTROL_NAME_USER_NAME_TEXT);
            Text passwordText = (Text) gridComposite.getControl(CONTROL_NAME_PASSWORD_TEXT);

            String userName = userNameText.getText();
            String password = passwordText.getText();

            _DigestId = generateDigest(userName + ":" + password);

            super.okPressed();
        }

    }

}
