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
package org.fusesource.ide.catalog.generator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.camel.catalog.CamelCatalog;
import org.apache.camel.catalog.DefaultCamelCatalog;
import org.apache.commons.lang.StringEscapeUtils;
import org.fusesource.ide.catalog.generator.model.component.Component;
import org.fusesource.ide.catalog.generator.model.component.ComponentModel;
import org.fusesource.ide.catalog.generator.model.component.ComponentParam;
import org.fusesource.ide.catalog.generator.model.component.UriParam;
import org.fusesource.ide.catalog.generator.model.dataformat.DataFormat;
import org.fusesource.ide.catalog.generator.model.dataformat.DataFormatModel;
import org.fusesource.ide.catalog.generator.model.dataformat.DataFormatProperty;
import org.fusesource.ide.catalog.generator.model.eip.EIP;
import org.fusesource.ide.catalog.generator.model.eip.EIPModel;
import org.fusesource.ide.catalog.generator.model.eip.EIPProperty;
import org.fusesource.ide.catalog.generator.model.language.Language;
import org.fusesource.ide.catalog.generator.model.language.LanguageModel;
import org.fusesource.ide.catalog.generator.model.language.LanguageProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author lhein
 *
 */
public class CatalogGenerator {
	public static Logger LOG = LoggerFactory.getLogger(CatalogGenerator.class);
	
	private String catalogsDir;
	
	/**
	 * 
	 */
	public CatalogGenerator(String catalogTargetDir) {
		this.catalogsDir = catalogTargetDir;
	}

	/**
	 * generates the catalog xml model
	 * 
	 * @throws Exception
	 */
	public void generateCatalogData() throws Exception {
		File catalogsParentDir = new File(".", this.catalogsDir);
		catalogsParentDir = new File(catalogsParentDir, "catalogs");
		String version = System.getProperty("camel.version");
		
        if (catalogsParentDir.exists() == false || catalogsParentDir.isDirectory() == false) catalogsParentDir.mkdirs();
        
        File catalogsVersionDir = new File(catalogsParentDir, version);
        if (catalogsVersionDir.exists() == false || catalogsVersionDir.isDirectory() == false) catalogsVersionDir.mkdirs();

        CamelCatalog cat = new DefaultCamelCatalog();
        ObjectMapper mapper = new ObjectMapper();
        
        System.err.println(">>>>> OUTPUT FOLDER: " + catalogsVersionDir.getAbsolutePath());
        
        createComponentModel(catalogsVersionDir, cat, mapper);
        createDataFormatModel(catalogsVersionDir, cat, mapper);
        createLanguageModel(catalogsVersionDir, cat, mapper);
        createEIPModel(catalogsVersionDir, cat, mapper);
	}
	
