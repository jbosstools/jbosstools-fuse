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

package org.fusesource.ide.zk.jmx.jmxdoc;

import javax.management.Descriptor;

import org.fusesource.ide.zk.jmx.data.JmxUtils;


/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public final class JmxType {

    private final Descriptor _Descriptor;
    private String _Name;
    private final String _RawName;

    public JmxType(String rawTypeName, Descriptor typeDescriptor) {
        _RawName = rawTypeName;
        _Descriptor = typeDescriptor;
    }

    /**
     * Returns the descriptor.
     * 
     * @return The descriptor
     */
    public Descriptor getDescriptor() {
        return _Descriptor;
    }

    /**
     * Returns the name.
     * 
     * @return The name
     */
    public final String getName() {
        if (_Name == null) {
            _Name = JmxUtils.getTypeName(getRawName(), getDescriptor());
        }

        return _Name;
    }

    /**
     * Returns the raw type name.
     * 
     * @return The raw type name
     */
    public String getRawName() {
        return _RawName;
    }

}
