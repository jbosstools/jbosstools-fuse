/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.fabric8.ui.actions.jclouds;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.fusesource.ide.commons.ui.Selections;
import org.fusesource.ide.commons.util.Strings;
import org.jclouds.ContextBuilder;
import org.jclouds.apis.ApiMetadata;
import org.jclouds.aws.ec2.reference.AWSEC2Constants;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.Image;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.sshj.config.SshjSshClientModule;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * @author lhein
 */
public class SelectCloudImageDialog extends TitleAreaDialog {

	private CloudDetails 	selectedCloud;
	private String 			location;
	private String 			selectedImageId;
	
	private Text 	txt_search;
	private Label 	lbl_search;
	private Button 	btn_search;
	private ListViewer  imageViewer;
	private ProgressBar progressBar;
	
	private Job		job;
	private ImageListContentProvider imageContentProvider = new ImageListContentProvider();
	
	/**
	 * @param parentShell
	 */
	public SelectCloudImageDialog(Shell parentShell, CloudDetails cloudDetails, String location) {
		super(parentShell);
		this.selectedCloud = cloudDetails;
		this.location = location;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Cloud Image Selection...");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#create()
	 */
	@Override
	public void create() {
		super.create();
		setTitle("Select a cloud image...");
		setMessage("Please select the cloud image you want to use...", IMessageProvider.INFORMATION);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
	    
		Composite container = new Composite(area, SWT.NONE);
	    GridLayout layout = new GridLayout(3, false);
	    container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	    container.setLayout(layout);

	    // now create the controls
	    this.lbl_search = new Label(container, SWT.NONE);
	    this.lbl_search.setText("Search for: ");
	    this.lbl_search.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
	    
	    this.txt_search = new Text(container, SWT.SINGLE | SWT.BORDER);
	    this.txt_search.setToolTipText("Please enter a search term...");
	    this.txt_search.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
	    this.txt_search.addKeyListener(new KeyListener() {
			
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.keyCode == SWT.CR) {
					btn_search.setEnabled(false);
					txt_search.setEnabled(false);
	    			getButton(OK).setEnabled(false);
	    			lookupImages(txt_search.getText());	
				}
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
			}
		});
	    this.txt_search.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				validate();
				btn_search.setEnabled(txt_search.getText().trim().length()>0);
			}
		});
	    
	    this.btn_search = new Button(container, SWT.BORDER | SWT.PUSH);
	    this.btn_search.setText("Search");
	    this.btn_search.setToolTipText("Press this button to search for an image named similar to what you entered in the text field...");
	    this.btn_search.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
	    this.btn_search.setEnabled(false);
	    this.btn_search.addSelectionListener(new SelectionAdapter() {
	    	/* (non-Javadoc)
	    	 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	    	 */
	    	@Override
	    	public void widgetSelected(SelectionEvent e) {
    			btn_search.setEnabled(false);
    			txt_search.setEnabled(false);
    			getButton(OK).setEnabled(false);
    			lookupImages(txt_search.getText());	    			
	    	}
	    });
	    
	    this.imageViewer = new ListViewer(container, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL);
	    this.imageViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true, 3, 10));
	    ((GridData)this.imageViewer.getControl().getLayoutData()).minimumHeight = 300;
	    this.imageViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				validate();
			}
		});
	    this.imageViewer.setContentProvider(imageContentProvider);
	    this.imageViewer.setLabelProvider(imageContentProvider);
	    	    
	    this.progressBar = new ProgressBar(container, SWT.BORDER | SWT.SMOOTH | SWT.HORIZONTAL | SWT.INDETERMINATE);
	    this.progressBar.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, true, 3, 1));
	    
	    container.pack();
	    
	    this.progressBar.setVisible(false);
	    
	    return area;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		Control c = super.createContents(parent);
		validate();
		return c;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#isResizable()
	 */
	@Override
	protected boolean isResizable() {
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.TrayDialog#isHelpAvailable()
	 */
	@Override
	public boolean isHelpAvailable() {
		return true;
	}
	
	/**
	 * validates the language and the condition and sets or clears 
	 * the error messages
	 * 
	 * @return	true if all is fine
	 */
	private void validate() {
		if (Strings.isBlank(txt_search.getText().trim())) {
			setErrorMessage("Please enter a search term...");
			return;
		}

		if (getButton(OK) != null) getButton(OK).setEnabled(!imageViewer.getSelection().isEmpty());
		setErrorMessage(null);
	}
	
	/**
	 * does the actual image lookup
	 */
	private void lookupImages(final String searchTerm) {
		progressBar.setVisible(true);
		imageViewer.setSelection(null);
		imageViewer.setInput(new Image[0]);
		imageViewer.refresh();
		getDialogArea().redraw();
		
		this.job = new Job("Looking up images containing term '" + txt_search.getText() + "'...") {

			@Override
			public IStatus run(IProgressMonitor monitor) {
				final Image[] data = prepareImageListViewer(searchTerm);
				
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						imageViewer.setInput(data);						
						progressBar.setVisible(false);
						btn_search.setEnabled(true);
						txt_search.setEnabled(true);
						getDialogArea().redraw();
					}
				});
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	protected void okPressed() {
		this.selectedImageId = ((Image)Selections.getFirstSelection(this.imageViewer.getSelection())).getId();
		super.okPressed();
	}
	
	/**
	 * returns the id of the selected cloud image
	 * 
	 * @return
	 */
	public String getSelectedCloudImageId() {
		return this.selectedImageId;
	}
	
	/**
	 * does the lookup
	 */
	private Image[] prepareImageListViewer(String searchTerm) {
		if (Strings.isBlank(this.location)) return new Image[0];

		ComputeServiceContext context = null;
		
		Properties overrides = new Properties();

		overrides.put("provider", selectedCloud.getProviderId());
		overrides.put(CloudDetails.PROPERTY_IDENTITY, selectedCloud.getIdentity());
		overrides.put(CloudDetails.PROPERTY_CREDENTIAL, selectedCloud.getCredential());
		if (!Strings.isBlank(selectedCloud.getOwnerId())) {
			overrides.put(AWSEC2Constants.PROPERTY_EC2_AMI_QUERY, "owner-id=" + selectedCloud.getOwnerId() + ";state=available;image-type=machine;root-device-type=ebs;name=*" + searchTerm.trim() + "*");
		} else {
			overrides.put(AWSEC2Constants.PROPERTY_EC2_AMI_QUERY, "state=available;image-type=machine;root-device-type=ebs;name=*" + searchTerm.trim() + "*");
		}
		overrides.put(AWSEC2Constants.PROPERTY_EC2_CC_REGIONS, this.location);

		ProviderMetadata selectedProvider = selectedCloud.getProvider();
		ApiMetadata selectedApi = selectedCloud.getApi();
		if (selectedProvider != null && selectedProvider != JClouds.EMPTY_PROVIDER) {
			context = ContextBuilder.newBuilder(selectedProvider)
	                  .credentials(selectedCloud.getIdentity(), selectedCloud.getCredential())
	                  .overrides(overrides)
	                  .modules(ImmutableSet.<Module> of(new Log4JLoggingModule(),
	                                                    new SshjSshClientModule()))
	                  .buildView(ComputeServiceContext.class);
		} else if (selectedApi != null && selectedApi != JClouds.EMPTY_API) {
			context = ContextBuilder.newBuilder(selectedApi)
		                  .credentials(selectedCloud.getIdentity(), selectedCloud.getCredential())
		                  .overrides(overrides)
		                  .modules(ImmutableSet.<Module> of(new Log4JLoggingModule(),
		                                                    new SshjSshClientModule()))
		                  .buildView(ComputeServiceContext.class);
		} else {
			return new Image[0];
		}
		
		Set images = context.getComputeService().listImages();
		if (images == null || images.isEmpty() || images.iterator() == null) return new Image[0];
		ArrayList<Image> imgs = new ArrayList<Image>();
		Iterator it = JClouds.sortedList(images).iterator();
		while (it.hasNext()) {
			Object o = it.next();
			imgs.add((Image)o);
		}
		return imgs.toArray(new Image[imgs.size()]);
	}
	
	private class ImageListContentProvider  implements ILabelProvider, IStructuredContentProvider {

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
		 */
		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
		 */
		@Override
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof Image[]) {
				return ((Image[])inputElement);
			}
			return new Object[0];
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
		 */
		@Override
		public void addListener(ILabelProviderListener listener) {
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
		 */
		@Override
		public void dispose() {
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
		 */
		@Override
		public boolean isLabelProperty(Object element, String property) {
			return true;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
		 */
		@Override
		public void removeListener(ILabelProviderListener listener) {
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
		 */
		@Override
		public org.eclipse.swt.graphics.Image getImage(Object element) {
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
		 */
		@Override
		public String getText(Object element) {
			if (element instanceof Image) return ((Image)element).getName() + (((Image)element).getDescription().trim().length()>0 ? " (" + ((Image)element).getDescription() + ")" : "");
			return element.toString();
		}
	}
}
