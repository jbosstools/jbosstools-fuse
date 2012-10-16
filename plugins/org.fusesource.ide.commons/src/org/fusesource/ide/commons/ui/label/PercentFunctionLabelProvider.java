package org.fusesource.ide.commons.ui.label;

import java.text.Format;
import java.text.NumberFormat;

import org.fusesource.ide.commons.util.Function1;


public class PercentFunctionLabelProvider extends FormatFunctionLabelProvider {

	public PercentFunctionLabelProvider(Function1 function) {
		super(function);
	}

	@Override
	protected Format createFormat() {
		return NumberFormat.getPercentInstance();
	}


}
