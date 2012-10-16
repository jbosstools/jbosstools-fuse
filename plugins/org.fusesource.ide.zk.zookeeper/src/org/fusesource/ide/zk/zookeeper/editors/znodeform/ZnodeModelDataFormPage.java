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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.zookeeper.data.Stat;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.fusesource.ide.zk.zookeeper.ZooKeeperActivator;
import org.fusesource.ide.zk.zookeeper.data.Znode;
import org.fusesource.ide.zk.zookeeper.model.ZnodeModel;
import org.fusesource.scalate.util.Files;


/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public class ZnodeModelDataFormPage extends BaseZnodeModelFormPage {

	public static final String ID = ZnodeModelDataFormPage.class.getName();

	protected static Set<String> textExtensions = new HashSet<String>(Arrays.asList("xml", "properties", "json"));

	public static final Image IMAGE = ZooKeeperActivator
			.getManagedImage(ZooKeeperActivator.IMAGE_KEY_OBJECT_ZNODE_DATA);

	public static final String TITLE = "Data";
	private static final int DEFAULT_EDIT_MODE_DATA_SCAN_RANGE = 200;

	private BaseZnodeModelDataEditor _ActiveEditor;
	private EditMode _EditMode;
	private Button _FileDataRadioButton;
	private ZnodeModelFileDataEditor _FileZnodeDataEditor;
	private Button _SingleLineTextDataRadioButton;

	private ZnodeModelTextDataEditor _SingleLineTextZnodeDataEditor;
	private Composite _StackComposite;
	private final List<BaseZnodeModelDataEditor> _ZnodeDataEditors;

	/**
	 * TODO: Comment.
	 * 
	 * @param editor
	 * @param id
	 * @param title
	 */
	public ZnodeModelDataFormPage(ZnodeModelFormEditor editor) {
		super(editor, ID, TITLE, IMAGE);
		_ZnodeDataEditors = new CopyOnWriteArrayList<BaseZnodeModelDataEditor>();
	}

	/**
	 * Returns the editMode.
	 * 
	 * @return The editMode
	 */
	public EditMode getEditMode() {
		return _EditMode;
	}

	/**
	 * TODO: Comment.
	 * 
	 * @param text
	 * @return
	 */
	public byte[] getZnodeDataFromEditor() throws Exception {
		return _ActiveEditor.getZnodeDataFromEditor();
	}

	@Override
	public void setDirty(boolean dirty) {
		super.setDirty(dirty);
		setEditModeSelectionEnabled(!dirty);
		if (!dirty) {
			for (BaseZnodeModelDataEditor dataEditor : _ZnodeDataEditors) {
				dataEditor.setDirty(false);
			}
		}
	}

	/**
	 * Sets the editMode.
	 * 
	 * @param editMode the editMode to set
	 */
	public void setEditMode(EditMode editMode) {

		_EditMode = editMode;
		BaseZnodeModelDataEditor activeEditor = null;

		switch (_EditMode) {

		case SINGLE_LINE_TEXT:
			_FileDataRadioButton.setSelection(false);
			_SingleLineTextDataRadioButton.setSelection(true);

			if (_SingleLineTextZnodeDataEditor == null) {
				createSingleLineTextZnodeDataEditor();
			}

			activeEditor = _SingleLineTextZnodeDataEditor;
			break;

		case FILE:
			_SingleLineTextDataRadioButton.setSelection(false);
			_FileDataRadioButton.setSelection(true);
			if (_FileZnodeDataEditor == null) {
				createFileZnodeDataEditor();
			}

			activeEditor = _FileZnodeDataEditor;
			break;
		}

		if (activeEditor != null && activeEditor != _ActiveEditor) {
			if (_ActiveEditor != null) {
				_ActiveEditor.setActiveEditor(false);
			}

			_ActiveEditor = activeEditor;

			StackLayout stackLayout = (StackLayout) _StackComposite.getLayout();
			stackLayout.topControl = _ActiveEditor;
			forceLayout();
			_ActiveEditor.forceFocus();
			_ActiveEditor.setActiveEditor(true);

		}

	}

	@Override
	protected Layout createClientLayout() {
		FormLayout clientLayout = (FormLayout) super.createClientLayout();
		clientLayout.spacing = 10;
		clientLayout.marginBottom = 10;
		return clientLayout;
	}

	@Override
	protected void createModelFormContent(IManagedForm managedForm, Composite client) {
		SelectionListener yesListener = new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				_ActiveEditor.syncZnodeModelData();
				setDirtyInternal(false);
				setInfoText(null);
				updateToolbarLabelText();
			}

		};

		initYesNoInfoBar(managedForm, yesListener);

		FormToolkit toolkit = managedForm.getToolkit();

		Composite editModeComposite = toolkit.createComposite(client);
		FormLayout layout = new FormLayout();
		layout.spacing = 12;
		editModeComposite.setLayout(layout);

		Label editModeLabel = toolkit.createLabel(editModeComposite, "Edit Mode: ", SWT.LEFT);

		_SingleLineTextDataRadioButton = toolkit.createButton(editModeComposite, "&Text", SWT.RADIO);
		_SingleLineTextDataRadioButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				setEditMode(EditMode.SINGLE_LINE_TEXT);
			}

		});

		_FileDataRadioButton = toolkit.createButton(editModeComposite, "&File", SWT.RADIO);
		_FileDataRadioButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				setEditMode(EditMode.FILE);
			}

		});

		Label separator = toolkit.createSeparator(client, SWT.HORIZONTAL);

		_StackComposite = toolkit.createComposite(client);
		StackLayout stackLayout = new StackLayout();
		stackLayout.marginWidth = 0;
		_StackComposite.setLayout(stackLayout);

		FormData editModeCompositeFormData = new FormData();
		editModeCompositeFormData.top = new FormAttachment(0, 0);
		editModeCompositeFormData.left = new FormAttachment(0, 0);
		editModeCompositeFormData.right = new FormAttachment(100, 0);
		editModeComposite.setLayoutData(editModeCompositeFormData);

		FormData editModeLabelFormData = new FormData();
		editModeLabelFormData.top = new FormAttachment(0, 0);
		editModeLabelFormData.left = new FormAttachment(0, 0);
		editModeLabel.setLayoutData(editModeLabelFormData);

		FormData singleLineTextDataButtonFormData = new FormData();
		singleLineTextDataButtonFormData.top = new FormAttachment(editModeLabel, 0, SWT.CENTER);
		singleLineTextDataButtonFormData.left = new FormAttachment(editModeLabel, 4);
		_SingleLineTextDataRadioButton.setLayoutData(singleLineTextDataButtonFormData);

		FormData fileDataButtonFormData = new FormData();
		fileDataButtonFormData.top = new FormAttachment(_SingleLineTextDataRadioButton, 0, SWT.CENTER);
		fileDataButtonFormData.left = new FormAttachment(_SingleLineTextDataRadioButton, 0);
		_FileDataRadioButton.setLayoutData(fileDataButtonFormData);

		FormData separatorFormData = new FormData();
		separatorFormData.top = new FormAttachment(editModeComposite);
		separatorFormData.left = new FormAttachment(0, 0);
		separatorFormData.right = new FormAttachment(100, 0);
		separator.setLayoutData(separatorFormData);

		FormData stackCompositeFormData = new FormData();
		stackCompositeFormData.top = new FormAttachment(separator);
		stackCompositeFormData.left = new FormAttachment(0, 0);
		stackCompositeFormData.right = new FormAttachment(100, 0);
		stackCompositeFormData.bottom = new FormAttachment(100, 0);
		_StackComposite.setLayoutData(stackCompositeFormData);

	}

	@Override
	protected void forceLayout() {
		super.forceLayout();
		if (_StackComposite != null) {
			_StackComposite.layout(true);
		}
	}

	@Override
	protected void modelModifiedExternally() {

		ZnodeModel znodeModel = getModel();

		if (znodeModel.isDestroyed()) {
			return;
		}

		ZnodeModelFormEditor editor = (ZnodeModelFormEditor) getEditor();
		Znode znode = znodeModel.getData();
		Stat stat = znode.getStat();

		if (!isDirty() || stat.getVersion() == editor.getLastModificationVersion()) {
			_ActiveEditor.syncZnodeModelData();
			updateToolbarLabelText();
		}
		else {
			editor.setActivePage(ID);
			setInfoText(EXTERNAL_MODIFICATION_INFO_TEXT);
		}

	}

	@Override
	protected void saveCompleted() {
		super.saveCompleted();
		setInfoText(null);
	}

	private void createFileZnodeDataEditor() {
		_FileZnodeDataEditor = new ZnodeModelFileDataEditor(this, _StackComposite, SWT.NULL);
		_ZnodeDataEditors.add(_FileZnodeDataEditor);
	}

	private void createSingleLineTextZnodeDataEditor() {
		_SingleLineTextZnodeDataEditor = new ZnodeModelTextDataEditor(this, _StackComposite, SWT.NULL);
		_ZnodeDataEditors.add(_SingleLineTextZnodeDataEditor);
	}

	/**
	 * TODO: Comment.
	 * 
	 */
	private void setDefaultEditMode() {

		EditMode defaultEditMode = EditMode.SINGLE_LINE_TEXT;

		ZnodeModel znodeModel = getModel();
		Znode znode = znodeModel.getData();
		String path = znode.getPath();
		String ext = Files.extension(path);
		if (ext == null || !textExtensions.contains(ext.toLowerCase())) {
			byte[] data = znode.getData();
			if (data != null) {
				int scanRange = Math.min(DEFAULT_EDIT_MODE_DATA_SCAN_RANGE, data.length);
				for (int i = 0; i < scanRange; i++) {
					byte b = data[i];

					// In ASCII:
					// Octal 012 = LF (Line Feed)
					// Octal 013 = VT (Vertical Tab)
					// Octal 014 = FF (Form Feed)
					// Octal 015 = CR (Carriage Return)

					if (b > 011 && b < 016) {
						defaultEditMode = EditMode.FILE;
						break;
					}
				}
			}
		}

		setEditMode(defaultEditMode);
	}

	/**
	 * TODO: Comment.
	 * 
	 * @param enable
	 */
	private void setEditModeSelectionEnabled(boolean enabled) {
		_SingleLineTextDataRadioButton.setEnabled(enabled);
		_FileDataRadioButton.setEnabled(enabled);
	}

	private void updateToolbarLabelText() {
		setToolbarLabelText(Znode.STAT_NAME_VERSION + ": " + getModel().getData().getStat().getVersion(),
				Znode.STAT_DESCRIPTION_VERSION);
	}

	/**
	 * TODO: Comment.
	 * 
	 * @author Mark Masse
	 */
	public static enum EditMode {
		FILE,
		SINGLE_LINE_TEXT;
	}

	@Override
	protected void initFromModelInternal() {
		setDefaultEditMode();
		updateToolbarLabelText();
	}

}
