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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.fusesource.ide.zk.jmx.JmxActivator;
import org.fusesource.ide.zk.jmx.data.MBeanAttribute;
import org.fusesource.ide.zk.jmx.model.MBeanAttributeModel;
import org.fusesource.ide.zk.jmx.viewers.MBeanAttributeModelElementType;

import javax.management.MBeanAttributeInfo;


/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public class MBeanAttributeModelMainFormPage extends MBeanFeatureModeMainFormPage<MBeanAttributeModel> {

    public static final String ID = MBeanAttributeModelMainFormPage.class.getName();
    public static final String INFO_SECTION_TITLE = "MBeanAttributeInfo";
    public static final String PRIMARY_SECTION_TITLE = "Attribute Value";

    public MBeanAttributeModelMainFormPage(MBeanAttributeModelFormEditor editor) {
        super(editor, ID);
        setPrimarySectionTitle(PRIMARY_SECTION_TITLE);
        setInfoSectionTitle(INFO_SECTION_TITLE);
    }

    protected Section createPrimarySection(ScrolledForm form, Composite client, FormToolkit toolkit) {
        Section primarySection = createTableSection(form, client, toolkit, getPrimarySectionTitle(), JmxActivator
                .getManagedImage(JmxActivator.IMAGE_KEY_OBJECT_MBEAN_ATTRIBUTE_VALUE));

        Table primarySectionTable = (Table) primarySection.getClient();
        initTableEdit(primarySectionTable, null, 1);

        return primarySection;
    }

    protected MBeanAttributeInfo getAttributeInfo() {
        return (MBeanAttributeInfo) getFeatureInfo();
    }

    protected final Table getPrimaryTable() {
        return (Table) getPrimarySection().getClient();
    }

    @Override
    protected void initInfoSectionFromModel() {

        super.initInfoSectionFromModel();

        Table table = getInfoTable();

        MBeanAttributeInfo attributeInfo = getAttributeInfo();

        TableItem item = new TableItem(table, SWT.NONE);
        item.setText(0, MBeanAttributeModelElementType.PROPERTY_NAME_TYPE);
        item.setText(1, attributeInfo.getType());

        item = new TableItem(table, SWT.NONE);
        item.setText(0, MBeanAttributeModelElementType.PROPERTY_NAME_READABLE);
        item.setText(1, String.valueOf(attributeInfo.isReadable()));

        item = new TableItem(table, SWT.NONE);
        item.setText(0, MBeanAttributeModelElementType.PROPERTY_NAME_WRITABLE);
        item.setText(1, String.valueOf(attributeInfo.isWritable()));

        item = new TableItem(table, SWT.NONE);
        item.setText(0, MBeanAttributeModelElementType.PROPERTY_NAME_IS_GETTER);
        item.setText(1, String.valueOf(attributeInfo.isIs()));

    }

    @Override
    protected void initPrimarySectionFromModel() {

        Table table = getPrimaryTable();

        table.removeAll();

        MBeanAttributeModel model = getModel();
        MBeanAttribute attribute = model.getData();

        String valueString = attribute.getValueAsString();
        String valueError = attribute.getValueRetrievalErrorMessage();

        TableItem item = new TableItem(table, SWT.NONE);
        item.setText(0, attribute.getName());

        if (valueError != null) {
            item.setText(1, valueError);
            item.setForeground(1, table.getDisplay().getSystemColor(SWT.COLOR_RED));
        }
        else {
            item.setText(1, valueString);
        }

        packTable(table, DEFAULT_NAME_VALUE_COLUMN_WIDTHS);
    }

}
