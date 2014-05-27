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

package org.fusesource.ide.zk.jmx.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.fusesource.ide.zk.jmx.data.JmxConnection;
import org.fusesource.ide.zk.jmx.data.JmxConnectionDescriptor;
import org.fusesource.ide.zk.jmx.model.JmxConnectionModel;
import org.fusesource.ide.zk.core.EclipseCoreActivator;
import org.fusesource.ide.zk.core.editors.DataModelFormPage;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXServiceURL;


/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public final class JmxConnectionModelMainFormPage extends DataModelFormPage<JmxConnectionModel> {

    public static final String ID = JmxConnectionModelMainFormPage.class.getName();
    public static final Image IMAGE = EclipseCoreActivator
            .getManagedImage(EclipseCoreActivator.IMAGE_KEY_OBJECT_MAIN_TAB);
    public static final String TITLE = "Main";

    protected static final String PROPERTY_NAME_CONNECTION_ID = "Connection Id";
    protected static final String PROPERTY_NAME_MBEAN_COUNT = "MBean Count";

    protected static final String EDIT_SECTION_TITLE = "Edit";

    protected static final int[] PROPERTIES_COLUMN_ALIGNMENTS = new int[] { SWT.LEFT, SWT.LEFT };
    protected static final String[] PROPERTIES_COLUMN_TITLES = new String[] { "Name", "Value" };
    protected static final int[] PROPERTIES_COLUMN_WIDTHS = new int[] { SWT.DEFAULT, 350 };
    protected static final String PROPERTIES_SECTION_TITLE = "Properties";
    protected static final int PROPERTIES_TABLE_STYLE = SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION;

    private Section _EditSection;
    private Section _PropertiesSection;
    private Text _ServiceUrlText;
    private Text _UserNameText;
    private Text _PasswordText;

    /**
     * TODO: Comment.
     * 
     * @param editor
     * @param id
     * @param title
     */
    public JmxConnectionModelMainFormPage(JmxConnectionModelFormEditor editor) {
        super(editor, ID, TITLE);
        setImage(IMAGE);
    }

    public JMXServiceURL getServiceUrl() throws MalformedURLException {
        String serviceUrl = _ServiceUrlText.getText();
        serviceUrl = (!serviceUrl.trim().isEmpty()) ? serviceUrl : null;
        return new JMXServiceURL(serviceUrl);
    }

    public String getUserName() {
        String userName = _UserNameText.getText();
        userName = (!userName.trim().isEmpty()) ? userName : null;
        return userName;
    }

    public String getPassword() {
        String password = _PasswordText.getText();
        password = (!password.trim().isEmpty()) ? password : null;
        return password;
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

        Table propertiesSectionTable = getPropertiesSectionTable();
        initTableEdit(propertiesSectionTable, null, 1);
    }

    protected Section createEditSection(final ScrolledForm form, Composite client, FormToolkit toolkit) {

        Section section = createSection(form, client, toolkit, EDIT_SECTION_TITLE, EclipseCoreActivator
                .getManagedImage(EclipseCoreActivator.IMAGE_KEY_OBJECT_EDIT));

        Composite sectionClient = createSectionClient(section, toolkit);

        Label serviceUrlLabel = toolkit.createLabel(sectionClient, "&Service URL: ");
        _ServiceUrlText = toolkit.createText(sectionClient, "", SWT.BORDER | SWT.SINGLE);

        _ServiceUrlText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                setDirtyInternal(true);
            }
        });

        Label userNameLabel = toolkit.createLabel(sectionClient, "&User Name: ");
        _UserNameText = toolkit.createText(sectionClient, "", SWT.BORDER | SWT.SINGLE);

        _UserNameText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                setDirtyInternal(true);
            }
        });

        Label passwordLabel = toolkit.createLabel(sectionClient, "&Password: ");
        _PasswordText = toolkit.createText(sectionClient, "", SWT.BORDER | SWT.SINGLE);
        _PasswordText.setEchoChar('*');

        _PasswordText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                setDirtyInternal(true);
            }
        });

        final Button showPasswordTextCheckBox = toolkit.createButton(sectionClient, "Show password &text", SWT.CHECK);
        showPasswordTextCheckBox.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                char echoChar = (showPasswordTextCheckBox.getSelection()) ? '\0' : '*';
                _PasswordText.setEchoChar(echoChar);
            }

        });

        Label noteLabel = toolkit.createLabel(sectionClient, "NOTE:  Password will be stored unencrypted in a file",
                SWT.LEFT | SWT.WRAP);

        FormData serviceUrlLabelFormData = new FormData();
        serviceUrlLabelFormData.top = new FormAttachment(_ServiceUrlText, 0, SWT.CENTER);
        serviceUrlLabelFormData.left = new FormAttachment(0, 0);
        serviceUrlLabel.setLayoutData(serviceUrlLabelFormData);

        FormData serviceUrlTextFormData = new FormData();
        serviceUrlTextFormData.top = new FormAttachment(0, 0);
        serviceUrlTextFormData.left = new FormAttachment(serviceUrlLabel);
        serviceUrlTextFormData.right = new FormAttachment(100, 0);
        _ServiceUrlText.setLayoutData(serviceUrlTextFormData);

        FormData userNameLabelFormData = new FormData();
        userNameLabelFormData.top = new FormAttachment(_UserNameText, 0, SWT.CENTER);
        userNameLabelFormData.left = new FormAttachment(0, 0);
        userNameLabel.setLayoutData(userNameLabelFormData);

        FormData userNameTextFormData = new FormData();
        userNameTextFormData.top = new FormAttachment(_ServiceUrlText);
        userNameTextFormData.left = new FormAttachment(_ServiceUrlText, 0, SWT.LEFT);
        userNameTextFormData.right = new FormAttachment(100, 0);
        _UserNameText.setLayoutData(userNameTextFormData);

        FormData passwordLabelFormData = new FormData();
        passwordLabelFormData.top = new FormAttachment(_PasswordText, 0, SWT.CENTER);
        passwordLabelFormData.left = new FormAttachment(0, 0);
        passwordLabel.setLayoutData(passwordLabelFormData);

        FormData passwordTextFormData = new FormData();
        passwordTextFormData.top = new FormAttachment(_UserNameText);
        passwordTextFormData.left = new FormAttachment(_ServiceUrlText, 0, SWT.LEFT);
        passwordTextFormData.right = new FormAttachment(100, 0);
        _PasswordText.setLayoutData(passwordTextFormData);

        FormData showPasswordTextCheckBoxFormData = new FormData();
        showPasswordTextCheckBoxFormData.top = new FormAttachment(_PasswordText);
        showPasswordTextCheckBoxFormData.left = new FormAttachment(0, 0);
        showPasswordTextCheckBoxFormData.right = new FormAttachment(100, 0);
        showPasswordTextCheckBox.setLayoutData(showPasswordTextCheckBoxFormData);

        FormData noteLabelFormData = new FormData();
        noteLabelFormData.top = new FormAttachment(showPasswordTextCheckBox);
        noteLabelFormData.left = new FormAttachment(0, 0);
        noteLabelFormData.right = new FormAttachment(100, 0);
        noteLabel.setLayoutData(noteLabelFormData);

        return section;
    }

    protected Section createPropertiesTableSection(ScrolledForm form, Composite client, FormToolkit toolkit) {
        return createTableSection(form, client, toolkit, PROPERTIES_SECTION_TITLE, EclipseCoreActivator
                .getManagedImage(EclipseCoreActivator.IMAGE_KEY_OBJECT_PROPERTIES), PROPERTIES_TABLE_STYLE,
                PROPERTIES_COLUMN_TITLES, PROPERTIES_COLUMN_ALIGNMENTS);
    }

    protected final Table getPropertiesSectionTable() {
        return (Table) getPropertiesSection().getClient();
    }

    @Override
    protected final void initFromModelInternal() {

        JmxConnectionModel model = getModel();
        JmxConnectionDescriptor descriptor = model.getKey();

        String jmxServiceUrl = String.valueOf(descriptor.getJmxServiceUrl());
        _ServiceUrlText.setText(jmxServiceUrl);

        String userName = descriptor.getUserName();
        userName = (userName != null) ? userName : "";
        _UserNameText.setText(userName);

        if (userName != null) {
            String password = descriptor.getPassword();
            password = (password != null) ? password : "";
            _PasswordText.setText(password);
        }
        
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
        }

        packTable(table, PROPERTIES_COLUMN_WIDTHS);

    }

    /**
     * TODO: Comment.
     * 
     * @return
     */
    private Map<String, String> getZooKeeperConnectionProperties() {

        JmxConnectionModel model = getModel();
        JmxConnection connection = model.getData();
        
        if (!connection.isConnected()) {
            return Collections.emptyMap();
        }
        
        JMXConnector connector = connection.getJMXConnector();
        MBeanServerConnection mbeanServerConnection = connection.getMBeanServerConnection();

        Map<String, String> properties = new LinkedHashMap<String, String>();
        
        try {
            putProperty(properties, PROPERTY_NAME_CONNECTION_ID, connector.getConnectionId());
        }
        catch (IOException e) {
        }

        try {
            putProperty(properties, PROPERTY_NAME_MBEAN_COUNT, mbeanServerConnection.getMBeanCount());
        }
        catch (IOException e) {
        }

        return properties;
    }

    private void putProperty(Map<String, String> properties, String key, Object value) {

        if (value == null) {
            value = "";
        }

        String stringValue = String.valueOf(value);

        properties.put(key, stringValue);
    }

}
