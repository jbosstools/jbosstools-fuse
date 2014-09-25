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
package org.fusesource.ide.sap.ui.properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.fusesource.ide.sap.ui.Messages;
import org.fusesource.ide.sap.ui.util.LayoutUtil;

@SuppressWarnings("restriction")
public class ServerDataStoreSection extends AbstractPropertySection {
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);
		
		Composite descriptionContainer = getWidgetFactory().createFlatFormComposite(parent);

		CLabel descriptionLabel = getWidgetFactory().createCLabel(descriptionContainer, Messages.ServerDataStoreSection_DescriptionLabel, SWT.NONE);
		descriptionLabel.setAlignment(SWT.CENTER);
		descriptionLabel.setLayoutData(LayoutUtil.descriptionLayoutData());

	}
}
