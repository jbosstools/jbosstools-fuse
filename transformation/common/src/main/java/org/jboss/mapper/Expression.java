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
package org.jboss.mapper;

/**
 * An Expression consists of a language identifier and expression text.
 */
public class Expression {

    private String language;
    private String expression;

    /**
     * Create a new Expression.
     * 
     * @param language expression language
     * @param expression expression text
     */
    public Expression(String language, String expression) {
        this.language = language;
        this.expression = expression;
    }

    /**
     * The name of the language used by this expression.
     * 
     * @return expression language name
     */
    public String getLanguage() {
        return language;
    }
    
    /**
     * The expression text.
     * 
     * @return expression text
     */
    public String getExpression() {
        return expression;
    }


    @Override
    public String toString() {
        return "expression[lanugage:" + language + ",expr:" + expression + "]";
    }
}
