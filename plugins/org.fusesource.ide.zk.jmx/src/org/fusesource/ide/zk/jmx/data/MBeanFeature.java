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

import javax.management.MBeanFeatureInfo;

/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public abstract class MBeanFeature<I extends MBeanFeatureInfo> {

    private I _Info;
    private final String _Name;

    /**
     * TODO: Comment.
     * 
     * @param name
     */
    MBeanFeature(String name) {
        _Name = name;
    }

    /**
     * Returns the info.
     * 
     * @return The info.
     */
    public I getInfo() {
        return _Info;
    }

    /**
     * Returns the name.
     * 
     * @return The name.
     */
    public String getName() {
        return _Name;
    }

    /**
     * Sets the info.
     * 
     * @param info
     *            The info to set.
     */
    public void setInfo(I info) {
        _Info = info;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" + (_Name != null ? "Name=" + _Name : "") + "]";
    }

}
