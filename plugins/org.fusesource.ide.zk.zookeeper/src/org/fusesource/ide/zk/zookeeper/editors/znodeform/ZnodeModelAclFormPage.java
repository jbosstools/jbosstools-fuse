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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.fusesource.ide.zk.zookeeper.ZooKeeperActivator;
import org.fusesource.ide.zk.zookeeper.data.Znode;
import org.fusesource.ide.zk.zookeeper.model.ZnodeModel;
import org.fusesource.ide.zk.zookeeper.widgets.ZnodeAclComposite;
import org.fusesource.ide.zk.zookeeper.widgets.OrchestrationComposite.IOrchestrationCompositeListener;

import java.util.EventObject;
import java.util.List;

import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public class ZnodeModelAclFormPage extends BaseZnodeModelTableFormPage {

    public static final String ID = ZnodeModelAclFormPage.class.getName();
    public static final Image IMAGE = ZooKeeperActivator.getManagedImage(ZooKeeperActivator.IMAGE_KEY_OBJECT_ZNODE_ACL);
    public static final String TITLE = "ACL";

    private ZnodeAclComposite _ZnodeAclComposite;

    public ZnodeModelAclFormPage(ZnodeModelFormEditor editor) {
        super(editor, ID, TITLE, IMAGE);
    }

    public List<ACL> getZnodeAclFromEditor() {
        return _ZnodeAclComposite.getZnodeAclFromTable();
    }

    @Override
    protected Table createTable(IManagedForm managedForm, Composite client) {

        FormToolkit toolkit = managedForm.getToolkit();

        _ZnodeAclComposite = new ZnodeAclComposite(client, SWT.NONE);
        toolkit.adapt(_ZnodeAclComposite);

        FormLayout compositeLayout = new FormLayout();
        compositeLayout.marginHeight = 0;
        compositeLayout.marginWidth = 0;
        compositeLayout.spacing = 8;
        _ZnodeAclComposite.setLayout(compositeLayout);
        _ZnodeAclComposite.setLayoutData(client.getLayoutData());

        _ZnodeAclComposite.addOrchestrationCompositeListener(new IOrchestrationCompositeListener() {

            @Override
            public void orchestrationChange(EventObject e) {
                setDirtyInternal(true);
            }
        });

        final Table table = super.createTable(managedForm, _ZnodeAclComposite);
        _ZnodeAclComposite.setTable(table);

        Button addButton = toolkit.createButton(_ZnodeAclComposite, "&Add", SWT.PUSH);
        _ZnodeAclComposite.setAddButton(addButton);

        Button removeButton = toolkit.createButton(_ZnodeAclComposite, "&Remove", SWT.PUSH);
        _ZnodeAclComposite.setRemoveButton(removeButton);

        Button setIdButton = toolkit.createButton(_ZnodeAclComposite, "&Set Id...", SWT.PUSH);
        _ZnodeAclComposite.setSetIdButton(setIdButton);

        FormData addButtonFormData = new FormData();
        addButtonFormData.top = new FormAttachment(0, 0);
        addButtonFormData.left = new FormAttachment(removeButton, 0, SWT.LEFT);
        addButtonFormData.right = new FormAttachment(100, 0);
        addButton.setLayoutData(addButtonFormData);

        FormData removeButtonFormData = new FormData();
        removeButtonFormData.top = new FormAttachment(addButton, 0);
        removeButtonFormData.right = new FormAttachment(100, 0);
        removeButton.setLayoutData(removeButtonFormData);

        FormData setIdButtonFormData = new FormData();
        setIdButtonFormData.top = new FormAttachment(removeButton, 0);
        setIdButtonFormData.right = new FormAttachment(100, 0);
        setIdButton.setLayoutData(setIdButtonFormData);

        _ZnodeAclComposite.init();

        SelectionListener yesListener = new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {

                initFromModel();
                setInfoText(null);
            }

        };
        initYesNoInfoBar(managedForm, yesListener);

        return table;
    }

    @Override
    protected void forceLayout() {
        super.forceLayout();
        if (_ZnodeAclComposite != null) {
            _ZnodeAclComposite.layout(true);
        }
    }

    @Override
    protected int[] getTableColumnAlignments() {
        return null;
    }

    @Override
    protected String[] getTableColumnTitles() {
        return ZnodeAclComposite.TABLE_COLUMN_TITLES;
    }

    @Override
    protected int[] getTableColumnWidths() {
        return ZnodeAclComposite.TABLE_COLUMN_WIDTHS;
    }

    @Override
    protected FormData getTableFormData() {
        FormData formData = new FormData();
        formData.top = new FormAttachment(0, 0);
        formData.left = new FormAttachment(0, 0);
        formData.bottom = new FormAttachment(100, 0);
        formData.right = new FormAttachment(_ZnodeAclComposite.getAddButton(), 0, SWT.LEFT);
        return formData;
    }

    @Override
    protected int getTableStyle() {
        return SWT.SINGLE | SWT.FULL_SELECTION;
    }

    @Override
    protected void initTableItemsFromZnode() {
        _ZnodeAclComposite.initTableItemsFromZnode(getModel().getData());
        setToolbarLabelText(Znode.STAT_NAME_AVERSION + ": " + getModel().getData().getStat().getAversion(),
                Znode.STAT_DESCRIPTION_AVERSION);
    }

    @Override
    protected void modelModifiedExternally() {
        ZnodeModel znodeModel = getModel();

        if (znodeModel.isDestroyed()) {
            return;
        }

        ZnodeModelFormEditor editor = (ZnodeModelFormEditor) getEditor();
        Znode znode = znodeModel.getData();
        Stat stat = znode.getStat();

        if (!isDirty() || stat.getAversion() == editor.getLastModificationAversion()) {
            initFromModel();
        }
        else {
            editor.setActivePage(ID);
            setInfoText(EXTERNAL_MODIFICATION_INFO_TEXT);
        }

    }

    @Override
    protected void saveCompleted() {
        super.saveCompleted();
        setInfoText(null);
        initFromModel();
    }

}
