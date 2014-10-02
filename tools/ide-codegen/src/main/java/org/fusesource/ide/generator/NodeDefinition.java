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
package org.fusesource.ide.generator;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.camel.model.DescriptionDefinition;
import org.apache.camel.model.ExpressionSubElementDefinition;
import org.apache.camel.model.OtherwiseDefinition;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.RedeliveryPolicyDefinition;
import org.apache.camel.model.ToDefinition;
import org.apache.camel.model.WhenDefinition;
import org.apache.camel.model.config.BatchResequencerConfig;
import org.apache.camel.model.config.StreamResequencerConfig;
import org.apache.camel.model.language.ExpressionDefinition;
import org.fusesource.ide.generator.model.Node;
import org.fusesource.scalate.introspector.BeanIntrospector;
import org.fusesource.scalate.introspector.BeanProperty;
import org.fusesource.scalate.introspector.Introspector;
import org.fusesource.scalate.introspector.Property;
import scala.collection.Iterator;

public class NodeDefinition<T> {

    private String name;
    private Class<T> clazz;
    private Generator generator;

    private String elementName;
    private String definitionName;
    private String id;

    private Introspector<T> introspector;

    private List<FieldProperty<T>> fields;
    private Map<String, Property<?>> propertyMap;
    private Map<String, Property<?>> allPropertyMap;
    private List<Property<?>> introspectionProperties;

    private Node modelNode;

    protected Set<String> primitivePropertyTypeNames = new HashSet<>(Arrays.asList(
            "java.lang.String", "java.lang.Boolean", "java.lang.Byte", "java.lang.Character",
            "java.lang.Short", "java.lang.Integer", "java.lang.Long", "java.lang.Float", "java.lang.Double", "java.util.Date"
    ));

    protected Set<String> ignoredProperties = new HashSet<>(Arrays.asList(
            "id", "description", "errorHandlerBuilder", "nodeFactory", "outputs", "parent", "whenClauses"
    ));

    protected Set<Class<?>> simplePropertyTypes = new HashSet<>(Arrays.asList(
            String.class, DescriptionDefinition.class, ExpressionDefinition.class,
            ExpressionSubElementDefinition.class, List.class
    ));

    protected Set<Class<?>> ignoreClasses = new HashSet<Class<?>>(Arrays.asList(
            ToDefinition.class
    ));

    public NodeDefinition(String name, Class<T> clazz, Generator generator) {
        this.name = name;
        this.clazz = clazz;
        this.generator = generator;

        XmlRootElement ann = clazz.getAnnotation(XmlRootElement.class);
        this.elementName = ann != null ? ann.name() : null;

        int idx = this.name.lastIndexOf("Definition");
        this.definitionName = idx > 0 ? name.substring(0, idx) : name;
        this.id = decapitalize(definitionName);

        this.introspector = new BeanIntrospector<>(clazz);

        this.modelNode = Generator.findElemById(generator.getXmlModel().nodes, getId());
    }

    public String decapitalize(String text) {
        return java.beans.Introspector.decapitalize(text);
    }

    public String capitalize(String text) {
        if (!(text == null || text.length() == 0)) {
            return text.substring(0, 1).toUpperCase() + text.substring(1);
        }
        return text;
    }

    public String splitCamelCase(String text) {
        StringBuilder buffer = new StringBuilder();
        char last = 'A';
        for (char c: text.toCharArray()) {
            if (Character.isLowerCase(last) && Character.isUpperCase(c)) {
                buffer.append(" ");
            }
            buffer.append(c);
            last = c;
        }
        return buffer.toString();
    }

    public boolean isProcessor() {
        return !Modifier.isAbstract(clazz.getModifiers()) &&
                ProcessorDefinition.class.isAssignableFrom(clazz) &&
                !ignoreClasses.contains(clazz);
    }

    protected boolean fieldBasedIntrospector() {
        XmlAccessorType annotation = clazz.getAnnotation(XmlAccessorType.class);
        return annotation != null && annotation.value() == XmlAccessType.FIELD;
    }

    public Map<String, Property<?>> getPropertyMap() {
        if (propertyMap == null) {
            propertyMap = new LinkedHashMap<>();
            // Scala collection -> Java collection
            Iterator<Property<T>> it = introspector.properties().toList().toIterator();
            while (it.hasNext()) {
                Property<T> p = it.next();
                if (!p.readOnly()) {
                    propertyMap.put(p.name(), p);
                }
            }
        }
        return propertyMap;
    }

    public Map<String, Property<?>> getAllPropertyMap() {
        if (allPropertyMap == null) {
            allPropertyMap = new LinkedHashMap<>();
            Iterator<Property<T>> it = introspector.properties().toList().toIterator();
            while (it.hasNext()) {
                Property<T> p = it.next();
                allPropertyMap.put(p.name(), p);
            }
        }
        return allPropertyMap;
    }

