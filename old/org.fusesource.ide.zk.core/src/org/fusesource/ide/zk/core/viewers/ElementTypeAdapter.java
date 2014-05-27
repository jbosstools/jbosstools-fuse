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


import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Table;
import org.fusesource.ide.zk.core.actions.BaseAction;

import java.util.List;

/**
 * Totally stubbed base implementation of the {@link IElementType} interface.  All methods return <code>null</code> or <code>-1</code>. 
 * 
 * @author Mark Masse
 */
public class ElementTypeAdapter implements IElementType {

    @Override
    public void fillContextMenu(IMenuManager manager) {
    }

    @Override
    public List<BaseAction> getActions() {
        return null;
    }

    @Override
    public int getChildCount(Object element) {
        return -1;
    }

    @Override
    public Object getChildElement(Object element, int index) {
        return null;
    }

    @Override
    public Object[] getChildren(Object element) {
        return null;
    }

    @Override
    public int[] getColumnAlignments() {
        return null;
    }

    @Override
    public Image getColumnImage(Object element, int arg1) {
        return null;
    }

    @Override
    public int getColumnIndex(String columnTitle) {
        return -1;
    }

    @Override
    public String getColumnText(Object element, int arg1) {
        return null;
    }

    @Override
    public String getColumnText(Object element, String columnTitle) {
        return null;
    }

    @Override
    public String[] getColumnTitles() {
        return null;
    }

    @Override
    public int[] getColumnWidths() {
        return null;
    }

    @Override
    public Image getImage(Object element) {
        return null;
    }

    @Override
    public Object getParent(Object element) {
        return null;
    }

    @Override
    public String getText(Object element) {
        return null;
    }

    @Override
    public String getToolTipText(Object element) {
        return null;
    }

    @Override
    public void packTable(Table table) {
    }

}
