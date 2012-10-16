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

import java.util.Set;

import org.fusesource.ide.zk.jmx.data.Domain;
import org.fusesource.ide.zk.jmx.data.JmxConnection;
import org.fusesource.ide.zk.core.model.DataModelSourceException;


/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public class DomainModelSource extends AbstractJmxModelSource<DomainModel, String, Domain> {

    /**
     * TODO: Comment.
     * 
     * @param jmxConnectionModel
     */
    public DomainModelSource(JmxConnectionModel jmxConnectionModel) {
        super(jmxConnectionModel);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.fusesource.ide.zk.zookeeper.model.DataModelSource#getData(java.lang.Object)
     */
    @Override
    public Domain getData(String domainName) {
        return getJmxConnection().getDomain(domainName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.fusesource.ide.zk.zookeeper.model.DataModelSource#getKeys()
     */
    @Override
    public Set<String> getKeys() throws DataModelSourceException {
        JmxConnection jmxConnection = getJmxConnection();
        return jmxConnection.getDomainNames();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.fusesource.ide.zk.zookeeper.model.AbstractDataModelSource#isGetKeysSupported()
     */
    @Override
    public boolean isGetKeysSupported() {
        return true;
    }

    @Override
    protected DomainModel createModelInternal(String domainName) {
        return new DomainModel(domainName, getJmxConnectionModel());
    }

}
