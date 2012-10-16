package org.fusesource.ide.fabric.navigator;

import org.eclipse.ui.part.IPage;
import org.fusesource.ide.commons.ui.form.FormPage;
import org.fusesource.ide.commons.ui.views.PageTabDescriptor;


public class ProfileTreeTabDescriptor extends PageTabDescriptor {
	private final ContainerNode node;

	public ProfileTreeTabDescriptor(String label, ContainerNode node) {
		super(label);
		this.node = node;
	}

	@Override
	protected IPage createPage() {
		return new FormPage(new ProfileTreeForm(node));
	}
}