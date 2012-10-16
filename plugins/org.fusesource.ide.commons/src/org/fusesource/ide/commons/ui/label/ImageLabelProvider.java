package org.fusesource.ide.commons.ui.label;

import org.eclipse.swt.graphics.Image;
import org.fusesource.ide.commons.tree.HasOwner;
import org.fusesource.ide.commons.ui.ImageProvider;
import org.fusesource.ide.commons.ui.propsrc.BeanPropertySource;
import org.fusesource.ide.commons.util.Function1;


public class ImageLabelProvider extends FunctionColumnLabelProvider {

	public ImageLabelProvider(Function1 function) {
		super(function);
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof ImageProvider) {
			ImageProvider ip = (ImageProvider) element;
			return ip.getImage();
		}
		if (element instanceof HasOwner) {
			HasOwner ho = (HasOwner) element;
			Object bean = ho.getOwner();
			if (bean instanceof ImageProvider) {
				ImageProvider ip = (ImageProvider) bean;
				return ip.getImage();
			}
		}
		if (element instanceof BeanPropertySource) {
			BeanPropertySource bps = (BeanPropertySource) element;
			Object bean = bps.getBean();
			if (bean instanceof ImageProvider) {
				ImageProvider ip = (ImageProvider) bean;
				return ip.getImage();
			}
		}
		return super.getImage(element);
	}


}
