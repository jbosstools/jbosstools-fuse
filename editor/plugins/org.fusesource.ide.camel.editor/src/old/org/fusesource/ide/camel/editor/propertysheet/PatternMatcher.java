/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package old.org.fusesource.ide.camel.editor.propertysheet;

import java.util.HashMap;

/**
 * @author lhein
 */
public class PatternMatcher {
	private String pattern;
	private HashMap<String, Integer> fieldNameToGroupIdx = new HashMap<String, Integer>();
	private HashMap<Integer, String> groupIdxToFieldName = new HashMap<Integer, String>();
	
	/**
	 * 
	 */
	public PatternMatcher() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * adds a match 
	 * 
	 * @param grp
	 * @param field
	 */
	public void addMatchResult(int grp, String field) {
		fieldNameToGroupIdx.put(field, grp);
		groupIdxToFieldName.put(grp, field);
	}
	
	/**
	 * @return the pattern
	 */
	public String getPattern() {
		return this.pattern;
	}

	/**
	 * @param pattern the pattern to set
	 */
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	
	/**
	 * 
	 * @param field
	 * @return
	 */
	public int getGroupForField(String field) {
		return fieldNameToGroupIdx.get(field);
	}
	
	/**
	 * 
	 * @param grp
	 * @return
	 */
	public String getFieldForGroup(int grp) {
		return groupIdxToFieldName.get(grp);
	}
}
