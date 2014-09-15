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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.fusesource.ide.commons.Viewers;
import org.fusesource.ide.commons.ui.Selections;
import org.fusesource.ide.fabric8.core.dto.ProfileDTO;
import org.fusesource.ide.fabric8.core.dto.ProfileRequirementsDTO;
import org.fusesource.ide.fabric8.core.dto.RequirementsDTO;
import org.fusesource.ide.fabric8.ui.actions.ProfileTreeSelectionFormSupport;
import org.fusesource.ide.fabric8.ui.navigator.ProfileNode;
import org.fusesource.ide.fabric8.ui.navigator.ProfileParentsContentProvider;


/**
 * @author lhein
 */
public class ProfileRequirementsForm extends ProfilesFormSupport {

	private final ProfileNode node;
	private RequirementsBean formModel = new RequirementsBean();
	private Text minInstances;
	private Text maxInstances;

	private ProfileRequirementsDTO pReqs;
	
	private IObservableValue minInstancesTextObservable;
	private IObservableValue minInstancesModelObservable;
	private IObservableValue maxInstancesTextObservable;
	private IObservableValue maxInstancesModelObservable;
	
	private Binding minInstancesBinding;
	private Binding maxInstancesBinding;	
	
	private PropertyChangeListener modelListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			saveRequirements();
		}
	};
	
	private FocusListener focusListener = new FocusListener() {
		@Override
		public void focusLost(FocusEvent e) {
			focusGained(e);
		}
		
		@Override
		public void focusGained(FocusEvent e) {
			minInstancesBinding.validateTargetToModel();
			maxInstancesBinding.validateTargetToModel();
		}
	};
	
	
	// lets add the parent selection...
	ProfileTreeSelectionFormSupport parentsForm = new ProfileTreeSelectionFormSupport() {

		@Override
		public void setFocus() {
			getProfilesViewer().getControl().setFocus();
		}

		@Override
		protected void createTextFields(Composite parent) {
			// TODO Auto-generated method stub
		}

		@Override
		protected void onProfileSelectionChanged() {
			super.onProfileSelectionChanged();
			okPressed();
		}

		@Override
		public void okPressed() {
			ProfileDTO[] profiles = getSelectedProfileArray();
			node.getProfile().setParents(Arrays.asList(profiles));
			// TODO we should reload the parent nodes now!
		}

		@Override
		public FormToolkit getToolkit() {
			return ProfileRequirementsForm.this.getToolkit();
		}
	};
		
	public ProfileRequirementsForm(ProfileNode node) {
		super();
		this.node = node;
	}
	
	private void saveRequirements() {
		RequirementsDTO reqs = this.node.getFabric().getFabricService().getRequirements();
		
		try {
			if (formModel.getMinInstances() == null &&
				formModel.getMaxInstances() == null && 
				formModel.getDependencies().size()==0) {
				// no requirements at all
				if (reqs.findProfileRequirements(this.node.getProfile().getId()) == null) {
					// there is no requirement so we don't save anything
					return;
				} else {
					// there was a requirement but user wants to delete it
					reqs.removeProfileRequirements(this.node.getProfile().getId());
					return;
				}
			}
			
			pReqs = reqs.getOrCreateProfileRequirement(this.node.getProfile().getId());
			pReqs.setMinimumInstances(this.formModel.getMinInstances());
			pReqs.setMaximumInstances(this.formModel.getMaxInstances());
			// create the id list
			List<String> profileDepList = new ArrayList<String>();
			//fill the list
			for (ProfileDTO p : formModel.getDependencies()) {
				profileDepList.add(p.getId());
			}
			// assign it
			pReqs.setDependentProfiles(profileDepList);
			// then save it
			reqs.addOrUpdateProfileRequirements(pReqs);
		} finally {
			this.node.getFabric().getFabricService().setRequirements(reqs);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.commons.ui.form.FormSupport#createTextFields(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createTextFields(Composite parent) {
		ProfileDTO profile = node.getProfile();
		String sectionTitle = profile.getId();

		Composite inner = createSectionComposite(sectionTitle, new GridData(GridData.FILL_BOTH));

		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		inner.setLayout(layout);
		
		createLabel(inner, "Minimum Instances");
		GridData data = new GridData(SWT.LEFT, SWT.TOP, true, false, 2, 1);
		data.minimumWidth = 60;
		minInstances = createText(inner, SWT.RIGHT | SWT.BORDER);
		minInstances.setLayoutData(data);
		minInstances.addFocusListener(focusListener);
		
		createLabel(inner, "Maximum Instances");
		data = new GridData(SWT.LEFT, SWT.TOP, true, false, 2, 1);
		data.minimumWidth = 60;
		maxInstances = createText(inner, SWT.RIGHT | SWT.BORDER);
		maxInstances.setLayoutData(data);
		maxInstances.addFocusListener(focusListener);
		
		createLabel(inner, "Dependencies");
		parentsForm.createColumnsViewer(inner);
		parentsForm.setProfilesViewerInput(node.getVersionNode());
		parentsForm.getProfilesViewer().setContentProvider(new ProfileParentsContentProvider(node));

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		parentsForm.getProfilesViewer().getTree().setLayoutData(gridData);
		parentsForm.getProfilesViewer().addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				processDependenciesChange(Selections.getSelectionList(parentsForm.getProfilesViewer()));
			}
		});

		createLabel(inner, "");
		
		Button clearSelectionBtn = new Button(inner, SWT.PUSH | SWT.BORDER);
		data = new GridData(SWT.LEFT, SWT.TOP, true, false, 2, 1);
		clearSelectionBtn.setLayoutData(data);
		clearSelectionBtn.setText("Deselect All");
		clearSelectionBtn.setToolTipText("Clears the selection...");
		clearSelectionBtn.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				parentsForm.getProfilesViewer().setSelection(new StructuredSelection());
				Viewers.refreshAsync(parentsForm.getProfilesViewer());
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);				
			}
		});
		
		bindValues();
		init();
		this.formModel.addPropertyChangeListener("minInstances", modelListener);
		this.formModel.addPropertyChangeListener("maxInstances", modelListener);
		this.formModel.addPropertyChangeListener("dependencies", modelListener);
	}
	
	private void processDependenciesChange(List dependencies) {
		this.formModel.clearDependencies();
		for (Object o : dependencies) {
			ProfileNode p = (ProfileNode)o;
			this.formModel.addDependency(p.getProfile());
		}
	}
	
	private void init() {
		RequirementsDTO reqs = this.node.getFabric().getFabricService().getRequirements();
		pReqs = reqs.findProfileRequirements(this.node.getProfile().getId());
		if (pReqs != null) {
			this.formModel.setMinInstances(pReqs.getMinimumInstances());
			this.formModel.setMaxInstances(pReqs.getMaximumInstances());
			for (String pId : pReqs.getDependentProfiles()) {
				ProfileDTO p = node.getFabric().getFabricService().getProfile(this.node.getVersionNode().getVersionId(), pId);
				this.formModel.addDependency(p);	
			}
		}
		parentsForm.setCheckedProfiles(formModel.getDependencies());
	}
	
	private void bindValues() {
		// The DataBindingContext object will manage the databindings
		// Lets bind it
		DataBindingContext ctx = new DataBindingContext();
				
		minInstancesTextObservable = WidgetProperties.text(SWT.Modify).observe(this.minInstances);
		minInstancesModelObservable = BeanProperties.value("minInstances").observe(this.formModel);
		
		maxInstancesTextObservable = WidgetProperties.text(SWT.Modify).observe(this.maxInstances);
		maxInstancesModelObservable = BeanProperties.value("maxInstances").observe(this.formModel);
		
		// Add a validator for number only
		IValidator optionalNumbersOnlyValidator = new IValidator() {
			@Override
			public IStatus validate(Object value) {
				String s = String.valueOf(value);
				
				if (s.trim().length()<1) return ValidationStatus.ok();
				if (s.matches("\\d*")) return ValidationStatus.ok();

				return ValidationStatus.error("Please enter a valid number of instances...");
			}
		};

		// now the validator for consistancy
		IValidator instancesValidator = new IValidator() {
			@Override
			public IStatus validate(Object value) {
				String smin = (String)minInstancesTextObservable.getValue();
				String smax = (String)maxInstancesTextObservable.getValue();
				Integer min = smin.trim().length()>0 ? Integer.parseInt(smin) : null;
				Integer max = smax.trim().length()>0 ? Integer.parseInt(smax) : null;
								
				if (min != null && max != null) {
					// check if valid
					if (min > max) return ValidationStatus.error("The maximum instances count can't be lower than the minimum count!");
				}
				
				return ValidationStatus.ok();
			}
		};
		
		

		UpdateValueStrategy targetToModel = new UpdateValueStrategy(false,  UpdateValueStrategy.POLICY_UPDATE);
		targetToModel.setAfterGetValidator(optionalNumbersOnlyValidator);
		targetToModel.setBeforeSetValidator(instancesValidator);
		targetToModel.setConverter(new IConverter() {
			@Override
			public Object getToType() {
				return new BigInteger("-1");
			}
			
			@Override
			public Object getFromType() {
				return new String();
			}
			
			@Override
			public Object convert(Object fromObject) {
				if (fromObject instanceof String) {
					String s = (String)fromObject;
					if (s.trim().length() > 0) {
						try {
							return new BigInteger(s);
						} catch (NumberFormatException ex) {
							// no number
						}
					}
				}
				return null;
			}
		});
		
		UpdateValueStrategy modelToTarget = new UpdateValueStrategy();
		
		minInstancesBinding = ctx.bindValue(minInstancesTextObservable, minInstancesModelObservable, targetToModel, modelToTarget);
		ControlDecorationSupport.create(minInstancesBinding, SWT.TOP | SWT.RIGHT);
		
		maxInstancesBinding = ctx.bindValue(maxInstancesTextObservable, maxInstancesModelObservable, targetToModel, modelToTarget);
		ControlDecorationSupport.create(maxInstancesBinding, SWT.TOP | SWT.RIGHT);
	}

	@Override
	protected Label createLabel(Composite inner, String text) {
		Label label = super.createLabel(inner, text);
		GridData data = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		label.setLayoutData(data);
		return label;
	}

	@Override
	public void setFocus() {
		minInstances.setFocus();
	}
	
	/**
	 * model class used for validating the form contents
	 * 
	 * @author lhein
	 */
	private class RequirementsBean implements PropertyChangeListener {
		private BigInteger minInstances;
		private BigInteger maxInstances;
		private List<ProfileDTO> dependencies = new ArrayList<ProfileDTO>();
		private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
		
		/**
		 * 
		 */
		public RequirementsBean() {
		}

		/**
		 * @return the minInstances
		 */
		public BigInteger getMinInstances() {
			return this.minInstances;
		}

		/**
		 * @param minInstances the minInstances to set
		 */
		public void setMinInstances(BigInteger minInstances) {
			propertyChangeSupport.firePropertyChange("minInstances", this.minInstances, this.minInstances = minInstances);
		}

		/**
		 * @return the maxInstances
		 */
		public BigInteger getMaxInstances() {
			return this.maxInstances;
		}

		/**
		 * @param maxInstances the maxInstances to set
		 */
		public void setMaxInstances(BigInteger maxInstances) {
			propertyChangeSupport.firePropertyChange("maxInstances", this.maxInstances, this.maxInstances = maxInstances);
		}

		/**
		 * @return the dependencies
		 */
		public List<ProfileDTO> getDependencies() {
			return this.dependencies;
		}
		
		public void clearDependencies() {
			this.dependencies.clear();
			propertyChangeSupport.firePropertyChange("dependencies", null, this.dependencies);
		}

		public void addDependency(ProfileDTO p) {
			if (this.dependencies.contains(p)) return;
			this.dependencies.add(p);
			propertyChangeSupport.firePropertyChange("dependencies", null, this.dependencies);
		}
		
		public void removeDependency(ProfileDTO p) {
			if (!this.dependencies.contains(p)) return;
			this.dependencies.remove(p);
			propertyChangeSupport.firePropertyChange("dependencies", null, this.dependencies);
		}
		
		public void addPropertyChangeListener(String propertyName,
				PropertyChangeListener listener) {
			propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
		}

		public void removePropertyChangeListener(PropertyChangeListener listener) {
			propertyChangeSupport.removePropertyChangeListener(listener);
		}
		
		/* (non-Javadoc)
		 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
		 */
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			propertyChangeSupport.firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
		}
	}
}
