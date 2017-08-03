/*******************************************************************************
 * Copyright (c) 2001, 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xml.core.internal.validation;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolver;
import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolverPlugin;
import org.eclipse.wst.common.uriresolver.internal.util.URIHelper;
import org.eclipse.wst.xml.core.internal.Logger;
import org.eclipse.wst.xml.core.internal.XMLCorePlugin;
import org.eclipse.wst.xml.core.internal.preferences.XMLCorePreferenceNames;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;

import com.ibm.icu.util.StringTokenizer;

/**
 * A helper class for the XML validator.
 *
 * @author Craig Salter, IBM
 * @author Lawrence Mandel, IBM
 */
public class ValidatorHelper
{
  public List namespaceURIList = new Vector();
  public boolean isGrammarEncountered = false;
  public boolean isDTDEncountered = false;
  public boolean isNamespaceEncountered = false;
  public String schemaLocationString = ""; //$NON-NLS-1$
  public int numDTDElements = 0;
  public boolean isDocumentElementEncountered = false;

  public static final boolean _trace = Boolean.valueOf(Platform.getDebugOption("org.eclipse.wst.xml.core/debug/validation")).booleanValue(); //$NON-NLS-1$
  /**
   * Constructor.
   */
  public ValidatorHelper()
  {
  }

  /**
   * Create an XML Reader.
   *
   * @return An XML Reader if one can be created or null.
   * @throws SAXNotSupportedException
   * @throws SAXNotRecognizedException
   */
  protected XMLReader createXMLReader(String uri) throws SAXNotRecognizedException, SAXNotSupportedException {
    XMLReader reader = null;

    ClassLoader originalClzLoader = Thread.currentThread().getContextClassLoader();
	Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

	try{
		reader = new org.apache.xerces.parsers.SAXParser();
	}finally{
		Thread.currentThread().setContextClassLoader(originalClzLoader);
	}

    reader.setFeature("http://apache.org/xml/features/continue-after-fatal-error", false); //$NON-NLS-1$
    reader.setFeature("http://xml.org/sax/features/namespace-prefixes", true); //$NON-NLS-1$
    reader.setFeature("http://xml.org/sax/features/namespaces", false); //$NON-NLS-1$
    reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false); //$NON-NLS-1$

    // http://bugs.eclipse.org/508083
    String xmlCoreId = XMLCorePlugin.getDefault().getBundle().getSymbolicName();
    boolean resolveExternalEntities = InstanceScope.INSTANCE.getNode(xmlCoreId).getBoolean(XMLCorePreferenceNames.RESOLVE_EXTERNAL_ENTITIES, false);

    reader.setFeature("http://xml.org/sax/features/external-general-entities", resolveExternalEntities); //$NON-NLS-1$
    reader.setFeature("http://xml.org/sax/features/external-parameter-entities", resolveExternalEntities); //$NON-NLS-1$

    reader.setContentHandler(new MyContentHandler(uri));
    reader.setErrorHandler(new InternalErrorHandler());

    LexicalHandler lexicalHandler = new LexicalHandler()
    {
      public void startDTD (String name, String publicId, String systemId)
      {
        isGrammarEncountered = true;
        isDTDEncountered = true;
      }

      public void endDTD() throws SAXException
      {
      }

      public void startEntity(String name) throws SAXException
      {
      }

      public void endEntity(String name) throws SAXException
      {
      }

      public void startCDATA() throws SAXException
      {
      }

      public void endCDATA() throws SAXException
      {
      }

      public void comment (char ch[], int start, int length) throws SAXException
      {
      }
    };
    reader.setProperty("http://xml.org/sax/properties/lexical-handler", lexicalHandler); //$NON-NLS-1$

