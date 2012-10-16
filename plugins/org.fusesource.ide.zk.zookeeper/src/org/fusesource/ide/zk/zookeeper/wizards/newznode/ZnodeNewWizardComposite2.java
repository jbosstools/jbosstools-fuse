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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.fusesource.ide.zk.zookeeper.model.ZnodeModel;
import org.fusesource.ide.zk.zookeeper.widgets.ZnodeAclComposite;
import org.fusesource.ide.zk.zookeeper.widgets.OrchestrationComposite.IOrchestrationCompositeListener;
import org.fusesource.ide.zk.core.widgets.grid.GridCompositeStatus;

import java.util.EventObject;
import java.util.List;

import org.apache.zookeeper.data.ACL;

/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public class ZnodeNewWizardComposite2 extends ZnodeNewWizardComposite {

    private ZnodeAclComposite _ZnodeAclComposite;

    /**
     * TODO: Comment.
     * 
     * @param parent
     */
    public ZnodeNewWizardComposite2(Composite parent, ZnodeModel parentZnodeModel) {
        super(parent, parentZnodeModel);
    }

    public List<ACL> getAcl() throws Exception {
        return _ZnodeAclComposite.getZnodeAclFromTable();
    }

    @Override
    protected void createContents() {

        super.createContents();

        Label aclLabel = new Label(this, SWT.LEAD);
        aclLabel.setText("ACL: ");
        aclLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

        _ZnodeAclComposite = new ZnodeAclComposite(this, SWT.NONE);

        GridLayout aclGridLayout = new GridLayout(2, false);
        aclGridLayout.marginWidth = 0;
        aclGridLayout.marginHeight = 0;
        aclGridLayout.horizontalSpacing = ((GridLayout) getLayout()).horizontalSpacing;
        _ZnodeAclComposite.setLayout(aclGridLayout);

        Table table = new Table(_ZnodeAclComposite, SWT.SINGLE | SWT.FULL_SELECTION | SWT.BORDER);
        String[] titles = ZnodeAclComposite.TABLE_COLUMN_TITLES;
        if (titles != null && titles.length > 0) {
            for (int i = 0; i < titles.length; i++) {
                TableColumn column = new TableColumn(table, SWT.NONE);
                column.setText(titles[i]);
            }

            table.setHeaderVisible(true);
        }
        table.setLinesVisible(true);
        _ZnodeAclComposite.setTable(table);

        _ZnodeAclComposite.addOrchestrationCompositeListener(new IOrchestrationCompositeListener() {

            @Override
            public void orchestrationChange(EventObject e) {
                modified(null);

            }
        });

        Button addButton = new Button(_ZnodeAclComposite, SWT.PUSH);
        addButton.setText("&Add");
        _ZnodeAclComposite.setAddButton(addButton);

        final Button removeButton = new Button(_ZnodeAclComposite, SWT.PUSH);
        removeButton.setText("&Remove");
        _ZnodeAclComposite.setRemoveButton(removeButton);

        final Button setIdButton = new Button(_ZnodeAclComposite, SWT.PUSH);
        setIdButton.setText("&Set Id...");
        _ZnodeAclComposite.setSetIdButton(setIdButton);

        _ZnodeAclComposite.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, true, true, 3, 0));
        GridData tableGridData = new GridData(SWT.BEGINNING, SWT.BEGINNING, true, true, 1, 10);
        tableGridData.minimumHeight = 150;
        table.setLayoutData(tableGridData);

        final GridData addButtonGridData = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
        addButton.setLayoutData(addButtonGridData);

        final GridData removeButtonGridData = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
        removeButton.setLayoutData(removeButtonGridData);

        setIdButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));

        setIdButton.addControlListener(new ControlListener() {

            @Override
            public void controlMoved(ControlEvent e) {
            }

            @Override
            public void controlResized(ControlEvent e) {
                int setIdButtonWidth = setIdButton.getSize().x;
                addButtonGridData.widthHint = setIdButtonWidth;
                removeButtonGridData.widthHint = setIdButtonWidth;
            }
        });

        _ZnodeAclComposite.init();

        ZnodeModel parentZnodeModel = getParentZnodeModel();

        table.setRedraw(false);
        try {
            for (ACL acl : parentZnodeModel.getData().getAcl()) {
                _ZnodeAclComposite.addAclTableItem(acl);
            }
        }
        finally {
            table.setRedraw(true);
        }
        table.pack();
        _ZnodeAclComposite.layout(true);

        int[] columnWidths = ZnodeAclComposite.TABLE_COLUMN_WIDTHS;
        TableColumn[] columns = table.getColumns();
        for (int i = 0; i < columns.length; i++) {

            if (columnWidths == null) {
                columns[i].pack();
            }
            else {
                int columnWidth = columnWidths[i];
                if (columnWidth == SWT.DEFAULT) {
                    columns[i].pack();
                }
                else {
                    columns[i].setWidth(columnWidth);
                }
            }
        }

    }

    @Override
    protected GridCompositeStatus updateStatus(Object source) {

        GridCompositeStatus status = super.updateStatus(source);
        if (status.getType().isError()) {
            return status;
        }

        if (_ZnodeAclComposite.getTable().getItemCount() == 0) {
            return new GridCompositeStatus(null, "ACL cannot be empty.", GridCompositeStatus.Type.ERROR_INVALID);
        }

        return GridCompositeStatus.OK_STATUS;
    }

}
