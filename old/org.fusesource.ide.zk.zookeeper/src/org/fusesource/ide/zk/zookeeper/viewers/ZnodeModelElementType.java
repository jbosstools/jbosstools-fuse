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

package org.fusesource.ide.zk.zookeeper.viewers;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.fusesource.ide.zk.zookeeper.ZooKeeperActivator;
import org.fusesource.ide.zk.zookeeper.actions.NewZnodeAction;
import org.fusesource.ide.zk.zookeeper.actions.TableEditAction;
import org.fusesource.ide.zk.zookeeper.actions.TableEditChildrenAction;
import org.fusesource.ide.zk.zookeeper.actions.ZooKeeperDeleteAction;
import org.fusesource.ide.zk.zookeeper.actions.ZooKeeperOpenAction;
import org.fusesource.ide.zk.zookeeper.data.Znode;
import org.fusesource.ide.zk.zookeeper.model.ZnodeModel;
import org.fusesource.ide.zk.core.actions.BaseAction;
import org.fusesource.ide.zk.core.actions.RefreshAction;
import org.fusesource.ide.zk.core.actions.BaseAction.InputType;
import org.fusesource.ide.zk.core.viewers.IElementType;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.zookeeper.data.Stat;

/**
 * {@link IElementType} for {@link ZnodeModel}.
 * 
 * @author Mark Masse
 */
public class ZnodeModelElementType extends AbstractZooKeeperDataModelElementType {



    public static final String COLUMN_TITLE_CONNECTION = "Connection";
    public static final String COLUMN_TITLE_DATA = "Data";
    public static final String COLUMN_TITLE_DATE_CREATED = "Date Created";
    public static final String COLUMN_TITLE_DATE_MODIFIED = "Date Modified";
    public static final String COLUMN_TITLE_PARENT = "Parent";
    public static final String COLUMN_TITLE_PATH = "Path";
    public static final String COLUMN_TITLE_SIZE = "Size";
    public static final String COLUMN_TITLE_VERSION = "Version";

    private static final int[] COLUMN_ALIGNMENTS_DEFAULT = new int[] { SWT.LEFT, SWT.LEFT, SWT.RIGHT, SWT.RIGHT,
            SWT.LEFT, SWT.LEFT };

    private static final int[] COLUMN_ALIGNMENTS_WITH_PARENT_CONTEXT = new int[] { SWT.LEFT, SWT.LEFT, SWT.RIGHT,
            SWT.RIGHT, SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.LEFT };

    private static final String[] COLUMN_TITLES_DEFAULT = new String[] { COLUMN_TITLE_PATH, COLUMN_TITLE_DATA,
            COLUMN_TITLE_VERSION, COLUMN_TITLE_SIZE, COLUMN_TITLE_DATE_MODIFIED, COLUMN_TITLE_DATE_CREATED };

    private static final String[] COLUMN_TITLES_WITH_PARENT_CONTEXT = new String[] { COLUMN_TITLE_PATH,
            COLUMN_TITLE_DATA, COLUMN_TITLE_VERSION, COLUMN_TITLE_SIZE, COLUMN_TITLE_DATE_MODIFIED,
            COLUMN_TITLE_DATE_CREATED, COLUMN_TITLE_PARENT, COLUMN_TITLE_CONNECTION };

    private static final int[] COLUMN_WIDTHS_DEFAULT = new int[] { SWT.DEFAULT, 150, SWT.DEFAULT, SWT.DEFAULT,
            SWT.DEFAULT, SWT.DEFAULT };
    private static final int[] COLUMN_WIDTHS_WITH_PARENT_CONTEXT = new int[] { SWT.DEFAULT, 150, SWT.DEFAULT,
            SWT.DEFAULT, SWT.DEFAULT, SWT.DEFAULT, SWT.DEFAULT, SWT.DEFAULT };

    private NewZnodeAction _NewZnodeAction;
    private final boolean _ParentContextColumnsDisplayed;
    private TableEditAction _TableEditAction;
    private TableEditChildrenAction _TableEditChildrenAction;

    public ZnodeModelElementType() {
        this(false);
    }

    public ZnodeModelElementType(boolean parentContextColumnsDisplayed) {
        _ParentContextColumnsDisplayed = parentContextColumnsDisplayed;
    }

    @Override
    public void fillContextMenu(IMenuManager manager) {

        NewZnodeAction newZnodeAction = getNewZnodeAction();
        if (newZnodeAction != null && newZnodeAction.isEnabled()) {
            manager.add(newZnodeAction);
        }

        ZooKeeperOpenAction openAction = getOpenAction();
        if (openAction != null && openAction.isEnabled()) {
            manager.add(new Separator());
            manager.add(openAction);
        }

        TableEditAction tableEditAction = getTableEditAction();
        if (tableEditAction != null && tableEditAction.isEnabled()) {
            manager.add(new Separator());
            manager.add(tableEditAction);
        }

        TableEditChildrenAction tableEditChildrenAction = getTableEditChildrenAction();
        if (tableEditChildrenAction != null && tableEditChildrenAction.isEnabled()) {
            manager.add(tableEditChildrenAction);
        }

        ZooKeeperDeleteAction deleteAction = getDeleteAction();
        if (deleteAction != null && deleteAction.isEnabled()) {
            manager.add(new Separator());
            manager.add(deleteAction);
        }

        RefreshAction refreshAction = getRefreshAction();
        if (refreshAction != null && refreshAction.isEnabled()) {
            manager.add(new Separator());
            manager.add(refreshAction);
        }

    }

    @Override
    public int getChildCount(Object parent) {
        ZnodeModel model = (ZnodeModel) parent;

        if (!model.getOwnerModel().isConnected()) {
            return 0;
        }

        List<String> children = model.getData().getChildren();
        if (children == null) {
            return 0;
        }
        return children.size();
    }

