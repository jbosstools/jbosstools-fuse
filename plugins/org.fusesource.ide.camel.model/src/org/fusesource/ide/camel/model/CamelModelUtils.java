package org.fusesource.ide.camel.model;

import org.apache.camel.model.BeanDefinition;
import org.apache.camel.model.MarshalDefinition;
import org.apache.camel.model.PolicyDefinition;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.TransactedDefinition;
import org.apache.camel.model.UnmarshalDefinition;

// TODO remove this class and use the one in
// org.fusesource.camel.tooling.util.CamelModelUtils
// from camel-tooling-util jar ASAP!
public class CamelModelUtils {

	public static boolean canAcceptOutput(Class<?> aClass, ProcessorDefinition def) {
		if (aClass == null) {
			return false;
		}

		// special for bean/marshal/unmarshal, until their isOutputSupport would return false
		if (BeanDefinition.class.isAssignableFrom(aClass)) {
			return false;
		}
		if (MarshalDefinition.class.isAssignableFrom(aClass) ||
				UnmarshalDefinition.class.isAssignableFrom(aClass) ||
				TransactedDefinition.class.isAssignableFrom(aClass)) {
			return false;
		}

		// use isOutputSupport on camel model
		if (ProcessorDefinition.class.isAssignableFrom(aClass)) {
			if (def != null) {
				boolean answer = def.isOutputSupported();
				return answer;
			}
		}

		// assume no output is supported
		return false;
	}

	public static boolean isNextSiblingStepAddedAsNodeChild(Class<?> aClass, ProcessorDefinition def) {
		boolean acceptOutput = canAcceptOutput(aClass, def);
		return !acceptOutput || PolicyDefinition.class.isAssignableFrom(aClass);
	}

}
