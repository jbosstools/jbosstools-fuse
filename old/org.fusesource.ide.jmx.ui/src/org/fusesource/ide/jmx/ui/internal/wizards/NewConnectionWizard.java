/*******************************************************************************
 * Copyright (c) 2006 Jeff Mesnil
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    "Rob Stryker" <rob.stryker@redhat.com> - Initial implementation
 *******************************************************************************/
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

package org.fusesource.ide.jmx.ui.internal.wizards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeItem;
import org.fusesource.ide.jmx.core.ExtensionManager;
import org.fusesource.ide.jmx.core.IConnectionProvider;
import org.fusesource.ide.jmx.core.IConnectionWrapper;
import org.fusesource.ide.jmx.ui.ConnectionWizardPage;
import org.fusesource.ide.jmx.ui.Messages;
import org.fusesource.ide.jmx.ui.UIExtensionManager;
import org.fusesource.ide.jmx.ui.UIExtensionManager.ConnectionProviderUI;


/**
 * The connection wizard
 */
public class NewConnectionWizard extends Wizard {
    private IConnectionProvider selected;
	public NewConnectionWizard() {
		super();
	}
	private HashMap<String, ConnectionProviderUI> providerMap;
	private HashMap<String, ConnectionWizardPage[]> pageMap;

	private IWizardPage firstPage;
    public void addPages() {
    	firstPage = createFirstPage();
    	addPage(firstPage);
    	providerMap = UIExtensionManager.getConnectionUIElements();
    	pageMap = new HashMap<String, ConnectionWizardPage[]>();
    	List<String> l = new ArrayList<String>();
    	l.addAll(providerMap.keySet());
    	Collections.sort(l);
    	for( Iterator<ConnectionProviderUI> i = providerMap.values().iterator(); i.hasNext();) {
    		ConnectionProviderUI ui = i.next();
    		ConnectionWizardPage[] pages = ui.createPages();
    		pageMap.put(ui.getId(), pages);
    		for( int j = 0; j < pages.length; j++ )
    			addPage(pages[j]);
    	}
    }

    public boolean canFinish() {
    	if( selected == null )
    		return false;
    	IWizardPage[] active = getActivePages();
    	if( active != null ) {
    		if( active.length > 0 ) {
    			if( active[active.length-1] == getContainer().getCurrentPage())
    				return true;
    			return false;
    		}
    	}
        return true;
    }


    private IWizardPage createFirstPage() {
    	return new FirstPage();
    }
	public boolean performFinish() {
		ConnectionWizardPage[] active = getActivePages();
    	if( active != null ) {
    		IConnectionWrapper wrap = null;
	    	for( int i = active.length-1; i >= 0 && wrap == null; i--) {
	    		try {
	    			wrap = active[i].getConnection();
	    		} catch( CoreException ce ) {
	    			// TODO LOG
	    		}
	    	}

	    	if( wrap != null ) {
	    		wrap.getProvider().addConnection(wrap);
	    		return true;
	    	}
    	}

		return true;
	}

    public IWizardPage getNextPage(IWizardPage page) {
    	IWizardPage[] active = getActivePages();
    	if( active != null && active.length > 0 && page == firstPage)
    			return active[0];

    	if( active != null ) {
    		for( int i = 0; i < active.length; i++ ) {
    			if( active[i] == page && i+1 < active.length )
    				return active[i+1];
    		}
    	}
    	return null;
    }

    public ConnectionWizardPage[] getActivePages() {
    	if( selected != null )
    		return pageMap.get(selected.getId());
    	return null;
    }

	private class FirstPage extends WizardPage {
		TreeViewer viewer;
		public FirstPage() {
			super(Messages.NewConnectionWizard);
			setDescription("Create a new JMX Connection");
		}
		public void createControl(Composite parent) {
			Composite main = new Composite(parent, SWT.NONE);
			main.setLayout(new FillLayout());
			viewer = new TreeViewer(main);
			viewer.setContentProvider(new FirstPageContentProvider());
			viewer.setLabelProvider(new FirstPageLabelProvider());
			viewer.setInput(this);
			viewer.addSelectionChangedListener(new ISelectionChangedListener() {
				public void selectionChanged(SelectionChangedEvent event) {
					viewerSelectionChanged();
				}
			});
			setControl(main);
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					TreeItem item = viewer.getTree().getItems()[0];
					viewer.setSelection(new StructuredSelection(item.getData()));
				}
			});
		}

		private void viewerSelectionChanged() {
			IStructuredSelection ssel = (IStructuredSelection)viewer.getSelection();
			IConnectionProvider cp = (IConnectionProvider)ssel.getFirstElement();
			selected = cp;
			getContainer().updateButtons();
		}
	}

	private class FirstPageContentProvider implements ITreeContentProvider {
		public Object[] getElements(Object inputElement) {
			ArrayList<IConnectionProvider> providers = new ArrayList<IConnectionProvider>();
			HashMap<String, ConnectionProviderUI> map = UIExtensionManager.getConnectionUIElements();
			Set<String> keys = map.keySet();
			Iterator<String> i = keys.iterator();
			while(i.hasNext()) {
				String id = i.next();
				if( ExtensionManager.getProvider(id) != null && ExtensionManager.getProvider(id).canCreate())
					providers.add(ExtensionManager.getProvider(id));
			}

			return providers
					.toArray(new IConnectionProvider[providers.size()]);
		}

		public void dispose() {
			// no need
		}
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// no need
		}

		public Object[] getChildren(Object parentElement) {
			return null;
		}

		public Object getParent(Object element) {
			return null;
		}

		public boolean hasChildren(Object element) {
			return false;
		}
	}

	private class FirstPageLabelProvider extends LabelProvider {
		private HashMap<String, Image> images = new HashMap<String, Image>();
	    public void dispose() {
	    	for( Iterator<Image> i = images.values().iterator(); i.hasNext(); )
	    		i.next().dispose();
	    	super.dispose();
	    }

		public Image getImage(Object element) {
			if( element instanceof IConnectionProvider ) {
				ConnectionProviderUI ui = UIExtensionManager.getConnectionProviderUI(((IConnectionProvider)element).getId());
				if( ui != null ) {
					if(images.containsKey(ui.getId()))
							return images.get(ui.getId());
					images.put(ui.getId(), ui.getImageDescriptor().createImage());
					return images.get(ui.getId());
				}
			}
			return null;
		}
		public String getText(Object element) {
			if( element instanceof IConnectionProvider ) {
				ConnectionProviderUI ui = UIExtensionManager.getConnectionProviderUI(((IConnectionProvider)element).getId());
				if( ui != null ) {
					return ui.getName();
				}
			}
			return element == null ? "" : element.toString();//$NON-NLS-1$
		}
	}

}
