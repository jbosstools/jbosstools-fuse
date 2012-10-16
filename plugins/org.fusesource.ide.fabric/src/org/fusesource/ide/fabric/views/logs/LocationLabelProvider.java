package org.fusesource.ide.fabric.views.logs;

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.jface.viewers.ViewerCell;

public class LocationLabelProvider extends StyledCellLabelProvider {

	@Override
	public void update(ViewerCell cell) {
		Object element = cell.getElement();
		LogEventBean log = LogEventBean.toLogEventBean(element);
		if (log != null) {
			String className = log.getClassName();
			
			// we don't want to display ? as location
			if (className.trim().equals("?")) className = null;
			
			if (className != null) {
				Styler style = null;
				StyledString styledString = new StyledString(className, style );

				String fileName = log.getFileName();
				if (fileName != null) {
					styledString.append(fileName, StyledString.COUNTER_STYLER);
				}
				cell.setText(styledString.toString());
				cell.setStyleRanges(styledString.getStyleRanges());
			}
		}
		super.update(cell);
	}





}
