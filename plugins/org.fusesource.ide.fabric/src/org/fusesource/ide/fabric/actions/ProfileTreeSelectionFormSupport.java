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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.fusesource.fabric.api.Profile;
import org.fusesource.ide.commons.Viewers;
import org.fusesource.ide.commons.ui.ICanValidate;
import org.fusesource.ide.commons.ui.Selections;
import org.fusesource.ide.commons.ui.form.FormSupport;
import org.fusesource.ide.fabric.navigator.ProfileNode;
import org.fusesource.ide.fabric.navigator.Profiles;
import org.fusesource.ide.fabric.navigator.VersionNode;

import com.google.common.collect.Lists;

public abstract class ProfileTreeSelectionFormSupport extends FormSupport {

	private List<Profile> selectedProfiles;
	private TreeViewer profilesViewer;
	private Button selectAllButton;
	private Button deselectAllButton;
	private List<Profile> initialProfileSelections;

	public ProfileTreeSelectionFormSupport() {
		super();
	}

	public ProfileTreeSelectionFormSupport(ICanValidate validator) {
		super(validator);
	}

	@Override
	public boolean isValid() {
		return super.isValid() && (hasCheckedProfiles() || !isSelectProfile());
	}

	/*
	public List<Profile> getSelectedProfiles() {
		return selectedProfiles;
	}

	public void setSelectedProfiles(List<Profile> selectedProfiles) {
		List<Profile> oldValue = this.selectedProfiles;
		this.selectedProfiles = selectedProfiles;
		firePropertyChange("selectedProfiles", oldValue, selectedProfiles);
	}
	 */

	public TreeViewer getProfilesViewer() {
		return profilesViewer;
	}

	/**
	 * Creates the columns viewer.
	 * 
	 * @param parent
	 *            The parent composite
	 */
	public void createColumnsViewer(Composite parent) {
		/*
		Label label = getToolkit().createLabel(parent, Messages.profileViewerLabel);
		label.setLayoutData(gridData);
		 */
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 1;

		profilesViewer = new TreeViewer(parent, SWT.BORDER
				| SWT.MULTI | SWT.FULL_SELECTION);
		profilesViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		/*
		profilesViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ColumnConfiguration) {
					ColumnConfiguration config = (ColumnConfiguration) element;
					return config.getName();
				}
				return super.getText(element);
			}});
		 */
		profilesViewer.setContentProvider(new ProfilesContentProvider());

		Control profilesControl = profilesViewer.getControl();
		getToolkit().adapt(profilesControl, true, true);

		profilesViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				onProfileSelectionChanged();

			}
		});
		//validateProfiles();
	}

	public void setProfilesViewerInput(Object input) {
		if (profilesViewer != null) {
			profilesViewer.setInput(input);
			profilesViewer.expandAll();
			if (initialProfileSelections != null) {
				setCheckedProfiles(initialProfileSelections.toArray(new Profile[initialProfileSelections.size()]));
				//profilesViewer.setSelection(new StructuredSelection(initialProfileSelections));
				//initialProfileSelections = null;
			}
		}
	}


	public void setInitialProfileSelections(Profile... profiles) {
		this.initialProfileSelections = Arrays.asList(profiles);
	}

	protected void validateProfiles() {
	}

	public boolean hasCheckedProfiles() {
		return getSelectedProfileList().size() > 0;
	}

	/**
	 * Creates the buttons.
	 * 
	 * @param parent
	 *            The parent composite
	 */
	protected void createButtons(Composite parent) {
		Composite composite = createButtonComposite(parent);
		/*

		selectAllButton = getToolkit().createButton(composite, Messages.selectAllLabel, SWT.PUSH);
		setButtonLayoutData(selectAllButton);
		selectAllButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				profilesViewer.setAllChecked(true);
			}
		});

		deselectAllButton = getToolkit().createButton(composite, Messages.deselectAllLabel, SWT.PUSH);
		setButtonLayoutData(deselectAllButton);
		deselectAllButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				profilesViewer.setAllChecked(false);
			}
		});
		 */
	}

	protected Composite createButtonComposite(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		composite.setLayout(layout);
		return composite;
	}

	public void setCheckedProfiles(Profile... selected) {
		// now lets find the selected ProfileNodes
		List<ProfileNode> selectedProfiles = new ArrayList<ProfileNode>();
		Object input = profilesViewer.getInput();
		if (input instanceof VersionNode) {
			VersionNode node = (VersionNode) input;
			for (Profile profile : selected) {
				ProfileNode profileNode = node.getProfileNode(profile);
				if (profileNode != null) {
					selectedProfiles.add(profileNode);
				}
			}
		}
		profilesViewer.setSelection(new StructuredSelection(selectedProfiles));
		Viewers.async(new Runnable() {

			@Override
			public void run() {
				onProfileSelectionChanged();
			}});
	}

	public List<Profile> getSelectedProfileList() {
		List<Profile> answer = Lists.newArrayList();
		if (profilesViewer != null) {
			List<Object> list = Selections.getSelectionList(profilesViewer);
			for (Object object : list) {
				Profile profile = Profiles.toProfile(object);
				if (profile != null) {
					answer.add(profile);
				}
			}
		}
		return answer;
	}

	public Profile[] getSelectedProfileArray() {
		List<Profile> selectedProfiles = getSelectedProfileList();
		Profile[] profiles = selectedProfiles.toArray(new Profile[selectedProfiles.size()]);
		return profiles;
	}

	protected boolean isSelectProfile() {
		return true;
	}

	protected void onProfileSelectionChanged() {
		validateProfiles();
		validate();
	}



}