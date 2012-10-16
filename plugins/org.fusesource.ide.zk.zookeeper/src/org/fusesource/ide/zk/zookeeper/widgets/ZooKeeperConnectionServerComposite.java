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

package org.fusesource.ide.zk.zookeeper.widgets;

import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Composite;
import org.fusesource.ide.zk.zookeeper.data.ZooKeeperServerDescriptor;
import org.fusesource.ide.zk.zookeeper.viewers.ZooKeeperServerDescriptorElementType;
import org.fusesource.ide.zk.zookeeper.wizards.newzookeeperserver.ZooKeeperServerNewWizard;


public class ZooKeeperConnectionServerComposite extends TableViewerOrchestrationComposite<ZooKeeperServerDescriptor> {

    public ZooKeeperConnectionServerComposite(Composite parent, int style) {
        super(parent, style, ZooKeeperServerDescriptor.class, new ZooKeeperServerDescriptorElementType());
    }

    @Override
    protected ZooKeeperServerDescriptor addElement() {
        ZooKeeperServerNewWizard serverWizard = new ZooKeeperServerNewWizard();

        WizardDialog wizardDialog = new WizardDialog(getShell(), serverWizard);
        wizardDialog.setBlockOnOpen(true);
        if (wizardDialog.open() == Window.OK) {

            return serverWizard.getServerDescriptor();
        }

        return null;
    }
}