    public List<FieldProperty<T>> getFields() {
        if (fields == null) {
            boolean allFields = fieldBasedIntrospector();
            fields = new LinkedList<>();
            for (Field f : clazz.getDeclaredFields()) {
                if (Reflections.isStatic(f)) {
                    continue;
                }
                if (!allFields && getPropertyMap().containsKey(f.getName())) {
                    continue;
                }
                if (Reflections.hasAnnotation(f, XmlTransient.class)) {
                    continue;
                }
                fields.add(new FieldProperty<T>(f));
            }
        }
        return fields;
    }

    public List<Property<?>> getIntrospectionProperties() {
        if (introspectionProperties == null) {
            List<Property<?>> props;
            if (fieldBasedIntrospector()) {
                // TODO include parents settings?
                Class<? super T> s = clazz.getSuperclass();
                List<Property<?>> d = new LinkedList<>();
                if (s != null && s != Object.class) {
//                    d.addAll(generator.getNodeDefinitionClassMap().get(s).getIntrospectionProperties());
                    d.addAll(new NodeDefinition<>(s.getCanonicalName(), s, generator).getIntrospectionProperties());
                }
                d.addAll(getFields());
                props = d;
            } else {
                // only include fields where there is a property with a JAXB annotated setter
                List<Property<?>> badProperties = new LinkedList<>();
                for (Field f : clazz.getDeclaredFields()) {
                    if (Reflections.isStatic(f) || !hasAnnotatedProperty(f) || getPropertyMap().containsKey(f.getName())) {
                        continue;
                    }
                    badProperties.add(new FieldProperty(f));
                }
                props = new LinkedList<>();
                Iterator<Property<T>> it = introspector.properties().toList().toIterator();
                while (it.hasNext()) {
                    Property<T> p = it.next();
                    props.add(p);
                }
                props.addAll(badProperties);
            }
            introspectionProperties = new LinkedList<>();
            for (Property<?> p : props) {
                Class<?> t = p.propertyType();
                if (ExpressionSubElementDefinition.class.isAssignableFrom(t)) {
                    introspectionProperties.add(new ConvertedProperty<>(p, ExpressionDefinition.class));
                } else if (List.class.isAssignableFrom(t) && p.name().equals("expressions")) {
                    introspectionProperties.add(new ConvertedProperty<>(p, ExpressionDefinition.class));
                } else {
                    introspectionProperties.add(p);
                }
            }
        }
        return introspectionProperties;
    }

    private boolean hasAnnotatedProperty(Field f) {
        BeanProperty<?> property = (BeanProperty<?>) getAllPropertyMap().get(f.getName());
        if (property != null) {
            return hasMethodAnnotations(property, Generator.XML_ANNOTATIONS) ||
                    Reflections.hasAnnotations(f, Generator.XML_ANNOTATIONS);
        } else {
            try {
                String name = "set" + capitalize(f.getName());
                Method m = clazz.getMethod(name, f.getType());
                return Reflections.hasAnnotations(m, Generator.XML_ANNOTATIONS);
            } catch (Exception e) {
                return false;
            }
        }
    }

    protected boolean hasMethodAnnotations(BeanProperty<?> bp, Class[] cs) {
        PropertyDescriptor d = bp.descriptor();
        return Reflections.hasAnnotations(d.getWriteMethod(), cs) || Reflections.hasAnnotations(d.getReadMethod(), cs);
    }

    public List<Property<?>> properties() {
        List<Property<?>> props = new LinkedList<>();
        for (Property<?> n : getIntrospectionProperties()) {
            if (!ignoredProperties.contains(n.name()) && !isRefForUri(n) && !isTransient(n)) {
                props.add(n);
            }
        }
        return props;
    }

    protected boolean isRefForUri(Property<?> p) {
        String n = p.name();
        return (n.equals("ref") && getPropertyMap().containsKey("uri")) ||
                n.endsWith("Ref") && getPropertyMap().containsKey(n.substring(0, n.length() - 3) + "Uri");
    }

    protected boolean isTransient(Property<?> p) {
        return p instanceof BeanProperty && hasMethodAnnotation((BeanProperty<?>) p, XmlTransient.class);
    }

    protected <A extends Annotation> boolean hasMethodAnnotation(BeanProperty<?> bp, Class<A> c) {
        PropertyDescriptor d = bp.descriptor();
        return Reflections.hasAnnotation(d.getWriteMethod(), c) || Reflections.hasAnnotation(d.getReadMethod(), c);
    }

