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

package org.fusesource.ide.camel.editor.views;

import java.text.NumberFormat;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.zest.core.viewers.EntityConnectionData;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.viewers.IConnectionStyleProvider;
import org.eclipse.zest.core.viewers.IEntityStyleProvider;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.fusesource.fon.util.messages.INodeStatistics;
import org.fusesource.fon.util.messages.NodeStatisticsContainer;
import org.fusesource.ide.camel.editor.Activator;
import org.fusesource.ide.camel.editor.utils.DiagramUtils;
import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.Flow;
import org.fusesource.ide.commons.tree.HasName;
import org.fusesource.ide.commons.tree.Node;
import org.fusesource.ide.commons.ui.ImageProvider;
import org.fusesource.ide.commons.util.Strings;
import org.fusesource.ide.graph.GraphLabelProviderSupport;


public class DiagramGraphLabelProvider extends GraphLabelProviderSupport implements ILabelProvider,
IEntityStyleProvider, IConnectionStyleProvider,
ISelectionChangedListener {
	private final DiagramView view;
	private Set<AbstractNode> selectedConnections;
	private NumberFormat numberFormat = NumberFormat.getInstance();
	private boolean useNodeIdForLabel;

	public DiagramGraphLabelProvider(DiagramView view) {
		super(view.getViewer());
		this.view = view;
		numberFormat.setMaximumFractionDigits(1);
		numberFormat.setMinimumFractionDigits(0);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		GraphViewer viewer = getViewer();
		if (selectedConnections != null) {
			for (AbstractNode node : selectedConnections) {
				viewer.unReveal(node);
			}
			selectedConnections = null;
		}

		ISelection selection = event.getSelection();
		if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
			selectedConnections = new HashSet<AbstractNode>();
			for (Object o : ((IStructuredSelection) selection).toList()) {
				if (o instanceof AbstractNode) {
					AbstractNode node = (AbstractNode) o;
					viewer.reveal(node);
					selectedConnections.add(node);
					/*
					AbstractNode output = (AbstractNode) o;
					for (AbstractNode node : output.getOutputs()) {
						viewer.reveal(node);
						selectedConnections.add(node);
					}
					 */
				}
			}
		}

		Object[] connections = viewer.getConnectionElements();
		for (int i = 0; i < connections.length; i++) {
			viewer.update(connections[i], null);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
	 */
	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	@Override
	public void addListener(ILabelProviderListener listener) {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	@Override
	public void removeListener(ILabelProviderListener listener) {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	@Override
	public Image getImage(Object element) {
		if (isShowIcon()) {
			if (isRouteNode(element)) {
				AbstractNode node = (AbstractNode) element;
				return node.getSmallImage();
			}
			if (element instanceof ImageProvider) {
				ImageProvider node = (ImageProvider) element;
				return node.getImage();
			}
		}
		return null;
	}

	protected boolean isRouteNode(Object element) {
		return element instanceof AbstractNode && !(element instanceof Flow);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(Object element) {
		try {
			if (isRouteNode(element)) {
				AbstractNode node = (AbstractNode) element;
				String label = DiagramUtils.filterFigureLabel(node.getDisplayText(useNodeIdForLabel));
				return label;
			} else if (element instanceof HasName) {
				HasName h = (HasName) element;
				return h.getName();
			} else if (element instanceof Flow) {
				Flow flow = (Flow) element;
				INodeStatistics stats = getStatsFor(flow);
				if (stats != null) {
					return statsLabel(stats);
				} else {
					return null;
				}
			} else if (element instanceof Node){
				return Strings.getOrElse(element, null);
			} else {
				// TODO use a strategy to display some label text...
				// e.g. timing stuff??
				return null;
			}
		} catch (Exception e) {
			Activator.getLogger().warning("Caught exception trying to get label: " + e, e);
			return null;
		}
	}


	/*
	 * (non-Javadoc)
	 * @see org.eclipse.zest.core.viewers.IEntityStyleProvider#getTooltip(java.lang.Object)
	 */
	@Override
	public IFigure getTooltip(Object entity) {
		if (isRouteNode(entity)) {
			AbstractNode node = (AbstractNode) entity;
			String label = node.getDisplayToolTip();

			String id = node.getId();
			if (id != null) {
				label = "[" + id + "] " + label;
			}
			// TODO add link to docs!
			return new Label(label);
		} else if (entity instanceof Flow) {
			Flow flow = (Flow) entity;
			INodeStatistics stats = getStatsFor(flow);
			if (stats != null) {
				return statsToolTip(stats);
			} else {
				return null;
			}
		} else if (entity instanceof Node) {
		}
		return null;
	}

	protected INodeStatistics getStatsFor(Flow flow) {
		NodeStatisticsContainer traceExchangeList = view.getNodeStatisticsContainer();
		INodeStatistics stats = null;
		AbstractNode node = flow.getTarget();
		if (traceExchangeList != null && node != null) {
			stats = traceExchangeList.getNodeStats(node.getId());
		}
		return stats;
	}

	protected String statsLabel(INodeStatistics stats) {
		long counter = stats.getCounter();
		if (counter > 0) {
			return "Total: " + counter;
		} else {
			return "";
		}
	}

	protected IFigure statsToolTip(INodeStatistics stats) {
		long counter = stats.getCounter();
		if (counter > 0) {
			return new Label("Exchanges total: " + counter + " / mean time: " + numberFormat.format(stats.getMeanElapsedTime())
					+ " / max time: " + numberFormat.format(stats.getMaxElapsedTime())
					+ " / min time: " + numberFormat.format(stats.getMinElapsedTime()));
		} else {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.zest.core.viewers.IEntityStyleProvider#fisheyeNode(java.lang.Object)
	 */
	@Override
	public boolean fisheyeNode(Object entity) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.zest.core.viewers.IEntityStyleProvider#getBorderWidth(java.lang.Object)
	 */
	@Override
	public int getBorderWidth(Object entity) {
		return 1;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.zest.core.viewers.IEntityStyleProvider#getBorderColor(java.lang.Object)
	 */
	@Override
	public Color getBorderColor(Object entity) {
		return Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.zest.core.viewers.IEntityStyleProvider#getBorderHighlightColor(java.lang.Object)
	 */
	@Override
	public Color getBorderHighlightColor(Object entity) {
		return Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.zest.core.viewers.IEntityStyleProvider#getForegroundColour(java.lang.Object)
	 */
	@Override
	public Color getForegroundColour(Object entity) {
		//		return null;
		return Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.zest.core.viewers.IEntityStyleProvider#getBackgroundColour(java.lang.Object)
	 */
	@Override
	public Color getBackgroundColour(Object entity) {
		if (isRouteNode(entity)) {
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.zest.core.viewers.IEntityStyleProvider#getNodeHighlightColor(java.lang.Object)
	 */
	@Override
	public Color getNodeHighlightColor(Object entity) {
		if (isRouteNode(entity)) {
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.zest.core.viewers.IConnectionStyleProvider#getLineWidth(java.lang.Object)
	 */
	@Override
	public int getLineWidth(Object rel) {
		int lineWidth = 1;
		// rel is a Flow...
		if (isRouteNode(rel)) {
			if (selectedConnections != null
					&& selectedConnections.contains(rel)) {
				return lineWidth;
			}

		} else if (rel instanceof EntityConnectionData) {
			if (selectedConnections != null
					&& selectedConnections.contains(rel)) {
				return lineWidth;
			}
		}
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.zest.core.viewers.IConnectionStyleProvider#getHighlightColor(java.lang.Object)
	 */
	@Override
	public Color getHighlightColor(Object rel) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.zest.core.viewers.IConnectionStyleProvider#getColor(java.lang.Object)
	 */
	@Override
	public Color getColor(Object rel) {
		return Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.zest.core.viewers.IConnectionStyleProvider#getConnectionStyle(java.lang.Object)
	 */
	@Override
	public int getConnectionStyle(Object rel) {
		if (isRouteNode(rel)) {
		}
		return ZestStyles.CONNECTIONS_DOT | ZestStyles.CONNECTIONS_DIRECTED;
	}
}
