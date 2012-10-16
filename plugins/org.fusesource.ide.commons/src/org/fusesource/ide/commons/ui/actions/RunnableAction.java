package org.fusesource.ide.commons.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Event;

public class RunnableAction extends Action {
	private final Runnable runnable;

	public RunnableAction(String text, Runnable runnable) {
		super(text);
		this.runnable = runnable;
	}

	public RunnableAction(String id, String text, Runnable runnable) {
		this(text, runnable);
		setId(id);
	}

	@Override
	public void run() {
		runnable.run();
	}

	@Override
	public void runWithEvent(Event event) {
		runnable.run();
	}


}
