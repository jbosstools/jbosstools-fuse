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

package org.fusesource.ide.zk.jmx.viewers;

import org.eclipse.swt.graphics.Image;
import org.fusesource.ide.zk.jmx.JmxActivator;
import org.fusesource.ide.zk.jmx.data.JmxConnection;
import org.fusesource.ide.zk.jmx.model.DomainModel;
import org.fusesource.ide.zk.jmx.model.JmxConnectionModel;

import java.util.List;
import java.util.Set;

import javax.management.remote.JMXServiceURL;


/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public class JmxConnectionModelElementType extends AbstractJmxDataModelElementType {

    @Override
    public int getChildCount(Object parent) {
        JmxConnectionModel model = (JmxConnectionModel) parent;
        
        JmxConnection connection = model.getData();
        if (!connection.isConnected()) {
            return 0;
        }
        
        Set<String> domainNames = model.getDomainNames();
        int childCount = 0;
        if (domainNames != null) {
            childCount = domainNames.size();
        }
        return childCount;
    }

    @Override
    public Object getChildElement(Object parent, int index) {

        JmxConnectionModel model = (JmxConnectionModel) parent;
        
        JmxConnection connection = model.getData();
        if (!connection.isConnected()) {
            return null;
        }
        
        List<DomainModel> domainModels = model.getDomainModels();
        if (domainModels != null && index < domainModels.size()) {
            return domainModels.get(index);
        }

        return null;
    }

    @Override
    public Image getImage(Object element) {
        JmxConnectionModel model = (JmxConnectionModel) element;
        if (model != null && !model.getData().isConnected()) {
            return JmxActivator.getManagedImage(JmxActivator.IMAGE_KEY_OBJECT_JMX_CONNECTION_NOT_CONNECTED);
        }
        
        return JmxActivator.getManagedImage(JmxActivator.IMAGE_KEY_OBJECT_JMX_CONNECTION);
    }

    @Override
    public Image getLargeImage(Object element) {
        JmxConnectionModel model = (JmxConnectionModel) element;        
        if (model != null && !model.getData().isConnected()) {
            return JmxActivator.getManagedImage(JmxActivator.IMAGE_KEY_OBJECT_JMX_CONNECTION_NOT_CONNECTED_LARGE);
        }

        return JmxActivator.getManagedImage(JmxActivator.IMAGE_KEY_OBJECT_JMX_CONNECTION_LARGE);
    }

    @Override
    public String getText(Object element) {

        JmxConnectionModel model = (JmxConnectionModel) element;
        if (model.getOwner() == null) {
            return model.getKey().getName();
        }
        else {
            JMXServiceURL jmxServiceURL = model.getKey().getJmxServiceUrl();
            String host = jmxServiceURL.getHost();
            String urlPath = jmxServiceURL.getURLPath();
            if ((host != null && !host.isEmpty()) || urlPath == null) {
                return String.valueOf(jmxServiceURL);
            }
            else {
                return urlPath;
            }

        }

    }

    @Override
    public String getToolTipText(Object element) {
        JmxConnectionModel model = (JmxConnectionModel) element;
        return String.valueOf(model.getKey().getJmxServiceUrl());
    }

}
