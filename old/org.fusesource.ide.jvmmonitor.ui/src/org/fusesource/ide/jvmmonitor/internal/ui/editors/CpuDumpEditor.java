/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.editors;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.ui.actions.JdtActionConstants;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.PageBook;
import org.fusesource.ide.jvmmonitor.core.cpu.CpuModelEvent;
import org.fusesource.ide.jvmmonitor.core.cpu.CpuModelFactory;
import org.fusesource.ide.jvmmonitor.core.cpu.ICallTreeNode;
import org.fusesource.ide.jvmmonitor.core.cpu.ICpuModel;
import org.fusesource.ide.jvmmonitor.core.cpu.ICpuModelChangeListener;
import org.fusesource.ide.jvmmonitor.core.cpu.IMethodNode;
import org.fusesource.ide.jvmmonitor.core.cpu.CpuModelEvent.CpuModelState;
import org.fusesource.ide.jvmmonitor.core.dump.CpuDumpParser;
import org.fusesource.ide.jvmmonitor.internal.ui.IHelpContextIds;
import org.fusesource.ide.jvmmonitor.internal.ui.actions.CollapseAllAction;
import org.fusesource.ide.jvmmonitor.internal.ui.actions.CopyAction;
import org.fusesource.ide.jvmmonitor.internal.ui.actions.OpenDeclarationAction;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.cpu.AbstractContentProvider;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.cpu.AbstractFilteredTree;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.cpu.CallTreeContentProvider;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.cpu.CallTreeFilteredTree;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.cpu.CallTreeLabelProvider;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.cpu.HotSpotsFilteredTree;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.cpu.HotSpotsLabelProvider;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.cpu.AbstractFilteredTree.ViewerType;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.cpu.actions.FindAction;
import org.fusesource.ide.jvmmonitor.ui.Activator;
import org.fusesource.ide.jvmmonitor.ui.ISharedImages;

import org.xml.sax.SAXException;

/**
 * The CPU dump editor.
 */
public class CpuDumpEditor extends AbstractDumpEditor {

    /** The CPU model. */
    protected ICpuModel cpuModel;

    /** The trees. */
    Map<AbstractFilteredTree, Integer> trees;

    /** The page book for callers/callees. */
    private PageBook callersCalleesPageBook;

    /** The call tree image. */
    private Image callTreeImage;

    /** The hot spots image. */
    private Image hotSpotsImage;

    /** The caller callee image. */
    private Image callerCalleeImage;

    /** The collapse all action. */
    CollapseAllAction collapseAllAction;

    /**
     * The constructor.
     */
    public CpuDumpEditor() {
        trees = new HashMap<AbstractFilteredTree, Integer>();
    }

    /*
     * @see AbstractDumpEditor#createClientPages()
     */
    @Override
    protected void createClientPages() {
        contributeToActionBars();

        createCallTreePage();
        createHotSpotsPage();
        createCallerCalleePage();

        refresh();

        addListeners();

        PlatformUI.getWorkbench().getHelpSystem()
                .setHelp(getContainer(), IHelpContextIds.CPU_DUMP_EDITOR);
    }

    /*
     * @see MultiPageEditorPart#init(IEditorSite, IEditorInput)
     */
    @Override
    public void init(IEditorSite site, IEditorInput input)
            throws PartInitException {
        super.init(site, input);

        cpuModel = new CpuModelFactory().createCpuModel();
        cpuModel.addModelChangeListener(new ICpuModelChangeListener() {
            @Override
            public void modelChanged(CpuModelEvent event) {
                if (event.state == CpuModelState.CallersCalleesTargetChanged) {
                    refresh();
                    if (cpuModel.getCallersCalleesTarget() != null) {
                        showCallerCalleeTab();
                    }
                }
            }
        });

        setPartName(input.getName());

        if (input instanceof IFileEditorInput) {
            String filePath = ((IFileEditorInput) input).getFile()
                    .getRawLocation().toOSString();
            loadDumpFile(filePath);
        } else if (input instanceof FileStoreEditorInput) {
            String filePath = ((FileStoreEditorInput) input).getURI().getPath();
            loadDumpFile(filePath);
        }
    }

