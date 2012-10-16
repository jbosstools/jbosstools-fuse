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

package org.fusesource.ide.zk.zookeeper.editors.znodetable;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.fusesource.ide.zk.zookeeper.ZooKeeperActivator;
import org.fusesource.ide.zk.zookeeper.data.Znode;
import org.fusesource.ide.zk.zookeeper.model.ZnodeModel;
import org.fusesource.ide.zk.zookeeper.viewers.ZnodeModelElementType;
import org.fusesource.ide.zk.core.EclipseCoreActivator;
import org.fusesource.ide.zk.core.editors.DataModelTableEditor;
import org.fusesource.ide.zk.core.widgets.TableEdit;
import org.fusesource.ide.zk.core.widgets.TableEdit.CommitEditRunnable;


/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public final class ZnodeModelTableEditor extends DataModelTableEditor<ZnodeModel> {

    public static final String ID = ZnodeModelTableEditor.class.getName() + ZooKeeperActivator.VERSION_SUFFIX;

    public ZnodeModelTableEditor() {
        super(ZnodeModel.class, new ZnodeModelElementType(true));
        setTitleImage(EclipseCoreActivator.getManagedImage(EclipseCoreActivator.IMAGE_KEY_ACTION_TABLE_EDIT));
    }

    @Override
    protected void configureTable(Table table) {
        table.setLinesVisible(true);

        ZnodeModelElementType znodeModelElementType = (ZnodeModelElementType) getModelElementType();
        int dataColumnIndex = znodeModelElementType.getColumnIndex(ZnodeModelElementType.COLUMN_TITLE_DATA);

        CommitEditRunnable commitEditRunnable = new CommitEditRunnable() {

            @Override
            public void run() {

                TableViewer tableViewer = getTableViewer();
                TableItem row = getRow();
                String newValue = getNewValue();
                Table table = tableViewer.getTable();
                int rowIndex = table.indexOf(row);
                final ZnodeModel znodeModel = (ZnodeModel) tableViewer.getElementAt(rowIndex);
                Znode znode = znodeModel.getData();

                byte[] data = null;
                if (newValue != null && !newValue.isEmpty()) {
                    // TODO: Need to support other Charsets?
                    data = newValue.getBytes();
                }

                znode.setData(data);
                znodeModel.setDirtyData(true);

                try {
                    znodeModel.updateData();
                }
                catch (Exception e) {
                    setError(e);
                }
            }
        };

        new TableEdit(table, commitEditRunnable, dataColumnIndex);
    }

}
