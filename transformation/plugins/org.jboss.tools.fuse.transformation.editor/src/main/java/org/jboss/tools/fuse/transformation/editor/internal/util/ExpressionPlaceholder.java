/******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: JBoss by Red Hat - Initial implementation.
 *****************************************************************************/
package org.jboss.tools.fuse.transformation.editor.internal.util;

import org.jboss.mapper.Expression;

/**
 *
 */
public class ExpressionPlaceholder implements Expression {

    private String language, expression;

    /**
     * @param language
     * @param expression
     */
    public ExpressionPlaceholder(final String language,
                                 final String expression) {
        this.language = language;
        this.expression = expression;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.mapper.Expression#getExpression()
     */
    @Override
    public String getExpression() {
        return expression;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.mapper.Expression#getLanguage()
     */
    @Override
    public String getLanguage() {
        return language;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.mapper.Expression#setExpression(java.lang.String)
     */
    @Override
    public void setExpression(final String expression) {
        this.expression = expression;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.mapper.Expression#setLanguage(java.lang.String)
     */
    @Override
    public void setLanguage(final String language) {
        this.language = language;
    }
}
