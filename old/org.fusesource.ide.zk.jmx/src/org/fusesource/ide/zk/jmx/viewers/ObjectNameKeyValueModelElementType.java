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

import org.eclipse.swt.graphics.Image;
import org.fusesource.ide.zk.jmx.JmxActivator;
import org.fusesource.ide.zk.jmx.data.ObjectNameKeyValue;
import org.fusesource.ide.zk.jmx.model.ObjectNameKeyValueModel;


/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public class ObjectNameKeyValueModelElementType extends AbstractObjectNameKeyValueModelElementType {

    @Override
    public Image getImage(Object element) {
        return JmxActivator.getManagedImage(JmxActivator.IMAGE_KEY_OBJECT_OBJECT_NAME_KEY_VALUE);
    }

    @Override
    public Image getLargeImage(Object element) {
        return JmxActivator.getManagedImage(JmxActivator.IMAGE_KEY_OBJECT_OBJECT_NAME_KEY_VALUE_LARGE);
    }

    @Override
    protected ObjectNameKeyValue getObjectNameKeyValue(Object element) {
        ObjectNameKeyValueModel model = (ObjectNameKeyValueModel) element;
        return model.getObjectNameKeyValue();
    }
}
