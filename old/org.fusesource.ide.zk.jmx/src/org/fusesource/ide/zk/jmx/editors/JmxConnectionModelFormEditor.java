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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PartInitException;
import org.fusesource.ide.zk.jmx.JmxActivator;
import org.fusesource.ide.zk.jmx.data.JmxConnectionDescriptor;
import org.fusesource.ide.zk.jmx.model.JmxConnectionModel;
import org.fusesource.ide.zk.core.editors.DataModelFormEditor;
import org.fusesource.ide.zk.core.model.GenericDataModelEvent;

import java.net.MalformedURLException;

import javax.management.remote.JMXServiceURL;


/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public class JmxConnectionModelFormEditor extends DataModelFormEditor<JmxConnectionModel> {

    public static final String ID = JmxConnectionModelFormEditor.class.getName();

    private JmxConnectionModelMainFormPage _MainPage;
    private JmxConnectionModelDomainsFormPage _DomainsPage;

    @Override
    public void doSave(IProgressMonitor monitor) {

        JmxConnectionModel model = getModel();

        if (model.isDestroyed()) {
            return;
        }

        JmxConnectionDescriptor descriptor = model.getKey();

        JMXServiceURL serviceUrl = descriptor.getJmxServiceUrl();
        String userName = descriptor.getUserName();
        String password = descriptor.getPassword();

        String errorMessageTitle = "Save Failed";

        if (_MainPage.isDirty()) {

            try {
                serviceUrl = _MainPage.getServiceUrl();
            }
            catch (MalformedURLException e) {
                MessageDialog.openError(getSite().getShell(), errorMessageTitle, "Invalid Service URL value: "
                        + e.getLocalizedMessage());
                monitor.setCanceled(true);
                return;
            }

            userName = _MainPage.getUserName();
            password = _MainPage.getPassword();
        }

        try {

            if (_MainPage.isDirty()) {
                descriptor.setJmxServiceUrl(serviceUrl);
                descriptor.setUserName(userName);
                descriptor.setPassword(password);
            }

            model.updateData();

            saveCompleted();
        }
        catch (Exception e) {
            JmxActivator.reportError(e);
            monitor.setCanceled(true);
        }
    }

    @Override
    protected void addPages() {
        try {
            _MainPage = new JmxConnectionModelMainFormPage(this);
            addPage(_MainPage);

            updateDomainsPage();
        }
        catch (PartInitException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected void modelDataChanged(GenericDataModelEvent event) {       
        super.modelDataChanged(event);
        
        updateDomainsPage();
    }

    @Override
    protected void modelDataRefreshed(GenericDataModelEvent event) {
        super.modelDataRefreshed(event);
        
        updateDomainsPage();
    }

    @Override
    protected void modelDestroyed(GenericDataModelEvent event) {
        super.modelDestroyed(event);
        close(false);
    }
    
    private void updateDomainsPage() {
        
        if (getModel().getData().isConnected()) {            
            if (_DomainsPage == null) {
                _DomainsPage = new JmxConnectionModelDomainsFormPage(this);
                try {
                    addPage(_DomainsPage);
                }
                catch (PartInitException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }            
        }   
        else if (_DomainsPage != null && getPageCount() > 1) {
            removePage(1);
            _DomainsPage.dispose();
            _DomainsPage = null;
        }
    }
    
}
