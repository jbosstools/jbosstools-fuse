/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.fusesource.ide.zk.core.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.fusesource.ide.zk.core.widgets.grid.GridComposite;
import org.fusesource.ide.zk.core.widgets.grid.GridCompositeEvent;
import org.fusesource.ide.zk.core.widgets.grid.GridCompositeStatus;
import org.fusesource.ide.zk.core.widgets.grid.IGridCompositeEventListener;


/**
 * Abstract base {@link TitleAreaDialog} class that hosts a {@link GridComposite}. Subclasses must override
 * {@link #createGridComposite(Composite) createGridComposite} to create the specific {@link GridComposite} instance.
 * 
 * @author Mark Masse
 */
public abstract class GridDialog extends TitleAreaDialog {

    private GridComposite _GridComposite;

    /**
     * Constructor.
     * 
     * @param parentShell The parent SWT {@link Shell}.
     */
    protected GridDialog(Shell parentShell) {
        super(parentShell);

    }

    /**
     * Returns the {@link GridComposite}.
     * 
     * @return The {@link GridComposite}
     */
    public final GridComposite getGridComposite() {
        return _GridComposite;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);

        _GridComposite.addGridCompositeEventListener(new IGridCompositeEventListener() {

            @Override
            public void modified(GridCompositeEvent event) {
                updateStatus(event);
            }
        });
    }

    @Override
    protected final Control createDialogArea(Composite parent) {

        int fullHorizontalSpan = ((GridLayout) parent.getLayout()).numColumns;

        Label topSeparator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
        topSeparator.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, fullHorizontalSpan, 1));

        _GridComposite = createGridComposite(parent);
        _GridComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, fullHorizontalSpan, 1));
        _GridComposite.init();

        Label bottomSeparator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
        bottomSeparator.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, fullHorizontalSpan, 1));

        return _GridComposite;
    }

    /**
     * Called from {@link GridDialog#createDialogArea(Composite)}. Subclasses must override this method to create the
     * dialog's {@link GridComposite} with the specified parent.
     * 
     * @param parent The parent {@link Composite} passed into {@link GridDialog#createDialogArea(Composite)}.
     * @return The dialog's {@link GridComposite}.
     */
    protected abstract GridComposite createGridComposite(Composite parent);

    @Override
    protected boolean isResizable() {
        return true;
    }

    private void updateStatus(GridCompositeEvent event) {
        GridCompositeStatus status = event.getStatus();

        boolean isError = status.getType().isError();
        String message = null;
        if (isError) {
            message = status.getMessage();
        }
        setErrorMessage(message);

        Button okButton = getButton(IDialogConstants.OK_ID);
        if (okButton != null) {
            okButton.setEnabled(!isError);
        }
    }

}
