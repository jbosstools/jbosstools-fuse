/******************************************************************************* 
 * Copyright (c) 2013 Red Hat, Inc. 
 *  All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 *
 ******************************************************************************/
package org.fusesource.ide.fabric8.ui.navigator.properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPart;
import org.fusesource.ide.commons.Viewers;
import org.fusesource.ide.commons.tree.Refreshables;
import org.fusesource.ide.commons.ui.Selections;
import org.fusesource.ide.commons.ui.actions.SeparatorFactory;
import org.fusesource.ide.commons.ui.views.TableViewSupport;
import org.fusesource.ide.commons.util.Function1;
import org.fusesource.ide.fabric8.core.dto.VersionDTO;
import org.fusesource.ide.fabric8.ui.FabricPlugin;
import org.fusesource.ide.fabric8.ui.actions.CreateVersionAction;
import org.fusesource.ide.fabric8.ui.navigator.Fabric;
import org.fusesource.ide.fabric8.ui.navigator.FabricNavigator;
import org.fusesource.ide.fabric8.ui.navigator.VersionNode;
import org.fusesource.ide.fabric8.ui.navigator.VersionsNode;
import org.jboss.tools.jmx.core.tree.Node;

/**
 * ContainersTabSection
 */
public class VersionsTabSection extends TableViewSupport {

    protected static SeparatorFactory separatorFactory = new SeparatorFactory(FabricStatusTableSheetPage.VIEW_ID);

    private Fabric current;
    private CreateVersionAction createVersionAction;
    private Separator separator1 = separatorFactory.createSeparator();
    private Separator separator2 = separatorFactory.createSeparator();
    
    private Runnable refreshRunnable = new Runnable() {

        @Override
        public void run() {
            refresh();
        }
    };

    public static VersionDTO asVersionDTO(Object element) {
        if (element instanceof VersionDTO) {
            return (VersionDTO) element;
        } else if (element instanceof VersionNode) {
        	return ((VersionNode)element).getVersion();
        }
        return null;
    }
    
    public VersionsTabSection() {
    }

    @Override
    public void setInput(IWorkbenchPart part, ISelection selection) {
    	VersionsNode vNode = (VersionsNode)Selections.getFirstSelection(selection);
        if (vNode == null) {
        	return;
        }
    	final Fabric fabric = vNode.getFabric();
        if (fabric == current) {
            return;
        }
        if (current != null) {
            current.removeFabricUpdateRunnable(refreshRunnable);
        }
        current = fabric;
        if (current != null) {
            current.addFabricUpdateRunnable(refreshRunnable);
        } 

    	Job loadJob = new Job("Loading " + vNode.toString() + " data...") {
			
			@Override
			protected IStatus run(IProgressMonitor monitor) {
		        final Collection<VersionDTO> versions = getVersions();
		        Display.getDefault().asyncExec(new Runnable() {
					
					@Override
					public void run() {
				        setInput(versions);
				        getViewer().setInput(versions);
				        getViewer().refresh(true);
				        if (fabric != null) {
					        getCreateVersionAction().setVersioNode(null);
				        }
				        updateActionStatus();
					}
				});
		        return Status.OK_STATUS;
			}
		}; 
		loadJob.schedule();
    }

    @Override
    public void dispose() {
        if (current != null) {
            current.removeFabricUpdateRunnable(refreshRunnable);
        }
        super.dispose();
    }

