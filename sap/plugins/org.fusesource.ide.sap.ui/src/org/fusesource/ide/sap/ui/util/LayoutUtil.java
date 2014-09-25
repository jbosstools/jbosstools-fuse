/*******************************************************************************
* Copyright (c) 2014 Red Hat, Inc.
* Distributed under license by Red Hat, Inc. All rights reserved.
* This program is made available under the terms of the
* Eclipse Public License v1.0 which accompanies this distribution,
* and is available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
* Red Hat, Inc. - initial API and implementation
* William Collins punkhornsw@gmail.com
******************************************************************************/ 
package org.fusesource.ide.sap.ui.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;

@SuppressWarnings("restriction")
public class LayoutUtil {

	public static FormData descriptionLayoutData() {
		FormData data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);
		return data;
	}
	
	public static FormData firstEntryLayoutData() {
		FormData data = new FormData();
		data.left = new FormAttachment(0, (int) 4 * AbstractPropertySection.STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);
		return data;
	}
	
	public static FormData labelLayoutData(Control referenceControl) {
		FormData data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(referenceControl, -ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(referenceControl, 0, SWT.CENTER);
		return data;
	}
	
	public static FormData entryLayoutData(Control referenceControl) {
		FormData data = new FormData();
		data = new FormData();
		data.left = new FormAttachment(0, (int) 4 * AbstractPropertySection.STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(referenceControl, 2 * ITabbedPropertyConstants.VSPACE);
		return data;
	}
	
}
