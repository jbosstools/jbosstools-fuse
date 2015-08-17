
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

package org.fusesource.ide.camel.editor.provider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.palette.IToolEntry;
import org.eclipse.graphiti.palette.impl.ObjectCreationToolEntry;
import org.eclipse.graphiti.tb.ContextMenuEntry;
import org.eclipse.graphiti.tb.IContextMenuEntry;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.fusesource.ide.camel.editor.AbstractNodes;
import org.fusesource.ide.camel.editor.Activator;
import org.fusesource.ide.camel.editor.EditorMessages;
import org.fusesource.ide.camel.editor.Messages;
import org.fusesource.ide.camel.editor.commands.DiagramOperations;
import org.fusesource.ide.camel.editor.editor.RiderDesignEditor;
import org.fusesource.ide.camel.editor.features.create.ext.CreateFigureFeature;
import org.fusesource.ide.camel.editor.features.custom.CreateNodeConnectionFeature;
import org.fusesource.ide.camel.editor.provider.ext.PaletteCategoryItemProvider;
import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.Endpoint;
import org.fusesource.ide.camel.model.RouteContainer;
import org.fusesource.ide.camel.model.RouteSupport;
import org.fusesource.ide.camel.model.catalog.CamelModelFactory;
import org.fusesource.ide.camel.model.catalog.eips.Eip;
import org.fusesource.ide.camel.model.generated.Route;
import org.fusesource.ide.camel.model.generated.UniversalEIPNode;
import org.fusesource.ide.commons.camel.tools.BeanDef;
import org.fusesource.ide.commons.util.Strings;


public abstract class AbstractAddNodeMenuFactory {

	protected static final String DEFAULT_IMAGE_KEY = "org.fusesource.demo.icons.generic.png_small";
	private AbstractNode selectedNode;
	private RiderDesignEditor editor;

	// Graphiti context menu

	/**
	 * setup the menu structure for the addNode context menu
	 * 
	 * @param rootMenu	the root menu entry (addNode)
	 * @param context	the context
	 * @param fp		the feature provider
	 */
	public void setupMenuStructure(ContextMenuEntry rootMenu, ICustomContext context, IFeatureProvider fp, ArrayList<IToolEntry> toolEntries) {
		AbstractNode selectedNode = getSelectedNode(context, fp);
		boolean onlyEndpoints = false;
		if (selectedNode instanceof Route || selectedNode == null) {
			// commented out as we already have a "add route" menu entry
			//addMenuItem(rootMenu, Messages.paletteRouteTitle, Messages.paletteRouteDescription, Route.class, context, fp);
			onlyEndpoints = true;
		}

		ContextMenuEntry connectorsEntry = new ContextMenuEntry(null, null);
		connectorsEntry.setText(Messages.connectorsDrawerTitle);
		connectorsEntry.setSubmenu(true);

		ContextMenuEntry endpointsEntry = new ContextMenuEntry(null, null);
		endpointsEntry.setText(Messages.endpointsDrawerTitle);
		endpointsEntry.setSubmenu(true);

		ContextMenuEntry routingEntry = new ContextMenuEntry(null, context);
		routingEntry.setText(Messages.routingDrawerTitle);
		routingEntry.setSubmenu(true);

		ContextMenuEntry controlFlowEntry = new ContextMenuEntry(null, context);
		controlFlowEntry.setText(Messages.controlFlowDrawerTitle);
		controlFlowEntry.setSubmenu(true);

		ContextMenuEntry transformationEntry = new ContextMenuEntry(null, context);
		transformationEntry.setText(Messages.transformationDrawerTitle);
		transformationEntry.setSubmenu(true);

		ContextMenuEntry miscEntry = new ContextMenuEntry(null, context);
		miscEntry.setText(Messages.miscellaneousDrawerTitle);
		miscEntry.setSubmenu(true);

		// then we need to fill the shelves menus
		fillEndpointsContextMenu(connectorsEntry, context, fp);
		// now add additional entries to connectors category
		fillAdditionalEndpointsContextMenu(connectorsEntry, context, fp, toolEntries);
		
		if (!onlyEndpoints) {
			fillRoutingContextMenu(routingEntry, context, fp);
			fillControlFlowContextMenu(controlFlowEntry, context, fp);
			fillTransformationContextMenu(transformationEntry, context, fp);
			fillMiscellaneousContextMenu(miscEntry, context, fp);
		}

		// lets find what endpoints are available...
		if (selectedNode != null) {
			Set<Endpoint> endpoints = AbstractNodes.getAllEndpoints(selectedNode);
			Map<String, BeanDef> beans = AbstractNodes.getAllBeans(selectedNode);
			if (endpoints.size() > 0 || beans.size() > 0) {
				/*
				// TODO add a separator...
				ContextMenuEntry menuEntry = new ContextMenuEntry(null, null);
				menuEntry.setSubmenu(false);
				// add entry to parent
				endpointsEntry.add(menuEntry);
				 */
			}
			if (endpoints.size() > 0) {
				boolean useChildMenu = false;
				if (useChildMenu) {
					ContextMenuEntry values = new ContextMenuEntry(null, null);
					values.setText(Messages.endpointsDrawerTitle);
					values.setSubmenu(true);

					addEndpointInstances(values, endpoints, context, fp);
					endpointsEntry.add(values);
				} else {
					addEndpointInstances(endpointsEntry, endpoints, context, fp);
				}
			}
			if (beans.size() > 0) {
				boolean useChildMenu = false;
				if (useChildMenu) {
					ContextMenuEntry values = new ContextMenuEntry(null, null);
					values.setText(Messages.endpointsDrawerTitle);
					values.setSubmenu(true);

					addBeanInstances(values, beans, context, fp);
					endpointsEntry.add(values);
				} else {
					addBeanInstances(endpointsEntry, beans, context, fp);
				}
			}
		}

		// sort connectors
		sortMenuByItemName(connectorsEntry);
		// sort endpoints
		sortMenuByItemName(endpointsEntry);
		
		// and finally we add the shelves entries to the parent menu
		rootMenu.add(connectorsEntry);
		rootMenu.add(endpointsEntry);
		rootMenu.add(routingEntry);
		rootMenu.add(controlFlowEntry);
		rootMenu.add(transformationEntry);
		rootMenu.add(miscEntry);
	}


