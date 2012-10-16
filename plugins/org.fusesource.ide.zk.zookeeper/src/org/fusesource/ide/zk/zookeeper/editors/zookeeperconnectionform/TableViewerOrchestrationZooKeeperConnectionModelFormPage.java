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

package org.fusesource.ide.zk.zookeeper.editors.zookeeperconnectionform;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.fusesource.ide.zk.zookeeper.model.ZooKeeperConnectionModel;
import org.fusesource.ide.zk.zookeeper.widgets.TableViewerOrchestrationComposite;
import org.fusesource.ide.zk.zookeeper.widgets.OrchestrationComposite.IOrchestrationCompositeListener;
import org.fusesource.ide.zk.core.editors.DataModelFormPage;

import java.util.Collection;
import java.util.EventObject;
import java.util.List;
import java.util.Set;


public abstract class TableViewerOrchestrationZooKeeperConnectionModelFormPage<E> extends
        DataModelFormPage<ZooKeeperConnectionModel> {

    private TableViewerOrchestrationComposite<E> _TableViewerOrchestrationComposite;

    public TableViewerOrchestrationZooKeeperConnectionModelFormPage(ZooKeeperConnectionModelFormEditor editor,
            String id, String title) {
        super(editor, id, title);
    }

    @Override
    protected void createModelFormContent(IManagedForm managedForm, Composite client) {

        FormToolkit toolkit = managedForm.getToolkit();

        _TableViewerOrchestrationComposite = createTableViewerOrchestrationComposite(client);
        toolkit.adapt(_TableViewerOrchestrationComposite);

        FormLayout compositeLayout = new FormLayout();
        compositeLayout.marginHeight = 0;
        compositeLayout.marginWidth = 0;
        compositeLayout.spacing = 8;
        _TableViewerOrchestrationComposite.setLayout(compositeLayout);
        _TableViewerOrchestrationComposite.setLayoutData(client.getLayoutData());

        Table table = toolkit.createTable(_TableViewerOrchestrationComposite, SWT.SINGLE | SWT.FULL_SELECTION
                | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        _TableViewerOrchestrationComposite.setTable(table);

        Button addButton = toolkit.createButton(_TableViewerOrchestrationComposite, "&Add", SWT.PUSH);
        _TableViewerOrchestrationComposite.setAddButton(addButton);

        Button removeButton = toolkit.createButton(_TableViewerOrchestrationComposite, "&Remove", SWT.PUSH);
        _TableViewerOrchestrationComposite.setRemoveButton(removeButton);

        FormData tableFormData = new FormData();
        tableFormData.top = new FormAttachment(0, 0);
        tableFormData.left = new FormAttachment(0, 0);
        tableFormData.bottom = new FormAttachment(100, 0);
        tableFormData.right = new FormAttachment(addButton, 0, SWT.LEFT);
        table.setLayoutData(tableFormData);

        FormData addButtonFormData = new FormData();
        addButtonFormData.top = new FormAttachment(0, 0);
        addButtonFormData.left = new FormAttachment(removeButton, 0, SWT.LEFT);
        addButtonFormData.right = new FormAttachment(100, 0);
        addButton.setLayoutData(addButtonFormData);

        FormData removeButtonFormData = new FormData();
        removeButtonFormData.top = new FormAttachment(addButton, 0);
        removeButtonFormData.right = new FormAttachment(100, 0);
        removeButton.setLayoutData(removeButtonFormData);

        _TableViewerOrchestrationComposite.addOrchestrationCompositeListener(new IOrchestrationCompositeListener() {

            @Override
            public void orchestrationChange(EventObject e) {
                setDirtyInternal(true);
            }
        });

        _TableViewerOrchestrationComposite.init();

    }

    protected abstract TableViewerOrchestrationComposite<E> createTableViewerOrchestrationComposite(Composite client);

    protected final Set<E> getElementSet() {
        return _TableViewerOrchestrationComposite.getElementSet();
    }

    protected final List<E> getElementList() {
        return _TableViewerOrchestrationComposite.getElementList();
    }

    protected abstract Collection<E> getElementsFromModel();

    protected final TableViewerOrchestrationComposite<E> getTableViewerOrchestrationComposite() {
        return _TableViewerOrchestrationComposite;
    }

    @Override
    protected final void initFromModelInternal() {
        if (_TableViewerOrchestrationComposite != null) {
            _TableViewerOrchestrationComposite.setElements(getElementsFromModel());
        }
    }

}
