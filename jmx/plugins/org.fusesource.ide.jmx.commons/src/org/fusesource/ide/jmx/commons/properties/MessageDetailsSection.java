/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.jmx.commons.properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

public class MessageDetailsSection extends AbstractPropertySection {

	private FormToolkit toolkit;

	public MessageDetailsSection() {
	}

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		// TODO Auto-generated method stub
		super.createControls(parent, aTabbedPropertySheetPage);
		
		toolkit = new FormToolkit(parent.getDisplay());
		ScrolledForm form = toolkit.createScrolledForm(parent);
		form.setText("Hello, Eclipse Forms");

		GridLayout layout = new GridLayout();
		  form.getBody().setLayout(layout);
		  
		  layout.numColumns = 2;
		  GridData gd = new GridData();
		  gd.horizontalSpan = 2;
		  Label label = new Label(form.getBody(), SWT.NULL);
		  label.setText("Text field label:");
		  Text text = new Text(form.getBody(), SWT.BORDER);
		  text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		  /*
		  Button button = new Button(form.getBody(), SWT.CHECK);
		  button.setText("An example of a checkbox in a form");
		  gd = new GridData();
		  gd.horizontalSpan = 2;
		  button.setLayoutData(gd);
		  */
	}

	@Override
	public void dispose() {
		toolkit.dispose();
		super.dispose();
	}
	

}
