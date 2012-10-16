package org.fusesource.ide.fabric.views.logs;

import org.fusesource.ide.fabric.navigator.ContainerNode;
import org.fusesource.insight.log.service.LogQueryCallback;


public class ContainerLogBrowser extends LogBrowserSupport {
	private final ContainerNode node;

	public ContainerLogBrowser(ContainerNode node) {
		this.node = node;
	}


	@Override
	protected <T> T execute(LogQueryCallback<T> callback) {
		return node.getContainerTemplate().execute(callback);
	}

}
