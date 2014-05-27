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

package org.fusesource.ide.zk.zookeeper.wizards.newzookeeperserver;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.fusesource.ide.zk.zookeeper.ZooKeeperActivator;
import org.fusesource.ide.zk.zookeeper.data.ZooKeeperServerDescriptor;
import org.fusesource.ide.zk.core.EclipseCoreActivator;
import org.fusesource.ide.zk.core.wizards.AbstractWizard;
import org.fusesource.ide.zk.jmx.data.JmxConnectionDescriptor;

import java.util.UUID;

import javax.management.remote.JMXServiceURL;


public class ZooKeeperServerNewWizard extends AbstractWizard implements INewWizard {

    public static final String ID = ZooKeeperServerNewWizard.class.getName() + ZooKeeperActivator.VERSION_SUFFIX;

    public static final String DESCRIPTION = "This wizard adds a new ZooKeeper server to the connection.";
    public static final String TITLE = "Add ZooKeeper Server Wizard";

    private static final String JMX_CONNECTION_NAME_PREFIX = "ZooKeeper Server - ";
    
    private ZooKeeperServerNewWizardPage1 _Page1;
    private ZooKeeperServerNewWizardPage2 _Page2;

    private ZooKeeperServerDescriptor _ServerDescriptor;

    /**
     * Constructor for ZooKeeperConnectionNewWizard.
     */
    public ZooKeeperServerNewWizard() {
        super(TITLE, DESCRIPTION, EclipseCoreActivator
                .getManagedImageDescriptor(EclipseCoreActivator.IMAGE_KEY_WIZARD_BANNER_ADD_SERVER));
    }

    @Override
    public void addPages() {
        _Page1 = new ZooKeeperServerNewWizardPage1(this);
        addPage(_Page1);
        _Page2 = new ZooKeeperServerNewWizardPage2(this);
        addPage(_Page2);
    }

    /**
     * Returns the serverDescriptor.
     * 
     * @return The serverDescriptor
     */
    public ZooKeeperServerDescriptor getServerDescriptor() {
        return _ServerDescriptor;
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
    }

    @Override
    public boolean performFinish() {

        String host = _Page1.getHost();
        int port = _Page1.getPort();

        _ServerDescriptor = new ZooKeeperServerDescriptor(host, port);

        if (_Page2.isJmxEnabled()) {
            JMXServiceURL jmxServiceUrl = _Page2.getServiceUrl();
            String userName = _Page2.getUserName();
            String password = _Page2.getPassword();

            // Generate a unique name
            String name = JMX_CONNECTION_NAME_PREFIX + UUID.randomUUID().toString();

            JmxConnectionDescriptor jmxConnectionDescriptor = new JmxConnectionDescriptor(name, jmxServiceUrl,
                    userName, password);
            _ServerDescriptor.setJmxConnectionDescriptor(jmxConnectionDescriptor);
        }

        return true;
    }

}