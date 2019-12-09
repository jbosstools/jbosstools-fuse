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
package org.jboss.tools.fuse.reddeer.requirement;

/**
 * Runner for managing local Camel route.
 * 
 * @author Andrej Podhradsky (apodhrad@redhat.com)
 * 
 */
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.eclipse.reddeer.common.condition.WaitCondition;
import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.jboss.tools.fuse.reddeer.utils.JarRunner;

public class CamelExampleRunner extends JarRunner {

	public static final Pattern IS_STARTED_PATTERN = Pattern.compile(".*Apache Camel .* started in .*");
	public static final Pattern IS_SUSPENDED_PATTERN = Pattern.compile(".*Apache Camel .* is suspended in .*");
	public static final Pattern IS_RESUMED_PATTERN = Pattern.compile(".*Apache Camel .* resumed in .*");

	private CamelExampleOutput output;

	public CamelExampleRunner(File jarFile) {
		this(jarFile, false);
	}

	public CamelExampleRunner(File jarFile, File jolokiaJarFile) {
		this(jarFile, false);
	}

	public CamelExampleRunner(File jarFile, boolean useJolokia) {
		super(jarFile);
		output = new CamelExampleOutput();
		setOutput(output);

		registerPattern(IS_STARTED_PATTERN);
		registerPattern(IS_SUSPENDED_PATTERN);
		registerPattern(IS_RESUMED_PATTERN);
	}

	@Override
	public void run(String... args) {
		super.run(args);
		waitForOutputWithPattern(IS_STARTED_PATTERN);
	}

	@Override
	public void stop() {
		super.stop();
	}

	public void registerPattern(Pattern pattern) {
		output.registerPattern(pattern);
	}

	public void waitForOutputWithPattern(Pattern pattern) {
		new WaitUntil(new CamelExampleOutputContainsRegex(output, pattern), TimePeriod.LONG);
	}

	private class CamelExampleOutput implements Appendable {

		public static final int LIMIT = 1000;

		private List<String> lines;
		private Set<Pattern> patternSet;
		private Set<Pattern> patternFound;

		public CamelExampleOutput() {
			lines = new LinkedList<>();
			patternSet = new HashSet<>();
			patternFound = new HashSet<>();
		}

		public void registerPattern(Pattern pattern) {
			patternSet.add(pattern);
		}

		public boolean isPatternFound(Pattern pattern) {
			return patternFound.contains(pattern);
		}

		public List<String> getLines() {
			return Collections.unmodifiableList(lines);
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			getLines().forEach(line -> builder.append(line + System.getProperty("line.separator")));
			return builder.toString();
		}

		@Override
		public Appendable append(CharSequence csq) throws IOException {
			lines.add(csq.toString());
			if (lines.size() > LIMIT) {
				lines.remove(0);
			}
			for (Pattern pattern : patternSet) {
				if (pattern.matcher(csq).matches()) {
					patternFound.add(pattern);
				}
			}
			return this;
		}

		@Override
		public Appendable append(CharSequence csq, int start, int end) throws IOException {
			if (csq != null) {
				csq = csq.subSequence(start, end);
			}
			return append(csq);
		}

		@Override
		public Appendable append(char c) throws IOException {
			return append(String.valueOf(c));
		}

	}

	private class CamelExampleOutputContainsRegex implements WaitCondition {

		private CamelExampleOutput output;
		private Pattern pattern;

		public CamelExampleOutputContainsRegex(CamelExampleOutput output, Pattern pattern) {
			this.output = output;
			this.pattern = pattern;
		}

		@Override
		public boolean test() {
			return output.isPatternFound(pattern);
		}

		@SuppressWarnings("unchecked")
		@Override
		public String getResult() {
			return output.toString();
		}

		@Override
		public String description() {
			return "output contains " + pattern;
		}

		@Override
		public String errorMessageWhile() {
			return "No line with " + pattern + " was found\n" + getResult();
		}

		@Override
		public String errorMessageUntil() {
			return "No line with " + pattern + " was found\n" + getResult();
		}

	}
}
