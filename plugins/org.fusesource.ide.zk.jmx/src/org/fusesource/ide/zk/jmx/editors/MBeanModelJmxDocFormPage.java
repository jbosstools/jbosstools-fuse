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

import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledFormText;
import org.fusesource.ide.zk.jmx.JmxActivator;
import org.fusesource.ide.zk.jmx.jmxdoc.JmxDocFormText;
import org.fusesource.ide.zk.jmx.model.MBeanModel;
import org.fusesource.ide.zk.core.editors.DataModelFormPage;


/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public class MBeanModelJmxDocFormPage extends DataModelFormPage<MBeanModel> {

    public static final String ID = MBeanModelJmxDocFormPage.class.getName();
    protected static final String TITLE = "JMXdoc";

    private ScrolledFormText _ScrolledFormText;
    private FormText _JmxDocFormText;

    public MBeanModelJmxDocFormPage(MBeanModelFormEditor editor) {
        super(editor, ID, TITLE);
        setImage(JmxActivator.getManagedImage(JmxActivator.IMAGE_KEY_OBJECT_JMX_DOC));
    }

    @Override
    protected void createModelFormContent(IManagedForm managedForm, Composite client) {
        FormToolkit toolkit = managedForm.getToolkit();

        _ScrolledFormText = new ScrolledFormText(client, false);
        _ScrolledFormText.setExpandHorizontal(true);
        toolkit.adapt(_ScrolledFormText, false, false);
        _JmxDocFormText = toolkit.createFormText(_ScrolledFormText, true);
        _ScrolledFormText.setFormText(_JmxDocFormText);
        JmxDocFormText.initFormText(_JmxDocFormText);

        FormData scrolledFormTextFormData = new FormData();
        scrolledFormTextFormData.top = new FormAttachment(0, 0);
        scrolledFormTextFormData.left = new FormAttachment(0, 0);
        scrolledFormTextFormData.right = new FormAttachment(100, 0);
        scrolledFormTextFormData.bottom = new FormAttachment(100, 0);
        _ScrolledFormText.setLayoutData(scrolledFormTextFormData);

    }

    @Override
    protected void initFromModelInternal() {

        if (_JmxDocFormText != null) {
            String jmxDocFormText = JmxDocFormText.getFormText(getModel().getDoc());
            _JmxDocFormText.setText(jmxDocFormText, true, false);
            _ScrolledFormText.pack();
            _ScrolledFormText.layout(true);
            forceLayout();
            
        }
    }

}
