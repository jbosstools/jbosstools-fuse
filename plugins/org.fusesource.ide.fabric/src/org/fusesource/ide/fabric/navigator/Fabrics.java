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

package org.fusesource.ide.fabric.navigator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import io.fabric8.api.FabricService;
import io.fabric8.api.Version;
import org.fusesource.ide.commons.Viewers;
import org.fusesource.ide.commons.tree.HasRefreshableUI;
import org.fusesource.ide.commons.tree.HasViewer;
import org.fusesource.ide.commons.tree.Node;
import org.fusesource.ide.commons.tree.RefreshableCollectionNode;
import org.fusesource.ide.commons.tree.RefreshableUI;
import org.fusesource.ide.commons.ui.ImageProvider;
import org.fusesource.ide.commons.util.Strings;
import org.fusesource.ide.fabric.FabricPlugin;
import org.fusesource.ide.fabric.actions.FabricDetails;
import org.fusesource.ide.fabric.actions.FabricDetailsAddAction;
import org.fusesource.ide.fabric.actions.jclouds.CreateJCloudsFabricAction;
import org.fusesource.ide.jmx.ui.internal.views.navigator.ContextMenuProvider;


public class Fabrics extends RefreshableCollectionNode implements ImageProvider, HasRefreshableUI, HasViewer, ContextMenuProvider {

	public static final String DEFAULT_FABRIC_URL = "http://localhost:8181/jolokia/";
	public static final String DEFAULT_FABRIC_URL_FORMAT = "http://%s:8181/jolokia/";

	public static final String DEFAULT_USERNAME = "karaf";

	public static final String DEFAULT_PASSWORD = "karaf";

	// TODO move to stanard repo before GA!
	public static final String DEFAULT_MAVEN_PROXY_URI = "https://repository.jboss.org/nexus/content/groups/ea";

	private final RefreshableUI refreshableUI;
	private List<Fabric> fabrics = new CopyOnWriteArrayList<Fabric>();
	private List<FabricListener> listeners = new ArrayList<FabricListener>();

	private boolean createLocalFabric;

	private FabricDetailsAddAction addAction;


	public Fabrics(RefreshableUI refreshableUI) {
		super(null);
		this.refreshableUI = refreshableUI;
		addAction = new FabricDetailsAddAction(this);
	}

	public FabricDetailsAddAction getAddAction() {
		return addAction;
	}

	@Override
	public RefreshableUI getRefreshableUI() {
		return refreshableUI;
	}

	@Override
	public Viewer getViewer() {
		return Viewers.getViewer(getRefreshableUI());
	}

	@Override
	public Image getImage() {
		return FabricPlugin.getDefault().getImage("fabric_folder.png");
	}

	@Override
	protected void loadChildren() {
		List<FabricDetails> list = getFabricDetailList();
		for (FabricDetails details : list) {
			addFabric(details);
		}
		if (createLocalFabric && list.isEmpty()) {
			addFabric("Local Fabric", DEFAULT_FABRIC_URL);
		}

		getFabricDetailList().addChangeListener(new IChangeListener() {

			@Override
			public void handleChange(ChangeEvent event) {
				refresh();
			}
		});
	}


	public WritableList getFabricDetailList() {
		return FabricDetails.getDetailList();
	}

	@Override
	public String toString() {
		return "Fabrics";
	}

	public List<Fabric> getFabrics() {
		return fabrics;
	}

	public Fabric getFabric(String name) {
		for (Fabric fabric : fabrics) {
			if (fabric.getName().equals(name)) {
				return fabric;
			}
		}
		return null;
	}

	@Override
	protected void checkLoaded() {
		super.checkLoaded();
		checkConnected();
	}


	private void checkConnected() {
		for (Fabric fabric : fabrics) {
			fabric.isConnected();
		}

	}

	public void addFabricListener(FabricListener listener) {
		listeners.add(listener);
	}

	public void removeFabricListener(FabricListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Adds a new fabric
	 * @return
	 */
	public Fabric addFabric(Fabric fabric) {
		fabrics.add(fabric);
		super.addChild(fabric);

		if (!isLoading()) {
			fireFabricChanged(new FabricEvent(fabric));
		}
		return fabric;
	}

	public Fabric addFabric(FabricDetails details) {
		details.flush();
		Fabric fabric = new Fabric(this, details);
		return addFabric(fabric);
	}

	/**
	 * Adds a new fabric
	 * @return
	 */
	public Fabric addFabric(String fabricName, String uris) {
		FabricDetails details = FabricDetails.newInstance(fabricName, uris);
		FabricPlugin.getLogger().debug("Created fabric details: " + details);
		return addFabric(details);
	}
	
	@Override
	public void provideContextMenu(IMenuManager menu) {
		menu.add(addAction);
		//menu.add(new CreateFabricAction(this));
		menu.add(new CreateJCloudsFabricAction(this));
	}

	protected void fireFabricChanged(FabricEvent fabricEvent) {
		for (FabricListener listener : listeners) {
			listener.onFabricEvent(fabricEvent);
		}
		refreshUI();
	}

	public static String getVersionName(Version version) {
		String text = null;
		if (version != null) {
			text = version.getId();
		}
		return Strings.getOrElse(text, "");
	}

	public static Fabric toFabric(Object object) {
		if (object instanceof Fabric) {
			return (Fabric) object;
		}
		if (object instanceof Node) {
			Node n = (Node) object;
			return toFabric(n.getParent());
		}
		return null;
	}

	public static Version getLatestVersion(FabricService fabricService) {
		Version[] versions = fabricService.getVersions();
		if (versions != null && versions.length > 0) {
			return versions[versions.length - 1];
		}
		return null;
	}

}
