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
import org.fusesource.ide.zk.jmx.jmxdoc.MBeanOperationDoc;
import org.fusesource.ide.zk.jmx.model.MBeanOperationModel;
import org.fusesource.ide.zk.jmx.viewers.MBeanOperationModelElementType;

import javax.management.MBeanOperationInfo;


/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public class MBeanOperationModelMainFormPage extends MBeanFeatureModeMainFormPage<MBeanOperationModel> {

    public static final String ID = MBeanOperationModelMainFormPage.class.getName();
    public static final String INFO_SECTION_TITLE = "MBeanOperationInfo";

    public MBeanOperationModelMainFormPage(MBeanOperationModelFormEditor editor) {
        super(editor, ID);
        setInfoSectionTitle(INFO_SECTION_TITLE);
    }

    protected MBeanOperationInfo getOperationInfo() {
        return (MBeanOperationInfo) getFeatureInfo();
    }

    @Override
    protected void initInfoSectionFromModel() {

        super.initInfoSectionFromModel();

        Table table = getInfoTable();

        MBeanOperationInfo operationInfo = getOperationInfo();

        int impact = operationInfo.getImpact();
        String impactString = MBeanOperationDoc.getImpactString(impact);

        TableItem item = new TableItem(table, SWT.NONE);
        item.setText(0, MBeanOperationModelElementType.PROPERTY_NAME_IMPACT);
        item.setText(1, impactString);

        item = new TableItem(table, SWT.NONE);
        item.setText(0, MBeanOperationModelElementType.PROPERTY_NAME_RETURN_TYPE);
        item.setText(1, operationInfo.getReturnType());
    }

    @Override
    protected void initPrimarySectionFromModel() {
    }

}
