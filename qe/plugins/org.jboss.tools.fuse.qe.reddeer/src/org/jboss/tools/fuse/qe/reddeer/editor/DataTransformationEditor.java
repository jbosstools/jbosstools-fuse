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
package org.jboss.tools.fuse.qe.reddeer.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.jboss.reddeer.common.logging.Logger;
import org.jboss.reddeer.common.wait.AbstractWait;
import org.jboss.reddeer.common.wait.TimePeriod;
import org.jboss.reddeer.common.wait.WaitUntil;
import org.jboss.reddeer.core.condition.ShellWithTextIsAvailable;
import org.jboss.reddeer.core.util.Display;
import org.jboss.reddeer.core.util.ResultRunnable;
import org.jboss.reddeer.swt.condition.WidgetIsEnabled;
import org.jboss.reddeer.swt.impl.button.PushButton;
import org.jboss.reddeer.swt.impl.combo.DefaultCombo;
import org.jboss.reddeer.swt.impl.ctab.DefaultCTabItem;
import org.jboss.reddeer.swt.impl.shell.DefaultShell;
import org.jboss.reddeer.swt.impl.styledtext.DefaultStyledText;
import org.jboss.reddeer.swt.impl.text.DefaultText;
import org.jboss.reddeer.swt.impl.toolbar.DefaultToolItem;
import org.jboss.reddeer.swt.impl.tree.DefaultTreeItem;
import org.jboss.reddeer.workbench.impl.editor.DefaultEditor;

/**
 * Data Transformation Editor
 * 
 * @author tsedmik
 */
public class DataTransformationEditor extends DefaultEditor {

	private List<Control> allWidgets;
	private Logger log = Logger.getLogger(DataTransformationEditor.class);

	public DataTransformationEditor(String title) {
		super(title);
	}

	/**
	 * Creates a basic transformation. NOTE: Supports XML and JSON data types.
	 * 
	 * @param source
	 *            Name of the source (root element)
	 * @param sourcePath
	 *            Path to the source element
	 * @param target
	 *            Name of the target (root element)
	 * @param targetPath
	 *            Path to the target element
	 */
	public void createTransformation(String source, String[] sourcePath, String target, String[] targetPath) {

		log.info("Create a new basic transformation");
		activate();
		new DefaultToolItem("Add a new mapping").click();
		log.info("Select a source entry: " + Arrays.toString(sourcePath));
		invokeMappingContextMenu(source, "Set property");
		new DefaultTreeItem(sourcePath).select();
		new PushButton("OK").click();
		log.info("Select a target entry: " + Arrays.toString(targetPath));
		invokeMappingContextMenu(target, "Set property");
		new DefaultTreeItem(targetPath).select();
		new WaitUntil(new WidgetIsEnabled(new PushButton("OK")));
		new PushButton("OK").click();
	}

	/**
	 * Creates a transformation 'variable' --> 'element'. NOTE: Supports XML and JSON data types.
	 * 
	 * @param source
	 *            Name of the source (root element)
	 * @param name
	 *            Name of the variable
	 * @param target
	 *            Name of the target (root element)
	 * @param targetPath
	 *            Path to the target element
	 */
	public void createVariableTransformation(String source, String name, String target, String[] targetPath) {

		log.info("Create a new variable transformation");
		activate();
		new DefaultToolItem("Add a new mapping").click();
		log.info("Select variable: " + name);
		invokeMappingContextMenu(source, "Set variable");
		new DefaultCombo().setSelection(name);
		new PushButton("OK").click();
		log.info("Select a target entry: " + Arrays.toString(targetPath));
		invokeMappingContextMenu(target, "Set property");
		new DefaultTreeItem(targetPath).select();
		new PushButton("OK").click();
	}

