package org.fusesource.ide.jmx.core.tree;

import java.util.List;

public interface NodeProvider {
	
	public void provide(Root root);

	public void provideRootNodes(List<NodeProvider> list);

}
