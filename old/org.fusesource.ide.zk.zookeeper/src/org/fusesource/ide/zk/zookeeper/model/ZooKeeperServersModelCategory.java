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

package org.fusesource.ide.zk.zookeeper.model;


import java.util.List;

import org.fusesource.ide.zk.core.model.AbstractDataModelCategory;

/**
 * The {@link ZooKeeperConnectionModel} named category of {@link ZooKeeperServerModel} child elements.
 * 
 * @author Mark Masse
 */
public class ZooKeeperServersModelCategory extends AbstractDataModelCategory<ZooKeeperConnectionModel> {

    /**
     * Constructor.
     * 
     * @param parentModel The parent {@link ZooKeeperConnectionModel}.
     */
    public ZooKeeperServersModelCategory(ZooKeeperConnectionModel parentModel) {
        super(parentModel, "Servers");
    }

    @Override
    public int getElementCount() {
        return getParentModel().getZooKeeperServerDescriptors().size();
    }

    @Override
    public List<?> getElements() {
        return getParentModel().getZooKeeperServerModels();
    }

}
