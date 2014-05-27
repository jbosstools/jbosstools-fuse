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

package org.fusesource.ide.zk.zookeeper.editors.zookeeperconnectionform;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.fusesource.ide.zk.zookeeper.ZooKeeperActivator;
import org.fusesource.ide.zk.zookeeper.data.Znode;
import org.fusesource.ide.zk.zookeeper.data.ZooKeeperConnection;
import org.fusesource.ide.zk.zookeeper.data.ZooKeeperConnectionDescriptor;
import org.fusesource.ide.zk.zookeeper.model.ZnodeModel;
import org.fusesource.ide.zk.zookeeper.model.ZooKeeperConnectionModel;
import org.fusesource.ide.zk.zookeeper.viewers.ZnodeModelElementType;
import org.fusesource.ide.zk.zookeeper.viewers.ZooKeeperConnectionModelElementType;
import org.fusesource.ide.zk.core.EclipseCoreActivator;
import org.fusesource.ide.zk.core.actions.BaseOpenAction;
import org.fusesource.ide.zk.core.editors.DataModelFormPage;
import org.fusesource.ide.zk.core.widgets.grid.GridTextInput;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.zookeeper.ZooKeeper.States;

/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public final class ZooKeeperConnectionModelMainFormPage extends DataModelFormPage<ZooKeeperConnectionModel> {

    public static final String ID = ZooKeeperConnectionModelMainFormPage.class.getName();
    public static final Image IMAGE = EclipseCoreActivator
            .getManagedImage(EclipseCoreActivator.IMAGE_KEY_OBJECT_MAIN_TAB);
    public static final String TITLE = "Main";

    protected static final String EDIT_SECTION_TITLE = "Edit";
    protected static final String ZNODE_SECTION_TITLE = "Znode";

    protected static final int[] PROPERTIES_COLUMN_ALIGNMENTS = new int[] { SWT.LEFT, SWT.LEFT };
    protected static final String[] PROPERTIES_COLUMN_TITLES = new String[] { "Name", "Value" };
    protected static final int[] PROPERTIES_COLUMN_WIDTHS = new int[] { SWT.DEFAULT, 350 };
    protected static final String PROPERTIES_SECTION_TITLE = "Properties";
    protected static final int PROPERTIES_TABLE_STYLE = SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION;

    private Section _EditSection;
    private Section _PropertiesSection;
    private Section _ZnodeSection;
    private Text _RootPathText;
    private Text _SessionTimeoutText;
    private ZnodeModelElementType _ZnodeModelElementType;

    /**
     * TODO: Comment.
     * 
     * @param editor
     * @param id
     * @param title
     */
    public ZooKeeperConnectionModelMainFormPage(ZooKeeperConnectionModelFormEditor editor) {
        super(editor, ID, TITLE);
        setImage(IMAGE);
        _ZnodeModelElementType = new ZnodeModelElementType();
    }

    public String getRootPath() {
        String rootPath = _RootPathText.getText();
        rootPath = (!rootPath.trim().isEmpty()) ? rootPath : null;
        return rootPath;
    }

    public int getSessionTimeout() {
        int sessionTimeout = -1;
        try {
            sessionTimeout = Integer.parseInt(_SessionTimeoutText.getText());
        }
        catch (NumberFormatException e) {
        }

        return sessionTimeout;
    }

    /**
     * Returns the propertiesSection.
     * 
     * @return The propertiesSection
     */
    public Section getPropertiesSection() {
        return _PropertiesSection;
    }

    @Override
    protected Layout createClientLayout() {
        GridLayout gridLayout = new GridLayout(1, true);
        gridLayout.verticalSpacing = 20;
        gridLayout.marginWidth = 8;
        return gridLayout;
    }

    @Override
    protected void createModelFormContent(IManagedForm managedForm, Composite client) {
        final ScrolledForm form = managedForm.getForm();
        FormToolkit toolkit = managedForm.getToolkit();

        _EditSection = createEditSection(form, client, toolkit);
        GridData editSectionGridData = new GridData(GridData.FILL_HORIZONTAL);
        _EditSection.setLayoutData(editSectionGridData);

        _PropertiesSection = createPropertiesTableSection(form, client, toolkit);
        GridData propertiesSectionGridData = new GridData(GridData.FILL_HORIZONTAL);
        _PropertiesSection.setLayoutData(propertiesSectionGridData);

        _ZnodeSection = createZnodeSection(form, client, toolkit);
        GridData znodeSectionGridData = new GridData(GridData.FILL_HORIZONTAL);
        _ZnodeSection.setLayoutData(znodeSectionGridData);

        Table propertiesSectionTable = getPropertiesSectionTable();
        initTableEdit(propertiesSectionTable, null, 1);
    }

    protected Section createEditSection(final ScrolledForm form, Composite client, FormToolkit toolkit) {

        Section section = createSection(form, client, toolkit, EDIT_SECTION_TITLE, EclipseCoreActivator
                .getManagedImage(EclipseCoreActivator.IMAGE_KEY_OBJECT_EDIT));

        Composite sectionClient = createSectionClient(section, toolkit);

        Label rootPathLabel = toolkit.createLabel(sectionClient, "&Root Path: ");
        _RootPathText = toolkit.createText(sectionClient, "", SWT.BORDER | SWT.SINGLE);

        _RootPathText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                setDirtyInternal(true);
            }
        });

        Label sessionTimeoutLabel = toolkit.createLabel(sectionClient, "&Session Timeout: ");
        _SessionTimeoutText = toolkit.createText(sectionClient, "", SWT.BORDER | SWT.SINGLE);
        _SessionTimeoutText.setTextLimit(10);

        _SessionTimeoutText.addVerifyListener(new VerifyListener() {

            @Override
            public void verifyText(VerifyEvent event) {
                String text = event.text;
                event.doit = text.length() == 0
                        || (Character.isDigit(text.charAt(0)) && GridTextInput.isValidIntegerText(text));
            }
        });

        _SessionTimeoutText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                setDirtyInternal(true);
            }
        });

        FormData rootPathLabelFormData = new FormData();
        rootPathLabelFormData.top = new FormAttachment(_RootPathText, 0, SWT.CENTER);
        rootPathLabelFormData.left = new FormAttachment(0, 0);
        rootPathLabel.setLayoutData(rootPathLabelFormData);

        FormData rootPathTextFormData = new FormData();
        rootPathTextFormData.top = new FormAttachment(0, 0);
        rootPathTextFormData.left = new FormAttachment(_SessionTimeoutText, 0, SWT.LEFT);
        rootPathTextFormData.right = new FormAttachment(100, 0);
        _RootPathText.setLayoutData(rootPathTextFormData);

        FormData sessionTimeoutLabelFormData = new FormData();
        sessionTimeoutLabelFormData.top = new FormAttachment(_SessionTimeoutText, 0, SWT.CENTER);
        sessionTimeoutLabelFormData.left = new FormAttachment(0, 0);
        sessionTimeoutLabel.setLayoutData(sessionTimeoutLabelFormData);

        FormData sessionTimeoutTextFormData = new FormData();
        sessionTimeoutTextFormData.top = new FormAttachment(_RootPathText);
        sessionTimeoutTextFormData.left = new FormAttachment(sessionTimeoutLabel);
        sessionTimeoutTextFormData.right = new FormAttachment(100, 0);
        _SessionTimeoutText.setLayoutData(sessionTimeoutTextFormData);

        return section;
    }

    protected Section createPropertiesTableSection(ScrolledForm form, Composite client, FormToolkit toolkit) {
        return createTableSection(form, client, toolkit, PROPERTIES_SECTION_TITLE, EclipseCoreActivator
                .getManagedImage(EclipseCoreActivator.IMAGE_KEY_OBJECT_PROPERTIES), PROPERTIES_TABLE_STYLE,
                PROPERTIES_COLUMN_TITLES, PROPERTIES_COLUMN_ALIGNMENTS);
    }

    protected Section createZnodeSection(final ScrolledForm form, Composite client, FormToolkit toolkit) {

        Section section = createSection(form, client, toolkit, ZNODE_SECTION_TITLE, ZooKeeperActivator
                .getManagedImage(ZooKeeperActivator.IMAGE_KEY_OBJECT_ZNODE_LEAF));

        Composite sectionClient = createSectionClient(section, toolkit);

        Label openZnodeLabel = toolkit.createLabel(sectionClient, "&Open Znode: ");
        final Text znodePathText = toolkit.createText(sectionClient, Znode.ROOT_PATH, SWT.BORDER | SWT.SINGLE);
        final Button openZnodeButton = toolkit.createButton(sectionClient, "Open", SWT.PUSH);

        znodePathText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                String path = znodePathText.getText();
                openZnodeButton.setEnabled(canOpenZnode(path));
            }

        });

        znodePathText.addTraverseListener(new TraverseListener() {

            @Override
            public void keyTraversed(TraverseEvent e) {
                if (e.detail == SWT.TRAVERSE_RETURN) {
                    String path = znodePathText.getText();
                    openZnode(path);
                }

            }
        });

        openZnodeButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                String path = znodePathText.getText();
                openZnode(path);
            }

        });

        FormData openZnodeLabelFormData = new FormData();
        openZnodeLabelFormData.top = new FormAttachment(znodePathText, 0, SWT.CENTER);
        openZnodeLabelFormData.left = new FormAttachment(0, 0);
        openZnodeLabel.setLayoutData(openZnodeLabelFormData);

        FormData znodePathTextFormData = new FormData();
        znodePathTextFormData.top = new FormAttachment(0, 0);
        znodePathTextFormData.left = new FormAttachment(openZnodeLabel);
        znodePathTextFormData.right = new FormAttachment(openZnodeButton, 0, SWT.LEFT);
        znodePathText.setLayoutData(znodePathTextFormData);

        FormData openZnodeButtonFormData = new FormData();
        openZnodeButtonFormData.top = new FormAttachment(znodePathText, 0, SWT.CENTER);
        openZnodeButtonFormData.right = new FormAttachment(100, 0);
        openZnodeButton.setLayoutData(openZnodeButtonFormData);

        return section;
    }

    protected final Table getPropertiesSectionTable() {
        return (Table) getPropertiesSection().getClient();
    }

    @Override
    protected final void initFromModelInternal() {

        ZooKeeperConnectionModel model = getModel();
        ZooKeeperConnectionDescriptor descriptor = model.getKey();

        String rootPath = descriptor.getRootPath();
        rootPath = (rootPath != null) ? rootPath : "/";
        _RootPathText.setText(rootPath);

        int sessionTimeout = descriptor.getSessionTimeout();
        _SessionTimeoutText.setText(String.valueOf(sessionTimeout));

        initPropertiesSectionFromModel();

        Section propertiesSection = getPropertiesSection();
        if (propertiesSection != null) {
            propertiesSection.layout(true);
        }
    }

    /**
     * TODO: Comment.
     * 
     */
    protected void initPropertiesSectionFromModel() {

        Table table = getPropertiesSectionTable();
        table.removeAll();

        Map<String, String> properties = getZooKeeperConnectionProperties();

        for (String key : properties.keySet()) {
            TableItem item = new TableItem(table, SWT.NONE);
            String value = properties.get(key);
            item.setText(0, key);
            item.setText(1, value);

            Color valueTextColor = table.getForeground();
            if (key.equals(ZooKeeperConnectionModelElementType.PROPERTY_NAME_STATE)) {
                if (!States.CONNECTED.name().equals(value)) {
                    valueTextColor = table.getDisplay().getSystemColor(SWT.COLOR_RED);
                }
                // else {
                // valueTextColor = table.getDisplay().getSystemColor(SWT.COLOR_GREEN);
                // }
            }
            
            item.setForeground(1, valueTextColor);
        }

        packTable(table, PROPERTIES_COLUMN_WIDTHS);

    }

    /**
     * TODO: Comment.
     * 
     * @return
     */
    private Map<String, String> getZooKeeperConnectionProperties() {

        ZooKeeperConnectionModel model = getModel();
        ZooKeeperConnectionDescriptor descriptor = model.getKey();
        ZooKeeperConnection connection = model.getData();

        Map<String, String> properties = new LinkedHashMap<String, String>();

        putProperty(properties, ZooKeeperConnectionModelElementType.PROPERTY_NAME_STATE, connection.getState().name());
        putProperty(properties, ZooKeeperConnectionModelElementType.PROPERTY_NAME_CONNECT_STRING, descriptor
                .getConnectString());
        putProperty(properties, ZooKeeperConnectionModelElementType.PROPERTY_NAME_SESSION_ID, connection.getSessionId());
        

        return properties;
    }

    private void putProperty(Map<String, String> properties, String key, Object value) {

        if (value == null) {
            value = "";
        }

        String stringValue = String.valueOf(value);

        properties.put(key, stringValue);
    }

    private boolean canOpenZnode(String path) {

        boolean validPath = (path != null && path.length() > 0);

        if (validPath) {
            try {
                Znode.validatePath(path, false);
            }
            catch (IllegalArgumentException ex) {
                validPath = false;
            }
        }

        return validPath;
    }

    private void openZnode(String path) {

        if (!canOpenZnode(path)) {
            String title = "Invalid Path";
            String message = "Znode path '" + path + "' is not valid.";
            MessageDialog.openError(Display.getCurrent().getActiveShell(), title, message);
            return;
        }

        ZooKeeperConnectionModel zooKeeperConnectionModel = getModel();
        ZnodeModel znodeModel = zooKeeperConnectionModel.getRootZnodeModel().getManager().getModel(path);

        if (znodeModel == null) {
            String title = "No Znode";
            String message = "Znode '" + path + "' does not exist.";
            MessageDialog.openError(Display.getCurrent().getActiveShell(), title, message);
            return;
        }

        BaseOpenAction openAction = _ZnodeModelElementType.getOpenAction();
        if (openAction != null) {

            try {
                openAction.runWithObject(znodeModel);
            }
            catch (Exception ex) {
                ZooKeeperActivator.reportError(ex);
            }
        }

    }

}
