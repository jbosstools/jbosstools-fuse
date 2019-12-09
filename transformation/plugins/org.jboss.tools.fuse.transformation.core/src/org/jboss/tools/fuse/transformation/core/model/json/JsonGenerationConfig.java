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
package org.jboss.tools.fuse.transformation.core.model.json;

import org.jsonschema2pojo.DefaultGenerationConfig;
import org.jsonschema2pojo.SourceType;

/**
 * Used to override default model configuration behavior of JsonSchema2Pojo.
 */
public class JsonGenerationConfig extends DefaultGenerationConfig {

    private boolean includeHashcodeAndEquals;
    private boolean includeToString;
    private boolean usePrimitives = true;
    private SourceType sourceType = SourceType.JSONSCHEMA;

    @Override
    public boolean isIncludeHashcodeAndEquals() {
        return includeHashcodeAndEquals;
    }

    public JsonGenerationConfig setIncludeHashcodeAndEquals(boolean includeHashcodeAndEquals) {
        this.includeHashcodeAndEquals = includeHashcodeAndEquals;
        return this;
    }

    @Override
    public boolean isIncludeToString() {
        return includeToString;
    }

    public JsonGenerationConfig setIncludeToString(boolean includeToString) {
        this.includeToString = includeToString;
        return this;
    }

    @Override
    public boolean isUsePrimitives() {
        return usePrimitives;
    }

    public JsonGenerationConfig setUsePrimitives(boolean usePrimitives) {
        this.usePrimitives = usePrimitives;
        return this;
    }

    @Override
    public SourceType getSourceType() {
        return sourceType;
    }

    public JsonGenerationConfig setSourceType(SourceType sourceType) {
        this.sourceType = sourceType;
        return this;
    }
}