    return reader;
  }

  /**
   * An error handler to suppress error and warning information.
   */
  private class InternalErrorHandler implements org.xml.sax.ErrorHandler
  {
	public InternalErrorHandler()
	{
	  super();
	}

    public void error(SAXParseException exception) throws SAXException
    {
    }

    public void fatalError(SAXParseException exception) throws SAXException
    {
    }

    public void warning(SAXParseException exception) throws SAXException
    {
    }
  }


  /**
   * Figures out the information needed for validation.
   *
   * @param uri The uri of the file to validate.
   * @param uriResolver A helper to resolve locations.
   */
  public void computeValidationInformation(String uri, Reader characterStream, URIResolver uriResolver)
  {
	  try {
	      XMLReader reader = createXMLReader(uri);
	      InputSource inputSource = new InputSource(uri);
	      inputSource.setCharacterStream(characterStream);

	      ClassLoader originalClzLoader = Thread.currentThread().getContextClassLoader();
	      Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
	      try {
	    	  reader.parse(inputSource);
	      }
	      finally {
	    	  Thread.currentThread().setContextClassLoader(originalClzLoader);
	      }


	  }
	  catch (SAXException e) {
		  if (_trace)
			  Logger.logException(e);
	  }
	  catch (IOException e) {
		  if (_trace)
			  Logger.logException(e);
	  }
  }



  /**
   * Handle the content while parsing the file.
   */
  class MyContentHandler extends org.xml.sax.helpers.DefaultHandler
  {
    /* (non-Javadoc)
     * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
     */
    boolean isRootElement = true;
    String baseURI;

    MyContentHandler(String uri)
    {
      this.baseURI = uri;
    }

    public void error(SAXParseException e) throws SAXException
    {
    }
    /* (non-Javadoc)
     * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
     */
    public void fatalError(SAXParseException e) throws SAXException
    {
    }
    /* (non-Javadoc)
     * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
     */
    public void warning(SAXParseException e) throws SAXException
    {
    }
    public String getPrefix(String name)
    {
      String prefix = null;
      int index = name.indexOf(":"); //$NON-NLS-1$
      if (index != -1)
      {
        prefix = name.substring(0, index);
      }
      return prefix;
    }

    public String getUnprefixedName(String name)
    {
      int index = name.indexOf(":"); //$NON-NLS-1$
      if (index != -1)
      {
        name = name.substring(index + 1);
      }
      return name;
    }

    public String getPrefixedName(String prefix, String localName)
    {
      return prefix != null && prefix.length() > 0 ? prefix + ":" + localName : localName;      //$NON-NLS-1$
    }

    public void startElement(String namespaceURI, String localName, String rawName, Attributes atts)
    {
      //String explicitLocation = null;
      if (isRootElement)
      {
        isDocumentElementEncountered = true;
        isRootElement = false;
        int nAtts = atts.getLength();
        String schemaInstancePrefix = null;
        for (int i =0; i < nAtts; i++)
        {
          String attributeName = atts.getQName(i);
          if (attributeName.equals("xmlns") || attributeName.startsWith("xmlns:")) //$NON-NLS-1$ //$NON-NLS-2$
          {
            isNamespaceEncountered = true;
            String value = atts.getValue(i);
            if (value.startsWith("http://www.w3.org/") && value.endsWith("/XMLSchema-instance")) //$NON-NLS-1$ //$NON-NLS-2$
            {
              schemaInstancePrefix = attributeName.equals("xmlns") ? "" : getUnprefixedName(attributeName); //$NON-NLS-1$ //$NON-NLS-2$
            }
          }
        }

        String prefix = getPrefix(rawName);
        String rootElementNamespaceDeclarationName = (prefix != null && prefix.length() > 0) ? "xmlns:" + prefix : "xmlns"; //$NON-NLS-1$ //$NON-NLS-2$
        String rootElementNamespace = rootElementNamespaceDeclarationName != null ? atts.getValue(rootElementNamespaceDeclarationName) : null;

        String location = null;

        // first we use any 'xsi:schemaLocation' or 'xsi:noNamespaceSchemaLocation' attribute
        // to determine a location
        if (schemaInstancePrefix != null)
        {
          location = atts.getValue(getPrefixedName(schemaInstancePrefix, "noNamespaceSchemaLocation")); //$NON-NLS-1$
          if (location == null)
          {
        	String schemaLoc = atts.getValue(getPrefixedName(schemaInstancePrefix, "schemaLocation"));  //$NON-NLS-1$
            location = getSchemaLocationForNamespace(schemaLoc, rootElementNamespace);
          }
        }
        if (rootElementNamespace == null)
        {
          rootElementNamespace = "";
        }

        location = URIResolverPlugin.createResolver().resolve(baseURI, rootElementNamespace, location);
        location = URIResolverPlugin.createResolver().resolvePhysicalLocation(baseURI, rootElementNamespace, location);
        if (location != null)
        {
          location = URIHelper.addImpliedFileProtocol(location);
        }

        schemaLocationString = location;

        if (location != null)
        {
          InputStream is = null;
          try
          {
            URL url = new URL(location);
            is = url.openStream();
            isGrammarEncountered = true;
          }
          catch(Exception e)
          {
        	// Do nothing.
            if(_trace)
              Logger.logException(e);
          }
          finally
          {
        	if(is != null)
        	{
        	  try
        	  {
        	    is.close();
        	  }
        	  catch(Exception e)
        	  {
        		// Do nothing.
        	  }
        	}
          }
        }
      }
    }
    /* (non-Javadoc)
     * @see org.xml.sax.ext.DeclHandler#elementDecl(java.lang.String, java.lang.String)
     */
    public void elementDecl(String name, String model)
    {
      numDTDElements++;
    }

    // The xsiSchemaLocationValue is a list of namespace/location pairs that are separated by whitespace
    // this method walks the list of pairs looking for the specified namespace and returns the associated
    // location.
    //
    protected String getSchemaLocationForNamespace(String xsiSchemaLocationValue, String namespace)
    {
      String result = null;
      if (xsiSchemaLocationValue != null && namespace != null)
      {

        StringTokenizer st = new StringTokenizer(xsiSchemaLocationValue);
        while(st.hasMoreTokens())
        {
          if(st.nextToken().equals(namespace))
          {
            if(st.hasMoreTokens())
            {
              result = st.nextToken();
            }
          }
          else
          {
            if(st.hasMoreTokens())
            {
              st.nextToken();
            }
          }
        }
      }
      return result;
    }
  }


  /**
   * Replace all instances in the string of the old pattern with the new pattern.
   *
   * @param string The string to replace the patterns in.
   * @param oldPattern The old pattern to replace.
   * @param newPattern The pattern used for replacement.
   * @return The modified string with all occurrances of oldPattern replaced by new Pattern.
   */
  protected static String replace(String string, String oldPattern, String newPattern)
  {
    int index = 0;
    while (index != -1)
    {
      index = string.indexOf(oldPattern, index);
      if (index != -1)
      {
        string = string.substring(0, index) + newPattern + string.substring(index + oldPattern.length());
        index = index + oldPattern.length();
      }
    }
    return string;
  }
}
