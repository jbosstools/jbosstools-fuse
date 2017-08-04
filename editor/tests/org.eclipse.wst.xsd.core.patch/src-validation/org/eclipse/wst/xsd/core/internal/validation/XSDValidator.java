/*******************************************************************************
 * Copyright (c) 2001, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     David Carver, Standards for Technology in Automotive Retail, bug 1147033
 *******************************************************************************/
package org.eclipse.wst.xsd.core.internal.validation;

import java.io.IOException;
import java.io.InputStream;

import org.apache.xerces.impl.Constants;
import org.apache.xerces.parsers.XMLGrammarPreparser;
import org.apache.xerces.util.XMLGrammarPoolImpl;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.grammars.XMLGrammarDescription;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.apache.xerces.xni.parser.XMLErrorHandler;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.parser.XMLParseException;
import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolver;
import org.eclipse.wst.xml.core.internal.Logger;
import org.eclipse.wst.xml.core.internal.validation.ValidatorHelper;
import org.eclipse.wst.xml.core.internal.validation.XMLValidator;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationInfo;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationReport;
import org.w3c.dom.DOMError;

/**
 * The XSDValidator will validate XSD files.
 */
public class XSDValidator
{
  protected URIResolver uriresolver = null;

  public ValidationReport validate(String uri)
  {
    return validate(uri, null);
  }
  
  public ValidationReport validate(String uri, InputStream inputStream)
  {
	return validate(uri, null, null);
  }
  
  /**
   * Validate the XSD file specified by the URI.
   * 
   * @param uri
   * 		The URI of the XSD file to validate.
   * @param inputStream 
   * 		An input stream representing the XSD file to validate.
   * @param configuration
   * 		A configuration for this validation run.
   */
  public ValidationReport validate(String uri, InputStream inputStream, XSDValidationConfiguration configuration)
  {
	if(configuration == null)
	{
	  configuration = new XSDValidationConfiguration();
	}
	ValidationInfo valinfo = new ValidationInfo(uri);
	XSDErrorHandler errorHandler = new XSDErrorHandler(valinfo);
	try
	{
	  XMLGrammarPreparser grammarPreparser = new XMLGrammarPreparser();
	  grammarPreparser.registerPreparser(XMLGrammarDescription.XML_SCHEMA,null/*schemaLoader*/);
		  
	  grammarPreparser.setProperty(Constants.XERCES_PROPERTY_PREFIX + Constants.XMLGRAMMAR_POOL_PROPERTY, new XMLGrammarPoolImpl());
	  grammarPreparser.setFeature(Constants.XERCES_FEATURE_PREFIX + Constants.CONTINUE_AFTER_FATAL_ERROR_FEATURE, false);
      grammarPreparser.setFeature(Constants.XERCES_FEATURE_PREFIX + Constants.NAMESPACES_FEATURE, true);
      grammarPreparser.setFeature(Constants.XERCES_FEATURE_PREFIX + Constants.NAMESPACE_PREFIXES_FEATURE, true);
	  grammarPreparser.setFeature(Constants.XERCES_FEATURE_PREFIX + Constants.VALIDATION_FEATURE, true);
	  grammarPreparser.setFeature(Constants.XERCES_FEATURE_PREFIX + Constants.SCHEMA_VALIDATION_FEATURE, true);

	  grammarPreparser.setFeature(Constants.XERCES_FEATURE_PREFIX + Constants.EXTERNAL_GENERAL_ENTITIES_FEATURE, true);
	  grammarPreparser.setFeature(Constants.XERCES_FEATURE_PREFIX + Constants.EXTERNAL_PARAMETER_ENTITIES_FEATURE, true);
	  grammarPreparser.setFeature(Constants.XERCES_FEATURE_PREFIX + Constants.WARN_ON_DUPLICATE_ATTDEF_FEATURE, true);
	     
	  if(configuration.getFeature(XSDValidationConfiguration.HONOUR_ALL_SCHEMA_LOCATIONS))
	  {
	    try
	    {
	      grammarPreparser.setFeature(Constants.XERCES_FEATURE_PREFIX + "honour-all-schemaLocations", true); //$NON-NLS-1$
	    }
        catch (Exception e)
	    {
	      // catch the exception and ignore
        	Logger.logException("working on "+ uri, e);
	    }
	  }
	  
	  if(configuration.getFeature(XSDValidationConfiguration.FULL_SCHEMA_CONFORMANCE)) {
		try
		{
		  grammarPreparser.setFeature(Constants.XERCES_FEATURE_PREFIX + Constants.SCHEMA_FULL_CHECKING, true);
		}
		catch (Exception e)
		{
			// ignore since we don't want to set it or can't.
			Logger.logException("working on "+ uri, e);
		}
		
	  }
	      
	  grammarPreparser.setErrorHandler(errorHandler);
	  if (uriresolver != null)
	  {
	    XSDEntityResolver resolver = new XSDEntityResolver(uriresolver, uri);
	    if (resolver != null)
	    {
	      grammarPreparser.setEntityResolver(resolver);
	    }
	  }

	  try
	  {
	  	XMLInputSource is = new XMLInputSource(null, uri, uri, inputStream, null);
	    grammarPreparser.getLoader(XMLGrammarDescription.XML_SCHEMA);
		grammarPreparser.preparseGrammar(XMLGrammarDescription.XML_SCHEMA,is);
	  }
	  catch (Exception e)
	  {
	    //parser will return null pointer exception if the document is structurally invalid
		//TODO: log error message
		//System.out.println(e);
		    Logger.logException("working on "+ uri, e);
      }
	}
	catch (Exception e)
	{
      // TODO: log error.
	  //System.out.println(e);
		  Logger.logException("working on "+ uri, e);
	}
	return valinfo;
  }