    /*
     * @see WorkbenchPart#setFocus()
     */
    @Override
    public void setFocus() {
        getContainer().setFocus();
        refresh();
    }

    /*
     * @see AbstractDumpEditor#dispose()
     */
    @Override
    public void dispose() {
        super.dispose();
        if (callTreeImage != null) {
            callTreeImage.dispose();
        }
        if (hotSpotsImage != null) {
            hotSpotsImage.dispose();
        }
        if (callerCalleeImage != null) {
            callerCalleeImage.dispose();
        }
    }

    /**
     * Refreshes the view.
     */
    protected void refresh() {
        refreshCallersCalleesPage();
        refreshContentDescription();
        cpuModel.refreshMaxValues();

        for (AbstractFilteredTree tree : trees.keySet()) {
            if (tree != null && !tree.getViewer().getControl().isDisposed()) {
                tree.getViewer().refresh();
            }
        }
    }

    /**
     * Creates the call tree page.
     */
    private void createCallTreePage() {
        Composite panel = new Composite(getContainer(), SWT.NONE);
        panel.setLayout(new FillLayout());

        final CallTreeFilteredTree callTreeFilteredTree = new CallTreeFilteredTree(
                panel, getEditorSite().getActionBars());
        TreeViewer callTreeViewer = callTreeFilteredTree.getViewer();
        callTreeViewer.setContentProvider(new CallTreeContentProvider());
        callTreeViewer.setLabelProvider(new CallTreeLabelProvider(
                callTreeViewer));
        callTreeViewer
                .addSelectionChangedListener(new ISelectionChangedListener() {
                    @Override
                    public void selectionChanged(SelectionChangedEvent event) {
                        callTreeFilteredTree
                                .updateStatusLine((IStructuredSelection) event
                                        .getSelection());
                    }
                });
        callTreeViewer.setUseHashlookup(true);
        callTreeViewer.setInput(cpuModel);

        int page = addPage(panel);
        trees.put(callTreeFilteredTree, page);
        setPageText(page, Messages.callTreePageLabel);
        setPageImage(page, getCallTreeImage());
    }

    /**
     * Creates the hot spots page.
     */
    private void createHotSpotsPage() {
        Composite panel = new Composite(getContainer(), SWT.NONE);
        panel.setLayout(new FillLayout());

        final HotSpotsFilteredTree hotSpotsFilteredTree = new HotSpotsFilteredTree(
                panel, getEditorSite().getActionBars());
        TreeViewer hotSpotsViewer = hotSpotsFilteredTree.getViewer();
        hotSpotsViewer.setContentProvider(new AbstractContentProvider() {
            @Override
            public Object[] getElements(Object inputElement) {
                if (inputElement instanceof ICpuModel) {
                    return ((ICpuModel) inputElement).getHotSpotRoots();
                }
                return new Object[0];
            }
        });
        hotSpotsViewer.setLabelProvider(new HotSpotsLabelProvider(
                hotSpotsFilteredTree));
        hotSpotsViewer
                .addSelectionChangedListener(new ISelectionChangedListener() {
                    @Override
                    public void selectionChanged(SelectionChangedEvent event) {
                        hotSpotsFilteredTree
                                .updateStatusLine((IStructuredSelection) event
                                        .getSelection());
                    }
                });
        hotSpotsViewer.setUseHashlookup(true);
        hotSpotsViewer.setInput(cpuModel);
        hotSpotsFilteredTree.sortColumn(hotSpotsViewer.getTree().getColumn(1));

        int page = addPage(panel);
        trees.put(hotSpotsFilteredTree, page);
        setPageText(page, Messages.hotSpotsPageLabel);
        setPageImage(page, getHotSpotsImage());
    }