    private void createComponentModel(File parentFolder, CamelCatalog cat, ObjectMapper mapper) throws IOException {
        File outputFile = new File(parentFolder, "components.xml");
        if (outputFile.exists() && outputFile.isFile()) outputFile.delete(); 
        // build component model
        HashMap<String, ComponentModel> knownComponents = new HashMap<String, ComponentModel>();
        List<String> components = cat.findComponentNames();
        
        for (String compName : components) {
        	String json = cat.componentJSonSchema(compName);
        	
        	ComponentModel model = mapper.readValue(json, ComponentModel.class);
        	
        	String id = model.getComponent().getScheme();
        	ComponentModel c = knownComponents.get(id);
            if (c == null) {
                c = model;
            } 
            c.getComponent().setId(model.getComponent().getScheme());
            c.getComponent().setScheme(model.getComponent().getScheme());                        
            knownComponents.put(id, c);
        }
        
        PrintWriter out = new PrintWriter(new FileWriter(outputFile));
        out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        out.println("<components>");
        
        Collection<ComponentModel> comps = knownComponents.values();
        for (ComponentModel compModel : comps) {
        	Component comp = compModel.getComponent();

        	out.println("   <component>");
            out.println("      <id>" + comp.getId() + "</id>");
            out.println("      <tags>");
            String[] tags = comp.getLabel().split(",");
            for (String tag : tags) {
            	out.println("         <tag>" + tag + "</tag>");
            }
            out.println("      </tags>");
            if (comp.getTitle() != null) out.println("      <title>" + comp.getTitle() + "</title>");
            out.println("      <description>" + StringEscapeUtils.escapeXml(comp.getDescription()) + "</description>");
            out.println("      <syntax>" + comp.getSyntax() + "</syntax>");
            out.println("      <class>" + comp.getJavaType() + "</class>");
            out.println("      <kind>" + comp.getKind() + "</kind>");
            if (comp.getExtendsScheme() != null)out.println("      <extendsScheme>" + comp.getExtendsScheme() + "</extendsScheme>");
            if (comp.getConsumerOnly() != null) out.println("      <consumerOnly>" + comp.getConsumerOnly() + "</consumerOnly>");
            if (comp.getProducerOnly() != null) out.println("      <producerOnly>" + comp.getProducerOnly() + "</producerOnly>");
            out.println("      <scheme>" + comp.getScheme() + "</scheme>");
            out.println("      <dependencies>");
            out.println("         <dependency>");
            out.println(String.format("            <groupId>%s</groupId>", comp.getGroupId()));
            out.println(String.format("            <artifactId>%s</artifactId>", comp.getArtifactId()));
            out.println(String.format("            <version>%s</version>", comp.getVersion()));
            out.println("         </dependency>");
            out.println("      </dependencies>");

            out.println("      <componentProperties>");
            for (ComponentParam p : compModel.getComponentParams()) {
                out.print("         <componentProperty name=\"" + p.getName() + "\" type=\"" + p.getType() + "\" javaType=\"" + p.getJavaType() + "\" kind=\"" + p.getKind() + "\" ");
                if (p.getChoiceString() != null) out.print("choice=\"" + p.getChoiceString() + "\" ");
                if (p.getDeprecated() != null) out.print("deprecated=\"" + p.getDeprecated() + "\" ");
                if (p.getDefaultValue() != null) { 
                	out.print("defaultValue=\"" + p.getDefaultValue() + "\" ");
                } else {
                	if (p.getJavaType().equalsIgnoreCase("java.lang.boolean") || 
                		p.getJavaType().equalsIgnoreCase("boolean")) {
                		out.print("defaultValue=\"false\" ");  // default for booleans is FALSE
                	} else if (p.getJavaType().equalsIgnoreCase("byte") || 
                			p.getJavaType().equalsIgnoreCase("short") ||
                			p.getJavaType().equalsIgnoreCase("int") ||
                			p.getJavaType().equalsIgnoreCase("long") ||
                			p.getJavaType().equalsIgnoreCase("float") || 
                			p.getJavaType().equalsIgnoreCase("double") ) {
                		out.print("defaultValue=\"0\" ");  // default for numbers is 0
                	}
                }
                if (p.getLabel() != null) out.print("label=\"" + p.getLabel() + "\" ");
                if (p.getRequired() != null) out.print("required=\"" + p.getRequired() + "\" ");
                out.println("description=\"" + (p.getDescription() != null ? p.getDescription() : "") + "\"/>");
            }           
            out.println("      </componentProperties>");   
            
            out.println("      <uriParameters>");
            for (UriParam p : compModel.getUriParams()) {
                out.print("         <uriParameter name=\"" + p.getName() + "\" type=\"" + p.getType() + "\" javaType=\"" + p.getJavaType() + "\" kind=\"" + p.getKind() + "\" ");
                if (p.getChoiceString() != null) out.print("choice=\"" + p.getChoiceString() + "\" ");
                if (p.getDeprecated() != null) out.print("deprecated=\"" + p.getDeprecated() + "\" ");
                if (p.getDefaultValue() != null) { 
                	out.print("defaultValue=\"" + StringEscapeUtils.escapeXml(p.getDefaultValue()) + "\" ");
                } else {
                	if (p.getJavaType().equalsIgnoreCase("java.lang.boolean") || 
                		p.getJavaType().equalsIgnoreCase("boolean")) {
                		out.print("defaultValue=\"false\" ");  // default for booleans is FALSE
                	} else if (p.getJavaType().equalsIgnoreCase("byte") || 
                			p.getJavaType().equalsIgnoreCase("short") ||
                			p.getJavaType().equalsIgnoreCase("int") ||
                			p.getJavaType().equalsIgnoreCase("long") ||
                			p.getJavaType().equalsIgnoreCase("float") || 
                			p.getJavaType().equalsIgnoreCase("double") ) {
                		out.print("defaultValue=\"0\" ");  // default for numbers is 0
                	}
                }
                if (p.getRequired() != null) out.print("required=\"" + p.getRequired() + "\" ");
                if (p.getLabel() != null) out.print("label=\"" + p.getLabel() + "\" ");
                if (p.getGroup() != null) out.print("group=\"" + p.getGroup() + "\" ");
                out.println("description=\"" + (p.getDescription() != null ? StringEscapeUtils.escapeXml(p.getDescription()) : "") + "\"/>");
            }           
            out.println("      </uriParameters>");            
            out.println("   </component>");
        }
        out.println("</components>");
        out.close();
    }
        
