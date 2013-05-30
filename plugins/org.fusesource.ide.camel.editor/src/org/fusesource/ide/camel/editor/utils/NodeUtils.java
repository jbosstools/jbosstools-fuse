/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.camel.editor.utils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.apache.camel.model.ExpressionNode;
import org.apache.camel.model.language.ExpressionDefinition;
import org.apache.camel.spi.Required;
import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.generated.Aggregate;
import org.fusesource.ide.camel.model.generated.ConvertBody;
import org.fusesource.ide.camel.model.generated.Log;
import org.fusesource.ide.camel.model.generated.Process;
import org.fusesource.ide.camel.model.generated.RemoveHeader;
import org.fusesource.ide.camel.model.generated.RemoveProperty;
import org.fusesource.ide.camel.model.generated.Resequence;
import org.fusesource.ide.camel.model.generated.SetHeader;
import org.fusesource.ide.camel.model.generated.SetOutHeader;
import org.fusesource.ide.camel.model.generated.SetProperty;


/**
 * @author lhein
 */
public class NodeUtils {

	public static String getPropertyName(final Object id) {
		String propertyName = id.toString();
		int idx = propertyName.indexOf('.');
		if (idx > 0) {
			propertyName = propertyName.substring(idx + 1);
		}
		propertyName = propertyName.substring(0, 1).toLowerCase() + propertyName.substring(1);
		return propertyName;
	}

	public static boolean isMandatory(Object bean, String propertyName) {
		// lets look at the setter method and see if its got a @Required
		// annotation
		if (bean instanceof AbstractNode) {
			AbstractNode node = (AbstractNode) bean;
			Class<?> camelClass = node.getCamelDefinitionClass();
			if (camelClass != null) {
				XmlAccessorType accessorType = camelClass.getAnnotation(XmlAccessorType.class);
				boolean useMethods = true;
				if (accessorType != null) {
					if (accessorType.value().equals(XmlAccessType.FIELD)) {
						useMethods = false;
					}
				}
				try {
					BeanInfo beanInfo = Introspector.getBeanInfo(camelClass);
					PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
					if (propertyDescriptors != null) {
						for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
							if (propertyName.equals(propertyDescriptor.getName())) {
								Method writeMethod = propertyDescriptor.getWriteMethod();
								if (writeMethod != null) {
									Required annotation = writeMethod.getAnnotation(Required.class);
									if (annotation != null) {
										return true;
									}
									if (useMethods) {
										XmlElement element = writeMethod.getAnnotation(XmlElement.class);
										if (element != null && element.required()) {
											return true;
										}
										XmlAttribute attribute = writeMethod.getAnnotation(XmlAttribute.class);
										if (attribute != null && attribute.required()) {
											return true;
										}
									}
								}
								break;
							}
						}
					}
					if (!useMethods) {
						Field[] fields = camelClass.getDeclaredFields();
						for (Field field : fields) {
							if (propertyName.equals(field.getName())) {
								Required annotation = field.getAnnotation(Required.class);
								if (annotation != null) {
									return true;
								}
								XmlElement element = field.getAnnotation(XmlElement.class);
								if (element != null && element.required()) {
									return true;
								}
								XmlAttribute attribute = field.getAnnotation(XmlAttribute.class);
								if (attribute != null && attribute.required()) {
									return true;
								}
							}
						}
					}
				} catch (IntrospectionException e) {
					// ignore
				}
			}
		}

		// expression is mandatory on resequence
		if (bean instanceof Resequence && "expression".equals(propertyName)) {
			return true;
		}

		// lets make all URI properties mandatory by default to avoid complex
		// validation with ref v uri
		boolean answer = ("uri".equals(propertyName) || propertyName.endsWith("Uri"))
				|| (bean instanceof Aggregate && "strategyRef".equals(propertyName))
				|| (bean instanceof ConvertBody && "type".equals(propertyName))
				|| (bean instanceof ExpressionDefinition && isMandatoryExpression(((AbstractNode)bean)))
				|| (bean instanceof Log && "message".equals(propertyName))
				|| (bean instanceof Process && "ref".equals(propertyName))
				|| (bean instanceof RemoveHeader && "headerName".equals(propertyName))
				|| (bean instanceof RemoveProperty && "propertyName".equals(propertyName))
				|| (bean instanceof SetHeader && "headerName".equals(propertyName))
				|| (bean instanceof SetOutHeader && "headerName".equals(propertyName))
				|| (bean instanceof SetProperty && "propertyName".equals(propertyName));
		return answer;
	}

	public static boolean isMandatoryExpression(AbstractNode node) {
		// is this expression mandatory?
		Class<?> camelClass = node.getCamelDefinitionClass();
		return ExpressionNode.class.isAssignableFrom(camelClass);
	}
	
}
