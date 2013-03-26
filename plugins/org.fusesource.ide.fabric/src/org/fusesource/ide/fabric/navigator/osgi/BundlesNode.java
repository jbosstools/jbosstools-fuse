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

package org.fusesource.ide.fabric.navigator.osgi;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.management.openmbean.TabularData;

import org.apache.maven.model.Model;
import org.eclipse.core.resources.IProject;
import org.eclipse.debug.internal.ui.views.console.ProcessConsole;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.views.properties.IPropertySource;
import org.fusesource.ide.commons.Viewers;
import org.fusesource.ide.commons.tree.Node;
import org.fusesource.ide.commons.tree.RefreshableCollectionNode;
import org.fusesource.ide.commons.tree.Refreshables;
import org.fusesource.ide.commons.ui.ImageProvider;
import org.fusesource.ide.commons.ui.Workbenches;
import org.fusesource.ide.commons.ui.drop.DropHandler;
import org.fusesource.ide.commons.ui.drop.DropHandlerFactory;
import org.fusesource.ide.commons.ui.propsrc.PropertySourceTableSheetPage;
import org.fusesource.ide.deployment.maven.MavenUtils;
import org.fusesource.ide.deployment.maven.ProjectDropHandler;
import org.fusesource.ide.deployment.maven.ProjectDropTarget;
import org.fusesource.ide.fabric.FabricPlugin;
import org.fusesource.ide.fabric.navigator.maven.FabricInstallAction;
import org.fusesource.ide.launcher.ui.ExecutePomActionPostProcessor;
import org.fusesource.ide.launcher.ui.ExecutePomActionSupport;

import com.google.common.base.Objects;

public class BundlesNode extends RefreshableCollectionNode implements ImageProvider, ProjectDropTarget, DropHandlerFactory {
	private final OsgiFacade facade;
	private String bundlefilterText;
	private boolean startOnDeploy = true;

	public BundlesNode(Node parent, OsgiFacade facade) {
		super(parent);
		this.facade = facade;
	}

	@Override
	public String toString() {
		return "Bundles";
	}


	@Override
	public Image getImage() {
		return FabricPlugin.getDefault().getImage("bundle.png");
	}

	@Override
	protected void loadChildren() {
	}


	public OsgiFacade getFacade() {
		return facade;
	}

	@Override
    public boolean requiresContentsPropertyPage() {
        return false;
    }

    @Override
	protected PropertySourceTableSheetPage createPropertySourceTableSheetPage() {
		return new BundlesTableSheetPage(this);
	}


	@Override
	public List<IPropertySource> getPropertySourceList() {
		List<IPropertySource> answer = new ArrayList<IPropertySource>();
		try {
			final TabularData tabularData = facade.listBundles();
			return TabularDataHelper.toPropertySources(tabularData);
		} catch (Exception e) {
			FabricPlugin.getLogger().error("Failed to fetch bundle state: " + e, e);
		}
		return answer;
	}

	@Override
	public DropHandler createDropHandler(DropTargetEvent event) {
		return new ProjectDropHandler(this);
	}

	@Override
	public void dropProject(IProject project, final Model mavenModel) {
		if (mavenModel != null) {

			ExecutePomActionSupport action = new FabricInstallAction();

			// set a post processor
			action.setPostProcessor(new ExecutePomActionPostProcessor() {
				/* (non-Javadoc)
				 * @see org.fusesource.ide.launcher.ui.ExecutePomActionPostProcessor#executeOnFailure()
				 */
				@Override
				public void executeOnFailure() {
				}

				/* (non-Javadoc)
				 * @see org.fusesource.ide.launcher.ui.ExecutePomActionPostProcessor#executeOnSuccess()
				 */
				@Override
				public void executeOnSuccess() {
					String uri = MavenUtils.getBundleURI(mavenModel);
					if ( uri != null) {
						try {
							Long foundId = null;
							List<IPropertySource> bundleList = getPropertySourceList();
							for (IPropertySource propertySource : bundleList) {
								BundleStateFacade bundleState = new BundleStateFacade(propertySource);
								String location = bundleState.getLocation();
								if (Objects.equal(location, uri)) {
									foundId = bundleState.getId();
								}
							}



							final Long id;
							if (foundId == null) {
								id = getFacade().installBundle(uri, startOnDeploy);
							} else {
								id = foundId;
								getFacade().updateBundleFromURL(foundId, uri);
							}
							Viewers.async(new Runnable() {

								@Override
								public void run() {
									refresh();
									IPage page = Workbenches.getPropertySheetPage();
									if (id != null && page instanceof BundlesTableSheetPage) {
										BundlesTableSheetPage bundlePage = (BundlesTableSheetPage) page;
										bundlePage.setSelectedBundleIds(Collections.singleton(id));
									}
								}});
						} catch (Exception e) {
							boolean done = false;
							ConsolePlugin plugin = ConsolePlugin.getDefault();
							if (plugin != null) {
								IConsoleManager conMan = plugin.getConsoleManager();
								IConsole[] consoles = conMan.getConsoles();
								for (int i = 0; i < consoles.length; i++) {
									IConsole console = consoles[i];
									if (console instanceof ProcessConsole) {
										ProcessConsole messageConsole = (ProcessConsole) console;
										IOConsoleOutputStream ios = messageConsole.newOutputStream();
										PrintWriter out = new PrintWriter(ios);
										out.println();
										out.println("FAILED to install into OSGi: " + uri);
										out.println(e.getMessage());
										e.printStackTrace(out);
										Set<Throwable> exceptions = new HashSet<Throwable>();
										Throwable t = e;
										boolean showAllCauses = false;
										while (true) {
											exceptions.add(t);
											Throwable c = t.getCause();
											if (c == null || c == t && exceptions.contains(c) || !showAllCauses) {
												break;
											} else {
												out.println("Caused by: " + c);
												c.printStackTrace(out);
												t = c;
											}
										}
										out.flush();
										done = true;
									}
								}
							}

							if (!done) {
								FabricPlugin.getLogger().warning("Failed to update bundle " + uri + ". " + e, e);
							}
						}
					}
				}
			});

			MavenUtils.launch(action);
		}
	}

	@Override
	public void refresh() {
		super.refresh();

		IPage currentPage = Workbenches.getPropertySheetPage();
		Refreshables.refresh(currentPage);
	}

	public String getBundlefilterText() {
		return bundlefilterText;
	}

	public void setBundlefilterText(String bundlefilterText) {
		this.bundlefilterText = bundlefilterText;
	}

	public void setBundleFilterText(String bundlefilterText) {
		this.bundlefilterText = bundlefilterText;
	}


}