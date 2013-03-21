/******************************************************************************* 
 * Copyright (c) 2013 Red Hat, Inc. 
 *  All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 *
 ******************************************************************************/
package org.fusesource.ide.fabric.navigator;

import org.fusesource.ide.commons.ui.form.FormPage;
import org.fusesource.ide.commons.ui.form.FormPagePropertyTabSection;

public class ProfileRequirementsTabSection extends FormPagePropertyTabSection {

    @Override
    protected FormPage createPage(Object selection) {
        return new FormPage(new ProfileRequirementsForm((ProfileNode) selection));
    }

}
