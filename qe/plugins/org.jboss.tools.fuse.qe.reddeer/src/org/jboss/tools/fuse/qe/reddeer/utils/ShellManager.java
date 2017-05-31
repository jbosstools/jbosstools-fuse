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
package org.jboss.tools.fuse.qe.reddeer.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.jboss.reddeer.common.logging.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * Facilitates SSH access and command execution
 * 
 * @author tsedmik
 */
public class ShellManager {

	private Session session = null;
	private Channel channel = null;
	private static Logger log = Logger.getLogger(ShellManager.class);

	/**
	 * Creates an instance of ShellManager and establishes a session to given host
	 * 
	 * @param user
	 *            username
	 * @param password
	 *            suitable password for given username
	 * @param host
	 *            IP address of host device
	 * @param port
	 *            Port number
	 * 
	 * @throws JSchException
	 *             In case something is wrong (username+password, connection establishing, ...)
	 */
	public ShellManager(String user, String password, String host, int port) throws JSchException {

		log.debug("Establishing connection...");
		JSch jsch = new JSch();
		session = jsch.getSession(user, host, port);
		session.setPassword(password);
		session.setConfig("StrictHostKeyChecking", "no");
		session.connect(30000);
		log.debug("Connection established");
	}

	/**
	 * Executes given command. Execution is synchronous - Method waits until reaction to given command is complete
	 * (InputStream is closed).
	 * 
	 * @param command
	 *            Command that is sent to the host for execution
	 * @return The host's reaction to given command
	 * 
	 * @throws JSchException
	 *             In case something is wrong with command execution
	 * @throws IOException
	 *             In case something is wrong with reading reaction to given command
	 */
	public String execute(String command) throws JSchException, IOException {

		StringBuilder output = new StringBuilder(1000);

		log.debug("Openig channel ...");
		channel = session.openChannel("exec");
		((ChannelExec) channel).setCommand(command);
		channel.setInputStream(null);
		((ChannelExec) channel).setErrStream(System.err);
		InputStream in = channel.getInputStream();
		log.debug("Executig command '" + command + "'...");
		channel.connect();
		byte[] tmp = new byte[1024];
		while (true) {
			while (in.available() > 0) {
				int i = in.read(tmp, 0, 1024);
				if (i < 0)
					break;
				output.append(new String(tmp, 0, i));
			}
			if (channel.isClosed()) {
				if (in.available() > 0)
					continue;
				output.append("exit-status: " + channel.getExitStatus());
				break;
			}
			try {
				Thread.sleep(1000);
			} catch (Exception ee) {
			}
		}
		log.debug("Execution done!");
		channel.disconnect();
		log.debug("Channel is closed");

		return output.toString();
	}

	/**
	 * Executes given command. Execution is asynchronous - Command execution is performed in a new thread.
	 * 
	 * @param command
	 *            Command that is sent to the host for execution
	 * @param out
	 *            OutputStream that contains reactions to given command
	 */
	public void asyncExecute(final String command, final OutputStream out) {

		Thread t = new Thread(new Runnable() {

			public void run() {
				Channel channel = null;
				try {
					log.debug("Openig channel ...");
					channel = session.openChannel("exec");
					((ChannelExec) channel).setCommand(command);
					channel.setInputStream(null);
					((ChannelExec) channel).setErrStream(System.err);
					InputStream in = channel.getInputStream();
					log.debug("Executig given command ...");
					channel.connect();
					int d;
					while ((d = in.read()) != -1) {
						out.write(d);
					}
					log.debug("Execution done!");
					out.close();
				} catch (Exception e) {

				} finally {
					channel.disconnect();
					log.debug("Channel is closed");
				}
			}
		});
		t.setDaemon(true);
		t.start();
	}

	/**
	 * Closes session. Should be called after services of ServiceManager is no longer needed.
	 */
	public void close() {
		log.debug("Closing connection...");
		if (session != null)
			session.disconnect();
		if (channel != null)
			channel.disconnect();
		log.debug("Connection closed");
	}
}
