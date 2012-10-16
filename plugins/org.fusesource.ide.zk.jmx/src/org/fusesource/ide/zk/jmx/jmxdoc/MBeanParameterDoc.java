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

import java.util.regex.Pattern;

import javax.management.MBeanParameterInfo;

/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public final class MBeanParameterDoc extends MBeanFeatureDoc<MBeanParameterInfo> {

    private static final Pattern REGEX_PATTERN_WHITESPACE = Pattern.compile("\\s+");

    private String _Name;
    private JmxType _Type;

    /**
     * TODO: Comment.
     * 
     * @param info
     */
    public MBeanParameterDoc(MBeanParameterInfo info) {
        super(info);
    }

    @Override
    public String getName() {
        if (_Name == null) {

            _Name = super.getName();

            if (_Name == null) {
                return _Name;
            }

            // Turn parameter names like "foo bar" into "fooBar" to make them look more Java-like

            String[] parts = REGEX_PATTERN_WHITESPACE.split(_Name.trim());
            if (parts.length > 0) {
                StringBuilder sb = new StringBuilder();
                sb.append(parts[0]);
                for (int i = 1; i < parts.length; i++) {
                    String part = parts[i];
                    sb.append(Character.toUpperCase(part.charAt(0)));
                    sb.append(part.substring(1));
                }
                _Name = sb.toString();
            }
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