	/**
	 * Creates a transformation 'expression' --> 'element' NOTE: Supports XML and JSON data types.
	 * 
	 * @param source
	 *            Name of the source (root element)
	 * @param language
	 *            Expression language
	 * @param expression
	 *            Expression value
	 * @param target
	 *            Name of the target (root element)
	 * @param targetPath
	 *            Path to the target element
	 */
	public void createExpressionTransformation(String source, String language, String expression, String target,
			String[] targetPath) {

		log.info("Create a new expression transformation");
		activate();
		new DefaultToolItem("Add a new mapping").click();
		log.info("Set expression: " + expression + " (" + language + ")");
		invokeMappingContextMenu(source, "Set expression");
		new DefaultCombo().setSelection(language);
		new DefaultText().setText(expression);
		new PushButton("OK").click();
		log.info("Select a target entry: " + Arrays.toString(targetPath));
		invokeMappingContextMenu(target, "Set property");
		new DefaultTreeItem(targetPath).select();
		new PushButton("OK").click();
	}

	/**
	 * Creates a new variable. NOTE: Without support of setting its value. Default value (name) is used.
	 * 
	 * @param name
	 *            Name of the variable
	 */
	public void createNewVariable(String name) {

		log.info("Create a new variable: " + name);
		activate();
		AbstractWait.sleep(TimePeriod.NORMAL);
		new DefaultCTabItem("Variables").activate();
		new DefaultToolItem("Add a new variable").click();
		new WaitUntil(new ShellWithTextIsAvailable("Add Variable"));
		new DefaultShell("Add Variable");
		new DefaultText().setText(name);
		new PushButton("OK").click();
	}

	/**
	 * Search for all children widgets of given parent recursively
	 * 
	 * @param parent
	 *            a control which child widgets we want to find
	 * @return all children widgets of given parent
	 */
	private List<Control> findAllWidgets(final Control parent) {

		log.debug("Searching for all child widgets of: " + parent);
		allWidgets = new ArrayList<Control>();
		Display.syncExec(new Runnable() {

			@Override
			public void run() {
				findWidgets(parent);
			}
		});
		return allWidgets;
	}

	/**
	 * It is related to {@link #findAllWidgets(Control)}
	 * 
	 * @param control
	 *            a control which child widgets we want to find
	 */
	private void findWidgets(Control control) {
		allWidgets.add(control);
		if (control instanceof Composite) {
			Composite composite = (Composite) control;
			for (Control child : composite.getChildren()) {
				findWidgets(child);
			}
		}
	}

	/**
	 * Invokes the context menu on one of mapping's nodes (source or target) and selects a context menu item
	 * 
	 * @param name
	 *            Name of the transformation node in mapping detail (in case of XML/JSON it is name of the root element)
	 * @param type
	 *            Name of the context menu item we want to select
	 */
	private void invokeMappingContextMenu(final String name, final String type) {

		log.debug("Start invoking mapping context menu");
		log.debug("Looking for the right widget (" + name + ") and opening the context menu");
		final Control label = Display.syncExec(new ResultRunnable<Control>() {

			@Override
			public Control run() {
				DefaultStyledText text = new DefaultStyledText(name);
				List<Control> widgets = findAllWidgets(text.getSWTWidget().getParent().getParent());
				for (Control c : widgets) {
					if (c instanceof Label) {
						c.setFocus();
						c.notifyListeners(SWT.MouseUp, new Event());
						return c;
					}
				}
				log.error("Cannot find the right widget!");
				return null;
			}
		});

		log.debug("Obtaining the context menu item (" + type + ") and hiding the context menu");
		final MenuItem item = Display.syncExec(new ResultRunnable<MenuItem>() {

			@Override
			public MenuItem run() {
				Menu menu = label.getMenu();
				MenuItem[] items = menu.getItems();
				for (MenuItem item : items) {
					if (item.getText().equals(type)) {
						menu.notifyListeners(SWT.Hide, new Event());
						menu.setVisible(false);
						return item;
					}
				}
				return null;
			}
		});

		log.debug("Selecting the context menu item");
		Display.asyncExec(new Runnable() {

			@Override
			public void run() {
				final Event event = new Event();
				event.time = (int) System.currentTimeMillis();
				event.widget = item;
				event.item = item;
				event.display = item.getDisplay();
				event.type = SWT.Selection;
				item.notifyListeners(SWT.Selection, event);
			}
		});

		new DefaultShell();
	}
}
