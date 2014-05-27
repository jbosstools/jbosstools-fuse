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

import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.fusesource.ide.zk.zookeeper.ZooKeeperActivator;
import org.fusesource.ide.zk.zookeeper.data.ZooKeeperServerDescriptor;
import org.fusesource.ide.zk.zookeeper.model.ZooKeeperConnectionModel;
import org.fusesource.ide.zk.zookeeper.model.ZooKeeperServerModel;
import org.fusesource.ide.zk.zookeeper.viewers.ZooKeeperServerModelElementType;
import org.fusesource.ide.zk.zookeeper.widgets.TableViewerOrchestrationComposite;
import org.fusesource.ide.zk.zookeeper.widgets.ZooKeeperConnectionServerComposite;
import org.fusesource.ide.zk.core.EclipseCoreActivator;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;


public final class ZooKeeperConnectionModelServersFormPage extends
        TableViewerOrchestrationZooKeeperConnectionModelFormPage<ZooKeeperServerDescriptor> {

    public static final String ID = ZooKeeperConnectionModelServersFormPage.class.getName();

    public static final Image IMAGE = EclipseCoreActivator
            .getManagedImage(EclipseCoreActivator.IMAGE_KEY_OBJECT_SERVERS);
    public static final String TITLE = "Servers";

    private ZooKeeperServerModelElementType _ZooKeeperServerModelElementType;

    public ZooKeeperConnectionModelServersFormPage(ZooKeeperConnectionModelFormEditor editor) {
        super(editor, ID, TITLE);
        setImage(IMAGE);

        _ZooKeeperServerModelElementType = new ZooKeeperServerModelElementType();
    }

    public Set<ZooKeeperServerDescriptor> getServersFromTable() {
        return getElementSet();
    }

    @Override
    protected void createModelFormContent(IManagedForm managedForm, Composite client) {
        super.createModelFormContent(managedForm, client);

        ZooKeeperConnectionServerComposite composite = (ZooKeeperConnectionServerComposite) getTableViewerOrchestrationComposite();
        TableViewer serverDescriptorTableViewer = composite.getTableViewer();
        serverDescriptorTableViewer.addOpenListener(new IOpenListener() {

            @Override
            public void open(OpenEvent event) {
                openSelectedServers(event.getSelection());
            }
        });

    }

    @Override
    protected TableViewerOrchestrationComposite<ZooKeeperServerDescriptor> createTableViewerOrchestrationComposite(
            Composite client) {

        return new ZooKeeperConnectionServerComposite(client, SWT.NULL);
    }

    @Override
    protected Collection<ZooKeeperServerDescriptor> getElementsFromModel() {
        return getModel().getKey().getServers();
    }

    private void openSelectedServers(ISelection selection) {
        IStructuredSelection structuredSelection = (IStructuredSelection) selection;
        Iterator<?> selectionIterator = structuredSelection.iterator();

        ZooKeeperConnectionModel connectionModel = getModel();

        while (selectionIterator.hasNext()) {
            ZooKeeperServerDescriptor descriptor = (ZooKeeperServerDescriptor) selectionIterator.next();

            if (!connectionModel.getKey().getServers().contains(descriptor)) {
                continue;
            }

            ZooKeeperServerModel serverModel = getModel().getZooKeeperServerModel(descriptor);
            if (serverModel == null) {
                continue;
            }

            try {
                _ZooKeeperServerModelElementType.getOpenAction().runWithObject(serverModel);
            }
            catch (Exception e) {
                ZooKeeperActivator.reportError(e);
                break;
            }
        }

    }

}
