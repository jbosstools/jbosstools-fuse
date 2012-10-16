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

package org.fusesource.ide.zk.jmx.editors;

import org.eclipse.ui.PartInitException;
import org.fusesource.ide.zk.jmx.model.MBeanModel;
import org.fusesource.ide.zk.jmx.viewers.DomainModelElementType;
import org.fusesource.ide.zk.jmx.viewers.JmxConnectionModelElementType;
import org.fusesource.ide.zk.core.editors.DataModelFormEditor;
import org.fusesource.ide.zk.core.model.DataModel;
import org.fusesource.ide.zk.core.viewers.DataModelElementType;

import java.util.Set;


/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public class MBeanModelFormEditor extends DataModelFormEditor<MBeanModel> {

    public static final String ID = MBeanModelFormEditor.class.getName();

    @Override
    protected DataModelElementType getOwnerModelElementType() {
        return new JmxConnectionModelElementType();
    }

    @Override
    protected DataModelElementType getParentModelElementType() {
        return new DomainModelElementType();
    }

    @Override
    protected DataModel<?, ?, ?> getParentModel() {
        return getModel().getDomainModel();
    }

    @Override
    protected void addPages() {
        try {

            MBeanModel model = getModel();

            MBeanModelMainFormPage page1 = new MBeanModelMainFormPage(this);
            addPage(page1);

            Set<String> attributeNames = model.getAttributeNames();
            int attributeCount = (attributeNames != null) ? attributeNames.size() : 0;

            Set<String> operationNames = model.getOperationNames();
            int operationCount = (operationNames != null) ? operationNames.size() : 0;

//            if (attributeCount > 0 || operationCount > 0) {
//                MBeanModelJmxDocFormPage page2 = new MBeanModelJmxDocFormPage(this);
//                addPage(page2);
//            }

            if (attributeCount > 0) {
                MBeanModelAttributesFormPage page3 = new MBeanModelAttributesFormPage(this);
                addPage(page3);
            }

            if (operationCount > 0) {
                MBeanModelOperationsFormPage page4 = new MBeanModelOperationsFormPage(this);
                addPage(page4);
            }
        }
        catch (PartInitException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
