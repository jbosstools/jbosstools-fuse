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

import org.eclipse.swt.widgets.Widget;
import org.fusesource.ide.zk.core.model.DataModel;
import org.fusesource.ide.zk.core.model.DataModelManager;
import org.fusesource.ide.zk.core.model.GenericDataModelEvent;
import org.fusesource.ide.zk.core.model.GenericDataModelManagerEvent;
import org.fusesource.ide.zk.core.model.IGenericDataModelEventListener;
import org.fusesource.ide.zk.core.model.IGenericDataModelManagerEventListener;
import org.fusesource.ide.zk.core.reflect.SwtThreadSafeDelegatingInvocationHandler;
import org.fusesource.ide.zk.core.reflect.SwtThreadSafeDelegatingInvocationHandler.IWidgetProvider;


/**
 * <p>
 * Within the library framework, this class is the primary "view" for the "models". 
 * </p>
 * <p>
 * It is a caching {@link IElementBinding} that accepts both {@link DataModel} and {@link DataModelManager} elements to bind. This
 * binding will register for data model (and manager) events and respond to the events by refreshing the viewer
 * accordingly.
 * </p>
 * 
 * @see SwtThreadSafeDelegatingInvocationHandler
 * 
 * @author Mark Masse
 */
public class DataModelElementBinding extends AbstractCachingElementBinding implements IElementBinding {

    private final IGenericDataModelManagerEventListener _DataModelManagerEventListener;
    private final IGenericDataModelEventListener _DataModelEventListener;

    public DataModelElementBinding() {

        _DataModelEventListener = (IGenericDataModelEventListener) SwtThreadSafeDelegatingInvocationHandler
                .createProxyInstance(new DataModelEventListener(), IGenericDataModelEventListener.class, true);

        _DataModelManagerEventListener = (IGenericDataModelManagerEventListener) SwtThreadSafeDelegatingInvocationHandler
                .createProxyInstance(new DataModelManagerEventListener(), IGenericDataModelManagerEventListener.class,
                        true);

    }

    @Override
    protected boolean bindHook(Object element) {

        if (element instanceof DataModel<?, ?, ?>) {
            DataModel<?, ?, ?> dataModel = (DataModel<?, ?, ?>) element;
            dataModel.addGenericEventListener(_DataModelEventListener);
            return true;
        }
        else if (element instanceof DataModelManager<?, ?, ?>) {
            DataModelManager<?, ?, ?> dataModelManager = (DataModelManager<?, ?, ?>) element;
            dataModelManager.addGenericEventListener(_DataModelManagerEventListener);
            return true;
        }

        return false;
    }

    @Override
    protected boolean unbindHook(Object element) {
        if (element instanceof DataModel<?, ?, ?>) {
            DataModel<?, ?, ?> dataModel = (DataModel<?, ?, ?>) element;
            dataModel.removeGenericEventListener(_DataModelEventListener);
            return true;
        }

        else if (element instanceof DataModelManager<?, ?, ?>) {
            DataModelManager<?, ?, ?> dataModelManager = (DataModelManager<?, ?, ?>) element;
            dataModelManager.removeGenericEventListener(_DataModelManagerEventListener);
            return true;
        }

        return false;
    }

    public class DataModelEventListener implements IGenericDataModelEventListener, IWidgetProvider {

        @Override
        public void dataModelDataChanged(GenericDataModelEvent event) {
            getViewerType().refreshElement(getViewer(), event.getModel());
        }

        @Override
        public void dataModelDataRefreshed(GenericDataModelEvent event) {
            getViewerType().refreshElement(getViewer(), event.getModel());
        }

        @Override
        public void dataModelDestroyed(GenericDataModelEvent event) {
            Object element = event.getModel();
            getViewerType().removeElement(getViewer(), element);
            unbind(element);
        }

        @Override
        public Widget getWidget() {
            return getViewer().getControl();
        }
    }

    public class DataModelManagerEventListener implements IGenericDataModelManagerEventListener, IWidgetProvider {

        @Override
        public void dataModelManagerDataModelAdded(GenericDataModelManagerEvent event) {
            getViewerType().addElement(getViewer(), event.getManager(), event.getModel());
        }

        @Override
        public void dataModelManagerDataModelRemoved(GenericDataModelManagerEvent event) {
            getViewerType().removeElement(getViewer(), event.getModel());
        }

        @Override
        public void dataModelManagerDestroyed(GenericDataModelManagerEvent event) {
        }

        @Override
        public Widget getWidget() {
            return getViewer().getControl();
        }
    }

}
