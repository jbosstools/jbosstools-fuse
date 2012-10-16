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


import org.eclipse.swt.widgets.Composite;
import org.fusesource.ide.zk.zookeeper.data.Znode;
import org.fusesource.ide.zk.zookeeper.model.ZnodeModel;
import org.fusesource.ide.zk.core.widgets.grid.GridComposite;

/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public class ZnodeNewWizardPage1 extends ZnodeNewWizardPage {
    
    /**
     * TODO: Comment.
     * 
     * @param wizard
     */
    public ZnodeNewWizardPage1(ZnodeNewWizard wizard, ZnodeModel parentZnodeModel) {
        super(wizard, parentZnodeModel);
    }
    
    public Znode getZnode() throws Exception {
        return ((ZnodeNewWizardComposite1) getGridComposite()).getZnode();
    }

    @Override
    protected GridComposite createGridComposite(Composite parent) {
        return new ZnodeNewWizardComposite1(parent, getParentZnodeModel());
    }

}