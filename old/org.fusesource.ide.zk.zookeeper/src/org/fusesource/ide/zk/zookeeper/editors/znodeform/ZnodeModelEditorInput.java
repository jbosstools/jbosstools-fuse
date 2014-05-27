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

package org.fusesource.ide.zk.zookeeper.editors.znodeform;

import org.fusesource.ide.zk.zookeeper.model.ZnodeModel;
import org.fusesource.ide.zk.zookeeper.viewers.ZnodeModelElementType;
import org.fusesource.ide.zk.core.editors.DataModelEditorInput;


/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public class ZnodeModelEditorInput extends DataModelEditorInput<ZnodeModel> {

    public ZnodeModelEditorInput(String editorId, ZnodeModel znodeModel) {
        super(editorId, znodeModel, new ZnodeModelElementType());
    }

}
