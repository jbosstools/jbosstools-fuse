package org.jboss.tools.fuse.transformation.editor.internal.util;

import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.jboss.tools.fuse.transformation.core.MappingOperation;

public class DateFormatInputDialog extends BaseDialog {
    
    private ComboViewer formatComboViewer;
    private String formatString = null;

    public DateFormatInputDialog(Shell shell, MappingOperation<?, ?> mapping) {
        super(shell);
    }

    @Override
    protected void constructContents(Composite parent) {
        parent.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).create());
        Label label = new Label(parent, SWT.NONE);
        label.setText("Date Format:");
        formatComboViewer = new ComboViewer(parent, SWT.NONE);
        formatComboViewer.setContentProvider(new ObservableListContentProvider());
        formatComboViewer.getCombo().setLayoutData(GridDataFactory.fillDefaults().grab(true, false).span(1, 1).create());
        WritableList formatList = new WritableList();
        formatList.add("yyyy-MM-dd HH:mm:ss");
        formatList.add("yyyy-MM-dd HH:mm:ss.SSS");
        formatList.add("yyyy-MM-dd HH:mm:ss.SSS Z");
        formatList.add("MM-dd-yyyy HH:mm:ss");
        formatList.add("MM/dd/yyyy HH:mm");
        formatList.add("");
        formatComboViewer.setInput(formatList);

        Label tipLabel = new Label(parent, SWT.NONE);
        tipLabel.setText("(Type your own format if yours is not listed)");
        tipLabel.setLayoutData(GridDataFactory.swtDefaults().span(2, 1).create());
        
        formatComboViewer.getCombo().addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
                final IStructuredSelection selection =
                    (IStructuredSelection)formatComboViewer.getSelection();
                formatString = (String)selection.getFirstElement();
                validate();
            }
        });
        
        formatComboViewer.getCombo().addModifyListener(new ModifyListener() {
            
            @Override
            public void modifyText(ModifyEvent e) {
                formatString = formatComboViewer.getCombo().getText();
                validate();
            }
        });
        
        if (formatString != null && !formatString.trim().isEmpty()) {
            formatComboViewer.getCombo().setText(formatString);
            formatComboViewer.getCombo().notifyListeners(SWT.Modify, null);
        }
        
    }

    @Override
    protected String message() {
        return "Select or enter the date format for the date conversion.";
    }

    @Override
    protected String title() {
        return "Date Format";
    }

    void validate() {
        if (getButton(IDialogConstants.OK_ID) != null) {
            getButton(IDialogConstants.OK_ID).setEnabled(formatString != null
                                                     && !formatString.isEmpty());
        }
    }
    
    public String getFormatString() {
        return formatString;
    }
    
    public void setFormatString(String format) {
        formatString = format;
    }
}
