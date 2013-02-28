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

package org.fusesource.ide.deployment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.ServerCore;
import org.fusesource.ide.commons.ui.Selections;
import org.fusesource.ide.commons.ui.UIConstants;
import org.fusesource.ide.commons.ui.drop.DeployMenuProvider;
import org.fusesource.ide.commons.util.Objects;
import org.fusesource.ide.deployment.config.HotfolderDeploymentConfiguration;
import org.fusesource.ide.deployment.handler.DeploymentHandler;


/**
 * @author lhein
 */
public class DeploymentContributionItem extends ContributionItem {

	private static final String CONFIGURE_DEPLOYMENTS_ID = "org.fusesource.ide.deployment.edit.command";
	private static final String EXECUTE_DEPLOYMENT_ID = "org.fusesource.ide.deployment.command";

	private Menu menu;
	private static Object previousSelectedData;
	private static final boolean showMenuIfNoPreviousSelection = true;


	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.ContributionItem#fill(org.eclipse.swt.widgets.Menu, int)
	 */
	@Override
	public void fill(final Menu menu, int index) {
		super.fill(menu, index);

		IProject resource = getProjectSelection();
		if (resource != null) {
			MenuItem subMenuItem = new MenuItem(menu, SWT.CASCADE);
			subMenuItem.setText(Messages.deployToolbarLabel);
			subMenuItem.setImage(DeployPlugin.getDefault().getImage("deploy.png"));

			Menu subMenu = new Menu(menu);
			subMenuItem.setMenu(subMenu);

			recreateMenu(subMenu, resource);
		}

	}


	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.ContributionItem#fill(org.eclipse.swt.widgets.ToolBar, int)
	 */
	@Override
	public void fill(final ToolBar parent, int index) {
		super.fill(parent, index);

		// lets find out the current selected
		final ToolItem item = new ToolItem(parent, SWT.DROP_DOWN);
		item.setToolTipText(Messages.deployToolbarTooltip);
		item.setImage(DeployPlugin.getDefault().getImage(DeployPlugin.DEPLOY_ICON));
		item.addListener(SWT.Selection, new Listener() {
			/*
			 * (non-Javadoc)
			 * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
			 */
			@Override
			public void handleEvent(Event event) {
				recreateMenu(parent);
				if (event.detail == SWT.ARROW) {
					Rectangle bounds = item.getBounds();
					Point point = parent.toDisplay(bounds.x, bounds.y + bounds.height);
					menu.setLocation(point);
					menu.setVisible(true);
				} else {

					// lets find the menu with the matching previous selection.
					MenuItem menuItem = findMenuItem(menu, previousSelectedData);
					if (menuItem != null){
						menuItem.notifyListeners(SWT.Selection, new Event());
						return;
					} else {
						// lets clear old garbage to be nice to the GC
						previousSelectedData = null;

						if (showMenuIfNoPreviousSelection) {
							Rectangle bounds = item.getBounds();
							Point point = parent.toDisplay(bounds.x, bounds.y + bounds.height);
							menu.setLocation(point);
							menu.setVisible(true);
						} else {

							// execute the default deployment
							HotfolderDeploymentConfiguration[] configs = determineDeploymentConfigurations();
							for (HotfolderDeploymentConfiguration cfg : configs) {
								if (cfg.isDefaultConfig()) {
									ICommandService commandService = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
									try {
										HashMap params = new HashMap();
										params.put(DeploymentHandler.DEPLOY_PARAMETER_KEY, cfg);
										ExecutionEvent evt = new ExecutionEvent(commandService.getCommand(EXECUTE_DEPLOYMENT_ID), params, null, null);
										commandService.getCommand(EXECUTE_DEPLOYMENT_ID).executeWithChecks(evt);
										return;
									} catch (Exception ex) {
										DeployPlugin.getLogger().error(ex);
										throw new RuntimeException("Execution exception occured: " + ex.getMessage());
									}
								}
							}

							// no default configuration so lets open the configuration UI
							openConfigurationUI();
						}
					}

				}
			}
		});
	}


