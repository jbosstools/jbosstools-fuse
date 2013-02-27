package org.fusesource.ide.camel.editor.editor;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.fusesource.ide.camel.editor.Activator;
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
    	
    	AddNodeMenuFactory factory = new AddNodeMenuFactory();
		factory.fillMenu(editor, menu);
    }
}
