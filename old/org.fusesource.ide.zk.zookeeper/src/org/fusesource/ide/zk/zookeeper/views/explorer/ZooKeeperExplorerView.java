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

package org.fusesource.ide.zk.zookeeper.views.explorer;

import java.util.Iterator;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.fusesource.ide.commons.ui.Selections;
import org.fusesource.ide.commons.ui.actions.ConnectDisconnectAction;
import org.fusesource.ide.zk.zookeeper.ZooKeeperActivator;
import org.fusesource.ide.zk.zookeeper.actions.NewZnodeAction;
import org.fusesource.ide.zk.zookeeper.actions.NewZooKeeperConnectionAction;
import org.fusesource.ide.zk.zookeeper.actions.TableEditAction;
import org.fusesource.ide.zk.zookeeper.actions.TableEditChildrenAction;
import org.fusesource.ide.zk.zookeeper.actions.ZooKeeperDeleteAction;
import org.fusesource.ide.zk.zookeeper.actions.ZooKeeperOpenAction;
import org.fusesource.ide.zk.zookeeper.data.ZooKeeperConnection;
import org.fusesource.ide.zk.zookeeper.data.ZooKeeperConnectionDescriptor;
import org.fusesource.ide.zk.zookeeper.model.ZnodeModel;
import org.fusesource.ide.zk.zookeeper.model.ZooKeeperConnectionModel;
import org.fusesource.ide.zk.zookeeper.model.ZooKeeperServerModel;
import org.fusesource.ide.zk.zookeeper.model.ZooKeeperServersModelCategory;
import org.fusesource.ide.zk.zookeeper.viewers.ZnodeModelElementType;
import org.fusesource.ide.zk.zookeeper.viewers.ZooKeeperConnectionModelElementType;
import org.fusesource.ide.zk.zookeeper.viewers.ZooKeeperServerModelElementType;
import org.fusesource.ide.zk.zookeeper.viewers.ZooKeeperServersModelCategoryElementType;
import org.fusesource.ide.zk.core.actions.RefreshAction;
import org.fusesource.ide.zk.core.actions.BaseAction.InputType;
import org.fusesource.ide.zk.core.model.DataModelManager;
import org.fusesource.ide.zk.core.viewers.DataModelManagerElementType;
import org.fusesource.ide.zk.core.viewers.ElementTypes;
import org.fusesource.ide.zk.core.viewers.ViewerFactory;
import org.fusesource.ide.zk.jmx.model.DomainModel;
import org.fusesource.ide.zk.jmx.model.JmxConnectionModel;
import org.fusesource.ide.zk.jmx.model.MBeanAttributeModel;
import org.fusesource.ide.zk.jmx.model.MBeanAttributesModelCategory;
import org.fusesource.ide.zk.jmx.model.MBeanModel;
import org.fusesource.ide.zk.jmx.model.MBeanOperationModel;
import org.fusesource.ide.zk.jmx.model.MBeanOperationsModelCategory;
import org.fusesource.ide.zk.jmx.model.ObjectNameKeyValueModel;
import org.fusesource.ide.zk.jmx.viewers.DomainModelElementType;
import org.fusesource.ide.zk.jmx.viewers.JmxConnectionModelElementType;
import org.fusesource.ide.zk.jmx.viewers.MBeanAttributeModelElementType;
import org.fusesource.ide.zk.jmx.viewers.MBeanAttributesModelCategoryElementType;
import org.fusesource.ide.zk.jmx.viewers.MBeanModelElementType;
import org.fusesource.ide.zk.jmx.viewers.MBeanOperationModelElementType;
import org.fusesource.ide.zk.jmx.viewers.MBeanOperationsModelCategoryElementType;
import org.fusesource.ide.zk.jmx.viewers.ObjectNameKeyValueModelElementType;


/**
 * The ZooKeeper Explorer view has a {@link TreeViewer} to display all of the {@link ZooKeeperConnectionModel ZooKeeper connections}.
 * 
 * @author Mark Masse
 */
public class ZooKeeperExplorerView extends ViewPart {

	public static final String ID = ZooKeeperExplorerView.class.getName() + ZooKeeperActivator.VERSION_SUFFIX;

	private ZooKeeperDeleteAction _DeleteAction;
	private NewZnodeAction _NewZnodeAction;
	private NewZooKeeperConnectionAction _NewZooKeeperConnectionAction;
	private ZooKeeperOpenAction _OpenAction;
	private RefreshAction _RefreshAction;
	private TableEditAction _TableEditAction;
	private TableEditChildrenAction _TableEditChildrenAction;
	private ConnectDisconnectAction connectAction;

	private TreeViewer _TreeViewer;

	// private Action _PropertiesAction;
	// private IWorkbenchAction _PropertiesWorkbenchAction;
	// private DrillDownAdapter _DrillDownAdapter;

	public ZooKeeperExplorerView() {
	}

	@Override
	public void init(IViewSite site) throws PartInitException {
		super.init(site);

		ZooKeeperActivator plugin = ZooKeeperActivator.getDefault();
		plugin.getZooKeeperConnectionModelManager().getModels();
	}

