package org.fusesource.ide.fabric.navigator;

import org.eclipse.ui.part.IPage;
import org.fusesource.ide.commons.ui.form.FormPage;
import org.fusesource.ide.commons.ui.views.PageTabDescriptor;


public class ProfileDetailsFormTabDescriptor extends PageTabDescriptor {
	private final ProfileNode node;

	public ProfileDetailsFormTabDescriptor(String label, ProfileNode node) {
		super(label);
		this.node = node;
	}

	@Override
	protected IPage createPage() {
		return new FormPage(new ProfileDetailsForm(null, node));
	}
}