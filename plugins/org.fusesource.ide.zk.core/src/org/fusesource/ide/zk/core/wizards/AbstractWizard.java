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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.Wizard;

/**
 * Abstract base {@link Wizard}.
 *
 * @see AbstractWizardPage
 *
 * @author Mark Masse
 */
public abstract class AbstractWizard extends Wizard {

    private final String _DefaultPageDescription;

    /**
     * Constructor.
     *
     * @param windowTitle The window title text.
     * @param defaultPageDescription The default description to show on the wizard's pages.
     * @param defaultPageImageDescriptor The default image to show on wizard's pages.
     */
    public AbstractWizard(String windowTitle, String defaultPageDescription, ImageDescriptor defaultPageImageDescriptor) {
        super();

        setNeedsProgressMonitor(true);
        setHelpAvailable(false);
        setWindowTitle(windowTitle);
        _DefaultPageDescription = defaultPageDescription;
        setDefaultPageImageDescriptor(defaultPageImageDescriptor);
    }

    /**
     * Returns the default page description.
     * 
     * @return The default page description.
     */
    public String getDefaultPageDescription() {
        return _DefaultPageDescription;
    }

}