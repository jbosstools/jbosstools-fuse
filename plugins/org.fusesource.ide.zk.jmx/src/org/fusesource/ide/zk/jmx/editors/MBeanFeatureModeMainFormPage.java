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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.fusesource.ide.zk.jmx.JmxActivator;
import org.fusesource.ide.zk.jmx.jmxdoc.JmxDocFormText;
import org.fusesource.ide.zk.jmx.model.MBeanFeatureModel;
import org.fusesource.ide.zk.core.editors.DataModelFormEditor;

import javax.management.Descriptor;
import javax.management.MBeanFeatureInfo;


/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public abstract class MBeanFeatureModeMainFormPage<M extends MBeanFeatureModel<M, ?>> extends
        BaseJmxModelMainFormPage<M> {

    protected static final String FEATURE_INFO_PROPERTY_NAME_NAME = "Name";
    protected static final String FEATURE_INFO_PROPERTY_NAME_DESCRIPTION = "Description";
    public static final String DETAIL_SECTION_TITLE = "Detail";

    private FormText _JmxDocFormText;

    public MBeanFeatureModeMainFormPage(DataModelFormEditor<M> editor, String id) {
        super(editor, id);
    }

    protected MBeanFeatureInfo getFeatureInfo() {
        return getModel().getData().getInfo();
    }

    @Override
    protected Descriptor getJmxDescriptor() {
        return getFeatureInfo().getDescriptor();
    }

    @Override
    protected void initInfoSectionFromModel() {

        Table table = getInfoTable();
        table.removeAll();

        MBeanFeatureInfo featureInfo = getFeatureInfo();

        TableItem nameItem = new TableItem(table, SWT.NONE);
        nameItem.setText(0, FEATURE_INFO_PROPERTY_NAME_NAME);
        nameItem.setText(1, featureInfo.getName());

        TableItem descriptionItem = new TableItem(table, SWT.NONE);
        descriptionItem.setText(0, FEATURE_INFO_PROPERTY_NAME_DESCRIPTION);
        descriptionItem.setText(1, featureInfo.getDescription());
    }

    @Override
    protected Section createDetailSection(ScrolledForm form, Composite client, FormToolkit toolkit) {

        Section section = createSection(form, client, toolkit, DETAIL_SECTION_TITLE, JmxActivator
                .getManagedImage(JmxActivator.IMAGE_KEY_OBJECT_JMX_DOC));
        Composite sectionClient = createSectionClient(section, toolkit);

        _JmxDocFormText = toolkit.createFormText(sectionClient, false);
        JmxDocFormText.initFormText(_JmxDocFormText);

        FormData jmxdocFormTextFormData = new FormData();
        jmxdocFormTextFormData.top = new FormAttachment(0, 0);
        jmxdocFormTextFormData.left = new FormAttachment(0, 0);
        _JmxDocFormText.setLayoutData(jmxdocFormTextFormData);

        GridData detailSectionGridData = new GridData(GridData.FILL_HORIZONTAL);
        section.setLayoutData(detailSectionGridData);

        return section;
    }

    @Override
    protected void initDetailSectionFromModel() {
        if (_JmxDocFormText != null) {
            String jmxDocFormText = JmxDocFormText.getFormText(getModel().getDoc(), false);
            _JmxDocFormText.setText(jmxDocFormText, true, false);
        }
    }

    @Override
    protected void initPrimarySectionFromModel() {
    }

}
