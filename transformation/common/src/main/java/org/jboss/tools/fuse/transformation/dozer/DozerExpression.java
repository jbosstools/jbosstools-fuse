/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.jboss.tools.fuse.transformation.dozer;

import static org.jboss.tools.fuse.transformation.dozer.TransformationArgumentHelper.emptyForNull;
import static org.jboss.tools.fuse.transformation.dozer.TransformationArgumentHelper.getArgumentPart;

import org.jboss.tools.fuse.transformation.Expression;
import org.jboss.tools.fuse.transformation.dozer.config.Field;

/**
 * Dozer implementation of Expression.
 */
public class DozerExpression implements Expression {

    private static final String SEP = ":";
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
        if (nextPart.equalsIgnoreCase("resource")) {
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
        return "expression[language:" + getLanguage() + ",expr:" + getExpression() + "]";
    }
}
