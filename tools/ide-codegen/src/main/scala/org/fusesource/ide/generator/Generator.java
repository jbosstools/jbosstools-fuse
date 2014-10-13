/**
 *  Copyright 2005-2014 Red Hat, Inc.
 *
 *  Red Hat licenses this file to you under the Apache License, version
 *  2.0 (the "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied.  See the License for the specific language governing
 *  permissions and limitations under the License.
 */
package org.fusesource.ide.generator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.transform.stream.StreamSource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.DescriptionDefinition;
import org.apache.camel.model.OtherwiseDefinition;
import org.apache.camel.model.ToDefinition;
import org.apache.camel.model.WhenDefinition;
import org.apache.commons.io.IOUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.fusesource.ide.generator.model.Node;
import org.fusesource.ide.generator.model.Nodeset;
import org.fusesource.ide.generator.velocity.Slf4jLogChute;
import org.fusesource.scalate.introspector.BeanProperty;
import org.fusesource.scalate.introspector.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Generator {

    public static Logger LOG = LoggerFactory.getLogger(Generator.class);

    public static String defaultSourceDir = "src/main/webapp/stencilsets/camel";
    public static String defaultOutputDir = defaultSourceDir;
    public static boolean eclipseMode = false;
    private static String SEPARATOR = "\n";

    public static Class[] XML_ANNOTATIONS = new Class[] {
            XmlAttribute.class, XmlElement.class, XmlElementRef.class, XmlElements.class
    };

    public static Set<Class<?>> ignoreClasses = new HashSet<Class<?>>(Arrays.asList(ToDefinition.class));

    private String outputDir = defaultOutputDir;
    private String sourceDir = defaultSourceDir;

    public Set<NodeDefinition<?>> nodeDefinitions = loadModelTypes();
    public Set<NodeDefinition<?>> baseClassAndNestedClasses = loadBaseClassAndNestedClasses();
    public Set<NodeDefinition<?>> nodeDefinitionsAndBaseClasses = new HashSet<>();

    private boolean debug = false;
    private VelocityEngine engine;

    private CamelContext camelContext = new DefaultCamelContext();

    private Map<String, NodeDefinition<?>> nodeDefinitionMap = new HashMap<>();
    private Map<Class<?>, NodeDefinition<?>> nodeDefinitionClassMap = new HashMap<>();

    private NodeDefinition toDefinition = createNodeDefinition("ToDefinition");

    private File dir = new File(outputDir);
    private File srcDir = new File(sourceDir);

    private String[] imageExtensions = new String[] { "png", "gif", "jpg", "jpeg" };

    private String eclipseIconDir = "../../../../../../../core/plugins/org.fusesource.ide.camel.model/icons/";

    private Nodeset xmlModel;

    public Generator(String outputDir, String sourceDir) {
        this.outputDir = outputDir;
        this.sourceDir = sourceDir;

        for (NodeDefinition<?> nd : nodeDefinitions) {
            nodeDefinitionMap.put(nd.getId(), nd);
            nodeDefinitionClassMap.put(nd.getClazz(), nd);
        }
        nodeDefinitionsAndBaseClasses.addAll(nodeDefinitions);
        nodeDefinitionsAndBaseClasses.addAll(baseClassAndNestedClasses);

        engine = new VelocityEngine();
        engine.setProperty(VelocityEngine.INPUT_ENCODING, "UTF-8");
        engine.setProperty(VelocityEngine.OUTPUT_ENCODING, "UTF-8");
        engine.setProperty(VelocityEngine.RUNTIME_LOG_LOGSYSTEM_CLASS, Slf4jLogChute.class.getName());
        engine.setProperty(VelocityEngine.RESOURCE_LOADER, "class");
        engine.setProperty("class.resource.loader.class", ClasspathResourceLoader.class.getName());
        engine.init();
    }

    public void generateCamelDescriptionElements(File file) {
        String text = findDescriptionNodes();
        file.getParentFile().mkdirs();
        writeText(file, text);
    }

    public void generateEclipseModel(String outputDir) throws IOException {
        eclipseMode = true;
        Thread.currentThread().setContextClassLoader(classLoader());

        LOG.info("Generating files to {}", outputDir);
        new File(outputDir).mkdirs();

        int[] codes = new int[SEPARATOR.length()];
        int idx = 0;
        for (char c : SEPARATOR.toCharArray()) {
            codes[idx++] = (int) c;
        }
        LOG.info("Separator used is length " + SEPARATOR.length() + " codes: " + Arrays.asList(codes));

        String srcDir = "org/fusesource/ide/generator/eclipse";
        render("ComplexProperties.txt", outputDir, srcDir);
        render("tooltips.properties", new File(outputDir + "/../l10n").getCanonicalPath(), srcDir);
        render("Tooltips.java", outputDir, srcDir);
        render("messages.properties", new File(outputDir + "/../l10n").getCanonicalPath(), srcDir);
        render("Messages.java", outputDir, srcDir);
        render("NodeFactory.java", outputDir, srcDir);

        // lets load our templates up first to test any errors
        String uri = srcDir + "/ModelBean.java.vm";
        engine.getTemplate(uri);

        List<String> errors = new LinkedList<>();

        for (NodeDefinition<?> node: nodeDefinitions) {
            if (debug) {
                LOG.info(node.getDefinitionName());
                for (Property<?> p: node.simpleProperties()) {
                    LOG.info("  simple:  " + p.label() + " " + javaScriptType(p));
                }
                for (Property<?> p: node.complexProperties()) {
                    LOG.info("  complex: " + p.label() + " " + p.propertyType().getName());
                }
            }

            try {
                VelocityContext context = new VelocityContext();
                context.put("generator", this);
                context.put("node", node);
                if (node.getName().equals("RouteDefinition")) {
                    context.put("isRoute", Boolean.TRUE);
                    context.put("baseClass", "RouteSupport");
                } else {
                    context.put("isRoute", Boolean.FALSE);
                    context.put("baseClass", "AbstractNode");
                }

                Set<String> propertyTypes = new LinkedHashSet<>();
                for (Property<?> p : node.beanProperties()) {
                    String pn = p.propertyType().getCanonicalName();
                    if (!pn.startsWith("java.lang") && pn.contains(".")) {
                        propertyTypes.add(pn);
                    }
                }
                propertyTypes.add("org.apache.camel.model." + node.getName());
                propertyTypes.add("org.apache.camel.model.language.ExpressionDefinition");
                context.put("importedTypes", propertyTypes);

                StringWriter sw = new StringWriter();
                engine.getTemplate(uri).merge(context, sw);
                String answer = sw.toString();
                String outFile = outputDir + "/" + node.getDefinitionName() + ".java";
                LOG.info("Generating file: {}", outFile);
                writeText(outFile, answer);
            }
            catch (Exception e) {
                LOG.error("Failed to compile " + uri + ": " + e.getMessage(), e);
            }
        }

        if (!errors.isEmpty()) {
            LOG.warn("add to NodeDefinition.documentationFile method:");
            for (String e: errors) {
                LOG.warn(" - " + e);
            }
        }
    }

    public void generateEclipseEditor(String outputDir) {
        eclipseMode = true;
        Thread.currentThread().setContextClassLoader(classLoader());

        LOG.info("Generating files to {}", outputDir);
        new File(outputDir).mkdirs();

        String srcDir = "org/fusesource/ide/generator/eclipse/editor";

        String[] templates = new String[] {
                "provider/generated/ProviderHelper.java",
                "provider/generated/AddNodeMenuFactory.java",
                "l10n/messages.properties",
                "Messages.java"
        };

        for (String t: templates) {
            render(t, outputDir, srcDir);
        }
    }

    public void run() {
        Thread.currentThread().setContextClassLoader(classLoader());

        if (debug) {
            for (NodeDefinition<?> n: nodeDefinitions) {
                LOG.debug(n.getName());
                for (Property<?> p: n.simpleProperties()) {
                    LOG.info("  simple:  " + p.label() + " " + javaScriptType(p));
                }
                for (Property<?> p: n.complexProperties()) {
                    LOG.info("  complex: " + p.label() + " " + p.propertyType().getName());
                }
            }
        }

        render("Dimensions.scala", "src/main/scala/org/fusesource/ide/generator");

        String[] uris = new String[] { "camel.json" };

        LOG.info("Generating files to {}", outputDir);
        new File(outputDir).mkdirs();

        for (String u: uris) {
            render(u, outputDir);
        }
    }

    public void generateHawtIO(String outputDir) throws FileNotFoundException {
        String[] uris = new String[] { "camelModel.json" };

        LOG.info("Generating files to {}", outputDir);
        new File(outputDir).mkdirs();

        for (String u: uris) {
            render(u, outputDir, "src/main/resources/org/fusesource/ide/generator/hawtio");

            // now lets parse and pretty print the JSON
            File file = new File(outputDir, u);
            Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
            JsonParser jp = new JsonParser();
            JsonElement je = jp.parse(new FileReader(file));
            String prettyJsonString = gson.toJson(je);
            writeText(new File(outputDir, "camelModel.js"), "var _apacheCamelModel = " + prettyJsonString + ";");
            LOG.info("Pretty printed JSON {}", file);
        }
    }

    private void render(String u, String outputDir) {
        render(u, outputDir, "src/main/resources/org/fusesource/ide/generator");
    }

    private void render(String u, String outputDir, String srcDir) {
        render(u, outputDir, srcDir, ".vm");
    }

    private void render(String u, String outputDir, String srcDir, String extension) {
        VelocityContext context = new VelocityContext();
        context.put("generator", this);

        String uri = srcDir + "/" + u + extension;
        LOG.info("rendering {}", uri);

        try {
            // lets make sure we don't have a compile error
            Template template = engine.getTemplate(uri);
            StringWriter sw = new StringWriter();
            template.merge(context, sw);
            String answer = sw.toString();

            String outFile = outputDir + "/" + u;
            LOG.info("Generating file: {}", outFile);
            writeText(outFile, answer);
        }
        catch (Exception e) {
            LOG.error("Failed to compile " + uri + " " + e.getMessage(), e);
      /*
                for (err <- e.errors) {
                  println(err.message + " at " + err.pos + " " + err.original.message)
                }
      */
        }
    }

    protected Set<NodeDefinition<?>> loadBaseClassAndNestedClasses() {
        Set<Class<?>> classes = new LinkedHashSet<>();
        classes.add(DescriptionDefinition.class);
        Set<String> ignoredTypeNames = new HashSet<>(Arrays.asList(
                "java.util.List",
                "java.lang.String",
                "org.apache.camel.model.language.Expression"
        ));
        for (NodeDefinition<?> node: nodeDefinitions) {
            for (Property<?> prop: node.beanProperties()) {
                Class<?> propType = prop.propertyType();
                if (!propType.isPrimitive() && node.isSimplePropertyType(prop) &&
                        !ignoredTypeNames.contains(propType.getName())) {
                    classes.add(propType);
                }
                Class<?> aClass = elementTypeClass(prop);
                if (aClass != null) {
                    classes.add(aClass);
                }
                XmlElement[] elements = node.xmlElements(prop);
                for (XmlElement el: elements) {
                    classes.add(el.type());
                }
            }
        }
        Set<NodeDefinition<?>> result = new LinkedHashSet<>();
        for (Class<?> c : classes) {
            result.add(new NodeDefinition<>(c.getName(), c, this));
        }
        return result;
    }

    private Class<?> elementTypeClass(Property<?> prop) {
        if (isJavaCollection(prop)) {
            if (prop instanceof FieldProperty) {
                FieldProperty fieldProp = (FieldProperty)prop;
                Field fld = fieldProp.getField();
                return elementTypeClass(fld.getGenericType());
            }
            if (prop instanceof BeanProperty) {
                BeanProperty beanProp = (BeanProperty)prop;
                Method readMethod = beanProp.descriptor().getReadMethod();
                if (readMethod != null) {
                    return elementTypeClass(readMethod.getGenericReturnType());
                }
            }
        }
        return null;
    }

    private Class<?> elementTypeClass(Type retType) {
        if (retType != null && retType instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType)retType;
            Type[] arguments = paramType.getActualTypeArguments();
            if (arguments != null && arguments.length > 0) {
                if (arguments[0] instanceof Class) {
                    return (Class<?>) arguments[0];
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    protected Set<NodeDefinition<?>> loadModelTypes() {
        String[] classNames = loadStrings("org/apache/camel/model/jaxb.index");
        Set<NodeDefinition<?>> set = new LinkedHashSet<>();
        try {
            for (String className : classNames) {
                NodeDefinition<?> n = createNodeDefinition(className);
                if (!n.isProcessor()) {
                    set.add(n);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return set;
    }

    protected String[] loadStrings(String uri) {
        URL url = classLoader().getResource(uri);
        if (url == null) {
            throw new IllegalArgumentException("Cannot find resource " + uri + "!");
        }
        List<String> result = new LinkedList<>();
        StringWriter writer = new StringWriter();
        try {
            InputStream is = url.openStream();
            IOUtils.copy(is, writer);
            IOUtils.closeQuietly(is);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        String text = writer.toString();
        for (String line : text.split("\n")) {
            if (line.length() > 0 && !line.startsWith("#")) {
                result.add(line);
            }
        }
        return result.toArray(new String[result.size()]);
    }

    protected ClassLoader classLoader() {
        return getClass().getClassLoader();
    }

    public String findDescriptionNodes() {
        StringBuilder builder = new StringBuilder();
        for (NodeDefinition<?> node: nodeDefinitions) {
            if (node.getIntrospector().propertyMap().contains("description") && node.getElementName() != null) {
                builder.append(node.getElementName());
                builder.append(" ");
            }
        }
        return builder.toString().trim();
    }

    private void writeText(String outFile, String answer) {
        writeText(new File(outFile), answer);
    }

    private void writeText(File outFile, String answer) {
        try {
            FileOutputStream os = new FileOutputStream(outFile);
            IOUtils.write(convertNewlines(answer), os);
            IOUtils.closeQuietly(os);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    protected String convertNewlines(String answer) {
        String[] lines = answer.split("\n");
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            sb.append(line).append(SEPARATOR);
        }
        return sb.toString();
    }

    protected NodeDefinition<?> createNodeDefinition(String n) {
        try {
            return new NodeDefinition<>(n, classLoader().loadClass("org.apache.camel.model." + n), this);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public Map<Class<?>, NodeDefinition<?>> getNodeDefinitionClassMap() {
        return nodeDefinitionClassMap;
    }

    public Set<NodeDefinition<?>> nodeDefinitions() {
        return nodeDefinitions;
    }

    /**
     * Returns the JavaScript type we should use for editing the property
     * @param prop
     * @return
     */
    private String javaScriptType(Property<?> prop) {
        if (prop.propertyType().isArray() || isJavaCollection(prop)) {
            return "array";
        }
        String number = "number";
        String string = "string";
        String bool = "bool";
        switch (prop.propertyType().getName()) {
            case "java.lang.Byte": return number;
            case "java.lang.Short": return number;
            case "java.lang.Integer": return number;
            case "java.lang.Long": return number;
            case "java.lang.Float": return number;
            case "java.lang.Double": return number;
            case "byte": return number;
            case "short": return number;
            case "int": return number;
            case "long": return number;
            case "float": return number;
            case "double": return number;
            case "java.util.Date": return string;
            case "java.lang.String": return string;
            case "boolean": return bool;
            case "java.lang.Boolean": return bool;
            default:
                return prop.propertyType().getName();
        }
    }

    private boolean isJavaCollection(Property prop) {
        switch (prop.propertyType().getName()) {
            case "java.util.List":
                return true;
            case "java.util.Set":
                return true;
            case "java.util.Collection":
                return true;
            case "java.lang.Iterable":
                return true;
            default:
                return false;
        }
    }

    public Nodeset getXmlModel() {
        if (xmlModel == null) {
            try {
                JAXBContext context = JAXBContext.newInstance(Nodeset.class);
                xmlModel = (Nodeset) context.createUnmarshaller().unmarshal(new StreamSource(getClass().getResourceAsStream("/org/fusesource/ide/generator/model.xml")));
            } catch (JAXBException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
        return xmlModel;
    }

    /* For Velocity */

    public boolean isValid(NodeDefinition<?> n, Property<?> p) {
        return !WhenDefinition.class.isAssignableFrom(p.propertyType()) && !OtherwiseDefinition.class.isAssignableFrom(p.propertyType()) && !n.isBeanProperty(p);
    }

    // XML helpers

//    public static String childElemText(Node on, String name, String defaultValue) {
//        if (on == null) {
//            return defaultValue;
//        } else {
//            String t = n\name
//            if (t.isEmpty)
//                defaultValue
//            else
//                t.text
//        }
//    }

    public static Node findElemById(List<Node> nodes, String id) {
        for (Node n : nodes) {
            if (n.id != null && n.id.equals(id)) {
                return n;
            }
        }
        return null;
    }

    public String findIconFileOrElse(String childDir, String name, String elseName) throws IOException {
        return findIconFileOrElse(childDir, name, imageExtensions, elseName);
    }

    /**
     * Returns the name if the file exists inside the childDir directory of the outputDir otherwise
     * return the elseName
     * @param childDir
     * @param name
     * @param extensions
     * @param elseName
     * @return
     */
    public String findIconFileOrElse(String childDir, String name, String[] extensions, String elseName) throws IOException {
        File subDir = new File(new File(srcDir, childDir).getCanonicalPath());
        if (!subDir.exists()) {
            LOG.warn("Icon dir " + subDir.getCanonicalPath() + " does not exist!");
        }
        for (String e : extensions) {
            File f = new File(subDir, name + "." + e);
            if (f.exists()) {
                return f.getName();
            }
        }
        return elseName;
    }

    public String getEclipseIconDir() {
        return eclipseIconDir;
    }
}
/*


object Generator {

    //val separator = System.getProperty("line.separator", "\r\n")



  private val singleton = new Generator

  def nodeDefinition(value: AnyRef) = {
    val clazz = value.getClass
    new NodeDefinition(clazz.getSimpleName, clazz, singleton)
  }







}

import Generator._
import Reflections._

object Dimension {
  val doesNotExist = Dimension("doesNotExist.svg", 58.5, 36)
}

class Generator() extends Logging {



  / * *
   * Make it easy to add a comma between values while iterating
   * /
  def comma[T](iter: Iterable[T])(fn: T => Any): Unit = {
    var first = true
    for (t <- iter) {
      if (first)
        first = false
      else
        RenderContext() << ", "
      fn(t)
    }
  }












  def toId(name: String): String = {
    val idx = name.lastIndexOf("Definition")
    val definitionName = if (idx > 0) name.substring(0, idx) else name
    return (new Strings()).decapitalize(definitionName)
  }

  def isProcessor(clazz: Class[_]) = !Modifier.isAbstract(clazz.getModifiers) &&
          classOf[ProcessorDefinition[_]].isAssignableFrom(clazz) &&
          !ignoreClasses.contains(clazz)


  def elementTypeId(prop: Property[_]): Option[String] = {
    elementTypeClass(prop) match {
      case Some(clazz) => Some(if (isProcessor(clazz)) {
        toId(clazz.getSimpleName)
      } else {
        clazz.getName
      })
      case _ => None
    }
  }

  def elementType(prop: Property[_]): Option[String] = {
    return elementTypeClass(prop) match {
      case Some(aClass) => Some(aClass.getName)
      case _ => None
    }
  }




  def isExpression(prop: Property[_]) = classOf[ExpressionDefinition].isAssignableFrom(prop.propertyType)

  def wrapLines(prop: Property[_]) = prop.name == "description"


  def findNodeDimensions: Seq[Dimension] = {
    new File(srcDir, "view").listFiles.filter(_.getName.matches("""node\..+\.svg""")).map{
        f =>
        val doc = XML.loadFile(f)
        var width = -1.0
        var height = -1.0
        for (r <- doc \\ "rect") {
          width = max(width, attributeDoubleValue(r, "width"))
          height = max(height, attributeDoubleValue(r, "height"))
        }

        val d = Dimension(f.getName, width, height)
        println("found: " + d)
        d
    }
  }


        / * *
         * Returns the double value of the given attribute or return the default value if none is provided
         * /
        def attributeDoubleValue(e: Node, name: String, defaultValue: Double = -1): Double = {
        e.attribute(name) match {
    case Some(s) =>
        if (s.isEmpty) {
            defaultValue
        }
        else {
            s.head.text.toDouble
        }
    case _ => defaultValue
        }
        }




}

*/