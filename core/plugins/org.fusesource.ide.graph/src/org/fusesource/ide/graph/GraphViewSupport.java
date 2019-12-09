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

package org.fusesource.ide.graph;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.zest.core.viewers.AbstractZoomableViewer;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.viewers.IZoomableWorkbenchPart;
import org.eclipse.zest.core.viewers.ZoomContributionViewItem;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.CompositeLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.DirectedGraphLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.HorizontalTreeLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.RadialLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;
import org.fusesource.ide.foundation.ui.util.Viewers;


public abstract class GraphViewSupport extends ViewPart implements IZoomableWorkbenchPart {

	protected GraphViewer viewer;

	String helpContextId = "org.fusesource.ide.camel.editor.viewer";

	private GraphLabelProviderSupport graphLabelProvider;
	private IAction selectAllAction;
	private IAction showIconAction;
	private IAction wrapLabelAction;
	private IAction radialLayoutAction;
	private IAction showLegendAction;
	private Action doubleClickAction;
	private ZoomContributionViewItem zoomContributionItem;
	private Action directedLayout;
	private Action horizontalTree;
	private Action radialLayout;
	private Action springLayout;
	private Action verticalTree;
	private ZoomContributionViewItem toolbarZoomContributionViewItem;
	private GraphFilter graphFilter;
	private boolean setLayoutChecked = false;

	public GraphViewSupport() {
		graphFilter = createGraphFilter();
	}

	public GraphViewer getViewer() {
		return viewer;
	}

	@Override
	public AbstractZoomableViewer getZoomableViewer() {
		return viewer;
	}

	protected abstract void doubleClickSelection(ISelection selection);

	protected abstract IContentProvider createGraphContentProvider();

	protected abstract GraphLabelProviderSupport createGraphLabelProvider();

	protected void setInputAndSelection(Object input, Object selection) {
		if (isViewerValid()) {
			viewer.setInput(input);
			if (selection != null) viewer.setSelection(new StructuredSelection(selection));
			Viewers.refresh(viewer);
		}
	}

	protected void setSelectedObject(Object node) {
		if (isViewerValid()) {
			viewer.setInput(node);
			IStructuredContentProvider contentProvider = (IStructuredContentProvider) viewer.getContentProvider();
			if (node != null) viewer.setSelection(new StructuredSelection(contentProvider.getElements(node)));
			Viewers.refresh(viewer);
		}
	}

	protected void setSelectedObjectOnly(Object node) {
		if (isViewerValid()) {
			viewer.setInput(node);
			if (node != null) viewer.setSelection(new StructuredSelection(node));
			Viewers.refresh(viewer);
		}
	}

