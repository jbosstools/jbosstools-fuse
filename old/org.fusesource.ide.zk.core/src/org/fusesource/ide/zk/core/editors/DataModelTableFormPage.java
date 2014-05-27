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

package org.fusesource.ide.zk.core.editors;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.fusesource.ide.zk.core.model.DataModel;
import org.fusesource.ide.zk.core.viewers.IElementType;
import org.fusesource.ide.zk.core.viewers.ViewerFactory;


/**
 * Base class for a table-based {@link DataModelFormPage} used within a {@link DataModelFormEditor}.
 * 
 * @see ViewerFactory
 * 
 * @author Mark Masse
 */
public abstract class DataModelTableFormPage<M extends DataModel<M, ?, ?>> extends DataModelFormPage<M> {

    public static final int DEFAULT_TABLE_STYLE = SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION;
    public static final FormData TABLE_FORM_DATA = new FormData();

    static {
        TABLE_FORM_DATA.top = new FormAttachment(0, 0);
        TABLE_FORM_DATA.left = new FormAttachment(0, 0);
        TABLE_FORM_DATA.bottom = new FormAttachment(100, 0);
        TABLE_FORM_DATA.right = new FormAttachment(100, 0);
    }

    private final Class<?> _TableModelClass;
    private final IElementType _TableModelElementType;
    private TableViewer _TableViewer;
    private final Object _TableViewerInput;
    private final IElementType _TableViewerInputElementType;

    public DataModelTableFormPage(DataModelFormEditor<M> editor, String id, String title, Class<?> tableModelClass,
            IElementType tableModelElementType, Object tableViewerInput, IElementType tableViewerInputElementType) {
        super(editor, id, title);
        _TableModelClass = tableModelClass;
        _TableModelElementType = tableModelElementType;
        _TableViewerInput = tableViewerInput;
        _TableViewerInputElementType = tableViewerInputElementType;
    }

    public final Class<?> getTableModelClass() {
        return _TableModelClass;
    }

    public final IElementType getTableModelElementType() {
        return _TableModelElementType;
    }

    public final TableViewer getTableViewer() {
        return _TableViewer;
    }

    public final Object getTableViewerInput() {
        return _TableViewerInput;
    }

    public final IElementType getTableViewerInputElementType() {
        return _TableViewerInputElementType;
    }

    protected void configureTable(Table table) {

    }

    @Override
    protected void createModelFormContent(IManagedForm managedForm, Composite client) {

        Table table = createTable(client);
        configureTable(table);
        table.setLayoutData(getTableFormData());

        _TableViewer = createTableViewer(table);
    }

    protected Table createTable(Composite client) {
        FormToolkit toolkit = getManagedForm().getToolkit();
        return toolkit.createTable(client, DEFAULT_TABLE_STYLE);
    }

    protected TableViewer createTableViewer(Table table) {

        return ViewerFactory.createDataModelTableViewer(getSite(), table, getTableModelClass(),
                getTableModelElementType(), getTableViewerInput(), getTableViewerInputElementType());
    }

    protected FormData getTableFormData() {
        return TABLE_FORM_DATA;
    }

    protected int getTableStyle() {
        return DEFAULT_TABLE_STYLE;
    }

    @Override
    protected void initFromModelInternal() {
    }

}
