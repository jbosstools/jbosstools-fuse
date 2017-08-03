/*******************************************************************************
 * Copyright (c) 2001, 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     David Carver - STAR - [205989] - [validation] validate XML after XInclude resolution
 *******************************************************************************/

package org.eclipse.wst.xml.core.internal.validation;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.xerces.impl.XMLErrorReporter;
import org.apache.xerces.impl.msg.XMLMessageFormatter;
import org.apache.xerces.parsers.XIncludeAwareParserConfiguration;
import org.apache.xerces.parsers.XML11Configuration;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.URIUtil;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolver;
import org.eclipse.wst.common.uriresolver.internal.util.URIHelper;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.internal.ValOperation;
import org.eclipse.wst.validation.internal.operations.LocalizedMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.xml.core.internal.Logger;
import org.eclipse.wst.xml.core.internal.XMLCorePlugin;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.IExternalSchemaLocationProvider;
import org.eclipse.wst.xml.core.internal.contentmodel.modelqueryimpl.ExternalSchemaLocationProviderRegistry;
import org.eclipse.wst.xml.core.internal.preferences.XMLCorePreferenceNames;
import org.eclipse.wst.xml.core.internal.validation.core.LazyURLInputStream;
import org.eclipse.wst.xml.core.internal.validation.core.NestedValidatorContext;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This class performs validation using a Xerces SAX parser.
 * Here's a quick overview of the details :
 *   - an ErrorHandler is used to collect errors into a list (so they may be displayed by the UI)
 *   - an EntityResolver is used along with the Xerces "external-schemaLocation" property to implement XML Catalog support
 *   - drops support for external general and parameter entities is disabled in the Xerces entity manager - http://bugs.eclipse.org/508083
 */
public class XMLValidator
{
  protected URIResolver uriResolver = null;
  protected Hashtable ingoredErrorKeyTable = new Hashtable();
  protected Set adjustLocationErrorKeySet = new TreeSet();

  protected static final String IGNORE_ALWAYS = "IGNORE_ALWAYS"; //$NON-NLS-1$
  protected static final String IGNORE_IF_DTD_WITHOUT_ELEMENT_DECL = "IGNORE_IF_DTD_WITHOUT_ELEMENT_DECL"; //$NON-NLS-1$
  protected static final String PREMATURE_EOF = "PrematureEOF"; //$NON-NLS-1$
  protected static final String ROOT_ELEMENT_TYPE_MUST_MATCH_DOCTYPEDECL = "RootElementTypeMustMatchDoctypedecl"; //$NON-NLS-1$
  protected static final String MSG_ELEMENT_NOT_DECLARED = "MSG_ELEMENT_NOT_DECLARED"; //$NON-NLS-1$

  // WTP XML validator specific key.
  protected static final String NO_GRAMMAR_FOUND = "NO_GRAMMAR_FOUND"; //$NON-NLS-1$
  protected static final String NO_DOCUMENT_ELEMENT_FOUND = "NO_DOCUMENT_ELEMENT_FOUND"; //$NON-NLS-1$

  private static final String FILE_NOT_FOUND_KEY = "FILE_NOT_FOUND"; //$NON-NLS-1$

  private StreamingMarkupValidator val = new StreamingMarkupValidator();
  
  private final String ANNOTATIONMSG = AnnotationMsg.class.getName();

  private final static boolean _trace = Boolean.valueOf(Platform.getDebugOption("org.eclipse.wst.xml.core/externalSchemaLocation")).booleanValue(); //$NON-NLS-1$
  /**
   * Constructor.
   */
  public XMLValidator()
  {
    // Here we add some error keys that we need to filter out when we're validating
    // against a DTD without any element declarations.
    ingoredErrorKeyTable.put(PREMATURE_EOF, IGNORE_ALWAYS);
    ingoredErrorKeyTable.put(ROOT_ELEMENT_TYPE_MUST_MATCH_DOCTYPEDECL, IGNORE_IF_DTD_WITHOUT_ELEMENT_DECL);
    ingoredErrorKeyTable.put(MSG_ELEMENT_NOT_DECLARED, IGNORE_IF_DTD_WITHOUT_ELEMENT_DECL);
    // Here we add some error keys that we need to adjust the location information for.
    // The location information will be adjusted to place the message on the line of the starting
    // element instead of on the line of the closing element.
    adjustLocationErrorKeySet.add("MSG_CONTENT_INVALID"); //$NON-NLS-1$
    adjustLocationErrorKeySet.add("MSG_CONTENT_INCOMPLETE"); //$NON-NLS-1$
    adjustLocationErrorKeySet.add("cvc-complex-type.2.4.b"); //$NON-NLS-1$
    adjustLocationErrorKeySet.add("cvc-complex-type.2.3"); //$NON-NLS-1$
  }

