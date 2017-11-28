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
package org.jboss.tools.fuse.reddeer.editor;

import java.io.InputStream;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.reddeer.common.exception.RedDeerException;
import org.eclipse.reddeer.common.logging.Logger;
import org.eclipse.reddeer.common.wait.AbstractWait;
import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitWhile;
import org.eclipse.reddeer.core.exception.CoreLayerException;
import org.eclipse.reddeer.eclipse.ui.views.properties.PropertySheet;
import org.eclipse.reddeer.gef.api.Palette;
import org.eclipse.reddeer.gef.editor.GEFEditor;
import org.eclipse.reddeer.gef.handler.ViewerHandler;
import org.eclipse.reddeer.gef.view.PaletteView;
import org.eclipse.reddeer.swt.api.CCombo;
import org.eclipse.reddeer.swt.api.Combo;
import org.eclipse.reddeer.swt.api.Text;
import org.eclipse.reddeer.swt.api.Widget;
import org.eclipse.reddeer.swt.condition.ShellIsAvailable;
import org.eclipse.reddeer.swt.exception.SWTLayerException;
import org.eclipse.reddeer.swt.impl.button.PushButton;
import org.eclipse.reddeer.swt.impl.ccombo.LabeledCCombo;
import org.eclipse.reddeer.swt.impl.combo.DefaultCombo;
import org.eclipse.reddeer.swt.impl.combo.LabeledCombo;
import org.eclipse.reddeer.swt.impl.ctab.DefaultCTabItem;
import org.eclipse.reddeer.swt.impl.menu.ContextMenuItem;
import org.eclipse.reddeer.swt.impl.menu.ShellMenuItem;
import org.eclipse.reddeer.swt.impl.shell.DefaultShell;
import org.eclipse.reddeer.swt.impl.styledtext.DefaultStyledText;
import org.eclipse.reddeer.swt.impl.text.DefaultText;
import org.eclipse.reddeer.swt.impl.text.LabeledText;
import org.eclipse.reddeer.workbench.core.condition.JobIsRunning;
import org.eclipse.reddeer.workbench.impl.shell.WorkbenchShell;
import org.eclipse.ui.IEditorInput;
import org.jboss.tools.fuse.reddeer.XPathEvaluator;
import org.jboss.tools.fuse.reddeer.component.AbstractURICamelComponent;
import org.jboss.tools.fuse.reddeer.component.CamelComponent;

/**
 * Manipulates with Camel Editor
 * 
 * @author tsedmik
 */
public class CamelEditor extends GEFEditor {

	private static Logger log = Logger.getLogger(CamelEditor.class);

	public CamelEditor(String title) {

		super(title);
	}

	@Override
	public void save() {
		activate();
		new WaitWhile(new JobIsRunning(), TimePeriod.LONG);
		new ShellMenuItem(new WorkbenchShell(), "File", "Save").select();
		new WaitWhile(new JobIsRunning(), TimePeriod.LONG);
	}

	@Override
	public void close() {
		save();
		super.close();
	}

	/**
	 * <p>
	 * Adds a component into/before/after another component in the Camel Editor.
	 * </p>
	 * <p>
	 * <b>Note:</b>This methods clicks on the center of the target component. It may cause problems in case of
	 * containers components (e.g. Route).
	 * </p>
	 * <p>
	 * <b>Note:</b>It does not wait until a new node is in the Camel Editor.
	 * </p>
	 * 
	 * @param name
	 *            Component name in Palette view
	 * @param parent
	 *            Parent name in the Camel Editor
	 */
	public void addComponent(String name, String parent) {
		log.debug("Adding '" + name + "' component into the Camel Editor");
		new PaletteView().open();
		Palette palette = ViewerHandler.getInstance().getPalette(viewer);
		palette.activateTool(name);
		new CamelComponentEditPart(parent).click();
	}

