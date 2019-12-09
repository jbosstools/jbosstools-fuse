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
package org.jboss.tools.fuse.transformation.core.dozer;

import static org.jboss.tools.fuse.transformation.core.dozer.TransformationArgumentHelper.emptyForNull;
import static org.jboss.tools.fuse.transformation.core.dozer.TransformationArgumentHelper.getArgumentPart;

import org.jboss.tools.fuse.transformation.core.Expression;
import org.jboss.tools.fuse.transformation.core.dozer.config.Field;

/**
 * Dozer implementation of Expression.
 */
public class DozerExpression implements Expression {

    private static final String SEP = ":"; //$NON-NLS-1$
    private Field field;

    /**
     * Create a new Expression.
     * 
     * @param field Dozer field-level mapping
     */
    public DozerExpression(Field field) {
        this.field = field;
    }

    @Override
    public String getLanguage() {
        return getArgumentPart(field, SEP, 0);
    }

    @Override
    public void setLanguage(String language) {
        field.setCustomConverterArgument(language + SEP + emptyForNull(getExpression()));
    }

    @Override
    public String getExpression() {
        String nextPart = getArgumentPart(field, SEP, 1);
        if (nextPart.equalsIgnoreCase("resource")) { //$NON-NLS-1$
            String scheme = getArgumentPart(field, SEP, 2);
            String path = getArgumentPart(field, SEP, 3);
            if (scheme != null && path != null) {
                return nextPart + SEP + scheme + SEP + path;
            }
        }
        return nextPart;
    }

    @Override
    public void setExpression(String expression) {
        field.setCustomConverterArgument(emptyForNull(getLanguage()) + SEP + expression);
    }

    @Override
    public String toString() {
        return "expression[language:" + getLanguage() + ",expr:" + getExpression() + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
}