	private void recreateMenu(final ToolBar parent) {
		menu = new Menu(parent.getShell(), SWT.POP_UP);

		IProject resource = getLastProjectSelection();
		if (resource != null) {
			fillEsbDeployMenus(menu, resource);
		}

		// get the defined hotdeploy folders from the preference page
		HotfolderDeploymentConfiguration[] configs = determineDeploymentConfigurations();
		for (final HotfolderDeploymentConfiguration cfg : configs) {
			MenuItem i = new MenuItem(menu, SWT.PUSH);
			i.setText(cfg.getName());
			i.setImage(DeployPlugin.getDefault().getImage("folder.gif"));
			i.setData(cfg.getHotDeployPath());
			i.addSelectionListener(new SelectionAdapter() {
				/* (non-Javadoc)
				 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
				 */
				@Override
				public void widgetSelected(SelectionEvent e) {
					ICommandService commandService = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
					try {
						HashMap params = new HashMap();
						params.put(DeploymentHandler.DEPLOY_PARAMETER_KEY, cfg);
						ExecutionEvent evt = new ExecutionEvent(commandService.getCommand(EXECUTE_DEPLOYMENT_ID), params, null, null);
						commandService.getCommand(EXECUTE_DEPLOYMENT_ID).executeWithChecks(evt);
					} catch (Exception ex) {
						DeployPlugin.getLogger().error(ex);
						throw new RuntimeException("Execution exception occured: " + ex.getMessage());
					}
				}
			});
		}

		// create a separator if we got any entries
		if (configs.length > 0) {
			new MenuItem(menu, SWT.SEPARATOR);
		}

		// then create the menu itself from the values we determined so far
		MenuItem cfgItem = new MenuItem(menu, SWT.PUSH);
		cfgItem.setText(Messages.configureDeploymentsMenuLabel);
		cfgItem.addSelectionListener(new SelectionAdapter() {
			/*
			 * (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				openConfigurationUI();
			}
		});
		addSelectionListeners(menu);
	}


	protected IProject getLastProjectSelection() {
		IProject firstSelection = getProjectSelection();
		if (!(firstSelection instanceof IProject)) {
			firstSelection = DeployViews.getActiveProject();
		}
		return firstSelection;
	}


	protected IProject getProjectSelection() {
		return DeployViews.selectionToProject(Selections.getWorkbenchSelection());
	}

	private void addSelectionListeners(Menu menu) {
		MenuItem[] items = menu.getItems();
		if (items != null) {
			for (MenuItem menuItem : items) {
				final Object data = menuItem.getData();
				if (data != null) {
					menuItem.addSelectionListener(new SelectionListener() {

						@Override
						public void widgetSelected(SelectionEvent e) {
							if (data != null) {
								previousSelectedData = data;
							}
						}

						@Override
						public void widgetDefaultSelected(SelectionEvent e) {
						}
					});
				}
				Menu child = menuItem.getMenu();
				if (child != null) {
					addSelectionListeners(child);
				}
			}
		}
	}


	protected MenuItem findMenuItem(Menu menu, Object selectData) {
		if (selectData != null) {
			MenuItem[] items = menu.getItems();
			if (items != null) {
				for (MenuItem menuItem : items) {
					Object data = menuItem.getData();
					if (Objects.equal(data, selectData)) {
						return menuItem;
					}
					Menu child = menuItem.getMenu();
					if (child != null) {
						MenuItem childItem = findMenuItem(child, selectData);
						if (childItem != null) {
							return childItem;
						}
					}
				}
			}
		}
		return null;
	}

	private void recreateMenu(final Menu parent, IResource resource) {
		fillEsbDeployMenus(parent, resource);

		// get the defined hotdeploy folders from the preference page
		HotfolderDeploymentConfiguration[] configs = determineDeploymentConfigurations();
		for (final HotfolderDeploymentConfiguration cfg : configs) {
			MenuItem i = new MenuItem(parent, SWT.PUSH);
			i.setText(cfg.getName());
			i.setImage(DeployPlugin.getDefault().getImage("folder.gif"));
			i.addSelectionListener(new SelectionAdapter() {
				/* (non-Javadoc)
				 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
				 */
				@Override
				public void widgetSelected(SelectionEvent e) {
					ICommandService commandService = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
					try {
						HashMap params = new HashMap();
						params.put(DeploymentHandler.DEPLOY_PARAMETER_KEY, cfg);
						ExecutionEvent evt = new ExecutionEvent(commandService.getCommand(EXECUTE_DEPLOYMENT_ID), params, null, null);
						commandService.getCommand(EXECUTE_DEPLOYMENT_ID).executeWithChecks(evt);
					} catch (Exception ex) {
						DeployPlugin.getLogger().error(ex);
						throw new RuntimeException("Execution exception occured: " + ex.getMessage());
					}
				}
			});
		}

