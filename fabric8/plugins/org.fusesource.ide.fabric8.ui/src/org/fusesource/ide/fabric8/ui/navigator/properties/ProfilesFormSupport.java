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

package org.fusesource.ide.fabric8.ui.navigator.properties;

import java.util.List;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.fusesource.ide.commons.Viewers;
import org.fusesource.ide.commons.ui.ICanValidate;
import org.fusesource.ide.commons.ui.Selections;
import org.fusesource.ide.commons.ui.Shells;
import org.fusesource.ide.commons.ui.Widgets;
import org.fusesource.ide.commons.ui.actions.ActionSupport;
import org.fusesource.ide.commons.ui.form.FormSupport;
import org.fusesource.ide.fabric8.core.dto.ProfileDTO;
import org.fusesource.ide.fabric8.ui.Messages;


public abstract class ProfilesFormSupport extends FormSupport {
	protected ListViewer featuresList;

	public static void createAddEditDeleteButtons(final ListEditor<String> editor, final ListViewer viewer, Composite buttonBar) {
		final ActionSupport addAction = new ActionSupport(editor.getAddButtonLabel(), editor.getAddButtonTooltip(), editor.getAddImageDescriptor()) {

			@Override
			public void run() {
				InputDialog dialog = new InputDialog(Shells.getShell(), editor.getAddDialogLabel(), editor.getAddDialogText(), "", null);
				int result = dialog.open();
				if (result == Window.OK) {
					String value = dialog.getValue();
					editor.addValue(value);
					viewer.setInput(editor.getList());
					Viewers.refresh(viewer);
					viewer.setSelection(new StructuredSelection(value));

				}
			}
		};
		final ActionSupport editAction = new ActionSupport(editor.getEditButtonLabel(), editor.getEditButtonTooltip(), editor.getEditImageDescriptor()) {

			@Override
			public void run() {
				final Object old = Selections.getFirstSelection(viewer);
				if (old != null) {
					InputDialog dialog = new InputDialog(Shells.getShell(), editor.getEditDialogLabel(), editor.getEditDialogText(), old.toString(), null);
					int result = dialog.open();
					if (result == Window.OK) {
						String value = dialog.getValue();
						editor.editValue(value,  old.toString());
						viewer.setInput(editor.getList());
						Viewers.refresh(viewer);
						viewer.setSelection(new StructuredSelection(value));
					}
				}
			}
		};


		Widgets.setDoubleClickAction(viewer, editAction);

		final ActionSupport deleteAction = new ActionSupport(editor.getDeleteButtonLabel(), editor.getDeleteButtonTooltip(), editor.getDeleteImageDescriptor()) {

			@Override
			public void run() {
				final Object old = Selections.getFirstSelection(viewer);
				if (old != null) {
					editor.removeValue(old.toString());
					viewer.setInput(editor.getList());
					Viewers.refresh(viewer);
				}
			}
		};

		final Button addButton = Widgets.createActionButton(buttonBar,  addAction);
		final Button editButton = Widgets.createActionButton(buttonBar,  editAction);
		final Button deleteButton = Widgets.createActionButton(buttonBar,  deleteAction);

		ISelectionChangedListener listener = new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				boolean selected = Selections.getFirstSelection(viewer) != null;
				editAction.setEnabled(selected);
				editButton.setEnabled(selected);
				deleteAction.setEnabled(selected);
				deleteButton.setEnabled(selected);

			}
		};
		viewer.addSelectionChangedListener(listener);
		listener.selectionChanged(new SelectionChangedEvent(viewer, new StructuredSelection()));
	}

	public ProfilesFormSupport() {
		super();
	}

	@Override
	protected boolean isMandatory(Object bean, String propertyName) {
		return false;
	}

	@Override
	public void setFocus() {
		if (featuresList != null) {
			featuresList.getControl().setFocus();
		}
	}

	@Override
	protected String getFormHeader() {
		return Messages.profilesForm_header;
	}

	protected void createProfileForm(final ProfileDTO profile, Composite inner) {
		ProfileBean bean = new ProfileBean(profile);

		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		inner.setLayout(layout);

		ListViewer featuresList = createBeanPropertyList(inner, bean, "features", Messages.profile_featuresLabel, Messages.profile_featuresTooltip,
				new ListEditor<String>(Messages.profile_addFeatureButtonLabel, Messages.profile_addFeatureButtonTooltip,
						Messages.profile_editFeatureButtonLabel, Messages.profile_editFeatureButtonTooltip,
						Messages.profile_deleteFeatureButtonLabel, Messages.profile_deleteFeatureButtonTooltip,
						Messages.profile_addFeatureDialogTitle, Messages.profile_addFeatureDialogText,
						Messages.profile_editFeatureDialogTitle, Messages.profile_editFeatureDialogText,
						Messages.profile_deleteFeatureDialogTitle) {

			@Override
			public List<String> getList() {
				return profile.getFeatures();
			}

			@Override
			public void setList(List<String> list) {
				profile.setFeatures(list);
			}
		});
		createBeanPropertyList(inner, bean, "repositories", Messages.profile_repositoriesLabel, Messages.profile_repositoriesTooltip,
				new ListEditor<String>(Messages.profile_addRepositoryButtonLabel, Messages.profile_addRepositoryButtonTooltip,
						Messages.profile_editRepositoryButtonLabel, Messages.profile_editRepositoryButtonTooltip,
						Messages.profile_deleteRepositoryButtonLabel, Messages.profile_deleteRepositoryButtonTooltip,
						Messages.profile_addRepositoryDialogTitle, Messages.profile_addRepositoryDialogText,
						Messages.profile_editRepositoryDialogTitle, Messages.profile_editRepositoryDialogText,
						Messages.profile_deleteRepositoryDialogTitle) {

			@Override
			public List<String> getList() {
				return profile.getRepositories();
			}

			@Override
			public void setList(List<String> list) {
				profile.setRepositories(list);
			}

		});
		createBeanPropertyList(inner, bean, "bundles", Messages.profile_bundlesLabel, Messages.profile_bundlesTooltip,
				new ListEditor<String>(Messages.profile_addBundleButtonLabel, Messages.profile_addBundleButtonTooltip,
						Messages.profile_editBundleButtonLabel, Messages.profile_editBundleButtonTooltip,
						Messages.profile_deleteBundleButtonLabel, Messages.profile_deleteBundleButtonTooltip,
						Messages.profile_addBundleDialogTitle, Messages.profile_addBundleDialogText,
						Messages.profile_editBundleDialogTitle, Messages.profile_editBundleDialogText,
						Messages.profile_deleteBundleDialogTitle) {

			@Override
			public List<String> getList() {
				return profile.getBundles();
			}

			@Override
			public void setList(List<String> list) {
				profile.setBundles(list);
			}
		});

		createBeanPropertyList(inner, bean, "fabs", Messages.profile_fabsLabel, Messages.profile_fabsTooltip,
				new ListEditor<String>(Messages.profile_addFabButtonLabel, Messages.profile_addFabButtonTooltip,
						Messages.profile_editFabButtonLabel, Messages.profile_editFabButtonTooltip,
						Messages.profile_deleteFabButtonLabel, Messages.profile_deleteFabButtonTooltip,
						Messages.profile_addFabDialogTitle, Messages.profile_addFabDialogText,
						Messages.profile_editFabDialogTitle, Messages.profile_editFabDialogText,
						Messages.profile_deleteFabDialogTitle) {

			@Override
			public List<String> getList() {
				return profile.getFabs();
			}

			@Override
			public void setList(List<String> list) {
				profile.setFabs(list);
			}
		});

		if (this.featuresList == null) {
			this.featuresList = featuresList;
		}
	}

	protected ListViewer createBeanPropertyList(Composite parent, Object bean, String propertyName, String labelText, String tooltip,
			final ListEditor<String> editor) {
		final ListViewer viewer = createBeanPropertyList(parent, bean, propertyName, labelText, tooltip, SWT.READ_ONLY | SWT.BORDER);
		viewer.setInput(editor.getList());
		//answer.setLabelProvider(JCloudsLabelProvider.getInstance());

		// now lets add the button bar...
		Composite buttonBar = new Composite(parent, SWT.NONE);

		GridData gridData = new GridData();
		gridData.horizontalSpan = 1;
		gridData.grabExcessHorizontalSpace = false;
		gridData.grabExcessVerticalSpace = false;
		gridData.horizontalAlignment = SWT.CENTER;
		buttonBar.setLayoutData(gridData);

		RowLayout layout = new RowLayout();
		layout.center = true;
		buttonBar.setLayout(layout);

		ProfilesForm.createAddEditDeleteButtons(editor, viewer, buttonBar);

		return viewer;
	}


	@Override
	public void okPressed() {
	}

	public ProfilesFormSupport(ICanValidate validator) {
		super(validator);
	}

}