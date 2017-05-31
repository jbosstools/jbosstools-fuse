/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.qe.reddeer.ext;

import java.util.List;

import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.views.IViewCategory;
import org.eclipse.ui.views.IViewDescriptor;
import org.hamcrest.Matcher;
import org.jboss.reddeer.common.condition.AbstractWaitCondition;
import org.jboss.reddeer.common.logging.Logger;
import org.jboss.reddeer.common.matcher.RegexMatcher;
import org.jboss.reddeer.common.wait.WaitUntil;
import org.jboss.reddeer.common.wait.WaitWhile;
import org.jboss.reddeer.core.condition.ShellWithTextIsActive;
import org.jboss.reddeer.core.exception.CoreLayerException;
import org.jboss.reddeer.core.handler.ViewHandler;
import org.jboss.reddeer.core.handler.WidgetHandler;
import org.jboss.reddeer.core.handler.WorkbenchPartHandler;
import org.jboss.reddeer.core.lookup.WidgetLookup;
import org.jboss.reddeer.core.matcher.WithTextMatcher;
import org.jboss.reddeer.core.matcher.WithTextMatchers;
import org.jboss.reddeer.swt.api.CTabItem;
import org.jboss.reddeer.swt.api.Menu;
import org.jboss.reddeer.swt.impl.button.OkButton;
import org.jboss.reddeer.swt.impl.ctab.DefaultCTabItem;
import org.jboss.reddeer.swt.impl.menu.ShellMenu;
import org.jboss.reddeer.swt.impl.shell.DefaultShell;
import org.jboss.reddeer.swt.impl.tree.DefaultTreeItem;
import org.jboss.reddeer.workbench.api.View;
import org.jboss.reddeer.workbench.api.WorkbenchPart;
import org.jboss.reddeer.workbench.exception.WorkbenchLayerException;
import org.jboss.reddeer.workbench.impl.shell.WorkbenchShell;
import org.jboss.reddeer.workbench.impl.view.AbstractView;

public class AbstractViewExt implements View {

	private static final String SHOW_VIEW = "Show View";
	
	protected static final Logger log = Logger.getLogger(AbstractView.class);
	
	protected String[] path;

	protected Matcher<String> viewNameMatcher;
	
	protected CTabItem cTabItem;
	
	/**
	 * Initialize view with given viewToolTip. If view is opened than it will be
	 * focused
	 * 
	 * @param viewToolTip
	 *            of view to initialize
	 */
	public AbstractViewExt(String viewToolTip) {
		this(new WithTextMatcher(viewToolTip));
	}

	/**
	 * Initialize view with given viewToolTip matcher. If view is opened than it
	 * will be focused
	 * 
	 * @param viewToolTip
	 *            matcher of view to initialize
	 */
	public AbstractViewExt(Matcher<String> viewToolTip) {
		this.viewNameMatcher = viewToolTip;
		path = findRegisteredViewPath(viewToolTip);
		cTabItem = getViewCTabItem();
	}

	/* (non-Javadoc)
	 * @see org.jboss.reddeer.workbench.api.WorkbenchPart#maximize()
	 */
	@Override
	public void maximize() {
		activate();
		log.info("Maximize view");
		WorkbenchPartHandler.getInstance().performAction(ActionFactory.MAXIMIZE);
	}

	/* (non-Javadoc)
	 * @see org.jboss.reddeer.workbench.api.WorkbenchPart#minimize()
	 */
	@Override
	public void minimize() {
		activate();
		log.info("Minimize view");
		WorkbenchPartHandler.getInstance().performAction(ActionFactory.MINIMIZE);
	}

	/**
	 * {@link WorkbenchPart.restore}
	 */
	@Override
	public void restore() {
		activate();
		log.info("Restore view");
		// in order to restore maximized window maximized action has to be
		// called
		WorkbenchPartHandler.getInstance().performAction(ActionFactory.MAXIMIZE);
	}

	/**
	 * {@link WorkbenchPart.activate}
	 */
	@Override
	public void activate() {
		log.info("Activate view " + viewTitle());
		cTabItemIsNotNull();
		getViewCTabItem().activate();
		ViewHandler.getInstance().focusChildControl();
	}

	/**
	 * Gets the view c tab item.
	 *
	 * @return the view c tab item
	 */
	protected CTabItem getViewCTabItem(){
		if (cTabItem != null && cTabItem.isDisposed()){
			cTabItem = null;
		}
		if (cTabItem == null) {
			if (!isOpened()){
				return cTabItem;
			}
			log.debug("Looking up CTabItem with text " + viewTitle());
			
			// @FIXME Should be replaced by RegexMatcher
			// for example: Properties view could be marked as dirty - *Properties
			try {
				cTabItem = new DefaultCTabItem(new WorkbenchShell(), viewNameMatcher);
			} catch (CoreLayerException e) {
				cTabItem = new DefaultCTabItem(new WorkbenchShell(), new WithTextMatcher("*" + viewTitle()));
			}
			
		}
		return cTabItem; 
	}
	
