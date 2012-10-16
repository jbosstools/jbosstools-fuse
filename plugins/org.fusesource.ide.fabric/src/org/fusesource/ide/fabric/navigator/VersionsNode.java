package org.fusesource.ide.fabric.navigator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.graphics.Image;
import org.fusesource.fabric.api.Version;
import org.fusesource.ide.commons.tree.Node;
import org.fusesource.ide.commons.ui.ImageProvider;
import org.fusesource.ide.fabric.FabricConnector;
import org.fusesource.ide.fabric.FabricPlugin;
import org.fusesource.ide.fabric.actions.CreateVersionAction;
import org.fusesource.ide.jmx.ui.internal.views.navigator.ContextMenuProvider;


public class VersionsNode extends FabricNodeSupport implements ImageProvider, ContextMenuProvider {
	private Map<String,VersionNode> map = new HashMap<String, VersionNode>();

	public VersionsNode(Fabric fabric) {
		super(fabric, fabric);
	}

	@Override
	public String toString() {
		return "Versions";
	}

	@Override
	public Image getImage() {
		return FabricPlugin.getDefault().getImage("version_folder.png");
	}

	@Override
	protected void loadChildren() {
		map.clear();
		FabricConnector connector = getFabric().getConnector();
		if (connector == null) return;
		Version[] versions = connector.getVersions();
		if (versions != null) {
			for (Version version : versions) {
				VersionNode node = new VersionNode(this, version);
				map.put(node.getVersionId(), node);
				addChild(node);
			}
		}
	}

	public VersionNode getVersionNode(String version) {
		return map.get(version);
	}

	public VersionNode getDefaultVersionNode() {
		checkLoaded();
		List<Node> childrenList = getChildrenList();
		for (Node node : childrenList) {
			if (node instanceof VersionNode) {
				VersionNode answer = (VersionNode) node;
				// force load
				answer.getChildren();
				return answer;
			}
		}
		return null;
	}

	@Override
	public void provideContextMenu(IMenuManager menu) {
		super.provideContextMenu(menu);

		menu.add(new Separator());
		menu.add(new CreateVersionAction(this));
	}

}
