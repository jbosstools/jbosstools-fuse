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

package org.fusesource.ide.zk.zookeeper.wizards.newznode;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.fusesource.ide.zk.zookeeper.data.Znode;
import org.fusesource.ide.zk.zookeeper.model.ZnodeModel;
import org.fusesource.ide.zk.core.ui.FileEditor;
import org.fusesource.ide.zk.core.widgets.grid.GridComposite;
import org.fusesource.ide.zk.core.widgets.grid.GridCompositeStatus;
import org.fusesource.ide.zk.core.widgets.grid.GridTextInput;

import java.io.File;
import java.io.IOException;


/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public class ZnodeNewWizardComposite1 extends ZnodeNewWizardComposite {

    private static final String CONTROL_NAME_DATA_FILE_BUTTON = "File";
    private static final String CONTROL_NAME_DATA_FILE_COMPOSITE = "Data File Composite";
    private static final String CONTROL_NAME_DATA_FILE_TEXT = "Data (File)";

    private static final String CONTROL_NAME_DATA_SINGLE_LINE_COMPOSITE = "Data Single Line Text Composite";
    private static final String CONTROL_NAME_DATA_SINGLE_LINE_TEXT = "Data (Text)";
    private static final String CONTROL_NAME_DATA_SINGLE_LINE_TEXT_BUTTON = "Text";

    private static final String CONTROL_NAME_DATA_STACK_COMPOSITE = "Data Stack Composite";

    private static final String CONTROL_NAME_CREATE_MODE_EPHEMERAL_BUTTON = "Ephemeral";
    private static final String CONTROL_NAME_PATH_TEXT = "Path";
    private static final String CONTROL_NAME_CREATE_MODE_PERSISTENT_BUTTON = "Persistent";
    private static final String CONTROL_NAME_CREATE_MODE_SEQUENTIAL_BUTTON = "Sequential";

    /**
     * TODO: Comment.
     * 
     * @param parent
     */
    public ZnodeNewWizardComposite1(Composite parent, ZnodeModel parentZnodeModel) {
        super(parent, parentZnodeModel);
    }

    public Znode getZnode() throws Exception {

        byte[] data = getZnodeData();

        ZnodeModel parentZnodeModel = getParentZnodeModel();

        Text pathText = (Text) getControl(CONTROL_NAME_PATH_TEXT);
        String relativePath = pathText.getText();
        Znode parentZnode = parentZnodeModel.getData();
        String parentPath = parentZnode.getPath();
        String absolutePath = Znode.getAbsolutePath(parentPath, relativePath);

        Button sequentialCheckbox = (Button) getControl(CONTROL_NAME_CREATE_MODE_SEQUENTIAL_BUTTON);
        boolean isSequential = sequentialCheckbox.getSelection();

        Button ephemeralRadioButton = (Button) getControl(CONTROL_NAME_CREATE_MODE_EPHEMERAL_BUTTON);
        boolean isEphemeral = ephemeralRadioButton.getSelection();

        Znode znode = new Znode(absolutePath);
        znode.setSequential(isSequential);
        znode.setEphemeral(isEphemeral);
        znode.setData(data);

        return znode;
    }

    @Override
    protected void createContents() {

        super.createContents();

        GridTextInput pathGridTextInput = new GridTextInput(this, GridTextInput.Type.VALUE_REQUIRED,
                CONTROL_NAME_PATH_TEXT, "&Path:", null, 2);
        addGridTextInput(pathGridTextInput);

        // TODO: Does ZooKeeper impose a path length limit?
        // pathGridTextInput.getText().setTextLimit(?);

        Group createModeGroup = new Group(this, SWT.NULL);
        createModeGroup.setText("Create Mode");
        createModeGroup.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 3, 1));
        createModeGroup.setLayout(getLayout());

        Button persistentButton = new Button(createModeGroup, SWT.RADIO);
        persistentButton.setText("Pe&rsistent");
        persistentButton.setSelection(true);
        addControl(CONTROL_NAME_CREATE_MODE_PERSISTENT_BUTTON, persistentButton);

        Button ephemeralButton = new Button(createModeGroup, SWT.RADIO);
        ephemeralButton.setText("&Ephemeral");
        ephemeralButton.setSelection(false);
        addControl(CONTROL_NAME_CREATE_MODE_EPHEMERAL_BUTTON, ephemeralButton);

        Button sequentialButton = new Button(createModeGroup, SWT.CHECK);
        sequentialButton.setText("&Sequential");
        sequentialButton.setSelection(false);
        addControl(CONTROL_NAME_CREATE_MODE_SEQUENTIAL_BUTTON, sequentialButton);
        sequentialButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                modified(e.item);
            }
        });

        Group dataGroup = new Group(this, SWT.NULL);
        dataGroup.setText("Data");
        dataGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
        dataGroup.setLayout(getLayout());

        Button singleLineTextDataButton = new Button(dataGroup, SWT.RADIO);
        singleLineTextDataButton.setText("&Text");
        singleLineTextDataButton.setSelection(true);
        singleLineTextDataButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        addControl(CONTROL_NAME_DATA_SINGLE_LINE_TEXT_BUTTON, singleLineTextDataButton);
        singleLineTextDataButton.addSelectionListener(new DataTypeRadioButtonSelectionListener(
                CONTROL_NAME_DATA_SINGLE_LINE_COMPOSITE));

        Button fileDataButton = new Button(dataGroup, SWT.RADIO);
        fileDataButton.setText("&File");
        fileDataButton.setSelection(false);
        fileDataButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 2, 1));
        addControl(CONTROL_NAME_DATA_FILE_BUTTON, fileDataButton);
        fileDataButton.addSelectionListener(new DataTypeRadioButtonSelectionListener(CONTROL_NAME_DATA_FILE_COMPOSITE));

        Composite dataStackComposite = new Composite(dataGroup, SWT.NULL);
        dataStackComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
        StackLayout dataStackLayout = new StackLayout();
        dataStackComposite.setLayout(dataStackLayout);
        addControl(CONTROL_NAME_DATA_STACK_COMPOSITE, dataStackComposite);

        SingleLineZnodeTextDataGridComposite singleLineTextGridComposite = new SingleLineZnodeTextDataGridComposite(
                dataStackComposite);
        singleLineTextGridComposite.init();
        addControl(CONTROL_NAME_DATA_SINGLE_LINE_COMPOSITE, singleLineTextGridComposite);

        FileZnodeDataGridComposite fileGridComposite = new FileZnodeDataGridComposite(dataStackComposite);
        fileGridComposite.init();
        addControl(CONTROL_NAME_DATA_FILE_COMPOSITE, fileGridComposite);

        dataStackLayout.topControl = singleLineTextGridComposite;

    }

    @Override
    protected GridCompositeStatus updateStatus(Object source) {

        GridCompositeStatus status = super.updateStatus(source);
        if (status.getType().isError()) {
            return status;
        }

        String message;

        if (source instanceof GridTextInput) {
            GridTextInput gridTextInput = (GridTextInput) source;
            if (gridTextInput.getName().equals(CONTROL_NAME_PATH_TEXT)) {

                Text pathText = gridTextInput.getText();

                String relativePath = pathText.getText();
                if (relativePath.indexOf(Znode.PATH_SEPARATOR_CHAR) >= 0) {
                    message = CONTROL_NAME_PATH_TEXT + " should not contain the '" + Znode.PATH_SEPARATOR_CHAR
                            + "' character.";
                    return new GridCompositeStatus(CONTROL_NAME_PATH_TEXT, message,
                            GridCompositeStatus.Type.ERROR_INVALID);
                }

                // TODO: Validate characters in the text (spaces etc)?

                ZnodeModel parentZnodeModel = getParentZnodeModel();
                Znode parentZnode = parentZnodeModel.getData();
                String parentPath = parentZnode.getPath();

                String absolutePath = Znode.getAbsolutePath(parentPath, relativePath);

                Button sequentialCheckbox = (Button) getControl(CONTROL_NAME_CREATE_MODE_SEQUENTIAL_BUTTON);
                boolean isSequential = sequentialCheckbox.getSelection();

                if (!isSequential) {
                    // Use the DataModelManager for a hash look-up (avoid possibly long child string list scan).
                    if (parentZnodeModel.getManager().findKeys(parentZnode).contains(absolutePath)) {

                        // TODO: Should this take the sequential flag into account (i think yes).

                        message = "Znode '" + absolutePath + "' already exists.";
                        return new GridCompositeStatus(CONTROL_NAME_PATH_TEXT, message,
                                GridCompositeStatus.Type.ERROR_INVALID);
                    }
                }

                try {
                    Znode.validatePath(absolutePath, isSequential);
                }
                catch (IllegalArgumentException e) {
                    message = e.getMessage();
                    return new GridCompositeStatus(CONTROL_NAME_PATH_TEXT, message,
                            GridCompositeStatus.Type.ERROR_INVALID);
                }
            }
        }

        return GridCompositeStatus.OK_STATUS;
    }

    /**
     * TODO: Comment.
     * 
     * @return
     */
    private byte[] getZnodeData() throws Exception {
        Composite dataStackComposite = (Composite) getControl(CONTROL_NAME_DATA_STACK_COMPOSITE);
        ZnodeDataGridComposite znodeDataGridComposite = (ZnodeDataGridComposite) ((StackLayout) dataStackComposite
                .getLayout()).topControl;
        return znodeDataGridComposite.getZnodeData();
    }

    private class DataTypeRadioButtonSelectionListener extends SelectionAdapter {

        private final String _DataCompositeName;

        /**
         * TODO: Comment.
         * 
         * @param dataCompositeName
         */
        public DataTypeRadioButtonSelectionListener(String dataCompositeName) {
            super();
            _DataCompositeName = dataCompositeName;
        }

        @Override
        public void widgetSelected(SelectionEvent e) {
            Control composite = getControl(_DataCompositeName);
            Composite dataStackComposite = (Composite) getControl(CONTROL_NAME_DATA_STACK_COMPOSITE);
            ((StackLayout) dataStackComposite.getLayout()).topControl = composite;
            dataStackComposite.layout();
            modified(composite);
        }
    }

    private class FileZnodeDataGridComposite extends ZnodeDataGridComposite {

        public FileZnodeDataGridComposite(Composite parent) {
            super(parent);
            setNumColumns(2);
        }

        @Override
        public byte[] getZnodeData() throws Exception {
            Text filePathText = (Text) getControl(CONTROL_NAME_DATA_FILE_TEXT);
            String filePath = filePathText.getText();
            File file = new File(filePath);
            String fileName = file.getName();
            if (!file.exists()) {
                throw new IOException("File '" + fileName + "' does not exist.");
            }

            if (!file.isFile()) {
                throw new IOException("Path '" + filePath + "' is not a valid file.");
            }

            long length = file.length();

            if (length > Znode.MAX_DATA_SIZE) {
                throw new Exception("File '" + fileName
                        + "' size is greater than the maximum allowed Znode data size (" + Znode.MAX_DATA_SIZE
                        + " bytes).");
            }

            FileEditor fileEditor = new FileEditor(file);
            return fileEditor.read();
        }

        @Override
        protected void createContents() {

            GridTextInput fileGridTextInput = new GridTextInput(this, GridTextInput.Type.DEFAULT,
                    CONTROL_NAME_DATA_FILE_TEXT, null, null);
            addGridTextInput(fileGridTextInput);

            Button browseButton = new Button(this, SWT.PUSH);
            browseButton.setText("&Browse...");
            browseButton.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {

                    Text filePathText = (Text) getControl(CONTROL_NAME_DATA_FILE_TEXT);

                    FileDialog fileDialog = new FileDialog(getShell());
                    fileDialog.setFileName(filePathText.getText());
                    String newPath = fileDialog.open();
                    if (newPath != null) {
                        filePathText.setText(newPath);
                    }

                }

            });
        }

    }

    private class SingleLineZnodeTextDataGridComposite extends TextZnodeDataGridComposite {

        public SingleLineZnodeTextDataGridComposite(Composite parent) {
            super(parent);
        }

        @Override
        public Text getText() {
            return (Text) getControl(CONTROL_NAME_DATA_SINGLE_LINE_TEXT);
        }

        @Override
        protected void createContents() {
            GridTextInput dataSingleLineGridTextInput = new GridTextInput(this, GridTextInput.Type.DEFAULT,
                    CONTROL_NAME_DATA_SINGLE_LINE_TEXT, null, null);
            addGridTextInput(dataSingleLineGridTextInput);
        }

    }

    private abstract class TextZnodeDataGridComposite extends ZnodeDataGridComposite {

        /**
         * TODO: Comment.
         * 
         * @param parent
         */
        protected TextZnodeDataGridComposite(Composite parent) {
            super(parent);
        }

        /**
         * TODO: Comment.
         * 
         * @return
         */
        public abstract Text getText();

        @Override
        public byte[] getZnodeData() throws Exception {
            return getZnodeData(getText());
        }

        /**
         * TODO: Comment.
         * 
         * @param text
         * @return
         */
        protected byte[] getZnodeData(Text text) throws Exception {
            String stringValue = text.getText();

            if (stringValue.isEmpty()) {
                return null;
            }

            // TODO: Need to support other Charsets?
            byte[] data = stringValue.getBytes();
            return data;
        }

        @Override
        protected void createContents() {
            // TODO Auto-generated method stub

        }

        @Override
        public void init() {
            super.init();
            getText().setFont(JFaceResources.getTextFont());
        }

    }

    private abstract class ZnodeDataGridComposite extends GridComposite {

        /**
         * TODO: Comment.
         * 
         * @param parent
         */
        protected ZnodeDataGridComposite(Composite parent) {
            super(parent);
            setNumColumns(1);
        }

        public abstract byte[] getZnodeData() throws Exception;

    }

}
