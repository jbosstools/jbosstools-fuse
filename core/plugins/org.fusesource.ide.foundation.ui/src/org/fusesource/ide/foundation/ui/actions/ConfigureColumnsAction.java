/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved.
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at https://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
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

package org.fusesource.ide.foundation.ui.actions;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.PropertySheet;
import org.eclipse.ui.views.properties.tabbed.ISection;
import org.eclipse.ui.views.properties.tabbed.TabContents;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.fusesource.ide.foundation.ui.internal.FoundationUIActivator;
import org.fusesource.ide.foundation.ui.propsrc.PropertySourceTableSheetPage;
import org.fusesource.ide.foundation.ui.util.IConfigurableColumns;
import org.fusesource.ide.foundation.ui.views.TabFolderSupport2;
import org.fusesource.ide.foundation.ui.views.TableViewSupport;


/**
 * The action to configure columns.
 */
public class ConfigureColumnsAction extends Action {

	/** The configurable columns. */
	private IConfigurableColumns columns;

	/** The configure columns dialog. */
	private ConfigureColumnsDialog dialog;

	/**
	 * The constructor.
	 * 
	 * @param columns
	 *            The configurable columns
	 */
	public ConfigureColumnsAction(IConfigurableColumns columns) {
		Assert.isNotNull(columns);

		setText(Messages.configureColumnsLabel);
		setImageDescriptor(FoundationUIActivator.getDefault().getSharedImages().descriptor(FoundationUIActivator.IMAGE_PROPS_ICON));
		this.columns = columns;

		setId(getClass().getName());
	}

	/*
	 * @see Action#run()
	 */
	@Override
	public void run() {
		// figured out that the columns field is mostly not set correct when
		// action gets invoked so we determine the column config here again
		determineColumns();
		// if we can't determine a column config there is no sense in
		// displaying the configuration dialog...so we skip that part
		if (this.columns == null) return;
		// now open the dialog
		getDialog().open();
	}

	private void determineColumns() {
		
		// initial clear of the columns field filled when creating the action
		this.columns = null;
		
		IWorkbenchPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
		if (part instanceof PropertySheet) {
			PropertySheet ps = (PropertySheet)part; 
			if (ps.getCurrentPage() instanceof PropertySourceTableSheetPage) {
				PropertySourceTableSheetPage sp = (PropertySourceTableSheetPage)ps.getCurrentPage();
				this.columns = (IConfigurableColumns)sp.getTableView();
			} else {
			    ISection[] secs = null;
			    if (ps.getCurrentPage() instanceof TabFolderSupport2) {
    				TabFolderSupport2 tfs = (TabFolderSupport2)ps.getCurrentPage();
    				secs = ((TabContents)tfs.getCurrentTab()).getSections();
			    } else if (ps.getCurrentPage() instanceof TabbedPropertySheetPage) {
			        TabbedPropertySheetPage tabbedPage = (TabbedPropertySheetPage) ps.getCurrentPage();
			        secs = tabbedPage.getCurrentTab() == null ? null : tabbedPage.getCurrentTab().getSections();
			    }
			    if (secs != null) {
    				for (ISection sec : secs) {
    					if (sec instanceof IConfigurableColumns) {
    						this.columns = (IConfigurableColumns) sec;
    						break;
    					} else {
    						FoundationUIActivator.pluginLog().logWarning("Unsupported type: " + sec.getClass().getName());
    					}
    				}
    			} else {
    				FoundationUIActivator.pluginLog().logWarning("Unsupported type: " + ps.getCurrentPage().getClass().getName());
    			}
			}
		} else if (part instanceof TableViewSupport) {
			this.columns = (IConfigurableColumns) ((TableViewSupport)part);
		} else {
			FoundationUIActivator.pluginLog().logWarning("Unsupported type: " + part.getClass().getName());
		}
	}
	
	/**
	 * Gets the configure columns dialog.
	 * 
	 * @return The configure columns dialog
	 */
	private ConfigureColumnsDialog getDialog() {
		return new ConfigureColumnsDialog(columns);
	}
}
