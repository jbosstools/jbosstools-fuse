/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved.
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.commons.ui.actions;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.PropertySheet;
import org.eclipse.ui.views.properties.tabbed.ISection;
import org.eclipse.ui.views.properties.tabbed.TabContents;
import org.fusesource.ide.commons.Activator;
import org.fusesource.ide.commons.ui.IConfigurableColumns;
import org.fusesource.ide.commons.ui.propsrc.PropertySourceTableSheetPage;
import org.fusesource.ide.commons.ui.views.TabFolderSupport2;
import org.fusesource.ide.commons.ui.views.TableViewSupport;


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
		setImageDescriptor(Activator.getDefault().getImageDescriptor("full/obj16/prop_ps.gif"));
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
			} else if (ps.getCurrentPage() instanceof TabFolderSupport2) {
				TabFolderSupport2 tfs = (TabFolderSupport2)ps.getCurrentPage();
				ISection[] secs = ((TabContents)tfs.getCurrentTab()).getSections();
				for (ISection sec : secs) {
					if (sec instanceof TableViewSupport) {
						this.columns = (IConfigurableColumns) ((TableViewSupport)sec);
						break;
					} else {
						System.out.println("Unsupported type: " + sec.getClass().getName());
					}
				}
			} else {
				System.out.println("Unsupported type: " + ps.getCurrentPage().getClass().getName());
			}	
		} else if (part instanceof TableViewSupport) {
			this.columns = (IConfigurableColumns) ((TableViewSupport)part);
		} else {
			System.out.println("Unsupported type: " + part.getClass().getName());
		}
	}
	
	/**
	 * Gets the configure columns dialog.
	 * 
	 * @return The configure columns dialog
	 */
	private ConfigureColumnsDialog getDialog() {
		if (dialog == null) {
			dialog = new ConfigureColumnsDialog(columns);
		}
		return dialog;
	}
}
