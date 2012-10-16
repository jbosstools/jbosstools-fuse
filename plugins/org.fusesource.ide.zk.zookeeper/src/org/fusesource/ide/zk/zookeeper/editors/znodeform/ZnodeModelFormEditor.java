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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.IManagedForm;
import org.fusesource.ide.zk.zookeeper.ZooKeeperActivator;
import org.fusesource.ide.zk.zookeeper.actions.NewZnodeAction;
import org.fusesource.ide.zk.zookeeper.data.Znode;
import org.fusesource.ide.zk.zookeeper.model.ZnodeModel;
import org.fusesource.ide.zk.zookeeper.model.ZooKeeperConnectionModel;
import org.fusesource.ide.zk.zookeeper.viewers.ZnodeModelElementType;
import org.fusesource.ide.zk.zookeeper.viewers.ZooKeeperConnectionModelElementType;
import org.fusesource.ide.zk.core.actions.BaseAction.InputType;
import org.fusesource.ide.zk.core.editors.DataModelFormEditor;
import org.fusesource.ide.zk.core.model.DataModelSourceException;
import org.fusesource.ide.zk.core.model.GenericDataModelEvent;
import org.fusesource.ide.zk.core.model.IGenericDataModelEventListener;
import org.fusesource.ide.zk.core.reflect.SwtThreadSafeDelegatingInvocationHandler;
import org.fusesource.ide.zk.core.reflect.SwtThreadSafeDelegatingInvocationHandler.IWidgetProvider;
import org.fusesource.ide.zk.core.viewers.DataModelElementType;

import java.util.List;

import org.apache.zookeeper.data.ACL;

public final class ZnodeModelFormEditor extends DataModelFormEditor<ZnodeModel> {

    public static final String ID = ZnodeModelFormEditor.class.getName() + ZooKeeperActivator.VERSION_SUFFIX;

    private ZnodeModelAclFormPage _AclZnodeFormPage;
    private ZnodeModelChildrenFormPage _ChildrenZnodeFormPage;
    private ZnodeModelDataFormPage _DataZnodeFormPage;
    private int _LastModificationAversion;
    private int _LastModificationVersion;
    private NewZnodeAction _NewZnodeAction;
    private ZnodeModelStatFormPage _StatZnodeFormPage;
    private IGenericDataModelEventListener _ZooKeeperConnectionModelEventListener;

    public ZnodeModelFormEditor() {
        _LastModificationVersion = -1;
        _LastModificationAversion = -1;
    }

    @Override
    public void doSave(IProgressMonitor monitor) {

        ZnodeModel znodeModel = getModel();

        if (znodeModel.isDestroyed()) {
            return;
        }

        Znode znode = znodeModel.getData();

        try {

            if (_DataZnodeFormPage != null && _DataZnodeFormPage.isDirty()) {

                byte[] data = null;
                try {
                    data = _DataZnodeFormPage.getZnodeDataFromEditor();
                }
                catch (Exception e) {

                    // TODO: Log error?

                    Shell shell = getEditorSite().getShell();
                    MessageBox errorMessageBox = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);

                    errorMessageBox.setMessage("Failed to save Znode data.  Error details: " + e.getMessage());
                    errorMessageBox.setText("Save Failed");
                    errorMessageBox.open();

                    monitor.setCanceled(true);
                    return;
                }

                znode.setData(data);
                znodeModel.setDirtyData(true);
                _LastModificationVersion = znode.getStat().getVersion() + 1;
            }

            if (_AclZnodeFormPage != null && _AclZnodeFormPage.isDirty()) {

                List<ACL> acl = null;
                try {
                    acl = _AclZnodeFormPage.getZnodeAclFromEditor();
                }
                catch (Exception e) {

                    // TODO: Log error?

                    Shell shell = getEditorSite().getShell();
                    MessageBox errorMessageBox = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);

                    errorMessageBox.setMessage("Failed to save Znode ACL.  Error details: " + e.getMessage());
                    errorMessageBox.setText("Save Failed");
                    errorMessageBox.open();

                    monitor.setCanceled(true);
                    return;
                }

                znode.setAcl(acl);
                znodeModel.setDirtyAcl(true);
                _LastModificationAversion = znode.getStat().getAversion() + 1;
            }

