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
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILazyTreeContentProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Table;
import org.fusesource.ide.zk.core.actions.BaseAction;

import java.util.List;


/**
 * Interface capable of translating an object into various display-related attributes. Instances of this interface act
 * as a bridge between plain old java objects and the text, images, and other display-oriented facets needed throughout
 * the UI. This interface encompasses the various interfaces used by {@link Viewer viewers} to provide a type-centric
 * approach to bridging data into a UI. 
 * 
 * @see ILabelProvider
 * @see IContentProvider
 * @see IStructuredContentProvider
 * @see ILazyTreeContentProvider
 * @see ITreeContentProvider
 * @see PluggableContentProvider
 * @see ElementTypesLabelProvider
 * @see ViewerFactory
 * @see ElementTypes
 * 
 * @author Mark Masse
 */
public interface IElementType {

    /**
     * Adds the element type actions to the context menu.
     * 
     * @param manager The {@link IMenuManager}.
     */
    void fillContextMenu(IMenuManager manager);

    /**
     * Returns the list of actions associated with this element type.
     * 
     * @return The list of actions associated with this element type.
     */
    List<BaseAction> getActions();

    /**
     * Returns the number of children owned by the specified parent element.
     * 
     * @param parent The parent object.
     * @return The number of children owned by the specified parent element.
     * 
     * @see PluggableContentProvider#hasChildren(Object)
     */
    int getChildCount(Object parent);

    /**
     * Returns the <em>ith</em> child element of the specified parent element.
     * 
     * @param parent The parent element.
     * @param index The child index.
     * @return The <em>ith</em> child element of the specified parent element.
     */
    Object getChildElement(Object parent, int index);

    /**
     * Returns an array of child elements for the specifed parent element.
     * 
     * @param parent The parent element.
     * @return The array of child elements.
     * 
     * @see PluggableContentProvider#getChildren(Object)
     * @see ITreeContentProvider#getChildren(Object)
     */
    Object[] getChildren(Object parent);

    /**
     * Returns an array of column alignments to use when displaying this element type in a {@link Table}. Valid array
     * values are {@link SWT#LEFT} and {@link SWT#RIGHT}.
     * 
     * @return An array of column alignments to use when displaying this element type in a {@link Table}.
     */
    int[] getColumnAlignments();

    /**
     * Returns the image to display in the specified {@link Table} column index.
     * 
     * @param element The table row element.
     * @param columnIndex The table column index.
     * @return The image to display in the specified {@link Table} column index.
     * 
     * @see TableViewer
     * @see ITableLabelProvider#getColumnImage(Object, int)
     * @see ElementTypesLabelProvider#getColumnImage(Object, int)
     */
    Image getColumnImage(Object element, int columnIndex);

    /**
     * Returns the index of the {@link Table} column with the specified title.
     * 
     * @param columnTitle The title of the column.
     * @return The table column index associated with the title.
     */
    int getColumnIndex(String columnTitle);

    /**
     * Returns the label text for the specified table row element and column index.
     * 
     * @param element The table row element.
     * @param columnIndex The table column index.
     * @return The label text for the specified table row element and column index.
     * 
     * @see TableViewer
     * @see ITableLabelProvider#getColumnText(Object, int)
     * @see ElementTypesLabelProvider#getColumnText(Object, int)
     */
    String getColumnText(Object element, int columnIndex);

    /**
     * Returns the label text for the specified table row element and column title.
     * 
     * @param element The table row element.
     * @param columnTitle The table column title.
     * @return The label text for the specified table row element and column title.
     * 
     * @see TableViewer
     * @see #getColumnText(Object, int)
     */
    String getColumnText(Object element, String columnTitle);

    /**
     * Returns an array of column titles to use when displaying this element type in a {@link Table}.
     * 
     * @return An array of column titles to use when displaying this element type in a {@link Table}.
     * 
     * @see TableViewer
     */
    String[] getColumnTitles();

    /**
     * Returns an array of column widths to use when displaying this element type in a {@link Table}. The array can
     * contain specific desired width values or {@link SWT#DEFAULT} to size the column to fit its longest label.
     * 
     * @return An array of column widths to use when displaying this element type in a {@link Table}.
     * 
     * @see TableViewer
     */
    int[] getColumnWidths();

    /**
     * Returns the image for the specified element.
     * 
     * @param element The viewer element.
     * @return The image for the specified element.
     * 
     * @see ILabelProvider#getImage(Object)
     */
    Image getImage(Object element);

    /**
     * Returns the parent for the given element.
     * 
     * @param element The child tree element.
     * @return The parent tree element for the given element.
     * 
     * @see TreeViewer
     * @see ITreeContentProvider#getParent(Object)
     */
    Object getParent(Object element);

    /**
     * Returns the label text for the specified viewer element.
     * 
     * @param element The viewer element.
     * @return The label text for the specified viewer element.
     * 
     * @see TableViewer
     * @see #getColumnText(Object, int)
     * @see ILabelProvider#getText(Object)
     */
    String getText(Object element);

    /**
     * Returns the tool tip text for the specified element.
     * 
     * @param element The viewer element.
     * @return The tool tip text for the specified element.
     */
    String getToolTipText(Object element);

    /**
     * Utility method to packs the specified table using the element's column widths.
     * 
     * @param table The {@link Table} to pack.
     */
    void packTable(Table table);
}
