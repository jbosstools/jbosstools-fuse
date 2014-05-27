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

import java.util.HashSet;
import java.util.Set;

/**
 * {@link ElementBinding} that caches all bound elements.
 * 
 * @author Mark Masse
 */
public abstract class AbstractCachingElementBinding extends ElementBinding {

    private Set<Object> _Elements;
    private boolean _UnbindingAll;

    /**
     * Constructor.
     */
    public AbstractCachingElementBinding() {
        _Elements = new HashSet<Object>(100);
    }

    @Override
    public final void bind(Object element) {
        if (isDisposed()) {
            return;
        }

        if (_Elements.contains(element)) {
            return;
        }

        if (bindHook(element)) {
            _Elements.add(element);
        }
    }

    @Override
    public final void unbind(Object element) {
        if (!_Elements.contains(element)) {
            return;
        }

        if (unbindHook(element) && !_UnbindingAll) {
            _Elements.remove(element);
        }
    }

    @Override
    public final void unbindAll() {

        _UnbindingAll = true;
        for (Object element : _Elements) {
            unbindHook(element);
        }
        _Elements.clear();
        _UnbindingAll = false;
    }

    @Override
    public void dispose() {
        if (isDisposed()) {
            return;
        }

        super.dispose();
        unbindAll();
    }

    /**
     * Hook method for subclasses to unbind.
     * 
     * @param element The element to unbind.
     * 
     * @see #unbind(Object)
     */
    protected abstract boolean unbindHook(Object element);

    /**
     * Hook method for subclasses to bind.
     * 
     * @param element The element to bind.
     * 
     * @see #bind(Object)
     */
    protected abstract boolean bindHook(Object element);
}
