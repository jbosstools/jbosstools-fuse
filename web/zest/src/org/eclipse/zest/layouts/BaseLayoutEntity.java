/**************************************************************************************
 * Copyright (C) 2009 Progress Software, Inc. All rights reserved.                    *
 * http://fusesource.com                                                              *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the AGPL license      *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package org.eclipse.zest.layouts;

/**
 * @version $Revision: 1.1 $
 */
public abstract class BaseLayoutEntity implements LayoutEntity {

    public int compareTo(Object o) {
        if (o instanceof BaseLayoutEntity) {
            return compareTo((BaseLayoutEntity) o);
        }
        else {
            return this.hashCode() - o.hashCode();
        }
    }

    public abstract int compareTo(BaseLayoutEntity o);

}
