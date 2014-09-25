/*******************************************************************************
* Copyright (c) 2014 Red Hat, Inc.
* Distributed under license by Red Hat, Inc. All rights reserved.
* This program is made available under the terms of the
* Eclipse Public License v1.0 which accompanies this distribution,
* and is available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
* Red Hat, Inc. - initial API and implementation
* William Collins punkhornsw@gmail.com
******************************************************************************/ 
package org.fusesource.ide.sap.ui.view;

import java.io.IOException;
import java.util.Collection;
import java.util.EventObject;
import java.util.HashMap;

import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CommandStack;
import org.eclipse.emf.common.command.CommandStackListener;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;
import org.eclipse.emf.edit.ui.action.RedoAction;
import org.eclipse.emf.edit.ui.action.UndoAction;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.fusesource.camel.component.sap.model.rfc.SapConnectionConfiguration;
import org.fusesource.camel.component.sap.util.ComponentDestinationDataProvider;
import org.fusesource.camel.component.sap.util.ComponentServerDataProvider;
import org.fusesource.ide.sap.ui.Activator;
import org.fusesource.ide.sap.ui.Messages;
import org.fusesource.ide.sap.ui.edit.command.TransactionalCommandStack;
import org.fusesource.ide.sap.ui.edit.idoc.IdocItemProviderAdapterFactory;
import org.fusesource.ide.sap.ui.edit.rfc.RfcItemProviderAdapterFactory;
import org.fusesource.ide.sap.ui.util.ModelUtil;

/**
 * Sap Connections View
 */

@SuppressWarnings("restriction")
public class SapConnectionsView extends ViewPart implements ISelectionChangedListener, ITabbedPropertySheetPageContributor, IEditingDomainProvider {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.fusesource.ide.sap.ui.view.SapConnectionsView"; //$NON-NLS-1$

	/**
	 * This keeps track of the editing domain that is used to track all changes
	 * to the model.
	 */
	protected AdapterFactoryEditingDomain editingDomain;

	/**
	 * This is the one adapter factory used for providing views of the model.
	 */
	protected ComposedAdapterFactory adapterFactory;

	/**
	 * The configuration object containing connection configurations to SAP
	 */
	private SapConnectionConfiguration sapConnectionConfiguration;

	/**
	 * This listens to to viewer.
	 */
	protected ISelectionChangedListener selectionChangedListener;

	/**
	 * The viewer for view.
	 */
	private TreeViewer viewer;

	/**
	 * This is the property sheet page.
	 * 
	 * @generated
	 */
	protected TabbedPropertySheetPage propertySheetPage;
	
	  /**
	   * This is the action used to implement undo.
	   */
	  protected UndoAction undoAction;

	  /**
	   * This is the action used to implement redo.
	   */
	  protected RedoAction redoAction;

	  protected IStructuredSelection selection;

	

	/**
	 * The constructor.
	 */
	public SapConnectionsView() {
		initializeEditingDomain();
	}
	
	@Override
	public String getContributorId() {
		return ID;
	}

	public AdapterFactoryEditingDomain getEditingDomain() {
		return editingDomain;
	}
	
