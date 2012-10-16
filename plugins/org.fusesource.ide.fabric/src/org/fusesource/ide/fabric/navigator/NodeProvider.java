package org.fusesource.ide.fabric.navigator;


public interface NodeProvider {
	
	/**
	 * Provide some new nodes to be added to the given Agent
	 * @param nodes
	 */
	public void provide(ContainerNode agentNode);

}
