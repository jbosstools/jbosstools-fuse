/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.editors;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.fusesource.ide.jvmmonitor.core.dump.IProfileInfo;
import org.fusesource.ide.jvmmonitor.ui.Activator;
import org.fusesource.ide.jvmmonitor.ui.ISharedImages;


/**
 * The dump editor.
 */
abstract public class AbstractDumpEditor extends MultiPageEditorPart {

    /** The info page. */
    InfoPage infoPage;

    /** The profile info. */
    IProfileInfo info;

    /** The info image. */
    private Image infoImage;

    /** The resource change listener. */
    private IResourceChangeListener resourceChangeListener;

    /*
     * @see MultiPageEditorPart#createPages()
     */
    @Override
    final protected void createPages() {
        createClientPages();
        createInfoPage();

        addPageChangedListener(new IPageChangedListener() {
            @Override
            public void pageChanged(PageChangedEvent event) {
                if (event.getSelectedPage().equals(infoPage)) {
                    infoPage.focusCommnentsText();
                }
            }
        });

        resourceChangeListener = new ResourceChangeListener();
        ResourcesPlugin.getWorkspace().addResourceChangeListener(
                resourceChangeListener);

    }

    /*
     * @see EditorPart#doSave(IProgressMonitor)
     */
    @Override
    public void doSave(IProgressMonitor monitor) {
        infoPage.doSave(monitor);
    }

    /*
     * @see EditorPart#doSaveAs()
     */
    @Override
    public void doSaveAs() {
        // do nothing
    }

    /*
     * @see EditorPart#isDirty()
     */
    @Override
    public boolean isDirty() {
        return infoPage.isDirty();
    }

    /*
     * @see EditorPart#isSaveAsAllowed()
     */
    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

    /*
     * @see WorkbenchPart#firePropertyChange(int)
     */
    @Override
    public void firePropertyChange(int propertyId) {
        super.firePropertyChange(propertyId);
    }

    /*
     * @see MultiPageEditorPart#dispose()
     */
    @Override
    public void dispose() {
        super.dispose();
        if (infoImage != null) {
            infoImage.dispose();
        }
        if (resourceChangeListener != null) {
            ResourcesPlugin.getWorkspace().removeResourceChangeListener(
                    resourceChangeListener);
        }
    }

    /**
     * Creates the client pages.
     */
    abstract protected void createClientPages();

    /**
     * Sets the profile info.
     * 
     * @param profileInfo
     *            The profile info
     */
    protected void setProfileInfo(IProfileInfo profileInfo) {
        this.info = profileInfo;
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                if (infoPage != null) {
                    infoPage.refresh(info);
                }
            }
        });
    }

    /**
     * Creates the info page.
     */
    private void createInfoPage() {
        infoPage = new InfoPage(this, getContainer());
        int page = addPage(infoPage);
        setPageText(page, Messages.infoTabLabel);
        setPageImage(page, getInfoImage());

        if (info != null) {
            infoPage.refresh(info);
        }
    }

    /**
     * Gets the info image.
     * 
     * @return The info image
     */
    private Image getInfoImage() {
        if (infoImage == null || infoImage.isDisposed()) {
            infoImage = Activator.getImageDescriptor(
                    ISharedImages.INFO_IMG_PATH).createImage();
        }
        return infoImage;
    }

    /**
     * The resource change listener.
     */
    private class ResourceChangeListener implements IResourceChangeListener {

        /**
         * The constructor.
         */
        public ResourceChangeListener() {
            // do nothing
        }

        /*
         * @see IResourceChangeListener#resourceChanged(IResourceChangeEvent)
         */
        @Override
        public void resourceChanged(IResourceChangeEvent event) {
            IResourceDelta delta = event.getDelta();
            if (delta != null && searchFile(delta)) {
                for (IWorkbenchWindow window : PlatformUI.getWorkbench()
                        .getWorkbenchWindows()) {
                    for (final IWorkbenchPage page : window.getPages()) {
                        Display.getDefault().asyncExec(new Runnable() {
                            @Override
                            public void run() {
                                page.closeEditor(AbstractDumpEditor.this, false);
                            }
                        });
                    }
                }
            }
        }

        /**
         * Search the resource corresponding to editor input in given resource
         * delta and its children.
         * 
         * @param delta
         *            The resource delta
         * @return True if a file corresponding to editor input is found
         */
        private boolean searchFile(IResourceDelta delta) {
            IEditorInput input = AbstractDumpEditor.this.getEditorInput();
            if (input instanceof IFileEditorInput) {
                String filePath = ((IFileEditorInput) input).getFile()
                        .getFullPath().toOSString();
                if (delta.getKind() == IResourceDelta.REMOVED
                        && delta.getFullPath().toOSString().equals(filePath)) {
                    return true;
                }
            }

            for (IResourceDelta element : delta.getAffectedChildren()) {
                if (searchFile(element)) {
                    return true;
                }
            }
            return false;
        }
    }
}