  /**
   * Set the URI Resolver to use.
   * 
   * @param uriResolver The URI Resolver to use.
   */
  public void setURIResolver(URIResolver uriResolver)
  {
    this.uriResolver = uriResolver;
    //entityResolver = new MyEntityResolver(uriResolver);
  }

 
  /**
   * Create an XML Reader.
   * 
   * @return The newly created XML reader or null if unsuccessful.
   * @throws Exception
   */
  protected XMLReader createXMLReader(final XMLValidationInfo valinfo, XMLEntityResolver entityResolver) throws Exception
  {     
    XMLReader reader = null;
    // move to Xerces-2... add the contextClassLoader stuff
    ClassLoader prevClassLoader = Thread.currentThread().getContextClassLoader();
    try
    {
      Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
      MyStandardParserConfiguration configuration = new MyStandardParserConfiguration(valinfo);
      reader = new org.apache.xerces.parsers.SAXParser(configuration)
      {
    	private XMLLocator locator = null;
    	
        /* (non-Javadoc)
         * @see org.apache.xerces.parsers.AbstractSAXParser#startDocument(org.apache.xerces.xni.XMLLocator, java.lang.String, org.apache.xerces.xni.NamespaceContext, org.apache.xerces.xni.Augmentations)
         */
        public void startDocument(org.apache.xerces.xni.XMLLocator theLocator, java.lang.String encoding, NamespaceContext nscontext, org.apache.xerces.xni.Augmentations augs)
        {
          locator = theLocator;
          valinfo.setXMLLocator(theLocator);
          super.startDocument(theLocator, encoding, nscontext, augs); 
        }

        /* (non-Javadoc)
         * @see org.apache.xerces.parsers.AbstractSAXParser#startElement(org.apache.xerces.xni.QName, org.apache.xerces.xni.XMLAttributes, org.apache.xerces.xni.Augmentations)
         */
        public void startElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException
        {
          valinfo.getStartElementLocations().push(new LocationCoordinate(locator.getLineNumber(), locator.getColumnNumber()));
		  super.startElement(element, attributes, augs);
		}
        
		/* (non-Javadoc)
		 * @see org.apache.xerces.parsers.AbstractSAXParser#endElement(org.apache.xerces.xni.QName, org.apache.xerces.xni.Augmentations)
		 */
		public void endElement(QName element, Augmentations augs) throws XNIException {
			super.endElement(element, augs);
			valinfo.getStartElementLocations().pop();
		}
      };

      reader.setFeature("http://apache.org/xml/features/continue-after-fatal-error", false); //$NON-NLS-1$
      reader.setFeature("http://xml.org/sax/features/namespace-prefixes", valinfo.isNamespaceEncountered()); //$NON-NLS-1$
      reader.setFeature("http://xml.org/sax/features/namespaces", valinfo.isNamespaceEncountered());               //$NON-NLS-1$
     
      reader.setContentHandler(new DefaultHandler()
      {
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
          valinfo.getErrorCustomizationManager().startElement(uri, localName);                    
        }
        
        public void endElement(String uri, String localName, String qName) throws SAXException {
          valinfo.getErrorCustomizationManager().endElement(uri, localName);
        }
      });      
      
      // MH make sure validation works even when a customer entityResolver is note set (i.e. via setURIResolver())
      if (entityResolver != null)
      {  
        reader.setProperty("http://apache.org/xml/properties/internal/entity-resolver", entityResolver); //$NON-NLS-1$
      }  
      reader.setProperty("http://xml.org/sax/properties/declaration-handler", new MyDeclHandler());      //$NON-NLS-1$
    } 
    catch(Exception e)
    { 
      Logger.logException(e);
      //e.printStackTrace();
    }
    finally
    {
      Thread.currentThread().setContextClassLoader(prevClassLoader);
    }
    return reader;
  }  

  /**
   * Validate the file located at the given URI.
   * 
   * @param uri The URI of the file to validate.
   * @return Returns an XML validation report.
   */
  public XMLValidationReport validate(String uri)
  {
    return validate(uri, null, new XMLValidationConfiguration());  
  }

  final String createStringForInputStream(InputStream inputStream)
  {
    // Here we are reading the file and storing to a stringbuffer.
    StringBuffer fileString = new StringBuffer();
    try
    {
      InputStreamReader inputReader = new InputStreamReader(inputStream, "UTF-8"); //$NON-NLS-1$
      BufferedReader reader = new BufferedReader(inputReader);
      char[] chars = new char[1024];
      int numberRead = reader.read(chars);
      while (numberRead != -1)
      {
        fileString.append(chars, 0, numberRead);
        numberRead = reader.read(chars);
      }
    }
    catch (Exception e)
    {
      if(ValidatorHelper._trace)
        Logger.logException(e);
    }
    return fileString.toString();
  }
  /**
   * Validate the inputStream
   * 
   * @param uri The URI of the file to validate.
   * @param the inputStream of the file to validate
   * @return Returns an XML validation report.
   */
  public XMLValidationReport validate(String uri, InputStream inputStream)
  {
	return validate(uri, inputStream, new XMLValidationConfiguration());
  }
  /**
   * Validate the inputStream
   * 
   * @param uri 
   * 		The URI of the file to validate.
   * @param inputstream
   * 		The inputStream of the file to validate
   * @param configuration
   * 		A configuration for this validation session.
   * @return 
   * 		Returns an XML validation report.
   */
  public XMLValidationReport validate(String uri, InputStream inputStream, XMLValidationConfiguration configuration)
  {
    return validate(uri, inputStream, configuration, null);  
  }
 
  /**
   * Validate the inputStream
   * 
   * @param uri 
   *    The URI of the file to validate.
   * @param inputstream
   *    The inputStream of the file to validate
   * @param configuration
   *    A configuration for this validation session.
   * @param result
   *    The validation result
   * @return 
   *    Returns an XML validation report.
   */
  public XMLValidationReport validate(String uri, InputStream inputStream, XMLValidationConfiguration configuration, ValidationResult result)
  {
	  return validate(uri, inputStream, configuration, null, null);
  }
  
  /**
   * Validate the inputStream
   * 
   * @param uri 
   *    The URI of the file to validate.
   * @param inputstream
   *    The inputStream of the file to validate
   * @param configuration
   *    A configuration for this validation session.
   * @param result
   *    The validation result
   * @param context
   *    The validation context   
   * @return 
   *    Returns an XML validation report.
   */
  public XMLValidationReport validate(String uri, InputStream inputStream, XMLValidationConfiguration configuration, ValidationResult result, NestedValidatorContext context)
  {
    String grammarFile = ""; //$NON-NLS-1$
    Reader reader1 = null; // Used for the preparse.
    Reader reader2 = null; // Used for validation parse.
    
    if (inputStream != null)
    {  
      String string = createStringForInputStream(inputStream);
      reader1 = new StringReader(string);
      reader2 = new StringReader(string);
    } 
        
    XMLValidationInfo valinfo = new XMLValidationInfo(uri);
    MyEntityResolver entityResolver = new MyEntityResolver(uriResolver, context); 
    ValidatorHelper helper = new ValidatorHelper(); 
    try
    {  
        helper.computeValidationInformation(uri, reader1, uriResolver);
        valinfo.setDTDEncountered(helper.isDTDEncountered);
        valinfo.setElementDeclarationCount(helper.numDTDElements);
        valinfo.setNamespaceEncountered(helper.isNamespaceEncountered);
        valinfo.setGrammarEncountered(helper.isGrammarEncountered);
        XMLReader reader = createXMLReader(valinfo, entityResolver);
        // Set the configuration option
        if (configuration.getFeature(XMLValidationConfiguration.HONOUR_ALL_SCHEMA_LOCATIONS))
        {
            reader.setFeature("http://apache.org/xml/features/honour-all-schemaLocations", true); //$NON-NLS-1$
        }
        if (configuration.getFeature(XMLValidationConfiguration.USE_XINCLUDE))
        {
          reader.setFeature("http://apache.org/xml/features/xinclude", true); //$NON-NLS-1$      
        }

        // Support external schema locations
        boolean isGrammarEncountered = helper.isGrammarEncountered;
        if (!isGrammarEncountered) {
        	isGrammarEncountered = checkExternalSchemas(reader, valinfo.getFileURI());
        }
        reader.setFeature("http://xml.org/sax/features/validation", isGrammarEncountered);  //$NON-NLS-1$
        reader.setFeature("http://apache.org/xml/features/validation/schema", isGrammarEncountered); //$NON-NLS-1$

        XMLErrorHandler errorhandler = new XMLErrorHandler(valinfo);
        reader.setErrorHandler(errorhandler);
        
        InputSource inputSource = new InputSource(uri);
        inputSource.setCharacterStream(reader2);
        
        ClassLoader originalClzLoader = Thread.currentThread().getContextClassLoader();
    	Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
    	
    	try{
    		reader.parse(inputSource);
    	}finally{
    		Thread.currentThread().setContextClassLoader(originalClzLoader);
    	}
           
        if(configuration.getIntFeature(XMLValidationConfiguration.INDICATE_NO_GRAMMAR) > 0 && 
        		valinfo.isValid() && !isGrammarEncountered)
        {
          if(configuration.getIntFeature(XMLValidationConfiguration.INDICATE_NO_GRAMMAR) == 1)
            valinfo.addWarning(XMLValidationMessages._WARN_NO_GRAMMAR, 1, 0, uri, NO_GRAMMAR_FOUND, null);
          else // 2
              valinfo.addError(XMLValidationMessages._WARN_NO_GRAMMAR, 1, 0, uri, NO_GRAMMAR_FOUND, null);
        }
        if(configuration.getIntFeature(XMLValidationConfiguration.INDICATE_NO_DOCUMENT_ELEMENT) > 0 && valinfo.isValid() && !helper.isDocumentElementEncountered) {
        	if(configuration.getIntFeature(XMLValidationConfiguration.INDICATE_NO_DOCUMENT_ELEMENT) == 1)
                valinfo.addWarning(XMLValidationMessages._NO_DOCUMENT_ELEMENT, 1, 0, uri, NO_DOCUMENT_ELEMENT_FOUND, null);
              else // 2
                  valinfo.addError(XMLValidationMessages._NO_DOCUMENT_ELEMENT, 1, 0, uri, NO_DOCUMENT_ELEMENT_FOUND, null);
        }
        if (helper.isDTDEncountered)
          grammarFile = entityResolver.getLocation();
        else
          grammarFile = helper.schemaLocationString;
    }
    catch (SAXParseException saxParseException)
    {
      // These errors are caught by the error handler.
      //addValidationMessage(valinfo, saxParseException);
      if(ValidatorHelper._trace)
          Logger.logException(saxParseException);
    }                 
    catch (IOException ioException)
    {
      addValidationMessage(valinfo, ioException);
      if(ValidatorHelper._trace)
          Logger.logException(ioException);
    }                 
    catch (Exception exception)
    {  
    	Logger.logException(exception.getLocalizedMessage(), exception);
    }

    // Now set up the dependencies
    // Wrap with try catch so that if something wrong happens, validation can
    // still proceed as before
    if (result != null)
    {
      try
      {
        IResource resource = getWorkspaceFileFromLocation(grammarFile);
        ArrayList resources = new ArrayList();
        if (resource != null)
          resources.add(resource);
        result.setDependsOn((IResource [])resources.toArray(new IResource [0]));
      }
      catch (Exception e)
      {
        Logger.logException(e.getLocalizedMessage(), e);
      }
    }

    if ( XMLCorePlugin.getDefault().getPluginPreferences().getBoolean(XMLCorePreferenceNames.MARKUP_VALIDATION)){
	    IReporter reporter = executeMarkupValidator(uri);
	    if (reporter != null){
		    List msgList = reporter.getMessages();
		    for (int i = 0;i < msgList.size();i++){
		    	LocalizedMessage msg = (LocalizedMessage)msgList.get(i);
		    	if (msg.getSeverity() == 2)
		    		valinfo.addError(msg.getLocalizedMessage(), msg.getLineNumber(), msg.getOffset(),valinfo.getFileURI(),"null",getMsgArguments(msg) ); //$NON-NLS-1$
		    	else if (msg.getSeverity() == 1)
		    		valinfo.addWarning(msg.getLocalizedMessage(), msg.getLineNumber(), msg.getOffset(),valinfo.getFileURI(),"null", getMsgArguments(msg)); //$NON-NLS-1$
		    }
	    }
    }
    
    return valinfo;
       
  }

	private boolean checkExternalSchemas(XMLReader reader, String fileURI) throws Exception {
		boolean isGrammarEncountered = false;
		final StringBuffer schemaLocation = new StringBuffer();
		String noNamespaceSchemaLocation = null;
		// Check the schema provider extension point
		IExternalSchemaLocationProvider[] providers = ExternalSchemaLocationProviderRegistry.getInstance().getProviders();
		for (int i = 0; i < providers.length; i++) {
			URI uri = null;
			try {
				uri = URIUtil.fromString(fileURI);
			}
			catch (URISyntaxException e) {
				Logger.logException(e.getLocalizedMessage(), e);
			}
			if (uri != null) {
				long time = _trace ? System.currentTimeMillis(): 0;
				final Map locations = providers[i].getExternalSchemaLocation(uri);
				if (_trace) {
					  long diff = System.currentTimeMillis() - time;
					  if (diff > 250)
						  Logger.log(Logger.INFO, "Schema location provider took [" + diff + "ms] for URI [" + fileURI + "]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				  }
				if (locations != null && !locations.isEmpty()) {
					Object path = locations.get(IExternalSchemaLocationProvider.SCHEMA_LOCATION);
					if (path instanceof String) {
						if (schemaLocation.length() > 0) {
							schemaLocation.append(' ');
						}
						schemaLocation.append(path);
					}
					path = locations.get(IExternalSchemaLocationProvider.NO_NAMESPACE_SCHEMA_LOCATION);
					if (path instanceof String) {
						noNamespaceSchemaLocation = (String)path;
					}
				} else {
				    if(_trace)
				      Logger.log(Logger.INFO, "No location found for "+ providers[i] + " with uri "+uri);
				}
			}
		}

		if (schemaLocation.length() > 0) {
			reader.setProperty(IExternalSchemaLocationProvider.SCHEMA_LOCATION, schemaLocation.toString());
			isGrammarEncountered = true;
		}
		if (noNamespaceSchemaLocation != null) {
			reader.setProperty(IExternalSchemaLocationProvider.NO_NAMESPACE_SCHEMA_LOCATION, noNamespaceSchemaLocation);
			isGrammarEncountered = true;
		}
		return isGrammarEncountered;
	}
 
  private Object[] getMsgArguments(LocalizedMessage msg){
	  Object obj = msg.getAttribute(ANNOTATIONMSG);
	  return new Object[]{obj};
  }
 

	private IReporter executeMarkupValidator(String uri){
		Path path = new Path(uri);
		String fileProtocol = "file://"; //$NON-NLS-1$
		int index = uri.indexOf(fileProtocol);
		
		IFile resource = null;
		if (index == 0){
			String transformedUri = uri.substring(fileProtocol.length());
			Path transformedPath = new Path(transformedUri);
			resource = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(transformedPath);
		}
		else {
			resource = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
			
		}
		IReporter reporter = null;
		if (resource != null){
			reporter = val.validate(resource, 0, new ValOperation().getState());
		}
		return reporter;
	}
  
  /**
   * Add a validation message to the specified list.
   * 
   * @param valinfo The validation info object to add the error to.
   * @param exception The exception that contains the validation information.
   */
  protected void addValidationMessage(XMLValidationInfo valinfo, IOException exception)
  { 
    String validationMessageStr = exception.getMessage();
	Throwable cause = exception.getCause() != null ? exception.getCause() : exception;
	while(validationMessageStr == null && cause != null){
		String localizedMessage = cause.getLocalizedMessage();
	    cause = cause.getCause();
	    if(cause == null && localizedMessage != null )
	    {
	      validationMessageStr = localizedMessage;
	    }
	}
	
	if (validationMessageStr != null)
    {
      if (cause instanceof FileNotFoundException)
      {
        validationMessageStr = NLS.bind(XMLValidationMessages._UI_PROBLEMS_VALIDATING_FILE_NOT_FOUND, new Object [] { validationMessageStr });
      }
      else if (cause instanceof UnknownHostException)
      {
    	validationMessageStr = NLS.bind(XMLValidationMessages._UI_PROBLEMS_VALIDATING_UNKNOWN_HOST, new Object [] { validationMessageStr });
      }
      else if(cause instanceof ConnectException)
      {
    	validationMessageStr = XMLValidationMessages._UI_PROBLEMS_CONNECTION_REFUSED;
      }
    }

    if (validationMessageStr != null)
    {
      XMLLocator locator = valinfo.getXMLLocator();
      valinfo.addWarning(validationMessageStr, locator != null ? locator.getLineNumber() : 1, locator != null ? locator.getColumnNumber() : 0, valinfo.getFileURI(), FILE_NOT_FOUND_KEY, null);
    }
  }
                                                                    
  /**
   * Add a validation message to the specified list.
   * 
   * @param valinfo The validation info object to add the error to.
   * @param exception The exception that contains the validation information.
   */
  protected void addValidationMessage(XMLValidationInfo valinfo, SAXParseException exception)
  { 
    if (exception.getMessage() != null)
    { 
      valinfo.addError(exception.getLocalizedMessage(), exception.getLineNumber(), exception.getColumnNumber(), exception.getSystemId());
    }
  }

  
  /**
   * A custom entity resolver that uses the URI resolver specified to resolve entities.
   */
  protected class MyEntityResolver implements XMLEntityResolver 
  {
    private URIResolver uriResolver;
    private String resolvedDTDLocation;
    private NestedValidatorContext context;
   
    /**
     * Constructor.
     * 
     * @param uriResolver The URI resolver to use with this entity resolver.
     * @param context The XML validator context.
     */
    public MyEntityResolver(URIResolver uriResolver, NestedValidatorContext context)
    {
      this.uriResolver = uriResolver;
      this.context = context;
    }
    
    /* (non-Javadoc)
     * @see org.apache.xerces.xni.parser.XMLEntityResolver#resolveEntity(org.apache.xerces.xni.XMLResourceIdentifier)
     */
    public XMLInputSource resolveEntity(XMLResourceIdentifier rid) throws XNIException, IOException
    {
        XMLInputSource inputSource = _internalResolveEntity(uriResolver, rid, context);
        if (inputSource != null)
        {
          resolvedDTDLocation = inputSource.getSystemId();
        }
        return inputSource;
    }
   
    public String getLocation()
    {
      return resolvedDTDLocation;
    }
  }
  
  // cs : I've refactored the common SAX based resolution code into this method for use by other validators 
  // (i.e. XML Schema, WSDL etc).   The other approach is maintain a copy for each validator that has
  // identical code.  In any case we should strive to ensure that the validators perform resolution consistently. 
  public static XMLInputSource _internalResolveEntity(URIResolver uriResolver, XMLResourceIdentifier rid) throws  IOException
  {
    return _internalResolveEntity(uriResolver, rid, null);
  }
  
  public static XMLInputSource _internalResolveEntity(URIResolver uriResolver, XMLResourceIdentifier rid, NestedValidatorContext context) throws  IOException
  {
    XMLInputSource is = null;
    
    if (uriResolver != null)
    {         
      String id = rid.getPublicId();
      if(id == null)
      {
        id = rid.getNamespace();
      }
      
      String location = null;
      if (id != null || rid.getLiteralSystemId() != null)
      {  
        location = uriResolver.resolve(rid.getBaseSystemId(), id, rid.getLiteralSystemId());
      }  
      
      if (location != null)
      {                     
        String physical = uriResolver.resolvePhysicalLocation(rid.getBaseSystemId(), id, location);

        // if physical is already a known bad uri, just go ahead and throw an exception
        if (context instanceof XMLNestedValidatorContext)
        {
          XMLNestedValidatorContext xmlContext = ((XMLNestedValidatorContext)context);

          if (xmlContext.isURIMarkedInaccessible(physical))
          {
        	 throw new FileNotFoundException(physical);
          }
        }
        
        is = new XMLInputSource(rid.getPublicId(), location, location);
        
        // This block checks that the file exists. If it doesn't we need to throw
        // an exception so Xerces will report an error. note: This may not be
        // necessary with all versions of Xerces but has specifically been 
        // experienced with the version included in IBM's 1.4.2 JDK.
        InputStream isTemp = null;
        try
        {
          isTemp = new URL(physical).openStream();
        }
        catch (IOException e)
        {
          // physical was a bad url, so cache it so we know next time
          if (context instanceof XMLNestedValidatorContext)
          {
            XMLNestedValidatorContext xmlContext = ((XMLNestedValidatorContext)context);
            xmlContext.markURIInaccessible(physical);
          }
          throw e;
        }
        finally
        {
          if(isTemp != null)
          {
            isTemp.close();
          }
        }
        is.setByteStream(new LazyURLInputStream(physical));      
      }
    }
    return is;    
  }      
  
  /**
   * An error handler to catch errors encountered while parsing the XML document.
   */
  protected class XMLErrorHandler implements org.xml.sax.ErrorHandler
  {

    private final int ERROR = 0;
    private final int WARNING = 1;
    private XMLValidationInfo valinfo;
    
    /**
     * Constructor.
     * 
     * @param valinfo The XML validation info object that will hold the validation messages.
     */
    public XMLErrorHandler(XMLValidationInfo valinfo)
    {
      this.valinfo = valinfo;
    }

    /**
     * Add a validation message with the given severity.
     * 
     * @param exception The exception that contains the message.
     * @param severity The severity of the message.
     */
    
    protected void addValidationMessage(SAXParseException exception, int severity)
    {
      if(exception.getSystemId() != null)
      {       	
    	int lineNumber = exception.getLineNumber();
    	int columnNumber = exception.getColumnNumber();
    	
    	// For the following three errors the line number will be modified to use that of the start
    	// tag instead of the end tag. Unless its a self ending tag and in that case the startElementLocations will be empty
    	String currentErrorKey = valinfo.currentErrorKey;
    	if (currentErrorKey != null && adjustLocationErrorKeySet.contains(currentErrorKey) && valinfo.getStartElementLocations().size() > 0)  
    	{
    	  LocationCoordinate adjustedCoordinates = (LocationCoordinate)valinfo.getStartElementLocations().peek();
    	  lineNumber = adjustedCoordinates.getLineNumber();
    	  columnNumber = adjustedCoordinates.getColumnNumner();
    	}
    	
        if(severity == WARNING)
        {
          valinfo.addWarning(exception.getLocalizedMessage(), lineNumber, columnNumber, exception.getSystemId());
        }
        else
        {
          valinfo.addError(exception.getLocalizedMessage(), lineNumber, columnNumber, exception.getSystemId(), valinfo.getCurrentErrorKey(), valinfo.getMessageArguments());
        }
      }
    }

    /* (non-Javadoc)
     * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
     */
    public void error(SAXParseException exception) throws SAXException
    {
      addValidationMessage(exception, ERROR);
    }

    /* (non-Javadoc)
     * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
     */
    public void fatalError(SAXParseException exception) throws SAXException
    {
      addValidationMessage(exception, ERROR);
    }

    /* (non-Javadoc)
     * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
     */
    public void warning(SAXParseException exception) throws SAXException
    {
      addValidationMessage(exception, WARNING);
    }
  }
                                                                          
  /** 
   * This class is used to count the elementDecls that are encountered in a DTD.
   */
  protected class MyDeclHandler implements DeclHandler 
  {
    
    /**
     * Constructor.
     * 
     * @param valinfo The XMLValidationInfo object that will count the declarations.
     */
    public MyDeclHandler()
    {
    }
    
    /* (non-Javadoc)
     * @see org.xml.sax.ext.DeclHandler#attributeDecl(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public void attributeDecl(String eName, String aName, String type, String valueDefault, String value) 
    {
    }                                    

    /* (non-Javadoc)
     * @see org.xml.sax.ext.DeclHandler#elementDecl(java.lang.String, java.lang.String)
     */
    public void elementDecl(String name, String model) 
    {
      //valinfo.increaseElementDeclarationCount();
    }
  
    /* (non-Javadoc)
     * @see org.xml.sax.ext.DeclHandler#externalEntityDecl(java.lang.String, java.lang.String, java.lang.String)
     */
    public void externalEntityDecl(String name, String publicId, String systemId) 
    {
    }      

    /* (non-Javadoc)
     * @see org.xml.sax.ext.DeclHandler#internalEntityDecl(java.lang.String, java.lang.String)
     */
    public void internalEntityDecl(String name, String value) 
    {
    }
  }

  /**
   * A XIncludeAwareParserConfiguration that creates an error reporter which can ignore
   * DTD error messages for DTD's with no elements defined.
   */

  protected class MyStandardParserConfiguration extends XIncludeAwareParserConfiguration
  {
  	XMLValidationInfo valinfo = null;
    List reportedExceptions = new ArrayList(); 
  	
  	/**
  	 * Constructor.
  	 * 
  	 * @param valinfo The XMLValidationInfo object to use.
  	 */
  	public MyStandardParserConfiguration(XMLValidationInfo valinfo)
  	{
      this.valinfo = valinfo;

      String xmlCoreId = XMLCorePlugin.getDefault().getBundle().getSymbolicName();
      boolean resolveExternalEntities = InstanceScope.INSTANCE.getNode(xmlCoreId).getBoolean(XMLCorePreferenceNames.RESOLVE_EXTERNAL_ENTITIES, false);
      setFeature(XML11Configuration.EXTERNAL_GENERAL_ENTITIES, resolveExternalEntities );
      setFeature(XML11Configuration.EXTERNAL_PARAMETER_ENTITIES, resolveExternalEntities);
      resetCommon();

  	  XMLErrorReporter errorReporter = createErrorReporter();
      if (errorReporter.getMessageFormatter(XMLMessageFormatter.XML_DOMAIN) == null) {
          XMLMessageFormatter xmft = new XMLMessageFormatter();
          errorReporter.putMessageFormatter(XMLMessageFormatter.XML_DOMAIN, xmft);
          errorReporter.putMessageFormatter(XMLMessageFormatter.XMLNS_DOMAIN, xmft);
      }  	  
      fErrorReporter = errorReporter;
	  setProperty(ERROR_REPORTER, errorReporter);
	  fCommonComponents.remove(fErrorReporter);
	  fCommonComponents.add(fErrorReporter);
  	}

    /* (non-Javadoc)
     * @see org.apache.xerces.parsers.DTDConfiguration#createErrorReporter()
     */
    protected XMLErrorReporter createErrorReporter() 
    {
    	return new XMLErrorReporter()
		{
            /* (non-Javadoc)
             * @see org.apache.xerces.impl.XMLErrorReporter#reportError(java.lang.String, java.lang.String, java.lang.Object[], short)
             */
            public void reportError(String domain, String key, Object[] arguments, short severity) throws XNIException
            {
		      boolean reportError = true;
              valinfo.setCurrentErrorKey(key);  
			  valinfo.setMessageArguments(arguments);
		      String ignoreCondition = (String)ingoredErrorKeyTable.get(key);
		      if (ignoreCondition != null)
		      {
		        if (ignoreCondition.equals(XMLValidator.IGNORE_IF_DTD_WITHOUT_ELEMENT_DECL))
		        {                    
		          boolean isDTDWithoutElementDeclarationEncountered = valinfo.isDTDWithoutElementDeclarationEncountered(); 
		          reportError = !isDTDWithoutElementDeclarationEncountered;  
		        }
		        else 
		        {
		          reportError = false;
		        }
		      }
		      if ("schema_reference.4".equals(key) && arguments.length > 0) //$NON-NLS-1$
              {
                Object location = arguments[0];  
                if (location != null)
                {  
                  if(reportedExceptions.contains(location))
                  {
                    reportError = false;
                  }
                  else
                  {
                    reportedExceptions.add(location);
                  }
                }
              }          
		      if (reportError)
		      {
		        super.reportError(domain, key, arguments, severity);
		        valinfo.getErrorCustomizationManager().considerReportedError(valinfo, key, arguments);
		      }
		    }
		};
    }
  }
  
  /** 
   * A line and column number coordinate.
   */
  protected class LocationCoordinate
  {	
	private int lineNo = -1;
    private int columnNo = -1;
    
    public LocationCoordinate(int lineNumber, int columnNumber)
    {
      this.lineNo = lineNumber;
      this.columnNo = columnNumber;
    }
    	
    public int getLineNumber()
    { 
      return this.lineNo;
    }
    	
    public int getColumnNumner()
    { 
      return this.columnNo;
    } 
  }
  
  protected IResource getWorkspaceFileFromLocation(String location)
  {
    if (location == null) return null;
    IWorkspace workspace = ResourcesPlugin.getWorkspace();
    // To canonicalize the EMF URI
    IPath canonicalForm = new Path(location);
    // Need to convert to absolute location...
    IPath pathLocation = new Path(URIHelper.removeProtocol(canonicalForm.toString()));
    // ...to find the resource file that is in the workspace
    IResource resourceFile = workspace.getRoot().getFileForLocation(pathLocation);
    // If the resource is resolved to a file from http, or a file outside
    // the workspace, then we will just ignore it.
    return resourceFile;
  }
}
