package org.fusesource.ide.camel.editor.features.custom;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

public class ZestLayoutAlgorithmnChoiceDialog extends ElementListSelectionDialog {
    public ZestLayoutAlgorithmnChoiceDialog(Shell parent) {
        super(parent, new LabelProvider() {
            @Override
			public String getText(Object element) {
                Integer idx = (Integer) element;
                return ZestLayoutDiagramFeature.layouts.get(idx - 1);
            }
        });
        Object[] elements = new Object[ZestLayoutDiagramFeature.layouts.size()];
        for (int i = 0; i < ZestLayoutDiagramFeature.layouts.size(); i++) {
            elements[i] = Integer.valueOf(i + 1);
        }
        setElements(elements);
        setTitle("Select Layout");
        setMultipleSelection(false);
    }

    @Override
    public int open() {
        int result = super.open();
        if (result < 0)
            return result;
        return (Integer) getFirstResult();
    }

}
