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

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;

import org.eclipse.draw2d.geometry.Point;
import org.jboss.reddeer.common.logging.Logger;
import org.jboss.reddeer.core.util.Display;
import org.jboss.reddeer.common.wait.AbstractWait;
import org.jboss.reddeer.common.wait.TimePeriod;

/**
 * Utilizes mouse operations via AWT Robot
 * 
 * @author tsedmik
 */
public class MouseAWTManager {

	private static Logger log = Logger.getLogger(MouseAWTManager.class);

	public static void AWTMouseMove(final int x, final int y) {
		Display.syncExec(new Runnable() {

			@Override
			public void run() {

				Robot robot;
				try {
					robot = new Robot();
					robot.mouseMove(x, y);
					robot.delay(1000);
				} catch (AWTException e) {
					log.error("Error during AWT Robot manipulation");
				}
			}
		});
		AbstractWait.sleep(TimePeriod.SHORT);
	}

	public static void AWTMouseMoveFromTo(final Point from, final Point to) {
		Display.syncExec(new Runnable() {

			@Override
			public void run() {

				Robot robot;
				try {
					robot = new Robot();

					// horizontal move
					if (from.x < to.x) {
						int i = from.x;
						while (i < to.x) {
							i += 1;
							robot.mouseMove(i, from.y);
						}
					} else {
						int i = from.x;
						while (i > to.x) {
							i -= 1;
							robot.mouseMove(i, from.y);
						}
					}

					// vertical move
					if (from.y < to.y) {
						int i = from.y;
						while (i < to.y) {
							i += 1;
							robot.mouseMove(to.x, i);
						}
					} else {
						int i = from.y;
						while (i > to.y) {
							i -= 1;
							robot.mouseMove(to.x, i);
						}
					}
				} catch (AWTException e) {
					log.error("Error during AWT Robot manipulation");
				}
			}
		});
		AbstractWait.sleep(TimePeriod.SHORT);
	}

	public static void AWTMousePress() {
		doMouseInputEvent(InputEvent.BUTTON1_MASK, true);
		AbstractWait.sleep(TimePeriod.SHORT);
	}

	public static void AWTMouseRelease() {
		doMouseInputEvent(InputEvent.BUTTON1_MASK, false);
		AbstractWait.sleep(TimePeriod.SHORT);
	}

	private static void doMouseInputEvent(final int event, final boolean press) {
		Display.syncExec(new Runnable() {

			@Override
			public void run() {

				Robot robot;
				try {
					robot = new Robot();
					if (press) {
						robot.mousePress(event);
					} else {
						robot.mouseRelease(event);
					}
					robot.delay(2000);
				} catch (AWTException e) {
					log.error("Error during AWT Robot manipulation");
				}
			}
		});
	}
}
