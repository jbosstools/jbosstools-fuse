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

package org.fusesource.ide.zk.jmx.actions;

import org.fusesource.ide.zk.jmx.JmxActivator;
import org.fusesource.ide.zk.jmx.model.JmxConnectionModel;
import org.fusesource.ide.zk.core.actions.BaseDeleteAction;


/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public class JmxDeleteAction extends BaseDeleteAction {

    public JmxDeleteAction() {
        addInputTypeClass(JmxConnectionModel.class);
    }

    @Override
    public void reportError(Exception e) {
        JmxActivator.reportError(e);
    }

    @Override
    protected String getObjectName(Object selectedObject) {
        if (selectedObject instanceof JmxConnectionModel) {
            return ((JmxConnectionModel) selectedObject).getData().getDescriptor().getName();
        }

        return null;
    }

    @Override
    protected String getObjectTypeName(Object selectedObject) {
        if (selectedObject instanceof JmxConnectionModel) {
            return "JMX connection";
        }

        return null;
    }

}
