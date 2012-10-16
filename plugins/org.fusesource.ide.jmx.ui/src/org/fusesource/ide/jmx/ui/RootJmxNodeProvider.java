package org.fusesource.ide.jmx.ui;

import java.util.List;

import org.fusesource.ide.commons.tree.RefreshableUI;


public interface RootJmxNodeProvider {

	void provideRootJmxNodes(RefreshableUI explorerContentProvider, List list);

}
