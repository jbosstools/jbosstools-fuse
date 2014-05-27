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

package org.fusesource.ide.zk.core.model;

/**
 * {@link DataModelSourceException} that signifies that the source does not support the requested operation.
 * 
 * @author Mark Masse
 */
public class OperationNotSupportedException extends DataModelSourceException {

    private static final long serialVersionUID = -671707065308474045L;

    public OperationNotSupportedException() {
        super();
    }

    public OperationNotSupportedException(String message) {
        super(message);
    }

    public OperationNotSupportedException(String message, Throwable cause) {
        super(message, cause);
    }

    public OperationNotSupportedException(Throwable cause) {
        super(cause);
    }

}
