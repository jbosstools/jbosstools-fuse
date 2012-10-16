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


import java.util.List;
import java.util.Set;

import javax.management.ObjectName;

import org.fusesource.ide.zk.jmx.data.ObjectNameKeyValue;
import org.fusesource.ide.zk.jmx.model.AbstractObjectNameKeyValueModel;
import org.fusesource.ide.zk.jmx.model.MBeanModel;
import org.fusesource.ide.zk.jmx.model.ObjectNameKeyValueModel;

/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public abstract class AbstractObjectNameKeyValueModelElementType extends AbstractJmxDataModelElementType {

    @Override
    public int getChildCount(Object parent) {

        AbstractObjectNameKeyValueModel<?, ?, ?> model = (AbstractObjectNameKeyValueModel<?, ?, ?>) parent;
        int childCount = 0;
        Set<String> childObjectNameKeyValuePairStrings = model.getChildObjectNameKeyValuePairStrings();
        if (childObjectNameKeyValuePairStrings != null) {
            childCount += childObjectNameKeyValuePairStrings.size();
        }
        Set<ObjectName> childMBeanObjectNames = model.getChildMBeanObjectNames();
        if (childMBeanObjectNames != null) {
            childCount += childMBeanObjectNames.size();
        }
        return childCount;
    }

    @Override
    public Object getChildElement(Object parent, int index) {

        AbstractObjectNameKeyValueModel<?, ?, ?> model = (AbstractObjectNameKeyValueModel<?, ?, ?>) parent;

        int childObjectNameKeyValueModelCount = 0;
        List<ObjectNameKeyValueModel> childObjectNameKeyValueModels = model.getChildObjectNameKeyValueModels();
        if (childObjectNameKeyValueModels != null) {
            childObjectNameKeyValueModelCount = childObjectNameKeyValueModels.size();
        }

        if (index < childObjectNameKeyValueModelCount) {
            return childObjectNameKeyValueModels.get(index);
        }

        int childMBeanModelCount = 0;
        List<MBeanModel> childMBeanModels = model.getChildMBeanModels();
        if (childMBeanModels != null) {
            childMBeanModelCount = childMBeanModels.size();
        }

        if (index < childObjectNameKeyValueModelCount + childMBeanModelCount) {
            return childMBeanModels.get(index - childObjectNameKeyValueModelCount);
        }

        return null;
    }

    @Override
    public final String getText(Object element) {
        ObjectNameKeyValue objectNameKeyValue = getObjectNameKeyValue(element);
        String text = objectNameKeyValue.getObjectNameValue();
        return text;
    }
    
    @Override
    public final String getToolTipText(Object element) {
        ObjectNameKeyValue objectNameKeyValue = getObjectNameKeyValue(element);
        return objectNameKeyValue.getObjectName().toString();
    }

    protected abstract ObjectNameKeyValue getObjectNameKeyValue(Object element);
}
