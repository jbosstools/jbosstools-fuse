/*******************************************************************************
 * Copyright (c) 2006 Jeff Mesnil
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
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

package org.fusesource.ide.jmx.core.providers;

import java.io.Serializable;

/**
 * @author Mitko Kolev
 *
 */
public class MBeanServerConnectionDescriptor implements Serializable {

    private static final long serialVersionUID = -8358701879017195518L;

    private final String id;
    private final String url;
    private final String userName;
    private final String password;

    public MBeanServerConnectionDescriptor(
    		String id, String url,
            String userName, String password) {
        this.id = id;
        this.url = url;
        this.userName = userName;
        this.password = password;
    }

    public String getID() {
        return id;
    }

    public String getURL() {
        return url;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

}
