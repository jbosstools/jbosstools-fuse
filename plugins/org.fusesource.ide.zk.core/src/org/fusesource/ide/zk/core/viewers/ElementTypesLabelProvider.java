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

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * Delegates the {@link ColumnLabelProvider} and {@link ITableLabelProvider} interface implementations to a configured
 * group of {@link ElementTypes}.
 * 
 * @see PluggableContentProvider
 * @see ViewerFactory
 * @see ElementTypes
 * @see IElementType
 * 
 * @author Mark Masse
 */
public class ElementTypesLabelProvider extends ColumnLabelProvider implements ITableLabelProvider {

    private final ElementTypes _ElementTypes;

    /**
     * Constructor.
     *
     * @param elementTypes The {@link ElementTypes} delegate.
     */
    public ElementTypesLabelProvider(ElementTypes elementTypes) {
        _ElementTypes = elementTypes;
    }

    /**
     * Returns the elementTypes.
     * 
     * @return The elementTypes
     */
    public ElementTypes getElementTypes() {
        return _ElementTypes;
    }

    @Override
    public Image getImage(Object element) {
        return _ElementTypes.get(element).getImage(element);
    }

    @Override
    public String getText(Object element) {
        return _ElementTypes.get(element).getText(element);
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public Image getColumnImage(Object element, int columnIndex) {
        return _ElementTypes.get(element).getColumnImage(element, columnIndex);
    }

    @Override
    public String getColumnText(Object element, int columnIndex) {
        return _ElementTypes.get(element).getColumnText(element, columnIndex);
    }

}
