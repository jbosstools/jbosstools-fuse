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
import org.fusesource.ide.zk.jmx.data.MBean;
import org.fusesource.ide.zk.jmx.data.ObjectNameKeyValue;
import org.fusesource.ide.zk.jmx.model.MBeanModel;

import java.util.Set;

import javax.management.MBeanInfo;


/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public class MBeanModelElementType extends AbstractObjectNameKeyValueModelElementType {

    public static final String COLUMN_TITLE_CLASS_NAME = "Class Name";
    public static final String COLUMN_TITLE_DESCRIPTION = "Description";
    public static final String COLUMN_TITLE_OBJECT_NAME = "ObjectName";

    private static final int[] COLUMN_ALIGNMENTS = new int[] { SWT.LEFT, SWT.LEFT, SWT.LEFT };

    private static final String[] COLUMN_TITLES = new String[] { COLUMN_TITLE_OBJECT_NAME, COLUMN_TITLE_CLASS_NAME,
            COLUMN_TITLE_DESCRIPTION };

    private static final int[] COLUMN_WIDTHS = new int[] { SWT.DEFAULT, 150, 150 };

    @Override
    public int getChildCount(Object parent) {

        int childCount = super.getChildCount(parent);

        MBeanModel model = (MBeanModel) parent;
        Set<String> attributeNames = model.getAttributeNames();
        if (attributeNames != null && attributeNames.size() > 0) {
            childCount++;
        }

        Set<String> operationNames = model.getOperationNames();
        if (operationNames != null && operationNames.size() > 0) {
            childCount++;
        }

        return childCount;
    }

    @Override
    public Object getChildElement(Object parent, int index) {

        int baseChildCount = super.getChildCount(parent);
        if (index < baseChildCount) {
            return super.getChildElement(parent, index);
        }

        MBeanModel model = (MBeanModel) parent;

        int baseIndex = baseChildCount;

        int attributeIndex = -1;
        int attributeCount = 0;
        Set<String> attributeNames = model.getAttributeNames();
        if (attributeNames != null) {
            attributeCount = attributeNames.size();
        }

        if (attributeCount > 0) {
            attributeIndex = baseIndex;

            if (index == attributeIndex) {
                return model.getAttributesModelCategory();
            }
        }

        int operationsIndex = -1;
        int operationCount = 0;
        Set<String> operationNames = model.getOperationNames();
        if (operationNames != null) {
            operationCount = operationNames.size();
        }

        if (operationCount > 0) {
            operationsIndex = baseIndex;

            if (attributeCount > 0) {
                operationsIndex++;
            }

            if (index == operationsIndex) {
                return model.getOperationsModelCategory();
            }
        }

        return null;
    }

    @Override
    public int[] getColumnAlignments() {
        return COLUMN_ALIGNMENTS;
    }

    @Override
    public String getColumnText(Object element, int columnIndex) {

        // 0:ObjectName | 1:Class Name | 2:Description

        MBeanModel model = (MBeanModel) element;
        MBean mbean = model.getData();
        MBeanInfo info = mbean.getInfo();
        switch (columnIndex) {

        case 0:
            return mbean.getObjectName().toString();

        case 1:
            return info.getClassName();

        case 2:
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
        return JmxActivator.getManagedImage(JmxActivator.IMAGE_KEY_OBJECT_MBEAN);
    }

    @Override
    public Image getLargeImage(Object element) {
        return JmxActivator.getManagedImage(JmxActivator.IMAGE_KEY_OBJECT_MBEAN_LARGE);
    }

    @Override
    protected ObjectNameKeyValue getObjectNameKeyValue(Object element) {
        MBeanModel model = (MBeanModel) element;
        return model.getObjectNameKeyValue();
    }

}
