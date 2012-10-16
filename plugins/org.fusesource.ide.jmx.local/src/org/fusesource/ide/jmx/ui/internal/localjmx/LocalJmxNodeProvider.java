package org.fusesource.ide.jmx.ui.internal.localjmx;

import java.util.List;

import org.fusesource.ide.commons.tree.PartialRefreshableNode;
import org.fusesource.ide.commons.tree.RefreshableUI;
import org.fusesource.ide.jmx.ui.RootJmxNodeProvider;



public class LocalJmxNodeProvider implements RootJmxNodeProvider{

	public void provideRootJmxNodes(RefreshableUI contentProvider, List list) {
		PartialRefreshableNode connections = new JvmConnectionsNode(null, contentProvider);
		list.add(connections);
	}

}
