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

package org.fusesource.ide.zk.core.editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

/**
 * Base implementation of the {@link IEditorInput} interface that uses an editor id to find the matching
 * {@link IEditorDescriptor} in order to implement several of the interface methods.
 * 
 * @author Mark Masse
 */
public abstract class BaseEditorInput implements IEditorInput {

    private final String _EditorId;

    /**
     * Constructor.
     * 
     * @param editorId The unique editor id that will be used to look up the {@link IEditorDescriptor}.
     */
    public BaseEditorInput(String editorId) {
        _EditorId = editorId;
    }

    @Override
    public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
        return null;
    }

    @Override
    public ImageDescriptor getImageDescriptor() {
        return getEditorDescriptor().getImageDescriptor();
    }

    @Override
    public String getName() {
        return getEditorDescriptor().getLabel();
    }

    @Override
    public IPersistableElement getPersistable() {
        return null;
    }

    @Override
    public String getToolTipText() {
        return getName();
    }

    /**
     * Returns the {@link IEditorDescriptor} associated with this input's editor id.
     * 
     * @return The {@link IEditorDescriptor} for the editor id.
     */
    protected IEditorDescriptor getEditorDescriptor() {
        String editorId = getEditorId();
        if (editorId == null) {
            return null;
        }

        IWorkbench workBench = PlatformUI.getWorkbench();
        IEditorRegistry editorRegistry = workBench.getEditorRegistry();
        IEditorDescriptor editorDescriptor = editorRegistry.findEditor(editorId);
        return editorDescriptor;
    }

    /**
     * Returns the editor id.
     * 
     * @return The editor id.
     */
    public String getEditorId() {
        return _EditorId;
    }

}