    public List<Property<?>> simpleProperties() {
        List<Property<?>> props = new LinkedList<>();
        for (Property<?> p : properties()) {
            if (!p.readOnly() && isSimplePropertyType(p) && !(definitionName.equals("Route") && p.name().equals("inputs"))) {
                props.add(p);
            }
        }
        return props;
    }

    public boolean isSimplePropertyType(Property<?> p) {
        Class<?> t = p.propertyType();
        return simplePropertyTypes.contains(t) || (Generator.eclipseMode &&
                (t.isPrimitive() || primitivePropertyTypeNames.contains(t.getCanonicalName()) || Enum.class.isAssignableFrom(t)));
    }

    public List<Property<?>> complexProperties() {
        List<Property<?>> props = new LinkedList<>();
        for (Property<?> p : properties()) {
            if (!p.readOnly() && !isSimplePropertyType(p)) {
                props.add(p);
            }
        }
        return props;
    }

    /**
     * The bean properties which are simple or expression properties excluding the common id & description properties
     *
     * @return
     */
    public List<Property<?>> beanProperties() {
        List<Property<?>> props = new LinkedList<>();
        props.addAll(simpleProperties());
        for (Property<?> p : complexProperties()) {
            if (isBeanProperty(p)) {
                props.add(p);
            }
        }
        return props;
    }

    public boolean isBeanProperty(Property<?> p) {
        boolean valid = !WhenDefinition.class.isAssignableFrom(p.propertyType()) &&
                !OtherwiseDefinition.class.isAssignableFrom(p.propertyType()) &&
                !Class.class.isAssignableFrom(p.propertyType());


        return isExpression(p) || RedeliveryPolicyDefinition.class.isAssignableFrom(p.propertyType()) ||
                BatchResequencerConfig.class.isAssignableFrom(p.propertyType()) ||
                StreamResequencerConfig.class.isAssignableFrom(p.propertyType()) ||
                (hasXmlAnnotatedField(p.name(), valid) && valid);
    }

    private boolean hasXmlAnnotatedField(String n, boolean valid) {
        FieldProperty<T> field = field(n);
        return field != null && Reflections.hasAnnotations(field.getField(), Generator.XML_ANNOTATIONS) && valid;
    }

    private FieldProperty<T> field(String n) {
        for (FieldProperty<T> field : getFields()) {
            if (field.name().equals(n)) {
                return field;
            }
        }
        return null;
    }

    public boolean isExpression(Property<?> p) {
        return ExpressionDefinition.class.isAssignableFrom(p.propertyType());
    }

    public XmlElement[] xmlElements(Property p) {
        FieldProperty<T> fp = field(p.name());
        if (fp != null) {
            XmlElements ann = fp.getField().getAnnotation(XmlElements.class);
            if (ann != null) {
                return ann.value();
            } else {
                return new XmlElement[0];
            }
        } else {
            return new XmlElement[0];
        }
    }

    /* getters & setters */

    public String getElementName() {
        return elementName;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return getTitle();
    }

