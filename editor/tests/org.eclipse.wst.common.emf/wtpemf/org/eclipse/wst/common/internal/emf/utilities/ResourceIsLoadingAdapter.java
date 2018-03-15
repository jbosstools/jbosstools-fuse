/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 * Created on Oct 26, 2004
 */
package org.eclipse.wst.common.internal.emf.utilities;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * @author mdelder
 */
public class ResourceIsLoadingAdapter extends AdapterImpl {

    private static final Class RESOURCE_IS_LOADING_ADAPTER_CLASS = ResourceIsLoadingAdapter.class;

    public ResourceIsLoadingAdapter() {
    }

    public static ResourceIsLoadingAdapter findAdapter(Resource aResource) {
        ResourceIsLoadingAdapter adapter = null;
        //System.out.println("ResourceIsLoadingAdapter Synchronizing on " + aResource);
        
        /* Synchronize on the Resource (which will be the target of 
         * the ResourceIsLoadingAdapter in the list, if it exists).
         * 
         * removeIsLoadingSupport() will coordinate with this 
         * synchronization.
         */
        EList<Adapter> resourceAdapters = aResource.eAdapters();
		synchronized(resourceAdapters) {

			try {
			ArrayList<Adapter> resourceAdaptersCopy = new ArrayList<>(resourceAdapters);
        	adapter = (ResourceIsLoadingAdapter) getAdapter(resourceAdaptersCopy, ResourceIsLoadingAdapter.class);
			} catch(ArrayIndexOutOfBoundsException aioobe) {
				System.out.println("size of the list:" + resourceAdapters.size()); //$NON-NLS-1$
				for (Adapter adapterTemp : resourceAdapters) {
					System.out.println(adapterTemp);
				}
			}
        }
        
        return adapter;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.emf.common.notify.Adapter#notifyChanged(org.eclipse.emf.common.notify.Notification)
     */
    @Override
	public void notifyChanged(Notification notification) {

        if (notification.getNotifier() != null) {

            // listen for the remove of the loading adapter
            if (isSetLoadedResourceNotification(notification)) removeIsLoadingSupport();
        }
    }

    /**
     * Default implementation is a no-op.
     */
    public void waitForResourceToLoad() {

    }
    public static Adapter getAdapter(List<Adapter> adapters, Object type)
    {
      for (int i = 0, size = adapters.size(); i < size; ++i)
      {
        Adapter adapter = adapters.get(i);
        if (adapter != null && adapter.isAdapterForType(type))
        {
          return adapter;
        }
      }
      return null;
    }

    /**
     * @param notification
     * @return
     */
    protected boolean isSetLoadedResourceNotification(Notification notification) {
        return notification.getFeatureID(null) == Resource.RESOURCE__IS_LOADED && notification.getEventType() == Notification.SET;
    }

    protected void removeIsLoadingSupport() {

        /* Synchronize on the target of the Adapter. If 
         * the list of adapters is searched for a 
         * ResourceIsLoadingAdapter using the 
         * ResourceIsLoadingAdapter.findAdapter() API, then
         * the access to remove the Adapter using this method 
         * will be coordinated.  
         */
        if (getTarget() != null) {
            //System.out.println("ResourceIsLoadingAdapter Synchronizing on " + getTarget());
            synchronized (getTarget().eAdapters()) {
                getTarget().eAdapters().remove(this);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.emf.common.notify.Adapter#isAdapterForType(java.lang.Object)
     */
    @Override
	public boolean isAdapterForType(Object type) {
        return type == RESOURCE_IS_LOADING_ADAPTER_CLASS;
    }

    /**
     * 
     */
    public void forceRelease() {

    }

}
