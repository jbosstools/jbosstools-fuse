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
import org.fusesource.ide.zk.core.model.DataModel;


/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public abstract class AbstractJmxModel<T extends AbstractJmxModel<T, K, D>, K extends Comparable<K>, D> extends DataModel<T, K, D> {

    private final JmxConnectionModel _JmxConnectionModel;

    /**
     * TODO: Comment.
     *
     * @param key
     * @param jmxConnectionModel
     */
    AbstractJmxModel(K key, JmxConnectionModel jmxConnectionModel) {
        super(key);
        _JmxConnectionModel = jmxConnectionModel;
    }

    @Override
    public JmxConnectionModel getOwnerModel() {        
        return _JmxConnectionModel;
    }

    /**
     * TODO: Comment.
     * 
     * @return
     */
    protected JmxConnection getJmxConnection() {
        return getOwnerModel().getData();
    }

}
