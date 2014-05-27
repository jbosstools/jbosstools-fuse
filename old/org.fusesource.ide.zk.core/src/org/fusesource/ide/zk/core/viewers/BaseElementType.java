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

package org.fusesource.ide.zk.core.viewers;


import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.fusesource.ide.zk.core.actions.BaseAction;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class that provides a concrete implementation for many of the {@link IElementType} interface methods.
 * 
 * @author Mark Masse
 */
public abstract class BaseElementType extends ElementTypeAdapter {

    public static DateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss z");

    private final List<BaseAction> _Actions;

    public BaseElementType() {
        _Actions = createActions();
    }

    @Override
    public final List<BaseAction> getActions() {
        return _Actions;
    }

    @Override
    public Object[] getChildren(Object parent) {
        int childCount = getChildCount(parent);
        Object[] children = new Object[childCount];
        for (int i = 0; i < childCount; i++) {
            children[i] = getChildElement(parent, i);
        }

        return children;
    }

    @Override
    public Image getColumnImage(Object element, int columnIndex) {
        if (columnIndex == 0) {
            return getImage(element);
        }
        return null;
    }

    @Override
    public int getColumnIndex(String columnTitle) {
        String[] columnTitles = getColumnTitles();
        for (int i = 0; i < columnTitles.length; i++) {
            if (columnTitles[i].equals(columnTitle)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public String getColumnText(Object element, int columnIndex) {
        if (columnIndex == 0) {
            return getText(element);
        }
        return null;
    }

    @Override
    public String getToolTipText(Object element) {
        return getText(element);
    }

    @Override
    public final String getColumnText(Object element, String columnTitle) {
        return getColumnText(element, getColumnIndex(columnTitle));
    }

    @Override
    public void packTable(Table table) {
        table.pack();

        TableColumn[] columns = table.getColumns();
        int[] columnWidths = getColumnWidths();
        for (int i = 0; i < columns.length; i++) {

            int columnWidth = columnWidths[i];
            if (columnWidth == SWT.DEFAULT) {
                columns[i].pack();
            }
            else {
                columns[i].setWidth(columnWidth);
            }
        }
    }

    protected List<BaseAction> createActions() {
        return new ArrayList<BaseAction>();
    }

}
