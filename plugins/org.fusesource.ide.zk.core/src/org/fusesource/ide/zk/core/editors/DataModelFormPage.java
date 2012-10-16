/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.fusesource.ide.zk.core.editors;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.fusesource.ide.zk.core.model.DataModel;
import org.fusesource.ide.zk.core.model.GenericDataModelEvent;
import org.fusesource.ide.zk.core.widgets.InfoBar;
import org.fusesource.ide.zk.core.widgets.TableEdit;
import org.fusesource.ide.zk.core.widgets.TableEdit.CommitEditRunnable;


/**
 * Base class for a {@link FormPage} used within a {@link DataModelFormEditor}.
 * 
 * @author Mark Masse
 */
public abstract class DataModelFormPage<M extends DataModel<M, ?, ?>> extends FormPage {

    public static final FormData FILL_BOTH_FORM_DATA = new FormData();
    public static final FormData FILL_WIDTH_FORM_DATA = new FormData();

    static {
        FILL_BOTH_FORM_DATA.top = new FormAttachment(0, 0);
        FILL_BOTH_FORM_DATA.left = new FormAttachment(0, 0);
        FILL_BOTH_FORM_DATA.bottom = new FormAttachment(100, 0);
        FILL_BOTH_FORM_DATA.right = new FormAttachment(100, 0);

        FILL_WIDTH_FORM_DATA.left = new FormAttachment(0, 0);
        FILL_WIDTH_FORM_DATA.right = new FormAttachment(100, 0);
    }

    private Composite _Client;
    private boolean _Dirty;
    private Image _Image;
    private InfoBar _InfoBar;
    private String _InfoText;
    private boolean _ProgrammaticUpdate;

    /**
     * Constructor.
     * 
     * @param editor The {@link DataModelFormEditor} that owns this page.
     * @param id The unique page identifier (must be unique within the scope of the editor).
     * @param title The title of the FormPage.
     */
    public DataModelFormPage(DataModelFormEditor<M> editor, String id, String title) {
        super(editor, id, title);
    }

    @SuppressWarnings("unchecked")
    public DataModelFormEditor<M> getDataModelFormEditor() {
        return (DataModelFormEditor<M>) getEditor();
    }

    /**
     * Returns the image.
     * 
     * @return The image
     */
    public Image getImage() {
        return _Image;
    }

    /**
     * Returns the infoText.
     * 
     * @return The infoText
     */
    public String getInfoText() {
        return _InfoText;
    }

    public M getModel() {
        return getDataModelFormEditor().getModel();
    }

    @Override
    public boolean isDirty() {
        return _Dirty;
    }

    /**
     * Returns the programmaticUpdate.
     * 
     * @return The programmaticUpdate
     */
    public boolean isProgrammaticUpdate() {
        return _ProgrammaticUpdate;
    }

    @Override
    public void setActive(boolean active) {
        if (active) {
            forceLayout();
        }
        super.setActive(active);
    }

    /**
     * This method is called directly by the parent editor to signify "external" changes to the dirty state. Subclasses
     * should call {@link #setDirtyInternal(boolean)} to handle dirty state changes that originate from within the page.
     * 
     * @param dirty
     */
    public void setDirty(boolean dirty) {

        boolean dirtyStateChanged = isDirty() != dirty;

        _Dirty = dirty;

        if (dirtyStateChanged) {
            forceLayout();
        }

    }

    /**
     * Subclasses should call this method whenever a dirty state change occurs within the page. The parent editor will
     * notify listeners that its dirty state has changed.
     * 
     * @param dirty
     */
    public final void setDirtyInternal(boolean dirty) {

        boolean dirtyStateChanged = isDirty() != dirty;

        if (isProgrammaticUpdate() && dirtyStateChanged && dirty) {
            return;
        }

        setDirty(dirty);

        if (dirtyStateChanged) {
            getEditor().editorDirtyStateChanged();
        }
    }

    /**
     * Sets the image.
     * 
     * @param image the image to set
     */
    public void setImage(Image image) {
        _Image = image;
    }

    public void setInfoText(String infoText) {
        _InfoText = infoText;

        InfoBar infoBar = getInfoBar();
        if (infoBar != null) {
            infoBar.setText(_InfoText);
            forceLayout();
        }
    }

    /**
     * Sets the programmaticUpdate.
     * 
     * @param programmaticUpdate the programmaticUpdate to set
     */
    public void setProgrammaticUpdate(boolean programmaticUpdate) {
        _ProgrammaticUpdate = programmaticUpdate;
    }

    protected void contributeToToolBar(IToolBarManager toolBarManager) {
    }

    protected final Composite createClient(IManagedForm managedForm, Composite body) {

        FormToolkit toolkit = managedForm.getToolkit();

        Composite client = new Composite(body, SWT.NULL) {

            @Override
            public Point computeSize(int wHint, int hHint, boolean changed) {
                Point superSize = super.computeSize(wHint, hHint, changed);
                return new Point(0, superSize.y);
            }

        };

        toolkit.adapt(client);
        client.setLayout(createClientLayout());

        return client;
    }

