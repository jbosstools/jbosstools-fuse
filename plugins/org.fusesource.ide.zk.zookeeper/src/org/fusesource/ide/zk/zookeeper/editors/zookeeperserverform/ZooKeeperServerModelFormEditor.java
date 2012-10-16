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

package org.fusesource.ide.zk.zookeeper.editors.zookeeperserverform;

import org.eclipse.ui.PartInitException;
import org.fusesource.ide.zk.zookeeper.ZooKeeperActivator;
import org.fusesource.ide.zk.zookeeper.model.ZooKeeperServerModel;
import org.fusesource.ide.zk.zookeeper.viewers.ZooKeeperConnectionModelElementType;
import org.fusesource.ide.zk.core.editors.DataModelFormEditor;
import org.fusesource.ide.zk.core.model.GenericDataModelEvent;
import org.fusesource.ide.zk.core.viewers.DataModelElementType;


/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public class ZooKeeperServerModelFormEditor extends DataModelFormEditor<ZooKeeperServerModel> {

    public static final String ID = ZooKeeperServerModelFormEditor.class.getName() + ZooKeeperActivator.VERSION_SUFFIX;

    private ZooKeeperServerModelFormPage _Page;

    @Override
    protected void addPages() {
        try {
            _Page = new ZooKeeperServerModelFormPage(this);
            addPage(_Page);
        }
        catch (PartInitException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected DataModelElementType getParentModelElementType() {
        return new ZooKeeperConnectionModelElementType();
    }

    @Override
    protected void modelDestroyed(GenericDataModelEvent event) {
        super.modelDestroyed(event);
        close(false);
    }
}
