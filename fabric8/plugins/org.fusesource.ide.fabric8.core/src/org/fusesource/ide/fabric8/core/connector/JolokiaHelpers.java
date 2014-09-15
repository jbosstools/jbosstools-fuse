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
package org.fusesource.ide.fabric8.core.connector;

import java.util.ArrayList;
import java.util.List;

import javax.management.MalformedObjectNameException;

import org.jolokia.client.request.J4pExecRequest;
import org.jolokia.client.request.J4pReadRequest;
import org.jolokia.client.request.J4pWriteRequest;

/**
 * @author lhein
 */
public class JolokiaHelpers {
	
	/**
	 * embeds one or more arguments into a list
	 * 
	 * @param args	the arguments to put in list
	 * @return	the list
	 */
	public static <T> List<T> toList(T... args) {
        List<T> rc = new ArrayList<T>();
        for (T arg : args) {
            rc.add(arg);
        }
        return rc;
    }
	
	/**
	 * creates an exec request 
	 * 
	 * @param mbean		the mbean object name url
	 * @param operation	the operation signature
	 * @param args		the arguments
	 * @return			the request object
	 * @throws MalformedObjectNameException	if the url of the mbean was malformed
	 */
	public static J4pExecRequest createExecRequest(String mbean, String operation, Object... args) throws MalformedObjectNameException {
        J4pExecRequest rc = new J4pExecRequest(mbean, operation, args);
        rc.setPreferredHttpMethod("POST");
        return rc;
    }
	
	/**
	 * creates a write request
	 * 
	 * @param mbean		the mbean object name url
	 * @param attribute	the attribute to write
	 * @param value		the value to write
	 * @return			the write request
	 * @throws MalformedObjectNameException	if the url of the mbean was malformed
	 */
	public static J4pWriteRequest createWriteRequest(String mbean, String attribute, Object value) throws MalformedObjectNameException {
        J4pWriteRequest answer = new J4pWriteRequest(mbean, attribute, value);
        answer.setPreferredHttpMethod("POST");
        return answer;
    }
	
	public static J4pReadRequest createReadRequest(String mbean, String attribute) throws MalformedObjectNameException {
        J4pReadRequest answer = null;
        if (attribute == null || attribute.toString().trim().length() < 1) {
            answer = new J4pReadRequest(mbean);
        } else {
            answer = new J4pReadRequest(mbean, attribute);
        }
        answer.setPreferredHttpMethod("POST");
        return answer;
    }
}