    protected Layout createClientLayout() {
        FormLayout clientLayout = new FormLayout();
        clientLayout.marginWidth = 8;
        clientLayout.marginHeight = 8;
        clientLayout.spacing = 8;
        return clientLayout;
    }

    @Override
    protected final void createFormContent(IManagedForm managedForm) {

        makeActions();

        ScrolledForm form = managedForm.getForm();

        form.setText(getTitle());
        form.setImage(getImage());
        FormLayout bodyLayout = new FormLayout();
        bodyLayout.marginWidth = 0;
        bodyLayout.marginHeight = 0;

        Composite body = form.getBody();
        body.setLayout(bodyLayout);
        Dialog.applyDialogFont(body);

        _InfoBar = createInfoBar(body);

        setInfoText(getInfoText());

        _Client = createClient(managedForm, body);

        FormData clientFormData = new FormData();

        if (_InfoBar != null) {
            clientFormData.top = new FormAttachment(_InfoBar, 0, SWT.BOTTOM);
        }
        else {
            clientFormData.top = new FormAttachment(0, 0);
        }
        clientFormData.left = new FormAttachment(0, 0);
        clientFormData.right = new FormAttachment(100, 0);
        clientFormData.bottom = new FormAttachment(100, 0);
        _Client.setLayoutData(clientFormData);

        IToolBarManager toolBarManager = form.getToolBarManager();
        contributeToToolBar(toolBarManager);
        toolBarManager.update(true);

        if (!getModel().isDestroyed()) {
            createModelFormContent(managedForm, _Client);
            initFromModel();
        }

    }

    protected InfoBar createInfoBar(Composite body) {
        InfoBar infoBar = new InfoBar(body, SWT.NULL);
        infoBar.setLayoutData(FILL_WIDTH_FORM_DATA);
        return infoBar;
    }

    protected abstract void createModelFormContent(IManagedForm managedForm, Composite client);

    protected Section createSection(ScrolledForm form, Composite client, FormToolkit toolkit, String title) {
        return createSection(form, client, toolkit, title, null);
    }

    protected Section createSection(ScrolledForm form, Composite client, FormToolkit toolkit, String title, Image image) {
        return createSection(form, client, toolkit, title, image, Section.TITLE_BAR);
    }

    protected Section createSection(final ScrolledForm form, Composite client, FormToolkit toolkit, String title,
            Image image, int style) {

        final Section section = toolkit.createSection(client, style);

        if (image != null) {
            final Composite titleComposite = new Composite(section, SWT.NULL) {

                public Point computeSize(int wHint, int hHint, boolean changed) {
                    Point size = super.computeSize(wHint, hHint, changed);
                    size.x = section.getSize().x - 10;
                    return size;
                }

            };

            section.addControlListener(new ControlAdapter() {

                @Override
                public void controlResized(ControlEvent e) {
                    titleComposite.pack();
                    section.layout(true);
                }

            });

            FormLayout titleCompositeLayout = new FormLayout();
            titleCompositeLayout.marginWidth = 0;
            titleCompositeLayout.marginHeight = 0;
            titleCompositeLayout.spacing = 3;

            titleComposite.setLayout(titleCompositeLayout);
            Label imageLabel = new Label(titleComposite, SWT.NULL);
            imageLabel.setImage(image);

            Label textLabel = new Label(titleComposite, SWT.NULL);
            textLabel.setText(title);
            textLabel.setFont(section.getFont());
            textLabel.setForeground(section.getTitleBarForeground());

            FormData imageLabelFormData = new FormData();
            imageLabelFormData.top = new FormAttachment(0, 0);
            imageLabelFormData.left = new FormAttachment(0, 0);
            imageLabel.setLayoutData(imageLabelFormData);
            imageLabel.pack();

            FormData textLabelFormData = new FormData();
            textLabelFormData.bottom = new FormAttachment(imageLabel, 0, SWT.CENTER);
            textLabelFormData.left = new FormAttachment(imageLabel);
            textLabel.setLayoutData(textLabelFormData);
            textLabel.pack();

            titleComposite.pack();
            section.setTextClient(titleComposite);
        }
        else {
            section.setText(title);
        }

        section.setExpanded(true);
        section.addExpansionListener(new ExpansionAdapter() {

            public void expansionStateChanged(ExpansionEvent e) {
                form.reflow(false);
            }
        });

        return section;
    }

    protected Composite createSectionClient(Section section, FormToolkit toolkit) {

        Composite sectionClient = toolkit.createComposite(section);
        FormLayout sectionClientLayout = new FormLayout();
        sectionClientLayout.marginWidth = 8;
        sectionClientLayout.marginHeight = 2;
        sectionClientLayout.spacing = 8;
        sectionClient.setLayout(sectionClientLayout);

        section.setClient(sectionClient);
        return sectionClient;
    }

