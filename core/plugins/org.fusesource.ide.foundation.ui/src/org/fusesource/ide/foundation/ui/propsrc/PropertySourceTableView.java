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

import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySourceProvider;
import org.eclipse.ui.views.properties.PropertyColumnLabelProvider;
import org.fusesource.ide.foundation.core.functions.Function1;
import org.fusesource.ide.foundation.core.functions.Function1WithReturnType;
import org.fusesource.ide.foundation.ui.internal.FoundationUIActivator;
import org.fusesource.ide.foundation.ui.label.FunctionColumnLabelProvider;
import org.fusesource.ide.foundation.ui.properties.PropertyDescriptors;
import org.fusesource.ide.foundation.ui.properties.PropertySources;
import org.fusesource.ide.foundation.ui.util.Viewers;
import org.fusesource.ide.foundation.ui.views.TableViewSupport;


/**
 * A table view of a collection of a collection of
 * {@link IPropertySourceProvider} instances
 * 
 */
public class PropertySourceTableView extends TableViewSupport implements IPropertySheetPage {

	public static final String ID = "org.fusesource.ide.fabric.views.PropertySourceTableView";

	private List<IPropertySource> propertySources = new ArrayList<>();

	private final String viewId;
	private Object input;

	private IPropertySource exemplar;

	public PropertySourceTableView(String viewId) {
		this.viewId = viewId;
	}

	public PropertySourceTableView(Class<?> beanType) {
		this(beanType.getName());
		try {
			this.exemplar = new BeanPropertySource(null, beanType);
		} catch (IntrospectionException e) {
			FoundationUIActivator.pluginLog().logWarning("Failed to create BeanPropertySource for " + beanType.getName() + ". " + e, e);
		}
	}

	public List<IPropertySource> getPropertySources() {
		return propertySources;
	}

	public void setPropertySources(List<IPropertySource> propertySources) {
		this.propertySources = propertySources;
		setInput(propertySources);
	}

	public Object getExemplar() {
		return exemplar;
	}

	public void setExemplar(IPropertySource exemplar) {
		this.exemplar = exemplar;
	}

	@Override
	public void setInput(Object input) {
		this.input = input;
		Viewers.setInput(getViewer(), input);
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
	}

	@Override
	public void setActionBars(IActionBars actionBars) {
	}

	@Override
	protected void createColumns() {
		int bounds = 100;
		int column = 0;
		clearColumns();

		List<IPropertySource> list = propertySources;
		if (list.isEmpty() && exemplar != null) {
			list = new ArrayList<>();
			list.add(exemplar);
		}

		boolean usePropertySourceProviderIfItHasNicerRenderers = false;
		if (usePropertySourceProviderIfItHasNicerRenderers) {
			SortedMap<String, TableViewerColumn> headers = new TreeMap<String, TableViewerColumn>();
			for (Object object : list) {
				final IPropertySource propertySource = PropertySources.asPropertySource(object);
				IPropertyDescriptor[] descriptors = propertySource.getPropertyDescriptors();
				if (descriptors != null) {
					for (final IPropertyDescriptor descriptor : descriptors) {
						final Object id = descriptor.getId();
						String header = PropertyDescriptors.getReadablePropertyName(descriptor);
						TableViewerColumn col = headers.get(header);
						if (col == null) {
							col = createTableViewerColumn(header, bounds, column++);
							headers.put(header, col);

							IPropertySourceProvider propertySourceProvider = new IPropertySourceProvider() {
								@Override
								public IPropertySource getPropertySource(Object object) {
									return PropertySources.asPropertySource(object);
								}

							};
							col.setLabelProvider(new PropertyColumnLabelProvider(propertySourceProvider, id));
						}
					}
				}
			}
		}
		else {
			SortedMap<String, Function1<?, ?>> headers = new TreeMap<>();
			for (Object object : list) {
				final IPropertySource propertySource = PropertySources.asPropertySource(object);
				IPropertyDescriptor[] descriptors = propertySource.getPropertyDescriptors();
				if (descriptors != null) {
					for (final IPropertyDescriptor descriptor : descriptors) {
						final Object id = descriptor.getId();
						String name = PropertyDescriptors.getReadablePropertyName(descriptor);
						Function1 function = new Function1WithReturnType() {
							@Override
							public Object apply(Object object) {
								IPropertySource property = PropertySources.asPropertySource(object);
								if (property != null) {
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
			int idx = 0;
			boolean pickedSortColumn = false;
			Set<Entry<String, Function1<?, ?>>> entrySet = headers.entrySet();
			for (Entry<String, Function1<?, ?>> entry : entrySet) {
				String header = entry.getKey();
				if (!pickedSortColumn && isDefaultSortColumn(header)) {
					setDefaultSortColumnIndex(idx);
					pickedSortColumn = true;
				}
				Function1<?, ?> function = entry.getValue();
				addFunction(function);
				TableViewerColumn col = createTableViewerColumn(header, bounds, column++);
				col.setLabelProvider(createColumnLabelProvider(header, function));
				idx++;
			}
		}
	}

	protected CellLabelProvider createColumnLabelProvider(String header, Function1<?, ?> function) {
		return new FunctionColumnLabelProvider(function);
	}

	protected boolean isDefaultSortColumn(String header) {
		if (header != null) {
			final String lower = header.toLowerCase();
			return lower.equals("id") || lower.equals("identifier") || lower.equals("name");
		}
		return false;
	}

	@Override
	protected void configureViewer() {
		viewer.setInput(input);
	}

	@Override
	protected IStructuredContentProvider createContentProvider() {
		return new IStructuredContentProvider() {

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
