package org.fusesource.ide.commons.ui.form;

import org.eclipse.core.databinding.UpdateValueStrategy;

public class TrimmingUpdateValueStrategy extends UpdateValueStrategy {
	public TrimmingUpdateValueStrategy(int updatePolicy) {
		super(updatePolicy);
	}

	@Override
	public Object convert(Object value) {
		Object answer = super.convert(value);
		if (answer instanceof String) {
			// remove decimal points on long values (see ECLIPSE-198)
			if (value instanceof Long) {
				answer = ((String) answer).replaceAll("\\.", "");
			}
			return answer.toString().trim();
		} 
		return answer;
	}
}