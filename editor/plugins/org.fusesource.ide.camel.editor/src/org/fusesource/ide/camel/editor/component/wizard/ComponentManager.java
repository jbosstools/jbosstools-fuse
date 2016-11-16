/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.camel.editor.component.wizard;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.fusesource.ide.camel.model.service.core.catalog.components.Component;
import org.fusesource.ide.camel.model.service.core.catalog.components.ComponentModel;

/**
 * @author Aurelien Pupier
 *
 */
public class ComponentManager {

	private Map<String, Set<Component>> tagMap = new HashMap<>();
	private Set<Component> noTagComponents = new HashSet<>();
	private ComponentModel componentModel;

	public ComponentManager(ComponentModel componentModel) {
		this.componentModel = componentModel;
		initTagMap(componentModel);
	}

	/**
	 * @param componentModel
	 */
	private void initTagMap(ComponentModel componentModel) {
		for (Component component : componentModel.getSupportedComponents()) {
			final List<String> componentTags = component.getTags();
			if (componentTags == null || componentTags.isEmpty()) {
				noTagComponents.add(component);
			} else {
				for (String tag : componentTags) {
					final Set<Component> componentsForTag = tagMap.get(tag);
					if (componentsForTag != null) {
						componentsForTag.add(component);
					} else {
						final Set<Component> set = new HashSet<>();
						set.add(component);
						tagMap.put(tag, set);
					}
				}
			}
		}
	}

	public Set<Component> getComponentForTag(String tag) {
		final Set<Component> componentSet = tagMap.get(tag);
		return componentSet != null ? componentSet : Collections.<Component> emptySet();
	}

	public Set<Component> getComponentWithoutTag() {
		return noTagComponents;
	}

	public Set<String> getTags() {
		return tagMap.keySet();
	}

	public Set<Component> getAllComponents() {
		return new HashSet<>(componentModel.getSupportedComponents());
	}

}
