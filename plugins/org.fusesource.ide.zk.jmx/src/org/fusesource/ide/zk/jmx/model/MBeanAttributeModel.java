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

import org.fusesource.ide.zk.jmx.data.MBeanAttribute;
import org.fusesource.ide.zk.jmx.jmxdoc.MBeanAttributeDoc;

/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public final class MBeanAttributeModel extends MBeanFeatureModel<MBeanAttributeModel, MBeanAttribute> {

    /**
     * TODO: Comment.
     * 
     * @param attributeName
     * @param mbeanModel
     * @param jmxConnectionModel
     */
    MBeanAttributeModel(String attributeName, MBeanModel mbeanModel, JmxConnectionModel jmxConnectionModel) {
        super(attributeName, mbeanModel, jmxConnectionModel);
    }

    @Override
    protected MBeanAttributeModel getThis() {
        return this;
    }

    @Override
    public MBeanAttributeDoc getDoc() {
        return new MBeanAttributeDoc(getData().getInfo());
    }

}
