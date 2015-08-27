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
package org.fusesource.ide.camel.editor.provider.generated;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.fusesource.ide.camel.editor.features.create.ext.CreateFigureFeature;
import org.fusesource.ide.camel.editor.provider.ImageProvider;
import org.fusesource.ide.camel.model.service.core.catalog.CamelModel;
import org.fusesource.ide.camel.model.service.core.catalog.CamelModelFactory;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;

/**
 * NOTE - this file is auto-generated using Velocity.
 *
 * DO NOT EDIT!
 *
 * @author lhein
 */
public class ProviderHelper {

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
    	ArrayList<ICreateFeature> ret = new ArrayList<ICreateFeature>();
    	CamelModel model = CamelModelFactory.getModelForVersion(CamelModelFactory.getLatestCamelVersion());
    	ArrayList<Eip> list = model.getEipModel().getSupportedEIPs();
    	Iterator<Eip> it = list.iterator();
    	while(it.hasNext()) {
    		Eip next = it.next();
    		try {
    			ICreateFeature f = new CreateFigureFeature(fp,
    					convertCamelCase(next.getName()), 
    					"Create a " + convertCamelCase(next.getName()), next);
    			if( f != null )
    				ret.add(f);
    		} catch(Exception e) {
    			e.printStackTrace();
    			// TODO logging
    		}
    	}
    	
    	return (ICreateFeature[]) ret.toArray(new ICreateFeature[ret.size()]);
    }

    /**
     * Helper method which provides all images for the figures in the palette
     *
     * @param imageProvider the image provider to use
     */
    public static void addFigureIcons(ImageProvider imageProvider) {
    	CamelModel model = CamelModelFactory.getModelForVersion(CamelModelFactory.getLatestCamelVersion());
    	ArrayList<Eip> list = model.getEipModel().getSupportedEIPs();
    	Iterator<Eip> it = list.iterator();
    	while(it.hasNext()) {
            imageProvider.addIconsForEIP(it.next());
    	}
    }

}
