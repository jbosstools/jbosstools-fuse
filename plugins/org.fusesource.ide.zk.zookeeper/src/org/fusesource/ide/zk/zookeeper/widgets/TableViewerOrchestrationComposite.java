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

package org.fusesource.ide.zk.zookeeper.widgets;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.fusesource.ide.zk.core.viewers.CollectionElementType;
import org.fusesource.ide.zk.core.viewers.IElementType;
import org.fusesource.ide.zk.core.viewers.ViewerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


public abstract class TableViewerOrchestrationComposite<E> extends TableOrchestrationComposite {

    private final Set<E> _Elements;
    private TableViewer _TableViewer;
    private final Class<?> _ElementClass;
    private final IElementType _ElementType;

    public TableViewerOrchestrationComposite(Composite parent, int style, Class<?> elementClass,
            IElementType elementType) {
        super(parent, style);

        _ElementClass = elementClass;
        _ElementType = elementType;
        _Elements = new LinkedHashSet<E>();
    }

    public final List<E> getElementList() {
        return new ArrayList<E>(_Elements);
    }

    public final Set<E> getElementSet() {
        return new LinkedHashSet<E>(_Elements);
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
    public void init() {

        final IElementType elementType = getElementType();
        final Table table = getTable();
        _TableViewer = ViewerFactory.createTableViewer(table, getElementClass(), elementType, _Elements,
                new CollectionElementType(), null);

        Button addButton = getAddButton();

        final Button removeButton = getRemoveButton();
        removeButton.setEnabled(false);

        addButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {

                E element = addElement();
                if (element != null) {
                    _Elements.add(element);
                    elementsChanged();
                }
            }

        });

        removeButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                IStructuredSelection selection = (IStructuredSelection) _TableViewer.getSelection();
                if (selection.isEmpty()) {
                    return;
                }

                _Elements.remove(selection.getFirstElement());
                elementsChanged();
            }

        });

        _TableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                IStructuredSelection selection = (IStructuredSelection) _TableViewer.getSelection();
                removeButton.setEnabled(!selection.isEmpty());
            }
        });

    }

    /**
     * Returns the elementClass.
     * 
     * @return The elementClass
     */
    public final Class<?> getElementClass() {
        return _ElementClass;
    }

    /**
     * Returns the elementType.
     * 
     * @return The elementType
     */
    public final IElementType getElementType() {
        return _ElementType;
    }

    public void setElements(Collection<E> elements) {
        _Elements.clear();
        _Elements.addAll(elements);
        elementsChanged();
    }

    protected abstract E addElement();

    private void elementsChanged() {

        TableViewer tableViewer = getTableViewer();
        tableViewer.refresh();

        IElementType elementType = getElementType();
        elementType.packTable(getTable());
        layout(true);

        fireOrchestrationChange();
    }

}
