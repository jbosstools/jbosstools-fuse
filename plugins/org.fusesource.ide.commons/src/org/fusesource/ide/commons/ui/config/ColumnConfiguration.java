package org.fusesource.ide.commons.ui.config;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.osgi.service.prefs.Preferences;

/**
 * Configuration details of a column
 */
public class ColumnConfiguration {
	private final String name;
	private final Preferences node;
	private CellLabelProvider labelProvider;

	public ColumnConfiguration(String name, Preferences node) {
		this.name = name;
		this.node = node;
	}

	@Override
	public String toString() {
		return "ColumnConfiguration(" + name + ")";
	}

	public String getName() {
		return name;
	}

	public Preferences getNode() {
		return node;
	}

	public boolean isVisible() {
		return node.getBoolean("visible", true);
	}

	public String getDescription() {
		return node.get("description", name);
	}

	public void setVisible(boolean value) {
		if (value) {
			node.remove("visible");
		} else {
			node.putBoolean("visible", value);
		}
	}

	public int getWidth() {
		return node.getInt("width", 0);
	}

	public void setWidth(int value) {
		if (value <= 0) {
			node.remove("width");
		} else {
			node.putInt("width", value);
		}
	}

	public String getLabelProviderStyle() {
		return node.get("labelProviderStyle", null);
	}

	public void setLabelProviderStyle(String value) {
		node.put("labelProviderStyle", value);
	}

	public CellLabelProvider getLabelProvider() {
		return labelProvider;
	}

	public void setLabelProvider(CellLabelProvider labelProvider) {
		this.labelProvider = labelProvider;
	}

}
