/**
 *  Copyright 2005-2016 Red Hat, Inc.
 *
 *  Red Hat licenses this file to you under the Apache License, version
 *  2.0 (the "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied.  See the License for the specific language governing
 *  permissions and limitations under the License.
 */
package com.mycompany.templates.camel.activemq;

import java.io.InputStream;
import java.util.Random;

import org.apache.camel.CamelContext;

/**
 * To generate random orders
 */
public class OrderGenerator {

    private int count = 1;
    private Random random = new Random();

    public InputStream generateOrder(CamelContext camelContext) {
        int number = random.nextInt(5) + 1;
        String name = "data/order" + number + ".xml";

        return generateOrderWithFileName(camelContext, name);
    }

    public InputStream generateOrderWithFileName(CamelContext camelContext, String name) {
        return camelContext.getClassResolver().loadResourceAsStream(name);
    }

    public String generateFileName() {
        return "order" + count++ + ".xml";
    }
}