	/**
	 * Adds a component into the Camel Editor at given position
	 * 
	 * @param component
	 *            component to add
	 * @param x
	 *            x-axis position
	 * @param y
	 *            y-axis position
	 */
	public void addCamelComponent(CamelComponent component, int x, int y) {

		log.debug("Adding '" + component.getLabel() + "' component into the Camel Editor at position [" + x + "," + y
				+ "]");
		addToolFromPalette(component.getPaletteEntry(), x, y);
		AbstractWait.sleep(TimePeriod.SHORT);
	}

	/**
	 * Adds a component into the Camel Editor on defined coordinates<br/>
	 * <b>Note:</b>It does not wait until a new node is in the Camel Editor.
	 * 
	 * @param component
	 *            Name of a component in Palette view
	 * @param x
	 *            x-axis coordinate
	 * @param y
	 *            y-axis coordinate
	 */
	public void addCamelComponent(String component, int x, int y) {

		log.debug("Adding '" + component + "' component into the Camel Editor");
		new PaletteView().open();
		Palette palette = ViewerHandler.getInstance().getPalette(viewer);
		palette.activateTool(component);
		click(x, y);
	}

	/**
	 * Adds a component into the Camel Editor into defined route<br/>
	 * <b>Note:</b>It does not wait until a new node is in the Camel Editor.
	 * 
	 * @param component
	 *            Name of a component in Palette view
	 * 
	 * @param route
	 *            Name of a route in Camel Editor
	 */
	public void addCamelComponent(String component, String route) {

		log.debug("Adding '" + component + "' component into the Camel Editor");
		new PaletteView().open();
		Palette palette = ViewerHandler.getInstance().getPalette(viewer);
		palette.activateTool(component);
		Point r = getInEditorCoords(route);
		addCamelComponent(component, r.x + 10, r.y + 50);
	}

	/**
	 * Adds a component into the Camel Editor into defined route
	 * 
	 * @param component
	 *            Name of a component in Palette view
	 * @param route
	 *            Name of a route in Camel Editor
	 */
	public void addCamelComponent(CamelComponent component, String route) {

		log.debug("Adding '" + component + "' component into '" + route + "' route");
		Point r = getInEditorCoords(route);
		addCamelComponent(component, r.x + 10, r.y + 10);
		new WaitWhile(new JobIsRunning(), TimePeriod.LONG);
		AbstractWait.sleep(TimePeriod.SHORT);
	}

	/**
	 * Deletes the given component from the Camel Editor
	 * 
	 * @param component
	 *            component to delete
	 */
	public void deleteCamelComponent(CamelComponent component) {

		log.debug("Removing '" + component.getLabel() + "' component from the Camel Editor");
		CamelComponentEditPart c = new CamelComponentEditPart(component.getLabel());
		c.select();
		c.delete();
	}

	/**
	 * Deletes the given component from the Camel Editor
	 * 
	 * @param component
	 *            label of the component to delete
	 */
	public void deleteCamelComponent(String component) {

		log.debug("Removing '" + component + "' component from the Camel Editor");
		CamelComponentEditPart c = new CamelComponentEditPart(component);
		c.select();
		c.delete();
	}

	/**
	 * Sets a breakpoint on given component in the Camel Editor
	 * 
	 * @param component
	 *            instance of component in Camel Editor
	 */
	public void setBreakpoint(String label) {

		log.debug("Setting a new breakpoint on component: " + label);
		doOperation(label, "Set Breakpoint");
		if (new ShellIsAvailable("Please confirm...").test()) {
			new DefaultShell("Please confirm...");
			new PushButton("OK").click();
		}
	}

