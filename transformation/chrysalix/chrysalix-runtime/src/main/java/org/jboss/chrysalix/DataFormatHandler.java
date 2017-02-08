/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. and others.
 * All rights reserved.  See the COPYRIGHT.txt file distributed with this work
 * for information regarding copyright ownership.  Some portions may be
 * licensed to Red Hat, Inc. under one or more contributor license agreements.
 * See the AUTHORS.txt file in the distribution for a full listing of
 * individual contributors.
 *
 * Chrysalix is free software. Unless otherwise indicated, all code in
 * Chrysalix is licensed to you under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * Chrysalix is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.chrysalix;

/**
 * Handles the interpretation and conversion of data to and from {@link Node nodes}.
 */
public interface DataFormatHandler {

    /**
     * @param data
     * 		The non-<code>null</code> source data to be converted to nodes. The data's type is dependent upon the handler's
     * 		implementation.
     * @param parent
     * 		The parent node under which the data should be stored.
     * @return the node representing the root of the supplied data.
     * @throws Exception if any error occurs.
     */
    Node toSourceNode(Object data,
                      Node parent) throws Exception;

    /**
     * @param targetNode
     * 		The target node to be converted to data in the format determined by the handler's implementation.
     * @return the node representing the root of the target data.
     * @throws Exception if any error occurs.
     */
    Object toTargetData(Node targetNode) throws Exception;

    /**
     * @param data The data used to create a new, empty target node or to convert existing data into a target node. The data's type
     * 		is dependent upon the handler's implementation.
     * @param parent
     * 		The parent node under which the data should be stored.
     * @return the node representing the root of the supplied data.
     * @throws Exception if any error occurs.
     */
    Node toTargetNode(Object data,
                      Node parent) throws Exception;
}
