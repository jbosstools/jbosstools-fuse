/**
 * 
 */
package org.fusesource.ide.camel.editor.features.other;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.impl.DefaultResizeShapeFeature;

/**
 * @author lhein
 *
 */
public class ResizeNodeFeature extends DefaultResizeShapeFeature {
	
	public ResizeNodeFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canResizeShape(IResizeShapeContext context) {
		return false;
	}
}
