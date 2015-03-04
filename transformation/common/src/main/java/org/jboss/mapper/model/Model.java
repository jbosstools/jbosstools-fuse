/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.jboss.mapper.model;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Model {

    private Class<?> modelClass;
    private String name;
    private String type;
    private Model parent;
    private HashMap<String, Model> children = new HashMap<String, Model>();
    private boolean isCollection;

    public Model(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public Model addChild(String name, String type) {
        Model node = new Model(name, type);
        node.parent = this;
        node.name = name;
        node.type = type;
        children.put(name, node);
        return node;
    }

    public void print(PrintStream out) {
        printModel(this, 0, out);
    }

    public Model get(String nodeName) {
        if (nodeName.contains(".")) {
            int idx = nodeName.indexOf(".");
            String parent = nodeName.substring(0, idx);
            String child = nodeName.substring(idx + 1, nodeName.length());
            return children.get(parent).get(child);
        } else {
            return children.get(nodeName);
        }
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Model getParent() {
        return parent;
    }

    public boolean isCollection() {
        return isCollection;
    }

    public Model setIsCollection(boolean isCollection) {
        this.isCollection = isCollection;
        return this;
    }

    public List<Model> getChildren() {
        return new ArrayList<Model>(children.values());
    }

    public List<String> listFields() {
        List<String> fields = new LinkedList<String>();
        return listFields(fields, this.children.values(), "");
    }

    public List<String> listFields(List<String> fieldList, 
            Collection<Model> fields, String prefix) {
        for (Model field : fields) {
            fieldList.add(prefix + field.getName());
            listFields(fieldList, field.children.values(), prefix + field.getName() + ".");
        }
        return fieldList;
    }

    @Override
    public String toString() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        print(new PrintStream(bos, true));
        return bos.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Model)) {
            return false;
        } else if (obj == this) {
            return true;
        }
        Model comp = (Model) obj;
        return isEqual(modelClass, comp.getModelClass())
                && isEqual(name, comp.getName())
                && isEqual(type, comp.type)
                && isEqual(children, comp.children);
    }

    @Override
    public int hashCode() {
        return hash(modelClass, name, type, children);
    }

    public Class<?> getModelClass() {
        return modelClass;
    }

    public void setModelClass(Class<?> modelClass) {
        this.modelClass = modelClass;
    }

    private void printModel(Model node, int depth, PrintStream out) {
        out.println(format(node, depth));
        for (Model child : node.children.values()) {
            printModel(child, depth + 1, out);
        }
    }

    private String format(Model node, int depth) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < depth; i++) {
            sb.append("  ");
        }
        sb.append(node.children.isEmpty() ? "- " : "* ");
        sb.append(node.name + " : " + node.type);
        return sb.toString();
    }

    private boolean isEqual(Object objA, Object objB) {
        // basic checks
        if (objA == null && objB == null) {
            return true;
        } else if (objA == null || objB == null) {
            return false;
        } else {
            return objA.equals(objB);
        }
    }

    private int hash(Object... vals) {
        int hash = 7;
        for (Object val : vals) {
            if (val != null) {
                hash = hash * 37 + val.hashCode();
            }
        }
        return hash;
    }
}
