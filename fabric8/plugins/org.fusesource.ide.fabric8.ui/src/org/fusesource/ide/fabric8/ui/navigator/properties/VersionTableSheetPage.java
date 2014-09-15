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

package org.fusesource.ide.fabric8.ui.navigator.properties;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.fusesource.ide.commons.Viewers;
import org.fusesource.ide.commons.tree.Refreshables;
import org.fusesource.ide.commons.ui.Selections;
import org.fusesource.ide.commons.ui.actions.SeparatorFactory;
import org.fusesource.ide.commons.ui.views.TableViewSupport;
import org.fusesource.ide.commons.util.Function1;
import org.fusesource.ide.fabric8.core.dto.ProfileStatusDTO;
import org.fusesource.ide.fabric8.core.dto.VersionDTO;
import org.fusesource.ide.fabric8.ui.actions.CreateVersionAction;
import org.fusesource.ide.fabric8.ui.navigator.Fabric;
import org.fusesource.ide.fabric8.ui.navigator.ProfileNode;
import org.fusesource.ide.fabric8.ui.navigator.VersionNode;
import org.fusesource.ide.fabric8.ui.navigator.VersionsNode;
import org.jboss.tools.jmx.core.tree.Node;

public class VersionTableSheetPage extends TableViewSupport implements IPropertySheetPage {

	protected static final String VIEW_ID = VersionTableSheetPage.class.getName();
	protected static SeparatorFactory separatorFactory = new SeparatorFactory(VIEW_ID);
	
	public static VersionDTO asVersionDTO(Object element) {
        if (element instanceof VersionDTO) {
            return (VersionDTO) element;
        } else if (element instanceof VersionNode) {
        	return ((VersionNode)element).getVersion();
        }
        return null;
    }
	
	private CreateVersionAction createVersionAction;
	private Separator separator1 = separatorFactory.createSeparator();
	private Separator separator2 = separatorFactory.createSeparator();
	
	private final Fabric fabric;
	private Runnable refreshRunnable = new Runnable() {

		@Override
		public void run() {
			refresh();
		}
	};

	public VersionTableSheetPage(Fabric fabric) {
		this.fabric = fabric;
		updateActionStatus();
	}

	@Override
	public void dispose() {
		super.dispose();
		fabric.removeFabricUpdateRunnable(refreshRunnable);
	}

	@Override
	public void refresh() {
		Viewers.async(new Runnable() {

			@Override
			public void run() {
				Refreshables.refresh(fabric.getVersionsNode());
				updateData();
				Refreshables.refresh(getViewer());
				updateActionStatus();
			}
		});
	}

	public Fabric getFabric() {
		return fabric;
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.commons.ui.views.TableViewSupport#configureViewer()
	 */
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
				VersionsNode versionsNode = fabric.getVersionsNode();
				if (versionsNode != null) {
					List<VersionDTO> selectedVersions = getSelectedVersions();
					if (!selectedVersions.isEmpty()) {
						VersionDTO version = selectedVersions.get(0);
						VersionNode versionNode = versionsNode.getVersionNode(version.getId());
						if (versionNode != null) {
							Selections.setSingleSelection(fabric.getRefreshableUI(), versionNode);
						}
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

		fabric.addFabricUpdateRunnable(refreshRunnable);
		updateData();
	}

	protected ProfileNode getSelectedProfileNode() {
		Object first = Selections.getFirstSelection(getViewer());
		if (first instanceof ProfileStatusDTO) {
			ProfileStatusDTO status = (ProfileStatusDTO) first;
			String id = status.getProfile();
			VersionNode version = getFabric().getDefaultVersionNode();
			if (version != null) {
				return version.getProfileNode(id);
			}
		}
		return null;
	}

	
	protected List<VersionDTO> getSelectedVersions() {
		List<VersionDTO> versions = new ArrayList<VersionDTO>();
		IStructuredSelection selection = Selections.getStructuredSelection(getViewer());
		if (selection != null) {
			Iterator iterator = selection.iterator();
			while (iterator.hasNext()) {
				VersionDTO version = VersionNode.toVersion(iterator.next());
				if (version != null) {
					versions.add(version);
				}
			}
		}
		return versions;
	}

	protected Set<String> getSelectedIds() {
		Set<String> answer = new HashSet<String>();
		List<VersionDTO> versions = getSelectedVersions();
		for (VersionDTO version : versions) {
			String id = version.getId();
			if (id != null) {
				answer.add(id);
			}
		}
		return answer;
	}
	
    protected CreateVersionAction getCreateVersionAction() {
        if (createVersionAction == null) {
        	createVersionAction = createCreateVersionAction((VersionNode) null);
        }
        return createVersionAction;
    }
    
    protected CreateVersionAction createCreateVersionAction(VersionNode version) {
    	if (version == null && fabric != null) {
    		return new CreateVersionAction(fabric.getVersionsNode());
    	}
        return new CreateVersionAction(version);
    }
	
	public void updateData() {
		VersionsNode versionsNode = fabric.getVersionsNode();
		if (versionsNode != null) {
			setInput(versionsNode.getChildrenList());
		}
	}
	
    protected VersionNode getSelectedVersionNode() {
        Object first = Selections.getFirstSelection(getViewer());
        if (first instanceof VersionDTO) {
            VersionDTO version = (VersionDTO) first;
            String id = version.getId();
            for (Node n : fabric.getVersionsNode().getChildrenList()) {
            	if (n != null && n instanceof VersionNode && ((VersionNode)n).getVersionId().equals(id)) {
            		return (VersionNode)n;
            	}
            }
        }
        return null;
    }

	protected void updateActionStatus() {
		VersionNode versionNode = getSelectedVersionNode();
		getCreateVersionAction().setFabric(fabric);
        getCreateVersionAction().setVersioNode(versionNode);
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.commons.ui.IConfigurableColumns#getColumnConfigurationId()
	 */
	@Override
	public String getColumnConfigurationId() {
		return VIEW_ID;
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.commons.ui.views.TableViewSupport#createColumns()
	 */
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

	/* (non-Javadoc)
	 * @see org.fusesource.ide.commons.ui.views.TableViewSupport#createContentProvider()
	 */
	@Override
	protected IStructuredContentProvider createContentProvider() {
		return ArrayContentProvider.getInstance();
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.commons.ui.views.ColumnViewSupport#getHelpID()
	 */
	@Override
	protected String getHelpID() {
		return VIEW_ID;
	}
}