	protected boolean isViewerValid() {
		Control control = viewer.getControl();
		boolean valid = control != null && !control.isDisposed();
		return valid;
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	@Override
	public void createPartControl(Composite parent) {
		class MyGraphViewer extends GraphViewer {
			public MyGraphViewer(Composite parent, int style) {
				super(parent, style);
				Graph graphControl = new Graph(parent, style) {
					/*
					 * (non-Javadoc)
					 * 
					 * @see org.eclipse.draw2d.FigureCanvas#computeSize(int,
					 * int, boolean)
					 */
					@Override
					public Point computeSize(int wHint, int hHint, boolean changed) {
						return new Point(0, 0);
					}
				};
				setControl(graphControl);
			}
		}

		ViewerFilter[] filters = createViewFilters();

		if (filters != null) {
			GridLayout layout = new GridLayout(2, false);
			parent.setLayout(layout);
			Label searchLabel = new Label(parent, SWT.NONE);
			searchLabel.setText("Search: ");
			final Text searchText = new Text(parent, SWT.BORDER | SWT.SEARCH);
			searchText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
			searchText.addKeyListener(new KeyAdapter() {
				@Override
				public void keyReleased(KeyEvent ke) {
					graphFilter.setSearchText(searchText.getText());
					if (Viewers.isValid(viewer)) {
						viewer.refresh();
						viewer.getGraphControl().applyLayout();
					}
				}
			});

		} else {
			GridLayout layout = new GridLayout(1, false);
			layout.marginHeight = 0;
			layout.marginWidth = 0;
			parent.setLayout(layout);
		}

		viewer = new MyGraphViewer(parent, SWT.BORDER);

		IContentProvider graphContentProvider = createGraphContentProvider();
		GraphLabelProviderSupport graphLabelProvider = createGraphLabelProvider();

		viewer.setContentProvider(graphContentProvider);
		viewer.setLabelProvider(graphLabelProvider);


		if (filters != null) {
			GridData gridData = new GridData();
			gridData.verticalAlignment = GridData.FILL;
			gridData.horizontalSpan = 2;
			gridData.grabExcessHorizontalSpace = true;
			gridData.grabExcessVerticalSpace = true;
			gridData.horizontalAlignment = GridData.FILL;
			viewer.getControl().setLayoutData(gridData);
		} else {
			viewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		}

		viewer.setConnectionStyle(ZestStyles.CONNECTIONS_DIRECTED);
		// switch off the animation
		viewer.setNodeStyle(ZestStyles.NODES_NO_ANIMATION);
		
		LayoutAlgorithm layout = new DirectedDiagramViewLayoutAlgorithm(SWT.VERTICAL | LayoutStyles.NO_LAYOUT_NODE_RESIZING | LayoutStyles.ENFORCE_BOUNDS);
		viewer.setLayoutAlgorithm(layout);
		
//		viewer.setLayoutAlgorithm(new CompositeLayoutAlgorithm(SWT.VERTICAL | LayoutStyles.NO_LAYOUT_NODE_RESIZING | LayoutStyles.ENFORCE_BOUNDS, 
//				new LayoutAlgorithm[] { 
//					new DirectedGraphLayoutAlgorithm(SWT.VERTICAL | LayoutStyles.NO_LAYOUT_NODE_RESIZING | LayoutStyles.ENFORCE_BOUNDS), 
//					new BigHorizontalShift(SWT.VERTICAL | LayoutStyles.NO_LAYOUT_NODE_RESIZING | LayoutStyles.ENFORCE_BOUNDS) }));
		
		/*
		viewer.setLayoutAlgorithm(new CompositeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING
				| LayoutStyles.ENFORCE_BOUNDS, //
				new LayoutAlgorithm[] { //
				new DirectedGraphLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING | LayoutStyles.ENFORCE_BOUNDS), //
				new HorizontalShift(LayoutStyles.NO_LAYOUT_NODE_RESIZING | LayoutStyles.ENFORCE_BOUNDS) }));

		 */

		if (graphLabelProvider instanceof ISelectionChangedListener) {
			viewer.addSelectionChangedListener((ISelectionChangedListener) graphLabelProvider);
		}

		if (filters != null) {
			viewer.setFilters(filters);
		}

		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), helpContextId);
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
	}

	protected ViewerFilter[] createViewFilters() {
		return new ViewerFilter[] { graphFilter };
	}

	private void hookContextMenu() {
		MenuManager manager = new MenuManager("#PopupMenu");
		manager.setRemoveAllWhenShown(true);
		manager.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				manager.removeAll();
				GraphViewSupport.this.fillContextMenu(manager);
			}
		});
		Menu menu = manager.createContextMenu(viewer.getControl());

		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(manager, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());

	}

	private void fillLocalPullDown(IMenuManager manager) {
		fillContextMenu(manager);
	}

	private void fillContextMenu(IMenuManager manager) {
		/*
		 * manager.add(selectAllAction);
		 * 
		 * if (!viewer.getSelection().isEmpty()) { manager.add(new Separator());
		 * }
		 * 
		 * manager.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
		 * manager.add(new Separator());
		 */

		/*
		 * { MenuManager subMenu = new MenuManager("Presentation");
		 * 
		 * subMenu.add(showIconAction); subMenu.add(wrapLabelAction);
		 * 
		 * manager.add(subMenu); }
		 */

		{
			MenuManager subMenu = new MenuManager("Layout");
			manager.add(subMenu);

			addRadioMenuItem(subMenu, directedLayout);
			addRadioMenuItem(subMenu, horizontalTree);
			addRadioMenuItem(subMenu, springLayout);
			addRadioMenuItem(subMenu, verticalTree);

			// kinda different???
			addRadioMenuItem(subMenu, radialLayout);
		}

		manager.add(new Separator());
		if (showLegendAction != null) {
			manager.add(showLegendAction);
		}

		if (zoomContributionItem != null) {
			manager.add(new Separator());
			manager.add(zoomContributionItem);
		}

		manager.add(new Separator());
		manager.add(toolbarZoomContributionViewItem);

	}

	private void addRadioMenuItem(MenuManager subMenu, final Action action) {
		subMenu.add(new ContributionItem() {

			@Override
			public void fill(Menu menu, int index) {
				MenuItem menuItem = new MenuItem(menu, SWT.RADIO);
				menuItem.setText(action.getText());
				menuItem.setAccelerator(action.getAccelerator());
				menuItem.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						action.run();
					}
				});
				if (!setLayoutChecked) {
					setLayoutChecked = true;
					menuItem.setSelection(true);
				}

			}
		});
		// menuItem.setImage(image)e(action.getImageDescriptor().getImageData().get)
	}

	protected Action createLayoutMenuAction(final String text, final LayoutAlgorithm layoutAlgorithm) {
		Action action = new Action(text, SWT.RADIO) {
			@Override
			public void run() {
				setChecked(true);
				viewer.setLayoutAlgorithm(new CompositeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING,
						new LayoutAlgorithm[] { layoutAlgorithm,
						new BigHorizontalShift(LayoutStyles.NO_LAYOUT_NODE_RESIZING) }));
				viewer.getGraphControl().applyLayout();
			}
		};
		return action;
	}

	private void fillLocalToolBar(IToolBarManager manager) {
	}

	private void makeActions() {
		toolbarZoomContributionViewItem = new ZoomContributionViewItem(this);

		directedLayout = createLayoutMenuAction("Directed", new DirectedDiagramViewLayoutAlgorithm(SWT.VERTICAL | LayoutStyles.NO_LAYOUT_NODE_RESIZING | LayoutStyles.ENFORCE_BOUNDS));
		directedLayout.setChecked(true);

		// createLayoutMenuAction("Grid", new
		// GridLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING));
		// createLayoutMenuAction("Horizontal", new
		// HorizontalLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING));
		horizontalTree = createLayoutMenuAction("Horizontal Tree", new HorizontalTreeLayoutAlgorithm(
				LayoutStyles.NO_LAYOUT_NODE_RESIZING));
		radialLayout = createLayoutMenuAction("Radial", new RadialLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING));
		springLayout = createLayoutMenuAction("Spring", new SpringLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING));
		// createLayoutMenuAction("Vertical", new
		// VerticalLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING));
		verticalTree = createLayoutMenuAction("Vertical Tree", new TreeLayoutAlgorithm(
				LayoutStyles.NO_LAYOUT_NODE_RESIZING));

		/*
		 * showLegendAction = new Action("Show UI Legend") { public void run() {
		 * RouteGraphLegendPopup popup = new
		 * RouteGraphLegendPopup(getEditor().getSite().getShell());
		 * popup.open(); } };
		 */

		selectAllAction = new Action("Select &All") {
			/*
			 * s (non-Javadoc)
			 * 
			 * @see org.eclipse.jface.action.Action#run()
			 */
			@Override
			public void run() {
				// TODO
			}
		};

		showIconAction = new Action("Show &Icon", SWT.CHECK) {
			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.jface.action.Action#run()
			 */
			@Override
			public void run() {
				graphLabelProvider.setShowIcon(isChecked());
			}
		};
		showIconAction.setChecked(true);

		wrapLabelAction = new Action("&Wrap Label", SWT.CHECK) {
			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.jface.action.Action#run()
			 */
			@Override
			public void run() {
				graphLabelProvider.setWrapLabel(isChecked());
			}
		};
		wrapLabelAction.setChecked(true);

		radialLayoutAction = new Action("&Radial Layout", SWT.CHECK) {
			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.jface.action.Action#run()
			 */
			@Override
			public void run() {
				if (isChecked()) {
					viewer.setLayoutAlgorithm(new CompositeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING, //
							new LayoutAlgorithm[] { //
							new RadialLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING),
							new BigHorizontalShift(LayoutStyles.NO_LAYOUT_NODE_RESIZING), }));

				} else {
					viewer.setLayoutAlgorithm(new CompositeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING /*
					 * |
					 * LayoutStyles
					 * .
					 * ENFORCE_BOUNDS
					 */, //
					 new LayoutAlgorithm[] { //
							new DirectedGraphLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING /*
							 * |
							 * LayoutStyles
							 * .
							 * ENFORCE_BOUNDS
							 */), //
							 new BigHorizontalShift(LayoutStyles.NO_LAYOUT_NODE_RESIZING) }));
				}
				viewer.getGraphControl().applyLayout();
			}
		};
		radialLayoutAction.setChecked(false);

		doubleClickAction = new Action() {
			@Override
			public void run() {
				ISelection selection = viewer.getSelection();
				doubleClickSelection(selection);
			}

		};
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(event -> doubleClickAction.run());
	}

	protected GraphFilter createGraphFilter() {
		return new GraphFilter(this);
	}

	protected String getMessageTitle() {
		return "Route View";
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	protected void showMessage(String message) {
		MessageDialog.openInformation(viewer.getControl().getShell(), getMessageTitle(), message);
	}
}