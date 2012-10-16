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

/**
 * Concrete implementation of the {@link IBinding} interface.
 * 
 * @author Mark Masse
 */
public class Binding implements IBinding {

    private boolean _Disposed;
    private StructuredViewer _Viewer;
    private IViewerType _ViewerType;

    @Override
    public void dispose() {
        _Disposed = true;
    }

    /**
     * Returns the viewer.
     * 
     * @return The viewer.
     */
    public StructuredViewer getViewer() {
        return _Viewer;
    }

    /**
     * Returns the viewerType.
     * 
     * @return The viewerType.
     */
    public IViewerType getViewerType() {
        return _ViewerType;
    }

    /**
     * Returns the disposed flag.
     * 
     * @return The disposed flag.
     */
    public boolean isDisposed() {
        return _Disposed;
    }

    @Override
    public void setViewer(StructuredViewer viewer) {
        _Viewer = viewer;
    }

    @Override
    public void setViewerType(IViewerType viewerType) {
        _ViewerType = viewerType;
    }

}
