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

package org.fusesource.ide.camel.editor.editor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Point;
import org.eclipse.zest.layouts.InvalidLayoutConfiguration;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.LayoutBendPoint;
import org.eclipse.zest.layouts.LayoutEntity;
import org.eclipse.zest.layouts.LayoutRelationship;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.HorizontalTreeLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;
import org.eclipse.zest.layouts.constraints.LayoutConstraint;
import org.fusesource.ide.camel.editor.Activator;
import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.Flow;
import org.fusesource.ide.camel.model.RouteContainer;
import org.fusesource.ide.camel.model.RouteSupport;


public class LayoutHelper {

	private static final int ROUTE_MAX_HEIGHT = 50;
	protected static final int ROUTE_MAX_WIDTH = 300;
	private static final int ROUTE_HEIGHT_PADDING = 150;
	protected static final int ROUTE_WIDTH_PADDING = 250;
	
	protected static final int MINIMUM_CANVAS_HEIGHT = 350;
	protected static final int MINIMUM_CANVAS_WIDTH = 400;


	protected int defaultNodeWidth = AbstractNode.DEFAULT_LAYOUT.width;
	protected int defaultNodeHeight = AbstractNode.DEFAULT_LAYOUT.height;
	protected int defaultRouteDepth = defaultNodeHeight * 5;
	protected int defaultNodeOffset = 10;
	protected int defaultRouteOffset = 4;
	protected int defaultRouteWidthMargin = 14;
	protected int defaultRouteHeightMargin = 14;
	private LayoutAlgorithm layout;
	private final RouteContainer model;
	private final Set<AbstractNode> nodes;
	private LayoutEntity[] entities;
	private LayoutRelationship[] relations;
	private double x = 0;
	private double y = 0;
	private int width;
	private int height;
	private boolean asynchronous;
	private boolean continuous;
	private Map<AbstractNode, LayoutEntity> map = new HashMap<AbstractNode, LayoutEntity>();
	private boolean horizontal = true;

	/**
	 * Performs the layout; detecting if the size is too small to properly lay
	 * out the diagram, increasing the size as required
	 */
	public static void layout(RouteContainer model, Point size) {
		if (size.x < MINIMUM_CANVAS_WIDTH) size.x = MINIMUM_CANVAS_WIDTH;
		if (size.y < MINIMUM_CANVAS_HEIGHT) size.y = MINIMUM_CANVAS_HEIGHT;
		int heightIncrease = 0;
		for (int i = 0; i < 7; i++) {
			if (heightIncrease > 0) {
				// lets increase the screen size
				size.x += Math.floor(heightIncrease * size.x / size.y) + 1;
				size.y += heightIncrease;

				Activator.getLogger().debug("Increasing size to: x = " + size.x + " y = " + size.y);
			}
			LayoutHelper layoutHelper = new LayoutHelper(model, size.x, size.y);
			layoutHelper.doLayout();

			// lets check for overlapping gaps
			heightIncrease = calculateHeightIncrease(model);
			if (heightIncrease <= 0) {
				break;
			}
		}
		Rectangle r = model.getLayout();
		if (r != null) {
			r = r.getCopy();
			if (size.x > r.width) {
				r.width = size.x;
			}
			if (size.y > r.height) {
				r.height = size.y;
			}
			model.setLayout(r);
		}
	}

	private static int calculateHeightIncrease(AbstractNode model) {
		int answer = 0;
		int nextY = 0;
		boolean first = true;
		for (AbstractNode node : model.getChildren()) {
			Rectangle r = node.getLayout();
			if (r != null) {
				if (first) {
					first = false;
				} else {
					int delta = nextY - r.y;
					if (delta > 0) {
						answer += delta;
					}
				}
				nextY = r.y + r.height + 100;
			}
		}
		return answer;
	}

	public LayoutHelper(RouteContainer model, int width, int height) {
		super();
		this.model = model;
		this.width = width;
		this.height = height;

		// provide some reasonable defaults if we're laying out before we're
		// visible
		// TODO we should really avoid this!!!
		if (width < 10) {
			this.width = MINIMUM_CANVAS_WIDTH;
		}
		if (height < 10) {
			this.height = MINIMUM_CANVAS_HEIGHT;
		}

		/*
		 * if (width < MINIMUM_CANVAS_WIDTH) { this.width =
		 * MINIMUM_CANVAS_WIDTH; } if (height < MINIMUM_CANVAS_HEIGHT) {
		 * this.height = MINIMUM_CANVAS_HEIGHT; }
		 */

		nodes = model.getDescendents();

		Set<Flow> flows = new HashSet<Flow>();
		entities = new LayoutEntity[nodes.size()];
		int idx = 0;
		for (AbstractNode node : nodes) {
			LayoutEntity entity = createLayoutEntity(node);
			map.put(node, entity);
			entities[idx++] = entity;
			flows.addAll(node.getAllConnections());
		}
		relations = new LayoutRelationship[flows.size()];
		idx = 0;
		for (Flow flow : flows) {
			relations[idx++] = createLayoutRelation(flow);
		}
	}