	private String[] findRegisteredViewPath(Matcher<String> title) {

		IViewDescriptor viewDescriptor = findView(title);
		IViewCategory categoryDescriptor = findViewCategory(viewDescriptor);
		return pathForView(viewDescriptor, categoryDescriptor);

	}

	private IViewDescriptor findView(Matcher<String> title) {
		IViewDescriptor[] views = PlatformUI.getWorkbench().getViewRegistry()
				.getViews();
		for (IViewDescriptor view : views) {
			if (title.matches(view.getLabel())) {
				return view;
			}
		}

		throw new WorkbenchLayerException("View \"" + title
				+ "\" is not registered in workbench");
	}

	private IViewCategory findViewCategory(IViewDescriptor viewDescriptor) {
		IViewCategory[] categories = PlatformUI.getWorkbench()
				.getViewRegistry().getCategories();
		for (IViewCategory category : categories) {
			for (IViewDescriptor ivd : category.getViews()) {
				if (ivd.getId().equals(viewDescriptor.getId())) {
					return category;
				}
			}
		}

		throw new WorkbenchLayerException("View \"" + viewDescriptor.getLabel()
				+ "\" is not registered in any category");
	}

	private String[] pathForView(IViewDescriptor viewDescriptor,
			IViewCategory categoryDescriptor) {
		String[] path = new String[2];
		path[0] = categoryDescriptor.getLabel();
		path[1] = viewDescriptor.getLabel();
		return path;
	}

	private String viewTitle() {
		return path[path.length - 1];
	}

	/* (non-Javadoc)
	 * @see org.jboss.reddeer.workbench.api.WorkbenchPart#close()
	 */
	@Override
	public void close() {
		activate();
		log.info("Close view");
		cTabItem.close();
		cTabItem = null;
	}

	/* (non-Javadoc)
	 * @see org.jboss.reddeer.workbench.api.View#open()
	 */
	@Override
	public void open() {
		log.info("Open view " + viewTitle());
		// view is not opened, it has to be opened via menu
		if (getViewCTabItem() == null){
			log.info("Open " + viewTitle() + " view via menu.");
			openViaMenu();
		}
		activate();
	}

	private void openViaMenu() {
		WithTextMatchers m = new WithTextMatchers(new RegexMatcher[] {
				new RegexMatcher("Window.*"),
				new RegexMatcher("Show View.*"),
				new RegexMatcher("Other...*") });
		Menu menu = new ShellMenu(m.getMatchers());
		menu.select();
		new DefaultShell(SHOW_VIEW);
		new DefaultTreeItem(path).select();
		new OkButton().click();
		new WaitWhile(new ShellWithTextIsActive(SHOW_VIEW));
		new WaitUntil(new ViewCTabIsAvailable());
	}

	private class ViewCTabIsAvailable extends AbstractWaitCondition {

		@Override
		public boolean test() {
			try {
				getViewCTabItem();
				return true;
			} catch (Exception e){
				return false;
			}
		}

		@Override
		public String description() {
			return "view's CTabItem is available";
		}
	}
	
	private void cTabItemIsNotNull() {
		log.debug("View's cTabItem is found: " 
				+ (cTabItem != null ? true : false));
		if (cTabItem == null) {
			throw new WorkbenchLayerException("Cannot perform the specified "
					+ "operation before initialization "
					+ "provided by open method");
		}
	}

	/**
	 * Returns the title of the view.
	 *
	 * @return Title of the view
	 */
	public String getTitle() {
		return viewTitle();
	}

	/* (non-Javadoc)
	 * @see org.jboss.reddeer.workbench.api.View#isVisible()
	 */
	@Override
	public boolean isVisible() {
		return getViewCTabItem().isShowing();
	}
	
	/* (non-Javadoc)
	 * @see org.jboss.reddeer.workbench.api.View#isOpened()
	 */
	@Override
	public boolean isOpened() {
		List<org.eclipse.swt.custom.CTabItem> tabs = WidgetLookup.getInstance().activeWidgets(new WorkbenchShell(), org.eclipse.swt.custom.CTabItem.class);
		for (org.eclipse.swt.custom.CTabItem tab : tabs){
			String text = WidgetHandler.getInstance().getText(tab).replace("*", "").trim(); // for example: Properties view could be marked as dirty - *Properties
			if (viewTitle().equals(text)){
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if the view is active. This method is not supported due to a bug.
	 * See https://bugs.eclipse.org/bugs/show_bug.cgi?id=468948 for details.
	 *
	 * @return true, if is active
	 * @throws UnsupportedOperationException the unsupported operation exception
	 */
	public boolean isActive(){
		throw new UnsupportedOperationException("Method isActive is not supported due to the bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=468948");
	}
}
