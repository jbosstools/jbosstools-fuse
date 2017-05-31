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
package org.jboss.tools.fuse.qe.reddeer.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.jboss.reddeer.common.condition.AbstractWaitCondition;
import org.jboss.reddeer.core.util.Display;
import org.jboss.reddeer.eclipse.ui.views.properties.TabbedPropertyList;
import org.jboss.reddeer.swt.impl.button.LabeledCheckBox;
import org.jboss.reddeer.swt.impl.ccombo.LabeledCCombo;
import org.jboss.reddeer.swt.impl.ctab.DefaultCTabFolder;
import org.jboss.reddeer.swt.impl.ctab.DefaultCTabItem;
import org.jboss.tools.fuse.qe.reddeer.ext.AbstractViewExt;
import org.jboss.tools.fuse.qe.reddeer.widget.LabeledTextExt;

/**
 * 
 * @author djelinek
 */
public class FusePropertiesView extends AbstractViewExt {

	public enum PropertyType {

		TEXT, COMBO, CHECKBOX
	}

	public enum DetailsProperty {

		URI, DESC, ID, PATTERN, REF
	}

	public enum PropertyTab {

		DETAILS, ADVANCED
	}

	public FusePropertiesView() {

		super("Properties");
	}

	public String getProperty(PropertyType type, String label) {

		activate();
		switch (type) {
		case TEXT:
			return new LabeledTextExt(label).getText();
		case COMBO:
			return new LabeledCCombo(label).getSelection();
		case CHECKBOX:
			return new LabeledCheckBox(label).getText();
		default:
			return null;
		}
	}

	public String setProperty(PropertyType type, String label, String value) {

		activate();
		switch (type) {
		case TEXT:
			new LabeledTextExt(label).setText(value);
			return new LabeledTextExt(label).getText();
		case COMBO:
			List<String> list = new LabeledCCombo(label).getItems();
			if (list.size() == 1)
				new LabeledCCombo(label).setSelection(list.get(0));
			else if (list.contains(value))
				new LabeledCCombo(label).setSelection(value);
			else
				new LabeledCCombo(label).setSelection(list.get(list.size()-1));
			return new LabeledCCombo(label).getSelection();
		case CHECKBOX:
			new LabeledCheckBox(label).click();
			if(new LabeledCheckBox(label).isChecked())
				return "true";
			else
				return "false";
		}
		return null;
	}

	public void setDetailsProperty(DetailsProperty property, String value) {

		activate();
		switch (property) {
		case URI:
			new LabeledTextExt("Uri *").setText(value);
			break;
		case DESC:
			new LabeledTextExt("Description").setText(value);
			break;
		case ID:
			new LabeledTextExt("Id").setText(value);
			break;
		case PATTERN:
			List<String> items = new LabeledCCombo("Pattern").getItems();
			if (items.size() == 1)
				new LabeledCCombo("Pattern").setSelection(items.get(0));
			else if (items.contains(value))
				new LabeledCCombo("Pattern").setSelection(value);
			else
				new LabeledCCombo("Pattern").setSelection(items.get(0));
			break;
		case REF:
			List<String> list = new LabeledCCombo("Ref (deprecated)").getItems();
			if (list.size() == 1)
				new LabeledCCombo("Ref (deprecated)").setSelection(list.get(0));
			else if (list.contains(value))
				new LabeledCCombo("Ref (deprecated)").setSelection(value);
			else
				new LabeledCCombo("Ref (deprecated)").setSelection(list.get(0));
			break;
		default:
			break;
		}
	}

	public String getDetailsProperty(DetailsProperty property) {

		activate();
		switch (property) {
		case URI:
			return new LabeledTextExt("Uri").getText();
		case DESC:
			return new LabeledTextExt("Description").getText();
		case ID:
			new LabeledTextExt("Id").getText();
		case PATTERN:
			return new LabeledCCombo("Pattern").getSelection();
		case REF:
			return new LabeledCCombo("Ref (deprecated)").getSelection();
		default:
			return null;
		}
	}

	public void setProperty(final Widget label, final Widget property, final String value) {

		activate();
		Display.syncExec(new Runnable() {
			@Override
			public void run() {

				if (property instanceof CCombo) {
					activate();
					List<String> items = new LabeledCCombo(((Label) label).getText()).getItems();
					if (items.size() == 1)
						new LabeledCCombo(((Label) label).getText()).setSelection(items.get(0));
					else if (items.contains(value))
						new LabeledCCombo(((Label) label).getText()).setSelection(value);
					else
						new LabeledCCombo(((Label) label).getText()).setSelection(items.get(1));
				} else if (property instanceof Text) {
					activate();
					new LabeledTextExt(((Label) label).getText()).setText(value);
				} else if (property instanceof Button) {
					activate();
					new LabeledCheckBox(((Label) label).getText()).click();
				}
			}
		});

	}
	