  /**
   * Set the URI resolver to use with XSD validation.
   * 
   * @param uriresolver
   *          The URI resolver to use.
   */
  public void setURIResolver(URIResolver uriresolver)
  {
    this.uriresolver = uriresolver;
  }

  /**
   * The XSDErrorHandler handle Xerces parsing errors and puts the errors
   * into the given ValidationInfo object.
   */
  protected class XSDErrorHandler implements XMLErrorHandler
  {
	  
    private final ValidationInfo valinfo;

    public XSDErrorHandler(ValidationInfo valinfo)
    {
      this.valinfo = valinfo;
    }
    
    /**
     * Add a validation message with the given severity.
     * 
     * @param errorKey The Xerces error key.
     * @param exception The exception that contains the information about the message.
     * @param severity The severity of the validation message.
     */
    protected void addValidationMessage(String errorKey, XMLParseException exception, int severity)
    { 
      String systemId = exception.getExpandedSystemId();
      if (systemId != null)
      {
        if (severity == DOMError.SEVERITY_WARNING)
        {
          valinfo.addWarning(exception.getLocalizedMessage(), exception.getLineNumber(), exception.getColumnNumber(), systemId);
        }
        else
        {
          valinfo.addError(exception.getLocalizedMessage(), exception.getLineNumber(), exception.getColumnNumber(), systemId, errorKey, null);
        }
      }
    }

    /* (non-Javadoc)
     * @see org.apache.xerces.xni.parser.XMLErrorHandler#warning(java.lang.String, java.lang.String, org.apache.xerces.xni.parser.XMLParseException)
     */
    public void warning(String domain, String key, XMLParseException exception) throws XNIException
	{
    	addValidationMessage(key, exception, DOMError.SEVERITY_WARNING);
	}

    /* (non-Javadoc)
     * @see org.apache.xerces.xni.parser.XMLErrorHandler#error(java.lang.String, java.lang.String, org.apache.xerces.xni.parser.XMLParseException)
     */
    public void error(String domain, String key, XMLParseException exception) throws XNIException
    {
    	addValidationMessage(key, exception, DOMError.SEVERITY_ERROR);
	}

    /* (non-Javadoc)
     * @see org.apache.xerces.xni.parser.XMLErrorHandler#fatalError(java.lang.String, java.lang.String, org.apache.xerces.xni.parser.XMLParseException)
     */
    public void fatalError(String domain, String key, XMLParseException exception) throws XNIException
	{
    	addValidationMessage(key, exception, DOMError.SEVERITY_FATAL_ERROR);
	}
  }

  /**
   * The XSDEntityResolver wraps an idresolver to provide entity resolution to
   * the XSD validator.
   */
  protected class XSDEntityResolver implements XMLEntityResolver
  {
    private URIResolver uriresolver = null;

    /**
     * Constructor.
     * 
     * @param idresolver
     *          The idresolver this entity resolver wraps.
     * @param baselocation The base location to resolve with.
     */
    public XSDEntityResolver(URIResolver uriresolver, String baselocation)
    {
      this.uriresolver = uriresolver;
    }
    
    /* (non-Javadoc)
     * @see org.apache.xerces.xni.parser.XMLEntityResolver#resolveEntity(org.apache.xerces.xni.XMLResourceIdentifier)
     */
    public XMLInputSource resolveEntity(XMLResourceIdentifier resourceIdentifier) throws XNIException, IOException
    {
      String literalSystemId = resourceIdentifier.getLiteralSystemId();
      if(literalSystemId != null)
      {
    	resourceIdentifier.setLiteralSystemId(literalSystemId.replace('\\','/'));
      }
        // TODO cs: In revision 1.1 we explicitly opened a stream to ensure
        // file I/O problems produced messages. I've remove this fudge for now
        // since I can't seem to reproduce the problem it was intended to fix.
        // I'm hoping the newer Xerces code base has fixed this problem and the fudge is defunct.
        return XMLValidator._internalResolveEntity(uriresolver, resourceIdentifier);
      
    }
  }   
}
