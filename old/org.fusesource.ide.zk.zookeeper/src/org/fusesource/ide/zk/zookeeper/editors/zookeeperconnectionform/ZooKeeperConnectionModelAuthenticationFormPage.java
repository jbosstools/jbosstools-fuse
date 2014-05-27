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

package org.fusesource.ide.zk.zookeeper.editors.zookeeperconnectionform;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.fusesource.ide.zk.zookeeper.ZooKeeperActivator;
import org.fusesource.ide.zk.zookeeper.data.AuthInfo;
import org.fusesource.ide.zk.zookeeper.widgets.TableViewerOrchestrationComposite;
import org.fusesource.ide.zk.zookeeper.widgets.ZooKeeperConnectionAuthInfoComposite;

import java.util.Collection;
import java.util.List;


public final class ZooKeeperConnectionModelAuthenticationFormPage extends
        TableViewerOrchestrationZooKeeperConnectionModelFormPage<AuthInfo> {

    public static final String ID = ZooKeeperConnectionModelAuthenticationFormPage.class.getName();
    public static final Image IMAGE = ZooKeeperActivator.getManagedImage(ZooKeeperActivator.IMAGE_KEY_OBJECT_AUTH);
    public static final String TITLE = "Authentication";

    public ZooKeeperConnectionModelAuthenticationFormPage(ZooKeeperConnectionModelFormEditor editor) {
        super(editor, ID, TITLE);
        setImage(IMAGE);
    }

    public List<AuthInfo> getAuthInfoFromTable() {
        return getElementList();
    }

    @Override
    protected TableViewerOrchestrationComposite<AuthInfo> createTableViewerOrchestrationComposite(Composite client) {
        return new ZooKeeperConnectionAuthInfoComposite(client, SWT.NULL);
    }

    @Override
    protected Collection<AuthInfo> getElementsFromModel() {
        return getModel().getKey().getAuthInfos();
    }

}
