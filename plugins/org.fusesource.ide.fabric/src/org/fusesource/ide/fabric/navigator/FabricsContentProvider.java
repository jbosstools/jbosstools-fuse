package org.fusesource.ide.fabric.navigator;


import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.fusesource.ide.commons.Viewers;
import org.fusesource.ide.commons.tree.Node;
import org.fusesource.ide.commons.ui.ImageProvider;
import org.fusesource.ide.fabric.FabricPlugin;
import org.fusesource.ide.fabric.navigator.jmx.FabricConnectionWrapper;
import org.fusesource.ide.jmx.ui.internal.views.navigator.MBeanExplorerContentProvider;
import org.fusesource.ide.jmx.ui.internal.views.navigator.MBeanExplorerLabelProvider;


public class FabricsContentProvider implements ITreeContentProvider,
ILabelProvider, IStructuredContentProvider, FabricListener, IChangeListener {

	// TODO should be able to avoid this tight coupling I think???
	private MBeanExplorerContentProvider delegate = new MBeanExplorerContentProvider();
	private MBeanExplorerLabelProvider delegateLabelProvider = new MBeanExplorerLabelProvider();
	private Viewer viewer;
	private FabricNavigator navigator;

	public FabricsContentProvider() {
	}

	@Override
	public void dispose() {
		removeFabricListener();
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = viewer;
		if (newInput instanceof FabricNavigator) {
			removeFabricListener();
			navigator = (FabricNavigator) newInput;
			navigator.getFabrics().addFabricListener(this);
			navigator.getCloudsNode().addChangeListener(this);
		}
		delegate.inputChanged(viewer, oldInput, newInput);
	}

	protected void removeFabricListener() {
		if (navigator != null) {
			navigator.getFabrics().removeFabricListener(this);
			navigator.getCloudsNode().removeChangeListener(this);
		}
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof FabricNavigator) {
			FabricNavigator navigator = (FabricNavigator) parentElement;
			return new Object[] { navigator.getFabrics(), navigator.getCloudsNode() };
		} else if (parentElement instanceof Node) {
			Node node = (Node) parentElement;
			return node.getChildren();
		}
		return delegate.getChildren(parentElement);
	}

	@Override
	public Object getParent(Object element) {
		if (isFabricNode(element)) {
			// TODO Auto-generated method stub
			return null;
		}
		return delegate.getParent(element);

	}

	@Override
	public boolean hasChildren(Object element) {
		if (isFabricNode(element)) {
			return true;
		}
		return delegate.hasChildren(element);
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
		delegateLabelProvider.addListener(listener);
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		if (isFabricNode(element)) {
			return true;
		} else {
			return delegateLabelProvider.isLabelProperty(element, property);
		}
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		delegateLabelProvider.removeListener(listener);
	}

	// ILabelProvider interface

	@Override
	public Image getImage(Object element) {
		if (element instanceof ImageProvider) {
			ImageProvider provider = (ImageProvider) element;
			Image image = provider.getImage();
			if (image != null) {
				return image;
			}
		}
		if (isFabricNode(element)) {
			return FabricPlugin.getDefault().getImage("fabric_folder.png");
		} else {
			return delegateLabelProvider.getImage(element);
		}
	}

	@Override
	public String getText(Object element) {
		if (element instanceof FabricNavigator) {
			return "Fabrics";
		} else if (element instanceof FabricConnectionWrapper) {
			return "JMX";
		} else if (element instanceof INode) {
			return element.toString();
		}
		return delegateLabelProvider.getText(element);
	}

	protected boolean isFabricNode(Object element) {
		return element instanceof FabricNavigator || element instanceof INode;
	}

	@Override
	public void onFabricEvent(FabricEvent fabricEvent) {
		fireRefresh(fabricEvent.getFabric(), true);
	}

	@Override
	public void handleChange(ChangeEvent event) {
		fireRefresh(navigator.getCloudsNode(), true);
	}

	private void fireRefresh(final Object node, final boolean full) {
		refreshTreeViewer(viewer, node, full);
	}

	public static void refreshTreeViewer(final Viewer viewer,
			final Object node, final boolean full) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				if (Viewers.isValid(viewer)) {
					if (full || node == null
							|| !(viewer instanceof StructuredViewer))
						viewer.refresh();
					else
						((StructuredViewer) viewer).refresh(node);
				}
			}
		});
	}
}
