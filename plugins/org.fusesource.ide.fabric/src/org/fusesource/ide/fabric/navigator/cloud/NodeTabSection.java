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
package org.fusesource.ide.fabric.navigator.cloud;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.fusesource.ide.commons.Viewers;
import org.fusesource.ide.commons.jobs.Jobs;
import org.fusesource.ide.commons.ui.Selections;
import org.fusesource.ide.commons.ui.Shells;
import org.fusesource.ide.commons.ui.actions.ActionSupport;
import org.fusesource.ide.commons.ui.actions.SeparatorFactory;
import org.fusesource.ide.commons.ui.actions.ToggleAction;
import org.fusesource.ide.commons.ui.views.TableViewSupport;
import org.fusesource.ide.commons.util.Function1;
import org.fusesource.ide.fabric.FabricPlugin;
import org.fusesource.ide.fabric.actions.Messages;
import org.fusesource.ide.fabric.actions.jclouds.JClouds;
import org.fusesource.ide.server.karaf.view.ITerminalConnectionListener;
import org.fusesource.ide.server.karaf.view.SshView;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.domain.Location;
import org.jclouds.domain.LoginCredentials;

import com.google.common.base.Function;
import com.google.common.base.Joiner;

public class NodeTabSection extends TableViewSupport {

    public static final Joiner spaceJoiner = Joiner.on(" ");
    protected static final String VIEW_ID = NodeTable.class.getName();
    protected static SeparatorFactory separatorFactory = new SeparatorFactory(VIEW_ID);

    private CloudNode current;

    private Separator separator1 = separatorFactory.createSeparator();
    private Separator separator2 = separatorFactory.createSeparator();
    private Separator separator3 = separatorFactory.createSeparator();

    private ToggleAction suspendResumeAction = new ToggleAction();
    private Action suspendAction;
    private Action resumeAction;
    private Action rebootAction;
    private Action destroyAction;
    private ActionSupport addAction;
    private ActionSupport openTerminalAction;
    private Set<String> selectedIds;

    @Override
    public void setInput(IWorkbenchPart part, ISelection selection) {
        CloudNode node = (CloudNode) Selections.getFirstSelection(selection);
        if (node == current) {
            return;
        }
        current = node;
        final WritableList nodes = node.getNodes();
        setInput(nodes);
        getViewer().setInput(nodes);
        getViewer().refresh(true);
    }

    public ComputeService getComputeService() {
        return current.getComputeService();
    }

    @Override
    protected void createColumns() {
        clearColumns();

        int bounds = 150;
        int column = 0;

        Function1 function = new Function1() {
            @Override
            public Object apply(Object element) {
                ComputeMetadata value = JClouds.asComputeMetadata(element);
                if (value != null) {
                    return value.getId();
                }
                return null;
            }
        };
        column = addColumnFunction(bounds, column, function, "Id");

        function = new Function1() {
            @Override
            public Object apply(Object element) {
                ComputeMetadata value = JClouds.asComputeMetadata(element);
                if (value != null) {
                    return value.getName();
                }
                return null;
            }
        };
        column = addColumnFunction(bounds, column, function, "Name");

        function = new Function1() {
            @Override
            public Object apply(Object element) {
                NodeMetadata value = JClouds.asNodeMetadata(element);
                if (value != null) {
                    return value.getState();
                }
                return null;
            }
        };
        column = addColumnFunction(bounds, column, function, "State");

        function = new Function1() {
            @Override
            public Object apply(Object element) {
                NodeMetadata value = JClouds.asNodeMetadata(element);
                if (value != null) {
                    Hardware hardware = value.getHardware();
                    if (hardware != null) {
                        return JClouds.text(hardware);
                    }
                }
                return null;
            }
        };
        column = addColumnFunction(bounds, column, function, "Hardware");

        function = new Function1() {
            @Override
            public Object apply(Object element) {
                NodeMetadata value = JClouds.asNodeMetadata(element);
                if (value != null) {
                    return value.getImageId();
                }
                return null;
            }
        };
        column = addColumnFunction(bounds, column, function, "Image");

        function = new Function1() {
            @Override
            public Object apply(Object element) {
                NodeMetadata value = JClouds.asNodeMetadata(element);
                if (value != null) {
                    OperatingSystem operatingSystem = value.getOperatingSystem();
                    if (operatingSystem != null) {
                        return JClouds.text(operatingSystem);
                    }
                }
                return null;
            }
        };
        column = addColumnFunction(bounds, column, function, "OS");

        function = new Function1() {
            @Override
            public Object apply(Object element) {
                NodeMetadata value = JClouds.asNodeMetadata(element);
                if (value != null) {
                    return value.getGroup();
                }
                return null;
            }
        };
        column = addColumnFunction(bounds, column, function, "Group");

        function = new Function1() {
            @Override
            public Object apply(Object element) {
                NodeMetadata value = JClouds.asNodeMetadata(element);
                if (value != null) {
                    return joinSpaces(value.getPublicAddresses());
                }
                return null;
            }
        };
        column = addColumnFunction(bounds, column, function, "Public IPs");

        function = new Function1() {
            @Override
            public Object apply(Object element) {
                NodeMetadata value = JClouds.asNodeMetadata(element);
                if (value != null) {
                    return joinSpaces(value.getPrivateAddresses());
                }
                return null;
            }
        };
        column = addColumnFunction(bounds, column, function, "Private IPs");

        function = new Function1() {
            @Override
            public Object apply(Object element) {
                Location value = JClouds.asLocation(element);
                if (value != null) {
                    return value.getDescription();
                }
                return null;
            }
        };
        column = addColumnFunction(bounds, column, function, "Location");

        function = new Function1() {
            @Override
            public Object apply(Object element) {
                ComputeMetadata value = JClouds.asComputeMetadata(element);
                if (value != null) {
                    return value.getUri();
                }
                return null;
            }
        };
        column = addColumnFunction(bounds, column, function, "URI");

        function = new Function1() {
            @Override
            public Object apply(Object element) {
                ComputeMetadata value = JClouds.asComputeMetadata(element);
                if (value != null) {
                    return joinSpaces(value.getTags());
                }
                return null;
            }
        };
        column = addColumnFunction(bounds, column, function, "Tags");
    }

