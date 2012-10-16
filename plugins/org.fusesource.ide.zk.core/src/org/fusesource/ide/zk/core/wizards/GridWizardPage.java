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

package org.fusesource.ide.zk.core.wizards;

import org.eclipse.swt.widgets.Composite;
import org.fusesource.ide.zk.core.widgets.grid.GridComposite;
import org.fusesource.ide.zk.core.widgets.grid.GridCompositeEvent;
import org.fusesource.ide.zk.core.widgets.grid.GridCompositeStatus;
import org.fusesource.ide.zk.core.widgets.grid.IGridCompositeEventListener;


/**
 * Wizard page that hosts a {@link GridComposite}.
 * 
 * @author Mark Masse
 */
public abstract class GridWizardPage extends AbstractWizardPage {

    private GridComposite _GridComposite;

    /**
     * Constructor.
     * 
     * @param wizard The wizard that owns this page.
     */
    public GridWizardPage(AbstractWizard wizard) {
        super(wizard);
    }

    @Override
    public final void createControl(Composite parent) {
        _GridComposite = createGridComposite(parent);
        _GridComposite.init();
        _GridComposite.addGridCompositeEventListener(new IGridCompositeEventListener() {

            @Override
            public void modified(GridCompositeEvent event) {
                updateStatus(event);
            }
        });

        setControl(_GridComposite);
        parent.layout(true);
    }

    /**
     * Returns the {@link GridComposite}.
     * 
     * @return The {@link GridComposite}.
     */
    public final GridComposite getGridComposite() {
        return _GridComposite;
    }

    /**
     * Subclasses must implement this method to create and return a {@link GridComposite} with the specified parent.
     * 
     * @param parent The {@link GridComposite} parent.
     * @return A {@link GridComposite} with the specified parent.
     */
    protected abstract GridComposite createGridComposite(Composite parent);

    private void updateStatus(GridCompositeEvent event) {
        GridCompositeStatus status = event.getStatus();

        boolean isError = status.getType().isError();
        String message = null;
        if (isError) {
            message = status.getMessage();
        }
        setErrorMessage(message);
        setPageComplete(!isError);
    }
}
