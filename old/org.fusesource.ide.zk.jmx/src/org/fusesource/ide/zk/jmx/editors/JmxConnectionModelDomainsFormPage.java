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

import org.fusesource.ide.zk.jmx.model.DomainModel;
import org.fusesource.ide.zk.jmx.model.JmxConnectionModel;
import org.fusesource.ide.zk.jmx.viewers.DomainModelElementType;
import org.fusesource.ide.zk.core.editors.DataModelTableFormPage;
import org.fusesource.ide.zk.core.viewers.CollectionElementType;


public class JmxConnectionModelDomainsFormPage extends DataModelTableFormPage<JmxConnectionModel> {

    public static final String ID = JmxConnectionModelDomainsFormPage.class.getName();

    public static final String TITLE = "MBean Domains";

    public JmxConnectionModelDomainsFormPage(JmxConnectionModelFormEditor editor) {
        super(editor, ID, TITLE, DomainModel.class, new DomainModelElementType(), editor.getModel().getDomainModels(),
                new CollectionElementType());

        setImage(getTableModelElementType().getImage(null));
    }

}
