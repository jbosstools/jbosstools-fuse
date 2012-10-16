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
import org.eclipse.jface.viewers.TreeViewer;

/**
 * {@link IViewerType} for {@link TreeViewer}.
 * 
 * @author Mark Masse
 */
public class TreeViewerType implements IViewerType {

    @Override
    public void addElement(StructuredViewer viewer, Object parent, Object element) {
        TreeViewer treeViewer = (TreeViewer) viewer;
        treeViewer.add(parent, element);
    }

    @Override
    public void refreshElement(StructuredViewer viewer, Object element) {
        TreeViewer treeViewer = (TreeViewer) viewer;
        treeViewer.refresh(element);
    }

    @Override
    public void removeElement(StructuredViewer viewer, Object element) {
        TreeViewer treeViewer = (TreeViewer) viewer;
        treeViewer.remove(element);
    }

    @Override
    public void setChildCount(StructuredViewer viewer, Object element, int childCount) {
        TreeViewer treeViewer = (TreeViewer) viewer;
        treeViewer.setChildCount(element, childCount);
    }

    @Override
    public void updateElement(StructuredViewer viewer, Object parent, int index, Object element) {
        TreeViewer treeViewer = (TreeViewer) viewer;
        treeViewer.replace(parent, index, element);
    }

}
