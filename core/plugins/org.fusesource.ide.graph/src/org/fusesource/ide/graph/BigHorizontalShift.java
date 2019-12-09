/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.eclipse.zest.layouts.LayoutEntity;
import org.eclipse.zest.layouts.algorithms.AbstractLayoutAlgorithm;
import org.eclipse.zest.layouts.dataStructures.InternalNode;
import org.eclipse.zest.layouts.dataStructures.InternalRelationship;

/**
 * @author lhein
 */
public class BigHorizontalShift extends AbstractLayoutAlgorithm {

	private static double DELTA = 10;
	private static double VSPACING = 5;

	public BigHorizontalShift(int styles) {
		super(styles);
	}

	public static void setDelta(double newDelta) {
		DELTA = newDelta;
	}
	
	public static void setVSpacing(double newVSpacing) {
		VSPACING = newVSpacing;
	}
	
	@Override
	protected void applyLayoutInternal(InternalNode[] entitiesToLayout, InternalRelationship[] relationshipsToConsider, double boundsX, double boundsY, double boundsWidth, double boundsHeight) {

		List<List<InternalNode>> row = new ArrayList<>();
		for (int i = 0; i < entitiesToLayout.length; i++) {
			addToRowList(entitiesToLayout[i], row);
		}

		int heightSoFar = 0;

		Collections.sort(row, new Comparator<List<InternalNode>>() {

			@Override
			public int compare(List<InternalNode> a0, List<InternalNode> a1) {
				LayoutEntity node0 = a0.get(0).getLayoutEntity();
				LayoutEntity node1 = a1.get(0).getLayoutEntity();
				return (int) (node0.getYInLayout() - node1.getYInLayout());
			}

		});

		Iterator<List<InternalNode>> iterator = row.iterator();
		while (iterator.hasNext()) {
			List<InternalNode> currentRow = iterator.next();
			Collections.sort(currentRow, new Comparator<InternalNode>() {
				@Override
				public int compare(InternalNode arg0, InternalNode arg1) {
					return (int) (arg1.getLayoutEntity().getYInLayout() - arg0.getLayoutEntity().getYInLayout());
				}
			});
			Iterator<InternalNode> iterator2 = currentRow.iterator();
			int i = 0;
			int width = (int) ((boundsWidth / 2) - currentRow.size() * 75);

			heightSoFar += currentRow.get(0).getLayoutEntity().getHeightInLayout() + VSPACING * 8;
			while (iterator2.hasNext()) {
				InternalNode currentNode = iterator2.next();

				double location = width + 10 * ++i;
				currentNode.setLocation(location, heightSoFar);
				width += currentNode.getLayoutEntity().getWidthInLayout();
			}
		}
	}

	private void addToRowList(InternalNode node, List<List<InternalNode>> list) {
		double layoutY = node.getLayoutEntity().getYInLayout();

		for (int i = 0; i < list.size(); i++) {
			List<InternalNode> currentRow = list.get(i);
			InternalNode currentRowNode = currentRow.get(0);
			double currentRowY = currentRowNode.getLayoutEntity().getYInLayout();
			//double currentRowHeight = currentRowNode.getLayoutEntity().getHeightInLayout();
			if (layoutY >= (currentRowY - DELTA) && layoutY <= currentRowY + DELTA) {
				currentRow.add(node);
				//list.add(i, currentRow);
				return;
			}
		}
		List<InternalNode> newRow = new ArrayList<>();
		newRow.add(node);
		list.add(newRow);
	}

	@Override
	protected int getCurrentLayoutStep() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected int getTotalNumberOfLayoutSteps() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected boolean isValidConfiguration(boolean asynchronous, boolean continuous) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected void postLayoutAlgorithm(InternalNode[] entitiesToLayout, InternalRelationship[] relationshipsToConsider) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void preLayoutAlgorithm(InternalNode[] entitiesToLayout, InternalRelationship[] relationshipsToConsider, double x, double y, double width, double height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setLayoutArea(double x, double y, double width, double height) {
		// TODO Auto-generated method stub
	}
}
