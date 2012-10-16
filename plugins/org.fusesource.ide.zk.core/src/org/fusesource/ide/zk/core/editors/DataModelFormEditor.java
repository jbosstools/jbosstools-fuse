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
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.HyperlinkGroup;
import org.eclipse.ui.forms.HyperlinkSettings;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.editor.SharedHeaderFormEditor;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.fusesource.ide.zk.core.actions.BaseOpenAction;
import org.fusesource.ide.zk.core.actions.RefreshAction;
import org.fusesource.ide.zk.core.actions.BaseAction.InputType;
import org.fusesource.ide.zk.core.model.DataModel;
import org.fusesource.ide.zk.core.model.GenericDataModelEvent;
import org.fusesource.ide.zk.core.model.IGenericDataModelEventListener;
import org.fusesource.ide.zk.core.reflect.SwtThreadSafeDelegatingInvocationHandler;
import org.fusesource.ide.zk.core.reflect.SwtThreadSafeDelegatingInvocationHandler.IWidgetProvider;
import org.fusesource.ide.zk.core.viewers.DataModelElementType;
import org.fusesource.ide.zk.core.widgets.BaseControlContribution;
import org.fusesource.ide.zk.core.widgets.ElementTypeDataModelImageHyperlinkView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * {@link SharedHeaderFormEditor} used to view/edit a {@link DataModel}. Pages must be {@link DataModelFormPage}
 * instances. Input must be {@link DataModelEditorInput}.
 * 
 * @author Mark Masse
 */
public abstract class DataModelFormEditor<M extends DataModel<M, ?, ?>> extends SharedHeaderFormEditor {

    private M _Model;
    private IGenericDataModelEventListener _ModelEventListener;
    private final List<DataModelFormPage<M>> _Pages;
    private RefreshAction _RefreshAction;

    public DataModelFormEditor() {
        _Pages = new ArrayList<DataModelFormPage<M>>();
    }

    public int addPage(DataModelFormPage<M> page) throws PartInitException {
        return addPage((IFormPage) page);
    }

    @Override
    public int addPage(IFormPage page) throws PartInitException {
        int index = getPageCount();
        addPage(index, page);
        return index;
    }

