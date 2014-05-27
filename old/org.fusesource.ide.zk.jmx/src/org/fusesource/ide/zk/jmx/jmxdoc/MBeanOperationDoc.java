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

import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;

/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public final class MBeanOperationDoc extends MBeanFeatureDoc<MBeanOperationInfo> {

    public static final String IMPACT_ACTION = "ACTION";
    public static final String IMPACT_ACTION_INFO = "ACTION_INFO";
    public static final String IMPACT_INFO = "INFO";
    public static final String IMPACT_UNKNOWN = "UNKNOWN";

    public static String getImpactString(int impact) {

        switch (impact) {
        case MBeanOperationInfo.ACTION:
            return IMPACT_ACTION;

        case MBeanOperationInfo.ACTION_INFO:
            return IMPACT_ACTION_INFO;

        case MBeanOperationInfo.INFO:
            return IMPACT_INFO;

        case MBeanOperationInfo.UNKNOWN:
        default:
            return IMPACT_UNKNOWN;
        }
    }

    private JmxType _ReturnType;
    private List<MBeanParameterDoc> _Parameters;

    public MBeanOperationDoc(MBeanOperationInfo info) {
        super(info);
    }

    public JmxType getReturnType() {
        if (_ReturnType == null) {
            _ReturnType = new JmxType(getInfo().getReturnType(), getDescriptor());
        }

        return _ReturnType;
    }

    public int getImpact() {
        return getInfo().getImpact();
    }

    public String getImpactString() {
        return getImpactString(getImpact());
    }

    public List<MBeanParameterDoc> getParameters() {

        if (_Parameters == null) {
            MBeanParameterInfo[] parameterInfos = getInfo().getSignature();

            if (parameterInfos == null || parameterInfos.length == 0) {
                return Collections.emptyList();
            }

            _Parameters = new ArrayList<MBeanParameterDoc>(parameterInfos.length);

            for (MBeanParameterInfo parameterInfo : parameterInfos) {
                MBeanParameterDoc parameterDoc = new MBeanParameterDoc(parameterInfo);
                _Parameters.add(parameterDoc);
            }
        }

        return _Parameters;
    }

}
