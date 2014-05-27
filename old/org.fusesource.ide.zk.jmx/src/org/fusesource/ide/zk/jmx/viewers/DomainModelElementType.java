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
import org.fusesource.ide.zk.jmx.data.Domain;
import org.fusesource.ide.zk.jmx.model.DomainModel;
import org.fusesource.ide.zk.jmx.model.MBeanModel;
import org.fusesource.ide.zk.jmx.model.ObjectNameKeyValueModel;

import java.util.List;
import java.util.Set;

import javax.management.ObjectName;


/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public class DomainModelElementType extends AbstractJmxDataModelElementType {

    public static final String PROPERTY_NAME_NAME = "Name";
    public static final String PROPERTY_NAME_MBEAN_COUNT = "MBean Count";

    private static final int[] COLUMN_ALIGNMENTS = new int[] { SWT.LEFT, SWT.RIGHT };
    private static final String[] COLUMN_TITLES = new String[] { PROPERTY_NAME_NAME, PROPERTY_NAME_MBEAN_COUNT };
    private static final int[] COLUMN_WIDTHS = new int[] { SWT.DEFAULT, SWT.DEFAULT };

    @Override
    public int[] getColumnAlignments() {
        return COLUMN_ALIGNMENTS;
    }

    @Override
    public String getColumnText(Object element, int columnIndex) {

        // 0:Name | 1:MBean Count

        DomainModel model = (DomainModel) element;
        Domain domain = model.getData();

        switch (columnIndex) {

        case 0:
            return domain.getName();

        case 1:

            int mbeanCount = 0;
            Set<ObjectName> objectNames = domain.getMBeanObjectNames();
            if (objectNames != null) {
                mbeanCount = objectNames.size();
            }

            return String.valueOf(mbeanCount);
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
    public int getChildCount(Object parent) {

        int childCount = 0;
        DomainModel model = (DomainModel) parent;
        Set<String> objectNameKeyValuePairStrings = model.getObjectNameKeyValuePairStrings();
        if (objectNameKeyValuePairStrings != null) {
            childCount += objectNameKeyValuePairStrings.size();
        }
        Set<ObjectName> mbeanObjectNames = model.getRootMBeanObjectNames();
        if (mbeanObjectNames != null) {
            childCount += mbeanObjectNames.size();
        }
        return childCount;
    }

    @Override
    public Object getChildElement(Object parent, int index) {

        DomainModel model = (DomainModel) parent;

        int objectNameKeyValueModelCount = 0;
        List<ObjectNameKeyValueModel> objectNameKeyValueModels = model.getObjectNameKeyValueModels();
        if (objectNameKeyValueModels != null) {
            objectNameKeyValueModelCount = objectNameKeyValueModels.size();
        }

        if (index < objectNameKeyValueModelCount) {
            return objectNameKeyValueModels.get(index);
        }

        int mbeanModelCount = 0;
        List<MBeanModel> mbeanModels = model.getRootMBeanModels();
        if (mbeanModels != null) {
            mbeanModelCount = mbeanModels.size();
        }

        if (index < objectNameKeyValueModelCount + mbeanModelCount) {
            return mbeanModels.get(index - objectNameKeyValueModelCount);
        }

        return null;
    }

    @Override
    public Image getImage(Object element) {
        return JmxActivator.getManagedImage(JmxActivator.IMAGE_KEY_OBJECT_MBEAN_DOMAIN);
    }

    @Override
    public Image getLargeImage(Object element) {
        return JmxActivator.getManagedImage(JmxActivator.IMAGE_KEY_OBJECT_MBEAN_DOMAIN_LARGE);
    }

    @Override
    public String getText(Object element) {
        DomainModel model = (DomainModel) element;
        String text = model.getData().getName();
        return text;
    }

}
