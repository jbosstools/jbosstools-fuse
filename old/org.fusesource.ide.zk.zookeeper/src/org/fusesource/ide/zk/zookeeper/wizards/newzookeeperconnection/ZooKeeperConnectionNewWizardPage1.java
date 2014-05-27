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

import org.eclipse.swt.widgets.Composite;
import org.fusesource.ide.zk.zookeeper.data.ZooKeeperConnectionDescriptor;
import org.fusesource.ide.zk.core.widgets.grid.GridComposite;
import org.fusesource.ide.zk.core.wizards.GridWizardPage;


/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public class ZooKeeperConnectionNewWizardPage1 extends GridWizardPage {

    /**
     * TODO: Comment.
     * 
     * @param wizard
     */
    public ZooKeeperConnectionNewWizardPage1(ZooKeeperConnectionNewWizard wizard) {
        super(wizard);
    }

    public ZooKeeperConnectionDescriptor getConnectionDescriptor() {
        ZooKeeperConnectionComposite1 composite = (ZooKeeperConnectionComposite1) getGridComposite();
        return composite.getConnectionDescriptor();
    }

    @Override
    protected GridComposite createGridComposite(Composite parent) {
        return new ZooKeeperConnectionComposite1(parent);
    }

}