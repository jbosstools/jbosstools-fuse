package org.fusesource.ide.commons.ui.label;

import java.util.Date;

import org.eclipse.jface.viewers.ViewerCell;
import org.fusesource.ide.commons.util.Strings;


public class LongTimestampAsTimeThenDateLabelProvider extends TimeThenDateLabelProvider {

	@Override
	protected Object convertValue(ViewerCell cell) {
		String text = cell.getText();
		if (Strings.isBlank(text)) {
			return null;
		}
		long n = Long.parseLong(text);
		return new Date(n);
	}

}
