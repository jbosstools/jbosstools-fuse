/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.reddeer.utils;

import static java.util.Arrays.stream;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Andrej Podhradsky (apodhrad@redhat.com)
 *
 */
public class JarRunner {

	private File jarFile;
	private Process process;
	private StreamGobbler input;
	private Appendable output;
	private String javaAgent;
	private Map<String, String> systemProperties;

	public JarRunner(File jarFile) {
		this.jarFile = jarFile;
		this.systemProperties = new HashMap<>();
	}

	public File getJarFile() {
		return jarFile;
	}

	public void setJavaAgent(String javaAgent) {
		this.javaAgent = javaAgent;
	}

	public void setSystemProperty(String key, String value) {
		systemProperties.put(key, value);
	}

	public void setOutput(Appendable output) {
		this.output = output;
	}

	public Appendable getOutput() {
		return output;
	}

	public void run(String... args) {
		ProcessBuilder pb = new ProcessBuilder(getCommand(args));
		pb.redirectErrorStream(true);
		try {
			process = pb.start();
		} catch (IOException ioe) {
			throw new JarRunnerException("Cannot start the process", ioe);
		}
		input = new StreamGobbler(process.getInputStream(), output);
		input.start();
	}

	public boolean isRunning() {
		return input != null && input.isAlive();
	}

	public void stop() {
		if (!isRunning()) {
			throw new JarRunnerException("The process is not running");
		}
		input.interrupt();
		try {
			input.join();
		} catch (InterruptedException ie) {
			throw new JarRunnerException("Cannot stop the process", ie);
		}
		process.destroy();
	}

	protected List<String> getCommand(String... args) {
		List<String> command = new ArrayList<>();
		command.add(System.getProperty("java.home") + "/bin/java");
		if (javaAgent != null) {
			command.add("-javaagent:" + javaAgent);
		}
		systemProperties.forEach((key, value) -> command.add("-D" + key + "=" + value));
		command.add("-jar");
		command.add(jarFile.getAbsolutePath());
		stream(args).forEach(arg -> command.add(arg));
		return command;
	}

	private class StreamGobbler extends Thread {

		private final InputStream is;
		private Appendable output;

		private StreamGobbler(InputStream is, Appendable output) {
			this.is = is;
			this.output = output;
		}

		@Override
		public void run() {
			try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
				String line;
				while (!isInterrupted() && (line = br.readLine()) != null) {
					if (output != null) {
						System.out.println(line);
						output.append(line);
					} else {
						System.out.println(line);
					}
				}
			} catch (IOException ioe) {
				throw new JarRunnerException(ioe);
			}
		}

	}

}