	private void sortMenuByItemName(ContextMenuEntry menuEntry) {
	 // sort endpoints
	    Arrays.sort(menuEntry.getChildren(), new Comparator<IContextMenuEntry>() {
		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(IContextMenuEntry o1, IContextMenuEntry o2) {
		    return o1.getText().compareToIgnoreCase(o2.getText());
		}
	    });
	}
	
	protected void addBeanInstances(ContextMenuEntry menu, Map<String, BeanDef> beans, ICustomContext context,
			IFeatureProvider fp) {
		ArrayList<String> processedBeans = new ArrayList<String>();
		
		for (Map.Entry<String,BeanDef> entry : beans.entrySet()) {
			final String name = entry.getKey();
			final String aClass = beans.get(name).getClassName();
			if ((Strings.isBlank(name) && Strings.isBlank(aClass)) || !beans.get(name).getBeanType().equalsIgnoreCase("bean")) {
				continue;
			}
			if (processedBeans.contains(name)) continue;
			processedBeans.add(name);
			
			String description = "bean '" + name + "' of type " + aClass;
			
			String version =  CamelModelFactory.getCamelVersion(null); 
			final Eip eip = CamelModelFactory.getModelForVersion(version).getEipModel().getEIPByClass("bean");
			
			CreateNodeConnectionFeature f = new CreateNodeConnectionFeature(fp, eip) {
				@Override
				protected AbstractNode createNode(ICustomContext ctx) throws Exception {
					UniversalEIPNode bean = new UniversalEIPNode(eip);
					bean.setName(name);
			    	bean.setShortPropertyValue("ref", name);
			    	bean.setShortPropertyValue("beanType", aClass);
					return bean;
				}
			};
			addMenuItem(menu, name, description, eip, context, fp, f);		
		}
	}


	protected void addEndpointInstances(ContextMenuEntry menu, Set<Endpoint> endpoints, ICustomContext context, IFeatureProvider fp) {
		SortedMap<String,Endpoint> map = new TreeMap<String,Endpoint>();
		ArrayList<String> processedURIs = new ArrayList<String>();
		for (Endpoint endpoint : endpoints) {
			map.put(endpoint.getDisplayText(), endpoint);
		}

		for (final Endpoint endpoint : map.values()) {
			String id = endpoint.getId();
			String url = endpoint.getUri();
			if (Strings.isBlank(id) && Strings.isBlank(url)) {
				continue;
			}
			if (processedURIs.contains(url)) continue;
			processedURIs.add(url);
			String description = endpoint.getDescription();
			String title = endpoint.getDisplayText();

			addMenuItem(menu, title, description, Endpoint.class, context, fp, new CreateNodeConnectionFeature(fp, Endpoint.class) {

				@Override
				protected AbstractNode createNode(ICustomContext ctx) throws Exception {
					return new Endpoint(endpoint);
				}
			});
		}
	}


