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

import org.fusesource.ide.zk.jmx.data.MBeanOperation;
import org.fusesource.ide.zk.jmx.jmxdoc.MBeanOperationDoc;

/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public final class MBeanOperationModel extends MBeanFeatureModel<MBeanOperationModel, MBeanOperation> {

    /**
     * TODO: Comment.
     * 
     * @param operationName
     * @param mbeanModel
     * @param jmxConnectionModel
     */
    MBeanOperationModel(String operationName, MBeanModel mbeanModel, JmxConnectionModel jmxConnectionModel) {
        super(operationName, mbeanModel, jmxConnectionModel);
    }

    @Override
    protected MBeanOperationModel getThis() {
        return this;
    }

    @Override
    public MBeanOperationDoc getDoc() {
        return new MBeanOperationDoc(getData().getInfo());
    }

}
