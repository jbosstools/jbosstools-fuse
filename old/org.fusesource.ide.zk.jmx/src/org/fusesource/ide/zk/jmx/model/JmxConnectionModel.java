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

package org.fusesource.ide.zk.jmx.model;

import java.util.List;
import java.util.Set;

import javax.management.ObjectName;

import org.fusesource.ide.zk.jmx.data.Domain;
import org.fusesource.ide.zk.jmx.data.IJmxConnectionEventListener;
import org.fusesource.ide.zk.jmx.data.JmxConnection;
import org.fusesource.ide.zk.jmx.data.JmxConnectionDescriptor;
import org.fusesource.ide.zk.jmx.data.JmxConnectionEvent;
import org.fusesource.ide.zk.core.model.DataModel;
import org.fusesource.ide.zk.core.model.DataModelManager;


/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public final class JmxConnectionModel extends DataModel<JmxConnectionModel, JmxConnectionDescriptor, JmxConnection> {

    private DataModelManager<DomainModel, String, Domain> _DomainModelManager;
    private final JmxConnectionListener _JmxConnectionListener;
    private final Object _Owner;

    /**
     * TODO: Comment.
     * 
     * @param key
     * @param data
     */
    JmxConnectionModel(JmxConnectionDescriptor jmxConnectionDescriptor, Object owner) {
        super(jmxConnectionDescriptor);
        _Owner = owner;
        _JmxConnectionListener = new JmxConnectionListener();
    }

    /**
     * TODO: Comment.
     * 
     * @param domainName
     * @return
     */
    public DomainModel getDomainModel(String domainName) {
        return _DomainModelManager.getModel(domainName);
    }

    /**
     * TODO: Comment.
     * 
     * @return
     */
    public List<DomainModel> getDomainModels() {
        return _DomainModelManager.getModels();
    }

    /**
     * TODO: Comment.
     * 
     * @return
     */
    public Set<String> getDomainNames() {
        return _DomainModelManager.getKeys();
    }

    /**
     * Returns the owner.
     * 
     * @return The owner
     */
    public Object getOwner() {
        return _Owner;
    }

    @Override
    public DataModel<?, ?, ?> getOwnerModel() {
        Object owner = getOwner();
        if (owner instanceof DataModel<?, ?, ?>) {
            return (DataModel<?, ?, ?>) owner;
        }
        return null;
    }

    @Override
    public DataModel<?, ?, ?> getParentModel() {
        return getOwnerModel();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.fusesource.ide.zk.zookeeper.model.DataModel#getThis()
     */
    @Override
    protected JmxConnectionModel getThis() {
        return this;
    }

    @Override
    protected void hookAfterDestroyed() {
        if (_DomainModelManager != null) {
            _DomainModelManager.destroy();
        }

        JmxConnection jmxConnection = getData();
        jmxConnection.close();
    }

    @Override
    protected void hookAfterSetData() {

        JmxConnection jmxConnection = getData();
        if (jmxConnection != null) {
            jmxConnection.addEventListener(_JmxConnectionListener);
            if (hasListeners()) {
                jmxConnection.connect();
            }
        }

        if (_DomainModelManager == null) {
            DomainModelSource domainModelSource = new DomainModelSource(this);
            _DomainModelManager = new DataModelManager<DomainModel, String, Domain>(domainModelSource);
        }
        else {
            _DomainModelManager.refreshManagedModels();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.fusesource.ide.zk.zookeeper.model.DataModel#beforeFirstListenerAdded()
     */
    @Override
    protected void hookBeforeFirstListenerAdded() {
        super.hookBeforeFirstListenerAdded();

        JmxConnection jmxConnection = getData();
        jmxConnection.connect();

    }

    @Override
    protected void hookBeforeSetData() {

        JmxConnection jmxConnection = getData();
        if (jmxConnection != null) {
            jmxConnection.removeEventListener(_JmxConnectionListener);
            jmxConnection.close();
        }
    }

    private class JmxConnectionListener implements IJmxConnectionEventListener {

        @Override
        public void mbeanRegistered(JmxConnectionEvent event) {
            ObjectName objectName = event.getObjectName();
            List<DomainModel> domainModels = _DomainModelManager.getManagedModels();
            if (domainModels == null) {
                return;
            }

            for (DomainModel domainModel : domainModels) {
                Domain domain = domainModel.getData();
                ObjectName domainPattern = domain.getDomainPatternObjectName();
                if (domainPattern.apply(objectName)) {
                    domainModel.refreshData();
                    return;
                }
            }
        }

        @Override
        public void mbeanUnregistered(JmxConnectionEvent event) {
            ObjectName objectName = event.getObjectName();
            List<DomainModel> domainModels = _DomainModelManager.getManagedModels();
            if (domainModels == null) {
                return;
            }

            for (DomainModel domainModel : domainModels) {
                Domain domain = domainModel.getData();
                ObjectName domainPattern = domain.getDomainPatternObjectName();
                if (domainPattern.apply(objectName)) {
                    MBeanModel mbeanModel = domainModel.getMBeanModel(objectName);
                    if (mbeanModel != null) {
                        mbeanModel.destroy();
                        return;
                    }
                }
            }
        }

        @Override
        public void connectionStateChanged(JmxConnectionEvent event) {
            fireDataModelDataChanged();
            JmxConnection connection = getData();
            if (!connection.isConnected()) {
                _DomainModelManager.destroyManagedModels();
            }
        }

    }

}
