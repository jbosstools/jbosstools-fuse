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
import org.fusesource.ide.zk.jmx.data.MBeanAttribute;
import org.fusesource.ide.zk.jmx.model.MBeanAttributeModel;

import javax.management.MBeanAttributeInfo;


/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public class MBeanAttributeModelElementType extends AbstractMBeanFeatureModelElementType {

    public static final String PROPERTY_NAME_IS_GETTER = "Has \"is\" Getter";

    public static final String PROPERTY_NAME_READABLE = "Readable";
    public static final String PROPERTY_NAME_TYPE = "Type";
    public static final String PROPERTY_NAME_VALUE = "Value";
    public static final String PROPERTY_NAME_WRITABLE = "Writable";

    private static final int[] COLUMN_ALIGNMENTS = new int[] { SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.LEFT };
    private static final String[] COLUMN_TITLES = new String[] { PROPERTY_NAME_NAME, PROPERTY_NAME_VALUE,
            PROPERTY_NAME_TYPE, PROPERTY_NAME_DESCRIPTION };
    private static final int[] COLUMN_WIDTHS = new int[] { SWT.DEFAULT, 200, SWT.DEFAULT, 200 };

    @Override
    public int[] getColumnAlignments() {
        return COLUMN_ALIGNMENTS;
    }

    @Override
    public String getColumnText(Object element, int columnIndex) {

        // 0:Name | 1:Value | 2:Type | 3:Description

        MBeanAttributeModel model = (MBeanAttributeModel) element;
        MBeanAttribute mbeanAttribute = model.getData();
        MBeanAttributeInfo info = mbeanAttribute.getInfo();
        switch (columnIndex) {

        case 0:
            return mbeanAttribute.getName();

        case 1:

            String valueError = mbeanAttribute.getValueRetrievalErrorMessage();
            if (valueError != null) {
                return valueError;
            }
            else {
                return mbeanAttribute.getValueAsString();
            }

        case 2:
            return info.getType();

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
        return JmxActivator.getManagedImage(JmxActivator.IMAGE_KEY_OBJECT_MBEAN_ATTRIBUTE);
    }

    @Override
    public Image getLargeImage(Object element) {
        return JmxActivator.getManagedImage(JmxActivator.IMAGE_KEY_OBJECT_MBEAN_ATTRIBUTE_LARGE);
    }

    @Override
    public Object getParent(Object element) {
        MBeanAttributeModel model = (MBeanAttributeModel) element;
        return model.getParentModel().getAttributesModelCategory();
    }

}
