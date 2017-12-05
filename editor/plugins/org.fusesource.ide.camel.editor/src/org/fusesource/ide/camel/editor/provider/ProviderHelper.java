/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.editor.provider;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.fusesource.ide.camel.editor.features.create.ext.CreateFigureFeature;
import org.fusesource.ide.camel.editor.utils.CamelUtils;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelCatalogCacheManager;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelModel;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;

/**
 * @author lhein
 */
public class ProviderHelper {

	public static final String CATEGORY_CONTROL_FLOW = "Control Flow";
	public static final String CATEGORY_ROUTING = "Routing";
	public static final String CATEGORY_MISC = "Miscellaneous";
	public static final String CATEGORY_COMPONENTS = "Components";
	public static final String CATEGORY_TRANSFORMATION = "Transformation";
	// used for specifying that an EIP doesn't belong to any of the above categories
	public static final String CATEGORY_NONE = "NONE";

	private static final HashMap<String, String> categoryMap;

	static {
		categoryMap = new HashMap<>();
		categoryMap.put("aggregate", CATEGORY_ROUTING);
		categoryMap.put("aop", CATEGORY_MISC);
		categoryMap.put("bean", CATEGORY_COMPONENTS);
		categoryMap.put("choice", CATEGORY_ROUTING);
		categoryMap.put("convertBodyTo", CATEGORY_TRANSFORMATION);
		categoryMap.put("delay",CATEGORY_CONTROL_FLOW);
		categoryMap.put("doCatch",CATEGORY_CONTROL_FLOW);
		categoryMap.put("doFinally",CATEGORY_CONTROL_FLOW);
		categoryMap.put("enrich", CATEGORY_TRANSFORMATION);
		categoryMap.put("filter", CATEGORY_ROUTING);
		categoryMap.put("hystrix", CATEGORY_ROUTING);
		categoryMap.put("idempotentConsumer", CATEGORY_ROUTING);
		categoryMap.put("inOnly", CATEGORY_TRANSFORMATION);
		categoryMap.put("inOut", CATEGORY_TRANSFORMATION);
		categoryMap.put("intercept",CATEGORY_CONTROL_FLOW);
		categoryMap.put("interceptFrom",CATEGORY_CONTROL_FLOW);
		categoryMap.put("interceptSendToEndpoint",CATEGORY_CONTROL_FLOW);
		categoryMap.put("loadBalance", CATEGORY_ROUTING);
		categoryMap.put("loop",CATEGORY_CONTROL_FLOW);
		categoryMap.put("log", CATEGORY_COMPONENTS);
		categoryMap.put("marshal", CATEGORY_TRANSFORMATION);
		categoryMap.put("multicast", CATEGORY_ROUTING);
		categoryMap.put("onCompletion",CATEGORY_CONTROL_FLOW);
		categoryMap.put("onException",CATEGORY_CONTROL_FLOW);
		categoryMap.put("onFallback",CATEGORY_CONTROL_FLOW);
		categoryMap.put("otherwise", CATEGORY_ROUTING);
		categoryMap.put("pipeline", CATEGORY_ROUTING);
		categoryMap.put("policy", CATEGORY_MISC);
		categoryMap.put("pollEnrich", CATEGORY_TRANSFORMATION);
		categoryMap.put("process", CATEGORY_COMPONENTS);
		categoryMap.put("recipientList", CATEGORY_ROUTING);
		categoryMap.put("removeHeader", CATEGORY_TRANSFORMATION);
		categoryMap.put("removeHeaders", CATEGORY_TRANSFORMATION);
		categoryMap.put("removeProperty", CATEGORY_TRANSFORMATION);
		categoryMap.put("removeProperties", CATEGORY_TRANSFORMATION);
		categoryMap.put("resequence", CATEGORY_ROUTING);
		categoryMap.put("rollback",CATEGORY_CONTROL_FLOW);
		categoryMap.put("route",CATEGORY_ROUTING);
		categoryMap.put("routingSlip", CATEGORY_ROUTING);
		categoryMap.put("sample", CATEGORY_MISC);
		categoryMap.put("setBody", CATEGORY_TRANSFORMATION);
		categoryMap.put("setExchangePattern", CATEGORY_TRANSFORMATION);
		categoryMap.put("setFaultBody", CATEGORY_TRANSFORMATION);
		categoryMap.put("setHeader", CATEGORY_TRANSFORMATION);
		categoryMap.put("setOutHeader", CATEGORY_TRANSFORMATION);
		categoryMap.put("setProperty", CATEGORY_TRANSFORMATION);
		categoryMap.put("sort", CATEGORY_ROUTING);
		categoryMap.put("split", CATEGORY_ROUTING);
		categoryMap.put("stop", CATEGORY_MISC);
		categoryMap.put("threads", CATEGORY_MISC);
		categoryMap.put("throttle",CATEGORY_CONTROL_FLOW);
		categoryMap.put("throwException",CATEGORY_CONTROL_FLOW);
		categoryMap.put("transacted",CATEGORY_CONTROL_FLOW);
		categoryMap.put("transform", CATEGORY_TRANSFORMATION);
		categoryMap.put("doTry",CATEGORY_CONTROL_FLOW);
		categoryMap.put("unmarshal", CATEGORY_TRANSFORMATION);
		categoryMap.put("validate", CATEGORY_MISC);
		categoryMap.put("when", CATEGORY_ROUTING);
		categoryMap.put("wireTap", CATEGORY_ROUTING);
	}
	
    private ProviderHelper() {
        // private default constructor
    }

    protected static String convertCamelCase(String original) {
    	String display = original.replaceAll("(\\p{Ll})(\\p{Lu})","$1 $2");
    	return capitalizeFirstLetter(display);
    }

    protected static String capitalizeFirstLetter(String input) {
    	return input.substring(0,1).toUpperCase() + input.substring(1);
    }
    
    /**
     * Helper method to return all create features available for the palette
     *
     * @param fp the feature provider
     * @return an array of create features for the palette
     */
    public static ICreateFeature[] getCreateFeatures(IFeatureProvider fp) {
    	CamelModel model = CamelCatalogCacheManager.getInstance().getCamelModelForProject(CamelUtils.project(fp), new NullProgressMonitor());
    	Collection<Eip> list = model.getEips();
    	return list.stream()
    		.map(eip -> new CreateFigureFeature(fp, convertCamelCase(eip.getName()), eip.getDescription(), eip))
    		.toArray(ICreateFeature[]::new);
    }

    /**
     * Helper method which provides all images for the figures in the palette
     *
     * @param imageProvider the image provider to use
     */
    public static void addFigureIcons(ImageProvider imageProvider) {
    	CamelModel model = CamelCatalogCacheManager.getInstance().getCamelModelForProject(CamelUtils.project(), new NullProgressMonitor());
    	model.getEips().forEach(imageProvider::addIconsForEIP);
    }

    /**
     * returns the correct category from a list of tags for a given eip
     * 
     * @param tags
     * @return
     */
    public static String getCategoryFromTags(List<String> tags) {
    	return tags.get(0); // TODO: we need a good way to find out the correct category for a tag list
    }
    
    public static String getCategoryFromEip(Eip eip) {
    	if (categoryMap.containsKey(eip.getName())) {
    		return categoryMap.get(eip.getName());
    	}
    	return CATEGORY_NONE;
    }

	public static boolean belongsToAnyCategory(Eip eip) {
		return !CATEGORY_NONE.equals(getCategoryFromEip(eip));
	}

}
