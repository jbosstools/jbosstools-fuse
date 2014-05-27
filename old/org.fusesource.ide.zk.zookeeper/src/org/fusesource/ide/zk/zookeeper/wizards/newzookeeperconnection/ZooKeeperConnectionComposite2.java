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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.fusesource.ide.zk.zookeeper.data.AuthInfo;
import org.fusesource.ide.zk.zookeeper.widgets.ZooKeeperConnectionAuthInfoComposite;
import org.fusesource.ide.zk.core.widgets.grid.GridComposite;

import java.util.List;


/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public class ZooKeeperConnectionComposite2 extends GridComposite {

    private ZooKeeperConnectionAuthInfoComposite _AuthInfoComposite;

    /**
     * TODO: Comment.
     * 
     * @param parent
     */
    public ZooKeeperConnectionComposite2(Composite parent) {
        super(parent);
        setNumColumns(1);
    }

    public List<AuthInfo> getAuthInfos() {
        return _AuthInfoComposite.getElementList();
    }

    @Override
    protected void createContents() {

        Label authInfoLabel = new Label(this, SWT.LEAD);
        authInfoLabel.setText("Auth Info:");
        authInfoLabel.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

        _AuthInfoComposite = new ZooKeeperConnectionAuthInfoComposite(this, SWT.NULL);
        _AuthInfoComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        GridLayout authInfoCompositeLayout = new GridLayout(2, false);
        authInfoCompositeLayout.marginWidth = 0;
        authInfoCompositeLayout.marginHeight = 0;
        authInfoCompositeLayout.horizontalSpacing = ((GridLayout) getLayout()).horizontalSpacing;
        _AuthInfoComposite.setLayout(authInfoCompositeLayout);

        Table table = new Table(_AuthInfoComposite, SWT.SINGLE | SWT.FULL_SELECTION | SWT.BORDER | SWT.H_SCROLL
                | SWT.V_SCROLL);
        GridData tableLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2);
        tableLayoutData.heightHint = 200;
        table.setLayoutData(tableLayoutData);
        _AuthInfoComposite.setTable(table);

        Button addButton = new Button(_AuthInfoComposite, SWT.NULL);
        addButton.setText("Add...");
        addButton.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, false, false));
        _AuthInfoComposite.setAddButton(addButton);

        final Button removeButton = new Button(_AuthInfoComposite, SWT.NULL);
        removeButton.setText("Remove");
        removeButton.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, false, false));
        _AuthInfoComposite.setRemoveButton(removeButton);

        _AuthInfoComposite.init();
    }

}