    @Override
    protected void configureViewer() {
    	addLocalMenuActions(
				separator1,
				getCreateVersionAction(),
				separator2);
		
		addToolBarActions(getCreateVersionAction());
    	
        getViewer().addDoubleClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(DoubleClickEvent event) {
                FabricNavigator nav = FabricPlugin.getFabricNavigator();
                if (nav != null && current != null) {
                    Object oSel = Selections.getFirstSelection(event.getSelection());
                    if (oSel != null && oSel instanceof VersionDTO) {
                        VersionDTO s = asVersionDTO(oSel);
                        String versionId = s.getId();
                        VersionNode versionNode = searchVersion(nav, versionId);
                        if (versionNode != null)
                            nav.selectReveal(new StructuredSelection(versionNode));
                    }
                }
            }
        });

        getViewer().addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                updateActionStatus();
            }
        });

        updateData();
    }

    private List<VersionDTO> getVersions() {
    	if (current == null) return Collections.EMPTY_LIST;
    	List<VersionDTO> result = new ArrayList<VersionDTO>();
    	for (Node node : current.getVersionsNode().getChildrenList()) {
    		VersionDTO vNode = VersionNode.toVersion(node);
    		if (vNode != null) result.add(vNode);
    	}
    	return result;
    }
    
    private VersionNode searchVersion(FabricNavigator nav, String versionId) {
        VersionNode result = null;
    	for (Node node : current.getVersionsNode().getChildrenList()) {
    		VersionNode vNode = VersionNode.toVersionNode(node);
    		if (vNode != null && vNode.getVersionId().equals(versionId)) {
    			result = vNode;
    			break;
    		}
    	}
        return result;
    }

    @Override
    public void refresh() {
        Viewers.async(new Runnable() {

            @Override
            public void run() {
                // TODO
                /*
                 * final Set<String> selectedIds = getSelectedIds();
                 * Refreshables.refresh(fabric.getContainersNode());
                 */

                if (current == null) {
                    return;
                }
                updateData();
                Refreshables.refresh(getViewer());
                /*
                 * setSelectedContainerIds(selectedIds);
                 */
                updateActionStatus();
            }
        });
    }

    protected void updateData() {
        setInput(getVersions());
    }

    @Override
    protected void createColumns() {
        clearColumns();

        int bounds = 100;
        int column = 0;

        Function1 function = new Function1() {
            @Override
            public Object apply(Object element) {
                VersionDTO version = asVersionDTO(element);
                if (version != null) {
                    return version.getId();
                }
                return null;
            }
        };
        column = addColumnFunction(250, column, function, "Id");

        function = new Function1() {
            @Override
            public Boolean apply(Object element) {
            	VersionDTO version = asVersionDTO(element);
                if (version != null) {
                    return version.isDefaultVersion();
                }
                return null;
            }
        };
        column = addColumnFunction(bounds, column, function, "Default");
    }

    @Override
    protected IStructuredContentProvider createContentProvider() {
        return ArrayContentProvider.getInstance();
    }

    @Override
    protected String getHelpID() {
        return VersionTableSheetPage.VIEW_ID;
    }
    
    /* (non-Javadoc)
     * @see org.fusesource.ide.commons.ui.IConfigurableColumns#getColumnConfigurationId()
     */
    @Override
    public String getColumnConfigurationId() {
    	return VersionTableSheetPage.VIEW_ID;
    }

    protected IStructuredSelection getSelection() {
        return Selections.getStructuredSelection(getViewer());
    }

    protected CreateVersionAction getCreateVersionAction() {
        if (createVersionAction == null) {
        	createVersionAction = createCreateVersionAction((VersionNode) null);
        }
        return createVersionAction;
    }
    
    protected VersionNode getSelectedVersionNode() {
        Object first = Selections.getFirstSelection(getViewer());
        if (first instanceof VersionDTO) {
            VersionDTO version = (VersionDTO) first;
            String id = version.getId();
            for (Node n : current.getVersionsNode().getChildrenList()) {
            	if (n != null && n instanceof VersionNode && ((VersionNode)n).getVersionId().equals(id)) {
            		return (VersionNode)n;
            	}
            }
        }
        return null;
    }
    
    protected CreateVersionAction createCreateVersionAction(VersionNode version) {
    	if (version == null && current != null) {
    		return new CreateVersionAction(current.getVersionsNode());
    	}
        return new CreateVersionAction(version);
    }
    
    protected void updateActionStatus() {
    	VersionNode versionNode = getSelectedVersionNode();
    	getCreateVersionAction().setFabric(current);
        getCreateVersionAction().setVersioNode(versionNode);
    }
}