	/**
	 * Sets a conditional breakpoint on the given component in the Camel Editor
	 * 
	 * @param label
	 *            label of instance of component in Camel Editor
	 * @param language
	 *            language of the condition
	 * @param condition
	 *            condition's expression
	 */
	public void setConditionalBreakpoint(String label, String language, String condition) {

		log.debug("Setting a new conditional breakpoint on component: " + label);
		doOperation(label, "Set Conditional Breakpoint");
		new DefaultShell();
		new DefaultCombo().setSelection(language);
		new DefaultStyledText().setText(condition);
		new PushButton("OK").click();
		if (new ShellIsAvailable("Please confirm...").test()) {
			new DefaultShell("Please confirm...");
			new PushButton("OK").click();
		}
	}

	/**
	 * Disables a breakpoint on the component
	 * 
	 * @param label
	 *            label of instance of component in Camel Editor
	 */
	public void disableBreakpoint(String label) {

		log.debug("Disabling breakpoint on component: " + label);
		doOperation(label, "Disable Breakpoint");
	}

	/**
	 * Enables a breakpoint on the component
	 * 
	 * @param label
	 *            label of instance of component in Camel Editor
	 */
	public void enableBreakpoint(String label) {

		log.debug("Enabling breakpoint on component: " + label);
		doOperation(label, "Enable Breakpoint");
	}

	/**
	 * Deletes a breakpoint on the component
	 * 
	 * @param label
	 *            label of instance of component in Camel Editor
	 */
	public void deleteBreakpoint(String label) {

		log.debug("Deleting breakpoint on component: " + label);
		doOperation(label, "Delete Breakpoint");
	}

	/**
	 * Sets new parameters of a conditional breakpoint on the component
	 * 
	 * @param label
	 *            label of instance of component in Camel Editor
	 * @param language
	 *            condition's language
	 * @param condition
	 *            condition's expression
	 */
	public void editConditionalBreakpoint(String label, String language, String condition) {

		log.debug("Editing conditional breakpoint on component: " + label);
		doOperation(label, "Edit Conditonal Breakpoint");
		new DefaultShell();
		new DefaultCombo().setSelection(language);
		new DefaultText().setText(condition);
		new PushButton("OK").click();
	}

	/**
	 * Checks whether is a breakpoint set on the component
	 * 
	 * @param label
	 *            label of instance of component in Camel Editor
	 * @return true - breakpoint is set, false - otherwise
	 */
	public boolean isBreakpointSet(String label) {

		new GEFEditor().click(5, 5);
		new CamelComponentEditPart(label).select();
		try {
			new ContextMenuItem("Set Breakpoint");
		} catch (SWTLayerException | CoreLayerException ex) {
			return true;
		}

		return false;
	}

	/**
	 * Checks whether is breakpoint enabled on the component
	 * 
	 * @param label
	 *            label of instance of component in Camel Editor
	 * @return true - breakpoint is enabled, false - otherwise
	 */
	public boolean isBreakpointEnabled(String label) {

		if (!isBreakpointSet(label))
			return false;

		new GEFEditor().click(5, 5);
		new CamelComponentEditPart(label).select();
		try {
			new ContextMenuItem("Enable Breakpoint");
		} catch (SWTLayerException | CoreLayerException ex) {
			return true;
		}

		return false;
	}

	/**
	 * Performs the given operation on a component described with the given label
	 * 
	 * @param label
	 *            label of instance of component in Camel Editor
	 * @param operation
	 *            name of operation in the context menu
	 */
	public void doOperation(String label, String... operation) {

		log.debug("Executing operation '" + operation + "' on the component: " + label);
		new GEFEditor().click(5, 5);
		new CamelComponentEditPart(label).select();
		try {
			new ContextMenuItem(operation).select();
		} catch (SWTLayerException | CoreLayerException ex) {
			log.error("Given operation is not present in the context menu of the component: " + label);
		}
	}

