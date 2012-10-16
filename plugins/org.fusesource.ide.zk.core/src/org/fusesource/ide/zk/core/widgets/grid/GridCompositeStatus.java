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

import org.eclipse.swt.widgets.Control;


/**
 * The status of a {@link GridComposite}.
 * 
 * @author Mark Masse
 */
public class GridCompositeStatus {

    /**
     * The "OK" status indicating that the entirety of the {@link GridComposite} is in a valid state.
     */
    public static final GridCompositeStatus OK_STATUS = new GridCompositeStatus(null, null, GridCompositeStatus.Type.OK);

    private final String _ControlName;
    private final String _Message;
    private final Type _Type;

    /**
     * Constructor.
     * 
     * @param controlName The name of the Control that is responsible for the status.
     * @param message The status message.
     * @param type The status {@link Type}.
     */
    public GridCompositeStatus(String controlName, String message, Type type) {
        _ControlName = controlName;
        _Message = message;
        _Type = type;
    }

    /**
     * Returns the {@link Control} name.
     * 
     * @return The Control name.
     */
    public String getControlName() {
        return _ControlName;
    }

    /**
     * Returns the message.
     * 
     * @return The message.
     */
    public String getMessage() {
        return _Message;
    }

    /**
     * Returns the {@link Type type}.
     * 
     * @return The type.
     */
    public Type getType() {
        return _Type;
    }

    /**
     * The {@link GridCompositeStatus} type.
     * 
     * @author Mark Masse
     */
    public static enum Type {
        
        /**
         * Indicates that one or more of the {@link GridComposite} child Controls is in an invalid state. 
         */
        ERROR_INVALID(0, true),
        
        /**
         * Indicates that one or more of the {@link GridComposite} child Controls requires a value. 
         */
        ERROR_REQUIRED(1, true),
        
        /**
         * Indicates that the entirety of the {@link GridComposite} is in a valid state.
         * 
         * @see GridCompositeStatus#OK_STATUS
         */
        OK(2, false);

        private final boolean _Error;
        private final int _Flag;

        Type(int flag, boolean error) {
            _Flag = flag;
            _Error = error;
        }

        /**
         * Returns the error.
         * 
         * @return The error
         */
        public boolean isError() {
            return _Error;
        }

        /**
         * Returns the flag.
         * 
         * @return The flag
         */
        public int getFlag() {
            return _Flag;
        }
    }

}
