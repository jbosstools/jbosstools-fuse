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
 * Interface for binding a disposable resource to a {@link StructuredViewer} and its elements.
 * 
 * @see PluggableContentProvider
 * @see ViewerFactory
 * 
 * @author Mark Masse
 */
public interface IElementBinding extends IBinding {

    /**
     * Binds the specified element.
     * 
     * @param element The element to bind.
     */
    void bind(Object element);

    /**
     * Unbinds the specified element.
     * 
     * @param element The element to unbind.
     */
    void unbind(Object element);

    /**
     * Unbinds all bound elements.
     */
    void unbindAll();
}