	@Override
	public void createPartControl(Composite parent) {

		DataModelManager<ZooKeeperConnectionModel, ZooKeeperConnectionDescriptor, ZooKeeperConnection> zooKeeperConnectionModelManager = ZooKeeperActivator
				.getDefault().getZooKeeperConnectionModelManager();

		ElementTypes elementTypes = new ElementTypes();

		elementTypes.add(zooKeeperConnectionModelManager.getClass(), new DataModelManagerElementType());
		elementTypes.add(ZooKeeperConnectionModel.class, new ZooKeeperConnectionModelElementType());
		elementTypes.add(ZooKeeperServersModelCategory.class, new ZooKeeperServersModelCategoryElementType());
		elementTypes.add(ZooKeeperServerModel.class, new ZooKeeperServerModelElementType());
		elementTypes.add(ZnodeModel.class, new ZnodeModelElementType());
		elementTypes.add(JmxConnectionModel.class, new JmxConnectionModelElementType());
		elementTypes.add(DomainModel.class, new DomainModelElementType());
		elementTypes.add(ObjectNameKeyValueModel.class, new ObjectNameKeyValueModelElementType());
		elementTypes.add(MBeanModel.class, new MBeanModelElementType());
		elementTypes.add(MBeanAttributesModelCategory.class, new MBeanAttributesModelCategoryElementType());
		elementTypes.add(MBeanAttributeModel.class, new MBeanAttributeModelElementType());
		elementTypes.add(MBeanOperationsModelCategory.class, new MBeanOperationsModelCategoryElementType());
		elementTypes.add(MBeanOperationModel.class, new MBeanOperationModelElementType());

		_TreeViewer = ViewerFactory.createDataModelTreeViewer(parent, SWT.MULTI | SWT.VIRTUAL | SWT.H_SCROLL
				| SWT.V_SCROLL, elementTypes, zooKeeperConnectionModelManager);

		_TreeViewer.getTree().addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(MouseEvent e) {
				getSite().getWorkbenchWindow().getActivePage().activate(ZooKeeperExplorerView.this);

			}
		});

		getSite().setSelectionProvider(_TreeViewer);

		// _DrillDownAdapter = new DrillDownAdapter(_TreeViewer);

		makeActions();
		hookContextMenu();
		contributeToActionBars();

	}

	@Override
	public void setFocus() {
		_TreeViewer.getControl().setFocus();
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillContextMenu(IMenuManager manager) {

		Object firstSelection = Selections.getFirstSelection(_TreeViewer);

		IStructuredSelection structuredSelection = (IStructuredSelection) _TreeViewer.getSelection();

		boolean emptySelection = false;
		boolean znodeSelected = false;
		if (structuredSelection != null && !structuredSelection.isEmpty()) {

			Iterator<?> selectionIterator = structuredSelection.iterator();
			while (selectionIterator.hasNext()) {
				Object selectedObject = selectionIterator.next();
				if (selectedObject instanceof ZnodeModel) {
					znodeSelected = true;
					break;
				}
			}
		}
		else {
			emptySelection = true;
		}
		if (connectAction.isEnabled()) {
			connectAction.refresh();
			manager.add(connectAction);
		}

		if (firstSelection == null) {
			manager.add(_NewZooKeeperConnectionAction);
		}

		if (emptySelection) {
			return;
		}

		if (znodeSelected) {
			manager.add(_NewZnodeAction);
		}

		manager.add(new Separator());
		manager.add(_OpenAction);
		manager.add(new Separator());

		if (znodeSelected) {
			manager.add(_TableEditAction);
			manager.add(_TableEditChildrenAction);
			manager.add(new Separator());
		}

		if (_DeleteAction.isEnabled()) {
			manager.add(_DeleteAction);
			manager.add(new Separator());
		}

		manager.add(_RefreshAction);
		manager.add(new Separator());

		// manager.add(_PropertiesWorkbenchAction);
		// manager.add(new Separator());

		// _DrillDownAdapter.addNavigationActions(manager);

		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(_NewZooKeeperConnectionAction);
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(_NewZooKeeperConnectionAction);
		manager.add(_NewZnodeAction);
		manager.add(new Separator());
		manager.add(_TableEditAction);

		// manager.add(new Separator());
		// _DrillDownAdapter.addNavigationActions(manager);
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {

			@Override
			public void menuAboutToShow(IMenuManager manager) {
				fillContextMenu(manager);
			}
		});

		Menu menu = menuMgr.createContextMenu(_TreeViewer.getControl());
		_TreeViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, _TreeViewer);
	}

	private void makeActions() {
		connectAction = new ConnectDisconnectAction(_TreeViewer);

		_DeleteAction = new ZooKeeperDeleteAction();
		_DeleteAction.setSelectionProvider(_TreeViewer);
		//_DeleteAction.hookGlobalAction(getViewSite().getActionBars());

		_TableEditChildrenAction = new TableEditChildrenAction();
		_TableEditChildrenAction.setSelectionProvider(_TreeViewer);

		_TableEditAction = new TableEditAction();
		_TableEditAction.setSelectionProvider(_TreeViewer);

		_NewZnodeAction = new NewZnodeAction(InputType.SINGLE_STRUCTURED_SELECTION);
		_NewZnodeAction.setSelectionProvider(_TreeViewer);

		_NewZooKeeperConnectionAction = new NewZooKeeperConnectionAction();
		_NewZooKeeperConnectionAction.setSelectionProvider(_TreeViewer);

		_OpenAction = new ZooKeeperOpenAction();
		_OpenAction.setSelectionProvider(_TreeViewer);

		_RefreshAction = new RefreshAction(InputType.STRUCTURED_SELECTION);
		_RefreshAction.setSelectionProvider(_TreeViewer);

		// IWorkbenchWindow window = getSite().getWorkbenchWindow();
		// _DeleteWorkbenchAction = ActionFactory.DELETE.create(window);

		// _PropertiesAction = new Action() {
		//
		// public void run() {
		// ISelection selection = _TreeViewer.getSelection();
		// Object obj = ((IStructuredSelection) selection).getFirstElement();
		// showMessage("Properties " + obj.toString());
		// }
		// };

		// _PropertiesWorkbenchAction = ActionFactory.PROPERTIES.create(window);
	}

}