    private void createDataFormatModel(File parentFolder, CamelCatalog cat, ObjectMapper mapper) throws IOException {
        File outputFile = new File(parentFolder, "dataformats.xml");
        if (outputFile.exists() && outputFile.isFile()) outputFile.delete(); 
    
        // build data format model
        HashMap<String, DataFormatModel> knownDataFormats = new HashMap<String, DataFormatModel>();
        List<String> dataformatNames = cat.findDataFormatNames();

        for (String dfName : dataformatNames) {
        	String json = cat.dataFormatJSonSchema(dfName);
        	
        	DataFormatModel model = mapper.readValue(json, DataFormatModel.class);
        	
        	String id = model.getDataformat().getName();
        	DataFormatModel c = knownDataFormats.get(id);
            if (c == null) {
                c = model;
            } 
            c.getDataformat().setName(model.getDataformat().getName());                        
            knownDataFormats.put(id, c);
        }
        
        PrintWriter out = new PrintWriter(new FileWriter(outputFile));
        out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        out.println("<dataformats>");
        
        Collection<DataFormatModel> dataformats = knownDataFormats.values();
        for (DataFormatModel dfModel : dataformats) {
        	DataFormat df = dfModel.getDataformat();

        	out.println("   <dataformat>");
            out.println("      <name>" + df.getName() + "</name>");
            out.println("      <tags>");
            String[] tags = df.getLabel().split(",");
            for (String tag : tags) {
            	out.println("         <tag>" + tag + "</tag>");
            }
            out.println("      </tags>");
            out.println("      <title>" + df.getTitle() + "</title>");
            out.println("      <description>" + df.getDescription() + "</description>");
            out.println("      <class>" + df.getJavaType() + "</class>");
            out.println("      <kind>" + df.getKind() + "</kind>");
            out.println("      <modelJavaType>" + df.getModelJavaType() + "</modelJavaType>");
            out.println("      <modelName>" + df.getModelName() + "</modelName>");
            out.println("      <dependencies>");
            out.println("         <dependency>");
            out.println(String.format("            <groupId>%s</groupId>", df.getGroupId()));
            out.println(String.format("            <artifactId>%s</artifactId>", df.getArtifactId()));
            out.println(String.format("            <version>%s</version>", df.getVersion()));
            out.println("         </dependency>");
            out.println("      </dependencies>");

            out.println("      <parameters>");
            for (DataFormatProperty p : dfModel.getParams()) {
                out.print("         <parameter name=\"" + p.getName() + "\" type=\"" + p.getType() + "\" javaType=\"" + p.getJavaType() + "\" kind=\"" + p.getKind() + "\" ");
                if (p.getChoiceString() != null) out.print("choice=\"" + p.getChoiceString() + "\" ");
                if (p.getDeprecated() != null) out.print("deprecated=\"" + p.getDeprecated() + "\" ");
                if (p.getDefaultValue() != null) { 
                	out.print("defaultValue=\"" + p.getDefaultValue() + "\" ");
                } else {
                	if (p.getJavaType().equalsIgnoreCase("java.lang.boolean") || 
                		p.getJavaType().equalsIgnoreCase("boolean")) {
                		out.print("defaultValue=\"false\" ");  // default for booleans is FALSE
                	} else if (p.getJavaType().equalsIgnoreCase("byte") || 
                			p.getJavaType().equalsIgnoreCase("short") ||
                			p.getJavaType().equalsIgnoreCase("int") ||
                			p.getJavaType().equalsIgnoreCase("long") ||
                			p.getJavaType().equalsIgnoreCase("float") || 
                			p.getJavaType().equalsIgnoreCase("double") ) {
                		out.print("defaultValue=\"0\" ");  // default for numbers is 0
                	}
                }
                if (p.getRequired() != null) out.print("required=\"" + p.getRequired() + "\" ");
                out.println("description=\"" + (p.getDescription() != null ? p.getDescription() : "") + "\"/>");
            }           
            out.println("      </parameters>");            
            out.println("   </dataformat>");
        }
        out.println("</dataformats>");
        out.close();
    }
    