	protected void addMenuItem(ContextMenuEntry parent, String label, String description, Class<? extends AbstractNode> clazz, ICustomContext context, IFeatureProvider fp) {
		addMenuItem(parent, label, description, clazz, context, fp, (ObjectCreationToolEntry)null);
	}

	
	protected void addMenuItem(ContextMenuEntry parent, String label, String description, Eip eip, ICustomContext context, IFeatureProvider fp) {
		addMenuItem(parent, label, description, eip, context, fp, (ObjectCreationToolEntry)null);
	}

	
	/**
	 * creates a new menu item and adds it to the parent menu
	 * 
	 * @param parent		the parent menu
	 * @param label			the menu label
	 * @param description	the menu description
	 * @param clazz			the class which is created by that menu item
	 * @param context		the custom context
	 * @param fp			the feature provider
	 */
	protected void addMenuItem(ContextMenuEntry parent, String label, String description, Class<? extends AbstractNode> clazz, ICustomContext context, IFeatureProvider fp, ObjectCreationToolEntry octe) {
		// check if we can actually connect to the current selection
		CreateNodeConnectionFeature feature = new CreateNodeConnectionFeature(fp, clazz, octe);
		addMenuItem(parent, label, description, clazz, context, fp, feature);
	}
	
	protected void addMenuItem(ContextMenuEntry parent, String label, String description, Eip eip, ICustomContext context, IFeatureProvider fp, ObjectCreationToolEntry octe) {
		// check if we can actually connect to the current selection
		CreateNodeConnectionFeature feature = new CreateNodeConnectionFeature(fp, eip, octe);
		addMenuItem(parent, label, description, eip, context, fp, feature);
	}



	protected void addMenuItem(ContextMenuEntry parent, String label, String description,
			Class<? extends AbstractNode> clazz, ICustomContext context, IFeatureProvider fp,
			CreateNodeConnectionFeature feature) {
		Object newObject = newInstance(clazz);
		if (newObject instanceof AbstractNode) {
			AbstractNode newNode = (AbstractNode) newObject;
			addMenuItem(parent, label, description, newNode, context, fp, feature);
		}
	}

	protected void addMenuItem(ContextMenuEntry parent, String label, String description,
			Eip eip, ICustomContext context, IFeatureProvider fp,
			CreateNodeConnectionFeature feature) {
		UniversalEIPNode newNode = new UniversalEIPNode(eip);
		addMenuItem(parent, label, description, newNode, context, fp, feature);
	}
	
	protected void addMenuItem(ContextMenuEntry parent, String label, String description,
			AbstractNode newNode, ICustomContext context, IFeatureProvider fp,
			CreateNodeConnectionFeature feature) {
			// TODO change to allow us to add any new item to the selection...
			//if (newNode instanceof RouteSupport || (selectedNode != null && selectedNode.canConnectTo(newNode))) {

			// create a sub-menu for all AddNode operations
			ContextMenuEntry menuEntry = new ContextMenuEntry(feature, context);
			// set the menu label
			menuEntry.setText(label);
			// set the description
			menuEntry.setDescription(description);
			try {
				// set the image
				String iconName = newNode.getIconName();
				menuEntry.setIconId(ImageProvider.getKeyForSmallIcon(iconName));
			} catch (Exception ex) {
				menuEntry.setIconId(DEFAULT_IMAGE_KEY);
			}
			// display sub-menu hierarchical or flat
			menuEntry.setSubmenu(false);
			// add entry to parent
			parent.add(menuEntry);
			//}
	}



	public static AbstractNode getSelectedNode(ICustomContext context, IFeatureProvider fp) {
		AbstractNode selectedNode = null;
		PictogramElement[] elements = context.getPictogramElements();
		if (elements != null && elements.length > 0) {
			Object bo = fp.getBusinessObjectForPictogramElement(elements[0]);
			if (bo instanceof AbstractNode) {
				selectedNode = (AbstractNode) bo;
			}
		}
		if (selectedNode == null){
			RiderDesignEditor editor = RiderDesignEditor.toRiderDesignEditor(fp);
			if (editor != null) {
				selectedNode = editor.getSelectedRoute();
			}
		}
		return selectedNode;
	}


