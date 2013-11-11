
package org.fusesource.ide.generator

import collection.JavaConversions._
import xml.{Elem, Node, XML}

import Math._
import java.{util => ju}
import java.io.{FileReader, FileInputStream, File}

import org.apache.camel.CamelContext
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.model._
import config.{BatchResequencerConfig, StreamResequencerConfig}
import org.apache.camel.model.language.{LanguageExpression, ExpressionDefinition}
import org.apache.camel.spi.Required

import org.fusesource.camel.tooling.util.Strings._
import org.fusesource.camel.tooling.util.XmlHelper._
import org.fusesource.camel.tooling.util.Objects._
import org.fusesource.camel.tooling.util.CamelModelUtils

import org.fusesource.scalate.{RenderContext, CompilerException, TemplateEngine}
import org.fusesource.scalate.introspector.{BeanProperty, Property, Introspector}
import org.fusesource.scalate.util.{Logging, IOUtil}
import java.lang.reflect.{AnnotatedElement, Field, Modifier}
import java.lang.annotation.{Annotation => JAnnotation}
import javax.xml.bind.annotation.{XmlRootElement, XmlElements, XmlElementRef, XmlAttribute, XmlElement, XmlAccessType, XmlAccessorType, XmlTransient}
import com.google.gson.{JsonParser, GsonBuilder}

object Reflections {

  def isStatic(f: Field): Boolean = {
    Modifier.isStatic(f.getModifiers)
  }

  def hasAnnotations(e: AnnotatedElement, cs: Seq[Class[_]]): Boolean = {
    cs.find{
        c =>
        val ca = c.asInstanceOf[Class[JAnnotation]]
        if (ca == null) {
          throw new ClassCastException("Could not convert: " + c.getName + " into an annotation class")
        }
        hasAnnotation(e, ca)
    }.isDefined
  }

  def hasAnnotation[T <: JAnnotation](e: AnnotatedElement, c: Class[T]): Boolean =
    e != null && e.getAnnotation(c) != null
}

object Generator {
  val defaultSourceDir = "src/main/webapp/stencilsets/camel"
  val defaultOutputDir = defaultSourceDir
  var eclipseMode = false

    //val separator = System.getProperty("line.separator", "\r\n")
  val separator = "\r\n"


  private val singleton = new Generator

  def nodeDefinition(value: AnyRef) = {
    val clazz = value.getClass
    new NodeDefinition(clazz.getSimpleName, clazz, singleton)
  }

  protected def convertNewlines(answer: String): String = {
    val lines = answer.split('\n')
    lines.mkString("", separator, separator)
  }

  def writeText(outFile: String, answer: String) {
    IOUtil.writeText(outFile, convertNewlines(answer))
  }

  def writeText(outFile: File, answer: String) {
    IOUtil.writeText(outFile, convertNewlines(answer))
  }


  // XML helpers

  def childElemText(on: Option[Node], name: String, defaultValue: => String): String = on match {
    case Some(n) =>
      val t = n \ name
      if (t.isEmpty)
        defaultValue
      else
        t.text
    case _ => defaultValue
  }

  def findElemById(s: Seq[Node], id: String): Option[Node] = s.find{
      n =>
      n.attribute("id") match {
        case Some(s) =>
        //val nid = s.mkString("")
          val nid = s.text
          //println("Found node with id '" + nid + "' when searching for id '" + id + "'")
          id == nid
        case _ =>
          false
      }
  }

  val xmlAnnotations = Seq(classOf[XmlAttribute], classOf[XmlElement], classOf[XmlElementRef], classOf[XmlElements])
}

import Generator._
import Reflections._

object Dimension {
  val doesNotExist = Dimension("doesNotExist.svg", 58.5, 36)
}

case class Dimension(name: String, width: Double, height: Double)

class Generator(val outputDir: String = Generator.defaultOutputDir, val sourceDir: String = Generator.defaultSourceDir) extends Logging {

  var debug = false
  var engine = new TemplateEngine
  engine.escapeMarkup = false

  var camelContext: CamelContext = new DefaultCamelContext

  lazy val nodeDefinitions = loadModelTypes
  lazy val nodeDefinitionMap = Map[String, NodeDefinition[_]](nodeDefinitions.map(n => n.id -> n): _*)
  lazy val nodeDefinitionClassMap = Map[Class[_], NodeDefinition[_]](nodeDefinitions.map(n => n.clazz -> n): _*)

