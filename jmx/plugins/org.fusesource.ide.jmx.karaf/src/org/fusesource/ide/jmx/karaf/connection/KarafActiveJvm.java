/******************************************************************************* 
 * Copyright (c) 2014 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.jmx.karaf.connection;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import javax.management.MBeanServerConnection;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osgi.util.NLS;
import org.jboss.tools.jmx.jvmmonitor.core.AbstractJvm;
import org.jboss.tools.jmx.jvmmonitor.core.Activator;
import org.jboss.tools.jmx.jvmmonitor.core.IActiveJvm;
import org.jboss.tools.jmx.jvmmonitor.core.IJvm;
import org.jboss.tools.jmx.jvmmonitor.core.ISWTResourceMonitor;
import org.jboss.tools.jmx.jvmmonitor.core.JvmCoreException;
import org.jboss.tools.jmx.jvmmonitor.core.JvmModel;
import org.jboss.tools.jmx.jvmmonitor.core.JvmModelEvent;
import org.jboss.tools.jmx.jvmmonitor.core.JvmModelEvent.State;
import org.jboss.tools.jmx.jvmmonitor.core.cpu.ICpuProfiler;
import org.jboss.tools.jmx.jvmmonitor.core.mbean.IMBeanServer;
import org.jboss.tools.jmx.jvmmonitor.internal.core.AbstractMBeanServer;
import org.jboss.tools.jmx.jvmmonitor.internal.core.Messages;
import org.jboss.tools.jmx.jvmmonitor.internal.core.SWTResourceMonitor;
import org.jboss.tools.jmx.jvmmonitor.internal.core.Util;
import org.jboss.tools.jmx.jvmmonitor.internal.core.cpu.CpuProfiler;

/**
 * The active JVM.
 */
public class KarafActiveJvm extends AbstractJvm implements IActiveJvm {

    /** The state indicating if attach mechanism is supported. */
    private boolean isAttachSupported;

    /** The state indicating if simply connecting is supported. */
    private boolean isConnectSupported;

    /** The state indicating if JVM is running on remote host. */
    private boolean isRemote;

    /** The state indicating if JVM has agent attached. */
    private boolean isAttached;

    /** The MXBean server. */
    private KarafMBeanServer mBeanServer;

    /** The CPU profiler. */
    private ICpuProfiler cpuProfiler;

    /** The SWT resource monitor. */
    private ISWTResourceMonitor swtResourceMonitor;

    
    private KarafServerConnection jmxConnection;
    
    public KarafActiveJvm(KarafServerConnection connection, IActiveJvm delegate) throws JvmCoreException {
        super(delegate.getPid(), delegate.getHost());
        
        isRemote = false;
        isConnectSupported = true;
        isAttachSupported = true;
        this.jmxConnection = connection;
        initialize();
        connect();
        saveJvmProperties();
    }

    public void connect() throws JvmCoreException {
    	connect(500); // TODO doesn't follow preferences
    }
    
    /*
     * @see IActiveJvm#connect(int)
     */
    @Override
    public void connect(int updatePeriod) throws JvmCoreException {
    	connect(updatePeriod, false);
    }


    /*
     * @see IActiveJvm#connect(int, boolean)
     */
	@Override
	public void connect(int updatePeriod, boolean attach)
			throws JvmCoreException {
    	if( !isConnectSupported ) {
      		 throw new IllegalStateException(Messages.connectNotSupportedMsg);
      	}
        mBeanServer.connect(updatePeriod);
        
        if( attach ) 
        	attach(false);
        
        JvmModel.getInstance().fireJvmModelChangeEvent(
                new JvmModelEvent(State.JvmConnected, this));
	}
    
    @Override
	public void attach() throws JvmCoreException {
    	attach(true);
    }
    
    private void attach(boolean fireEvent) throws JvmCoreException {
        if (!isAttachSupported) {
            throw new IllegalStateException(Messages.attachNotSupportedMsg);
        }

        if( !isAttached ) {
	        if (!isRemote) {
	            JvmModel.getInstance().getAgentLoadHandler().loadAgent(this);
	            isAttached = true;
	        }
	        if (swtResourceMonitor.isSupported()) {
	            swtResourceMonitor.setTracking(true);
	        }
        }
        if( fireEvent ) {
	        JvmModel.getInstance().fireJvmModelChangeEvent(
	                new JvmModelEvent(State.JvmModified, this));
        }
    }
    