	// MenuManager creation

	public void fillMenu(RiderDesignEditor editor, Menu menu, ArrayList<IToolEntry> additionalEndpoints) {
		this.editor = editor;
		this.selectedNode = editor.getSelectedNode();
		List<MenuManager> menus = new ArrayList<MenuManager>();
		if ((selectedNode instanceof RouteContainer) && !(selectedNode instanceof RouteSupport)) {
			// lets just add a new route
			MenuManager subMenu = new MenuManager(EditorMessages.paletteRouteTitle, "org.fusesource.ide.actions.add.routes");
			addMenuItem(subMenu, EditorMessages.addRouteTitle, EditorMessages.addRouteDescription, Route.class);

			addMenu(menus, subMenu);
		} else {
			MenuManager subMenu = new MenuManager(Messages.endpointsDrawerTitle, "org.fusesource.ide.actions.add.endpoints");
			fillEndpointsMenu(subMenu);
			fillAdditionalEndpointsMenu(subMenu, additionalEndpoints);
			Arrays.sort(subMenu.getItems(), new Comparator<IContributionItem>() {
			    /* (non-Javadoc)
			     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
			     */
			    @Override
			    public int compare(IContributionItem o1, IContributionItem o2) {
	                 return o1.getId().compareToIgnoreCase(o2.getId());
			    }
			});
			addMenu(menus, subMenu);

			subMenu = new MenuManager(Messages.routingDrawerTitle, "org.fusesource.ide.actions.add.routing");
			fillRoutingMenu(subMenu);
			addMenu(menus, subMenu);

			subMenu = new MenuManager(Messages.controlFlowDrawerTitle, "org.fusesource.ide.actions.add.control");
			fillControlFlowMenu(subMenu);
			addMenu(menus, subMenu);

			subMenu = new MenuManager(Messages.transformationDrawerTitle, "org.fusesource.ide.actions.add.transformation");
			fillTransformationMenu(subMenu);
			addMenu(menus, subMenu);

			subMenu = new MenuManager(Messages.miscellaneousDrawerTitle, "org.fusesource.ide.actions.add.miscellaneous");
			fillMiscellaneousMenu(subMenu);
			addMenu(menus, subMenu);
		}

		int index = menu.getItemCount();
		if (menus.size() == 1) {
			// lets remove the items and move them directly onto the menu
			// directly
			MenuManager childMenu = menus.get(0);
			IContributionItem[] items = childMenu.getItems();
			childMenu.removeAll();
			for (IContributionItem item : items) {
				item.fill(menu, index++);
			}
		} else if (menus.size() > 1) {
			for (MenuManager childMenu : menus) {
				childMenu.fill(menu, menu.getItemCount());
			}
		}
	}

	/*
	public void fillMenu2(RiderDesignEditor editor, IMenuManager menu) {
		AbstractNode selected = editor.getSelectedNode();
		fillMenu(editor, menu, selected);
	}
	 */

	public void fillMenu(RiderDesignEditor editor, MenuManager menu, AbstractNode node, ArrayList<IToolEntry> additionalEndpoints) {
		this.editor = editor;
		this.selectedNode = node;
		List<MenuManager> menus = new ArrayList<MenuManager>();
		if ((selectedNode instanceof RouteContainer) && !(selectedNode instanceof RouteSupport)) {
			// lets just add a new route
			MenuManager subMenu = new MenuManager(EditorMessages.paletteRouteTitle, "org.fusesource.ide.actions.add.routes");
			addMenuItem(subMenu, EditorMessages.addRouteTitle, EditorMessages.addRouteDescription, Route.class);

			addMenu(menus, subMenu);
		} else {
			MenuManager subMenu = new MenuManager(Messages.endpointsDrawerTitle, "org.fusesource.ide.actions.add.endpoints");
			fillEndpointsMenu(subMenu);
			fillAdditionalEndpointsMenu(subMenu, additionalEndpoints);
			addMenu(menus, subMenu);

			subMenu = new MenuManager(Messages.routingDrawerTitle, "org.fusesource.ide.actions.add.routing");
			fillRoutingMenu(subMenu);
			addMenu(menus, subMenu);

			subMenu = new MenuManager(Messages.controlFlowDrawerTitle, "org.fusesource.ide.actions.add.control");
			fillControlFlowMenu(subMenu);
			addMenu(menus, subMenu);

			subMenu = new MenuManager(Messages.transformationDrawerTitle, "org.fusesource.ide.actions.add.transformation");
			fillTransformationMenu(subMenu);
			addMenu(menus, subMenu);

			subMenu = new MenuManager(Messages.miscellaneousDrawerTitle, "org.fusesource.ide.actions.add.miscellaneous");
			fillMiscellaneousMenu(subMenu);
			addMenu(menus, subMenu);
		}

		if (menus.size() == 1) {
			// lets remove the items and move them directly onto the menu
			// directly
			MenuManager childMenu = menus.get(0);
			IContributionItem[] items = childMenu.getItems();
			childMenu.removeAll();
			for (IContributionItem item : items) {
				menu.add(item);
			}
		} else if (menus.size() > 1) {
			for (MenuManager childMenu : menus) {
				menu.add(childMenu);
			}
		}
	}

