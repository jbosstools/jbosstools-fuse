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

package org.fusesource.ide.zk.core.widgets.grid;

import java.util.EventObject;

/**
 * A {@link GridComposite} event.
 * 
 * @author Mark Masse
 */
@SuppressWarnings("serial")
public final class GridCompositeEvent extends EventObject {

    private final GridCompositeStatus _Status;

    /**
     * Constructor.
     * 
     * @param source The {@link GridComposite source}.
     * @param status The {@link GridCompositeStatus status}.
     */
    public GridCompositeEvent(GridComposite source, GridCompositeStatus status) {
        super(source);
        _Status = status;
    }

    /**
     * Returns the {@link GridComposite} source.
     * 
     * @return The {@link GridComposite} source.
     */
    public GridComposite getGridComposite() {
        return (GridComposite) getSource();
    }

    /**
     * Returns the {@link GridCompositeStatus status}.
     * 
     * @return The status.
     */
    public GridCompositeStatus getStatus() {
        return _Status;
    }

}
