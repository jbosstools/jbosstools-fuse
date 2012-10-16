package org.fusesource.ide.commons.ui.label;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class TimeThenDateLabelProvider extends FormatLabelProvider {

	public TimeThenDateLabelProvider() {
	}

	@Override
	protected DateFormat createFormat() {
		return new SimpleDateFormat("HH:mm:ss.SSS   dd-MMM-yyyy");
	}


}
