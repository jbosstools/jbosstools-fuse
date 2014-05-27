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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.fusesource.ide.zk.core.actions.BaseDeleteAction;
import org.fusesource.ide.zk.core.model.DataModel;
import org.fusesource.ide.zk.core.viewers.IElementType;
import org.fusesource.ide.zk.core.viewers.ViewerFactory;


/**
 * {@link EditorPart} used to view/edit {@link DataModel DataModels} in a {@link TableViewer}.
 * 
 * @see ViewerFactory
 * 
 * @author Mark Masse
 */
public abstract class DataModelTableEditor<M extends DataModel<M, ?, ?>> extends EditorPart {

    public static final int DEFAULT_TABLE_STYLE = SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION;

    private final Class<M> _ModelClass;
    private IElementType _ModelElementType;
    private TableViewer _TableViewer;

    public DataModelTableEditor(Class<M> modelClass, IElementType modelElementType) {
        _ModelClass = modelClass;
        _ModelElementType = modelElementType;
    }

    @Override
    public final void createPartControl(Composite parent) {

        Table table = createTable(parent);
        configureTable(table);

        _TableViewer = createTableViewer(table);

        getEditorSite().setSelectionProvider(_TableViewer);
    }

    @Override
    public void doSave(IProgressMonitor monitor) {
        // Not supported.
    }

    @Override
    public void doSaveAs() {
        // Not supported.
    }

    @SuppressWarnings("unchecked")
    public final DataModelTableEditorInput<M> getDataModelTableEditorInput() {
        return (DataModelTableEditorInput<M>) getEditorInput();
    }

    /**
     * Returns the modelClass.
     * 
     * @return The modelClass
     */
    public final Class<M> getModelClass() {
        return _ModelClass;
    }

    /**
     * Returns the tableViewer.
     * 
     * @return The tableViewer
     */
    public final TableViewer getTableViewer() {
        return _TableViewer;
    }

    @Override
    public final void init(IEditorSite site, IEditorInput input) throws PartInitException {
        init(input);
        setSite(site);
        setInput(input);
    }

    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

    @Override
    public void setFocus() {
        _TableViewer.getTable().setFocus();
    }

    protected void configureTable(Table table) {

    }

    protected Table createTable(Composite parent) {
        return new Table(parent, DEFAULT_TABLE_STYLE);
    }

    protected TableViewer createTableViewer(Table table) {

        DataModelTableEditorInput<M> dataModelTableEditorInput = getDataModelTableEditorInput();

        return ViewerFactory.createDataModelTableViewer(getSite(), table, getModelClass(), getModelElementType(),
                dataModelTableEditorInput.getTableViewerInput(), dataModelTableEditorInput
                        .getTableViewerInputElementType());
    }

    /**
     * Returns the deleteAction.
     * 
     * @return The deleteAction
     */
    protected BaseDeleteAction getDeleteAction() {
        return null;
    }

    /**
     * Returns the model's IElementType.
     * 
     * @return The model's IElementType
     */
    protected final IElementType getModelElementType() {
        return _ModelElementType;
    }

    protected void init(IEditorInput input) throws PartInitException {

        if (!(input instanceof DataModelTableEditorInput<?>)) {
            throw new PartInitException("Invalid Input: Must be " + DataModelTableEditorInput.class.getName());
        }

        setPartName(input.getName());
        setTitleToolTip(input.getName());
    }

}
