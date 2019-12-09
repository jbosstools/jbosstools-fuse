/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.transformation.core;

/**
 * An Expression consists of a language identifier and expression text.
 */
public interface Expression {

    /**
     * The name of the language used by this expression.
     * 
     * @return expression language name
     */
    String getLanguage();

    /**
     * Set the language for this expression.
     * 
     * @param language expression language name
     */
    void setLanguage(String language);
    
    /**
     * The expression text.
     * 
     * @return expression text
     */
    String getExpression();
    
    /**
     * Sets the expression content.
     * @param expression string containing complete expression
     */
    void setExpression(String expression);
}
