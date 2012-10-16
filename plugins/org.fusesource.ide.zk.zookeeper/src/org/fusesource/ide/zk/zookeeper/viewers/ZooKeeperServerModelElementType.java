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

package org.fusesource.ide.zk.zookeeper.viewers;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Table;
import org.fusesource.ide.zk.zookeeper.data.ZooKeeperServerDescriptor;
import org.fusesource.ide.zk.zookeeper.model.ZooKeeperServerModel;
import org.fusesource.ide.zk.core.EclipseCoreActivator;
import org.fusesource.ide.zk.core.viewers.IElementType;


/**
 * {@link IElementType} for {@link ZooKeeperServerModel}.
 * 
 * @author Mark Masse
 */
public class ZooKeeperServerModelElementType extends AbstractZooKeeperDataModelElementType {

    private ZooKeeperServerDescriptorElementType _DescriptorElementType;

    public ZooKeeperServerModelElementType() {
        _DescriptorElementType = new ZooKeeperServerDescriptorElementType();
    }

    @Override
    public int getChildCount(Object parent) {
        ZooKeeperServerModel model = (ZooKeeperServerModel) parent;
        if (model.getData().getDescriptor().getJmxConnectionDescriptor() != null) {
            return 1;
        }
        return 0;
    }

    @Override
    public Object getChildElement(Object parent, int index) {
        ZooKeeperServerModel model = (ZooKeeperServerModel) parent;
        if (index == 0) {
            return model.getJmxConnectionModel();
        }
        return null;
    }

    @Override
    public int[] getColumnAlignments() {
        return _DescriptorElementType.getColumnAlignments();
    }

    @Override
    public Image getColumnImage(Object element, int columnIndex) {
        return _DescriptorElementType.getColumnImage(getDescriptor(element), columnIndex);
    }

    @Override
    public int getColumnIndex(String columnTitle) {
        return _DescriptorElementType.getColumnIndex(columnTitle);
    }

    @Override
    public String getColumnText(Object element, int columnIndex) {
        return _DescriptorElementType.getColumnText(getDescriptor(element), columnIndex);
    }

    @Override
    public String[] getColumnTitles() {
        return _DescriptorElementType.getColumnTitles();
    }

    @Override
    public int[] getColumnWidths() {
        return _DescriptorElementType.getColumnWidths();
    }

    @Override
    public Image getImage(Object element) {
        return _DescriptorElementType.getImage(getDescriptor(element));
    }

    @Override
    public Image getLargeImage(Object element) {
        return EclipseCoreActivator.getManagedImage(EclipseCoreActivator.IMAGE_KEY_OBJECT_SERVER_LARGE);
    }

    @Override
    public Object getParent(Object element) {
        ZooKeeperServerModel model = (ZooKeeperServerModel) element;
        return model.getParentModel().getZooKeeperServersModelCategory();
    }

    @Override
    public String getText(Object element) {
        return _DescriptorElementType.getText(getDescriptor(element));
    }

    @Override
    public String getToolTipText(Object element) {
        return _DescriptorElementType.getToolTipText(getDescriptor(element));
    }

    @Override
    public void packTable(Table table) {
        _DescriptorElementType.packTable(table);
    }

    private ZooKeeperServerDescriptor getDescriptor(Object element) {
        ZooKeeperServerModel model = (ZooKeeperServerModel) element;
        return model.getKey();
    }

}
