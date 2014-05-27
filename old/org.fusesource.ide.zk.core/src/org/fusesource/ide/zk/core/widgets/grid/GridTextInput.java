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

package org.fusesource.ide.zk.core.widgets.grid;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * A {@link Label} and {@link Text} pairing.
 * 
 * @see GridComposite
 * @see GridCompositeStatus
 * 
 * @author Mark Masse
 */
public class GridTextInput {

    /**
     * {@link GridData} that fills horizontally. Useful for {@link Text} layout.
     */
    public final static GridData GRID_DATA_FILL = new GridData(SWT.FILL, SWT.CENTER, true, false);

    /**
     * {@link GridData} that is fixed (static). Useful for {@link Label} layout.
     */
    public final static GridData GRID_DATA_FIXED = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);

    /**
     * {@link Label} creation style.
     */
    public final static int STYLE_LABEL = SWT.LEAD;

    /**
     * {@link Text} creation style.
     */
    public final static int STYLE_TEXT = SWT.BORDER | SWT.SINGLE;

    /**
     * Returns <code>true</code> if the specified text represents a valid {@link Integer} value.
     * 
     * @param text The text to test.
     * @return <code>true</code> if the specified text represents a valid {@link Integer} value.
     */
    public static boolean isValidIntegerText(String text) {
        int intValue = -1;

        try {
            intValue = Integer.parseInt(text);
        }
        catch (NumberFormatException e) {
            return false;
        }

        if (intValue < 0) {
            return false;
        }

        return true;
    }

    private final String _DefaultTextValue;
    private Label _Label;
    private final String _Name;
    private final Text _Text;
    private final Type _Type;

    /**
     * Constructor.
     *
     * @param parent The parent {@link Composite}.
     * @param type The {@link Type}.
     * @param name The {@link GridTextInput} name.
     * @param labelText The {@link Label} text.
     * @param labelStyle The {@link Label} style.
     * @param labelLayoutData The {@link Label} {@link GridData layout data}.
     * @param textText The {@link Text} text.
     * @param textStyle The {@link Text} style.
     * @param textLayoutData The {@link Text} {@link GridData layout data}.
     */
    public GridTextInput(Composite parent, Type type, String name, String labelText, int labelStyle,
            GridData labelLayoutData, String textText, int textStyle, GridData textLayoutData) {

        _Type = type;

        _DefaultTextValue = textText;

        if (_DefaultTextValue != null && _Type.isInteger()) {
            if (!isValidIntegerText(_DefaultTextValue)) {
                throw new IllegalArgumentException("Invalid integer value: " + _DefaultTextValue);
            }
        }

        _Name = name;

        if (labelText != null) {
            _Label = new Label(parent, labelStyle);
            _Label.setText(labelText);
            _Label.setLayoutData(labelLayoutData);
            _Label.pack();
        }

        _Text = new Text(parent, textStyle);
        if (textText != null) {
            _Text.setText(textText);
        }
        _Text.setLayoutData(textLayoutData);

        if (_Type.isInteger()) {
            _Text.setTextLimit(10);
        }

        _Text.setData(this);

    }

    /**
     * Constructor.
     *
     * @param parent The parent {@link Composite}.
     * @param type The {@link Type}.
     * @param name The {@link GridTextInput} name.
     * @param labelText The {@link Label} text.
     * @param textText The {@link Text} text.
     */
    public GridTextInput(Composite parent, Type type, String name, String labelText, String textText) {
        this(parent, type, name, labelText, STYLE_LABEL, GRID_DATA_FIXED, textText, STYLE_TEXT, GRID_DATA_FILL);
    }

    /**
     * Constructor.
     *
     * @param parent The parent {@link Composite}.
     * @param type The {@link Type}.
     * @param name The {@link GridTextInput} name.
     * @param labelText The {@link Label} text.
     * @param textText The {@link Text} text.
     * @param textHorizontalSpan The number of columns that the {@link Text} {@link GridData layout data} should span.
     */
    public GridTextInput(Composite parent, Type type, String name, String labelText, String textText,
            int textHorizontalSpan) {
        this(parent, type, name, labelText, STYLE_LABEL, GRID_DATA_FIXED, textText, STYLE_TEXT, new GridData(SWT.FILL,
                SWT.CENTER, true, false, textHorizontalSpan, 1));
    }

    /**
     * Returns the default text value.
     * 
     * @return The default text value.
     */
    public String getDefaultTextValue() {
        return _DefaultTextValue;
    }

    /**
     * Returns the label.
     * 
     * @return The label
     */
    public Label getLabel() {
        return _Label;
    }

    /**
     * Returns the name.
     * 
     * @return The name
     */
    public String getName() {
        return _Name;
    }

    /**
     * Returns the text.
     * 
     * @return The text
     */
    public Text getText() {
        return _Text;
    }

    /**
     * Returns the type.
     * 
     * @return The type
     */
    public Type getType() {
        return _Type;
    }

    /**
     * Sets the enabled state of the {@link Label} and {@link Text}.
     * 
     * @param enabled The new enabled state.
     * 
     * @see Label#setEnabled(boolean)
     * @see Text#setEnabled(boolean)
     */
    public void setEnabled(boolean enabled) {
        _Label.setEnabled(enabled);
        _Text.setEnabled(enabled);
    }

    /**
     * The {@link GridTextInput} type.
     * 
     * @author Mark Masse
     */
    public static enum Type {
        DEFAULT(0, false, false),
        INTEGER(2, false, true),
        INTEGER_VALUE_REQUIRED(3, true, true),
        VALUE_REQUIRED(1, true, false);

        private final int _Flag;
        private final boolean _Integer;
        private final boolean _ValueRequired;

        private Type(int flag, boolean valueRequired, boolean integer) {
            _Flag = flag;
            _ValueRequired = valueRequired;
            _Integer = integer;
        }

        /**
         * Returns the flag.
         * 
         * @return The flag
         */
        public int getFlag() {
            return _Flag;
        }

        /**
         * Returns the integer.
         * 
         * @return The integer
         */
        public boolean isInteger() {
            return _Integer;
        }

        /**
         * Returns the valueRequired.
         * 
         * @return The valueRequired
         */
        public boolean isValueRequired() {
            return _ValueRequired;
        }
    }

}
