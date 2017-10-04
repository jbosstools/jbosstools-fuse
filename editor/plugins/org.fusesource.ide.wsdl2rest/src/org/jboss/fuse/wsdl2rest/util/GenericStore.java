package org.jboss.fuse.wsdl2rest.util;
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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class GenericStore {

    private Map<Integer, Object> map = new LinkedHashMap<>();

    public Integer[] list() {
        synchronized (map) {
            Set<Integer> keySet = map.keySet();
            return new ArrayList<>(keySet).toArray(new Integer[keySet.size()]);
        }
    }

    public Object get(Integer id) {
        synchronized (map) {
            return map.get(id);
        }
    }

    public Integer add(Object obj) {
        synchronized (map) {
            int id = map.size() + 1;
            map.put(id, obj);
            return id;
        }
    }

    public Integer update(Integer id, Object obj) {
        Integer result = null;
        synchronized (map) {
            if (map.get(id) != null) {
                map.put(id, obj);
                result = id;
            }
        }
        return result;
    }

    public Object delete(Integer id) {
        Object result;
        synchronized (map) {
            result = map.remove(id);
        }
        return result;
    }
}