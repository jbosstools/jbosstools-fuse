package org.fusesource.ide.jmx.ui.internal.actions;


import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * @author lhein
 */
public abstract class ExpandAllAction extends Action implements IWorkbenchWindowActionDelegate {

	/**
	 * creates the refresh action
	 */
	public ExpandAllAction() {
		/*
		setText(Messages.ExpandAllAction_text);
		setDescription(Messages.ExpandAll_description);
		setToolTipText(Messages.RExpandAll_tooltip);
		JMXImages.setLocalImageDescriptors(this, "refresh.gif");
		 */
	}

}