	public void doLayout() {
		try {
			ensureNodesAreNotOnTopOfEachOther(nodes);

			getLayout().applyLayout(entities, relations, x, y, width, height, asynchronous, continuous);

			moveNodesToTop();

		} catch (InvalidLayoutConfiguration e) {
			Activator.getLogger().error("Failed to layout graph: " + e, e);
		}
	}

	/**
	 * Lets try move the shapes up near the top of the screen
	 */
	protected void moveNodesToTop() {
		for (AbstractNode node : model.getChildren()) {
			setRouteSize(node);
		}

		// TODO should we ensure the routes are not covering each other??
	}

	/**
	 * Sizes the route size based on the new layout
	 */
	protected void setRouteSize(AbstractNode route) {
		int x_min = 0, y_min = 0, maxWidth = ROUTE_MAX_WIDTH, maxHeight = ROUTE_MAX_HEIGHT;

		boolean first = true;
		for (AbstractNode node : route.getChildren()) {
			node.layout();
			Rectangle r = node.getLayout();
			if (first) {
				first = false;
				x_min = r.x;
				y_min = r.y;
				maxWidth = r.x + r.width;
				maxHeight = r.y + r.height;
			} else {
				x_min = Math.min(x_min, r.x);
				y_min = Math.min(y_min, r.y);
				maxWidth = Math.max(maxWidth, r.x + r.width);
				maxHeight = Math.max(maxHeight, r.y + r.height);
			}
		}

		// finally make it a bit bigger
		x_min = Math.max(0, x_min - 5);
		y_min = Math.max(0, y_min - 5);
		maxWidth += ROUTE_WIDTH_PADDING;
		maxHeight += ROUTE_HEIGHT_PADDING;

		// lets subtract the current x/y from the width
		maxWidth -= x_min;
		maxHeight -= y_min;

		route.setLayout(new Rectangle(x_min, y_min, maxWidth, maxHeight));
	}

	protected void showRouteLayout() {
		List<AbstractNode> children = model.getChildren();
		if (children.size() > 0) {
			AbstractNode child = children.get(0);
			Activator.getLogger().debug("First child layout: " + child.getLayout() + " child: " + child);
		}
	}


	/**
	 * Lets put the shapes into a different area so that the layout algorithm
	 * doesn't think the shapes are meant to be on top of each other
	 */
	private void ensureNodesAreNotOnTopOfEachOther(Set<AbstractNode> nodes) {
		int routeCount = 0;
		int nodeCount = 0;

		for (AbstractNode node : nodes) {
			Rectangle r = node.getLayout();
			if (r == null) {
				r = new Rectangle();
			} else {
				r = new Rectangle(r);
			}
			if (node instanceof RouteSupport) {
				if (r.x == 0 && r.y == 0) {
					// r.y = routeCount * defaultRouteDepth +
					// defaultRouteOffset;
					r.width = width - defaultRouteWidthMargin;
					// r.height = height - defaultRouteHeightMargin;
				}
				routeCount++;
				nodeCount = 0;
			} else if (node instanceof RouteContainer) {
				// don't change position...
			} else {
				if (r.x == 0 && r.y == 0) {
					r.x = nodeCount * defaultNodeWidth;
					// r.y = routeCount * defaultRouteDepth + defaultNodeOffset;
				}
				nodeCount++;
			}
			node.setLayout(r);
		}
	}

	// Properties

	public LayoutAlgorithm getLayout() {
		if (layout == null) {
			int style = LayoutStyles.NONE;
			// int style = LayoutStyles.NO_LAYOUT_NODE_RESIZING;
			// int style = LayoutStyles.NONE;
			if (horizontal) {
				layout = new HorizontalTreeLayoutAlgorithm(style);
			} else {
				layout = new TreeLayoutAlgorithm(style);
			}
		}
		return layout;
	}

