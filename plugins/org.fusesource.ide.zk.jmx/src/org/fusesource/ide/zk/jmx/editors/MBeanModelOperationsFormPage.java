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

import org.fusesource.ide.zk.jmx.model.MBeanModel;
import org.fusesource.ide.zk.jmx.model.MBeanOperationModel;
import org.fusesource.ide.zk.jmx.viewers.MBeanOperationModelElementType;
import org.fusesource.ide.zk.jmx.viewers.MBeanOperationsModelCategoryElementType;
import org.fusesource.ide.zk.core.editors.DataModelTableFormPage;


public class MBeanModelOperationsFormPage extends DataModelTableFormPage<MBeanModel> {

    public static final String ID = MBeanModelOperationsFormPage.class.getName();

    public static final String TITLE = "Operations";

    public MBeanModelOperationsFormPage(MBeanModelFormEditor editor) {
        super(editor, ID, TITLE, MBeanOperationModel.class, new MBeanOperationModelElementType(), editor.getModel()
                .getOperationsModelCategory(), new MBeanOperationsModelCategoryElementType());

        setImage(getTableViewerInputElementType().getImage(null));
    }

}