	public String getLabel(final Widget widget) {
		
		activate();
		final StringBuffer buff = new StringBuffer();

		Display.syncExec(new Runnable() {
			
			@Override
			public void run() {
				
				Label label = (Label) widget;
				buff.append(label.getText());
			}
		});
		
		return buff.toString();
	}

	public HashMap<Widget, Widget> getDetailsProperties() {

		activate();
		final CTabFolder folde = new DefaultCTabFolder().getSWTWidget();
		final LinkedList<Widget> list = new LinkedList<>();
		final HashMap<Widget, Widget> m = new HashMap<>();

		Display.syncExec(new Runnable() {
			@Override
			public void run() {
				Control[] c = folde.getChildren();
				Control[] childrens = ((Composite) c[1]).getChildren();

				CCombo combo = null;
				Label label = null;
				Text text = null;
				Button button = null;

				for (Control children : childrens) {

					Widget w = ((Widget) children);

					if (w instanceof CCombo) {
						combo = (CCombo) w;
						list.add(combo);
					} else if (w instanceof Label) {
						label = (Label) w;
						list.add(label);
					} else if (w instanceof Text) {
						text = (Text) w;
						list.add(text);
					} else if (w instanceof Button) {
						button = (Button) w;
						list.add(button);
					}
				}

				for (Iterator<Widget> iterator = list.iterator(); iterator.hasNext();) {
					Widget widget = (Widget) iterator.next();
					Widget temp = widget;
					widget = (Widget) iterator.next();
					m.put(temp, widget);
				}

			}
		});

		return m;
	}

	/**
	 * Selects tab with a given label.
	 * 
	 * @param label
	 *            Label
	 */
	public void selectTab(String label) {
		
		activate();
		List<String> old = new ArrayList<String>();

		try {
			old = new TabbedPropertyList().getTabs();
		} catch (Exception ex) {
			// probably not rendered yet
		}
		//new WaitUntil(new AnotherTabsRendered(old), TimePeriod.NORMAL, false);

		if (old.contains(label))
			new TabbedPropertyList().selectTab(label);
	}
	
	public List<String> getTabs() {

		activate();
		return new TabbedPropertyList().getTabs();
	}

	public void switchPropertiesTab(String tab) {

		activate();
		new DefaultCTabItem(tab).activate();
	}

	public ArrayList<String> getPropertiesTabsTitles() {

		activate();
		final ArrayList<String> list = new ArrayList<>();
		Display.syncExec(new Runnable() {
			@Override
			public void run() {
				CTabItem[] items = new DefaultCTabFolder().getSWTWidget().getItems();
				for (CTabItem item : items) {
					list.add(item.getText());
				}
			}
		});
		return list;
	}

	public HashMap<Widget, Widget> getAdvancedProperties(final String tab) {

		activate();
		final CTabFolder folde = new DefaultCTabFolder().getSWTWidget();
		final LinkedList<Widget> list = new LinkedList<>();
		final HashMap<Widget, Widget> m = new HashMap<>();
		final ArrayList<String> listTab = new ArrayList<>();

		Display.syncExec(new Runnable() {
			@Override
			public void run() {

				CTabItem[] tabs = new DefaultCTabFolder().getSWTWidget().getItems();
				for (CTabItem item : tabs) {
					listTab.add(item.getText());
				}
				// --
				Control[] c = folde.getChildren();
				Control[] childrens = ((Composite) c[listTab.indexOf(tab) + 1]).getChildren();

				CCombo combo = null;
				Label label = null;
				Text text = null;
				Button button = null;

				for (Control children : childrens) {

					Widget w = ((Widget) children);

					if (w instanceof CCombo) {
						combo = (CCombo) w;
						list.add(combo);
					} else if (w instanceof Label) {
						label = (Label) w;
						list.add(label);
					} else if (w instanceof Text) {
						text = (Text) w;
						list.add(text);
					} else if (w instanceof Button) {
						button = (Button) w;
						list.add(button);
					}
				}

				for (Iterator<Widget> iterator = list.iterator(); iterator.hasNext();) {
					Widget widget = (Widget) iterator.next();
					Widget temp = widget;
					// this is not a very good solution, but so far it is sufficient -> in future think better solution variants
					if (list.size() % 2 != 1) 
						widget = (Widget) iterator.next();
					m.put(temp, widget);
					// ----
				}

			}
		});

		return m;
	}

	private class AnotherTabsRendered extends AbstractWaitCondition {

		private List<String> old;

		public AnotherTabsRendered(List<String> old) {
			this.old = old;
		}

		@Override
		public boolean test() {
			List<String> actual = new ArrayList<String>();

			try {
				actual = new TabbedPropertyList().getTabs();
			} catch (Exception ex) {
				// probably not rendered yet
			}

			return !actual.equals(old);
		}

		@Override
		public String description() {
			return "Wait for tabs of focused element to be rendered";
		}

	}

}
