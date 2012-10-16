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

import javax.management.MBeanAttributeInfo;

/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public final class MBeanAttributeDoc extends MBeanFeatureDoc<MBeanAttributeInfo> {

    public static final String NAME_PREFIX_GET = "get";
    public static final String NAME_PREFIX_IS = "is";

    private String _Name;
    private JmxType _Type;

    public MBeanAttributeDoc(MBeanAttributeInfo info) {
        super(info);
    }

    @Override
    public String getName() {
        if (_Name == null) {

            _Name = super.getName();

            if (_Name == null) {
                return _Name;
            }

            MBeanAttributeInfo info = getInfo();
            String prefix = NAME_PREFIX_GET;
            if (info.isIs()) {
                prefix = NAME_PREFIX_IS;
            }

            _Name = prefix + _Name;
        }

        return _Name;
    }

    public JmxType getType() {
        if (_Type == null) {
            _Type = new JmxType(getInfo().getType(), getDescriptor());
        }

        return _Type;
    }

}
