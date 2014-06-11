/*******************************************************************************
 * Copyright (c) 2007 Jeff Mesnil
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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

package org.fusesource.ide.jmx.ui.internal.editors;


import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.fusesource.ide.commons.Viewers;
import org.fusesource.ide.jmx.core.MBeanAttributeInfoWrapper;
import org.fusesource.ide.jmx.core.MBeanInfoWrapper;
import org.fusesource.ide.jmx.ui.Messages;
import org.fusesource.ide.jmx.ui.internal.tables.MBeanAttributesTable;


public class AttributesSection extends SectionPart {

	private MBeanAttributesTable attributesTable;

	public AttributesSection(MBeanInfoWrapper wrapper,
			final IManagedForm managedForm, Composite parent) {
		super(parent, managedForm.getToolkit(), Section.TITLE_BAR);

		FormToolkit toolkit = managedForm.getToolkit();
		Section section = getSection();
		section.marginWidth = 10;
		section.marginHeight = 5;
		section.setText(Messages.AttributesSection_title);
		Composite container = toolkit.createComposite(section, SWT.WRAP);
		section.setClient(container);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		container.setLayout(layout);

		attributesTable = new MBeanAttributesTable(container, toolkit);
		attributesTable.setInput(wrapper);

		final SectionPart spart = new SectionPart(section);
		managedForm.addPart(spart);
		attributesTable.getViewer().addSelectionChangedListener(
				new ISelectionChangedListener() {
					public void selectionChanged(SelectionChangedEvent event) {
						managedForm.fireSelectionChanged(spart, event
								.getSelection());
					}
				});
	}

	public Viewer getTableViewer() {
		return attributesTable.getViewer();
	}

	@Override
	public void refresh() {
		super.refresh();
		Viewers.refresh(attributesTable.getViewer());
	}

	@Override
	public boolean setFormInput(Object input) {
		if (input instanceof MBeanAttributeInfoWrapper) {
			MBeanAttributeInfoWrapper wrapper = (MBeanAttributeInfoWrapper) input;
			ISelection selection = new StructuredSelection(wrapper);
			attributesTable.getViewer().setSelection(selection, true);
			return true;
		}
		return false;
	}
}
