/*******************************************************************************
 * Copyright (c) 2002, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     Jesper Steen Moller - jesper@selskabet.org - bug 112284
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.catalog;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.wst.xml.core.internal.Logger;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalog;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalogElement;
import org.eclipse.wst.xml.core.internal.validation.ValidatorHelper;


public class CatalogElement implements ICatalogElement
{
	int type;

	String id;

	String base;

	Map attributes = new HashMap();

	ICatalog ownerCatalog;

	public CatalogElement(int aType)
	{
		super();
		type = aType;
	}

	public int getType()
	{
		return type;
	}

	public String getBase()
	{
		return base;
	}

	public void setBase(String base)
	{
		this.base = base;
	}

	public String getAttributeValue(String name)
	{
		return (String) attributes.get(name);
	}

	public void setAttributeValue(String name, String value)
	{
		attributes.put(name, value);
	}

	public String[] getAttributes()
	{
		Collection c = attributes.values();
		String[] result = new String[c.size()];
		attributes.keySet().toArray(result);
		return result;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public ICatalog getOwnerCatalog()
	{
		return ownerCatalog;
	}

	public void setOwnerCatalog(ICatalog catalog)
	{
		this.ownerCatalog = catalog;
	}

	protected static String makeAbsolute(String baseLocation, String location)
	{
		URL local = null;
		location = location.replace('\\', '/');
		try
		{
			URL baseURL = new URL(baseLocation);
			local = new URL(baseURL, location);
		} catch (MalformedURLException e)
		{
			if(ValidatorHelper._trace) {
				Logger.logException(e);
			}
		}

		if (local != null)
		{
			return local.toString();
		} else
		{
			return location;
		}
	}

	public String getAbsolutePath(String path)
	{
		try
		{
			URI uri = new URI(path);
			if (uri.isAbsolute())
			{
				return path;
			}
		} catch (URISyntaxException e)
		{
			if(ValidatorHelper._trace) {
				Logger.logException(e);
			}
		}

		if (this.base != null && !this.base.equals("")) //$NON-NLS-1$
		{
			return makeAbsolute(base, path);		
		}
		
		String result = path;
		Catalog catalog = (Catalog) getOwnerCatalog();
		if (catalog != null)
		{
			String base = catalog.getBase();
			if (base == null || base.equals("")) //$NON-NLS-1$
			{
				base = catalog.getLocation();
			}
			result = makeAbsolute(base, path);
		}
		return result;
	}
	
	  /*
	   * Since we don't have events notifications for entry properties, clone()
	   * could allow to copy and edit entry and then replace it in catalog. Entry
	   * replacement will signal ICatalogEvent @return
	   */
	  public Object clone()
	  {
		ICatalogElement element = ownerCatalog.createCatalogElement(type);
	    String[] attributes = getAttributes();   
	    for (int i = 0; i < attributes.length; i++)
	    {
	      String attrName = attributes[i];
	      String attrValue = getAttributeValue(attrName);
	      element.setAttributeValue(attrName, attrValue);
	    }
		element.setOwnerCatalog(ownerCatalog);
		element.setId(id);
		element.setBase(base);
	    return element;
	  }

	
}
