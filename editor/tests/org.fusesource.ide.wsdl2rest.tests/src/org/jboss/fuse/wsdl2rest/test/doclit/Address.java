package org.jboss.fuse.wsdl2rest.test.doclit;
/*
 * Copyright (c) 2008 SL_OpenSource Consortium
 * All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import org.jboss.fuse.wsdl2rest.test.Item;

@WebService
@SOAPBinding(style = Style.DOCUMENT)
public interface Address {

    /**
     * List the available resource ids.
     */
    @WebMethod
    // [#6] Cannot use array type return in doc/lit operation
    // Integer[] listAddresses();
    String listAddresses();

    /**
     * Get the resource value for the given id.
     * @return The resource value or null
     */
    @WebMethod
    Item getAddress(Integer id);

    /**
     * Add a resource with the given value.
     * @return The new resource id
     */
    @WebMethod
    Integer addAddress(Item item);

    /**
     * Update a resource for the given id with the given value.
     * @return The resource id or null
     */
    @WebMethod
    Integer updAddress(Item item);

    /**
     * Delete a resource with the given id.
     * @return The resource value or null
     */
    @WebMethod
    Item delAddress(Integer id);
}