	/**
	 * Sets an id to a component described with the given label
	 * 
	 * @param label
	 *            label of instance of component in Camel Editor
	 * @param id
	 *            component's id
	 */
	public void setId(String label, String id) {

		log.debug("Setting id '" + id + "' to the component: " + label);
		PropertySheet properties = new PropertySheet();
		properties.open();
		AbstractWait.sleep(TimePeriod.SHORT);
		selectEditPart(label);
		AbstractWait.sleep(TimePeriod.SHORT);
		properties.activate();
		properties.selectTab("Details");
		AbstractWait.sleep(TimePeriod.SHORT);
		new LabeledText("Id").setText(id);
		activate();
		AbstractWait.sleep(TimePeriod.SHORT);
	}

	/**
	 * Switches between 'Design' and 'Source' tab of the Camel Editor
	 * 
	 * @param name
	 *            'Design' or 'Source'
	 */
	public static void switchTab(String name) {

		log.debug("Switching on the tab '" + name + "'");
		new DefaultCTabItem(name).activate();
	}

	/**
	 * Checks whether a component with given name is available in the Camel Editor
	 * 
	 * @param name
	 *            name of the component
	 * @return true - component is available, false - otherwise
	 */
	public boolean isComponentAvailable(String name) {

		log.debug("Looking for '" + name + "' component in the Camel Editor");
		new GEFEditor().click(5, 5);
		AbstractWait.sleep(TimePeriod.SHORT);
		try {
			new CamelComponentEditPart(name).select();
		} catch (RedDeerException ex) {
			return false;
		}
		return true;
	}

	/**
	 * Sets a property to desired value. It presumes that some component in the Camel Editor is selected.
	 * 
	 * @param name
	 *            name of the property
	 * @param value
	 *            value of the property
	 */
	public void setProperty(String name, String value) {
		setProperty(Text.class, name, value);
	}

	/**
	 * Sets a property to desired value. It presumes that some component in the Camel Editor is selected.
	 * 
	 * @param type
	 *            widget type
	 * @param name
	 *            name of the property
	 * @param value
	 *            value of the property
	 */
	public void setProperty(Class<? extends Widget> type, String name, String value) {
		log.debug("Setting '" + value + "' as the property '" + name + "' of selelected component in the Camel Editor");
		PropertySheet prop = new PropertySheet();
		prop.open();
		prop.activate();
		prop.selectTab("Details");
		if (type.equals(Text.class)) {
			new LabeledText(name).setText(value);
		} else if (type.equals(Combo.class)) {
			new LabeledCombo(name).setSelection(value);
		} else if (type.equals(CCombo.class)) {
			new LabeledCCombo(name).setSelection(value);
		} else {
			throw new UnsupportedOperationException("Property of type '" + type + "' is not supported");
		}
		activate();
		AbstractWait.sleep(TimePeriod.SHORT);
	}

	/**
	 * Sets a property to desired value.
	 * 
	 * @param component
	 *            component in the Camel editor
	 * @param name
	 *            name of the property
	 * @param value
	 *            value of the property
	 */
	public void setProperty(String component, String name, String value) {

		log.debug("Setting '" + value + "' as the property '" + name + "' of selelected component in the Camel Editor");
		PropertySheet properties = new PropertySheet();
		properties.open();
		selectEditPart(component);
		properties.activate();
		properties.selectTab("Details");
		new LabeledText(name).setText(value);
		activate();
		AbstractWait.sleep(TimePeriod.SHORT);
	}

	/**
	 * Sets a property to desired value.
	 * 
	 * @param component
	 *            component in the Camel editor
	 * @param name
	 *            name of the property
	 * @param value
	 *            value of the property
	 */
	public void setAdvancedProperty(String component, String name, String value) {

		log.debug("Setting '" + value + "' as the advanced property '" + name
				+ "' of selelected component in the Camel Editor");
		PropertySheet properties = new PropertySheet();
		properties.open();
		selectEditPart(component);
		properties.activate();
		properties.selectTab("Advanced");
		new LabeledText(name).setText(value);
		activate();
		AbstractWait.sleep(TimePeriod.SHORT);
	}