    public void addPage(int index, DataModelFormPage<M> page) throws PartInitException {
        addPage(index, (IFormPage) page);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addPage(int index, IFormPage page) throws PartInitException {

        if (!(page instanceof DataModelFormPage<?>)) {
            throw new PartInitException("Invalid Page: Must be " + DataModelFormPage.class.getName());
        }

        DataModelFormPage<M> dataModelFormPage = (DataModelFormPage<M>) page;

        super.addPage(index, page);

        _Pages.add(index, dataModelFormPage);

        Image pageImage = dataModelFormPage.getImage();
        if (pageImage != null) {
            setPageImage(index, pageImage);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        unregisterModelEventListener();
    }

    @Override
    public void doSave(IProgressMonitor monitor) {
    }

    @Override
    public void doSaveAs() {
    }

    /**
     * Returns the elementType.
     * 
     * @return The elementType
     */
    public final DataModelElementType getElementType() {
        return getDataModelEditorInput().getElementType();
    }

    public String getId() {
        return getClass().getName();
    }

    /**
     * Returns the model.
     * 
     * @return The model
     */
    public M getModel() {
        return _Model;
    }

    /**
     * Returns the modelDestroyedMessage.
     * 
     * @return The modelDestroyedMessage
     */
    public String getModelDestroyedMessage() {
        return null;
    }

    /**
     * Returns the pages.
     * 
     * @return The pages
     */
    public List<DataModelFormPage<M>> getPages() {
        return Collections.unmodifiableList(_Pages);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        super.init(site, input);

        if (!(input instanceof DataModelEditorInput<?>)) {
            throw new PartInitException("Invalid Input: Must be " + DataModelEditorInput.class.getName());
        }

        _Model = ((DataModelEditorInput<M>) input).getModel();

        registerModelEventListener();
        updateTitle();
    }

    @SuppressWarnings("unchecked")
    public final DataModelEditorInput<M> getDataModelEditorInput() {
        return (DataModelEditorInput<M>) getEditorInput();
    }

    @Override
    public boolean isDirty() {

        if (getModel().isDestroyed()) {
            return false;
        }

        for (DataModelFormPage<M> page : _Pages) {
            if (page.isDirty()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

    @Override
    public void removePage(int pageIndex) {

        super.removePage(pageIndex);

        DataModelFormPage<M> removePage = null;
        for (DataModelFormPage<M> page : _Pages) {
            if (page.getIndex() == pageIndex) {
                removePage = page;
                break;
            }
        }

        if (removePage != null) {
            _Pages.remove(removePage);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setFocus() {
        super.setFocus();

        RefreshAction refreshAction = getRefreshAction();
        if (refreshAction != null) {
            refreshAction.updateState();
        }

        IFormPage formPage = getActivePageInstance();
        if (formPage == null) {
            return;
        }

        if (!(formPage instanceof DataModelFormPage<?>)) {
            return;
        }

        DataModelFormPage<M> dataModelFormPage = (DataModelFormPage<M>) formPage;
        dataModelFormPage.forceLayout();
    }

    protected void addImageHyperlinkToolBarContribution(final IManagedForm headerForm,
            final IToolBarManager toolBarManager, final BaseControlContribution baseControlContribution,
            final DataModel<?, ?, ?> model) {

        final Separator separator = new Separator();
        toolBarManager.add(separator);
        toolBarManager.add(baseControlContribution);

        baseControlContribution.addControlDisposeListener(new DisposeListener() {

            @Override
            public void widgetDisposed(DisposeEvent e) {

                if (!model.isDestroyed()) {
                    // Only do this clean up when the model destruction caused the widget disposal
                    // WARNING: toolBarManager.update(true); throws a NullPointer if called during app exit.
                    return;
                }

                toolBarManager.remove(separator);
                separator.dispose();
                toolBarManager.remove(baseControlContribution);
                toolBarManager.update(true);
            }
        });
    }

    @Override
    protected void addPages() {
    }

    protected void contributeToToolBar(final IManagedForm headerForm, final IToolBarManager toolBarManager) {

        RefreshAction refreshAction = getRefreshAction();

        if (refreshAction != null) {
            toolBarManager.add(refreshAction);
        }

        DataModel<?, ?, ?> parentModel = getParentModel();
        DataModelElementType parentModelElementType = getParentModelElementType();
        if (parentModel != null && parentModelElementType != null) {
            final BaseControlContribution parentImageHyperlinkToolBarContribution = createImageHyperlinkToolBarContribution(
                    "Parent Link", parentModel, parentModelElementType);

            if (parentImageHyperlinkToolBarContribution != null) {
                addImageHyperlinkToolBarContribution(headerForm, toolBarManager,
                        parentImageHyperlinkToolBarContribution, parentModel);
            }
        }

        DataModel<?, ?, ?> ownerModel = getOwnerModel();
        DataModelElementType ownerModelElementType = getOwnerModelElementType();
        if (ownerModel != null && ownerModelElementType != null && ownerModel != parentModel) {
            final BaseControlContribution ownerImageHyperlinkToolBarContribution = createImageHyperlinkToolBarContribution(
                    "Owner Link", ownerModel, ownerModelElementType);

            if (ownerImageHyperlinkToolBarContribution != null) {
                addImageHyperlinkToolBarContribution(headerForm, toolBarManager,
                        ownerImageHyperlinkToolBarContribution, ownerModel);
            }
        }
    }

    @Override
    protected void createHeaderContents(IManagedForm headerForm) {
        super.createHeaderContents(headerForm);

        FormToolkit toolkit = headerForm.getToolkit();
        ScrolledForm scrolledForm = headerForm.getForm();
        Form form = scrolledForm.getForm();
        toolkit.decorateFormHeading(form);

        updateTitle();
        makeActions();

        IToolBarManager toolBarManager = form.getToolBarManager();
        contributeToToolBar(headerForm, toolBarManager);
        toolBarManager.update(true);

    }

    protected BaseControlContribution createImageHyperlinkToolBarContribution(String id,
            final DataModel<?, ?, ?> model, final DataModelElementType modelElementType) {

        BaseControlContribution controlContribution = new BaseControlContribution(id) {

            @Override
            protected Control createControlInternal(Composite parent) {
                ImageHyperlink imageHyperlink = new ImageHyperlink(parent, SWT.TOP | SWT.WRAP);
                HyperlinkGroup group = new HyperlinkGroup(imageHyperlink.getDisplay());
                group.setHyperlinkUnderlineMode(HyperlinkSettings.UNDERLINE_HOVER);
                group.add(imageHyperlink);

                imageHyperlink.addHyperlinkListener(new HyperlinkAdapter() {

                    @Override
                    public void linkActivated(HyperlinkEvent e) {
                        BaseOpenAction openAction = modelElementType.getOpenAction();
                        if (openAction != null) {

                            try {
                                openAction.runWithObject(model);
                            }
                            catch (Exception e1) {
                                openAction.reportError(e1);
                            }
                        }
                    }
                });

                ElementTypeDataModelImageHyperlinkView view = new ElementTypeDataModelImageHyperlinkView(model,
                        imageHyperlink, modelElementType);
                view.updateView();

                return imageHyperlink;
            }
        };

        return controlContribution;
    }

    protected Image getFormImage(M model) {
        return getElementType().getLargeImage(model);
    }

    protected String getFormText(M model) {
        return getElementType().getText(model);
    }

    protected DataModel<?, ?, ?> getOwnerModel() {
        M model = getModel();
        return model.getOwnerModel();
    }

    protected DataModelElementType getOwnerModelElementType() {
        return null;
    }

    /**
     * Returns the pages.
     * 
     * @return The pages
     */
    protected List<DataModelFormPage<M>> getPagesInternal() {
        return _Pages;
    }

    protected DataModel<?, ?, ?> getParentModel() {
        M model = getModel();
        return model.getParentModel();
    }

    protected DataModelElementType getParentModelElementType() {
        return null;
    }

    protected String getPartName(M model) {
        return getElementType().getText(model);
    }

    /**
     * Returns the refreshAction.
     * 
     * @return The refreshAction
     */
    protected final RefreshAction getRefreshAction() {
        return _RefreshAction;
    }

    protected Image getTitleImage(M model) {
        return getElementType().getImage(model);
    }

    protected String getTitleToolTip(M model) {
        return getElementType().getToolTipText(model);
    }

    protected void makeActions() {
        _RefreshAction = new RefreshAction(InputType.EDITOR_INPUT);
    }

    protected void modelDataChanged(GenericDataModelEvent event) {
        if (getContainer().isDisposed()) {
            return;
        }

        updateTitle();

        for (DataModelFormPage<M> page : _Pages) {
            page.modelDataChanged(event);
        }

    }

    protected void modelDataRefreshed(GenericDataModelEvent event) {
        if (getContainer().isDisposed()) {
            return;
        }

        updateTitle();

        for (DataModelFormPage<M> page : _Pages) {
            page.modelDataRefreshed(event);
        }
    }

    protected void modelDestroyed(GenericDataModelEvent event) {
        if (getContainer().isDisposed()) {
            return;
        }

        updateTitle();
        editorDirtyStateChanged();

        String modelDestroyedMessage = getModelDestroyedMessage();
        if (modelDestroyedMessage != null) {
            IManagedForm headerForm = getHeaderForm();
            ScrolledForm scrolledForm = headerForm.getForm();
            Form form = scrolledForm.getForm();
            form.setMessage(modelDestroyedMessage, IMessageProvider.ERROR);
        }

        for (DataModelFormPage<M> page : _Pages) {
            page.modelDestroyed(event);
        }

    }

    protected void registerModelEventListener() {

        M model = getModel();

        if (model == null) {
            return;
        }

        if (_ModelEventListener == null) {
            ModelEventListener modelEventListenerDelegate = new ModelEventListener();
            _ModelEventListener = (IGenericDataModelEventListener) SwtThreadSafeDelegatingInvocationHandler
                    .createProxyInstance(modelEventListenerDelegate, IGenericDataModelEventListener.class, true);
        }

        model.addGenericEventListener(_ModelEventListener);
    }

    protected void saveCompleted() {

        for (DataModelFormPage<M> page : _Pages) {
            page.saveCompleted();
        }

        editorDirtyStateChanged();
    }

    protected void unregisterModelEventListener() {
        M model = getModel();

        if (model != null && _ModelEventListener != null) {
            model.removeGenericEventListener(_ModelEventListener);
        }
    }

    protected void updateTitle() {

        M model = getModel();
        if (model == null) {
            return;
        }

        setPartName(getPartName(model));
        setTitleToolTip(getTitleToolTip(model));
        setTitleImage(getTitleImage(model));

        IManagedForm headerForm = getHeaderForm();

        if (headerForm != null) {
            ScrolledForm scrolledForm = headerForm.getForm();
            Form form = scrolledForm.getForm();
            form.setText(getFormText(model));
            form.setImage(getFormImage(model));
        }
    }

    public final class ModelEventListener implements IGenericDataModelEventListener, IWidgetProvider {

        @Override
        public void dataModelDataChanged(GenericDataModelEvent event) {
            modelDataChanged(event);
        }

        @Override
        public void dataModelDataRefreshed(GenericDataModelEvent event) {
            modelDataRefreshed(event);
        }

        @Override
        public void dataModelDestroyed(GenericDataModelEvent event) {
            modelDestroyed(event);
        }

        @Override
        public Widget getWidget() {
            return getContainer();
        }

    }

}