    private void createLanguageModel(File parentFolder, CamelCatalog cat, ObjectMapper mapper) throws IOException {
    	File outputFile = new File(parentFolder, "languages.xml");
    	if (outputFile.exists() && outputFile.isFile()) outputFile.delete(); 
        
        // build language model
        HashMap<String, LanguageModel> knownLanguages = new HashMap<String, LanguageModel>();
        List<String> languageNames = cat.findLanguageNames();

        for (String langName : languageNames) {
        	String json = cat.languageJSonSchema(langName);
        	
        	LanguageModel model = mapper.readValue(json, LanguageModel.class);
        	
        	String id = model.getLanguage().getName();
        	LanguageModel c = knownLanguages.get(id);
            if (c == null) {
                c = model;
            } 
            c.getLanguage().setName(model.getLanguage().getName());                        
            knownLanguages.put(id, c);
        }
        
        PrintWriter out = new PrintWriter(new FileWriter(outputFile));
        out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        out.println("<languages>");
        
        Collection<LanguageModel> languages = knownLanguages.values();
        for (LanguageModel langModel : languages) {
        	Language lang = langModel.getLanguage();

        	out.println("   <language>");
            out.println("      <name>" + lang.getName() + "</name>");
            out.println("      <tags>");
            String[] tags = lang.getLabel().split(",");
            for (String tag : tags) {
            	out.println("         <tag>" + tag + "</tag>");
            }
            out.println("      </tags>");
            out.println("      <title>" + lang.getTitle() + "</title>");
            out.println("      <description>" + lang.getDescription() + "</description>");
            out.println("      <class>" + lang.getJavaType() + "</class>");
            out.println("      <kind>" + lang.getKind() + "</kind>");
            out.println("      <modelJavaType>" + lang.getModelJavaType() + "</modelJavaType>");
            out.println("      <dependencies>");
            out.println("         <dependency>");
            out.println(String.format("            <groupId>%s</groupId>", lang.getGroupId()));
            out.println(String.format("            <artifactId>%s</artifactId>", lang.getArtifactId()));
            out.println(String.format("            <version>%s</version>", lang.getVersion()));
            out.println("         </dependency>");
            out.println("      </dependencies>");

            out.println("      <parameters>");
            for (LanguageProperty p : langModel.getParams()) {
                out.print("         <parameter name=\"" + p.getName() + "\" type=\"" + p.getType() + "\" javaType=\"" + p.getJavaType() + "\" kind=\"" + p.getKind() + "\" ");
                if (p.getChoiceString() != null) out.print("choice=\"" + p.getChoiceString() + "\" ");
                if (p.getDeprecated() != null) out.print("deprecated=\"" + p.getDeprecated() + "\" ");
                if (p.getDefaultValue() != null) { 
                	out.print("defaultValue=\"" + p.getDefaultValue() + "\" ");
                } else {
                	if (p.getJavaType().equalsIgnoreCase("java.lang.boolean") || 
                		p.getJavaType().equalsIgnoreCase("boolean")) {
                		out.print("defaultValue=\"false\" ");  // default for booleans is FALSE
                	} else if (p.getJavaType().equalsIgnoreCase("byte") || 
                			p.getJavaType().equalsIgnoreCase("short") ||
                			p.getJavaType().equalsIgnoreCase("int") ||
                			p.getJavaType().equalsIgnoreCase("long") ||
                			p.getJavaType().equalsIgnoreCase("float") || 
                			p.getJavaType().equalsIgnoreCase("double") ) {
                		out.print("defaultValue=\"0\" ");  // default for numbers is 0
                	}
                }
                if (p.getRequired() != null) out.print("required=\"" + p.getRequired() + "\" ");
                out.println("description=\"" + (p.getDescription() != null ? p.getDescription() : "") + "\"/>");
            }           
            out.println("      </parameters>");            
            out.println("   </language>");
        }
        out.println("</languages>");
        out.close();
    }
    
