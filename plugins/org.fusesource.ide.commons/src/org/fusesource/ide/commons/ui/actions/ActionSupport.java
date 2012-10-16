package org.fusesource.ide.commons.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

public abstract class ActionSupport extends Action {

	public ActionSupport(String text, ImageDescriptor image) {
		super(text, image);
		init();
	}

	public ActionSupport(String text, String tooltip, ImageDescriptor image) {
		this(text, image);
		setToolTipText(tooltip);
		init();
	}

	public ActionSupport(String text, int style) {
		super(text, style);
		init();
	}

	public ActionSupport(String text) {
		super(text);
		init();
	}

	private void init() {
		setId(getClass().getName());
	}
}
