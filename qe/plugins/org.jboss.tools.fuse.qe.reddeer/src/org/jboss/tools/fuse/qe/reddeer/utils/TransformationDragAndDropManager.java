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
import org.jboss.reddeer.swt.api.TableItem;
import org.jboss.reddeer.swt.api.TreeItem;
import org.jboss.reddeer.swt.impl.table.DefaultTableItem;
import org.jboss.reddeer.swt.impl.tree.DefaultTree;
import org.jboss.reddeer.swt.impl.tree.DefaultTreeItem;
import org.jboss.reddeer.core.util.Display;
import org.jboss.reddeer.core.util.ResultRunnable;
import org.jboss.reddeer.common.wait.AbstractWait;
import org.jboss.reddeer.common.wait.TimePeriod;
import org.jboss.reddeer.swt.widgets.Widget;

/**
 * Utilizes Drag&Drop items in Data Transformation Editor to create a transformation
 * 
 * @author tsedmik
 */
public class TransformationDragAndDropManager {

	private Logger log = Logger.getLogger(TransformationDragAndDropManager.class);

	/**
	 * Performs Drag&Drop operations via AWT Robot
	 */
	public void performDragAndDrop(String[] from, String[] to) {

		final Point fromCoords = getCoords(getFromTreeItem(from));
		final Point toCoords = getCoords(getToTreeItem(to));
		doDragAndDrop(fromCoords, toCoords);
	}

	public void performVariableDragAndDrop(String from, String[] to) {

		final Point fromCoords = getCoords(getFromTableItem(from));
		final Point toCoords = getCoords(getToTreeItemVariable(to));
		doDragAndDrop(fromCoords, toCoords);
	}

	private TreeItem getFromTreeItem(String[] from) {

		log.debug("Tries to access 'From' item: " + from);
		return new DefaultTreeItem(new DefaultTree(0), from);
	}

	private TreeItem getToTreeItem(String[] to) {

		log.debug("Tries to access 'To' item: " + to);
		boolean isFound = false;
		for (TreeItem item : new DefaultTree(1).getAllItems()) {
			if (isFound) {
				item.select();
				break;
			}
			if (item.getText().equals(to[to.length - 1])) {
				isFound = true;
				continue;
			}
		}
		return new DefaultTreeItem(new DefaultTree(1), to);
	}

	private TreeItem getToTreeItemVariable(String[] to) {

		log.debug("Tries to access 'To' item: " + to);
		boolean isFound = false;
		for (TreeItem item : new DefaultTree().getAllItems()) {
			if (isFound) {
				item.select();
				break;
			}
			if (item.getText().equals(to[to.length - 1])) {
				isFound = true;
				continue;
			}
		}
		return new DefaultTreeItem(new DefaultTree(), to);
	}

	private TableItem getFromTableItem(String name) {

		return new DefaultTableItem(name);
	}

	private Point getCoords(Widget item) {

		if (item instanceof TreeItem) {
			TreeItem temp = (TreeItem) item;
			temp.select();
			final org.eclipse.swt.widgets.TreeItem widget = temp.getSWTWidget();
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
		} else {
			TableItem temp = (TableItem) item;
			temp.select();
			final org.eclipse.swt.widgets.TableItem widget = temp.getSWTWidget();
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

	private void doDragAndDrop(final Point fromCoords, final Point toCoords) {

		Display.syncExec(new Runnable() {

			@Override
			public void run() {

				Robot robot;
				try {
					robot = new Robot();
					robot.mouseMove(fromCoords.x, fromCoords.y);
					robot.mousePress(InputEvent.BUTTON1_MASK);
					int i = fromCoords.y;
					if (fromCoords.y > toCoords.y) {
						while (i > toCoords.y) {
							i -= 1;
							robot.mouseMove(fromCoords.x + 10, i);
						}
					} else {
						while (i < toCoords.y) {
							i += 1;
							robot.mouseMove(fromCoords.x + 10, i);
						}
					}
					i = fromCoords.x + 10;
					while (i < toCoords.x) {
						i += 1;
						robot.mouseMove(i, toCoords.y);
					}
					robot.delay(1000);
				} catch (AWTException e) {
					log.error("Error during AWT Robot manipulation");
				}
			}
		});
		AbstractWait.sleep(TimePeriod.getCustom(3));
		Display.syncExec(new Runnable() {

			@Override
			public void run() {

				Robot robot;
				try {
					robot = new Robot();
					robot.mouseMove(toCoords.x, toCoords.y);
					robot.delay(2000);
					robot.mouseRelease(InputEvent.BUTTON1_MASK);
				} catch (AWTException e) {
					log.error("Error during AWT Robot manipulation");
				}
			}
		});
	}
}
