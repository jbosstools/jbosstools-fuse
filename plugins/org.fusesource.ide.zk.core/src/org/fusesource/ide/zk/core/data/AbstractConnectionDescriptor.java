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

package org.fusesource.ide.zk.core.data;

/**
 * Base class for objects that describe a server connection.  Connection descriptors have unique {@link  #getName() names}.
 * 
 * @see org.fusesource.ide.zk.core.runtime.ConnectionDescriptorFiles
 *
 * @author Mark Masse
 */
public abstract class AbstractConnectionDescriptor<T extends AbstractConnectionDescriptor<T>> implements Comparable<T> {

    final String _Name;

    /**
     * Constructor. 
     *
     * @param name The unique name for this connection descriptor.
     */
    public AbstractConnectionDescriptor(String name) {
        _Name = name;
    }

    @Override
    public int compareTo(T o) {
        return _Name.compareTo(o._Name);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        T other = (T) obj;
        if (_Name == null) {
            if (other._Name != null)
                return false;
        }
        else if (!_Name.equals(other._Name))
            return false;
        return true;
    }

    /**
     * Returns the name.
     * 
     * @return The name
     */
    public final String getName() {
        return _Name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_Name == null) ? 0 : _Name.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return getClass().getName() + " [Name=" + _Name + "]";
    }

}
