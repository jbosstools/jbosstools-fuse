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

import javax.management.Descriptor;
import javax.management.MBeanFeatureInfo;

/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public abstract class MBeanFeatureDoc<I extends MBeanFeatureInfo> {

    private final I _Info;

    public MBeanFeatureDoc(I info) {
        _Info = info;
    }

    /**
     * TODO: Comment.
     *
     * @return
     * @see javax.management.MBeanFeatureInfo#getDescription()
     */
    public String getDescription() {
        return getInfo().getDescription();
    }

    /**
     * TODO: Comment.
     *
     * @return
     * @see javax.management.MBeanFeatureInfo#getDescriptor()
     */
    public Descriptor getDescriptor() {
        return getInfo().getDescriptor();
    }

    public I getInfo() {
        return _Info;
    }

    /**
     * TODO: Comment.
     *
     * @return
     * @see javax.management.MBeanFeatureInfo#getName()
     */
    public String getName() {
        return getInfo().getName();
    }
    
    public final String getRawName() {
        return getInfo().getName();
    }

}
