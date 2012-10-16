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

package org.fusesource.ide.zk.zookeeper.editors.znodeform;


import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.HyperlinkGroup;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.fusesource.ide.zk.zookeeper.ZooKeeperActivator;
import org.fusesource.ide.zk.zookeeper.data.Znode;
import org.fusesource.ide.zk.zookeeper.model.ZnodeModel;
import org.fusesource.ide.zk.core.ui.FileEditor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public class ZnodeModelFileDataEditor extends BaseZnodeModelDataEditor {

    private static final String[] FILTER_EXTENSIONS = new String[] { ".txt", ".xml", ".properties", "*.*" };
    private static final String[] FILTER_NAMES = new String[] { "Text (*.txt)", "XML (*.xml)", "Properties (*.properties)", "All (*.*)" };
    
    private static final String EXPORT_BUTTON_TEXT = "Export.  Edit Znode's data in a file editor.";

    private static final String IMPORT_BUTTON_TEXT = "Import.  Replace Znode's data with the contents of a file.";
    private static final String SAVE_MESSAGE_LABEL_TEXT = "NOTE: 'Save' will replace the Znode's data with the contents of the file:";
    private Button _ExportButton;

    private List<Control> _ExportControls;
    private Button _ExportEditorPreferenceCheckBox;
    private FileEditor _ExportFileEditor;
    // private IEditorPart _ExportFileEditorPart;
    private Label _ExportLabel;
    private Button _ExportRadioButton;
    private Text _ExportText;

    private FileEditMode _FileEditMode;
    private ImageHyperlink _FileImageHyperlink;

    private Button _ImportButton;
    private List<Control> _ImportControls;
    private Label _ImportLabel;
    private Button _ImportRadioButton;
    private Text _ImportText;

    private Composite _SaveMessageComposite;

    /**
     * TODO: Comment.
     * 
     * @param dataZnodeFormPage
     * @param parent
     * @param style
     */
    public ZnodeModelFileDataEditor(final ZnodeModelDataFormPage dataZnodeFormPage, Composite parent, int style) {
        super(dataZnodeFormPage, parent, style);

        // final IPartService partService = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService();
        // final IPartListener exportFileEditorPartClosed = new IPartListener() {
        //            
        // @Override
        // public void partOpened(IWorkbenchPart part) {
        // }
        //            
        // @Override
        // public void partDeactivated(IWorkbenchPart part) {
        // }
        //            
        // @Override
        // public void partClosed(IWorkbenchPart part) {
        //                
        // if (part == _ExportFileEditorPart) {
        // ZnodeEditor editor = getZnodeEditor();
        // editor.getSite().getPage().activate(editor);
        // editor.setActivePage(dataZnodeFormPage.getId());
        // }
        // }
        //            
        // @Override
        // public void partBroughtToTop(IWorkbenchPart part) {
        // }
        //            
        // @Override
        // public void partActivated(IWorkbenchPart part) {
        // }
        // };
        //        
        // partService.addPartListener(exportFileEditorPartClosed);

        addDisposeListener(new DisposeListener() {

            @Override
            public void widgetDisposed(DisposeEvent e) {

                // partService.removePartListener(exportFileEditorPartClosed);

                if (_ExportFileEditor != null) {
                    _ExportFileEditor.dispose();
                }
            }
        });

    }

    /**
     * Returns the fileEditMode.
     * 
     * @return The fileEditMode
     */
    public FileEditMode getFileEditMode() {
        return _FileEditMode;
    }

    @Override
    public byte[] getZnodeDataFromEditor() throws IOException {

        File file = null;
        FileEditor fileEditor = null;

        if (_FileEditMode == FileEditMode.IMPORT) {

            String filePath = _ImportText.getText();
            file = new File(filePath);
            fileEditor = new FileEditor(file);

        }
        else if (_FileEditMode == FileEditMode.EXPORT) {
            fileEditor = _ExportFileEditor;
            file = fileEditor.getFile();
        }

        String fileName = file.getName();
        if (!file.exists()) {
            throw new IOException("File '" + fileName + "' does not exist.");
        }

        if (!file.isFile()) {
            throw new IOException("Path '" + file.getAbsolutePath() + "' is not a valid file.");
        }

        long length = file.length();

        if (length > Znode.MAX_DATA_SIZE) {
            throw new IOException("File '" + fileName + "' size is greater than the maximum allowed Znode data size ("
                    + Znode.MAX_DATA_SIZE + " bytes).");
        }

        return fileEditor.read();
    }

    @Override
    public void setDirty(boolean dirty) {
        boolean dirtyStateChanged = dirty != isDirty();
        super.setDirty(dirty);

        _ImportRadioButton.setEnabled(!dirty || _FileEditMode == FileEditMode.IMPORT);
        _ExportRadioButton.setEnabled(!dirty || _FileEditMode == FileEditMode.EXPORT);

        boolean exportControlsEnabled = (!dirty && _FileEditMode == FileEditMode.EXPORT);
        _ExportButton.setEnabled(exportControlsEnabled);
        _ExportEditorPreferenceCheckBox.setEnabled(exportControlsEnabled);

        _SaveMessageComposite.setVisible(dirty);

        if (dirty) {

            boolean preferIdeEditor = _ExportEditorPreferenceCheckBox.getSelection();

            String filePath;
            File file;
            Image fileImage;

            if (_FileEditMode == FileEditMode.IMPORT) {
                filePath = _ImportText.getText();
                file = new File(filePath);
                fileImage = FileEditor.getFileAssociationImage(filePath, preferIdeEditor);
            }
            else {
                file = _ExportFileEditor.getFile();
                filePath = file.getAbsolutePath();
                fileImage = _ExportFileEditor.getFileAssociationImage();
            }

            _FileImageHyperlink.setImage(fileImage);
            _FileImageHyperlink.setText(file.getName());
            _FileImageHyperlink.setToolTipText(filePath);
            _FileImageHyperlink.setData(file);
            _FileImageHyperlink.setHref(filePath);
            
            _SaveMessageComposite.layout(true);

        }
        else if (dirtyStateChanged) {
            _ImportText.setText("");
            _ExportText.setText("");

            if (_ExportFileEditor != null) {
                _ExportFileEditor.dispose();
                _ExportFileEditor = null;
            }
        }
    }

    /**
     * Sets the fileEditMode.
     * 
     * @param fileEditMode the fileEditMode to set
     */
    public void setFileEditMode(FileEditMode fileEditMode) {

        _FileEditMode = fileEditMode;

        if (_FileEditMode == FileEditMode.IMPORT) {
            setControlsEnabled(_ExportControls, false);
            setControlsEnabled(_ImportControls, true);
        }
        else if (_FileEditMode == FileEditMode.EXPORT) {
            setControlsEnabled(_ImportControls, false);
            setControlsEnabled(_ExportControls, true);
        }
    }

    @Override
    protected void createContent() {

        FormToolkit toolkit = getToolkit();

        _ImportRadioButton = toolkit.createButton(this, IMPORT_BUTTON_TEXT, SWT.RADIO);
        _ImportRadioButton.setSelection(true);
        _ImportRadioButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                setFileEditMode(FileEditMode.IMPORT);
            }

        });

        _ImportControls = new ArrayList<Control>(3);

        _ImportLabel = toolkit.createLabel(this, "File:");
        _ImportControls.add(_ImportLabel);

        _ImportText = toolkit.createText(this, null, SWT.BORDER | SWT.SINGLE);
        _ImportText.setEditable(false);
        _ImportControls.add(_ImportText);

        _ImportButton = toolkit.createButton(this, "Import...", SWT.PUSH);
        _ImportButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                startImport();
            }

        });

        _ImportControls.add(_ImportButton);

        _ExportRadioButton = toolkit.createButton(this, EXPORT_BUTTON_TEXT, SWT.RADIO);
        _ExportRadioButton.setSelection(false);
        _ExportRadioButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                setFileEditMode(FileEditMode.EXPORT);
            }

        });

        _ExportControls = new ArrayList<Control>(4);

        _ExportEditorPreferenceCheckBox = toolkit.createButton(this, "Prefer IDE editor", SWT.CHECK);
        _ExportEditorPreferenceCheckBox.setSelection(true);
        _ExportControls.add(_ExportEditorPreferenceCheckBox);

        _ExportLabel = toolkit.createLabel(this, "File:");
        _ExportControls.add(_ExportLabel);

        _ExportText = toolkit.createText(this, null, SWT.BORDER | SWT.SINGLE);
        _ExportText.setEditable(false);
        _ExportControls.add(_ExportText);

        _ExportButton = toolkit.createButton(this, "Export...", SWT.PUSH);
        _ExportButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                startExport();
            }

        });
        _ExportControls.add(_ExportButton);

        _SaveMessageComposite = toolkit.createComposite(this);
        toolkit.adapt(_SaveMessageComposite);
        FormLayout saveMessageCompositeLayout = new FormLayout();
        saveMessageCompositeLayout.marginWidth = 0;
        saveMessageCompositeLayout.marginBottom = 0;
        saveMessageCompositeLayout.marginTop = 20;
        _SaveMessageComposite.setLayout(saveMessageCompositeLayout);
        _SaveMessageComposite.setVisible(false);

        Label saveMessageLabel = toolkit.createLabel(_SaveMessageComposite, SAVE_MESSAGE_LABEL_TEXT, SWT.LEFT
                | SWT.WRAP);

        _FileImageHyperlink = toolkit.createImageHyperlink(_SaveMessageComposite, SWT.CENTER);
        _FileImageHyperlink.setUnderlined(false);
        HyperlinkGroup hyperlinkGroup = new HyperlinkGroup(getDisplay());
        hyperlinkGroup.setHyperlinkUnderlineMode(HyperlinkGroup.UNDERLINE_HOVER);
        hyperlinkGroup.add(_FileImageHyperlink);

        _FileImageHyperlink.addHyperlinkListener(new HyperlinkAdapter() {

            @Override
            public void linkActivated(HyperlinkEvent e) {
                File file = (File) _FileImageHyperlink.getData();
                try {
                    FileEditor.editFile(file, _ExportEditorPreferenceCheckBox.getSelection());
                }
                catch (Exception e1) {
                    // TODO: Log error?
                    Shell shell = getShell();
                    MessageBox errorMessageBox = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);

                    errorMessageBox.setMessage("Failed to open file '" + file + "'.");
                    errorMessageBox.setText("Open Failed");
                    errorMessageBox.open();
                }
            }
        });

        FormData importRadioButtonFormData = new FormData();
        importRadioButtonFormData.top = new FormAttachment(0, 0);
        importRadioButtonFormData.left = new FormAttachment(0, 0);
        importRadioButtonFormData.right = new FormAttachment(100, 0);
        _ImportRadioButton.setLayoutData(importRadioButtonFormData);

        FormData importLabelFormData = new FormData();
        importLabelFormData.top = new FormAttachment(_ImportRadioButton, 6, SWT.BOTTOM);
        importLabelFormData.left = new FormAttachment(_ImportRadioButton, 12, SWT.LEFT);
        _ImportLabel.setLayoutData(importLabelFormData);

        FormData importTextFormData = new FormData();
        importTextFormData.top = new FormAttachment(_ImportLabel, 0, SWT.CENTER);
        importTextFormData.left = new FormAttachment(_ImportLabel);
        importTextFormData.right = new FormAttachment(_ImportButton, 0, SWT.LEFT);
        _ImportText.setLayoutData(importTextFormData);

        FormData importButtonFormData = new FormData();
        importButtonFormData.top = new FormAttachment(_ImportText, 0, SWT.CENTER);
        importButtonFormData.right = new FormAttachment(100, 0);
        _ImportButton.setLayoutData(importButtonFormData);

        FormData exportRadioButtonFormData = new FormData();
        exportRadioButtonFormData.top = new FormAttachment(_ImportLabel, 20);
        exportRadioButtonFormData.left = new FormAttachment(0, 0);
        exportRadioButtonFormData.right = new FormAttachment(100, 0);
        _ExportRadioButton.setLayoutData(exportRadioButtonFormData);

        FormData exportEditorPreferenceCheckBoxFormData = new FormData();
        exportEditorPreferenceCheckBoxFormData.top = new FormAttachment(_ExportRadioButton, 6, SWT.BOTTOM);
        exportEditorPreferenceCheckBoxFormData.left = new FormAttachment(_ExportRadioButton, 12, SWT.LEFT);
        exportEditorPreferenceCheckBoxFormData.right = new FormAttachment(100, 0);
        _ExportEditorPreferenceCheckBox.setLayoutData(exportEditorPreferenceCheckBoxFormData);

        FormData exportLabelFormData = new FormData();
        exportLabelFormData.top = new FormAttachment(_ExportEditorPreferenceCheckBox);
        exportLabelFormData.left = new FormAttachment(_ExportEditorPreferenceCheckBox, 0, SWT.LEFT);
        _ExportLabel.setLayoutData(exportLabelFormData);

        FormData exportTextFormData = new FormData();
        exportTextFormData.top = new FormAttachment(_ExportLabel, 0, SWT.CENTER);
        exportTextFormData.left = new FormAttachment(_ExportLabel);
        exportTextFormData.right = new FormAttachment(_ExportButton, 0, SWT.LEFT);
        _ExportText.setLayoutData(exportTextFormData);

        FormData exportButtonFormData = new FormData();
        exportButtonFormData.top = new FormAttachment(_ExportText, 0, SWT.CENTER);
        exportButtonFormData.right = new FormAttachment(100, 0);
        _ExportButton.setLayoutData(exportButtonFormData);

        FormData saveMessageCompositeFormData = new FormData();
        saveMessageCompositeFormData.top = new FormAttachment(_ExportText);
        saveMessageCompositeFormData.left = new FormAttachment(0, 0);
        saveMessageCompositeFormData.right = new FormAttachment(100, 0);
        saveMessageCompositeFormData.bottom = new FormAttachment(100, 0);
        _SaveMessageComposite.setLayoutData(saveMessageCompositeFormData);

        FormData saveMessageLabelFormData = new FormData();
        saveMessageLabelFormData.top = new FormAttachment(0, 0);
        saveMessageLabelFormData.left = new FormAttachment(0, 0);
        saveMessageLabelFormData.right = new FormAttachment(100, 0);
        saveMessageLabel.setLayoutData(saveMessageLabelFormData);

        FormData fileImageHyperlinkFormData = new FormData();
        fileImageHyperlinkFormData.top = new FormAttachment(saveMessageLabel, 6);
        fileImageHyperlinkFormData.left = new FormAttachment(saveMessageLabel, 12, SWT.LEFT);
        fileImageHyperlinkFormData.right = new FormAttachment(100, 0);
        _FileImageHyperlink.setLayoutData(fileImageHyperlinkFormData);

        setFileEditMode(FileEditMode.IMPORT);

    }

    @Override
    protected void hookSyncZnodeModelData(ZnodeModel znodeModel) {
        // This editor does not display the znode data, so there is nothing to do
    }

    private void setControlsEnabled(List<Control> controls, boolean enabled) {
        for (Control control : controls) {
            control.setEnabled(enabled);
        }
    }

    private void startExport() {

        ZnodeModel znodeModel = getZnodeModel();
        String prefix = znodeModel.getData().getRelativePath();
        if (prefix.equals(Znode.ROOT_PATH)) {
            prefix = "root";
        }

        File directory = ZooKeeperActivator.getDefault().getZnodeDataDirectory();
        directory.mkdirs();

        FileDialog fileDialog = new FileDialog(getManagedForm().getForm().getShell(), SWT.SAVE);
        fileDialog.setFileName(prefix + ".txt");
        fileDialog.setFilterExtensions(FILTER_EXTENSIONS);
        fileDialog.setFilterNames(FILTER_NAMES);
        fileDialog.setFilterPath(directory.getAbsolutePath());
        if (fileDialog.open() == null) {
            return;
        }

        String selectedPath = fileDialog.getFileName();

        IPath path = new Path(selectedPath);
        String suffix = path.getFileExtension();
        if (suffix != null) {
            suffix.trim();
        }
        else {
            suffix = ".txt";
        }

        if (suffix.length() == 0) {
            suffix = ".txt";
        }
        else {
            suffix = "." + suffix;
        }

        Znode znode = znodeModel.getData();
        byte[] data = znode.getData();

        try {
            _ExportFileEditor = FileEditor.createTempFileEditor(prefix, suffix, directory, data,
                    _ExportEditorPreferenceCheckBox.getSelection());
        }
        catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            return;
        }

        try {
            // _ExportFileEditorPart = _ExportFileEditor.edit();
            _ExportFileEditor.edit();
        }
        catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            return;
        }

        _ExportText.setText(_ExportFileEditor.getFile().getAbsolutePath());

        setDirtyInternal(true);

    }

    private void startImport() {

        FileDialog fileDialog = new FileDialog(getShell());
        fileDialog.setFileName(_ImportText.getText());
        String newPath = fileDialog.open();
        if (newPath != null) {
            _ImportText.setText(newPath);
            setDirtyInternal(true);
        }
    }

    /**
     * TODO: Comment.
     * 
     * @author Mark Masse
     */
    public static enum FileEditMode {
        EXPORT,
        IMPORT;
    }

}