    protected Section createTableSection(ScrolledForm form, Composite client, FormToolkit toolkit, String title,
            Image image, int sectionStyle, int tableStyle, String[] columnTitles, int[] columnAlignments) {

        Section section = createSection(form, client, toolkit, title, image, sectionStyle);
        Table table = toolkit.createTable(section, tableStyle);

        for (int i = 0; i < columnTitles.length; i++) {
            TableColumn column = new TableColumn(table, SWT.NONE);
            column.setText(columnTitles[i]);
            column.setAlignment(columnAlignments[i]);
        }

        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        section.setClient(table);

        return section;
    }

    protected Section createTableSection(ScrolledForm form, Composite client, FormToolkit toolkit, String title,
            Image image, int tableStyle, String[] columnTitles, int[] columnAlignments) {

        return createTableSection(form, client, toolkit, title, image, Section.TITLE_BAR, tableStyle, columnTitles,
                columnAlignments);
    }

    protected Section createTableSection(ScrolledForm form, Composite client, FormToolkit toolkit, String title,
            int tableStyle, String[] columnTitles, int[] columnAlignments) {

        return createTableSection(form, client, toolkit, title, null, Section.TITLE_BAR, tableStyle, columnTitles,
                columnAlignments);

    }

    protected void forceLayout() {
        ScrolledForm form = getManagedForm().getForm();
        Composite body = form.getBody();
        body.layout(true);
        Composite client = getClient();
        if (client != null) {
            client.layout(true);
        }
    }

    protected Composite getClient() {
        return _Client;
    }

    /**
     * Returns the infoBar.
     * 
     * @return The infoBar
     */
    protected InfoBar getInfoBar() {
        return _InfoBar;
    }

    protected final void initFromModel() {
        setProgrammaticUpdate(true);

        try {
            initFromModelInternal();
        }
        finally {
            setProgrammaticUpdate(false);
        }
    }

    protected abstract void initFromModelInternal();

    protected final void initTableEdit(Table table, CommitEditRunnable commitEditRunnable, int columnIndex) {
        new TableEdit(table, commitEditRunnable, columnIndex);
    }

    protected void initYesNoInfoBar(IManagedForm managedForm, SelectionListener yesListener) {

        SelectionListener noListener = new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                setInfoText(null);
            }

        };

        initYesNoInfoBar(managedForm, yesListener, noListener);
    }

    /**
     * Initializes the {@link InfoBar} with "Yes" and "No" {@link Button buttons}.
     * 
     * @param managedForm The {@link IManagedForm} instance.
     * @param yesListener The {@link SelectionListener} to handle the Yes button selection.
     */
    protected void initYesNoInfoBar(IManagedForm managedForm, SelectionListener yesListener,
            SelectionListener noListener) {

        InfoBar infoBar = getInfoBar();

        FormToolkit toolkit = managedForm.getToolkit();

        Button yesButton = toolkit.createButton(infoBar, "Yes", SWT.PUSH);
        if (yesListener != null) {
            yesButton.addSelectionListener(yesListener);
        }

        Button noButton = toolkit.createButton(infoBar, "No", SWT.PUSH);
        if (noListener != null) {
            noButton.addSelectionListener(noListener);
        }

        FormData yesButtonFormData = new FormData();
        yesButtonFormData.top = new FormAttachment(0, 0);
        yesButtonFormData.right = new FormAttachment(noButton);
        yesButton.setLayoutData(yesButtonFormData);

        FormData noButtonFormData = new FormData();
        noButtonFormData.top = new FormAttachment(0, 0);
        noButtonFormData.right = new FormAttachment(100, 0);
        noButton.setLayoutData(noButtonFormData);

        FormData labelFormData = new FormData();
        labelFormData.top = new FormAttachment(yesButton, 0, SWT.CENTER);
        labelFormData.left = new FormAttachment(0, 0);
        infoBar.getLabel().setLayoutData(labelFormData);

    }

    protected void makeActions() {
    }

    protected void modelDataChanged(GenericDataModelEvent event) {
        modelModifiedExternally();
    }

    protected void modelDataRefreshed(GenericDataModelEvent event) {
        modelModifiedExternally();
    }

    protected void modelDestroyed(GenericDataModelEvent event) {
    }

    protected void modelModifiedExternally() {
        if (!getModel().isDestroyed()) {
            initFromModel();
        }
    }

    protected void packTable(Table table, int[] columnWidths) {
        if (table == null) {
            return;
        }

        table.pack();

        TableColumn[] columns = table.getColumns();

        for (int i = 0; i < columns.length; i++) {

            int columnWidth = columnWidths[i];
            if (columnWidth == SWT.DEFAULT) {
                columns[i].pack();
            }
            else {
                columns[i].setWidth(columnWidth);
            }
        }
    }

    /**
     * Called whenever the editor has completed a save of this page.
     */
    protected void saveCompleted() {
        setDirty(false);
    }

}
