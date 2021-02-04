/*******************************************************************************
 * Copyright (c) 2021 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.ui.bot.tests.utils;

import java.util.stream.Stream;

import org.eclipse.reddeer.common.condition.AbstractWaitCondition;
import org.eclipse.reddeer.eclipse.ui.problems.Problem;
import org.eclipse.reddeer.eclipse.ui.views.markers.ProblemsView;
import org.eclipse.reddeer.eclipse.ui.views.markers.ProblemsView.ProblemType;
import org.jboss.tools.fuse.ui.bot.tests.ProblemsViewTest;

public class FuseToolingProblems extends AbstractWaitCondition {
	
	private int numberOfExpectedProblems;

	public FuseToolingProblems(int numberOfExpectedProblems) {
		this.numberOfExpectedProblems = numberOfExpectedProblems;
	}

	@Override
	public boolean test() {
		return numberOfExpectedProblems == getFuseToolingProblems().count();
	}

	@Override
	public String description() {
		return "Fuse Tooling Validation should discover "+numberOfExpectedProblems+" problems! There are " + getFuseToolingProblems() + " problems.";
	}
	
	private Stream<Problem> getFuseToolingProblems() {
		ProblemsView view = new ProblemsView();
		view.open();
		return view.getProblems(ProblemType.ALL).stream()
				.filter(problem -> ProblemsViewTest.FUSE_TOOLING_PROBLEM_TYPE.equals(problem.getType()));
	}
}
