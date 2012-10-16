package org.fusesource.ide.fabric.navigator;

import org.eclipse.ui.part.IPage;
import org.fusesource.ide.commons.ui.form.FormPage;
import org.fusesource.ide.commons.ui.views.PageTabDescriptor;


public class ProfilesTabDescriptor extends PageTabDescriptor {
	private final ContainerNode node;

	public ProfilesTabDescriptor(String label, ContainerNode node) {
		super(label);
		this.node = node;
	}

	@Override
	protected IPage createPage() {
		return new FormPage(new ProfilesForm(null, node));
	}
}