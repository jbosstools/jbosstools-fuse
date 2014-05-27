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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * A simple "information bar" widget similar to the one shown at the top of Internet Explorer when it decides to tell you that
 * your PC was just infected by the binary equivalent of the T-Virus.
 * 
 * @see SWT#COLOR_INFO_BACKGROUND
 * @see SWT#COLOR_INFO_FOREGROUND
 * 
 * @author Mark Masse
 */
public class InfoBar extends Composite {

    private static final Point ZERO_SIZE = new Point(0, 0);

    private final Label _Label;

    /**
     * Constructor.
     * 
     * @param parent The parent {@link Composite}.
     * @param style The InfoBar {@link Composite#getStyle() style}.
     */
    public InfoBar(Composite parent, int style) {
        super(parent, style);
        setVisible(false);

        FormLayout layout = new FormLayout();
        layout.marginTop = 5;
        layout.marginBottom = 5;
        layout.marginLeft = 5;
        layout.marginRight = 5;
        layout.spacing = 5;
        setLayout(layout);

        Color backgroundColor = parent.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND);

        setBackground(backgroundColor);
        _Label = new Label(this, SWT.LEAD | SWT.WRAP);
        _Label.setBackground(backgroundColor);

        FormData labelFormData = new FormData();
        labelFormData.top = new FormAttachment(0, 0);
        labelFormData.left = new FormAttachment(0, 0);
        _Label.setLayoutData(labelFormData);
    }

    @Override
    public Point computeSize(int wHint, int hHint, boolean changed) {
        if (!isVisible()) {
            return ZERO_SIZE;
        }
        return super.computeSize(wHint, hHint, changed);
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
     * Sets the information text.
     * 
     * @param text The text to display.
     */
    public void setText(String text) {

        if (isDisposed()) {
            return;
        }

        setVisible(text != null);

        if (text == null) {
            text = "";
        }

        Color textColor = getDisplay().getSystemColor(SWT.COLOR_INFO_FOREGROUND);
        _Label.setForeground(textColor);
        _Label.setText(text);

        layout(true);
        getParent().layout(true);
    }

}
