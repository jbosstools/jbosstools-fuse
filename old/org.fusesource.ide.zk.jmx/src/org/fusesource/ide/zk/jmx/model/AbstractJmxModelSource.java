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
import org.fusesource.ide.zk.core.model.DataModelSource;


/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public abstract class AbstractJmxModelSource<M extends AbstractJmxModel<M, K, D>, K extends Comparable<K>, D> extends
        DataModelSource<M, K, D> {

    private final JmxConnectionModel _JmxConnectionModel;

    /**
     * TODO: Comment.
     * 
     * @param jmxConnectionModel
     */
    public AbstractJmxModelSource(JmxConnectionModel jmxConnectionModel) {
        _JmxConnectionModel = jmxConnectionModel;
    }

    /**
     * TODO: Comment.
     * 
     * @return
     */
    public JmxConnectionModel getJmxConnectionModel() {
        return _JmxConnectionModel;
    }

    /**
     * TODO: Comment.
     * 
     * @return
     */
    protected JmxConnection getJmxConnection() {
        return getJmxConnectionModel().getData();
    }

}
