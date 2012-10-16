package org.fusesource.ide.camel.editor.editor;

import java.beans.PropertyChangeEvent;

import org.eclipse.gef.commands.Command;

public class PropertyChangeCommand extends Command {

	public PropertyChangeCommand(PropertyChangeEvent evt) {
	}

	@Override
	public boolean canExecute() {
		return true;
	}

	@Override
	public boolean canUndo() {
		return true;
	}



}