	protected void addMenu(List<MenuManager> menus, MenuManager subMenu) {
		IContributionItem[] items = subMenu.getItems();
		if (items != null && items.length > 0) {
			menus.add(subMenu);
		}
	}


	protected void addMenuItem(IMenuManager menu, final String title, final String description, 
			final Class<? extends AbstractNode> aClass) {
		// lets decide if we can actually add this kind of node first...
		Object newObject = newInstance(aClass);
		if (newObject instanceof AbstractNode) {
			final AbstractNode newNode = (AbstractNode) newObject;
			addMenuItem(menu, title, description, newNode, null, aClass);
		}
	}
	
	protected void addMenuItem(IMenuManager menu, final String title, final String description, 
			Eip eip) {
		UniversalEIPNode newObject = new UniversalEIPNode(eip);
		addMenuItem(menu, title, description, newObject, eip, null);
	}
	
	protected void addMenuItem(IMenuManager menu, final String title, final String description, 
			final AbstractNode newNode, final Eip eip, final Class<? extends AbstractNode> aClass) {
			if (newNode instanceof RouteSupport || (selectedNode != null && selectedNode.canConnectTo(newNode))) {
				final AbstractNode node = selectedNode;
				Action action = new Action() {
					@Override
					public void runWithEvent(Event event) {
						if (newNode instanceof Route) {
							editor.addNewRoute();
						} else {
							if( aClass != null )
								DiagramOperations.addNode(editor, aClass, node);
							else 
								DiagramOperations.addNode(editor, eip, node);
						}

						/*

						Object newObject = newInstance(aClass);
						if (newObject instanceof AbstractNode) {
							final AbstractNode newNode = (AbstractNode) newObject;
							RouteContainer parent = selectedNode.getParent();
							RouteSupport route = null;
							if (selectedNode instanceof RouteSupport) {
								route = (RouteSupport) selectedNode;
								parent = route;
							} else if (selectedNode instanceof RouteContainer && parent == null) {
								parent = (RouteContainer) selectedNode;
							} else if (parent instanceof RouteSupport) {
								route = (RouteSupport) parent;
							}
							newNode.setParent(parent);
							RouteContainer root = null;
							if (route != null) {
								Activator.getLogger().debug("Creating a new: " + title + " on selected node: " + selectedNode);

								// lets figure out a reasonable x, y based being
								// bigger by some margin that biggest X and max
								// y
								root = route.getParent();
								if (root == null) {
									root = route;
								}
							} else {
								root = parent;
							}

							Rectangle bounds = newNode.getLayout();
							Set<AbstractNode> descendents = root.getDescendents();
							for (AbstractNode descendant : descendents) {
								Rectangle layout = descendant.getLayout();
								if (layout.x > bounds.x) {
									bounds.x = layout.x;
								}
								if (layout.y > bounds.y) {
									bounds.y = layout.y;
								}
							}
							bounds.x += NEWNODE_X_OFFSET;
							bounds.y = selectedNode.getLayout().y;

							// lets force the route to be big too
							if (route != null) {
								Rectangle routeLayout = route.getLayout();
								if (routeLayout.width < bounds.x + ROUTE_WIDTH_MARGIN) {
									routeLayout.width = bounds.x + ROUTE_WIDTH_MARGIN;
									route.setLayout(routeLayout);
								}
								newNode.setLayout(bounds);
							}

							CompoundCommand command = new CompoundCommand("Add " + title);

							if (newNode instanceof RouteSupport) {
								RouteCreateCommand nodeCommand = new RouteCreateCommand((RouteSupport) newNode, parent, bounds);
								command.add(nodeCommand);
							} else {
								AbstractNodeCreateCommand nodeCommand = new AbstractNodeCreateCommand(newNode, route,
										bounds);
								command.add(nodeCommand);
							}

							if (selectedNode instanceof Container) {
								// no flow when adding to a container
							} else {
								FlowCreateCommand flowCommand = new FlowCreateCommand(selectedNode);
								flowCommand.setTarget(newNode);
								command.add(flowCommand);
							}

							command.add(new Command(NLS.bind(EditorMessages.selectCommandLabel, title)) {

								@Override
								public void execute() {
									// lets force the viewer to refresh...
									editor.autoLayoutRoute();
									nodeViewer.setSelectedNode(newNode);
								}

								@Override
								public void redo() {
									execute();
								}
							});

							editor.getCommandStack().execute(command);
					} else {
						Activator.getLogger().error("No parent available for new node: " + newObject + " selected node: "
								+ selectedNode);
					}
						 */
					}
				};
				action.setId("org.fusesource.ide.actions.add." + newNode.getPatternName());
				action.setText(title);
				action.setToolTipText(description);
				action.setDescription(description);
				action.setImageDescriptor(getImageDescriptor(newNode.getSmallIconName()));
				menu.add(action);
			}
	}


