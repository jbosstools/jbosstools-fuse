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

package org.fusesource.ide.zk.jmx.jmxdoc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;

/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public class MBeanDoc {

    private final MBeanInfo _Info;

    private List<MBeanAttributeDoc> _Attributes;
    private List<MBeanOperationDoc> _Operations;

    public MBeanDoc(MBeanInfo info) {
        _Info = info;
    }

    /**
     * Returns the info.
     * 
     * @return The info
     */
    public final MBeanInfo getInfo() {
        return _Info;
    }

    public List<MBeanAttributeDoc> getAttributes() {

        if (_Attributes == null) {
            MBeanAttributeInfo[] attributeInfos = getInfo().getAttributes();

            if (attributeInfos == null || attributeInfos.length == 0) {
                return Collections.emptyList();
            }

            _Attributes = new ArrayList<MBeanAttributeDoc>(attributeInfos.length);

            for (MBeanAttributeInfo attributeInfo : attributeInfos) {
                MBeanAttributeDoc attributeDoc = new MBeanAttributeDoc(attributeInfo);
                _Attributes.add(attributeDoc);
            }
        }

        return _Attributes;
    }

    public List<MBeanOperationDoc> getOperations() {

        if (_Operations == null) {
            MBeanOperationInfo[] operationInfos = getInfo().getOperations();

            if (operationInfos == null || operationInfos.length == 0) {
                return Collections.emptyList();
            }

            _Operations = new ArrayList<MBeanOperationDoc>(operationInfos.length);

            for (MBeanOperationInfo operationInfo : operationInfos) {
                MBeanOperationDoc operationDoc = new MBeanOperationDoc(operationInfo);
                _Operations.add(operationDoc);
            }
        }

        return _Operations;
    }
}
