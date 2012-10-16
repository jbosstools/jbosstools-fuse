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

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Widget;
import org.fusesource.ide.zk.core.model.DataModel;
import org.fusesource.ide.zk.core.viewers.IElementType;


/**
 * Makes use of a {@link IElementType} instance to retrieve display values from the {@link DataModel}.
 * 
 * 
 * @author Mark Masse
 */
public abstract class AbstractElementTypeDataModelWidgetView<W extends Widget> extends AbstractDataModelWidgetView<W> {

    private final IElementType _ElementType;

    /**
     * Constructor.
     * 
     * @param model The {@link DataModel}.
     * @param widget The {@link Widget}.
     * @param elementType The model's {@link IElementType} used to retrieve display values.
     */
    public AbstractElementTypeDataModelWidgetView(DataModel<?, ?, ?> model, W widget, IElementType elementType) {
        super(model, widget);
        _ElementType = elementType;
    }

    @Override
    public void updateView() {
        DataModel<?, ?, ?> model = getModel();
        W widget = getWidget();
        setWidgetText(widget, _ElementType.getText(model));
        setWidgetImage(widget, _ElementType.getImage(model));
        setWidgetToolTipText(widget, _ElementType.getToolTipText(model));
    }

    /**
     * Sets the specified text on the specified {@link Widget}.
     * 
     * @param widget The {@link Widget}.
     * @param text The text to set.
     */
    protected abstract void setWidgetText(W widget, String text);

    /**
     * Sets the specified {@link Image} on the specified {@link Widget}.
     * 
     * @param widget The {@link Widget}.
     * @param image The {@link Image} to set.
     */
    protected abstract void setWidgetImage(W widget, Image image);

    /**
     * Sets the specified ToolTip text on the specified {@link Widget}.
     * 
     * @param widget The {@link Widget}.
     * @param toolTipText The ToolTip text to set.
     */
    protected abstract void setWidgetToolTipText(W widget, String toolTipText);
}