  lazy val baseClassAndNestedClasses = loadBaseClassAndNestedClasses

  lazy val toDefinition = createNodeDefinition("ToDefinition")

  val dir = new File(outputDir)
  val srcDir = new File(sourceDir)

  val imageExtensions = List("png", "gif", "jpg", "jpeg")

  val eclipseIconDir = "../../../../../../../plugins/org.fusesource.ide.camel.model/icons/"


  /**
   * Make it easy to add a comma between values while iterating
   */
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


  def run: Unit = {
    Thread.currentThread.setContextClassLoader(classLoader)

    if (debug) {
      for (n <- nodeDefinitions) {
        println(n.name)
        for (p <- n.simpleProperties) {
          println("  simple:  " + p.label + " " + javaScriptType(p))
        }
        for (p <- n.complexProperties) {
          println("  complex: " + p.label + " " + p.propertyType.getName)
        }
        println
      }
    }

    render("Dimensions.scala", "src/main/scala/org/fusesource/ide/generator")

    var uris = List("camel.json")

    println("Generating files to " + outputDir)
    new File(outputDir).mkdirs

    for (u <- uris) {
      render(u, outputDir)
    }
  }


  def generateEclipseModel(outputDir: String): Unit = {
    eclipseMode = true
    Thread.currentThread.setContextClassLoader(classLoader)

    println("Generating files to " + outputDir)
    new File(outputDir).mkdirs

    println("Separator used is length " + separator.size + " codes: " + separator.map { _.toInt} )

    val srcDir = "src/main/resources/org/fusesource/ide/generator/eclipse"
    render("ComplexProperties.txt", outputDir, srcDir)
    render("tooltips.properties", new File(outputDir + "/../l10n").getCanonicalPath, srcDir)
    render("Tooltips.java", outputDir, srcDir)
    render("messages.properties", new File(outputDir + "/../l10n").getCanonicalPath, srcDir)
    render("Messages.java", outputDir, srcDir)
    render("NodeFactory.java", outputDir, srcDir)

    // lets load our templates up first to test any errors
    val uri = srcDir + "/modelBean.ssp"
    engine.load(uri)

    var errors = List[String]()

    for (n <- nodeDefinitions) {
      if (debug) {
        println(n.definitionName)
        for (p <- n.simpleProperties) {
          println("  simple:  " + p.label + " " + javaScriptType(p))
        }
        for (p <- n.complexProperties) {
          println("  complex: " + p.label + " " + p.propertyType.getName)
        }
        println
      }

      try {
        val attributes = Map("generator" -> this, "node" -> n)

        val answer = engine.layout(uri, attributes)
        val outFile = outputDir + "/" + n.definitionName + ".java"
        println("Generating file: " + outFile)
        writeText(outFile, answer)
      }
      catch {
        case e: CompilerException =>
          println("Failed to compile " + uri + " " + e.getMessage)
          e.printStackTrace
      }

    }

    if (!errors.isEmpty) {
      println("WARN: add to NodeDefinition.documentationFile method:")
      for (e <- errors.reverseIterator) {
        println(e)
      }
    }
  }


  def findDescriptionNodes(): String = {
    val builder = new StringBuilder()
    for (node <- nodeDefinitions) {
      if (node.introspector.propertyMap.contains("description")) {
        builder.append(node.elementName)
        builder.append(" ")
      }
    }
    return builder.toString().trim()
  }

  def generateCamelDescriptionElements(file: File): Unit = {
    val text = findDescriptionNodes()
    file.getParentFile.mkdirs()
    writeText(file, text)
  }

  def generateEclipseEditor(outputDir: String): Unit = {
    eclipseMode = true
    Thread.currentThread.setContextClassLoader(classLoader)

    println("Generating files to " + outputDir)
    new File(outputDir).mkdirs

    val srcDir = "src/main/resources/org/fusesource/ide/generator/eclipse/editor"

    val templates = List(
      "provider/generated/ProviderHelper.java",
      "provider/generated/AddNodeMenuFactory.java",
      "l10n/messages.properties",
      "Messages.java"
      )

    for (t <- templates) {
      render(t, outputDir, srcDir)
    }
  }


