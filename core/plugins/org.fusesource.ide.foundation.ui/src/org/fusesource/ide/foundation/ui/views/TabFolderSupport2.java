/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.foundation.ui.views;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.internal.views.properties.tabbed.view.TabbedPropertyRegistry;
import org.eclipse.ui.views.properties.PropertySheet;
import org.eclipse.ui.views.properties.tabbed.ISection;
import org.eclipse.ui.views.properties.tabbed.ITabDescriptor;
import org.eclipse.ui.views.properties.tabbed.ITabSelectionListener;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.eclipse.ui.views.properties.tabbed.TabContents;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.fusesource.ide.foundation.core.util.Objects;
import org.fusesource.ide.foundation.ui.internal.FoundationUIActivator;
import org.fusesource.ide.foundation.ui.util.PreferencesHelper;
import org.osgi.service.prefs.Preferences;


public abstract class TabFolderSupport2 extends TabbedPropertySheetPage {

	private static final String TAB_SELECTION_LABEL = "tabSelectionLabel";
	private final String contributorId;
	private List selectionQueue;
	private IViewSite viewSite;
	private PropertySheet propertySheet;

	public TabFolderSupport2(final String contributorId, boolean showTitle) {
		super(new ITabbedPropertySheetPageContributor() {

			@Override
			public String getContributorId() {
				return "org.fusesource.ide.commons.propertyContributor";
			}
		}, showTitle);
		this.contributorId = contributorId;

		// now lets replace the dodgy registry...
		TabbedPropertyRegistry registry = new TabbedPropertyRegistry(contributorId) {

			@Override
			protected ITabDescriptor[] getAllTabDescriptors() {
				return TabFolderSupport2.this.getTabDescriptors();
			}

			@Override
			protected IConfigurationElement[] getConfigurationElements(String extensionPointId) {
				IConfigurationElement[] configurationElements = super.getConfigurationElements(extensionPointId);
				if (configurationElements == null || configurationElements.length == 0) {
					configurationElements = new IConfigurationElement[] { new IConfigurationElement() {

						@Override
						public int getHandleId() {
							return contributorId.hashCode();
						}
						
						@Override
						public Object createExecutableExtension(String propertyName) throws CoreException {
							// TODO Auto-generated method stub
							return null;
						}

						@Override
						public String getAttribute(String name) throws InvalidRegistryObjectException {
							if (name.equals("contributorId")) {
								return contributorId;
							}
							return null;
						}

						@Override
						public String getAttribute(String attrName, String locale)
								throws InvalidRegistryObjectException {
							// TODO Auto-generated method stub
							return null;
						}

						@Override
						public String getAttributeAsIs(String name) throws InvalidRegistryObjectException {
							return null;
						}

						@Override
						public String[] getAttributeNames() throws InvalidRegistryObjectException {
							return new String[] { "contributorId" };
						}

						@Override
						public IConfigurationElement[] getChildren() throws InvalidRegistryObjectException {
							return new IConfigurationElement[0];
						}

						@Override
						public IConfigurationElement[] getChildren(String name) throws InvalidRegistryObjectException {
							return new IConfigurationElement[0];
						}

						@Override
						public IExtension getDeclaringExtension() throws InvalidRegistryObjectException {
							// TODO Auto-generated method stub
							return null;
						}

						@Override
						public String getName() throws InvalidRegistryObjectException {
							// TODO Auto-generated method stub
							return null;
						}

						@Override
						public Object getParent() throws InvalidRegistryObjectException {
							// TODO Auto-generated method stub
							return null;
						}

						@Override
						public String getValue() throws InvalidRegistryObjectException {
							// TODO Auto-generated method stub
							return null;
						}

						@Override
						public String getValue(String locale) throws InvalidRegistryObjectException {
							// TODO Auto-generated method stub
							return null;
						}

						@Override
						public String getValueAsIs() throws InvalidRegistryObjectException {
							// TODO Auto-generated method stub
							return null;
						}

						@Override
						public String getNamespace() throws InvalidRegistryObjectException {
							return null;
						}

						@Override
						public String getNamespaceIdentifier() throws InvalidRegistryObjectException {
							return null;
						}

						@Override
						public IContributor getContributor() throws InvalidRegistryObjectException {
							return null;
						}

						@Override
						public boolean isValid() {
							return true;
						}} };
				}
				return configurationElements;
			}



		};
		try {
			Objects.setField(this, "registry", registry, TabbedPropertySheetPage.class);
		} catch (Exception e) {
			FoundationUIActivator.pluginLog().logError("Failed to change the 'registry' field on " + this + ". " + e, e);
		}

		try {
			selectionQueue = (List) Objects.getField(this, "selectionQueue", TabbedPropertySheetPage.class);
		} catch (Exception e) {
			FoundationUIActivator.pluginLog().logError("Failed to get the 'selectionQueue' field on " + this + ". " + e, e);
		}
	}

	protected abstract ITabDescriptor[] getTabDescriptors();


	@Override
	protected IStructuredContentProvider getTabListContentProvider() {
		// TODO Auto-generated method stub
		return new IStructuredContentProvider() {

			@Override
			public void dispose() {
			}

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

			@Override
			public Object[] getElements(Object inputElement) {
				return getTabDescriptors();
			}};
	}

	protected String getId() {
		return contributorId;
	}

	public IViewSite getViewSite() {
		return viewSite;
	}

	public PropertySheet getPropertySheet() {
		return propertySheet;
	}

	public void init(PropertySheet propertySheet) {
		this.propertySheet = propertySheet;
		this.viewSite = propertySheet.getViewSite();
	}

	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		gotoPreviousSelectedTab();
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		try {
			super.selectionChanged(part, selection);
		} catch (Throwable e) {
			FoundationUIActivator.pluginLog().logWarning("Caught: " + e, e);
		}

		TabContents currentTab = getCurrentTab();
		if (currentTab != null) {
			ISection[] sections = currentTab.getSections();
			if (sections != null && sections.length > 0) {
				ISection section = sections[0];
				if (section instanceof ColumnViewSupport) {
					ColumnViewSupport cvs = (ColumnViewSupport) section;
					cvs.setSelectionProvider();
				}
			}
		}
	}

	protected void gotoPreviousSelectedTab() {
		final String id = getSelectedTabLabel();
		if (id != null) {
			selectionQueue.remove(id);
			selectionQueue.add(0, id);
		}

		addTabSelectionListener(new ITabSelectionListener() {

			@Override
			public void tabSelected(ITabDescriptor tabDescriptor) {
				if (tabDescriptor != null) {
					setSelectedTabLabel(tabDescriptor.getLabel());
				}
			}
		});
	}

	protected String getSelectedTabLabel() {
		Preferences node = getConfigurationNode();
		return node.get(TAB_SELECTION_LABEL, null);
	}

	protected void setSelectedTabLabel(String label) {
		if (label != null){
			Preferences node = getConfigurationNode();
			Object oldValue = node.get(TAB_SELECTION_LABEL, null);
			if (!Objects.equal(oldValue, label)) {
				node.put(TAB_SELECTION_LABEL, label);
				PreferencesHelper.flush(node);
			}
		}
	}

	protected Preferences getConfigurationNode() {
		return PreferencesHelper.configurationNode(getId(), "TabFolder");
	}
}