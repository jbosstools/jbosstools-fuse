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

package org.fusesource.ide.zk.jmx.viewers;

import org.fusesource.ide.zk.jmx.model.MBeanFeatureModel;

/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public abstract class AbstractMBeanFeatureModelElementType extends AbstractJmxDataModelElementType {

    public static final String PROPERTY_NAME_DESCRIPTION = "Description";

    public static final String PROPERTY_NAME_NAME = "Name";
    @Override
    public int getChildCount(Object parent) {
        return 0;
    }
    
    
    @Override
    public Object getChildElement(Object parent, int index) {
        return null;
    }

    @Override
    public String getText(Object element) {
        MBeanFeatureModel<?, ?> model = (MBeanFeatureModel<?, ?>) element;
        String text = model.getData().getName();
        return text;
    }

    @Override
    public String getToolTipText(Object element) {
        MBeanFeatureModel<?, ?> model = (MBeanFeatureModel<?, ?>) element;
        return model.getParentModel().getObjectNameKeyValue().getObjectName().toString() + "." + getText(element);
    }

}
