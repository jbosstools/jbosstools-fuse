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

package org.fusesource.ide.launcher.ui.tabs;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.internal.ui.SWTFactory;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.m2e.actions.MavenLaunchConstants;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.FilteredResourcesSelectionDialog;
import org.fusesource.ide.launcher.debug.util.CamelDebugUtils;
import org.fusesource.ide.launcher.run.util.CamelContextLaunchConfigConstants;
import org.fusesource.ide.launcher.ui.Activator;

/**
 * @author lhein
 */
public class CamelContextFileTab extends AbstractLaunchConfigurationTab {
	
	private FilteredResourcesSelectionDialog browseDialog;
	private Text camelContextFileText;
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.AbstractLaunchConfigurationTab#getImage()
	 */
	@Override
	public Image getImage() {
		return Activator.getDefault().getImage("camel.png");
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		Composite c = SWTFactory.createComposite(parent, 1, 1, GridData.FILL_BOTH);
		setControl(c);
		Group group = SWTFactory.createGroup(c, "Select Camel Context file...", 2, 1, GridData.FILL_HORIZONTAL);
		this.camelContextFileText = SWTFactory.createSingleText(group, 1);
		this.camelContextFileText.addModifyListener(new ModifyListener() {
			/*
			 * (non-Javadoc)
			 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
			 */
			@Override
			public void modifyText(ModifyEvent e) {
				setDirty(true);
				updateLaunchConfigurationDialog();
			}
		});
		
		Button selectFileButton = SWTFactory.createPushButton(group, "Browse...", null);
		selectFileButton.addSelectionListener(new SelectionListener() {
			/*
			 * (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				IContainer container = ResourcesPlugin.getWorkspace().getRoot();
				browseDialog = new FilteredResourcesSelectionDialog(e.display.getActiveShell(), false, container, IResource.FILE) {
					
					/*
					 * (non-Javadoc)
					 * @see org.eclipse.ui.dialogs.FilteredResourcesSelectionDialog#fillContentProvider(org.eclipse.ui.dialogs.FilteredItemsSelectionDialog.AbstractContentProvider, org.eclipse.ui.dialogs.FilteredItemsSelectionDialog.ItemsFilter, org.eclipse.core.runtime.IProgressMonitor)
					 */
					@Override
					protected void fillContentProvider(final AbstractContentProvider contentProvider, ItemsFilter itemsFilter, org.eclipse.core.runtime.IProgressMonitor progressMonitor) throws CoreException {
						AbstractContentProvider filteringContentProvider = new AbstractContentProvider() {
							/*
							 * (non-Javadoc)
							 * @see org.eclipse.ui.dialogs.FilteredItemsSelectionDialog.AbstractContentProvider#add(java.lang.Object, org.eclipse.ui.dialogs.FilteredItemsSelectionDialog.ItemsFilter)
							 */
							@Override
							public synchronized void add(Object item, ItemsFilter filter) {
								if (filter.matchItem(item)) {
									if (item instanceof IFile) {
										IFile ifile = (IFile) item;
										IContentTypeManager contentTypeManager = Platform.getContentTypeManager();
										InputStream stream = null;
										try {
											stream = ifile.getContents(true);
											IContentType t = contentTypeManager.findContentTypeFor(stream, ifile.getName());
											if (t.getId().equals("org.fusesource.ide.camel.editor.camelContentType")) {
												contentProvider.add(item, filter);
											}
										} catch (Exception ex) {
											Activator.getLogger().error(ex);
										} finally {
											if (stream != null) {
												try {
													stream.close();
												} catch (IOException ex) {
													Activator.getLogger().error(ex);
												}
											}
										}										
									}
								}
							}
						};
						super.fillContentProvider(filteringContentProvider, itemsFilter, progressMonitor);
					};
				};
				browseDialog.setInitialPattern("*.xml", FilteredResourcesSelectionDialog.FULL_SELECTION);
				browseDialog.setBlockOnOpen(true);
				browseDialog.open();
				if (browseDialog.getReturnCode() == FilteredResourcesSelectionDialog.OK) {
					// get the result of selection
					Object[] res = browseDialog.getResult();
					IFile f = (IFile)res[0];
					camelContextFileText.setText(f.getLocation().toOSString());
				}
				updateLaunchConfigurationDialog();
			}
			
			/*
			 * (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);				
			}
		});
		Dialog.applyDialogFont(parent);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#setDefaults(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 */
	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(CamelContextLaunchConfigConstants.ATTR_FILE, CamelContextLaunchConfigConstants.DEFAULT_CONTEXT_NAME);
		configuration.setAttribute(MavenLaunchConstants.ATTR_GOALS, "clean package org.apache.camel:camel-maven-plugin:run");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#initializeFrom(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		String fileName = CamelDebugUtils.getRawCamelContextFilePathFromLaunchConfig(configuration);
		if (fileName == null) fileName = CamelContextLaunchConfigConstants.DEFAULT_CONTEXT_NAME;
		camelContextFileText.setText(fileName);
		updateLaunchConfigurationDialog();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#performApply(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 */
	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(CamelContextLaunchConfigConstants.ATTR_FILE, camelContextFileText.getText().trim());
		setDirty(false);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getName()
	 */
	@Override
	public String getName() {
		return "Camel";
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.AbstractLaunchConfigurationTab#isValid(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	@Override
	public boolean isValid(ILaunchConfiguration launchConfig) {
		IPath file = new Path(camelContextFileText.getText());
		if (file.toFile().exists() && file.toFile().isFile()) {
			setErrorMessage(null);
			return true;
		} else {
			setErrorMessage("Please select the Camel context file you want to start.");
			return false;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.AbstractLaunchConfigurationTab#canSave()
	 */
	@Override
	public boolean canSave() {
		return isValid(null);
	}
}
