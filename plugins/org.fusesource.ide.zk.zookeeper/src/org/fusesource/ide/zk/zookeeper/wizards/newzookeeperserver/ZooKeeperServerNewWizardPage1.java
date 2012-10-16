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

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.fusesource.ide.zk.zookeeper.data.ZooKeeperServerDescriptor;
import org.fusesource.ide.zk.core.widgets.grid.GridComposite;
import org.fusesource.ide.zk.core.widgets.grid.GridTextInput;
import org.fusesource.ide.zk.core.wizards.GridWizardPage;


/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public class ZooKeeperServerNewWizardPage1 extends GridWizardPage {

    private static final String CONTROL_NAME_HOST_TEXT = "Host";
    private static final String CONTROL_NAME_PORT_TEXT = "Port";

    /**
     * TODO: Comment.
     * 
     * @param wizard
     */
    public ZooKeeperServerNewWizardPage1(ZooKeeperServerNewWizard wizard) {
        super(wizard);
    }

    public String getHost() {
        Text hostText = (Text) getGridComposite().getControl(CONTROL_NAME_HOST_TEXT);
        String host = hostText.getText();
        return host.trim();
    }

    public int getPort() {
        Text portText = (Text) getGridComposite().getControl(CONTROL_NAME_PORT_TEXT);
        int port = Integer.parseInt(portText.getText());
        return port;
    }

    @Override
    public boolean canFlipToNextPage() {

        ZooKeeperServerNewWizardPage2 jmxPage = (ZooKeeperServerNewWizardPage2) getNextPage();

        if (jmxPage != null) {
            GridComposite jmxPageGridComposite = jmxPage.getGridComposite();

            if (jmxPageGridComposite != null) {

                String hostPortString = getHost() + ":" + String.valueOf(ZooKeeperServerDescriptor.DEFAULT_JMX_PORT);
                String defaultJmxServiceUrlString = "service:jmx:rmi:///jndi/rmi://" + hostPortString + "/jmxrmi";

                Text jmxUrlText = (Text) jmxPageGridComposite
                        .getControl(ZooKeeperServerNewWizardPage2.CONTROL_NAME_JMX_URL_TEXT);
                
                if (jmxUrlText != null && !jmxUrlText.isDisposed()) {
                    jmxUrlText.setText(defaultJmxServiceUrlString);
                }
            }
        }

        return super.canFlipToNextPage();
    }

    @Override
    protected GridComposite createGridComposite(Composite parent) {

        GridComposite gridComposite = new GridComposite(parent) {

            @Override
            protected void createContents() {

                GridTextInput hostGridTextInput = new GridTextInput(this, GridTextInput.Type.VALUE_REQUIRED,
                        CONTROL_NAME_HOST_TEXT, "&Host:", ZooKeeperServerDescriptor.DEFAULT_HOST);
                addGridTextInput(hostGridTextInput);
                Text hostText = hostGridTextInput.getText();
                hostText.selectAll();
                hostText.setTextLimit(500);

                GridTextInput portGridTextInput = new GridTextInput(this, GridTextInput.Type.INTEGER_VALUE_REQUIRED,
                        CONTROL_NAME_PORT_TEXT, "&Port:", String.valueOf(ZooKeeperServerDescriptor.DEFAULT_PORT));
                addGridTextInput(portGridTextInput);
                Text portText = portGridTextInput.getText();
                portText.selectAll();

            }
        };

        return gridComposite;
    }
}