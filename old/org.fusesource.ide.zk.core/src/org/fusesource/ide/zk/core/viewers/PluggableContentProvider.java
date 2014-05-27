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

import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILazyTreeContentProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.custom.BusyIndicator;
import org.fusesource.ide.zk.core.reflect.DelegatingInvocationHandler;


/**
 * Indirectly implements various {@link Viewer} ContentProvider interfaces. Note that this class is not intended for use
 * outside of the {@link ViewerFactory}. It has been made <code>public</code> so it can be accessed by a
 * {@link DelegatingInvocationHandler}.
 * 
 * @see ViewerFactory
 * @see IContentProvider
 * @see IStructuredContentProvider
 * @see ILazyTreeContentProvider
 * @see ITreeContentProvider
 * 
 * @author Mark Masse
 */
public class PluggableContentProvider {

    private final IElementBinding _ElementBinding;
    private final ElementTypes _ElementTypes;
    private Object _Input;
    private StructuredViewer _Viewer;
    private final IViewerType _ViewerType;

    /**
     * Constructor.
     * 
     * @param viewerType The {@link IViewerType}.
     * @param elementTypes The {@link ElementTypes}.
     * @param elementBinding The {@link IElementBinding}.
     */
    public PluggableContentProvider(IViewerType viewerType, ElementTypes elementTypes, IElementBinding elementBinding) {

        _ViewerType = viewerType;
        _ElementTypes = elementTypes;
        _ElementBinding = elementBinding;

        if (_ElementBinding != null) {
            _ElementBinding.setViewerType(_ViewerType);
        }
    }

    /**
     * Disposes this provider's {@link IElementBinding}.
     */
    public void dispose() {
        if (_ElementBinding != null) {
            _ElementBinding.unbindAll();
            _ElementBinding.dispose();
        }
    }

    /**
     * @see IElementType#getChildren(Object)
     */
    public Object[] getChildren(Object parentElement) {
        Object[] children = _ElementTypes.get(parentElement).getChildren(parentElement);
        bindElements(children);
        return children;
    }

    /**
     * Returns the {@link IElementBinding}.
     * 
     * @return The {@link IElementBinding}.
     */
    public IElementBinding getElementBinding() {
        return _ElementBinding;
    }

    /**
     * @see IElementType#getChildren(Object)
     */
    public Object[] getElements(Object inputElement) {
        if (inputElement == _Input) {
            Object[] elements = _ElementTypes.get(inputElement).getChildren(inputElement);
            bindElements(elements);
            return elements;
        }
        throw new IllegalArgumentException("Unexpected inputElement: " + inputElement);
    }

    /**
     * Returns the {@link ElementTypes}.
     * 
     * @return The {@link ElementTypes}.
     */
    public ElementTypes getElementTypes() {
        return _ElementTypes;
    }

    /**
     * Returns the {@link StructuredViewer} input.
     * 
     * @return The {@link StructuredViewer} input.
     */
    public Object getInput() {
        return _Input;
    }

    /**
     * @see IElementType#getParent(Object)
     */
    public Object getParent(Object element) {
        return _ElementTypes.get(element).getParent(element);
    }

    /**
     * Returns the {@link StructuredViewer}.
     * 
     * @return The {@link StructuredViewer}.
     */
    public StructuredViewer getViewer() {
        return _Viewer;
    }

    /**
     * Returns the {@link IViewerType}.
     * 
     * @return The {@link IViewerType}.
     */
    public IViewerType getViewerType() {
        return _ViewerType;
    }

    /**
     * @see #getChildCount(Object)
     */
    public boolean hasChildren(Object element) {
        return (getChildCount(element) > 0);
    }

    /**
     * @see IContentProvider#inputChanged(Viewer, Object, Object)
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        setViewer((StructuredViewer) viewer);
        setInput(newInput);
    }

    /**
     * @see #getChildCount(Object)
     * @see IViewerType#setChildCount(StructuredViewer, Object, int)
     */
    public void updateChildCount(Object element, int currentChildCount) {
        int childCount = getChildCount(element);
        if (childCount != currentChildCount) {
            _ViewerType.setChildCount(getViewer(), element, childCount);
        }
    }

    /**
     * @see IViewerType#updateElement(StructuredViewer, Object, int, Object)
     */
    public void updateElement(int index) {
        runWithBusyIndicator(new UpdateElementRunnable(_Input, index));
    }

    /**
     * @see IViewerType#updateElement(StructuredViewer, Object, int, Object)
     */
    public void updateElement(Object parent, int index) {
        runWithBusyIndicator(new UpdateElementRunnable(parent, index));
    }

    private void bindElement(Object element) {
        if (_ElementBinding == null) {
            return;
        }

        _ElementBinding.bind(element);
    }

    private void bindElements(Object[] elements) {
        if (_ElementBinding == null) {
            return;
        }

        if (elements == null) {
            return;
        }

        for (Object element : elements) {
            bindElement(element);
        }
    }

    private int getChildCount(Object parent) {
        return _ElementTypes.get(parent).getChildCount(parent);
    }

    private Object getChildElement(Object parent, int index) {
        Object element = _ElementTypes.get(parent).getChildElement(parent, index);
        bindElement(element);
        return element;
    }

    private void runWithBusyIndicator(Runnable runnable) {
        BusyIndicator.showWhile(_Viewer.getControl().getDisplay(), runnable);
    }

    private void setInput(Object input) {
        _Input = input;

        if (_Input != null && _ElementBinding != null) {
            _ElementBinding.bind(_Input);
        }

    }

    private void setViewer(StructuredViewer viewer) {
        _Viewer = viewer;

        if (_Viewer != null && _ElementBinding != null) {
            _ElementBinding.setViewer(_Viewer);
        }
    }

    private void updateElementInner(Object parent, int index) {
        Object element = getChildElement(parent, index);
        if (element == null) {
            return;
        }

        _ViewerType.updateElement(_Viewer, parent, index, element);
        int childCount = getChildCount(element);
        _ViewerType.setChildCount(_Viewer, element, childCount);
    }

    private class UpdateElementRunnable implements Runnable {

        private int _Index;
        private Object _Parent;

        public UpdateElementRunnable(Object parent, int index) {
            _Parent = parent;
            _Index = index;
        }

        @Override
        public void run() {
            updateElementInner(_Parent, _Index);
        }
    }

}
