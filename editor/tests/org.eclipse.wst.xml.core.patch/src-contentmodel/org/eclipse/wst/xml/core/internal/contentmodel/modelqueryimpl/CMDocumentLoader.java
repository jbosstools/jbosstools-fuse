/*******************************************************************************
 * Copyright (c) 2002, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.contentmodel.modelqueryimpl;
                          
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.CRC32;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.sse.core.utils.StringUtils;
import org.eclipse.wst.xml.core.internal.Logger;
import org.eclipse.wst.xml.core.internal.XMLCorePlugin;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.CMDocumentManager;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.IExternalSchemaLocationProvider;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.xml.core.internal.contentmodel.util.CMDocumentCache;
import org.eclipse.wst.xml.core.internal.contentmodel.util.NamespaceInfo;
import org.eclipse.wst.xml.core.internal.contentmodel.util.NamespaceTable;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 *
 */
public class CMDocumentLoader
{                                           
  private final static boolean _trace = Boolean.valueOf(Platform.getDebugOption("org.eclipse.wst.xml.core/externalSchemaLocation")).booleanValue(); //$NON-NLS-1$
  protected Document document;
  protected ModelQuery modelQuery;
  protected CMDocumentManager cmDocumentManager;
  protected boolean isInferredGrammarEnabled = true;  
  protected CMDocumentLoadingNamespaceTable namespaceTable;
  protected int count = 0;
    
  public CMDocumentLoader(Document document, ModelQuery modelQuery)
  {                             
    this(document, modelQuery.getCMDocumentManager());
  }
  
  public CMDocumentLoader(Document document, CMDocumentManager cmDocumentManager)
  {   
    this.document = document;                     
    this.cmDocumentManager = cmDocumentManager;  	
  }
  
  public void loadCMDocuments()
  {          
    //System.out.println("----------loadCMDocuments ------------");          
    //long time = System.currentTimeMillis();
       
    boolean walkDocument = false;
            
    cmDocumentManager.removeAllReferences();
      
    String[] doctypeInfo = XMLAssociationProvider.getDoctypeInfo(document);
    if (doctypeInfo != null)
    {
      // load the doctype if required
      walkDocument = handleGrammar(doctypeInfo[0], doctypeInfo[1], "DTD"); //$NON-NLS-1$
    }                                   
    else
    {      
    	if (document.getDoctype() != null) {
    		final String internalSubset = document.getDoctype().getInternalSubset();
    		if (internalSubset != null) {
    			IPath path = getInternalSubsetPath(document);
    			if (!Path.EMPTY.equals(path)) {
    				File subsets = path.removeLastSegments(1).toFile();
        			if (!subsets.exists()) {
        				subsets.mkdir();
        			}
        			FileOutputStream stream = null;
        			try {
        				stream = new FileOutputStream(path.toFile());
        				final String charset = ((IFile) ((IAdaptable) document).getAdapter(IResource.class)).getCharset();
        				stream.write(internalSubset.getBytes(charset != null ? charset : "UTF-8")); //$NON-NLS-1$
        				stream.flush();
        			} catch (FileNotFoundException e) {
        				Logger.logException(e);
        			} catch (CoreException e) {
        				Logger.logException(e);
        			} catch (UnsupportedEncodingException e) {
        				Logger.logException(e);
        			} catch (IOException e) {
        				Logger.logException(e);
        			}
        			finally {
        				if (stream != null) {
        					try {
        						stream.close();
        					}
        					catch (IOException e) {
        						Logger.logException(e);
        					}
        				}
        			}
        			walkDocument = handleGrammar(path.toPortableString(), path.toFile().toURI().toString(), "DTD"); //$NON-NLS-1$
    			}
    		}
    	}
    	else {
	      Element element = getRootElement(document);
	      if (element != null)
	      {
	        namespaceTable = new CMDocumentLoadingNamespaceTable(document);   
	        namespaceTable.addElement(element);
	        if (namespaceTable.isNamespaceEncountered())
	        {   
	          walkDocument = true;
	          //System.out.println("isNamespaceAware");
	        }
	        else
	        {
	          namespaceTable = null;
	          walkDocument = isInferredGrammarEnabled;
	          //System.out.println("is NOT namespaceAware");
	        }        
	      }
    	}
    } 

    if (walkDocument)
    {
    	if (!checkExternalSchema())
    		visitNode(document);   
    } 

    //System.out.println("--- elapsed time (" + count + ") = " + (System.currentTimeMillis() - time));
  }