  def generateHawtIO(outputDir: String): Unit = {
    var uris = List("camelModel.json")

    println("Generating files to " + outputDir)
    new File(outputDir).mkdirs

    for (u <- uris) {
      render(u, outputDir, "src/main/resources/org/fusesource/ide/generator/hawtio")

      // now lets parse and pretty print the JSON
      val file = new File(outputDir, u)
      val gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
      val jp = new JsonParser()
      val je = jp.parse(new FileReader(file))
      val prettyJsonString = gson.toJson(je)
      IOUtil.writeText(new File(outputDir, "camelModel.js"), "var _apacheCamelModel = " + prettyJsonString + ";")
      println("Pretty printed JSON " + file)
    }
  }

  def render(u: String, outputDir: String, srcDir: String = "src/main/resources/org/fusesource/ide/generator", extension: String = ".ssp"): Unit = {
    val attributes = Map("generator" -> this)
    val uri = srcDir + "/" + u + extension
    println("rendering " + uri)

    try {
      // lets make sure we don't have a compile error
      val template = engine.load(uri)
      val answer = engine.layout(uri, attributes)


      val outFile = outputDir + "/" + u
      println("Generating file: " + outFile)
      writeText(outFile, answer)
    }
    catch {
      case e: Throwable =>
        println("Failed to compile " + uri + " " + e.getMessage)
        e.printStackTrace
      /*
                for (err <- e.errors) {
                  println(err.message + " at " + err.pos + " " + err.original.message)
                }
      */
    }
  }

  /**
   * Returns the JavaScript type we should use for editing the property
   */

  def javaScriptType(prop: Property[_]): String = {
    return prop.propertyType.getName()
    /*
		val number = "number"
		val string = "string"
		val bool = "bool"
    prop.propertyType.getName() match {
			case "java.lang.Byte" => number
			case "java.lang.Short" => number
			case "java.lang.Integer" => number
			case "java.lang.Long" => number
			case "java.lang.Float" => number
			case "java.lang.Double" => number
			case "byte" => number
			case "short" => number
			case "int" => number
			case "long" => number
			case "float" => number
			case "double" => number
			case "java.util.Date" => string
			case "java.lang.String" => string
			case "boolean" => bool
			case "java.lang.Boolean" => bool
			case n: String => n
    }
    */
  }

  def isExpression(prop: Property[_]) = classOf[ExpressionDefinition].isAssignableFrom(prop.propertyType)

  def wrapLines(prop: Property[_]) = prop.name == "description"

  def findIconFileOrElse(childDir: String, name: String, elseName: => String): String =
    findIconFileOrElse(childDir, name, imageExtensions, elseName)

  /**
   * Returns the name if the file exists inside the childDir directory of the outputDir otherwise
   * return the elseName
   */
  def findIconFileOrElse(childDir: String, name: String, extensions: List[String], elseName: => String): String = {
    val subDir = new File(new File(srcDir, childDir).getCanonicalPath)
    if (!subDir.exists) println("Icon dir " + subDir.getCanonicalPath + " does not exist!")
    extensions.map(e => new File(subDir, name + "." + e)).find(_.exists) match {
      case Some(file) => file.getName
      case _ => elseName
    }
  }

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

  lazy val xmlModel: Elem = loadXmlModel()

  protected def loadXmlModel(): Elem = {
    var in = getClass.getResourceAsStream("model.xml")
    if (in == null) {
      in = new FileInputStream("src/main/resources/org/fusesource/ide/generator/model.xml")
      //throw new IllegalStateException("Could not find model.xml on the classpath in package " + getClass.getPackage.getName)
    }
    XML.load(in)
  }

  protected def loadModelTypes: Seq[NodeDefinition[_]] = {
    loadStrings("org/apache/camel/model/jaxb.index").
            map(n => createNodeDefinition(n)).
            filter(_.isProcessor)
  }

  protected def loadBaseClassAndNestedClasses: Seq[NodeDefinition[_]] = {
    val classes = scala.collection.mutable.HashSet[Class[_]]()
    classes += classOf[DescriptionDefinition]
    val ignoredTypeNames = Set("java.util.List", "java.lang.String",
      "org.apache.camel.model.language.Expression")
    for (node <- nodeDefinitions) {
      for (prop <- node.beanProperties) {
        val propType = prop.propertyType
        if (!propType.isPrimitive && node.isSimplePropertyType(prop) &&
                !ignoredTypeNames.contains(propType.getName)) {
          classes += propType
        }
        val elements = node.xmlElements(prop)
        for (el <- elements) {
          classes += el.`type`()
        }
      }
    }
    classes.map(c => NodeDefinition(c.getName, c, this)).toSeq
  }

