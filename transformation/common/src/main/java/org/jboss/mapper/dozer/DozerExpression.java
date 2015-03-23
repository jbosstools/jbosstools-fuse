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
package org.jboss.mapper.dozer;

import static org.jboss.mapper.dozer.CustomParameterHelper.emptyForNull;
import static org.jboss.mapper.dozer.CustomParameterHelper.getParameterPart;

import org.jboss.mapper.Expression;
import org.jboss.mapper.dozer.config.Field;

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
        return getParameterPart(field, SEP, 0);
    }

    @Override
    public void setLanguage(String language) {
        field.setCustomConverterParam(language + SEP + emptyForNull(getExpression()));
    }

    @Override
    public String getExpression() {
        return getParameterPart(field, SEP, 1);
    }

    @Override
    public void setExpression(String expression) {
        field.setCustomConverterParam(emptyForNull(getLanguage()) + SEP + expression);
    }

    @Override
    public String toString() {
        return "expression[lanugage:" + getLanguage() + ",expr:" + getExpression() + "]";
    }
}
