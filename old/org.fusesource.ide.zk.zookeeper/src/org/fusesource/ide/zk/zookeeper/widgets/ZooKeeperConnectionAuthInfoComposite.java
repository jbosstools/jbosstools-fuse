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

import org.eclipse.swt.widgets.Composite;
import org.fusesource.ide.zk.zookeeper.data.AuthInfo;
import org.fusesource.ide.zk.zookeeper.dialogs.AddAuthInfoDialog;
import org.fusesource.ide.zk.zookeeper.viewers.AuthInfoElementType;


public class ZooKeeperConnectionAuthInfoComposite extends TableViewerOrchestrationComposite<AuthInfo> {

    public ZooKeeperConnectionAuthInfoComposite(Composite parent, int style) {
        super(parent, style, AuthInfo.class, new AuthInfoElementType());
    }

    @Override
    protected AuthInfo addElement() {

        AddAuthInfoDialog dialog = new AddAuthInfoDialog(getShell());
        dialog.setBlockOnOpen(true);
        if (dialog.open() == AddAuthInfoDialog.OK) {
            return dialog.getAuthInfo();
        }

        return null;
    }

}
