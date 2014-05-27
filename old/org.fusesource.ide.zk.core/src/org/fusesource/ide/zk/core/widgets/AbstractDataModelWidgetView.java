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

package org.fusesource.ide.zk.core.widgets;


import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Widget;
import org.fusesource.ide.zk.core.model.DataModel;
import org.fusesource.ide.zk.core.model.GenericDataModelEvent;
import org.fusesource.ide.zk.core.model.IGenericDataModelEventListener;
import org.fusesource.ide.zk.core.reflect.SwtThreadSafeDelegatingInvocationHandler;
import org.fusesource.ide.zk.core.reflect.SwtThreadSafeDelegatingInvocationHandler.IWidgetProvider;

/**
 * Connects a {@link Widget} (the "view") to a {@link DataModel}.
 *
 * @author Mark Masse
 */
public abstract class AbstractDataModelWidgetView<W extends Widget> implements IGenericDataModelEventListener,
        IWidgetProvider {

    private final DataModel<?, ?, ?> _Model;
    private IGenericDataModelEventListener _ModelEventListener;
    private final W _Widget;

    /**
     * Constructor.
     *
     * @param model The {@link DataModel}.
     * @param widget The {@link Widget} view.
     */
    public AbstractDataModelWidgetView(DataModel<?, ?, ?> model, W widget) {
        _Model = model;
        _Widget = widget;

        registerModelEventListener();

        _Widget.addDisposeListener(new DisposeListener() {

            @Override
            public void widgetDisposed(DisposeEvent e) {
                unregisterModelEventListener();
                _Widget.removeDisposeListener(this);
            }
        });
    }

    @Override
    public void dataModelDataChanged(GenericDataModelEvent event) {
        W widget = getWidget();
        if (!widget.isDisposed()) {
            updateView();
        }
    }

    @Override
    public void dataModelDataRefreshed(GenericDataModelEvent event) {
        W widget = getWidget();
        if (!widget.isDisposed()) {
            updateView();
        }
    }

    @Override
    public void dataModelDestroyed(GenericDataModelEvent event) {
        W widget = getWidget();
        if (!widget.isDisposed()) {
            widget.dispose();
        }
    }

    /**
     * Returns the model.
     * 
     * @return The model
     */
    public final DataModel<?, ?, ?> getModel() {
        return _Model;
    }

    @Override
    public final W getWidget() {
        return _Widget;
    }

    /**
     * Updates the view ({@link Widget}) to reflect possible {@link DataModel} changes.
     */
    public abstract void updateView();

    protected void registerModelEventListener() {

        DataModel<?, ?, ?> model = getModel();

        if (model == null) {
            return;
        }

        if (_ModelEventListener == null) {
            _ModelEventListener = (IGenericDataModelEventListener) SwtThreadSafeDelegatingInvocationHandler
                    .createProxyInstance(this, IGenericDataModelEventListener.class, true);
        }

        model.addGenericEventListener(_ModelEventListener);
    }

    protected void unregisterModelEventListener() {
        DataModel<?, ?, ?> model = getModel();

        if (model != null && _ModelEventListener != null) {
            model.removeGenericEventListener(_ModelEventListener);
            _ModelEventListener = null;
        }
    }

}