  protected boolean checkExternalSchema() {
	  boolean externalSchemaLoaded = false;
	  if (document instanceof IDOMDocument) {
		  final String baseLocation = ((IDOMDocument) document).getModel().getBaseLocation();
		  if (baseLocation == null)
			  return false;
		  final IPath basePath = new Path(baseLocation);
		  IFile file = null;
		  if (basePath.segmentCount() > 1) {
			  file = ResourcesPlugin.getWorkspace().getRoot().getFile(basePath);
		  }
		  final URI uri = (file == null || !file.isAccessible()) ? new File(baseLocation).toURI() : file.getLocationURI();
		  if (uri != null) {
			  IExternalSchemaLocationProvider[] providers = ExternalSchemaLocationProviderRegistry.getInstance().getProviders();
			  for (int i = 0; i < providers.length; i++) {
				  long time = _trace ? System.currentTimeMillis(): 0;
				  final Map locations = providers[i].getExternalSchemaLocation(uri);
				  if (_trace) {
					  long diff = System.currentTimeMillis() - time;
					  if (diff > 250)
						  Logger.log(Logger.INFO, "Schema location provider took [" + diff + "ms] for URI [" + uri + "]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				  }
				  if (locations != null && !locations.isEmpty()) {
					  // Use the externalSchemaLocation
					  if (namespaceTable != null && namespaceTable.isNamespaceEncountered()) {
						  final String location = locations.get(IExternalSchemaLocationProvider.SCHEMA_LOCATION).toString();
						  if (location != null) {
							  final String[] ids = StringUtils.asArray(location);
							  // namespace : location pairs
							  if (ids.length >= 2 && ids.length % 2 == 0) {
								  if (!externalSchemaLoaded)
									  cmDocumentManager.removeAllReferences();
								  for (int j = 0; j < ids.length; j+=2) {
									  handleGrammar(ids[j], ids[j + 1], "XSD"); //$NON-NLS-1$
									  externalSchemaLoaded = true;
								  }
							  }
						  }
					  }
					  else { // noNamespace
						  handleGrammar(uri.toString(), locations.get(IExternalSchemaLocationProvider.NO_NAMESPACE_SCHEMA_LOCATION).toString(), "XSD"); //$NON-NLS-1$
						  externalSchemaLoaded = true;
						  break;
					  }
				  }
			  }
		  }
	  }
	  return externalSchemaLoaded;
  }

  public boolean handleGrammar(String publicId, String systemId, String type)
  {           
    boolean result = false;
    
    int status = cmDocumentManager.getCMDocumentStatus(publicId);
    if (status == CMDocumentCache.STATUS_NOT_LOADED)
    {
      cmDocumentManager.addCMDocumentReference(publicId, systemId, type);
    }                 
    else if (status == CMDocumentCache.STATUS_ERROR)
    {
      result = true;
    }
    return result;
  } 
    

  public void handleElement(Element element)
  {  
    visitChildNodes(element);
  }

                             
  public void handleElementNS(Element element)
  {
    namespaceTable.addElement(element);
    visitChildNodes(element);
  }
                                                    

  public void visitNode(Node node)
  {                    
    int nodeType = node.getNodeType();
    if (nodeType == Node.ELEMENT_NODE)
    {
      count++;       

      Element element = (Element)node;    
      if (namespaceTable == null)
      {
        handleElement(element); 
      }
      else
      {
        handleElementNS(element);
      }            
    }
    else if (nodeType == Node.DOCUMENT_NODE)
    {
      visitChildNodes(node);
    }
  }


  protected void visitChildNodes(Node node)
  {   
	  for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) 
    {
	    visitNode(child);
    }
	}             


  protected class CMDocumentLoadingNamespaceTable extends NamespaceTable
  {                                        
    protected List newNamespaceList;

    public CMDocumentLoadingNamespaceTable(Document document)
    {                                                          
      super(document);     
    }  
                                           

    public void addElement(Element element)
    {                               
      newNamespaceList = null;
      super.addElement(element);  
      if (newNamespaceList != null)
      {
        for (Iterator i = newNamespaceList.iterator(); i.hasNext(); )
        {
          NamespaceInfo info = (NamespaceInfo)i.next();
          handleGrammar(info.uri, info.locationHint, "XSD"); //$NON-NLS-1$
        }
      }
    }                 
     
                               
    protected void internalAddNamespaceInfo(String key, NamespaceInfo info)
    {
      super.internalAddNamespaceInfo(key, info);           
      if (newNamespaceList == null)
      {
        newNamespaceList = new ArrayList();
      }
      newNamespaceList.add(info);    
    }                     
  }

  
  protected Element getRootElement(Document document)
  {
    Element result = null;
    NodeList nodeList = document.getChildNodes();
    int nodeListLength = nodeList.getLength();
    for (int i = 0 ; i < nodeListLength; i++)
    {
      Node node = nodeList.item(i);
      if (node.getNodeType() == Node.ELEMENT_NODE)
      {
        result = (Element)node;
        break;
      }
    }
    return result;
  }

  static IPath getInternalSubsetPath(Document document) {
	  IPath path = Path.EMPTY;
	  if (document instanceof IAdaptable) {
			final IResource resource = (IResource) ((IAdaptable) document).getAdapter(IResource.class);
			if (resource instanceof IFile) {
				CRC32 calc = new CRC32();
				calc.update(resource.getFullPath().toString().getBytes());
	  			path = XMLCorePlugin.getDefault().getStateLocation().append("internalsubsets").append(calc.getValue() + ".dtd"); //$NON-NLS-1$ //$NON-NLS-2$
			}
	  }
	  return path;
  }
}
