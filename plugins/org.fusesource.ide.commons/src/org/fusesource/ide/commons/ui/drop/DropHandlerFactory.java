package org.fusesource.ide.commons.ui.drop;

import org.eclipse.swt.dnd.DropTargetEvent;

/**
 * A factory of {@link DropHandler} objects for dealing with a drag drop
 */
public interface DropHandlerFactory {
	
	DropHandler createDropHandler(DropTargetEvent event);

}