	/**
	 * Sets a property to desired value.
	 * 
	 * @param component
	 *            camel component
	 * @param name
	 *            name of the property
	 * @param value
	 *            value of the property
	 */
	public void setAdvancedProperty(AbstractURICamelComponent component, String name, String value) {

		log.debug("Setting '" + value + "' as the advanced property '" + name
				+ "' of selelected component in the Camel Editor");
		PropertySheet properties = new PropertySheet();
		properties.open();
		activate();
		new CamelComponentEditPart(component).select();
		properties.activate();
		properties.selectTab("Advanced");
		new LabeledText(name).setText(value);
		component.setProperty(name, value);
		activate();
		AbstractWait.sleep(TimePeriod.SHORT);
		save();
	}

	/**
	 * Sets 'Uri' property
	 * 
	 * @param value
	 *            value of 'Uri'
	 */
	public void setUriProperty(String value) {

		log.debug("Setting '" + value + "' as the property 'Uri' of selelected component in the Camel Editor");
		new PropertySheet().open();
		new PropertySheet().selectTab("Generic");
		new DefaultCombo(0).setText(value);
		activate();
		AbstractWait.sleep(TimePeriod.SHORT);
	}

	/**
	 * Sets a property to desired value.
	 * 
	 * @param component
	 *            component in the Camel editor
	 * @param name
	 *            name of the property
	 * @param value
	 *            value of the property
	 */
	public void setComboProperty(String component, int position, String value) {

		log.debug("Setting '" + value + "' as the property number '" + position
				+ "' of selelected component in the Camel Editor");
		PropertySheet properties = new PropertySheet();
		properties.open();
		selectEditPart(component);
		properties.activate();
		properties.selectTab("Generic");
		new DefaultCombo(position).setText(value);
		activate();
		AbstractWait.sleep(TimePeriod.SHORT);
	}

	/**
	 * Selects an edit part with a given name
	 * 
	 * @param name
	 *            name of edit part
	 */
	public void selectEditPart(String name) {

		activate();
		new CamelComponentEditPart(name).select();
	}

	/**
	 * Click on an edit part with a given name
	 * 
	 * @param name
	 *            name of edit part
	 */
	public void clickOnEditPart(String name) {

		activate();
		new CamelComponentEditPart(name).click();
	}

	/**
	 * Invoke 'Layout Diagram' from the context menu of given component
	 * 
	 * @param name
	 *            name of edit part
	 */
	public void layoutDiagram(String name) {

		new CamelComponentEditPart(name).select();
		new ContextMenuItem("Layout Diagram").select();
	}

	/**
	 * Retrieves coordinates of given component
	 * 
	 * @param name
	 *            name of camel component
	 * @return relative coordinates of given element in Camel Editor
	 */
	private Point getInEditorCoords(String name) {
		activate();
		final CamelComponentEditPart component = new CamelComponentEditPart(name);
		final int x = component.getBounds().x;
		final int y = component.getBounds().y;
		return new Point(x, y);
	}

	public String xpath(String expr) throws CoreException {
		XPathEvaluator xpath = new XPathEvaluator(getInputStream());
		String result = xpath.evaluateString(expr);
		return result;
	}

	public InputStream getInputStream() throws CoreException {
		IEditorInput editorInput = editorPart.getEditorInput();
		IFile iFile = editorInput.getAdapter(IFile.class);
		if (iFile == null) {
			throw new RuntimeException("No file is associated to the Camel editor");
		}
		return iFile.getContents();
	}

	/**
	 * Types text into Search box in Palette
	 * 
	 * @param text
	 *            search box input
	 */
	public void paletteSearch(String text) {
		new DefaultText().setText(text);
		AbstractWait.sleep(TimePeriod.SHORT);
	}

	/**
	 * Retrieves all available palette components
	 * 
	 * @return List of available components names
	 */
	public List<String> palleteGetComponents() {
		return getPalette().getTools();
	}

	@Override
	public Palette getPalette() {
		return ViewerHandler.getInstance().getPalette(viewer);
	}
}
