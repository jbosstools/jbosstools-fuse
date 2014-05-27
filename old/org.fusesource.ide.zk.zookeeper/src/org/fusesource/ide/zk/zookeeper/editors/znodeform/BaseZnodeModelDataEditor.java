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

package org.fusesource.ide.zk.zookeeper.editors.znodeform;


import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.fusesource.ide.zk.zookeeper.model.ZnodeModel;

/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public abstract class BaseZnodeModelDataEditor extends Composite {

    private boolean _ActiveEditor;
    private final ZnodeModelDataFormPage _DataZnodeFormPage;
    private boolean _Dirty;
    private boolean _ProgrammaticUpdate;

    /**
     * TODO: Comment.
     * 
     * @param parent
     * @param style
     */
    public BaseZnodeModelDataEditor(ZnodeModelDataFormPage dataZnodeFormPage, Composite parent, int style) {
        super(parent, style);
        _DataZnodeFormPage = dataZnodeFormPage;
        _DataZnodeFormPage.getManagedForm().getToolkit().adapt(this);        
        setLayout(createLayout());
        createContent();        
    }

    /**
     * Returns the dataZnodeFormPage.
     * 
     * @return The dataZnodeFormPage
     */
    public final ZnodeModelDataFormPage getDataZnodeFormPage() {
        return _DataZnodeFormPage;
    }

    /**
     * TODO: Comment.
     * 
     * @return
     */
    public IManagedForm getManagedForm() {
        return getDataZnodeFormPage().getManagedForm();
    }

    /**
     * TODO: Comment.
     * 
     * @return
     */
    public FormToolkit getToolkit() {
        return getManagedForm().getToolkit();
    }

    /**
     * TODO: Comment.
     * 
     * @return
     */
    public abstract byte[] getZnodeDataFromEditor() throws Exception;

    /**
     * TODO: Comment.
     * 
     * @return
     */
    public final ZnodeModelFormEditor getZnodeEditor() {
        return (ZnodeModelFormEditor) getDataZnodeFormPage().getEditor();
    }

    /**
     * TODO: Comment.
     * 
     * @return
     */
    public final ZnodeModel getZnodeModel() {
        return getDataZnodeFormPage().getModel();
    }

    public boolean isActiveEditor() {
        return _ActiveEditor;
    }

    /**
     * Returns the dirty.
     * 
     * @return The dirty
     */
    public boolean isDirty() {
        return _Dirty;
    }

    /**
     * Returns the programmaticUpdate.
     * 
     * @return The programmaticUpdate
     */
    public boolean isProgrammaticUpdate() {
        return _ProgrammaticUpdate;
    }

    public void setActiveEditor(boolean active) {
        _ActiveEditor = active;

        if (active && !isDirty()) {
            syncZnodeModelData();
        }
    }

    public void setDirty(boolean dirty) {
        _Dirty = dirty;
    }

    /**
     * Sets the programmaticUpdate.
     * 
     * @param programmaticUpdate the programmaticUpdate to set
     */
    public void setProgrammaticUpdate(boolean programmaticUpdate) {
        _ProgrammaticUpdate = programmaticUpdate;
    }

    public final void syncZnodeModelData() {
        setProgrammaticUpdate(true);
        hookSyncZnodeModelData(getZnodeModel());
        setProgrammaticUpdate(false);
    }

    /**
     * TODO: Comment.
     * 
     */
    protected abstract void createContent();

    protected Layout createLayout() {
        FormLayout formLayout = new FormLayout();
        formLayout.marginHeight = 8;
        formLayout.marginWidth = 8;
        formLayout.spacing = 8;       
        return formLayout;
    }

    protected abstract void hookSyncZnodeModelData(ZnodeModel znodeModel);

    protected final void setDirtyInternal(boolean dirty) {
        if (isProgrammaticUpdate()) {
            return;
        }

        setDirty(dirty);
        getDataZnodeFormPage().setDirtyInternal(dirty);
        
    }

}
