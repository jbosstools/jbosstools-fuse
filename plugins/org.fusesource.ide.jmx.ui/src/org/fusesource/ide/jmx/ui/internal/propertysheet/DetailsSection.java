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

package org.fusesource.ide.jmx.ui.internal.propertysheet;

import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;

/**
 * Shows the property details for the currently selected node
 */
public class DetailsSection extends AbstractPropertySection {

	private static final String[] EMPTY_STRING_ARRAY = new String[0];


	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#setInput(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);

		
		Object input = ((IStructuredSelection) selection).getFirstElement();
		
		/*
		if (input instanceof AbstractNode) {
			newNode = (AbstractNode) input;
		} else if (input instanceof AbstractNodeEditPart) {
			AbstractNodeEditPart editPart = (AbstractNodeEditPart) input;
			newNode = editPart.getModelNode();
		} else if (input instanceof AbstractNodeTreeEditPart) {
			AbstractNodeTreeEditPart treePart = (AbstractNodeTreeEditPart) input;
			newNode = treePart.getModelNode();
		}
		if (newNode instanceof Container) {
			nodeContainer = (Container) newNode;
		} else {
			Container parent = newNode.getParent();
			if (parent != null) {
				nodeContainer = parent;
			}
		}

		if (lastNode == newNode || newNode == null) {
			// no need to repaint...
			return;
		}
		node = newNode;
		onNodeChanged(node);
		*/
	}

	// private Section section;
	/*

	private FormToolkit toolkit;

	private Form form;

	private Composite parent;

	private DataBindingContext bindingContext;

	private boolean descriptionTitleBar;

	protected void onNodeChanged(AbstractNode node) {

		bindingContext = new DataBindingContext();

		if (form != null) {
			form.dispose();
		}
		toolkit = new FormToolkit(parent.getDisplay());

		
		// parent.setLayout(new FillLayout());
		parent.setLayout(new GridLayout(1, false));

		form = toolkit.createForm(parent);
		form.setLayoutData(new GridData(GridData.FILL_BOTH));
		form.setText(EditorMessages.propertiesDetailsTitle);
		toolkit.decorateFormHeading(form);

		form.getBody().setLayout(new GridLayout(2, false));

		Composite sbody = form.getBody();


		if (node != null) {
			final IMessageManager mmng = form.getMessageManager();

			form.setText(node.getPatternName());

			IPropertyDescriptor idDescriptor = null;
			IPropertyDescriptor descriptionDescriptor = null;

			IPropertyDescriptor[] propertyDescriptors = node
					.getPropertyDescriptors();

			for (int i = 0; i < 2; i++) {
				for (IPropertyDescriptor descriptor : propertyDescriptors) {
					final Object id = descriptor.getId();
					if ("AbstractNode.Id".equals(id)) {
						idDescriptor = descriptor;
					} else if (NODE_DESCRIPTION.equals(id)) {
						descriptionDescriptor = descriptor;
					} else {
						String propertyName = getPropertyName(id);
						boolean mandatory = descriptor instanceof ExpressionPropertyDescriptor || isMandatory(node, propertyName);
						if ((mandatory && i == 0) || (!mandatory && i == 1)) {
							createDecoratedTextField(descriptor, toolkit, sbody, mmng);
						}
					}
				}
			}

			if (idDescriptor != null || descriptionDescriptor != null) {
					if (idDescriptor != null) {
					createDecoratedTextField(idDescriptor, toolkit, sbody, mmng);
				}
				if (descriptionDescriptor != null) {
					createDecoratedTextField(descriptionDescriptor, toolkit,
							sbody, mmng);
				}
			}

		} else {
			// lets zap the old form
			form.setText(EditorMessages.propertiesDetailsTitle);
		}
		// section.pack();
		// form.pack();
		form.layout();
		parent.layout();
	}
	
	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
		this.parent = parent;
		super.createControls(parent, aTabbedPropertySheetPage);
	}
*/

}
	
