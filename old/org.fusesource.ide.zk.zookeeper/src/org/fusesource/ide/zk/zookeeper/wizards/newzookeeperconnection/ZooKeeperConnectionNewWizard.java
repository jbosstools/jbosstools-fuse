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

package org.fusesource.ide.zk.zookeeper.wizards.newzookeeperconnection;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.fusesource.ide.zk.zookeeper.ZooKeeperActivator;
import org.fusesource.ide.zk.zookeeper.data.AuthInfo;
import org.fusesource.ide.zk.zookeeper.data.ZooKeeperConnection;
import org.fusesource.ide.zk.zookeeper.data.ZooKeeperConnectionDescriptor;
import org.fusesource.ide.zk.zookeeper.model.ZooKeeperConnectionModel;
import org.fusesource.ide.zk.zookeeper.views.explorer.ZooKeeperExplorerView;
import org.fusesource.ide.zk.core.model.DataModelManager;
import org.fusesource.ide.zk.core.wizards.AbstractWizard;

import java.util.List;


public class ZooKeeperConnectionNewWizard extends AbstractWizard implements INewWizard {

    public static final String ID = ZooKeeperConnectionNewWizard.class.getName() + ZooKeeperActivator.VERSION_SUFFIX;

    public static final String DESCRIPTION = "This wizard creates a new ZooKeeper connection.";
    public static final String TITLE = "New ZooKeeper Connection Wizard";

    private ZooKeeperConnectionNewWizardPage1 _Page1;
    private ZooKeeperConnectionNewWizardPage2 _Page2;

    /**
     * Constructor for ZooKeeperConnectionNewWizard.
     */
    public ZooKeeperConnectionNewWizard() {
        super(TITLE, DESCRIPTION, ZooKeeperActivator
                .getManagedImageDescriptor(ZooKeeperActivator.IMAGE_KEY_WIZARD_BANNER_NEW_ZOO_KEEPER_CONNECTION));
    }

    @Override
    public void addPages() {
        _Page1 = new ZooKeeperConnectionNewWizardPage1(this);
        addPage(_Page1);

        _Page2 = new ZooKeeperConnectionNewWizardPage2(this);
        addPage(_Page2);

    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
    }

    @Override
    public boolean performFinish() {

        ZooKeeperConnectionDescriptor connectionDescriptor = _Page1.getConnectionDescriptor();

        List<AuthInfo> authInfos = _Page2.getAuthInfos();
        if (authInfos != null && authInfos.size() > 0) {
            connectionDescriptor.setAuthInfos(authInfos);
        }

        ZooKeeperActivator plugin = ZooKeeperActivator.getDefault();

        DataModelManager<ZooKeeperConnectionModel, ZooKeeperConnectionDescriptor, ZooKeeperConnection> zooKeeperConnectionModelManager = plugin
                .getZooKeeperConnectionModelManager();

        zooKeeperConnectionModelManager.insertData(connectionDescriptor, null);

        // Force the new model creation
        zooKeeperConnectionModelManager.getModel(connectionDescriptor);

        IWorkbenchPage page = plugin.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        try {
            page.showView(ZooKeeperExplorerView.ID);
        }
        catch (PartInitException e) {
            ZooKeeperActivator.reportError(e);
        }

        return true;
    }

}