    public String getTitle() {
        return splitCamelCase(definitionName);
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public String getDefinitionName() {
        return definitionName;
    }

    public String getName() {
        return name;
    }

    public Introspector<T> getIntrospector() {
        return introspector;
    }

    public String tooltip() {
        return modelNode == null || modelNode.tooltip == null ? getDescription() : modelNode.tooltip;
    }

    public String propertyTooltip(String name) {
        return propertyElementTooltip(name, name);
    }

    public String propertyElementTooltip(String name, String defaultValue) {
        String text = defaultValue;
        if (modelNode != null) {
            for (org.fusesource.ide.generator.model.Property p : modelNode.properties) {
                if (p.id != null && (p.id.equals(name) || p.id.toLowerCase().equals(name.toLowerCase())) && p.tooltip != null) {
                    text = p.tooltip;
                }
            }
        }
        return text;
    }

    public String propertyElementLabel(String name) {
        String text = createDefaultLabel(name);
        if (modelNode != null) {
            for (org.fusesource.ide.generator.model.Property p : modelNode.properties) {
                if (p.id != null && (p.id.equals(name) || p.id.toLowerCase().equals(name.toLowerCase())) && p.label != null) {
                    text = p.label;
                }
            }
        }
        return text;
    }

    public String propertyLabel(String name) {
        return propertyElementLabel(name);
    }

    /**
     * Split the string using CamelCase, then lower case each word
     * @param text
     * @return
     */
    protected String createDefaultLabel(String text) {
        return capitalize(splitCamelCase(text));
    }

    public String propertyType(Property<?> p) {
        String name = p.propertyType().getSimpleName();
        if (name.startsWith("java.lang.")) {
            return name.substring("java.lang.".length());
        } else {
            return name;
        }
    }

    protected boolean isDefinitionName(String ... prefixes) {
        for (String p : prefixes) {
            if (getId().startsWith(p)) {
                return true;
            }
        }
        return false;
    }

    public String group() {
        if (isDefinitionName("bean", "log", "process", "to", "from", "endpoint")) {
            return "Components";
        }
        else if (isDefinitionName("aggregate", "choice", "dynamicRouter", "filter", "idempotentConsumer", "loadBalance", "multicast", "otherwise", "pipeline", "recipient", "resequence", "routing", "split", "sort", "when", "wireTap")) {
            return "Routing";
        }
        else if (isDefinitionName("aOP", "catch", "delay", "finally", "intercept", "loop", "on", "rollback", "throttle", "throw", "transacted", "try")) {
            return "Control Flow";
        }
        else if (isDefinitionName("convert", "enrich", "inO", "marshal", "pollEnrich", "remove", "set", "transform", "unmarshal")) {
            return "Transformation";
        }
        else {
            return "Miscellaneous";
        }
    }

    public String defaultImageName() {
        switch (group()) {
            case "Transformation":
                return "transform";
            case "Endpoints":
                return "endpoint";
            default:
                return "generic";
        }
    }

    /**
     * Returns the contextId for the nodes
     * @return
     */
    public String documentationFile() {
        return modelNode == null || modelNode.contextId == null ? "allEIPs" : modelNode.contextId;
    }

    public boolean isPrimitiveBooleanPropertyType(Property<?> p) {
        return p.propertyType().getSimpleName().equals("boolean");
    }

    public String getOrIsMethodPrefix(Property<?> p) {
        return isPrimitiveBooleanPropertyType(p) ? "is" : "get";
    }

    public boolean isBooleanPropertyType(Property<?> p) {
        return isPrimitiveBooleanPropertyType(p) || p.propertyType().getCanonicalName().equals("java.lang.Boolean");
    }

    public boolean isEnumPropertyType(Property<?> p) {
        return Enum.class.isAssignableFrom(p.propertyType());
    }

    public boolean isListPropertyType(Property<?> p) {
        return List.class.isAssignableFrom(p.propertyType());
    }

    public String getterExpression(String source, Property<?> p) {
        String prefix = isPrimitiveBooleanPropertyType(p) ? "is" : "get";
        String method = prefix + capitalize(p.name());
        try {
            Method m = clazz.getMethod(method);
            return source + "." + method + "()";
        } catch (Exception e) {
            // lets use reflection if no method
            String pn;
            switch (propertyType(p)) {
                case "boolean":
                    pn = "Boolean";
                    break;
                case "byte":
                    pn = "Byte";
                    break;
                case "short":
                    pn = "Short";
                    break;
                case "long":
                    pn = "Long";
                    break;
                case "int":
                    pn = "Integer";
                    break;
                case "float":
                    pn = "Float";
                    break;
                case "double":
                    pn = "Double";
                    break;
                default:
                    pn = propertyType(p);
            }
            return "Objects.<" + pn + ">getField(" + source + ", \"" + p.name() + "\")";
        }
    }

    public String setterExpression(String source, Property<?> p, String value) {
        String method = "set" + capitalize(p.name());
        try {
            clazz.getMethod(method, p.propertyType());
            return source + "." + method + "(" + value + ")";
        } catch (Exception e) {
            // lets use reflection if no method
            return "Objects.setField(" + source + ", \"" + p.name() + "\", " + value + ")";
        }
    }

}

/*
    def isRequired(p: Property[_]): Boolean = p match {
        case bp: BeanProperty[_] =>
            hasMethodAnnotation(bp, classOf[Required])
        case _ => false
    }


    def isExpressionNode = !Modifier.isAbstract(clazz.getModifiers) && classOf[ExpressionNode].isAssignableFrom(clazz) &&
            !ignoreClasses.contains(clazz)

    def isBeanRef(prop: Property[_]) = classOf[BeanDefinition].isAssignableFrom(clazz) && prop.name == "ref"

    def isBeanMethod(prop: Property[_]) = classOf[BeanDefinition].isAssignableFrom(clazz) && prop.name == "method"



    def canAcceptInput(): Boolean = {
        return isProcessor && CamelModelUtils.canAcceptInput(clazz.getName())
    }

    def canAcceptOutput(): Boolean = {
        import generator.camelContext
        return isProcessor && CamelModelJavaHelper.canAcceptOutput(camelContext, clazz)
    }

    def isNextSiblingStepAddedAsNodeChild(): Boolean = {
        import generator.camelContext
        return isProcessor && CamelModelJavaHelper.isNextSiblingStepAddedAsNodeChild(camelContext, clazz)
    }




    // TODO extract from annotation on the definition?










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
                        var lang = (new Objects()).getOrElse(properties.get("language").asInstanceOf[String], "XPath").toLowerCase
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
}

*/
