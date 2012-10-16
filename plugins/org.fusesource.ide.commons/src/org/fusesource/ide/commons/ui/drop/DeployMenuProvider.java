package org.fusesource.ide.commons.ui.drop;

import org.eclipse.core.resources.IResource;
import org.eclipse.swt.widgets.Menu;

public interface DeployMenuProvider {

	void appendDeployActions(Menu menu, IResource resource);

}
