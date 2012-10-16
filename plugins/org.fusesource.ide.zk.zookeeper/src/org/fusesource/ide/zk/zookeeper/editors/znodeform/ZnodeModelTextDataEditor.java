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

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.fusesource.ide.zk.zookeeper.data.Znode;
import org.fusesource.ide.zk.zookeeper.model.ZnodeModel;

/**
 * TODO: Comment.
 * 
 * @author Mark Masse
 */
public class ZnodeModelTextDataEditor extends BaseZnodeModelDataEditor {

	private Text _Text;

	/**
	 * TODO: Comment.
	 * 
	 * @param dataZnodeFormPage
	 * @param parent
	 * @param style
	 */
	public ZnodeModelTextDataEditor(ZnodeModelDataFormPage dataZnodeFormPage, Composite parent, int style) {
		super(dataZnodeFormPage, parent, style);
	}

	@Override
	public byte[] getZnodeDataFromEditor() {
		String stringValue = _Text.getText();

		if (stringValue.isEmpty()) {
			return null;
		}

		// TODO: Need to support other Charsets?
		byte[] data = stringValue.getBytes();
		return data;
	}

	@Override
	public void setActiveEditor(boolean active) {
		super.setActiveEditor(active);
		if (active) {
			_Text.forceFocus();
		}
	}

	@Override
	protected void createContent() {

		FormToolkit toolkit = getToolkit();

		_Text = toolkit.createText(this, "", SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL);
		_Text.setFont(JFaceResources.getTextFont());

		setLayout(new FillLayout());
		/*
		FormData formData = new FormData();
		formData.top = new FormAttachment(0, 0);
		formData.left = new FormAttachment(0, 0);
		formData.right = new FormAttachment(100, 0);

		_Text.setLayoutData(formData);
		 */

		_Text.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				setDirtyInternal(true);
			}
		});

	}

	@Override
	protected void hookSyncZnodeModelData(ZnodeModel znodeModel) {
		Znode znode = znodeModel.getData();
		_Text.setText(znode.getDataAsString());
	}

}
