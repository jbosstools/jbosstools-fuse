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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.tools.fuse.transformation.core.model.Model;

/**
 * Utility code related to Dozer configuration.
 */
public final class DozerUtil {
    
    // Regex which catches indexes in a field name
    private static final Pattern INDEX_PATTERN = Pattern.compile("\\[[0-9]+\\]"); //$NON-NLS-1$
    
    public static List<Integer> getFieldIndexes(String fieldVal) {
        ArrayList<Integer> indices = new ArrayList<Integer>();
        if (fieldVal != null) {
            for (String fieldName : fieldVal.split("\\.")) { //$NON-NLS-1$
                indices.add(getIndex(fieldName));
            }
        }
        return indices;
    }
    
    /**
     * Remove all [n] indexes from a field name
     * @param fieldVal field name
     * @return field name with indexes removed
     */
    public static String removeIndexes(String fieldVal) {
        return fieldVal != null && fieldVal.indexOf('[') > 0
                ? fieldVal.replaceAll(INDEX_PATTERN.pattern(), "") //$NON-NLS-1$
                : fieldVal;
    }
    
    /**
     * Returns the field name used in a dozer config based on a model and root 
     * type.
     * @param model field model
     * @param rootType the class used in a mapping definition.  The field name
     * is created relative to this root type.
     * @return field name
     */
    public static String getFieldName(final Model model, final String rootType) {
        return getFieldName(model, rootType, noIndex(model));
    }
    
    /**
     * Returns the field name used in a dozer config based on a model and root 
     * type with a list of indexes for all fields in the ancenstry of the field model.
     * @param model field model
     * @param rootType the class used in a mapping definition.  The field name
     * is created relative to this root type.
     * @param indexes a list of indexes 
     * @return field name
     */
    public static String getFieldName(final Model model, final String rootType, List<Integer> indexes) {
        int depth = numberOfNodes(model);
        if (depth != indexes.size()) {
            throw new IllegalArgumentException("Size of index list " + indexes.size()  //$NON-NLS-1$
                    + "does not match depth of model tree " + depth); //$NON-NLS-1$
        }
        
        // The model tree is bottom-top order while the indexes are top-bottom, so reverse the list
        List<Integer> reversedIndex = new ArrayList<Integer>(indexes.size());
        reversedIndex.addAll(indexes);
        Collections.reverse(reversedIndex);
        Iterator<Integer> indexItr = reversedIndex.iterator();
        
        // Start with the bottom node and then iterate up through parent nodes until we hit 
        // the root type or an unindexed collection
        StringBuilder name = new StringBuilder(formatName(model.getName(), indexItr.next()));
        for (Model parent = model.getParent(); parent != null; parent = parent.getParent()) {
            if (parent.getType().equals(rootType)) {
                break;
            }
            Integer parentIdx = indexItr.next();
            if (parent.isCollection() && parentIdx == null) {
                break;
            }
            name.insert(0, formatName(parent.getName(), parentIdx) + "."); //$NON-NLS-1$
        }
        return name.toString();
    }
    
	/**
	 * /!\ Public for test purpose
	 */
	public static Integer getIndex(String fieldName) {
        Matcher matcher = INDEX_PATTERN.matcher(fieldName);
        if (matcher.find()) {
            String indexStr = matcher.group();
            return Integer.valueOf(indexStr.substring(1, indexStr.length() - 1));
        } else {
            return null;
        }
    }
    
    static String formatName(String name, Integer index) {
        return index != null ? name + "[" + index + "]" : name; //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    public static int numberOfNodes(Model model) {
        int nodes = 0;
        for (Model m = model.getParent() ; m != null ; m = m.getParent()) {
            ++nodes;
        }
        return nodes;
    }
    
    /**
     * Creates an index list for a model field with all nulls, which is the
     * equivalent of no index for any level of the model.
     */
    public static List<Integer> noIndex(Model model) {
        Integer[] indexes = new Integer[numberOfNodes(model)];
        Arrays.fill(indexes, null);
        return Arrays.asList(indexes);
    }
}
