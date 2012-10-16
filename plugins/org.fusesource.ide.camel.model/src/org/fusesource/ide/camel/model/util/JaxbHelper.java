package org.fusesource.ide.camel.model.util;

import java.lang.reflect.AnnotatedElement;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;

public class JaxbHelper {

	/**
	 * Is method based reflection (rather than field based) used?
	 */
	public static boolean useMethodReflection(Class<?> aClass) {
		XmlAccessorType accessorType = aClass.getAnnotation(XmlAccessorType.class);
		boolean useMethods = true;
		if (accessorType != null) {
			if (accessorType.value().equals(XmlAccessType.FIELD)) {
				useMethods = false;
			}
		}
		return useMethods;
	}

	public static boolean hasXmlAnnotation(AnnotatedElement e) {
		return e.getAnnotation(XmlElement.class) != null || e.getAnnotation(XmlAttribute.class) != null || e.getAnnotation(XmlElementRef.class) != null;
	}

}
