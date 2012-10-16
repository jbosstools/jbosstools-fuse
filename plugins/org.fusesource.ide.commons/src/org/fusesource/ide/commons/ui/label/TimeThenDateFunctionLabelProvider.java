package org.fusesource.ide.commons.ui.label;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.fusesource.ide.commons.util.Function1;


public class TimeThenDateFunctionLabelProvider extends FormatFunctionLabelProvider {

	public TimeThenDateFunctionLabelProvider(Function1 function) {
		super(function);
	}

	@Override
	protected DateFormat createFormat() {
		return new SimpleDateFormat("HH:mm:ss.SSS   dd-MMM-yyyy");
	}


}
