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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.fusesource.ide.zk.jmx.model.MBeanModel;

import javax.management.Descriptor;
import javax.management.MBeanInfo;


/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public class MBeanModelMainFormPage extends BaseJmxModelMainFormPage<MBeanModel> {

    public static final String ID = MBeanModelMainFormPage.class.getName();
    public static final String INFO_SECTION_TITLE = "MBeanInfo";

    protected static final String INFO_PROPERTY_NAME_OBJECT_NAME = "Object Name";
    protected static final String INFO_PROPERTY_NAME_CLASS_NAME = "Class Name";
    protected static final String INFO_PROPERTY_NAME_DESCRIPTION = "Description";

    public MBeanModelMainFormPage(MBeanModelFormEditor editor) {
        super(editor, ID);
        setInfoSectionTitle(INFO_SECTION_TITLE);
    }

    protected MBeanInfo getInfo() {
        return getModel().getData().getInfo();
    }

    @Override
    protected Descriptor getJmxDescriptor() {
        return getInfo().getDescriptor();
    }

    @Override
    protected void initInfoSectionFromModel() {

        Table table = getInfoTable();
        table.removeAll();

        MBeanInfo info = getInfo();

        String objectName = String.valueOf(getModel().getData().getObjectName());
        String className = info.getClassName();
        String description = info.getDescription();

        TableItem item = new TableItem(table, SWT.NONE);
        item.setText(0, INFO_PROPERTY_NAME_OBJECT_NAME);
        item.setText(1, objectName);

        item = new TableItem(table, SWT.NONE);
        item.setText(0, INFO_PROPERTY_NAME_CLASS_NAME);
        item.setText(1, className);

        item = new TableItem(table, SWT.NONE);
        item.setText(0, INFO_PROPERTY_NAME_DESCRIPTION);
        item.setText(1, description);
    }

    @Override
    protected void initPrimarySectionFromModel() {
    }

    @Override
    protected void initDetailSectionFromModel() {
    }

}
