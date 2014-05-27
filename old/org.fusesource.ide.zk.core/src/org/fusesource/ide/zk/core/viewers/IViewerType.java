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

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;

/**
 * Interface that wraps the methods of a {@link StructuredViewer} subclass.
 * 
 * @see PluggableContentProvider
 * @see ViewerFactory
 * @see TableViewerType
 * @see TreeViewerType
 * 
 * @author Mark Masse
 */
public interface IViewerType {

    /**
     * Adds an element to the {@link StructuredViewer viewer}.
     * 
     * @param viewer The {@link StructuredViewer}.
     * @param parent The parent element.
     * @param element The element to add.
     * 
     * @see TableViewer#add(Object)
     * @see TreeViewer#add(Object, Object)
     */
    void addElement(StructuredViewer viewer, Object parent, Object element);

    /**
     * Refreshes the specified {@link StructuredViewer viewer} element.
     * 
     * @param viewer The {@link StructuredViewer}.
     * @param element The element to refresh.
     * 
     * @see StructuredViewer#refresh(Object)
     */
    void refreshElement(StructuredViewer viewer, Object element);

    /**
     * Removes the specified element.
     * 
     * @param viewer The {@link StructuredViewer}.
     * @param element The element to remove.
     * 
     * @see TableViewer#remove(Object)
     * @see TreeViewer#remove(Object)
     */
    void removeElement(StructuredViewer viewer, Object element);

    /**
     * Sets the child count for the specified element.
     * 
     * @param viewer The {@link StructuredViewer}.
     * @param element The parent element.
     * @param childCount The number of children.
     * 
     * @see TreeViewer#setChildCount(Object, int)
     */
    void setChildCount(StructuredViewer viewer, Object element, int childCount);

    /**
     * Updates the specified child element
     * 
     * @param viewer The {@link StructuredViewer}.
     * @param parent The parent element (may be <code>null</code>).
     * @param index The index of the element.
     * @param element The new element value.
     * 
     * @see TableViewer#replace(Object, int)
     * @see TreeViewer#replace(Object, int, Object)
     */
    void updateElement(StructuredViewer viewer, Object parent, int index, Object element);

}
