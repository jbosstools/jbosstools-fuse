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
 * Interface for binding a disposable resource to a {@link StructuredViewer}.
 * 
 * @author Mark Masse
 */
public interface IBinding {

    /**
     * Sets the {@link StructuredViewer}.
     * 
     * @param viewer The viewer.
     */
    void setViewer(StructuredViewer viewer);

    /**
     * Sets the {@link IViewerType}.
     * 
     * @param viewerType The viewer type.
     */
    void setViewerType(IViewerType viewerType);

    /**
     * Disposes of this binding.
     */
    void dispose();
}