	public void setLayout(LayoutAlgorithm layout) {
		this.layout = layout;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public boolean isAsynchronous() {
		return asynchronous;
	}

	public void setAsynchronous(boolean asynchronous) {
		this.asynchronous = asynchronous;
	}

	public boolean isContinuous() {
		return continuous;
	}

	public void setContinuous(boolean continuous) {
		this.continuous = continuous;
	}

	public RouteContainer getModel() {
		return model;
	}

	// Implementation methods

	protected LayoutEntity getLayoutEntity(AbstractNode node) {
		if (node != null) {
			return map.get(node);
		}
		return null;
	}

	protected LayoutRelationship createLayoutRelation(final Flow flow) {
		return new LayoutRelationship() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.zest.layouts.LayoutItem#setGraphData(java.lang.Object
			 * )
			 */
			@Override
			public void setGraphData(Object o) {
				// TODO Auto-generated method stub

			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.zest.layouts.LayoutItem#getGraphData()
			 */
			@Override
			public Object getGraphData() {
				// TODO Auto-generated method stub
				return null;
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.zest.layouts.LayoutRelationship#getSourceInLayout()
			 */
			@Override
			public LayoutEntity getSourceInLayout() {
				return getLayoutEntity(flow.getSource());
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.zest.layouts.LayoutRelationship#getDestinationInLayout
			 * ()
			 */
			@Override
			public LayoutEntity getDestinationInLayout() {
				return getLayoutEntity(flow.getTarget());
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.zest.layouts.LayoutRelationship#setLayoutInformation
			 * (java.lang.Object)
			 */
			@Override
			public void setLayoutInformation(Object layoutInformation) {
				// TODO Auto-generated method stub

			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.zest.layouts.LayoutRelationship#getLayoutInformation
			 * ()
			 */
			@Override
			public Object getLayoutInformation() {
				// TODO Auto-generated method stub
				return null;
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.zest.layouts.LayoutRelationship#setBendPoints(org
			 * .eclipse.zest.layouts.LayoutBendPoint[])
			 */
			@Override
			public void setBendPoints(LayoutBendPoint[] bendPoints) {
				// TODO Auto-generated method stub

			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.zest.layouts.LayoutRelationship#clearBendPoints()
			 */
			@Override
			public void clearBendPoints() {
				// TODO Auto-generated method stub

			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.zest.layouts.LayoutRelationship#populateLayoutConstraint
			 * (org.eclipse.zest.layouts.constraints.LayoutConstraint)
			 */
			@Override
			public void populateLayoutConstraint(LayoutConstraint constraint) {
				// TODO Auto-generated method stub

			}
		};
	}

	protected LayoutEntity createLayoutEntity(AbstractNode node) {
		return new NodeLayoutEntity(node);
	}

	public static class NodeLayoutEntity implements LayoutEntity {
		private final AbstractNode node;
		private Object graphData;
		private Object layoutInformation;

		public NodeLayoutEntity(AbstractNode node) {
			this.node = node;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(Object that) {
			if (this == that) {
				return 0;
			}
			return 0;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.zest.layouts.LayoutItem#setGraphData(java.lang.Object)
		 */
		@Override
		public void setGraphData(Object graphData) {
			this.graphData = graphData;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.zest.layouts.LayoutItem#getGraphData()
		 */
		@Override
		public Object getGraphData() {
			return graphData;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.zest.layouts.LayoutEntity#setLocationInLayout(double,
		 * double)
		 */
		@Override
		public void setLocationInLayout(double x, double y) {
			Rectangle r = node.getLayout();
			r.x = (int) Math.round(x);
			r.y = (int) Math.round(y);
			Rectangle c = node.getLayout();
			if (c != null) {
				r.width = c.width;
				r.height = c.height;
			}
			node.setLayout(r);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.zest.layouts.LayoutEntity#setSizeInLayout(double,
		 * double)
		 */
		@Override
		public void setSizeInLayout(double width, double height) {
			if (isAllowResize()) {
				Rectangle r = node.getLayout();
				r.width = (int) Math.round(width);
				r.height = (int) Math.round(height);
				Rectangle c = node.getLayout();
				if (c != null) {
					r.x = c.x;
					r.y = c.y;
				}
				node.setLayout(r);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.zest.layouts.LayoutEntity#getXInLayout()
		 */
		@Override
		public double getXInLayout() {
			Rectangle r = node.getLayout();
			if (r != null)
				return r.x;
			return 0;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.zest.layouts.LayoutEntity#getYInLayout()
		 */
		@Override
		public double getYInLayout() {
			Rectangle r = node.getLayout();
			if (r != null)
				return r.y;
			return 0;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.zest.layouts.LayoutEntity#getWidthInLayout()
		 */
		@Override
		public double getWidthInLayout() {
			Rectangle r = node.getLayout();
			if (r != null)
				return r.width;
			return 0;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.zest.layouts.LayoutEntity#getHeightInLayout()
		 */
		@Override
		public double getHeightInLayout() {
			Rectangle r = node.getLayout();
			if (r != null)
				return r.height;
			return 0;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.zest.layouts.LayoutEntity#getLayoutInformation()
		 */
		@Override
		public Object getLayoutInformation() {
			return layoutInformation;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.zest.layouts.LayoutEntity#setLayoutInformation(java.lang
		 * .Object)
		 */
		@Override
		public void setLayoutInformation(Object layoutInformation) {
			this.layoutInformation = layoutInformation;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.zest.layouts.LayoutEntity#populateLayoutConstraint(org
		 * .eclipse.zest.layouts.constraints.LayoutConstraint)
		 */
		@Override
		public void populateLayoutConstraint(LayoutConstraint constraint) {
			// TODO Auto-generated method stub

		}

		public boolean isAllowResize() {
			// resizing the routes doesn't seem to grok the containment
			return false;
		}
	}

}
