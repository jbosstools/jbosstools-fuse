/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.fabric.actions.jclouds;

import static org.fusesource.ide.fabric.actions.jclouds.JClouds.text;

import org.eclipse.jface.viewers.LabelProvider;
import org.jclouds.apis.ApiMetadata;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.domain.Location;
import org.jclouds.domain.ResourceMetadata;
import org.jclouds.providers.ProviderMetadata;

public class JCloudsLabelProvider extends LabelProvider {
	private static JCloudsLabelProvider instance = new JCloudsLabelProvider();

	public static JCloudsLabelProvider getInstance() {
		return instance;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof ProviderMetadata) {
			return JClouds.text((ProviderMetadata) element);
		} else if (element instanceof ApiMetadata) {
			return JClouds.text((ApiMetadata) element);
		} else if (element instanceof Location) {
			return text((Location) element);
		} else if (element instanceof ComputeMetadata) {
			return JClouds.text((ComputeMetadata) element);
		} else if (element instanceof ResourceMetadata) {
			return JClouds.text((ResourceMetadata<?>) element);
		} else {
			return super.getText(element);
		}
	}

}