  protected def createNodeDefinition(n: String) = NodeDefinition(n, classLoader.loadClass("org.apache.camel.model." + n), this)

  protected def loadStrings(uri: String): Seq[String] = {
    val uri = classLoader.getResource("org/apache/camel/model/jaxb.index")
    if (uri == null) {
      throw new IllegalArgumentException("Cannot find resource!")
    }
    val text = IOUtil.loadText(uri.openStream)
    text.split('\n').filter(s => s.length > 0 && !s.startsWith("#"))
  }

  protected def classLoader = getClass.getClassLoader
}

case class NodeDefinition[T](name: String, clazz: Class[T], generator: Generator) extends Logging {
  private val idx = name.lastIndexOf("Definition")

  protected val ignoreClasses = Set[Class[_]](classOf[ToDefinition])

  protected val simplePropertyTypes = Set[Class[_]](classOf[String], classOf[DescriptionDefinition], classOf[ExpressionDefinition],
    classOf[ExpressionSubElementDefinition], classOf[ju.List[_]])

  protected val ignoredProperties = Set("id", "description", "errorHandlerBuilder", "nodeFactory", "outputs", "parent", "whenClauses")

  protected val primitivePropertyTypeNames = Set[String]("java.lang.String", "java.lang.Boolean", "java.lang.Byte", "java.lang.Character",
    "java.lang.Short", "java.lang.Integer", "java.lang.Long", "java.lang.Float", "java.lang.Double", "java.util.Date")


  val definitionName = if (idx > 0) name.substring(0, idx) else name
  val id = decapitalize(definitionName)

  lazy val elementName = {
    val ann = clazz.getAnnotation[XmlRootElement](classOf[XmlRootElement])
    if (ann != null) ann.name() else null
  }

  lazy val propertyMap = Map[String, Property[_]](introspector.properties.filter(p => !p.readOnly).map(p => p.name -> p): _*)
  lazy val allPropertyMap = Map[String, Property[_]](introspector.properties.map(p => p.name -> p): _*)

  lazy val introspector = Introspector(clazz)

  lazy val fields = {
    val allFields = fieldBasedIntrospector
    clazz.getDeclaredFields.filter(f => !isStatic(f)
            && (allFields || !propertyMap.contains(f.getName)) && !hasAnnotation(f, classOf[XmlTransient])).map(f => FieldProperty(f))
  }

  protected def fieldBasedIntrospector: Boolean = {
    val annotation = clazz.getAnnotation(classOf[XmlAccessorType])
    annotation != null && annotation.value == XmlAccessType.FIELD
  }

  lazy val introspectionProperties: Seq[Property[_]] = {
    val props = if (fieldBasedIntrospector) {
      // TODO include parents settings?
      val s = clazz.getSuperclass
      val d = if (s != null && s != classOf[Object]) {
        //generator.nodeDefinitionClassMap(s).introspectionProperties
        NodeDefinition(s.getCanonicalName, s, generator).introspectionProperties
      } else {
        Seq[Property[_]]()
      }
      d ++ fields
    } else {

      def hasAnnotatedProperty(f: Field) = allPropertyMap.get(f.getName) match {
        case Some(bp: BeanProperty[_]) =>
          hasMethodAnnotations(bp, xmlAnnotations) || hasAnnotations(f, xmlAnnotations)
        case _ =>
          try {
            val name = "set" + f.getName.capitalize
            val m = clazz.getMethod(name, f.getType)
            hasAnnotations(m, xmlAnnotations)
          }
          catch {
            case e => false
          }
      }

      // only include fields where there is a property with a JAXB annotated setter
      val badProperties = clazz.getDeclaredFields.filter{
          f =>
          !isStatic(f) && hasAnnotatedProperty(f) && !propertyMap.contains(f.getName)
      }.map(f => FieldProperty(f))

      introspector.properties ++ badProperties
    }
    props.map{
        p =>
        val t = p.propertyType
        if (classOf[ExpressionSubElementDefinition].isAssignableFrom(t)) {
          ConvertedProperty(p, classOf[ExpressionDefinition])
        } else if (classOf[ju.List[_]].isAssignableFrom(t) && p.name == "expressions") {
          ConvertedProperty(p, classOf[ExpressionDefinition])
        } else {
          p
        }
    }
  }

  def simpleProperties: Seq[Property[_]] = properties.filter(p => !p.readOnly && isSimplePropertyType(p) && !(definitionName == "Route" && p.name == "inputs"))

  def complexProperties: Seq[Property[_]] = properties.filter(p => !p.readOnly && !isSimplePropertyType(p))


  /**
   *  The bean properties which are simple or expression properties excluding the common id & description properties
   */
  val beanProperties: Seq[Property[_]] =
    simpleProperties ++ complexProperties.filter(p => isBeanProperty(p))

  def properties: Seq[Property[_]] = {
    introspectionProperties.filter(n => !ignoredProperties.contains(n.name) && !isRefForUri(n) && !isTransient(n))
  }

  protected def isRefForUri(p: Property[_]): Boolean = {
    val n = p.name
    (n == "ref" && propertyMap.contains("uri")) || (n.endsWith("Ref") && propertyMap.contains(n.dropRight(3) + "Uri"))
  }

  protected def isTransient(p: Property[_]): Boolean = p match {
    case bp: BeanProperty[_] =>
      hasMethodAnnotation(bp, classOf[XmlTransient])
    case _ => false
  }

  protected def hasMethodAnnotations(bp: BeanProperty[_], cs: Seq[Class[_]]) = {
    val d = bp.descriptor
    hasAnnotations(d.getWriteMethod, cs) || hasAnnotations(d.getReadMethod, cs)
  }

  protected def hasMethodAnnotation[T <: JAnnotation](bp: BeanProperty[_], c: Class[T]) = {
    val d = bp.descriptor
    hasAnnotation(d.getWriteMethod, c) || hasAnnotation(d.getReadMethod, c)
  }

  def isRequired(p: Property[_]): Boolean = p match {
    case bp: BeanProperty[_] =>
      hasMethodAnnotation(bp, classOf[Required])
    case _ => false
  }

  def isProcessor = !Modifier.isAbstract(clazz.getModifiers) && classOf[ProcessorDefinition[_]].isAssignableFrom(clazz) &&
          !ignoreClasses.contains(clazz)

  def isExpressionNode = !Modifier.isAbstract(clazz.getModifiers) && classOf[ExpressionNode].isAssignableFrom(clazz) &&
          !ignoreClasses.contains(clazz)

  def isBeanRef(prop: Property[_]) = classOf[BeanDefinition].isAssignableFrom(clazz) && prop.name == "ref"

  def isBeanMethod(prop: Property[_]) = classOf[BeanDefinition].isAssignableFrom(clazz) && prop.name == "method"


	
	def canAcceptInput(): Boolean = {
		return CamelModelUtils.canAcceptInput(clazz.getName())
	}
	
	def canAcceptOutput(): Boolean = {
    import generator.camelContext
    return CamelModelJavaHelper.canAcceptOutput(camelContext, clazz)
	}

	def isNextSiblingStepAddedAsNodeChild(): Boolean = {
    import generator.camelContext
    return CamelModelJavaHelper.isNextSiblingStepAddedAsNodeChild(camelContext, clazz)
	}

  def title: String = {
    splitCamelCase(definitionName)
  }

  /**
   * Returns the contextId for the nodes
   */
  def documentationFile: String = childElemText(modelNode, "contextId", "allEIPs")


  // TODO extract from annotation on the definition?

  def description = title

  def tooltip: String = childElemText(modelNode, "tooltip", description)

  def propertyTooltip(name: String) = propertyElementText(name, "tooltip", name)

  def propertyLabel(name: String) = propertyElementText(name, "label", createDefaultLabel(name))

  def propertyElementText(name: String, elementName: String, defaultValue: => String) = propertiesNodes match {
    case Some(n) =>
      val c = n.child
      var pn = findElemById(c, name)
      if (pn.isEmpty) pn = findElemById(c, name.toLowerCase)
      childElemText(pn, elementName, defaultValue)
    case _ => defaultValue
  }


  /**
   * Split the string using CamelCase, then lower case each word
   *
   */
  protected def createDefaultLabel(text: String) = splitCamelCase(text).capitalize

  protected lazy val modelNode = findElemById(generator.xmlModel.child, id)

  protected lazy val propertiesNodes = modelNode match {
    case Some(n) => (n \ "properties").headOption
    case _ => None
  }

  def group: String = {
    if (isDefinitionName("bean", "log", "process", "to", "from", "endpoint")) {
      "Endpoints"
    }
    else if (isDefinitionName("aggregate", "choice", "dynamicRouter", "filter", "idempotentConsumer", "loadBalance", "multicast", "otherwise", "pipeline", "recipient", "resequence", "routing", "split", "sort", "when", "wireTap")) {
      "Routing"
    }
    else if (isDefinitionName("aOP", "catch", "delay", "finally", "intercept", "loop", "on", "rollback", "throttle", "throw", "transacted", "try")) {
      "Control Flow"
    }
    else if (isDefinitionName("convert", "enrich", "inO", "marshal", "pollEnrich", "remove", "set", "transform", "unmarshal")) {
      "Transformation"
    }
    else {
      "Miscellaneous"
    }
  }

  def role = id match {
  //case "choice" => "\"choice\", \"interaction\""
    case "choice" => "\"choice\", \"to\""
    case "when" => "\"toWhenOtherwise\", \"from\""
    case "otherwise" => "\"toWhenOtherwise\", \"from\""
    case _ => "\"from\", \"to\""
  }

  def containsRole = id match {
    case "choice" => "\"when\", \"otherwise\""
    case _ => "\"to\", \"choice\""
  }

  //def dimension = Dimensions.dimensions.get(svg).getOrElse(Dimension.doesNotExist)

  def svg = generator.findIconFileOrElse("view/", "node." + id, List("svg"),
    if (group == "Transformation") {
      "node.transform.svg"
    }
    else if (group == "Endpoints") {
      "node.endpoint.svg"
    }
    else {
      "node.generic.svg"
    })

  def defaultImageName = group match {
    case "Transformation" => "transform"
    case "Endpoints" => "endpoint"
    case _ => "generic"
  }

  def icon = generator.findIconFileOrElse("icons/", "node." + id, "node.generic.gif")


  def createNode(properties: ju.Map[String, AnyRef]): Any = {
    import generator.camelContext
    val bean = camelContext.getInjector.newInstance(clazz)

    for ((k, v) <- properties) {
      debug("  " + k + " = " + v)
      def ignoreValue = debug("Ignored key: " + k + " value: " + v + " since it is blank")

      introspector.property(k) match {
        case Some(p) =>
          v match {
            case "" => ignoreValue
            case None => ignoreValue
            case null => ignoreValue

            case _ =>
              debug("    setting property: " + p + " to: " + v)
              val convertedValue = if (classOf[ExpressionDefinition].isAssignableFrom(p.propertyType)) {
                var lang = getOrElse(properties.get("language").asInstanceOf[String], "XPath").toLowerCase
                println("====== attempt to set an expression to: " + v + " for lang: " + lang)
                new LanguageExpression(lang, v.toString)
              } else {
                camelContext.getTypeConverter.mandatoryConvertTo(p.propertyType, v)
              }
              p.set(bean, convertedValue)
              debug("    property is now: " + p(bean) + " after evaluating to " + convertedValue)
          }

        case _ =>
          warn("No such property: " + k + " in " + this)
      }
    }
    println("Created: " + bean)
    bean

  }

  def propertyType[T](p: Property[_]): String = {
    val name = p.propertyType.getSimpleName
    if (name.startsWith("java.lang.")) {
      name.substring("java.lang.".length)
    } else {
      name
    }
  }

  def getterExpression[T](source: String, p: Property[_]): String = {
    val prefix = if (isPrimitiveBooleanPropertyType(p)) "is" else "get"
    val method = prefix + p.name.capitalize
    try {
      clazz.getMethod(method)

      p match {
        case cp: ConvertedProperty[_] =>
          source + "." + method + "()"
        case _ =>
          source + "." + method + "()"
      }
    }
    catch {
      case e =>
      // lets use reflection if no method
        val pn = propertyType(p) match {
          case "boolean" => "Boolean"
          case "byte" => "Byte"
          case "short" => "Short"
          case "int" => "Integer"
          case "long" => "Long"
          case "float" => "Float"
          case "double" => "Double"
          case s => s
        }
        "Objects.<" + pn + ">getField(" + source + ", \"" + p.name + "\")"
    }
  }

  def setterExpression[T](source: String, p: Property[_], value: String): String = {
    val method = "set" + p.name.capitalize
    try {
      clazz.getMethod(method, p.propertyType)

      p match {
        case cp: ConvertedProperty[_] =>
          source + "." + method + "(" + value + ")"
        case _ =>
          source + "." + method + "(" + value + ")"
      }
    }
    catch {
      case e =>
      // lets use reflection if no method
        "Objects.setField(" + source + ", \"" + p.name + "\", " + value + ")"
    }
  }


  def getOrIsMethodPrefix[T](p: Property[_]): String =
    if (isPrimitiveBooleanPropertyType(p)) "is" else "get"

  def isPrimitiveBooleanPropertyType(p: Property[_]): Boolean =
    p.propertyType.getSimpleName == "boolean"

  def isEnumPropertyType(p: Property[_]): Boolean =
    classOf[Enum[_]].isAssignableFrom(p.propertyType)

  def isBeanProperty(p: Property[_]): Boolean =  {
    def valid = !classOf[WhenDefinition].isAssignableFrom(p.propertyType) &&
            !classOf[OtherwiseDefinition].isAssignableFrom(p.propertyType) &&
            !classOf[Class[_]].isAssignableFrom(p.propertyType)

    def hasXmlAnnotatedField(n: String) = {
      field(n) match {
        case Some(f) => hasAnnotations(f.field, xmlAnnotations) && valid
        case _ => false
      }
    }

    isExpression(p) || classOf[RedeliveryPolicyDefinition].isAssignableFrom(p.propertyType) ||
            classOf[BatchResequencerConfig].isAssignableFrom(p.propertyType) ||
            classOf[StreamResequencerConfig].isAssignableFrom(p.propertyType) ||
            (hasXmlAnnotatedField(p.name) && valid)
  }

  def xmlElements(p: Property[_]): Array[XmlElement] = {
    field(p.name) match {
      case Some(fp) =>
        val ann = fp.field.getAnnotation(classOf[XmlElements])
        if (ann != null) {
          ann.value
        } else {
          Array()
        }
      case _ => Array()
    }
  }

  def field(n: String) = fields.find(f => f.name == n)

  def isListPropertyType(p: Property[_]): Boolean =
    classOf[ju.List[_]].isAssignableFrom(p.propertyType)

  def isBooleanPropertyType(p: Property[_]): Boolean =
    isPrimitiveBooleanPropertyType(p) || p.propertyType.getCanonicalName == "java.lang.Boolean"

  def isSimplePropertyType(p: Property[_]) = {
    val t = p.propertyType
    simplePropertyTypes.contains(t) || (eclipseMode &&
            (t.isPrimitive || primitivePropertyTypeNames.contains(t.getCanonicalName) || classOf[Enum[_]].isAssignableFrom(t)))
  }

  def isExpression(p: Property[_]): Boolean = {
    classOf[ExpressionDefinition].isAssignableFrom(p.propertyType)
  }

  protected def isDefinitionName(prefixes: String*): Boolean =
    prefixes.find(p => id.startsWith(p)).isDefined
}


case class FieldProperty[T](field: Field) extends Property[T] {
  def name = field.getName

  def propertyType = field.getType

  def readOnly = false

  def optional = false

  def label = name

  def description = name

  def evaluate(instance: T) = field.get(instance)

  def set(instance: T, value: Any) = field.set(instance, value)

  override def toString = "FieldProperty(" + name + ": " + propertyType.getName + ")"
}

case class ConvertedProperty[T](property: Property[T], propertyType: Class[_]) extends Property[T] {
  def name = property.name

  def actualPropertyType = property.propertyType

  def readOnly = property.readOnly

  def optional = property.optional

  def label = property.label

  def description = property.description

  def evaluate(instance: T) = property.evaluate(instance)

  def set(instance: T, value: Any) = property.set(instance, value)

  override def toString = "ConvertedProperty(" + actualPropertyType + ": " + property + ")"
}