	public SapConnectionConfiguration getSapConnectionConfiguration() {
		return sapConnectionConfiguration;
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		createModel();

		viewer = new TreeViewer(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new AdapterFactoryContentProvider(adapterFactory));
		viewer.setLabelProvider(new AdapterFactoryLabelProvider(adapterFactory));
		viewer.setInput(sapConnectionConfiguration.eResource());
		viewer.addSelectionChangedListener(this);
		viewer.expandAll();

		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem()
				.setHelp(viewer.getControl(), "org.jboss.jca.adapters.sap.model.editor.viewer"); //$NON-NLS-1$

		initActions(getViewSite().getActionBars());
		
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();

		getSite().setSelectionProvider(viewer);
	}

	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		if (adapter.equals(IPropertySheetPage.class)) {
			return getPropertySheetPage();
		} else {
			return super.getAdapter(adapter);
		}
	}

	@Override
	public void dispose() {
		// Save all changes to resource set.
		for (Resource resource : editingDomain.getResourceSet().getResources()) {
			try {
				resource.save(null);
			} catch (IOException e) {
				Activator.getLogger().warning(Messages.SapConnectionsView_ErrorWhenSavingViewState, e);
			}
		}

		if (propertySheetPage != null) {
			propertySheetPage.dispose();
		}

		// Unregister data stores
		ComponentDestinationDataProvider.INSTANCE.removeDestinationDataStore(sapConnectionConfiguration.getDestinationDataStore());
		ComponentServerDataProvider.INSTANCE.removeServerDataStore(sapConnectionConfiguration.getServerDataStore());
		
	}

	public void setSelectionToViewer(Collection<?> collection) {
		final Collection<?> theSelection = collection;

		if (theSelection != null && !theSelection.isEmpty()) {
			Runnable runnable = new Runnable() {
				public void run() {
					viewer.setSelection(new StructuredSelection(theSelection.toArray()), true);
				}
			};
			getSite().getShell().getDisplay().asyncExec(runnable);
		}
	}

	public IPropertySheetPage getPropertySheetPage() {
		if (propertySheetPage == null) {
			propertySheetPage = new TabbedPropertySheetPage(this) {
				@Override
				public void setActionBars(IActionBars actionBars) {
					super.setActionBars(actionBars);
				    actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(), undoAction);
				    actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(), redoAction);
				}
			};
		}

		return propertySheetPage;
	}

	private void hookContextMenu() {
		MenuManager menuManager = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuManager.setRemoveAllWhenShown(true);

		menuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				SapConnectionsView.this.fillContextMenu(manager);
			}
		});

		Menu menu = menuManager.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuManager, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
	}

	private void fillContextMenu(IMenuManager menuManager) {

		menuManager.add(new Separator("edit")); //$NON-NLS-1$

		menuManager.add(new Separator("additions")); //$NON-NLS-1$
		menuManager.add(new Separator("additions-end")); //$NON-NLS-1$

	}

	private void fillLocalToolBar(IToolBarManager manager) {
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {

			}
		});
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		selection = event.getSelection() instanceof IStructuredSelection ? (IStructuredSelection) event.getSelection() : StructuredSelection.EMPTY;
		update();
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	protected void initializeEditingDomain() {
		// Create an adapter factory that yields item providers.
		//
		adapterFactory = new ComposedAdapterFactory(ComposedAdapterFactory.Descriptor.Registry.INSTANCE);

		adapterFactory.addAdapterFactory(new ResourceItemProviderAdapterFactory());
		adapterFactory.addAdapterFactory(new RfcItemProviderAdapterFactory());
		adapterFactory.addAdapterFactory(new IdocItemProviderAdapterFactory());
		adapterFactory.addAdapterFactory(new ReflectiveItemProviderAdapterFactory());

		// Create the command stack that will notify this editor as commands are
		// executed.
		//
		BasicCommandStack commandStack = new TransactionalCommandStack();
		commandStack.addCommandStackListener(new CommandStackListener() {

			@Override
			public void commandStackChanged(final EventObject event) {
				getViewSite().getShell().getDisplay().asyncExec(new Runnable() {

					@Override
					public void run() {
						update();
						Command mostRecentCommand = ((CommandStack) event.getSource()).getMostRecentCommand();
						if (mostRecentCommand != null) {
							setSelectionToViewer(mostRecentCommand.getAffectedObjects());
						}
						if (propertySheetPage != null && !propertySheetPage.getControl().isDisposed() && propertySheetPage.getCurrentTab() != null) {
							propertySheetPage.refresh();
						}
					}
				});

			}
		});

		// Create the editing domain with a special command stack.
		//
		editingDomain = new AdapterFactoryEditingDomain(adapterFactory, commandStack, new HashMap<Resource, Boolean>());
	}

	protected void createModel() {
		
		sapConnectionConfiguration = ModelUtil.getModel(editingDomain.getResourceSet());

		// Register data stores
		ComponentDestinationDataProvider.INSTANCE.addDestinationDataStore(sapConnectionConfiguration.getDestinationDataStore());
		ComponentServerDataProvider.INSTANCE.addServerDataStore(sapConnectionConfiguration.getServerDataStore());
	}
		
	protected void update() {
		undoAction.update();
		redoAction.update();
	}
	
	protected void initActions(IActionBars actionBars) {
	    ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();

	    undoAction = new UndoAction(editingDomain);
	    undoAction.setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_UNDO));
	    actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(), undoAction);

	    redoAction = new RedoAction(editingDomain);
	    redoAction.setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_REDO));
	    actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(), redoAction);
		
	    actionBars.updateActionBars();
	}
}