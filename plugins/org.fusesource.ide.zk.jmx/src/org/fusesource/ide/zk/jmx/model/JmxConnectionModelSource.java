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

package org.fusesource.ide.zk.jmx.model;

import org.fusesource.ide.zk.jmx.data.JmxConnection;
import org.fusesource.ide.zk.jmx.data.JmxConnectionDescriptor;
import org.fusesource.ide.zk.core.model.AbstractConnectionModelSource;
import org.fusesource.ide.zk.core.model.DataModelSourceException;
import org.fusesource.ide.zk.core.runtime.ConnectionDescriptorFiles;


/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public class JmxConnectionModelSource extends
        AbstractConnectionModelSource<JmxConnectionModel, JmxConnectionDescriptor, JmxConnection> {

    private final Object _Owner;

    public JmxConnectionModelSource(ConnectionDescriptorFiles<JmxConnectionDescriptor> files) {
        this(files, null);
    }

    public JmxConnectionModelSource(ConnectionDescriptorFiles<JmxConnectionDescriptor> files, Object owner) {
        super(files);
        _Owner = owner;
    }

    @Override
    public JmxConnection getData(JmxConnectionDescriptor jmxConnectionDescriptor) throws DataModelSourceException {
        return new JmxConnection(jmxConnectionDescriptor);
    }

    /**
     * Returns the owner.
     * 
     * @return The owner
     */
    public Object getOwner() {
        return _Owner;
    }

    @Override
    protected JmxConnectionModel createModelInternal(JmxConnectionDescriptor jmxConnectionDescriptor) {
        return new JmxConnectionModel(jmxConnectionDescriptor, getOwner());
    }

}
