package org.fusesource.ide.commons.ui.label;

import java.text.DateFormat;

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.fusesource.ide.commons.Activator;


public class FormatLabelProvider extends StyledCellLabelProvider {
	private DateFormat format;

	@Override
	public void update(ViewerCell cell) {
		Object element = cell.getElement();
		if (element != null) {
			try {
				DateFormat f = getFormat();
				if (f != null) {
					String text = "";
					Object convertValue = convertValue(cell);
					if (convertValue != null) {
						text = f.format(convertValue);
					}
					cell.setText(text);
				}
			} catch (Exception e) {
				Activator.getLogger().warning("Failed to format " + element
						+ " of type " + element.getClass().getName()
						+ " using formatter: " + format + ". " + e, e);
			}
		}
		super.update(cell);
	}

	public DateFormat getFormat() {
		if (format == null) {
			format = createFormat();
		}
		return format;
	}

	public void setFormat(DateFormat format) {
		this.format = format;
	}

	protected DateFormat createFormat() {
		return null;
	}


	/**
	 * Strategy method to allow derived classes to convert the value
	 */
	protected Object convertValue(ViewerCell cell) {
		return cell.getText();
	}
}