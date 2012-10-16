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

import java.util.HashMap;
import java.util.Map;

/**
 * Simple class that maps {@link Class classes} to {@link IElementType element types}.
 * 
 * @see IElementType
 * @see PluggableContentProvider
 * @see ViewerFactory
 * 
 * @author Mark Masse
 */
public final class ElementTypes {

    private final Map<Class<?>, IElementType> _ElementTypes;

    /**
     * Constructor.
     */
    public ElementTypes() {
        _ElementTypes = new HashMap<Class<?>, IElementType>();
    }

    /**
     * Adds a {@link IElementType}.
     * 
     * @param elementClass The element {@link Class}.
     * @param elementType The {@link IElementType} to add.
     */
    public void add(Class<?> elementClass, IElementType elementType) {
        _ElementTypes.put(elementClass, elementType);
    }

    /**
     * Removes a {@link IElementType}.
     * 
     * @param elementClass The element {@link Class} map key.
     */
    public void remove(Class<?> elementClass) {
        _ElementTypes.remove(elementClass);
    }

    /**
     * Returns the {@link IElementType} associated with the specified element. The element's {@link Class} is used to
     * find an associated {@link IElementType}.
     * 
     * @param element The element {@link Object}.
     * @return The {@link IElementType} associated with the specified element.
     * @throws IllegalArgumentException if the element is <code>null</code> or hasn't had its {@link Class} mapped to an
     *             {@link IElementType}.
     * 
     * @see #add(Class, IElementType)
     */
    public IElementType get(Object element) {
        if (element == null) {
            throw new IllegalArgumentException("null element");
        }

        Class<?> type = element.getClass();
        if (!_ElementTypes.containsKey(type)) {
            throw new IllegalArgumentException("No IElementType found for element: " + element);
        }
        return _ElementTypes.get(type);
    }

}
