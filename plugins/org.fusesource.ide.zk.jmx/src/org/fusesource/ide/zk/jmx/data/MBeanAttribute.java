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

package org.fusesource.ide.zk.jmx.data;

import javax.management.MBeanAttributeInfo;

/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public final class MBeanAttribute extends MBeanFeature<MBeanAttributeInfo> {

    private Object _Value;
    private String _ValueRetrievalErrorMessage;

    /**
     * TODO: Comment.
     * 
     * @param name
     */
    MBeanAttribute(String name) {
        super(name);
    }

    /**
     * Returns the value.
     * 
     * @return The value.
     */
    public Object getValue() {
        return _Value;
    }

    public String getValueAsString() {
        return JmxUtils.stringValueOf(getValue());
    }

    /**
     * Returns the valueRetrievalErrorMessage.
     * 
     * @return The valueRetrievalErrorMessage
     */
    public String getValueRetrievalErrorMessage() {
        return _ValueRetrievalErrorMessage;
    }

    /**
     * Sets the value.
     * 
     * @param value The value to set.
     */
    public void setValue(Object value) {
        _Value = value;
    }

    /**
     * Sets the valueRetrievalErrorMessage.
     * 
     * @param valueRetrievalErrorMessage The valueRetrievalErrorMessage to set
     */
    public void setValueRetrievalErrorMessage(String valueRetrievalErrorMessage) {
        _ValueRetrievalErrorMessage = valueRetrievalErrorMessage;
    }

    @Override
    public String toString() {
        return "MBeanAttribute [" + (getName() != null ? "Name=" + getName() + ", " : "")
                + (getValue() != null ? "Value=" + getValue() : "") + "]";
    }
}
