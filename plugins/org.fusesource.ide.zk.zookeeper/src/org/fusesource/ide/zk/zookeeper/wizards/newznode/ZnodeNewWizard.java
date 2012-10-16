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

package org.fusesource.ide.zk.zookeeper.wizards.newznode;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.fusesource.ide.zk.zookeeper.ZooKeeperActivator;
import org.fusesource.ide.zk.zookeeper.data.Znode;
import org.fusesource.ide.zk.zookeeper.model.ZnodeModel;
import org.fusesource.ide.zk.zookeeper.views.explorer.ZooKeeperExplorerView;
import org.fusesource.ide.zk.core.wizards.AbstractWizard;

import java.util.List;

import org.apache.zookeeper.data.ACL;

public class ZnodeNewWizard extends AbstractWizard implements INewWizard {

    public static final String ID = ZnodeNewWizard.class.getName() + ZooKeeperActivator.VERSION_SUFFIX;

    public static final String DESCRIPTION = "This wizard creates a new Znode.";
    public static final String TITLE = "New Znode Wizard";

    private ZnodeModel _ParentZnodeModel;
    private ZnodeNewWizardPage1 _Page1;
    private ZnodeNewWizardPage2 _Page2;

    /**
     * Constructor for ZooKeeperConnectionNewWizard.
     */
    public ZnodeNewWizard() {
        super(TITLE, DESCRIPTION, ZooKeeperActivator
                .getManagedImageDescriptor(ZooKeeperActivator.IMAGE_KEY_WIZARD_BANNER_NEW_ZNODE));
    }

    @Override
    public void addPages() {
        _Page1 = new ZnodeNewWizardPage1(this, _ParentZnodeModel);
        _Page2 = new ZnodeNewWizardPage2(this, _ParentZnodeModel);
        addPage(_Page1);
        addPage(_Page2);
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        Object firstElement = selection.getFirstElement();
        _ParentZnodeModel = (ZnodeModel) firstElement;
    }

    @Override
    public boolean performFinish() {

        if (_ParentZnodeModel.isDestroyed()) {
            return false;
        }

        ZooKeeperActivator plugin = ZooKeeperActivator.getDefault();

        Znode znode = null;
        try {
            znode = _Page1.getZnode();
            List<ACL> acl = _Page2.getAcl();
            znode.setAcl(acl);
            _ParentZnodeModel.getManager().insertData(znode.getPath(), znode);
        }
        catch (Exception e) {
            ZooKeeperActivator.reportError(e);
            return false;
        }

        IWorkbenchPage page = plugin.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        try {
            page.showView(ZooKeeperExplorerView.ID);
        }
        catch (PartInitException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return true;
    }

}