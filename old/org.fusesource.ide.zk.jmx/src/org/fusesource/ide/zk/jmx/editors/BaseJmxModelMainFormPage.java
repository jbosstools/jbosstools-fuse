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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.fusesource.ide.zk.core.EclipseCoreActivator;
import org.fusesource.ide.zk.core.editors.DataModelFormEditor;
import org.fusesource.ide.zk.core.editors.DataModelFormPage;
import org.fusesource.ide.zk.core.model.DataModel;

import javax.management.Descriptor;


/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public abstract class BaseJmxModelMainFormPage<M extends DataModel<M, ?, ?>> extends DataModelFormPage<M> {

    protected static final int[] DEFAULT_NAME_VALUE_COLUMN_ALIGNMENTS = new int[] { SWT.LEFT, SWT.LEFT };
    protected static final String[] DEFAULT_NAME_VALUE_COLUMN_TITLES = new String[] { "Name", "Value" };
    protected static final int[] DEFAULT_NAME_VALUE_COLUMN_WIDTHS = new int[] { SWT.DEFAULT, 350 };
    protected static final int DEFAULT_TABLE_STYLE = SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION;

    protected static final Image IMAGE = EclipseCoreActivator
            .getManagedImage(EclipseCoreActivator.IMAGE_KEY_OBJECT_MAIN_TAB);
    protected static final String TITLE = "Main";

    protected static final String DESCRIPTOR_SECTION_TITLE = "Descriptor";

    private Section _DetailSection;

    private Section _DescriptorSection;
    private String _DescriptorSectionTitle;

    private Section _InfoSection;
    private String _InfoSectionTitle;

    private Section _PrimarySection;
    private String _PrimarySectionTitle;

    public BaseJmxModelMainFormPage(DataModelFormEditor<M> editor, String id) {
        super(editor, id, TITLE);
        setImage(IMAGE);
        setDescriptorSectionTitle(DESCRIPTOR_SECTION_TITLE);
    }

    /**
     * Returns the descriptorSectionTitle.
     * 
     * @return The descriptorSectionTitle
     */
    public String getDescriptorSectionTitle() {
        return _DescriptorSectionTitle;
    }

    /**
     * Returns the infoSectionTitle.
     * 
     * @return The infoSectionTitle
     */
    public String getInfoSectionTitle() {
        return _InfoSectionTitle;
    }

    /**
     * Returns the primarySectionTitle.
     * 
     * @return The primarySectionTitle
     */
    public String getPrimarySectionTitle() {
        return _PrimarySectionTitle;
    }

    /**
     * Sets the descriptorSectionTitle.
     * 
     * @param descriptorSectionTitle the descriptorSectionTitle to set
     */
    public void setDescriptorSectionTitle(String descriptorSectionTitle) {
        _DescriptorSectionTitle = descriptorSectionTitle;
        if (_DescriptorSection != null) {
            _DescriptorSection.setText(_DescriptorSectionTitle);
        }
    }

    /**
     * Sets the infoSectionTitle.
     * 
     * @param infoSectionTitle the infoSectionTitle to set
     */
    public void setInfoSectionTitle(String infoSectionTitle) {
        _InfoSectionTitle = infoSectionTitle;
        if (_InfoSection != null) {
            _InfoSection.setText(_InfoSectionTitle);
        }
    }

    /**
     * Sets the primarySectionTitle.
     * 
     * @param primarySectionTitle the primarySectionTitle to set
     */
    public void setPrimarySectionTitle(String primarySectionTitle) {
        _PrimarySectionTitle = primarySectionTitle;
        if (_PrimarySection != null) {
            _PrimarySection.setText(_PrimarySectionTitle);
        }
    }

    @Override
    protected Layout createClientLayout() {
        GridLayout gridLayout = new GridLayout(1, true);
        gridLayout.verticalSpacing = 20;
        gridLayout.marginWidth = 8;
        return gridLayout;
    }

    protected Section createDescriptorSection(ScrolledForm form, Composite client, FormToolkit toolkit) {

        Descriptor descriptor = getJmxDescriptor();
        if (descriptor == null || descriptor.getFieldNames().length == 0) {
            return null;
        }

        return createTableSection(form, client, toolkit, getDescriptorSectionTitle(), EclipseCoreActivator
                .getManagedImage(EclipseCoreActivator.IMAGE_KEY_OBJECT_PROPERTIES));
    }

    protected Section createInfoSection(ScrolledForm form, Composite client, FormToolkit toolkit) {
        return createTableSection(form, client, toolkit, getInfoSectionTitle(), EclipseCoreActivator
                .getManagedImage(EclipseCoreActivator.IMAGE_KEY_OBJECT_INFORMATION));
    }

    @Override
    protected final void createModelFormContent(IManagedForm managedForm, Composite client) {
        ScrolledForm form = managedForm.getForm();
        FormToolkit toolkit = managedForm.getToolkit();

        _PrimarySection = createPrimarySection(form, client, toolkit);
        _DetailSection = createDetailSection(form, client, toolkit);
        _InfoSection = createInfoSection(form, client, toolkit);
        _DescriptorSection = createDescriptorSection(form, client, toolkit);

        if (_PrimarySection != null) {
            GridData primarySectionGridData = new GridData(GridData.FILL_HORIZONTAL);
            _PrimarySection.setLayoutData(primarySectionGridData);
        }

        if (_DetailSection != null) {
            GridData detailSectionGridData = new GridData(GridData.FILL_HORIZONTAL);
            _DetailSection.setLayoutData(detailSectionGridData);
        }

        if (_InfoSection != null) {
            Table infoSectionTable = getInfoTable();
            initTableEdit(infoSectionTable, null, 1);
            GridData infoSectionGridData = new GridData(GridData.FILL_BOTH);
            _InfoSection.setLayoutData(infoSectionGridData);
        }

        if (_DescriptorSection != null) {
            Table descriptorSectionTable = getDescriptorTable();
            initTableEdit(descriptorSectionTable, null, 1);
            GridData descriptorSectionGridData = new GridData(GridData.FILL_BOTH);
            _DescriptorSection.setLayoutData(descriptorSectionGridData);
        }

    }

    protected Section createPrimarySection(ScrolledForm form, Composite client, FormToolkit toolkit) {
        return null;
    }

    protected Section createDetailSection(ScrolledForm form, Composite client, FormToolkit toolkit) {
        return null;
    }

    protected Section createTableSection(final ScrolledForm form, Composite client, FormToolkit toolkit,
            String sectionTitle, Image image) {
        return createTableSection(form, client, toolkit, sectionTitle, image, DEFAULT_TABLE_STYLE,
                DEFAULT_NAME_VALUE_COLUMN_TITLES, DEFAULT_NAME_VALUE_COLUMN_ALIGNMENTS);
    }

    /**
     * Returns the descriptorSection.
     * 
     * @return The descriptorSection
     */
    protected final Section getDescriptorSection() {
        return _DescriptorSection;
    }

    /**
     * Returns the descriptorTable.
     * 
     * @return The descriptorTable
     */
    protected final Table getDescriptorTable() {
        if (_DescriptorSection == null) {
            return null;
        }

        return (Table) _DescriptorSection.getClient();
    }

    /**
     * Returns the infoSection.
     * 
     * @return The infoSection
     */
    protected final Section getInfoSection() {
        return _InfoSection;
    }

    /**
     * Returns the infoTable.
     * 
     * @return The infoTable
     */
    protected final Table getInfoTable() {
        return (Table) getInfoSection().getClient();
    }

    /**
     * Returns the primarySection.
     * 
     * @return The primarySection
     */
    protected final Section getPrimarySection() {
        return _PrimarySection;
    }

    protected void initDescriptorSectionFromModel() {

        Descriptor descriptor = getJmxDescriptor();
        if (descriptor == null) {
            return;
        }

        Table table = getDescriptorTable();
        if (table == null) {
            return;
        }

        table.removeAll();

        for (String fieldName : descriptor.getFieldNames()) {
            TableItem item = new TableItem(table, SWT.NONE);
            Object value = descriptor.getFieldValue(fieldName);
            item.setText(0, fieldName);
            item.setText(1, String.valueOf(value));
        }

        packTable(table, DEFAULT_NAME_VALUE_COLUMN_WIDTHS);
    }

    protected abstract Descriptor getJmxDescriptor();

    @Override
    protected void initFromModelInternal() {

        initPrimarySectionFromModel();
        initDetailSectionFromModel();
        initInfoSectionFromModel();
        packTable(getInfoTable(), DEFAULT_NAME_VALUE_COLUMN_WIDTHS);
        initDescriptorSectionFromModel();
        packTable(getDescriptorTable(), DEFAULT_NAME_VALUE_COLUMN_WIDTHS);

        // forceLayout();

        Section primarySection = getPrimarySection();
        if (primarySection != null) {
            primarySection.layout(true);
        }
        Section infoSection = getInfoSection();
        if (infoSection != null) {
            infoSection.layout(true);
        }
        Section descriptionSection = getDescriptorSection();
        if (descriptionSection != null) {
            descriptionSection.layout(true);
        }
    }

    protected abstract void initDetailSectionFromModel();

    protected abstract void initInfoSectionFromModel();

    protected abstract void initPrimarySectionFromModel();

}
