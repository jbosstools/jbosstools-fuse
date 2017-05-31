/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.qe.reddeer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 
 * @author apodhrad
 *
 */
public class HttpClient {

	private String url;

	public HttpClient(String url) {
		this.url = url;
	}

	public String get() throws MalformedURLException, IOException {
		StringBuffer response = new StringBuffer();
		HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
		con.setRequestMethod("GET");
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String line = null;
		while ((line = in.readLine()) != null) {
			response.append(line);
		}
		in.close();
		return response.toString();
	}

	public String post(String data) throws MalformedURLException, IOException {
		StringBuffer response = new StringBuffer();
		HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
		con.setRequestMethod("POST");
		if (data != null) {
			con.setDoOutput(true);
			con.getOutputStream().write(data.getBytes());
		}
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String line = null;
		while ((line = in.readLine()) != null) {
			response.append(line);
		}
		in.close();
		return response.toString();
	}
}