    /**
     * Creates the caller/callee page.
     */
    private void createCallerCalleePage() {
        callersCalleesPageBook = new PageBook(getContainer(), SWT.NONE);
        Label messageLabel = new Label(callersCalleesPageBook, SWT.WRAP);
        messageLabel.setText(Messages.noCallersCalleesMessage);

        SashForm sashForm = new SashForm(callersCalleesPageBook, SWT.NONE);
        sashForm.setOrientation(SWT.VERTICAL);
        sashForm.setLayoutData(new GridData(GridData.FILL_BOTH));

        int page = addPage(callersCalleesPageBook);
        createCallerViewer(sashForm, page);
        createCalleeViewer(sashForm, page);

        setPageText(page, Messages.callerCalleePageLabel);
        setPageImage(page, getCallerCalleeImage());

        callersCalleesPageBook.showPage(sashForm);
    }

    /**
     * Creates the caller viewer.
     * 
     * @param parent
     *            The parent composite
     * @param page
     *            The page
     */
    private void createCallerViewer(Composite parent, int page) {
        final HotSpotsFilteredTree callerFilteredTree = new HotSpotsFilteredTree(
                parent, getEditorSite().getActionBars()) {
            @Override
            public ViewerType getViewerType() {
                return ViewerType.Caller;
            }

            @Override
            protected String getMethodColumnName() {
                return org.fusesource.ide.jvmmonitor.internal.ui.properties.cpu.Messages.callerColumnLabel;
            }

            @Override
            protected String getMethodColumnToolTip() {
                return org.fusesource.ide.jvmmonitor.internal.ui.properties.cpu.Messages.callerColumnToolTip;
            }
        };
        TreeViewer callerViewer = callerFilteredTree.getViewer();
        callerViewer.setContentProvider(new AbstractContentProvider() {
            @Override
            public Object[] getElements(Object inputElement) {
                if (inputElement instanceof ICpuModel) {
                    return ((ICpuModel) inputElement).getCallers();
                }
                return new Object[0];
            }
        });
        callerViewer.setLabelProvider(new HotSpotsLabelProvider(
                callerFilteredTree));
        callerViewer
                .addSelectionChangedListener(new ISelectionChangedListener() {
                    @Override
                    public void selectionChanged(SelectionChangedEvent event) {
                        callerFilteredTree
                                .updateStatusLine((IStructuredSelection) event
                                        .getSelection());
                    }
                });
        callerViewer.setUseHashlookup(true);
        callerViewer.setInput(cpuModel);
        callerFilteredTree.sortColumn(callerViewer.getTree().getColumn(1));

        trees.put(callerFilteredTree, page);
    }

    /**
     * Create the callee viewer.
     * 
     * @param parent
     *            The parent composite
     * @param page
     *            The page
     */
    private void createCalleeViewer(Composite parent, int page) {
        final HotSpotsFilteredTree calleeFilteredTree = new HotSpotsFilteredTree(
                parent, getEditorSite().getActionBars()) {
            @Override
            public ViewerType getViewerType() {
                return ViewerType.Callee;
            }

            @Override
            protected String getMethodColumnName() {
                return org.fusesource.ide.jvmmonitor.internal.ui.properties.cpu.Messages.calleeColumnLabel;
            }

            @Override
            protected String getMethodColumnToolTip() {
                return org.fusesource.ide.jvmmonitor.internal.ui.properties.cpu.Messages.calleeColumnToolTip;
            }
        };
        TreeViewer calleeViewer = calleeFilteredTree.getViewer();
        calleeViewer.setContentProvider(new AbstractContentProvider() {
            @Override
            public Object[] getElements(Object inputElement) {
                if (inputElement instanceof ICpuModel) {
                    return ((ICpuModel) inputElement).getCallees();
                }
                return new Object[0];
            }
        });
        calleeViewer.setLabelProvider(new HotSpotsLabelProvider(
                calleeFilteredTree));
        calleeViewer
                .addSelectionChangedListener(new ISelectionChangedListener() {
                    @Override
                    public void selectionChanged(SelectionChangedEvent event) {
                        calleeFilteredTree
                                .updateStatusLine((IStructuredSelection) event
                                        .getSelection());
                    }
                });
        calleeViewer.setUseHashlookup(true);
        calleeViewer.setInput(cpuModel);
        calleeFilteredTree.sortColumn(calleeViewer.getTree().getColumn(1));

        trees.put(calleeFilteredTree, page);
    }

