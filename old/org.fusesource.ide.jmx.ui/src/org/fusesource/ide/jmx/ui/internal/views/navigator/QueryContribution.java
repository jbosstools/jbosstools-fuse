/*******************************************************************************
 * Copyright (c) 2006 Jeff Mesnil
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    "Rob Stryker" <rob.stryker@redhat.com> - Initial implementation
 *******************************************************************************/
/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.jmx.ui.internal.views.navigator;

import java.util.HashMap;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;

public class QueryContribution {

	private static HashMap<Viewer, QueryContribution> map =
			new HashMap<Viewer, QueryContribution>();

	public static QueryContribution getContributionFor(Viewer v) {
		return map.get(v);
	}

	public static String getFilterText(Viewer viewer) {
		QueryContribution qc = map.get(viewer);
		if( qc != null ) {
			return qc.filterText;
		}
		return null;
	}


	public static class QueryFilter extends ViewerFilter {
		@Override
		public boolean select(Viewer viewer, Object parentElement,
				Object element) {

			QueryContribution contrib = QueryContribution.getContributionFor(viewer);
			if( contrib != null ) {
				return contrib.shouldShow(element, parentElement);
			}
			return true;
		}
	}


	private String filterText, oldFilterText;
	private HashMap<Object, Boolean> cache = new HashMap<Object, Boolean>();
	private Navigator navigator;
	private boolean requiresRefine;
	public QueryContribution(final Navigator navigator) {
		this.navigator = navigator;
		map.put(navigator.getCommonViewer(), this);
		addListener();
	}

	protected void addListener() {
		navigator.getFilterText().addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				oldFilterText = filterText;
				filterText = navigator.getFilterText().getText();
				final String old = oldFilterText == null ? "" : oldFilterText;
				final String neww = filterText == null ? "" : filterText;

				if( old.equals("") || neww.equals("") || !neww.startsWith(old)) {
					clearCache();
				}  else if(neww.startsWith(old) && !neww.equals(old)) {
					requiresRefine = true;
				}
				cacheEntry(requiresRefine, (ITreeContentProvider)navigator.getCommonViewer().getContentProvider());
				navigator.getCommonViewer().refresh();
			}
		});
	}

	protected void clearCache() {
		cache = new HashMap<Object,Boolean>();
		requiresRefine = false;
	}

	protected void cacheEntry(boolean refine, ITreeContentProvider provider) {
		Object[] elements = provider.getElements(navigator.getCommonViewer().getInput());
		for( int i = 0; i < elements.length; i++ )
			cache(elements[i], refine, provider);
	}
	protected boolean cache(Object o, boolean refine, ITreeContentProvider provider) {
		if( !refine ) {
			if( cache.get(o) != null ) {
				return cache.get(o).booleanValue();
			}
		}

		// If I match, all my children and grandchildren must match
		String elementAsString = MBeanExplorerLabelProvider.getText2(o);
		if( elementAsString != null && filterText != null && elementAsString.contains(filterText)) {
			recurseTrue(o, provider);
			return true;
		}

		// if I don't match, then if ANY of my children match, I also match
		boolean belongs = false;
		Object tmp;
		Object[] children = provider.getChildren(o);
		for( int i = 0; i < children.length; i++ ) {
			tmp = cache.get(children[i]);
			if( !refine || (tmp != null && ((Boolean)tmp).booleanValue())) {
				belongs |= cache(children[i], refine, provider);
			}
		}
		cache.put(o, new Boolean(belongs));
		return belongs;
	}

	protected void recurseTrue(Object o, ITreeContentProvider provider) {
		cache.put(o, new Boolean(true));
		Object[] children = provider.getChildren(o);
		for( int i = 0; i < children.length; i++ )
			recurseTrue(children[i], provider);
	}

	public boolean shouldShow(Object element, Object parentElement) {
		String filterText = this.filterText;
		if( filterText != null && !("".equals(filterText))) {
			boolean tmp = cache.get(element) != null && cache.get(element).booleanValue();
			return tmp;
		}
		return true;
	}

	public void dispose() {
		clearCache();
		oldFilterText = null;
		filterText = null;
	}

}