    @Override
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);

        getViewer().addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                selectionUpdated();
            }
        });

        // TODO add a wizard...
        addAction = new ActionSupport(Messages.jcloud_addNodeButton, Messages.jcloud_addNodeButtonTooktip, FabricPlugin
                .getPlugin().getImageDescriptor("add_obj.gif")) {
            @Override
            public void run() {
                // TODO open a wizard to choose an image to start..
            }

        };

        openTerminalAction = new ActionSupport(Messages.jcloud_openTerminalLabel, Messages.jcloud_openTerminalTooltip,
                FabricPlugin.getPlugin().getImageDescriptor("terminal_view.gif")) {
            @Override
            public void run() {
                openTerminal();
            }
        };

        suspendAction = new ActionSupport(Messages.jcloud_suspendNodeButton, Messages.jcloud_suspendNodeButtonTooktip,
                FabricPlugin.getPlugin().getImageDescriptor("stop_task.gif")) {

            @Override
            public void run() {
                final List<ComputeMetadata> selectedNodes = getSelectedNodes();
                String message = makeNodesString(selectedNodes, "Suspending ", "");

                Jobs.schedule(message, new Runnable() {

                    @Override
                    public void run() {
                        foreachNode(selectedNodes, "Suspend node failed", new Function<ComputeMetadata, Void>() {

                            @Override
                            public Void apply(ComputeMetadata node) {
                                String id = JClouds.id(node);
                                if (id != null) {
                                    getComputeService().suspendNode(id);
                                }
                                return null;
                            }
                        });
                        asyncReload();
                    }

                });
            }

        };

        resumeAction = new ActionSupport(Messages.jcloud_resumeNodeButton, Messages.jcloud_resumeNodeButtonTooktip,
                FabricPlugin.getPlugin().getImageDescriptor("start_task.gif")) {

            @Override
            public void run() {
                final List<ComputeMetadata> selectedNodes = getSelectedNodes();
                String message = makeNodesString(selectedNodes, "Resuming ", "");

                Jobs.schedule(message, new Runnable() {

                    @Override
                    public void run() {
                        foreachNode(selectedNodes, "Resume node failed", new Function<ComputeMetadata, Void>() {

                            @Override
                            public Void apply(ComputeMetadata node) {
                                String id = JClouds.id(node);
                                if (id != null) {
                                    getComputeService().resumeNode(id);
                                }
                                return null;
                            }
                        });
                        asyncReload();
                    }
                });
            }

        };

        rebootAction = new ActionSupport(Messages.jcloud_rebootNodeButton, Messages.jcloud_rebootNodeButtonTooktip,
                FabricPlugin.getPlugin().getImageDescriptor("restart_task.gif")) {

            @Override
            public void run() {
                final List<ComputeMetadata> selectedNodes = getSelectedNodes();
                String message = makeNodesString(selectedNodes, "Rebooting ", "");

                Jobs.schedule(message, new Runnable() {

                    @Override
                    public void run() {
                        foreachNode(selectedNodes, "Reboot node failed", new Function<ComputeMetadata, Void>() {

                            @Override
                            public Void apply(ComputeMetadata node) {
                                String id = JClouds.id(node);
                                if (id != null) {
                                    getComputeService().rebootNode(id);
                                }
                                return null;
                            }
                        });
                        asyncReload();
                    }
                });
            }
        };

        destroyAction = new ActionSupport(Messages.jcloud_destroyNodeButton, Messages.jcloud_destroyNodeButtonTooktip,
                FabricPlugin.getPlugin().getImageDescriptor("delete.gif")) {

            @Override
            public void run() {
                final List<ComputeMetadata> selectedNodes = getSelectedNodes();
                if (!selectedNodes.isEmpty()) {
                    String message = makeNodesString(selectedNodes, "Destroy ", "?");
                    boolean confirm = MessageDialog.openConfirm(Shells.getShell(), "Delete Compute Nodes", message);
                    if (confirm) {
                        Jobs.schedule(makeNodesString(selectedNodes, "Destroying ", ""), new Runnable() {
                            @Override
                            public void run() {
                                foreachNode(selectedNodes, "Destroy node failed",
                                        new Function<ComputeMetadata, Void>() {

                                            @Override
                                            public Void apply(ComputeMetadata node) {
                                                String id = JClouds.id(node);
                                                if (id != null) {
                                                    getComputeService().destroyNode(id);
                                                }
                                                return null;
                                            }
                                        });
                                asyncReload();
                            }
                        });
                    }
                }
            }
        };

        suspendResumeAction.setCurrentAction(suspendAction);

        selectionUpdated();

        addLocalMenuActions(separator1, openTerminalAction,
        // TODO addAction,
                separator2, suspendAction, resumeAction, rebootAction, separator3, destroyAction);

        addToolBarActions(openTerminalAction,
        // TODO addAction,
                suspendAction, resumeAction, rebootAction, destroyAction);

        setDoubleClickAction(openTerminalAction);
    }

    /**
     * Applies an operation to each selected node
     */
    protected void foreachSelectedNode(String title, Function<ComputeMetadata, Void> function) {
        List<ComputeMetadata> selectedNodes = getSelectedNodes();
        foreachNode(selectedNodes, title, function);
    }

    protected void foreachNode(List<ComputeMetadata> selectedNodes, String title,
            Function<ComputeMetadata, Void> function) {
        for (ComputeMetadata md : selectedNodes) {
            if (md != null) {
                try {
                    function.apply(md);
                } catch (Exception e) {
                    FabricPlugin.showUserError(title, e.getMessage(), e);
                    break;
                }
            }
        }
    }

    @Override
    protected void configureViewer() {
        setInput(getNodes());
        // reload();
    }

    public WritableList getNodes() {
        return current == null ? WritableList.withElementType(ComputeMetadata.class) : current.getNodes();
    }

    @Override
    public void refresh() {
        if (current != null) {
            current.reloadNodes();
        }
    }

    @Override
    protected IStructuredContentProvider createContentProvider() {
        return ArrayContentProvider.getInstance();
    }

    @Override
    protected String getHelpID() {
        return NodeTable.class.getName();
    }

    public ComputeMetadata getSelectedNode() {
        return JClouds.asComputeMetadata(Selections.getFirstSelection(getViewer()));
    }

    public List<ComputeMetadata> getSelectedNodes() {
        List<Object> list = Selections.getSelectionList(getViewer());
        List<ComputeMetadata> answer = new ArrayList<ComputeMetadata>();
        for (Object object : list) {
            ComputeMetadata metadata = JClouds.asComputeMetadata(object);
            if (metadata != null) {
                answer.add(metadata);
            }
        }
        return answer;
    }

    public Set<String> getSelectedStates() {
        Set<String> answer = new HashSet<String>();
        List<Object> list = Selections.getSelectionList(getViewer());
        for (Object object : list) {
            NodeMetadata value = JClouds.asNodeMetadata(object);
            if (value != null) {
                NodeState state = value.getState();
                if (state != null) {
                    answer.add(state.toString());
                }
            }
        }
        return answer;
    }

    protected void selectionUpdated() {
        ComputeMetadata n = getSelectedNode();
        boolean selected = n != null;

        List<ComputeMetadata> selectedNodes = getSelectedNodes();
        if (!getNodes().isEmpty()) {
            Set<String> idSet = new HashSet<String>();
            for (ComputeMetadata node : selectedNodes) {
                String id = node.getId();
                if (id != null) {
                    idSet.add(id);
                }
            }
            this.selectedIds = idSet;
        }
        if (selected) {
            Status state = JClouds.getState(n);
            Action action = suspendAction;
            if (state != Status.RUNNING) {
                action = resumeAction;
            }
            suspendResumeAction.setCurrentAction(action);
        }

        openTerminalAction.setEnabled(selectedNodes.size() == 1 && selectedStatesOnly("RUNNING"));
        suspendAction.setEnabled(selectedStatesOnly("RUNNING"));
        resumeAction.setEnabled(selectedStatesOnly("SUSPENDED"));
        rebootAction.setEnabled(selected);
        destroyAction.setEnabled(selectedNodes.size() > 0 && !getSelectedStates().contains("TERMINATED"));
    }

    /**
     * The underlying model has changed so refresh the viewer
     */
    @Override
    protected void refreshViewerOnChange() {
        super.refreshViewerOnChange();

        // lets restore the selection
        if (selectedIds != null) {
            List<Object> selected = new ArrayList<Object>();
            WritableList nodes = getNodes();
            for (Object object : nodes) {
                ComputeMetadata md = JClouds.asComputeMetadata(object);
                if (md != null) {
                    String id = md.getId();
                    if (selectedIds.contains(id)) {
                        selected.add(object);
                    }
                }
            }
            if (!selected.isEmpty()) {
                getViewer().setSelection(new StructuredSelection(selected));
            }
        }
    }

    /**
     * Returns true if the selected nodes all are at the given state
     */
    protected boolean selectedStatesOnly(String state) {
        Set<String> selectedStates = getSelectedStates();
        return selectedStates.size() == 1 && selectedStates.contains(state);
    }

    public Object joinSpaces(Set<String> tags) {
        if (tags != null) {
            return spaceJoiner.join(new TreeSet<String>(tags));
        }
        return null;
    }

    protected void openTerminal() {
        String id = getClass().getName();

        ComputeMetadata computeNode = getSelectedNode();
        NodeMetadata node;
        if (computeNode instanceof NodeMetadata) {
            node = (NodeMetadata) computeNode;
        } else
            return;

        String host = node.getHostname();
        Set<String> addressSet = node.getPublicAddresses();
        if (addressSet != null) {
            List<String> publicAddresses = new ArrayList<String>(addressSet);
            if (publicAddresses.size() > 0) {
                host = publicAddresses.get(0);
            }
        }
        int port = node.getLoginPort();
        LoginCredentials credentials = node.getCredentials();
        // TODO is there a better way to figure this out if there's no
        // credentials?
        String user = "admin";
        String password = null;
        if (credentials != null) {
            user = credentials.getUser();
            password = credentials.getPassword();
        }

        // open the terminal view
        IViewPart vp = FabricPlugin.openTerminalView();
        if (vp == null || vp instanceof SshView == false) {
            FabricPlugin.getLogger().error("Unable to open the terminal view!");
            return;
        }

        // get the view
        final SshView connectorView = (SshView) vp;

        connectorView.setPartName(id);

        // add a connection listener
        connectorView.addConnectionListener(new ITerminalConnectionListener() {

            @Override
            public void onDisconnect() {
            }

            @Override
            public void onConnect() {
                connectorView.setFocus();
            }
        });

        // create the connection
        try {
            System.out.println("Creating the connection if it doesn't exist for host: " + host + " port " + port
                    + " user " + user);

            // TODO set the title? open a new view if there's not one already
            // etc?
            connectorView.createConnectionIfNotExists(host, port, user, password);
        } catch (Exception ex) {
            FabricPlugin.getLogger().error("Unable to connect via SSH", ex);
        }
    }

    protected String makeNodesString(List<ComputeMetadata> selectedNodes, String prefix, String postfix) {
        boolean first = true;
        StringBuilder builder = new StringBuilder(prefix);
        for (ComputeMetadata node : selectedNodes) {
            if (first)
                first = false;
            else
                builder.append(",\n");
            builder.append(node.getId());
        }
        builder.append(postfix);
        String message = builder.toString();
        return message;
    }

    protected void asyncReload() {
        Viewers.async(new Runnable() {

            @Override
            public void run() {
                refresh();

            }
        });
    }

}
