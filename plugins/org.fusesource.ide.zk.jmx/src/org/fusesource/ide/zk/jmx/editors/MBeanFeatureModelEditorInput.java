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

package org.fusesource.ide.zk.jmx.editors;

import org.fusesource.ide.zk.jmx.model.MBeanFeatureModel;
import org.fusesource.ide.zk.core.editors.DataModelEditorInput;
import org.fusesource.ide.zk.core.viewers.DataModelElementType;


/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public abstract class MBeanFeatureModelEditorInput<M extends MBeanFeatureModel<M, ?>> extends DataModelEditorInput<M> {

    public MBeanFeatureModelEditorInput(String editorId, M model, DataModelElementType elementType) {
        super(editorId, model, elementType);
    }

}