    private void createEIPModel(File parentFolder, CamelCatalog cat, ObjectMapper mapper) throws IOException {
        File outputFile = new File(parentFolder, "eips.xml");
        if (outputFile.exists() && outputFile.isFile()) outputFile.delete(); 
    
        // build eip model
        HashMap<String, EIPModel> knownEIPs = new HashMap<String, EIPModel>();
        List<String> eipNames = cat.findModelNames();

        for (String eipName : eipNames) {
        	String json = cat.modelJSonSchema(eipName);
        	
        	EIPModel model = mapper.readValue(json, EIPModel.class);
        	
        	String id = model.getEip().getName();
        	EIPModel c = knownEIPs.get(id);
            if (c == null) {
                c = model;
            } 
            c.getEip().setName(model.getEip().getName());                        
            knownEIPs.put(id, c);
        }
        
        PrintWriter out = new PrintWriter(new FileWriter(outputFile));
        out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        out.println("<eips>");
        
        Collection<EIPModel> eips = knownEIPs.values();
        for (EIPModel eipModel : eips) {
        	EIP eip = eipModel.getEip();

        	out.println("   <eip>");
            out.println("      <name>" + eip.getName() + "</name>");
            out.println("      <tags>");
            String[] tags = eip.getLabel().split(",");
            for (String tag : tags) {
            	out.println("         <tag>" + tag + "</tag>");
            }
            out.println("      </tags>");
            out.println("      <title>" + eip.getTitle() + "</title>");
            out.println("      <description>" + eip.getDescription() + "</description>");
            out.println("      <class>" + eip.getJavaType() + "</class>");
            out.println("      <kind>" + eip.getKind() + "</kind>");
            out.println("      <input>" + eip.getInput() + "</input>");
            out.println("      <output>" + eip.getOutput() + "</output>");
            
            out.println("         <parameters>");
            for (EIPProperty p : eipModel.getParams()) {
                out.print("         <parameter name=\"" + p.getName() + "\" type=\"" + p.getType() + "\" javaType=\"" + p.getJavaType() + "\" kind=\"" + p.getKind() + "\" ");
                if (p.getOneOfString() != null) out.print("oneOf=\"" + p.getOneOfString() + "\" ");
                if (p.getChoiceString() != null) out.print("choice=\"" + p.getChoiceString() + "\" ");
                if (p.getDeprecated() != null) out.print("deprecated=\"" + p.getDeprecated() + "\" ");
                if (p.getDefaultValue() != null) { 
                	out.print("defaultValue=\"" + p.getDefaultValue() + "\" ");
                } else {
                	if (p.getJavaType().equalsIgnoreCase("java.lang.boolean") || 
                		p.getJavaType().equalsIgnoreCase("boolean")) {
                		out.print("defaultValue=\"false\" ");  // default for booleans is FALSE
                	} else if (p.getJavaType().equalsIgnoreCase("byte") || 
                			p.getJavaType().equalsIgnoreCase("short") ||
                			p.getJavaType().equalsIgnoreCase("int") ||
                			p.getJavaType().equalsIgnoreCase("long") ||
                			p.getJavaType().equalsIgnoreCase("float") || 
                			p.getJavaType().equalsIgnoreCase("double") ) {
                		out.print("defaultValue=\"0\" ");  // default for numbers is 0
                	}
                }
                if (p.getGroup() != null) out.print("group=\"" + p.getGroup() + "\" ");
                if (p.getRequired() != null) out.print("required=\"" + p.getRequired() + "\" ");
                if (p.getOriginalVariableName() != null) out.print("originalFieldName=\"" + p.getOriginalVariableName() + "\" ");
                out.println("description=\"" + (p.getDescription() != null ? p.getDescription() : "") + "\"/>");
            }           
            out.println("      </parameters>");            
            out.println("   </eip>");
        }
        out.println("</eips>");
        out.close();
    }
	
	public static void main(String[] args) throws Exception {
		CatalogGenerator gen = new CatalogGenerator("target");
		gen.generateCatalogData();
	}
}
