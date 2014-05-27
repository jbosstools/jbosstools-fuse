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

package org.fusesource.ide.zk.zookeeper.editors.znodeform;


import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.fusesource.ide.zk.zookeeper.ZooKeeperActivator;
import org.fusesource.ide.zk.zookeeper.data.Znode;
import org.fusesource.ide.zk.zookeeper.model.ZnodeModel;

import java.text.DateFormat;
import java.util.Date;

import org.apache.zookeeper.data.Stat;

/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public class ZnodeModelStatFormPage extends BaseZnodeModelTableFormPage {

    public static final String ID = ZnodeModelStatFormPage.class.getName();

    public static final Image IMAGE = ZooKeeperActivator
            .getManagedImage(ZooKeeperActivator.IMAGE_KEY_OBJECT_ZNODE_STAT);

    public static final String[] TABLE_COLUMN_TITLES = new String[] { "Name", "Value", "Description" };

    public static final String TITLE = "Stat";

    public ZnodeModelStatFormPage(ZnodeModelFormEditor editor) {
        super(editor, ID, TITLE, IMAGE);
    }

    @Override
    protected int[] getTableColumnAlignments() {
        return null;
    }

    @Override
    protected String[] getTableColumnTitles() {
        return TABLE_COLUMN_TITLES;
    }

    @Override
    protected int[] getTableColumnWidths() {
        return null;
    }

    @Override
    protected void initTableItemsFromZnode() {

        Table table = getTable();
        table.removeAll();

        ZnodeModel znodeModel = getModel();
        Znode znode = znodeModel.getData();
        Stat stat = znode.getStat();

        DateFormat format = DateFormat.getDateTimeInstance();
        String ctime = format.format(new Date(stat.getCtime()));
        String mtime = format.format(new Date(stat.getMtime()));

        addStatValueTableItem(table, Znode.STAT_NAME_CZXID, String.valueOf(stat.getCzxid()),
                Znode.STAT_DESCRIPTION_CZXID);
        addStatValueTableItem(table, Znode.STAT_NAME_MZXID, String.valueOf(stat.getMzxid()),
                Znode.STAT_DESCRIPTION_MZXID);
        addStatValueTableItem(table, Znode.STAT_NAME_CTIME, ctime, Znode.STAT_DESCRIPTION_CTIME);
        addStatValueTableItem(table, Znode.STAT_NAME_MTIME, mtime, Znode.STAT_DESCRIPTION_MTIME);
        addStatValueTableItem(table, Znode.STAT_NAME_VERSION, String.valueOf(stat.getVersion()),
                Znode.STAT_DESCRIPTION_VERSION);
        addStatValueTableItem(table, Znode.STAT_NAME_CVERSION, String.valueOf(stat.getCversion()),
                Znode.STAT_DESCRIPTION_CVERSION);
        addStatValueTableItem(table, Znode.STAT_NAME_AVERSION, String.valueOf(stat.getAversion()),
                Znode.STAT_DESCRIPTION_AVERSION);
        addStatValueTableItem(table, Znode.STAT_NAME_EPHEMERAL_OWNER, String.valueOf(stat.getEphemeralOwner()),
                Znode.STAT_DESCRIPTION_EPHEMERAL_OWNER);
        addStatValueTableItem(table, Znode.STAT_NAME_DATA_LENGTH, String.valueOf(stat.getDataLength()),
                Znode.STAT_DESCRIPTION_DATA_LENGTH);
        addStatValueTableItem(table, Znode.STAT_NAME_NUM_CHILDREN, String.valueOf(stat.getNumChildren()),
                Znode.STAT_DESCRIPTION_NUM_CHILDREN);

    }

    private void addStatValueTableItem(Table table, String name, String value, String description) {
        TableItem item = new TableItem(table, SWT.NONE);

        item.setText(0, name);
        item.setText(1, value);
        item.setText(2, description);
    }
}
