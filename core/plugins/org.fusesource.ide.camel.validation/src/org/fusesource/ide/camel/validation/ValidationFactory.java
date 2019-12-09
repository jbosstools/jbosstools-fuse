/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.camel.validation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.validation.diagram.BasicNodeValidator;


/**
 * @author lhein
 */
public class ValidationFactory {
	
	private static Map<Class<? extends AbstractCamelModelElement>, ValidationSupport> registeredValidators = new HashMap<>();
	private static ValidationFactory instance;
	
	static {
		// register a general validator which should basically work for mandatory field checking
		registeredValidators.put(AbstractCamelModelElement.class, new BasicNodeValidator());
		// you may register special validators for specific model classes here but they all need to implement IValidationSupport
		// ...
        //registeredValidators.put(Endpoint.class, new BasicUriValidator());
	}
	
	/**
	 * 
	 * @param model
	 */
	private ValidationFactory() {
	}
	
	public static synchronized ValidationFactory getInstance() {
		if (instance == null) {
			instance = new ValidationFactory();
		}
		return instance;
	}

    /**
     * 
     * @param node
     * @return
     */
    public ValidationResult validate(AbstractCamelModelElement node) {
    	ValidationResult result = null;
    	
    	Iterator<Class<? extends AbstractCamelModelElement>> it = registeredValidators.keySet().iterator();
    	while (it.hasNext()) {
			Class<? extends AbstractCamelModelElement> c = it.next();
    	    
    	    if (c.isInstance(node)) {
                ValidationSupport validator = registeredValidators.get(c);
                if (validator == null) {
                    validator = registeredValidators.get(AbstractCamelModelElement.class);
                } 
                result = validator.validate(node);
    	    }
    	}
    	    	
    	return result;
    }
}