            if (znodeModel.isDirtyData() || znodeModel.isDirtyAcl()) {

                try {
                    znodeModel.updateData();
                    saveCompleted();
                }
                catch (DataModelSourceException e) {
                    ZooKeeperActivator.reportError(e);
                    monitor.setCanceled(true);
                }
            }
        }
        finally {
            _LastModificationVersion = -1;
            _LastModificationAversion = -1;
        }

    }

    /**
     * Returns the lastModificationAversion.
     * 
     * @return The lastModificationAversion
     */
    public final int getLastModificationAversion() {
        return _LastModificationAversion;
    }

    /**
     * Returns the lastModificationVersion.
     * 
     * @return The lastModificationVersion
     */
    public int getLastModificationVersion() {
        return _LastModificationVersion;
    }

    @Override
    public String getModelDestroyedMessage() {
        String message = "Znode no longer exists";
        if (getZooKeeperConnectionModel().isDestroyed()) {
            message = message + " (ZooKeeper connection deleted)";
        }
        return message;
    }

    /**
     * Returns the ZooKeeperConnectionModel.
     * 
     * @return The ZooKeeperConnectionModel
     */
    public ZooKeeperConnectionModel getZooKeeperConnectionModel() {
        ZnodeModel znodeModel = getModel();
        if (znodeModel == null) {
            return null;
        }

        return znodeModel.getOwnerModel();
    }

    @Override
    public boolean isDirty() {
        if (getZooKeeperConnectionModel().isDestroyed()) {
            return false;
        }

        return super.isDirty();
    }

    @Override
    public void setFocus() {
        super.setFocus();
        _NewZnodeAction.updateState();
    }

    @Override
    protected void addPages() {

        try {
            _DataZnodeFormPage = new ZnodeModelDataFormPage(this);
            addPage(_DataZnodeFormPage);

            if (!getModel().getData().isEphemeral()) {
                _ChildrenZnodeFormPage = new ZnodeModelChildrenFormPage(this);
                addPage(_ChildrenZnodeFormPage);
            }

            _AclZnodeFormPage = new ZnodeModelAclFormPage(this);
            addPage(_AclZnodeFormPage);

            _StatZnodeFormPage = new ZnodeModelStatFormPage(this);
            addPage(_StatZnodeFormPage);
        }
        catch (PartInitException e) {
            // TODO: Log
            e.printStackTrace();
        }

    }

    @Override
    protected void contributeToToolBar(IManagedForm headerForm, IToolBarManager toolBarManager) {
        toolBarManager.add(_NewZnodeAction);
        toolBarManager.add(new Separator());
        super.contributeToToolBar(headerForm, toolBarManager);
    }

    @Override
    protected DataModelElementType getOwnerModelElementType() {
        return new ZooKeeperConnectionModelElementType();
    }

    @Override
    protected DataModelElementType getParentModelElementType() {
        return new ZnodeModelElementType();
    }

    @Override
    protected String getFormText(ZnodeModel model) {
        return model.getData().getPath();
    }

    @Override
    protected void makeActions() {
        super.makeActions();
        _NewZnodeAction = new NewZnodeAction(InputType.EDITOR_INPUT);
    }

    @Override
    protected void registerModelEventListener() {
        super.registerModelEventListener();

        ZooKeeperConnectionModelEventListener zooKeeperConnectionModelEventListenerDelegate = new ZooKeeperConnectionModelEventListener();

        _ZooKeeperConnectionModelEventListener = (IGenericDataModelEventListener) SwtThreadSafeDelegatingInvocationHandler
                .createProxyInstance(zooKeeperConnectionModelEventListenerDelegate,
                        IGenericDataModelEventListener.class, true);

        getZooKeeperConnectionModel().addGenericEventListener(_ZooKeeperConnectionModelEventListener);

    }

    @Override
    protected void unregisterModelEventListener() {
        super.unregisterModelEventListener();

        if (_ZooKeeperConnectionModelEventListener != null) {
            getZooKeeperConnectionModel().removeGenericEventListener(_ZooKeeperConnectionModelEventListener);
        }
    }

    public final class ZooKeeperConnectionModelEventListener implements IGenericDataModelEventListener, IWidgetProvider {

        @Override
        public void dataModelDataChanged(GenericDataModelEvent event) {
        }

        @Override
        public void dataModelDataRefreshed(GenericDataModelEvent event) {
        }

        @Override
        public void dataModelDestroyed(GenericDataModelEvent event) {
            if (getContainer().isDisposed()) {
                return;
            }

            editorDirtyStateChanged();
        }

        @Override
        public Widget getWidget() {
            return getContainer();
        }

    }

}
