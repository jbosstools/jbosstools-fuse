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
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public abstract class BaseZnodeModelTableFormPage extends BaseZnodeModelFormPage {

    public static final FormData TABLE_FORM_DATA = new FormData();

    static {
        TABLE_FORM_DATA.top = new FormAttachment(0, 0);
        TABLE_FORM_DATA.left = new FormAttachment(0, 0);
        TABLE_FORM_DATA.bottom = new FormAttachment(100, 0);
        TABLE_FORM_DATA.right = new FormAttachment(100, 0);
    }

    private Table _Table;

    public BaseZnodeModelTableFormPage(ZnodeModelFormEditor editor, String id, String title, Image image) {
        super(editor, id, title, image);
    }

    protected abstract void initTableItemsFromZnode();

    protected Table createTable(IManagedForm managedForm, Composite client) {
        FormToolkit toolkit = managedForm.getToolkit();
        Table table = toolkit.createTable(client, getTableStyle());
        table.setLinesVisible(true);
        return table;
    }

    protected void createTableColumns() {
        Table table = getTable();
        if (table == null) {
            return;
        }

        String[] titles = getTableColumnTitles();
        if (titles != null && titles.length > 0) {

            int[] columnAlignments = getTableColumnAlignments();

            for (int i = 0; i < titles.length; i++) {
                TableColumn column = new TableColumn(table, SWT.NONE);
                column.setText(titles[i]);

                if (columnAlignments != null) {
                    column.setAlignment(columnAlignments[i]);
                }
            }

            table.setHeaderVisible(true);
        }
    }

    protected abstract String[] getTableColumnTitles();

    protected abstract int[] getTableColumnAlignments();

    protected abstract int[] getTableColumnWidths();

    protected FormData getTableFormData() {
        return TABLE_FORM_DATA;
    }

    protected int getTableStyle() {
        return SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION;
    }

    @Override
    protected void createModelFormContent(IManagedForm managedForm, final Composite client) {

        _Table = createTable(managedForm, client);
        _Table.setLayoutData(getTableFormData());
        createTableColumns();
    }

    protected final Table getTable() {
        return _Table;
    }
    
    protected final void initFromModelInternal() {

        Table table = getTable();

        if (table == null || getModel().isDestroyed()) {
            return;
        }

        table.setRedraw(false);

        try {
            initTableItemsFromZnode();
            setDirtyInternal(false);
        }
        finally {
            table.setRedraw(true);
        }

        table.pack();
        forceLayout();

        TableColumn[] columns = getTable().getColumns();
        int[] columnWidths = getTableColumnWidths();
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

}