		// create a separator if we got any entries
		if (configs.length > 0) {
			new MenuItem(parent, SWT.SEPARATOR);
		}

		// then create the menu itself from the values we determined so far
		MenuItem cfgItem = new MenuItem(parent, SWT.PUSH);
		cfgItem.setText(Messages.configureDeploymentsMenuLabel);
		cfgItem.addSelectionListener(new SelectionAdapter() {
			/*
			 * (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				openConfigurationUI();
			}
		});
		addSelectionListeners(parent);
	}


	protected void fillEsbDeployMenus(final Menu parent, IResource resource) {
		if (resource != null) {
			IViewPart fabricNavigator = DeployViews.findView(UIConstants.FABRIC_EXPLORER_VIEW_ID);
			IViewPart jmxExplorer = DeployViews.findView(UIConstants.JMX_EXPLORER_VIEW_ID);

			if (fabricNavigator instanceof DeployMenuProvider) {
				DeployMenuProvider menuProvider = (DeployMenuProvider) fabricNavigator;
				menuProvider.appendDeployActions(parent, resource);
			}

			if (jmxExplorer instanceof DeployMenuProvider) {
				DeployMenuProvider menuProvider = (DeployMenuProvider) jmxExplorer;
				menuProvider.appendDeployActions(parent, resource);
			}
		}
	}

	protected void openConfigurationUI() {
		IHandlerService handlerService = (IHandlerService) PlatformUI.getWorkbench().getService(IHandlerService.class);
		try {
			handlerService.executeCommand(CONFIGURE_DEPLOYMENTS_ID, new Event());
		} catch (Exception ex) {
			DeployPlugin.getLogger().error(ex);
			throw new RuntimeException("Error executing configure command: " + ex.getMessage());
		}
	}

	/**
	 * this method provides all hotdeploy configurations from both the preferences page
	 * and also from the defined supported servers / runtimes
	 * 
	 * @return	an array of configurations (never null but maybe empty)
	 */
	protected HotfolderDeploymentConfiguration[] determineDeploymentConfigurations() {
		List<HotfolderDeploymentConfiguration> configs = new ArrayList<HotfolderDeploymentConfiguration>();

		// first load the definitions from the hotdeployment preference page storage
		HotfolderDeploymentConfiguration[] storedConfigs = ConfigurationUtils.loadPreferences();
		for (HotfolderDeploymentConfiguration cfg : storedConfigs) {
			configs.add(cfg);
		}

		// now check for Karaf server definitions and add them as well
		for (IRuntime rt : ServerCore.getRuntimes()) {
			if (rt != null) {
				IRuntimeType runtimeType = rt.getRuntimeType();
				if (runtimeType != null) {
					String id = runtimeType.getId();
					if (id != null) {
						if (id.startsWith("org.fusesource.ide.server.karaf.runtime.") ||
								id.startsWith("org.fusesource.ide.server.smx.runtime.")) {
							// create a dummy config item for that server
							HotfolderDeploymentConfiguration cfg = new HotfolderDeploymentConfiguration();
							cfg.setName(rt.getName() + " (" + rt.getLocation().toOSString() + ")");
							cfg.setDescription(rt.getLocation().toOSString());
							cfg.setHotDeployPath(rt.getLocation().append("deploy").toOSString());

							// and add it to the list
							configs.add(cfg);
						}
					}
				}
			}
		}
		return configs.toArray(new HotfolderDeploymentConfiguration[configs.size()]);
	}
}
