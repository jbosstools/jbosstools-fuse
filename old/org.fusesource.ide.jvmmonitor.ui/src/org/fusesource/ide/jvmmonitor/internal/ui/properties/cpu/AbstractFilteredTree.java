/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved.
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.cpu;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.StatusLineContributionItem;
import org.eclipse.jface.util.Util;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.dialogs.PatternFilter;
import org.fusesource.ide.commons.ui.actions.ConfigureColumnsAction;
import org.fusesource.ide.jvmmonitor.core.cpu.IMethodNode;
import org.fusesource.ide.jvmmonitor.internal.ui.actions.CopyAction;
import org.fusesource.ide.jvmmonitor.internal.ui.actions.OpenDeclarationAction;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.cpu.actions.FindAction;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.cpu.actions.FocusOnAction;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.cpu.actions.ShowCallersCalleesAction;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.thread.ConfigurableFilteredTree;


/**
 * The abstract filtered tree.
 */
abstract public class AbstractFilteredTree extends ConfigurableFilteredTree {

	/** The action bars. */
	protected IActionBars actionBars;

	/** The open action. */
	OpenDeclarationAction openAction;

	/** The find action. */
	FindAction findAction;

	/** The copy action. */
	CopyAction copyAction;

	/** The focus on frame action. */
	FocusOnAction focusOnFrameAction;

	/** The show callers/callees action. */
	ShowCallersCalleesAction showCallersCalleesAction;

	/** The configure columns action. */
	protected ConfigureColumnsAction configureColumnsAction;

	/**
	 * The constructor.
	 * 
	 * @param parent
	 *            The parent composite
	 * @param actionBars
	 *            The action bars
	 */
	protected AbstractFilteredTree(Composite parent, IActionBars actionBars) {
		super(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION
				| SWT.MULTI, new PatternFilter(), true);

		this.actionBars = actionBars;

		createContextMenu();
		setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

		addListeners();
	}

	/*
	 * @see FilteredTree#createControl(Composite, int)
	 */
	@Override
	protected void createControl(Composite composite, int treeStyle) {
		super.createControl(composite, treeStyle);

		// adjust the indentation of filter composite
		GridData data = (GridData) filterComposite.getLayoutData();
		data.horizontalIndent = 2;
		data.verticalIndent = 2;
		filterComposite.setLayoutData(data);
	}

	/*
	 * @see IConfigurableColumn#getId()
	 */
	@Override
	public String getColumnConfigurationId() {
		return getClass().getName();
	}

	/**
	 * Updates the status line.
	 * 
	 * @param selection
	 *            the selection
	 */
	public void updateStatusLine(IStructuredSelection selection) {
		IStatusLineManager manager = actionBars.getStatusLineManager();

		IContributionItem[] items = manager.getItems();
		StatusLineContributionItem selfTimeStatusLineItem = null;
		for (IContributionItem item : items) {
			if (item instanceof StatusLineContributionItem) {
				selfTimeStatusLineItem = (StatusLineContributionItem) item;
			}
		}

		// create the status line
		if (selfTimeStatusLineItem == null) {
			selfTimeStatusLineItem = new StatusLineContributionItem(
					"SelfTimeContributionItem"); //$NON-NLS-1$
			manager.add(selfTimeStatusLineItem);
		}

		// clear the status line
		if (selection == null) {
			selfTimeStatusLineItem.setText(Util.ZERO_LENGTH_STRING);
			return;
		}

		// set text on status line
		double percentage = 0;
		double time = 0;
		for (Object object : selection.toArray()) {
			if (object instanceof IMethodNode) {
				percentage += ((IMethodNode) object).getSelfTimeInPercentage();
				time += ((IMethodNode) object).getSelfTime();
			}
		}
		String text = Util.ZERO_LENGTH_STRING;
		if (percentage != 0) {
			text = String.format("Self Time: %.0fms  %.1f", time, percentage) + '%'; //$NON-NLS-1$
		}
		selfTimeStatusLineItem.setText(text);
	}

	/**
	 * Gets the viewer type.
	 * 
	 * @return The viewer type
	 */
	abstract public ViewerType getViewerType();

	/**
	 * Configure the given tree.
	 */
	@Override
	abstract protected void configureTree();

	/**
	 * Creates the context menu.
	 */
	protected void createContextMenu() {

		// create actions
		openAction = OpenDeclarationAction.createOpenDeclarationAction(actionBars);
		copyAction = (CopyAction) actionBars
				.getGlobalActionHandler(ActionFactory.COPY.getId());
		findAction = (FindAction) actionBars
				.getGlobalActionHandler(ActionFactory.FIND.getId());
		focusOnFrameAction = new FocusOnAction(this);
		showCallersCalleesAction = new ShowCallersCalleesAction(getViewer());
		configureColumnsAction = new ConfigureColumnsAction(this);

		// create menu manager
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				focusOnFrameAction.aboutToShow();
				manager.add(openAction);
				manager.add(new Separator());
				manager.add(copyAction);
				manager.add(findAction);
				manager.add(focusOnFrameAction);
				manager.add(showCallersCalleesAction);
				manager.add(new Separator());
				manager.add(configureColumnsAction);
				addMenus(manager);
			}
		});

		// create context menu
		Menu menu = menuMgr.createContextMenu(getViewer().getControl());
		getViewer().getControl().setMenu(menu);
	}

	/**
	 * Add menus.
	 * 
	 * @param manager
	 *            The menu manager
	 */
	protected void addMenus(IMenuManager manager) {
		// do nothing
	}


	/**
	 * Adds the listeners.
	 */
	private void addListeners() {
		TreeViewer viewer = getViewer();
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				openAction.selectionChanged(event);
				copyAction.selectionChanged(event);
			}
		});

		// set viewer not when tree item is selected but when tree gets focus
		viewer.getControl().addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				findAction.setViewer(getViewer(), getViewerType());
			}
		});

		viewer.addSelectionChangedListener(showCallersCalleesAction);
		viewer.addOpenListener(new IOpenListener() {
			@Override
			public void open(OpenEvent event) {
				openAction.run();
			}
		});
	}

	/**
	 * The CPU viewer type.
	 */
	public enum ViewerType {

		/** The call tree. */
		CallTree,

		/** The hot spots. */
		HotSpots,

		/** The caller. */
		Caller,

		/** The callee. */
		Callee;
	}
}
