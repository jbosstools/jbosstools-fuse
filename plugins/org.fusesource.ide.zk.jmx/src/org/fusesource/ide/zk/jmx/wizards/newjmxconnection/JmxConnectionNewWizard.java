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

package org.fusesource.ide.zk.jmx.wizards.newjmxconnection;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.fusesource.ide.zk.jmx.JmxActivator;
import org.fusesource.ide.zk.jmx.data.JmxConnection;
import org.fusesource.ide.zk.jmx.data.JmxConnectionDescriptor;
import org.fusesource.ide.zk.jmx.model.JmxConnectionModel;
import org.fusesource.ide.zk.jmx.views.explorer.JmxExplorerView;
import org.fusesource.ide.zk.core.model.DataModelManager;
import org.fusesource.ide.zk.core.wizards.AbstractWizard;

import javax.management.remote.JMXServiceURL;


public class JmxConnectionNewWizard extends AbstractWizard implements INewWizard {

    public static final String ID = JmxConnectionNewWizard.class.getName();

    public static final String DESCRIPTION = "This wizard creates a new JMX connection.";
    public static final String TITLE = "New JMX Connection Wizard";

    private JmxConnectionNewWizardPage1 _Page1;

    /**
     * Constructor for JmxConnectionNewWizard.
     */
    public JmxConnectionNewWizard() {
        super(TITLE, DESCRIPTION, JmxActivator
                .getManagedImageDescriptor(JmxActivator.IMAGE_KEY_WIZARD_BANNER_NEW_JMX_CONNECTION));
    }

    @Override
    public void addPages() {
        _Page1 = new JmxConnectionNewWizardPage1(this);
        addPage(_Page1);
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
    }

    @Override
    public boolean performFinish() {

        
        String name = _Page1.getConnectionName();
        JMXServiceURL serviceURL = _Page1.getServiceUrl();
        String userName = _Page1.getUserName();
        String password = (userName != null) ? _Page1.getPassword() : null;

        JmxConnectionDescriptor connectionDescriptor = new JmxConnectionDescriptor(name, serviceURL, userName, password);
        
        JmxActivator plugin = JmxActivator.getDefault();

        DataModelManager<JmxConnectionModel, JmxConnectionDescriptor, JmxConnection> zooKeeperConnectionModelManager = plugin
                .getJmxConnectionModelManager();

        zooKeeperConnectionModelManager.insertData(connectionDescriptor, null);

        // Force the new model creation
        zooKeeperConnectionModelManager.getModel(connectionDescriptor);

        IWorkbenchPage page = plugin.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        try {
            page.showView(JmxExplorerView.ID);
        }
        catch (PartInitException e) {
            JmxActivator.reportError(e);
        }

        return true;
    }

}