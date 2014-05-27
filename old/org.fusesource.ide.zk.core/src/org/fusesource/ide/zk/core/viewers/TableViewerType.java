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

/**
 * {@link IViewerType} for {@link TableViewer}.
 * 
 * @author Mark Masse
 */
public class TableViewerType implements IViewerType {

    @Override
    public void addElement(StructuredViewer viewer, Object parent, Object element) {
        TableViewer tableViewer = (TableViewer) viewer;
        tableViewer.add(element);
    }

    @Override
    public void refreshElement(StructuredViewer viewer, Object element) {
        TableViewer tableViewer = (TableViewer) viewer;
        tableViewer.refresh(element);
    }

    @Override
    public void removeElement(StructuredViewer viewer, Object element) {
        TableViewer tableViewer = (TableViewer) viewer;
        tableViewer.remove(element);
    }

    @Override
    public void setChildCount(StructuredViewer viewer, Object element, int childCount) {
        // Not possible
    }

    @Override
    public void updateElement(StructuredViewer viewer, Object parent, int index, Object element) {
        TableViewer tableViewer = (TableViewer) viewer;
        tableViewer.replace(element, index);
    }

}