    @Override
    public Object getChildElement(Object parent, int index) {

        ZnodeModel model = (ZnodeModel) parent;

        if (!model.getOwnerModel().isConnected()) {
            return null;
        }

        List<ZnodeModel> childModels = model.getChildModels();
        if (childModels != null && index < childModels.size()) {
            return childModels.get(index);
        }

        return null;
    }

    @Override
    public Object[] getChildren(Object parent) {
        ZnodeModel model = (ZnodeModel) parent;

        List<ZnodeModel> childModels;
        if (!model.getOwnerModel().isConnected()) {
            childModels = Collections.emptyList();
        }
        else {
            childModels = model.getChildModels();
            if (childModels == null) {
                childModels = Collections.emptyList();
            }
        }
        return childModels.toArray();
    }

    public int[] getColumnAlignments() {
        if (isParentContextColumnsDisplayed()) {
            return COLUMN_ALIGNMENTS_WITH_PARENT_CONTEXT;
        }
        return COLUMN_ALIGNMENTS_DEFAULT;
    }

    @Override
    public Image getColumnImage(Object element, int columnIndex) {
        if (columnIndex == 0) {
            return super.getColumnImage(element, columnIndex);
        }
        else if (columnIndex == 6) {
            ZnodeModel model = (ZnodeModel) element;
            ZnodeModel parentModel = model.getParentModel();
            return getColumnImage(parentModel, 0);
        }
        else if (columnIndex == 7) {
            return ZooKeeperActivator.getManagedImage(ZooKeeperActivator.IMAGE_KEY_OBJECT_ZOO_KEEPER_CONNECTION);
        }

        return null;
    }

    @Override
    public String getColumnText(Object element, int columnIndex) {

        // 0:Path | 1:Data | 2:Version | 3:Size | 4:Date Modified | 5:Date Created | 6:Parent | 7: Connection

        ZnodeModel model = (ZnodeModel) element;
        Znode znode = model.getData();
        Stat stat = znode.getStat();

        switch (columnIndex) {

        case 0:
            return znode.getRelativePath();

        case 1:
            return znode.getDataAsString();

        case 2:
            return String.valueOf(stat.getVersion());

        case 3:
            int dataLength = stat.getDataLength();
            String size;
            if (dataLength > 1024) {
                size = String.valueOf(dataLength / 1024) + " KB";
            }
            else {
                size = String.valueOf(dataLength) + " bytes";
            }

            return size;

        case 4:
            return DEFAULT_DATE_FORMAT.format(new Date(stat.getMtime()));

        case 5:
            return DEFAULT_DATE_FORMAT.format(new Date(stat.getCtime()));

        case 6:
            return model.getData().getParentPath();

        case 7:
            return model.getOwnerModel().getData().getDescriptor().getName();

        }
        return null;
    }

    public String[] getColumnTitles() {
        if (isParentContextColumnsDisplayed()) {
            return COLUMN_TITLES_WITH_PARENT_CONTEXT;
        }
        return COLUMN_TITLES_DEFAULT;
    }

    public int[] getColumnWidths() {
        if (isParentContextColumnsDisplayed()) {
            return COLUMN_WIDTHS_WITH_PARENT_CONTEXT;
        }
        return COLUMN_WIDTHS_DEFAULT;
    }

    @Override
    public Image getImage(Object element) {
        ZnodeModel model = (ZnodeModel) element;
        if (model == null) {
            return null;
        }

        return ZooKeeperActivator.getZnodeSmallImage(model.getData());
    }

    @Override
    public Image getLargeImage(Object element) {
        ZnodeModel model = (ZnodeModel) element;
        if (model == null) {
            return null;
        }

        return ZooKeeperActivator.getZnodeLargeImage(model.getData());
    }

    /**
     * Returns the newZnodeAction.
     * 
     * @return The newZnodeAction
     */
    public final NewZnodeAction getNewZnodeAction() {
        return _NewZnodeAction;
    }

    @Override
    public Object getParent(Object element) {
        ZnodeModel model = (ZnodeModel) element;
        if (!model.getOwnerModel().isConnected() && !model.getKey().equals(Znode.ROOT_PATH)) {
            return null;
        }
        
        return super.getParent(element);
    }
    
    /**
     * Returns the tableEditAction.
     * 
     * @return The tableEditAction
     */
    public final TableEditAction getTableEditAction() {
        return _TableEditAction;
    }

    /**
     * Returns the tableEditChildrenAction.
     * 
     * @return The tableEditChildrenAction
     */
    public final TableEditChildrenAction getTableEditChildrenAction() {
        return _TableEditChildrenAction;
    }

    @Override
    public String getText(Object element) {
        ZnodeModel model = (ZnodeModel) element;
        return model.getData().getRelativePath();
    }

    @Override
    public String getToolTipText(Object element) {
        ZnodeModel model = (ZnodeModel) element;
        return model.getData().getPath();
    }

    /**
     * Returns <code>true</code> if the parent should be displayed in the table.
     * 
     * @return <code>true</code> if the parent should be displayed in the table.
     */
    public boolean isParentContextColumnsDisplayed() {
        return _ParentContextColumnsDisplayed;
    }

    @Override
    protected List<BaseAction> createActions() {

        List<BaseAction> tableActions = super.createActions();

        _NewZnodeAction = new NewZnodeAction(InputType.SINGLE_STRUCTURED_SELECTION);
        tableActions.add(_NewZnodeAction);

        _TableEditAction = new TableEditAction();
        tableActions.add(_TableEditAction);

        _TableEditChildrenAction = new TableEditChildrenAction();
        tableActions.add(_TableEditChildrenAction);

        return tableActions;
    }

}
