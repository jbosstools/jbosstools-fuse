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

package org.fusesource.ide.foundation.ui.propsrc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySourceProvider;
import org.fusesource.ide.foundation.core.functions.Function1;
import org.fusesource.ide.foundation.core.functions.Function1WithReturnType;
import org.fusesource.ide.foundation.ui.label.FunctionColumnLabelProvider;
import org.fusesource.ide.foundation.ui.properties.PropertyDescriptors;
import org.fusesource.ide.foundation.ui.views.TreeViewSupport;


/**
 * A table view of a collection of a collection of
 * {@link IPropertySourceProvider} instances
 * 
 */
public class PropertySourceTreeView extends TreeViewSupport implements IPropertySheetPage {

	public static final String ID = "org.fusesource.ide.fabric.views.PropertySourceTableView";

	private List<IPropertySource> propertySources = new ArrayList<>();

	private final String viewId;

	public PropertySourceTreeView(String viewId) {
		this.viewId = viewId;
	}

	public List<IPropertySource> getPropertySources() {
		return propertySources;
	}

	public void setPropertySources(List<IPropertySource> propertySources) {
		this.propertySources = propertySources;
	}

	@Override
	public String getColumnConfigurationId() {
		return viewId;
	}

	@Override
	protected String getHelpID() {
		// TODO should we use the viewID or the generic help ID??
		return ID;
	}


	@Override
	public void createControl(Composite parent) {
		createPartControl(parent);
	}

	@Override
	public Control getControl() {
		return getViewer().getControl();
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setActionBars(IActionBars actionBars) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void createColumns() {
		int bounds = 100;
		int column = 0;
		clearColumns();

		SortedMap<String, Function1> headers = new TreeMap<String, Function1>();
		for (final IPropertySource propertySource : propertySources) {
			IPropertyDescriptor[] descriptors = propertySource.getPropertyDescriptors();
			if (descriptors != null) {
				for (final IPropertyDescriptor descriptor : descriptors) {
					final Object id = descriptor.getId();
					String name = PropertyDescriptors.getReadablePropertyName(descriptor);
					Function1 function = new Function1WithReturnType() {
						@Override
						public Object apply(Object object) {
							if (object instanceof IPropertySource) {
								IPropertySource property = (IPropertySource) object;
								return property.getPropertyValue(id);
							}
							return null;
						}

						@Override
						public Class<?> getReturnType() {
							return PropertyDescriptors.getPropertyType(descriptor);
						}
					};
					headers.put(name, function);
				}
			}
		}
		Set<Entry<String, Function1>> entrySet = headers.entrySet();
		for (Entry<String, Function1> entry : entrySet) {
			String header = entry.getKey();
			Function1 function = entry.getValue();
			addFunction(function);
			TreeViewerColumn col = createTreeViewerColumn(header, bounds, column++);
			col.setLabelProvider(new FunctionColumnLabelProvider(function));
		}
	}

	@Override
	protected void configureViewer() {
		viewer.setInput(propertySources);
	}

	@Override
	protected ITreeContentProvider createContentProvider() {
		return new ITreeContentProvider() {

			@Override
			public void inputChanged(Viewer v, Object oldInput, Object newInput) {
			}

			@Override
			public void dispose() {
			}

			@Override
			public Object[] getElements(Object parent) {
				return propertySources.toArray();
			}

			@Override
			public Object[] getChildren(Object parentElement) {
				return null;
			}

			@Override
			public Object getParent(Object element) {
				return null;
			}

			@Override
			public boolean hasChildren(Object element) {
				return false;
			}

		};
	}

	@Override
	public void refresh() {
		for (IPropertySource propSource : propertySources) {
			if (propSource instanceof BeanPropertySource) {
				((BeanPropertySource) propSource).cleanCache();
			}
		}
		super.refresh();
	}

}
