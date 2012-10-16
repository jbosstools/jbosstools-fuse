package org.fusesource.ide.jmx.ui.internal.actions;


import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.fusesource.ide.commons.tree.Node;
import org.fusesource.ide.commons.tree.Refreshable;
import org.fusesource.ide.commons.util.Objects;
import org.fusesource.ide.jmx.core.IConnectionWrapper;
import org.fusesource.ide.jmx.core.tree.Root;
import org.fusesource.ide.jmx.ui.Messages;
import org.fusesource.ide.jmx.ui.internal.JMXImages;


/**
 * @author lhein
 */
public class RefreshAction extends Action implements IWorkbenchWindowActionDelegate {
	private IWorkbenchWindow window;
	private StructuredViewer viewer;
	private final String viewId;

	/**
	 * creates the refresh action
	 */
	public RefreshAction(String viewId) {
		this.viewId = viewId;
		Objects.notNull(viewId, "No viewId for RefreshAction");
		setText(Messages.RefreshAction_text);
		setDescription(Messages.RefreshAction_description);
		setToolTipText(Messages.RefreshAction_tooltip);
		JMXImages.setLocalImageDescriptors(this, "refresh.gif");
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		IWorkbench wb = PlatformUI.getWorkbench();
		if (wb == null) {
			return;
		}

		IWorkbenchWindow aww = wb.getActiveWorkbenchWindow();
		if (aww == null) {
			return;
		}

		IWorkbenchPage ap = aww.getActivePage();
		if (ap == null) {
			return;
		}

		ISelection sel = getSelection(ap);
		if (sel instanceof StructuredSelection) {
			StructuredSelection ss = (StructuredSelection) sel;
			Object onode = ss.getFirstElement();
			if (onode instanceof Refreshable) {
				Refreshable refreshable = (Refreshable) onode;
				refreshable.refresh();
				refreshViewer(onode);
			} else {
				IConnectionWrapper wrapper = null;
				if (onode instanceof IConnectionWrapper) {
					wrapper = (IConnectionWrapper) onode;
				} else if (onode instanceof Node) {
					Node node = (Node) onode;
					while ((node instanceof Root) == false && node != null) {
						node = node.getParent();
					}
					if (node != null) {
						wrapper = ((Root) node).getConnection();
					}
				}
				if (wrapper != null) {
					try {
						wrapper.disconnect();
						wrapper.connect();
						refreshViewer(wrapper);
						if (viewer instanceof TreeViewer) {
							TreeViewer treeViewer = (TreeViewer) viewer;
							treeViewer.expandToLevel(wrapper, 1);
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		}
	}

	protected ISelection getSelection(IWorkbenchPage ap) {
		return ap.getSelection(viewId);
	}

	private void refreshViewer(Object node) {
		if (viewer != null) {
			viewer.refresh(node);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		run();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
	 */
	public void dispose() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
	 */
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

	public void setViewer(StructuredViewer viewer) {
		this.viewer = viewer;
	}
}
