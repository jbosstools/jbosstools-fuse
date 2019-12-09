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

package org.fusesource.ide.foundation.ui.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class Images {

	/**
	 * Return the <code>Image</code> to be used when displaying an error.
	 * 
	 * @return image the error image
	 */
	public static Image getErrorImage() {
		return getSWTImage(SWT.ICON_ERROR);
	}

	/**
	 * Return the <code>Image</code> to be used when displaying a warning.
	 * 
	 * @return image the warning image
	 */
	public static Image getWarningImage() {
		return getSWTImage(SWT.ICON_WARNING);
	}

	/**
	 * Return the <code>Image</code> to be used when displaying information.
	 * 
	 * @return image the information image
	 */
	public static Image getInfoImage() {
		return getSWTImage(SWT.ICON_INFORMATION);
	}

	/**
	 * Return the <code>Image</code> to be used when displaying a question.
	 * 
	 * @return image the question image
	 */
	public static Image getQuestionImage() {
		return getSWTImage(SWT.ICON_QUESTION);
	}


	/**
	 * Get an <code>Image</code> from the provide SWT image constant.
	 * 
	 * @param imageID
	 *            the SWT image constant
	 * @return image the image
	 */
	protected static Image getSWTImage(final int imageID) {
		final Display display = Shells.getDisplay();
		if (display == null) {
			return null;
		}
		final Image[] image = new Image[1];
		display.syncExec(new Runnable() {
			@Override
			public void run() {
				image[0] = display.getSystemImage(imageID);
			}
		});

		return image[0];

	}
}
