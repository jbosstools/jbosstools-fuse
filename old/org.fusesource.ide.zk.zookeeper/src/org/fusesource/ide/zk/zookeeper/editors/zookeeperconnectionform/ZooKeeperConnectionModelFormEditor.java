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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PartInitException;
import org.fusesource.ide.zk.zookeeper.ZooKeeperActivator;
import org.fusesource.ide.zk.zookeeper.data.AuthInfo;
import org.fusesource.ide.zk.zookeeper.data.Znode;
import org.fusesource.ide.zk.zookeeper.data.ZooKeeperConnectionDescriptor;
import org.fusesource.ide.zk.zookeeper.data.ZooKeeperServerDescriptor;
import org.fusesource.ide.zk.zookeeper.model.ZooKeeperConnectionModel;
import org.fusesource.ide.zk.core.editors.DataModelFormEditor;
import org.fusesource.ide.zk.core.model.GenericDataModelEvent;

import java.util.List;
import java.util.Set;


/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public class ZooKeeperConnectionModelFormEditor extends DataModelFormEditor<ZooKeeperConnectionModel> {

    public static final String ID = ZooKeeperConnectionModelFormEditor.class.getName()
            + ZooKeeperActivator.VERSION_SUFFIX;

    private ZooKeeperConnectionModelAuthenticationFormPage _AuthenticationPage;
    private ZooKeeperConnectionModelMainFormPage _MainPage;
    private ZooKeeperConnectionModelServersFormPage _ServersPage;

    // private ZnodeModelElementType _ZnodeModelElementType;

    @Override
    public void doSave(IProgressMonitor monitor) {

        ZooKeeperConnectionModel model = getModel();

        if (model.isDestroyed()) {
            return;
        }

        ZooKeeperConnectionDescriptor descriptor = model.getKey();

        int sessionTimeout = descriptor.getSessionTimeout();
        String rootPath = descriptor.getRootPath();
        Set<ZooKeeperServerDescriptor> servers = descriptor.getServers();
        List<AuthInfo> authInfos = descriptor.getAuthInfos();

        String errorMessageTitle = "Save Failed";

        if (_MainPage.isDirty()) {

            sessionTimeout = _MainPage.getSessionTimeout();

            if (sessionTimeout <= 0) {
                MessageDialog.openError(getSite().getShell(), errorMessageTitle, "Invalid session timeout value.");
                monitor.setCanceled(true);
                return;
            }

            rootPath = _MainPage.getRootPath();

            try {
                Znode.validatePath(rootPath, false);
            }
            catch (IllegalArgumentException e) {
                MessageDialog.openError(getSite().getShell(), errorMessageTitle, "Invalid root path: "
                        + e.getLocalizedMessage());
                monitor.setCanceled(true);
                return;
            }

        }

        if (_ServersPage.isDirty()) {
            servers = _ServersPage.getServersFromTable();

            if (servers.isEmpty()) {
                MessageDialog.openError(getSite().getShell(), errorMessageTitle, "At least one server must be added.");
                monitor.setCanceled(true);
                return;
            }
        }

        if (_AuthenticationPage.isDirty()) {
            authInfos = _AuthenticationPage.getAuthInfoFromTable();
        }

        try {

            if (_MainPage.isDirty()) {
                descriptor.setRootPath(rootPath);
                descriptor.setSessionTimeout(sessionTimeout);
            }

            if (_ServersPage.isDirty()) {
                descriptor.setServers(servers);
            }

            if (_AuthenticationPage.isDirty()) {
                descriptor.setAuthInfos(authInfos);
            }

            model.updateData();

            saveCompleted();
        }
        catch (Exception e) {
            ZooKeeperActivator.reportError(e);
            monitor.setCanceled(true);
        }
    }

    // TODO: Implement "save as" using below as a starting point...

    // @Override
    // public void doSave(IProgressMonitor monitor) {
    //
    // ZooKeeperConnectionModel model = getModel();
    //
    // if (model.isDestroyed()) {
    // return;
    // }
    //
    // // The ZooKeeperConnectionDescriptor is the ZooKeeperConnectionModel's key and DataModel keys are immutable.
    // // Therefore we cannot simply edit the descriptor but rather we need to create a new one. A new key also means a
    // // new ZooKeeperConnectionModel, which is a bit weird because the user is conceptually saving the same model
    // // that they started editing but under the covers we need to destroy that model and create a new one. Basically
    // // we are doing a "Save As" here.
    //
    // ZooKeeperConnectionDescriptor oldDescriptor = model.getKey();
    // ZooKeeperConnectionDescriptor newDescriptor;
    //
    // if (_MainPage.isDirty()) {
    //
    // String name = _MainPage.getName();
    // int sessionTimeout = _MainPage.getSessionTimeout();
    // newDescriptor = new ZooKeeperConnectionDescriptor(name, sessionTimeout);
    //
    // String rootPath = _MainPage.getRootPath();
    // newDescriptor.setRootPath(rootPath);
    // }
    // else {
    // newDescriptor = new ZooKeeperConnectionDescriptor(oldDescriptor.getName(), oldDescriptor
    // .getSessionTimeout());
    // newDescriptor.setRootPath(oldDescriptor.getRootPath());
    // }
    //
    // if (_ServersPage.isDirty()) {
    // Set<ZooKeeperServerDescriptor> servers = _ServersPage.getServersFromTable();
    // newDescriptor.setServers(servers);
    // }
    // else {
    // newDescriptor.setServers(oldDescriptor.getServers());
    // }
    //
    // if (_AuthenticationPage.isDirty()) {
    // List<AuthInfo> authInfos = _AuthenticationPage.getAuthInfoFromTable();
    // newDescriptor.setAuthInfos(authInfos);
    // }
    // else {
    // newDescriptor.setAuthInfos(oldDescriptor.getAuthInfos());
    // }
    //
    // try {
    //
    // ZooKeeperActivator plugin = ZooKeeperActivator.getDefault();
    // ZooKeeperConnectionDescriptorFiles files = plugin.getZooKeeperConnectionDescriptorFiles();
    //
    // // Delete the old descriptor file
    // files.delete(oldDescriptor);
    //
    // // Save the new descriptor file.
    // files.save(newDescriptor);
    //
    // // For completeness finish the save.
    // saveCompleted();
    //
    // // Destroy the old model.
    // model.destroy();
    //
    // // Create the new model.
    // ZooKeeperConnectionModel newModel = plugin.getZooKeeperConnectionModelManager().getModel(newDescriptor);
    //
    // // Open the new model.
    // _ZnodeModelElementType.getOpenAction().runWithObject(newModel);
    // }
    // catch (Exception e) {
    // ZooKeeperActivator.reportError(e);
    // monitor.setCanceled(true);
    // }
    // finally {
    // // Regardless of what else happens we need to close this editor because this save is a one-way trip.
    // close(false);
    // }
    // }

    @Override
    protected void addPages() {
        try {
            _MainPage = new ZooKeeperConnectionModelMainFormPage(this);
            addPage(_MainPage);

            _ServersPage = new ZooKeeperConnectionModelServersFormPage(this);
            addPage(_ServersPage);

            _AuthenticationPage = new ZooKeeperConnectionModelAuthenticationFormPage(this);
            addPage(_AuthenticationPage);
        }
        catch (PartInitException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected void modelDestroyed(GenericDataModelEvent event) {
        super.modelDestroyed(event);
        close(false);
    }
}
