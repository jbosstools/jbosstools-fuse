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
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Control;

/**
 * Concrete implementation of {@link IElementBinding}.
 * 
 * @see PluggableContentProvider
 * @see ViewerFactory
 * 
 * @author Mark Masse
 */
public abstract class ElementBinding extends Binding implements IElementBinding {

    private final ControlDisposeListener _ControlDisposeListener;

    public ElementBinding() {
        _ControlDisposeListener = new ControlDisposeListener();
    }

    @Override
    public void setViewer(StructuredViewer viewer) {
        removeViewerControlDisposeListener();
        super.setViewer(viewer);
        addViewerControlDisposeListener();
    }

    private void addViewerControlDisposeListener() {
        StructuredViewer viewer = getViewer();
        if (viewer == null) {
            return;
        }
        Control control = viewer.getControl();
        if (control.isDisposed()) {
            return;
        }

        control.addDisposeListener(_ControlDisposeListener);
    }

    private void removeViewerControlDisposeListener() {
        StructuredViewer viewer = getViewer();
        if (viewer == null) {
            return;
        }
        Control control = viewer.getControl();
        control.removeDisposeListener(_ControlDisposeListener);
    }

    private class ControlDisposeListener implements DisposeListener {

        @Override
        public void widgetDisposed(DisposeEvent e) {
            unbindAll();
        }

    }

}
