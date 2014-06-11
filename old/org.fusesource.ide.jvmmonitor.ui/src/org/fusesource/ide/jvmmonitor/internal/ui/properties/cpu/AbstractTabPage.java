/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.cpu;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.part.PageBook;
import org.fusesource.ide.jvmmonitor.core.IActiveJvm;
import org.fusesource.ide.jvmmonitor.core.cpu.ICpuModelChangeListener;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.cpu.actions.ConfigureCpuProfilerAction;


/**
 * The tab page.
 */
abstract public class AbstractTabPage extends PageBook {

    /** The message page. */
    Composite messagePage;

    /** The active JVM. */
    protected IActiveJvm jvm;

    /** The view form. */
    protected ViewForm viewForm;

    /** The method label. */
    private Label contentDescriptionLabel;

    /** The CPU model change listener. */
    protected ICpuModelChangeListener cpuModelChangeListener;

    /** The CPU section. */
    CpuSection cpuSection;

    /**
     * The constructor.
     * 
     * @param cpuSection
     *            The CPU section
     * @param tabFolder
     *            The tab folder
     */
    public AbstractTabPage(CpuSection cpuSection, CTabFolder tabFolder) {
        super(tabFolder, SWT.NONE);
        this.cpuSection = cpuSection;

        createMessageLabel();
        createViewForm();

        showPage(viewForm);

        createContentDescriptionLabel();
    }

    /*
     * @see Widget#dispose()
     */
    @Override
    public void dispose() {
        super.dispose();
        if (jvm != null) {
            jvm.getCpuProfiler().getCpuModel()
                    .removeModelChangeListener(cpuModelChangeListener);
        }
    }

    /**
     * Update the page.
     * 
     * @param isPackageSpecified
     *            The state indicating if the packages are specified
     */
    protected void updatePage(boolean isPackageSpecified) {
        if (viewForm.isDisposed() || messagePage.isDisposed()) {
            return;
        }

        if (isPackageSpecified) {
            showPage(viewForm);
        } else {
            showPage(messagePage);
        }
    }

    /**
     * Sets the content description.
     * 
     * @param desctiption
     *            The content description
     */
    protected void setContentDescription(String desctiption) {
        if (desctiption == null || desctiption.isEmpty()) {
            if (!viewForm.isDisposed()) {
                viewForm.setTopLeft(null);
            }
        } else {
            if (!contentDescriptionLabel.isDisposed()) {
                contentDescriptionLabel.setText(desctiption);
                contentDescriptionLabel.setToolTipText(desctiption);
                viewForm.setTopLeft(contentDescriptionLabel);
            }
        }
    }

    /**
     * Sets the input.
     * 
     * @param newJvm
     *            The active JVM
     */
    protected void setInput(IActiveJvm newJvm) {
        if (!newJvm.equals(jvm)) {
            if (jvm != null) {
                jvm.getCpuProfiler().getCpuModel()
                        .removeModelChangeListener(cpuModelChangeListener);
            }
            jvm = newJvm;
            newJvm.getCpuProfiler().getCpuModel()
                    .addModelChangeListener(cpuModelChangeListener);
        }
    }

    /**
     * Refreshes.
     */
    abstract protected void refresh();

    /**
     * Gets the filtered trees.
     * 
     * @return The filtered trees
     */
    abstract protected List<AbstractFilteredTree> getFilteredTrees();

    /**
     * Creates the message label.
     */
    private void createMessageLabel() {
        messagePage = new Composite(this, SWT.NONE);
        messagePage.setLayout(new GridLayout(3, false));
        messagePage.setBackground(Display.getDefault().getSystemColor(
                SWT.COLOR_LIST_BACKGROUND));

        FormToolkit toolkit = new FormToolkit(Display.getDefault());
        toolkit.createLabel(messagePage, Messages.patckagesNotSpecifiedMsg);
        Hyperlink hyperlink = toolkit.createHyperlink(messagePage,
                Messages.selectPackagesMsg, SWT.NONE);
        hyperlink.addHyperlinkListener(new HyperlinkAdapter() {
            @Override
            public void linkActivated(HyperlinkEvent e) {
                new ConfigureCpuProfilerAction(cpuSection).run();
            }
        });
    }

    /**
     * Creates the view form.
     */
    private void createViewForm() {
        viewForm = new ViewForm(this, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.verticalSpacing = 1;
        viewForm.setLayout(layout);
    }

    /**
     * Creates the content description label.
     */
    private void createContentDescriptionLabel() {
        contentDescriptionLabel = new Label(viewForm, SWT.NONE);
        contentDescriptionLabel.setLayoutData(new GridData(
                GridData.FILL_HORIZONTAL));
    }
}
