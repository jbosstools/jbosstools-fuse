/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     James Strachan <jstracha@redhat.com> - Camel specific updates
 *******************************************************************************/
package org.fusesource.ide.branding.wizards;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.internal.junit.Messages;
import org.eclipse.jdt.internal.junit.util.LayoutUtil;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.ITreePathLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeNodeContentProvider;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.ViewerLabel;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ContainerCheckedTreeViewer;
import org.fusesource.ide.branding.RiderHelpContextIds;
import org.fusesource.ide.camel.model.service.core.io.CamelIOHandler;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.camel.model.service.core.model.CamelRouteContainerElement;
import org.fusesource.ide.camel.model.service.core.model.CamelRouteElement;
import org.fusesource.ide.foundation.core.util.URIs;


/**
 * The class <code>NewCamelTestWizardPageTwo</code> contains controls and
 * validation routines for the second page of the 'New JUnit TestCase Wizard'.
 * <p>
 * Clients can use the page as-is and add it to their own wizard, or extend it
 * to modify validation or add and remove controls.
 * </p>
 * 
 * @since 3.1
 */
@SuppressWarnings("restriction")
public class NewCamelTestWizardPageTwo extends WizardPage {

	public static class EndpointMaps {
		private String inputEndpoint;
		private final Map<String, String> inputEndpoints;
		private final Map<String, String> outputEndpoints;

		public EndpointMaps(Map<String, String> inputEndpoints, Map<String, String> outputEndpoints) {
			this.inputEndpoints = inputEndpoints;
			this.outputEndpoints = outputEndpoints;

			for (String name : inputEndpoints.keySet()) {
				if (inputEndpoint == null) {
					inputEndpoint = getInputEndpointVariableName(name);
					break;
				}
			}
		}

		public String getInputEndpoint() {
			return inputEndpoint;
		}

		public Map<String, String> getInputEndpoints() {
			return inputEndpoints;
		}

		public String getInputEndpointVariableName(String name) {
			return name + "Endpoint";
		}

		public Map<String, String> getOutputEndpoints() {
			return outputEndpoints;
		}
	}

	protected static class EndpointsLabelProvider extends LabelProvider implements ITreePathLabelProvider {

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITreePathLabelProvider#updateLabel(org.eclipse.jface.viewers.ViewerLabel, org.eclipse.jface.viewers.TreePath)
		 */
		@Override
		public void updateLabel(ViewerLabel label, TreePath elementPath) {
			Object last = elementPath.getLastSegment();
			String text;
			if (last instanceof TreeNode) {
				TreeNode node = (TreeNode) last;
				text = node.getValue().toString();
			} else {
				text = last.toString();
			}
			label.setText(text);
		}
	}

	protected static class EndpointTreeNode extends TreeNode {
		private final boolean input;

		public EndpointTreeNode(Object value, boolean input) {
			super(value);
			this.input = input;
		}

		public String getUri() {
			return getValue().toString();
		}

		public boolean isInput() {
			return input;
		}
	}

	private static final String PAGE_NAME = "NewCamelTestWizardPage2"; //$NON-NLS-1$

	private static final String STORE_CREATE_FINAL_METHOD_STUBS = PAGE_NAME + ".CREATE_FINAL_METHOD_STUBS"; //$NON-NLS-1$
	private static final String STORE_USE_TASKMARKER = PAGE_NAME + ".USE_TASKMARKER"; //$NON-NLS-1$
	private Object[] fCheckedObjects;
	private Button fCreateFinalMethodStubsButton;
	private boolean fCreateFinalStubs;
	private boolean fCreateTasks;
	private Button fCreateTasksButton;
	private ContainerCheckedTreeViewer fInputEndpointsTree;


	private Label fSelectedEndpointsLabel;

	private IFile xmlFileUnderTest;

	/**
	 * Creates a new <code>NewCamelTestWizardPageTwo</code>.
	 */
	public NewCamelTestWizardPageTwo() {
		super(PAGE_NAME);
		setTitle(WizardMessages.NewCamelTestWizardPageTwo_title);
		setDescription(WizardMessages.NewCamelTestWizardPageTwo_description);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		container.setLayout(layout);

		createEndpointsTreeControls(container);
		/*
		 * createSpacer(container); createButtonChoices(container);
		 */
		setControl(container);
		restoreWidgetValues();
		Dialog.applyDialogFont(container);
		PlatformUI.getWorkbench().getHelpSystem()
		.setHelp(container, RiderHelpContextIds.NEW_CAMEL_TESTCASE_WIZARD_PAGE2);
	}

