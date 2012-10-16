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

import org.fusesource.ide.zk.jmx.data.Domain;
import org.fusesource.ide.zk.jmx.data.ObjectNameKeyValue;
import org.fusesource.ide.zk.core.model.DataModelSourceException;


/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public class ObjectNameKeyValueModelSource extends
        AbstractObjectNameKeyValueModelSource<ObjectNameKeyValueModel, String, ObjectNameKeyValue> {

    /**
     * TODO: Comment.
     * 
     * @param domainModel
     * @param jmxConnectionModel
     */
    public ObjectNameKeyValueModelSource(DomainModel domainModel, JmxConnectionModel jmxConnectionModel) {
        super(domainModel, jmxConnectionModel);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.fusesource.ide.zk.zookeeper.model.DataModelSource#getData(java.lang.Object)
     */
    @Override
    public ObjectNameKeyValue getData(String keyValuePairString) throws DataModelSourceException {
        Domain domain = getDomain();
        ObjectNameKeyValue objectNameKeyValue = domain.getObjectNameKeyValue(keyValuePairString);
        if (objectNameKeyValue == null || objectNameKeyValue.isMBean()) {
            return null;
        }
        return objectNameKeyValue;
    }

    @Override
    protected ObjectNameKeyValueModel createModelInternal(String keyValuePairString) {
        return new ObjectNameKeyValueModel(keyValuePairString, getDomainModel(), getJmxConnectionModel());
    }

    /*
     * (non-Javadoc)
     * 
     * 
     * 
     * @seeorg.fusesource.ide.zk.zookeeper.model.jmx.AbstractObjectNameKeyValueModelSource#getKey(org.massedynamic.
     * eclipse.zookeeper .data.jmx. ObjectNameKeyValue )
     */
    @Override
    protected String getKey(ObjectNameKeyValue objectNameKeyValue) {
        if (objectNameKeyValue.isMBean()) {
            return null;
        }
        return objectNameKeyValue.getKeyValuePairString();
    }

}
