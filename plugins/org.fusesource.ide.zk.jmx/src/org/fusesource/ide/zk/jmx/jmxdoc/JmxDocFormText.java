/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.fusesource.ide.zk.jmx.jmxdoc;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.ui.forms.widgets.FormText;

import java.util.List;

/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public final class JmxDocFormText {

    public static final String FONT_CODE_KEY = "code";
    public static final String FONT_H1_KEY = "H1";
    public static final String FONT_H3_KEY = "H3";
    public static final String INDENT = "    ";
    public static final String HEADING_ATTRIBUTE_DETAIL = "Attribute Detail";
    public static final String HEADING_OPERATION_DETAIL = "Operation Detail";
    public static final String HEADING_PARAMETERS = "Parameters:";
    public static final String HEADING_RETURNS = "Returns:";

    public static void initFormText(FormText formText) {

        formText.setWhitespaceNormalized(false);

        Font formTextFont = formText.getFont();
        FontData formTextFontData = formTextFont.getFontData()[0];

        FontData h1FontData = new FontData(formTextFontData.getName(), formTextFontData.getHeight() + 5, SWT.BOLD);
        final Font h1Font = new Font(formTextFont.getDevice(), h1FontData);
        formText.setFont(FONT_H1_KEY, h1Font);

        FontData h3FontData = new FontData(formTextFontData.getName(), formTextFontData.getHeight() + 3, SWT.BOLD);
        final Font h3Font = new Font(formTextFont.getDevice(), h3FontData);
        formText.setFont(FONT_H3_KEY, h3Font);

        Font codeFont = JFaceResources.getTextFont();
        formText.setFont(FONT_CODE_KEY, codeFont);

        formText.addDisposeListener(new DisposeListener() {

            @Override
            public void widgetDisposed(DisposeEvent e) {
                h1Font.dispose();
                h3Font.dispose();
            }
        });

        // Set fontKeySet = JFaceResources.getFontRegistry().getKeySet();
        // if (fontKeySet != null) {
        // for (Object fontKey : fontKeySet) {
        // System.out.println(fontKey);
        // }
        // }

    }

    public static String getFormTextHeader(MBeanFeatureDoc<?> featureDoc) {
        StringBuilder jmxdoc = new StringBuilder();

        jmxdoc.append("<p>");
        jmxdoc.append("<span font=\"" + FONT_H3_KEY + "\">");
        jmxdoc.append(featureDoc.getName());
        jmxdoc.append("</span>");
        jmxdoc.append("</p>");

        return jmxdoc.toString();
    }

    public static String getFormTextDescription(MBeanFeatureDoc<?> featureDoc) {
        StringBuilder jmxdoc = new StringBuilder();

        String name = featureDoc.getRawName();
        String description = featureDoc.getDescription();
        if (description != null && !description.equals(name) && !description.trim().isEmpty()) {
            jmxdoc.append("<p>");
            jmxdoc.append(INDENT);
            jmxdoc.append(description);
            jmxdoc.append("</p>");
        }

        return jmxdoc.toString();
    }

    public static String getFormTextSignature(MBeanOperationDoc operationDoc) {

        StringBuilder jmxdoc = new StringBuilder();

        String name = operationDoc.getName();
        String returnType = operationDoc.getReturnType().getName();

        jmxdoc.append("<p>");
        jmxdoc.append("<span font=\"" + FONT_CODE_KEY + "\">");
        jmxdoc.append(returnType).append(" ").append(name).append("(");

        // Signature Parameters

        List<MBeanParameterDoc> parameters = operationDoc.getParameters();
        int parameterCount = parameters.size();
        for (int i = 0; i < parameterCount; i++) {
            MBeanParameterDoc parameter = parameters.get(i);
            String parameterType = parameter.getType().getName();
            String parameterName = parameter.getName();
            jmxdoc.append(parameterType).append(" ").append(parameterName);

            if (i < parameterCount - 1) {
                jmxdoc.append(", ");
            }
        }

        jmxdoc.append(")");
        jmxdoc.append("</span>");
        jmxdoc.append("</p>");

        return jmxdoc.toString();
    }

    public static String getFormTextSignature(MBeanAttributeDoc attributeDoc) {
        StringBuilder jmxdoc = new StringBuilder();

        String name = attributeDoc.getName();
        String returnType = attributeDoc.getType().getName();

        jmxdoc.append("<p>");
        jmxdoc.append("<span font=\"" + FONT_CODE_KEY + "\">");
        jmxdoc.append(returnType).append(" ").append(name).append("()");
        jmxdoc.append("</span>");
        jmxdoc.append("</p>");

        return jmxdoc.toString();
    }

    public static String getFormTextParameters(MBeanOperationDoc operationDoc) {
        StringBuilder jmxdoc = new StringBuilder();

        List<MBeanParameterDoc> parameters = operationDoc.getParameters();
        int parameterCount = parameters.size();

        if (parameterCount > 0) {
            jmxdoc.append("<p>");
            jmxdoc.append(INDENT);
            jmxdoc.append("<b>");
            jmxdoc.append(HEADING_PARAMETERS);
            jmxdoc.append("</b>");
            jmxdoc.append("</p>");

            jmxdoc.append("<p>");

            for (int i = 0; i < parameterCount; i++) {
                MBeanParameterDoc parameter = parameters.get(i);

                String parameterName = parameter.getName();
                String parameterDescription = parameter.getDescription();

                jmxdoc.append(INDENT);
                jmxdoc.append(INDENT);
                jmxdoc.append("<span font=\"" + FONT_CODE_KEY + "\">");
                jmxdoc.append(parameterName);
                jmxdoc.append("</span>");
                jmxdoc.append(" - ").append(parameterDescription);

                if (i < parameterCount - 1) {
                    jmxdoc.append("<br/>");
                }
            }

            jmxdoc.append("</p>");
        }

        return jmxdoc.toString();
    }

    public static String getFormTextReturns(JmxType returnType) {
        StringBuilder jmxdoc = new StringBuilder();

        String returnTypeString = returnType.getName();

        if (returnTypeString != null && !returnTypeString.equals("void")) {
            jmxdoc.append("<p>");
            jmxdoc.append(INDENT);
            jmxdoc.append("<b>");
            jmxdoc.append(HEADING_RETURNS);
            jmxdoc.append("</b>");
            jmxdoc.append("</p>");

            jmxdoc.append("<p>");

            jmxdoc.append(INDENT);
            jmxdoc.append(INDENT);
            jmxdoc.append("<span font=\"" + FONT_CODE_KEY + "\">");
            jmxdoc.append(returnTypeString);
            jmxdoc.append("</span>");
            jmxdoc.append("<br/>");
            jmxdoc.append("</p>");
        }

        return jmxdoc.toString();
    }

    public static String getFormText(MBeanDoc mbeanDoc) {
        StringBuilder jmxdoc = new StringBuilder();

        jmxdoc.append("<form>");

        List<MBeanAttributeDoc> attributes = mbeanDoc.getAttributes();
        int attributeCount = attributes.size();

        if (attributeCount > 0) {
            jmxdoc.append("<p>");
            jmxdoc.append("<span font=\"" + FONT_H1_KEY + "\">");
            jmxdoc.append(HEADING_ATTRIBUTE_DETAIL);
            jmxdoc.append("</span>");
            jmxdoc.append("</p>");

            // jmxdoc.append("<p>");

            for (int i = 0; i < attributeCount; i++) {
                MBeanAttributeDoc attributeDoc = attributes.get(i);

                jmxdoc.append(getFormTextHeader(attributeDoc));
                jmxdoc.append(getFormTextSignature(attributeDoc));
                jmxdoc.append(getFormTextDescription(attributeDoc));
                jmxdoc.append(getFormTextReturns(attributeDoc.getType()));

                if (i < attributeCount - 1) {
                    jmxdoc.append("<br/>");
                }
            }

            // jmxdoc.append("</p>");
        }

        List<MBeanOperationDoc> operations = mbeanDoc.getOperations();
        int operationCount = operations.size();

        if (operationCount > 0) {

            if (attributeCount > 0) {

            }

            jmxdoc.append("<p>");
            jmxdoc.append("<span font=\"" + FONT_H1_KEY + "\">");
            jmxdoc.append(HEADING_OPERATION_DETAIL);
            jmxdoc.append("</span>");
            jmxdoc.append("</p>");

            // jmxdoc.append("<p>");

            for (int i = 0; i < operationCount; i++) {
                MBeanOperationDoc operationDoc = operations.get(i);

                jmxdoc.append(getFormTextHeader(operationDoc));
                jmxdoc.append(getFormTextSignature(operationDoc));
                jmxdoc.append(getFormTextDescription(operationDoc));
                jmxdoc.append(getFormTextParameters(operationDoc));
                jmxdoc.append(getFormTextReturns(operationDoc.getReturnType()));

                if (i < operationCount - 1) {
                    jmxdoc.append("<br/>");
                }
            }

            // jmxdoc.append("</p>");
        }

        jmxdoc.append("</form>");

        return jmxdoc.toString();
    }

    public static String getFormText(MBeanFeatureDoc<?> featureDoc, boolean includeHeader) {
        if (featureDoc instanceof MBeanOperationDoc) {
            return getFormText((MBeanOperationDoc) featureDoc, includeHeader);
        }
        else if (featureDoc instanceof MBeanAttributeDoc) {
            return getFormText((MBeanAttributeDoc) featureDoc, includeHeader);
        }

        return "<form></form>";
    }

    public static String getFormText(MBeanAttributeDoc attributeDoc, boolean includeHeader) {
        StringBuilder jmxdoc = new StringBuilder();

        jmxdoc.append("<form>");

        if (includeHeader) {
            jmxdoc.append(getFormTextHeader(attributeDoc));
        }

        jmxdoc.append(getFormTextSignature(attributeDoc));
        jmxdoc.append(getFormTextDescription(attributeDoc));
        jmxdoc.append(getFormTextReturns(attributeDoc.getType()));

        jmxdoc.append("</form>");

        return jmxdoc.toString();
    }

    public static String getFormText(MBeanOperationDoc operationDoc, boolean includeHeader) {
        StringBuilder jmxdoc = new StringBuilder();

        jmxdoc.append("<form>");

        if (includeHeader) {
            jmxdoc.append(getFormTextHeader(operationDoc));
        }

        jmxdoc.append(getFormTextSignature(operationDoc));
        jmxdoc.append(getFormTextDescription(operationDoc));
        jmxdoc.append(getFormTextParameters(operationDoc));
        jmxdoc.append(getFormTextReturns(operationDoc.getReturnType()));

        jmxdoc.append("</form>");

        return jmxdoc.toString();
    }

}
