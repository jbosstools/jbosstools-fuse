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

package org.fusesource.ide.zk.zookeeper.data;

import java.util.EventObject;

/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
@SuppressWarnings("serial")
public final class ZooKeeperConnectionEvent extends EventObject {

    public ZooKeeperConnectionEvent(ZooKeeperConnection zkConnection) {
        super(zkConnection);
    }

    /**
     * TODO: Comment.
     * 
     * @return
     */
    public final ZooKeeperConnection getZooKeeperConnection() {
        return (ZooKeeperConnection) getSource();
    }

}
