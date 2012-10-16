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
import org.fusesource.ide.zk.zookeeper.ZooKeeperActivator;
import org.fusesource.ide.zk.zookeeper.data.ZooKeeperConnectionDescriptor;
import org.fusesource.ide.zk.zookeeper.model.ZooKeeperConnectionModel;
import org.fusesource.ide.zk.core.viewers.IElementType;


/**
 * {@link IElementType} for {@link ZooKeeperConnectionModel}.
 * 
 * @author Mark Masse
 */
public class ZooKeeperConnectionModelElementType extends AbstractZooKeeperDataModelElementType {

    public static final String PROPERTY_NAME_CONNECT_STRING = "Connect String";
    public static final String PROPERTY_NAME_NAME = "Name";
    public static final String PROPERTY_NAME_ROOT_PATH = "Root Path";
    public static final String PROPERTY_NAME_SESSION_ID = "Session Id";
    public static final String PROPERTY_NAME_SESSION_TIMEOUT = "Session Timeout";
    public static final String PROPERTY_NAME_STATE = "State";

    @Override
    public int getChildCount(Object parent) {
        ZooKeeperConnectionModel model = (ZooKeeperConnectionModel) parent;

        if (!model.isConnected()) {
            return 1;
        }

        return 2;
    }

    @Override
    public Object getChildElement(Object parent, int index) {

        ZooKeeperConnectionModel model = (ZooKeeperConnectionModel) parent;
        if (index == 0) {
            return model.getZooKeeperServersModelCategory();
        }
        else if (index == 1 && model.isConnected()) {
            return model.getRootZnodeModel();
        }

        return null;
    }

    @Override
    public Image getImage(Object element) {
        ZooKeeperConnectionModel model = (ZooKeeperConnectionModel) element;
        if (model != null && !model.isConnected()) {
            return ZooKeeperActivator.getManagedImage(ZooKeeperActivator.IMAGE_KEY_OBJECT_ZOO_KEEPER_CONNECTION_NOT_CONNECTED);
        }
        return ZooKeeperActivator.getManagedImage(ZooKeeperActivator.IMAGE_KEY_OBJECT_ZOO_KEEPER_CONNECTION);
    }

    @Override
    public Image getLargeImage(Object element) {
        ZooKeeperConnectionModel model = (ZooKeeperConnectionModel) element;
        if (model != null && !model.isConnected()) {
            return ZooKeeperActivator.getManagedImage(ZooKeeperActivator.IMAGE_KEY_OBJECT_ZOO_KEEPER_CONNECTION_NOT_CONNECTED_LARGE);
        }
        return ZooKeeperActivator.getManagedImage(ZooKeeperActivator.IMAGE_KEY_OBJECT_ZOO_KEEPER_CONNECTION_LARGE);        
    }

    @Override
    public String getText(Object element) {
        ZooKeeperConnectionModel model = (ZooKeeperConnectionModel) element;
        return model.getData().getDescriptor().getName();
    }

    @Override
    public String getToolTipText(Object element) {
        ZooKeeperConnectionModel model = (ZooKeeperConnectionModel) element;
        ZooKeeperConnectionDescriptor descriptor = model.getData().getDescriptor();
        return getText(element) + " [" + descriptor.getConnectString() + "]";
    }

}
