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

package org.fusesource.ide.camel.editor.editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.eclipse.graphiti.palette.IToolEntry;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.fusesource.ide.camel.editor.Activator;
import org.fusesource.ide.camel.editor.provider.ToolBehaviourProvider;
import org.fusesource.ide.camel.editor.provider.generated.AddNodeMenuFactory;

/**
 * @author lhein
 */
public class AddMenuContributionItem extends ContributionItem {

	/**
	 * 
	 */
	public AddMenuContributionItem() {
		super();
	}

	/**
	 * @param id
	 */
	public AddMenuContributionItem(String id) {
		super(id);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.ContributionItem#isDynamic()
	 */
	@Override
	public boolean isDynamic() {
		return true;
	}
	
    /* (non-Javadoc)
     * @see org.eclipse.jface.action.ContributionItem#fill(org.eclipse.swt.widgets.Menu, int)
     */
    @Override
    public void fill(Menu menu, int index) {
    	super.fill(menu, index);

    	final RiderDesignEditor editor = Activator.getDiagramEditor();
    	
    	for (MenuItem item : menu.getItems()) {
    		item.dispose();
    	}
    	
    	ToolBehaviourProvider tbp = (ToolBehaviourProvider)editor.getDiagramTypeProvider().getCurrentToolBehaviorProvider();
        ArrayList<IToolEntry> additionalEndpoints = new ArrayList<IToolEntry>();
        additionalEndpoints.addAll(tbp.getConnectorsToolEntries());
        additionalEndpoints.addAll(tbp.getExtensionPointToolEntries());
    	
        // sort the palette entries
        Collections.sort(additionalEndpoints, new Comparator<IToolEntry>() {
            /* (non-Javadoc)
             * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
             */
            @Override
            public int compare(IToolEntry o1, IToolEntry o2) {
                return o1.getLabel().compareToIgnoreCase(o2.getLabel());
            }
        });
        
    	AddNodeMenuFactory factory = new AddNodeMenuFactory();
		factory.fillMenu(editor, menu, additionalEndpoints);
    }
}
