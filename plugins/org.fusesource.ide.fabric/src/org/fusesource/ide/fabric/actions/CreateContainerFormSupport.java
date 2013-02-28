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

package org.fusesource.ide.fabric.actions;

import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.fusesource.fabric.api.Version;
import org.fusesource.ide.commons.Viewers;
import org.fusesource.ide.commons.ui.ICanValidate;
import org.fusesource.ide.commons.ui.Selections;
import org.fusesource.ide.commons.ui.form.Forms;
import org.fusesource.ide.fabric.navigator.Fabric;
import org.fusesource.ide.fabric.navigator.VersionNode;
import org.fusesource.ide.fabric.navigator.VersionsNode;


public abstract class CreateContainerFormSupport extends ProfileTreeSelectionFormSupport {

	public static final String AGENT_NAME_PROPERTY = "agentName";
	public static final String AGENT_VERSION_PROPERTY = "agentVersion";

	VersionNode versionNode;

	private String agentName;
	private Text containerNameField;
	private ComboViewer versionCombo;

	public CreateContainerFormSupport(ICanValidate validator, VersionNode versionNode, String defaultAgentName) {
		super(validator);
		this.versionNode = versionNode;
		this.agentName = defaultAgentName;
		addMandatoryPropertyNames(AGENT_VERSION_PROPERTY);
		addMandatoryPropertyNames(AGENT_NAME_PROPERTY);
	}

	@Override
	public abstract void okPressed();

	public String getAgentName() {
		return agentName;
	}

	public void setAgentName(String agentName) {
		String oldValue = this.agentName;
		this.agentName = agentName;
		firePropertyChange(AGENT_NAME_PROPERTY, oldValue, agentName);
	}

	public VersionNode getAgentVersion() {
		return getVersionNode();
	}

	public void setAgentVersion(VersionNode version) {
		VersionNode oldVersion = this.versionNode;
		this.versionNode = version;
		if (version != null) {
			setProfilesViewerInput(version);
			firePropertyChange(AGENT_VERSION_PROPERTY, oldVersion, this.versionNode);
		}
	}

	@Override
	protected String getFormHeader() {
		return Messages.agentFormHeader;
	}

	@Override
	public void setFocus() {
		containerNameField.setFocus();
	}

	public VersionNode getVersionNode() {
		return versionNode;
	}

	public Fabric getFabric() {
		if (versionNode != null) {
			return versionNode.getFabric();
		}
		return null;
	}

	@Override
	public Control createDialogArea(Composite parent) {
		Composite outer = createWizardArea(parent);
		createButtons(outer);

		return parent;
	}

	public Composite createWizardArea(Composite parent) {
		createForm(parent);

		Composite inner = createSectionComposite(getSectionHeader(), new GridData(GridData.FILL_HORIZONTAL));

		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		inner.setLayout(layout);

		createTextFields(inner);

		if (isSelectProfile()) {
			Composite outer = createSectionComposite(Messages.selectedProfiles, new GridData(GridData.FILL_BOTH));
			layout = new GridLayout();
			layout.numColumns = 2;
			layout.marginHeight = 0;
			layout.marginWidth = 0;
			outer.setLayout(layout);

			createColumnsViewer(outer);
			loadPreference();

			return outer;
		} else {
			return inner;
		}
	}

	protected String getSectionHeader() {
		return Messages.agentFieldsHeader;
	}

	@Override
	protected void createTextFields(Composite inner) {
		containerNameField = createBeanPropertyTextField(inner, this, AGENT_NAME_PROPERTY, Messages.agentNameLabel, Messages.agentNameTooltip);

		if (chooseVersion()) {
			versionCombo = createBeanPropertyCombo(inner, this, AGENT_VERSION_PROPERTY, Messages.agentVersionLabel, Messages.agentVersionTooltip, SWT.READ_ONLY);
			versionCombo.setContentProvider(new IStructuredContentProvider() {
				@Override
				public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				}

				@Override
				public void dispose() {
				}

				@Override
				public Object[] getElements(Object inputElement) {
					if (inputElement != null && inputElement instanceof VersionsNode) {
						VersionsNode versions = (VersionsNode)inputElement;
						return versions.getChildren();
					}
					return null;
				}
			});
			versionCombo.addSelectionChangedListener(new ISelectionChangedListener() {
				@Override
				public void selectionChanged(SelectionChangedEvent event) {
					if (event.getSelection().isEmpty() || event.getSelection() instanceof IStructuredSelection == false) {
						// invalid selection
						return;
					}
					IStructuredSelection isel = (IStructuredSelection)event.getSelection();
					Object o = isel.getFirstElement();
					if (o instanceof VersionNode) {
						setAgentVersion((VersionNode)o);
					}
				}
			});
			Viewers.async(new Runnable() {

				@Override
				public void run() {
					versionCombo.setSelection(new StructuredSelection(versionNode), true);
				}});

		}
	}

	protected boolean chooseVersion() {
		return true;
	}

	public String getNewAgentName() {
		return containerNameField.getText();
	}

	protected Version getVersion() {
		if (versionCombo != null) {
			return (Version) Selections.getFirstSelection(versionCombo.getSelection());

		}
		return null;
	}

	@Override
	protected void validateProfiles() {
		IStatus status;
		if (hasCheckedProfiles()) {
			status = ValidationStatus.ok();
		} else {
			status = ValidationStatus.error(Messages.noProfileSelected);
		}
		TreeViewer profilesViewer = getProfilesViewer();
		Forms.updateMessageManager(getMessageManager(), profilesViewer, profilesViewer.getControl(), status, "selectedProfiles");
	}

	/**
	 * Loads the preference.
	 */
	protected void loadPreference() {
		if (versionCombo != null) {
			this.versionCombo.setInput(getFabric().getVersionsNode());
		}
		VersionNode v = this.versionNode;
		// we have to clear the selected version node because otherwise the
		// validator does not recognize the changed selection and therefore
		// the version has to be reselected even if its already selected to
		// get the validation done
		setAgentVersion(null);
		if (versionCombo != null) {
			versionCombo.getCombo().clearSelection();
			for (int i = 0; i < this.versionCombo.getCombo().getItemCount(); i++) {
				if (this.versionCombo.getElementAt(i).equals(v)) {
					this.versionCombo.getCombo().select(i);
					break;
				}
			}
		}
		setAgentVersion(v);
	}
}