	protected Object newInstance(final Class<?> aClass) {
		try {
			return aClass.newInstance();
		} catch (Exception e) {
			Activator.getLogger().warning("Failed to create instance of " + aClass.getName() + ". " + e, e);
			return null;
		}
	}

	public ImageDescriptor getImageDescriptor(String key) {
		return org.fusesource.ide.camel.model.Activator.getDefault().getImageDescriptor(key);
	}

	private void fillAdditionalEndpointsContextMenu(ContextMenuEntry menu, ICustomContext context, IFeatureProvider fp, ArrayList<IToolEntry> toolEntries) {
	    for (IToolEntry te : toolEntries) {
	        if (te instanceof ObjectCreationToolEntry) {
	            ObjectCreationToolEntry octe = (ObjectCreationToolEntry)te;
	            if (octe.getCreateFeature() instanceof PaletteCategoryItemProvider) {
		        	PaletteCategoryItemProvider pcip = (PaletteCategoryItemProvider) octe.getCreateFeature();
		        	if (pcip != null && pcip.getCategoryType() == PaletteCategoryItemProvider.CATEGORY_TYPE.COMPONENTS) {
		        	    CreateFigureFeature cff = (CreateFigureFeature)octe.getCreateFeature();
		        	    Class<? extends AbstractNode> c = cff.getClazz();
		        	    if( c != null )
		        	    	addMenuItem(menu, octe.getLabel(), octe.getDescription(), cff.getClazz(), context, fp, octe);
		        	    else 
		        	    	addMenuItem(menu, octe.getLabel(), octe.getDescription(), cff.getEip(), context, fp, octe);
		        	}
	            }
	        }	        
	    }
	}	
	
	private void fillAdditionalEndpointsMenu(IMenuManager menu, ArrayList<IToolEntry> toolEntries) {
        for (IToolEntry te : toolEntries) {
            if (te instanceof ObjectCreationToolEntry) {
                ObjectCreationToolEntry octe = (ObjectCreationToolEntry)te;
                addMenuItem(menu, octe.getLabel(), octe.getDescription(), Endpoint.class);
            }           
        }
    }   

	protected abstract void fillTransformationContextMenu(ContextMenuEntry menu, ICustomContext context, IFeatureProvider fp);

	protected abstract void fillControlFlowContextMenu(ContextMenuEntry menu, ICustomContext context, IFeatureProvider fp);

	protected abstract void fillRoutingContextMenu(ContextMenuEntry menu, ICustomContext context, IFeatureProvider fp);

	protected abstract void fillEndpointsContextMenu(ContextMenuEntry menu, ICustomContext context, IFeatureProvider fp);

	protected abstract void fillMiscellaneousContextMenu(ContextMenuEntry menu, ICustomContext context, IFeatureProvider fp);

	protected abstract void fillTransformationMenu(IMenuManager menu);

	protected abstract void fillControlFlowMenu(IMenuManager menu);

	protected abstract void fillRoutingMenu(IMenuManager menu);

	protected abstract void fillEndpointsMenu(IMenuManager menu);

	protected abstract void fillMiscellaneousMenu(IMenuManager menu);

}
