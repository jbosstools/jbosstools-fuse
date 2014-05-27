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

package org.fusesource.ide.zk.jmx.viewers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.fusesource.ide.zk.jmx.JmxActivator;
import org.fusesource.ide.zk.jmx.data.MBeanOperation;
import org.fusesource.ide.zk.jmx.jmxdoc.MBeanOperationDoc;
import org.fusesource.ide.zk.jmx.model.MBeanOperationModel;

import javax.management.MBeanOperationInfo;


/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public class MBeanOperationModelElementType extends AbstractMBeanFeatureModelElementType {

    public static final String PROPERTY_NAME_IMPACT = "Impact";
    public static final String PROPERTY_NAME_RETURN_TYPE = "Return Type";

    private static final int[] COLUMN_ALIGNMENTS = new int[] { SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.LEFT };
    private static final String[] COLUMN_TITLES = new String[] { PROPERTY_NAME_NAME, PROPERTY_NAME_RETURN_TYPE,
            PROPERTY_NAME_IMPACT, PROPERTY_NAME_DESCRIPTION };
    private static final int[] COLUMN_WIDTHS = new int[] { SWT.DEFAULT, SWT.DEFAULT, SWT.DEFAULT, 200 };

    @Override
    public int[] getColumnAlignments() {
        return COLUMN_ALIGNMENTS;
    }

    @Override
    public String getColumnText(Object element, int columnIndex) {

        // 0:Name | 1:Return Type | 2:Impact | 3:Description

        MBeanOperationModel model = (MBeanOperationModel) element;
        MBeanOperation mbeanOperation = model.getData();
        MBeanOperationInfo info = mbeanOperation.getInfo();

        switch (columnIndex) {

        case 0:
            return mbeanOperation.getName();

        case 1:
            return info.getReturnType();

        case 2:
            return MBeanOperationDoc.getImpactString(info.getImpact());

        case 3:
            return info.getDescription();

        }

        return null;
    }

    @Override
    public String[] getColumnTitles() {
        return COLUMN_TITLES;
    }

    @Override
    public int[] getColumnWidths() {
        return COLUMN_WIDTHS;
    }

    @Override
    public Image getImage(Object element) {
        return JmxActivator.getManagedImage(JmxActivator.IMAGE_KEY_OBJECT_MBEAN_OPERATION);
    }

    @Override
    public Image getLargeImage(Object element) {
        return JmxActivator.getManagedImage(JmxActivator.IMAGE_KEY_OBJECT_MBEAN_OPERATION_LARGE);
    }

    @Override
    public Object getParent(Object element) {
        MBeanOperationModel model = (MBeanOperationModel) element;
        return model.getParentModel().getOperationsModelCategory();
    }
}
