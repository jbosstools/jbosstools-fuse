/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.foundation.ui.views;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.views.properties.tabbed.AbstractSectionDescriptor;
import org.eclipse.ui.views.properties.tabbed.AbstractTabDescriptor;
import org.eclipse.ui.views.properties.tabbed.ISection;
import org.eclipse.ui.views.properties.tabbed.ISectionDescriptor;


public abstract class PageTabDescriptor extends AbstractTabDescriptor {
	private static final AtomicLong counter = new AtomicLong(0);

	private final String label;
	private final String category;

	public PageTabDescriptor(String label) {
		this(label, "Fuse");
	}

	public PageTabDescriptor(String label, String category) {
		this.label = label;
		this.category = category;
	}

	@Override
	public String getCategory() {
		return category;
	}

	@Override
	public String getId() {
		// lets generate a default ID
		return getClass().getName() + "." + label; // + "." + counter.incrementAndGet();
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public List<ISectionDescriptor> getSectionDescriptors() {
		List<ISectionDescriptor>  list = new ArrayList<ISectionDescriptor>();
		list.add(createSectionDescriptor());
		return list;
	}

	protected ISectionDescriptor createSectionDescriptor() {
		return new AbstractSectionDescriptor() {

			@Override
			public boolean appliesTo(IWorkbenchPart part, ISelection selection) {
				// lets cheat :)
				return true;
			}

			@Override
			public String getId() {
				return PageTabDescriptor.this.getId();
			}

			@Override
			public ISection getSectionClass() {
				IPage page = createPage();
				if (page instanceof ISection) {
					return (ISection) page;
				} else {
					return new PageSection(page);
				}
			}

			@Override
			public String getTargetTab() {
				return label;
			}};
	}

	protected abstract IPage createPage();

}
