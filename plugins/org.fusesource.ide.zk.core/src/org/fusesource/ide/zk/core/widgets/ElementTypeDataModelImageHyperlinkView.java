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
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.fusesource.ide.zk.core.model.DataModel;
import org.fusesource.ide.zk.core.viewers.IElementType;


/**
 * Concrete data model view that displays the {@link DataModel} as a {@link ImageHyperlink}.
 * 
 * @author Mark Masse
 */
public class ElementTypeDataModelImageHyperlinkView extends AbstractElementTypeDataModelWidgetView<ImageHyperlink> {

    /**
     * Constructor.
     * 
     * @param model The {@link DataModel}.
     * @param widget The {@link ImageHyperlink} view.
     * @param elementType The model's {@link IElementType}.
     */
    public ElementTypeDataModelImageHyperlinkView(DataModel<?, ?, ?> model, ImageHyperlink widget,
            IElementType elementType) {
        super(model, widget, elementType);
    }

    @Override
    protected void setWidgetImage(ImageHyperlink widget, Image image) {
        widget.setImage(image);
    }

    @Override
    protected void setWidgetText(ImageHyperlink widget, String text) {
        widget.setText(text);
    }

    @Override
    protected void setWidgetToolTipText(ImageHyperlink widget, String toolTipText) {
        widget.setToolTipText(toolTipText);

    }

}
