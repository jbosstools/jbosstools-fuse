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

package org.fusesource.ide.zk.core.widgets;

import org.eclipse.jface.action.ControlContribution;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * {@link ControlContribution} that provides access to the created {@link Control}.
 * 
 * @author Mark Masse
 */
public abstract class BaseControlContribution extends ControlContribution {

    private Control _Control;
    private Set<DisposeListener> _DisposeListeners;

    /**
     * Constructor.
     * 
     * @param id The contribution item id.
     */
    protected BaseControlContribution(String id) {
        super(id);
    }

    /**
     * Adds a {@link DisposeListener} to the created {@link Control}. Listeners can be added either before or after the
     * Control is created.
     * 
     * @param disposeListener The {@link DisposeListener} to add.
     * 
     * @see Control#addControlDisposeListener(DisposeListener)
     */
    public void addControlDisposeListener(DisposeListener disposeListener) {

        Control control = getControl();
        if (control != null) {
            if (control.isDisposed()) {
                return;
            }

            control.addDisposeListener(disposeListener);
        }
        else {
            if (_DisposeListeners == null) {
                _DisposeListeners = new LinkedHashSet<DisposeListener>();
            }

            _DisposeListeners.add(disposeListener);
        }
    }

    /**
     * Returns the {@link Control}.
     * 
     * @return The control
     */
    public Control getControl() {
        return _Control;
    }

    /**
     * Removes a {@link DisposeListener} from the created {@link Control}. Listeners can be removed either before or
     * after the Control is created.
     * 
     * @param disposeListener The {@link DisposeListener} to remove.
     * 
     * @see Control#removeDisposeListener(DisposeListener)
     */
    public void removeControlDisposeListener(DisposeListener disposeListener) {
        Control control = getControl();
        if (control != null) {
            control.removeDisposeListener(disposeListener);
        }
        else if (_DisposeListeners != null) {
            _DisposeListeners.remove(disposeListener);
        }
    }

    @Override
    protected final Control createControl(Composite parent) {

        _Control = createControlInternal(parent);

        if (_Control != null && !_Control.isDisposed() && _DisposeListeners != null) {
            for (DisposeListener disposeListener : _DisposeListeners) {
                _Control.addDisposeListener(disposeListener);
            }
            _DisposeListeners.clear();
        }

        return _Control;
    }

    /**
     * Subclasses must override this method to create the {@link Control}.
     * 
     * @param parent The parent {@link Composite}.
     * @return The new {@link Control} to contribute.
     * 
     * @see #createControl(Composite)
     */
    protected abstract Control createControlInternal(Composite parent);

}
