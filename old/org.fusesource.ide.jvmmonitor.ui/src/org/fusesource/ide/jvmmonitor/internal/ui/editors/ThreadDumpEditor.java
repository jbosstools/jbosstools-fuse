/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.editors;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.fusesource.ide.jvmmonitor.core.IThreadElement;
import org.fusesource.ide.jvmmonitor.core.dump.ThreadDumpParser;
import org.fusesource.ide.jvmmonitor.internal.ui.IHelpContextIds;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.thread.IThreadInput;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.thread.ThreadSashForm;
import org.fusesource.ide.jvmmonitor.ui.Activator;
import org.fusesource.ide.jvmmonitor.ui.ISharedImages;

import org.xml.sax.SAXException;

/**
 * The thread dump editor.
 */
public class ThreadDumpEditor extends AbstractDumpEditor {

    /** The thread sash form. */
    ThreadSashForm threadSashForm;

    /** The thread list elements. */
    List<IThreadElement> threadListElements;

    /** The thread image. */
    private Image threadImage;

    /**
     * The constructor.
     */
    public ThreadDumpEditor() {
        threadListElements = new ArrayList<IThreadElement>();
    }

    /*
     * @see AbstractDumpEditor#createClientPages()
     */
    @Override
    protected void createClientPages() {
        createThreadsPage();

        PlatformUI.getWorkbench().getHelpSystem()
                .setHelp(getContainer(), IHelpContextIds.THREADS_DUMP_EDITOR);
    }

    /*
     * @see EditorPart#init(IEditorSite, IEditorInput)
     */
    @Override
    public void init(IEditorSite site, IEditorInput input)
            throws PartInitException {
        setSite(site);
        setInput(input);

        setPartName(input.getName());

        if (input instanceof IFileEditorInput) {
            String filePath = ((IFileEditorInput) input).getFile()
                    .getRawLocation().toOSString();
            parseDumpFile(filePath);
        } else if (input instanceof FileStoreEditorInput) {
            String filePath = ((FileStoreEditorInput) input).getURI().getPath();
            parseDumpFile(filePath);
        }
    }

    /*
     * @see WorkbenchPart#setFocus()
     */
    @Override
    public void setFocus() {
        threadSashForm.setFocus();
    }

    /*
     * @see AbstractDumpEditor#dispose()
     */
    @Override
    public void dispose() {
        super.dispose();
        if (threadImage != null) {
            threadImage.dispose();
        }
    }

    /**
     * Gets the thread sash form.
     * 
     * @return The thread sash form
     */
    protected ThreadSashForm getThreadSashForm() {
        return threadSashForm;
    }

    /**
     * Creates the threads page.
     */
    private void createThreadsPage() {
        threadSashForm = new ThreadSashForm(getContainer(), getEditorSite()
                .getActionBars());
        threadSashForm.setInput(new IThreadInput() {
            @Override
            public IThreadElement[] getThreadListElements() {
                return threadListElements.toArray(new IThreadElement[0]);
            }
        });
        int page = addPage(threadSashForm);
        setPageText(page, Messages.threadsTabLabel);
        setPageImage(page, getThreadImage());

        threadSashForm.refresh();
    }

    /**
     * Gets the thread image.
     * 
     * @return The thread image
     */
    private Image getThreadImage() {
        if (threadImage == null || threadImage.isDisposed()) {
            threadImage = Activator.getImageDescriptor(
                    ISharedImages.THREAD_IMG_PATH).createImage();
        }
        return threadImage;
    }

    /**
     * Parses the dump file.
     * 
     * @param filePath
     *            The file path
     */
    private void parseDumpFile(final String filePath) {

        Job job = new Job(Messages.parseThreadDumpFileJobLabel) {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                final ThreadDumpParser parser = new ThreadDumpParser(new File(
                        filePath), threadListElements, monitor);

                try {
                    parser.parse();
                } catch (ParserConfigurationException e) {
                    return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
                            "Could not load thread dump file.", e); //$NON-NLS-1$
                } catch (SAXException e) {
                    return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
                            "Could not load thread dump file.", e); //$NON-NLS-1$
                } catch (IOException e) {
                    return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
                            "Could not load thread dump file.", e); //$NON-NLS-1$
                }

                setProfileInfo(parser.getProfileInfo());
                Display.getDefault().asyncExec(new Runnable() {
                    @Override
                    public void run() {
                        if (threadSashForm != null) {
                            threadSashForm.refresh();
                        }
                    }
                });

                return Status.OK_STATUS;
            }
        };
        job.schedule();
    }
}
