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

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;

import org.eclipse.swt.widgets.Composite;
import org.jboss.reddeer.common.logging.Logger;
import org.jboss.reddeer.eclipse.jdt.ui.ProjectExplorer;
import org.jboss.reddeer.swt.api.TreeItem;
import org.jboss.reddeer.swt.impl.tree.DefaultTreeItem;
import org.jboss.tools.fuse.qe.reddeer.view.FuseJMXNavigator;
import org.jboss.reddeer.core.util.Display;
import org.jboss.reddeer.core.util.ResultRunnable;
import org.jboss.reddeer.common.wait.AbstractWait;
import org.jboss.reddeer.common.wait.TimePeriod;

/**
 * Utilizes sending messages into the Camel Endpoint - drag a message XML file in 'Project Explorer' and drop it on a
 * Camel Endpoint in 'JMX Navigator' View.
 * 
 * @author tsedmik
 */
public class TracingDragAndDropManager {

	private Logger log = Logger.getLogger(TracingDragAndDropManager.class);
	private String[] from = null;
	private String[] to = null;

	/**
	 * Constructor
	 * 
	 * @param from
	 *            path in 'Project Explorer' view to a XML message file
	 * @param to
	 *            path in 'JMX Navigator' view to an endpoint
	 */
	public TracingDragAndDropManager(String[] from, String[] to) {

		this.from = from;
		this.to = to;
	}

	/**
	 * Performs Drag&Drop operations via AWT Robot
	 */
	public void performDragAndDrop() {

		if (from == null || to == null) {
			log.error("Attribute 'From' or 'To' is null!");
			return;
		}

		final Point fromCoords = getCoords(getFromTreeItem());
		final Point toCoords = getCoords(getToTreeItem());
		Display.syncExec(new Runnable() {

			@Override
			public void run() {

				Robot robot;
				try {
					robot = new Robot();
					robot.mouseMove(fromCoords.x, fromCoords.y);
					robot.delay(100);
					robot.mousePress(InputEvent.BUTTON1_MASK);
					robot.delay(10);
					int i = fromCoords.y;
					if (fromCoords.y > toCoords.y) {
						while (i > toCoords.y) {
							i -= 1;
							robot.mouseMove(fromCoords.x, i);
						}
					} else {
						while (i < toCoords.y) {
							i += 1;
							robot.mouseMove(fromCoords.x, i);
						}
					}
					i = fromCoords.x;
					while (i < toCoords.x) {
						i += 1;
						robot.mouseMove(i, toCoords.y);
					}
				} catch (AWTException e) {
					log.error("Error during AWT Robot manipulation");
				}
			}
		});
		AbstractWait.sleep(TimePeriod.SHORT);
		Display.syncExec(new Runnable() {

			@Override
			public void run() {

				Robot robot;
				try {
					robot = new Robot();
					robot.mouseRelease(InputEvent.BUTTON1_MASK);
					robot.delay(2000);
				} catch (AWTException e) {
					log.error("Error during AWT Robot manipulation");
				}
			}
		});
	}

	/**
	 * Tries to access an item in 'Project Explorer' view
	 * 
	 * @return item corresponding with given path
	 */
	private TreeItem getFromTreeItem() {

		log.debug("Tries to access 'From' item: " + from);
		new ProjectExplorer().open();
		return new DefaultTreeItem(from);
	}

	/**
	 * Tries to access an item in 'JMX Navigator' view
	 * 
	 * @return item corresponding with given path
	 */
	private TreeItem getToTreeItem() {

		log.debug("Tries to access 'To' item: " + from);
		FuseJMXNavigator jmx = new FuseJMXNavigator();
		jmx.open();
		jmx.setShouldCollapseLocalProcesses(false);
		return jmx.getNode(to);
	}

	/**
	 * Retrieves coordinates of given element
	 * 
	 * @param item
	 *            an item.
	 * @return absolute coordinates of given element (x + 10, y + 10)
	 */
	private Point getCoords(TreeItem item) {

		item.select();
		final org.eclipse.swt.widgets.TreeItem widget = item.getSWTWidget();
		return Display.syncExec(new ResultRunnable<Point>() {

			@Override
			public Point run() {

				int x = widget.getBounds().x;
				int y = widget.getBounds().y;
				Composite parent = widget.getParent();
				x += parent.toDisplay(1, 1).x;
				y += parent.toDisplay(1, 1).y;
				return new Point(x + 10, y + 10);
			}
		});
	}
}
