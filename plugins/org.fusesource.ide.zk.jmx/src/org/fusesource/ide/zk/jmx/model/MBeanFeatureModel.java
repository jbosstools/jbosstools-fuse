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

import javax.management.MBeanFeatureInfo;

import org.fusesource.ide.zk.jmx.data.MBeanFeature;
import org.fusesource.ide.zk.jmx.jmxdoc.MBeanFeatureDoc;


/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public abstract class MBeanFeatureModel<M extends MBeanFeatureModel<M, D>, D extends MBeanFeature<?>> extends
        AbstractJmxModel<M, String, D> {

    private final MBeanModel _MBeanModel;

    /**
     * TODO: Comment.
     * 
     * @param featureName
     * @param mbeanModel
     * @param jmxConnectionModel
     */
    MBeanFeatureModel(String featureName, MBeanModel mbeanModel, JmxConnectionModel jmxConnectionModel) {
        super(featureName, jmxConnectionModel);
        _MBeanModel = mbeanModel;
    }

    @Override
    public MBeanModel getParentModel() {
        return _MBeanModel;
    }
    
    public abstract MBeanFeatureDoc<? extends MBeanFeatureInfo> getDoc();

}
