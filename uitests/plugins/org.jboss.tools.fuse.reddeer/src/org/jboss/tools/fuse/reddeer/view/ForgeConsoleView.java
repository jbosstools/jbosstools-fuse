/******************************************************************************* 
 * Copyright (c) 2018 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.fuse.reddeer.view;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.reddeer.common.condition.AbstractWaitCondition;
import org.eclipse.reddeer.common.logging.Logger;
import org.eclipse.reddeer.common.matcher.RegexMatcher;
import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.common.wait.WaitWhile;
import org.eclipse.reddeer.core.matcher.WithTooltipTextMatcher;
import org.eclipse.reddeer.swt.exception.SWTLayerException;
import org.eclipse.reddeer.swt.impl.menu.ToolItemMenu;
import org.eclipse.reddeer.swt.impl.styledtext.DefaultStyledText;
import org.eclipse.reddeer.swt.impl.toolbar.DefaultToolItem;
import org.eclipse.reddeer.workbench.core.condition.JobIsRunning;
import org.eclipse.reddeer.workbench.impl.toolbar.ViewToolBar;
import org.eclipse.reddeer.workbench.impl.view.WorkbenchView;
import org.jboss.tools.fuse.reddeer.ForgeOption;

/**
 * Forge Console view RedDeer implementation
 * 
 * @author psrna
 *
 */
public class ForgeConsoleView extends WorkbenchView {

	protected final static Logger log = Logger.getLogger(ForgeConsoleView.class);

	public ForgeConsoleView() {
		super("Forge Console");
	}

	/**
	 * Start currently selected Forge Runtime Call {@link #selectRuntime(RegexMatcher)} if there is a need to start
	 * different runtime as the one that is currently selected.
	 */
	public void start() {

		log.info("Attempt to START Forge Runtime");
		open();
		ViewToolBar tb = new ViewToolBar();

		RegexMatcher rm = new RegexMatcher("Start.*");
		WithTooltipTextMatcher tm = new WithTooltipTextMatcher(rm);
		new DefaultToolItem(tb, 0, tm).click();

		new WaitWhile(new JobIsRunning(), TimePeriod.VERY_LONG);
	}

	/**
	 * Stop Forge Console
	 */
	public void stop() {

		log.info("Attempt to STOP Forge Runtime");
		open();
		ViewToolBar tb = new ViewToolBar();

		RegexMatcher rm = new RegexMatcher("Stop.*");
		WithTooltipTextMatcher tm = new WithTooltipTextMatcher(rm);
		new DefaultToolItem(tb, 0, tm).click();

		new WaitWhile(new JobIsRunning());
	}

	/**
	 * Clear Forge Console
	 */
	public void clear() {

		log.info("Attempt to CLEAR Forge Runtime");
		open();
		ViewToolBar tb = new ViewToolBar();

		WithTooltipTextMatcher tm = new WithTooltipTextMatcher("Clear Console");
		new DefaultToolItem(tb, 0, tm).click();
	}

	/**
	 * Select Forge Runtime
	 * 
	 * @param rm
	 *               RegexMatcher witch matches Forge Runtime version. e.g. "Forge 2.*" or "Forge 1.*"
	 */
	@SuppressWarnings("unchecked")
	public void selectRuntime(RegexMatcher rm) {

		log.info("Attempt to SELECT Forge Runtime");
		open();
		ViewToolBar tb = new ViewToolBar();

		WithTooltipTextMatcher tm = new WithTooltipTextMatcher("Select Forge Runtime");
		DefaultToolItem defaultToolItem = new DefaultToolItem(tb, 0, tm);

		new ToolItemMenu(defaultToolItem).getItem(rm);
	}

	/**
	 * Returns console test.
	 * 
	 * @return Console text
	 */
	public String getConsoleText() {
		activate();
		new WaitUntil(new ConsoleHasTextWidget());
		return new DefaultStyledText().getText();
	}

	/**
	 * Tries to retrieve a collection of options.
	 * For example 'Project type' --> war, fuse, jar, ...
	 * 
	 * @return a collection of the last displayed options, an empty collection otherwise
	 */
	public List<ForgeOption> getOptions() {
		String consoleText = getConsoleText();
		consoleText = consoleText.substring(consoleText.lastIndexOf("[0]"));
		List<ForgeOption> options = new ArrayList<>();
		for (String line : consoleText.split("\n")) {
			if (line.equals("")) break; // end of options detections
			int optionNumber = Character.getNumericValue(line.charAt(1));
			boolean defaultValue = line.charAt(5) == 'x' ? true : false;
			String name = line.substring(8);
			options.add(new ForgeOption(optionNumber, defaultValue, name));
		}
		return options;
	}

	/**
	 * Sets console text
	 * 
	 * @author psrna
	 *
	 */
	public void setConsoleText(String text) {
		activate();
		new WaitUntil(new ConsoleHasTextWidget());
		new DefaultStyledText().setText(text);
	}

	/**
	 * Toggle button Link With Editor in Forge Console View.
	 * 
	 * @author jkopriva@redhat.com
	 */
	public void linkWithEditorToggle(boolean toggle) {
		open();
		ViewToolBar tb = new ViewToolBar();
		RegexMatcher rm = new RegexMatcher("Link.*");
		WithTooltipTextMatcher tm = new WithTooltipTextMatcher(rm);
		new DefaultToolItem(tb, 0, tm).toggle(toggle);
	}

	private class ConsoleHasTextWidget extends AbstractWaitCondition {

		@Override
		public boolean test() {
			try {
				new DefaultStyledText();
			} catch (SWTLayerException ex) {
				return false;
			}
			return true;
		}

		@Override
		public String description() {
			return "console has styled text";
		}
	}
}