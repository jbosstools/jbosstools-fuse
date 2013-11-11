/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.server.view;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.widgets.Display;
import org.eclipse.tm.internal.terminal.provisional.api.ISettingsStore;
import org.eclipse.tm.internal.terminal.provisional.api.ITerminalConnector;
import org.eclipse.tm.internal.terminal.provisional.api.PreferenceSettingStore;
import org.eclipse.tm.internal.terminal.provisional.api.TerminalConnectorExtension;
import org.eclipse.tm.internal.terminal.ssh.SshConnector;
import org.eclipse.tm.internal.terminal.ssh.SshSettings;
import org.eclipse.tm.internal.terminal.view.TerminalView;
import org.eclipse.ui.IViewPart;
import org.fusesource.ide.commons.util.Objects;


/**
 * @author lhein
 */
public class SshView extends TerminalView {

	private static final int MAX_RETRIES = 3;
	private static final int DELAY = 5000;

	private List<ITerminalConnectionListener> connectionListeners = new LinkedList<ITerminalConnectionListener>();

	private SshConnector sshc;
	private boolean connected = false;

	public void addConnectionListener(ITerminalConnectionListener listener) {
		if (!this.connectionListeners.contains(listener)) {
			this.connectionListeners.add(listener);
		}
	}

	public void removeConnectionListener(ITerminalConnectionListener listener) {
		this.connectionListeners.remove(listener);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.ViewPart#setPartName(java.lang.String)
	 */
	@Override
	public void setPartName(String partName) {
		super.setPartName("Shell");
	}

	public void createConnectionIfNotExists(String host, int port, String user,
			String pass) throws Exception {

		// open the terminal view
		IViewPart vp = ServerViewPlugin.openTerminalView();
		if (vp == null || vp instanceof TerminalView == false) {
			ServerViewPlugin.getLogger().error("Unable to open the terminal view!");
			return;
		}

		// get the view
		final TerminalView connectorView = (TerminalView)vp;

		ITerminalConnector conn = TerminalConnectorExtension.makeTerminalConnector("org.eclipse.tm.internal.terminal.ssh.SshConnector");
		if (conn != null) {
			// force instantiating the real connector
			conn.makeSettingsPage();
			sshc = (SshConnector) conn.getAdapter(SshConnector.class);
			if (sshc != null) {
				SshSettings settings = (SshSettings)sshc.getSshSettings();
				settings.setHost(host);
				if (user != null) {
					settings.setUser(user);
				}
				if (pass != null) {
					settings.setPassword(pass);
				}
				settings.setPort("" + port);
				settings.setKeepalive("300");
				settings.setTimeout("45");

				try {
					Method mGetStore = Objects.getMethodDescriptor(connectorView, "getPreferenceSettingsStore", TerminalView.class, null);
					PreferenceSettingStore store = (PreferenceSettingStore)mGetStore.invoke(connectorView, null);
					// When the settings dialog is closed, we persist the Terminal settings.
					Method mSave = Objects.getMethodDescriptor(connectorView, "saveSettings", TerminalView.class, ISettingsStore.class, ITerminalConnector.class);
					// we also save it in the preferences. This will keep the last change
					// made to this connector as default...
					mSave.invoke(connectorView, store, conn);

					Method mSetCon = Objects.getMethodDescriptor(connectorView, "setConnector", TerminalView.class, ITerminalConnector.class);
					mSetCon.invoke(connectorView, conn);
					Display.getDefault().syncExec(new Runnable() {
						public void run() {
							try {
								int cnt = 0;
								while (sshc.getInputStream() == null && cnt < MAX_RETRIES) {

									// try to connect
									onTerminalConnect();

									if (sshc.getInputStream() == null) {
										// wait some seconds before trying to connect otherwise SSHD wouldn't be ready
										try {
											Thread.sleep(DELAY);
										} catch (InterruptedException e) {
											e.printStackTrace();
										} finally {
											cnt++;
										}
									}
								}

							} catch (Exception ex) {
								ex.printStackTrace();
							}
						}
					});

				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.view.TerminalView#updateStatus()
	 */
	@Override
	public void updateStatus() {
		super.updateStatus();
		if (this.connected == true && !fCtlTerminal.isConnected()) {
			onTerminalDisconnect();
		}
		this.connected = fCtlTerminal.isConnected();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.tm.internal.terminal.view.TerminalView#onTerminalConnect()
	 */
	@Override
	public void onTerminalConnect() {
		super.onTerminalConnect();
		fireOnConnect();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.tm.internal.terminal.view.TerminalView#onTerminalDisconnect()
	 */
	@Override
	public void onTerminalDisconnect() {
		try {
			super.onTerminalDisconnect();
			fireOnDisconnect();
			Display.getDefault().syncExec(new Runnable() {
				
				@Override
				public void run() {
					// clear the console view to make it clear to the user that the connection ended
					fActionEditClearAll.run();					
				}
			});
		} catch (Exception ex) {
			ServerViewPlugin.getLogger().error("Problem occured while disconnecting from SSH terminal...", ex);
		}
	}

	private void fireOnConnect() {
		for (ITerminalConnectionListener l : this.connectionListeners) {
			l.onConnect();
		}
	}

	private void fireOnDisconnect() {
		for (ITerminalConnectionListener l : this.connectionListeners) {
			l.onDisconnect();
		}
	}
}