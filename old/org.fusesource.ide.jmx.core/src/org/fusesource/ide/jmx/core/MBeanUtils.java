/*******************************************************************************
 * Copyright (c) 2006 Jeff Mesnil
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

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

package org.fusesource.ide.jmx.core;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;

import org.fusesource.ide.jmx.core.util.StringUtils;


public class MBeanUtils {
    public static Object[] getParameters(String[] textParams,
            MBeanParameterInfo[] params) throws ClassNotFoundException {
        if (textParams == null || params == null) {
            return null;
        }
        Object[] ret = new Object[textParams.length];
        for (int i = 0; i < ret.length && i < params.length; i++) {
            MBeanParameterInfo param = params[i];
            String texti = textParams[i];
            if (texti.length() == 0) {
                ret[i] = null;
            } else if (param.getType().equals("byte")) { //$NON-NLS-1$
                ret[i] = new Byte(texti);
            } else if (param.getType().equals("short")) {//$NON-NLS-1$
                ret[i] = new Short(texti);
            } else if (param.getType().equals("java.lang.Short")) {//$NON-NLS-1$
                ret[i] = new Short(texti);
            } else if (param.getType().equals("int")) {//$NON-NLS-1$
                ret[i] = new Integer(texti);
            } else if (param.getType().equals("java.lang.Integer")) {//$NON-NLS-1$
                ret[i] = new Integer(texti);
            } else if (param.getType().equals("long")) {//$NON-NLS-1$
                ret[i] = new Long(texti);
            } else if (param.getType().equals("java.lang.Long")) {//$NON-NLS-1$
                ret[i] = new Long(texti);
            } else if (param.getType().equals("float")) {//$NON-NLS-1$
                ret[i] = new Float(texti);
            } else if (param.getType().equals("java.lang.Float")) {//$NON-NLS-1$
                ret[i] = new Float(texti);
            } else if (param.getType().equals("double")) {//$NON-NLS-1$
                ret[i] = new Double(texti);
            } else if (param.getType().equals("java.lang.Double")) {//$NON-NLS-1$
                ret[i] = new Double(texti);
            } else if (param.getType().equals("char")) {//$NON-NLS-1$
                ret[i] = new Character(texti.charAt(0));
            } else if (param.getType().equals("boolean")) {//$NON-NLS-1$
                ret[i] = new Boolean(texti);
            } else if (MBeanUtils.class
                    .getClassLoader()
                    .loadClass("java.lang.Number").isAssignableFrom(MBeanUtils.class.getClassLoader().loadClass(param.getType()))) {//$NON-NLS-1$
                ret[i] = createNumber(texti);
            } else {
                ret[i] = texti;
            }
        }
        return ret;
    }

    public static Object getValue(String valueStr, String type)
            throws ClassNotFoundException {
        if (valueStr == null || type == null) {
            return null;
        }
        if (type.equals("byte")) //$NON-NLS-1$
            return new Byte(valueStr);
        if (type.equals("short")) //$NON-NLS-1$
            return new Short(valueStr);
        if (type.equals("java.lang.Short")) //$NON-NLS-1$
            return new Short(valueStr);
        if (type.equals("int")) //$NON-NLS-1$
            return new Integer(valueStr);
        if (type.equals("java.lang.Integer")) //$NON-NLS-1$
            return new Integer(valueStr);
        if (type.equals("long")) //$NON-NLS-1$
            return new Long(valueStr);
        if (type.equals("java.lang.Long")) //$NON-NLS-1$
            return new Long(valueStr);
        if (type.equals("float")) //$NON-NLS-1$
            return new Float(valueStr);
        if (type.equals("java.lang.Float")) //$NON-NLS-1$
            return new Float(valueStr);
        if (type.equals("double")) //$NON-NLS-1$
            return new Double(valueStr);
        if (type.equals("java.lang.Double")) //$NON-NLS-1$
            return new Double(valueStr);
        if (type.equals("char")) //$NON-NLS-1$
            return new Character(valueStr.charAt(0));
        if (type.equals("boolean")) //$NON-NLS-1$
            return new Boolean(valueStr);
        if (MBeanUtils.class.getClassLoader().loadClass("java.lang.Number") //$NON-NLS-1$
                .isAssignableFrom(
                        MBeanUtils.class.getClassLoader().loadClass(type)))
            return createNumber(valueStr);

        return valueStr;
    }

    public static Number createNumber(String val) {
        try {
            return new Byte(val);
        } catch (NumberFormatException e) {
        }
        try {
            return new BigDecimal(val);
        } catch (NumberFormatException e) {
        }
        try {
            return new BigInteger(val);
        } catch (NumberFormatException e) {
        }
        return null;
    }

    public static String prettySignature(MBeanOperationInfo opInfo) {
        StringBuffer sig = new StringBuffer(opInfo.getName());
        MBeanParameterInfo[] params = opInfo.getSignature();
        sig.append('(');
        for (int i = 0; i < params.length; i++) {
            if (i > 0) {
                sig.append(", "); //$NON-NLS-1$
            }
            MBeanParameterInfo param = params[i];
            sig.append(StringUtils.toString(param.getType(), false));
        }
        sig.append(')');
        return sig.toString();
    }
}