    /*
     * @see IActiveJvm#disconnect()
     */
    @Override
    public void disconnect() {
        mBeanServer.dispose();
        try {
            if (swtResourceMonitor.isSupported()) {
                swtResourceMonitor.setTracking(false);
            }
        } catch (JvmCoreException e) {
            // do nothing
        }

        JvmModel.getInstance().fireJvmModelChangeEvent(
                new JvmModelEvent(State.JvmDisconnected, this));
    }
    
    /*
     * @see IActiveJvm#isAttached()
     */
	@Override
	public boolean isAttached() {
		return isAttached;
	}

    /*
     * @see IActiveJvm#isConnected()
     */
    @Override
    public boolean isConnected() {
        return jmxConnection.isConnected();
    }

    /*
     * @see IActiveJvm#isAttachSupported()
     */
    @Override
    public boolean isConnectionSupported() {
        return isAttachSupported;
    }

    /*
     * @see IActiveJvm#getErrorStateMessage()
     */
    @Override
    public String getErrorStateMessage() {
        return null;
    }

    /*
     * @see IActiveJvm#isRemote()
     */
    @Override
    public boolean isRemote() {
        return isRemote;
    }

    /*
     * @see IActiveJvm#getMBeanServer()
     */
    @Override
    public IMBeanServer getMBeanServer() {
        return mBeanServer;
    }

    /*
     * @see IActiveJvm#getCpuProfiler()
     */
    @Override
    public ICpuProfiler getCpuProfiler() {
        return cpuProfiler;
    }

    /*
     * @see IActiveJvm#getSWTResourceMonitor()
     */
    @Override
    public ISWTResourceMonitor getSWTResourceMonitor() {
        return swtResourceMonitor;
    }

    /*
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return getMainClass();
    }

    /**
     * Saves the JVM properties.
     */
    @Override
	public void saveJvmProperties() {
        IFileStore fileStore;
        try {
            fileStore = Util.getFileStore(IJvm.PROPERTIES_FILE,
                    getPersistenceDirectory());
            if (fileStore.fetchInfo().exists()) {
                return;
            }
        } catch (JvmCoreException e) {
            Activator.log(IStatus.ERROR, Messages.savePropertiesFileFailedMsg,
                    e);
            return;
        }

        Properties props = new Properties();
        OutputStream os = null;
        try {
            os = fileStore.openOutputStream(EFS.NONE, null);

            int pid = getPid();
            int port = getPort();
            String mainClass = getMainClass();

            props.setProperty(IJvm.PID_PROP_KEY, String.valueOf(pid));
            props.setProperty(IJvm.PORT_PROP_KEY, String.valueOf(port));
            if (mainClass != null) {
                props.setProperty(IJvm.MAIN_CLASS_PROP_KEY, mainClass);
            }
            props.setProperty(IJvm.HOST_PROP_KEY, getHost().getName());

            props.storeToXML(os, "JVM Properties"); //$NON-NLS-1$
        } catch (CoreException e) {
            Activator.log(IStatus.ERROR, NLS.bind(
                    Messages.openOutputStreamFailedMsg, fileStore.toURI()
                            .getPath()), e);
        } catch (IOException e) {
            try {
                fileStore.delete(EFS.NONE, null);
            } catch (CoreException e1) {
                // do nothing
            }
            Activator.log(IStatus.ERROR, NLS.bind(
                    Messages.writePropertiesFileFailedMsg, fileStore.toURI()
                            .getPath()), e);
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    // do nothing
                }
            }
        }
    }
    
    /**
     * Initializes the active JVM.
     * 
     */
    private void initialize() {
        mBeanServer = createMBeanServer();
        cpuProfiler = new CpuProfiler(this);
        swtResourceMonitor = new SWTResourceMonitor(this);
    }
    
    
    private static class KarafMBeanServer extends AbstractMBeanServer {

		private KarafServerConnection jmxConn;
		protected KarafMBeanServer(KarafServerConnection connection, IActiveJvm jvm) {
			super(jvm);
			setJvmReachable(true);
			this.jmxConn = connection;
		}


		@Override
		public MBeanServerConnection getConnection() {
			return jmxConn.getActiveConnection();
		}

		@Override
		protected MBeanServerConnection createMBeanServerConnection()
				throws JvmCoreException {
			return getConnection();
		}
	    @Override
		public void connect(int updatePeriod) throws JvmCoreException {
	    	super.connect(updatePeriod);
	    }		
	    @Override
		public void dispose() {
	    	super.dispose();
	    }
    }
    
    private KarafMBeanServer createMBeanServer() {
    	return new KarafMBeanServer(jmxConnection, this);
    }
}
