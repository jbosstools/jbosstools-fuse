/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.project.camel.ui;

import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.common.componentcore.datamodel.FacetInstallDataModelProvider;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetDataModelProperties;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetProjectCreationDataModelProperties;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetProjectCreationDataModelProperties.FacetDataModelMap;
import org.eclipse.wst.common.frameworks.datamodel.DataModelEvent;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelListener;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.ui.AbstractFacetWizardPage;
import org.eclipse.wst.common.project.facet.ui.IFacetWizardPage;
import org.fusesource.ide.project.Messages;
import org.fusesource.ide.project.camel.ICamelFacetDataModelProperties;

public class CamelFacetInstallationPage extends AbstractFacetWizardPage implements IFacetWizardPage, ICamelFacetDataModelProperties {

	private Text contentFolder;
	private Label contentRootLabel;
	private Button createBlueprintDescriptor;
	
	
	private IDataModel model;
	public CamelFacetInstallationPage() {
		super( "camel.facet.install.page"); //$NON-NLS-1$
		setTitle(Messages.NewCamelProject_FacetInstallationPage);
		setDescription(Messages.NewCamelProject_FacetInstallationPageDesc);
		
	}

	public void setConfig(Object config) {
		System.out.println("config = " + config);
		System.out.println("set config");
		this.model = (IDataModel)config;
		IDataModel o =(IDataModel) model.getProperty(FacetInstallDataModelProvider.MASTER_PROJECT_DM);
		//FacetDataModelMap map = (FacetDataModelMap) model.getProperty(IFacetProjectCreationDataModelProperties.FACET_DM_MAP);
		if( o == null ) {
			// This is likely a pre-existing project having the camel facet added. We should avoid customizing project structure
			model.setProperty(UPDATE_PROJECT_STRUCTURE, false);
		}
		if( o != null ) {
			// This is a new camel project. We can customize the structure all we want. 
			model.setProperty(UPDATE_PROJECT_STRUCTURE, true);
			o.addListener(new IDataModelListener() {
				public void propertyChanged(DataModelEvent event) {
					System.out.println("Property changed");
					if( IFacetProjectCreationDataModelProperties.FACET_ACTION_MAP.equals(event.getPropertyName())) {
						updateWidgetsFromModel();
					}
				}
			});
		}
	}

	public void createControl(Composite parent) {
		setControl(createTopLevelComposite(parent));
	}
	
	private void changePageStatus(){
		boolean webfound = hasWebFacet();
		boolean shouldUpdateStructure = model.getBooleanProperty(UPDATE_PROJECT_STRUCTURE);
		boolean displayContentFolder = webfound || shouldUpdateStructure;

		if(displayContentFolder && !validFolderName(contentFolder.getText())){
			setErrorMessage(Messages.NewCamelProject_FacetInstallationPage_ContentRootError);
		} else {
			setErrorMessage(null);
		}
		setPageComplete(isPageComplete());
	}
	
	private boolean validFolderName(String folderName) {
		IWorkspace ws = ResourcesPlugin.getWorkspace();
		return ws.validateName(folderName, IResource.FOLDER).isOK();
	}

	
	protected Composite createTopLevelComposite(Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		createProjectGroup(composite);
//		//set page status
		changePageStatus();
		
		return composite;
	}

	private void createProjectGroup(Composite parent){
		Composite prjGroup = new Composite(parent, SWT.NONE);
		prjGroup.setLayout(new GridLayout(1, false));
		prjGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		
		
		this.contentRootLabel = new Label(prjGroup, SWT.NONE);
		this.contentRootLabel.setText(Messages.NewCamelProject_ContentRootLabel);
		this.contentRootLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		this.contentFolder = new Text(prjGroup, SWT.BORDER);
		this.contentFolder.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.contentFolder.setData("label", this.contentRootLabel); //$NON-NLS-1$ // wtf??
		this.contentFolder.setText(model.getStringProperty(CAMEL_CONTENT_FOLDER));
		contentFolder.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e){
				 String content = contentFolder.getText();
				 if(content != null && !content.equals("")){ //$NON-NLS-1$
					 model.setProperty(CAMEL_CONTENT_FOLDER, content);
				 }
				 changePageStatus();
			}
		});
		this.createBlueprintDescriptor = new Button(prjGroup, SWT.CHECK);
		this.createBlueprintDescriptor.setText("Create blueprint descriptor");
		createBlueprintDescriptor.addSelectionListener( new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				boolean selection = createBlueprintDescriptor.getSelection();
				model.setProperty(CREATE_BLUEPRINT_DESCRIPTOR, selection);
			}
		});
		
		updateWidgetsFromModel();
	}
	
	
	private boolean hasWebFacet() {
		boolean webfound = false;
		IFacetedProjectWorkingCopy wc = (IFacetedProjectWorkingCopy) model.getProperty(IFacetDataModelProperties.FACETED_PROJECT_WORKING_COPY);
		Set<IProjectFacetVersion> enabled = wc.getProjectFacets();
		Iterator<IProjectFacetVersion> it = enabled.iterator();
		while(it.hasNext()) {
			IProjectFacetVersion i = it.next();
			String id = i.getProjectFacet().getId();
			if( i.getProjectFacet().getId().equals("jst.web")) {
				webfound = true;
			}
		}
		return webfound;
	}
	
	private void updateWidgetsFromModel() {
		boolean uiLoaded = createBlueprintDescriptor != null;
		if( uiLoaded ) {
			boolean webfound = hasWebFacet();
			boolean shouldUpdateStructure = model.getBooleanProperty(UPDATE_PROJECT_STRUCTURE);
			boolean displayContentFolder = webfound || shouldUpdateStructure;
			if( !displayContentFolder) {
				model.setProperty(CAMEL_CONTENT_FOLDER, null);
				contentFolder.setText("");
			} else {
				contentFolder.setText("camelcontent");
			}
			contentFolder.setEnabled(displayContentFolder);
			contentFolder.setVisible(displayContentFolder);
			contentRootLabel.setVisible(displayContentFolder);
		}
	}
}