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

package org.fusesource.ide.zk.core.viewers;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.ILazyTreeContentProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IWorkbenchPartSite;
import org.fusesource.ide.zk.core.actions.BaseAction;
import org.fusesource.ide.zk.core.model.DataModel;
import org.fusesource.ide.zk.core.reflect.DelegatingInvocationHandler;

import java.lang.reflect.Proxy;
import java.util.List;


/**
 * Factory for {@link Viewer viewers}.
 * 
 * @see TableViewer
 * @see TreeViewer
 * @see ITreeContentProvider
 * @see IStructuredContentProvider
 * @see ILazyTreeContentProvider
 * @see ILazyContentProvider
 * @see DataModel
 * @see DelegatingInvocationHandler
 * 
 * @author Mark Masse
 */
public final class ViewerFactory {

    /**
     * Create a new {@link TableViewer} with {@link DataModel} elements.
     * 
     * @param parent The {@link Table table's} parent.
     * @param style The {@link Table table's} style.
     * @param elementTypes The {@link TableViewer} element types.
     * @param input The {@link TableViewer} input.
     * 
     * @return A new {@link TableViewer}.
     */
    public static TableViewer createDataModelTableViewer(Composite parent, int style, ElementTypes elementTypes,
            Object input) {

        IElementBinding elementBinding = new DataModelElementBinding();

        return createTableViewer(parent, style, elementTypes, input, elementBinding);
    }

    /**
     * Create a new {@link TableViewer} with {@link DataModel} elements.
     * 
     * @param site The {@link IWorkbenchPartSite} used to register the table's context menu.
     * @param table The {@link Table}.
     * @param dataModelClass The {@link DataModel} element {@link Class}.
     * @param dataModelElementType The {@link DataModel} {@link IElementType}.
     * @param input The {@link TableViewer} input.
     * @param inputElementType The input {@link IElementType}.
     * @return A new {@link TableViewer}.
     */
    public static TableViewer createDataModelTableViewer(IWorkbenchPartSite site, Table table, Class<?> dataModelClass,
            final IElementType dataModelElementType, Object input, IElementType inputElementType) {

        TableViewer tableViewer = createTableViewer(table, dataModelClass, dataModelElementType, input,
                inputElementType, new DataModelElementBinding());

        List<BaseAction> tableActions = dataModelElementType.getActions();
        if (tableActions != null) {
            for (BaseAction action : tableActions) {
                action.setSelectionProvider(tableViewer);
            }

            MenuManager menuManager = new MenuManager("#PopupMenu");
            menuManager.setRemoveAllWhenShown(true);
            menuManager.addMenuListener(new IMenuListener() {

                public void menuAboutToShow(IMenuManager manager) {
                    dataModelElementType.fillContextMenu(manager);
                }

            });

            Menu menu = menuManager.createContextMenu(table);
            table.setMenu(menu);
            site.registerContextMenu(menuManager, tableViewer);
        }

        return tableViewer;
    }

    /**
     * Create a new {@link TableViewer} with {@link DataModel} elements.
     * 
     * @param table The {@link Table}.
     * @param elementTypes The {@link TableViewer} element types.
     * @param input The {@link TableViewer} input.
     * @return A new {@link TableViewer}.
     */
    public static TableViewer createDataModelTableViewer(Table table, ElementTypes elementTypes, Object input) {

        IElementBinding elementBinding = new DataModelElementBinding();

        return createTableViewer(table, elementTypes, input, elementBinding);
    }

    /**
     * Create a new {@link TableViewer} with {@link DataModel} elements.
     * 
     * @param parent The {@link Table table's} parent.
     * @param style The {@link Table table's} style.
     * @param elementTypes The {@link TableViewer} element types.
     * @param input The {@link TableViewer} input.
     * @return A new {@link TableViewer}.
     */
    public static TreeViewer createDataModelTreeViewer(Composite parent, int style, ElementTypes elementTypes,
            Object input) {

        IElementBinding elementBinding = new DataModelElementBinding();

        return createTreeViewer(parent, style, elementTypes, input, elementBinding);
    }

    /**
     * Create a new {@link TableViewer}.
     * 
     * @param parent The {@link Table table's} parent.
     * @param style The {@link Table table's} style.
     * @param elementTypes The {@link TableViewer} element types.
     * @param input The {@link TableViewer} input.
     * @return A new {@link TableViewer}.
     */
    public static TableViewer createTableViewer(Composite parent, int style, ElementTypes elementTypes, Object input) {
        return createTableViewer(parent, style, elementTypes, input, null);
    }

    /**
     * Create a new {@link TableViewer}.
     * 
     * @param parent The {@link Table table's} parent.
     * @param style The {@link Table table's} style.
     * @param elementTypes The {@link TableViewer} element types.
     * @param input The {@link TableViewer} input.
     * @param elementBinding The {@link IElementBinding}.
     * @return A new {@link TableViewer}.
     */
    public static TableViewer createTableViewer(Composite parent, int style, ElementTypes elementTypes, Object input,
            IElementBinding elementBinding) {

        TableViewer tableViewer = new TableViewer(parent, style);
        initTableViewer(tableViewer, elementTypes, input, elementBinding);
        return tableViewer;
    }

    /**
     * Create a new {@link TableViewer}.
     * 
     * @param table The {@link Table}.
     * @param elementClass The element {@link Class}.
     * @param elementType The {@link IElementType}.
     * @param input The {@link TableViewer} input.
     * @param inputElementType The input {@link IElementType}.
     * @param elementBinding The {@link IElementBinding}.
     * @return A new {@link TableViewer}.
     */
    public static TableViewer createTableViewer(Table table, Class<?> elementClass, final IElementType elementType,
            Object input, IElementType inputElementType, IElementBinding elementBinding) {

        ElementTypes elementTypes = createTableElementTypes(elementClass, elementType, input, inputElementType);

        createTableColumns(table, elementType);

        TableViewer tableViewer = createTableViewer(table, elementTypes, input, elementBinding);

        elementType.packTable(table);

        return tableViewer;
    }