	private void createEndpointsTreeControls(Composite container) {
		Label label = new Label(container, SWT.LEFT | SWT.WRAP);
		label.setFont(container.getFont());
		label.setText(WizardMessages.NewCamelTestWizardPageTwo_methods_tree_label);
		GridData gd = new GridData();
		gd.horizontalSpan = 2;
		label.setLayoutData(gd);

		fInputEndpointsTree = new ContainerCheckedTreeViewer(container, SWT.BORDER);
		gd = new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);
		gd.heightHint = 180;
		fInputEndpointsTree.getTree().setLayoutData(gd);

		fInputEndpointsTree.setLabelProvider(new JavaElementLabelProvider());
		fInputEndpointsTree.setAutoExpandLevel(2);
		fInputEndpointsTree.addCheckStateListener(event -> doCheckedStateChanged());

		Composite buttonContainer = new Composite(container, SWT.NONE);
		gd = new GridData(GridData.FILL_VERTICAL);
		buttonContainer.setLayoutData(gd);
		GridLayout buttonLayout = new GridLayout();
		buttonLayout.marginWidth = 0;
		buttonLayout.marginHeight = 0;
		buttonContainer.setLayout(buttonLayout);

		Button fSelectAllButton = new Button(buttonContainer, SWT.PUSH);
		fSelectAllButton.setText(WizardMessages.NewCamelTestWizardPageTwo_selectAll);
		gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		fSelectAllButton.setLayoutData(gd);
		fSelectAllButton.addSelectionListener(new SelectionAdapter() {
			/*
			 * (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectAllEndpoints();
				doCheckedStateChanged();
			}
		});
		LayoutUtil.setButtonDimensionHint(fSelectAllButton);

		Button fDeselectAllButton = new Button(buttonContainer, SWT.PUSH);
		fDeselectAllButton.setText(WizardMessages.NewCamelTestWizardPageTwo_deselectAll);
		gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		fDeselectAllButton.setLayoutData(gd);
		fDeselectAllButton.addSelectionListener(new SelectionAdapter() {
			/*
			 * (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				fInputEndpointsTree.setCheckedElements(new Object[0]);
				doCheckedStateChanged();
			}
		});
		LayoutUtil.setButtonDimensionHint(fDeselectAllButton);

		/* No of selected methods label */
		fSelectedEndpointsLabel = new Label(container, SWT.LEFT);
		fSelectedEndpointsLabel.setFont(container.getFont());
		doCheckedStateChanged();
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		fSelectedEndpointsLabel.setLayoutData(gd);

		Label emptyLabel = new Label(container, SWT.LEFT);
		gd = new GridData();
		gd.horizontalSpan = 1;
		emptyLabel.setLayoutData(gd);

		onXmlFileUnderTestChanged();
	}

	private void doCheckedStateChanged() {
		Object[] checked = fInputEndpointsTree.getCheckedElements();
		fCheckedObjects = checked;

		int checkedEndpointCount = 0;
		for (Object element : checked) {
			if (element instanceof EndpointTreeNode) {
				checkedEndpointCount++;
			}
		}
		String label; //$NON-NLS-1$
		if (checkedEndpointCount == 1) {
			label = Messages.format(WizardMessages.NewCamelTestWizardPageTwo_selected_endpoints_label_one, Integer.valueOf(checkedEndpointCount));
		} else {
			label = Messages.format(WizardMessages.NewCamelTestWizardPageTwo_selected_endpoints_label_many, Integer.valueOf(checkedEndpointCount));
		}
		fSelectedEndpointsLabel.setText(label);
	}

	/**
	 * Returns a map of local name -> URI for input and output endpoints to be
	 * used in testing. Local names should be unique
	 */
	public EndpointMaps getCheckedEndpointMaps() {
		List<EndpointTreeNode> endpoints = getCheckedEndpoints();
		Map<String, String> inputEndpoints = new LinkedHashMap<>();
		Map<String, String> outputEndpoints = new LinkedHashMap<>();

		int inputCounter = 0;
		int outputCounter = 0;

		for (EndpointTreeNode node : endpoints) {
			String key;
			String value = node.getUri();
			if (node.isInput()) {
				key = "input";
				if (!URIs.isTimerEndpointURI(value)) {
					if (inputCounter++ > 0) {
						key += Integer.toString(inputCounter);
					}
					inputEndpoints.put(key, value);
				}
			} else {
				key = "output";
				if (outputCounter++ > 0) {
					key += Integer.toString(outputCounter);
				}
				outputEndpoints.put(key, value);
			}
		}
		return new EndpointMaps(inputEndpoints, outputEndpoints);
	}

	/**
	 * Returns all checked endpoints in the tree
	 */
	public List<EndpointTreeNode> getCheckedEndpoints() {
		List<EndpointTreeNode> list = new ArrayList<>();
		if (fCheckedObjects != null) {
			for (Object object : fCheckedObjects) {
				if (object instanceof EndpointTreeNode) {
					list.add((EndpointTreeNode) object);
				}
			}
		}
		return list;
	}

	/**
	 * Returns true if the checkbox for final method stubs is checked.
	 * 
	 * @return <code>true</code> is returned if methods should be created final
	 */
	public boolean getCreateFinalMethodStubsButtonSelection() {
		return fCreateFinalStubs;
	}

	/**
	 * Returns true if the checkbox for creating tasks is checked.
	 * 
	 * @return <code>true</code> is returned if tasks should be created
	 */
	public boolean isCreateTasks() {
		return fCreateTasks;
	}

	protected TreeNode loadEndpointSummary() {
		CamelIOHandler io = new CamelIOHandler();
		CamelFile cf = io.loadCamelModel(xmlFileUnderTest, new NullProgressMonitor());

		TreeNode root = new TreeNode("Endpoints");
		TreeNode inputs = new TreeNode("Inputs");
		TreeNode outputs = new TreeNode("Outputs");
		inputs.setParent(root);
		outputs.setParent(root);
		root.setChildren(new TreeNode[] { inputs, outputs });

		setChildren(inputs, getInputs(cf.getRouteContainer()), true);
		setChildren(outputs, getOutputs(cf.getRouteContainer()), false);

		return root;
	}

	private List<AbstractCamelModelElement> getInputs(CamelRouteContainerElement context) {
		List<AbstractCamelModelElement> eps = new ArrayList<>();
		
		for (AbstractCamelModelElement route : context.getChildElements()) {
			if (route instanceof CamelRouteElement) {
				CamelRouteElement r = (CamelRouteElement)route;
				eps.addAll(r.getInputs());				
			}
		}
		
		return eps;
	}
	
	private List<AbstractCamelModelElement> getOutputs(CamelRouteContainerElement context) {
		List<AbstractCamelModelElement> eps = new ArrayList<>();
		
		for (AbstractCamelModelElement route : context.getChildElements()) {
			if (route instanceof CamelRouteElement) {
				CamelRouteElement r = (CamelRouteElement)route;
				collectEndpoints(r.getOutputs(), eps);				
			}
		}
		
		return eps;
	}
	
	private void collectEndpoints(List<AbstractCamelModelElement> searchList, List<AbstractCamelModelElement> resultList) {
		for (AbstractCamelModelElement cme : searchList) {
			if (cme.isEndpointElement()) resultList.add(cme);
			if (!cme.getChildElements().isEmpty()) {
				collectEndpoints(cme.getChildElements(), resultList);
			}
		}
	}
	
	protected void onXmlFileUnderTestChanged() {
		// lets eagerly update the widgets in case we don't view this page
		if (xmlFileUnderTest == null || fInputEndpointsTree == null) {
			return;
		}

		TreeNode root = loadEndpointSummary();
		fInputEndpointsTree.setContentProvider(new TreeNodeContentProvider());
		fInputEndpointsTree.setInput(new TreeNode[] { root });
		fInputEndpointsTree.setLabelProvider(new EndpointsLabelProvider());
		selectAllEndpoints();
		fInputEndpointsTree.expandAll();
		doCheckedStateChanged();
	}

	/**
	 * Use the dialog store to restore widget values to the values that they
	 * held last time this wizard was used to completion
	 */
	private void restoreWidgetValues() {
		IDialogSettings settings = getDialogSettings();
		if (settings != null) {
			fCreateTasks = settings.getBoolean(STORE_USE_TASKMARKER);
			if (fCreateTasksButton != null) {
				fCreateTasksButton.setSelection(fCreateTasks);
			}
			fCreateFinalStubs = settings.getBoolean(STORE_CREATE_FINAL_METHOD_STUBS);
			if (fCreateFinalMethodStubsButton != null) {
				fCreateFinalMethodStubsButton.setSelection(fCreateFinalStubs);
			}
		}
	}

	protected void selectAllEndpoints() {
		fInputEndpointsTree.setCheckedElements((Object[]) fInputEndpointsTree.getInput());
	}

	private void setChildren(TreeNode node, List<AbstractCamelModelElement> endpointList, boolean input) {
		List<TreeNode> children = new ArrayList<>();
		for (AbstractCamelModelElement e : endpointList) {
			if (!e.isEndpointElement()) continue; 
			TreeNode child = new EndpointTreeNode(e.getParameter("uri"), input);
			child.setParent(node);
			children.add(child);
		}
		node.setChildren(children.toArray(new TreeNode[children.size()]));
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.DialogPage#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			fInputEndpointsTree.getControl().setFocus();
		}
	}

	public void setXmlFileUnderTest(IFile xmlFileUnderTest) {
		this.xmlFileUnderTest = xmlFileUnderTest;
		onXmlFileUnderTestChanged();
	}
}
