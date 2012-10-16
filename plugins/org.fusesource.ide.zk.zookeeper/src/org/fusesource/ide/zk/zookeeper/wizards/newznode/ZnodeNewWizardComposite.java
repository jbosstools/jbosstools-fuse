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

package org.fusesource.ide.zk.zookeeper.wizards.newznode;


import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Widget;
import org.fusesource.ide.zk.zookeeper.ZooKeeperActivator;
import org.fusesource.ide.zk.zookeeper.data.Znode;
import org.fusesource.ide.zk.zookeeper.model.ZnodeModel;
import org.fusesource.ide.zk.core.model.GenericDataModelEvent;
import org.fusesource.ide.zk.core.model.IGenericDataModelEventListener;
import org.fusesource.ide.zk.core.reflect.SwtThreadSafeDelegatingInvocationHandler;
import org.fusesource.ide.zk.core.reflect.SwtThreadSafeDelegatingInvocationHandler.IWidgetProvider;
import org.fusesource.ide.zk.core.widgets.grid.GridComposite;
import org.fusesource.ide.zk.core.widgets.grid.GridCompositeStatus;

/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public abstract class ZnodeNewWizardComposite extends GridComposite {

    protected static final String CONTROL_NAME_CONNECTION_LABEL = "ZooKeeper Connection";
    protected static final String CONTROL_NAME_PARENT_IMAGE_LABEL = "Parent Znode Image";
    protected static final String CONTROL_NAME_PARENT_PATH_LABEL = "Parent Znode";

    private final ZnodeModel _ParentZnodeModel;

    /**
     * TODO: Comment.
     * 
     * @param parent
     */
    public ZnodeNewWizardComposite(Composite parent, ZnodeModel parentZnodeModel) {
        super(parent);

        _ParentZnodeModel = parentZnodeModel;

        ParentZnodeModelEventListener delegate = new ParentZnodeModelEventListener();

        final IGenericDataModelEventListener parentZnodeModelEventListener = (IGenericDataModelEventListener) SwtThreadSafeDelegatingInvocationHandler
                .createProxyInstance(delegate, IGenericDataModelEventListener.class, true);

        _ParentZnodeModel.addGenericEventListener(parentZnodeModelEventListener);
        addDisposeListener(new DisposeListener() {

            @Override
            public void widgetDisposed(DisposeEvent e) {
                _ParentZnodeModel.removeGenericEventListener(parentZnodeModelEventListener);
            }
        });

        setNumColumns(3);
    }

    /**
     * Returns the parentZnodeModel.
     * 
     * @return The parentZnodeModel
     */
    public ZnodeModel getParentZnodeModel() {
        return _ParentZnodeModel;
    }

    @Override
    protected void createContents() {

        ZnodeModel parentZnodeModel = getParentZnodeModel();

        Label connectionLabel = new Label(this, SWT.LEAD);
        connectionLabel.setText("Connection:");
        connectionLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));

        Label connectionImageLabel = new Label(this, SWT.LEAD);
        connectionImageLabel.setImage(ZooKeeperActivator
                .getManagedImage(ZooKeeperActivator.IMAGE_KEY_OBJECT_ZOO_KEEPER_CONNECTION));
        connectionImageLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));

        Label connectionValueLabel = new Label(this, SWT.LEAD);
        connectionValueLabel.setText(parentZnodeModel.getOwnerModel().getKey().getName());
        connectionValueLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        addControl(CONTROL_NAME_CONNECTION_LABEL, connectionValueLabel);
        addControlDecoration(CONTROL_NAME_CONNECTION_LABEL, connectionValueLabel);

        Label parentLabel = new Label(this, SWT.LEAD);
        parentLabel.setText("Parent:");
        parentLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));

        Label parentImageLabel = new Label(this, SWT.LEAD);
        parentImageLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        addControl(CONTROL_NAME_PARENT_IMAGE_LABEL, parentImageLabel);
        updateParentZnodeImage();

        Label parentValueLabel = new Label(this, SWT.LEAD);
        parentValueLabel.setText(parentZnodeModel.getData().getPath());
        parentValueLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        addControl(CONTROL_NAME_PARENT_PATH_LABEL, parentValueLabel);
        addControlDecoration(CONTROL_NAME_PARENT_PATH_LABEL, parentValueLabel);

    }

    @Override
    protected GridCompositeStatus updateStatus(Object source) {

        GridCompositeStatus status = super.updateStatus(source);
        if (status.getType().isError()) {
            return status;
        }

        String message;

        if (source instanceof Label) {
            ZnodeModel parentZnodeModel = getParentZnodeModel();

            Label parentValueLabel = (Label) getControl(CONTROL_NAME_PARENT_PATH_LABEL);
            if (parentValueLabel == source && parentZnodeModel.isDestroyed()) {
                Znode parentZnode = parentZnodeModel.getData();
                String parentZnodePath = parentZnode.getPath();
                message = "Parent Znode '" + parentZnodePath + "' not available.";
                return new GridCompositeStatus(CONTROL_NAME_PARENT_PATH_LABEL, message,
                        GridCompositeStatus.Type.ERROR_INVALID);

            }
        }

        return GridCompositeStatus.OK_STATUS;
    }

    private void updateParentZnodeImage() {
        ZnodeModel parentZnodeModel = getParentZnodeModel();
        Label parentImageLabel = (Label) getControl(CONTROL_NAME_PARENT_IMAGE_LABEL);
        if (parentImageLabel != null && !parentImageLabel.isDisposed()) {
            parentImageLabel.setImage(ZooKeeperActivator.getZnodeSmallImage(parentZnodeModel.getData()));
        }
    }

    public class ParentZnodeModelEventListener implements IGenericDataModelEventListener, IWidgetProvider {

        @Override
        public void dataModelDataChanged(GenericDataModelEvent event) {
            parentZnodeModelChanged();
        }

        @Override
        public void dataModelDataRefreshed(GenericDataModelEvent event) {
            parentZnodeModelChanged();
        }

        @Override
        public void dataModelDestroyed(GenericDataModelEvent event) {
            parentZnodeModelChanged();
        }

        @Override
        public Widget getWidget() {
            return ZnodeNewWizardComposite.this;
        }

        private void parentZnodeModelChanged() {
            if (!getWidget().isDisposed()) {

                if (!_ParentZnodeModel.isDestroyed()) {
                    updateParentZnodeImage();
                }

                modified(null);
            }
        }

    }

}