    /**
     * Create a new {@link TableViewer}.
     * 
     * @param table The {@link Table}.
     * @param elementTypes The {@link TableViewer} element types.
     * @param input The {@link TableViewer} input.
     * @param elementBinding The {@link IElementBinding}.
     * @return A new {@link TableViewer}.
     */
    public static TableViewer createTableViewer(Table table, ElementTypes elementTypes, Object input,
            IElementBinding elementBinding) {

        TableViewer tableViewer = new TableViewer(table);
        initTableViewer(tableViewer, elementTypes, input, elementBinding);
        return tableViewer;
    }

    /**
     * Create a new {@link TreeViewer}.
     * 
     * @param parent The {@link Tree tree's} parent.
     * @param style The {@link Tree tree's} style.
     * @param elementTypes The {@link TreeViewer} element types.
     * @param input The {@link TreeViewer} input.
     * @param elementBinding The {@link IElementBinding}.
     * @return A new {@link TreeViewer}.
     */
    public static TreeViewer createTreeViewer(Composite parent, int style, ElementTypes elementTypes, Object input,
            IElementBinding elementBinding) {

        TreeViewer treeViewer = new TreeViewer(parent, style);
        initTreeViewer(treeViewer, elementTypes, input, elementBinding);
        return treeViewer;
    }

    /**
     * Create a new {@link TreeViewer}.
     * 
     * @param tree The {@link Tree}.
     * @param elementTypes The {@link TreeViewer} element types.
     * @param input The {@link TreeViewer} input.
     * @param elementBinding The {@link IElementBinding}.
     * @return A new {@link TreeViewer}.
     */
    public static TreeViewer createTreeViewer(Tree tree, ElementTypes elementTypes, Object input,
            IElementBinding elementBinding) {

        TreeViewer treeViewer = new TreeViewer(tree);

        initTreeViewer(treeViewer, elementTypes, input, elementBinding);

        return treeViewer;
    }

    private static void createTableColumns(Table table, IElementType tableElementType) {

        String[] columnTitles = tableElementType.getColumnTitles();
        int[] columnAlignments = tableElementType.getColumnAlignments();

        for (int i = 0; i < columnTitles.length; i++) {
            TableColumn column = new TableColumn(table, SWT.NONE);
            column.setText(columnTitles[i]);
            column.setAlignment(columnAlignments[i]);
            column.setMoveable(true);
        }

        table.setHeaderVisible(true);
    }

    private static ElementTypes createTableElementTypes(Class<?> dataModelClass, IElementType dataModelElementType,
            Object input, IElementType inputElementType) {

        ElementTypes elementTypes = new ElementTypes();
        elementTypes.add(input.getClass(), inputElementType);
        elementTypes.add(dataModelClass, dataModelElementType);
        return elementTypes;
    }

    private static void initTableViewer(TableViewer tableViewer, ElementTypes elementTypes, Object input,
            IElementBinding elementBinding) {

        IViewerType viewerType = new TableViewerType();

        Table table = tableViewer.getTable();
        Class<?> contentProviderInterfaceType = IStructuredContentProvider.class;
        if ((table.getStyle() & SWT.VIRTUAL) != 0) {
            contentProviderInterfaceType = ILazyContentProvider.class;
        }

        registerTableListeners(table);

        initViewer(tableViewer, elementTypes, input, elementBinding, viewerType, contentProviderInterfaceType);
    }

    private static void initTreeViewer(TreeViewer treeViewer, ElementTypes elementTypes, Object input,
            IElementBinding elementBinding) {

        IViewerType viewerType = new TreeViewerType();

        Class<?> contentProviderInterfaceType = ITreeContentProvider.class;
        if ((treeViewer.getTree().getStyle() & SWT.VIRTUAL) != 0) {
            contentProviderInterfaceType = ILazyTreeContentProvider.class;
        }

        initViewer(treeViewer, elementTypes, input, elementBinding, viewerType, contentProviderInterfaceType);

    }

    private static void initViewer(StructuredViewer viewer, ElementTypes elementTypes, Object input,
            IElementBinding elementBinding, IViewerType viewerType, Class<?> contentProviderInterfaceType) {

        PluggableContentProvider pluggableContentProvider = new PluggableContentProvider(viewerType, elementTypes,
                elementBinding);

        ElementTypesLabelProvider labelProvider = new ElementTypesLabelProvider(elementTypes);

        DelegatingInvocationHandler invocationHandler = new DelegatingInvocationHandler(pluggableContentProvider);

        IContentProvider contentProvider = (IContentProvider) Proxy.newProxyInstance(contentProviderInterfaceType
                .getClassLoader(), new Class[] { contentProviderInterfaceType }, invocationHandler);

        viewer.setContentProvider(contentProvider);
        viewer.setLabelProvider(labelProvider);
        viewer.setUseHashlookup(true);
        viewer.setInput(input);
    }

    private static void registerTableListeners(final Table table) {

        final MouseListener mouseListener = new MouseAdapter() {

            @Override
            public void mouseDown(MouseEvent e) {
                if (table.getItem(new Point(e.x, e.y)) == null) {
                    table.deselectAll();
                }
            }

        };

        table.addMouseListener(mouseListener);

        table.addDisposeListener(new DisposeListener() {

            @Override
            public void widgetDisposed(DisposeEvent e) {
                table.removeDisposeListener(this);
                table.removeMouseListener(mouseListener);

            }
        });

    }

}