    /**
     * Adds the listeners.
     */
    private void addListeners() {
        cpuModel.addModelChangeListener(new ICpuModelChangeListener() {
            @Override
            public void modelChanged(CpuModelEvent e) {
                Display.getDefault().asyncExec(new Runnable() {
                    @Override
                    public void run() {
                        refresh();
                    }
                });
            }
        });

        addPageChangedListener(new IPageChangedListener() {
            @Override
            public void pageChanged(PageChangedEvent event) {
                pageSelectionChanged();
            }
        });
    }

    /**
     * Notifies that page selection has been changed.
     */
    void pageSelectionChanged() {
        AbstractFilteredTree tree = getActiveFilteredTree();
        if (tree == null) {
            setContentDescription(""); //$NON-NLS-1$
            return;
        }

        collapseAllAction.setViewer(tree.getViewer());
        FindAction findAction = (FindAction) getEditorSite().getActionBars()
                .getGlobalActionHandler(ActionFactory.FIND.getId());
        if (findAction != null) {
            findAction.setViewer(tree.getViewer(), tree.getViewerType());
        }
    }

    /**
     * Loads the dump file.
     * 
     * @param filePath
     *            The file path
     */
    private void loadDumpFile(final String filePath) {

        Job job = new Job(Messages.parseCpuDumpFileJobLabel) {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                CpuDumpParser parser = new CpuDumpParser(new File(filePath),
                        cpuModel, monitor);

                try {
                    parser.parse();
                } catch (ParserConfigurationException e) {
                    return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
                            "Could not load CPU dump file.", e); //$NON-NLS-1$
                } catch (SAXException e) {
                    return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
                            "Could not load CPU dump file.", e); //$NON-NLS-1$
                } catch (IOException e) {
                    return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
                            "Could not load CPU dump file.", e); //$NON-NLS-1$
                }

                setProfileInfo(parser.getProfileInfo());

                return Status.OK_STATUS;
            }
        };
        job.schedule();
    }

    /**
     * Contributes to action bars.
     */
    private void contributeToActionBars() {
        IActionBars actionBars = getEditorSite().getActionBars();
        IToolBarManager manager = actionBars.getToolBarManager();
        collapseAllAction = new CollapseAllAction();

        // check if CollapseAllAction has been already added
        boolean exist = false;
        for (IContributionItem contributionItem : manager.getItems()) {
            if (contributionItem.getId().equals(collapseAllAction.getId())) {
                exist = true;
                break;
            }
        }
        if (!exist) {
            manager.add(collapseAllAction);
        }

        actionBars.setGlobalActionHandler(JdtActionConstants.OPEN,
                new OpenDeclarationAction());
        actionBars.setGlobalActionHandler(ActionFactory.FIND.getId(),
                new FindAction());
        actionBars.setGlobalActionHandler(ActionFactory.COPY.getId(),
                new CopyAction());
    }

    /**
     * Shows the caller/callee tab.
     */
    void showCallerCalleeTab() {
        for (Entry<AbstractFilteredTree, Integer> entry : trees.entrySet()) {
            AbstractFilteredTree filteredTree = entry.getKey();
            if (filteredTree != null
                    && filteredTree.getViewerType() == ViewerType.Caller) {
                setActivePage(entry.getValue());

                // setActivePage() doesn't send PageChangedEvent
                pageSelectionChanged();
            }
        }
    }

    /**
     * Refreshes the callers/callees page.
     */
    private void refreshCallersCalleesPage() {
        Label label = null;
        SashForm sashForm = null;
        for (Control control : callersCalleesPageBook.getChildren()) {
            if (control instanceof Label) {
                label = (Label) control;
            } else if (control instanceof SashForm) {
                sashForm = (SashForm) control;
            }
        }

        if (label == null || sashForm == null) {
            throw new IllegalStateException("label and sashform cannot be null"); //$NON-NLS-1$
        }

        Control control = (cpuModel.getCallersCalleesTarget() == null) ? label
                : sashForm;
        callersCalleesPageBook.showPage(control);
    }

    /**
     * Gets one of filtered trees on active page.
     * 
     * @return The active filtered tree, or <tt>null</tt> if not available (when
     *         selecting info tab)
     */
    private AbstractFilteredTree getActiveFilteredTree() {
        for (Entry<AbstractFilteredTree, Integer> entry : trees.entrySet()) {
            AbstractFilteredTree filteredTree = entry.getKey();
            if (entry.getValue() == getActivePage()) {
                return filteredTree;
            }
        }
        return null;
    }

    /**
     * Refreshes the content description.
     */
    private void refreshContentDescription() {
        StringBuilder description = new StringBuilder();
        AbstractFilteredTree activeTree = getActiveFilteredTree();
        if (activeTree == null) {
            setContentDescription(description.toString());
            return;
        }

        ViewerType type = activeTree.getViewerType();

        IMethodNode callersCalleesTarget = cpuModel.getCallersCalleesTarget();
        if ((type == ViewerType.Caller || type == ViewerType.Callee)
                && callersCalleesTarget != null) {
            description.append(NLS.bind(Messages.callersCalleesTargetIndicator,
                    callersCalleesTarget.getName()));
        }

        ICallTreeNode focusedNode = cpuModel.getFocusTarget();
        if (focusedNode != null) {
            if ((type != ViewerType.Caller && type != ViewerType.Callee)
                    || callersCalleesTarget != null) {
                description.append(NLS.bind(Messages.focusTargetIndicator,
                        focusedNode.getNonqualifiedName()));
            }
        }
        String thread = null;
        if (focusedNode != null) {
            thread = focusedNode.getThread();
        } else if (callersCalleesTarget != null) {
            thread = callersCalleesTarget.getThread();
        }

        if (description.length() > 0 && thread != null) {
            description.append(NLS.bind(Messages.threadIndicator, thread));
        }

        setContentDescription(description.toString());
    }

    /**
     * Gets the call tree image.
     * 
     * @return The call tree image
     */
    private Image getCallTreeImage() {
        if (callTreeImage == null || callTreeImage.isDisposed()) {
            callTreeImage = Activator.getImageDescriptor(
                    ISharedImages.CALL_TREE_IMG_PATH).createImage();
        }
        return callTreeImage;
    }

    /**
     * Gets the hot spots image.
     * 
     * @return The hot spots image
     */
    private Image getHotSpotsImage() {
        if (hotSpotsImage == null || hotSpotsImage.isDisposed()) {
            hotSpotsImage = Activator.getImageDescriptor(
                    ISharedImages.HOT_SPOTS_IMG_PATH).createImage();
        }
        return hotSpotsImage;
    }

    /**
     * Gets the caller callee image.
     * 
     * @return The caller callee image
     */
    private Image getCallerCalleeImage() {
        if (callerCalleeImage == null || callerCalleeImage.isDisposed()) {
            callerCalleeImage = Activator.getImageDescriptor(
                    ISharedImages.CALLER_CALLEE_IMG_PATH).createImage();
        }
        return callerCalleeImage;
    }
}
