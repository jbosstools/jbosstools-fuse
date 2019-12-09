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

package org.fusesource.ide.jmx.camel.navigator;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.fusesource.ide.foundation.core.functions.Function1;
import org.fusesource.ide.foundation.core.functions.Function1Adapter;
import org.fusesource.ide.foundation.core.functions.FunctionLong;
import org.fusesource.ide.foundation.ui.views.TreeViewSupport;
import org.fusesource.ide.jmx.camel.navigator.stats.model.HasTotalStatistics;
import org.fusesource.ide.jmx.commons.messages.IInvocationStatistics;
import org.jboss.tools.jmx.core.tree.Node;


public class ProcessorCallView extends TreeViewSupport implements IPropertySheetPage {

	public static final String ID = ProcessorCallView.class.getName();
	private final Node input;

	public ProcessorCallView(Node input) {
		this.input = input;
	}	

	@Override
	public String getColumnConfigurationId() {
		return ID;
	}

	@Override
	protected String getHelpID() {
		return ID;
	}

	@Override
	protected void createViewer() {
		super.createViewer();

		getViewer().expandAll();
	}

	@Override
	protected void createColumns() {
		int bounds = 100;
		int columnIndex = 0;
		clearColumns();

		Function1<?,?> function = new Function1Adapter<Object,String>(String.class) {
			@Override
			public String apply(Object element) {
				if (element instanceof Node) {
					Node node = (Node) element;
					return node.toString();
				}
				return null;
			}
		};
		columnIndex = addColumnFunction(bounds, columnIndex, function, "ID");

		function = new FunctionLong<Object>() {
			@Override
			public Long apply(Object element) {
				IInvocationStatistics stats = getTotalStatistics(element);
				if (stats != null){
					return stats.getTotalElapsedTime();
				}
				return null;
			}
		};
		columnIndex = addColumnFunction(bounds, columnIndex, function, "Total Time (ms)");

		function = new FunctionLong<Object>() {
			@Override
			public Long apply(Object element) {
				IInvocationStatistics stats = getNodeStatistics(element);
				if (stats != null){
					return stats.getTotalElapsedTime();
				}
				return null;
			}
		};
		columnIndex = addColumnFunction(bounds, columnIndex, function, "Self Time (ms)");

		function = new FunctionLong<Object>() {
			@Override
			public Long apply(Object element) {
				IInvocationStatistics stats = getTotalStatistics(element);
				if (stats != null){
					return stats.getCounter();
				}
				return null;
			}
		};
		addColumnFunction(bounds, columnIndex, function, "Count");
	}


	protected IInvocationStatistics getNodeStatistics(Object element) {
		if (element instanceof IInvocationStatistics) {
			return (IInvocationStatistics) element;
		}
		if (element instanceof ProcessorNodeSupport) {
			ProcessorNodeSupport node = (ProcessorNodeSupport) element;
			return node.getNodeStatistics();
		}
		return null;
	}

	protected IInvocationStatistics getTotalStatistics(Object element) {
		if (element instanceof IInvocationStatistics) {
			return (IInvocationStatistics) element;
		}
		if (element instanceof HasTotalStatistics) {
			HasTotalStatistics node = (HasTotalStatistics) element;
			IInvocationStatistics answer = node.getTotalStatistics();
			if (answer != null) {
				return answer;
			}
		}
		if (element instanceof ProcessorNodeSupport) {
			ProcessorNodeSupport node = (ProcessorNodeSupport) element;
			return node.getTotalStatistics();
		}
		return null;
	}


	@Override
	protected void configureViewer() {
		viewer.setInput(input);
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
			public Object[] getElements(Object element) {
				if (element instanceof Node) {
					Node node = (Node) element;
					return node.getChildren();
				}
				return null;
			}

			@Override
			public Object[] getChildren(Object element) {
				if (element instanceof Node) {
					Node node = (Node) element;
					return node.getChildren();
				}
				return null;
			}

			@Override
			public Object getParent(Object element) {
				if (element instanceof Node) {
					Node node = (Node) element;
					return node.getParent();
				}
				return null;
			}

			@Override
			public boolean hasChildren(Object element) {
				if (element instanceof Node) {
					/*
					Node node = (Node) element;
					return node.getChildren().length > 0;
					 */
					return true;
				}
				return false;
			}

		};
	}
}
