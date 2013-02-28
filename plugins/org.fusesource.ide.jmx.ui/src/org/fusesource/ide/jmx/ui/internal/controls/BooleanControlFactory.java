/*******************************************************************************
 * Copyright (c) 2007 Jeff Mesnil
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *      Benjamin Walstrum (issue #24)
 *******************************************************************************/

/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.jmx.ui.internal.controls;


import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.fusesource.ide.jmx.ui.extensions.IAttributeControlFactory;
import org.fusesource.ide.jmx.ui.extensions.IWritableAttributeHandler;


public class BooleanControlFactory implements IAttributeControlFactory {

	public Control createControl(final Composite parent, final FormToolkit toolkit,
			final boolean writable, final String type, final Object value, 
			final IWritableAttributeHandler handler) {

		boolean booleanValue = ((Boolean) value).booleanValue();
        if (!writable) {
            if (toolkit != null) {
                return toolkit.createText(parent, Boolean
                        .toString(booleanValue), SWT.SINGLE);
            } else {
                Text text = new Text(parent, SWT.SINGLE);
                text.setText(Boolean.toString(booleanValue));
                return text;
            }
        }

        final Combo combo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
        if (toolkit != null) {
            combo.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
            toolkit.paintBordersFor(combo);
        }
        combo.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_BLUE));
        combo.setItems(new String[] { Boolean.TRUE.toString(),
                Boolean.FALSE.toString() });
        if (booleanValue) {
            combo.select(0);
        } else {
            combo.select(1);
        }
        if (handler != null) {
            combo.addListener(SWT.Selection, new Listener() {
                public void handleEvent(Event event) {
                    Boolean newValue = Boolean.valueOf(combo.getText());
                    handler.write(newValue);
                }
            });
        }
        return combo;
	}
	
}
